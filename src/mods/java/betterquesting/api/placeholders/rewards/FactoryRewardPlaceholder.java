package betterquesting.api.placeholders.rewards;

import betterquesting.api2.registry.IFactoryData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryRewardPlaceholder implements IFactoryData<RewardPlaceholder, NBTTagCompound> {
   public static final FactoryRewardPlaceholder INSTANCE = new FactoryRewardPlaceholder();
   private final ResourceLocation ID = new ResourceLocation("betterquesting:placeholder");

   private FactoryRewardPlaceholder() {
   }

   public ResourceLocation getRegistryName() {
      return this.ID;
   }

   public RewardPlaceholder createNew() {
      return new RewardPlaceholder();
   }

   public RewardPlaceholder loadFromData(NBTTagCompound nbt) {
      RewardPlaceholder reward = this.createNew();
      reward.readFromNBT(nbt);
      return reward;
   }
}
