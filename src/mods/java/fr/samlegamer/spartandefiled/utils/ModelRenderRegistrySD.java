package fr.samlegamer.spartandefiled.utils;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import net.minecraft.item.Item;

public class ModelRenderRegistrySD {
   public ModelRenderRegistrySD() {
   }

   public static void addItemToRegistry(Item item, String modelLoc) {
      SpartanWeaponryAPI.addItemModelToRegistry(item, "spartandefiled", modelLoc);
   }
}
