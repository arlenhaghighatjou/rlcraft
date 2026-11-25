package com.fantasticsource.fantasticlib.api;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class FLibAPI {
   @CapabilityInject(INBTCap.class)
   public static final Capability<INBTCap> NBT_CAP = null;

   public FLibAPI() {
   }

   public static void attachNBTCapToEntityIf(String modid, Predicate<Entity> predicate) {
      INBTCap.registeredModIDs.add(modid);
      INBTCap.entityPredicates.put(modid, predicate);
   }

   public static void attachNBTCapToStackIf(String modid, Predicate<ItemStack> predicate) {
      INBTCap.registeredModIDs.add(modid);
      INBTCap.stackPredicates.put(modid, predicate);
   }

   public static void attachNBTCapToWorldIf(String modid, Predicate<World> predicate) {
      INBTCap.registeredModIDs.add(modid);
      INBTCap.worldPredicates.put(modid, predicate);
   }

   public static void attachNBTCapToTEIf(String modid, Predicate<TileEntity> predicate) {
      INBTCap.registeredModIDs.add(modid);
      INBTCap.tePredicates.put(modid, predicate);
   }

   public static void attachNBTCapToChunkIf(String modid, Predicate<Chunk> predicate) {
      INBTCap.registeredModIDs.add(modid);
      INBTCap.chunkPredicates.put(modid, predicate);
   }

   public static void attachNBTCapToVillageIf(String modid, Predicate<Village> predicate) {
      INBTCap.registeredModIDs.add(modid);
      INBTCap.villagePredicates.put(modid, predicate);
   }

   public static INBTCap getNBTCap(Entity entity) {
      return (INBTCap)entity.getCapability(NBT_CAP, (EnumFacing)null);
   }

   public static INBTCap getNBTCap(ItemStack stack) {
      return (INBTCap)stack.getCapability(NBT_CAP, (EnumFacing)null);
   }

   public static INBTCap getNBTCap(World world) {
      return (INBTCap)world.getCapability(NBT_CAP, (EnumFacing)null);
   }

   public static INBTCap getNBTCap(TileEntity te) {
      return (INBTCap)te.getCapability(NBT_CAP, (EnumFacing)null);
   }

   public static INBTCap getNBTCap(Chunk chunk) {
      return (INBTCap)chunk.getCapability(NBT_CAP, (EnumFacing)null);
   }

   public static INBTCap getNBTCap(Village village) {
      return (INBTCap)village.getCapability(NBT_CAP, (EnumFacing)null);
   }
}
