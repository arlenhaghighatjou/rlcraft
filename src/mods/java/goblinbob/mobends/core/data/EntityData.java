package goblinbob.mobends.core.data;

import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.IBendsModel;
import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.core.math.vector.SmoothVector3f;
import goblinbob.mobends.core.pack.state.PackAnimationState;
import goblinbob.mobends.core.util.GUtil;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class EntityData<E extends Entity> implements IBendsModel {
   protected int entityID;
   protected final E entity;
   protected double positionX;
   protected double positionY;
   protected double positionZ;
   protected double prevMotionX;
   protected double prevMotionY;
   protected double prevMotionZ;
   protected double motionX;
   protected double motionY;
   protected double motionZ;
   protected final HashMap<String, Object> nameToPartMap = new HashMap();
   public SmoothVector3f globalOffset;
   public SmoothVector3f localOffset;
   public SmoothOrientation renderRotation;
   public SmoothOrientation centerRotation;
   public boolean onGround = true;
   public Boolean onGroundOverride = null;
   public Boolean stillnessOverride = null;
   public final PackAnimationState packAnimationState;
   private static final float STRAFING_THRESHOLD = 30.0F;

   public EntityData(E entity) {
      this.entity = entity;
      if (this.entity != null) {
         this.entityID = entity.getEntityId();
         this.positionX = this.entity.posX;
         this.positionY = this.entity.posY;
         this.positionZ = this.entity.posZ;
      }

      this.motionX = this.prevMotionX = 0.0D;
      this.motionY = this.prevMotionY = 1.0D;
      this.motionZ = this.prevMotionZ = 0.0D;
      this.packAnimationState = new PackAnimationState();
      this.initModelPose();
   }

   public void overrideOnGroundState(boolean state) {
      this.onGroundOverride = state;
   }

   public void unsetOnGroundStateOverride() {
      this.onGroundOverride = null;
   }

   public void overrideStillness(boolean stillness) {
      this.stillnessOverride = stillness;
   }

   public void unsetStillnessOverride() {
      this.stillnessOverride = null;
   }

   public void initModelPose() {
      this.globalOffset = new SmoothVector3f();
      this.localOffset = new SmoothVector3f();
      this.renderRotation = new SmoothOrientation();
      this.centerRotation = new SmoothOrientation();
      this.nameToPartMap.put("renderRotation", this.renderRotation);
      this.nameToPartMap.put("centerRotation", this.centerRotation);
   }

   public void updateParts(float ticksPerFrame) {
      this.globalOffset.update(ticksPerFrame);
      this.localOffset.update(ticksPerFrame);
      this.renderRotation.update(ticksPerFrame);
      this.centerRotation.update(ticksPerFrame);
   }

   public boolean calcOnGround() {
      if (this.onGroundOverride != null) {
         return this.onGroundOverride.booleanValue();
      } else {
         BlockPos position = new BlockPos(Math.floor(this.entity.posX), Math.floor(this.entity.posY), Math.floor(this.entity.posZ));
         IBlockState block = this.entity.world.getBlockState(position);
         IBlockState blockBelow = this.entity.world.getBlockState(position.add(0, -1, 0));
         if (this.motionY > 0.0D || !(block.getBlock() instanceof BlockStairs) && !(blockBelow.getBlock() instanceof BlockStairs)) {
            List<AxisAlignedBB> list = this.entity.world.getCollisionBoxes(this.entity, this.entity.getEntityBoundingBox().offset(0.0D, -0.125D, 0.0D));
            return list.size() > 0;
         } else {
            return true;
         }
      }
   }

   public boolean calcCollidedHorizontally() {
      List<AxisAlignedBB> list = this.entity.world.getCollisionBoxes(this.entity, this.entity.getEntityBoundingBox().offset(this.motionX, 0.0D, this.motionZ));
      return list.size() > 0;
   }

   public double getPositionX() {
      return this.positionX;
   }

   public double getPositionY() {
      return this.positionY;
   }

   public double getPositionZ() {
      return this.positionZ;
   }

   public double getMotionX() {
      return this.motionX;
   }

   public double getMotionY() {
      return this.motionY;
   }

   public double getMotionZ() {
      return this.motionZ;
   }

   public double getPrevMotionX() {
      return this.prevMotionX;
   }

   public double getPrevMotionY() {
      return this.prevMotionY;
   }

   public double getPrevMotionZ() {
      return this.prevMotionZ;
   }

   public double getInterpolatedMotionX() {
      return this.prevMotionX + (this.motionX - this.prevMotionX) * (double)DataUpdateHandler.partialTicks;
   }

   public double getInterpolatedMotionY() {
      return this.prevMotionY + (this.motionY - this.prevMotionY) * (double)DataUpdateHandler.partialTicks;
   }

   public double getInterpolatedMotionZ() {
      return this.prevMotionZ + (this.motionZ - this.prevMotionZ) * (double)DataUpdateHandler.partialTicks;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public boolean isStillHorizontally() {
      double deadZone = 0.0025D;
      double horizontalSqMagnitude = this.motionX * this.motionX + this.motionZ * this.motionZ;
      return this.stillnessOverride != null ? this.stillnessOverride.booleanValue() : horizontalSqMagnitude < 0.0025D;
   }

   public abstract IAnimationController<?> getController();

   public void update(float partialTicks) {
      if (this.entity != null) {
         this.updateParts(DataUpdateHandler.ticksPerFrame);
      }
   }

   public E getEntity() {
      return this.entity;
   }

   public float getLookAngle() {
      Vec3d lookVec = this.entity.getLookVec();
      return (float)GUtil.angleFromCoordinates(lookVec.x, lookVec.z);
   }

   private float getWorldMovementAngle() {
      return (float)GUtil.angleFromCoordinates(this.motionX, this.motionZ);
   }

   public float getMovementAngle() {
      return this.isStillHorizontally() ? 0.0F : this.getWorldMovementAngle() - this.getLookAngle();
   }

   public double getForwardMomentum() {
      if (this.isStillHorizontally()) {
         return 0.0D;
      } else {
         Vec3d lookVec = this.entity.getLookVec();
         Vec3d lookVecHorizontal = (new Vec3d(lookVec.x, 0.0D, lookVec.z)).normalize();
         return lookVecHorizontal.x * this.motionX + lookVecHorizontal.z * this.motionZ;
      }
   }

   public double getSidewaysMomentum() {
      if (this.isStillHorizontally()) {
         return 0.0D;
      } else {
         Vec3d rightVec = this.entity.getLookVec().rotateYaw(-1.5707964F);
         Vec3d rightVecHorizontal = (new Vec3d(rightVec.x, 0.0D, rightVec.z)).normalize();
         return rightVecHorizontal.x * this.motionX + rightVecHorizontal.z * this.motionZ;
      }
   }

   public boolean isStrafing() {
      float angle = this.getMovementAngle();
      return angle >= 30.0F && angle <= 150.0F || angle >= -150.0F && angle <= -30.0F;
   }

   public boolean isUnderwater() {
      if (!this.entity.isInWater()) {
         return false;
      } else {
         int blockX = MathHelper.floor(this.entity.posX);
         int blockY = MathHelper.floor(this.entity.posY + 2.0D);
         int blockZ = MathHelper.floor(this.entity.posZ);
         IBlockState state = Minecraft.getMinecraft().world.getBlockState(new BlockPos(blockX, blockY, blockZ));
         return state.getBlock() instanceof BlockStaticLiquid;
      }
   }

   public double getPrevMotionMagnitude() {
      return Math.sqrt(this.prevMotionX * this.prevMotionX + this.prevMotionY * this.prevMotionY + this.prevMotionZ * this.prevMotionZ);
   }

   public double getMotionMagnitude() {
      return Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
   }

   public double getInterpolatedMotionMagnitude() {
      return interpolateMagitude(this.getMotionMagnitude(), this.getPrevMotionMagnitude());
   }

   public double getXZMotionMagnitude() {
      return Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
   }

   public double getPrevXZMotionMagnitude() {
      return Math.sqrt(this.prevMotionX * this.prevMotionX + this.prevMotionZ * this.prevMotionZ);
   }

   public double getInterpolatedXZMotionMagnitude() {
      return interpolateMagitude(this.getXZMotionMagnitude(), this.getPrevXZMotionMagnitude());
   }

   private static double interpolateMagitude(double magnitude, double prevMagnitude) {
      return prevMagnitude + (magnitude - prevMagnitude) * (double)DataUpdateHandler.partialTicks;
   }

   public void updateClient() {
      this.prevMotionX = this.motionX;
      this.prevMotionY = this.motionY;
      this.prevMotionZ = this.motionZ;
      this.motionX = this.entity.posX - this.positionX;
      this.motionY = this.entity.posY - this.positionY;
      this.motionZ = this.entity.posZ - this.positionZ;
      this.positionX = this.entity.posX;
      this.positionY = this.entity.posY;
      this.positionZ = this.entity.posZ;
   }

   public Object getPartForName(String name) {
      return this.nameToPartMap.get(name);
   }

   public abstract void onTicksRestart();
}
