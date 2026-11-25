package com.oblivioussp.spartanshields.client.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BannerTextures.Cache;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;

public class TileEntityItemStackRendererSS extends TileEntityItemStackRenderer {
   protected final Cache SHIELD_BANNER_DESIGNS;
   protected final ResourceLocation TEXTURE_SHIELD_NO_PATTERN;
   protected final Item shield;
   protected final ModelShield shieldModel;
   protected final TileEntityBanner teBanner = new TileEntityBanner();

   public TileEntityItemStackRendererSS(ItemShield itemShield, String textureNoPattern, String texturePattern, String bannerID, ModelShield modelShield) {
      this.shield = itemShield;
      this.shieldModel = modelShield;
      this.TEXTURE_SHIELD_NO_PATTERN = new ResourceLocation("spartanshields", textureNoPattern);
      this.SHIELD_BANNER_DESIGNS = new Cache(bannerID, new ResourceLocation("spartanshields", texturePattern), "textures/entity/shield/");
   }

   public void renderByItem(ItemStack stack) {
      this.renderByItem(stack, 1.0F);
   }

   public void renderByItem(ItemStack stack, float partialTicks) {
      Item item = stack.getItem();
      if (item == this.shield) {
         if (stack.getSubCompound("BlockEntityTag") != null) {
            this.teBanner.setItemValues(stack, true);
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.SHIELD_BANNER_DESIGNS.getResourceLocation(this.teBanner.getPatternResourceLocation(), this.teBanner.getPatternList(), this.teBanner.getColorList()));
         } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.TEXTURE_SHIELD_NO_PATTERN);
         }

         GlStateManager.pushMatrix();
         GlStateManager.scale(1.0F, -1.0F, -1.0F);
         this.shieldModel.render();
         GlStateManager.popMatrix();
      }

      super.renderByItem(stack, partialTicks);
   }
}
