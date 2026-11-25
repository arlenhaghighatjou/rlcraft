package com.fantasticsource.mctools.nbtcap;

import com.fantasticsource.fantasticlib.api.FLibAPI;
import com.fantasticsource.fantasticlib.api.INBTCap;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NBTCap implements INBTCap {
   protected LinkedHashMap<String, NBTTagCompound> compounds = new LinkedHashMap();

   public NBTCap() {
   }

   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
      EntityPlayer player = event.getEntityPlayer();
      INBTCap nbtCap = (INBTCap)player.getCapability(FLibAPI.NBT_CAP, (EnumFacing)null);
      INBTCap oldNBTCap = (INBTCap)event.getOriginal().getCapability(FLibAPI.NBT_CAP, (EnumFacing)null);
      if (nbtCap != null && oldNBTCap != null) {
         for(String modid : oldNBTCap.getRegisteredModIDs()) {
            nbtCap.setCompound(modid, oldNBTCap.getCompound(modid));
         }

      }
   }

   @SubscribeEvent
   public static void attachToEntity(AttachCapabilitiesEvent<Entity> event) {
      Entity entity = (Entity)event.getObject();

      for(Predicate<Entity> predicate : entityPredicates.values()) {
         if (predicate.test(entity)) {
            event.addCapability(new ResourceLocation("fantasticlib", "nbtcap"), new NBTCapProvider());
            return;
         }
      }

   }

   @SubscribeEvent
   public static void attachToStack(AttachCapabilitiesEvent<ItemStack> event) {
      ItemStack stack = (ItemStack)event.getObject();

      for(Predicate<ItemStack> predicate : stackPredicates.values()) {
         if (predicate.test(stack)) {
            event.addCapability(new ResourceLocation("fantasticlib", "nbtcap"), new NBTCapProvider());
            return;
         }
      }

   }

   @SubscribeEvent
   public static void attachToWorld(AttachCapabilitiesEvent<World> event) {
      World world = (World)event.getObject();

      for(Predicate<World> predicate : worldPredicates.values()) {
         if (predicate.test(world)) {
            event.addCapability(new ResourceLocation("fantasticlib", "nbtcap"), new NBTCapProvider());
            return;
         }
      }

   }

   @SubscribeEvent
   public static void attachToTE(AttachCapabilitiesEvent<TileEntity> event) {
      TileEntity te = (TileEntity)event.getObject();

      for(Predicate<TileEntity> predicate : tePredicates.values()) {
         if (predicate.test(te)) {
            event.addCapability(new ResourceLocation("fantasticlib", "nbtcap"), new NBTCapProvider());
            return;
         }
      }

   }

   @SubscribeEvent
   public static void attachToChunk(AttachCapabilitiesEvent<Chunk> event) {
      Chunk chunk = (Chunk)event.getObject();

      for(Predicate<Chunk> predicate : chunkPredicates.values()) {
         if (predicate.test(chunk)) {
            event.addCapability(new ResourceLocation("fantasticlib", "nbtcap"), new NBTCapProvider());
            return;
         }
      }

   }

   @SubscribeEvent
   public static void attachToVillage(AttachCapabilitiesEvent<Village> event) {
      Village village = (Village)event.getObject();

      for(Predicate<Village> predicate : villagePredicates.values()) {
         if (predicate.test(village)) {
            event.addCapability(new ResourceLocation("fantasticlib", "nbtcap"), new NBTCapProvider());
            return;
         }
      }

   }

   public String[] getRegisteredModIDs() {
      return (String[])registeredModIDs.toArray(new String[0]);
   }

   public NBTTagCompound getCompound(String modid) {
      return (NBTTagCompound)this.compounds.computeIfAbsent(modid, (o) -> {
         return new NBTTagCompound();
      });
   }

   public void setCompound(String modid, NBTTagCompound compound) {
      this.compounds.put(modid, compound);
   }
}
