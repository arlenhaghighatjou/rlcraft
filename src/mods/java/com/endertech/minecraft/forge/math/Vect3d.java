package com.endertech.minecraft.forge.math;

import com.endertech.common.Args;
import com.endertech.common.CommonMath;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Vect3d {
   public static final Vect3d ZERO = new Vect3d(0.0D, 0.0D, 0.0D);
   public final double x;
   public final double y;
   public final double z;

   protected Vect3d(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static Vect3d from(double x, double y, double z) {
      return new Vect3d(x, y, z);
   }

   public static Vect3d from(Vec3d vec3d) {
      return from(vec3d.x, vec3d.y, vec3d.z);
   }

   public static Vect3d from(Vec3i vec3i) {
      return from((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ());
   }

   public static Vect3d from(BlockPos pos) {
      return from((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
   }

   public static Vect3d difference(Vect3d vecA, Vect3d vecB) {
      if (vecA == null) {
         vecA = ZERO;
      }

      if (vecB == null) {
         vecB = ZERO;
      }

      return new Vect3d(vecA.x - vecB.x, vecA.y - vecB.y, vecA.z - vecB.z);
   }

   public static Vect3d mult(Vect3d vecA, Vect3d vecB) {
      return vecA != null && vecB != null ? new Vect3d(vecA.y * vecB.z - vecA.z * vecB.y, vecA.z * vecB.x - vecA.x * vecB.z, vecA.x * vecB.y - vecA.y * vecB.x) : ZERO;
   }

   public static Vect3d normalize(Vect3d vec) {
      double length = length(vec);
      return length >= 1.0E-4D ? new Vect3d(vec.x / length, vec.y / length, vec.z / length) : ZERO;
   }

   public static Vect3d sum(Vect3d vecA, Vect3d vecB) {
      if (vecA == null) {
         vecA = ZERO;
      }

      if (vecB == null) {
         vecB = ZERO;
      }

      return new Vect3d(vecA.x + vecB.x, vecA.y + vecB.y, vecA.z + vecB.z);
   }

   public static double scalarMult(Vect3d vecA, Vect3d vecB) {
      return vecA != null && vecB != null ? vecA.x * vecB.x + vecA.y * vecB.y + vecA.z * vecB.z : 0.0D;
   }

   public static Vect3d straightMult(Vect3d vecA, Vect3d vecB) {
      if (vecA == null) {
         vecA = ZERO;
      }

      if (vecB == null) {
         vecB = ZERO;
      }

      return new Vect3d(vecA.x * vecB.x, vecA.y * vecB.y, vecA.z * vecB.z);
   }

   public static CommonMath.Angle angle(Vect3d vecA, Vect3d vecB) {
      double length = vecA.length() * vecB.length();
      if (CommonMath.notZero(length)) {
         double dot = scalarMult(vecA, vecB);
         double var10000 = dot / length;
         double cos = CommonMath.Range.enclose(dot, -1.0D, 1.0D);
         CommonMath.Angle angle = CommonMath.Angle.fromCos(cos);
         return angle;
      } else {
         return CommonMath.Angle.ZERO;
      }
   }

   public static Vect3d approx(float factor, Vect3d vecA, Vect3d vecB) {
      if (vecA == null) {
         vecA = ZERO;
      }

      if (vecB == null) {
         vecB = ZERO;
      }

      float x = CommonMath.Approx.straight(factor, (float)vecA.x, (float)vecB.x);
      float y = CommonMath.Approx.straight(factor, (float)vecA.y, (float)vecB.y);
      float z = CommonMath.Approx.straight(factor, (float)vecA.z, (float)vecB.z);
      return new Vect3d((double)x, (double)y, (double)z);
   }

   public Vect3d approx(float factor, Vect3d vec) {
      return approx(factor, this, vec);
   }

   public static double length(Vect3d vec) {
      return vec != null ? Math.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z) : 0.0D;
   }

   public double length() {
      return length(this);
   }

   public static Vect3d setLength(Vect3d vec, double length) {
      if (vec != null && vec.notZero()) {
         double factor = length / vec.length();
         return vec.scale(factor);
      } else {
         return ZERO;
      }
   }

   public Vect3d resize(double length) {
      return setLength(this, length);
   }

   public Vect3d truncate(double length) {
      return this.length() > length ? setLength(this, length) : this;
   }

   public Vect3d expand(double length) {
      return this.length() < length ? setLength(this, length) : this;
   }

   public boolean isZero() {
      return !this.notZero();
   }

   public boolean notZero() {
      return CommonMath.notZero(this.x) || CommonMath.notZero(this.y) || CommonMath.notZero(this.z);
   }

   public Vect3d add(Vect3d vec) {
      return sum(this, vec);
   }

   public Vect3d add(double scalar) {
      return new Vect3d(this.x + scalar, this.y + scalar, this.z + scalar);
   }

   public Vect3d subtract(Vect3d vec) {
      return difference(this, vec);
   }

   public Vect3d subtract(double scalar) {
      return new Vect3d(this.x - scalar, this.y - scalar, this.z - scalar);
   }

   public Vect3d move(double dx, double dy, double dz) {
      return new Vect3d(this.x + dx, this.y + dy, this.z + dz);
   }

   public static Vect3d scale(Vect3d vec, double scale) {
      return vec != null ? new Vect3d(vec.x * scale, vec.y * scale, vec.z * scale) : ZERO;
   }

   public Vect3d scale(double scale) {
      return scale(this, scale);
   }

   public static Vect3d invert(Vect3d vec) {
      return vec != null ? new Vect3d(-vec.x, -vec.y, -vec.z) : ZERO;
   }

   public Vect3d invert() {
      return invert(this);
   }

   public static Vect3d divide(Vect3d vec, double divider) {
      return vec != null && CommonMath.notZero(divider) ? new Vect3d(vec.x / divider, vec.y / divider, vec.z / divider) : ZERO;
   }

   public Vect3d divide(double divider) {
      return divide(this, divider);
   }

   public static double distance(Vect3d vecA, Vect3d vecB) {
      return difference(vecA, vecB).length();
   }

   public double distance(Vect3d vec) {
      return distance(this, vec);
   }

   public float pitch() {
      double length = Math.sqrt(this.x * this.x + this.z * this.z);
      return ForgeMath.arcTan(this.y, length);
   }

   public float yaw() {
      return ForgeMath.arcTan(this.x, this.z);
   }

   public static Rotation rotation(Vect3d vec) {
      return vec != null ? new Rotation(vec.pitch(), vec.yaw()) : Rotation.ZERO;
   }

   public static Rotation rotationWrapped(Vect3d vec) {
      if (vec != null) {
         float pitch = MathHelper.wrapDegrees(vec.pitch());
         float yaw = MathHelper.wrapDegrees(vec.yaw());
         return new Rotation(pitch, yaw);
      } else {
         return Rotation.ZERO;
      }
   }

   public Rotation rotation() {
      return rotation(this);
   }

   public Rotation rotationWrapped() {
      return rotationWrapped(this);
   }

   public static Vect3d maxOfAll(Vect3d vecA, Vect3d vecB) {
      if (vecA == null) {
         vecA = ZERO;
      }

      if (vecB == null) {
         vecB = ZERO;
      }

      return new Vect3d(Math.max(vecA.x, vecB.x), Math.max(vecA.y, vecB.y), Math.max(vecA.z, vecB.z));
   }

   public static Vect3d minOfAllByAbs(Vect3d vecA, Vect3d vecB) {
      if (vecA == null) {
         vecA = ZERO;
      }

      if (vecB == null) {
         vecB = ZERO;
      }

      return new Vect3d(CommonMath.getMinByAbs(vecA.x, vecB.x), CommonMath.getMinByAbs(vecA.y, vecB.y), CommonMath.getMinByAbs(vecA.z, vecB.z));
   }

   public static Vect3d rotateAroundX(Vect3d vec, float degrees) {
      if (vec != null) {
         float cosA = ForgeMath.cos(degrees);
         float sinA = ForgeMath.sin(degrees);
         return new Vect3d(vec.x, vec.y * (double)cosA + vec.z * (double)sinA, vec.z * (double)cosA - vec.y * (double)sinA);
      } else {
         return ZERO;
      }
   }

   public Vect3d rotateAroundX(float degrees) {
      return rotateAroundX(this, degrees);
   }

   public static Vect3d rotateAroundY(Vect3d vec, float degrees) {
      if (vec != null) {
         float cosA = ForgeMath.cos(degrees);
         float sinA = ForgeMath.sin(degrees);
         return new Vect3d(vec.x * (double)cosA + vec.z * (double)sinA, vec.y, vec.z * (double)cosA - vec.x * (double)sinA);
      } else {
         return ZERO;
      }
   }

   public Vect3d rotateAroundY(float degrees) {
      return rotateAroundY(this, degrees);
   }

   public static Vect3d rotateAroundZ(Vect3d vec, float degrees) {
      if (vec != null) {
         float cosA = ForgeMath.cos(degrees);
         float sinA = ForgeMath.sin(degrees);
         return new Vect3d(vec.x * (double)cosA + vec.y * (double)sinA, vec.y * (double)cosA - vec.x * (double)sinA, vec.z);
      } else {
         return ZERO;
      }
   }

   public Vect3d rotateAroundZ(float degrees) {
      return rotateAroundZ(this, degrees);
   }

   public Vect3d mult(Vect3d vec) {
      return mult(this, vec);
   }

   public Vect3d normalize() {
      return normalize(this);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Vect3d)) {
         return super.equals(obj);
      } else {
         Vect3d vec = (Vect3d)obj;
         return vec.x == this.x && vec.y == this.y && vec.z == this.z;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.x, this.y, this.z});
   }

   public boolean notEqual(Vect3d vec) {
      return !this.equals(vec);
   }

   public static Vect3d readFromByteBuf(ByteBuf buf) {
      return new Vect3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
   }

   public Vect3d invertX() {
      return this.withX(-this.x);
   }

   public Vect3d invertY() {
      return this.withY(-this.y);
   }

   public Vect3d invertZ() {
      return this.withZ(-this.z);
   }

   public Vect3d withX(double x) {
      return from(x, this.y, this.z);
   }

   public Vect3d withY(double y) {
      return from(this.x, y, this.z);
   }

   public Vect3d withZ(double z) {
      return from(this.x, this.y, z);
   }

   public static void writeToByteBuf(Vect3d vec, ByteBuf buf) {
      buf.writeDouble(vec.x);
      buf.writeDouble(vec.y);
      buf.writeDouble(vec.z);
   }

   public Vec3d toVec3d() {
      return new Vec3d(this.x, this.y, this.z);
   }

   public BlockPos toBlockPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public String toString() {
      return Vect3d.class.getSimpleName() + Args.group(Args.get("x", Double.valueOf(this.x)), Args.get("y", Double.valueOf(this.y)), Args.get("z", Double.valueOf(this.z)));
   }

   public static Vect3d projection(Vect3d vecA, Vect3d vecB) {
      double projLength = scalarMult(vecA, vecB.normalize());
      Vect3d projection = vecB.resize(projLength);
      return projection;
   }

   public Vect3d projectOn(Vect3d vec) {
      return projection(this, vec);
   }
}
