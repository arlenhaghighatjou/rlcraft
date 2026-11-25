package vazkii.quark.decoration.block;

import java.util.Locale;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.block.BlockVariantLeaves;
import vazkii.quark.world.feature.TreeVariants;

public class BlockLeafCarpet extends BlockMetaVariants<BlockLeafCarpet.Variants> implements IBlockColorProvider, IQuarkBlock {
   protected static final AxisAlignedBB CARPET_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

   public BlockLeafCarpet() {
      super("leaf_carpet", Material.LEAVES, BlockLeafCarpet.Variants.class);
      this.setHardness(0.2F);
      this.setSoundType(SoundType.PLANT);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   @SideOnly(Side.CLIENT)
   public IBlockColor getBlockColor() {
      return (state, worldIn, pos, tintIndex) -> {
         IBlockState base = ((BlockLeafCarpet.Variants)state.getValue(this.getVariantProp())).getBaseState();
         return Minecraft.getMinecraft().getBlockColors().colorMultiplier(base, worldIn, pos, tintIndex);
      };
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return (stack, tintIndex) -> {
         ItemStack baseStack = BlockLeafCarpet.Variants.values()[Math.min(BlockLeafCarpet.Variants.values().length - 1, stack.getItemDamage())].getBaseStack();
         return Minecraft.getMinecraft().getItemColors().colorMultiplier(baseStack, tintIndex);
      };
   }

   public boolean shouldDisplayVariant(int variant) {
      return !BlockLeafCarpet.Variants.values()[variant].getBaseStack().isEmpty();
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
      return null;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return CARPET_AABB;
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos blockPos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public IStateMapper getStateMapper() {
      return null;
   }

   public static enum Variants implements IStringSerializable {
      OAK_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 0), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, EnumType.OAK)),
      SPRUCE_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 1), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, EnumType.SPRUCE)),
      BIRCH_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 2), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, EnumType.BIRCH)),
      JUNGLE_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 3), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, EnumType.JUNGLE)),
      ACACIA_LEAF_CARPET(new ItemStack(Blocks.LEAVES2, 1, 0), Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, EnumType.ACACIA)),
      DARK_OAK_LEAF_CARPET(new ItemStack(Blocks.LEAVES2, 1, 1), Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, EnumType.ACACIA)),
      SWAMP_LEAF_CARPET(() -> {
         return new ItemStack(TreeVariants.variant_leaves, 1, 0);
      }, () -> {
         return TreeVariants.variant_leaves.getDefaultState().withProperty(BlockVariantLeaves.VARIANT, BlockVariantLeaves.Variant.SWAMP_LEAVES);
      }),
      SAKURA_LEAF_CARPET(() -> {
         return new ItemStack(TreeVariants.variant_leaves, 1, 1);
      }, () -> {
         return TreeVariants.variant_leaves.getDefaultState().withProperty(BlockVariantLeaves.VARIANT, BlockVariantLeaves.Variant.SAKURA_LEAVES);
      });

      public final Supplier<ItemStack> baseStack;
      public final Supplier<IBlockState> baseState;

      private Variants(ItemStack baseStack, IBlockState baseState) {
         this(() -> {
            return (void)baseStack;
         }, () -> {
            return (void)baseState;
         });
      }

      private Variants(Supplier<ItemStack> baseStack, Supplier<IBlockState> baseState) {
         this.baseStack = baseStack;
         this.baseState = baseState;
      }

      public ItemStack getBaseStack() {
         return (ItemStack)this.baseStack.get();
      }

      public IBlockState getBaseState() {
         return (IBlockState)this.baseState.get();
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
