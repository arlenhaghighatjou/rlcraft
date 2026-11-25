package c4.comforts.common.items;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ComfortsItems {
   @ObjectHolder("comforts:sleeping_bag")
   public static ItemSleepingBag SLEEPING_BAG;
   @ObjectHolder("comforts:hammock")
   public static ItemHammock HAMMOCK;

   public ComfortsItems() {
   }

   @SideOnly(Side.CLIENT)
   public static void initModels() {
      SLEEPING_BAG.initModel();
      HAMMOCK.initModel();
   }
}
