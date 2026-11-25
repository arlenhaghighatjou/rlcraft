package knightminer.inspirations.shared;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SharedClientProxy extends ClientProxy {
   public SharedClientProxy() {
   }

   public void preInit() {
      super.preInit();
      IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
      if (manager instanceof IReloadableResourceManager) {
         ((IReloadableResourceManager)manager).registerReloadListener(ClientUtil::onResourceReload);
      } else {
         Inspirations.log.error("Failed to register resource reload listener, expected instance of IReloadableResourceManager but got {}", manager.getClass());
      }

   }

   @SubscribeEvent
   public void registerModels(ModelRegistryEvent event) {
      registerItemMetaDynamic(InspirationsShared.materials);
      registerItemMetaDynamic(InspirationsShared.edibles);
   }

   @SubscribeEvent
   public void registerTextures(Pre event) {
      TextureMap map = event.getMap();
      map.registerSprite(Util.getResource("blocks/fluid_colorless"));
      map.registerSprite(Util.getResource("blocks/fluid_colorless_flow"));
   }
}
