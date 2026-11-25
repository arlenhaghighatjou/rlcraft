package cursedflames.bountifulbaubles.capability;

import cursedflames.bountifulbaubles.wormhole.DebugTarget;
import cursedflames.bountifulbaubles.wormhole.IWormholeTarget;
import cursedflames.bountifulbaubles.wormhole.PlayerTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityWormholePins {
   @CapabilityInject(CapabilityWormholePins.IWormholePins.class)
   public static final Capability<CapabilityWormholePins.IWormholePins> PIN_CAP = null;

   public CapabilityWormholePins() {
   }

   public static void registerCapability() {
      CapabilityManager.INSTANCE.register(CapabilityWormholePins.IWormholePins.class, new CapabilityWormholePins.Storage(), CapabilityWormholePins.DefaultImpl::new);
   }

   @SubscribeEvent
   public static void onEntityConstruct(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(new ResourceLocation("bountifulbaubles", "IWormholePins"), new ICapabilitySerializable<NBTTagCompound>() {
            CapabilityWormholePins.IWormholePins inst = (CapabilityWormholePins.IWormholePins)CapabilityWormholePins.PIN_CAP.getDefaultInstance();

            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
               return capability == CapabilityWormholePins.PIN_CAP;
            }

            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
               return (T)(capability == CapabilityWormholePins.PIN_CAP ? CapabilityWormholePins.PIN_CAP.cast(this.inst) : null);
            }

            public NBTTagCompound serializeNBT() {
               return (NBTTagCompound)CapabilityWormholePins.PIN_CAP.getStorage().writeNBT(CapabilityWormholePins.PIN_CAP, this.inst, (EnumFacing)null);
            }

            public void deserializeNBT(NBTTagCompound nbt) {
               CapabilityWormholePins.PIN_CAP.getStorage().readNBT(CapabilityWormholePins.PIN_CAP, this.inst, (EnumFacing)null, nbt);
            }
         });
      }
   }

   public static NBTTagCompound targetListToNBT(List<IWormholeTarget> targets) {
      NBTTagCompound tag = new NBTTagCompound();
      int i = 0;

      for(IWormholeTarget target : targets) {
         NBTTagCompound targetNBT = target.toNBT();
         if (targetNBT != null) {
            tag.setTag(String.valueOf(i++), targetNBT);
         }
      }

      return tag;
   }

   public static List<IWormholeTarget> targetListFromNBT(List<IWormholeTarget> targets, NBTTagCompound tag) {
      targets.clear();

      for(int i = 0; tag.hasKey(String.valueOf(i)); ++i) {
         NBTTagCompound entry = tag.getCompoundTag(String.valueOf(i));
         IWormholeTarget target = targetFromNBT(entry);
         if (target != null) {
            targets.add(target);
         }
      }

      return targets;
   }

   public static IWormholeTarget targetFromNBT(NBTTagCompound tag) {
      String type = tag.getString("type");
      IWormholeTarget target = null;
      if (type.equals("player")) {
         target = new PlayerTarget();
      } else if (type.equals("debug")) {
         target = new DebugTarget();
      }

      if (target == null) {
         return null;
      } else {
         target.fromNBT(tag);
         target.setEnabled(tag.hasKey("enabled") ? tag.getBoolean("enabled") : true);
         return target;
      }
   }

   public static class DefaultImpl implements CapabilityWormholePins.IWormholePins {
      List<IWormholeTarget> pinList = new ArrayList();

      public DefaultImpl() {
      }

      public List<IWormholeTarget> getPinList() {
         return this.pinList;
      }
   }

   public interface IWormholePins {
      List<IWormholeTarget> getPinList();
   }

   public static class Storage implements IStorage<CapabilityWormholePins.IWormholePins> {
      public Storage() {
      }

      public NBTBase writeNBT(Capability<CapabilityWormholePins.IWormholePins> capability, CapabilityWormholePins.IWormholePins instance, EnumFacing side) {
         List<IWormholeTarget> pins = instance.getPinList();
         return CapabilityWormholePins.targetListToNBT(pins);
      }

      public void readNBT(Capability<CapabilityWormholePins.IWormholePins> capability, CapabilityWormholePins.IWormholePins instance, EnumFacing side, NBTBase nbtBase) {
         if (nbtBase instanceof NBTTagCompound) {
            CapabilityWormholePins.targetListFromNBT(instance.getPinList(), (NBTTagCompound)nbtBase);
         }
      }
   }
}
