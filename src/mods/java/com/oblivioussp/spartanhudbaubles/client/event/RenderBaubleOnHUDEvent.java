package com.oblivioussp.spartanhudbaubles.client.event;

import com.oblivioussp.spartanhudbaubles.client.gui.AlignmentHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderBaubleOnHUDEvent extends Event {
   protected final ItemStack itemStack;
   protected final int positionX;
   protected final int positionY;
   protected final AlignmentHelper.Alignment alignment;
   protected final boolean isVertical;
   protected final ScaledResolution resolution;
   protected final GuiIngame guiIngame;
   protected final FontRenderer fontRenderer;

   private RenderBaubleOnHUDEvent(ItemStack stack, int posX, int posY, AlignmentHelper.Alignment align, boolean vertical, ScaledResolution res, GuiIngame gui, FontRenderer font) {
      this.itemStack = stack;
      this.positionX = posX;
      this.positionY = posY;
      this.alignment = align;
      this.isVertical = vertical;
      this.resolution = res;
      this.guiIngame = gui;
      this.fontRenderer = font;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public int getPositionX() {
      return this.positionX;
   }

   public int getPositionY() {
      return this.positionY;
   }

   public AlignmentHelper.Alignment getAlignment() {
      return this.alignment;
   }

   public boolean isVertical() {
      return this.isVertical;
   }

   public ScaledResolution getScaledResolution() {
      return this.resolution;
   }

   public GuiIngame getGuiIngame() {
      return this.guiIngame;
   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public static class Post extends RenderBaubleOnHUDEvent {
      public Post(ItemStack stack, int posX, int posY, AlignmentHelper.Alignment align, boolean vertical, ScaledResolution res, GuiIngame gui, FontRenderer font) {
      }
   }

   public static class Pre extends RenderBaubleOnHUDEvent {
      public Pre(ItemStack stack, int posX, int posY, AlignmentHelper.Alignment align, boolean vertical, ScaledResolution res, GuiIngame gui, FontRenderer font) {
      }
   }
}
