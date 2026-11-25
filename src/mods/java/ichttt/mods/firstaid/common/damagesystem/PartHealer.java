package ichttt.mods.firstaid.common.damagesystem;

import ichttt.mods.firstaid.api.damagesystem.AbstractPartHealer;
import net.minecraft.item.ItemStack;

public class PartHealer extends AbstractPartHealer {
   private int ticksPassed = 0;
   private int heals = 0;

   public PartHealer(int ticksPerHeal, int maxHeal, ItemStack stack) {
      super(maxHeal, stack, ticksPerHeal);
   }

   public AbstractPartHealer loadNBT(int ticksPassed, int heals) {
      this.ticksPassed = ticksPassed;
      this.heals = heals;
      return this;
   }

   public boolean hasFinished() {
      return this.heals >= this.maxHeal;
   }

   public boolean tick() {
      if (this.hasFinished()) {
         return false;
      } else {
         ++this.ticksPassed;
         boolean nextHeal = this.ticksPassed >= this.ticksPerHeal;
         if (nextHeal) {
            this.ticksPassed = 0;
            ++this.heals;
         }

         return nextHeal;
      }
   }

   public int getTicksPassed() {
      return this.ticksPassed;
   }

   public int getHealsDone() {
      return this.heals;
   }
}
