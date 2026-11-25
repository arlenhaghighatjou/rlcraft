package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEggInIce extends BlockContainer {
   public Item itemBlock;

   public BlockEggInIce() {
      super(Material.ICE);
      this.slipperiness = 0.98F;
      this.setHardness(0.5F);
      this.setLightOpacity(3);
      this.setSoundType(SoundType.GLASS);
      this.setUnlocalizedName("iceandfire.egginice");
      GameRegistry.registerTileEntity(TileEntityEggInIce.class, "eggInIce");
      this.setRegistryName("iceandfire", "egginice");
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityEggInIce();
   }

   @SideOnly(Side.CLIENT)
   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(Blocks.ICE);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockstate, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      Block block = iblockstate.getBlock();
      if (worldIn.getBlockState(pos.offset(side.getOpposite())) != iblockstate) {
         return true;
      } else if (block == this) {
         return false;
      } else {
         return block == this ? false : super.shouldSideBeRendered(iblockstate, worldIn, pos, side);
      }
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
      if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityEggInIce) {
         TileEntityEggInIce tile = (TileEntityEggInIce)worldIn.getTileEntity(pos);
         tile.spawnEgg();
      }

      player.addStat(StatList.getBlockStats(this));
      player.addExhaustion(0.025F);
      if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
         List<ItemStack> items = new ArrayList();
         ItemStack itemstack = new ItemStack(Blocks.ICE, 1);
         if (!itemstack.isEmpty()) {
            items.add(itemstack);
         }

         ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0F, true, player);

         for(ItemStack is : items) {
            spawnAsEntity(worldIn, pos, is);
         }
      } else {
         if (worldIn.provider.doesWaterVaporize()) {
            worldIn.setBlockToAir(pos);
            return;
         }

         int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
         this.harvesters.set(player);
         this.dropBlockAsItem(worldIn, pos, state, i);
         this.harvesters.set((Object)null);
         Material material = worldIn.getBlockState(pos.down()).getMaterial();
         if (material.blocksMovement() || material.isLiquid()) {
            worldIn.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
         }
      }

   }

   public int quantityDropped(EaglercraftRandom random) {
      return 0;
   }

   public int getMobilityFlag() {
      return 0;
   }

   public boolean isOpaqueCube(IBlockState blockstate) {
      return false;
   }

   public boolean isFullCube(IBlockState blockstate) {
      return false;
   }

   public int getRenderType() {
      return 3;
   }
}
