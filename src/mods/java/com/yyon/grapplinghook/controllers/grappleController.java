package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.ClientProxyClass;
import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class grappleController {
   public int entityId;
   public World world;
   public Entity entity;
   public HashSet<grappleArrow> arrows = new HashSet();
   public HashSet<Integer> arrowIds = new HashSet();
   public boolean attached = true;
   public vec motion;
   public double playerforward = 0.0D;
   public double playerstrafe = 0.0D;
   public boolean playerjump = false;
   public boolean playersneak = false;
   public vec playermovement_unrotated = new vec(0.0D, 0.0D, 0.0D);
   public vec playermovement = new vec(0.0D, 0.0D, 0.0D);
   public int ongroundtimer = 0;
   public int maxongroundtimer = 3;
   public double maxlen;
   public double playermovementmult = 0.0D;
   public int controllerid;
   public GrappleCustomization custom = null;
   boolean prevonground = false;
   public double repelmaxpush = 0.3D;
   public boolean rocket_key = false;
   public double rocket_on = 0.0D;
   boolean isonwall = false;
   vec walldirection = null;
   RayTraceResult wallrun_raytrace_result = null;
   int ticks_since_last_wallrun_sound_effect = 0;

   public grappleController(int arrowId, int entityId, World world, vec pos, int controllerid, GrappleCustomization custom) {
      this.entityId = entityId;
      this.world = world;
      this.custom = custom;
      if (this.custom != null) {
         this.playermovementmult = this.custom.playermovementmult;
         this.maxlen = custom.maxlen;
      }

      this.controllerid = controllerid;
      this.entity = world.getEntityByID(entityId);
      this.motion = vec.motionvec(this.entity);
      vec newmotion = new vec(this.entity.posX - this.entity.prevPosX, this.entity.posY - this.entity.prevPosY, this.entity.posZ - this.entity.prevPosZ);
      if (newmotion.x / this.motion.x < 2.0D && this.motion.x / newmotion.x < 2.0D && newmotion.y / this.motion.y < 2.0D && this.motion.y / newmotion.y < 2.0D && newmotion.z / this.motion.z < 2.0D && this.motion.z / newmotion.z < 2.0D) {
         this.motion = newmotion;
      }

      this.ongroundtimer = 0;
      grapplemod.registerController(this.entityId, this);
      if (arrowId != -1) {
         Entity arrowentity = world.getEntityByID(arrowId);
         if (arrowentity != null && !arrowentity.isDead && arrowentity instanceof grappleArrow) {
            this.addArrow((grappleArrow)arrowentity);
         } else {
            this.unattach();
         }
      }

      if (custom != null && custom.rocket) {
         grapplemod.proxy.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
      }

   }

   public void unattach() {
      if (grapplemod.controllers.containsValue(this)) {
         this.attached = false;
         grapplemod.unregisterController(this.entityId);
         if (this.controllerid != grapplemod.AIRID) {
            grapplemod.network.sendToServer(new GrappleEndMessage(this.entityId, this.arrowIds));
            grapplemod.proxy.createControl(grapplemod.AIRID, -1, this.entityId, this.entity.world, new vec(0.0D, 0.0D, 0.0D), (BlockPos)null, this.custom);
         }
      }

   }

   public void doClientTick() {
      if (this.attached) {
         if (this.entity != null && !this.entity.isDead) {
            this.updatePlayerPos();
         } else {
            this.unattach();
         }
      }

   }

   public void receivePlayerMovementMessage(float strafe, float forward, boolean jump, boolean sneak) {
      this.playerforward = (double)forward;
      this.playerstrafe = (double)strafe;
      this.playersneak = sneak;
      this.playermovement_unrotated = new vec((double)strafe, 0.0D, (double)forward);
      this.playermovement = this.playermovement_unrotated.rotate_yaw((double)((float)((double)this.entity.rotationYaw * 0.017453292519943295D)));
   }

   public void updatePlayerPos() {
      Entity entity = this.entity;
      if (this.attached && entity != null) {
         if (entity.isRiding()) {
            this.unattach();
            this.updateServerPos();
            return;
         }

         this.normalGround(false);
         this.normalCollisions(false);
         this.applyAirFriction();
         vec playerpos = vec.positionvec(entity);
         playerpos = playerpos.add(new vec(0.0D, (double)entity.getEyeHeight(), 0.0D));
         vec additionalmotion = new vec(0.0D, 0.0D, 0.0D);
         vec gravity = new vec(0.0D, -0.05D, 0.0D);
         this.motion.add_ip(gravity);
         boolean doJump = false;
         double jumpSpeed = 0.0D;
         boolean isClimbing = false;
         boolean motor = false;
         if (this.custom.motor) {
            if (ClientProxyClass.key_motoronoff.isKeyDown() && this.custom.motorwhencrouching) {
               motor = true;
            } else if (!ClientProxyClass.key_motoronoff.isKeyDown() && this.custom.motorwhennotcrouching) {
               motor = true;
            }
         }

         boolean close = false;
         vec averagemotiontowards = new vec(0.0D, 0.0D, 0.0D);
         double min_spherevec_dist = 99999.0D;

         for(grappleArrow arrow : this.arrows) {
            vec arrowpos = vec.positionvec(arrow);
            if (this.custom.phaserope) {
               arrow.segmenthandler.updatepos(arrowpos, playerpos, arrow.r);
            } else {
               arrow.segmenthandler.update(arrowpos, playerpos, arrow.r, false);
            }

            vec anchor = arrow.segmenthandler.getclosest(arrowpos);
            double distToAnchor = arrow.segmenthandler.getDistToAnchor();
            double remaininglength = motor ? Math.max(this.custom.maxlen, arrow.r) - distToAnchor : arrow.r - distToAnchor;
            vec oldspherevec = playerpos.sub(anchor);
            vec spherevec = oldspherevec.changelen(remaininglength);
            vec spherechange = spherevec.sub(oldspherevec);
            if (spherevec.length() < min_spherevec_dist) {
               min_spherevec_dist = spherevec.length();
            }

            averagemotiontowards.add_ip(spherevec.changelen(-1.0D));
            if (motor) {
               arrow.r = distToAnchor + oldspherevec.length();
            }

            if (oldspherevec.length() >= remaininglength) {
               if (oldspherevec.length() - remaininglength > GrappleConfig.getconf().rope_snap_buffer) {
                  this.unattach();
                  this.updateServerPos();
                  return;
               }

               additionalmotion = spherechange;
            }

            double dist = oldspherevec.length();
            this.calctaut(dist, arrow);
            if (entity instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)entity;
               boolean isjumping = ClientProxyClass.key_jumpanddetach.isKeyDown();
               isjumping = isjumping && !this.playerjump;
               this.playerjump = ClientProxyClass.key_jumpanddetach.isKeyDown();
               if (isjumping && this.ongroundtimer <= 0) {
                  double timer = (double)(this.entity.world.getTotalWorldTime() - ClientProxyClass.prev_rope_jump_time);
                  if (timer > GrappleConfig.getconf().rope_jump_cooldown_s * 20.0D) {
                     doJump = true;
                     jumpSpeed = this.getJumpPower(player, spherevec, arrow);
                  }
               }

               if (ClientProxyClass.key_slow.isKeyDown()) {
                  vec motiontorwards = spherevec.changelen(-0.1D);
                  motiontorwards = new vec(motiontorwards.x, 0.0D, motiontorwards.z);
                  if (this.motion.dot(motiontorwards) < 0.0D) {
                     this.motion.add_ip(motiontorwards);
                  }

                  vec newmotion = this.dampenmotion(this.motion, motiontorwards);
                  this.motion = new vec(newmotion.x, this.motion.y, newmotion.z);
               }

               if ((ClientProxyClass.key_climb.isKeyDown() || ClientProxyClass.key_climbup.isKeyDown() || ClientProxyClass.key_climbdown.isKeyDown()) && !motor) {
                  isClimbing = true;
                  if (anchor.y > playerpos.y) {
                     double climbup = 0.0D;
                     if (ClientProxyClass.key_climb.isKeyDown()) {
                        climbup = this.playerforward;
                        if (this.playersneak) {
                           climbup /= 0.3D;
                        }

                        if (climbup > 1.0D) {
                           climbup = 1.0D;
                        } else if (climbup < -1.0D) {
                           climbup = -1.0D;
                        }
                     } else if (ClientProxyClass.key_climbup.isKeyDown()) {
                        climbup = 1.0D;
                     } else if (ClientProxyClass.key_climbdown.isKeyDown()) {
                        climbup = -1.0D;
                     }

                     if (climbup != 0.0D && (dist + distToAnchor < this.maxlen || climbup > 0.0D || this.maxlen == 0.0D)) {
                        arrow.r = dist + distToAnchor;
                        arrow.r -= climbup * GrappleConfig.getconf().climb_speed;
                        if (arrow.r < distToAnchor) {
                           arrow.r = dist + distToAnchor;
                        }

                        vec additionalmovementdown = spherevec.changelen(-climbup * GrappleConfig.getconf().climb_speed).proj(new vec(0.0D, 1.0D, 0.0D));
                        if (additionalmovementdown.y < 0.0D) {
                           additionalmotion.add_ip(additionalmovementdown);
                        }
                     }
                  }
               }
            }

            if (dist + distToAnchor < 2.0D) {
               close = true;
            }

            if (anchor.sub(playerpos.add(this.motion)).length() > remaininglength) {
               this.motion = this.motion.removealong(spherevec);
            }
         }

         averagemotiontowards.changelen_ip(1.0D);
         vec facing = (new vec(entity.getLookVec())).normalize();
         if (motor) {
            boolean dopull = true;
            if (this.custom.doublehook && this.arrows.size() == 1) {
               boolean isdouble = true;

               for(grappleArrow arrow : this.arrows) {
                  if (!arrow.isdouble) {
                     isdouble = false;
                  }
               }

               if (isdouble && !this.custom.oneropepull) {
                  dopull = false;
               }
            }

            vec totalpull = new vec(0.0D, 0.0D, 0.0D);
            double accel = this.custom.motoracceleration / (double)this.arrows.size();
            double minabssidewayspull = 999.0D;
            boolean firstpull = true;
            boolean pullispositive = true;
            boolean pullissameway = true;

            for(grappleArrow arrow : this.arrows) {
               vec arrowpos = vec.positionvec(arrow);
               vec anchor = arrow.segmenthandler.getclosest(arrowpos);
               vec spherevec = playerpos.sub(anchor);
               vec pull = spherevec.mult(-1.0D);
               arrow.pull = accel;
               totalpull.add_ip(pull.changelen(accel));
               pull.changelen_ip(arrow.pull);
               if ((pull.dot(facing) > 0.0D || this.custom.pullbackwards) && this.custom.smartdoublemotor && this.arrows.size() > 1) {
                  vec facingxy = new vec(facing.x, 0.0D, facing.z);
                  vec facingside = facingxy.cross(new vec(0.0D, 1.0D, 0.0D)).normalize();
                  vec sideways = pull.proj(facingside);
                  vec currentsideways = this.motion.proj(facingside);
                  sideways.add_ip(currentsideways);
                  double sidewayspull = sideways.dot(facingside);
                  if (Math.abs(sidewayspull) < minabssidewayspull) {
                     minabssidewayspull = Math.abs(sidewayspull);
                  }

                  if (firstpull) {
                     firstpull = false;
                     pullispositive = sidewayspull >= 0.0D;
                  } else if (pullispositive != sidewayspull >= 0.0D) {
                     pullissameway = false;
                  }
               }
            }

            if (this.custom.smartdoublemotor && this.arrows.size() > 1) {
               totalpull = new vec(0.0D, 0.0D, 0.0D);

               for(grappleArrow arrow : this.arrows) {
                  vec arrowpos = vec.positionvec(arrow);
                  vec anchor = arrow.segmenthandler.getclosest(arrowpos);
                  vec spherevec = playerpos.sub(anchor);
                  vec pull = spherevec.mult(-1.0D);
                  pull.changelen_ip(arrow.pull);
                  if (pull.dot(facing) <= 0.0D && !this.custom.pullbackwards) {
                     if (arrow.isdouble && !this.custom.oneropepull) {
                        dopull = false;
                     }
                  } else {
                     vec facingxy = new vec(facing.x, 0.0D, facing.z);
                     vec facingside = facingxy.cross(new vec(0.0D, 1.0D, 0.0D)).normalize();
                     vec sideways = pull.proj(facingside);
                     vec currentsideways = this.motion.proj(facingside);
                     sideways.add_ip(currentsideways);
                     double sidewayspull = sideways.dot(facingside);
                     if (pullissameway) {
                        if (Math.abs(sidewayspull) > minabssidewayspull + 0.05D) {
                           arrow.pull = 0.0D;
                        }
                     } else {
                        arrow.pull = arrow.pull * minabssidewayspull / Math.abs(sidewayspull);
                     }

                     totalpull.add_ip(pull.changelen(arrow.pull));
                  }
               }
            }

            double pullmult = 1.0D;
            if (this.custom.smartmotor && totalpull.y > 0.0D && this.ongroundtimer <= 0 && !entity.onGround) {
               vec pullxzvector = new vec(totalpull.x, 0.0D, totalpull.z);
               double pullxz = pullxzvector.length();
               double motionxz = this.motion.proj(pullxzvector).dot(pullxzvector.normalize());
               double facingxz = facing.proj(pullxzvector).dot(pullxzvector.normalize());
               pullmult = (facingxz * (this.motion.y + gravity.y) - motionxz * facing.y) / (facing.y * pullxz - facingxz * totalpull.y);
               if (facing.y * pullxz - facingxz * totalpull.y == 0.0D) {
                  pullmult = 9999.0D;
               }

               double pulll = pullmult * totalpull.length();
               if (pulll > this.custom.motoracceleration) {
                  pulll = this.custom.motoracceleration;
               }

               if (pulll < 0.0D) {
                  pulll = 0.0D;
               }

               pullmult = pulll / totalpull.length();
            }

            if (this.motion.dot(totalpull) > 0.0D && this.motion.proj(totalpull).length() + totalpull.mult(pullmult).length() > this.custom.motormaxspeed) {
               pullmult = (this.custom.motormaxspeed - this.motion.proj(totalpull).length()) / totalpull.length();
               if (pullmult < 0.0D) {
                  pullmult = 0.0D;
               }
            }

            if (this.custom.motordampener && totalpull.length() != 0.0D) {
               this.motion = this.dampenmotion(this.motion, totalpull);
            }

            if (dopull) {
               for(grappleArrow arrow : this.arrows) {
                  vec arrowpos = vec.positionvec(arrow);
                  vec anchor = arrow.segmenthandler.getclosest(arrowpos);
                  vec spherevec = playerpos.sub(anchor);
                  vec pull = spherevec.mult(-1.0D);
                  pull.changelen_ip(arrow.pull * pullmult);
                  if ((pull.dot(facing) > 0.0D || this.custom.pullbackwards) && arrow.pull > 0.0D) {
                     this.motion.add_ip(pull);
                  }
               }
            }

            if (close && this.arrows.size() <= 1 && (entity.collided || entity.onGround)) {
               this.motion.mult_ip(0.6D);
            }
         }

         if (this.custom.repel) {
            vec blockpush = this.check_repel(playerpos, entity.world);
            blockpush.mult_ip(this.custom.repelforce * 0.5D);
            blockpush = new vec(blockpush.x * 0.5D, blockpush.y * 2.0D, blockpush.z * 0.5D);
            this.motion.add_ip(blockpush);
         }

         if (this.custom.rocket) {
            this.motion.add_ip(this.rocket(entity));
         }

         if (!doJump && !isClimbing) {
            this.applyPlayerMovement();
         }

         if (doJump) {
            if (jumpSpeed <= 0.0D) {
               jumpSpeed = 0.0D;
            }

            if (jumpSpeed > GrappleConfig.getconf().rope_jump_power) {
               jumpSpeed = GrappleConfig.getconf().rope_jump_power;
            }

            this.doJump(entity, jumpSpeed, averagemotiontowards, min_spherevec_dist);
            ClientProxyClass.prev_rope_jump_time = this.entity.world.getTotalWorldTime();
            return;
         }

         vec newmotion = this.motion.add(additionalmotion);
         if (Double.isNaN(newmotion.x) || Double.isNaN(newmotion.y) || Double.isNaN(newmotion.z)) {
            newmotion = new vec(0.0D, 0.0D, 0.0D);
            this.motion = new vec(0.0D, 0.0D, 0.0D);
            System.out.println("error: motion is NaN");
         }

         entity.motionX = newmotion.x;
         entity.motionY = newmotion.y;
         entity.motionZ = newmotion.z;
         this.updateServerPos();
      }

   }

   public void calctaut(double dist, grappleArrow arrow) {
      if (arrow != null) {
         if (dist < arrow.r) {
            double taut = 1.0D - (arrow.r - dist) / 5.0D;
            if (taut < 0.0D) {
               taut = 0.0D;
            }

            arrow.taut = taut;
         } else {
            arrow.taut = 1.0D;
         }
      }

   }

   public void normalCollisions(boolean sliding) {
      vec pos = vec.positionvec(this.entity);
      if (this.entity.collidedHorizontally) {
         if (this.entity.motionX == 0.0D && (!sliding || this.tryStepUp(new vec(this.motion.x, 0.0D, 0.0D)))) {
            this.motion.x = 0.0D;
         }

         if (this.entity.motionZ == 0.0D && (!sliding || this.tryStepUp(new vec(0.0D, 0.0D, this.motion.z)))) {
            this.motion.z = 0.0D;
         }
      }

      if (sliding && !this.entity.collidedHorizontally) {
         if (this.entity.posX - this.entity.prevPosX == 0.0D) {
            this.motion.x = 0.0D;
         }

         if (this.entity.posZ - this.entity.prevPosZ == 0.0D) {
            this.motion.z = 0.0D;
         }
      }

      if (this.entity.collidedVertically) {
         if (this.entity.onGround) {
            if (!sliding && GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump)) {
               this.motion.y = this.entity.motionY;
            } else if (this.motion.y < 0.0D) {
               this.motion.y = 0.0D;
            }
         } else if (this.motion.y > 0.0D && this.entity.lastTickPosY == this.entity.posY) {
            this.motion.y = 0.0D;
         }
      }

   }

   public boolean tryStepUp(vec collisionmotion) {
      if (collisionmotion.length() == 0.0D) {
         return false;
      } else {
         vec moveoffset = collisionmotion.changelen(0.05D).add(0.0D, (double)this.entity.stepHeight + 0.01D, 0.0D);
         if (this.world.getCollisionBoxes(this.entity, this.entity.getEntityBoundingBox().offset(moveoffset.x, moveoffset.y, moveoffset.z)).isEmpty()) {
            if (!this.entity.onGround) {
               this.entity.posX += moveoffset.x;
               this.entity.posY += moveoffset.y;
               this.entity.posZ += moveoffset.z;
               this.entity.setPosition(this.entity.posX, this.entity.posY, this.entity.posZ);
               this.entity.prevPosX = this.entity.posX;
               this.entity.prevPosY = this.entity.posY;
               this.entity.prevPosZ = this.entity.posZ;
            }

            this.entity.collidedHorizontally = false;
            return false;
         } else {
            return true;
         }
      }
   }

   public void normalGround(boolean sliding) {
      if (this.entity.onGround) {
         this.ongroundtimer = this.maxongroundtimer;
      } else if (this.ongroundtimer > 0) {
         --this.ongroundtimer;
      }

      if ((this.entity.onGround || this.ongroundtimer > 0) && !sliding) {
         this.motion = vec.motionvec(this.entity);
         if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
            this.motion.y += 0.05D;
         }
      }

      this.prevonground = this.entity.onGround;
   }

   private double getJumpPower(Entity player, double jumppower) {
      double maxjump = GrappleConfig.getconf().rope_jump_power;
      if (this.ongroundtimer > 0) {
         this.ongroundtimer = 20;
         return 0.0D;
      } else {
         if (player.onGround) {
            jumppower = 0.0D;
         }

         if (player.collided) {
            jumppower = maxjump;
         }

         if (jumppower < 0.0D) {
            jumppower = 0.0D;
         }

         return jumppower;
      }
   }

   public void doJump(Entity player, double jumppower, vec averagemotiontowards, double min_spherevec_dist) {
      if (jumppower > 0.0D) {
         if (GrappleConfig.getconf().rope_jump_at_angle && min_spherevec_dist > 1.0D) {
            this.motion.add_ip(averagemotiontowards.changelen(jumppower));
            player.motionX = this.motion.x;
            player.motionY = this.motion.y;
            player.motionZ = this.motion.z;
         } else if (jumppower > player.motionY + jumppower) {
            player.motionY = jumppower;
         } else {
            player.motionY += jumppower;
         }
      }

      this.unattach();
      this.updateServerPos();
   }

   public double getJumpPower(Entity player, vec spherevec, grappleArrow arrow) {
      double maxjump = GrappleConfig.getconf().rope_jump_power;
      vec jump = new vec(0.0D, maxjump, 0.0D);
      if (spherevec != null && !GrappleConfig.getconf().rope_jump_at_angle) {
         jump = jump.proj(spherevec);
      }

      double jumppower = jump.y;
      if (spherevec != null && spherevec.y > 0.0D) {
         jumppower = 0.0D;
      }

      if (arrow != null && arrow.r < 1.0D && player.posY < arrow.posY) {
         jumppower = maxjump;
      }

      jumppower = this.getJumpPower(player, jumppower);
      double current_speed = GrappleConfig.getconf().rope_jump_at_angle ? -this.motion.dist_along(spherevec) : this.motion.y;
      if (current_speed > 0.0D) {
         jumppower -= current_speed;
      }

      if (jumppower < 0.0D) {
         jumppower = 0.0D;
      }

      return jumppower;
   }

   public vec dampenmotion(vec motion, vec forward) {
      vec newmotion = motion.proj(forward);
      double dampening = 0.05D;
      return new vec(newmotion.x * dampening + motion.x * (1.0D - dampening), newmotion.y * dampening + motion.y * (1.0D - dampening), newmotion.z * dampening + motion.z * (1.0D - dampening));
   }

   public void updateServerPos() {
      grapplemod.network.sendToServer(new PlayerMovementMessage(this.entityId, this.entity.posX, this.entity.posY, this.entity.posZ, this.entity.motionX, this.entity.motionY, this.entity.motionZ));
   }

   public void receiveGrappleDetach() {
      this.unattach();
   }

   public void receiveEnderLaunch(double x, double y, double z) {
      this.motion.add_ip(x, y, z);
      this.entity.motionX = this.motion.x;
      this.entity.motionY = this.motion.y;
      this.entity.motionZ = this.motion.z;
   }

   public void applyAirFriction() {
      double dragforce = 0.004999999888241291D;
      if (this.entity.isInWater() || this.entity.isInLava()) {
         dragforce = 0.25D;
      }

      double vel = this.motion.length();
      dragforce = vel * dragforce;
      vec airfric = new vec(this.motion.x, this.motion.y, this.motion.z);
      airfric.changelen_ip(-dragforce);
      this.motion.add_ip(airfric);
   }

   public void applyPlayerMovement() {
      this.motion.add_ip(this.playermovement.changelen(0.015D + this.motion.length() * 0.01D).mult(this.playermovementmult));
   }

   public void addArrow(grappleArrow arrow) {
      this.arrows.add(arrow);
      arrow.r = arrow.segmenthandler.getDist(vec.positionvec(arrow), vec.positionvec(this.entity).add(new vec(0.0D, (double)this.entity.getEyeHeight(), 0.0D)));
      this.arrowIds.add(Integer.valueOf(arrow.getEntityId()));
   }

   public vec check_repel(vec p, World w) {
      p = p.add(0.0D, 0.75D, 0.0D);
      vec v = new vec(0.0D, 0.0D, 0.0D);
      double t = (1.0D + Math.sqrt(5.0D)) / 2.0D;
      BlockPos pos = new BlockPos(Math.floor(p.x), Math.floor(p.y), Math.floor(p.z));
      if (this.hasblock(pos, w)) {
         v.add_ip(0.0D, 1.0D, 0.0D);
      } else {
         v.add_ip(this.vecdist(p, new vec(-1.0D, t, 0.0D), w));
         v.add_ip(this.vecdist(p, new vec(1.0D, t, 0.0D), w));
         v.add_ip(this.vecdist(p, new vec(-1.0D, -t, 0.0D), w));
         v.add_ip(this.vecdist(p, new vec(1.0D, -t, 0.0D), w));
         v.add_ip(this.vecdist(p, new vec(0.0D, -1.0D, t), w));
         v.add_ip(this.vecdist(p, new vec(0.0D, 1.0D, t), w));
         v.add_ip(this.vecdist(p, new vec(0.0D, -1.0D, -t), w));
         v.add_ip(this.vecdist(p, new vec(0.0D, 1.0D, -t), w));
         v.add_ip(this.vecdist(p, new vec(t, 0.0D, -1.0D), w));
         v.add_ip(this.vecdist(p, new vec(t, 0.0D, 1.0D), w));
         v.add_ip(this.vecdist(p, new vec(-t, 0.0D, -1.0D), w));
         v.add_ip(this.vecdist(p, new vec(-t, 0.0D, 1.0D), w));
      }

      if (v.length() > this.repelmaxpush) {
         v.changelen_ip(this.repelmaxpush);
      }

      return v;
   }

   public vec vecdist(vec p, vec v, World w) {
      for(double i = 0.5D; i < 10.0D; i += 0.5D) {
         vec v2 = v.changelen(i);
         BlockPos pos = new BlockPos(Math.floor(p.x + v2.x), Math.floor(p.y + v2.y), Math.floor(p.z + v2.z));
         if (this.hasblock(pos, w)) {
            vec v3 = new vec((double)pos.getX() + 0.5D - p.x, (double)pos.getY() + 0.5D - p.y, (double)pos.getZ() + 0.5D - p.z);
            v3.changelen_ip(-1.0D / Math.pow(v3.length(), 2.0D));
            return v3;
         }
      }

      return new vec(0.0D, 0.0D, 0.0D);
   }

   public boolean hasblock(BlockPos pos, World w) {
      boolean isblock = false;
      IBlockState blockstate = w.getBlockState(pos);
      Block b = blockstate.getBlock();
      if (!b.isAir(blockstate, w, pos)) {
         isblock = true;
      }

      return isblock;
   }

   public void receiveGrappleDetachHook(int hookid) {
      if (this.arrowIds.contains(Integer.valueOf(hookid))) {
         this.arrowIds.remove(Integer.valueOf(hookid));
      } else {
         System.out.println("Error: controller received hook detach, but hook id not in arrowIds");
      }

      grappleArrow arrowToRemove = null;

      for(grappleArrow arrow : this.arrows) {
         if (arrow.getEntityId() == hookid) {
            arrowToRemove = arrow;
            break;
         }
      }

      if (arrowToRemove != null) {
         this.arrows.remove(arrowToRemove);
      } else {
         System.out.println("Error: controller received hook detach, but arrow not in arrows");
      }

   }

   public vec rocket(Entity entity) {
      if (ClientProxyClass.key_rocket.isKeyDown()) {
         this.rocket_on = grapplemod.proxy.getRocketFunctioning();
         double rocket_force = this.custom.rocket_force * 0.225D * this.rocket_on;
         double yaw = (double)entity.rotationYaw;
         double pitch = (double)(-entity.rotationPitch);
         pitch = pitch + this.custom.rocket_vertical_angle;
         vec force = new vec(0.0D, 0.0D, rocket_force);
         force = force.rotate_pitch(Math.toRadians(pitch));
         force = force.rotate_yaw(Math.toRadians(yaw));
         this.rocket_key = true;
         return force;
      } else {
         this.rocket_key = false;
         this.rocket_on = 0.0D;
         return new vec(0.0D, 0.0D, 0.0D);
      }
   }

   public vec getnearbywall(vec tryfirst, vec trysecond, double extra) {
      float entitywidth = this.entity.width;

      for(vec direction : new vec[]{tryfirst, trysecond, tryfirst.mult(-1.0D), trysecond.mult(-1.0D)}) {
         RayTraceResult raytraceresult = this.entity.world.rayTraceBlocks(this.entity.getPositionVector(), vec.positionvec(this.entity).add(direction.changelen((double)(entitywidth / 2.0F) + extra)).toVec3d(), false, true, false);
         if (raytraceresult != null && raytraceresult.typeOfHit == Type.BLOCK) {
            this.wallrun_raytrace_result = raytraceresult;
            return direction;
         }
      }

      return null;
   }

   public vec getwalldirection() {
      vec tryfirst = new vec(0.0D, 0.0D, 0.0D);
      vec trysecond = new vec(0.0D, 0.0D, 0.0D);
      if (Math.abs(this.motion.x) > Math.abs(this.motion.z)) {
         tryfirst.x = this.motion.x > 0.0D ? 1.0D : -1.0D;
         trysecond.z = this.motion.z > 0.0D ? 1.0D : -1.0D;
      } else {
         tryfirst.z = this.motion.z > 0.0D ? 1.0D : -1.0D;
         trysecond.x = this.motion.x > 0.0D ? 1.0D : -1.0D;
      }

      return this.getnearbywall(tryfirst, trysecond, 0.05D);
   }

   public vec getcorner(int cornernum, vec facing, vec sideways) {
      vec corner = new vec(0.0D, 0.0D, 0.0D);
      if (cornernum / 2 == 0) {
         corner.add_ip(facing);
      } else {
         corner.add_ip(facing.mult(-1.0D));
      }

      if (cornernum % 2 == 0) {
         corner.add_ip(sideways);
      } else {
         corner.add_ip(sideways.mult(-1.0D));
      }

      return corner;
   }

   public boolean wallnearby(double dist) {
      float entitywidth = this.entity.width;
      vec v1 = new vec((double)(entitywidth / 2.0F) + dist, 0.0D, 0.0D);
      vec v2 = new vec(0.0D, 0.0D, (double)(entitywidth / 2.0F) + dist);

      for(int i = 0; i < 4; ++i) {
         vec corner1 = this.getcorner(i, v1, v2);
         vec corner2 = this.getcorner((i + 1) % 4, v1, v2);
         RayTraceResult raytraceresult = this.entity.world.rayTraceBlocks(vec.positionvec(this.entity).add(corner1).toVec3d(), vec.positionvec(this.entity).add(corner2).toVec3d(), false, true, false);
         if (raytraceresult != null && raytraceresult.typeOfHit == Type.BLOCK) {
            return true;
         }
      }

      return false;
   }

   public boolean iswallrunning() {
      double current_speed = Math.sqrt(Math.pow(this.motion.x, 2.0D) + Math.pow(this.motion.z, 2.0D));
      if (current_speed < GrappleConfig.getconf().wallrun_min_speed) {
         this.isonwall = false;
         return false;
      } else {
         if (this.isonwall) {
            grapplemod.proxy.setWallrunTicks(grapplemod.proxy.getWallrunTicks() + 1);
         }

         if ((double)grapplemod.proxy.getWallrunTicks() < GrappleConfig.getconf().max_wallrun_time * 40.0D) {
            if (!this.playersneak) {
               if (this.isonwall && !this.entity.onGround && this.entity.collidedHorizontally) {
                  if (this.entity instanceof EntityLivingBase && ((EntityLivingBase)this.entity).isOnLadder()) {
                     return false;
                  }

                  return true;
               }

               if (grapplemod.proxy.iswallrunning(this.entity, this.motion)) {
                  this.isonwall = true;
                  return true;
               }
            }

            this.isonwall = false;
         }

         if (grapplemod.proxy.getWallrunTicks() > 0 && (this.entity.onGround || !this.entity.collidedHorizontally && !this.wallnearby(0.2D))) {
            this.ticks_since_last_wallrun_sound_effect = 0;
         }

         return false;
      }
   }

   public boolean applywallrun() {
      boolean wallrun = this.iswallrunning();
      if (this.playerjump) {
         if (wallrun) {
            return false;
         }

         this.playerjump = false;
      }

      if (wallrun && !ClientProxyClass.key_jumpanddetach.isKeyDown()) {
         vec wallside = this.getwalldirection();
         if (wallside != null) {
            this.walldirection = wallside;
         }

         if (this.walldirection == null) {
            return false;
         }

         if (!this.playerjump) {
            this.motion.y = 0.0D;
         }

         double dragforce = GrappleConfig.getconf().wallrun_drag;
         double vel = this.motion.length();
         if (dragforce > vel) {
            dragforce = vel;
         }

         vec wallfric = new vec(this.motion);
         if (wallside != null) {
            wallfric.removealong(wallside);
         }

         wallfric.changelen_ip(-dragforce);
         this.motion.add_ip(wallfric);
         ++this.ticks_since_last_wallrun_sound_effect;
         if ((double)this.ticks_since_last_wallrun_sound_effect > GrappleConfig.client_options.wallrun_sound_effect_time_s * 20.0D * GrappleConfig.getconf().wallrun_max_speed / (vel + 1.0E-8D) && this.wallrun_raytrace_result != null && this.wallrun_raytrace_result.typeOfHit == Type.BLOCK) {
            BlockPos blockpos = this.wallrun_raytrace_result.getBlockPos();
            IBlockState blockState = this.entity.world.getBlockState(blockpos);
            Block blockIn = blockState.getBlock();
            SoundType soundtype = blockIn.getSoundType(blockState, this.world, blockpos, this.entity);
            if (!blockIn.getDefaultState().getMaterial().isLiquid()) {
               this.entity.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.3F * GrappleConfig.client_options.wallrun_sound_volume, soundtype.getPitch());
               this.ticks_since_last_wallrun_sound_effect = 0;
            }
         }
      }

      boolean isjumping = ClientProxyClass.key_jumpanddetach.isKeyDown() && this.isonwall;
      isjumping = isjumping && !this.playerjump;
      this.playerjump = ClientProxyClass.key_jumpanddetach.isKeyDown() && this.isonwall;
      if (isjumping && wallrun) {
         grapplemod.proxy.setWallrunTicks(0);
         vec jump = new vec(0.0D, GrappleConfig.getconf().wall_jump_up, 0.0D);
         if (this.walldirection != null) {
            jump.add_ip(this.walldirection.mult(-GrappleConfig.getconf().wall_jump_side));
         }

         this.motion.add_ip(jump);
         wallrun = false;
         grapplemod.proxy.playWallrunJumpSound(this.entity);
      }

      return wallrun;
   }

   public vec wallrun_press_against_wall() {
      return this.walldirection != null ? this.walldirection.changelen(0.05D) : new vec(0.0D, 0.0D, 0.0D);
   }

   public void doublejump() {
      if (-this.motion.y <= GrappleConfig.getconf().dont_doublejump_if_falling_faster_than) {
         if (this.motion.y < 0.0D && !GrappleConfig.getconf().doublejump_relative_to_falling) {
            this.motion.y = 0.0D;
         }

         this.motion.y += GrappleConfig.getconf().doublejumpforce;
         this.entity.motionX = this.motion.x;
         this.entity.motionY = this.motion.y;
         this.entity.motionZ = this.motion.z;
      }
   }

   public void applySlidingFriction() {
      double dragforce = GrappleConfig.getconf().sliding_friction;
      if (dragforce > this.motion.length()) {
         dragforce = this.motion.length();
      }

      vec airfric = new vec(this.motion.x, this.motion.y, this.motion.z);
      airfric.changelen_ip(-dragforce);
      this.motion.add_ip(airfric);
   }

   public void slidingJump() {
      this.motion.y = GrappleConfig.getconf().slidingjumpforce;
   }
}
