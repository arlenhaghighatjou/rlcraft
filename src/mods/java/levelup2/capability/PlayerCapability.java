package levelup2.capability;

import java.util.UUID;
import levelup2.api.IProcessor;
import levelup2.player.IPlayerClass;
import levelup2.util.Library;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerCapability {
   @CapabilityInject(IPlayerClass.class)
   public static Capability<IPlayerClass> PLAYER_CLASS = null;
   @CapabilityInject(IProcessor.class)
   public static Capability<IProcessor> MACHINE_PROCESSING = null;

   public PlayerCapability() {
   }

   public static class CapabilityPlayerClass<T extends IPlayerClass> implements IStorage<IPlayerClass> {
      public CapabilityPlayerClass() {
      }

      public NBTBase writeNBT(Capability<IPlayerClass> capability, IPlayerClass player, EnumFacing side) {
         return player.saveNBTData(new NBTTagCompound());
      }

      public void readNBT(Capability<IPlayerClass> capability, IPlayerClass player, EnumFacing side, NBTBase nbt) {
         player.loadNBTData((NBTTagCompound)nbt);
      }
   }

   public static class CapabilityProcessorClass<T extends IProcessor> implements IStorage<IProcessor> {
      public CapabilityProcessorClass() {
      }

      public NBTBase writeNBT(Capability<IProcessor> capability, IProcessor process, EnumFacing side) {
         return process.writeToNBT(new NBTTagCompound());
      }

      public void readNBT(Capability<IProcessor> capability, IProcessor process, EnumFacing side, NBTBase nbt) {
         process.readFromNBT((NBTTagCompound)nbt);
      }
   }

   public static class CapabilityProcessorDefault implements IProcessor {
      private EntityPlayer player;
      protected UUID playerUUID;
      protected TileEntity tile;

      public CapabilityProcessorDefault(TileEntity entity) {
         this.tile = entity;
      }

      public void setUUID(UUID playerUUID) {
         this.playerUUID = playerUUID;
      }

      public void readFromNBT(NBTTagCompound tag) {
         if (tag.hasKey("player_uuid")) {
            this.playerUUID = UUID.fromString(tag.getString("player_uuid"));
            this.player = Library.getPlayerFromUUID(this.playerUUID);
         }

      }

      public NBTTagCompound writeToNBT(NBTTagCompound tag) {
         if (this.playerUUID != null) {
            tag.setString("player_uuid", this.playerUUID.toString());
         }

         return tag;
      }

      public EntityPlayer getPlayerFromUUID() {
         if (this.playerUUID == null) {
            if (this.player != null) {
               this.player = null;
            }

            return null;
         } else {
            if (this.player == null) {
               this.player = Library.getPlayerFromUUID(this.playerUUID);
            }

            return this.player;
         }
      }

      public void extraProcessing(EntityPlayer player) {
      }
   }
}
