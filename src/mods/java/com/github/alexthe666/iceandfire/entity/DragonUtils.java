package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class DragonUtils {
   public DragonUtils() {
   }

   public static BlockPos getBlockInView(EntityDragonBase dragon) {
      float radius = 0.75F * (0.7F * dragon.getRenderSize() / 3.0F) * -7.0F - (float)dragon.getRNG().nextInt(dragon.getDragonStage() * 6);
      float neg = dragon.getRNG().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = dragon.renderYawOffset;
      if (dragon.hasHomePosition && dragon.homePos != null) {
         BlockPos dragonPos = new BlockPos(dragon);
         BlockPos ground = dragon.world.getHeight(dragonPos);
         int distFromGround = (int)dragon.posY - ground.getY();

         for(int i = 0; i < 10; ++i) {
            BlockPos pos = new BlockPos(dragon.homePos.getX() + dragon.getRNG().nextInt(IceAndFire.CONFIG.dragonWanderFromHomeDistance) - IceAndFire.CONFIG.dragonWanderFromHomeDistance, distFromGround > 16 ? (int)Math.min((double)IceAndFire.CONFIG.maxDragonFlight, dragon.posY + (double)dragon.getRNG().nextInt(16) - 8.0D) : (int)dragon.posY + dragon.getRNG().nextInt(16) + 1, dragon.homePos.getZ() + dragon.getRNG().nextInt(IceAndFire.CONFIG.dragonWanderFromHomeDistance * 2) - IceAndFire.CONFIG.dragonWanderFromHomeDistance);
            if (!dragon.isTargetBlocked(new Vec3d(pos)) && dragon.getDistanceSqToCenter(pos) > 6.0D) {
               return pos;
            }
         }
      }

      float angle = 0.017453292F * renderYawOffset + 3.15F + dragon.getRNG().nextFloat() * neg;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      BlockPos radialPos = new BlockPos(dragon.posX + extraX, 0.0D, dragon.posZ + extraZ);
      BlockPos ground = dragon.world.getHeight(radialPos);
      int distFromGround = (int)dragon.posY - ground.getY();
      BlockPos newPos = radialPos.up(distFromGround > 16 ? (int)Math.min((double)IceAndFire.CONFIG.maxDragonFlight, dragon.posY + (double)dragon.getRNG().nextInt(16) - 8.0D) : (int)dragon.posY + dragon.getRNG().nextInt(16) + 1);
      BlockPos var10000 = dragon.doesWantToLand() ? ground : newPos;
      return !dragon.isTargetBlocked(new Vec3d(newPos)) && dragon.getDistanceSqToCenter(newPos) > 6.0D ? newPos : null;
   }

   public static BlockPos getWaterBlockInView(EntityDragonBase dragon) {
      float radius = 0.75F * (0.7F * dragon.getRenderSize() / 3.0F) * -7.0F - (float)dragon.getRNG().nextInt(dragon.getDragonStage() * 6);
      float neg = dragon.getRNG().nextBoolean() ? 1.0F : -1.0F;
      float angle = 0.017453292F * dragon.renderYawOffset + 3.15F + dragon.getRNG().nextFloat() * neg;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      BlockPos radialPos = new BlockPos(dragon.posX + extraX, 0.0D, dragon.posZ + extraZ);
      BlockPos ground = dragon.world.getHeight(radialPos);
      int distFromGround = (int)dragon.posY - ground.getY();
      BlockPos newPos = radialPos.up(distFromGround > 16 ? (int)Math.min((double)IceAndFire.CONFIG.maxDragonFlight, dragon.posY + (double)dragon.getRNG().nextInt(16) - 8.0D) : (int)dragon.posY + dragon.getRNG().nextInt(16) + 1);
      BlockPos var10000 = dragon.doesWantToLand() ? ground : newPos;
      BlockPos surface = dragon.world.getBlockState(newPos.down(2)).getMaterial() != Material.WATER ? newPos.down(dragon.getRNG().nextInt(10) + 1) : newPos;
      return dragon.getDistanceSqToCenter(surface) > 6.0D && dragon.world.getBlockState(surface).getMaterial() == Material.WATER ? surface : null;
   }

   public static EntityLivingBase riderLookingAtEntity(final EntityLivingBase dragon, EntityLivingBase rider, double dist) {
      Vec3d vec3d = rider.getPositionEyes(1.0F);
      Vec3d vec3d1 = rider.getLook(1.0F);
      Vec3d vec3d2 = vec3d.addVector(vec3d1.x * dist, vec3d1.y * dist, vec3d1.z * dist);
      Entity pointedEntity = null;
      List<Entity> list = rider.world.getEntitiesInAABBexcluding(rider, rider.getEntityBoundingBox().expand(vec3d1.x * dist, vec3d1.y * dist, vec3d1.z * dist).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
         public boolean apply(@Nullable Entity entity) {
            return entity != null && entity.canBeCollidedWith() && entity instanceof EntityLivingBase && !entity.isEntityEqual(dragon) && !entity.isOnSameTeam(dragon) && (!(entity instanceof IDeadMob) || !((IDeadMob)entity).isMobDead());
         }
      }));
      double d2 = dist;

      for(int j = 0; j < list.size(); ++j) {
         Entity entity1 = (Entity)list.get(j);
         AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize() + 2.0D);
         RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
         if (axisalignedbb.contains(vec3d)) {
            if (d2 >= 0.0D) {
               pointedEntity = entity1;
               d2 = 0.0D;
            }
         } else if (raytraceresult != null) {
            double d3 = vec3d.distanceTo(raytraceresult.hitVec);
            if (d3 < d2 || d2 == 0.0D) {
               if (entity1.getLowestRidingEntity() == rider.getLowestRidingEntity() && !rider.canRiderInteract()) {
                  if (d2 == 0.0D) {
                     pointedEntity = entity1;
                  }
               } else {
                  pointedEntity = entity1;
                  d2 = d3;
               }
            }
         }
      }

      return (EntityLivingBase)pointedEntity;
   }

   public static BlockPos getBlockInViewHippogryph(EntityHippogryph hippo) {
      float radius = -12.599999F - (float)hippo.getRNG().nextInt(48);
      float neg = hippo.getRNG().nextBoolean() ? 1.0F : -1.0F;
      float angle = 0.017453292F * hippo.renderYawOffset + 3.15F + hippo.getRNG().nextFloat() * neg;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      if (hippo.hasHomePosition && hippo.homePos != null) {
         BlockPos dragonPos = new BlockPos(hippo);
         BlockPos ground = hippo.world.getHeight(dragonPos);
         int distFromGround = (int)hippo.posY - ground.getY();

         for(int i = 0; i < 10; ++i) {
            BlockPos pos = new BlockPos(hippo.homePos.getX() + hippo.getRNG().nextInt(IceAndFire.CONFIG.dragonWanderFromHomeDistance) - IceAndFire.CONFIG.dragonWanderFromHomeDistance, distFromGround > 16 ? (int)Math.min((double)IceAndFire.CONFIG.maxDragonFlight, hippo.posY + (double)hippo.getRNG().nextInt(16) - 8.0D) : (int)hippo.posY + hippo.getRNG().nextInt(16) + 1, hippo.homePos.getZ() + hippo.getRNG().nextInt(IceAndFire.CONFIG.dragonWanderFromHomeDistance * 2) - IceAndFire.CONFIG.dragonWanderFromHomeDistance);
            if (!hippo.isTargetBlocked(new Vec3d(pos)) && hippo.getDistanceSqToCenter(pos) > 6.0D) {
               return pos;
            }
         }
      }

      BlockPos radialPos = new BlockPos(hippo.posX + extraX, 0.0D, hippo.posZ + extraZ);
      BlockPos ground = hippo.world.getHeight(radialPos);
      int distFromGround = (int)hippo.posY - ground.getY();
      BlockPos newPos = radialPos.up(distFromGround > 16 ? (int)Math.min((double)IceAndFire.CONFIG.maxDragonFlight, hippo.posY + (double)hippo.getRNG().nextInt(16) - 8.0D) : (int)hippo.posY + hippo.getRNG().nextInt(16) + 1);
      BlockPos var10000 = hippo.doesWantToLand() ? ground : newPos;
      return !hippo.isTargetBlocked(new Vec3d(newPos)) && hippo.getDistanceSqToCenter(newPos) > 6.0D ? newPos : null;
   }

   public static BlockPos getBlockInViewStymphalian(EntityStymphalianBird bird) {
      float radius = -9.45F - (float)bird.getRNG().nextInt(24);
      float neg = bird.getRNG().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = bird.flock != null && !bird.flock.isLeader(bird) ? getStymphalianFlockDirection(bird) : bird.renderYawOffset;
      float angle = 0.017453292F * renderYawOffset + 3.15F + bird.getRNG().nextFloat() * neg;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      BlockPos radialPos = getStymphalianFearPos(bird, new BlockPos(bird.posX + extraX, 0.0D, bird.posZ + extraZ));
      BlockPos ground = bird.world.getHeight(radialPos);
      int distFromGround = (int)bird.posY - ground.getY();
      int flightHeight = Math.min(IceAndFire.CONFIG.stymphalianBirdFlightHeight, bird.flock != null && !bird.flock.isLeader(bird) ? ground.getY() + bird.getRNG().nextInt(16) : ground.getY() + bird.getRNG().nextInt(16));
      BlockPos newPos = radialPos.up(distFromGround > 16 ? flightHeight : (int)bird.posY + bird.getRNG().nextInt(16) + 1);
      BlockPos var10000 = bird.doesWantToLand() ? ground : newPos;
      return !bird.isTargetBlocked(new Vec3d(newPos)) && bird.getDistanceSqToCenter(newPos) > 6.0D ? newPos : null;
   }

   private static BlockPos getStymphalianFearPos(EntityStymphalianBird bird, BlockPos fallback) {
      if (bird.getVictor() != null && bird.getVictor() instanceof EntityCreature) {
         Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom((EntityCreature)bird.getVictor(), 16, IceAndFire.CONFIG.stymphalianBirdFlightHeight, new Vec3d(bird.getVictor().posX, bird.getVictor().posY, bird.getVictor().posZ));
         if (vec3d != null) {
            BlockPos pos = new BlockPos(vec3d);
            return new BlockPos(pos.getX(), 0, pos.getZ());
         }
      }

      return fallback;
   }

   private static float getStymphalianFlockDirection(EntityStymphalianBird bird) {
      EntityStymphalianBird leader = bird.flock.getLeader();
      if (bird.getDistanceSq(leader) > 2.0D) {
         double d0 = leader.posX - bird.posX;
         double d2 = leader.posZ - bird.posZ;
         double var10000 = leader.posY + (double)leader.getEyeHeight() - (bird.posY + (double)bird.getEyeHeight());
         double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
         float f = (float)(MathHelper.atan2(d2, d0) * 57.29577951308232D) - 90.0F;
         float degrees = MathHelper.wrapDegrees(f - bird.rotationYaw);
         return bird.rotationYaw + degrees;
      } else {
         return leader.renderYawOffset;
      }
   }

   public static BlockPos getBlockInTargetsViewCockatrice(EntityCockatrice cockatrice, EntityLivingBase target) {
      float radius = (float)(10 + cockatrice.getRNG().nextInt(10));
      float neg = cockatrice.getRNG().nextBoolean() ? 1.0F : -1.0F;
      float angle = 0.017453292F * target.rotationYawHead;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      BlockPos radialPos = new BlockPos(target.posX + extraX, 0.0D, target.posZ + extraZ);
      BlockPos ground = target.world.getHeight(radialPos);
      return !cockatrice.isTargetBlocked(new Vec3d(ground)) && cockatrice.getDistanceSqToCenter(ground) > 30.0D ? ground : target.getPosition();
   }

   public static BlockPos getBlockInTargetsViewSeaSerpent(EntitySeaSerpent serpent, EntityLivingBase target) {
      float radius = 10.0F * serpent.getSeaSerpentScale() + (float)serpent.getRNG().nextInt(10);
      float neg = serpent.getRNG().nextBoolean() ? 1.0F : -1.0F;
      float angle = 0.017453292F * target.rotationYawHead;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      BlockPos radialPos = new BlockPos(target.posX + extraX, 0.0D, target.posZ + extraZ);
      BlockPos ground = target.world.getHeight(radialPos);
      return serpent.getDistanceSqToCenter(ground) > 30.0D ? ground : target.getPosition();
   }

   public static boolean canTameDragonAttack(EntityTameable dragon, Entity entity) {
      String className = entity.getClass().getSimpleName();
      if (!className.contains("VillagerMCA") && !className.contains("MillVillager") && !className.contains("Citizen")) {
         if (!(entity instanceof EntityVillager) && !(entity instanceof EntityGolem) && !(entity instanceof EntityPlayer)) {
            if (entity instanceof EntityTameable) {
               return !((EntityTameable)entity).isTamed();
            } else {
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean isVillager(Entity entity) {
      String className = entity.getClass().getSimpleName();
      return entity instanceof INpc || className.contains("VillagerMCA") || className.contains("MillVillager") || className.contains("Citizen");
   }

   public static boolean isAnimaniaMob(Entity entity) {
      String className = entity.getClass().getCanonicalName().toLowerCase();
      return className.contains("animania");
   }

   public static boolean isLivestock(Entity entity) {
      String className = entity.getClass().getSimpleName();
      return entity instanceof EntityCow || entity instanceof EntitySheep || entity instanceof EntityPig || entity instanceof EntityChicken || entity instanceof EntityRabbit || entity instanceof AbstractHorse || className.contains("Cow") || className.contains("Sheep") || className.contains("Pig") || className.contains("Chicken") || className.contains("Rabbit") || className.contains("Peacock") || className.contains("Goat") || className.contains("Ferret") || className.contains("Hedgehog") || className.contains("Peahen") || className.contains("Peafowl") || className.contains("Sow") || className.contains("Hog") || className.contains("Hog");
   }

   public static boolean canDragonBreak(Block block) {
      return block != Blocks.BARRIER && block != Blocks.OBSIDIAN && block != Blocks.END_STONE && block != Blocks.BEDROCK && block != Blocks.END_PORTAL && block != Blocks.END_PORTAL_FRAME && block != Blocks.COMMAND_BLOCK && block != Blocks.REPEATING_COMMAND_BLOCK && block != Blocks.CHAIN_COMMAND_BLOCK && block != Blocks.IRON_BARS && block != Blocks.END_GATEWAY;
   }

   public static boolean hasSameOwner(EntityTameable cockatrice, Entity entity) {
      if (entity instanceof EntityTameable) {
         EntityTameable tameable = (EntityTameable)entity;
         if (tameable.getOwnerId() != null && cockatrice.getOwnerId() != null && tameable.getOwnerId().equals(cockatrice.getOwnerId())) {
            return true;
         }
      }

      return false;
   }

   public static boolean isAlive(EntityLivingBase entity) {
      return !(entity instanceof IDeadMob) || !((IDeadMob)entity).isMobDead();
   }

   public static boolean canGrief(boolean weak) {
      if (weak) {
         return IceAndFire.CONFIG.dragonGriefing == 0;
      } else {
         return IceAndFire.CONFIG.dragonGriefing < 2;
      }
   }
}
