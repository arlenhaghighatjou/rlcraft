package net.blay09.mods.waystones;

import javax.annotation.Nullable;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

public class PlayerWaystoneHelper {
   public static final String WAYSTONES = "Waystones";
   public static final String WAYSTONE_LIST = "WaystoneList";
   public static final String LAST_FREE_WARP = "LastFreeWarp";
   public static final String LAST_WARP_STONE_USE = "LastWarpStoneUse";

   public PlayerWaystoneHelper() {
   }

   public static NBTTagCompound getWaystonesTag(EntityPlayer player) {
      return player.getEntityData().getCompoundTag("PlayerPersisted").getCompoundTag("Waystones");
   }

   public static NBTTagCompound getOrCreateWaystonesTag(EntityPlayer player) {
      NBTTagCompound persistedTag = player.getEntityData().getCompoundTag("PlayerPersisted");
      NBTTagCompound waystonesTag = persistedTag.getCompoundTag("Waystones");
      persistedTag.setTag("Waystones", waystonesTag);
      player.getEntityData().setTag("PlayerPersisted", persistedTag);
      return waystonesTag;
   }

   public static void store(EntityPlayer player, WaystoneEntry[] entries, long lastFreeWarp, long lastWarpStoneUse) {
      NBTTagCompound tagCompound = getOrCreateWaystonesTag(player);
      NBTTagList tagList = new NBTTagList();

      for(WaystoneEntry entry : entries) {
         tagList.appendTag(entry.writeToNBT());
      }

      tagCompound.setTag("WaystoneList", tagList);
      tagCompound.setLong("LastFreeWarp", lastFreeWarp);
      tagCompound.setLong("LastWarpStoneUse", lastWarpStoneUse);
   }

   public static boolean canFreeWarp(EntityPlayer player) {
      return System.currentTimeMillis() - getLastFreeWarp(player) > (long)(WaystoneConfig.general.teleportButtonCooldown * 1000);
   }

   public static boolean canUseWarpStone(EntityPlayer player) {
      return System.currentTimeMillis() - getLastWarpStoneUse(player) > (long)(WaystoneConfig.general.warpStoneCooldown * 1000);
   }

   public static void setLastFreeWarp(EntityPlayer player, long lastFreeWarp) {
      getOrCreateWaystonesTag(player).setLong("LastFreeWarp", lastFreeWarp);
   }

   public static long getLastFreeWarp(EntityPlayer player) {
      return getWaystonesTag(player).getLong("LastFreeWarp");
   }

   public static void setLastWarpStoneUse(EntityPlayer player, long lastWarpStone) {
      getOrCreateWaystonesTag(player).setLong("LastWarpStoneUse", lastWarpStone);
   }

   public static long getLastWarpStoneUse(EntityPlayer player) {
      return getWaystonesTag(player).getLong("LastWarpStoneUse");
   }

   @Nullable
   public static WaystoneEntry getLastWaystone(EntityPlayer player) {
      NBTTagCompound tagCompound = getWaystonesTag(player);
      NBTTagList tagList = tagCompound.getTagList("WaystoneList", 10);
      return tagList.tagCount() > 0 ? WaystoneEntry.read(tagList.getCompoundTagAt(tagList.tagCount() - 1)) : null;
   }

   public static int getTravelCostByDistance(EntityPlayer player, WaystoneEntry waystone) {
      if (player.world.provider.getDimension() != waystone.getDimensionId()) {
         return WaystoneConfig.general.dimensionalXpCost;
      } else if (WaystoneConfig.general.blocksPerXPLevel <= 0) {
         return 0;
      } else {
         int dist = (int)Math.sqrt(player.getDistanceSqToCenter(waystone.getPos()));
         int zeroBasedCost = MathHelper.clamp(dist / WaystoneConfig.general.blocksPerXPLevel, 0, WaystoneConfig.general.maximumXpCost);
         return !WaystoneConfig.general.shortTravelFree ? zeroBasedCost + 1 : zeroBasedCost;
      }
   }
}
