package net.ilexiconn.llibrary.client.model.tools;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AdvancedModelRenderer extends ModelRenderer {
   public float defaultRotationX;
   public float defaultRotationY;
   public float defaultRotationZ;
   public float defaultOffsetX;
   public float defaultOffsetY;
   public float defaultOffsetZ;
   public float defaultPositionX;
   public float defaultPositionY;
   public float defaultPositionZ;
   public float scaleX;
   public float scaleY;
   public float scaleZ;
   public float opacity;
   public int textureOffsetX;
   public int textureOffsetY;
   public boolean scaleChildren;
   private AdvancedModelBase model;
   private AdvancedModelRenderer parent;
   private int displayList;
   private boolean compiled;

   public AdvancedModelRenderer(AdvancedModelBase model, String name) {
      super(model, name);
      this.scaleX = 1.0F;
      this.scaleY = 1.0F;
      this.scaleZ = 1.0F;
      this.opacity = 1.0F;
      this.model = model;
   }

   public AdvancedModelRenderer(AdvancedModelBase model) {
      this(model, (String)null);
   }

   public AdvancedModelRenderer(AdvancedModelBase model, int textureOffsetX, int textureOffsetY) {
      this(model);
      this.setTextureOffset(textureOffsetX, textureOffsetY);
   }

   public AdvancedModelRenderer add3DTexture(float posX, float posY, float posZ, int width, int height) {
      this.cubeList.add(new Model3DTexture(this, this.textureOffsetX, this.textureOffsetY, posX, posY, posZ, width, height));
      return this;
   }

   public ModelRenderer addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
      partName = this.boxName + "." + partName;
      TextureOffset textureoffset = this.model.getTextureOffset(partName);
      this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
      this.cubeList.add((new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F)).setBoxName(partName));
      return this;
   }

   public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F));
      return this;
   }

   public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F, mirrored));
      return this;
   }

   public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor));
   }

   public void setShouldScaleChildren(boolean scaleChildren) {
      this.scaleChildren = scaleChildren;
   }

   public void setScale(float scaleX, float scaleY, float scaleZ) {
      this.scaleX = scaleX;
      this.scaleY = scaleY;
      this.scaleZ = scaleZ;
   }

   public void setScaleX(float scaleX) {
      this.scaleX = scaleX;
   }

   public void setScaleY(float scaleY) {
      this.scaleY = scaleY;
   }

   public void setScaleZ(float scaleZ) {
      this.scaleZ = scaleZ;
   }

   public void setOpacity(float opacity) {
      this.opacity = opacity;
   }

   public void updateDefaultPose() {
      this.defaultRotationX = this.rotateAngleX;
      this.defaultRotationY = this.rotateAngleY;
      this.defaultRotationZ = this.rotateAngleZ;
      this.defaultOffsetX = this.offsetX;
      this.defaultOffsetY = this.offsetY;
      this.defaultOffsetZ = this.offsetZ;
      this.defaultPositionX = this.rotationPointX;
      this.defaultPositionY = this.rotationPointY;
      this.defaultPositionZ = this.rotationPointZ;
   }

   public void resetToDefaultPose() {
      this.rotateAngleX = this.defaultRotationX;
      this.rotateAngleY = this.defaultRotationY;
      this.rotateAngleZ = this.defaultRotationZ;
      this.offsetX = this.defaultOffsetX;
      this.offsetY = this.defaultOffsetY;
      this.offsetZ = this.defaultOffsetZ;
      this.rotationPointX = this.defaultPositionX;
      this.rotationPointY = this.defaultPositionY;
      this.rotationPointZ = this.defaultPositionZ;
   }

   public void addChild(ModelRenderer child) {
      super.addChild(child);
      if (child instanceof AdvancedModelRenderer) {
         AdvancedModelRenderer advancedChild = (AdvancedModelRenderer)child;
         advancedChild.setParent(this);
      }

   }

   public AdvancedModelRenderer getParent() {
      return this.parent;
   }

   public void setParent(AdvancedModelRenderer parent) {
      this.parent = parent;
   }

   public void parentedPostRender(float scale) {
      if (this.parent != null) {
         this.parent.parentedPostRender(scale);
      }

      this.postRender(scale);
   }

   public void renderWithParents(float scale) {
      if (this.parent != null) {
         this.parent.renderWithParents(scale);
      }

      this.render(scale);
   }

   public void render(float scale) {
      if (!this.isHidden && this.showModel) {
         GlStateManager.pushMatrix();
         if (!this.compiled) {
            this.compileDisplayList(scale);
         }

         GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
         GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
         if (this.rotateAngleZ != 0.0F) {
            GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
         }

         if (this.rotateAngleY != 0.0F) {
            GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleY), 0.0F, 1.0F, 0.0F);
         }

         if (this.rotateAngleX != 0.0F) {
            GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleX), 1.0F, 0.0F, 0.0F);
         }

         if (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F) {
            GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
         }

         if (this.opacity != 1.0F) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, this.opacity);
         }

         GlStateManager.callList(this.displayList);
         if (this.opacity != 1.0F) {
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         if (!this.scaleChildren && (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F)) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
            GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleY), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleX), 1.0F, 0.0F, 0.0F);
            }
         }

         if (this.childModels != null) {
            for(ModelRenderer childModel : this.childModels) {
               childModel.render(scale);
            }
         }

         GlStateManager.popMatrix();
      }

   }

   private void compileDisplayList(float scale) {
      this.displayList = GLAllocation.generateDisplayLists(1);
      GlStateManager.glNewList(this.displayList, 4864);
      WorldRenderer buffer = Tessellator.getInstance().getBuffer();

      for(ModelBox box : this.cubeList) {
         box.render(buffer, scale);
      }

      GlStateManager.glEndList();
      this.compiled = true;
   }

   public AdvancedModelBase getModel() {
      return this.model;
   }

   private float calculateRotation(float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
      float movementScale = this.model.getMovementScale();
      float rotation = MathHelper.cos(f * speed * movementScale + offset) * degree * movementScale * f1 + weight * f1;
      return invert ? -rotation : rotation;
   }

   public void walk(float speed, float degree, boolean invert, float offset, float weight, float walk, float walkAmount) {
      this.rotateAngleX += this.calculateRotation(speed, degree, invert, offset, weight, walk, walkAmount);
   }

   public void flap(float speed, float degree, boolean invert, float offset, float weight, float flap, float flapAmount) {
      this.rotateAngleZ += this.calculateRotation(speed, degree, invert, offset, weight, flap, flapAmount);
   }

   public void swing(float speed, float degree, boolean invert, float offset, float weight, float swing, float swingAmount) {
      this.rotateAngleY += this.calculateRotation(speed, degree, invert, offset, weight, swing, swingAmount);
   }

   public void bob(float speed, float degree, boolean bounce, float f, float f1) {
      float movementScale = this.model.getMovementScale();
      degree = degree * movementScale;
      speed = speed * movementScale;
      float bob = (float)(Math.sin((double)(f * speed)) * (double)f1 * (double)degree - (double)(f1 * degree));
      if (bounce) {
         bob = (float)(-Math.abs(Math.sin((double)(f * speed)) * (double)f1 * (double)degree));
      }

      this.rotationPointY += bob;
   }

   public AdvancedModelRenderer setTextureOffset(int textureOffsetX, int textureOffsetY) {
      this.textureOffsetX = textureOffsetX;
      this.textureOffsetY = textureOffsetY;
      return this;
   }

   public void transitionTo(AdvancedModelRenderer to, float timer, float maxTime) {
      this.rotateAngleX += (to.rotateAngleX - this.rotateAngleX) / maxTime * timer;
      this.rotateAngleY += (to.rotateAngleY - this.rotateAngleY) / maxTime * timer;
      this.rotateAngleZ += (to.rotateAngleZ - this.rotateAngleZ) / maxTime * timer;
      this.rotationPointX += (to.rotationPointX - this.rotationPointX) / maxTime * timer;
      this.rotationPointY += (to.rotationPointY - this.rotationPointY) / maxTime * timer;
      this.rotationPointZ += (to.rotationPointZ - this.rotationPointZ) / maxTime * timer;
      this.offsetX += (to.offsetX - this.offsetX) / maxTime * timer;
      this.offsetY += (to.offsetY - this.offsetY) / maxTime * timer;
      this.offsetZ += (to.offsetZ - this.offsetZ) / maxTime * timer;
   }

   public Vec3d getWorldPos(Entity entity) {
      Vec3d modelPos = this.getModelPos(this, new Vec3d((double)(this.rotationPointX / 16.0F), (double)(this.rotationPointY / 16.0F), (double)(this.rotationPointZ / 16.0F)));
      double x = modelPos.x;
      double y = modelPos.y + 1.5D;
      double z = modelPos.z;
      Matrix4d entityTranslate = new Matrix4d();
      Matrix4d entityRotate = new Matrix4d();
      entityTranslate.set(new Vector3d(entity.posX, entity.posY, entity.posZ));
      entityRotate.rotY(-Math.toRadians((double)entity.rotationYaw));
      Point3d rendererPos = new Point3d(x, y, z);
      entityRotate.transform(rendererPos);
      entityTranslate.transform(rendererPos);
      return new Vec3d(rendererPos.getX(), rendererPos.getY(), rendererPos.getZ());
   }

   public Vec3d getModelPos(AdvancedModelRenderer modelRenderer, Vec3d recurseValue) {
      double x = recurseValue.x;
      double y = recurseValue.y;
      double z = recurseValue.z;
      Point3d rendererPos = new Point3d(x, y, z);
      AdvancedModelRenderer parent = modelRenderer.getParent();
      if (parent != null) {
         Matrix4d boxTranslate = new Matrix4d();
         Matrix4d boxRotateX = new Matrix4d();
         Matrix4d boxRotateY = new Matrix4d();
         Matrix4d boxRotateZ = new Matrix4d();
         boxTranslate.set(new Vector3d((double)(parent.rotationPointX / 16.0F), (double)(-parent.rotationPointY / 16.0F), (double)(-parent.rotationPointZ / 16.0F)));
         boxRotateX.rotX((double)parent.rotateAngleX);
         boxRotateY.rotY((double)(-parent.rotateAngleY));
         boxRotateZ.rotZ((double)(-parent.rotateAngleZ));
         boxRotateX.transform(rendererPos);
         boxRotateY.transform(rendererPos);
         boxRotateZ.transform(rendererPos);
         boxTranslate.transform(rendererPos);
         return this.getModelPos(parent, new Vec3d(rendererPos.getX(), rendererPos.getY(), rendererPos.getZ()));
      } else {
         return new Vec3d(rendererPos.getX(), rendererPos.getY(), rendererPos.getZ());
      }
   }
}
