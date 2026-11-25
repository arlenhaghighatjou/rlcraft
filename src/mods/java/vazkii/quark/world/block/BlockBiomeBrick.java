package vazkii.quark.world.block;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.feature.UndergroundBiomes;

public class BlockBiomeBrick extends BlockMetaVariants<BlockBiomeBrick.Variants> implements IQuarkBlock {
   public BlockBiomeBrick() {
      super("biome_brick", Material.ROCK, BlockBiomeBrick.Variants.class);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public boolean shouldDisplayVariant(int variant) {
      return BlockBiomeBrick.Variants.values()[variant].isEnabled();
   }

   public static enum Variants implements IStringSerializable {
      FIRE_STONE_BRICK(() -> {
         return UndergroundBiomes.firestoneEnabled;
      }),
      ICY_STONE_BRICK(() -> {
         return UndergroundBiomes.icystoneEnabled;
      });

      private final Supplier<Boolean> enabledCond;

      private Variants(Supplier<Boolean> enabledCond) {
         this.enabledCond = enabledCond;
      }

      public boolean isEnabled() {
         return ((Boolean)this.enabledCond.get()).booleanValue();
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
