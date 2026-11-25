package snownee.kiwi.potion;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionMod extends Potion {
   private final boolean shouldRender;
   private final boolean canCure;
   private final boolean registerType;
   final int tickrate;
   private final String name;

   public PotionMod(String name, boolean shouldRender, int icon, boolean isBadEffect, int color, int tick, boolean canCure) {
      this(name, shouldRender, icon, isBadEffect, color, tick, canCure, true);
   }

   public PotionMod(String name, boolean shouldRender, int icon, boolean isBadEffect, int color, int tick, boolean canCure, boolean registerType) {
      super(isBadEffect, color);
      if (!isBadEffect) {
         this.setBeneficial();
      }

      this.name = name;
      this.shouldRender = shouldRender;
      this.canCure = canCure;
      this.registerType = registerType;
      this.tickrate = tick;
      this.setIconIndex(icon % 8, icon / 8);
   }

   public void register(String modid) {
      this.setPotionName(modid + ".potion." + this.name);
      this.setRegistryName(modid, this.name);
   }

   public boolean shouldRender(PotionEffect effect) {
      return this.shouldRender;
   }

   public List<ItemStack> getCurativeItems() {
      return this.canCure ? super.getCurativeItems() : Collections.emptyList();
   }

   public boolean shouldRenderInvText(PotionEffect effect) {
      return this.shouldRender(effect);
   }

   public boolean shouldRenderHUD(PotionEffect effect) {
      return this.shouldRender(effect);
   }

   @SideOnly(Side.CLIENT)
   public int getStatusIconIndex() {
      Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(this.getRegistryName().getResourceDomain(), "textures/gui/potions.png"));
      return super.getStatusIconIndex();
   }

   public Collection<PotionType> getPotionTypes() {
      return this.registerType ? Arrays.asList(new PotionType(this.getRegistryName().getResourceDomain() + "." + this.getRegistryName().getResourcePath(), new PotionEffect[]{new PotionEffect(this, this.isBadEffect() ? 600 : 1200, 0)})) : Collections.EMPTY_LIST;
   }

   public boolean isReady(int duration, int amplifier) {
      return this.tickrate > 0 && duration % this.tickrate == 0;
   }
}
