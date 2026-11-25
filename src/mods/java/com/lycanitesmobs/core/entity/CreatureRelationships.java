package com.lycanitesmobs.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CreatureRelationships {
   protected BaseCreatureEntity creatureEntity;
   protected Map<UUID, CreatureRelationshipEntry> relationships = new HashMap();

   public CreatureRelationships(BaseCreatureEntity creatureEntity) {
      this.creatureEntity = creatureEntity;
   }

   @Nullable
   public CreatureRelationshipEntry getEntry(Entity entity) {
      return this.relationships.containsKey(entity.getUniqueID()) ? (CreatureRelationshipEntry)this.relationships.get(entity.getUniqueID()) : null;
   }

   public List<EntityPlayer> getPlayers() {
      List<EntityPlayer> players = new ArrayList();
      if (this.getCreatureEntity().getEntityWorld().isRemote) {
         return players;
      } else {
         for(CreatureRelationshipEntry relationshipEntry : this.relationships.values()) {
            EntityPlayer player = this.getCreatureEntity().getEntityWorld().getPlayerEntityByUUID(relationshipEntry.getTargetEntityUUID());
            if (player != null) {
               players.add(player);
            }
         }

         return players;
      }
   }

   public CreatureRelationshipEntry getOrCreateEntry(Entity entity) {
      CreatureRelationshipEntry relationshipEntry = this.getEntry(entity);
      if (relationshipEntry == null) {
         relationshipEntry = new CreatureRelationshipEntry(this);
         relationshipEntry.setTarget(entity);
         this.relationships.put(relationshipEntry.getTargetEntityUUID(), relationshipEntry);
      }

      return relationshipEntry;
   }

   public BaseCreatureEntity getCreatureEntity() {
      return this.creatureEntity;
   }

   public void load(NBTTagCompound nbt) {
      if (nbt.hasKey("CreatureRelationships")) {
         this.relationships.clear();
         NBTTagList relationshipNbtList = nbt.getTagList("CreatureRelationships", 10);

         for(int i = 0; i < relationshipNbtList.tagCount(); ++i) {
            NBTTagCompound relationshipNbt = relationshipNbtList.getCompoundTagAt(i);
            CreatureRelationshipEntry creatureRelationshipEntry = new CreatureRelationshipEntry(this);
            creatureRelationshipEntry.load(relationshipNbt);
            this.relationships.put(creatureRelationshipEntry.getTargetEntityUUID(), creatureRelationshipEntry);
         }
      }

   }

   public void save(NBTTagCompound nbt) {
      NBTTagList relationshipNbtList = new NBTTagList();
      int i = 0;

      for(CreatureRelationshipEntry creatureRelationshipEntry : this.relationships.values()) {
         NBTTagCompound relationshipNbt = new NBTTagCompound();
         creatureRelationshipEntry.save(relationshipNbt);
         relationshipNbtList.appendTag(relationshipNbt);
      }

      nbt.setTag("CreatureRelationships", relationshipNbtList);
   }
}
