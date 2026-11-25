package knightminer.inspirations.utility.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

public class BlockRedstoneBarrel extends Block {
   private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
   public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

   public BlockRedstoneBarrel() {
      super(Material.ROCK);
      this.setCreativeTab(CreativeTabs.REDSTONE);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{LEVEL});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(LEVEL)).intValue();
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      drops.add(new ItemStack(this));
      int level = ((Integer)state.getValue(LEVEL)).intValue();
      if (level > 0) {
         drops.add(new ItemStack(Items.REDSTONE, level));
      }

   }

   /** @deprecated */
   @Deprecated
   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return BOUNDS;
   }

   /** @deprecated */
   @Deprecated
   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return ((Integer)blockState.getValue(LEVEL)).intValue();
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      int level = ((Integer)state.getValue(LEVEL)).intValue();
      ItemStack stack = player.getHeldItem(hand);
      if (stack.getItem() == Items.REDSTONE) {
         if (level < 15) {
            if (!world.isRemote) {
               if (!player.capabilities.isCreativeMode) {
                  stack.shrink(1);
               }

               this.setLevel(world, pos, state, level + 1);
            }

            return true;
         } else {
            return true;
         }
      } else if (level > 0) {
         if (!world.isRemote) {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.REDSTONE));
            this.setLevel(world, pos, state, level - 1);
         }

         return true;
      } else {
         return false;
      }
   }

   public void setLevel(World world, BlockPos pos, IBlockState state, int level) {
      world.setBlockState(pos, state.withProperty(LEVEL, Integer.valueOf(MathHelper.clamp(level, 0, 15))), 2);
      world.updateComparatorOutputLevel(pos, this);
   }
}
