package ichttt.mods.firstaid.common.util;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.AABBAlignedBoundingBox;
import ichttt.mods.firstaid.common.damagesystem.distribution.StandardDamageDistribution;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Pair;

public class PlayerSizeHelper {
   private static final Map<EntityEquipmentSlot, AABBAlignedBoundingBox> NORMAL_BOXES;
   private static final Map<EntityEquipmentSlot, AABBAlignedBoundingBox> SNEAKING_BOXES;

   public PlayerSizeHelper() {
   }

   @Nonnull
   public static Map<EntityEquipmentSlot, AABBAlignedBoundingBox> getBoxes(Entity entity) {
      return entity.isSneaking() ? SNEAKING_BOXES : NORMAL_BOXES;
   }

   public static EntityEquipmentSlot getSlotTypeForProjectileHit(Entity hittingObject, EntityPlayer toTest) {
      Map<EntityEquipmentSlot, AABBAlignedBoundingBox> toUse = getBoxes(toTest);
      Vec3d oldPosition = new Vec3d(hittingObject.posX, hittingObject.posY, hittingObject.posZ);
      Vec3d newPosition = new Vec3d(hittingObject.posX + hittingObject.motionX, hittingObject.posY + hittingObject.motionY, hittingObject.posZ + hittingObject.motionZ);
      float[] inflationSteps = new float[]{0.01F, 0.1F, 0.2F, 0.3F};

      for(float inflation : inflationSteps) {
         EntityEquipmentSlot bestSlot = null;
         double bestValue = Double.MAX_VALUE;

         for(Entry<EntityEquipmentSlot, AABBAlignedBoundingBox> entry : toUse.entrySet()) {
            AxisAlignedBB axisalignedbb = ((AABBAlignedBoundingBox)entry.getValue()).createAABB(toTest.getEntityBoundingBox()).grow((double)inflation);
            RayTraceResult rtr = axisalignedbb.calculateIntercept(oldPosition, newPosition);
            if (rtr != null) {
               double d1 = oldPosition.squareDistanceTo(rtr.hitVec);
               double d2 = 0.0D;
               if (d1 + d2 < bestValue) {
                  bestSlot = (EntityEquipmentSlot)entry.getKey();
                  bestValue = d1 + d2;
               }
            }
         }

         if (bestSlot != null) {
            if (FirstAidConfig.debug) {
               FirstAid.LOGGER.info("getSlotTypeForProjectileHit: Inflation: " + inflation + " best slot: " + bestSlot);
            }

            return bestSlot;
         }
      }

      if (FirstAidConfig.debug) {
         FirstAid.LOGGER.info("getSlotTypeForProjectileHit: Not found!");
      }

      return null;
   }

   public static IDamageDistribution getMeleeDistribution(EntityPlayer player, DamageSource source) {
      Entity causingEntity = source.getTrueSource();
      if (causingEntity != null && causingEntity == source.getImmediateSource() && causingEntity instanceof EntityMob) {
         EntityMob mobEntity = (EntityMob)causingEntity;
         if (mobEntity.getAttackTarget() == player && mobEntity.tasks.taskEntries.stream().filter((entryx) -> {
            return entry.using;
         }).anyMatch((prioritizedGoal) -> {
            return prioritizedGoal.action instanceof EntityAIAttackMelee;
         })) {
            Map<EntityEquipmentSlot, AABBAlignedBoundingBox> boxes = getBoxes(player);
            if (!boxes.isEmpty()) {
               List<EntityEquipmentSlot> allowedParts = new ArrayList();
               AxisAlignedBB modAABB = mobEntity.getEntityBoundingBox().grow((double)(mobEntity.width * 2.0F + player.width), 0.0D, (double)(mobEntity.width * 2.0F + player.width));

               for(Entry<EntityEquipmentSlot, AABBAlignedBoundingBox> entry : boxes.entrySet()) {
                  AxisAlignedBB partAABB = ((AABBAlignedBoundingBox)entry.getValue()).createAABB(player.getEntityBoundingBox());
                  if (modAABB.intersects(partAABB)) {
                     allowedParts.add(entry.getKey());
                  }
               }

               if (FirstAidConfig.debug) {
                  FirstAid.LOGGER.info("getMeleeDistribution: Has distribution with {}", allowedParts);
               }

               if (allowedParts.isEmpty() && player.posY > mobEntity.posY && player.posY - mobEntity.posY < (double)(mobEntity.width * 2.0F)) {
                  if (FirstAidConfig.debug) {
                     FirstAid.LOGGER.info("Hack adding feet");
                  }

                  allowedParts.add(EntityEquipmentSlot.FEET);
               }

               if (!allowedParts.isEmpty() && !allowedParts.containsAll(Arrays.asList(CommonUtils.ARMOR_SLOTS))) {
                  List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> list = new ArrayList();

                  for(EntityEquipmentSlot allowedPart : allowedParts) {
                     list.add(Pair.of(allowedPart, CommonUtils.getPartArrayForSlot(allowedPart)));
                  }

                  return new StandardDamageDistribution(list, true, true);
               }
            }
         }
      }

      return null;
   }

   static {
      Map<EntityEquipmentSlot, AABBAlignedBoundingBox> builder = new LinkedHashMap();
      builder.put(EntityEquipmentSlot.FEET, new AABBAlignedBoundingBox(0.0D, 0.0D, 0.0D, 1.0D, 0.15D, 1.0D));
      builder.put(EntityEquipmentSlot.LEGS, new AABBAlignedBoundingBox(0.0D, 0.15D, 0.0D, 1.0D, 0.45D, 1.0D));
      builder.put(EntityEquipmentSlot.CHEST, new AABBAlignedBoundingBox(0.0D, 0.45D, 0.0D, 1.0D, 0.8D, 1.0D));
      builder.put(EntityEquipmentSlot.HEAD, new AABBAlignedBoundingBox(0.0D, 0.8D, 0.0D, 1.0D, 1.0D, 1.0D));
      NORMAL_BOXES = Collections.unmodifiableMap(builder);
      builder = new LinkedHashMap();
      builder.put(EntityEquipmentSlot.FEET, new AABBAlignedBoundingBox(0.0D, 0.0D, 0.0D, 1.0D, 0.15D, 1.0D));
      builder.put(EntityEquipmentSlot.LEGS, new AABBAlignedBoundingBox(0.0D, 0.15D, 0.0D, 1.0D, 0.4D, 1.0D));
      builder.put(EntityEquipmentSlot.CHEST, new AABBAlignedBoundingBox(0.0D, 0.4D, 0.0D, 1.0D, 0.75D, 1.0D));
      builder.put(EntityEquipmentSlot.HEAD, new AABBAlignedBoundingBox(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D));
      SNEAKING_BOXES = Collections.unmodifiableMap(builder);
   }
}
