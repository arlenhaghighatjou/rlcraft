package net.darkhax.surge.mixins.minecraft.client.resources;

import java.util.List;
import java.util.function.Predicate;
import net.darkhax.surge.lib.ICheckableResourceManager;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({FallbackResourceManager.class})
public class MixinFallbackResourceManager implements ICheckableResourceManager {
   @Shadow
   protected List<IResourcePack> resourcePacks;

   public MixinFallbackResourceManager() {
   }

   public boolean hasResource(ResourceLocation location) {
      return this.resourcePacks.stream().anyMatch((pack) -> {
         return pack.resourceExists(location);
      });
   }
}
