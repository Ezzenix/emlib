package com.ezzenix.emlib;

import com.ezzenix.emlib.config.EmConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if forge {
/*import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import java.util.ConcurrentModificationException;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.event.RegisterCommandsEvent;
//? >=1.21.6 {
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
//? } else
//import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(value = EmLib.MOD_ID)
public class EmLib {
*///? }

//? if neoforge {
/*import net.neoforged.fml.ModContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(value = EmLib.MOD_ID, dist = Dist.CLIENT)
public class EmLib {
*///? }

//? if fabric {
import net.fabricmc.api.ModInitializer;

public class EmLib implements ModInitializer {
//? }

    public static final String MOD_ID = "emlib";
    public static final String MOD_NAME = "EmLib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	private static void initialize() {

	}

	//? if forge {
    /*public EmLib(final FMLJavaModLoadingContext context) {
        initialize();
    }

	@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class EmLibBusEvents {
		@SubscribeEvent
		public static void onPostInit(FMLClientSetupEvent event) {
			ModList.get().forEachModContainer((modId, modContainer) -> {
				if (EmConfig.instances.containsKey(modId)) {
					modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> EmConfig.getScreen(screen, modId)));
				}
			});
		}
	}
    *///? }

	//? if neoforge {
    /*public EmLib(ModContainer container) {
        initialize();
    }

	//? if >= 1.21.6 {
	@EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
	//? } else {
	/^@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	^///? }
	public static class EmLibBusEvents {
		@SubscribeEvent
		public static void onPostInit(FMLClientSetupEvent event) {
			ModList.get().forEachModContainer((modId, modContainer) -> {
				if (EmConfig.instances.containsKey(modId)) {
					modContainer.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, screen) -> EmConfig.getScreen(screen, modId));
				}
			});
		}
	}
    *///? }

	//? if fabric {
	@Override
	public void onInitialize() {
		initialize();
	}
	//? }
}
