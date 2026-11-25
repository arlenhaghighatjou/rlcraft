package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.core.info.Beastiary;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.temp.ItemStaffSummoning;
import com.lycanitesmobs.core.network.MessagePetEntry;
import com.lycanitesmobs.core.network.MessagePetEntryRemove;
import com.lycanitesmobs.core.network.MessagePlayerStats;
import com.lycanitesmobs.core.network.MessageSummonSet;
import com.lycanitesmobs.core.network.MessageSummonSetSelection;
import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.pets.PetManager;
import com.lycanitesmobs.core.pets.PlayerFamiliars;
import com.lycanitesmobs.core.pets.SummonSet;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;

public class ExtendedPlayer {
   public EntityPlayer player;
   public Beastiary beastiary;
   public PetManager petManager;
   public long timePlayed = 0L;
   public CreatureType selectedCreatureType;
   public CreatureInfo selectedCreature;
   public int selectedSubspecies = 0;
   public int selectedVariant = 0;
   public int selectedPetType = 0;
   public PetEntry selectedPet;
   public long currentTick = 0L;
   public boolean needsFullSync = true;
   public IBlockState justBrokenBlock;
   protected int justBrokenClearTime;
   public byte controlStates = 0;
   public boolean hasAttacked = false;
   public int spiritCharge = 100;
   public int spiritMax;
   public int spirit;
   public int spiritReserved;
   public int spiritRecharge;
   public int selectedSummonSet;
   public int summonFocusCharge;
   public int summonFocusMax;
   public int summonFocus;
   public int summonFocusRecharge;
   public Map<Integer, SummonSet> summonSets;
   public int summonSetMax;
   public PortalEntity staffPortal;
   public int creatureStudyCooldown;
   public int creatureStudyCooldownMax;
   private boolean initialSetup;

   public static ExtendedPlayer getForPlayer(EntityPlayer player) {
      if (player == null) {
         return null;
      } else {
         ExtendedPlayer extendedPlayer = (ExtendedPlayer)player.getCapability(LycanitesMobs.EXTENDED_PLAYER, (EnumFacing)null);
         if (extendedPlayer.player != player) {
            extendedPlayer.setPlayer(player);
         }

         return extendedPlayer;
      }
   }

   public ExtendedPlayer() {
      this.spiritMax = this.spiritCharge * 10;
      this.spirit = this.spiritMax;
      this.spiritReserved = 0;
      this.spiritRecharge = 10;
      this.selectedSummonSet = 1;
      this.summonFocusCharge = 600;
      this.summonFocusMax = this.summonFocusCharge * 10;
      this.summonFocus = this.summonFocusMax;
      this.summonFocusRecharge = 10;
      this.summonSets = new HashMap();
      this.summonSetMax = 5;
      this.creatureStudyCooldown = 0;
      this.creatureStudyCooldownMax = 200;
      this.initialSetup = false;
      this.beastiary = new Beastiary(this);
      this.petManager = new PetManager(this.player);
      this.spiritRecharge = LycanitesMobs.config.getInt("Player", "Spirit Recharge", this.spiritRecharge, "How much spirit a player regains per second. Default is 10, was 1 in earlier versions.");
      this.summonFocusRecharge = LycanitesMobs.config.getInt("Player", "Summoning Focus Recharge", this.summonFocusRecharge, "How much summoning focus a player regains per second. Default is 10, was 1 in earlier versions.");
      this.creatureStudyCooldownMax = CreatureManager.getInstance().config.creatureStudyCooldown;
   }

   public void setPlayer(EntityPlayer player) {
      this.player = player;
      this.petManager.host = player;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public boolean canMeleeBigEntity(Entity targetEntity) {
      if (!(targetEntity instanceof EntityLivingBase)) {
         return false;
      } else {
         float targetWidth = targetEntity.width;
         float targetHeight = targetEntity.height;
         if (targetEntity instanceof BaseCreatureEntity) {
            BaseCreatureEntity targetCreature = (BaseCreatureEntity)targetEntity;
            targetWidth *= targetCreature.hitAreaWidthScale;
            targetHeight *= targetCreature.hitAreaHeightScale;
         }

         if (targetWidth <= 4.0F && targetHeight <= 4.0F) {
            return false;
         } else {
            double heightOffset = this.player.posY - targetEntity.posY;
            double heightCompensation = 0.0D;
            if (heightOffset > 0.0D) {
               heightCompensation = Math.min(heightOffset, (double)targetHeight);
            }

            double distance = Math.sqrt((double)this.player.getDistance(targetEntity));
            double range = 6.0D + heightCompensation + (double)(targetWidth / 2.0F);
            return distance <= range;
         }
      }
   }

   public void meleeAttack(Entity targetEntity) {
      if (!this.hasAttacked && !this.player.getHeldItemMainhand().isEmpty() && this.canMeleeBigEntity(targetEntity)) {
         this.player.attackTargetEntityWithCurrentItem(targetEntity);
         this.player.resetCooldown();
         this.player.swingArm(EnumHand.MAIN_HAND);
      }

   }

   public void onUpdate() {
      ++this.timePlayed;
      this.hasAttacked = false;
      boolean creative = this.player.capabilities.isCreativeMode;
      boolean sync = false;
      if (this.justBrokenClearTime > 0 && --this.justBrokenClearTime <= 0) {
         this.justBrokenBlock = null;
      }

      this.spirit = Math.min(Math.max(this.spirit, 0), this.spiritMax - this.spiritReserved);
      if (this.spirit < this.spiritMax - this.spiritReserved) {
         this.spirit += this.spiritRecharge;
         if (!this.player.getEntityWorld().isRemote && this.currentTick % 20L == 0L || this.spirit == this.spiritMax - this.spiritReserved) {
            sync = true;
         }
      }

      this.summonFocus = Math.min(Math.max(this.summonFocus, 0), this.summonFocusMax);
      if (this.summonFocus < this.summonFocusMax) {
         this.summonFocus += this.summonFocusRecharge;
         if (!this.player.getEntityWorld().isRemote && !creative && this.currentTick % 20L == 0L || this.summonFocus < this.summonFocusMax || !this.player.getHeldItemMainhand().isEmpty() && this.player.getHeldItemMainhand().getItem() instanceof ItemStaffSummoning || !this.player.getHeldItemOffhand().isEmpty() && this.player.getHeldItemOffhand().getItem() instanceof ItemStaffSummoning) {
            sync = true;
         }
      }

      if (this.creatureStudyCooldown > 0) {
         --this.creatureStudyCooldown;
         sync = true;
      }

      if (!this.player.getEntityWorld().isRemote && sync) {
         MessagePlayerStats message = new MessagePlayerStats(this);
         LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
      }

      if (!this.initialSetup) {
         if (!this.player.getEntityWorld().isRemote) {
            this.loadFamiliars();
         }

         if (this.player.getEntityWorld().isRemote) {
            VersionChecker.VersionInfo latestVersion = VersionChecker.INSTANCE.getLatestVersion();
            if (latestVersion != null && latestVersion.isNewer && VersionChecker.INSTANCE.enabled) {
               this.player.sendMessage(new TextComponentString(LanguageManager.translate("lyc.version.newer").replace("{current}", "2.0.8.9").replace("{latest}", latestVersion.versionNumber)));
            }
         }

         this.initialSetup = true;
      }

      if (!this.player.getEntityWorld().isRemote && this.needsFullSync) {
         this.beastiary.sendAllToClient();
         this.sendAllSummonSetsToPlayer();
         MessageSummonSetSelection message = new MessageSummonSetSelection(this);
         LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
         this.sendPetEntriesToPlayer("");
      }

      this.petManager.onUpdate(this.player.getEntityWorld());
      ++this.currentTick;
      this.needsFullSync = false;
      if (!this.isControlActive(ExtendedPlayer.CONTROL_ID.RIGHT_MOUSE) && (this.player.getHeldItemMainhand().getItem() instanceof ItemEquipment || this.player.getHeldItemOffhand().getItem() instanceof ItemEquipment)) {
         this.player.stopActiveHand();
      }

   }

   public void setJustBrokenBlock(IBlockState blockState) {
      this.justBrokenBlock = blockState;
      this.justBrokenClearTime = 60;
   }

   public SummonSet getSummonSet(int setID) {
      if (setID <= 0) {
         LycanitesMobs.logWarning("", "Attempted to access set " + setID + " but the minimum ID is 1. Player: " + this.player);
         return this.getSummonSet(1);
      } else if (setID > this.summonSetMax) {
         LycanitesMobs.logWarning("", "Attempted to access set " + setID + " but the maximum set ID is " + this.summonSetMax + ". Player: " + this.player);
         return this.getSummonSet(this.summonSetMax);
      } else {
         if (!this.summonSets.containsKey(Integer.valueOf(setID))) {
            this.summonSets.put(Integer.valueOf(setID), new SummonSet(this));
         }

         return (SummonSet)this.summonSets.get(Integer.valueOf(setID));
      }
   }

   public SummonSet getSelectedSummonSet() {
      return this.getSummonSet(this.selectedSummonSet);
   }

   public void setSelectedSummonSet(int targetSetID) {
      this.selectedSummonSet = targetSetID;
   }

   public int validateSummonSetID(int targetSetID) {
      for(targetSetID = Math.max(Math.min(targetSetID, this.summonSetMax), 1); !this.getSummonSet(targetSetID).isUseable() && targetSetID > 1 && !"".equals(this.getSummonSet(targetSetID).summonType); --targetSetID) {
         ;
      }

      return targetSetID;
   }

   public Beastiary getBeastiary() {
      return this.beastiary;
   }

   public void loadFamiliars() {
      Map<EaglercraftUUID, PetEntry> playerFamiliars = PlayerFamiliars.INSTANCE.getFamiliarsForPlayer(this.player);
      if (!playerFamiliars.isEmpty()) {
         for(PetEntry petEntry : playerFamiliars.values()) {
            if (this.petManager.hasEntry(petEntry)) {
               PetEntry currentFamiliarEntry = this.petManager.getEntry(petEntry.petEntryID);
               currentFamiliarEntry.copy(petEntry);
            } else {
               this.petManager.addEntry(petEntry);
               petEntry.entity = null;
            }
         }

         this.sendPetEntriesToPlayer("familiar");
      }

   }

   public boolean studyCreature(Entity entity, int experience, boolean useCooldown, boolean alwaysShowMessage) {
      if (!(entity instanceof BaseCreatureEntity)) {
         if (useCooldown && !this.player.getEntityWorld().isRemote) {
            this.player.sendStatusMessage(new TextComponentString(LanguageManager.translate("message.beastiary.unknown")), true);
         }

         return false;
      } else if (useCooldown && this.creatureStudyCooldown > 0) {
         if (!this.player.getEntityWorld().isRemote) {
            this.player.sendStatusMessage(new TextComponentString(LanguageManager.translate("message.beastiary.study.recharging")), true);
         }

         return false;
      } else {
         BaseCreatureEntity creature = (BaseCreatureEntity)entity;
         if (creature.isTamed()) {
            return false;
         } else {
            experience = creature.scaleKnowledgeExperience(experience);
            CreatureKnowledge newKnowledge = this.beastiary.addCreatureKnowledge(creature, experience);
            if (newKnowledge != null) {
               if (useCooldown) {
                  this.creatureStudyCooldown = this.creatureStudyCooldownMax;
               }

               if (!this.player.getEntityWorld().isRemote) {
                  if (newKnowledge.getMaxExperience() == 0) {
                     this.player.sendStatusMessage(new TextComponentString(LanguageManager.translate("message.beastiary.study.full") + " " + creature.creatureInfo.getTitle()), true);
                  } else if (experience > 0) {
                     boolean showMessage = alwaysShowMessage;
                     if (!alwaysShowMessage) {
                        float messageThreshold = (float)newKnowledge.getMaxExperience() * 0.25F;
                        int fromExperience = Math.max(0, newKnowledge.experience - experience);
                        int wrappedExperience = fromExperience % (int)messageThreshold;
                        showMessage = (float)(wrappedExperience + experience) >= messageThreshold;
                     }

                     if (showMessage) {
                        this.player.sendStatusMessage(new TextComponentString(" " + LanguageManager.translate("message.beastiary.study") + newKnowledge.getCreatureInfo().getTitle() + " " + newKnowledge.experience + "/" + newKnowledge.getMaxExperience() + " (+" + experience + ")"), true);
                     }
                  }
               }

               return true;
            } else {
               if (useCooldown && !this.player.getEntityWorld().isRemote) {
                  this.player.sendStatusMessage(new TextComponentString(LanguageManager.translate("message.beastiary.study.full") + " " + creature.creatureInfo.getTitle()), true);
               }

               return false;
            }
         }
      }
   }

   public void onDeath() {
   }

   public void sendPetEntriesToPlayer(String entryType) {
      if (!this.player.getEntityWorld().isRemote) {
         LycanitesMobs.logDebug("Packets", "Sending all pet entries to client.");

         for(PetEntry petEntry : this.petManager.entries.values()) {
            if (entryType.equals(petEntry.getType()) || "".equals(entryType)) {
               MessagePetEntry message = new MessagePetEntry(this, petEntry);
               LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
            }
         }

      }
   }

   public void sendPetEntryToPlayer(PetEntry petEntry) {
      if (!this.player.getEntityWorld().isRemote) {
         MessagePetEntry message = new MessagePetEntry(this, petEntry);
         LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
      }
   }

   public void sendPetEntryRemoveToPlayer(PetEntry petEntry) {
      if (!this.player.getEntityWorld().isRemote) {
         MessagePetEntryRemove message = new MessagePetEntryRemove(this, petEntry);
         LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
      }
   }

   public void sendPetEntryToServer(PetEntry petEntry) {
      if (this.player.getEntityWorld().isRemote) {
         MessagePetEntry message = new MessagePetEntry(this, petEntry);
         LycanitesMobs.packetHandler.sendToServer(message);
      }
   }

   public void sendPetEntryRemoveRequest(PetEntry petEntry) {
      if (this.player.getEntityWorld().isRemote) {
         petEntry.remove();
         MessagePetEntryRemove message = new MessagePetEntryRemove(this, petEntry);
         LycanitesMobs.packetHandler.sendToServer(message);
      }
   }

   public void sendAllSummonSetsToPlayer() {
      if (!this.player.getEntityWorld().isRemote) {
         LycanitesMobs.logDebug("Packets", "Sending all summoning sets to client.");

         for(byte setID = 1; setID <= this.summonSetMax; ++setID) {
            MessageSummonSet message = new MessageSummonSet(this, setID);
            LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
         }

      }
   }

   public void sendSummonSetToServer(byte setID) {
      if (this.player.getEntityWorld().isRemote) {
         MessageSummonSet message = new MessageSummonSet(this, setID);
         LycanitesMobs.packetHandler.sendToServer(message);
      }
   }

   public void updateControlStates(byte controlStates) {
      this.controlStates = controlStates;
   }

   public boolean isControlActive(ExtendedPlayer.CONTROL_ID controlID) {
      return (this.controlStates & controlID.id) > 0;
   }

   public void requestGUI(byte guiID) {
      if (guiID == GuiHandler.Beastiary.PETS.id) {
         this.sendPetEntriesToPlayer("pet");
      }

      if (guiID == GuiHandler.Beastiary.PETS.id) {
         this.sendPetEntriesToPlayer("mount");
      }

      if (guiID == GuiHandler.Beastiary.PETS.id) {
         this.sendPetEntriesToPlayer("familiar");
      }

   }

   public void readNBT(NBTTagCompound nbtTagCompound) {
      NBTTagCompound extTagCompound = nbtTagCompound.getCompoundTag("LycanitesMobsPlayer");
      this.beastiary.readFromNBT(extTagCompound);
      this.petManager.readFromNBT(extTagCompound);
      if (extTagCompound.hasKey("SummonFocus")) {
         this.summonFocus = extTagCompound.getInteger("SummonFocus");
      }

      if (extTagCompound.hasKey("Spirit")) {
         this.spirit = extTagCompound.getInteger("Spirit");
      }

      if (extTagCompound.hasKey("CreatureStudyCooldown")) {
         this.creatureStudyCooldown = extTagCompound.getInteger("CreatureStudyCooldown");
      }

      if (extTagCompound.hasKey("SelectedSummonSet")) {
         this.selectedSummonSet = extTagCompound.getInteger("SelectedSummonSet");
      }

      if (extTagCompound.hasKey("SummonSets")) {
         NBTTagList nbtSummonSets = extTagCompound.getTagList("SummonSets", 10);

         for(int setID = 0; setID < this.summonSetMax; ++setID) {
            NBTTagCompound nbtSummonSet = nbtSummonSets.getCompoundTagAt(setID);
            SummonSet summonSet = new SummonSet(this);
            summonSet.readFromNBT(nbtSummonSet);
            this.summonSets.put(Integer.valueOf(setID + 1), summonSet);
         }
      }

      if (extTagCompound.hasKey("TimePlayed")) {
         this.timePlayed = extTagCompound.getLong("TimePlayed");
      }

   }

   public void writeNBT(NBTTagCompound nbtTagCompound) {
      NBTTagCompound extTagCompound = new NBTTagCompound();
      this.beastiary.writeToNBT(extTagCompound);
      this.petManager.writeToNBT(extTagCompound);
      extTagCompound.setInteger("SummonFocus", this.summonFocus);
      extTagCompound.setInteger("Spirit", this.spirit);
      extTagCompound.setInteger("CreatureStudyCooldown", this.creatureStudyCooldown);
      extTagCompound.setInteger("SelectedSummonSet", this.selectedSummonSet);
      extTagCompound.setLong("TimePlayed", this.timePlayed);
      NBTTagList nbtSummonSets = new NBTTagList();

      for(int setID = 0; setID < this.summonSetMax; ++setID) {
         NBTTagCompound nbtSummonSet = new NBTTagCompound();
         SummonSet summonSet = this.getSummonSet(setID + 1);
         summonSet.writeToNBT(nbtSummonSet);
         nbtSummonSets.appendTag(nbtSummonSet);
      }

      extTagCompound.setTag("SummonSets", nbtSummonSets);
      nbtTagCompound.setTag("LycanitesMobsPlayer", extTagCompound);
   }

   public static enum CONTROL_ID {
      JUMP((byte)1),
      MOUNT_DISMOUNT((byte)2),
      MOUNT_ABILITY((byte)4),
      MOUNT_INVENTORY((byte)8),
      ATTACK((byte)16),
      DESCEND((byte)32),
      RIGHT_MOUSE((byte)64);

      public byte id;

      private CONTROL_ID(byte i) {
         this.id = i;
      }
   }
}
