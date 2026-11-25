package vazkii.quark.world.entity.ai;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.player.EntityPlayer;
import vazkii.quark.base.util.MutableVectorHolder;
import vazkii.quark.world.entity.EntityStoneling;

public class EntityAIActWary extends EntityAIWanderAvoidWater {
   private final EntityStoneling stoneling;
   private final boolean scaredBySuddenMovement;
   private final double range;
   private boolean startled;
   private final Map<EntityPlayer, MutableVectorHolder> lastPositions = new WeakHashMap();
   private final Map<EntityPlayer, MutableVectorHolder> lastSpeeds = new WeakHashMap();

   public EntityAIActWary(EntityStoneling stoneling, double speed, double range, boolean scaredBySuddenMovement) {
      super(stoneling, speed, 1.0F);
      this.stoneling = stoneling;
      this.range = range;
      this.scaredBySuddenMovement = scaredBySuddenMovement;
   }

   private static void updateMotion(MutableVectorHolder holder, double x, double y, double z) {
      holder.x = x;
      holder.y = y;
      holder.z = z;
   }

   private static void updatePos(MutableVectorHolder holder, Entity entity) {
      holder.x = entity.posX;
      holder.y = entity.posY;
      holder.z = entity.posZ;
   }

   private static MutableVectorHolder initPos(EntityPlayer p) {
      MutableVectorHolder holder = new MutableVectorHolder();
      updatePos(holder, p);
      return holder;
   }

   public void startle() {
      this.startled = true;
   }

   protected boolean shouldApplyPath() {
      return super.shouldExecute();
   }

   public void updateTask() {
      if (this.stoneling.getNavigator().noPath() && this.shouldApplyPath()) {
         this.startExecuting();
      }

   }

   public boolean shouldContinueExecuting() {
      return this.shouldExecute();
   }

   public void resetTask() {
      this.stoneling.getNavigator().clearPath();
   }

   public boolean shouldExecute() {
      if (!this.startled && !this.stoneling.isPlayerMade()) {
         List<EntityPlayer> playersAround = this.stoneling.world.getEntitiesWithinAABB(EntityPlayer.class, this.stoneling.getEntityBoundingBox().grow(this.range), (playerx) -> {
            return player != null && !player.capabilities.isCreativeMode && player.getDistanceSq(this.stoneling) < this.range * this.range;
         });
         if (playersAround.isEmpty()) {
            return false;
         } else {
            for(EntityPlayer player : playersAround) {
               if (!player.isSneaking()) {
                  this.startled = true;
                  return false;
               }

               if (this.scaredBySuddenMovement) {
                  MutableVectorHolder lastSpeed = (MutableVectorHolder)this.lastSpeeds.computeIfAbsent(player, (p) -> {
                     return new MutableVectorHolder();
                  });
                  MutableVectorHolder lastPos = (MutableVectorHolder)this.lastPositions.computeIfAbsent(player, EntityAIActWary::initPos);
                  double dX = player.posX - lastPos.x;
                  double dY = player.posY - lastPos.y;
                  double dZ = player.posZ - lastPos.z;
                  double xDisplacement = dX - lastSpeed.x;
                  double yDisplacement = dY - lastSpeed.y;
                  double zDisplacement = dZ - lastSpeed.z;
                  updateMotion(lastSpeed, dX, dY, dZ);
                  updatePos(lastPos, player);
                  double displacementSq = xDisplacement * xDisplacement + yDisplacement * yDisplacement + zDisplacement * zDisplacement;
                  if (displacementSq < 0.01D) {
                     return true;
                  }

                  this.startled = true;
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
