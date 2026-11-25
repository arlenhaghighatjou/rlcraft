package vazkii.quark.building.block;

import java.util.Locale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockBark extends BlockMetaVariants<BlockBark.Variants> implements IQuarkBlock {
   public BlockBark() {
      super("bark", Material.WOOD, BlockBark.Variants.class);
      this.setHardness(2.0F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 20;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 5;
   }

   public static enum Variants implements IStringSerializable {
      BARK_OAK,
      BARK_SPRUCE,
      BARK_BIRCH,
      BARK_JUNGLE,
      BARK_ACACIA,
      BARK_DARK_OAK;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
