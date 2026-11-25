package xzeroair.trinkets.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public class Tiara extends ModelBiped {
   public static final ResourceLocation TEXTURE = new ResourceLocation("xat:textures/ender_tiara_model.png");
   public ModelRenderer Tiara;
   public ModelRenderer Tiara1Right;
   public ModelRenderer Tiara1Left;
   public ModelRenderer Tiara2Right;
   public ModelRenderer Tiara2Left;
   public ModelRenderer Tiara3Right;
   public ModelRenderer Tiara3Left;
   public ModelRenderer Tiara4Right;
   public ModelRenderer Tiara4Left;
   public boolean equippedAsAccessory = false;

   public Tiara() {
      this.textureWidth = 16;
      this.textureHeight = 16;
      this.Tiara = new ModelRenderer(this, 0, 0);
      this.Tiara.addBox(-1.0F, -0.0F, -4.05F, 2, 2, 1, 0.0F);
      this.Tiara.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.Tiara1Right = new ModelRenderer(this, 0, 3);
      this.Tiara1Right.addBox(-0.9F, -0.0F, -4.0F, 2, 1, 1, 0.0F);
      this.Tiara1Right.setRotationPoint(0.0F, 1.0F, 0.0F);
      this.setRotateAngle(this.Tiara1Right, 0.0F, 0.5061455F, 0.0F, 1.0F);
      this.Tiara1Left = new ModelRenderer(this, 0, 3);
      this.Tiara1Left.mirror = true;
      this.Tiara1Left.addBox(-1.1F, -0.0F, -4.0F, 2, 1, 1, 0.0F);
      this.Tiara1Left.setRotationPoint(0.0F, 1.0F, 0.0F);
      this.setRotateAngle(this.Tiara1Left, 0.0F, -0.5061455F, 0.0F, 1.0F);
      this.Tiara2Right = new ModelRenderer(this, 0, 3);
      this.Tiara2Right.addBox(-0.8F, -0.0F, -3.9F, 2, 1, 1, 0.0F);
      this.Tiara2Right.setRotationPoint(0.0F, 1.0F, 0.0F);
      this.setRotateAngle(this.Tiara2Right, 0.0F, 1.012291F, 0.0F, 1.0F);
      this.Tiara2Left = new ModelRenderer(this, 0, 3);
      this.Tiara2Left.mirror = true;
      this.Tiara2Left.addBox(-1.2F, -0.0F, -3.9F, 2, 1, 1, 0.0F);
      this.Tiara2Left.setRotationPoint(0.0F, 1.0F, 0.0F);
      this.setRotateAngle(this.Tiara2Left, 0.0F, -1.012291F, 0.0F, 1.0F);
      this.Tiara3Left = new ModelRenderer(this, 0, 3);
      this.Tiara3Left.mirror = true;
      this.Tiara3Left.addBox(-1.4F, -0.0F, -3.7F, 2, 1, 1, 0.0F);
      this.Tiara3Left.setRotationPoint(0.0F, 1.0F, 0.0F);
      this.setRotateAngle(this.Tiara3Left, 0.0F, -1.5707964F, 0.0F, 1.0F);
      this.Tiara3Right = new ModelRenderer(this, 0, 3);
      this.Tiara3Right.addBox(-0.6F, -0.0F, -3.7F, 2, 1, 1, 0.0F);
      this.Tiara3Right.setRotationPoint(0.0F, 1.0F, 0.0F);
      this.setRotateAngle(this.Tiara3Right, 0.0F, 1.5707964F, 0.0F, 1.0F);
      this.Tiara4Right = new ModelRenderer(this, 0, 5);
      this.Tiara4Right.addBox(-0.9F, -0.0F, -4.0F, 1, 1, 1, 0.0F);
      this.Tiara4Right.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.setRotateAngle(this.Tiara4Right, 0.0F, 0.5061455F, 0.0F, 1.0F);
      this.Tiara4Left = new ModelRenderer(this, 0, 5);
      this.Tiara4Left.mirror = true;
      this.Tiara4Left.addBox(-0.1F, -0.0F, -4.0F, 1, 1, 1, 0.0F);
      this.Tiara4Left.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.setRotateAngle(this.Tiara4Left, 0.0F, -0.5061455F, 0.0F, 1.0F);
      this.Tiara.addChild(this.Tiara1Right);
      this.Tiara.addChild(this.Tiara1Left);
      this.Tiara.addChild(this.Tiara2Right);
      this.Tiara.addChild(this.Tiara2Left);
      this.Tiara.addChild(this.Tiara3Right);
      this.Tiara.addChild(this.Tiara3Left);
      this.Tiara.addChild(this.Tiara4Right);
      this.Tiara.addChild(this.Tiara4Left);
   }

   protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
      float f;
      for(f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return prevYawOffset + partialTicks * f;
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch, float scale, float partialTicks) {
      this.setRotateAngle(this.Tiara, 0.0F, 0.0F, 0.0F, scale);
      float sneak = this.isSneak ? 0.2F : 0.0F;
      this.Tiara.offsetX = 0.0F;
      this.Tiara.offsetY = 0.0F;
      this.Tiara.offsetZ = 0.0F;
      this.Tiara.rotationPointX = 0.0F;
      this.Tiara.rotationPointY = 0.0F;
      this.Tiara.rotationPointZ = 0.0F;
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, yaw, pitch, scale, entity);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, sneak, 0.0F);
      if (this.equippedAsAccessory) {
         Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
         this.Tiara.render(scale);
         this.Tiara.postRender(scale);
      } else {
         this.bipedHead.postRender(scale);

         try {
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
            TextureManager texManager = Minecraft.getMinecraft().getTextureManager();
            IBakedModel model = renderer.getItemModelMesher().getModelManager().getModel(new ModelResourceLocation(new ResourceLocation("xat", "moon_rose"), "inventory"));
            if (model != null) {
               model = model.getOverrides().handleItemState(model, ItemStack.EMPTY, (World)null, (EntityLivingBase)null);
               model.handlePerspective(TransformType.HEAD);
               texManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
               texManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.enableRescaleNormal();
               GlStateManager.alphaFunc(516, 0.1F);
               GlStateManager.enableBlend();
               GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
               GlStateManager.pushMatrix();
               model = ForgeHooksClient.handleCameraTransforms(model, TransformType.HEAD, false);
               GlStateManager.translate(-0.5F, -0.0F, -0.5F);
               if (!model.isBuiltInRenderer()) {
                  Tessellator tessellator = Tessellator.getInstance();
                  WorldRenderer bufferbuilder = tessellator.getBuffer();
                  bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

                  for(EnumFacing enumfacing : EnumFacing.values()) {
                     renderer.renderQuads(bufferbuilder, model.getQuads((IBlockState)null, enumfacing, 0L), -1, ItemStack.EMPTY);
                  }

                  renderer.renderQuads(bufferbuilder, model.getQuads((IBlockState)null, (EnumFacing)null, 0L), -1, ItemStack.EMPTY);
                  tessellator.draw();
               }

               GlStateManager.cullFace(CullFace.BACK);
               GlStateManager.popMatrix();
               GlStateManager.disableRescaleNormal();
               GlStateManager.disableBlend();
               texManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
               texManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
               GlStateManager.pushMatrix();
            }

            GlStateManager.popMatrix();
         } catch (Exception var19) {
            ;
         }
      }

      GlStateManager.popMatrix();
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch, float scale) {
      this.render(entity, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch, scale, 1.0F);
   }

   public void copyFromModel(ModelRenderer source, ModelRenderer dest) {
      dest.rotateAngleX = source.rotateAngleX;
      dest.rotateAngleY = source.rotateAngleY;
      dest.rotateAngleZ = source.rotateAngleZ;
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
   }

   public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z, float scale) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
      if (modelRenderer.rotateAngleX == 0.0F && modelRenderer.rotateAngleY == 0.0F && modelRenderer.rotateAngleZ == 0.0F) {
         if (modelRenderer.rotationPointX != 0.0F || modelRenderer.rotationPointY != 0.0F || modelRenderer.rotationPointZ != 0.0F) {
            GlStateManager.translate(modelRenderer.rotationPointX * scale, modelRenderer.rotationPointY * scale, modelRenderer.rotationPointZ * scale);
         }
      } else {
         GlStateManager.translate(modelRenderer.rotationPointX * scale, modelRenderer.rotationPointY * scale, modelRenderer.rotationPointZ * scale);
         if (modelRenderer.rotateAngleZ != 0.0F) {
            GlStateManager.rotate(modelRenderer.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
         }

         if (modelRenderer.rotateAngleY != 0.0F) {
            GlStateManager.rotate(modelRenderer.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
         }

         if (modelRenderer.rotateAngleX != 0.0F) {
            GlStateManager.rotate(modelRenderer.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
         }
      }

   }
}
