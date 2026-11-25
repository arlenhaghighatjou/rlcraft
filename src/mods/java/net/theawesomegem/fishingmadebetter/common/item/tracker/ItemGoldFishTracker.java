package net.theawesomegem.fishingmadebetter.common.item.tracker;

import net.theawesomegem.fishingmadebetter.common.data.FishData;

public class ItemGoldFishTracker extends ItemFishTracker {
   public ItemGoldFishTracker() {
      super(ItemFishTracker.TrackingVision.NORMAL, FishData.FishingLiquid.WATER, 100, "fish_tracker_gold");
   }
}
