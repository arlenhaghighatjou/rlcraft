package vazkii.quark.decoration.block;

import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockPaperLantern extends BlockMetaVariants<BlockPaperLantern.Variants> implements IQuarkBlock {
   private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, 0.125D, 0.125D, 0.875D, 0.875D, 0.875D);

   public BlockPaperLantern() {
      super("paper_lantern", Material.WOOD, BlockPaperLantern.Variants.class);
      this.setHardness(1.5F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setLightLevel(1.0F);
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 30;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 60;
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.CENTER;
   }

   public static enum Variants implements IStringSerializable {
      PAPER_LANTERN,
      PAPER_LANTERN_SAKURA;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
