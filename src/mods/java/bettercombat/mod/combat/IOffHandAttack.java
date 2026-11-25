package bettercombat.mod.combat;

import net.minecraft.entity.player.EntityPlayer;

public interface IOffHandAttack {
   int getOffhandCooldown();

   void setOffhandCooldown(int var1);

   void tick();

   void swingOffHand(EntityPlayer var1);
}
