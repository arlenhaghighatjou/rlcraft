package org.orecruncher.dsurround.client.footsteps;

import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.footsteps.accents.FootstepAccents;
import org.orecruncher.dsurround.client.fx.ParticleCollections;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.sound.SoundEngine;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.acoustics.AcousticRegistry;
import org.orecruncher.dsurround.registry.acoustics.EventType;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.acoustics.IOptions;
import org.orecruncher.dsurround.registry.footstep.BlockMap;
import org.orecruncher.dsurround.registry.footstep.FootprintStyle;
import org.orecruncher.dsurround.registry.footstep.Substrate;
import org.orecruncher.dsurround.registry.footstep.Variator;
import org.orecruncher.dsurround.registry.sound.SoundRegistry;
import org.orecruncher.lib.TimeUtils;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.lib.compat.EntityLivingBaseUtil;
import org.orecruncher.lib.compat.EntityUtil;
import org.orecruncher.lib.math.MathStuff;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class Generator {
   public static final double PROBE_DEPTH = 0.0625D;
   protected static final Random RANDOM = XorShiftRandom.current();
   protected static final int BRUSH_INTERVAL = 100;
   protected static final Consumer<Footprint> GENERATE_PRINT = (print) -> {
      Vec3d loc = print.getStepLocation();
      World world = print.getEntity().getEntityWorld();
      ParticleCollections.addFootprint(print.getStyle(), world, loc, print.getRotation(), print.getScale(), print.isRightFoot());
   };
   protected final Variator VAR;
   protected final BlockMap blockMap;
   protected float dmwBase;
   protected float dwmYChange;
   protected double yPosition;
   protected boolean didJump;
   protected boolean isFlying;
   protected float fallDistance;
   protected float lastReference;
   protected boolean isImmobile;
   protected long timeImmobile;
   protected boolean isRightFoot;
   protected boolean isOnLadder;
   protected boolean isInWater;
   protected boolean isSneaking;
   protected boolean isJumping;
   protected double xMovec;
   protected double zMovec;
   protected boolean scalStat;
   protected boolean stepThisFrame;
   protected BlockPos messyPos = BlockPos.ORIGIN;
   protected long brushesTime;
   protected double distanceWalkedOnStepModified;
   protected int pedometer;
   protected static final ObjectArray<IAcoustic> accents = new ObjectArray(4);
   protected final ObjectArray<Footprint> footprints = new ObjectArray(4);
   protected final SoundPlayer soundPlayer;

   public Generator(@Nonnull Variator var) {
      this.VAR = var;
      this.blockMap = RegistryManager.FOOTSTEPS.getBlockMap();
      this.soundPlayer = new SoundPlayer(this.VAR.VOLUME_SCALE);
   }

   public int getPedometer() {
      return this.pedometer;
   }

   public void generateFootsteps(@Nonnull EntityLivingBase entity) {
      if (!entity.isRiding() && !entity.isPlayerSleeping()) {
         if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
            this.didJump = false;
            this.stepThisFrame = false;
            this.isOnLadder = entity.isOnLadder();
            this.isInWater = entity.isInWater();
            this.isSneaking = entity.isSneaking();
            this.isJumping = EntityLivingBaseUtil.isJumping(entity);
            this.simulateFootsteps(entity);
            this.simulateAirborne(entity);
            this.simulateBrushes(entity);
            this.soundPlayer.think();
            if (this.footprints.size() > 0) {
               this.footprints.forEach(GENERATE_PRINT);
               this.footprints.clear();
            }

            if (this.stepThisFrame) {
               ++this.pedometer;
            }

            if (this.didJump && ModOptions.sound.enableJumpSound && this.VAR.PLAY_JUMP && !this.isSneaking) {
               this.soundPlayer.playAcoustic(entity.getPositionVector(), RegistryManager.FOOTSTEPS.JUMP, EventType.JUMP, (IOptions)null);
            }

            if (SoundEngine.getVolume(SoundRegistry.FOOTSTEPS) > 0.0F) {
               EntityUtil.setNextStepDistance(entity, Integer.MAX_VALUE);
            } else {
               int dist = EntityUtil.getNextStepDistance(entity);
               if (dist == Integer.MAX_VALUE) {
                  EntityUtil.setNextStepDistance(entity, 0);
               }
            }

         }
      }
   }

   protected boolean stoppedImmobile(float reference) {
      long current = TimeUtils.currentTimeMillis();
      float diff = this.lastReference - reference;
      this.lastReference = reference;
      if (!this.isImmobile && diff == 0.0F) {
         this.timeImmobile = current;
         this.isImmobile = true;
      } else if (this.isImmobile && diff != 0.0F) {
         this.isImmobile = false;
         return current - this.timeImmobile > (long)this.VAR.IMMOBILE_DURATION;
      }

      return false;
   }

   protected void updateWalkedOnStep(@Nonnull EntityLivingBase entity) {
      double dX = entity.posX - entity.prevPosX;
      double dY = entity.posY - entity.prevPosY;
      double dZ = entity.posZ - entity.prevPosZ;
      this.distanceWalkedOnStepModified += Math.sqrt(dX * dX + dY * dY + dZ * dZ) * 0.6000000238418579D;
   }

   protected void simulateFootsteps(@Nonnull EntityLivingBase entity) {
      this.updateWalkedOnStep(entity);
      float distanceReference = (float)this.distanceWalkedOnStepModified;
      if (this.dmwBase > distanceReference) {
         this.dmwBase = 0.0F;
         this.dwmYChange = 0.0F;
      }

      double movX = entity.motionX;
      double movZ = entity.motionZ;
      double scal = movX * this.xMovec + movZ * this.zMovec;
      if (this.scalStat != scal < 0.0010000000474974513D) {
         this.scalStat = !this.scalStat;
         if (this.scalStat && this.VAR.PLAY_WANDER && !this.hasSpecialStoppingConditions(entity)) {
            this.playSinglefoot(entity, 0.0D, EventType.WANDER, this.isRightFoot);
         }
      }

      this.xMovec = movX;
      this.zMovec = movZ;
      if (entity.onGround || this.isInWater || this.isOnLadder) {
         EventType event = null;
         float dwm = distanceReference - this.dmwBase;
         boolean immobile = this.stoppedImmobile(distanceReference);
         if (immobile && !this.isOnLadder) {
            dwm = 0.0F;
            this.dmwBase = distanceReference;
         }

         float distance = 0.0F;
         if (entity.isOnLadder() && !entity.onGround) {
            distance = this.VAR.STRIDE_LADDER;
         } else if (!this.isInWater && MathStuff.abs(this.yPosition - entity.posY) > 0.4D) {
            if (this.yPosition < entity.posY) {
               distance = this.VAR.STRIDE_STAIR;
               event = this.speedDisambiguator(entity, EventType.UP, EventType.UP_RUN);
            } else if (!this.isSneaking) {
               distance = -1.0F;
               event = this.speedDisambiguator(entity, EventType.DOWN, EventType.DOWN_RUN);
            }

            this.dwmYChange = distanceReference;
         } else {
            distance = this.VAR.STRIDE;
         }

         if (event == null) {
            event = this.speedDisambiguator(entity, EventType.WALK, EventType.RUN);
         }

         distance = this.reevaluateDistance(event, distance);
         if (dwm > distance) {
            this.produceStep(entity, event, 0.0D);
            this.stepped(entity, event);
            this.dmwBase = distanceReference;
         }
      }

      if (entity.onGround) {
         this.yPosition = entity.posY;
      }

   }

   protected void stepped(@Nonnull EntityLivingBase entity, @Nonnull EventType event) {
   }

   protected float reevaluateDistance(@Nonnull EventType event, float distance) {
      return distance;
   }

   protected void produceStep(@Nonnull EntityLivingBase entity, @Nonnull EventType event) {
      this.produceStep(entity, event, 0.0D);
   }

   protected void produceStep(@Nonnull EntityLivingBase entity, @Nullable EventType event, double verticalOffsetAsMinus) {
      if (!this.playSpecialStoppingConditions(entity)) {
         if (event == null) {
            event = this.speedDisambiguator(entity, EventType.WALK, EventType.RUN);
         }

         this.playSinglefoot(entity, verticalOffsetAsMinus, event, this.isRightFoot);
         this.isRightFoot = !this.isRightFoot;
      }

      this.stepThisFrame = true;
   }

   protected void simulateAirborne(@Nonnull EntityLivingBase entity) {
      if ((entity.onGround || this.isOnLadder) == this.isFlying) {
         this.isFlying = !this.isFlying;
         this.simulateJumpingLanding(entity);
      }

      if (this.isFlying) {
         this.fallDistance = entity.fallDistance;
      }

   }

   protected void simulateJumpingLanding(@Nonnull EntityLivingBase entity) {
      if (!this.hasSpecialStoppingConditions(entity)) {
         if (this.isFlying && this.isJumping) {
            if (this.VAR.EVENT_ON_JUMP && entity.motionY > 0.0D) {
               this.didJump = true;
               double speed = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
               if (speed < (double)this.VAR.SPEED_TO_JUMP_AS_MULTIFOOT) {
                  this.playMultifoot(entity, 0.4D, EventType.JUMP);
               } else {
                  this.playSinglefoot(entity, 0.4D, EventType.JUMP, this.isRightFoot);
               }
            }
         } else if (!this.isFlying && this.fallDistance > 0.0F) {
            if (this.fallDistance > this.VAR.LAND_HARD_DISTANCE_MIN) {
               this.playMultifoot(entity, 0.0D, EventType.LAND);
            } else if (!this.stepThisFrame && !this.isSneaking) {
               this.playSinglefoot(entity, 0.0D, this.speedDisambiguator(entity, EventType.CLIMB, EventType.CLIMB_RUN), this.isRightFoot);
               this.isRightFoot = !this.isRightFoot;
            }
         }

      }
   }

   protected EventType speedDisambiguator(@Nonnull EntityLivingBase entity, @Nonnull EventType walk, @Nonnull EventType run) {
      double velocity = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
      return velocity > (double)this.VAR.SPEED_TO_RUN ? run : walk;
   }

   protected void simulateBrushes(@Nonnull EntityLivingBase entity) {
      long current = TimeUtils.currentTimeMillis();
      if (current >= this.brushesTime) {
         this.brushesTime = current + 100L;
         if (this.proceedWithStep() && (entity.motionX != 0.0D || entity.motionZ != 0.0D)) {
            int yy = MathStuff.floor(entity.posY - 0.0625D - entity.getYOffset() - (entity.onGround ? 0.0D : 0.25D));
            BlockPos pos = new BlockPos(entity.posX, (double)yy, entity.posZ);
            if (!this.messyPos.equals(pos)) {
               this.messyPos = pos;
               Association assos = this.findAssociationMessyFoliage(entity, pos);
               if (assos != null) {
                  this.playAssociation(assos, EventType.WALK);
               }
            }
         }
      }

   }

   protected boolean proceedWithStep() {
      return !this.isSneaking;
   }

   protected void playSinglefoot(@Nonnull EntityLivingBase entity, double verticalOffsetAsMinus, @Nonnull EventType eventType, boolean foot) {
      if (this.proceedWithStep()) {
         Association assos = this.findAssociation(entity, verticalOffsetAsMinus, foot);
         this.playAssociation(assos, eventType);
      }

   }

   protected void playMultifoot(@Nonnull EntityLivingBase entity, double verticalOffsetAsMinus, EventType eventType) {
      if (this.proceedWithStep()) {
         Association leftFoot = this.findAssociation(entity, verticalOffsetAsMinus, false);
         Association rightFoot = this.findAssociation(entity, verticalOffsetAsMinus, true);
         this.playAssociation(leftFoot, eventType);
         this.playAssociation(rightFoot, eventType);
      }

   }

   protected void playAssociation(@Nullable Association assos, @Nonnull EventType eventType) {
      if (assos != null && !assos.isNotEmitter()) {
         this.soundPlayer.playAcoustic(assos, eventType);
      }

   }

   protected boolean shouldProducePrint(@Nonnull EntityLivingBase entity) {
      return ModOptions.effects.enableFootprints && this.VAR.HAS_FOOTPRINT && (entity.onGround || !this.isJumping && !entity.isAirBorne) && !entity.isInvisibleToPlayer(EnvironStateHandler.EnvironState.getPlayer());
   }

   @Nullable
   protected Association findAssociation(@Nonnull EntityLivingBase entity, double verticalOffsetAsMinus, boolean isRightFoot) {
      float rotDegrees = MathStuff.wrapDegrees(entity.rotationYaw);
      double rot = (double)MathStuff.toRadians(rotDegrees);
      float feetDistanceToCenter = isRightFoot ? -this.VAR.DISTANCE_TO_CENTER : this.VAR.DISTANCE_TO_CENTER;
      double xx = entity.posX + MathStuff.cos(rot) * (double)feetDistanceToCenter;
      double zz = entity.posZ + MathStuff.sin(rot) * (double)feetDistanceToCenter;
      double minY = entity.getEntityBoundingBox().minY;
      FootStrikeLocation loc = new FootStrikeLocation(entity, xx, minY - 0.0625D - verticalOffsetAsMinus, zz);
      AcousticResolver resolver = new AcousticResolver(ClientChunkCache.instance(), this.blockMap, loc, (double)this.VAR.DISTANCE_TO_CENTER);
      Association result = this.addSoundOverlay(entity, resolver.findAssociationForEvent());
      if (result != null && result.hasStrikeLocation() && this.shouldProducePrint(entity)) {
         Vec3d printPos = result.getStrikeLocation().footprintPosition();
         if (printPos != null) {
            FootprintStyle style = this.VAR.FOOTPRINT_STYLE;
            if (entity instanceof EntityPlayer) {
               style = FootprintStyle.getStyle(ModOptions.effects.footprintStyle);
            }

            Footprint print = Footprint.produce(style, entity, printPos, rotDegrees, this.VAR.FOOTPRINT_SCALE, isRightFoot);
            this.footprints.add(print);
         }
      }

      return result;
   }

   protected boolean playSpecialStoppingConditions(@Nonnull EntityLivingBase entity) {
      if (entity.isInWater()) {
         if (this.proceedWithStep()) {
            float volume = (float)MathStuff.sqrt(entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ) * 1.25F;
            ConfigOptions options = new ConfigOptions();
            options.setGlidingVolume(volume > 1.0F ? 1.0F : volume);
            this.soundPlayer.playAcoustic(entity.getPositionVector(), RegistryManager.FOOTSTEPS.SWIM, entity.isInsideOfMaterial(Material.WATER) ? EventType.SWIM : EventType.WALK, options);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean hasSpecialStoppingConditions(@Nonnull EntityLivingBase entity) {
      return entity.isInWater();
   }

   @Nullable
   protected Association findAssociationMessyFoliage(@Nonnull EntityLivingBase entity, @Nonnull BlockPos pos) {
      Association result = null;
      BlockPos up = pos.up();
      IBlockState above = ClientChunkCache.instance().getBlockState(up);
      if (above != Blocks.AIR.getDefaultState()) {
         IAcoustic[] acoustics = this.blockMap.getBlockAcoustics(above, Substrate.MESSY);
         if (acoustics == AcousticRegistry.MESSY_GROUND) {
            acoustics = this.blockMap.getBlockAcoustics(above, Substrate.FOLIAGE);
            if (acoustics != AcousticRegistry.NOT_EMITTER) {
               result = new Association(entity, acoustics);
            }
         }
      }

      return result;
   }

   @Nullable
   protected Association addSoundOverlay(@Nonnull EntityLivingBase entity, @Nullable Association assoc) {
      if (entity.onGround) {
         accents.clear();
         BlockPos pos = assoc != null ? assoc.getStepPos() : null;
         FootstepAccents.provide(entity, pos, accents);
         if (accents.size() > 0) {
            if (assoc == null) {
               assoc = new Association(entity, (IAcoustic[])accents.toArray(new IAcoustic[0]));
            } else {
               assoc.add(accents);
            }
         }
      }

      return assoc;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("didJump: ").append(this.didJump).append(' ');
      builder.append("onLadder: ").append(this.isOnLadder).append(' ');
      builder.append("flying: ").append(this.isFlying).append(' ');
      builder.append("immobile: ").append(this.isImmobile).append(' ');
      builder.append("steps: ").append(this.pedometer);
      return builder.toString();
   }
}
