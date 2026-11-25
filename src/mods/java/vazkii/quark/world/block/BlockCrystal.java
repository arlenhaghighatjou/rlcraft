package vazkii.quark.world.block;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockCrystal extends BlockMetaVariants<BlockCrystal.Variants> implements IQuarkBlock {
   public BlockCrystal() {
      super("crystal", Material.GLASS, BlockCrystal.Variants.class);
      this.setHardness(0.3F);
      this.setSoundType(SoundType.GLASS);
      this.setLightLevel(0.73333335F);
      this.setLightOpacity(0);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setHarvestLevel("pickaxe", 0);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
      Block block = iblockstate.getBlock();
      return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }

   @Nullable
   public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos) {
      return ((BlockCrystal.Variants)state.getValue(this.variantProp)).color;
   }

   public static enum Variants implements IStringSerializable {
      CRYSTAL_WHITE(1.0F, 1.0F, 1.0F),
      CRYSTAL_RED(1.0F, 0.0F, 0.0F),
      CRYSTAL_ORANGE(1.0F, 0.5F, 0.0F),
      CRYSTAL_YELLOW(1.0F, 1.0F, 0.0F),
      CRYSTAL_GREEN(0.0F, 1.0F, 0.0F),
      CRYSTAL_BLUE(0.0F, 1.0F, 1.0F),
      CRYSTAL_INDIGO(0.0F, 0.0F, 1.0F),
      CRYSTAL_VIOLET(1.0F, 0.0F, 1.0F),
      CRYSTAL_BLACK(0.0F, 0.0F, 0.0F);

      public final float[] color;

      private Variants(float r, float g, float b) {
         this.color = new float[]{r, g, b};
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
