package bettercombat.mod.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;

public class DefaultImplSecondHurtTimer implements ISecondHurtTimer {
   private int hurtTime;

   public DefaultImplSecondHurtTimer() {
   }

   public int getHurtTimerBCM() {
      return this.hurtTime;
   }

   public void setHurtTimerBCM(int hurtTimer) {
      this.hurtTime = hurtTimer;
   }

   public void tick() {
      if (this.hurtTime > 0) {
         --this.hurtTime;
      }

   }

   public boolean attackEntityFromOffhand(Entity target, DamageSource dmgSrc, float amount) {
      if (!target.isEntityInvulnerable(dmgSrc) && this.hurtTime <= 0 && !target.world.isRemote && (target instanceof EntityLivingBase || target instanceof MultiPartEntityPart)) {
         EntityLivingBase targetMain;
         if (target instanceof MultiPartEntityPart) {
            targetMain = (EntityLivingBase)((MultiPartEntityPart)target).parent;
         } else {
            targetMain = (EntityLivingBase)target;
         }

         if (targetMain.getHealth() <= 0.0F) {
            return false;
         } else {
            boolean successfulAttack = false;
            if (this.hurtTime <= 0) {
               Entity trueSrc = dmgSrc.getTrueSource();
               if (trueSrc instanceof EntityPlayer) {
                  EntityPlayer player = (EntityPlayer)trueSrc;
                  ItemStack buf = player.getHeldItemMainhand();
                  player.setHeldItem(EnumHand.MAIN_HAND, player.getHeldItemOffhand());
                  player.setHeldItem(EnumHand.OFF_HAND, buf);
               }

               int mainHurtTime = targetMain.hurtTime;
               int mainHurtResistance = targetMain.hurtResistantTime;
               targetMain.hurtTime = 0;
               targetMain.hurtResistantTime = 0;
               successfulAttack = target.attackEntityFrom(dmgSrc, amount);
               if (successfulAttack) {
                  this.hurtTime = 10;
               }

               targetMain.hurtTime = mainHurtTime;
               targetMain.hurtResistantTime = mainHurtResistance;
               if (trueSrc instanceof EntityPlayer) {
                  EntityPlayer player = (EntityPlayer)trueSrc;
                  ItemStack buf = player.getHeldItemOffhand();
                  player.setHeldItem(EnumHand.OFF_HAND, player.getHeldItemMainhand());
                  player.setHeldItem(EnumHand.MAIN_HAND, buf);
               }
            }

            return successfulAttack;
         }
      } else {
         return false;
      }
   }
}
