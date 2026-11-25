package com.oblivioussp.spartanshields.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.fml.common.Optional.Interface;
import thaumcraft.api.items.IWarpingGear;

@Interface(
   iface = "thaumcraft.api.items.IWarpingGear",
   modid = "thaumcraft"
)
public class ItemShieldVoid extends ItemShieldExtraMod implements IWarpingGear {
   public ItemShieldVoid(String unlocName, int maxDamage, ToolMaterial toolMaterial, String materialOreDictName, String externalModName) {
      super(unlocName, maxDamage, toolMaterial, materialOreDictName, externalModName);
   }

   public int getWarp(ItemStack itemstack, EntityPlayer player) {
      return 2;
   }
}
