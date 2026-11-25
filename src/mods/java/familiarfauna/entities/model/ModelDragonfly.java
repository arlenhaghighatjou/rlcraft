package familiarfauna.entities.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDragonfly extends ModelBase {
   ModelRenderer body;
   ModelRenderer rightwing;
   ModelRenderer leftwing;
   ModelRenderer head;

   public ModelDragonfly() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.body = new ModelRenderer(this, 0, 0);
      this.body.addBox(0.0F, -3.0F, -1.0F, 1, 10, 1);
      this.body.setRotationPoint(0.0F, 21.0F, 2.0F);
      this.setRotation(this.body, 1.570796F, 0.0F, 0.0F);
      this.rightwing = new ModelRenderer(this, 0, 6);
      this.rightwing.addBox(-7.0F, 0.0F, -4.0F, 7, 1, 7);
      this.rightwing.setRotationPoint(0.0F, 21.0F, 2.0F);
      this.setRotation(this.rightwing, 0.0F, 0.0F, 0.0F);
      this.leftwing = new ModelRenderer(this, 0, 16);
      this.leftwing.addBox(0.0F, 0.0F, -4.0F, 7, 1, 7);
      this.leftwing.setRotationPoint(1.0F, 21.0F, 2.0F);
      this.setRotation(this.leftwing, 0.0F, 0.0F, 0.0F);
      this.head = new ModelRenderer(this, 4, 0);
      this.head.addBox(-1.0F, 0.0F, -4.0F, 3, 1, 1);
      this.head.setRotationPoint(0.0F, 21.0F, 2.0F);
      this.setRotation(this.head, 0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.body.render(f5);
      this.rightwing.render(f5);
      this.leftwing.render(f5);
      this.head.render(f5);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
      super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.rightwing.rotateAngleZ = -(MathHelper.cos(f2 * 5.0F) * 3.1415927F * 0.15F);
      this.leftwing.rotateAngleZ = MathHelper.cos(f2 * 5.0F) * 3.1415927F * 0.15F;
   }
}
