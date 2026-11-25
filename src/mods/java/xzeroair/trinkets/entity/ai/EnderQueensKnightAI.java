package xzeroair.trinkets.entity.ai;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.init.ModItems;

public class EnderQueensKnightAI extends EntityAITarget {
   EntityPlayer queen;
   EntityEnderman knight;
   EntityLivingBase attacker;
   private int timestamp;

   public EnderQueensKnightAI(EntityEnderman theDefendingKnightIn) {
      super(theDefendingKnightIn, false);
      this.knight = theDefendingKnightIn;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      AxisAlignedBB bBox = this.knight.getEntityBoundingBox().grow(16.0D, 4.0D, 16.0D);
      List<EntityPlayer> entLivList = this.knight.getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, bBox);
      if (!entLivList.isEmpty()) {
         for(EntityPlayer stuff : entLivList) {
            boolean ability = TrinketHelper.entityHasAbility("ender_queen".toString(), stuff);
            if (TrinketHelper.AccessoryCheck(stuff, ModItems.trinkets.TrinketEnderTiara) || ability) {
               this.queen = stuff;
               break;
            }

            this.queen = null;
         }
      }

      if (this.queen == null) {
         return false;
      } else {
         this.attacker = this.queen.getRevengeTarget();
         int i = this.queen.getRevengeTimer();
         return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
      }
   }

   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.attacker);
      EntityLivingBase entitylivingbase = this.queen;
      if (entitylivingbase != null) {
         this.timestamp = entitylivingbase.getRevengeTimer();
      }

      super.startExecuting();
   }
}
