package com.yungnickyoung.minecraft.bettermineshafts.integration;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

public class Integrations {
   public static RusticModule RUSTIC = new RusticModule();
   public static CharmModule CHARM = new CharmModule();
   public static VariedCommoditiesModule VARIED_COMMODITIES = new VariedCommoditiesModule();
   private static final List<CompatModule> MODULES_LIST = Lists.newArrayList(new CompatModule[]{RUSTIC, CHARM, VARIED_COMMODITIES});
   private static List<IBlockState> LANTERNS = Lists.newArrayList();
   private static List<IBlockState> LEFT_TORCHES = Lists.newArrayList(new IBlockState[]{Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST)});
   private static List<IBlockState> RIGHT_TORCHES = Lists.newArrayList(new IBlockState[]{Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST)});

   public Integrations() {
   }

   public static void update() {
      MODULES_LIST.forEach((module) -> {
         if (module.shouldBeEnabled() && !module.isEnabled()) {
            module.enable();
         }

         if (!module.shouldBeEnabled() && module.isEnabled()) {
            module.disable();
         }

      });
      MODULES_LIST.stream().filter(CompatModule::isEnabled).forEach((module) -> {
         addIfAbsent(LANTERNS, module.lanterns);
      });
      MODULES_LIST.stream().filter(CompatModule::isEnabled).forEach((module) -> {
         addIfAbsent(LEFT_TORCHES, module.leftTorches);
      });
      MODULES_LIST.stream().filter(CompatModule::isEnabled).forEach((module) -> {
         addIfAbsent(RIGHT_TORCHES, module.rightTorches);
      });
      MODULES_LIST.stream().filter((module) -> {
         return !module.isEnabled();
      }).forEach((module) -> {
         LANTERNS.removeAll(module.lanterns);
      });
      MODULES_LIST.stream().filter((module) -> {
         return !module.isEnabled();
      }).forEach((module) -> {
         LEFT_TORCHES.removeAll(module.leftTorches);
      });
      MODULES_LIST.stream().filter((module) -> {
         return !module.isEnabled();
      }).forEach((module) -> {
         RIGHT_TORCHES.removeAll(module.rightTorches);
      });
   }

   public static IBlockState getLantern(Random random) {
      if (LANTERNS.size() == 0) {
         return null;
      } else {
         float delta = 1.0F / (float)LANTERNS.size();
         float currBottom = 0.0F;
         float r = random.nextFloat();

         for(IBlockState lantern : LANTERNS) {
            if (r >= currBottom && r < currBottom + delta) {
               return lantern;
            }

            currBottom += delta;
         }

         return (IBlockState)LANTERNS.get(LANTERNS.size() - 1);
      }
   }

   public static IBlockState getLeftTorch(Random random) {
      if (LEFT_TORCHES.size() == 0) {
         return null;
      } else {
         float delta = 1.0F / (float)LEFT_TORCHES.size();
         float currBottom = 0.0F;
         float r = random.nextFloat();

         for(IBlockState torch : LEFT_TORCHES) {
            if (r >= currBottom && r < currBottom + delta) {
               return torch;
            }

            currBottom += delta;
         }

         return (IBlockState)LEFT_TORCHES.get(LEFT_TORCHES.size() - 1);
      }
   }

   public static IBlockState getRightTorch(Random random) {
      if (RIGHT_TORCHES.size() == 0) {
         return null;
      } else {
         float delta = 1.0F / (float)RIGHT_TORCHES.size();
         float currBottom = 0.0F;
         float r = random.nextFloat();

         for(IBlockState torch : RIGHT_TORCHES) {
            if (r >= currBottom && r < currBottom + delta) {
               return torch;
            }

            currBottom += delta;
         }

         return (IBlockState)RIGHT_TORCHES.get(RIGHT_TORCHES.size() - 1);
      }
   }

   private static <T> void addIfAbsent(List<T> list, T item) {
      if (!list.contains(item)) {
         list.add(item);
      }

   }

   private static <T> void addIfAbsent(List<T> list, Collection<T> items) {
      items.forEach((item) -> {
         addIfAbsent(list, item);
      });
   }
}
