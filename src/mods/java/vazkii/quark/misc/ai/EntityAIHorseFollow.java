package vazkii.quark.misc.ai;

import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import vazkii.quark.misc.feature.HorseWhistle;

public class EntityAIHorseFollow extends EntityAIBase {
   private final AbstractHorse horse;
   private EntityLivingBase owner;
   private final World world;
   private final double followSpeed;
   private final PathNavigate petPathfinder;
   private int timeToRebuildPath;
   private float oldWaterCost;
   private static final EaglercraftUUID HORSE_SIGHT = EaglercraftUUID.fromString("f58c267b-67fb-4997-b2a0-27649fb0aa9a");

   public EntityAIHorseFollow(AbstractHorse horse, double followSpeedIn) {
      this.horse = horse;
      this.world = horse.world;
      this.followSpeed = followSpeedIn;
      this.petPathfinder = horse.getNavigator();
      this.owner = null;
      this.horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(HORSE_SIGHT);
      this.setMutexBits(3);
      if (!(horse.getNavigator() instanceof PathNavigateGround) && !(horse.getNavigator() instanceof PathNavigateFlying)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   public void setOwner(EntityLivingBase player) {
      if (Objects.equals(player.getUniqueID(), this.horse.getOwnerUniqueId()) && !this.horse.getLeashed() && !this.horse.isBeingRidden()) {
         this.owner = player;
      }

   }

   public boolean shouldExecute() {
      return this.owner != null && !this.horse.getLeashed() && Objects.equals(this.owner.getUniqueID(), this.horse.getOwnerUniqueId()) && !this.horse.isBeingRidden();
   }

   public boolean shouldContinueExecuting() {
      return !this.petPathfinder.noPath() && this.owner != null;
   }

   public void startExecuting() {
      this.timeToRebuildPath = 0;
      this.oldWaterCost = this.horse.getPathPriority(PathNodeType.WATER);
      this.horse.setPathPriority(PathNodeType.WATER, 0.0F);
      this.horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier(HORSE_SIGHT, "Whistle range bonus", HorseWhistle.horseSummonRange, 0));
   }

   public void resetTask() {
      this.owner = null;
      this.petPathfinder.clearPath();
      this.horse.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
      this.horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(HORSE_SIGHT);
   }

   public void updateTask() {
      this.horse.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float)this.horse.getVerticalFaceSpeed());
      if (--this.timeToRebuildPath <= 0) {
         this.timeToRebuildPath = 10;
         if (!this.petPathfinder.tryMoveToEntityLiving(this.owner, this.followSpeed) && !this.horse.getLeashed() && !this.horse.isRiding() && this.horse.getDistanceSq(this.owner) >= HorseWhistle.horseSummonRange * HorseWhistle.horseSummonRange / 4.0D) {
            if (!HorseWhistle.horsesAreMagical) {
               return;
            }

            int ownerX = MathHelper.floor(this.owner.posX) - 2;
            int ownerZ = MathHelper.floor(this.owner.posZ) - 2;
            int ownerY = MathHelper.floor(this.owner.getEntityBoundingBox().minY);

            for(int xOffset = 0; xOffset <= 4; ++xOffset) {
               for(int zOffset = 0; zOffset <= 4; ++zOffset) {
                  if ((xOffset < 1 || zOffset < 1 || xOffset > 3 || zOffset > 3) && this.isTeleportFriendlyBlock(ownerX, ownerZ, ownerY, xOffset, zOffset)) {
                     this.horse.setLocationAndAngles((double)((float)(ownerX + xOffset) + 0.5F), (double)ownerY, (double)((float)(ownerZ + zOffset) + 0.5F), this.horse.rotationYaw, this.horse.rotationPitch);
                     this.petPathfinder.clearPath();
                     return;
                  }
               }
            }
         }
      }

   }

   protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset) {
      BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.horse) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
   }
}
