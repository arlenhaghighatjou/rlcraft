package moddy10.craftablechainmail.init;

import moddy10.craftablechainmail.item.ModItem;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ObjectHolder("craftablechainmail")
public final class ModItems {
   public static final ModItem chainmail_plate = null;

   private ModItems() {
      throw new RuntimeException("No " + this.getClass().getName() + " instance for you!");
   }

   @SideOnly(Side.CLIENT)
   public static void initModels() {
      chainmail_plate.initModel();
   }
}
