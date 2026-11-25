package familiarfauna.init;

import familiarfauna.api.FFItems;
import familiarfauna.item.ItemBugHabitat;
import familiarfauna.item.ItemBugNet;
import familiarfauna.util.inventory.CreativeTabFF;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

public class ModItems {
   public ModItems() {
   }

   public static void init() {
      registerItems();
   }

   public static void registerItems() {
      FFItems.ff_icon = registerItem(new Item(), "ff_icon");
      FFItems.ff_icon.setCreativeTab((CreativeTabs)null);
      FFItems.bug_net = registerItem(new ItemBugNet(), "bug_net");
      FFItems.bug_habitat = registerItem(new ItemBugHabitat(), "bug_habitat");
      FFItems.pixie_dust = registerItem(new Item(), "pixie_dust");
      FFItems.snail_shell = registerItem(new Item(), "snail_shell");
      FFItems.turkey_leg_raw = registerItem((new ItemFood(1, 0.25F, true)).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F), "turkey_leg_raw");
      FFItems.turkey_leg_cooked = registerItem(new ItemFood(5, 0.5F, true), "turkey_leg_cooked");
      FFItems.venison_raw = registerItem(new ItemFood(3, 0.3F, true), "venison_raw");
      FFItems.venison_cooked = registerItem(new ItemFood(8, 0.8F, true), "venison_cooked");
   }

   public static Item registerItem(Item item, String name) {
      return registerItem(item, name, CreativeTabFF.instance);
   }

   public static Item registerItem(Item item, String name, CreativeTabs tab) {
      item.setUnlocalizedName(name);
      if (tab != null) {
         item.setCreativeTab(CreativeTabFF.instance);
      }

      item.setRegistryName(new ResourceLocation("familiarfauna", name));
      ForgeRegistries.ITEMS.register(item);
      if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
         if (item.getHasSubtypes()) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            item.getSubItems(CreativeTabFF.instance, subItems);

            for(ItemStack subItem : subItems) {
               String subItemName = item.getUnlocalizedName(subItem);
               subItemName = subItemName.substring(subItemName.indexOf(".") + 1);
               ModelBakery.registerItemVariants(item, new ResourceLocation[]{new ResourceLocation("familiarfauna", subItemName)});
               ModelLoader.setCustomModelResourceLocation(item, subItem.getMetadata(), new ModelResourceLocation("familiarfauna:" + subItemName, "inventory"));
            }
         } else {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("familiarfauna:" + name, "inventory"));
         }
      }

      return item;
   }
}
