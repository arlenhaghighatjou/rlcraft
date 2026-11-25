package vazkii.quark.building.block;

import java.util.Locale;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockSoulSandstone extends BlockMetaVariants<BlockSoulSandstone.Variants> implements IQuarkBlock {
   public BlockSoulSandstone() {
      super("soul_sandstone", Material.ROCK, BlockSoulSandstone.Variants.class);
      this.setHardness(1.0F);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public static enum Variants implements IStringSerializable {
      SOUL_SANDSTONE,
      CHISELED_SOUL_SANDSTONE,
      SMOOTH_SOUL_SANDSTONE;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
