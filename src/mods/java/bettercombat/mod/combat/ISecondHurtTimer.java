package bettercombat.mod.combat;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public interface ISecondHurtTimer {
   int getHurtTimerBCM();

   void setHurtTimerBCM(int var1);

   void tick();

   boolean attackEntityFromOffhand(Entity var1, DamageSource var2, float var3);
}
