package vazkii.quark.building.block;

import java.util.Locale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockStainedPlanks extends BlockMetaVariants<BlockStainedPlanks.Variants> implements IQuarkBlock {
   public BlockStainedPlanks() {
      super("stained_planks", Material.WOOD, BlockStainedPlanks.Variants.class);
      this.setHardness(2.0F);
      this.setResistance(5.0F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public static enum Variants implements IStringSerializable {
      STAINED_PLANKS_WHITE,
      STAINED_PLANKS_ORANGE,
      STAINED_PLANKS_MAGENTA,
      STAINED_PLANKS_LIGHT_BLUE,
      STAINED_PLANKS_YELLOW,
      STAINED_PLANKS_LIME,
      STAINED_PLANKS_PINK,
      STAINED_PLANKS_GRAY,
      STAINED_PLANKS_SILVER,
      STAINED_PLANKS_CYAN,
      STAINED_PLANKS_PURPLE,
      STAINED_PLANKS_BLUE,
      STAINED_PLANKS_BROWN,
      STAINED_PLANKS_GREEN,
      STAINED_PLANKS_RED,
      STAINED_PLANKS_BLACK;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
