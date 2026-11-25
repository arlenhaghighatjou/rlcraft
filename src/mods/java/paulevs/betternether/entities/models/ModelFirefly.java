package paulevs.betternether.entities.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import paulevs.betternether.entities.EntityFirefly;

public class ModelFirefly extends ModelBase implements IModelTechne {
   ModelRenderer leg1;
   ModelRenderer leg2;
   ModelRenderer leg3;
   ModelRenderer leg4;
   ModelRenderer leg5;
   ModelRenderer leg6;
   ModelRenderer body;
   ModelRenderer glow;

   public ModelFirefly() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.leg1 = new ModelRenderer(this, 20, 0);
      this.leg1.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.leg1.setRotationPoint(-1.5F, 22.5F, -2.0F);
      this.leg1.setTextureSize(32, 32);
      this.leg1.mirror = true;
      this.setRotation(this.leg1, 0.0F, -0.7853982F, 0.535372F);
      this.leg2 = new ModelRenderer(this, 20, 0);
      this.leg2.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.leg2.setRotationPoint(-1.5F, 22.5F, 0.0F);
      this.leg2.setTextureSize(32, 32);
      this.leg2.mirror = true;
      this.setRotation(this.leg2, 0.0F, 0.0F, 0.535372F);
      this.leg3 = new ModelRenderer(this, 20, 0);
      this.leg3.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.leg3.setRotationPoint(-1.5F, 22.5F, 2.0F);
      this.leg3.setTextureSize(32, 32);
      this.leg3.mirror = true;
      this.setRotation(this.leg3, 0.0F, 0.7853982F, 0.535372F);
      this.leg4 = new ModelRenderer(this, 20, 0);
      this.leg4.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.leg4.setRotationPoint(1.5F, 22.5F, -2.0F);
      this.leg4.setTextureSize(32, 32);
      this.leg4.mirror = true;
      this.setRotation(this.leg4, 0.0F, -2.356194F, 0.535372F);
      this.leg5 = new ModelRenderer(this, 20, 0);
      this.leg5.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.leg5.setRotationPoint(1.5F, 22.5F, 0.0F);
      this.leg5.setTextureSize(32, 32);
      this.leg5.mirror = true;
      this.setRotation(this.leg5, 0.0F, 3.141593F, 0.535372F);
      this.leg6 = new ModelRenderer(this, 20, 0);
      this.leg6.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.leg6.setRotationPoint(1.5F, 22.5F, 2.0F);
      this.leg6.setTextureSize(32, 32);
      this.leg6.mirror = true;
      this.setRotation(this.leg6, 0.0F, 2.356194F, 0.535372F);
      this.body = new ModelRenderer(this, 0, 0);
      this.body.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
      this.body.setRotationPoint(-2.5F, 18.0F, -2.5F);
      this.body.setTextureSize(32, 32);
      this.body.mirror = true;
      this.setRotation(this.body, 0.0F, 0.0F, 0.0F);
      this.glow = new ModelRenderer(this, 0, 10);
      this.glow.addBox(0.0F, 0.0F, 0.0F, 6, 6, 6);
      this.glow.setRotationPoint(-3.0F, 17.5F, -3.0F);
      this.glow.setTextureSize(32, 32);
      this.glow.mirror = true;
      this.setRotation(this.glow, 0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      GL11.glPushMatrix();
      ((EntityFirefly)entity).transform();
      this.leg1.render(f5);
      this.leg2.render(f5);
      this.leg3.render(f5);
      this.leg4.render(f5);
      this.leg5.render(f5);
      this.leg6.render(f5);
      ((EntityFirefly)entity).bindColor();
      GlStateManager.enableBlend();
      GlStateManager.disableLighting();
      GlStateManager.disableAlpha();
      this.body.render(f5);
      this.glow.render(f5);
      GlStateManager.enableAlpha();
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GL11.glPopMatrix();
   }
}
