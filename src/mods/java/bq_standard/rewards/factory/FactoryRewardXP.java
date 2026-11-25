package bq_standard.rewards.factory;

import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.rewards.RewardXP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryRewardXP implements IFactoryData<IReward, NBTTagCompound> {
   public static final FactoryRewardXP INSTANCE = new FactoryRewardXP();

   public FactoryRewardXP() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard", "xp");
   }

   public RewardXP createNew() {
      return new RewardXP();
   }

   public RewardXP loadFromData(NBTTagCompound json) {
      RewardXP reward = new RewardXP();
      reward.readFromNBT(json);
      return reward;
   }
}
