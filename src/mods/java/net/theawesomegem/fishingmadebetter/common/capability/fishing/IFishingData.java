package net.theawesomegem.fishingmadebetter.common.capability.fishing;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.theawesomegem.fishingmadebetter.common.data.FishCaughtData;
import net.theawesomegem.fishingmadebetter.common.networking.packet.PacketKeybindS;

public interface IFishingData {
   void setFishTime(int var1);

   int getFishTime();

   void setFishing(boolean var1);

   boolean isFishing();

   void setReelAmount(int var1);

   int getReelAmount();

   void setReelTarget(int var1);

   int getReelTarget();

   void setErrorVariance(int var1);

   int getErrorVariance();

   void setTimeBeforeFishSwimToHook(int var1);

   int getTimeBeforeFishSwimToHook();

   void setFishPopulation(FishPopulation var1);

   FishPopulation getFishPopulation();

   void setLastFailedFishing(int var1);

   int getLastFailedFishing();

   void setLastFishTime(int var1);

   int getLastFishTime();

   void setFishDistance(int var1);

   int getFishDistance();

   void setFishDistanceTime(int var1);

   int getFishDistanceTime();

   void setFishDeepLevel(int var1);

   int getFishDeepLevel();

   void setFishWeight(int var1);

   int getFishWeight();

   void setFishCaughtData(FishCaughtData var1);

   FishCaughtData getFishCaughtData();

   void setUsingVanillaRod(boolean var1);

   boolean getUsingVanillaRod();

   void setTimeSinceTracking(long var1);

   long getTimeSinceTracking();

   int getFishMomentum();

   void setFishMomentum(int var1);

   int getFishTugging();

   void setFishTugging(int var1);

   int getTensionMomentum();

   void setTensionMomentum(int var1);

   PacketKeybindS.Keybind getKeybind();

   void setKeybind(PacketKeybindS.Keybind var1);

   Integer[] getMinigameBackground();

   void setMinigameBackground(int var1, int var2, int var3, int var4, int var5);

   void setMinigameBackground(Integer[] var1);

   int getLineBreak();

   void setLineBreak(int var1);

   void startFishing(Random var1, ItemStack var2, Integer[] var3);

   void reset(boolean var1);

   void reset();
}
