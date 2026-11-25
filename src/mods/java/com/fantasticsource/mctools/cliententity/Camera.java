package com.fantasticsource.mctools.cliententity;

import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Smoothing;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Camera extends ClientEntity {
   protected static final double OFFSET_COLLISION_BUFFER_DIRECT = 0.2D;
   protected static final double OFFSET_COLLISION_BUFFER_FORWARD = 0.2D;
   protected static final Field MINECRAFT_RENDER_VIEW_ENTITY_FIELD = ReflectionTool.getField(Minecraft.class, "renderViewEntity", "renderViewEntity");
   protected static Camera camera = null;
   public static final int PLAYER_RENDER_IF_THIRD_PERSON = 0;
   public static final int PLAYER_RENDER_ALWAYS = 1;
   public static final int PLAYER_RENDER_NEVER = 2;
   public static int playerRenderMode = 0;
   public static boolean allowControl = true;
   public static double followOffsetLR = 0.0D;
   protected boolean active = false;
   protected int mode;
   protected int originalMode;
   protected Entity toFollow = null;
   protected static boolean control1 = false;
   protected static boolean control2 = false;

   public static Camera getCamera() {
      if (camera == null) {
         camera = new Camera((World)null);
      }

      return camera;
   }

   protected Camera(World worldIn) {
      super(worldIn);
      this.setSize(0.0F, 0.0F);
      this.forceSpawn = true;
   }

   public boolean isActive() {
      return this.active;
   }

   public void activate(Entity toFollow, int mode) {
      this.activate(toFollow, toFollow.world, toFollow.posX, toFollow.posY + (double)toFollow.getEyeHeight(), toFollow.posZ, toFollow.getRotationYawHead(), toFollow.rotationPitch, mode);
   }

   public void activate(World world, double x, double y, double z, float yaw, float pitch, int mode) {
      this.activate((Entity)null, world, x, y, z, yaw, pitch, mode);
   }

   protected void activate(Entity toFollow, World world, double x, double y, double z, float yaw, float pitch, int mode) {
      if (this.active) {
         this.deactivate();
      }

      this.active = true;
      this.toFollow = toFollow;
      this.world = world;
      this.dimension = world.provider.getDimension();
      this.posX = x;
      this.prevPosX = x;
      this.posY = y;
      this.prevPosY = y;
      this.posZ = z;
      this.prevPosZ = z;
      this.rotationYaw = yaw;
      this.prevRotationYaw = yaw;
      this.rotationPitch = pitch;
      this.prevRotationPitch = pitch;
      this.isDead = false;
      world.spawnEntity(this);
      this.mode = mode;
      if (mode == -1) {
         this.originalMode = -1;
      } else {
         GameSettings gs = Minecraft.getMinecraft().gameSettings;
         this.originalMode = gs.thirdPersonView;
         gs.thirdPersonView = mode;
      }

      Minecraft.getMinecraft().setRenderViewEntity(camera);
   }

   public void deactivate() {
      if (this.active) {
         this.active = false;
         this.world.removeEntity(this);
         this.world = null;
         Minecraft mc = Minecraft.getMinecraft();
         if (this.mode != -1) {
            mc.gameSettings.thirdPersonView = this.originalMode;
         }

         mc.setRenderViewEntity(mc.player);
      }

   }

   public void onUpdate() {
      if (this.active && this.mode != -1) {
         Minecraft.getMinecraft().gameSettings.thirdPersonView = this.mode;
      }

      super.onUpdate();
   }

   public void onEntityUpdate() {
   }

   @SubscribeEvent
   public static void trackFollowed(RenderTickEvent event) {
      if (getCamera().active && event.phase == Phase.START) {
         if (camera.toFollow != null) {
            followEntity(event.renderTickTime);
         }

      }
   }

   protected static void followEntity(float partialTick) {
      Entity entity = camera.toFollow;
      if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
         camera.rotationYaw = entity.getRotationYawHead();
         camera.rotationPitch = entity.rotationPitch;
         camera.prevRotationYaw = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).prevRotationYawHead : entity.prevRotationYaw;
         camera.prevRotationPitch = entity.prevRotationPitch;
      } else {
         camera.rotationYaw = (float)Smoothing.interpolate(entity instanceof EntityLivingBase ? (double)((EntityLivingBase)entity).prevRotationYawHead : (double)entity.prevRotationYaw, (double)entity.getRotationYawHead(), (double)partialTick, 0);
         camera.rotationPitch = (float)Smoothing.interpolate((double)entity.prevRotationPitch, (double)entity.rotationPitch, (double)partialTick, 0);
         camera.prevRotationYaw = camera.rotationYaw;
         camera.prevRotationPitch = camera.rotationPitch;
      }

      if (followOffsetLR != 0.0D) {
         World world = entity.world;
         double testFollowOffsetLR = followOffsetLR > 0.0D ? followOffsetLR + 0.2D : followOffsetLR - 0.2D;
         Vec3d start = entity.getPositionEyes(1.0F);
         Vec3d testStart = start.addVector(-0.2D * TrigLookupTable.TRIG_TABLE_1024.sin(Tools.degtorad((double)camera.rotationYaw)), 0.0D, 0.2D * TrigLookupTable.TRIG_TABLE_1024.cos(Tools.degtorad((double)camera.rotationYaw)));
         Vec3d testEnd = testStart.subtract(testFollowOffsetLR * TrigLookupTable.TRIG_TABLE_1024.cos(Tools.degtorad((double)camera.rotationYaw)), 0.0D, testFollowOffsetLR * TrigLookupTable.TRIG_TABLE_1024.sin(Tools.degtorad((double)camera.rotationYaw)));
         RayTraceResult testResult = ImprovedRayTracing.rayTraceBlocks(world, testStart, testEnd, Math.abs(testFollowOffsetLR), true);
         Vec3d testHitVec = testResult.hitVec != null ? testResult.hitVec : testEnd;
         Vec3d testDif = testHitVec.subtract(testStart);
         double testDist = testDif.lengthVector() - 0.2D;
         if (testDist > 0.0D) {
            Vec3d end = start.subtract(testFollowOffsetLR * TrigLookupTable.TRIG_TABLE_1024.cos(Tools.degtorad((double)camera.rotationYaw)), 0.0D, testFollowOffsetLR * TrigLookupTable.TRIG_TABLE_1024.sin(Tools.degtorad((double)camera.rotationYaw)));
            RayTraceResult result = ImprovedRayTracing.rayTraceBlocks(world, start, end, testDist + 0.2D, true);
            Vec3d hitVec = result.hitVec != null ? result.hitVec : end;
            Vec3d dif = hitVec.subtract(start);
            double dist = dif.lengthVector() - 0.2D;
            if (dist > 0.0D) {
               camera.setPosition(dif.normalize().scale(Tools.min(testDist, dist)).add(start));
            }
         }
      }

      camera.prevPosX = camera.posX;
      camera.prevPosY = camera.posY;
      camera.prevPosZ = camera.posZ;
   }

   public void setPositionAndRotation(Vec3d position, float yaw, float pitch) {
      this.setPosition(position);
      this.setRotation(yaw, pitch);
   }

   public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
      this.setPosition(x, y, z);
      this.setRotation(yaw, pitch);
   }

   public void setPosition(Vec3d vec) {
      this.setPosition(vec.x, vec.y, vec.z);
   }

   public float getEyeHeight() {
      return 0.0F;
   }

   @SubscribeEvent
   public static void renderPlayerPre(Pre event) {
      Minecraft mc = Minecraft.getMinecraft();
      switch(playerRenderMode) {
      case 0:
      default:
         if (mc.gameSettings.thirdPersonView == 0) {
            return;
         }
      case 1:
         if (getCamera().active && event.getEntityPlayer() == mc.player) {
            mc.getRenderManager().renderViewEntity = mc.player;
         }

         return;
      case 2:
      }
   }

   @SubscribeEvent
   public static void renderPlayerPost(Post event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (getCamera().active && event.getEntityPlayer() == mc.player) {
         mc.getRenderManager().renderViewEntity = camera;
      }

   }

   @SubscribeEvent
   public static void preOverlayRender(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre event) {
      if (event.getType() == ElementType.ALL) {
         if (getCamera().active) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.setRenderViewEntity(mc.player);
         }

      }
   }

   @SubscribeEvent
   public static void postOverlayRender(net.minecraftforge.client.event.RenderGameOverlayEvent.Post event) {
      if (event.getType() == ElementType.ALL) {
         if (getCamera().active) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.setRenderViewEntity(camera);
         }

      }
   }

   @SubscribeEvent
   public static void controlFixPre1(PlayerSPPushOutOfBlocksEvent event) {
      if (allowControl) {
         Minecraft mc = Minecraft.getMinecraft();
         if (getCamera().active && event.getEntityPlayer() == mc.player) {
            ReflectionTool.set(MINECRAFT_RENDER_VIEW_ENTITY_FIELD, mc, mc.player);
            control1 = true;
         }

      }
   }

   @SubscribeEvent
   public static void controlFixPre2(PlayerTickEvent event) {
      if (allowControl) {
         Minecraft mc = Minecraft.getMinecraft();
         if (event.phase == Phase.END && getCamera().active && event.player == mc.player) {
            ReflectionTool.set(MINECRAFT_RENDER_VIEW_ENTITY_FIELD, mc, mc.player);
            control2 = true;
         }

      }
   }

   @SubscribeEvent
   public static void controlFixPost(GetCollisionBoxesEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if ((control1 || control2) && getCamera().active && event.getWorld().isRemote) {
         control1 = false;
         control2 = false;
         ReflectionTool.set(MINECRAFT_RENDER_VIEW_ENTITY_FIELD, mc, camera);
      }

   }

   static {
      MinecraftForge.EVENT_BUS.register(Camera.class);
   }
}
