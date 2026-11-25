package artifacts.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ModelNightVisionGoggles extends ModelBase {
   private static final ResourceLocation TEXTURES = new ResourceLocation("artifacts", "textures/entity/layer/night_vision_goggles.png");
   protected ModelRenderer headBand;
   protected ModelRenderer goggleBase;
   protected ModelRenderer eyeLeft;
   protected ModelRenderer eyeRight;
   protected ModelRenderer eyeLeftOverlay;
   protected ModelRenderer eyeRightOverlay;

   public ModelNightVisionGoggles() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.headBand = new ModelRenderer(this, 0, 32);
      this.headBand.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.9F);
      this.goggleBase = new ModelRenderer(this, 0, 53);
      this.goggleBase.addBox(-4.0F, -6.0F, -4.95F, 8, 4, 1);
      this.headBand.addChild(this.goggleBase);
      this.eyeLeft = new ModelRenderer(this, 0, 48);
      this.eyeLeft.addBox(1.5F, -5.0F, -7.95F, 2, 2, 3);
      this.headBand.addChild(this.eyeLeft);
      this.eyeRight = new ModelRenderer(this, 10, 48);
      this.eyeRight.addBox(-3.5F, -5.0F, -7.95F, 2, 2, 3);
      this.headBand.addChild(this.eyeRight);
      this.eyeLeftOverlay = new ModelRenderer(this, 20, 48);
      this.eyeLeftOverlay.addBox(1.5F, -5.0F, -7.95F, 2, 2, 3);
      this.headBand.addChild(this.eyeLeftOverlay);
      this.eyeRightOverlay = new ModelRenderer(this, 30, 48);
      this.eyeRightOverlay.addBox(-3.5F, -5.0F, -7.95F, 2, 2, 3);
      this.headBand.addChild(this.eyeRightOverlay);
      this.headBand.showModel = false;
      this.goggleBase.showModel = false;
      this.eyeLeft.showModel = false;
      this.eyeRight.showModel = false;
      this.eyeLeftOverlay.showModel = false;
      this.eyeRightOverlay.showModel = false;
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES);
         this.headBand.showModel = true;
         this.goggleBase.showModel = true;
         this.eyeLeft.showModel = true;
         this.eyeRight.showModel = true;
         this.headBand.render(scale);
         this.goggleBase.showModel = false;
         this.eyeLeft.showModel = false;
         this.eyeRight.showModel = false;
         this.headBand.showModel = false;
         float lastLightmapX = OpenGlHelper.lastBrightnessX;
         float lastLightmapY = OpenGlHelper.lastBrightnessY;
         int light = 15728880;
         int lightmapX = light % 65536;
         int lightmapY = light / 65536;
         this.eyeLeftOverlay.showModel = true;
         this.eyeRightOverlay.showModel = true;
         GlStateManager.disableLighting();
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lightmapX, (float)lightmapY);
         this.eyeLeftOverlay.render(scale);
         this.eyeRightOverlay.render(scale);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastLightmapX, lastLightmapY);
         GlStateManager.enableLighting();
         this.eyeLeftOverlay.showModel = false;
         this.eyeRightOverlay.showModel = false;
      }
   }
}
