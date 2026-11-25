package tschipp.callablehorses.common.capabilities.horseowner;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tschipp.callablehorses.CallableHorses;
import tschipp.callablehorses.common.capabilities.storedhorse.HorseProvider;
import tschipp.callablehorses.common.capabilities.storedhorse.IStoredHorse;

public class HorseOwner implements IHorseOwner {
   private int horseNum = 0;
   private NBTTagCompound horseNBT = new NBTTagCompound();
   private String storageUUID = "";
   private int lastSeenDim = 0;
   private BlockPos lastSeenPos = BlockPos.ORIGIN;

   public HorseOwner() {
   }

   public AbstractHorse getHorseEntity(World world) {
      Entity entity = EntityList.createEntityFromNBT(this.horseNBT, world);
      if (entity instanceof AbstractHorse) {
         ++this.horseNum;
         IStoredHorse horse = (IStoredHorse)entity.getCapability(HorseProvider.HORSE_CAPABILITY, (EnumFacing)null);
         horse.setHorseNum(this.horseNum);
         entity.setUniqueId(UUID.randomUUID());
         entity.extinguish();
         ((AbstractHorse)entity).hurtTime = 0;
         entity.dimension = world.provider.getDimension();
         entity.timeUntilPortal = 0;
         return (AbstractHorse)entity;
      } else {
         CallableHorses.LOGGER.error("The entity with NBT " + this.horseNBT.toString() + " wasn't a horse somehow?...");
         return null;
      }
   }

   public NBTTagCompound getHorseNBT() {
      return this.horseNBT;
   }

   public void setHorse(AbstractHorse horse, EntityPlayer player) {
      this.storageUUID = UUID.randomUUID().toString();
      IStoredHorse storedHorse = (IStoredHorse)horse.getCapability(HorseProvider.HORSE_CAPABILITY, (EnumFacing)null);
      storedHorse.setHorseNum(this.horseNum);
      storedHorse.setOwned(true);
      storedHorse.setOwnerUUID(player.getGameProfile().getId().toString());
      storedHorse.setStorageUUID(this.storageUUID);
      NBTTagCompound tag = horse.serializeNBT();
      this.horseNBT = tag;
   }

   public void clearHorse() {
      this.horseNum = 0;
      this.horseNBT = new NBTTagCompound();
      this.storageUUID = "";
      this.lastSeenDim = 0;
      this.lastSeenPos = BlockPos.ORIGIN;
   }

   public int getHorseNum() {
      return this.horseNum;
   }

   public void setHorseNum(int num) {
      this.horseNum = num;
   }

   public String getStorageUUID() {
      return this.storageUUID;
   }

   public void setStorageUUID(String id) {
      this.storageUUID = id;
   }

   public void setHorseNBT(NBTTagCompound nbt) {
      this.horseNBT = nbt;
   }

   public void setLastSeenPosition(BlockPos pos) {
      this.lastSeenPos = pos;
   }

   public BlockPos getLastSeenPosition() {
      return this.lastSeenPos;
   }

   public int getLastSeenDim() {
      return this.lastSeenDim;
   }

   public void setLastSeenDim(int i) {
      this.lastSeenDim = i;
   }
}
