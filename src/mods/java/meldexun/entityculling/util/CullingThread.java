package meldexun.entityculling.util;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.function.IntSupplier;
import java.util.stream.LongStream;
import meldexun.entityculling.EntityCulling;
import meldexun.entityculling.config.EntityCullingConfig;
import meldexun.entityculling.integration.Hats;
import meldexun.entityculling.util.raytracing.Int2BoolTriFunction;
import meldexun.entityculling.util.raytracing.RaytracingEngine;
import meldexun.renderlib.api.IBoundingBoxCache;
import meldexun.renderlib.api.IEntityRendererCache;
import meldexun.renderlib.api.ILoadable;
import meldexun.renderlib.api.ITileEntityRendererCache;
import meldexun.renderlib.config.RenderLibConfig;
import meldexun.renderlib.integration.Optifine;
import meldexun.renderlib.util.MutableAABB;
import meldexun.renderlib.util.timer.CPUTimer;
import meldexun.renderlib.util.timer.ITimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class CullingThread extends Thread {
   private final CachedBlockAccess cachedBlockAccess = new CachedBlockAccess();
   private final MutableBlockPos mutablePos = new MutableBlockPos();
   private final RaytracingEngine engine = new RaytracingEngine(EntityCullingConfig.cacheSize, (x, y, z) -> {
      return this.cachedBlockAccess.getBlockState(this.mutablePos.setPos(x, y, z)).isOpaqueCube();
   }, () -> {
      return Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
   });
   private double sleepOverhead = 0.0D;
   public final ITimer timer = new ITimer() {
      private final ITimer timer = new CPUTimer("CPU (Cull Async)", 100);
      private volatile String avgString = "0.0ms";
      private volatile String minString = "0.0ms";
      private volatile String maxString = "0.0ms";

      public void update() {
         if (RenderLibConfig.showFrameTimes) {
            this.avgString = this.timer.avgString();
            this.minString = this.timer.minString();
            this.maxString = this.timer.maxString();
         }

         this.timer.update();
      }

      public void stop() {
         this.timer.stop();
      }

      public void start() {
         this.timer.start();
      }

      public LongStream results() {
         return this.timer.results();
      }

      public String getName() {
         return this.timer.getName();
      }

      public String avgString() {
         return this.avgString;
      }

      public String minString() {
         return this.minString;
      }

      public String maxString() {
         return this.maxString;
      }
   };
   private boolean spectator;
   private double camX;
   private double camY;
   private double camZ;
   private double x;
   private double y;
   private double z;

   public CullingThread() {
      this.setName("Culling Thread");
      this.setDaemon(true);
   }

   public void run() {
      Minecraft mc = Minecraft.getMinecraft();

      while(true) {
         long t = System.nanoTime();
         this.timer.update();
         this.timer.start();

         try {
            World world = mc.world;
            EntityPlayer player = mc.player;
            Entity renderViewEntity = mc.getRenderViewEntity();
            if (world != null && player != null && renderViewEntity != null) {
               this.cachedBlockAccess.setupCached(world);
               this.spectator = player.isSpectator();
               float partialTicks = mc.getRenderPartialTicks();
               this.x = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * (double)partialTicks;
               this.y = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * (double)partialTicks;
               this.z = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * (double)partialTicks;
               Vec3d cameraPosition = ActiveRenderInfo.getCameraPosition();
               this.camX = this.x + cameraPosition.x;
               this.camY = this.y + cameraPosition.y;
               this.camZ = this.z + cameraPosition.z;
               this.engine.setup(this.camX, this.camY, this.camZ);

               for(Entity entity : world.loadedEntityList) {
                  try {
                     ;
                  } catch (NoSuchElementException | ConcurrentModificationException var21) {
                     break;
                  }

                  if (entity != null) {
                     this.updateEntityCullingState(entity);
                  }
               }

               for(TileEntity tileEntity : world.loadedTileEntityList) {
                  try {
                     ;
                  } catch (NoSuchElementException | ConcurrentModificationException var20) {
                     break;
                  }

                  if (tileEntity != null) {
                     this.updateTileEntityCullingState(tileEntity);
                  }
               }
            }
         } catch (Throwable var22) {
            mc.crashed(new CrashReport("Culling Thread crashed!", var22));
         } finally {
            this.cachedBlockAccess.clearCache();
            this.engine.clearCache();
         }

         this.timer.stop();
         double d = (double)(System.nanoTime() - t) / 1000000.0D + this.sleepOverhead;
         this.sleepOverhead = d % 1.0D;
         long sleepTime = 10L - (long)d;
         if (sleepTime > 0L) {
            try {
               Thread.sleep(sleepTime);
            } catch (InterruptedException var19) {
               Thread.currentThread().interrupt();
            }
         }
      }
   }

   private void updateEntityCullingState(Entity entity) {
      if (!EntityCulling.useOpenGlBasedCulling()) {
         ((ICullable)entity).setCulled(!this.checkEntityVisibility(entity));
      }

      if (Optifine.isOptifineDetected()) {
         ((ICullable)entity).setShadowCulled(!this.checkEntityShadowVisibility(entity));
      }

   }

   private void updateTileEntityCullingState(TileEntity tileEntity) {
      if (!EntityCulling.useOpenGlBasedCulling()) {
         ((ICullable)tileEntity).setCulled(!this.checkTileEntityVisibility(tileEntity));
      }

      if (Optifine.isOptifineDetected()) {
         ((ICullable)tileEntity).setShadowCulled(!this.checkTileEntityShadowVisibility(tileEntity));
      }

   }

   private boolean checkEntityVisibility(Entity entity) {
      if (!EntityCullingConfig.enabled) {
         return true;
      } else if (EntityCullingConfig.disabledInSpectator && this.spectator) {
         return true;
      } else if (!EntityCullingConfig.entity.skipHiddenEntityRendering) {
         return true;
      } else if (EntityCulling.isHatsInstalled && Hats.isHat(entity)) {
         return false;
      } else if (EntityCullingConfig.entity.alwaysRenderBosses && !entity.isNonBoss()) {
         return true;
      } else if (EntityCullingConfig.entity.alwaysRenderEntitiesWithName && entity.getAlwaysRenderNameTagForRender()) {
         return true;
      } else if (EntityCullingConfig.entity.alwaysRenderPlayers && entity instanceof EntityPlayer) {
         return true;
      } else if (EntityCullingConfig.entity.alwaysRenderViewEntity && entity == Minecraft.getMinecraft().getRenderViewEntity()) {
         return true;
      } else if (((Boolean)EntityCullingConfig.entity.skipHiddenEntityRenderingBlacklistImpl.get(entity)).booleanValue()) {
         return true;
      } else if (!((IEntityRendererCache)entity).hasRenderer()) {
         return true;
      } else if (!((ILoadable)entity).isChunkLoaded()) {
         return true;
      } else {
         MutableAABB aabb = ((IBoundingBoxCache)entity).getCachedBoundingBox();
         if (aabb.sizeX() <= EntityCullingConfig.entity.skipHiddenEntityRenderingSize && aabb.sizeY() <= EntityCullingConfig.entity.skipHiddenEntityRenderingSize && aabb.sizeZ() <= EntityCullingConfig.entity.skipHiddenEntityRenderingSize) {
            return !((ICullable)entity).canBeOcclusionCulled() ? true : this.checkBox(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ());
         } else {
            return true;
         }
      }
   }

   private boolean checkTileEntityVisibility(TileEntity tileEntity) {
      if (!EntityCullingConfig.enabled) {
         return true;
      } else if (EntityCullingConfig.disabledInSpectator && this.spectator) {
         return true;
      } else if (!EntityCullingConfig.tileEntity.skipHiddenTileEntityRendering) {
         return true;
      } else if (((Boolean)EntityCullingConfig.tileEntity.skipHiddenTileEntityRenderingBlacklistImpl.get(tileEntity)).booleanValue()) {
         return true;
      } else if (!((ITileEntityRendererCache)tileEntity).hasRenderer()) {
         return true;
      } else if (!((ILoadable)tileEntity).isChunkLoaded()) {
         return true;
      } else {
         MutableAABB aabb = ((IBoundingBoxCache)tileEntity).getCachedBoundingBox();
         if (aabb.sizeX() <= EntityCullingConfig.tileEntity.skipHiddenTileEntityRenderingSize && aabb.sizeY() <= EntityCullingConfig.tileEntity.skipHiddenTileEntityRenderingSize && aabb.sizeZ() <= EntityCullingConfig.tileEntity.skipHiddenTileEntityRenderingSize) {
            return !((ICullable)tileEntity).canBeOcclusionCulled() ? true : this.checkBox(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ());
         } else {
            return true;
         }
      }
   }

   private boolean checkEntityShadowVisibility(Entity entity) {
      if (!EntityCullingConfig.optifineShaderOptions.entityShadowsCulling) {
         return true;
      } else {
         if (!EntityCulling.useOpenGlBasedCulling()) {
            if (!((ICullable)entity).isCulled()) {
               return true;
            }

            if (!EntityCullingConfig.optifineShaderOptions.entityShadowsCullingLessAggressiveMode) {
               return false;
            }
         } else {
            if (!EntityCullingConfig.enabled) {
               return true;
            }

            if (EntityCullingConfig.disabledInSpectator && this.spectator) {
               return true;
            }

            if (!EntityCullingConfig.entity.skipHiddenEntityRendering) {
               return true;
            }

            if (EntityCullingConfig.entity.alwaysRenderBosses && !entity.isNonBoss()) {
               return true;
            }

            if (EntityCullingConfig.entity.alwaysRenderEntitiesWithName && entity.getAlwaysRenderNameTagForRender()) {
               return true;
            }

            if (EntityCullingConfig.entity.alwaysRenderPlayers && entity instanceof EntityPlayer) {
               return true;
            }

            if (EntityCullingConfig.entity.alwaysRenderViewEntity && entity == Minecraft.getMinecraft().getRenderViewEntity()) {
               return true;
            }

            if (((Boolean)EntityCullingConfig.entity.skipHiddenEntityRenderingBlacklistImpl.get(entity)).booleanValue()) {
               return true;
            }

            if (!((IEntityRendererCache)entity).hasRenderer()) {
               return true;
            }

            if (!((ILoadable)entity).isChunkLoaded()) {
               return true;
            }

            if (!((ICullable)entity).canBeOcclusionCulled()) {
               return true;
            }
         }

         return this.engine.raytraceUncachedThreshold(entity.posX, entity.posY + (double)entity.height * 0.5D, entity.posZ, EntityCullingConfig.optifineShaderOptions.entityShadowsCullingLessAggressiveModeDiff);
      }
   }

   private boolean checkTileEntityShadowVisibility(TileEntity tileEntity) {
      if (!EntityCullingConfig.optifineShaderOptions.tileEntityShadowsCulling) {
         return true;
      } else {
         if (!EntityCulling.useOpenGlBasedCulling()) {
            if (!((ICullable)tileEntity).isCulled()) {
               return true;
            }

            if (!EntityCullingConfig.optifineShaderOptions.tileEntityShadowsCullingLessAggressiveMode) {
               return false;
            }
         } else {
            if (!EntityCullingConfig.enabled) {
               return true;
            }

            if (EntityCullingConfig.disabledInSpectator && this.spectator) {
               return true;
            }

            if (!EntityCullingConfig.tileEntity.skipHiddenTileEntityRendering) {
               return true;
            }

            if (((Boolean)EntityCullingConfig.tileEntity.skipHiddenTileEntityRenderingBlacklistImpl.get(tileEntity)).booleanValue()) {
               return true;
            }

            if (!((ITileEntityRendererCache)tileEntity).hasRenderer()) {
               return true;
            }

            if (!((ILoadable)tileEntity).isChunkLoaded()) {
               return true;
            }

            if (!((ICullable)tileEntity).canBeOcclusionCulled()) {
               return true;
            }
         }

         BlockPos pos = tileEntity.getPos();
         return this.engine.raytraceUncachedThreshold((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, EntityCullingConfig.optifineShaderOptions.tileEntityShadowsCullingLessAggressiveModeDiff);
      }
   }

   private boolean checkBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return EntityCullingConfig.enableRaytraceCache ? this.checkBoxCached(minX, minY, minZ, maxX, maxY, maxZ) : this.checkBoxUncached(minX, minY, minZ, maxX, maxY, maxZ);
   }

   private boolean checkBoxCached(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      int startX = MathHelper.floor(minX);
      int startY = MathHelper.floor(minY);
      int startZ = MathHelper.floor(minZ);
      int endX = MathHelper.ceil(maxX);
      int endY = MathHelper.ceil(maxY);
      int endZ = MathHelper.ceil(maxZ);
      if (this.camX >= (double)startX && this.camX <= (double)endX && this.camY >= (double)startY && this.camY <= (double)endY && this.camZ >= (double)startZ && this.camZ <= (double)endZ) {
         return true;
      } else {
         if (this.camX < (double)startX) {
            if (IntUtil.anyMatch(startY, endY, startZ, endZ, (y, z) -> {
               return this.engine.raytraceCachedThreshold(startX, y, z, EntityCullingConfig.raytraceThreshold);
            })) {
               return true;
            }
         } else if (this.camX > (double)endX && IntUtil.anyMatch(startY, endY, startZ, endZ, (y, z) -> {
            return this.engine.raytraceCachedThreshold(endX, y, z, EntityCullingConfig.raytraceThreshold);
         })) {
            return true;
         }

         if (this.camY < (double)startY) {
            if (IntUtil.anyMatch(this.camX < (double)startX ? startX + 1 : startX, this.camX > (double)endX ? endX - 1 : endX, startZ, endZ, (x, z) -> {
               return this.engine.raytraceCachedThreshold(x, startY, z, EntityCullingConfig.raytraceThreshold);
            })) {
               return true;
            }
         } else if (this.camY > (double)endY && IntUtil.anyMatch(this.camX < (double)startX ? startX + 1 : startX, this.camX > (double)endX ? endX - 1 : endX, startZ, endZ, (x, z) -> {
            return this.engine.raytraceCachedThreshold(x, endY, z, EntityCullingConfig.raytraceThreshold);
         })) {
            return true;
         }

         if (this.camZ < (double)startZ) {
            if (IntUtil.anyMatch(this.camX < (double)startX ? startX + 1 : startX, this.camX > (double)endX ? endX - 1 : endX, this.camY < (double)startY ? startY + 1 : startY, this.camY > (double)endY ? endY - 1 : endY, (x, y) -> {
               return this.engine.raytraceCachedThreshold(x, y, startZ, EntityCullingConfig.raytraceThreshold);
            })) {
               return true;
            }
         } else if (this.camZ > (double)endZ && IntUtil.anyMatch(this.camX < (double)startX ? startX + 1 : startX, this.camX > (double)endX ? endX - 1 : endX, this.camY < (double)startY ? startY + 1 : startY, this.camY > (double)endY ? endY - 1 : endY, (x, y) -> {
            return this.engine.raytraceCachedThreshold(x, y, endZ, EntityCullingConfig.raytraceThreshold);
         })) {
            return true;
         }

         return false;
      }
   }

   private boolean checkBoxUncached(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      if (this.camX >= minX && this.camX <= maxX && this.camY >= minY && this.camY <= maxY && this.camZ >= minZ && this.camZ <= maxZ) {
         return true;
      } else {
         double deltaX = maxX - minX;
         double deltaY = maxY - minY;
         double deltaZ = maxZ - minZ;
         int stepsX = MathHelper.ceil(deltaX);
         int stepsY = MathHelper.ceil(deltaY);
         int stepsZ = MathHelper.ceil(deltaZ);
         double dx = deltaX / (double)stepsX;
         double dy = deltaY / (double)stepsY;
         double dz = deltaZ / (double)stepsZ;
         if (this.camX < minX) {
            if (IntUtil.anyMatch(0, stepsY, 0, stepsZ, (y, z) -> {
               return this.engine.raytraceUncachedThreshold(minX, minY + (double)y * dy, minZ + (double)z * dz, EntityCullingConfig.raytraceThreshold);
            })) {
               return true;
            }
         } else if (this.camX > maxX && IntUtil.anyMatch(0, stepsY, 0, stepsZ, (y, z) -> {
            return this.engine.raytraceUncachedThreshold(minX, minY + (double)y * dy, minZ + (double)z * dz, EntityCullingConfig.raytraceThreshold);
         })) {
            return true;
         }

         if (this.camY < minY) {
            if (IntUtil.anyMatch(this.camX < minX ? 1 : 0, this.camX > maxX ? stepsX - 1 : stepsX, 0, stepsZ, (x, z) -> {
               return this.engine.raytraceUncachedThreshold(minX + (double)x * dx, minY, minZ + (double)z * dz, EntityCullingConfig.raytraceThreshold);
            })) {
               return true;
            }
         } else if (this.camY > maxY && IntUtil.anyMatch(this.camX < minX ? 1 : 0, this.camX > maxX ? stepsX - 1 : stepsX, 0, stepsZ, (x, z) -> {
            return this.engine.raytraceUncachedThreshold(minX + (double)x * dx, minY, minZ + (double)z * dz, EntityCullingConfig.raytraceThreshold);
         })) {
            return true;
         }

         if (this.camZ < minZ) {
            if (IntUtil.anyMatch(this.camX < minX ? 1 : 0, this.camX > maxX ? stepsX - 1 : stepsX, this.camY < minY ? 1 : 0, this.camY > maxX ? stepsY - 1 : stepsY, (x, y) -> {
               return this.engine.raytraceUncachedThreshold(minX + (double)x * dx, minY + (double)y * dy, minZ, EntityCullingConfig.raytraceThreshold);
            })) {
               return true;
            }
         } else if (this.camZ > maxZ && IntUtil.anyMatch(this.camX < minX ? 1 : 0, this.camX > maxX ? stepsX - 1 : stepsX, this.camY < minY ? 1 : 0, this.camY > maxX ? stepsY - 1 : stepsY, (x, y) -> {
            return this.engine.raytraceUncachedThreshold(minX + (double)x * dx, minY + (double)y * dy, minZ, EntityCullingConfig.raytraceThreshold);
         })) {
            return true;
         }

         return false;
      }
   }
}
