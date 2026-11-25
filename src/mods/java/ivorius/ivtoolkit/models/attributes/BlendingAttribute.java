package ivorius.ivtoolkit.models.attributes;

import ivorius.ivtoolkit.models.Attribute;

public class BlendingAttribute extends Attribute {
   public static final String Alias = "blended";
   public static final long Type = register("blended");
   public boolean blended;
   public int sourceFunction;
   public int destFunction;
   public float opacity;

   public static final boolean is(long mask) {
      return (mask & Type) == mask;
   }

   public BlendingAttribute() {
      this((BlendingAttribute)null);
   }

   public BlendingAttribute(boolean blended, int sourceFunc, int destFunc, float opacity) {
      super(Type);
      this.opacity = 1.0F;
      this.blended = blended;
      this.sourceFunction = sourceFunc;
      this.destFunction = destFunc;
      this.opacity = opacity;
   }

   public BlendingAttribute(int sourceFunc, int destFunc, float opacity) {
      this(true, sourceFunc, destFunc, opacity);
   }

   public BlendingAttribute(int sourceFunc, int destFunc) {
      this(sourceFunc, destFunc, 1.0F);
   }

   public BlendingAttribute(boolean blended, float opacity) {
      this(blended, 34184, 771, opacity);
   }

   public BlendingAttribute(float opacity) {
      this(true, opacity);
   }

   public BlendingAttribute(BlendingAttribute copyFrom) {
      this(copyFrom == null ? true : copyFrom.blended, copyFrom == null ? '\u8588' : copyFrom.sourceFunction, copyFrom == null ? 771 : copyFrom.destFunction, copyFrom == null ? 1.0F : copyFrom.opacity);
   }

   public BlendingAttribute copy() {
      return new BlendingAttribute(this);
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 947 * result + (this.blended ? 1 : 0);
      result = 947 * result + this.sourceFunction;
      result = 947 * result + this.destFunction;
      return result;
   }
}
