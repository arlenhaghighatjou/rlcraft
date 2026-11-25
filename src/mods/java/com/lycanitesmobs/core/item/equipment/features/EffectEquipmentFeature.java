package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EffectEquipmentFeature extends EquipmentFeature {
   public String effectType;
   public String effectTarget;
   public int effectDuration = 0;
   public int effectStrength = 0;

   public EffectEquipmentFeature() {
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
      this.effectType = json.get("effectType").getAsString();
      this.effectTarget = json.get("effectTarget").getAsString();
      if (json.has("effectDuration")) {
         this.effectDuration = json.get("effectDuration").getAsInt();
      }

      if (json.has("effectStrength")) {
         this.effectStrength = json.get("effectStrength").getAsInt();
      }

   }

   public String getDescription(ItemStack itemStack, int level) {
      if (!this.isActive(itemStack, level)) {
         return null;
      } else {
         String description = LanguageManager.translate("equipment.feature." + this.featureType) + " " + this.getEffectTypeName();
         if (this.effectStrength > 0) {
            description = description + " " + LanguageManager.translate("entity.level") + " " + this.effectStrength;
         }

         if (!"self".equals(this.effectTarget) && this.effectDuration > 0) {
            description = description + " " + (float)this.effectDuration / 20.0F + "s";
         }

         description = description + " (" + this.effectTarget + ")";
         return description;
      }
   }

   public String getSummary(ItemStack itemStack, int level) {
      if (!this.isActive(itemStack, level)) {
         return null;
      } else {
         String summary = this.getEffectTypeName();
         if (this.effectStrength > 0) {
            summary = summary + " " + LanguageManager.translate("entity.level") + " " + this.effectStrength;
         }

         if (!"self".equals(this.effectTarget) && this.effectDuration > 0) {
            summary = summary + " " + (float)this.effectDuration / 20.0F + "s";
         }

         summary = summary + " (" + this.effectTarget + ")";
         return summary;
      }
   }

   public String getEffectTypeName() {
      if ("burning".equals(this.effectType)) {
         return LanguageManager.translate("effect.burning");
      } else {
         ResourceLocation effectResource = new ResourceLocation(this.effectType);
         return LanguageManager.translate("effect." + effectResource.getResourcePath());
      }
   }

   public void onHitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase attacker) {
      if (target != null && attacker != null) {
         EntityLivingBase effectTarget = target;
         if ("self".equalsIgnoreCase(this.effectTarget)) {
            effectTarget = attacker;
         }

         if ("burning".equalsIgnoreCase(this.effectType)) {
            effectTarget.setFire(Math.round((float)this.effectDuration / 20.0F));
         } else {
            Potion potion = (Potion)GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation(this.effectType));
            if (potion != null && this.effectStrength > 0) {
               effectTarget.addPotionEffect(new PotionEffect(potion, this.effectDuration, this.effectStrength - 1));
            }

         }
      }
   }
}
