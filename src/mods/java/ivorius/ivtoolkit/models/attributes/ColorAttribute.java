package ivorius.ivtoolkit.models.attributes;

import ivorius.ivtoolkit.models.Attribute;
import java.awt.Color;

public class ColorAttribute extends Attribute {
   public static final String DiffuseAlias = "diffuseColor";
   public static final long Diffuse = register("diffuseColor");
   public static final String SpecularAlias = "specularColor";
   public static final long Specular = register("specularColor");
   public static final String AmbientAlias = "ambientColor";
   public static final long Ambient = register("ambientColor");
   public static final String EmissiveAlias = "emissiveColor";
   public static final long Emissive = register("emissiveColor");
   public static final String ReflectionAlias = "reflectionColor";
   public static final long Reflection = register("reflectionColor");
   public static final String AmbientLightAlias = "ambientLightColor";
   public static final long AmbientLight = register("ambientLightColor");
   public static final String FogAlias = "fogColor";
   public static final long Fog = register("fogColor");
   protected static long Mask = Ambient | Diffuse | Specular | Emissive | Reflection | AmbientLight | Fog;
   public Color color;

   public static final boolean is(long mask) {
      return (mask & Mask) != 0L;
   }

   public static final ColorAttribute createAmbient(Color color) {
      return new ColorAttribute(Ambient, color);
   }

   public static final ColorAttribute createAmbient(float r, float g, float b, float a) {
      return new ColorAttribute(Ambient, r, g, b, a);
   }

   public static final ColorAttribute createDiffuse(Color color) {
      return new ColorAttribute(Diffuse, color);
   }

   public static final ColorAttribute createDiffuse(float r, float g, float b, float a) {
      return new ColorAttribute(Diffuse, r, g, b, a);
   }

   public static final ColorAttribute createSpecular(Color color) {
      return new ColorAttribute(Specular, color);
   }

   public static final ColorAttribute createSpecular(float r, float g, float b, float a) {
      return new ColorAttribute(Specular, r, g, b, a);
   }

   public static final ColorAttribute createReflection(Color color) {
      return new ColorAttribute(Reflection, color);
   }

   public static final ColorAttribute createReflection(float r, float g, float b, float a) {
      return new ColorAttribute(Reflection, r, g, b, a);
   }

   public ColorAttribute(long type) {
      super(type);
      this.color = new Color(255, 255, 255);
      if (!is(type)) {
         throw new RuntimeException("Invalid type specified");
      }
   }

   public ColorAttribute(long type, Color color) {
      this(type);
      if (color != null) {
         this.color = color;
      }

   }

   public ColorAttribute(long type, float r, float g, float b, float a) {
      this(type);
      this.color = new Color(r, g, b, a);
   }

   public ColorAttribute(ColorAttribute copyFrom) {
      this(copyFrom.type, copyFrom.color);
   }

   public Attribute copy() {
      return new ColorAttribute(this);
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 953 * result + this.color.hashCode();
      return result;
   }
}
