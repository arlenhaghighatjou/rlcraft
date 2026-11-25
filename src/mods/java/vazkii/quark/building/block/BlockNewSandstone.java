package vazkii.quark.building.block;

import java.util.Locale;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.SoulSandstone;

public class BlockNewSandstone extends BlockMetaVariants<BlockNewSandstone.Variants> implements IQuarkBlock {
   public BlockNewSandstone() {
      super("sandstone_new", Material.ROCK, BlockNewSandstone.Variants.class);
      this.setHardness(0.8F);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public boolean shouldDisplayVariant(int variant) {
      return ModuleLoader.isFeatureEnabled(SoulSandstone.class) || variant < 4;
   }

   public static enum Variants implements IStringSerializable {
      SANDSTONE_SMOOTH(false, true),
      SANDSTONE_BRICKS(true, true),
      RED_SANDSTONE_SMOOTH(false, true),
      RED_SANDSTONE_BRICKS(true, true),
      SOUL_SANDSTONE_SMOOTH(false, true),
      SOUL_SANDSTONE_BRICKS(true, true);

      public final boolean stairs;
      public final boolean slabs;

      private Variants(boolean stairs, boolean slabs) {
         this.stairs = stairs;
         this.slabs = slabs;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
