package c4.comforts.common.capability;

import c4.comforts.common.ConfigHandler;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityWellRested {
   @CapabilityInject(CapabilityWellRested.IWellRested.class)
   public static final Capability<CapabilityWellRested.IWellRested> RESTED_CAP = null;
   public static final EnumFacing DEFAULT_FACING = null;
   public static final ResourceLocation ID = new ResourceLocation("comforts", "wellRested");
   private static final String WAKE_TAG = "wakeTime";
   private static final String TIRED_TAG = "tiredTime";

   public CapabilityWellRested() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(CapabilityWellRested.IWellRested.class, new IStorage<CapabilityWellRested.IWellRested>() {
         public NBTBase writeNBT(Capability<CapabilityWellRested.IWellRested> capability, CapabilityWellRested.IWellRested instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setLong("wakeTime", instance.getWakeTime());
            compound.setLong("tiredTime", instance.getTiredTime());
            return compound;
         }

         public void readNBT(Capability<CapabilityWellRested.IWellRested> capability, CapabilityWellRested.IWellRested instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound)nbt;
            instance.setWakeTime(compound.getLong("wakeTime"));
            instance.setTiredTime(compound.getLong("tiredTime"));
         }
      }, CapabilityWellRested.WellRested::new);
      if (ConfigHandler.wellRested) {
         MinecraftForge.EVENT_BUS.register(new CapabilityWellRested.EventHandler());
      }

   }

   @Nullable
   public static CapabilityWellRested.IWellRested getRested(EntityPlayer playerIn) {
      return playerIn != null && playerIn.hasCapability(RESTED_CAP, DEFAULT_FACING) ? (CapabilityWellRested.IWellRested)playerIn.getCapability(RESTED_CAP, DEFAULT_FACING) : null;
   }

   public static ICapabilityProvider createProvider(CapabilityWellRested.IWellRested rested) {
      return new CapabilityWellRested.Provider(rested, RESTED_CAP, DEFAULT_FACING);
   }

   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public void attachCapabilities(AttachCapabilitiesEvent<Entity> evt) {
         if (evt.getObject() instanceof EntityPlayer) {
            evt.addCapability(CapabilityWellRested.ID, CapabilityWellRested.createProvider(new CapabilityWellRested.WellRested()));
         }

      }

      @SubscribeEvent
      public void onPlayerRespawn(Clone evt) {
         CapabilityWellRested.IWellRested newWellRested = CapabilityWellRested.getRested(evt.getEntityPlayer());
         CapabilityWellRested.IWellRested oldWellRested = CapabilityWellRested.getRested(evt.getOriginal());
         if (newWellRested != null && oldWellRested != null) {
            newWellRested.setWakeTime(oldWellRested.getWakeTime());
            newWellRested.setTiredTime(oldWellRested.getTiredTime());
         }

      }

      @SubscribeEvent
      public void onPlayerSleep(PlayerSleepInBedEvent evt) {
         EntityPlayer player = evt.getEntityPlayer();
         if (!player.world.isRemote) {
            CapabilityWellRested.IWellRested wellRested = CapabilityWellRested.getRested(player);
            long worldTime = player.getEntityWorld().getWorldTime();
            if (wellRested != null) {
               if (wellRested.getWakeTime() > worldTime) {
                  wellRested.setWakeTime(0L);
                  wellRested.setTiredTime(0L);
               }

               if (wellRested.getTiredTime() > worldTime) {
                  evt.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("capability.comforts.notSleepy", new Object[0]), true);
                  evt.setResult(SleepResult.OTHER_PROBLEM);
               }
            }
         }

      }

      @SubscribeEvent
      public void onPlayerWakeUp(PlayerWakeUpEvent evt) {
         EntityPlayer player = evt.getEntityPlayer();
         CapabilitySleepTime.ISleepTime sleepTime = CapabilitySleepTime.getSleepTime(player);
         CapabilityWellRested.IWellRested wellRested = CapabilityWellRested.getRested(player);
         if (sleepTime != null && wellRested != null) {
            long wakeTime = player.world.getWorldTime();
            long timeSlept = wakeTime - sleepTime.getSleepTime();
            if (timeSlept > 500L) {
               wellRested.setWakeTime(wakeTime);
               wellRested.setTiredTime(wakeTime + (long)((float)timeSlept / ConfigHandler.sleepyRatio));
            }
         }

      }
   }

   public interface IWellRested {
      long getWakeTime();

      void setWakeTime(long var1);

      long getTiredTime();

      void setTiredTime(long var1);
   }

   public static class Provider implements ICapabilitySerializable<NBTBase> {
      final Capability<CapabilityWellRested.IWellRested> capability;
      final EnumFacing facing;
      final CapabilityWellRested.IWellRested instance;

      Provider(CapabilityWellRested.IWellRested instance, Capability<CapabilityWellRested.IWellRested> capability, @Nullable EnumFacing facing) {
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

      final Capability<CapabilityWellRested.IWellRested> getCapability() {
         return this.capability;
      }

      EnumFacing getFacing() {
         return this.facing;
      }

      final CapabilityWellRested.IWellRested getInstance() {
         return this.instance;
      }

      public NBTBase serializeNBT() {
         return this.getCapability().writeNBT(this.getInstance(), this.getFacing());
      }

      public void deserializeNBT(NBTBase nbt) {
         this.getCapability().readNBT(this.getInstance(), this.getFacing(), nbt);
      }
   }

   public static class WellRested implements CapabilityWellRested.IWellRested {
      private long wakeTime = 0L;
      private long tiredTime = 0L;

      public WellRested() {
      }

      public long getWakeTime() {
         return this.wakeTime;
      }

      public void setWakeTime(long wakeTime) {
         this.wakeTime = wakeTime;
      }

      public long getTiredTime() {
         return this.tiredTime;
      }

      public void setTiredTime(long tiredTime) {
         this.tiredTime = tiredTime;
      }
   }
}
