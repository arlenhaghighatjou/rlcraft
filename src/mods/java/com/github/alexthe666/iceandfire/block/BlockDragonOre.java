package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonOre extends Block {
   public Item itemBlock;

   public BlockDragonOre(int toollevel, float hardness, float resistance, String name, String gameName) {
      super(Material.ROCK);
      this.setCreativeTab(IceAndFire.TAB);
      this.setHarvestLevel("pickaxe", toollevel);
      this.setResistance(resistance);
      this.setHardness(hardness);
      this.setUnlocalizedName(name);
      this.setRegistryName("iceandfire", gameName);
   }

   public int quantityDroppedWithBonus(int fortune, EaglercraftRandom random) {
      if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped((IBlockState)this.getBlockState().getValidStates().iterator().next(), random, fortune)) {
         int i = random.nextInt(fortune + 2) - 1;
         if (i < 0) {
            i = 0;
         }

         return this.quantityDropped(random) * (i + 1);
      } else {
         return this.quantityDropped(random);
      }
   }

   public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
      EaglercraftRandom rand = world instanceof World ? ((World)world).rand : new EaglercraftRandom();
      return this.getItemDropped(state, rand, fortune) != Item.getItemFromBlock(this) && this == ModBlocks.sapphireOre ? MathHelper.getInt(rand, 3, 7) : 0;
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return this == ModBlocks.sapphireOre ? ModItems.sapphireGem : Item.getItemFromBlock(ModBlocks.silverOre);
   }
}
