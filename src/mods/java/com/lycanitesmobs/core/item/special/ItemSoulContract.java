package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.pets.PetEntry;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemSoulContract extends ItemBase {
   public ItemSoulContract() {
      this.setMaxStackSize(1);
      this.itemName = "soul_contract";
      this.setup();
   }

   public EaglercraftUUID getOwnerUUID(ItemStack itemStack) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      EaglercraftUUID uuid = null;
      if (nbt.hasUniqueId("ownerUUID")) {
         uuid = nbt.getUniqueId("ownerUUID");
      }

      return uuid;
   }

   public EaglercraftUUID getPetEntryUUID(ItemStack itemStack) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      EaglercraftUUID uuid = null;
      if (nbt.hasUniqueId("petEntryUUID")) {
         uuid = nbt.getUniqueId("petEntryUUID");
      }

      return uuid;
   }

   public void addInformation(ItemStack itemStack, World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
      super.addInformation(itemStack, world, tooltip, tooltipFlag);
      EaglercraftUUID ownerUUID = this.getOwnerUUID(itemStack);
      EaglercraftUUID petEntryUUID = this.getPetEntryUUID(itemStack);
      if (ownerUUID != null && petEntryUUID != null) {
         EntityPlayer owner = world.getPlayerEntityByUUID(ownerUUID);
         ExtendedPlayer extendedOwner = ExtendedPlayer.getForPlayer(owner);
         if (owner == null || extendedOwner == null) {
            tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.offline"));
            return;
         }

         PetEntry petEntry = extendedOwner.petManager.getEntry(petEntryUUID);
         if (petEntry == null) {
            tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.released"));
            tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.owner") + " " + owner.getDisplayName());
            return;
         }

         tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.bound") + " " + petEntry.getDisplayName());
         tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.owner") + " " + owner.getDisplayName().getFormattedText());
      }

   }

   public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
      if (entity instanceof TameableCreatureEntity && ((TameableCreatureEntity)entity).isBoundPet()) {
         TameableCreatureEntity tameableCreatureEntity = (TameableCreatureEntity)entity;
         EaglercraftUUID ownerUUID = this.getOwnerUUID(itemStack);
         EaglercraftUUID petEntryUUID = this.getPetEntryUUID(itemStack);
         return ownerUUID != null && petEntryUUID != null ? this.transferSoul(itemStack, player, tameableCreatureEntity, ownerUUID, petEntryUUID) : this.bindSoul(itemStack, player, tameableCreatureEntity);
      } else {
         if (!player.getEntityWorld().isRemote) {
            player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.invalid")));
         }

         return false;
      }
   }

   public boolean bindSoul(ItemStack itemStack, EntityPlayer player, TameableCreatureEntity tameableCreatureEntity) {
      if (tameableCreatureEntity.getPlayerOwner() == player && !"familiar".equalsIgnoreCase(tameableCreatureEntity.getPetEntry().getType())) {
         if (!player.getEntityWorld().isRemote) {
            NBTTagCompound nbt = this.getTagCompound(itemStack);
            nbt.setUniqueId("ownerUUID", player.getUniqueID());
            nbt.setUniqueId("petEntryUUID", tameableCreatureEntity.getPetEntry().petEntryID);
            itemStack.setTagCompound(nbt);
            player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.bound")));
         }

         return true;
      } else {
         if (!player.getEntityWorld().isRemote) {
            player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.not_owner")));
         }

         return false;
      }
   }

   public boolean transferSoul(ItemStack itemStack, EntityPlayer player, TameableCreatureEntity tameableCreatureEntity, EaglercraftUUID ownerUUID, EaglercraftUUID petEntryUUID) {
      if (player.getUniqueID().equals(ownerUUID)) {
         if (petEntryUUID.equals(tameableCreatureEntity.getPetEntry().petEntryID)) {
            if (!player.getEntityWorld().isRemote) {
               NBTTagCompound nbt = this.getTagCompound(itemStack);
               nbt.removeTag("ownerUUIDMost");
               nbt.removeTag("ownerUUIDLeast");
               nbt.removeTag("petEntryUUIDMost");
               nbt.removeTag("petEntryUUIDLeast");
               itemStack.setTagCompound(nbt);
               player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.unbound")));
            }

            return true;
         } else {
            return this.bindSoul(itemStack, player, tameableCreatureEntity);
         }
      } else if (!petEntryUUID.equals(tameableCreatureEntity.getPetEntry().petEntryID)) {
         if (!player.getEntityWorld().isRemote) {
            player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.wrong_target")));
         }

         return false;
      } else {
         EntityPlayer owner = player.getEntityWorld().getPlayerEntityByUUID(ownerUUID);
         ExtendedPlayer extendedOwner = ExtendedPlayer.getForPlayer(owner);
         if (owner != null && extendedOwner != null) {
            ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
            if (extendedPlayer == null) {
               return false;
            } else {
               if (!player.getEntityWorld().isRemote) {
                  extendedOwner.petManager.removeEntry(tameableCreatureEntity.getPetEntry());
                  extendedPlayer.petManager.addEntry(tameableCreatureEntity.getPetEntry());
                  extendedOwner.sendPetEntriesToPlayer(tameableCreatureEntity.getPetEntry().getType());
                  extendedOwner.sendPetEntryRemoveToPlayer(tameableCreatureEntity.getPetEntry());
                  extendedPlayer.sendPetEntriesToPlayer(tameableCreatureEntity.getPetEntry().getType());
                  owner.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.transferred")));
                  player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.transferred")));
               }

               tameableCreatureEntity.setPlayerOwner(player);
               itemStack.shrink(1);
               return true;
            }
         } else {
            if (!player.getEntityWorld().isRemote) {
               player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.owner_missing")));
            }

            return false;
         }
      }
   }

   public boolean hasEffect(ItemStack itemStack) {
      return this.getOwnerUUID(itemStack) != null && this.getPetEntryUUID(itemStack) != null;
   }
}
