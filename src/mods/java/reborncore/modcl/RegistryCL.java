package reborncore.modcl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import reborncore.RebornRegistry;

public abstract class RegistryCL {
   public LinkedHashMap<String, ItemCL> itemRegistry = new LinkedHashMap();
   public LinkedHashMap<String, BlockCL> blockRegistry = new LinkedHashMap();
   public LinkedHashMap<String, BlockContainerCL> blockContainerRegistry = new LinkedHashMap();
   public HashMap<ItemStack, String> oreEntries = new HashMap();

   public RegistryCL() {
   }

   protected static void register(ItemCL item) {
      RebornRegistry.registerItem(item);
   }

   protected static void register(BlockCL block) {
      RebornRegistry.registerBlock(block);
   }

   protected static void register(BlockContainerCL block) {
      RebornRegistry.registerBlock(block);
      GameRegistry.registerTileEntity(block.tileEntity, "TilePackagerRD");
   }

   public abstract void init(ModCL var1);
}
