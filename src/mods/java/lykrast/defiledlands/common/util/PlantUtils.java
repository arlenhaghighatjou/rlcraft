package lykrast.defiledlands.common.util;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import lykrast.defiledlands.common.entity.IEntityDefiled;
import lykrast.defiledlands.common.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PlantUtils {
   private PlantUtils() {
   }

   public static boolean vulnerableToBlastem(Entity ent) {
      if (ent instanceof IEntityDefiled && !((IEntityDefiled)ent).affectedByBlastem()) {
         return false;
      } else {
         return !(ent instanceof EntityPlayer) || !isWearingPhytoprostasia((EntityPlayer)ent);
      }
   }

   public static boolean vulnerableToVilespine(Entity ent) {
      if (ent instanceof IEntityDefiled && !((IEntityDefiled)ent).affectedByVilespine()) {
         return false;
      } else {
         return !(ent instanceof EntityPlayer) || !isWearingPhytoprostasia((EntityPlayer)ent);
      }
   }

   private static boolean isWearingPhytoprostasia(EntityPlayer player) {
      IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

      for(int i = 0; i < baubles.getSlots(); ++i) {
         if (baubles.getStackInSlot(i).getItem() == ModItems.phytoprostasiaAmulet) {
            return true;
         }
      }

      return false;
   }
}
