package com.ferreusveritas.dynamictrees.systems.poissondisc;

public class Vec2i {
   public int x;
   public int z;
   public boolean tight;

   public Vec2i() {
   }

   public Vec2i(int x, int z) {
      this(x, z, false);
   }

   public Vec2i(int x, int z, boolean tight) {
      this.set(x, z);
      this.tight = tight;
   }

   public Vec2i(Vec2i aCoord) {
      this(aCoord.x, aCoord.z);
      this.setTight(aCoord.tight);
   }

   public Vec2i set(int x, int z) {
      this.x = x;
      this.z = z;
      return this;
   }

   public Vec2i set(Vec2i other) {
      this.set(other.x, other.z);
      this.tight = other.tight;
      return this;
   }

   public Vec2i setTight(boolean state) {
      this.tight = state;
      return this;
   }

   public boolean isTight() {
      return this.tight;
   }

   public Vec2i add(int x, int z) {
      this.x += x;
      this.z += z;
      return this;
   }

   public Vec2i sub(int x, int z) {
      this.x -= x;
      this.z -= z;
      return this;
   }

   public Vec2i add(Vec2i other) {
      return this.add(other.x, other.z);
   }

   public Vec2i sub(Vec2i other) {
      return this.sub(other.x, other.z);
   }

   public double len() {
      return Math.sqrt((double)(this.x * this.x + this.z * this.z));
   }

   public double angle() {
      return Math.atan2((double)this.z, (double)this.x);
   }

   public static int crossProduct(Vec2i c1, Vec2i c2) {
      return c1.x * c2.z - c1.z * c2.x;
   }

   public boolean equals(Object o) {
      Vec2i v = (Vec2i)o;
      return this.x == v.x && this.z == v.z;
   }

   public int hashCode() {
      return this.x ^ this.z * 98764313;
   }

   public String toString() {
      return "Coord " + this.x + "," + this.z + "," + (this.tight ? "T" : "L");
   }
}
