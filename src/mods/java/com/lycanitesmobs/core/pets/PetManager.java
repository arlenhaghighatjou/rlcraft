package com.lycanitesmobs.core.pets;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class PetManager {
   public EntityLivingBase host;
   public Map<EaglercraftUUID, PetEntry> entries = new HashMap();
   public List<PetEntry> newEntries = new ArrayList();
   public List<PetEntry> removedEntries = new ArrayList();
   public Map<EaglercraftUUID, NBTTagCompound> entryNBTs = new HashMap();

   public PetManager(EntityLivingBase host) {
      this.host = host;
   }

   public boolean hasEntry(PetEntry petEntry) {
      return this.entries.containsKey(petEntry.petEntryID);
   }

   public void addEntry(PetEntry petEntry) {
      if (this.entries.containsKey(petEntry.petEntryID)) {
         LycanitesMobs.logWarning("", "[Pet Manager] Tried to add a Pet Entry that is already added!");
      } else {
         if (this.entryNBTs.containsKey(petEntry.petEntryID)) {
            petEntry.readFromNBT((NBTTagCompound)this.entryNBTs.get(petEntry.petEntryID));
         }

         this.entries.put(petEntry.petEntryID, petEntry);
         petEntry.onAdd(this);
         this.newEntries.add(petEntry);
      }
   }

   public void removeEntry(PetEntry petEntry) {
      if (!this.entries.containsValue(petEntry)) {
         LycanitesMobs.logWarning("", "[Pet Manager] Tried to remove a pet entry that isn't added!");
      } else {
         this.entries.remove(petEntry.petEntryID);
      }
   }

   public PetEntry getEntry(EaglercraftUUID id) {
      return (PetEntry)this.entries.get(id);
   }

   public List<PetEntry> createEntryListByType(String type) {
      List<PetEntry> filteredEntries = new ArrayList();

      for(PetEntry petEntry : this.entries.values()) {
         if (type.equalsIgnoreCase(petEntry.getType())) {
            filteredEntries.add(petEntry);
         }
      }

      return filteredEntries;
   }

   public void onUpdate(World world) {
      if (!this.entryNBTs.isEmpty()) {
         for(PetEntry petEntry : this.entries.values()) {
            if (this.entryNBTs.containsKey(petEntry.petEntryID)) {
               petEntry.readFromNBT((NBTTagCompound)this.entryNBTs.get(petEntry.petEntryID));
               this.entryNBTs.remove(petEntry.petEntryID);
            }
         }

         for(NBTTagCompound nbtEntry : this.entryNBTs.values()) {
            if (!(this.host instanceof EntityPlayer) || !nbtEntry.getString("Type").equalsIgnoreCase("familiar") || PlayerFamiliars.INSTANCE.playerFamiliars.containsKey(this.host.getUniqueID()) && ((Map)PlayerFamiliars.INSTANCE.playerFamiliars.get(this.host.getUniqueID())).containsKey(nbtEntry.getUniqueId("EaglercraftUUID"))) {
               PetEntry petEntry = new PetEntry(nbtEntry.getUniqueId("EaglercraftUUID"), nbtEntry.getString("Type"), this.host, nbtEntry.getString("SummonType"));
               petEntry.readFromNBT(nbtEntry);
               if (petEntry.active) {
                  this.addEntry(petEntry);
               }
            }
         }

         this.entryNBTs.clear();
      }

      if (this.newEntries.size() > 0) {
         if (!world.isRemote && this.host instanceof EntityPlayer) {
            for(PetEntry petEntry : this.newEntries) {
               ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)this.host);
               if (playerExt != null) {
                  playerExt.sendPetEntryToPlayer(petEntry);
               }
            }
         }

         this.newEntries = new ArrayList();
      }

      int newSpiritReserved = 0;

      for(PetEntry petEntry : this.entries.values()) {
         if (petEntry.host != this.host) {
            petEntry.setOwner(this.host);
         }

         if (this.host instanceof EntityPlayer) {
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)this.host);
            if (playerExt != null && petEntry.usesSpirit()) {
               int spiritCost = petEntry.getSpiritCost();
               if (petEntry.spawningActive && petEntry.active) {
                  newSpiritReserved += spiritCost;
                  if (playerExt.spirit + playerExt.spiritReserved < newSpiritReserved || newSpiritReserved > playerExt.spiritMax) {
                     petEntry.spawningActive = false;
                     newSpiritReserved -= spiritCost;
                  }
               }
            }
         }

         if (petEntry.active) {
            petEntry.onUpdate(world);
         } else {
            this.removedEntries.add(petEntry);
         }
      }

      if (this.removedEntries.size() > 0) {
         for(PetEntry petEntry : this.removedEntries) {
            this.removeEntry(petEntry);
         }

         this.removedEntries = new ArrayList();
      }

      if (this.host instanceof EntityPlayer) {
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)this.host);
         if (playerExt != null) {
            playerExt.spiritReserved = newSpiritReserved;
         }
      }

   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasKey("PetManager")) {
         NBTTagList entryList = nbtTagCompound.getTagList("PetManager", 10);

         for(int i = 0; i < entryList.tagCount(); ++i) {
            NBTTagCompound nbtEntry = entryList.getCompoundTagAt(i);
            if (!nbtEntry.hasUniqueId("EaglercraftUUID") && nbtEntry.hasKey("EntryName")) {
               LycanitesMobs.logInfo("", "[Pets] Converting Pet Entry NBT from older mod version: " + nbtEntry.getString("EntryName") + "...");
               nbtEntry.setUniqueId("EaglercraftUUID", EaglercraftUUID.randomUUID());
            }

            if (nbtEntry.hasUniqueId("EaglercraftUUID")) {
               this.entryNBTs.put(nbtEntry.getUniqueId("EaglercraftUUID"), nbtEntry);
            } else {
               LycanitesMobs.logWarning("", "[Pets] A Pet Entry was missing a EaglercraftUUID and EntryName, this is either a bug or NBT data has been tampered with, please report this!");
            }
         }

      }
   }

   public void writeToNBT(NBTTagCompound nbtTagCompound) {
      NBTTagList entryList = new NBTTagList();

      for(PetEntry petEntry : this.entries.values()) {
         NBTTagCompound nbtEntry = new NBTTagCompound();
         petEntry.writeToNBT(nbtEntry);
         entryList.appendTag(nbtEntry);
      }

      nbtTagCompound.setTag("PetManager", entryList);
   }
}
