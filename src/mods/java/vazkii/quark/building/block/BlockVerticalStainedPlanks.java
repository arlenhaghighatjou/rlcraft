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

public class BlockVerticalStainedPlanks extends BlockMetaVariants<BlockVerticalStainedPlanks.Variants> implements IQuarkBlock {
   public BlockVerticalStainedPlanks() {
      super("vertical_stained_planks", Material.WOOD, BlockVerticalStainedPlanks.Variants.class);
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
      VERTICAL_STAINED_PLANKS_WHITE,
      VERTICAL_STAINED_PLANKS_ORANGE,
      VERTICAL_STAINED_PLANKS_MAGENTA,
      VERTICAL_STAINED_PLANKS_LIGHT_BLUE,
      VERTICAL_STAINED_PLANKS_YELLOW,
      VERTICAL_STAINED_PLANKS_LIME,
      VERTICAL_STAINED_PLANKS_PINK,
      VERTICAL_STAINED_PLANKS_GRAY,
      VERTICAL_STAINED_PLANKS_SILVER,
      VERTICAL_STAINED_PLANKS_CYAN,
      VERTICAL_STAINED_PLANKS_PURPLE,
      VERTICAL_STAINED_PLANKS_BLUE,
      VERTICAL_STAINED_PLANKS_BROWN,
      VERTICAL_STAINED_PLANKS_GREEN,
      VERTICAL_STAINED_PLANKS_RED,
      VERTICAL_STAINED_PLANKS_BLACK;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
