package net.theawesomegem.fishingmadebetter;

import net.lax1dude.eaglercraft.v1_8.HString;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.common.item.ItemFishBucket;
import net.theawesomegem.fishingmadebetter.util.ItemStackUtil;
import net.theawesomegem.fishingmadebetter.util.TimeUtil;

public class BetterFishUtil {
   public BetterFishUtil() {
   }

   @Nullable
   public static String getFishId(ItemStack itemStack) {
      return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("FishId") ? itemStack.getTagCompound().getString("FishId") : null;
   }

   public static int getFishWeight(ItemStack itemStack) {
      return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("FishWeight") ? itemStack.getTagCompound().getInteger("FishWeight") : 1;
   }

   public static long getFishCaughtTime(ItemStack itemStack) {
      return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("FishCaughtTime") ? itemStack.getTagCompound().getLong("FishCaughtTime") : 0L;
   }

   public static void setFishCaughtTime(ItemStack itemStack, int time) {
      NBTTagCompound tagCompound = itemStack.getTagCompound();
      if (tagCompound == null) {
         tagCompound = new NBTTagCompound();
      }

      tagCompound.setLong("FishCaughtTime", (long)time);
      itemStack.setTagCompound(tagCompound);
   }

   public static boolean doesFishHasScale(ItemStack itemStack) {
      return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("FishScale") ? itemStack.getTagCompound().getBoolean("FishScale") : false;
   }

   public static void setFishHasScale(ItemStack itemStack, boolean value) {
      NBTTagCompound tagCompound = itemStack.getTagCompound();
      if (tagCompound == null) {
         tagCompound = new NBTTagCompound();
      }

      tagCompound.setBoolean("FishScale", value);
      itemStack.setTagCompound(tagCompound);
      if (!value) {
         List<String> tooltipList = new ArrayList();
         tooltipList.add(HString.format("Weight: %d", getFishWeight(itemStack)));
         tooltipList.add(HString.format("Scale: %s", "Detached"));
         tooltipList.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Dead" + TextFormatting.RESET);
         itemStack = ItemStackUtil.appendToolTip(itemStack, tooltipList);
         setFishCaughtTime(itemStack, 0);
      }

   }

   public static boolean isBetterFish(ItemStack itemStack) {
      String fishId = getFishId(itemStack);
      if (itemStack.getItem() instanceof ItemFishBucket) {
         return false;
      } else {
         return fishId != null && fishId.length() > 0;
      }
   }

   public static boolean isValidBait(ItemStack itemStack) {
      String baitString = ((ResourceLocation)Item.REGISTRY.getNameForObject(itemStack.getItem())).toString();
      if (baitString == null) {
         return false;
      } else {
         Integer baitMeta = itemStack.getMetadata();
         return CustomConfigurationHandler.possibleBaitMap.containsKey(baitString) && ((ArrayList)CustomConfigurationHandler.possibleBaitMap.get(baitString)).contains(baitMeta);
      }
   }

   public static boolean isDead(ItemStack itemStack, long time) {
      String fishId = getFishId(itemStack);
      if (fishId == null) {
         return true;
      } else {
         FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(fishId);
         if (fishData == null) {
            return true;
         } else {
            long fishCaughtTime = getFishCaughtTime(itemStack);
            if (fishCaughtTime == 0L) {
               return true;
            } else if (fishData.allowScaling && !doesFishHasScale(itemStack)) {
               return true;
            } else {
               return time >= fishCaughtTime + TimeUtil.secondsToMinecraftTicks(fishData.timeOutsideOfWater);
            }
         }
      }
   }
}
