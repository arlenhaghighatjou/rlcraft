package net.theawesomegem.fishingmadebetter.common.capability.fishing;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishCaughtData;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.common.item.fishingrod.ItemBetterFishingRod;
import net.theawesomegem.fishingmadebetter.common.networking.packet.PacketKeybindS;
import net.theawesomegem.fishingmadebetter.util.RandomUtil;

public class FishingData implements IFishingData {
   private int fishTime;
   private boolean fishing;
   private int reelAmount;
   private int reelTarget;
   private int errorVariance;
   private int timeBeforeFishSwimToHook;
   private int fishDistance;
   private int fishDeepLevel;
   private int fishDistanceTime;
   private FishCaughtData fishCaughtData;
   private FishPopulation fishPopulation;
   private boolean usingVanillaRod = false;
   private int fishMomentum;
   private int fishTugging;
   private int tensionMomentum;
   private PacketKeybindS.Keybind keybind;
   private Integer[] minigameBackground = new Integer[5];
   private int lineBreak;
   private int lastFailedFishing;
   private int lastFishTime;
   private int fishWeight;
   private long timeSinceTracking;

   public FishingData() {
      this.reset();
   }

   public void setFishTime(int value) {
      this.fishTime = value;
   }

   public int getFishTime() {
      return this.fishTime;
   }

   public void setFishing(boolean value) {
      this.fishing = value;
   }

   public boolean isFishing() {
      return this.fishing;
   }

   public void setReelAmount(int value) {
      value = Math.min(1100 - this.errorVariance, Math.max(this.errorVariance, value));
      this.reelAmount = value;
   }

   public int getReelAmount() {
      return this.reelAmount;
   }

   public void setReelTarget(int value) {
      value = Math.min(1070, Math.max(30, value));
      this.reelTarget = value;
   }

   public int getReelTarget() {
      return this.reelTarget;
   }

   public void setErrorVariance(int value) {
      if (value < 0) {
         value = 0;
      }

      this.errorVariance = value;
   }

   public int getErrorVariance() {
      return this.errorVariance;
   }

   public void setTimeBeforeFishSwimToHook(int value) {
      this.timeBeforeFishSwimToHook = value;
   }

   public int getTimeBeforeFishSwimToHook() {
      return this.timeBeforeFishSwimToHook;
   }

   public void setFishPopulation(FishPopulation fishPopulation) {
      this.fishPopulation = fishPopulation;
   }

   public FishPopulation getFishPopulation() {
      return this.fishPopulation;
   }

   public void setLastFailedFishing(int value) {
      this.lastFailedFishing = value;
   }

   public int getLastFailedFishing() {
      return this.lastFailedFishing;
   }

   public void setLastFishTime(int value) {
      this.lastFishTime = value;
   }

   public int getLastFishTime() {
      return this.lastFishTime;
   }

   public void setFishDistance(int value) {
      this.fishDistance = Math.max(0, value);
   }

   public int getFishDistance() {
      return this.fishDistance;
   }

   public void setFishDistanceTime(int value) {
      this.fishDistanceTime = value;
   }

   public int getFishDistanceTime() {
      return this.fishDistanceTime;
   }

   public void setFishDeepLevel(int value) {
      this.fishDeepLevel = value;
   }

   public int getFishDeepLevel() {
      return this.fishDeepLevel;
   }

   public void setFishWeight(int value) {
      this.fishWeight = value;
   }

   public int getFishWeight() {
      return this.fishWeight;
   }

   public void setFishCaughtData(FishCaughtData fishCaughtData) {
      this.fishCaughtData = fishCaughtData;
   }

   public FishCaughtData getFishCaughtData() {
      return this.fishCaughtData;
   }

   public boolean getUsingVanillaRod() {
      return this.usingVanillaRod;
   }

   public void setUsingVanillaRod(boolean value) {
      this.usingVanillaRod = value;
   }

   public long getTimeSinceTracking() {
      return this.timeSinceTracking;
   }

   public void setTimeSinceTracking(long time) {
      this.timeSinceTracking = time;
   }

   public int getFishMomentum() {
      return this.fishMomentum;
   }

   public void setFishMomentum(int momentum) {
      this.fishMomentum = momentum;
   }

   public int getFishTugging() {
      return this.fishTugging;
   }

   public void setFishTugging(int tugging) {
      this.fishTugging = tugging;
   }

   public int getTensionMomentum() {
      return this.tensionMomentum;
   }

   public void setTensionMomentum(int momentum) {
      this.tensionMomentum = momentum;
   }

   public PacketKeybindS.Keybind getKeybind() {
      return this.keybind;
   }

   public void setKeybind(PacketKeybindS.Keybind keybind) {
      this.keybind = keybind;
   }

   public Integer[] getMinigameBackground() {
      return this.minigameBackground;
   }

   public void setMinigameBackground(int a, int b, int c, int d, int e) {
      this.minigameBackground[0] = a;
      this.minigameBackground[1] = b;
      this.minigameBackground[2] = c;
      this.minigameBackground[3] = d;
      this.minigameBackground[4] = e;
   }

   public int getLineBreak() {
      return this.lineBreak;
   }

   public void setLineBreak(int value) {
      this.lineBreak = Math.min(60, Math.max(0, value));
   }

   public void setMinigameBackground(Integer[] minigameBackground) {
      this.minigameBackground = minigameBackground;
   }

   public void startFishing(Random random, ItemStack fishingRod, Integer[] minigameBackground) {
      if (!this.isFishing()) {
         if (this.fishPopulation != null) {
            String fishId = this.fishPopulation.fishId;
            this.reset();
            FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(fishId);
            FishCaughtData fishCaughtData = FishCaughtData.fromFishData(fishData, random);
            this.setFishCaughtData(fishCaughtData);
            this.setFishTime(fishCaughtData.fishTime);
            this.setFishing(true);
            this.setErrorVariance((2 + fishCaughtData.errorVariance + ItemBetterFishingRod.getBobberItem(fishingRod).getVarianceModifier()) * 10);
            this.setReelAmount(RandomUtil.getRandomInRange(random, 15, 95) * 10);
            this.setReelTarget(RandomUtil.getRandomInRange(random, 30, 80) * 10);
            this.setFishDistance((ItemBetterFishingRod.getReelItem(fishingRod).getReelRange() - fishCaughtData.deepLevel) * 10);
            this.setFishDistanceTime(0);
            this.setFishDeepLevel(ItemBetterFishingRod.getReelItem(fishingRod).getReelRange() * 10);
            this.setFishWeight(fishCaughtData.weight);
            this.setFishMomentum(0);
            this.setFishTugging(0);
            this.setTensionMomentum(0);
            this.setKeybind(PacketKeybindS.Keybind.NONE);
            this.setMinigameBackground(minigameBackground);
            this.setLineBreak(0);
            this.setLastFishTime(this.getFishTime());
         }
      }
   }

   public void reset(boolean alsoResetFishSwimToHook) {
      if (alsoResetFishSwimToHook) {
         this.setTimeBeforeFishSwimToHook(-1);
         this.fishPopulation = null;
      }

      this.fishCaughtData = null;
      this.setFishTime(0);
      this.setFishing(false);
      this.setErrorVariance(0);
      this.setReelAmount(0);
      this.setReelTarget(0);
      this.setFishDistance(0);
      this.setFishDistanceTime(0);
      this.setFishDeepLevel(0);
      this.setFishWeight(0);
      this.setFishMomentum(0);
      this.setFishTugging(0);
      this.setTensionMomentum(0);
      this.setKeybind(PacketKeybindS.Keybind.NONE);
      this.setMinigameBackground(new Integer[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)});
      this.setLineBreak(0);
   }

   public void reset() {
      this.reset(true);
   }
}
