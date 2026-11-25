package net.theawesomegem.fishingmadebetter.common.item.tracker;

import net.theawesomegem.fishingmadebetter.common.data.FishData;

public class ItemObsidianFishTracker extends ItemFishTracker {
   public ItemObsidianFishTracker() {
      super(ItemFishTracker.TrackingVision.BEST, FishData.FishingLiquid.LAVA, 100, "fish_tracker_obsidian");
   }
}
