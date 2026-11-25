package me.ichun.mods.ichunutil.common.grab;

import java.util.ArrayList;
import java.util.EnumMap;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.grab.handlers.GrabbedEntityBlockHandler;
import me.ichun.mods.ichunutil.common.grab.handlers.GrabbedFallingBlockHandler;
import me.ichun.mods.ichunutil.common.grab.handlers.GrabbedFireballHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;

public class GrabHandler {
   public String identifier;
   public EntityLivingBase grabber;
   public Entity grabbed;
   public int grabberId;
   public int grabbedId;
   public float grabDistance;
   public float yawTweak;
   public float pitchTweak;
   public boolean forceTerminate;
   public boolean terminated;
   public int time;
   public static EnumMap<Side, ArrayList<GrabHandler>> grabbedEntities = new EnumMap<Side, ArrayList<GrabHandler>>(Side.class) {
      {
         this.put(Side.SERVER, new ArrayList());
         this.put(Side.CLIENT, new ArrayList());
      }
   };
   public static ArrayList<Integer> dimensionalEntities = new ArrayList();
   private static ArrayList<GrabHandler.GrabbedEntityHandler> entityHandlers = new ArrayList<GrabHandler.GrabbedEntityHandler>() {
      {
         this.add(new GrabbedFireballHandler());
         this.add(new GrabbedFallingBlockHandler());
         this.add(new GrabbedEntityBlockHandler());
      }
   };

   public GrabHandler(EntityLivingBase grabber, Entity grabbed, float distance) {
      this.identifier = RandomStringUtils.randomAscii(20);
      this.grabber = grabber;
      this.grabbed = grabbed;
      this.grabberId = grabber.getEntityId();
      this.grabbedId = grabbed.getEntityId();
      this.grabDistance = distance;
   }

   public GrabHandler(String identifier, int grabberId, int grabbedId, float dist) {
      this.identifier = identifier;
      this.grabberId = grabberId;
      this.grabbedId = grabbedId;
      this.grabDistance = dist;
   }

   public void update() {
      ++this.time;
      Vec3d pos = EntityHelper.getEntityPositionEyes(this.grabber, 1.0F);
      this.grabber.rotationYawHead += this.yawTweak;
      this.grabber.rotationPitch += this.pitchTweak;
      Vec3d look = this.grabber.getLookVec();
      this.grabber.rotationYawHead -= this.yawTweak;
      this.grabber.rotationPitch -= this.pitchTweak;
      Vec3d grabPos = new Vec3d(pos.x + look.x * (double)this.grabDistance, pos.y + look.y * (double)this.grabDistance, pos.z + look.z * (double)this.grabDistance);
      float distTolerance = this.grabToleranceTillTeleport();
      if (distTolerance > 0.0F && this.grabbed.getDistance(grabPos.x, grabPos.y, grabPos.z) > (double)distTolerance) {
         this.grabbed.lastTickPosX = this.grabbed.prevPosX = this.grabbed.posX = this.grabber.posX;
         this.grabbed.lastTickPosY = this.grabbed.prevPosY = this.grabbed.posY = this.grabber.posY;
         this.grabbed.lastTickPosZ = this.grabbed.prevPosZ = this.grabbed.posZ = this.grabber.posZ;
         this.grabbed.setLocationAndAngles(this.grabbed.posX, this.grabbed.posY, this.grabbed.posZ, this.grabbed.rotationYaw, this.grabbed.rotationPitch);
         EntityHelper.setVelocity(this.grabbed, grabPos.x - this.grabbed.posX, grabPos.y - (this.grabbed.getEntityBoundingBox().minY + this.grabbed.getEntityBoundingBox().maxY) / 2.0D, grabPos.z - this.grabbed.posZ);
         this.grabbed.move(MoverType.SELF, this.grabbed.motionX, this.grabbed.motionY, this.grabbed.motionZ);
         this.grabbed.setLocationAndAngles(this.grabbed.posX, this.grabbed.posY, this.grabbed.posZ, this.grabbed.rotationYaw, this.grabbed.rotationPitch);
      }

      EntityHelper.setVelocity(this.grabbed, grabPos.x - this.grabbed.posX, grabPos.y - (this.grabbed.getEntityBoundingBox().minY + this.grabbed.getEntityBoundingBox().maxY) / 2.0D, grabPos.z - this.grabbed.posZ);
      this.grabbed.fallDistance = -((float)(this.grabbed.motionY * this.grabbed.motionY));
      this.grabbed.onGround = false;
      this.grabbed.isAirBorne = true;
      this.grabbed.timeUntilPortal = 5;

      for(GrabHandler.GrabbedEntityHandler handler : entityHandlers) {
         if (handler.eligible(this.grabbed)) {
            handler.handle(this);
         }
      }

   }

   public void transfer(GrabHandler transfer) {
      transfer.grabDistance = this.grabDistance;
   }

   public float grabToleranceTillTeleport() {
      return 5.0F;
   }

   public boolean shouldTerminate() {
      return this.forceTerminate || this.grabber != null && this.grabbed != null && (this.grabbed.isDead || !this.grabber.isEntityAlive() || this.grabbed == this.grabber.getRidingEntity() || this.grabbed.dimension != this.grabber.dimension || (double)this.grabbed.getDistance(this.grabber) > (double)this.grabDistance + (double)this.grabToleranceTillTeleport() * 1.25D);
   }

   public boolean canSendAcrossDimensions() {
      return true;
   }

   public void terminate() {
      this.terminated = true;
   }

   @SideOnly(Side.CLIENT)
   public void getIDs() {
      WorldClient client = Minecraft.getMinecraft().world;
      if (this.grabber == null) {
         if (this.grabberId == -1) {
            this.grabber = Minecraft.getMinecraft().player;
         } else {
            Entity ent = client.getEntityByID(this.grabberId);
            if (ent instanceof EntityLivingBase) {
               this.grabber = (EntityLivingBase)ent;
            }
         }
      }

      if (this.grabbed == null) {
         this.grabbed = client.getEntityByID(this.grabbedId);
      }

   }

   public static void tick(Side side) {
      ArrayList<GrabHandler> ents = (ArrayList)grabbedEntities.get(side);

      for(int i = ents.size() - 1; i >= 0; --i) {
         GrabHandler handler = (GrabHandler)ents.get(i);
         if (handler.grabber != null && handler.grabbed != null) {
            if (handler.shouldTerminate()) {
               handler.terminate();
               ents.remove(i);
            } else {
               handler.update();
            }
         } else {
            handler.getIDs();
         }
      }

   }

   public static void grab(GrabHandler base, Side side) {
      ArrayList<GrabHandler> ents = (ArrayList)grabbedEntities.get(side);
      ents.add(base);
   }

   public static GrabHandler release(EntityLivingBase grabber, Side side, Class<? extends GrabHandler> clz) {
      ArrayList<GrabHandler> ents = (ArrayList)grabbedEntities.get(side);

      for(int i = ents.size() - 1; i >= 0; --i) {
         GrabHandler handler = (GrabHandler)ents.get(i);
         if (handler.grabber == grabber && (clz == null || clz.isInstance(handler))) {
            handler.terminate();
            ents.remove(i);
            return handler;
         }
      }

      return null;
   }

   public static ArrayList<GrabHandler> getHandlers(EntityLivingBase grabber, Side side) {
      ArrayList<GrabHandler> handlers = new ArrayList();
      ArrayList<GrabHandler> ents = (ArrayList)grabbedEntities.get(side);

      for(int i = ents.size() - 1; i >= 0; --i) {
         GrabHandler handler = (GrabHandler)ents.get(i);
         if (handler.grabber == grabber) {
            handlers.add(handler);
         }
      }

      return handlers;
   }

   public static boolean hasHandlerType(EntityLivingBase grabber, Side side, Class<? extends GrabHandler> clz) {
      return getFirstHandler(grabber, side, clz) != null;
   }

   public static GrabHandler getFirstHandler(EntityLivingBase grabber, Side side, Class<? extends GrabHandler> clz) {
      for(GrabHandler handler : getHandlers(grabber, side)) {
         if (clz == null || clz.isInstance(handler)) {
            return handler;
         }
      }

      return null;
   }

   public static boolean isGrabbed(Entity grabbed, Side side) {
      ArrayList<GrabHandler> ents = (ArrayList)grabbedEntities.get(side);

      for(int i = ents.size() - 1; i >= 0; --i) {
         GrabHandler handler = (GrabHandler)ents.get(i);
         if (handler.grabbed == grabbed) {
            return true;
         }
      }

      return false;
   }

   public static void registerEntityHandler(GrabHandler.GrabbedEntityHandler handler) {
      entityHandlers.add(handler);
   }

   public interface GrabbedEntityHandler {
      boolean eligible(Entity var1);

      void handle(GrabHandler var1);
   }
}
