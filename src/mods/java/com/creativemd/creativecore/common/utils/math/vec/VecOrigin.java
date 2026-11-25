package com.creativemd.creativecore.common.utils.math.vec;

import com.creativemd.creativecore.common.utils.math.collision.MatrixUtils;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class VecOrigin implements IVecOrigin {
   protected boolean rotated = false;
   private Vector3d center;
   private Vector3d translation = new Vector3d(0.0D, 0.0D, 0.0D);
   private Matrix3d rotation = MatrixUtils.createIdentityMatrix();
   private Matrix3d rotationInv;
   protected double rotX;
   protected double rotY;
   protected double rotZ;
   protected double rotXLast;
   protected double rotYLast;
   protected double rotZLast;
   protected double offsetX;
   protected double offsetY;
   protected double offsetZ;
   protected double offsetXLast;
   protected double offsetYLast;
   protected double offsetZLast;

   public VecOrigin(Vector3d center) {
      this.rotationInv = this.rotation;
      this.center = center;
   }

   public double offX() {
      return this.offsetX;
   }

   public double offY() {
      return this.offsetY;
   }

   public double offZ() {
      return this.offsetZ;
   }

   public double rotX() {
      return this.rotX;
   }

   public double rotY() {
      return this.rotY;
   }

   public double rotZ() {
      return this.rotZ;
   }

   public boolean isRotated() {
      return this.rotated;
   }

   protected void updateRotated() {
      this.rotated = this.rotX % 360.0D != 0.0D || this.rotY % 360.0D != 0.0D || this.rotZ % 360.0D != 0.0D;
      this.rotation = MatrixUtils.createRotationMatrix(this.rotX, this.rotY, this.rotZ);
      this.rotationInv.invert(this.rotation);
   }

   protected void updateTranslation() {
      this.translation.set(this.offsetX, this.offsetY, this.offsetZ);
   }

   public void offX(double value) {
      this.offsetX = value;
      this.updateTranslation();
   }

   public void offY(double value) {
      this.offsetY = value;
      this.updateTranslation();
   }

   public void offZ(double value) {
      this.offsetZ = value;
      this.updateTranslation();
   }

   public void off(double x, double y, double z) {
      this.offsetX = x;
      this.offsetY = y;
      this.offsetZ = z;
      this.updateTranslation();
   }

   public void rotX(double value) {
      this.rotX = value;
      this.updateRotated();
   }

   public void rotY(double value) {
      this.rotY = value;
      this.updateRotated();
   }

   public void rotZ(double value) {
      this.rotZ = value;
      this.updateRotated();
   }

   public void rot(double x, double y, double z) {
      this.rotX = x;
      this.rotY = y;
      this.rotZ = z;
      this.updateRotated();
   }

   public Vector3d center() {
      return this.center;
   }

   public Matrix3d rotation() {
      return this.rotation;
   }

   public Matrix3d rotationInv() {
      return this.rotationInv;
   }

   public Vector3d translation() {
      return this.translation;
   }

   public void setCenter(Vector3d vec) {
      this.center.set(vec);
   }

   public double offXLast() {
      return this.offsetXLast;
   }

   public double offYLast() {
      return this.offsetYLast;
   }

   public double offZLast() {
      return this.offsetZLast;
   }

   public double rotXLast() {
      return this.rotXLast;
   }

   public double rotYLast() {
      return this.rotYLast;
   }

   public double rotZLast() {
      return this.rotZLast;
   }

   public void tick() {
      this.rotXLast = this.rotX;
      this.rotYLast = this.rotY;
      this.rotZLast = this.rotZ;
      this.offsetXLast = this.offsetX;
      this.offsetYLast = this.offsetY;
      this.offsetZLast = this.offsetZ;
   }

   public IVecOrigin getParent() {
      return null;
   }
}
