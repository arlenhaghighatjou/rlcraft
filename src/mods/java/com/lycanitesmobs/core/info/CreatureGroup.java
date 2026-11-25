package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class CreatureGroup {
   public List<CreatureInfo> creatures = new ArrayList();
   public String name;
   public List<CreatureGroup> huntGroups = new ArrayList();
   public List<String> huntGroupNames = new ArrayList();
   public List<CreatureGroup> packGroups = new ArrayList();
   public List<String> packGroupNames = new ArrayList();
   public List<CreatureGroup> waryGroups = new ArrayList();
   public List<String> waryGroupNames = new ArrayList();
   public List<CreatureGroup> fleeGroups = new ArrayList();
   public List<String> fleeGroupNames = new ArrayList();
   public List<CreatureGroup> ignoreGroups = new ArrayList();
   public List<String> ignoreGroupNames = new ArrayList();
   public CreatureGroup.Interaction defaultInteraction = CreatureGroup.Interaction.HUNT;
   public boolean animals = false;
   public boolean humanoids = false;
   public boolean raiders = false;
   public boolean frozen = false;
   public boolean inferno = false;
   public List<String> entityIds = new ArrayList();

   public CreatureGroup() {
   }

   public void loadFromJson(JsonObject json) {
      this.name = json.get("name").getAsString();
      if (json.has("hunt")) {
         this.huntGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("hunt"));
      }

      if (json.has("pack")) {
         this.packGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("pack"));
      }

      if (json.has("wary")) {
         this.waryGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("wary"));
      }

      if (json.has("flee")) {
         this.fleeGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("flee"));
      }

      if (json.has("ignore")) {
         this.ignoreGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("ignore"));
      }

      if (json.has("default")) {
         this.defaultInteraction = CreatureGroup.Interaction.valueOf(json.get("default").getAsString().toUpperCase(Locale.ENGLISH));
      }

      if (json.has("animals")) {
         this.animals = json.get("animals").getAsBoolean();
      }

      if (json.has("humanoids")) {
         this.humanoids = json.get("humanoids").getAsBoolean();
      }

      if (json.has("raiders")) {
         this.raiders = json.get("raiders").getAsBoolean();
      }

      if (json.has("frozen")) {
         this.frozen = json.get("frozen").getAsBoolean();
      }

      if (json.has("inferno")) {
         this.inferno = json.get("inferno").getAsBoolean();
      }

      if (json.has("entityIds")) {
         this.entityIds = JSONHelper.getJsonStrings(json.get("entityIds").getAsJsonArray());
      }

   }

   public void init() {
      for(String groupName : this.huntGroupNames) {
         CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
         if (groupName.equals(this.name)) {
            group = this;
         }

         if (group != null) {
            this.huntGroups.add(group);
         }
      }

      for(String groupName : this.packGroupNames) {
         CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
         if (groupName.equals(this.name)) {
            group = this;
         }

         if (group != null) {
            this.packGroups.add(group);
         }
      }

      for(String groupName : this.waryGroupNames) {
         CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
         if (groupName.equals(this.name)) {
            group = this;
         }

         if (group != null) {
            this.waryGroups.add(group);
         }
      }

      for(String groupName : this.fleeGroupNames) {
         CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
         if (groupName.equals(this.name)) {
            group = this;
         }

         if (group != null) {
            this.fleeGroups.add(group);
         }
      }

      for(String groupName : this.ignoreGroupNames) {
         CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
         if (groupName.equals(this.name)) {
            group = this;
         }

         if (group != null) {
            this.ignoreGroups.add(group);
         }
      }

      LycanitesMobs.logDebug("Creature Group", "Loaded Creature Group: " + this.getName());
   }

   public String getName() {
      return this.name;
   }

   public void addCreature(CreatureInfo creature) {
      if (!this.creatures.contains(creature)) {
         this.creatures.add(creature);
      }
   }

   public boolean hasEntity(Entity entity) {
      if (entity instanceof BaseCreatureEntity) {
         return ((BaseCreatureEntity)entity).creatureInfo.groups.contains(this);
      } else if (!(entity instanceof EntityLivingBase)) {
         return false;
      } else if (this.animals && entity instanceof EntityAnimal) {
         return true;
      } else if (!this.humanoids || !(entity instanceof EntityPlayer) && !(entity instanceof EntityVillager) && !(entity instanceof AbstractIllager) && !(entity instanceof EntityWitch)) {
         if (!this.raiders || !(entity instanceof AbstractIllager) && !(entity instanceof EntityWitch)) {
            if (this.frozen && entity instanceof EntitySnowman) {
               return true;
            } else if (!this.inferno || !(entity instanceof EntityBlaze) && !(entity instanceof EntityMagmaCube)) {
               ResourceLocation entityResourceLocation = EntityList.getKey(entity);
               if (entityResourceLocation == null) {
                  return false;
               } else {
                  String entityId = entityResourceLocation.toString();
                  return this.entityIds.contains(entityId);
               }
            } else {
               return true;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public boolean shouldRevenge(Entity entity) {
      for(CreatureGroup group : this.ignoreGroups) {
         if (group.hasEntity(entity)) {
            return false;
         }
      }

      for(CreatureGroup group : this.waryGroups) {
         if (group.hasEntity(entity)) {
            return false;
         }
      }

      for(CreatureGroup group : this.fleeGroups) {
         if (group.hasEntity(entity)) {
            return false;
         }
      }

      return this.defaultInteraction == CreatureGroup.Interaction.HUNT || this.defaultInteraction == CreatureGroup.Interaction.RETALIATE;
   }

   public boolean shouldHunt(Entity entity) {
      for(CreatureGroup group : this.ignoreGroups) {
         if (group.hasEntity(entity)) {
            return false;
         }
      }

      for(CreatureGroup group : this.huntGroups) {
         if (group.hasEntity(entity)) {
            return true;
         }
      }

      return this.defaultInteraction == CreatureGroup.Interaction.HUNT;
   }

   public boolean shouldPackHunt(Entity entity) {
      for(CreatureGroup group : this.ignoreGroups) {
         if (group.hasEntity(entity)) {
            return false;
         }
      }

      for(CreatureGroup group : this.packGroups) {
         if (group.hasEntity(entity)) {
            return true;
         }
      }

      return this.defaultInteraction == CreatureGroup.Interaction.PACKHUNT;
   }

   public boolean shouldFlee(Entity entity) {
      for(CreatureGroup group : this.fleeGroups) {
         if (group.hasEntity(entity)) {
            return true;
         }
      }

      return this.defaultInteraction == CreatureGroup.Interaction.FLEE;
   }

   static enum Interaction {
      HUNT("hunt"),
      PACKHUNT("pack"),
      WARY("wary"),
      FLEE("flee"),
      IGNORE("ignore"),
      RETALIATE("retaliate");

      public final String name;

      private Interaction(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }
}
