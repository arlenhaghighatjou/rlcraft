package com.lycanitesmobs.core.block;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockDoubleSlab extends BlockPillar {
   protected String slabName;

   public BlockDoubleSlab(Material material, ModInfo group, String name, String slabName) {
      super(material, group, name);
      this.slabName = slabName;
   }

   public String getLocalizedName() {
      return LanguageManager.translate(this.getUnlocalizedName() + ".name");
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.getDescription(stack, world));
   }

   public String getDescription(ItemStack itemStack, World world) {
      return "§a" + LanguageManager.translate(this.getUnlocalizedName() + ".description");
   }

   public Item getItemDropped(IBlockState state, Random random, int zero) {
      Block slabBlock = ObjectManager.getBlock(this.slabName);
      return slabBlock != null ? Item.getItemFromBlock(slabBlock) : super.getItemDropped(state, random, zero);
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public int quantityDropped(Random random) {
      return ObjectManager.getBlock(this.slabName) != null ? 2 : 1;
   }
}
