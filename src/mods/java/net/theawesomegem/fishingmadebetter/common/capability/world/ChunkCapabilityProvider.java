package net.theawesomegem.fishingmadebetter.common.capability.world;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ChunkCapabilityProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(IChunkFishingData.class)
   public static final Capability<IChunkFishingData> CHUNK_FISHING_DATA_CAP = null;
   private final IChunkFishingData chunkFishingData = new ChunkFishingData();

   public ChunkCapabilityProvider() {
   }

   public NBTBase serializeNBT() {
      return CHUNK_FISHING_DATA_CAP.getStorage().writeNBT(CHUNK_FISHING_DATA_CAP, this.chunkFishingData, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      CHUNK_FISHING_DATA_CAP.getStorage().readNBT(CHUNK_FISHING_DATA_CAP, this.chunkFishingData, (EnumFacing)null, nbt);
   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CHUNK_FISHING_DATA_CAP;
   }

   @Nullable
   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CHUNK_FISHING_DATA_CAP ? CHUNK_FISHING_DATA_CAP.cast(this.chunkFishingData) : null);
   }
}
