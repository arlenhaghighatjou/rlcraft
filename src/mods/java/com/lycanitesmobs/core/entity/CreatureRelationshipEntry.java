package com.lycanitesmobs.core.entity;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CreatureRelationshipEntry {
   protected CreatureRelationships creatureRelationships;
   protected UUID targetEntityUUID;
   protected int reputation = 0;

   public CreatureRelationshipEntry(CreatureRelationships creatureRelationships) {
      this.creatureRelationships = creatureRelationships;
   }

   public int getReputation() {
      return this.reputation;
   }

   public void setReputation(int reputation) {
      this.reputation = reputation;
      this.creatureRelationships.getCreatureEntity().queueSync();
   }

   public boolean increaseReputation(int amount) {
      int max = this.creatureRelationships.getCreatureEntity().creatureInfo.getTamingReputation();
      int min = -this.creatureRelationships.getCreatureEntity().creatureInfo.getTamingReputation();
      if (this.reputation >= max) {
         return false;
      } else {
         this.reputation = Math.max(Math.min(this.reputation + amount, max), min);
         this.creatureRelationships.getCreatureEntity().queueSync();
         return true;
      }
   }

   public boolean decreaseReputation(int amount) {
      int max = this.creatureRelationships.getCreatureEntity().creatureInfo.getTamingReputation();
      int min = -this.creatureRelationships.getCreatureEntity().creatureInfo.getTamingReputation();
      if (this.reputation <= min) {
         return false;
      } else {
         this.reputation = Math.max(Math.min(this.reputation - amount, max), min);
         this.creatureRelationships.getCreatureEntity().queueSync();
         return true;
      }
   }

   public boolean canAttack() {
      return this.reputation < this.creatureRelationships.getCreatureEntity().creatureInfo.getTamingReputation();
   }

   public boolean canHunt() {
      return this.reputation < this.creatureRelationships.getCreatureEntity().creatureInfo.getFriendlyReputation();
   }

   public boolean shouldDefend() {
      return this.reputation >= this.creatureRelationships.getCreatureEntity().creatureInfo.getFriendlyReputation();
   }

   public UUID getTargetEntityUUID() {
      return this.targetEntityUUID;
   }

   public void setTarget(Entity entity) {
      this.targetEntityUUID = entity.getUniqueID();
   }

   public void load(NBTTagCompound nbt) {
      if (nbt.hasUniqueId("TargetUUID")) {
         this.targetEntityUUID = nbt.getUniqueId("TargetUUID");
      }

      if (nbt.hasKey("Reputation")) {
         this.reputation = nbt.getInteger("Reputation");
      }

   }

   public void save(NBTTagCompound nbt) {
      nbt.setUniqueId("TargetUUID", this.targetEntityUUID);
      nbt.setInteger("Reputation", this.reputation);
   }
}
