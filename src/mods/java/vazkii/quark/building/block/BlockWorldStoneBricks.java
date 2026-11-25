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

public class BlockWorldStoneBricks extends BlockMetaVariants<BlockWorldStoneBricks.Variants> implements IQuarkBlock {
   public BlockWorldStoneBricks() {
      super("world_stone_bricks", Material.ROCK, BlockWorldStoneBricks.Variants.class);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public boolean shouldDisplayVariant(int variant) {
      return BlockWorldStoneBricks.Variants.values()[variant].isEnabled();
   }

   public static enum Variants implements IStringSerializable {
      STONE_GRANITE_BRICKS("granite", WorldStoneBricks.class),
      STONE_DIORITE_BRICKS("diorite", WorldStoneBricks.class),
      STONE_ANDESITE_BRICKS("andesite", WorldStoneBricks.class),
      STONE_BASALT_BRICKS("basalt", Basalt.class),
      STONE_MARBLE_BRICKS("marble", RevampStoneGen.class, () -> {
         return RevampStoneGen.enableMarble;
      }),
      STONE_LIMESTONE_BRICKS("limestone", RevampStoneGen.class, () -> {
         return RevampStoneGen.enableLimestone;
      }),
      STONE_JASPER_BRICKS("jasper", RevampStoneGen.class, () -> {
         return RevampStoneGen.enableJasper;
      }),
      STONE_SLATE_BRICKS("slate", RevampStoneGen.class, () -> {
         return RevampStoneGen.enableSlate;
      });

      public final String blockName;
      public final Class<? extends Feature> featureLink;
      private final Supplier<Boolean> enabledCond;

      private Variants(String name, Class<? extends Feature> clazz) {
         this(name, clazz, () -> {
            return (void)true;
         });
      }

      private Variants(String name, Class<? extends Feature> clazz, Supplier<Boolean> enabledCond) {
         this.blockName = name;
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
