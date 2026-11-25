package ivorius.ivtoolkit.models.attributes;

import ivorius.ivtoolkit.models.Attribute;
import ivorius.ivtoolkit.models.textures.Texture;
import ivorius.ivtoolkit.models.textures.TextureSub;

public class TextureAttribute extends Attribute {
   public static final String DiffuseAlias = "diffuseTexture";
   public static final long Diffuse = register("diffuseTexture");
   public static final String SpecularAlias = "specularTexture";
   public static final long Specular = register("specularTexture");
   public static final String BumpAlias = "bumpTexture";
   public static final long Bump = register("bumpTexture");
   public static final String NormalAlias = "normalTexture";
   public static final long Normal = register("normalTexture");
   protected static long Mask = Diffuse | Specular | Bump | Normal;
   public final Texture texture;

   public static final boolean is(long mask) {
      return (mask & Mask) != 0L;
   }

   public static TextureAttribute createDiffuse(Texture texture) {
      return new TextureAttribute(Diffuse, texture);
   }

   public static TextureAttribute createDiffuse(TextureSub region) {
      return new TextureAttribute(Diffuse, region);
   }

   public static TextureAttribute createSpecular(Texture texture) {
      return new TextureAttribute(Specular, texture);
   }

   public static TextureAttribute createSpecular(TextureSub region) {
      return new TextureAttribute(Specular, region);
   }

   public static TextureAttribute createNormal(Texture texture) {
      return new TextureAttribute(Normal, texture);
   }

   public static TextureAttribute createNormal(TextureSub region) {
      return new TextureAttribute(Normal, region);
   }

   public static TextureAttribute createBump(Texture texture) {
      return new TextureAttribute(Bump, texture);
   }

   public TextureAttribute(long type, Texture texture) {
      super(type);
      if (!is(type)) {
         throw new RuntimeException("Invalid type specified");
      } else {
         this.texture = texture;
      }
   }

   public Attribute copy() {
      return new TextureAttribute(this.type, this.texture);
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 991 * result + this.texture.hashCode();
      return result;
   }
}
