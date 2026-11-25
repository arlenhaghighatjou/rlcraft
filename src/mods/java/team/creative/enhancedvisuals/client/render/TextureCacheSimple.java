package team.creative.enhancedvisuals.client.render;

import net.minecraft.util.ResourceLocation;

public class TextureCacheSimple extends TextureCache {
   public final ResourceLocation location;

   public TextureCacheSimple(ResourceLocation location) {
      this.location = location;
   }

   public ResourceLocation getResource() {
      return this.location;
   }

   public ResourceLocation getFirst() {
      return this.location;
   }
}
