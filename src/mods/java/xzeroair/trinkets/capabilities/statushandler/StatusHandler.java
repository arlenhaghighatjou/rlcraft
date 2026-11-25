package xzeroair.trinkets.capabilities.statushandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.CapabilityBase;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.status.StatusEffectPacket;
import xzeroair.trinkets.traits.statuseffects.StatusEffectsEnum;

public class StatusHandler extends CapabilityBase<StatusHandler, EntityLivingBase> {
   Map<String, TrinketStatusEffect> effects = new HashMap();

   public StatusHandler(EntityLivingBase entity) {
      super(entity);
   }

   public void onUpdate() {
      if (!this.effects.isEmpty()) {
         for(Entry<String, TrinketStatusEffect> entry : this.effects.entrySet()) {
            if (((TrinketStatusEffect)entry.getValue()).getDuration() > 0) {
               ((TrinketStatusEffect)entry.getValue()).tickDuration();
            } else {
               this.effects.remove(entry.getKey());
            }
         }
      }

      if (this.effects.containsKey(StatusEffectsEnum.paralysis.getName())) {
         if (!TrinketHelper.AccessoryCheck((EntityLivingBase)this.object, ModItems.trinkets.TrinketArcingOrb)) {
            ((EntityLivingBase)this.object).motionX = 0.0D;
            if (((EntityLivingBase)this.object).motionY > 0.0D) {
               ((EntityLivingBase)this.object).motionY = 0.0D;
            }

            ((EntityLivingBase)this.object).motionZ = 0.0D;
            ((EntityLivingBase)this.object).onGround = false;
         } else {
            this.remove(StatusEffectsEnum.paralysis.getIndex());
         }
      }

      if (this.effects.containsKey(StatusEffectsEnum.bleed.getName())) {
         if (!TrinketHelper.AccessoryCheck((EntityLivingBase)this.object, ModItems.trinkets.TrinketFaelisClaw)) {
            TrinketStatusEffect bleeding = (TrinketStatusEffect)this.effects.get(StatusEffectsEnum.bleed.getName());
            float damage = 0.5F * (float)bleeding.getLevel();
            int duration = bleeding.getDuration();
            if (duration % 20 == 0) {
               ((EntityLivingBase)this.object).attackEntityFrom(DamageSource.MAGIC, damage);
            }
         } else {
            this.remove(StatusEffectsEnum.bleed.getIndex());
         }
      }

      if (this.effects.containsKey(StatusEffectsEnum.Invigorated.getName())) {
         ;
      }

   }

   public void apply(int effectIndex, int dur, int level, Entity source) {
      StatusEffectsEnum effectEnum = StatusEffectsEnum.getStatusByIndex(effectIndex);
      TrinketStatusEffect effect = new TrinketStatusEffect(effectEnum, dur, level, source);
      this.apply(effect);
   }

   public void apply(TrinketStatusEffect effect) {
      if (!this.effects.containsKey(effect.getEffectName())) {
         this.effects.put(effect.getEffectName(), effect);
         if (!((EntityLivingBase)this.object).getEntityWorld().isRemote && this.object instanceof EntityPlayerMP) {
            NetworkHandler.sendTo(new StatusEffectPacket(effect.getSource(), (EntityLivingBase)this.object, effect), (EntityPlayerMP)this.object);
         }
      }

   }

   public void combine(int effectIndex, int dur, int level) {
      StatusEffectsEnum effectEnum = StatusEffectsEnum.getStatusByIndex(effectIndex);
      if (this.effects.containsKey(effectEnum.getName())) {
         TrinketStatusEffect e = (TrinketStatusEffect)this.effects.get(effectEnum.getName());
         e.addDuration(dur);
         e.addLevel(level);
      }

   }

   public void combine(TrinketStatusEffect effect) {
      if (this.effects.containsKey(effect.getEffectName())) {
         TrinketStatusEffect e = (TrinketStatusEffect)this.effects.get(effect.getEffectName());
         e.addDuration(effect.getDuration());
         e.addLevel(effect.getLevel());
      }

   }

   public void remove(int effectIndex) {
      StatusEffectsEnum effect = StatusEffectsEnum.getStatusByIndex(effectIndex);
      if (this.effects.containsKey(effect.getName())) {
         this.effects.remove(effect.getName());
      }

   }

   public void remove(StatusEffectsEnum effect) {
      if (this.effects.containsKey(effect.getName())) {
         this.effects.remove(effect.getName());
      }

   }

   public void removeAll() {
      if (!this.effects.isEmpty()) {
         this.effects.clear();
      }

   }

   public Map<String, TrinketStatusEffect> getActiveEffects() {
      return this.effects;
   }

   public void savedNBTData(NBTTagCompound tag) {
   }

   public void loadNBTData(NBTTagCompound nbt) {
   }
}
