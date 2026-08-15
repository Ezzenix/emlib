package com.ezzenix.emlib.util.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

//? if >=1.21.9
import net.minecraft.client.input.MouseButtonEvent;

public class CompoundWidget extends AbstractWidget implements ContainerEventHandler {
	private final List<Renderable> renderables = new ArrayList<>();
	private final List<GuiEventListener> children = new ArrayList<>();
	private final List<NarratableEntry> narratables = new ArrayList<>();

	private GuiEventListener focused;
	private boolean isDragging;

	public CompoundWidget(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		for (Renderable child : this.renderables) {
			child.extractRenderState(graphics, mouseX, mouseY, a);
		}
	}

	protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(final T widget) {
		this.renderables.add(widget);
		return (T)this.addWidget(widget);
	}

	protected <T extends Renderable> T addRenderableOnly(final T renderable) {
		this.renderables.add(renderable);
		return renderable;
	}

	protected <T extends GuiEventListener & NarratableEntry> T addWidget(final T widget) {
		this.children.add(widget);
		this.narratables.add(widget);
		return widget;
	}

	public void removeWidget(GuiEventListener widget) {
		if (widget instanceof Renderable renderable) {
			this.renderables.remove(renderable);
		}

		if (widget instanceof NarratableEntry narratableEntry) {
			this.narratables.remove(narratableEntry);
		}

		if (this.getFocused() == widget) {
			this.setFocused(null);
		}

		this.children.remove(widget);
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return this.children;
	}

	@Override
	public boolean isDragging() {
		return this.isDragging;
	}

	@Override
	public void setDragging(boolean dragging) {
		this.isDragging = dragging;
	}

	@Override
	public GuiEventListener getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(GuiEventListener focused) {
		this.focused = focused;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}

	@Override
	//? if >=1.21.9 {
	public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
		if (!this.isActive()) return false;
		return ContainerEventHandler.super.mouseClicked(event, doubled);
	//? } else {
	/*public boolean mouseClicked(double x, double y, int button) {
		return ContainerEventHandler.super.mouseClicked(x, y, button);
	*///? }
	}

	@Override
	//? if >=1.21.9 {
	public boolean mouseReleased(MouseButtonEvent event) {
		return ContainerEventHandler.super.mouseReleased(event);
		//? } else {
	/*public boolean mouseReleased(double x, double y, int button) {
		return ContainerEventHandler.super.mouseReleased(x, y, button);
	*///? }
	}

	@Override
	//? if >=1.21.9 {
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (!this.isActive()) return false;
		return ContainerEventHandler.super.mouseDragged(event, dx, dy);
		//? } else {
	/*public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
		return ContainerEventHandler.super.mouseDragged(x, y, button, dx, dy);
	*///? }
	}

	@Override
	//? if >1.20.1 {
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		if (!this.isActive()) return false;
		return ContainerEventHandler.super.mouseScrolled(x, y, scrollX, scrollY);
	//? } else {
	/*public boolean mouseScrolled(double x, double y, double scroll) {
		return ContainerEventHandler.super.mouseScrolled(x, y, scroll);
	*///? }
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		for (GuiEventListener child : this.children) {
			if (child.isMouseOver(mouseX, mouseY)) {
				return true;
			}
		}
		return super.isMouseOver(mouseX, mouseY);
	}
}
