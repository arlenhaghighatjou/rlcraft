package ichttt.mods.firstaid.api.damagesystem;

import net.minecraft.item.ItemStack;

public abstract class AbstractPartHealer {
   public final int maxHeal;
   public final ItemStack stack;
   public final int ticksPerHeal;

   public AbstractPartHealer(int maxHeal, ItemStack stack, int ticksPerHeal) {
      this.maxHeal = maxHeal;
      this.stack = stack;
      this.ticksPerHeal = ticksPerHeal;
   }

   public abstract AbstractPartHealer loadNBT(int var1, int var2);

   public abstract boolean hasFinished();

   public abstract boolean tick();

   public abstract int getTicksPassed();

   public abstract int getHealsDone();
}
