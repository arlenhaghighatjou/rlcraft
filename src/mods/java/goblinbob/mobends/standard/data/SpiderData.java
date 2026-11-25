package goblinbob.mobends.standard.data;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.core.util.GUtil;
import goblinbob.mobends.standard.animation.controller.SpiderController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SpiderData extends LivingEntityData<EntitySpider> {
   public ModelPartTransform spiderHead;
   public ModelPartTransform spiderNeck;
   public ModelPartTransform spiderBody;
   public SpiderData.Limb[] limbs;
   protected final SpiderController controller = new SpiderController();
   protected float prevCrawlProgress = 0.0F;
   protected float crawlProgress = 0.0F;
   protected EnumFacing wallFacing = null;

   public SpiderData(EntitySpider entity) {
      super(entity);
   }

   public SpiderController getController() {
      return this.controller;
   }

   public float getCrawlProgress() {
      return this.crawlProgress;
   }

   public float getInterpolatedCrawlProgress() {
      return GUtil.lerp(this.prevCrawlProgress, this.crawlProgress, DataUpdateHandler.partialTicks);
   }

   public void onTicksRestart() {
   }

   public void update(float partialTicks) {
      super.update(partialTicks);
   }

   public void initModelPose() {
      super.initModelPose();
      this.spiderBody = new ModelPartTransform();
      this.spiderNeck = new ModelPartTransform();
      this.spiderHead = new ModelPartTransform();
      this.limbs = new SpiderData.Limb[8];

      for(int i = 0; i < this.limbs.length; ++i) {
         this.limbs[i] = new SpiderData.Limb(this, i);
         this.nameToPartMap.put("leg" + (i + 1), this.limbs[i].upperPart);
         this.nameToPartMap.put("foreLeg" + (i + 1), this.limbs[i].lowerPart);
      }

      this.nameToPartMap.put("body", this.spiderBody);
      this.nameToPartMap.put("neck", this.spiderNeck);
      this.nameToPartMap.put("head", this.spiderHead);
      this.spiderHead.position.set(0.0F, 15.0F, -3.0F);
      this.spiderNeck.position.set(0.0F, 15.0F, 0.0F);
      this.spiderBody.position.set(0.0F, 15.0F, 9.0F);
   }

   public void updateParts(float ticksPerFrame) {
      super.updateParts(ticksPerFrame);
      this.spiderBody.update(ticksPerFrame);
      this.spiderNeck.update(ticksPerFrame);
      this.spiderHead.update(ticksPerFrame);

      for(SpiderData.Limb limb : this.limbs) {
         limb.upperPart.update(ticksPerFrame);
         limb.lowerPart.update(ticksPerFrame);
      }

   }

   public void updateClient() {
      super.updateClient();

      for(SpiderData.Limb limb : this.limbs) {
         limb.updateClient();
      }

      this.prevCrawlProgress = this.crawlProgress;
      this.crawlProgress += MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.wallFacing = this.calcWallFacing();
   }

   public EnumFacing calcWallFacing() {
      if (!((EntitySpider)this.entity).isOnLadder()) {
         return null;
      } else {
         BlockPos position = new BlockPos(Math.floor(((EntitySpider)this.entity).posX), Math.floor(((EntitySpider)this.entity).posY), Math.floor(((EntitySpider)this.entity).posZ));
         IBlockState blockN = ((EntitySpider)this.entity).world.getBlockState(position.add(EnumFacing.NORTH.getDirectionVec()));
         IBlockState blockS = ((EntitySpider)this.entity).world.getBlockState(position.add(EnumFacing.SOUTH.getDirectionVec()));
         IBlockState blockW = ((EntitySpider)this.entity).world.getBlockState(position.add(EnumFacing.WEST.getDirectionVec()));
         IBlockState blockE = ((EntitySpider)this.entity).world.getBlockState(position.add(EnumFacing.EAST.getDirectionVec()));
         if (blockN.getBlock() != Blocks.AIR) {
            return EnumFacing.NORTH;
         } else if (blockS.getBlock() != Blocks.AIR) {
            return EnumFacing.SOUTH;
         } else if (blockW.getBlock() != Blocks.AIR) {
            return EnumFacing.WEST;
         } else {
            return blockE.getBlock() != Blocks.AIR ? EnumFacing.EAST : null;
         }
      }
   }

   public EnumFacing getWallFacing() {
      return this.wallFacing;
   }

   public float getCrawlingRotation() {
      return this.wallFacing == null ? 0.0F : this.wallFacing.getHorizontalAngle();
   }

   public static class IKResult {
      public final double worldX;
      public final double worldZ;
      public final double localX;
      public final double localZ;
      public final double deltaX;
      public final double deltaZ;
      public final double xzDistance;
      public final double xzAngle;

      IKResult(double worldX, double worldZ, double localX, double localZ, double deltaX, double deltaZ, double xzDistance, double xzAngle) {
         this.worldX = worldX;
         this.worldZ = worldZ;
         this.localX = localX;
         this.localZ = localZ;
         this.deltaX = deltaX;
         this.deltaZ = deltaZ;
         this.xzDistance = xzDistance;
         this.xzAngle = xzAngle;
      }
   }

   public static class Limb {
      private final SpiderData data;
      public final ModelPartTransform upperPart;
      public final ModelPartTransform lowerPart;
      private final int index;
      private final boolean odd;
      private final double neutralYaw;
      private double worldX;
      private double worldZ;
      private double prevWorldX;
      private double prevWorldZ;
      private double adjustTargetX = 0.0D;
      private double adjustTargetZ = 0.0D;
      private float adjustingProgress = 1.0F;
      private float adjustingSpeed = 0.2F;

      public Limb(SpiderData data, int index) {
         this.data = data;
         this.upperPart = new ModelPartTransform();
         this.lowerPart = new ModelPartTransform();
         this.index = index;
         this.odd = index % 2 == 1;
         double neutralYaw = (double)this.index / (double)(data.limbs.length - 1) * 2.0D - 1.0D;
         this.neutralYaw = this.odd ? neutralYaw * 1.3D : 3.141592653589793D - neutralYaw * 1.3D;
         int z = 2 - index / 2;
         this.upperPart.position.set(this.odd ? 4.0F : -4.0F, 15.0F, (float)z);
         this.lowerPart.position.set(this.odd ? 11.0F : -11.0F, -1.0F, 0.0F);
         this.resetPosition();
      }

      public void resetPosition() {
         float distance = 1.0F;
         float bodyYaw = ((EntitySpider)this.data.entity).renderYawOffset / 180.0F * 3.1415927F;
         this.worldX = Math.cos(this.neutralYaw + (double)bodyYaw) * 1.0D + this.data.getPositionX();
         this.worldZ = Math.sin(this.neutralYaw + (double)bodyYaw) * 1.0D + this.data.getPositionZ();
         this.prevWorldX = this.worldX;
         this.prevWorldZ = this.worldZ;
      }

      void updateClient() {
         this.prevWorldX = this.worldX;
         this.prevWorldZ = this.worldZ;
         if (this.adjustingProgress < 1.0F) {
            this.adjustingProgress += this.adjustingSpeed;
            if (this.adjustingProgress >= 1.0F) {
               this.worldX = this.adjustTargetX;
               this.worldZ = this.adjustTargetZ;
               this.adjustingProgress = 1.0F;
            } else {
               this.worldX += (this.adjustTargetX - this.worldX) * 0.2D;
               this.worldZ += (this.adjustTargetZ - this.worldZ) * 0.2D;
            }
         }

      }

      public void setAngleAndDistance(float angle, float distance) {
         this.setLocalPosition((double)(MathHelper.cos(angle) * distance + this.upperPart.position.x * 0.0625F), (double)(MathHelper.sin(angle) * distance - this.upperPart.position.z * 0.0625F));
      }

      public void adjustToNeutralPosition() {
         if (this.adjustingProgress == 1.0F) {
            this.adjustingSpeed = 0.2F;
            this.adjustingProgress = 0.0F;
            float distance = 1.2F;
            float bodyYaw = ((EntitySpider)this.data.entity).renderYawOffset / 180.0F * 3.1415927F;
            this.adjustTargetX = Math.cos(this.neutralYaw + (double)bodyYaw) * 1.2000000476837158D + this.data.getPositionX();
            this.adjustTargetZ = Math.sin(this.neutralYaw + (double)bodyYaw) * 1.2000000476837158D + this.data.getPositionZ();
         }
      }

      public void adjustToWorldPosition(double x, double z, float adjustingSpeed) {
         if (this.adjustingProgress == 1.0F) {
            this.adjustingSpeed = adjustingSpeed;
            this.adjustingProgress = 0.0F;
            this.adjustTargetX = x;
            this.adjustTargetZ = z;
         }
      }

      public void adjustToLocalPosition(double x, double z, float adjustingSpeed) {
         if (this.adjustingProgress == 1.0F) {
            this.adjustingSpeed = adjustingSpeed;
            this.adjustingProgress = 0.0F;
            float bodyYaw = ((EntitySpider)this.data.entity).renderYawOffset / 180.0F * 3.1415927F;
            this.adjustTargetX = x * Math.cos((double)bodyYaw) - z * Math.sin((double)bodyYaw) + this.data.getPositionX();
            this.adjustTargetZ = x * Math.sin((double)bodyYaw) + z * Math.cos((double)bodyYaw) + this.data.getPositionZ();
         }
      }

      public void setLocalPosition(double x, double z) {
         this.adjustingProgress = 1.0F;
         float bodyYaw = ((EntitySpider)this.data.entity).renderYawOffset / 180.0F * 3.1415927F;
         this.worldX = this.adjustTargetX = x * Math.cos((double)bodyYaw) - z * Math.sin((double)bodyYaw) + this.data.getPositionX();
         this.worldZ = this.adjustTargetZ = x * Math.sin((double)bodyYaw) + z * Math.cos((double)bodyYaw) + this.data.getPositionZ();
      }

      public SpiderData.IKResult solveIK(double bodyX, double bodyZ, float pt) {
         double renderYawOffset = (double)((((EntitySpider)this.data.entity).prevRenderYawOffset + (((EntitySpider)this.data.entity).renderYawOffset - ((EntitySpider)this.data.entity).prevRenderYawOffset) * pt) / 180.0F) * 3.141592653589793D;
         double spiderX = ((EntitySpider)this.data.entity).prevPosX + (((EntitySpider)this.data.entity).posX - ((EntitySpider)this.data.entity).prevPosX) * (double)pt;
         double spiderZ = ((EntitySpider)this.data.entity).prevPosZ + (((EntitySpider)this.data.entity).posZ - ((EntitySpider)this.data.entity).prevPosZ) * (double)pt;
         double worldLimbX = this.prevWorldX + (this.worldX - this.prevWorldX) * (double)pt;
         double worldLimbZ = this.prevWorldZ + (this.worldZ - this.prevWorldZ) * (double)pt;
         double x = (worldLimbX - spiderX) / 0.0625D;
         double z = -(worldLimbZ - spiderZ) / 0.0625D;
         double localX = x * Math.cos(renderYawOffset) - z * Math.sin(renderYawOffset) - bodyX;
         double localZ = x * Math.sin(renderYawOffset) + z * Math.cos(renderYawOffset) - bodyZ;
         double deltaX = (double)this.upperPart.position.x - localX;
         double deltaZ = (double)this.upperPart.position.z - localZ;
         double xzDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
         double xzAngle = Math.atan2(deltaX, deltaZ);
         return new SpiderData.IKResult(worldLimbX, worldLimbZ, localX, localZ, deltaX, deltaZ, xzDistance, xzAngle);
      }

      public void applyIK(SpiderData.IKResult result, double groundLevel, double liftHeight, float pt) {
         double xzAngle = this.odd ? 1.5707963267948966D + result.xzAngle : -1.5707963267948966D + result.xzAngle;
         this.upperPart.rotation.orientY((float)(xzAngle / 3.141592653589793D * 180.0D));
         this.lowerPart.rotation.orientZero();
         SpiderController.putLimbOnGround(this.upperPart.rotation, this.lowerPart.rotation, this.odd, result.xzDistance, groundLevel - 7.0D + Math.sin((double)this.adjustingProgress * 3.141592653589793D) * liftHeight);
      }

      public double getNeutralYaw() {
         return this.neutralYaw;
      }

      public double getPrevWorldX() {
         return this.prevWorldX;
      }

      public double getPrevWorldZ() {
         return this.prevWorldZ;
      }

      public double getWorldX() {
         return this.worldX;
      }

      public double getWorldZ() {
         return this.worldZ;
      }

      public float getAdjustingProgress() {
         return this.adjustingProgress;
      }

      public boolean isOdd() {
         return this.odd;
      }
   }
}
