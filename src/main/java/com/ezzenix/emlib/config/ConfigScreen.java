package com.ezzenix.emlib.config;

import com.ezzenix.emlib.EmLib;
import com.ezzenix.emlib.util.EmId;
import com.ezzenix.emlib.util.EmPort;
import com.ezzenix.emlib.util.IconButtonWidget;
import com.ezzenix.emlib.util.RightClickableButton;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

//? if >=1.21.9
import net.minecraft.client.input.MouseButtonEvent;

public class ConfigScreen extends Screen {
	private final Screen parent;
	private final EmConfig instance;
	private ConfigListWidget list;

	public ColorEditorWidget activeColorEditor = null;

	private static final int ROW_WIDTH = 380;
	private static final int BUTTON_WIDTH = 120;

	public ConfigScreen(Screen parent, EmConfig instance) {
		super(Component.literal(instance.getTitle()));
		this.parent = parent;
		this.instance = instance;
	}

	@Override
	protected void init() {
		super.init();

		this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), (button) -> {
			this.instance.save();
			EmPort.screen(this.parent);
		}).bounds(this.width / 2 - 100, this.height - 26, 200, 20).build());

		this.list = new ConfigListWidget(this.minecraft, this.width, this.height - 57, 24, 26);
		this.addWidget(this.list);

		this.activeColorEditor = null;

		this.updateList();
	}

	@Override
	public void tick() {
		super.tick();
		updateButtons();
	}

	public void changed() { }

	public void updateButtons() {
		if (this.list == null) return;
		for (ConfigListWidget.Entry entry : this.list.children()) {
			if (entry.buttons != null && entry.buttons.size() >= 2) {
				if (entry.buttons.get(1) instanceof Button button)
					button.active = !Objects.equals(String.valueOf(entry.info.getValue()), String.valueOf(entry.info.defaultValue));
			}
		}
	}

	public void updateList() {
		this.list.clear();
		int rightX = width/2+ROW_WIDTH/2;
		int buttonLeftX = rightX-BUTTON_WIDTH-25;
		for (EntryInfo info : this.instance.entries) {
			if (info.comment != null) {
				this.list.add(List.of(), info);
				continue;
			}

			Tooltip tooltip = info.getTooltip(this.instance.modId);

			IconButtonWidget resetButton = new IconButtonWidget(b -> {
				info.setValue(info.defaultValue);
				updateList();
			}, EmId.of(EmLib.MOD_ID, "textures/gui/sprites/reset.png"), 12, Component.translatable("controls.reset"));
			resetButton.setX(rightX - 20);

			if (info.getType() == boolean.class) {
				/* boolean toggle */
				Button button = Button.builder(getBooleanComponent(info), (b) -> {
					boolean currentValue = (boolean) info.getValue();
					info.setValue(!currentValue);
					b.setMessage(getBooleanComponent(info));
				}).tooltip(tooltip).bounds(buttonLeftX, 0, BUTTON_WIDTH, 20).build();

				this.list.add(List.of(button, resetButton), info);
			} else if (info.getType() == int.class && info.entry.isColor()) {
				/* color editor */
				ColorEditorWidget editor = new ColorEditorWidget(buttonLeftX, 0, 120, 90, (int)info.getValue());
				ConfigColorButton widget = new ConfigColorButton(buttonLeftX, 0, BUTTON_WIDTH, 20, info, editor, this);
				widget.setTooltip(tooltip);

				this.list.add(List.of(widget, resetButton), info);
			} else if (info.entry.isSlider() && (info.getType() == int.class || info.getType() == float.class || info.getType() == double.class)) {
				/* number slider */
				double normalized = (Double.parseDouble(info.getValue().toString()) - info.entry.min()) / (info.entry.max() - info.entry.min());
				ConfigSliderWidget slider = new ConfigSliderWidget(buttonLeftX, 0, BUTTON_WIDTH, 20, normalized, info);
				slider.setTooltip(tooltip);

				this.list.add(List.of(slider, resetButton), info);
			} else if (info.getType() == String.class || info.getType() == int.class || info.getType() == float.class || info.getType() == double.class) {
				/* string/number input  */
				ConfigEditBoxWidget editBox = new ConfigEditBoxWidget(buttonLeftX, 0, BUTTON_WIDTH, 20, info);
				editBox.setTooltip(tooltip);

				this.list.add(List.of(editBox, resetButton), info);
			} else if (info.getType().isEnum()) {
				/* enum cycle */
				Button button = new RightClickableButton(buttonLeftX, 0, BUTTON_WIDTH, 20, getEnumComponent(info), (b, wasRightClick) -> {
					Object[] constants = info.getType().getEnumConstants();
					Enum<?> currentValue = (Enum<?>) info.getValue();

					int nextIndex = currentValue.ordinal() + (wasRightClick ? -1 : 1);
					if (nextIndex < 0) nextIndex = constants.length-1;
					if (nextIndex >= constants.length) nextIndex = 0;
					info.setValue(constants[nextIndex]);
					b.setMessage(getEnumComponent(info));
				});

				this.list.add(List.of(button, resetButton), info);
			}
		}
		this.updateButtons();
	}

	private Component getBooleanComponent(EntryInfo info) {
		return (boolean)info.getValue() ? Component.translatable("emlib.yes").withStyle(ChatFormatting.GREEN) : Component.translatable("emlib.no").withStyle(ChatFormatting.RED);
	}

	private Component getEnumComponent(EntryInfo info) {
		String languageKey = this.instance.modId + ".config.enum." + info.getType().getSimpleName() + "." + info.getValue().toString();
		if (Language.getInstance().has(languageKey)) {
			return Component.translatable(languageKey);
		} else {
			return Component.literal(info.getValue().toString());
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		//? if <=1.20.1
		//super.renderDirtBackground(graphics);

		super.extractRenderState(graphics, mouseX, mouseY, delta);
		this.list.extractRenderState(graphics, mouseX, mouseY, delta);

		graphics.centeredText(Minecraft.getInstance().font, this.title, this.width/2, 8, 0xffffffff);

		if (this.activeColorEditor != null) {
			this.activeColorEditor.extractRenderState(graphics, mouseX, mouseY, delta);
		}
	}

	@Override
	public void onClose() {
		this.instance.save();
		EmPort.screen(this.parent);
	}

	private class ConfigListWidget extends ContainerObjectSelectionList<ConfigListWidget.Entry> {
		public ConfigListWidget(Minecraft mc, int width, int height, int y, int itemHeight) {
			//? if >=1.20.3 {
			super(mc, width, height, y, itemHeight);
			//? } else {
			/*super(mc, width, height, y, height+y, itemHeight);
			*///? }
		}

		public void add(List<AbstractWidget> buttons, EntryInfo info) {
			this.addEntry(new Entry(buttons, info));
		}

		public void clear() {
			this.clearEntries();
		}

		@Override
		public int /*? if >=1.21.4 {*/ scrollBarX() /*?} else {*/ /*getScrollbarPosition() *//*?}*/ {
			return ConfigScreen.this.width - /*? if >=26.1 {*/ this.scrollbarWidth() /*?} else {*/ /*6*//*?}*/ - 4;
		}

		@Override
		public int getRowWidth() {
			return ROW_WIDTH;
		}

		public class Entry extends ContainerObjectSelectionList.Entry<Entry> {
			public final List<AbstractWidget> buttons;
			private final EntryInfo info;
			public final Component title;
			private final boolean centered;

			private float currentOffsetX = 0f;
			private float currentBgAlpha = 0f;

			public Entry(List<AbstractWidget> buttons, EntryInfo info) {
				this.buttons = buttons;
				this.info = info;
				this.title = info.getName(ConfigScreen.this.instance.modId);
				this.centered = info.comment != null && info.comment.centered();
			}

			@Override
			//? if >= 1.21.9 {
			public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				int x = this.getX();
				int y = this.getY();
			//?} else {
			/*public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			*///?}
				boolean isHovered = this.isMouseOver(mouseX, mouseY) && info.entry != null;

				float targetOffsetX = isHovered ? 4f : 0f;
				float targetBgAlpha = isHovered ? 0.04f : 0f;

				float speed = 0.65f;
				float adaptiveSpeed = 1.0f - (float)Math.pow(1.0f - speed, tickDelta);

				this.currentOffsetX = Mth.lerp(adaptiveSpeed, this.currentOffsetX, targetOffsetX);
				this.currentBgAlpha = Mth.lerp(adaptiveSpeed, this.currentBgAlpha, targetBgAlpha);

				/* draw selection background */
				int alphaInt = (int) (this.currentBgAlpha * 255);
				if (alphaInt > 0) {
					int bgColor = (alphaInt << 24) | 0xFFFFFF;
					int w = ConfigScreen.this.width;
					int h = 26;
					graphics.fill(0, y, x + w, y + h, bgColor);
				}

				/* draw buttons */
				buttons.forEach(b -> {
					b.setY(y + 3);
					b.extractRenderState(graphics, mouseX, mouseY, tickDelta);
				});

				/* draw text label */
				int color = isHovered ? 0xff87ff95 : 0xffffffff;
				if (this.centered) {
					graphics.centeredText(Minecraft.getInstance().font, this.title, ConfigScreen.this.width/2, y+8, color);
				} else {
					graphics.text(Minecraft.getInstance().font, this.title, x + (int)currentOffsetX, y+8, color);
				}
			}

			@Override @NotNull
			public List<? extends GuiEventListener> children() {
				return Lists.newArrayList(buttons);
			}

			@Override @NotNull
			public List<? extends NarratableEntry> narratables() {
				return Lists.newArrayList(buttons);
			}
		}
	}

	@Override
	//? if >=1.21.9 {
	public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
		double x = event.x();
		double y = event.y();
	//? } else {
	/*public boolean mouseClicked(double x, double y, int button) {
	*///? }
		if (this.activeColorEditor != null) {
			if (this.activeColorEditor.isMouseOver(x, y)) {
				this.setFocused(this.activeColorEditor);
				this.setDragging(true);
				//? if >=1.21.9 {
				return this.activeColorEditor.mouseClicked(event, doubled);
				//? } else
				//return this.activeColorEditor.mouseClicked(x, y, button);
			} else if (!(getBottomChildAt(x, y).orElse(null) instanceof ConfigColorButton)) {
				// click is outside editor
				this.activeColorEditor = null;
			}
		}
		//? if >=1.21.9 {
		return super.mouseClicked(event, doubled);
		//? } else
		//return super.mouseClicked(x, y, button);
	}

	private Optional<GuiEventListener> getBottomChildAt(double mouseX, double mouseY) {
		Optional<GuiEventListener> child = this.getChildAt(mouseX, mouseY);
		while (child.isPresent() && child.get() instanceof ContainerEventHandler container) {
			child = container.getChildAt(mouseX, mouseY);
		}
		return child;
	}
}
