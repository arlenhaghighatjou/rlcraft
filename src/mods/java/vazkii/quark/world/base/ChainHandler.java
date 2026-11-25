package vazkii.quark.world.base;

import com.google.common.base.Predicate;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.automation.feature.ChainLinkage;
import vazkii.quark.base.network.message.MessageSyncChain;

public class ChainHandler {
   public static final String LINKED_TO = "Quark:VehicleLink";
   public static final double DRAG = 0.95D;
   public static final float CHAIN_SLACK = 2.0F;
   public static final float MAX_DISTANCE = 8.0F;
   private static final float STIFFNESS = 0.4F;
   private static final float DAMPING = 0.2F;
   private static final float MIN_FORCE = 0.05F;
   private static final float MAX_FORCE = 6.0F;

   public ChainHandler() {
   }

   private static <T extends Entity> void adjustVelocity(T master, Entity follower) {
      if (master != follower && !master.world.isRemote) {
         double dist = (double)master.getDistance(follower);
         Vec3d masterPosition = master.getPositionVector();
         Vec3d followerPosition = follower.getPositionVector();
         Vec3d direction = followerPosition.subtract(masterPosition);
         direction = direction.subtract(0.0D, direction.y, 0.0D).normalize();
         double stretch = dist - 2.0D;
         double springX = 0.4000000059604645D * stretch * direction.x;
         double springZ = 0.4000000059604645D * stretch * direction.z;
         springX = MathHelper.clamp(springX, -6.0D, 6.0D);
         springZ = MathHelper.clamp(springZ, -6.0D, 6.0D);
         double totalSpringSq = springX * springX + springZ * springZ;
         if (totalSpringSq > 0.002500000176951289D) {
            master.motionX += springX;
            master.motionZ += springZ;
            follower.motionX -= springX;
            follower.motionZ -= springZ;
            Vec3d newMasterVelocity = new Vec3d(master.motionX, 0.0D, master.motionZ);
            Vec3d newFollowerVelocity = new Vec3d(follower.motionX, 0.0D, follower.motionZ);
            double deviation = newFollowerVelocity.subtract(newMasterVelocity).dotProduct(direction);
            double dampX = 0.20000000298023224D * deviation * direction.x;
            double dampZ = 0.20000000298023224D * deviation * direction.z;
            dampX = MathHelper.clamp(dampX, -6.0D, 6.0D);
            dampZ = MathHelper.clamp(dampZ, -6.0D, 6.0D);
            master.motionX += dampX;
            master.motionZ += dampZ;
            follower.motionX -= dampX;
            follower.motionZ -= dampZ;
         }

      }
   }

   public static EaglercraftUUID getLink(Entity vehicle) {
      if (!canBeLinked(vehicle)) {
         return null;
      } else {
         return !vehicle.getEntityData().hasUniqueId("Quark:VehicleLink") ? null : vehicle.getEntityData().getUniqueId("Quark:VehicleLink");
      }
   }

   public static boolean canBeLinkedTo(Entity entity) {
      return entity instanceof EntityBoat || entity instanceof EntityMinecart || entity instanceof EntityPlayer;
   }

   public static boolean canBeLinked(Entity entity) {
      return entity instanceof EntityBoat || entity instanceof EntityMinecart;
   }

   public static <T extends Entity> Entity getLinked(T vehicle) {
      EaglercraftUUID uuid = getLink(vehicle);
      if (uuid == null) {
         return null;
      } else {
         for(Entity entity : vehicle.world.getEntitiesWithinAABB(Entity.class, vehicle.getEntityBoundingBox().grow(8.0D), ChainHandler::canBeLinkedTo)) {
            if (entity.getUniqueID().equals(uuid)) {
               return entity;
            }
         }

         return null;
      }
   }

   public static <T extends Entity> void adjustVehicle(T cart) {
      Entity other = getLinked(cart);
      if (other == null) {
         if (getLink(cart) != null) {
            breakChain(cart);
         }

      } else {
         if (!(other instanceof EntityPlayer)) {
            adjustVelocity(other, cart);
         }

         cart.motionX *= 0.95D;
         cart.motionZ *= 0.95D;
      }
   }

   private static <T extends Entity> void breakChain(T cart) {
      setLink(cart, (EaglercraftUUID)null, true);
      if (!cart.world.isRemote && cart.world.getGameRules().getBoolean("doEntityDrops")) {
         cart.entityDropItem(new ItemStack(ChainLinkage.chain), 0.0F);
      }

   }

   public static void updateLinkState(Entity entity, EaglercraftUUID uuid, EntityPlayer player) {
      if (player instanceof EntityPlayerMP) {
         NetworkHandler.INSTANCE.sendTo(new MessageSyncChain(entity.getEntityId(), uuid), (EntityPlayerMP)player);
      }

   }

   public static void setLink(Entity entity, EaglercraftUUID uuid, boolean sync) {
      if (canBeLinked(entity)) {
         if (entity.getUniqueID().equals(uuid)) {
            return;
         }

         if (uuid != null) {
            entity.getEntityData().setUniqueId("Quark:VehicleLink", uuid);
         } else {
            entity.getEntityData().removeTag("Quark:VehicleLinkMost");
            entity.getEntityData().removeTag("Quark:VehicleLinkLeast");
         }

         if (sync && entity.world instanceof WorldServer) {
            WorldServer world = (WorldServer)entity.world;

            for(EntityPlayer target : world.getEntityTracker().getTrackingPlayers(entity)) {
               updateLinkState(entity, uuid, target);
            }
         }
      }

   }
}
