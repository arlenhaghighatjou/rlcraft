package xzeroair.trinkets.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class Wings extends ModelBase {
   public static final ResourceLocation TEXTURE = new ResourceLocation("xat:textures/fairy_wings_fake.png");
   public ModelRenderer LeftWing;
   public ModelRenderer RightWing;
   protected double distanceMovedTotal = 0.0D;
   protected static final double CYCLES_PER_BLOCK = 3.0D;
   protected int cycleIndex = 0;

   public Wings() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.RightWing = new ModelRenderer(this, 0, 0);
      this.RightWing.setRotationPoint(-2.0F, 1.0F, 2.0F);
      this.RightWing.addBox(0.0F, -2.0F, 0.0F, 0, 16, 8, 0.0F);
      this.LeftWing = new ModelRenderer(this, 0, 0);
      this.LeftWing.setRotationPoint(1.0F, 1.0F, 2.0F);
      this.LeftWing.addBox(0.0F, -2.0F, 0.0F, 0, 16, 8, 0.0F);
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      GlStateManager.pushMatrix();
      Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
      GlStateManager.disableCull();
      this.RightWing.render(scale);
      this.LeftWing.render(scale);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
   }

   public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      float v = 0.08F;
      float f = 1.0F;
      float flap = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount * 1.0F / 1.0F;
      if (entityIn.motionX < 0.07999999821186066D && entityIn.motionZ < 0.07999999821186066D && entityIn.motionX > -0.07999999821186066D && entityIn.motionZ > -0.07999999821186066D && entityIn.onGround) {
         this.RightWing.rotateAngleY = -0.5F;
         this.LeftWing.rotateAngleY = 0.5F;
      } else {
         if (!entityIn.onGround && !entityIn.isRiding()) {
            double time = (double)((float)entityIn.world.getWorldTime() + ageInTicks);
            float f1 = 16.0F;
            float f2 = 32.0F;
            float f3 = 48.0F;
            float f4 = 64.0F;
            float[][] Cycle = new float[][]{{64.0F, 0.0F, 0.0F}, {48.0F, 0.0F, 0.0F}, {32.0F, 0.0F, 0.0F}, {16.0F, 0.0F, 0.0F}, {1.0F, 0.0F, 0.0F}, {1.0F, 0.0F, 0.0F}, {16.0F, 0.0F, 0.0F}, {32.0F, 0.0F, 0.0F}, {48.0F, 0.0F, 0.0F}, {64.0F, 0.0F, 0.0F}};
            this.cycleIndex = (int)(time * 3.0D % (double)Cycle.length);
            this.LeftWing.rotateAngleY = this.degToRad(Cycle[this.cycleIndex][0]);
            this.RightWing.rotateAngleY = -this.degToRad(Cycle[this.cycleIndex][0]);
         }

         if (entityIn.onGround) {
            this.RightWing.rotateAngleY = -flap;
            this.LeftWing.rotateAngleY = flap;
         }
      }

   }

   protected void updateDistanceMovedTotal(Entity parEntity) {
      this.distanceMovedTotal += parEntity.getDistance(parEntity.prevPosX, parEntity.prevPosY, parEntity.prevPosZ);
   }

   protected double getDistanceMovedTotal() {
      return this.distanceMovedTotal;
   }

   protected float degToRad(float degrees) {
      return degrees * 3.1415927F / 180.0F;
   }

   protected void setRotation(ModelRenderer model, float rotX, float rotY, float rotZ) {
      model.rotateAngleX = this.degToRad(rotX);
      model.rotateAngleY = this.degToRad(rotY);
      model.rotateAngleZ = this.degToRad(rotZ);
   }

   protected void spinX(ModelRenderer model) {
      model.rotateAngleX += this.degToRad(0.5F);
   }

   protected void spinY(ModelRenderer model) {
      model.rotateAngleY += this.degToRad(0.5F);
   }

   protected void spinZ(ModelRenderer model) {
      model.rotateAngleZ += this.degToRad(0.5F);
   }
}
