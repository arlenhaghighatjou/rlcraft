package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

public class EquipmentFeature {
   public String featureType;
   public int levelMin = -1;
   public int levelMax = -1;

   public EquipmentFeature() {
   }

   public static EquipmentFeature createFromJSON(JsonObject json) {
      String type = json.get("featureType").getAsString();
      EquipmentFeature equipmentFeature = null;
      if ("slot".equalsIgnoreCase(type)) {
         equipmentFeature = new SlotEquipmentFeature();
      } else if ("harvest".equalsIgnoreCase(type)) {
         equipmentFeature = new HarvestEquipmentFeature();
      } else if ("damage".equalsIgnoreCase(type)) {
         equipmentFeature = new DamageEquipmentFeature();
      } else if ("effect".equalsIgnoreCase(type)) {
         equipmentFeature = new EffectEquipmentFeature();
      } else if ("summon".equalsIgnoreCase(type)) {
         equipmentFeature = new SummonEquipmentFeature();
      } else if ("projectile".equalsIgnoreCase(type)) {
         equipmentFeature = new ProjectileEquipmentFeature();
      }

      if (equipmentFeature == null) {
         return null;
      } else {
         equipmentFeature.featureType = type;
         equipmentFeature.loadFromJSON(json);
         return equipmentFeature;
      }
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("levelMin")) {
         this.levelMin = json.get("levelMin").getAsInt();
      }

      if (json.has("levelMax")) {
         this.levelMax = json.get("levelMax").getAsInt();
      }

   }

   public boolean isActive(ItemStack itemStack, int level) {
      if (this.levelMin > -1 && level < this.levelMin) {
         return false;
      } else {
         return this.levelMax <= -1 || level <= this.levelMax;
      }
   }

   public String getDescription(ItemStack itemStack, int level) {
      return !this.isActive(itemStack, level) ? null : "";
   }

   public String getSummary(ItemStack itemStack, int level) {
      return !this.isActive(itemStack, level) ? null : this.featureType;
   }
}
