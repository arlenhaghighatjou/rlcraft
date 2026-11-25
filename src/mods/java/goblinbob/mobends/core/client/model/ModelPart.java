package goblinbob.mobends.core.client.model;

import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.core.math.TransformUtils;
import goblinbob.mobends.core.math.matrix.IMat4x4d;
import goblinbob.mobends.core.math.physics.AABBoxGroup;
import goblinbob.mobends.core.math.physics.IAABBox;
import goblinbob.mobends.core.math.physics.ICollider;
import goblinbob.mobends.core.math.vector.IVec3f;
import goblinbob.mobends.core.math.vector.Vec3f;
import goblinbob.mobends.core.util.GlHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelPart extends ModelRenderer implements IModelPart {
   public Vec3f position;
   public Vec3f scale;
   public Vec3f offset;
   public SmoothOrientation rotation;
   public float offsetScale;
   public Vec3f globalOffset;
   protected List<MutatedBox> mutatedBoxes;
   protected IModelPart parent;
   protected ICollider collider;

   public ModelPart(ModelBase model, boolean register, int texOffsetX, int texOffsetY) {
      super(model, texOffsetX, texOffsetY);
      this.position = new Vec3f();
      this.scale = new Vec3f(1.0F, 1.0F, 1.0F);
      this.offset = new Vec3f();
      this.rotation = new SmoothOrientation();
      this.offsetScale = 1.0F;
      this.globalOffset = new Vec3f();
      this.mutatedBoxes = new ArrayList();
      if (!register) {
         model.boxList.remove(model.boxList.size() - 1);
      }

   }

   public ModelPart(ModelBase model, boolean register) {
      this(model, register, 0, 0);
   }

   public ModelPart(ModelBase model, int texOffsetX, int texOffsetY) {
      this(model, true, texOffsetX, texOffsetY);
   }

   @SideOnly(Side.CLIENT)
   public void render(float scale) {
      this.renderPart(scale);
   }

   @SideOnly(Side.CLIENT)
   public void renderWithRotation(float scale) {
      this.renderPart(scale);
   }

   public void renderPart(float scale) {
      if (this.isShowing()) {
         if (!this.compiled) {
            this.compileDisplayList(scale);
         }

         GlStateManager.pushMatrix();
         this.applyCharacterTransform(scale);
         GlStateManager.callList(this.displayList);
         if (this.childModels != null) {
            for(ModelRenderer childModel : this.childModels) {
               childModel.render(scale);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   public void renderJustPart(float scale) {
      if (this.isShowing()) {
         if (!this.compiled) {
            this.compileDisplayList(scale);
         }

         GlStateManager.pushMatrix();
         this.applyLocalTransform(scale);
         GlStateManager.callList(this.displayList);
         if (this.childModels != null) {
            for(ModelRenderer childModel : this.childModels) {
               childModel.render(scale);
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

   public void applyLocalTransform(float scale, IMat4x4d matrix) {
      if (this.position.x != 0.0F || this.position.y != 0.0F || this.position.z != 0.0F) {
         TransformUtils.translate(matrix, (double)(this.position.x * scale * this.offsetScale), (double)(this.position.y * scale * this.offsetScale), (double)(this.position.z * scale * this.offsetScale));
      }

      if (this.offset.x != 0.0F || this.offset.y != 0.0F || this.offset.z != 0.0F) {
         TransformUtils.translate(matrix, (double)(this.offset.x * scale * this.offsetScale), (double)(this.offset.y * scale * this.offsetScale), (double)(this.offset.z * scale * this.offsetScale));
      }

      TransformUtils.rotate(matrix, this.rotation.getSmooth());
      if (this.scale.x != 0.0F || this.scale.y != 0.0F || this.scale.z != 0.0F) {
         TransformUtils.scale(matrix, (double)this.scale.x, (double)this.scale.y, (double)this.scale.z, matrix);
      }

   }

   public void applyPostTransform(float scale) {
   }

   @SideOnly(Side.CLIENT)
   protected void compileDisplayList(float scale) {
      this.displayList = GLAllocation.generateDisplayLists(1);
      GlStateManager.glNewList(this.displayList, 4864);
      WorldRenderer bufferbuilder = Tessellator.getInstance().getBuffer();

      for(ModelBox modelBox : this.cubeList) {
         modelBox.render(bufferbuilder, scale);
      }

      GlStateManager.glEndList();
      this.compiled = true;
   }

   public void update(float ticksPerFrame) {
      this.rotation.update(ticksPerFrame);
   }

   public void setRotationPoint(float x, float y, float z) {
   }

   public ModelPart setPosition(float x, float y, float z) {
      this.position.set(x, y, z);
      return this;
   }

   public ModelPart setOffset(float x, float y, float z) {
      this.offsetX = x;
      this.offsetY = y;
      this.offsetZ = z;
      return this;
   }

   public ModelPart setScale(float x, float y, float z) {
      this.scale.x = x;
      this.scale.y = y;
      this.scale.z = z;
      return this;
   }

   public ModelPart resetScale() {
      this.scale.set(0.0F, 0.0F, 0.0F);
      return this;
   }

   public BoxFactory developBox(float x, float y, float z, int dx, int dy, int dz, float scaleFactor) {
      return (new BoxFactory(x, y, z, dx, dy, dz, scaleFactor)).setTarget(this);
   }

   public ModelPart addVanillaBox(ModelBox box) {
      this.cubeList.add(box);
      this.compiled = false;
      return this;
   }

   public ModelPart addBox(MutatedBox box) {
      this.mutatedBoxes.add(box);
      this.cubeList.add(box);
      this.compiled = false;
      return this;
   }

   public ModelPart addModelBox(float x, float y, float z, int width, int height, int length, float scaleFactor) {
      return this.addBox(new MutatedBox(this, this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, length, scaleFactor));
   }

   public ModelPart addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
      partName = this.boxName + "." + partName;
      TextureOffset textureoffset = this.baseModel.getTextureOffset(partName);
      this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
      return this.addBox((MutatedBox)(new MutatedBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F)).setBoxName(partName));
   }

   public ModelPart addBox(float offX, float offY, float offZ, int width, int height, int depth) {
      return this.addBox(new MutatedBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F));
   }

   public ModelPart addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
      return this.addBox(new MutatedBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F, mirrored));
   }

   public void addBox(float x, float y, float z, int width, int height, int length, float scaleFactor) {
      this.addModelBox(x, y, z, width, height, length, scaleFactor);
   }

   public MutatedBox getBox() {
      return this.getBox(0);
   }

   public MutatedBox getBox(int idx) {
      return (MutatedBox)this.cubeList.get(idx);
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

   public IModelPart getParent() {
      return this.parent;
   }

   public boolean isShowing() {
      return this.showModel && !this.isHidden;
   }

   protected void updateBounds() {
      if (this.mutatedBoxes.size() == 1) {
         this.collider = ((MutatedBox)this.mutatedBoxes.get(0)).createAABB();
      } else {
         IAABBox[] bounds = new IAABBox[this.mutatedBoxes.size()];

         for(int i = 0; i < bounds.length; ++i) {
            bounds[i] = ((MutatedBox)this.mutatedBoxes.get(i)).createAABB();
         }

         this.collider = new AABBoxGroup(bounds);
      }

   }

   public ModelPart setMirror(boolean mirror) {
      this.mirror = mirror;
      return this;
   }

   public void finish() {
      this.rotation.finish();
   }

   public void syncUp(IModelPart part) {
      if (part != null) {
         this.position.set(part.getPosition());
         this.offset.set(part.getOffset());
         this.rotation.set(part.getRotation());
         this.scale.set(part.getScale());
         this.offsetScale = part.getOffsetScale();
         this.globalOffset.set(part.getGlobalOffset());
      }
   }

   public void setVisible(boolean showModel) {
      this.showModel = showModel;
   }

   public ModelPart setParent(IModelPart parent) {
      this.parent = parent;
      return this;
   }

   public int getTextureOffsetX() {
      return this.textureOffsetX;
   }

   public int getTextureOffsetY() {
      return this.textureOffsetY;
   }
}
