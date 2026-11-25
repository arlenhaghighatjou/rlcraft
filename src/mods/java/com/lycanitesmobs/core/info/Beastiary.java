package com.lycanitesmobs.core.info;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.FearEntity;
import com.lycanitesmobs.core.network.MessageBeastiary;
import com.lycanitesmobs.core.network.MessageCreatureKnowledge;
import com.lycanitesmobs.core.pets.SummonSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;

public class Beastiary {
   public ExtendedPlayer extendedPlayer;
   public Map<String, CreatureKnowledge> creatureKnowledgeList = new HashMap();

   public Beastiary(ExtendedPlayer extendedPlayer) {
      this.extendedPlayer = extendedPlayer;
   }

   public boolean addCreatureKnowledge(CreatureKnowledge newKnowledge, boolean sendToClient) {
      CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(newKnowledge.creatureName);
      if (creatureInfo == null) {
         return false;
      } else if (creatureInfo.dummy) {
         return false;
      } else {
         CreatureKnowledge currentKnowledge = this.getCreatureKnowledge(creatureInfo.getName());
         if (currentKnowledge != null) {
            currentKnowledge.rank = newKnowledge.rank;
            currentKnowledge.experience = newKnowledge.experience;
            if (sendToClient) {
               this.sendToClient(currentKnowledge);
            }

            return true;
         } else {
            this.creatureKnowledgeList.put(newKnowledge.creatureName, newKnowledge);
            if (sendToClient) {
               this.sendAddedMessage(newKnowledge);
               this.sendToClient(newKnowledge);
            }

            return true;
         }
      }
   }

   public CreatureKnowledge addCreatureKnowledge(Entity entity, int experience) {
      if (entity instanceof BaseCreatureEntity && !(entity instanceof FearEntity)) {
         CreatureInfo creatureInfo = ((BaseCreatureEntity)entity).creatureInfo;
         CreatureKnowledge newKnowledge = this.getCreatureKnowledge(creatureInfo.getName());
         if (newKnowledge == null) {
            newKnowledge = new CreatureKnowledge(this.extendedPlayer.getBeastiary(), creatureInfo.getName(), 1, experience);
            newKnowledge.getMaxExperience();
         } else {
            if (newKnowledge.getMaxExperience() <= 0) {
               return null;
            }

            newKnowledge.addExperience(experience);
         }

         this.addCreatureKnowledge(newKnowledge, true);
         return newKnowledge;
      } else {
         return null;
      }
   }

   public void sendAddedMessage(CreatureKnowledge creatureKnowledge) {
      if (!this.extendedPlayer.player.getEntityWorld().isRemote && CreatureManager.getInstance().config.beastiaryKnowledgeMessages) {
         CreatureInfo creatureInfo = creatureKnowledge.getCreatureInfo();
         String message = LanguageManager.translate("message.beastiary.rank");
         if (creatureKnowledge.rank == 1) {
            message = LanguageManager.translate("message.beastiary.new");
         }

         message = message.replace("%creature%", creatureInfo.getTitle());
         message = message.replace("%rank%", "" + creatureKnowledge.rank);
         this.extendedPlayer.player.sendMessage(new TextComponentString(message));
         if (creatureInfo.isSummonable() && creatureKnowledge.rank == 2) {
            String summonMessage = LanguageManager.translate("message.beastiary.summonable");
            summonMessage = summonMessage.replace("%creature%", creatureInfo.getTitle());
            this.extendedPlayer.player.sendMessage(new TextComponentString(summonMessage));
         }

         if (creatureInfo.isTameable() && creatureKnowledge.rank == 2) {
            String tameMessage = LanguageManager.translate("message.beastiary.tameable");
            tameMessage = tameMessage.replace("%creature%", creatureInfo.getTitle());
            this.extendedPlayer.player.sendMessage(new TextComponentString(tameMessage));
         }

      }
   }

   public void sendKnownMessage(CreatureKnowledge creatureKnowledge) {
      if (!this.extendedPlayer.player.getEntityWorld().isRemote) {
         CreatureInfo creatureInfo = creatureKnowledge.getCreatureInfo();
         CreatureKnowledge currentKnowledge = this.extendedPlayer.getBeastiary().getCreatureKnowledge(creatureInfo.getName());
         String message = LanguageManager.translate("message.beastiary.known");
         message = message.replace("%creature%", creatureInfo.getTitle());
         message = message.replace("%rank%", "" + currentKnowledge.rank);
         this.extendedPlayer.player.sendMessage(new TextComponentString(message));
      }
   }

   public CreatureKnowledge getCreatureKnowledge(String creatureName) {
      return !this.creatureKnowledgeList.containsKey(creatureName) ? null : (CreatureKnowledge)this.creatureKnowledgeList.get(creatureName);
   }

   public boolean hasKnowledgeRank(String creatureName, int rank) {
      CreatureKnowledge creatureKnowledge = this.getCreatureKnowledge(creatureName);
      if (creatureKnowledge == null) {
         return false;
      } else {
         return creatureKnowledge.rank >= rank;
      }
   }

   public int getCreaturesDiscovered(CreatureType creatureType) {
      if (this.creatureKnowledgeList.size() == 0) {
         return 0;
      } else {
         int creaturesDescovered = 0;

         for(Entry<String, CreatureKnowledge> creatureKnowledgeEntry : this.creatureKnowledgeList.entrySet()) {
            if (creatureKnowledgeEntry.getValue() != null && ((CreatureKnowledge)creatureKnowledgeEntry.getValue()).getCreatureInfo().creatureType == creatureType) {
               ++creaturesDescovered;
            }
         }

         return creaturesDescovered;
      }
   }

   public Map<Integer, String> getSummonableList() {
      Map<Integer, String> minionList = new HashMap();
      int minionIndex = 0;

      for(String minionName : this.creatureKnowledgeList.keySet()) {
         CreatureKnowledge creatureKnowledge = (CreatureKnowledge)this.creatureKnowledgeList.get(minionName);
         if (creatureKnowledge.rank >= 2 && SummonSet.isSummonableCreature(minionName)) {
            minionList.put(Integer.valueOf(minionIndex++), minionName);
         }
      }

      return minionList;
   }

   public void sendToClient(CreatureKnowledge newKnowledge) {
      if (!this.extendedPlayer.player.getEntityWorld().isRemote) {
         MessageCreatureKnowledge message = new MessageCreatureKnowledge(newKnowledge);
         LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.extendedPlayer.getPlayer());
      }
   }

   public void sendAllToClient() {
      LycanitesMobs.logDebug("Packets", "Sending all beastiary to client.");
      MessageBeastiary message = new MessageBeastiary(this);
      LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.extendedPlayer.getPlayer());
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasKey("CreatureKnowledge")) {
         this.creatureKnowledgeList.clear();
         NBTTagList knowledgeList = nbtTagCompound.getTagList("CreatureKnowledge", 10);

         for(int i = 0; i < knowledgeList.tagCount(); ++i) {
            NBTTagCompound nbtKnowledge = knowledgeList.getCompoundTagAt(i);
            if (nbtKnowledge.hasKey("CreatureName")) {
               String creatureName = nbtKnowledge.getString("CreatureName");
               int rank = 0;
               if (nbtKnowledge.hasKey("Rank")) {
                  rank = nbtKnowledge.getInteger("Rank");
               }

               int experience = 0;
               if (nbtKnowledge.hasKey("Experience")) {
                  experience = nbtKnowledge.getInteger("Experience");
               }

               CreatureKnowledge creatureKnowledge = new CreatureKnowledge(this, creatureName, rank, experience);
               this.addCreatureKnowledge(creatureKnowledge, false);
            }
         }

      }
   }

   public void writeToNBT(NBTTagCompound nbtTagCompound) {
      NBTTagList knowledgeList = new NBTTagList();

      for(Entry<String, CreatureKnowledge> creatureKnowledgeEntry : this.creatureKnowledgeList.entrySet()) {
         CreatureKnowledge creatureKnowledge = (CreatureKnowledge)creatureKnowledgeEntry.getValue();
         NBTTagCompound nbtKnowledge = new NBTTagCompound();
         nbtKnowledge.setString("CreatureName", creatureKnowledge.creatureName);
         nbtKnowledge.setInteger("Rank", creatureKnowledge.rank);
         nbtKnowledge.setInteger("Experience", creatureKnowledge.experience);
         knowledgeList.appendTag(nbtKnowledge);
      }

      nbtTagCompound.setTag("CreatureKnowledge", knowledgeList);
   }
}
