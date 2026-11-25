package bq_standard.rewards.factory;

import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.registry.IFactoryData;
import bq_standard.rewards.RewardRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryRewardRecipe implements IFactoryData<IReward, NBTTagCompound> {
   public static final FactoryRewardRecipe INSTANCE = new FactoryRewardRecipe();

   public FactoryRewardRecipe() {
   }

   public ResourceLocation getRegistryName() {
      return new ResourceLocation("bq_standard", "recipe");
   }

   public RewardRecipe createNew() {
      return new RewardRecipe();
   }

   public RewardRecipe loadFromData(NBTTagCompound json) {
      RewardRecipe reward = new RewardRecipe();
      reward.readFromNBT(json);
      return reward;
   }
}
