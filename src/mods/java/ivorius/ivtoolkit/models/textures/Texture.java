package ivorius.ivtoolkit.models.textures;

public interface Texture {
   void bindTexture();

   float minU();

   float maxU();

   float minV();

   float maxV();
}
