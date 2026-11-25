package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityBelph extends TameableCreatureEntity implements IMob {
   protected static final DataParameter<Integer> HELLFIRE_ENERGY = EntityDataManager.createKey(EntityBelph.class, DataSerializers.VARINT);
   public int hellfireEnergy = 0;
   public List<EntityHellfireOrb> hellfireOrbs = new ArrayList();

   public EntityBelph(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F).setChaseTime(-1));
      if (this.getNavigator() instanceof PathNavigateGround) {
         PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
         pathNavigateGround.setBreakDoors(true);
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(HELLFIRE_ENERGY, Integer.valueOf(this.hellfireEnergy));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(HELLFIRE_ENERGY, Integer.valueOf(this.hellfireEnergy));
      } else {
         try {
            this.hellfireEnergy = ((Integer)this.dataManager.get(HELLFIRE_ENERGY)).intValue();
         } catch (Exception var2) {
            ;
         }
      }

      if (this.getEntityWorld().isRemote && this.hellfireEnergy > 0) {
         EntityRahovart.updateHellfireOrbs(this, this.updateTick, 3, this.hellfireEnergy, 0.5F, this.hellfireOrbs);
      }

   }

   public boolean canAttackEntity(EntityLivingBase target) {
      if (this.isTamed()) {
         return super.canAttackEntity(target);
      } else {
         return target instanceof EntityBehemoth ? false : super.canAttackEntity(target);
      }
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("doomfireball", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      super.attackRanged(target, range);
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

   public boolean canBurn() {
      return false;
   }
}
