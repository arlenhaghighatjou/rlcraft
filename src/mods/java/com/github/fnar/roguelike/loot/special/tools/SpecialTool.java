package com.github.fnar.roguelike.loot.special.tools;

import com.github.fnar.minecraft.item.Enchantment;
import com.github.fnar.minecraft.item.RldItemStack;
import com.github.fnar.roguelike.loot.special.SpecialEquipment;
import greymerk.roguelike.treasure.loot.Quality;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class SpecialTool extends SpecialEquipment {
   public SpecialTool() {
   }

   public static RldItemStack createTool(EaglercraftRandom random, Quality quality) {
      return chooseTool(random, quality).asStack();
   }

   private static SpecialTool chooseTool(EaglercraftRandom random, Quality quality) {
      switch(random.nextInt(4)) {
      case 0:
         return new SpecialPickaxe(random, quality);
      case 1:
         return new SpecialAxe(random, quality);
      case 2:
         return new SpecialShovel(random, quality);
      case 3:
      default:
         return new SpecialHoe(random, quality);
      }
   }

   protected void withFortune(EaglercraftRandom random) {
      if (random.nextDouble() < 0.15D) {
         int enchantmentLevel = random.nextInt(4);
         if (enchantmentLevel > 0) {
            this.withEnchantment(Enchantment.Effect.FORTUNE.atLevel(enchantmentLevel));
            if (random.nextBoolean()) {
               this.withSuffix("of Prospecting");
            } else {
               this.withPrefix("Dwarven");
            }

         }
      }
   }

   protected void withSilkTouch(EaglercraftRandom random) {
      if (random.nextDouble() < 0.15D) {
         this.withEnchantment(Enchantment.Effect.SILK_TOUCH);
         if (random.nextBoolean()) {
            this.withPrefix("Precision");
         } else {
            this.withPrefix("Elven");
         }

      }
   }

   protected void withEfficiency(EaglercraftRandom random) {
      int enchantmentLevel = random.nextInt(4);
      if (enchantmentLevel > 0) {
         this.withEnchantment(Enchantment.Effect.EFFICIENCY.atLevel(enchantmentLevel));
         if (enchantmentLevel >= 3) {
            this.withPrefix("Artisan's");
         }

      }
   }

   protected SpecialTool withToolEnchantments(EaglercraftRandom random) {
      this.withEfficiency(random);
      this.withSilkTouch(random);
      this.withFortune(random);
      return this;
   }
}
