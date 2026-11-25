package com.chaosthedude.realistictorches.items;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RealisticTorchesItems {
   public static final List<Item> REGISTRY = new ArrayList();
   public static ItemMatchbox matchbox;
   public static Item glowstoneCrystal;
   public static Item glowstonePaste;

   public RealisticTorchesItems() {
   }

   public static void register() {
      matchbox = (ItemMatchbox)registerItem(new ItemMatchbox(), "matchbox");
      glowstoneCrystal = registerItem((new Item()).setUnlocalizedName("realistictorches.glowstone_crystal").setCreativeTab(CreativeTabs.MATERIALS), "glowstone_crystal");
      glowstonePaste = registerItem((new Item()).setUnlocalizedName("realistictorches.glowstone_paste").setCreativeTab(CreativeTabs.MATERIALS), "glowstone_paste");
   }

   protected static <T extends Item> T registerItem(T itemType, String name) {
      itemType.setRegistryName(name);
      ForgeRegistries.ITEMS.register(itemType);
      REGISTRY.add(itemType);
      return itemType;
   }
}
