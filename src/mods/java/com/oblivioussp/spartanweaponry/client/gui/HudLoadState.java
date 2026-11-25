package com.oblivioussp.spartanweaponry.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;

public class HudLoadState extends HudElement {
   public static HudLoadState hudActive = null;
   protected boolean isLoaded = false;
   protected boolean isOffhand = false;
   protected float loadProgress = 0.0F;
   protected int highlightedSlot = -1;
   protected final int COLOUR_LOADING = 1627365376;
   protected final int COLOUR_LOADED = 1614856256;
   protected final int COLOUR_AIMING = 1616953536;

   public HudLoadState() {
      super(0, 0);
   }

   public void setLoaded(boolean loaded) {
      this.isLoaded = loaded;
   }

   public void setLoadProgress(float progress) {
      this.loadProgress = progress;
   }

   public void setOffhand(boolean offhand) {
      this.isOffhand = offhand;
      if (offhand) {
         this.highlightedSlot = -1;
      }

   }

   public void setHighlightedSlot(int slot) {
      this.highlightedSlot = slot;
   }

   public void render() {
      Minecraft mc = Minecraft.getMinecraft();
      ScaledResolution res = new ScaledResolution(mc);
      int posX = this.isOffhand ? res.getScaledWidth() / 2 - 117 : res.getScaledWidth() / 2 - 88 + this.highlightedSlot * 20;
      int posY = res.getScaledHeight() - 19;
      int colour = this.isLoaded ? 1614856256 : 1627365376;
      if (!this.isLoaded) {
         posY = res.getScaledHeight() - 3 - MathHelper.clamp(MathHelper.floor(16.0F * this.loadProgress), 0, 16);
      }

      if (this.highlightedSlot != -1 || this.isOffhand) {
         GuiIngame var10000 = mc.ingameGUI;
         GuiIngame.drawRect(posX, posY, posX + 16, res.getScaledHeight() - 3, colour);
      }

   }
}
