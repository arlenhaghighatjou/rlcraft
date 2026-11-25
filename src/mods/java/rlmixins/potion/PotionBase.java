package rlmixins.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PotionBase extends Potion {
   private final ResourceLocation texture;

   public PotionBase(String name, boolean isBadEffect, int liquidColor) {
      super(isBadEffect, liquidColor);
      this.texture = new ResourceLocation("rlmixins", "textures/effects/" + name + ".png");
      this.setRegistryName("rlmixins", name);
      this.setPotionName("effects.rlmixins." + name);
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public boolean hasStatusIcon() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
      this.renderInventoryEffect(x, y, effect, Minecraft.getMinecraft());
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      if (this.getTexture() != null) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(this.getTexture());
         Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
      }

   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
      this.renderHUDEffect(x, y, effect, Minecraft.getMinecraft(), alpha);
   }

   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      if (this.getTexture() != null) {
         mc.getTextureManager().bindTexture(this.getTexture());
         Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
      }

   }

   public boolean shouldRenderHUD(PotionEffect effect) {
      return true;
   }

   public boolean shouldRender(PotionEffect effect) {
      return true;
   }

   public boolean shouldRenderInvText(PotionEffect effect) {
      return true;
   }
}
