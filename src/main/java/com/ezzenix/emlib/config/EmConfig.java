package com.ezzenix.emlib.config;

import com.ezzenix.emlib.EmLib;
import com.ezzenix.emlib.util.Platform;
import com.google.gson.*;
import net.minecraft.client.gui.screens.Screen;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EmConfig {
	private static final Gson GSON = new GsonBuilder()
		.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.PRIVATE, Modifier.FINAL)
		.addSerializationExclusionStrategy(new ExclusionStrategy() {
			public boolean shouldSkipClass(Class<?> clazz) { return false; }
			public boolean shouldSkipField(FieldAttributes fieldAttributes) {
				return fieldAttributes.getAnnotation(Option.class) == null;
			}
		})
		.registerTypeAdapterFactory(new EnumTypeAdapterFactory())
		.setPrettyPrinting().create();

	final List<EntryInfo> entries = new ArrayList<>();

	protected String modId;
	protected String title;
	protected File configFile;
	protected Class<? extends EmConfig> configClass;
	protected ScreenFactory screenFactory = ConfigScreen::new;
	protected boolean needsScreenUpdate = false;

	public static final Map<String, EmConfig> instances = new HashMap<>();

	protected static <T extends EmConfig> T createInstance(Class<? extends EmConfig> configClass) {
		try {
			//noinspection unchecked
			return (T) configClass.getDeclaredConstructor().newInstance();
		}
		catch (Exception e) { throw new RuntimeException(e); }
	}

	public static void init(String modId, Class<? extends EmConfig> config) {
		EmConfig instance = createInstance(config);
		instance.modId = modId;
		instance.configClass = config;
		instance.configFile = new File(Platform.getConfigDirectory().toFile(), modId+".json");
		instances.put(modId, instance);

		if (config.isAnnotationPresent(Config.class)) {
			if (!config.getAnnotation(Config.class).title().trim().isEmpty()) {
				instance.title = config.getAnnotation(Config.class).title().trim();
			}
		}

		for (Field field : config.getFields()) {
			if (
				(field.isAnnotationPresent(Option.class) || field.isAnnotationPresent(Comment.class))
				&& !field.isAnnotationPresent(Hidden.class)
			) {
				instance.entries.add(new EntryInfo(field, modId));
			}
		}

		instance.load();
	}

	private static EmConfig assertConfig(String modId) {
		EmConfig instance = instances.get(modId);
		if (instance == null) {
			throw new IllegalArgumentException("Could not find registered config with modId " + modId);
		}
		return instance;
	}

	public static void setScreenFactory(String modId, ScreenFactory screenFactory) {
		EmConfig instance = assertConfig(modId);
		instance.screenFactory = screenFactory;
	}

	public static Screen getScreen(Screen parent, String modId) {
		EmConfig instance = assertConfig(modId);
		return instance.screenFactory.supply(parent, instance);
	}

	public static void save(String modId) {
		EmConfig instance = instances.get(modId);
		if (instance != null) {
			instance.save();
		}
	}

	public void load() {
		if (!configFile.exists()) {
			save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8)) {
			EmConfig loaded = GSON.fromJson(reader, configClass);
			for (EntryInfo info : this.entries) {
				Object loadedValue = info.field.get(loaded);
				if (loadedValue != null) {
					info.field.set(this, loadedValue);
				}
			}
		} catch (JsonParseException e) {
			EmLib.LOGGER.error("Config for mod {} is invalid", this.modId, e);

			/* save backup and save new default config */
			File backup = new File(configFile.getPath() + ".bak");
			try {
				Files.move(
					configFile.toPath(),
					backup.toPath(),
					StandardCopyOption.REPLACE_EXISTING
				);
			} catch (IOException backupException) {
				EmLib.LOGGER.error("Failed to back up invalid config for mod {}", this.modId, backupException);
			}

			save();
		} catch (Exception e) {
			EmLib.LOGGER.error("Failed to read config for mod {}", this.modId, e);
		}

		this.entries.forEach(EntryInfo::updateLocked);
	}

	public void save() {
		try {
			if (!configFile.getParentFile().exists()) {
				configFile.getParentFile().mkdirs();
			}
			try (Writer writer = Files.newBufferedWriter(configFile.toPath(), StandardCharsets.UTF_8)) {
				GSON.toJson(this, writer);
			}
		} catch (Exception e) {
			EmLib.LOGGER.error("Failed to save config for mod {}", this.modId, e);
		}
	}

	public String getModId() {
		return this.modId;
	}

	public String getTitle() {
		return this.title;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Option {
		double min() default Double.MIN_NORMAL;
		double max() default Double.MAX_VALUE;
		int precision() default 10;
		String suffix() default "";
		boolean offText() default false;
		boolean isSlider() default false;
		boolean isColor() default false;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Comment {
		boolean centered() default true;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Requires {
		String modId() default "";
		String option() default "";
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Hidden {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Config {
		String title() default "";
	}

	@FunctionalInterface
	public interface ScreenFactory {
		Screen supply(Screen parent, EmConfig instance);
	}

}
