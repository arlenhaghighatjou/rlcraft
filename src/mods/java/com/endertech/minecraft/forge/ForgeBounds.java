package com.endertech.minecraft.forge;

import com.endertech.common.FloatBounds;
import com.endertech.common.IntBounds;

public enum ForgeBounds {
   ATTACK_SPEED(0.0F, 4.0F),
   BARGAINS_PER_TRADER(1, 4),
   BLOCK_HARDNESS(-1, 100),
   BLOCK_RESISTANCE(-1, 320),
   CHEMICAL_EXPLOSION_PROBABILITY(0.0F, 0.5F),
   CHEMICAL_EXPLOSION_STRENGTH(0.0F, 8.0F),
   DIMENSION_ID(Integer.MIN_VALUE, Integer.MAX_VALUE),
   DURABILITY(0, 65535),
   ENCHANTABILITY(0, 25),
   ENTITY_AIR(0, 300),
   EXPERIENCE(0, 256),
   FACTOR(0.0F, 1.0F),
   FOG_DENSITY(0.0F, 0.1F),
   FOOD_LEVEL(0, 20),
   HARDNESS(0, 10),
   HARVEST_LEVEL(0, 4),
   HARVEST_XP(0, 7),
   HEIGHT(0, 255),
   KEY_CODE(-100, 223),
   LIGHT_OPACITY(0, 255),
   LIGHT_VALUE(0, 15),
   ORE_BLOCKS_IN_DEPOSIT(16, 256),
   ORE_BLOCKS_IN_VEIN(1, 12),
   ORE_VEINS_IN_CHUNK(1, 32),
   ORE_Y(5, 128),
   PERCENTAGE(0, 100),
   POLLUTION(0, 8192),
   PROBABILITY(0, 1),
   RAIL_SIGNAL_DISTANCE(0, 16),
   REDSTONE_POWER(0, 15),
   RESISTANCE(0.0F, 32.0F),
   SMELTING_XP(0, 7),
   STACK_SIZE(1, 64),
   TOUGHNESS(0.0F, 5.0F);

   private final IntBounds intBounds;
   private final FloatBounds floatBounds;

   private ForgeBounds(int min, int max) {
      this.intBounds = new IntBounds(min, max);
      this.floatBounds = new FloatBounds((float)min, (float)max);
   }

   private ForgeBounds(float min, float max) {
      this.floatBounds = new FloatBounds(min, max);
      this.intBounds = new IntBounds((int)min, (int)max);
   }

   public static float invertFactor(float factor) {
      return FACTOR.getFloatBounds().getMax().floatValue() - factor;
   }

   public FloatBounds getFloatBounds() {
      return this.floatBounds;
   }

   public IntBounds getIntBounds() {
      return this.intBounds;
   }
}
