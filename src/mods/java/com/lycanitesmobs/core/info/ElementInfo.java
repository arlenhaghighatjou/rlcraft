package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ElementInfo {
   public String name = "default";
   protected List<String> componentNames = new ArrayList();
   public List<ElementInfo> components = new ArrayList();
   public String type;
   public List<String> buffs = new ArrayList();
   public List<String> debuffs = new ArrayList();
   public List<String> immunities = new ArrayList();
   public double buffDurationMultiplier = 1.0D;
   public double debuffDurationMultiplier = 1.0D;
   public boolean canBurn = true;
   public boolean canFreeze = true;

   public ElementInfo() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("name")) {
         this.name = json.get("name").getAsString();
      }

      if (json.has("components")) {
         this.componentNames = JSONHelper.getJsonStrings(json.get("components").getAsJsonArray());
      }

      if (json.has("type")) {
         this.type = json.get("type").getAsString();
      } else {
         this.type = this.componentNames.isEmpty() ? "primal" : "compound";
      }

      if (json.has("buffs")) {
         this.buffs = JSONHelper.getJsonStrings(json.get("buffs").getAsJsonArray());
      }

      if (json.has("debuffs")) {
         this.debuffs = JSONHelper.getJsonStrings(json.get("debuffs").getAsJsonArray());
      }

      if (json.has("immunities")) {
         this.immunities = JSONHelper.getJsonStrings(json.get("immunities").getAsJsonArray());
      }

      if (json.has("buffDurationMultiplier")) {
         this.buffDurationMultiplier = json.get("buffDurationMultiplier").getAsDouble();
      }

      if (json.has("debuffDurationMultiplier")) {
         this.debuffDurationMultiplier = json.get("debuffDurationMultiplier").getAsDouble();
      }

      if (json.has("canBurn")) {
         this.canBurn = json.get("canBurn").getAsBoolean();
      }

      if (json.has("canFreeze")) {
         this.canFreeze = json.get("canFreeze").getAsBoolean();
      }

   }

   public void init() {
      if (this.type.equalsIgnoreCase("compound")) {
         for(String componentName : this.componentNames) {
            if (ElementManager.getInstance().elements.containsKey(componentName)) {
               this.components.add(ElementManager.getInstance().elements.get(componentName));
            }
         }
      }

   }

   public String getTitle() {
      return LanguageManager.translate("element." + this.name + ".name");
   }

   public String getDescription() {
      return LanguageManager.translate("element." + this.name + ".description");
   }

   public void buffEntity(EntityLivingBase targetEntity, int duration, int amplifier) {
      if (duration > 0 && amplifier >= 0) {
         duration = Math.round((float)duration * (float)this.buffDurationMultiplier);

         for(String buff : this.buffs) {
            Potion potion = (Potion)GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation(buff));
            if (potion != null) {
               targetEntity.addPotionEffect(new PotionEffect(potion, duration, amplifier));
            }
         }

      }
   }

   public void debuffEntity(EntityLivingBase targetEntity, int duration, int amplifier) {
      if (duration > 0 && amplifier >= 0) {
         duration = Math.round((float)duration * (float)this.debuffDurationMultiplier);

         for(String debuff : this.debuffs) {
            if ("burning".equalsIgnoreCase(debuff)) {
               targetEntity.setFire(duration / 20);
            } else {
               Potion potion = (Potion)GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation(debuff));
               if (potion != null) {
                  targetEntity.addPotionEffect(new PotionEffect(potion, duration, amplifier));
               }
            }
         }

      }
   }

   public boolean isEffectApplicable(PotionEffect effect) {
      if (effect != null && effect.getPotion() != null && effect.getPotion().getRegistryName() != null) {
         if (this.debuffs.contains(effect.getPotion().getRegistryName().toString())) {
            return false;
         } else {
            return !this.immunities.contains(effect.getPotion().getRegistryName().toString());
         }
      } else {
         return false;
      }
   }

   public boolean canBurn() {
      return this.canBurn;
   }

   public boolean canFreeze() {
      return this.canFreeze;
   }
}
