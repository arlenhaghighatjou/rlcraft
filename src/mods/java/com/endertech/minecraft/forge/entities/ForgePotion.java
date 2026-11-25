package com.endertech.minecraft.forge.entities;

import com.endertech.common.CommonTime;
import com.endertech.minecraft.forge.ForgeLocating;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ColorARGB;
import com.endertech.minecraft.forge.math.GameTime;
import com.endertech.minecraft.forge.units.IForgeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ForgePotion extends Potion {
   protected final ResourceLocation texture;

   protected ForgePotion(ForgeMod mod, boolean isBadEffect, ColorARGB liquidColor) {
      super(isBadEffect, liquidColor.getARGB());
      String name = IForgeUnit.getClassRegName(this.getClass());
      this.texture = ForgeLocating.potionTexture(mod.getId(), name);
      this.setPotionName("potion." + name);
      this.setRegistryName(name);
      ForgeRegistries.POTIONS.register(this);
   }

   public abstract void performEffect(EntityLivingBase var1, int var2);

   public PotionEffect createEffect(CommonTime.Interval duration) {
      return this.createEffect(duration, true);
   }

   public PotionEffect createEffect(CommonTime.Interval duration, boolean showParticles) {
      int ticks = (int)GameTime.inServerTicks(duration);
      PotionEffect effect = new PotionEffect(this, ticks, 0, false, showParticles);
      return effect;
   }

   public boolean isReady(int ticksLeft, int amplifier) {
      int reminder = ticksLeft % this.getUpdateInterval();
      return reminder == 0;
   }

   public int getUpdateInterval() {
      return 5;
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      this.drawTexture(x + 7, y + 8, mc);
   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      this.drawTexture(x + 4, y + 4, mc);
   }

   @SideOnly(Side.CLIENT)
   protected int getTextureSize() {
      return 16;
   }

   @SideOnly(Side.CLIENT)
   protected void drawTexture(int x, int y, Minecraft mc) {
      mc.renderEngine.bindTexture(this.texture);
      int size = this.getTextureSize();
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, size, size, (float)size, (float)size);
   }
}
