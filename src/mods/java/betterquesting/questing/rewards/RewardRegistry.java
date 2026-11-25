package betterquesting.questing.rewards;

import betterquesting.api.placeholders.rewards.FactoryRewardPlaceholder;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.api2.registry.IRegistry;
import betterquesting.core.BetterQuesting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class RewardRegistry implements IRegistry<IFactoryData<IReward, NBTTagCompound>, IReward> {
   public static final RewardRegistry INSTANCE = new RewardRegistry();
   private final HashMap<ResourceLocation, IFactoryData<IReward, NBTTagCompound>> rewardRegistry = new HashMap();

   public RewardRegistry() {
   }

   public void register(IFactoryData<IReward, NBTTagCompound> factory) {
      if (factory == null) {
         throw new NullPointerException("Tried to register null reward");
      } else if (factory.getRegistryName() == null) {
         throw new IllegalArgumentException("Tried to register a reward with a null name: " + factory.getClass());
      } else if (!this.rewardRegistry.containsKey(factory.getRegistryName()) && !this.rewardRegistry.containsValue(factory)) {
         this.rewardRegistry.put(factory.getRegistryName(), factory);
      } else {
         throw new IllegalArgumentException("Cannot register dupliate reward type: " + factory.getRegistryName());
      }
   }

   public IFactoryData<IReward, NBTTagCompound> getFactory(ResourceLocation registryName) {
      return (IFactoryData)this.rewardRegistry.get(registryName);
   }

   public List<IFactoryData<IReward, NBTTagCompound>> getAll() {
      return new ArrayList(this.rewardRegistry.values());
   }

   public IReward createNew(ResourceLocation registryName) {
      try {
         IFactoryData<? extends IReward, NBTTagCompound> factory;
         if (FactoryRewardPlaceholder.INSTANCE.getRegistryName().equals(registryName)) {
            factory = FactoryRewardPlaceholder.INSTANCE;
         } else {
            factory = this.getFactory(registryName);
         }

         if (factory == null) {
            BetterQuesting.logger.log(Level.ERROR, "Tried to load missing reward type '" + registryName + "'! Are you missing an expansion pack?");
            return null;
         } else {
            return (IReward)factory.createNew();
         }
      } catch (Exception var3) {
         BetterQuesting.logger.log(Level.ERROR, "Unable to instatiate reward: " + registryName, var3);
         return null;
      }
   }
}
