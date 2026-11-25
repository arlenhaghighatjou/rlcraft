package xzeroair.trinkets.client.events;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.init.Elements;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.items.trinkets.TrinketDragonsEye;
import xzeroair.trinkets.items.trinkets.TrinketSeaStone;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.util.TrinketsConfig;

public class PlayerCameraSetupEvents {
   private static Minecraft mc = Minecraft.getMinecraft();

   public PlayerCameraSetupEvents() {
   }

   private PlayerCameraSetupEvents.CameraTranslation getVanillaTranslation(EntityRenderer renderer, double partialTicks) {
      Entity entity = mc.getRenderViewEntity();
      float f = entity.getEyeHeight();
      double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
      double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + (double)f;
      double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
      int perspective = mc.gameSettings.thirdPersonView;
      float x = 0.0F;
      float y = 0.0F;
      float z = 0.0F;
      if (perspective > 0) {
         double d3 = 4.0D + 0.0D * partialTicks;
         float f1 = entity.rotationYaw;
         float f2 = entity.rotationPitch;
         if (perspective == 2) {
            f2 += 180.0F;
         }

         float sq = 0.017453292F;
         double d4 = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
         double d5 = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
         double d6 = (double)(-MathHelper.sin(f2 * 0.017453292F)) * d3;

         for(int i = 0; i < 8; ++i) {
            float f3 = (float)((i & 1) * 2 - 1);
            float f4 = (float)((i >> 1 & 1) * 2 - 1);
            float f5 = (float)((i >> 2 & 1) * 2 - 1);
            f3 = f3 * 0.1F;
            f4 = f4 * 0.1F;
            f5 = f5 * 0.1F;
            double startX = d0 + (double)f3;
            double startY = d1 + (double)f4;
            double startZ = d2 + (double)f5;
            Vec3d start = new Vec3d(startX, startY, startZ);
            double endX = d0 - d4 + (double)f3 + (double)f5;
            double endY = d1 - d6 + (double)f4;
            double endZ = d2 - d5 + (double)f5;
            Vec3d end = new Vec3d(endX, endY, endZ);
            RayTraceResult raytraceresult = mc.world.rayTraceBlocks(start, end);
            if (raytraceresult != null) {
               double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));
               if (d7 < d3) {
                  d3 = d7;
               }
            }
         }

         z = (float)d3;
      } else {
         z = 0.05F;
      }

      return new PlayerCameraSetupEvents.CameraTranslation(x, y, perspective == 2 ? -z : z);
   }

   public PlayerCameraSetupEvents.CameraTranslation updatedOffset(EntityRenderer renderer, double multiplier, double partialTicks) {
      Entity entity = mc.getRenderViewEntity();
      float f = entity.getEyeHeight();
      double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
      double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + (double)f;
      double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
      int perspective = mc.gameSettings.thirdPersonView;
      double d = 4.0D * multiplier;
      float x = 0.0F;
      float y = 0.0F;
      float z = 0.0F;
      if (perspective > 0) {
         double d3 = d + (d - d) * partialTicks;
         float f1 = entity.rotationYaw;
         float f2 = entity.rotationPitch;
         if (perspective == 2) {
            f2 += 180.0F;
         }

         float sq = 0.017453292F;
         double d4 = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
         double d5 = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
         double d6 = (double)(-MathHelper.sin(f2 * 0.017453292F)) * d3;

         for(int i = 0; i < 8; ++i) {
            float f3 = (float)((i & 1) * 2 - 1);
            float f4 = (float)((i >> 1 & 1) * 2 - 1);
            float f5 = (float)((i >> 2 & 1) * 2 - 1);
            f3 = f3 * 0.1F;
            f4 = f4 * 0.1F;
            f5 = f5 * 0.1F;
            double startX = d0 + (double)f3;
            double startY = d1 + (double)f4;
            double startZ = d2 + (double)f5;
            Vec3d start = new Vec3d(startX, startY, startZ);
            double endX = d0 - d4 + (double)f3 + (double)f5;
            double endY = d1 - d6 + (double)f4;
            double endZ = d2 - d5 + (double)f5;
            Vec3d end = new Vec3d(endX, endY, endZ);
            RayTraceResult raytraceresult = mc.world.rayTraceBlocks(start, end);
            if (raytraceresult != null) {
               double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));
               if (d7 < d3) {
                  if (multiplier <= 1.0D) {
                     d3 = d7 * multiplier;
                  } else {
                     d3 = d7;
                  }
               }
            }
         }

         if (perspective == 2) {
            ;
         }

         z = (float)d3;
      } else {
         z = 0.05F;
      }

      return new PlayerCameraSetupEvents.CameraTranslation(x, y, perspective == 2 ? z : -z);
   }

   private void applyBobbing(float partialTicks) {
      if (mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)mc.getRenderViewEntity();
         float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
         float f1 = -(entityplayer.distanceWalkedModified + f * partialTicks);
         float f2 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTicks;
         float f3 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTicks;
         GlStateManager.translate(MathHelper.sin(f1 * 3.1415927F) * f2 * 0.15F, -Math.abs(MathHelper.cos(f1 * 3.1415927F) * f2) * 0.0F, 0.0F);
         GlStateManager.rotate(MathHelper.sin(f1 * 3.1415927F) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(Math.abs(MathHelper.cos(f1 * 3.1415927F - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
      }

   }

   @SubscribeEvent
   public void CameraSetup(CameraSetup event) {
      if (TrinketsConfig.CLIENT.cameraHeight) {
         Entity entity = event.getEntity();
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            Capabilities.getEntityProperties(player, (prop) -> {
               EntityRacePropertiesHandler handler = prop.getRaceHandler();
               if (!prop.isNormalSize() || handler.isTransforming()) {
                  float defaultHeight = prop.getDefaultHeight();
                  float height = handler.getHeight();
                  int perspective = mc.gameSettings.thirdPersonView;
                  if (perspective > 0) {
                     PlayerCameraSetupEvents.CameraTranslation vanilla = this.getVanillaTranslation(event.getRenderer(), event.getRenderPartialTicks());
                     GlStateManager.translate(0.0F, 0.0F, vanilla.getZ());
                     PlayerCameraSetupEvents.CameraTranslation offset = this.updatedOffset(event.getRenderer(), (double)(height / defaultHeight), event.getRenderPartialTicks());
                     GlStateManager.translate(0.0D, 0.0D, (double)offset.getZ());
                  }
               }

            });
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void renderFogDensityEvent(FogDensity event) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player.isInWater()) {
         boolean hasSeaStone = !TrinketHelper.getAccessory(player, (acc) -> {
            return acc.getItem() instanceof TrinketSeaStone;
         }).isEmpty();
         boolean hasAffinity = TrinketHelper.entityHasAbility("water_affinity".toString(), player);
         if (hasSeaStone || hasAffinity) {
            if (event.isCancelable() && !event.isCanceled()) {
               event.setCanceled(true);
            }

            event.setDensity(0.0F);
         }
      }

   }

   @SubscribeEvent
   public void renderBlockOverlay(RenderBlockOverlayEvent event) {
      OverlayType overlay = event.getOverlayType();
      if (overlay == OverlayType.FIRE || overlay == OverlayType.WATER) {
         boolean cancel = !TrinketHelper.getAccessory(event.getPlayer(), (acc) -> {
            if (overlay != OverlayType.FIRE || (!(acc.getItem() instanceof TrinketDragonsEye) || ((TrinketDragonsEye)acc.getItem()).getPrimaryElement() != Elements.FIRE) && !acc.getItem().getRegistryName().toString().equalsIgnoreCase(ModItems.RaceTrinkets.TrinketDragonRing.getRegistryName().toString())) {
               return overlay == OverlayType.WATER && acc.getItem() instanceof TrinketSeaStone;
            } else {
               return true;
            }
         }).isEmpty();
         boolean hasFireAffinity = overlay == OverlayType.FIRE && TrinketHelper.entityHasAbility("fire_immunity".toString(), event.getPlayer());
         boolean hasWaterAffinity = overlay == OverlayType.WATER && TrinketHelper.entityHasAbility("water_affinity".toString(), event.getPlayer());
         if ((cancel || hasFireAffinity || hasWaterAffinity) && event.isCancelable() && !event.isCanceled()) {
            event.setCanceled(true);
         }
      }

   }

   private static class CameraTranslation {
      private float x;
      private float y;
      private float z;

      public CameraTranslation(float x, float y, float z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public final float getX() {
         return this.x;
      }

      public final float getY() {
         return this.y;
      }

      public final float getZ() {
         return this.z;
      }
   }
}
