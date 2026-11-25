package paulevs.betternether.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.BetterNether;

public abstract class BlockBNStoneSlab extends BlockSlab {
   public static final PropertyEnum<BlockBNStoneSlab.Variant> VARIANT = PropertyEnum.create("variant", BlockBNStoneSlab.Variant.class);

   public BlockBNStoneSlab(String name) {
      super(Material.ROCK, MapColor.LIME_STAINED_HARDENED_CLAY);
      this.setSoundType(SoundType.STONE);
      this.setHardness(1.5F);
      this.setCreativeTab(BetterNether.BN_TAB);
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      IBlockState iblockstate = this.blockState.getBaseState();
      if (!this.isDouble()) {
         iblockstate = iblockstate.withProperty(HALF, EnumBlockHalf.BOTTOM);
      }

      this.setDefaultState(iblockstate.withProperty(VARIANT, BlockBNStoneSlab.Variant.DEFAULT));
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockBNStoneSlab.Variant.DEFAULT);
      if (!this.isDouble()) {
         iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
      }

      return iblockstate;
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      if (!this.isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP) {
         i |= 8;
      }

      return i;
   }

   protected BlockStateContainer createBlockState() {
      return this.isDouble() ? new BlockStateContainer(this, new IProperty[]{VARIANT}) : new BlockStateContainer(this, new IProperty[]{HALF, VARIANT});
   }

   public String getUnlocalizedName(int meta) {
      return super.getUnlocalizedName();
   }

   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   public Comparable<?> getTypeForItem(ItemStack stack) {
      return BlockBNStoneSlab.Variant.DEFAULT;
   }

   public static class Double extends BlockBNStoneSlab {
      private Block drop;

      public Double(String name) {
         super(name);
      }

      public boolean isDouble() {
         return true;
      }

      public void setDrop(Block drop) {
         this.drop = drop;
      }

      public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
         return Item.getItemFromBlock(this.drop);
      }

      public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
         return new ItemStack(this.drop);
      }
   }

   public static class Half extends BlockBNStoneSlab {
      private Block fullBlock;

      public Half(String name, Block fullBlock) {
         super(name);
         this.fullBlock = fullBlock;
      }

      public boolean isDouble() {
         return false;
      }

      public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
         ItemStack item = playerIn.getHeldItemMainhand();
         if (item.getItem() == Item.getItemFromBlock(this) && state.getBlock().getDefaultState() == this.getDefaultState()) {
            float pitch = playerIn.cameraPitch;
            if (state.getValue(HALF) != EnumBlockHalf.TOP || facing != EnumFacing.DOWN && (double)hitY >= 0.5D) {
               if (state.getValue(HALF) != EnumBlockHalf.BOTTOM || facing != EnumFacing.UP && (double)hitY <= 0.5D) {
                  return false;
               } else {
                  worldIn.setBlockState(pos, this.fullBlock.getDefaultState());
                  worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  if (!playerIn.isCreative()) {
                     item.setCount(item.getCount() - 1);
                  }

                  return true;
               }
            } else {
               worldIn.setBlockState(pos, this.fullBlock.getDefaultState());
               worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
               if (!playerIn.isCreative()) {
                  item.setCount(item.getCount() - 1);
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public static enum Variant implements IStringSerializable {
      DEFAULT;

      private Variant() {
      }

      public String getName() {
         return "default";
      }
   }
}
