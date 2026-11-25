package vazkii.quark.building.block;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.WorldStoneBricks;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;

public class BlockWorldStonePavement extends BlockMetaVariants<BlockWorldStonePavement.Variants> implements IQuarkBlock {
   public BlockWorldStonePavement() {
      super("world_stone_pavement", Material.ROCK, BlockWorldStonePavement.Variants.class);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public boolean shouldDisplayVariant(int variant) {
      return BlockWorldStonePavement.Variants.values()[variant].isEnabled();
   }

   public static enum Variants implements IStringSerializable {
      STONE_GRANITE_PAVEMENT(WorldStoneBricks.class),
      STONE_DIORITE_PAVEMENT(WorldStoneBricks.class),
      STONE_ANDESITE_PAVEMENT(WorldStoneBricks.class),
      STONE_BASALT_PAVEMENT(Basalt.class),
      STONE_MARBLE_PAVEMENT(RevampStoneGen.class, () -> {
         return RevampStoneGen.enableMarble;
      }),
      STONE_LIMESTONE_PAVEMENT(RevampStoneGen.class, () -> {
         return RevampStoneGen.enableLimestone;
      }),
      STONE_JASPER_PAVEMENT(RevampStoneGen.class, () -> {
         return RevampStoneGen.enableJasper;
      }),
      STONE_SLATE_PAVEMENT(RevampStoneGen.class, () -> {
         return RevampStoneGen.enableSlate;
      });

      public final Class<? extends Feature> featureLink;
      private final Supplier<Boolean> enabledCond;

      private Variants(Class<? extends Feature> clazz) {
         this(clazz, () -> {
            return (void)true;
         });
      }

      private Variants(Class<? extends Feature> clazz, Supplier<Boolean> enabledCond) {
         this.featureLink = clazz;
         this.enabledCond = enabledCond;
      }

      public boolean isEnabled() {
         return ModuleLoader.isFeatureEnabled(this.featureLink) && ((Boolean)this.enabledCond.get()).booleanValue();
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
