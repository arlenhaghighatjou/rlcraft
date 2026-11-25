package net.darkhax.surge.mixins.minecraft.client.resources;

import java.util.Map;
import net.darkhax.surge.lib.ICheckableResourceManager;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({SimpleReloadableResourceManager.class})
public class MixinSimpleReloadableResourceManager implements ICheckableResourceManager {
   @Shadow
   private Map<String, FallbackResourceManager> domainResourceManagers;

   public MixinSimpleReloadableResourceManager() {
   }

   public boolean hasResource(ResourceLocation location) {
      FallbackResourceManager manager = (FallbackResourceManager)this.domainResourceManagers.get(location.getResourceDomain());
      return manager instanceof ICheckableResourceManager && ((ICheckableResourceManager)manager).hasResource(location);
   }
}
