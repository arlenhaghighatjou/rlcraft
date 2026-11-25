package vazkii.quark.building.block;

import java.util.Locale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockQuiltedWool extends BlockMetaVariants<BlockQuiltedWool.Variants> implements IQuarkBlock {
   public BlockQuiltedWool() {
      super("quilted_wool", Material.CLOTH, BlockQuiltedWool.Variants.class);
      this.setHardness(0.8F);
      this.setSoundType(SoundType.CLOTH);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public static enum Variants implements IStringSerializable {
      WOOL_QUILTED_WHITE,
      WOOL_QUILTED_ORANGE,
      WOOL_QUILTED_MAGENTA,
      WOOL_QUILTED_LIGHT_BLUE,
      WOOL_QUILTED_YELLOW,
      WOOL_QUILTED_LIME,
      WOOL_QUILTED_PINK,
      WOOL_QUILTED_GRAY,
      WOOL_QUILTED_SILVER,
      WOOL_QUILTED_CYAN,
      WOOL_QUILTED_PURPLE,
      WOOL_QUILTED_BLUE,
      WOOL_QUILTED_BROWN,
      WOOL_QUILTED_GREEN,
      WOOL_QUILTED_RED,
      WOOL_QUILTED_BLACK;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
