package vazkii.quark.world.entity.ai;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public class EntityAIFavorBlock extends EntityAIBase {
   private final EntityCreature creature;
   private final double movementSpeed;
   private final Predicate<IBlockState> targetBlock;
   protected int runDelay;
   private int timeoutCounter;
   private int maxStayTicks;
   protected BlockPos destinationBlock;

   public EntityAIFavorBlock(EntityCreature creature, double speed, Predicate<IBlockState> predicate) {
      this.destinationBlock = BlockPos.ORIGIN;
      this.creature = creature;
      this.movementSpeed = speed;
      this.targetBlock = predicate;
      this.setMutexBits(5);
   }

   public EntityAIFavorBlock(EntityCreature creature, double speed, Block block) {
      this(creature, speed, (state) -> {
         return state.getBlock() == block;
      });
   }

   public EntityAIFavorBlock(EntityCreature creature, double speed, IBlockState targetState) {
      this(creature, speed, (state) -> {
         return state == targetState;
      });
   }

   public boolean shouldExecute() {
      if (this.runDelay > 0) {
         --this.runDelay;
         return false;
      } else {
         this.runDelay = 200 + this.creature.getRNG().nextInt(200);
         return this.searchForDestination();
      }
   }

   public boolean shouldContinueExecuting() {
      return this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.targetBlock.test(this.creature.world.getBlockState(this.destinationBlock));
   }

   public void startExecuting() {
      this.creature.getNavigator().tryMoveToXYZ((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, this.movementSpeed);
      this.timeoutCounter = 0;
      this.maxStayTicks = this.creature.getRNG().nextInt(this.creature.getRNG().nextInt(1200) + 1200) + 1200;
   }

   public void updateTask() {
      if (this.creature.getDistanceSqToCenter(this.destinationBlock.up()) > 1.0D) {
         ++this.timeoutCounter;
         if (this.timeoutCounter % 40 == 0) {
            this.creature.getNavigator().tryMoveToXYZ((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, this.movementSpeed);
         }
      } else {
         --this.timeoutCounter;
      }

   }

   private boolean searchForDestination() {
      double followRange = this.creature.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
      double xBase = this.creature.posX;
      double yBase = this.creature.posY;
      double zBase = this.creature.posZ;
      MutableBlockPos pos = new MutableBlockPos();

      for(int yShift = 0; yShift <= 1; yShift = yShift > 0 ? -yShift : 1 - yShift) {
         for(int seekDist = 0; (double)seekDist < followRange; ++seekDist) {
            for(int xShift = 0; xShift <= seekDist; xShift = xShift > 0 ? -xShift : 1 - xShift) {
               for(int zShift = xShift < seekDist && xShift > -seekDist ? seekDist : 0; zShift <= seekDist; zShift = zShift > 0 ? -zShift : 1 - zShift) {
                  pos.setPos(xBase + (double)xShift, yBase + (double)yShift - 1.0D, zBase + (double)zShift);
                  if (this.creature.isWithinHomeDistanceFromPosition(pos) && this.targetBlock.test(this.creature.world.getBlockState(pos))) {
                     this.destinationBlock = pos;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }
}
