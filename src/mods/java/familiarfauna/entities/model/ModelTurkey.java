package familiarfauna.entities.model;

import familiarfauna.entities.EntityTurkey;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTurkey extends ModelBase {
   public ModelRenderer body;
   public ModelRenderer leftLeg;
   public ModelRenderer rightLeg;
   public ModelRenderer chest;
   public ModelRenderer neck;
   public ModelRenderer beak;
   public ModelRenderer wattle;
   public ModelRenderer plume;
   public ModelRenderer leftWing;
   public ModelRenderer rightWing;

   public ModelTurkey() {
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.body = new ModelRenderer(this, 0, 0);
      this.body.addBox(-6.0F, 0.0F, -6.0F, 12, 8, 14);
      this.body.setRotationPoint(0.0F, 10.0F, 0.0F);
      this.setRotation(this.body, 0.0F, 0.0F, 0.0F);
      this.leftLeg = new ModelRenderer(this, 44, 22);
      this.leftLeg.addBox(0.0F, 0.0F, -5.0F, 5, 6, 4);
      this.leftLeg.setRotationPoint(0.0F, 18.0F, 4.0F);
      this.setRotation(this.leftLeg, 0.0F, 0.0F, 0.0F);
      this.rightLeg = new ModelRenderer(this, 44, 22);
      this.rightLeg.addBox(-5.0F, 0.0F, -5.0F, 5, 6, 4);
      this.rightLeg.setRotationPoint(0.0F, 18.0F, 4.0F);
      this.setRotation(this.rightLeg, 0.0F, 0.0F, 0.0F);
      this.chest = new ModelRenderer(this, 0, 22);
      this.chest.addBox(-4.0F, -4.0F, -4.0F, 8, 5, 5);
      this.chest.setRotationPoint(0.0F, 14.0F, -3.0F);
      this.setRotation(this.chest, 0.7853982F, 0.0F, 0.0F);
      this.neck = new ModelRenderer(this, 0, 32);
      this.neck.addBox(-2.0F, -10.0F, -5.0F, 4, 10, 3);
      this.setRotation(this.neck, 0.0F, 0.0F, 0.0F);
      this.chest.addChild(this.neck);
      this.beak = new ModelRenderer(this, 0, 45);
      this.beak.addBox(-1.0F, -8.0F, -7.0F, 2, 1, 2);
      this.setRotation(this.beak, 0.0F, 0.0F, 0.0F);
      this.chest.addChild(this.beak);
      this.wattle = new ModelRenderer(this, 14, 32);
      this.wattle.addBox(-1.0F, -7.0F, -6.0F, 2, 7, 1);
      this.setRotation(this.wattle, 0.0F, 0.0F, 0.0F);
      this.chest.addChild(this.wattle);
      this.plume = new ModelRenderer(this, 52, 0);
      this.plume.addBox(-11.0F, -13.0F, 6.0F, 22, 14, 0);
      this.plume.setRotationPoint(0.0F, 12.0F, 0.0F);
      this.setRotation(this.plume, -0.2617994F, 0.0F, 0.0F);
      this.leftWing = new ModelRenderer(this, 26, 22);
      this.leftWing.addBox(0.0F, 0.0F, 0.0F, 1, 6, 8);
      this.leftWing.setRotationPoint(6.0F, 11.0F, -4.0F);
      this.setRotation(this.leftWing, 0.0F, 0.0F, 0.0F);
      this.rightWing = new ModelRenderer(this, 26, 22);
      this.rightWing.addBox(-1.0F, 0.0F, 0.0F, 1, 6, 8);
      this.rightWing.setRotationPoint(-6.0F, 11.0F, -4.0F);
      this.setRotation(this.rightWing, 0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.6F, 0.6F, 0.6F);
         GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
         this.body.render(scale);
         this.leftLeg.render(scale);
         this.rightLeg.render(scale);
         this.chest.render(scale);
         this.leftWing.render(scale);
         this.rightWing.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.body.render(scale);
         this.leftLeg.render(scale);
         this.rightLeg.render(scale);
         this.chest.render(scale);
         if (((EntityTurkey)entityIn).getTurkeyType() == 0) {
            this.plume.render(scale);
         }

         this.leftWing.render(scale);
         this.rightWing.render(scale);
      }

   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      this.chest.rotateAngleX = headPitch * 0.017453292F + 0.7853982F;
      this.neck.rotateAngleX = headPitch * 0.017453292F - 0.7853982F;
      this.beak.rotateAngleX = headPitch * 0.017453292F - 0.7853982F;
      this.wattle.rotateAngleX = headPitch * 0.017453292F - 0.7853982F;
      this.chest.rotateAngleY = netHeadYaw * 0.017453292F;
      this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
      this.rightWing.rotateAngleZ = ageInTicks;
      this.leftWing.rotateAngleZ = -ageInTicks;
   }
}
