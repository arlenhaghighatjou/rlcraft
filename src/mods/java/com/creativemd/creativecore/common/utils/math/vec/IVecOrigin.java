package com.creativemd.creativecore.common.utils.math.vec;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.box.BoxCorner;
import com.creativemd.creativecore.common.utils.math.box.OrientatedBoundingBox;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public interface IVecOrigin {
   double offX();

   double offY();

   double offZ();

   double rotX();

   double rotY();

   double rotZ();

   double offXLast();

   double offYLast();

   double offZLast();

   double rotXLast();

   double rotYLast();

   double rotZLast();

   boolean isRotated();

   void offX(double var1);

   void offY(double var1);

   void offZ(double var1);

   void off(double var1, double var3, double var5);

   void rotX(double var1);

   void rotY(double var1);

   void rotZ(double var1);

   void rot(double var1, double var3, double var5);

   Vector3d center();

   void setCenter(Vector3d var1);

   Matrix3d rotation();

   Matrix3d rotationInv();

   Vector3d translation();

   void tick();

   IVecOrigin getParent();

   default double translationCombined(Axis axis) {
      return VectorUtils.get(axis, this.translation());
   }

   default void onlyRotateWithoutCenter(Vector3d vec) {
      this.rotation().transform(vec);
   }

   default void transformPointToWorld(Vector3d vec) {
      vec.sub(this.center());
      this.rotation().transform(vec);
      vec.add(this.center());
      vec.add(this.translation());
   }

   default void transformPointToFakeWorld(Vector3d vec) {
      vec.sub(this.translation());
      vec.sub(this.center());
      this.rotationInv().transform(vec);
      vec.add(this.center());
   }

   default Vec3d transformPointToWorld(Vec3d vec) {
      Vector3d real = new Vector3d(vec.x, vec.y, vec.z);
      this.transformPointToWorld(real);
      return new Vec3d(real.x, real.y, real.z);
   }

   default Vec3d transformPointToFakeWorld(Vec3d vec) {
      Vector3d real = new Vector3d(vec.x, vec.y, vec.z);
      this.transformPointToFakeWorld(real);
      return new Vec3d(real.x, real.y, real.z);
   }

   default AxisAlignedBB getAxisAlignedBox(AxisAlignedBB box) {
      double minX = Double.MAX_VALUE;
      double minY = Double.MAX_VALUE;
      double minZ = Double.MAX_VALUE;
      double maxX = -1.7976931348623157E308D;
      double maxY = -1.7976931348623157E308D;
      double maxZ = -1.7976931348623157E308D;

      for(int i = 0; i < BoxCorner.values().length; ++i) {
         Vector3d vec = BoxCorner.values()[i].getVector(box);
         this.transformPointToWorld(vec);
         minX = Math.min(minX, vec.x);
         minY = Math.min(minY, vec.y);
         minZ = Math.min(minZ, vec.z);
         maxX = Math.max(maxX, vec.x);
         maxY = Math.max(maxY, vec.y);
         maxZ = Math.max(maxZ, vec.z);
      }

      return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   default OrientatedBoundingBox getOrientatedBox(AxisAlignedBB box) {
      double minX = Double.MAX_VALUE;
      double minY = Double.MAX_VALUE;
      double minZ = Double.MAX_VALUE;
      double maxX = -1.7976931348623157E308D;
      double maxY = -1.7976931348623157E308D;
      double maxZ = -1.7976931348623157E308D;

      for(int i = 0; i < BoxCorner.values().length; ++i) {
         Vector3d vec = BoxCorner.values()[i].getVector(box);
         this.transformPointToFakeWorld(vec);
         minX = Math.min(minX, vec.x);
         minY = Math.min(minY, vec.y);
         minZ = Math.min(minZ, vec.z);
         maxX = Math.max(maxX, vec.x);
         maxY = Math.max(maxY, vec.y);
         maxZ = Math.max(maxZ, vec.z);
      }

      return new OrientatedBoundingBox(this, minX, minY, minZ, maxX, maxY, maxZ);
   }

   @SideOnly(Side.CLIENT)
   default void setupRenderingInternal(Entity entity, float partialTicks) {
      double rotX = this.rotXLast() + (this.rotX() - this.rotXLast()) * (double)partialTicks;
      double rotY = this.rotYLast() + (this.rotY() - this.rotYLast()) * (double)partialTicks;
      double rotZ = this.rotZLast() + (this.rotZ() - this.rotZLast()) * (double)partialTicks;
      double offX = this.offXLast() + (this.offX() - this.offXLast()) * (double)partialTicks;
      double offY = this.offYLast() + (this.offY() - this.offYLast()) * (double)partialTicks;
      double offZ = this.offZLast() + (this.offZ() - this.offZLast()) * (double)partialTicks;
      Vector3d rotationCenter = this.center();
      GlStateManager.translate(offX, offY, offZ);
      GlStateManager.translate(rotationCenter.x, rotationCenter.y, rotationCenter.z);
      GL11.glRotated(rotX, 1.0D, 0.0D, 0.0D);
      GL11.glRotated(rotY, 0.0D, 1.0D, 0.0D);
      GL11.glRotated(rotZ, 0.0D, 0.0D, 1.0D);
      GlStateManager.translate(-rotationCenter.x, -rotationCenter.y, -rotationCenter.z);
   }

   @SideOnly(Side.CLIENT)
   default void setupRendering(Entity entity, float partialTicks) {
      GlStateManager.translate(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
      this.setupRenderingInternal(entity, partialTicks);
   }

   default boolean hasChanged() {
      return this.offXLast() != this.offX() || this.offYLast() != this.offY() || this.offZLast() != this.offZ() || this.rotXLast() != this.rotX() || this.rotYLast() != this.rotY() || this.rotZLast() != this.rotZ();
   }
}
