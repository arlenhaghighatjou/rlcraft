package org.jwaresoftware.mcmods.lib;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class MagnifyingPotionEffect extends StubbornPotionEffect {
   private static final int _MAX_AMPLIFIER = 3;

   public MagnifyingPotionEffect(Potion potion, int duration, int amplifier, boolean ambient, boolean particles) {
      super(potion, duration, amplifier, ambient, particles);
   }

   public MagnifyingPotionEffect(Potion potion, int duration, int amplifier) {
      super(potion, duration, amplifier);
   }

   public MagnifyingPotionEffect(StubbornPotionEffect from) {
      super(from);
   }

   public String getType() {
      return "magnifier";
   }

   protected PotionEffect combineInternal(PotionEffect other) {
      PotionEffect out = null;
      if (other.getPotion() == this.getPotion()) {
         int new_amplifier = other.getAmplifier();
         int cur_amplifier = this.getAmplifier();
         if (new_amplifier < 0 || cur_amplifier < 0) {
            return other;
         }

         if (new_amplifier == 0) {
            new_amplifier = Math.min(cur_amplifier + 1, 3);
            out = new PotionEffect(this.getPotion(), this.getDuration(), new_amplifier, other.getIsAmbient(), other.doesShowParticles());
         } else if (new_amplifier > cur_amplifier) {
            new_amplifier = Math.min(cur_amplifier + (new_amplifier - cur_amplifier) + 1, 3);
            int new_duration = Math.min(this._original_duration, other.getDuration() + this.getDuration());
            out = new PotionEffect(this.getPotion(), new_duration, new_amplifier, other.getIsAmbient(), other.doesShowParticles());
         }
      }

      if (out == null) {
         out = super.combineInternal(other);
      }

      return out;
   }
}
