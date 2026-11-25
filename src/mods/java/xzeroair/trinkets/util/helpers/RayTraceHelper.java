package xzeroair.trinkets.util.helpers;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import xzeroair.trinkets.Trinkets;

public class RayTraceHelper {
   private static final Predicate<Entity> Targets = Predicates.and(new Predicate[]{EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, (ent) -> {
      return ent != null && ent.canBeCollidedWith();
   }});
   private static List<Vec3d> points = new ArrayList();
   private static Vec3d first;
   private static Vec3d second;
   private static Vec3d third;
   private static Vec3d forth;

   public RayTraceHelper() {
   }

   public static RayTraceResult rayTrace(EntityLivingBase entity, boolean useLiquids) {
      double distance = 5.0D;
      if (entity instanceof EntityPlayer) {
         distance = entity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
      }

      return rayTrace(entity.world, entity, distance, useLiquids, !useLiquids);
   }

   public static RayTraceResult rayTrace(World world, EntityLivingBase entity, boolean useLiquids) {
      double distance = 5.0D;
      if (entity instanceof EntityPlayer) {
         distance = entity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
      }

      return rayTrace(world, entity, distance, useLiquids, !useLiquids);
   }

   public static RayTraceResult rayTrace(EntityLivingBase entity, double distance) {
      return rayTrace(entity.world, entity, distance, false, true);
   }

   public static RayTraceResult rayTrace(EntityLivingBase entity, double distance, boolean useLiquids) {
      return rayTrace(entity.world, entity, distance, useLiquids, !useLiquids);
   }

   public static RayTraceResult rayTrace(World world, EntityLivingBase entity, double distance, boolean useLiquids) {
      return rayTrace(world, entity, distance, useLiquids, !useLiquids);
   }

   public static RayTraceResult rayTrace(World world, EntityLivingBase entity, double distance, boolean useLiquids, boolean ignoreBlockWithoutBoundingBox) {
      Vec3d start = entity.getPositionEyes(1.0F);
      Vec3d lookVec = entity.getLookVec();
      Vec3d end = start.addVector(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
      RayTraceResult result = getRaytraceResult(world, entity, start, end, distance, useLiquids, ignoreBlockWithoutBoundingBox, (Predicate)null);
      return result;
   }

   public static RayTraceResult getRaytraceResult(World world, EntityLivingBase entity, Vec3d start, Vec3d end, double distance, boolean useLiquids, boolean ignoreBlockWithoutBoundingBox, Predicate<Entity> excluding) {
      RayTraceResult result = world.rayTraceBlocks(start, end, useLiquids, ignoreBlockWithoutBoundingBox, false);
      Vec3d lookVec = entity.getLookVec();
      if (result != null && result.typeOfHit == Type.BLOCK) {
         BlockPos pos = result.getBlockPos();
         if (world.getBlockState(pos).getCollisionBoundingBox(world, pos) != Block.NULL_AABB) {
            distance = result.hitVec.distanceTo(start);
            end = start.addVector(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
         }
      }

      Predicate<Entity> predicate = Predicates.and(EntitySelectors.NOT_SPECTATING, (ent) -> {
         return ent != null && ent.canBeCollidedWith();
      });
      Entity hitEntity = findEntityOnPath(entity, start, end, distance, excluding == null ? predicate : excluding);
      if (hitEntity != null) {
         result = new RayTraceResult(hitEntity);
      }

      return result;
   }

   public static Entity findEntityOnPath(EntityLivingBase entity, Vec3d start, Vec3d end, double distance, Predicate<Entity> excluding) {
      Vec3d lookVec = entity.getLookVec();
      List<Entity> targets = entity.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance).grow(1.0D, 1.0D, 1.0D), excluding);
      List<Pair<Entity, Double>> hitTargets = new ArrayList();

      for(Entity target : targets) {
         AxisAlignedBB targetBB = target.getEntityBoundingBox().grow((double)target.getCollisionBorderSize());
         if (targetBB.contains(start)) {
            hitTargets.add(Pair.of(target, Double.valueOf(0.0D)));
         } else {
            RayTraceResult targetResult = targetBB.calculateIntercept(start, end);
            if (targetResult != null) {
               double d3 = start.distanceTo(targetResult.hitVec);
               if (d3 < distance) {
                  hitTargets.add(Pair.of(target, Double.valueOf(d3)));
               }
            }
         }
      }

      if (!hitTargets.isEmpty()) {
         hitTargets.sort(Comparator.comparing(Pair::getRight));
         Entity hitEntity = (Entity)((Pair)hitTargets.stream().findFirst().get()).getLeft();
         return hitEntity;
      } else {
         return null;
      }
   }

   public static Entity findEntityOnPath(EntityLivingBase entity, Vec3d start, Vec3d end) {
      Entity hitEntity = null;
      List<Entity> list = entity.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(entity.motionX, entity.motionY, entity.motionZ).grow(1.0D), Targets);
      double d0 = 0.0D;

      for(Entity entity1 : list) {
         if (entity1 != entity) {
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
            if (raytraceresult != null) {
               double d1 = start.squareDistanceTo(raytraceresult.hitVec);
               if (d1 < d0 || d0 == 0.0D) {
                  hitEntity = entity1;
                  d0 = d1;
               }
            }
         }
      }

      return hitEntity;
   }

   public static void rayTraceEntity(RayTraceHelper.Beam beam, Function<Entity, Boolean> consumer) {
      Vec3d start = beam.getStart();
      Vec3d lookVec = beam.getLookVec();
      Vec3d end = beam.getEnd();
      double dist = beam.getDist();
      World world = beam.getWorld();
      EntityLivingBase player = beam.getEntity();
      List<Entity> targets = world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().expand(lookVec.x * dist, lookVec.y * dist, lookVec.z * dist).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, (ent) -> {
         return (void)(ent != null && ent.canBeCollidedWith());
      }));
      List<Pair<Entity, Double>> hitTargets = new ArrayList();

      for(Entity target : targets) {
         AxisAlignedBB targetBB = target.getEntityBoundingBox().grow((double)target.getCollisionBorderSize());
         if (targetBB.contains(start)) {
            hitTargets.add(Pair.of(target, Double.valueOf(0.0D)));
         } else {
            RayTraceResult targetResult = targetBB.calculateIntercept(start, end);
            if (targetResult != null) {
               double d3 = start.distanceTo(targetResult.hitVec);
               if (d3 < dist) {
                  hitTargets.add(Pair.of(target, Double.valueOf(d3)));
               }
            }
         }
      }

      if (!hitTargets.isEmpty()) {
         hitTargets.sort(Comparator.comparing(Pair::getRight));
         hitTargets.stream().filter((pair) -> {
            return (void)((Boolean)consumer.apply(pair.getLeft())).booleanValue();
         }).findFirst();
      } else {
         consumer.apply((Object)null);
      }

   }

   public static void drawLightning(Vec3d start, Vec3d end, World world, double dist, double density, float intensity) {
      int color = 2515356;
      Trinkets.proxy.renderEffect(1, world, start.x, start.y, start.z, end.x, end.y, end.z, 2515356, 0.9F, intensity);
      Trinkets.proxy.renderEffect(2, world, start.x, start.y, start.z, end.x, end.y, end.z, 2515356, 0.9F, intensity);
   }

   public static void drawPath(Vec3d start, Vec3d end, World world, RayTraceHelper.Beam beam, int color, double density) {
      Random rand = new Random();
      double dist = beam.getDist();
      double r1 = rand.nextDouble() * (double)(rand.nextBoolean() ? -1 : 1);
      double r2 = rand.nextDouble() * (double)(rand.nextBoolean() ? -1 : 1);
      double r3 = rand.nextDouble() * (double)(rand.nextBoolean() ? -1 : 1);
      first = start;

      for(double done = 0.0D; done < dist; done += density) {
         double alpha = done / dist;
         double x = interpolate(end.x + 0.5D, start.x, alpha);
         double y = interpolate(end.y + 0.5D, start.y, alpha);
         double z = interpolate(end.z + 0.5D, start.z, alpha);
         int widthRange = rand.nextInt(6) * (rand.nextBoolean() ? -1 : 1);
         int widthRange2 = rand.nextInt(3) * (rand.nextBoolean() ? -1 : 1);
         int widthRange3 = rand.nextInt(6) * (rand.nextBoolean() ? -1 : 1);
         double t1 = (double)((float)widthRange * 0.1F);
         double t2 = (double)((float)widthRange2 * 0.1F);
         double t3 = (double)((float)widthRange3 * 0.1F);
         second = new Vec3d(x + r1, y + r2, z + r3);
         if (world.isRemote) {
            ;
         }

         first = second;
      }

   }

   public static double interpolate(double start, double end, double alpha) {
      return start + (end - start) * alpha;
   }

   public static double interpolateReverse(double start, double end, double alpha) {
      return end + (end - start) * alpha;
   }

   public static class Beam {
      private final World world;
      private final EntityLivingBase player;
      private final double maxDist;
      private Vec3d start;
      private Vec3d lookVec;
      private Vec3d end;
      private double dist;
      private double accuracy;
      private final boolean canBreak;
      private final Random rand = new Random();

      public Beam(World world, EntityLivingBase player, double maxDist, double accuracy, boolean canBreak) {
         this.world = world;
         this.player = player;
         this.maxDist = maxDist;
         accuracy = accuracy / 2.0D;
         this.canBreak = canBreak;
         this.calculate();
      }

      private void calculate() {
         float prevYaw = this.player.rotationYaw;
         float prevPitch = this.player.rotationPitch;
         this.start = this.player.getPositionEyes(1.0F);
         this.player.rotationYaw = (float)((double)this.player.rotationYaw + (2.0D * this.accuracy * this.rand.nextDouble() - this.accuracy));
         this.player.rotationYaw %= 360.0F;
         this.player.rotationPitch = (float)((double)this.player.rotationPitch + (2.0D * this.accuracy * this.rand.nextDouble() - this.accuracy));
         this.lookVec = this.player.getLookVec();
         this.player.rotationYaw = prevYaw;
         this.player.rotationPitch = prevPitch;
         this.end = this.start.addVector(this.lookVec.x * this.maxDist, this.lookVec.y * this.maxDist, this.lookVec.z * this.maxDist);
         RayTraceResult result = this.world.rayTraceBlocks(this.start, this.end);
         this.dist = this.maxDist;
         if (result != null && result.typeOfHit == Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            if (this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos) != Block.NULL_AABB) {
               this.dist = result.hitVec.distanceTo(this.start);
               this.end = this.start.addVector(this.lookVec.x * this.dist, this.lookVec.y * this.dist, this.lookVec.z * this.dist);
            }
         }

      }

      public Vec3d getStart() {
         return this.start;
      }

      public Vec3d getLookVec() {
         return this.lookVec;
      }

      public Vec3d getEnd() {
         return this.end;
      }

      public double getDist() {
         return this.dist;
      }

      public World getWorld() {
         return this.world;
      }

      public EntityLivingBase getEntity() {
         return this.player;
      }

      public double getMaxDist() {
         return this.maxDist;
      }
   }
}
