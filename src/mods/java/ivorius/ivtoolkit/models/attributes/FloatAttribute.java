package ivorius.ivtoolkit.models.attributes;

import ivorius.ivtoolkit.models.Attribute;

public class FloatAttribute extends Attribute {
   public static final String ShininessAlias = "shininess";
   public static final long Shininess = register("shininess");
   public static final String AlphaTestAlias = "alphaTest";
   public static final long AlphaTest = register("alphaTest");
   public float value;

   public static FloatAttribute createShininess(float value) {
      return new FloatAttribute(Shininess, value);
   }

   public static FloatAttribute createAlphaTest(float value) {
      return new FloatAttribute(AlphaTest, value);
   }

   public FloatAttribute(long type) {
      super(type);
   }

   public FloatAttribute(long type, float value) {
      super(type);
      this.value = value;
   }

   public Attribute copy() {
      return new FloatAttribute(this.type, this.value);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         if (!super.equals(o)) {
            return false;
         } else {
            FloatAttribute that = (FloatAttribute)o;
            return Float.compare(that.value, this.value) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (this.value != 0.0F ? Float.floatToIntBits(this.value) : 0);
      return result;
   }
}
