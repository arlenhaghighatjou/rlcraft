package vazkii.quark.world.client.model;

import java.util.Random;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelWraith extends ModelBase {
   public final ModelRenderer body;
   public final ModelRenderer rightArm;
   public final ModelRenderer leftArm;

   public ModelWraith() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.body = new ModelRenderer(this, 0, 0);
      this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.body.addBox(-4.0F, -8.0F, -4.0F, 8, 24, 8, 0.0F);
      this.leftArm = new ModelRenderer(this, 32, 16);
      this.leftArm.mirror = true;
      this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
      this.rightArm = new ModelRenderer(this, 32, 16);
      this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      GlStateManager.pushMatrix();
      Random rng = new Random((long)entity.getEntityId());
      float time = (float)entity.ticksExisted + f + (float)rng.nextInt(10000000);
      GlStateManager.translate(0.0D, Math.sin((double)(time / 16.0F)) * 0.1D + 0.15D, 0.0D);
      this.leftArm.rotateAngleX = (float)Math.toRadians((double)(-50.0F + rng.nextFloat() * 20.0F));
      this.rightArm.rotateAngleX = (float)Math.toRadians((double)(-50.0F + rng.nextFloat() * 20.0F));
      this.leftArm.rotateAngleZ = (float)Math.toRadians((double)(-110.0F + (float)Math.cos((double)(time / (float)(8 + rng.nextInt(2)))) * (8.0F + rng.nextFloat() * 8.0F)));
      this.rightArm.rotateAngleZ = (float)Math.toRadians((double)(110.0F + (float)Math.cos((double)((time + 300.0F) / (float)(8 + rng.nextInt(2)))) * (8.0F + rng.nextFloat() * 8.0F)));
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      float r = 1.0F;
      float g = 1.0F;
      float b = 1.0F;
      float mute = 0.5F;
      if (rng.nextInt(80) == 0) {
         switch(rng.nextInt(6)) {
         case 0:
            g = mute;
            b = mute;
            break;
         case 1:
            r = mute;
            b = mute;
            break;
         case 2:
            r = mute;
            g = mute;
            break;
         case 3:
            r = mute;
            break;
         case 4:
            g = mute;
            break;
         case 5:
            b = mute;
         }
      }

      GlStateManager.color(r, g, b, 0.7F + (float)Math.sin((double)(time / 20.0F)) * 0.3F);
      this.body.render(f5);
      this.leftArm.render(f5);
      this.rightArm.render(f5);
      GlStateManager.popMatrix();
   }
}
