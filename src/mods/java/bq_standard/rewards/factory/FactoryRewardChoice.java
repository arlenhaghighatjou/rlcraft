package bq_standard.rewards.factory;

import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.rewards.RewardChoice;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryRewardChoice implements IFactoryData<IReward, NBTTagCompound> {
   public static final FactoryRewardChoice INSTANCE = new FactoryRewardChoice();

   public FactoryRewardChoice() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard", "choice");
   }

   public RewardChoice createNew() {
      return new RewardChoice();
   }

   public RewardChoice loadFromData(NBTTagCompound json) {
      RewardChoice reward = new RewardChoice();
      reward.readFromNBT(json);
      return reward;
   }
}
