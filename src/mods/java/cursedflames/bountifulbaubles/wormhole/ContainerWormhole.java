package cursedflames.bountifulbaubles.wormhole;

import cursedflames.bountifulbaubles.capability.CapabilityWormholePins;
import cursedflames.bountifulbaubles.network.NBTPacket;
import cursedflames.bountifulbaubles.network.PacketHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class ContainerWormhole extends Container {
   EntityPlayer player;
   List<IWormholeTarget> targets = new ArrayList();
   List<IWormholeTarget> pinned;
   int pinCount = 0;
   boolean dirty = false;
   boolean guiDirty = false;

   public ContainerWormhole(EntityPlayer player) {
      this.player = player;
      if (!player.world.isRemote) {
         List<IWormholeTarget> pinned = new ArrayList();
         CapabilityWormholePins.IWormholePins cap = (CapabilityWormholePins.IWormholePins)player.getCapability(CapabilityWormholePins.PIN_CAP, (EnumFacing)null);
         if (cap != null) {
            pinned = cap.getPinList();
         }

         this.pinned = pinned;
         Map<Integer, IWormholeTarget> pinnedFound = new HashMap();
         List<EntityPlayer> players = new ArrayList(player.world.playerEntities);
         players.remove(player);
         boolean survivalOnly = !player.isCreative() && !player.isSpectator();

         for(EntityPlayer entity : players) {
            if (!entity.isSpectator() && (!survivalOnly || !entity.isCreative())) {
               this.targets.add(new PlayerTarget(entity));
            }
         }

         for(int i = 0; i < this.targets.size(); ++i) {
            IWormholeTarget target = (IWormholeTarget)this.targets.get(i);

            for(int j = 0; j < pinned.size(); ++j) {
               IWormholeTarget other = (IWormholeTarget)pinned.get(j);
               if (target.isEqual(other)) {
                  pinnedFound.put(Integer.valueOf(j), target);
                  this.targets.remove(i);
                  --i;
                  break;
               }
            }
         }

         for(int i = pinned.size() - 1; i >= 0; --i) {
            IWormholeTarget target;
            if (pinnedFound.containsKey(Integer.valueOf(i))) {
               target = (IWormholeTarget)pinnedFound.get(Integer.valueOf(i));
            } else {
               target = (IWormholeTarget)pinned.get(i);
               target.setEnabled(false);
            }

            this.targets.add(0, target);
         }

         this.pinCount = pinned.size();
         this.dirty = true;
      }
   }

   public boolean canInteractWith(EntityPlayer player) {
      return player == this.player;
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();
      if (!this.player.world.isRemote && this.dirty) {
         NBTTagCompound changes = new NBTTagCompound();
         NBTTagCompound targetsNBT = new NBTTagCompound();
         int i = 0;

         for(IWormholeTarget target : this.targets) {
            NBTTagCompound targetNBT = target.toNBT();
            if (targetNBT != null) {
               targetNBT.setBoolean("enabled", target.isEnabled());
               targetsNBT.setTag(String.valueOf(i++), targetNBT);
            }
         }

         changes.setTag("targets", targetsNBT);
         changes.setInteger("pinCount", this.pinCount);

         for(int j = 0; j < this.listeners.size(); ++j) {
            if (this.listeners.get(j) instanceof EntityPlayerMP) {
               PacketHandler.INSTANCE.sendTo(new NBTPacket(changes, PacketHandler.HandlerIds.WORMHOLE_UPDATE_GUI.id), (EntityPlayerMP)this.listeners.get(j));
            }
         }

         this.dirty = false;
      }
   }

   public void readChanges(NBTTagCompound tag) {
      NBTTagCompound targetsNBT = tag.getCompoundTag("targets");
      if (targetsNBT.getSize() > 0) {
         this.targets.clear();
         CapabilityWormholePins.targetListFromNBT(this.targets, targetsNBT);
         this.guiDirty = true;
      }

      if (tag.hasKey("pinCount")) {
         this.pinCount = tag.getInteger("pinCount");
      }

   }

   public void pin(int index) {
      if (index >= 0 && index <= this.targets.size() - 1) {
         IWormholeTarget target = (IWormholeTarget)this.targets.get(index);
         if (index < this.pinCount) {
            --this.pinCount;
            this.targets.add(this.pinCount + 1, target);
            this.targets.remove(index);
            this.pinned.remove(index);
         } else {
            ++this.pinCount;
            this.targets.remove(index);
            this.targets.add(this.pinCount - 1, target);
            this.pinned.add(target);
         }

         this.dirty = true;
      }
   }
}
