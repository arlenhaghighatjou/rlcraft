package familiarfauna.entities.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSnail extends ModelBase {
   ModelRenderer shell;
   ModelRenderer body;
   ModelRenderer lefteye;
   ModelRenderer righteye;

   public ModelSnail() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.shell = new ModelRenderer(this, 0, 0);
      this.shell.addBox(-1.0F, 0.0F, -3.0F, 3, 8, 8);
      this.shell.setRotationPoint(0.0F, 14.0F, 0.0F);
      this.setRotation(this.shell, 0.0F, 0.0F, 0.0F);
      this.body = new ModelRenderer(this, 0, 16);
      this.body.addBox(0.0F, 0.0F, 0.0F, 3, 2, 12);
      this.body.setRotationPoint(-1.0F, 22.0F, -6.0F);
      this.setRotation(this.body, 0.0F, 0.0F, 0.0F);
      this.lefteye = new ModelRenderer(this, 22, 12);
      this.lefteye.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
      this.lefteye.setRotationPoint(1.0F, 19.0F, -5.0F);
      this.setRotation(this.lefteye, 0.0F, 0.0F, 0.0F);
      this.righteye = new ModelRenderer(this, 22, 12);
      this.righteye.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
      this.righteye.setRotationPoint(-1.0F, 19.0F, -5.0F);
      this.setRotation(this.righteye, 0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.shell.render(f5);
      this.body.render(f5);
      this.lefteye.render(f5);
      this.righteye.render(f5);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
      super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
   }
}
