package rustic.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCabinetDouble extends ModelBase {
   ModelRenderer bottom;
   ModelRenderer top;
   ModelRenderer back;
   ModelRenderer right;
   ModelRenderer left;
   public ModelRenderer door;
   ModelRenderer handle;

   public ModelCabinetDouble(boolean mirror) {
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.bottom = new ModelRenderer(this, 0, 0);
      this.bottom.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16);
      this.bottom.setRotationPoint(-8.0F, 23.0F, -8.0F);
      this.bottom.setTextureSize(128, 128);
      this.bottom.mirror = true;
      this.setRotation(this.bottom, 0.0F, 0.0F, 0.0F);
      this.top = new ModelRenderer(this, 64, 0);
      this.top.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16);
      this.top.setRotationPoint(-8.0F, -8.0F, -8.0F);
      this.top.setTextureSize(128, 128);
      this.top.mirror = true;
      this.setRotation(this.top, 0.0F, 0.0F, 0.0F);
      this.back = new ModelRenderer(this, 0, 17);
      this.back.addBox(0.0F, 0.0F, 0.0F, 16, 30, 1);
      this.back.setRotationPoint(-8.0F, -7.0F, 7.0F);
      this.back.setTextureSize(128, 128);
      this.back.mirror = true;
      this.setRotation(this.back, 0.0F, 0.0F, 0.0F);
      this.right = new ModelRenderer(this, 0, 48);
      this.right.addBox(0.0F, 0.0F, 0.0F, 1, 30, 15);
      this.right.setRotationPoint(-8.0F, -7.0F, -8.0F);
      this.right.setTextureSize(128, 128);
      this.right.mirror = true;
      this.setRotation(this.right, 0.0F, 0.0F, 0.0F);
      this.left = new ModelRenderer(this, 32, 48);
      this.left.addBox(0.0F, 0.0F, 0.0F, 1, 30, 15);
      this.left.setRotationPoint(7.0F, -7.0F, -8.0F);
      this.left.setTextureSize(128, 128);
      this.left.mirror = true;
      this.setRotation(this.left, 0.0F, 0.0F, 0.0F);
      this.door = new ModelRenderer(this, 0, 93);
      this.door.addBox(mirror ? -14.0F : 0.0F, -15.0F, -0.5F, 14, 30, 1);
      this.door.setRotationPoint(mirror ? 7.0F : -7.0F, 8.0F, -6.5F);
      this.door.setTextureSize(128, 128);
      this.door.mirror = true;
      this.setRotation(this.door, 0.0F, 0.5F, 0.0F);
      this.handle = new ModelRenderer(this, 0, 124);
      this.handle.addBox(mirror ? -13.0F : 12.0F, -1.0F, -1.5F, 1, 2, 1);
      this.handle.setRotationPoint(mirror ? 7.0F : -7.0F, 8.0F, -6.5F);
      this.handle.setTextureSize(128, 128);
      this.handle.mirror = true;
      this.setRotation(this.handle, 0.0F, 0.0F, 0.0F);
   }

   public void renderAll() {
      this.handle.rotateAngleY = this.door.rotateAngleY;
      this.bottom.render(0.0625F);
      this.top.render(0.0625F);
      this.back.render(0.0625F);
      this.right.render(0.0625F);
      this.left.render(0.0625F);
      this.door.render(0.0625F);
      this.handle.render(0.0625F);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
      this.bottom.render(f5);
      this.top.render(f5);
      this.back.render(f5);
      this.right.render(f5);
      this.left.render(f5);
      this.door.render(f5);
      this.handle.render(f5);
   }

   public void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
      super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
   }
}
