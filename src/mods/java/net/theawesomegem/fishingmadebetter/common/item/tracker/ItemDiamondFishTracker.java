package net.theawesomegem.fishingmadebetter.common.item.tracker;

import net.theawesomegem.fishingmadebetter.common.data.FishData;

public class ItemDiamondFishTracker extends ItemFishTracker {
   public ItemDiamondFishTracker() {
      super(ItemFishTracker.TrackingVision.BEST, FishData.FishingLiquid.WATER, 150, "fish_tracker_diamond");
   }
}
