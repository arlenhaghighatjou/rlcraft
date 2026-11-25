package com.tmtravlr.potioncore.potion;

import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PotionCorePotion extends Potion {
   private ResourceLocation icon;
   private boolean enabled = true;

   public PotionCorePotion(String potionName, boolean bad, int color) {
      super(bad, color);
      if (!bad) {
         this.setBeneficial();
      }

      this.setRegistryName(potionName);
      this.setPotionName("effect." + potionName);
      this.icon = new ResourceLocation("potioncore", "textures/gui/potion/" + potionName + ".png");
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void registerPotionAttributeModifiers() {
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public void affectEntity(Entity thrownPotion, Entity thrower, EntityLivingBase entity, int amplifier, double potency) {
      this.performEffect(entity, amplifier);
   }

   @SideOnly(Side.CLIENT)
   public boolean hasStatusIcon() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int posX, int posZ, PotionEffect effect, Minecraft mc) {
      this.renderPotionIcon(posX + 6, posZ + 7, mc);
   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      this.renderPotionIcon(x + 3, y + 3, mc);
   }

   @SideOnly(Side.CLIENT)
   private void renderPotionIcon(int x, int y, Minecraft mc) {
      mc.getTextureManager().bindTexture(this.icon);
      int width = 18;
      int height = 18;
      float widthRatio = 0.055555556F;
      float heightRatio = 0.055555556F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexBuffer = tessellator.getBuffer();
      vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      vertexBuffer.pos((double)(x + 0), (double)(y + height), 0.0D).tex(0.0D, (double)((float)height * heightRatio)).endVertex();
      vertexBuffer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((float)width * widthRatio), (double)((float)height * heightRatio)).endVertex();
      vertexBuffer.pos((double)(x + width), (double)(y + 0), 0.0D).tex((double)((float)width * widthRatio), 0.0D).endVertex();
      vertexBuffer.pos((double)(x + 0), (double)(y + 0), 0.0D).tex(0.0D, 0.0D).endVertex();
      tessellator.draw();
   }
}
