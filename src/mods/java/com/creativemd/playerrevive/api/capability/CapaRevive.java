package com.creativemd.playerrevive.api.capability;

import com.creativemd.playerrevive.api.CombatTrackerClone;
import com.creativemd.playerrevive.api.IRevival;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapaRevive {
   @CapabilityInject(IRevival.class)
   public static Capability<IRevival> reviveCapa = null;

   public CapaRevive() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(IRevival.class, new CapaReviveStorage(), CapaRevive.DefaultImpl::new);
   }

   public static class DefaultImpl implements IRevival {
      public DefaultImpl() {
      }

      public void tick() {
      }

      public boolean isHealty() {
         return false;
      }

      public void stopBleeding() {
      }

      public void startBleeding(EntityPlayer player, DamageSource source) {
      }

      public float getProgress() {
         return 0.0F;
      }

      public boolean isRevived() {
         return false;
      }

      public boolean isDead() {
         return false;
      }

      public int getTimeLeft() {
         return 0;
      }

      public void kill() {
      }

      public List<EntityPlayer> getRevivingPlayers() {
         return null;
      }

      public NBTTagCompound serializeNBT() {
         return null;
      }

      public void deserializeNBT(NBTTagCompound nbt) {
      }

      public DamageSource getSource() {
         return null;
      }

      public CombatTrackerClone getTrackerClone() {
         return null;
      }
   }
}
