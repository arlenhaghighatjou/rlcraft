package com.lycanitesmobs.core.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.GenericFoodItem;
import com.lycanitesmobs.core.item.GenericItem;
import java.util.Iterator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemInfo {
   public Item item;
   protected String name;
   public ModInfo group;

   public ItemInfo(ModInfo group) {
      this.group = group;
   }

   public void loadFromJSON(JsonObject json) {
      this.name = json.get("name").getAsString();
      LycanitesMobs.logDebug("", "Loading item " + this.name + " from JSON...");
      String modelName = null;
      if (json.has("model")) {
         modelName = json.get("model").getAsString();
      }

      CreativeTabs group = LycanitesMobs.itemsTab;
      if (json.has("group")) {
         String groupName = json.get("group").getAsString();
         if ("blocks".equalsIgnoreCase(groupName)) {
            group = LycanitesMobs.blocksTab;
         }

         if ("creatures".equalsIgnoreCase(groupName)) {
            group = LycanitesMobs.creaturesTab;
         }
      }

      int maxStackSize = 64;
      if (json.has("maxStackSize")) {
         maxStackSize = json.get("maxStackSize").getAsInt();
      }

      FoodInfo food = null;
      if (json.has("food")) {
         JsonObject foodJson = json.get("food").getAsJsonObject();
         food = new FoodInfo();
         food.hunger(foodJson.get("hunger").getAsInt());
         food.saturation(foodJson.get("saturation").getAsFloat());
         if (!foodJson.has("alwaysEdible") || foodJson.get("alwaysEdible").getAsBoolean()) {
            food.setAlwaysEdible();
         }

         if (foodJson.has("fast") && foodJson.get("fast").getAsBoolean()) {
            food.fastToEat();
         }

         if (foodJson.has("meat") && foodJson.get("meat").getAsBoolean()) {
            food.meat();
         }

         if (foodJson.has("effects")) {
            JsonArray effectsJson = foodJson.getAsJsonArray("effects");
            Iterator<JsonElement> jsonIterator = effectsJson.iterator();

            while(jsonIterator.hasNext()) {
               JsonObject foodEffectJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
               String effectId = foodEffectJson.get("effectId").getAsString();
               float chance = 1.0F;
               if (foodEffectJson.has("chance")) {
                  chance = foodEffectJson.get("chance").getAsFloat();
               }

               EffectEntry effectEntry = new EffectEntry(effectId, foodEffectJson.get("duration").getAsInt() * 20, foodEffectJson.get("amplifier").getAsInt(), chance);
               food.effect(effectEntry);
            }
         }
      }

      ItemProperties properties = new ItemProperties();
      properties.group(group);
      properties.maxStackSize(maxStackSize);
      if (food != null) {
         properties.food(food);
         this.item = new GenericFoodItem(properties, this.name);
      } else {
         this.item = new GenericItem(properties, this.name);
         ((GenericItem)this.item).modelName = modelName;
      }

   }

   public Item getItem() {
      return this.item;
   }

   public String getName() {
      return this.name;
   }
}
