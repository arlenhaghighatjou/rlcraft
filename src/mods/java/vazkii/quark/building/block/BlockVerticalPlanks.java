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

public class BlockVerticalPlanks extends BlockMetaVariants<BlockVerticalPlanks.Variants> implements IQuarkBlock {
   public BlockVerticalPlanks() {
      super("vertical_planks", Material.WOOD, BlockVerticalPlanks.Variants.class);
      this.setHardness(2.0F);
      this.setResistance(5.0F);
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
      VERTICAL_OAK_PLANKS,
      VERTICAL_SPRUCE_PLANKS,
      VERTICAL_BIRCH_PLANKS,
      VERTICAL_JUNGLE_PLANKS,
      VERTICAL_ACACIA_PLANKS,
      VERTICAL_DARK_OAK_PLANKS;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
