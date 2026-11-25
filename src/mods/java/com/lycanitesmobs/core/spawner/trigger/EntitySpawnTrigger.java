package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.ResourceLocation;

public abstract class EntitySpawnTrigger extends SpawnTrigger {
   public List<EnumCreatureAttribute> entityTypes = new ArrayList();
   public String entityTypesListType = "whitelist";
   public List<String> entityIds = new ArrayList();
   public String entityIdsListType = "blacklist";

   public EntitySpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("entityTypes")) {
         JsonArray jsonArray = json.get("entityTypes").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            EnumCreatureAttribute entityType = EnumCreatureAttribute.valueOf(((JsonElement)jsonIterator.next()).getAsString().toUpperCase(Locale.ENGLISH));
            if (entityType != null) {
               this.entityTypes.add(entityType);
            }
         }
      }

      if (json.has("entityTypesListType")) {
         this.entityTypesListType = json.get("entityTypesListType").getAsString();
      }

      if (json.has("entityIds")) {
         JsonArray jsonArray = json.get("entityIds").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            String entityId = ((JsonElement)jsonIterator.next()).getAsString();
            if (entityId != null) {
               this.entityIds.add(entityId);
            }
         }
      }

      if (json.has("entityIdsListType")) {
         this.entityIdsListType = json.get("entityIdsListType").getAsString();
      }

      super.loadFromJSON(json);
   }

   public boolean isMatchingEntity(EntityLivingBase killedEntity) {
      if (!this.entityTypes.isEmpty()) {
         if (this.entityTypes.contains(killedEntity.getCreatureAttribute())) {
            if ("blacklist".equalsIgnoreCase(this.entityTypesListType)) {
               return false;
            }
         } else if ("whitelist".equalsIgnoreCase(this.entityTypesListType)) {
            return false;
         }
      }

      if (!this.entityIds.isEmpty()) {
         ResourceLocation entityResourceLocation = EntityList.getKey(killedEntity);
         if (entityResourceLocation == null) {
            return false;
         }

         String entityId = entityResourceLocation.toString();
         if (entityId == null) {
            if ("whitelist".equalsIgnoreCase(this.entityIdsListType)) {
               return false;
            }
         } else if (this.entityIds.contains(entityId)) {
            if ("blacklist".equalsIgnoreCase(this.entityIdsListType)) {
               return false;
            }
         } else if ("whitelist".equalsIgnoreCase(this.entityIdsListType)) {
            return false;
         }
      }

      return true;
   }
}
