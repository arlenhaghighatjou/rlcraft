package goblinbob.mobends.standard.client.model.armor;

import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.core.math.TransformUtils;
import goblinbob.mobends.core.math.matrix.IMat4x4d;
import goblinbob.mobends.core.math.physics.ICollider;
import goblinbob.mobends.core.math.vector.IVec3f;
import goblinbob.mobends.core.math.vector.Vec3f;
import goblinbob.mobends.core.util.GlHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PartContainer extends ModelRenderer implements IModelPart {
   public Vec3f position;
   public Vec3f offset;
   public Vec3f innerOffset;
   public Vec3f scale;
   public SmoothOrientation rotation;
   public float offsetScale = 1.0F;
   public Vec3f globalOffset = new Vec3f();
   private ModelRenderer innerModel;
   protected IModelPart parent;
   protected ICollider collider;

   public PartContainer(ModelBase modelBase, ModelRenderer model) {
      super(modelBase, 0, 0);
      this.innerModel = model;
      this.position = new Vec3f();
      this.offset = new Vec3f();
      this.innerOffset = new Vec3f();
      this.scale = new Vec3f(1.0F, 1.0F, 1.0F);
      this.rotation = new SmoothOrientation();
      this.mirror = model.mirror;
   }

   public ModelRenderer getModel() {
      return this.innerModel;
   }

   public IModelPart getParent() {
      return this.parent;
   }

   public PartContainer setParent(IModelPart parent) {
      this.parent = parent;
      return this;
   }

   public PartContainer setPosition(float x, float y, float z) {
      this.position.set(x, y, z);
      return this;
   }

   public PartContainer setInnerOffset(float x, float y, float z) {
      this.innerOffset.set(x, y, z);
      return this;
   }

   private void renderContainedModel(float scale) {
      float x = this.innerModel.rotationPointX;
      float y = this.innerModel.rotationPointY;
      float z = this.innerModel.rotationPointZ;
      float ox = this.innerModel.offsetX;
      float oy = this.innerModel.offsetY;
      float oz = this.innerModel.offsetZ;
      this.innerModel.rotateAngleX = this.innerModel.rotateAngleY = this.innerModel.rotateAngleZ = 0.0F;
      this.innerModel.rotationPointX = this.innerModel.rotationPointY = this.innerModel.rotationPointZ = 0.0F;
      this.innerModel.offsetX = this.innerModel.offsetY = this.innerModel.offsetZ = 0.0F;
      this.innerModel.showModel = true;
      this.innerModel.isHidden = false;
      this.innerModel.render(scale);
      this.innerModel.rotationPointX = x;
      this.innerModel.rotationPointY = y;
      this.innerModel.rotationPointZ = z;
      this.innerModel.offsetX = ox;
      this.innerModel.offsetY = oy;
      this.innerModel.offsetZ = oz;
   }

   @SideOnly(Side.CLIENT)
   public void render(float scale) {
      this.renderPart(scale);
   }

   public void renderPart(float scale) {
      if (this.isShowing()) {
         GlStateManager.pushMatrix();
         this.applyCharacterTransform(scale);
         if (this.innerOffset.x != 0.0F || this.innerOffset.y != 0.0F || this.innerOffset.z != 0.0F) {
            GlStateManager.translate(this.innerOffset.x * scale, this.innerOffset.y * scale, this.innerOffset.z * scale);
         }

         this.renderContainedModel(scale);
         if (this.childModels != null) {
            for(int k = 0; k < this.childModels.size(); ++k) {
               ((ModelRenderer)this.childModels.get(k)).render(scale);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   public void renderJustPart(float scale) {
      if (this.isShowing()) {
         GlStateManager.pushMatrix();
         this.applyLocalTransform(scale);
         if (this.innerOffset.x != 0.0F || this.innerOffset.y != 0.0F || this.innerOffset.z != 0.0F) {
            GlStateManager.translate(this.innerOffset.x * scale, this.innerOffset.y * scale, this.innerOffset.z * scale);
         }

         this.renderContainedModel(scale);
         if (this.childModels != null) {
            for(int k = 0; k < this.childModels.size(); ++k) {
               ((ModelRenderer)this.childModels.get(k)).render(scale);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   @SideOnly(Side.CLIENT)
   public void postRender(float scale) {
      this.applyCharacterTransform(scale);
      this.applyPostTransform(scale);
   }

   public void applyCharacterTransform(float scale) {
      if (this.parent != null) {
         this.parent.applyCharacterTransform(scale);
      }

      this.applyLocalTransform(scale);
   }

   public void applyLocalTransform(float scale) {
      if (this.position.x != 0.0F || this.position.y != 0.0F || this.position.z != 0.0F) {
         GlStateManager.translate(this.position.x * scale * this.offsetScale, this.position.y * scale * this.offsetScale, this.position.z * scale * this.offsetScale);
      }

      if (this.offset.x != 0.0F || this.offset.y != 0.0F || this.offset.z != 0.0F) {
         GlStateManager.translate(this.offset.x * scale * this.offsetScale, this.offset.y * scale * this.offsetScale, this.offset.z * scale * this.offsetScale);
      }

      GlHelper.rotate(this.rotation.getSmooth());
      if (this.scale.x != 0.0F || this.scale.y != 0.0F || this.scale.z != 0.0F) {
         GlStateManager.scale(this.scale.x, this.scale.y, this.scale.z);
      }

   }

   public void applyPostTransform(float scale) {
   }

   public void update(float ticksPerFrame) {
      this.rotation.update(ticksPerFrame);
   }

   public Vec3f getPosition() {
      return this.position;
   }

   public Vec3f getScale() {
      return this.scale;
   }

   public Vec3f getOffset() {
      return this.offset;
   }

   public SmoothOrientation getRotation() {
      return this.rotation;
   }

   public float getOffsetScale() {
      return this.offsetScale;
   }

   public IVec3f getGlobalOffset() {
      return this.globalOffset;
   }

   public void syncUp(IModelPart part) {
      if (part != null) {
         this.position.set(part.getPosition());
         this.rotation.set(part.getRotation());
         this.offset.set(part.getOffset());
         this.scale.set(part.getScale());
         this.offsetScale = part.getOffsetScale();
      }
   }

   public boolean isShowing() {
      return this.showModel && !this.isHidden;
   }

   public void setVisible(boolean showModel) {
      this.showModel = showModel;
   }

   public void applyPreTransform(float scale) {
      if (this.globalOffset.x != 0.0F || this.globalOffset.y != 0.0F || this.globalOffset.z != 0.0F) {
         GlStateManager.translate(this.globalOffset.x * scale, this.globalOffset.y * scale, this.globalOffset.z * scale);
      }

   }

   public void applyPreTransform(float scale, IMat4x4d dest) {
      if (this.globalOffset.x != 0.0F || this.globalOffset.y != 0.0F || this.globalOffset.z != 0.0F) {
         TransformUtils.translate(dest, (double)(this.globalOffset.x * scale), (double)(this.globalOffset.y * scale), (double)(this.globalOffset.z * scale));
      }

   }

   public void applyLocalTransform(float scale, IMat4x4d matrix) {
      if (this.position.x != 0.0F || this.position.y != 0.0F || this.position.z != 0.0F) {
         TransformUtils.translate(matrix, (double)(this.position.x * scale * this.offsetScale), (double)(this.position.y * scale * this.offsetScale), (double)(this.position.z * scale * this.offsetScale), matrix);
      }

      if (this.offset.x != 0.0F || this.offset.y != 0.0F || this.offset.z != 0.0F) {
         TransformUtils.translate(matrix, (double)(this.offset.x * scale * this.offsetScale), (double)(this.offset.y * scale * this.offsetScale), (double)(this.offset.z * scale * this.offsetScale));
      }

      TransformUtils.rotate(matrix, this.rotation.getSmooth());
      if (this.scale.x != 0.0F || this.scale.y != 0.0F || this.scale.z != 0.0F) {
         TransformUtils.scale(matrix, (double)this.scale.x, (double)this.scale.y, (double)this.scale.z);
      }

   }
}
