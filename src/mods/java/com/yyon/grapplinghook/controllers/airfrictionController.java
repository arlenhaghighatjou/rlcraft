package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class airfrictionController extends grappleController {
   public final double playermovementmult = 0.5D;
   public int ignoregroundcounter = 0;
   public boolean was_sliding = false;
   public boolean was_wallrunning = false;
   public boolean was_rocket = false;
   public boolean first_tick_since_created = true;

   public airfrictionController(int arrowId, int entityId, World world, vec pos, int id, GrappleCustomization custom) {
      super(arrowId, entityId, world, pos, id, custom);
   }

   public void updatePlayerPos() {
      Entity entity = this.entity;
      if (entity != null) {
         if (entity.isRiding()) {
            this.unattach();
            this.updateServerPos();
         } else {
            vec additionalmotion = new vec(0.0D, 0.0D, 0.0D);
            if (GrappleConfig.getconf().dont_override_movement_in_air && !entity.onGround && !this.was_sliding && !this.was_wallrunning && !this.was_rocket && !this.first_tick_since_created) {
               this.motion = vec.motionvec(entity);
               this.unattach();
            } else {
               if (this.attached) {
                  boolean issliding = grapplemod.proxy.issliding(entity, this.motion);
                  if (issliding && !this.was_sliding) {
                     this.playSlideSound();
                  }

                  if (this.ignoregroundcounter <= 0) {
                     this.normalGround(issliding);
                     this.normalCollisions(issliding);
                  }

                  this.applyAirFriction();
                  if (this.entity.isInWater() || this.entity.isInLava()) {
                     this.unattach();
                     return;
                  }

                  if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isOnLadder()) {
                     this.unattach();
                     return;
                  }

                  boolean doesrocket = false;
                  if (this.custom != null && this.custom.rocket) {
                     vec rocket = this.rocket(entity);
                     this.motion.add_ip(rocket);
                     if (rocket.length() > 0.0D) {
                        doesrocket = true;
                     }
                  }

                  if (issliding) {
                     this.applySlidingFriction();
                  }

                  boolean wallrun = this.applywallrun();
                  if (!issliding && !this.was_sliding) {
                     if (!wallrun) {
                        double max_motion = GrappleConfig.getconf().airstrafe_max_speed;
                        double accel = GrappleConfig.getconf().airstrafe_acceleration;
                        vec motion_horizontal = this.motion.removealong(new vec(0.0D, 1.0D, 0.0D));
                        double prev_motion = motion_horizontal.length();
                        vec new_motion_horizontal = motion_horizontal.add(this.playermovement.changelen(accel));
                        double angle = motion_horizontal.angle(new_motion_horizontal);
                        if (new_motion_horizontal.length() > max_motion && new_motion_horizontal.length() > prev_motion) {
                           double ninety_deg = 1.5707963267948966D;
                           double new_max_motion = max_motion;
                           if (angle < ninety_deg && prev_motion > max_motion) {
                              new_max_motion = prev_motion + (max_motion - prev_motion) * (angle / 1.5707963267948966D);
                           }

                           new_motion_horizontal.changelen_ip(new_max_motion);
                        }

                        this.motion.x = new_motion_horizontal.x;
                        this.motion.z = new_motion_horizontal.z;
                     } else {
                        this.motion = this.motion.removealong(new vec(0.0D, 1.0D, 0.0D));
                        if (this.walldirection != null) {
                           this.motion = this.motion.removealong(this.walldirection);
                        }

                        vec new_movement = this.playermovement.changelen(GrappleConfig.getconf().wallrun_speed * 1.5D);
                        if (this.walldirection != null) {
                           new_movement = new_movement.removealong(this.walldirection);
                        }

                        if (new_movement.length() > GrappleConfig.getconf().wallrun_speed) {
                           new_movement.changelen_ip(GrappleConfig.getconf().wallrun_speed);
                        }

                        vec current_motion_along = this.motion.removealong(new vec(0.0D, 1.0D, 0.0D));
                        vec new_motion_along = this.motion.add(new_movement).removealong(new vec(0.0D, 1.0D, 0.0D));
                        if (this.walldirection != null) {
                           current_motion_along = current_motion_along.removealong(this.walldirection);
                           new_motion_along = new_motion_along.removealong(this.walldirection);
                        }

                        if (current_motion_along.length() <= GrappleConfig.getconf().wallrun_max_speed || current_motion_along.dot(new_movement) < 0.0D) {
                           this.motion.add_ip(new_movement);
                           if (new_motion_along.length() > GrappleConfig.getconf().wallrun_max_speed) {
                              this.motion.changelen_ip(GrappleConfig.getconf().wallrun_max_speed);
                           }
                        }

                        additionalmotion.add_ip(this.wallrun_press_against_wall());
                     }
                  }

                  if (entity instanceof EntityLivingBase) {
                     EntityLivingBase entityliving = (EntityLivingBase)entity;
                     if (entityliving.isElytraFlying()) {
                        this.unattach();
                     }
                  }

                  vec gravity = new vec(0.0D, -0.05D, 0.0D);
                  if (!wallrun) {
                     this.motion.add_ip(gravity);
                  }

                  vec newmotion = this.motion.add(additionalmotion);
                  entity.motionX = newmotion.x;
                  entity.motionY = newmotion.y;
                  entity.motionZ = newmotion.z;
                  this.updateServerPos();
                  if (entity.onGround && !issliding && !wallrun) {
                     if (!doesrocket) {
                        if (this.ignoregroundcounter <= 0) {
                           this.unattach();
                        }
                     } else {
                        this.motion = vec.motionvec(entity);
                     }
                  }

                  if (this.ignoregroundcounter > 0) {
                     --this.ignoregroundcounter;
                  }

                  this.was_sliding = issliding;
                  this.was_wallrunning = wallrun;
                  this.was_rocket = doesrocket;
                  this.first_tick_since_created = false;
               }

            }
         }
      }
   }

   public void receiveEnderLaunch(double x, double y, double z) {
      super.receiveEnderLaunch(x, y, z);
      this.ignoregroundcounter = 2;
   }

   public void slidingJump() {
      super.slidingJump();
      this.ignoregroundcounter = 2;
   }

   public void playSlideSound() {
      grapplemod.proxy.playSlideSound(this.entity);
   }
}
