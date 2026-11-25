package com.charles445.simpledifficulty.debug;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDEnchantments;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.SDPotions;

public class DebugVerifier {
   public DebugVerifier() {
   }

   public void verify() {
      SimpleDifficulty.logger.debug("Running DebugVerifier");
      this.test(SDItems.canteen, SDItems.items.get("canteen"));
      this.test(SDItems.charcoalFilter, SDItems.items.get("charcoal_filter"));
      this.test(SDItems.juice, SDItems.items.get("juice"));
      this.test(SDItems.purifiedWaterBottle, SDItems.items.get("purified_water_bottle"));
      this.test(SDItems.ice_chunk, SDItems.items.get("ice_chunk"));
      this.test(SDItems.magma_chunk, SDItems.items.get("magma_chunk"));
      this.test(SDItems.thermometer, SDItems.items.get("thermometer"));
      this.test(SDItems.wool_helmet, SDItems.items.get("wool_helmet"));
      this.test(SDItems.wool_chestplate, SDItems.items.get("wool_chestplate"));
      this.test(SDItems.wool_leggings, SDItems.items.get("wool_leggings"));
      this.test(SDItems.wool_boots, SDItems.items.get("wool_boots"));
      this.test(SDItems.ice_helmet, SDItems.items.get("ice_helmet"));
      this.test(SDItems.ice_chestplate, SDItems.items.get("ice_chestplate"));
      this.test(SDItems.ice_leggings, SDItems.items.get("ice_leggings"));
      this.test(SDItems.ice_boots, SDItems.items.get("ice_boots"));
      this.test(SDItems.woolArmorMaterial, SDItems.armorMaterials.get("woolArmorMaterial"));
      this.test(SDItems.iceArmorMaterial, SDItems.armorMaterials.get("iceArmorMaterial"));
      this.test(SDFluids.blockPurifiedWater, SDFluids.fluidBlocks.get(SDFluids.fluidBlocks.keySet().iterator().next()));
      this.test(SDFluids.purifiedWater, SDFluids.fluids.get(SDFluids.fluids.keySet().iterator().next()));
      this.test(SDBlocks.campfire, SDBlocks.blocks.get("campfire"));
      this.test(SDBlocks.rainCollector, SDBlocks.blocks.get("rain_collector"));
      this.test(SDBlocks.heater, SDBlocks.blocks.get("heater"));
      this.test(SDBlocks.chiller, SDBlocks.blocks.get("chiller"));
      this.test(SDBlocks.spit, SDBlocks.blocks.get("spit"));
      this.test(SDEnchantments.chilling, SDEnchantments.enchantments.get("chilling"));
      this.test(SDEnchantments.heating, SDEnchantments.enchantments.get("heating"));
      this.test(SDPotions.hyperthermia, SDPotions.potions.get("hyperthermia"));
      this.test(SDPotions.hypothermia, SDPotions.potions.get("hypothermia"));
      this.test(SDPotions.thirsty, SDPotions.potions.get("thirsty"));
      this.test(SDPotions.parasites, SDPotions.potions.get("parasites"));
      this.test(SDPotions.cold_resist, SDPotions.potions.get("cold_resist"));
      this.test(SDPotions.heat_resist, SDPotions.potions.get("heat_resist"));
      this.test(SDPotions.cold_resist_type, SDPotions.potionTypes.get("cold_resist_type"));
      this.test(SDPotions.long_cold_resist_type, SDPotions.potionTypes.get("long_cold_resist_type"));
      this.test(SDPotions.heat_resist_type, SDPotions.potionTypes.get("heat_resist_type"));
      this.test(SDPotions.long_heat_resist_type, SDPotions.potionTypes.get("long_heat_resist_type"));
      SimpleDifficulty.logger.debug("Stopping DebugVerifier");
   }

   public void test(Object a, Object b) {
      if (a != null && b != null) {
         if (!a.equals(b)) {
            SimpleDifficulty.logger.debug("Test Failure: " + a.toString() + ".equals(" + b.toString() + ")");
            this.printFailure(" .equals ", a, b);
         }

         if (a != b) {
            SimpleDifficulty.logger.debug("Test Failure: " + a.toString() + " == " + b.toString());
            this.printFailure(" == ", a, b);
         }

      } else {
         this.printFailure(" null ", a, b);
      }
   }

   private void printFailure(String s, Object a, Object b) {
      SimpleDifficulty.logger.error("Pointer verification failure:" + a.toString() + s + b.toString());
   }
}
