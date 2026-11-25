package bq_standard.rewards.factory;

import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.rewards.RewardScoreboard;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryRewardScoreboard implements IFactoryData<IReward, NBTTagCompound> {
   public static final FactoryRewardScoreboard INSTANCE = new FactoryRewardScoreboard();

   public FactoryRewardScoreboard() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard", "scoreboard");
   }

   public RewardScoreboard createNew() {
      return new RewardScoreboard();
   }

   public RewardScoreboard loadFromData(NBTTagCompound json) {
      RewardScoreboard reward = new RewardScoreboard();
      reward.readFromNBT(json);
      return reward;
   }
}
