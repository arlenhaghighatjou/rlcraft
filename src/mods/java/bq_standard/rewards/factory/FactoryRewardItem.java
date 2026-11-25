package bq_standard.rewards.factory;

import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.rewards.RewardItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryRewardItem implements IFactoryData<IReward, NBTTagCompound> {
   public static final FactoryRewardItem INSTANCE = new FactoryRewardItem();

   public FactoryRewardItem() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard", "item");
   }

   public RewardItem createNew() {
      return new RewardItem();
   }

   public RewardItem loadFromData(NBTTagCompound json) {
      RewardItem reward = new RewardItem();
      reward.readFromNBT(json);
      return reward;
   }
}
