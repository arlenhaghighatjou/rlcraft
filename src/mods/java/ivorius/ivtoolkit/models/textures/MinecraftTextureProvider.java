package ivorius.ivtoolkit.models.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class MinecraftTextureProvider implements TextureProvider {
   private String basePath;

   public MinecraftTextureProvider(String basePath) {
      this.basePath = basePath;
   }

   public MinecraftTextureProvider.Texture provideTexture(String textureName) {
      return new MinecraftTextureProvider.Texture(new ResourceLocation(this.basePath + textureName));
   }

   public static class Texture implements Texture {
      private ResourceLocation resourceLocation;

      public Texture(ResourceLocation resourceLocation) {
         this.resourceLocation = resourceLocation;
      }

      public void bindTexture() {
         Minecraft.getMinecraft().getTextureManager().bindTexture(this.resourceLocation);
      }

      public float minU() {
         return 0.0F;
      }

      public float maxU() {
         return 1.0F;
      }

      public float minV() {
         return 0.0F;
      }

      public float maxV() {
         return 1.0F;
      }
   }
}
