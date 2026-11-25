package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCrusk extends TameableCreatureEntity implements IGroupHeavy {
   public EntityCrusk(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.25D;
      this.growthTime = -120000;
      this.trueSight = true;
      this.setupMob();
      this.hitAreaWidthScale = 1.5F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextPriorityGoalIndex++, (new StealthGoal(this)).setStealthTime(60));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPlayer.class).setLongMemory(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public boolean canStealth() {
      if (this.isTamed() && this.isSitting()) {
         return false;
      } else {
         IBlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0));
         if (blockState.getBlock() != Blocks.AIR) {
            if (blockState.getMaterial() == Material.GROUND) {
               return true;
            }

            if (blockState.getMaterial() == Material.GRASS) {
               return true;
            }

            if (blockState.getMaterial() == Material.LEAVES) {
               return true;
            }

            if (blockState.getMaterial() == Material.SAND) {
               return true;
            }

            if (blockState.getMaterial() == Material.CLAY) {
               return true;
            }

            if (blockState.getMaterial() == Material.SNOW) {
               return true;
            }

            if (blockState.getMaterial() == Material.CRAFTED_SNOW) {
               return true;
            }
         }

         return blockState.getBlock() == Blocks.NETHERRACK;
      }
   }

   public boolean canBeTempted() {
      return this.isChild();
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

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      if (type.equals("cactus")) {
         return false;
      } else {
         return type.equals("inWall") ? false : super.isDamageTypeApplicable(type, source, damage);
      }
   }
}
