package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.item.ItemStack;

public class SlotEquipmentFeature extends EquipmentFeature {
   public String slotType;

   public SlotEquipmentFeature() {
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
      this.slotType = json.get("slotType").getAsString();
   }

   public String getDescription(ItemStack itemStack, int level) {
      return !this.isActive(itemStack, level) ? null : LanguageManager.translate("equipment.feature." + this.featureType) + " " + LanguageManager.translate("equipment.slot." + this.slotType);
   }

   public String getSummary(ItemStack itemStack, int level) {
      return this.getDescription(itemStack, level);
   }
}
