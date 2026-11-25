package team.creative.enhancedvisuals.client.render;

import net.minecraft.util.ResourceLocation;

public class TextureCacheAnimation extends TextureCache {
   public final ResourceLocation[] locations;
   public final int animationSpeed;

   public TextureCacheAnimation(ResourceLocation[] locations, int animationSpeed) {
      this.locations = locations;
      this.animationSpeed = animationSpeed;
   }

   public ResourceLocation getFirst() {
      return this.locations[0];
   }

   public ResourceLocation getResource() {
      long time = Math.abs(System.nanoTime() / 3000000L / (long)this.animationSpeed);
      return this.locations[(int)(time % (long)this.locations.length)];
   }
}
