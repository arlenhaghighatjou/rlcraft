package ivorius.ivtoolkit.models.attributes;

import ivorius.ivtoolkit.models.Attribute;

public class DepthTestAttribute extends Attribute {
   public static final String Alias = "depthStencil";
   public static final long Type = register("depthStencil");
   protected static long Mask = Type;
   public int depthFunc;
   public float depthRangeNear;
   public float depthRangeFar;
   public boolean depthMask;

   public static final boolean is(long mask) {
      return (mask & Mask) != 0L;
   }

   public DepthTestAttribute() {
      this(515);
   }

   public DepthTestAttribute(boolean depthMask) {
      this(515, depthMask);
   }

   public DepthTestAttribute(int depthFunc) {
      this(depthFunc, true);
   }

   public DepthTestAttribute(int depthFunc, boolean depthMask) {
      this(depthFunc, 0.0F, 1.0F, depthMask);
   }

   public DepthTestAttribute(int depthFunc, float depthRangeNear, float depthRangeFar) {
      this(depthFunc, depthRangeNear, depthRangeFar, true);
   }

   public DepthTestAttribute(int depthFunc, float depthRangeNear, float depthRangeFar, boolean depthMask) {
      this(Type, depthFunc, depthRangeNear, depthRangeFar, depthMask);
   }

   public DepthTestAttribute(long type, int depthFunc, float depthRangeNear, float depthRangeFar, boolean depthMask) {
      super(type);
      if (!is(type)) {
         throw new RuntimeException("Invalid type specified");
      } else {
         this.depthFunc = depthFunc;
         this.depthRangeNear = depthRangeNear;
         this.depthRangeFar = depthRangeFar;
         this.depthMask = depthMask;
      }
   }

   public DepthTestAttribute(DepthTestAttribute rhs) {
      this(rhs.type, rhs.depthFunc, rhs.depthRangeNear, rhs.depthRangeFar, rhs.depthMask);
   }

   public Attribute copy() {
      return new DepthTestAttribute(this);
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 971 * result + this.depthFunc;
      result = 971 * result + (this.depthMask ? 1 : 0);
      return result;
   }
}
