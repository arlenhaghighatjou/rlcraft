package familiarfauna.entities.model;

import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDeer extends ModelQuadruped {
   public ModelRenderer neck;
   public ModelRenderer snout;
   public ModelRenderer ear1;
   public ModelRenderer ear2;
   public ModelRenderer tail;
   public ModelRenderer antler1;
   public ModelRenderer antler2;
   protected float childYOffset = 8.0F;
   protected float childZOffset = 4.0F;

   public ModelDeer() {
      super(14, 0.0F);
      this.textureWidth = 128;
      this.textureHeight = 32;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-3.0F, -3.0F, -6.0F, 6, 6, 6);
      this.head.setRotationPoint(0.0F, 3.0F, -9.0F);
      this.setRotation(this.head, 0.0F, 0.0F, 0.0F);
      this.snout = new ModelRenderer(this, 24, 0);
      this.snout.addBox(-2.0F, -1.5F, -10.0F, 4, 4, 4);
      this.setRotation(this.snout, 0.0F, 0.0F, 0.0F);
      this.head.addChild(this.snout);
      this.ear1 = new ModelRenderer(this, 32, 16);
      this.ear1.addBox(-1.0F, -8.0F, -2.0F, 3, 6, 1);
      this.setRotation(this.ear1, 0.0F, 0.0F, -1.047198F);
      this.head.addChild(this.ear1);
      this.ear2 = new ModelRenderer(this, 32, 23);
      this.ear2.addBox(-2.0F, -8.0F, -2.0F, 3, 6, 1);
      this.setRotation(this.ear2, 0.0F, 0.0F, 1.047198F);
      this.head.addChild(this.ear2);
      this.antler1 = new ModelRenderer(this, 70, 18);
      this.antler1.addBox(-7.0F, -14.0F, -2.0F, 8, 12, 1);
      this.setRotation(this.antler1, -0.5235988F, -1.047198F, 0.0F);
      this.head.addChild(this.antler1);
      this.antler2 = new ModelRenderer(this, 88, 18);
      this.antler2.addBox(-1.0F, -14.0F, -2.0F, 8, 12, 1);
      this.setRotation(this.antler2, -0.5235988F, 1.047198F, 0.0F);
      this.head.addChild(this.antler2);
      this.neck = new ModelRenderer(this, 0, 12);
      this.neck.addBox(-2.0F, 0.0F, -9.0F, 4, 6, 9);
      this.neck.setRotationPoint(0.0F, 6.0F, -2.0F);
      this.setRotation(this.neck, -0.6283185F, 0.0F, 0.0F);
      this.body = new ModelRenderer(this, 40, 0);
      this.body.addBox(-4.0F, -9.0F, -7.0F, 8, 18, 7);
      this.body.setRotationPoint(0.0F, 5.0F, 1.0F);
      this.setRotation(this.body, 1.570796F, 0.0F, 0.0F);
      this.leg1 = new ModelRenderer(this, 70, 0);
      this.leg1.addBox(0.0F, 0.0F, 0.0F, 3, 15, 3);
      this.leg1.setRotationPoint(-4.2F, 9.0F, 6.0F);
      this.setRotation(this.leg1, 0.0F, 0.0F, 0.0F);
      this.leg2 = new ModelRenderer(this, 82, 0);
      this.leg2.addBox(-3.0F, 0.0F, 0.0F, 3, 15, 3);
      this.leg2.setRotationPoint(4.2F, 9.0F, 6.0F);
      this.setRotation(this.leg2, 0.0F, 0.0F, 0.0F);
      this.leg3 = new ModelRenderer(this, 94, 0);
      this.leg3.addBox(0.0F, 0.0F, 0.0F, 3, 15, 3);
      this.leg3.setRotationPoint(-4.2F, 9.0F, -7.0F);
      this.setRotation(this.leg3, 0.0F, 0.0F, 0.0F);
      this.leg4 = new ModelRenderer(this, 106, 0);
      this.leg4.addBox(-3.0F, 0.0F, 0.0F, 3, 15, 3);
      this.leg4.setRotationPoint(4.2F, 9.0F, -7.0F);
      this.setRotation(this.leg4, 0.0F, 0.0F, 0.0F);
      this.tail = new ModelRenderer(this, 32, 8);
      this.tail.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2);
      this.tail.setRotationPoint(-1.0F, 7.0F, 8.0F);
      this.setRotation(this.tail, 0.4363323F, 0.0F, 0.0F);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.head.render(scale);
         this.body.render(scale);
         this.leg1.render(scale);
         this.leg2.render(scale);
         this.leg3.render(scale);
         this.leg4.render(scale);
         this.neck.render(scale);
         this.tail.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.head.render(scale);
         this.body.render(scale);
         this.leg1.render(scale);
         this.leg2.render(scale);
         this.leg3.render(scale);
         this.leg4.render(scale);
         this.neck.render(scale);
         this.tail.render(scale);
      }

   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }
}
