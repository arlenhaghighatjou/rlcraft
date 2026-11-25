package codersafterdark.compatskills.utils;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.ConfigHandler;
import codersafterdark.reskillable.base.LevelLockHandler;
import com.google.common.base.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Utils {
   public Utils() {
   }

   public static String formatRequirements(String[] requirements) {
      if (requirements == null) {
         return "null";
      } else {
         String reqString = (String)Arrays.stream(requirements).map((s) -> {
            return s + ", ";
         }).collect(Collectors.joining());
         if (!reqString.isEmpty()) {
            reqString = reqString.substring(0, reqString.length() - 2);
         }

         return reqString;
      }
   }

   public static ITextComponent getError(RequirementHolder holder, PlayerData data, TextComponentTranslation error) {
      TextComponentTranslation error2 = new TextComponentTranslation("compatskills.misc.requirements", new Object[0]);
      String reqString = (String)holder.getRequirements().stream().map((requirement) -> {
         return "\n " + requirement.getToolTip(data) + ' ';
      }).collect(Collectors.joining());
      return new TextComponentString(error.getUnformattedComponentText() + ' ' + error2.getUnformattedComponentText() + ' ' + reqString);
   }

   public static RayTraceResult getLookingAt(EntityPlayer player) {
      if (player == null) {
         return null;
      } else {
         Vec3d eyes = new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
         float pitch = -player.rotationPitch * 0.017453292F;
         float yaw = -player.rotationYaw * 0.017453292F - 3.1415927F;
         double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
         double pitchReach = (double)(-MathHelper.cos(pitch)) * reach;
         Vec3d vec3d = eyes.addVector((double)MathHelper.sin(yaw) * pitchReach, (double)MathHelper.sin(pitch) * reach, (double)MathHelper.cos(yaw) * pitchReach);
         return player.getEntityWorld().rayTraceBlocks(eyes, vec3d);
      }
   }

   public static Entity lookingAt(EntityPlayer player) {
      if (player == null) {
         return null;
      } else {
         double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
         double distance = reach;
         Vec3d eyes = new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
         RayTraceResult objectMouseOver = getLookingAt(player);
         if (objectMouseOver != null) {
            distance = objectMouseOver.hitVec.distanceTo(eyes);
         }

         Vec3d look = player.getLook(1.0F);
         double lookReachX = look.x * reach;
         double lookReachY = look.y * reach;
         double lookReachZ = look.z * reach;
         List<Entity> list = player.getEntityWorld().getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().expand(lookReachX, lookReachY, lookReachZ).grow(1.0D, 1.0D, 1.0D), (ent) -> {
            return ent != null && (!(ent instanceof EntityPlayer) || !((EntityPlayer)ent).isSpectator()) && ent.canBeCollidedWith();
         });
         double bestDistance = distance;
         Vec3d direction = eyes.addVector(lookReachX, lookReachY, lookReachZ);
         Entity hitEntity = null;

         for(Entity entity : list) {
            AxisAlignedBB entityBB = entity.getEntityBoundingBox().grow((double)entity.getCollisionBorderSize());
            if (entityBB.contains(eyes)) {
               return entity;
            }

            RayTraceResult raytraceresult = entityBB.calculateIntercept(eyes, direction);
            if (raytraceresult != null) {
               double dist = eyes.distanceTo(raytraceresult.hitVec);
               if (dist == 0.0D) {
                  return entity;
               }

               if (dist < bestDistance) {
                  hitEntity = entity;
                  bestDistance = dist;
               }
            }
         }

         return hitEntity;
      }
   }

   public static String getEntityID(Entity entity) {
      for(EntityEntry entry : ForgeRegistries.ENTITIES.getValuesCollection()) {
         if (entry.getEntityClass() == entity.getClass()) {
            return entry.getRegistryName() != null ? entry.getRegistryName().toString() : "no_registry_name";
         }
      }

      return "no_registry_name";
   }

   public static boolean skipPlayer(EntityPlayer player) {
      return player == null || !ConfigHandler.enforceOnCreative && player.isCreative() || !ConfigHandler.enforceFakePlayers && LevelLockHandler.isFake(player);
   }
}
