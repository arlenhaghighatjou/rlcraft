package net.theawesomegem.fishingmadebetter.common.item.tracker;

import net.theawesomegem.fishingmadebetter.common.data.FishData;

public class ItemIronFishTracker extends ItemFishTracker {
   public ItemIronFishTracker() {
      super(ItemFishTracker.TrackingVision.BAD, FishData.FishingLiquid.WATER, 50, "fish_tracker_iron");
   }
}
