package com.yungnickyoung.minecraft.yungslaw.integration;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.yungslaw.integration.modules.AE2Module;
import com.yungnickyoung.minecraft.yungslaw.integration.modules.CompatModule;
import com.yungnickyoung.minecraft.yungslaw.integration.modules.QuarkModule;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;

public class Integrations {
   public static QuarkModule QUARK = new QuarkModule();
   public static AE2Module AE2 = new AE2Module();
   public static List<CompatModule> MODULES = Lists.newArrayList(new CompatModule[]{QUARK, AE2});
   public static List<IBlockState> ORES = Lists.newArrayList();

   public Integrations() {
   }

   public static void update() {
      MODULES.forEach((module) -> {
         if (module.shouldBeEnabled() && !module.isEnabled()) {
            module.enable();
         }

         if (!module.shouldBeEnabled() && module.isEnabled()) {
            module.disable();
         }

      });
      MODULES.stream().filter(CompatModule::isEnabled).forEach((module) -> {
         addIfAbsent(ORES, module.ores);
      });
      MODULES.stream().filter((module) -> {
         return !module.isEnabled();
      }).forEach((module) -> {
         ORES.removeAll(module.ores);
      });
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
