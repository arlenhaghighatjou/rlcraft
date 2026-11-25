package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.item.ItemStack;

public class DamageEquipmentFeature extends EquipmentFeature {
   public int damageAmount = 1;
   public double damageCooldown = 1.0D;
   public double damageKnockback = 0.0D;
   public double damageRange = 0.0D;
   public double damageSweep = 45.0D;

   public DamageEquipmentFeature() {
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
      if (json.has("damageAmount")) {
         this.damageAmount = json.get("damageAmount").getAsInt();
      }

      if (json.has("damageCooldown")) {
         this.damageCooldown = json.get("damageCooldown").getAsDouble();
      }

      if (json.has("damageKnockback")) {
         this.damageKnockback = json.get("damageKnockback").getAsDouble();
      }

      if (json.has("damageRange")) {
         this.damageRange = json.get("damageRange").getAsDouble();
      }

      if (json.has("damageSweep")) {
         this.damageSweep = json.get("damageSweep").getAsDouble();
      }

   }

   public String getDescription(ItemStack itemStack, int level) {
      if (!this.isActive(itemStack, level)) {
         return null;
      } else {
         String description = LanguageManager.translate("equipment.feature." + this.featureType) + " " + this.damageAmount;
         if (this.damageCooldown > 0.0D) {
            description = description + "\n" + LanguageManager.translate("equipment.feature.damage.cooldown") + " " + String.format("%.1f", this.damageCooldown);
         }

         if (this.damageKnockback > 0.0D) {
            description = description + "\n" + LanguageManager.translate("equipment.feature.damage.knockback") + " " + String.format("%.0f", this.damageKnockback);
         }

         if (this.damageRange > 0.0D) {
            description = description + "\n" + LanguageManager.translate("equipment.feature.damage.range") + " " + String.format("%.1f", this.damageRange);
         }

         if (this.damageSweep > 0.0D) {
            description = description + "\n" + LanguageManager.translate("equipment.feature.damage.sweep") + " " + String.format("%.0f", this.damageSweep);
         }

         return description;
      }
   }

   public String getSummary(ItemStack itemStack, int level) {
      return this.getDescription(itemStack, level);
   }
}
