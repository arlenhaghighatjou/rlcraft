package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class EntityTarantula extends TameableCreatureEntity implements IMob {
   public EntityTarantula(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.getEntityWorld().isRemote) {
         if (this.hasAttackTarget()) {
            if (this.rand.nextInt(10) == 0) {
               this.leap(6.0F, 0.6D, this.getAttackTarget());
            }
         } else if (this.rand.nextInt(50) == 0 && this.isMoving()) {
            this.leap(1.0D, 1.0D);
         }
      }

      if (!this.getEntityWorld().isRemote && (this.ticksExisted % 10 == 0 || this.isMoving() && this.ticksExisted % 5 == 0)) {
         int trailHeight = 1;

         for(int y = 0; y < trailHeight; ++y) {
            Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
            if (block == Blocks.AIR || block == Blocks.SNOW || block == ObjectManager.getBlock("quickweb")) {
               this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), ObjectManager.getBlock("quickweb").getDefaultState());
            }
         }
      }

   }

   public boolean canClimb() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public float getFallResistance() {
      return 100.0F;
   }

   public boolean webProof() {
      return true;
   }
}
