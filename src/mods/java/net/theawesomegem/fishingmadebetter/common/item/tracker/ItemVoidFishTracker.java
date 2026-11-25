package net.theawesomegem.fishingmadebetter.common.item.tracker;

import net.theawesomegem.fishingmadebetter.common.data.FishData;

public class ItemVoidFishTracker extends ItemFishTracker {
   public ItemVoidFishTracker() {
      super(ItemFishTracker.TrackingVision.BEST, FishData.FishingLiquid.VOID, 100, "fish_tracker_void");
   }
}
