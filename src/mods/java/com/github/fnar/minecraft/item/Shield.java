package com.github.fnar.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class Shield extends RldBaseItem {
   private final List<Design> designs = Lists.newArrayList();

   private Shield() {
   }

   public static Shield newShield() {
      return new Shield();
   }

   public ItemType getItemType() {
      return ItemType.SHIELD;
   }

   public Shield withRandomPattern(EaglercraftRandom random) {
      this.withDesign(Design.randomDesign(random));
      return this;
   }

   public Shield withRandomPatterns(EaglercraftRandom random, int count) {
      IntStream.range(0, count).forEach((i) -> {
         this.withRandomPattern(random);
      });
      return this;
   }

   public Shield withDesign(Design design) {
      this.designs.add(design);
      return this;
   }

   public List<Design> getDesigns() {
      return this.designs;
   }
}
