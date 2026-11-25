package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.SRPMain;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBase extends Item {
   protected byte version;

   public ItemBase(String name, int maxStack, byte id) {
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      this.maxStackSize = maxStack;
      this.version = id;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.WHITE + I18n.format("tootip.srparasites.item." + this.version, new Object[0]));
   }
}
