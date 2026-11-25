package betterquesting.api2.cache;

import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CapabilityProviderQuestCache implements ICapabilityProvider, ICapabilitySerializable<NBTTagCompound> {
   @CapabilityInject(QuestCache.class)
   public static Capability<QuestCache> CAP_QUEST_CACHE;
   public static final ResourceLocation LOC_QUEST_CACHE = new ResourceLocation("betterquesting", "quest_cache");
   private final QuestCache cache = new QuestCache();

   public CapabilityProviderQuestCache() {
   }

   public NBTTagCompound serializeNBT() {
      return this.cache.serializeNBT();
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.cache.deserializeNBT(nbt);
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CAP_QUEST_CACHE;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CAP_QUEST_CACHE ? CAP_QUEST_CACHE.cast(this.cache) : null);
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(QuestCache.class, new IStorage<QuestCache>() {
         @Nullable
         public NBTBase writeNBT(Capability<QuestCache> capability, QuestCache instance, EnumFacing side) {
            return instance.serializeNBT();
         }

         public void readNBT(Capability<QuestCache> capability, QuestCache instance, EnumFacing side, NBTBase nbt) {
            if (nbt instanceof NBTTagCompound) {
               instance.deserializeNBT((NBTTagCompound)nbt);
            }

         }
      }, QuestCache::new);
   }
}
