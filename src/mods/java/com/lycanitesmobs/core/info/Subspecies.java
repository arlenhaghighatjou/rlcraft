package com.lycanitesmobs.core.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Subspecies {
   public Map<Integer, Variant> variants = new HashMap();
   public int index;
   public int priority = 0;
   public String name;
   @SideOnly(Side.CLIENT)
   public Class<? extends ModelBase> modelClass;
   public List<ElementInfo> elements = new ArrayList();
   public List<SpawnCondition> spawnConditions = new ArrayList();
   public Vec3d mountOffset = new Vec3d(0.0D, 1.0D, 0.0D);

   public static Subspecies createFromJSON(CreatureInfo creatureInfo, JsonObject json) {
      String name = null;
      if (json.has("name")) {
         name = json.get("name").getAsString().toLowerCase();
      }

      Subspecies subspecies = new Subspecies(name, json.get("index").getAsInt());
      if (json.has("variants")) {
         for(JsonElement jsonElement : json.get("variants").getAsJsonArray()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Variant variant = Variant.createFromJSON(creatureInfo, jsonObject);
            subspecies.variants.put(Integer.valueOf(variant.index), variant);
         }
      }

      if (json.has("modelClass")) {
         try {
            LycanitesMobs.proxy.loadSubspeciesModel(subspecies, json.get("modelClass").getAsString());
         } catch (Exception var9) {
            LycanitesMobs.logWarning("", "[Creature] Unable to find a valid Java Model Class: " + json.get("modelClass").getAsString() + " for subspecies: " + subspecies.getTitle() + " entity: " + creatureInfo.getTitle());
         }
      }

      if (json.has("priority")) {
         subspecies.priority = json.get("priority").getAsInt();
      }

      List<String> elementNames = new ArrayList();
      if (json.has("element")) {
         elementNames.add(json.get("element").getAsString());
      }

      if (json.has("elements")) {
         elementNames = JSONHelper.getJsonStrings(json.get("elements").getAsJsonArray());
      }

      subspecies.elements.clear();

      for(String elementName : elementNames) {
         ElementInfo element = ElementManager.getInstance().getElement(elementName);
         if (element == null) {
            throw new RuntimeException("[Creature] The element " + elementName + " cannot be found for subspecies: " + subspecies.name);
         }

         subspecies.elements.add(element);
      }

      if (json.has("conditions")) {
         JsonArray jsonArray = json.get("conditions").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject conditionJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            SpawnCondition spawnCondition = SpawnCondition.createFromJSON(conditionJson);
            subspecies.spawnConditions.add(spawnCondition);
         }
      }

      subspecies.mountOffset = JSONHelper.getVec3d(json, "mountOffset", (Vec3d)null);
      return subspecies;
   }

   public Subspecies(String name, int index) {
      this.name = name;
      this.index = index;
   }

   public void load(CreatureInfo creatureInfo) {
      if (this.name != null && !creatureInfo.loadedSubspeciesSkins.contains(this.name)) {
         creatureInfo.addSounds("." + this.name);
         creatureInfo.loadedSubspeciesSkins.add(this.name);
      }

   }

   public String getTitle() {
      return this.name != null ? LanguageManager.translate("subspecies." + this.name + ".name") : "";
   }

   public boolean canSpawn(EntityLivingBase entityLiving) {
      if (entityLiving != null) {
         World world = entityLiving.getEntityWorld();

         for(SpawnCondition condition : this.spawnConditions) {
            if (!condition.isMet(world, (EntityPlayer)null, entityLiving.getPosition())) {
               return false;
            }
         }
      }

      return true;
   }

   public Variant getRandomVariant(EntityLivingBase entity, boolean rare) {
      LycanitesMobs.logDebug("Subspecies", "~0===== Variant =====0~");
      LycanitesMobs.logDebug("Subspecies", "Selecting random variant for: " + entity);
      if (rare) {
         LycanitesMobs.logDebug("Subspecies", "The conditions have been set to rare increasing the chances of a variant being picked.");
      }

      if (this.variants.isEmpty()) {
         LycanitesMobs.logDebug("Subspecies", "No variants available, will be base variant.");
         return null;
      } else {
         LycanitesMobs.logDebug("Subspecies", "Variants Available: " + this.variants.size());
         int baseSpeciesWeightScaled = Variant.BASE_WEIGHT;
         if (rare) {
            baseSpeciesWeightScaled = Math.round((float)baseSpeciesWeightScaled / 4.0F);
         }

         int totalWeight = baseSpeciesWeightScaled;

         for(Variant variant : this.variants.values()) {
            totalWeight += variant.weight;
         }

         LycanitesMobs.logDebug("Subspecies", "Total Weight: " + totalWeight);
         int roll = entity.getRNG().nextInt(totalWeight) + 1;
         LycanitesMobs.logDebug("Subspecies", "Rolled: " + roll);
         if (roll <= baseSpeciesWeightScaled) {
            LycanitesMobs.logDebug("Subspecies", "Base variant selected: " + baseSpeciesWeightScaled);
            return null;
         } else {
            int checkWeight = baseSpeciesWeightScaled;

            for(Variant variant : this.variants.values()) {
               checkWeight += variant.weight;
               if (roll <= checkWeight) {
                  LycanitesMobs.logDebug("Subspecies", "Variant selected: " + variant.toString());
                  return variant;
               }
            }

            LycanitesMobs.logWarning("", "The roll was higher than the Total Weight, this shouldn't happen.");
            return null;
         }
      }
   }

   public Variant getVariant(int index) {
      return !this.variants.containsKey(Integer.valueOf(index)) ? null : (Variant)this.variants.get(Integer.valueOf(index));
   }

   public Variant getChildVariant(EntityLivingBase entity, Variant hostVariant, Variant partnerVariant) {
      if (hostVariant != null) {
         int var10000 = hostVariant.index;
      } else {
         boolean var9 = false;
      }

      if (partnerVariant != null) {
         int var10 = partnerVariant.index;
      } else {
         boolean var11 = false;
      }

      if (hostVariant == partnerVariant) {
         return hostVariant;
      } else {
         int hostWeight = hostVariant != null ? hostVariant.weight : Variant.BASE_WEIGHT;
         int partnerWeight = partnerVariant != null ? partnerVariant.weight : Variant.BASE_WEIGHT;
         int roll = entity.getRNG().nextInt(hostWeight + partnerWeight);
         return roll > hostWeight ? partnerVariant : hostVariant;
      }
   }

   public String toString() {
      return this.name != null ? this.name : "normal";
   }

   public static int getIndexFromOld(int oldIndex) {
      return oldIndex > 3 ? 1 : 0;
   }
}
