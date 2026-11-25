package com.fantasticsource.tiamatinventory.api;

import com.fantasticsource.tools.ReflectionTool;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

public class TiamatInventoryAPI {
   private static Field clientInventoryField = null;
   private static LinkedHashMap<EaglercraftUUID, ITiamatPlayerInventory> tiamatServerInventories = null;

   public TiamatInventoryAPI() {
   }

   public static ITiamatPlayerInventory getTiamatPlayerInventory(EntityPlayer player) {
      if (player.world.isRemote) {
         return clientInventoryField == null ? null : (ITiamatPlayerInventory)ReflectionTool.get(clientInventoryField, (Object)null);
      } else {
         return tiamatServerInventories == null ? null : (ITiamatPlayerInventory)tiamatServerInventories.getOrDefault(player.getPersistentID(), (Object)null);
      }
   }

   static {
      try {
         for(Field field : Class.forName("com.fantasticsource.tiamatinventory.inventory.TiamatPlayerInventory").getDeclaredFields()) {
            if (field.getName().equals("tiamatServerInventories")) {
               tiamatServerInventories = (LinkedHashMap)field.get((Object)null);
            } else if (field.getName().equals("tiamatClientInventory")) {
               clientInventoryField = field;
            }
         }
      } catch (IllegalAccessException | ClassNotFoundException var4) {
         if (Loader.isModLoaded("tiamatinventory")) {
            var4.printStackTrace();
         }
      }

   }
}
