package dev.satyrn.wolfarmor.common.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PotionEffectEvent extends Event {
   private final Potion potion;
   private final EntityLivingBase entity;
   private final int amplifier;

   public PotionEffectEvent(Potion potion, EntityLivingBase entity, int amplifier) {
      this.potion = potion;
      this.entity = entity;
      this.amplifier = amplifier;
   }

   public Potion getPotion() {
      return this.potion;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isCancelable() {
      return true;
   }
}
