package com.github.fnar.minecraft.item;

import java.util.Optional;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class Arrow implements RldItem {
   private Potion tip;

   public Arrow() {
   }

   public static Arrow newArrow() {
      return new Arrow();
   }

   public static Arrow newRandomHarmful(EaglercraftRandom random) {
      return newArrow().withTip(Potion.newPotion().withEffect(Potion.Effect.chooseRandomAmong(random, Potion.Effect.HARMFUL)));
   }

   public ItemType getItemType() {
      return ItemType.ARROW;
   }

   public Arrow withTip(Potion potion) {
      this.tip = potion;
      return this;
   }

   public Optional<Potion> getTip() {
      return Optional.ofNullable(this.tip);
   }
}
