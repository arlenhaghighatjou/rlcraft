package sereneseasons.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import sereneseasons.api.SSItems;
import sereneseasons.core.SereneSeasons;
import sereneseasons.item.ItemSeasonClock;
import sereneseasons.util.inventory.CreativeTabSS;

public class ModItems {
   public ModItems() {
   }

   public static void init() {
      registerItems();
   }

   public static void registerItems() {
      SSItems.ss_icon = registerItem(new Item(), "ss_icon");
      SSItems.ss_icon.setCreativeTab((CreativeTabs)null);
      SSItems.season_clock = registerItem(new ItemSeasonClock(), "season_clock");
   }

   public static Item registerItem(Item item, String name) {
      return registerItem(item, name, CreativeTabSS.instance);
   }

   public static Item registerItem(Item item, String name, CreativeTabs tab) {
      item.setUnlocalizedName(name);
      if (tab != null) {
         item.setCreativeTab(CreativeTabSS.instance);
      }

      item.setRegistryName(new ResourceLocation("sereneseasons", name));
      ForgeRegistries.ITEMS.register(item);
      SereneSeasons.proxy.registerItemSided(item);
      return item;
   }
}
