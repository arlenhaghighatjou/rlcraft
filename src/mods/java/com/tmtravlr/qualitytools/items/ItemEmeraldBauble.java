package com.tmtravlr.qualitytools.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.tmtravlr.qualitytools.QualityToolsMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;

@Interface(
   modid = "baubles",
   iface = "baubles.api.IBauble",
   striprefs = true
)
public class ItemEmeraldBauble extends Item implements IBauble {
   public static final Item EMERALD_RING = ((Item)(new ItemEmeraldBauble()).setRegistryName("emerald_ring")).setUnlocalizedName("emerald_ring").setMaxStackSize(1).setCreativeTab(QualityToolsMod.tabQualityTools);
   public static final Item EMERALD_AMULET = ((Item)(new ItemEmeraldBauble()).setRegistryName("emerald_amulet")).setUnlocalizedName("emerald_amulet").setMaxStackSize(1).setCreativeTab(QualityToolsMod.tabQualityTools);

   public ItemEmeraldBauble() {
   }

   @Method(
      modid = "baubles"
   )
   public BaubleType getBaubleType(ItemStack itemstack) {
      return this == EMERALD_RING ? BaubleType.RING : BaubleType.AMULET;
   }
}
