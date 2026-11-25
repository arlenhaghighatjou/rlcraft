package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.GetBlockGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.GetItemGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityKobold extends TameableCreatureEntity implements IMob {
   public boolean griefing = true;
   public boolean theivery = true;
   private int torchLootingTime = 20;

   public EntityKobold(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.spreadFire = false;
      this.canGrow = false;
      this.babySpawnChance = 0.1D;
      this.setupMob();
   }

   protected void initEntityAI() {
      this.tasks.addTask(this.nextIdleGoalIndex++, (new GetItemGoal(this)).setDistanceMax(8.0D).setSpeed(1.2D));
      if (this.griefing) {
         this.tasks.addTask(this.nextIdleGoalIndex++, (new GetBlockGoal(this)).setDistanceMax(8).setSpeed(1.2D).setBlockName("torch").setTamedLooting(false));
      }

      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPlayer.class).setLongMemory(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void loadCreatureFlags() {
      this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
      this.theivery = this.creatureInfo.getFlag("theivery", this.theivery);
   }

   protected void despawnEntity() {
      super.despawnEntity();
      if (this.isDead && !this.isTamed() && this.inventory.hasBagItems()) {
         this.inventory.dropInventory();
      }

   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.griefing && this.torchLootingTime-- <= 0) {
         this.torchLootingTime = 60;
         int distance = 2;
         String targetName = "torch";
         new ArrayList();

         for(int x = (int)this.posX - distance; x < (int)this.posX + distance; ++x) {
            for(int y = (int)this.posY - distance; y < (int)this.posY + distance; ++y) {
               for(int z = (int)this.posZ - distance; z < (int)this.posZ + distance; ++z) {
                  BlockPos pos = new BlockPos(x, y, z);
                  Block searchBlock = this.getEntityWorld().getBlockState(pos).getBlock();
                  if (searchBlock != Blocks.AIR) {
                     BlockPos possibleTarget = null;
                     if (ObjectLists.isName(searchBlock, targetName)) {
                        this.getEntityWorld().destroyBlock(pos, true);
                        break;
                     }
                  }
               }
            }
         }
      }

   }

   public boolean shouldCreatureGroupRevenge(EntityLivingBase target) {
      return target instanceof EntityPlayer && target.getHealth() / target.getMaxHealth() <= 0.5F ? true : super.shouldCreatureGroupRevenge(target);
   }

   public boolean shouldCreatureGroupHunt(EntityLivingBase target) {
      return target instanceof EntityPlayer && target.getHealth() / target.getMaxHealth() <= 0.5F ? true : super.shouldCreatureGroupHunt(target);
   }

   public boolean shouldCreatureGroupFlee(EntityLivingBase target) {
      return target instanceof EntityPlayer && target.getHealth() / target.getMaxHealth() <= 0.5F ? false : super.shouldCreatureGroupFlee(target);
   }

   public boolean canAttackEntity(EntityLivingBase targetEntity) {
      return !this.isTamed() && targetEntity.getHealth() / targetEntity.getMaxHealth() > 0.5F ? false : super.canAttackEntity(targetEntity);
   }

   public boolean canPickupItems() {
      return this.theivery;
   }
}
