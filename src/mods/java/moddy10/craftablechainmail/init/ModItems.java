package moddy10.craftablechainmail.init;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import moddy10.craftablechainmail.item.ModItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModItems {
   public static ModItem chainmail_plate = null;

   private ModItems() {
      throw new RuntimeException("No " + this.getClass().getName() + " instance for you!");
   }

   @SideOnly(Side.CLIENT)
   public static void initModels() {
      chainmail_plate.initModel();
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("craftablechainmail:chainmail_plate"), v -> chainmail_plate = (ModItem)v);
   }
}
