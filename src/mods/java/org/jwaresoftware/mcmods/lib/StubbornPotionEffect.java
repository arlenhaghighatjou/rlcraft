package org.jwaresoftware.mcmods.lib;

import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class StubbornPotionEffect extends PotionEffect {
   static final int _MAX_EXTENDED_DURATION = 5 * SharedGlue.DEFAULT_TICKS_PER_FULL_DAYNIGHT_CYCLE();
   protected final int _original_duration;

   public static final StubbornPotionEffect from(@Nonnull PotionEffect e) {
      return new StubbornPotionEffect(e.getPotion(), e.getDuration(), e.getAmplifier(), e.getIsAmbient(), e.doesShowParticles());
   }

   public StubbornPotionEffect(Potion potion, int duration, int amplifier, boolean ambient, boolean particles) {
      super(potion, duration, amplifier, ambient, particles);
      this._original_duration = duration;
   }

   public StubbornPotionEffect(Potion potion, int duration, int amplifier) {
      this(potion, duration, amplifier, false, true);
   }

   public StubbornPotionEffect(StubbornPotionEffect from) {
      super(from);
      this._original_duration = from._original_duration;
   }

   public String getType() {
      return "stubborn";
   }

   public List<ItemStack> getCurativeItems() {
      List<ItemStack> l = super.getCurativeItems();
      ListIterator<ItemStack> litr = l.listIterator();

      while(litr.hasNext()) {
         ItemStack stack = (ItemStack)litr.next();
         if (Potions.isMilk(stack, true)) {
            litr.remove();
            break;
         }
      }

      return l;
   }

   protected PotionEffect combineInternal(PotionEffect other) {
      if (other.getPotion() == this.getPotion()) {
         int new_amplifier = other.getAmplifier();
         int cur_amplifier = this.getAmplifier();
         if (new_amplifier < 0 || cur_amplifier < 0 || new_amplifier > cur_amplifier) {
            return other;
         }

         int cur_duration = this.getDuration();
         boolean check_duration = cur_duration < _MAX_EXTENDED_DURATION;
         int new_duration;
         if (new_amplifier == cur_amplifier) {
            new_duration = cur_duration + other.getDuration();
         } else {
            float percent = ((float)new_amplifier + 1.0F) / ((float)cur_amplifier + 1.0F) + 0.2F;
            new_duration = cur_duration + (int)(percent * (float)other.getDuration());
         }

         if (check_duration && new_duration > _MAX_EXTENDED_DURATION) {
            new_duration = _MAX_EXTENDED_DURATION;
         }

         if (new_duration > cur_duration) {
            other = new PotionEffect(this.getPotion(), new_duration, cur_amplifier, other.getIsAmbient(), other.doesShowParticles());
         }
      }

      return other;
   }

   public final void combine(PotionEffect other) {
      if (!Potions.isLikelyForever(other)) {
         other = this.combineInternal(other);
      }

      super.combine(other);
   }

   public final int getOriginalDuration() {
      return this._original_duration;
   }
}
