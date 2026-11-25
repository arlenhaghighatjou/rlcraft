package artifacts.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ModelSnorkel extends ModelBase {
   public static final ResourceLocation TEXTURES = new ResourceLocation("artifacts", "textures/entity/layer/snorkel.png");
   protected ModelRenderer snorkelMouthPiece;
   protected ModelRenderer snorkelTubeThing;
   protected ModelRenderer snorkelGoggles;
   protected ModelRenderer snorkelGogglesOverlay;

   public ModelSnorkel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.snorkelMouthPiece = new ModelRenderer(this, 0, 46);
      this.snorkelMouthPiece.addBox(-2.0F, -1.5F, -6.0F, 8, 2, 2, 0.0F);
      this.snorkelGoggles = new ModelRenderer(this, 28, 32);
      this.snorkelGoggles.addBox(-4.0F, -7.0F, -4.0F, 8, 8, 8, 1.0F);
      this.snorkelMouthPiece.addChild(this.snorkelGoggles);
      this.snorkelGogglesOverlay = new ModelRenderer(this, 28, 48);
      this.snorkelGogglesOverlay.addBox(-4.0F, -7.0F, -4.0F, 8, 8, 8, 1.0F);
      this.snorkelMouthPiece.addChild(this.snorkelGogglesOverlay);
      this.snorkelTubeThing = new ModelRenderer(this, 0, 32);
      this.snorkelTubeThing.addBox(4.0F, -5.0F, -3.0F, 2, 2, 12, 0.0F);
      this.snorkelTubeThing.rotateAngleX = 0.7853F;
      this.snorkelMouthPiece.addChild(this.snorkelTubeThing);
      this.snorkelMouthPiece.showModel = false;
      this.snorkelTubeThing.showModel = false;
      this.snorkelGoggles.showModel = false;
      this.snorkelGogglesOverlay.showModel = false;
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, true);
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, boolean renderFull) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES);
         this.snorkelMouthPiece.showModel = true;
         this.snorkelTubeThing.showModel = true;
         this.snorkelGoggles.showModel = renderFull;
         this.snorkelMouthPiece.render(scale);
         this.snorkelGoggles.showModel = false;
         this.snorkelMouthPiece.showModel = false;
         this.snorkelTubeThing.showModel = false;
         if (renderFull) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
            this.snorkelGogglesOverlay.showModel = true;
            this.snorkelMouthPiece.render(scale);
            this.snorkelGogglesOverlay.showModel = false;
            GlStateManager.disableBlend();
         }

      }
   }
}
