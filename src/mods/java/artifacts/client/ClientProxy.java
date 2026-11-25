package artifacts.client;

import artifacts.client.model.layer.LayerAmulet;
import artifacts.client.model.layer.LayerBelt;
import artifacts.client.model.layer.LayerCloak;
import artifacts.client.model.layer.LayerDrinkingHat;
import artifacts.client.model.layer.LayerGloves;
import artifacts.client.model.layer.LayerNightVisionGoggles;
import artifacts.client.model.layer.LayerSnorkel;
import artifacts.client.renderer.RenderHallowStar;
import artifacts.client.renderer.RenderMimic;
import artifacts.common.IProxy;
import artifacts.common.entity.EntityHallowStar;
import artifacts.common.entity.EntityMimic;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy implements IProxy {
   public ClientProxy() {
   }

   public void preInit() {
      RenderingRegistry.registerEntityRenderingHandler(EntityMimic.class, RenderMimic.FACTORY);
      RenderingRegistry.registerEntityRenderingHandler(EntityHallowStar.class, RenderHallowStar.FACTORY);
   }

   public void init() {
      addRenderLayers();
   }

   public void postInit() {
   }

   public void registerItemRenderer(Item item, int meta, String name) {
      ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("artifacts:" + name, "inventory"));
   }

   private static void addRenderLayers() {
      Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
      addLayersToSkin((RenderPlayer)skinMap.get("default"), false);
      addLayersToSkin((RenderPlayer)skinMap.get("slim"), true);
   }

   private static void addLayersToSkin(RenderPlayer renderPlayer, boolean slim) {
      renderPlayer.addLayer(new LayerGloves(slim, renderPlayer));
      renderPlayer.addLayer(new LayerDrinkingHat(renderPlayer));
      renderPlayer.addLayer(new LayerAmulet(renderPlayer));
      renderPlayer.addLayer(new LayerBelt(renderPlayer));
      renderPlayer.addLayer(new LayerCloak(renderPlayer));
      renderPlayer.addLayer(new LayerNightVisionGoggles(renderPlayer));
      renderPlayer.addLayer(new LayerSnorkel(renderPlayer));
   }
}
