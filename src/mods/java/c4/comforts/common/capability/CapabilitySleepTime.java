package c4.comforts.common.capability;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

public class CapabilitySleepTime {
   public static Capability<CapabilitySleepTime.ISleepTime> SLEEP_TIME_CAP = null;
   public static final EnumFacing DEFAULT_FACING = null;
   public static final ResourceLocation ID = new ResourceLocation("comforts", "sleepTime");

   public CapabilitySleepTime() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(CapabilitySleepTime.ISleepTime.class, new IStorage<CapabilitySleepTime.ISleepTime>() {
         public NBTBase writeNBT(Capability<CapabilitySleepTime.ISleepTime> capability, CapabilitySleepTime.ISleepTime instance, EnumFacing side) {
            return new NBTTagLong(instance.getSleepTime());
         }

         public void readNBT(Capability<CapabilitySleepTime.ISleepTime> capability, CapabilitySleepTime.ISleepTime instance, EnumFacing side, NBTBase nbt) {
            instance.setSleepTime(((NBTTagLong)nbt).getLong());
         }
      }, CapabilitySleepTime.SleepTime::new);
      (new CapabilitySleepTime.EventHandler()).register(MinecraftForge.EVENT_BUS);
   }

   @Nullable
   public static CapabilitySleepTime.ISleepTime getSleepTime(EntityPlayer playerIn) {
      return playerIn != null && playerIn.hasCapability(SLEEP_TIME_CAP, DEFAULT_FACING) ? (CapabilitySleepTime.ISleepTime)playerIn.getCapability(SLEEP_TIME_CAP, DEFAULT_FACING) : null;
   }

   public static ICapabilityProvider createProvider(CapabilitySleepTime.ISleepTime sleeping) {
      return new CapabilitySleepTime.Provider(sleeping, SLEEP_TIME_CAP, DEFAULT_FACING);
   }

   private static class EventHandler {
      private EventHandler() {
      }

      public void register(EventBus bus) {
         bus.registerGeneric(AttachCapabilitiesEvent.class, Entity.class, this::attachCapabilities);
         bus.register(PlayerSleepInBedEvent.class, this::onPlayerSleep);
      }

      public void attachCapabilities(AttachCapabilitiesEvent<Entity> evt) {
         if (evt.getObject() instanceof EntityPlayer) {
            evt.addCapability(CapabilitySleepTime.ID, CapabilitySleepTime.createProvider(new CapabilitySleepTime.SleepTime()));
         }

      }

      public void onPlayerSleep(PlayerSleepInBedEvent evt) {
         EntityPlayer player = evt.getEntityPlayer();
         World world = player.getEntityWorld();
         if (!world.isRemote) {
            CapabilitySleepTime.ISleepTime sleepTime = CapabilitySleepTime.getSleepTime(player);
            if (sleepTime != null) {
               sleepTime.setSleepTime(world.getWorldTime());
            }
         }

      }
   }

   public interface ISleepTime {
      long getSleepTime();

      void setSleepTime(long var1);
   }

   public static class Provider implements ICapabilitySerializable<NBTBase> {
      final Capability<CapabilitySleepTime.ISleepTime> capability;
      final EnumFacing facing;
      final CapabilitySleepTime.ISleepTime instance;

      Provider(CapabilitySleepTime.ISleepTime instance, Capability<CapabilitySleepTime.ISleepTime> capability, @Nullable EnumFacing facing) {
         this.instance = instance;
         this.capability = capability;
         this.facing = facing;
      }

      public boolean hasCapability(@Nullable Capability<?> capability, EnumFacing facing) {
         return capability == this.getCapability();
      }

      public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
         return (T)(capability == this.getCapability() ? this.getCapability().cast(this.instance) : null);
      }

      final Capability<CapabilitySleepTime.ISleepTime> getCapability() {
         return this.capability;
      }

      EnumFacing getFacing() {
         return this.facing;
      }

      final CapabilitySleepTime.ISleepTime getInstance() {
         return this.instance;
      }

      public NBTBase serializeNBT() {
         return this.getCapability().writeNBT(this.getInstance(), this.getFacing());
      }

      public void deserializeNBT(NBTBase nbt) {
         this.getCapability().readNBT(this.getInstance(), this.getFacing(), nbt);
      }
   }

   public static class SleepTime implements CapabilitySleepTime.ISleepTime {
      private long sleepTime;

      public SleepTime() {
      }

      public long getSleepTime() {
         return this.sleepTime;
      }

      public void setSleepTime(long time) {
         this.sleepTime = time;
      }
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(CapabilitySleepTime.ISleepTime.class, capability -> SLEEP_TIME_CAP = capability);
   }
}
