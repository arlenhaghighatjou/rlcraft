package hunternif.mc.atlas.api;

import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.impl.MarkerApiImpl;
import hunternif.mc.atlas.api.impl.TileApiImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class AtlasAPI {
   private static final int VERSION = 4;
   public static final TileAPI tiles = new TileApiImpl();
   public static final MarkerAPI markers = new MarkerApiImpl();
   @ObjectHolder("antiqueatlas:antique_atlas")
   public static final Item ATLAS_ITEM = new Item();

   public AtlasAPI() {
   }

   public static int getVersion() {
      return 4;
   }

   public static TileAPI getTileAPI() {
      return tiles;
   }

   public static MarkerAPI getMarkerAPI() {
      return markers;
   }

   public static List<Integer> getPlayerAtlases(EntityPlayer player) {
      if (!SettingsConfig.gameplay.itemNeeded) {
         return Collections.singletonList(Integer.valueOf(player.getUniqueID().hashCode()));
      } else {
         List<Integer> list = new ArrayList();

         for(ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == ATLAS_ITEM) {
               list.add(Integer.valueOf(stack.getItemDamage()));
            }
         }

         return list;
      }
   }
}
