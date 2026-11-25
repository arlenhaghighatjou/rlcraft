package com.charles445.simpledifficulty.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PotionBase extends Potion {
   protected int xOffset = 0;
   protected int yOffset = 0;
   protected boolean drawHUD = true;
   protected boolean drawInventory = true;
   protected boolean drawInventoryText = true;

   public abstract ResourceLocation getTexture();

   public PotionBase() {
      super(false, 16777215);
   }

   public PotionBase(boolean isBadEffect, int liquidColor) {
      super(isBadEffect, liquidColor);
   }

   protected ResourceLocation formatTexture(String s) {
      return new ResourceLocation("simpledifficulty", "textures/potions/" + s + ".png");
   }

   public boolean hasStatusIcon() {
      return this.drawHUD;
   }

   public void removePotionCoreEffect(EntityLivingBase entity, Potion potion) {
      if (entity.isPotionActive(potion) && entity.getActivePotionEffect(potion).getDuration() > 1) {
         entity.removePotionEffect(potion);
         entity.addPotionEffect(new PotionEffect(potion, 1));
      }

   }

   public boolean isReady(int duration, int amplifier) {
      return this.isReadyVar(duration, amplifier, 50);
   }

   public boolean isReadyVar(int duration, int amplifier, int var) {
      int k = var >> amplifier;
      if (k > 0) {
         return duration % k == 0;
      } else {
         return true;
      }
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
      this.renderInventoryEffect(x, y, effect, Minecraft.getMinecraft());
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      if (this.getTexture() != null) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(this.getTexture());
         Gui.drawModalRectWithCustomSizedTexture(x + this.xOffset + 6, y + this.yOffset + 7, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
      }

   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
      this.renderHUDEffect(x, y, effect, Minecraft.getMinecraft(), alpha);
   }

   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      if (this.getTexture() != null) {
         mc.getTextureManager().bindTexture(this.getTexture());
         Gui.drawModalRectWithCustomSizedTexture(x + this.xOffset + 3, y + this.yOffset + 3, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
      }

   }

   public boolean shouldRenderHUD(PotionEffect effect) {
      return this.drawHUD;
   }

   public boolean shouldRender(PotionEffect effect) {
      return this.drawInventory;
   }

   public boolean shouldRenderInvText(PotionEffect effect) {
      return this.drawInventoryText;
   }
}
