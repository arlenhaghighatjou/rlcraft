package bq_standard.rewards.factory;

import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.rewards.RewardCommand;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryRewardCommand implements IFactoryData<IReward, NBTTagCompound> {
   public static final FactoryRewardCommand INSTANCE = new FactoryRewardCommand();

   public FactoryRewardCommand() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard", "command");
   }

   public RewardCommand createNew() {
      return new RewardCommand();
   }

   public RewardCommand loadFromData(NBTTagCompound json) {
      RewardCommand reward = new RewardCommand();
      reward.readFromNBT(json);
      return reward;
   }
}
