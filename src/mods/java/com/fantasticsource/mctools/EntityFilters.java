package com.fantasticsource.mctools;

import com.fantasticsource.lwjgl.Quaternion;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

public class EntityFilters {
   public static final LinkedHashMap<Integer, String> INLUSION_MODES = new LinkedHashMap();
   public static final int INCLUSION_MODE_ENTITY_POSITION = 0;
   public static final int INCLUSION_MODE_GEOMETRIC_CENTER = 1;
   public static final int INCLUSION_MODE_RECTANGULAR_PRISM_ANY = 2;
   public static final int INCLUSION_MODE_RECTANGULAR_PRISM_FULL = 3;
   public static final int INCLUSION_MODE_CYLINDER_ANY = 4;
   public static final int INCLUSION_MODE_CYLINDER_FULL = 5;
   private static final double DEFAULT_DISTRIBUTED_RAYTRACE_SPACING = 0.5D;

   public EntityFilters() {
   }

   public static ArrayList<Entity> inWorld(World world, ArrayList<Entity> entitiesToCheck) {
      entitiesToCheck.removeIf((entity) -> {
         return entity.world != world;
      });
      return entitiesToCheck;
   }

   public static ArrayList<Entity> inCube(Vec3d origin, double halfSize, int inclusionMode, ArrayList<Entity> entitiesToCheck) {
      double top = origin.y + halfSize;
      double bottom = origin.y - halfSize;
      double west = origin.x - halfSize;
      double east = origin.x + halfSize;
      double north = origin.z - halfSize;
      double south = origin.z + halfSize;
      switch(inclusionMode) {
      case 0:
         entitiesToCheck.removeIf((entity) -> {
            return entity.posX < west || entity.posX > east || entity.posZ < north || entity.posZ > south || entity.posY > top || entity.posY < bottom;
         });
         break;
      case 1:
         entitiesToCheck.removeIf((entity) -> {
            if (entity.posX >= west && entity.posX <= east && entity.posZ >= north && entity.posZ <= south) {
               double centerY = entity.posY + (double)entity.height * 0.5D;
               return centerY > top || centerY < bottom;
            } else {
               return true;
            }
         });
         break;
      case 2:
         entitiesToCheck.removeIf((entity) -> {
            double halfW = (double)entity.width * 0.5D;
            double eWest = entity.posX - halfW;
            double eEast = entity.posX + halfW;
            if (eWest <= east && eEast >= west) {
               double eNorth = entity.posZ - halfW;
               double eSouth = entity.posZ + halfW;
               if (eNorth <= south && eSouth >= north) {
                  double halfH = (double)entity.height * 0.5D;
                  double eTop = entity.posY + halfH;
                  double eBottom = entity.posY - halfH;
                  return eTop < bottom || eBottom > top;
               } else {
                  return true;
               }
            } else {
               return true;
            }
         });
         break;
      case 3:
      case 5:
         entitiesToCheck.removeIf((entity) -> {
            double halfW = (double)entity.width * 0.5D;
            double eWest = entity.posX - halfW;
            double eEast = entity.posX + halfW;
            if (eWest >= west && eEast <= east) {
               double eNorth = entity.posZ - halfW;
               double eSouth = entity.posZ + halfW;
               if (eNorth >= north && eSouth <= south) {
                  double halfH = (double)entity.height * 0.5D;
                  double eTop = entity.posY + halfH;
                  double eBottom = entity.posY - halfH;
                  return eTop > top || eBottom < bottom;
               } else {
                  return true;
               }
            } else {
               return true;
            }
         });
         break;
      case 4:
      default:
         throw new NotImplementedException("Mode not implemented for this method: " + inclusionMode);
      }

      return entitiesToCheck;
   }

   public static ArrayList<Entity> inSphere(Vec3d origin, double radius, int inclusionMode, ArrayList<Entity> entitiesToCheck) {
      double squareRadius = radius * radius;
      switch(inclusionMode) {
      case 0:
         entitiesToCheck.removeIf((entity) -> {
            return origin.squareDistanceTo(entity.getPositionVector()) > squareRadius;
         });
         break;
      case 1:
         entitiesToCheck.removeIf((entity) -> {
            return origin.squareDistanceTo(entity.getPositionVector().addVector(0.0D, (double)entity.height * 0.5D, 0.0D)) > squareRadius;
         });
         break;
      case 2:
      default:
         throw new NotImplementedException("Mode not implemented for this method: " + inclusionMode);
      case 3:
         entitiesToCheck.removeIf((entity) -> {
            double halfW = (double)entity.width * 0.5D;
            double halfH = (double)entity.height * 0.5D;
            Vec3d geoCenter = entity.getPositionVector().addVector(0.0D, halfH, 0.0D);
            return origin.squareDistanceTo(geoCenter.addVector(origin.x < geoCenter.x ? halfW : -halfW, origin.y < geoCenter.y ? halfH : -halfH, origin.z < geoCenter.z ? halfW : -halfW)) > squareRadius;
         });
      }

      return entitiesToCheck;
   }

   public static ArrayList<Entity> inCone(Vec3d origin, Vec3d direction, double range, double angle, boolean LOS, ArrayList<Entity> entitiesToCheck) {
      return inCone(origin, direction, range, angle, LOS, entitiesToCheck, 0.5D);
   }

   public static ArrayList<Entity> inCone(Vec3d origin, Vec3d direction, double range, double angle, boolean LOS, ArrayList<Entity> entitiesToCheck, double distributedRaytraceSpacing) {
      Vec3d pos2 = origin.add(direction);
      float yaw = (float)MCTools.getYawDeg(origin, pos2, TrigLookupTable.TRIG_TABLE_1024);
      float pitch = (float)MCTools.getPitchDeg(origin, pos2, TrigLookupTable.TRIG_TABLE_1024);
      return inCone(origin, yaw, pitch, range, angle, LOS, entitiesToCheck, distributedRaytraceSpacing);
   }

   public static ArrayList<Entity> inCone(Vec3d origin, float yaw, float pitch, double range, double angle, boolean LOS, ArrayList<Entity> entitiesToCheck) {
      return inCone(origin, yaw, pitch, range, angle, LOS, entitiesToCheck, 0.5D);
   }

   public static ArrayList<Entity> inCone(Vec3d origin, float yaw, float pitch, double range, double angle, boolean LOS, ArrayList<Entity> entitiesToCheck, double distributedRaytraceSpacing) {
      Vec3d coneAxisEnd = origin.add(Vec3d.fromPitchYaw(pitch, yaw).normalize().scale(range));
      double squareRange = origin.squareDistanceTo(coneAxisEnd);
      double halfAngle = angle / 2.0D;
      ExplicitPriorityQueue<Entity> queue = new ExplicitPriorityQueue<Entity>(entitiesToCheck.size());
      if (!LOS) {
         for(Entity target : entitiesToCheck) {
            Vec3d targetCenter = target.getPositionVector().addVector(0.0D, (double)target.height * 0.5D, 0.0D);
            double squareDist = origin.squareDistanceTo(targetCenter);
            if (squareDist <= squareRange && MCTools.angleDifDeg(origin, coneAxisEnd, targetCenter) <= halfAngle) {
               queue.add(target, squareDist);
            }
         }
      } else {
         for(Entity target : entitiesToCheck) {
            Vec3d targetCenter = target.getPositionVector().addVector(0.0D, (double)target.height * 0.5D, 0.0D);
            double squareDist = origin.squareDistanceTo(targetCenter);
            if (squareDist <= squareRange) {
               if (squareDist < Math.pow((double)(target.width / 2.0F), 2.0D)) {
                  queue.add(target, squareDist);
               } else if (ImprovedRayTracing.entityPenetration(target, origin, coneAxisEnd, true) >= 0.0D) {
                  queue.add(target, squareDist);
               } else if (halfAngle != 0.0D) {
                  double angleDif = MCTools.angleDifDeg(origin, coneAxisEnd, targetCenter);
                  if (angleDif <= halfAngle && ImprovedRayTracing.entityPenetration(target, origin, targetCenter, true) >= 0.0D) {
                     queue.add(target, squareDist);
                  } else {
                     double distance = Math.sqrt(squareDist);
                     double subConeStep = Tools.radtodeg(MCTools.TRIG_TABLE.arctan(distributedRaytraceSpacing / distance));
                     int subConeCount = (int)(halfAngle / subConeStep);
                     subConeStep = halfAngle / (double)subConeCount;
                     double subConeAngle = subConeStep;
                     Vec3d pitchYaw = Vec3d.fromPitchYaw(0.0F, yaw + 90.0F);
                     Quaternion qPitchAxis = new Quaternion((float)pitchYaw.x, (float)pitchYaw.y, (float)pitchYaw.z, 0.0F);
                     pitchYaw = Vec3d.fromPitchYaw(pitch, yaw);
                     Quaternion qPitchYaw = new Quaternion((float)pitchYaw.x, (float)pitchYaw.y, (float)pitchYaw.z, 0.0F);
                     boolean stop = false;

                     for(int cone = 0; cone < subConeCount; ++cone) {
                        double radius = distance * MCTools.TRIG_TABLE.sin(Tools.degtorad(subConeAngle));
                        double rollStep = 3.141592653589793D * radius * 2.0D / distributedRaytraceSpacing;
                        int thetaStepCount = Tools.max((int)rollStep + 1, 4);
                        rollStep = 6.283185307179586D / (double)thetaStepCount;
                        double roll = rollStep;
                        Quaternion theta0 = MCTools.rotatedQuaternion(qPitchYaw, qPitchAxis, Tools.degtorad(subConeAngle));

                        for(int thetaStepI = 0; thetaStepI < thetaStepCount; ++thetaStepI) {
                           Quaternion qRotated = MCTools.rotatedQuaternion(theta0, qPitchYaw, roll);
                           qRotated.scale((float)distance);
                           Vec3d pos = (new Vec3d((double)qRotated.x, (double)qRotated.y, (double)qRotated.z)).add(origin);
                           if (ImprovedRayTracing.entityPenetration(target, origin, pos, true) > 0.0D) {
                              queue.add(target, squareDist);
                              stop = true;
                              break;
                           }

                           roll += rollStep;
                        }

                        if (stop) {
                           break;
                        }

                        subConeAngle += subConeStep;
                     }
                  }
               }
            }
         }
      }

      ArrayList<Entity> result = new ArrayList();
      Collections.addAll(result, queue.toArray(new Entity[0]));
      return result;
   }

   static {
      INLUSION_MODES.put(Integer.valueOf(0), "Entity Position");
      INLUSION_MODES.put(Integer.valueOf(1), "Geometric Center");
      INLUSION_MODES.put(Integer.valueOf(2), "Any Rectangular Prism Point");
      INLUSION_MODES.put(Integer.valueOf(3), "Full Rectangular Prism");
      INLUSION_MODES.put(Integer.valueOf(4), "Any Cylinder Point");
      INLUSION_MODES.put(Integer.valueOf(5), "Full Cylinder");
   }
}
