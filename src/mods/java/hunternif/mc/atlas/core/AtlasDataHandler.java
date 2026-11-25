package hunternif.mc.atlas.core;

import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class AtlasDataHandler {
   private static final String ATLAS_DATA_PREFIX = "aAtlas_";
   private final Map<String, AtlasData> atlasDataClientCache = new ConcurrentHashMap();

   public AtlasDataHandler() {
   }

   public AtlasData getAtlasData(ItemStack stack, World world) {
      return stack.getItem() == RegistrarAntiqueAtlas.ATLAS ? this.getAtlasData(stack.getItemDamage(), world) : null;
   }

   public AtlasData getAtlasData(int atlasID, World world) {
      String key = this.getAtlasDataKey(atlasID);
      AtlasData data = null;
      if (world.isRemote) {
         data = (AtlasData)this.atlasDataClientCache.get(key);
      }

      if (data == null) {
         data = (AtlasData)world.loadData(AtlasData.class, key);
         if (data == null) {
            data = new AtlasData(key);
            world.setData(key, data);
         }

         if (world.isRemote) {
            this.atlasDataClientCache.put(key, data);
         }
      }

      return data;
   }

   public Collection<AtlasData> getAllLoadedAtlasData() {
      return this.atlasDataClientCache.values();
   }

   private String getAtlasDataKey(int atlasID) {
      return "aAtlas_" + atlasID;
   }

   @SubscribeEvent
   public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
      this.atlasDataClientCache.clear();
   }
}
