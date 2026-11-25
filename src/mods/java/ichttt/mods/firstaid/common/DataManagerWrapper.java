package ichttt.mods.firstaid.common;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution;
import ichttt.mods.firstaid.common.damagesystem.distribution.HealthDistribution;
import ichttt.mods.firstaid.common.damagesystem.distribution.RandomDamageDistribution;
import ichttt.mods.firstaid.common.network.MessageApplyAbsorption;
import ichttt.mods.firstaid.common.util.CommonUtils;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DataManagerWrapper extends EntityDataManager {
   private final EntityPlayer player;
   private final EntityDataManager parent;
   private boolean track = true;

   public DataManagerWrapper(EntityPlayer player, EntityDataManager parent) {
      super(player);
      this.player = player;
      this.parent = parent;
   }

   @Nonnull
   public <T> T get(@Nonnull DataParameter<T> key) {
      if (key == EntityPlayer.ABSORPTION) {
         this.parent.set(key, ((AbstractPlayerDamageModel)Objects.requireNonNull(this.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null))).getAbsorption());
      }

      return (T)this.parent.get(key);
   }

   public <T> void set_impl(@Nonnull DataParameter<T> key, @Nonnull T value) {
      this.parent.set(key, value);
   }

   public <T> void set(@Nonnull DataParameter<T> key, @Nonnull T value) {
      if (!this.track) {
         if (key != EntityLivingBase.HEALTH) {
            this.set_impl(key, value);
         }

      } else {
         if (key == EntityPlayer.ABSORPTION) {
            float floatValue = ((Float)value).floatValue();
            if (this.player instanceof EntityPlayerMP) {
               EntityPlayerMP playerMP = (EntityPlayerMP)this.player;
               if (playerMP.connection != null) {
                  FirstAid.NETWORKING.sendTo(new MessageApplyAbsorption(floatValue), playerMP);
               }
            }

            ((AbstractPlayerDamageModel)Objects.requireNonNull(this.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null))).setAbsorption(floatValue);
         } else if (key == EntityLivingBase.HEALTH && value instanceof Float && !this.player.world.isRemote) {
            float aFloat = ((Float)value).floatValue();
            if (aFloat > this.player.getMaxHealth()) {
               ((AbstractPlayerDamageModel)Objects.requireNonNull(this.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null))).forEach((damageablePart) -> {
                  damageablePart.currentHealth = (float)damageablePart.getMaxHealth();
               });
            } else {
               AbstractPlayerDamageModel damageModel;
               if ((damageModel = (AbstractPlayerDamageModel)this.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null)) != null && damageModel.isWaitingForHelp()) {
                  if (FirstAidConfig.debug) {
                     CommonUtils.debugLogStacktrace("SetHealth falltrough");
                  }
               } else if (FirstAidConfig.watchSetHealth && !Float.isInfinite(aFloat) && !Float.isNaN(aFloat) && aFloat > 0.0F && this.player instanceof EntityPlayerMP && ((EntityPlayerMP)this.player).connection != null) {
                  float orig = ((Float)this.get(EntityLivingBase.HEALTH)).floatValue();
                  if (orig > 0.0F && !Float.isNaN(orig) && !Float.isInfinite(orig)) {
                     if (FirstAidConfig.scaleMaxHealth) {
                        orig = Math.min(orig, (float)this.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
                     }

                     float healed = aFloat - orig;
                     if ((double)Math.abs(healed) > 0.001D) {
                        if (healed < 0.0F) {
                           if (FirstAidConfig.debug) {
                              CommonUtils.debugLogStacktrace("DAMAGING: " + -healed);
                           }

                           DamageDistribution.handleDamageTaken(RandomDamageDistribution.getDefault(), (AbstractPlayerDamageModel)this.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null), -healed, this.player, DamageSource.MAGIC, true, true);
                        } else {
                           if (FirstAidConfig.debug) {
                              CommonUtils.debugLogStacktrace("HEALING: " + healed);
                           }

                           HealthDistribution.addRandomHealth(healed, this.player, true);
                        }
                     }

                     return;
                  }
               }
            }
         }

         this.set_impl(key, value);
      }
   }

   public void toggleTracking(boolean status) {
      if (FirstAidConfig.debug) {
         CommonUtils.debugLogStacktrace("Tracking status change from " + this.track + " to " + status);
      }

      this.track = status;
   }

   public <T> void register(DataParameter<T> key, @Nonnull T value) {
      this.parent.register(key, value);
   }

   public <T> void setDirty(@Nonnull DataParameter<T> key) {
      this.parent.setDirty(key);
   }

   public boolean isDirty() {
      return this.parent.isDirty();
   }

   @Nullable
   public List<DataEntry<?>> getDirty() {
      return this.parent.getDirty();
   }

   public void writeEntries(PacketBuffer buf) throws IOException {
      this.parent.writeEntries(buf);
   }

   @Nullable
   public List<DataEntry<?>> getAll() {
      return this.parent.getAll();
   }

   @SideOnly(Side.CLIENT)
   public void setEntryValues(List<DataEntry<?>> entriesIn) {
      this.parent.setEntryValues(entriesIn);
   }

   @SideOnly(Side.CLIENT)
   public <T> void setEntryValue(DataEntry<T> target, DataEntry<?> source) {
      this.parent.setEntryValue(target, source);
   }

   public boolean isEmpty() {
      return this.parent.isEmpty();
   }

   public void setClean() {
      this.parent.setClean();
   }

   @Nonnull
   public <T> DataEntry<T> getEntry(DataParameter<T> key) {
      return this.parent.getEntry(key);
   }
}
