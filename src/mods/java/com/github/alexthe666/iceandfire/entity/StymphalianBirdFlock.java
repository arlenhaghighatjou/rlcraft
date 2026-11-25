package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.StymphalianBirdAIAirTarget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class StymphalianBirdFlock {
   private EntityStymphalianBird leader;
   private ArrayList<EntityStymphalianBird> members = new ArrayList();
   private BlockPos leaderTarget;
   private BlockPos prevLeaderTarget;
   private EaglercraftRandom random;
   private int distance = 15;

   private StymphalianBirdFlock() {
   }

   public static StymphalianBirdFlock createFlock(EntityStymphalianBird bird) {
      StymphalianBirdFlock flock = new StymphalianBirdFlock();
      flock.leader = bird;
      flock.members = new ArrayList();
      flock.members.add(bird);
      flock.leaderTarget = bird.airTarget;
      flock.random = bird.getRNG();
      return flock;
   }

   public boolean isLeader(EntityStymphalianBird bird) {
      return this.leader != null && this.leader == bird;
   }

   @Nullable
   public static StymphalianBirdFlock getNearbyFlock(EntityStymphalianBird bird) {
      float d0 = (float)IceAndFire.CONFIG.stymphalianBirdFlockLength;
      List<Entity> list = bird.world.getEntitiesInAABBexcluding(bird, (new AxisAlignedBB(bird.posX, bird.posY, bird.posZ, bird.posX + 1.0D, bird.posY + 1.0D, bird.posZ + 1.0D)).grow((double)d0, 10.0D, (double)d0), EntityStymphalianBird.STYMPHALIAN_PREDICATE);
      Collections.sort(list, new Sorter(bird));
      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (entity instanceof EntityStymphalianBird) {
               EntityStymphalianBird other = (EntityStymphalianBird)entity;
               if (other.flock != null) {
                  return other.flock;
               }
            }
         }
      }

      return null;
   }

   public void addToFlock(EntityStymphalianBird bird) {
      this.members.add(bird);
   }

   public void update() {
      if (!this.members.isEmpty() && (this.leader == null || this.leader.isDead)) {
         this.leader = (EntityStymphalianBird)this.members.get(this.random.nextInt(this.members.size()));
      }

      if (this.leader != null && !this.leader.isDead) {
         this.prevLeaderTarget = this.leaderTarget;
         this.leaderTarget = this.leader.airTarget;
      }

   }

   public void onLeaderAttack(EntityLivingBase attackTarget) {
      for(EntityStymphalianBird bird : this.members) {
         if (bird.getAttackTarget() == null && !this.isLeader(bird)) {
            bird.setAttackTarget(attackTarget);
         }
      }

   }

   public EntityStymphalianBird getLeader() {
      return this.leader;
   }

   public void setTarget(BlockPos target) {
      this.leaderTarget = target;

      for(EntityStymphalianBird bird : this.members) {
         if (!this.isLeader(bird)) {
            bird.airTarget = StymphalianBirdAIAirTarget.getNearbyAirTarget(bird);
         }
      }

   }

   public void setFlying(boolean flying) {
      for(EntityStymphalianBird bird : this.members) {
         if (!this.isLeader(bird)) {
            bird.setFlying(flying);
         }
      }

   }

   public void setFearTarget(EntityLivingBase living) {
      for(EntityStymphalianBird bird : this.members) {
         bird.setVictor(living);
      }

   }
}
