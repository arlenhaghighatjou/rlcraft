package me.ichun.mods.ichunutil.common.module.worldportals.client.render;

import java.util.ArrayList;
import java.util.Queue;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.culling.ClippingHelperPortal;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.culling.Frustum;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.RenderGlobalProxy;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import me.ichun.mods.ichunutil.common.module.worldportals.common.portal.EntityTransformationStack;
import me.ichun.mods.ichunutil.common.module.worldportals.common.portal.WorldPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class WorldPortalRenderer {
   public static int renderLevel = 0;
   public static int renderCount = 0;
   public static int frameCount;
   public static ArrayList<Float> rollFactor = new ArrayList();

   public WorldPortalRenderer() {
   }

   public static float getRollFactor(int level, float partialTick) {
      if (level <= 0) {
         return EntityHelper.interpolateValues(WorldPortals.eventHandlerClient.prevCameraRoll, WorldPortals.eventHandlerClient.cameraRoll, partialTick);
      } else if (level > rollFactor.size()) {
         return 0.0F;
      } else {
         float roll = EntityHelper.interpolateValues(WorldPortals.eventHandlerClient.prevCameraRoll, WorldPortals.eventHandlerClient.cameraRoll, partialTick);

         for(int i = level - 1; i >= 0; --i) {
            roll += ((Float)rollFactor.get(i)).floatValue();
         }

         return roll;
      }
   }

   public static void renderWorldPortal(Minecraft mc, WorldPortal portal, Entity ent, float[] appliedOffset, float[] appliedRotation, float partialTick) {
      if (RendererHelper.canUseStencils() && renderLevel <= iChunUtil.config.maxRecursion - 1 && renderCount < iChunUtil.config.maxRendersPerTick && portal.hasPair()) {
         ++renderLevel;
         ++renderCount;
         GL11.glEnable(2960);
         GlStateManager.colorMask(false, false, false, false);
         GlStateManager.depthMask(true);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glStencilFunc(519, iChunUtil.config.stencilValue + renderLevel, 255);
         if (renderLevel == 1) {
            GL11.glStencilOp(7680, 7680, 7681);
         } else {
            GL11.glStencilOp(7680, 7680, 7682);
         }

         GL11.glStencilMask(255);
         GL11.glClearStencil(0);
         if (renderLevel == 1) {
            GlStateManager.clear(1024);
         }

         GlStateManager.disableTexture2D();
         portal.drawPlane(partialTick);
         GL11.glStencilMask(0);
         GL11.glStencilFunc(514, iChunUtil.config.stencilValue + renderLevel, 255);
         GlStateManager.depthFunc(519);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GL11.glBegin(7);
         GL11.glVertex3d(1.0D, -1.0D, 1.0D);
         GL11.glVertex3d(1.0D, 1.0D, 1.0D);
         GL11.glVertex3d(-1.0D, 1.0D, 1.0D);
         GL11.glVertex3d(-1.0D, -1.0D, 1.0D);
         GL11.glEnd();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.depthFunc(515);
         GlStateManager.colorMask(true, true, true, true);
         GlStateManager.enableNormalize();
         GlStateManager.enableTexture2D();
         GlStateManager.enableLighting();
         rollFactor.add(Float.valueOf(appliedRotation[2]));
         drawWorld(mc, portal, ent, appliedOffset, appliedRotation, partialTick);
         if (!rollFactor.isEmpty()) {
            rollFactor.remove(rollFactor.size() - 1);
         }

         GlStateManager.disableTexture2D();
         GL11.glColorMask(false, false, false, false);
         if (renderLevel > 1) {
            GL11.glStencilFunc(519, iChunUtil.config.stencilValue + renderLevel - 1, 255);
            GL11.glStencilOp(7680, 7680, 7683);
            GL11.glStencilMask(255);
         }

         portal.drawPlane(partialTick);
         if (renderLevel > 1) {
            GL11.glStencilMask(0);
            GL11.glStencilFunc(514, iChunUtil.config.stencilValue + renderLevel - 1, 255);
         }

         GL11.glColorMask(true, true, true, true);
         GlStateManager.enableTexture2D();
         if (renderLevel == 1) {
            GL11.glDisable(2960);
         }

         --renderLevel;
      }

   }

   private static void drawWorld(Minecraft mc, WorldPortal worldPortal, Entity renderer, float[] posOffset, float[] rotOffset, float partialTick) {
      WorldPortal pair = worldPortal.getPair();
      TileEntityRendererDispatcher.instance.drawBatch(MinecraftForgeClient.getRenderPass());
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      boolean hideGui = mc.gameSettings.hideGUI;
      mc.gameSettings.hideGUI = true;
      double cameraZoom = mc.entityRenderer.cameraZoom;
      mc.entityRenderer.cameraZoom = 1.0125000476837158D;
      int renderDist = mc.gameSettings.renderDistanceChunks;
      mc.gameSettings.renderDistanceChunks = worldPortal.getRenderDistanceChunks();
      RenderGlobal global = mc.renderGlobal;
      mc.renderGlobal = WorldPortals.eventHandlerClient.renderGlobalProxy;
      WorldPortals.eventHandlerClient.renderGlobalProxy.cloudTickCounter = global.cloudTickCounter;
      WorldPortals.eventHandlerClient.renderGlobalProxy.bindViewFrustum(pair);
      WorldPortals.eventHandlerClient.renderGlobalProxy.displayListEntitiesDirty = true;
      WorldPortals.eventHandlerClient.renderGlobalProxy.storePlayerInfo();
      AxisAlignedBB pairFlatPlane = pair.getFlatPlane();
      double destX = (pairFlatPlane.maxX + pairFlatPlane.minX) / 2.0D;
      double destY = (pairFlatPlane.maxY + pairFlatPlane.minY) / 2.0D;
      double destZ = (pairFlatPlane.maxZ + pairFlatPlane.minZ) / 2.0D;
      if (mc.gameSettings.thirdPersonView == 0) {
         ItemStack usable = ItemHandler.getUsableDualHandedItem(mc.player);
         if (!usable.isEmpty()) {
            mc.player.setActiveHand(usable.equals(mc.player.getHeldItem(EnumHand.MAIN_HAND)) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
         }
      }

      EntityTransformationStack ets = (new EntityTransformationStack(renderer)).moveEntity(destX, destY, destZ, posOffset, rotOffset, partialTick);
      drawWorld(WorldPortals.eventHandlerClient.renderGlobalProxy, renderer, worldPortal, pair, partialTick);
      ets.pop();
      if (mc.gameSettings.thirdPersonView == 0 && !ItemHandler.getUsableDualHandedItem(mc.player).isEmpty()) {
         mc.player.resetActiveHand();
      }

      mc.gameSettings.renderDistanceChunks = renderDist;
      mc.renderGlobal = global;
      mc.entityRenderer.cameraZoom = cameraZoom;
      mc.gameSettings.hideGUI = hideGui;
      ForgeHooksClient.setRenderPass(0);
      double d0 = renderer.lastTickPosX + (renderer.posX - renderer.lastTickPosX) * (double)partialTick;
      double d1 = renderer.lastTickPosY + (renderer.posY - renderer.lastTickPosY) * (double)partialTick;
      double d2 = renderer.lastTickPosZ + (renderer.posZ - renderer.lastTickPosZ) * (double)partialTick;
      if (renderLevel > 1) {
         WorldPortals.eventHandlerClient.renderGlobalProxy.bindViewFrustum(pair);
         WorldPortals.eventHandlerClient.renderGlobalProxy.renderContainer.initialize(d0, d1, d2);
         WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityX = renderer.posX;
         WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityY = renderer.posY;
         WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityZ = renderer.posZ;
         WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityPitch = (double)renderer.rotationPitch;
         WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityYaw = (double)renderer.rotationYaw;
      }

      TileEntityRendererDispatcher.instance.prepare(mc.world, mc.getTextureManager(), mc.fontRenderer, renderer, mc.objectMouseOver, partialTick);
      mc.getRenderManager().cacheActiveRenderInfo(mc.world, mc.fontRenderer, renderer, mc.pointedEntity, mc.gameSettings, partialTick);
      mc.getRenderManager().renderPosX = d0;
      mc.getRenderManager().renderPosY = d1;
      mc.getRenderManager().renderPosZ = d2;
      TileEntityRendererDispatcher.staticPlayerX = d0;
      TileEntityRendererDispatcher.staticPlayerY = d1;
      TileEntityRendererDispatcher.staticPlayerZ = d2;
      ActiveRenderInfo.updateRenderInfo(mc.player, false);
      ClippingHelperPortal.getInstance();
      Particle.interpPosX = d0;
      Particle.interpPosY = d1;
      Particle.interpPosZ = d2;
      Particle.cameraViewDir = renderer.getLook(partialTick);
      GlStateManager.popMatrix();
      TileEntityRendererDispatcher.instance.preDrawBatch();
   }

   private static void drawWorld(RenderGlobalProxy renderglobal, Entity entity, WorldPortal portal, WorldPortal pair, float partialTick) {
      Minecraft mc = Minecraft.getMinecraft();
      ParticleManager particlemanager = mc.effectRenderer;
      GlStateManager.enableCull();
      GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
      GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.enableLighting();
      GlStateManager.disableFog();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTick;
      double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTick;
      double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTick;
      setupCameraTransform(mc, mc.entityRenderer, entity, partialTick, 2);
      ActiveRenderInfo.updateRenderInfo(mc.player, false);
      ClippingHelperPortal.getInstance();
      ICamera icamera = new Frustum();
      icamera.setPosition(d0, d1, d2);
      RenderHelper.disableStandardItemLighting();
      if (mc.gameSettings.renderDistanceChunks >= 4) {
         mc.entityRenderer.setupFog(-1, partialTick);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(mc.entityRenderer.getFOVModifier(partialTick, true), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, mc.entityRenderer.farPlaneDistance * 2.0F);
         GlStateManager.matrixMode(5888);
         renderglobal.renderSky(partialTick, 2);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(mc.entityRenderer.getFOVModifier(partialTick, true), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, mc.entityRenderer.farPlaneDistance * MathHelper.SQRT_2);
         GlStateManager.matrixMode(5888);
      }

      mc.entityRenderer.setupFog(0, partialTick);
      GlStateManager.shadeModel(7425);
      if (entity.posY + (double)entity.getEyeHeight() < 128.0D) {
         mc.entityRenderer.renderCloudsCheck(renderglobal, partialTick, 2, d0, d1, d2);
      }

      mc.entityRenderer.setupFog(0, partialTick);
      mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      RenderHelper.disableStandardItemLighting();
      renderglobal.setupTerrain(entity, (double)partialTick, icamera, frameCount++, mc.player.isSpectator());
      int j = Math.min(Minecraft.getDebugFPS(), mc.gameSettings.limitFramerate);
      j = Math.max(j, 60);
      long l = Math.max((long)(1000000000 / j / 4), 0L);
      mc.renderGlobal.updateChunks(System.nanoTime() + l);
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlpha();
      renderglobal.renderBlockLayer(BlockRenderLayer.SOLID, (double)partialTick, 2, entity);
      GlStateManager.enableAlpha();
      renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double)partialTick, 2, entity);
      mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT, (double)partialTick, 2, entity);
      mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      RenderHelper.enableStandardItemLighting();
      ForgeHooksClient.setRenderPass(0);
      renderglobal.renderEntities(entity, icamera, partialTick, portal, pair);
      ForgeHooksClient.setRenderPass(0);
      RenderHelper.disableStandardItemLighting();
      mc.entityRenderer.disableLightmap();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
      mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, partialTick);
      mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.disableBlend();
      mc.entityRenderer.enableLightmap();
      Particle.interpPosX = d0;
      Particle.interpPosY = d1;
      Particle.interpPosZ = d2;
      Particle.cameraViewDir = entity.getLook(partialTick);
      float f = 0.017453292F;
      float f1 = MathHelper.cos(entity.rotationYaw * 0.017453292F);
      float f2 = MathHelper.sin(entity.rotationYaw * 0.017453292F);
      float f3 = -f2 * MathHelper.sin(entity.rotationPitch * 0.017453292F);
      float f4 = f1 * MathHelper.sin(entity.rotationPitch * 0.017453292F);
      float f5 = MathHelper.cos(entity.rotationPitch * 0.017453292F);

      for(int i = 0; i < 2; ++i) {
         Queue<Particle> queue = particlemanager.fxLayers[3][i];
         if (!queue.isEmpty()) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer bufferbuilder = tessellator.getBuffer();

            for(Particle particle : queue) {
               if (!portal.getCullRender() || canDrawParticle(particle, pair.getFaceOn(), pair.getPos())) {
                  particle.renderParticle(bufferbuilder, entity, partialTick, f1, f5, f2, f3, f4);
               }
            }
         }
      }

      RenderHelper.disableStandardItemLighting();
      mc.entityRenderer.setupFog(0, partialTick);
      float ff = ActiveRenderInfo.getRotationX();
      float ff1 = ActiveRenderInfo.getRotationZ();
      float ff2 = ActiveRenderInfo.getRotationYZ();
      float ff3 = ActiveRenderInfo.getRotationXY();
      float ff4 = ActiveRenderInfo.getRotationXZ();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.alphaFunc(516, 0.003921569F);

      for(int i_nf = 0; i_nf < 3; ++i_nf) {
         int i = i_nf;

         for(int jj = 0; jj < 2; ++jj) {
            if (!particlemanager.fxLayers[i][jj].isEmpty()) {
               switch(jj) {
               case 0:
                  GlStateManager.depthMask(false);
                  break;
               case 1:
                  GlStateManager.depthMask(true);
               }

               switch(i) {
               case 0:
               default:
                  mc.getTextureManager().bindTexture(ParticleManager.PARTICLE_TEXTURES);
                  break;
               case 1:
                  mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
               }

               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               Tessellator tessellator = Tessellator.getInstance();
               WorldRenderer bufferbuilder = tessellator.getBuffer();
               bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

               for(Particle particle : particlemanager.fxLayers[i][jj]) {
                  if (!portal.getCullRender() || canDrawParticle(particle, pair.getFaceOn(), pair.getPos())) {
                     particle.renderParticle(bufferbuilder, entity, partialTick, ff, ff4, ff1, ff2, ff3);
                  }
               }

               tessellator.draw();
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.alphaFunc(516, 0.1F);
      mc.entityRenderer.disableLightmap();
      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      mc.entityRenderer.renderRainSnow(partialTick);
      GlStateManager.depthMask(true);
      renderglobal.renderWorldBorder(entity, partialTick);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.alphaFunc(516, 0.1F);
      mc.entityRenderer.setupFog(0, partialTick);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.shadeModel(7425);
      renderglobal.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, (double)partialTick, 2, entity);
      RenderHelper.enableStandardItemLighting();
      ForgeHooksClient.setRenderPass(1);
      renderglobal.renderEntities(entity, icamera, partialTick, portal, pair);
      ForgeHooksClient.setRenderPass(-1);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (entity.posY + (double)entity.getEyeHeight() >= 128.0D) {
         mc.entityRenderer.renderCloudsCheck(renderglobal, partialTick, 2, d0, d1, d2);
      }

      ForgeHooksClient.dispatchRenderLast(renderglobal, partialTick);
      RenderHelper.enableStandardItemLighting();
   }

   private static void setupCameraTransform(Minecraft mc, EntityRenderer entityRenderer, Entity entity, float partialTicks, int pass) {
      entityRenderer.farPlaneDistance = (float)(mc.gameSettings.renderDistanceChunks * 16);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      float f = 0.07F;
      if (mc.gameSettings.anaglyph) {
         GlStateManager.translate((float)(-(pass * 2 - 1)) * f, 0.0F, 0.0F);
      }

      if (entityRenderer.cameraZoom != 1.0D) {
         GlStateManager.translate((float)entityRenderer.cameraYaw, (float)(-entityRenderer.cameraPitch), 0.0F);
         GlStateManager.scale(entityRenderer.cameraZoom, entityRenderer.cameraZoom, 1.0D);
      }

      Project.gluPerspective(entityRenderer.getFOVModifier(partialTicks, true), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, entityRenderer.farPlaneDistance * MathHelper.SQRT_2);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      if (mc.gameSettings.anaglyph) {
         GlStateManager.translate((float)(pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      entityRenderer.hurtCameraEffect(partialTicks);
      if (mc.gameSettings.viewBobbing) {
         entityRenderer.applyBobbing(partialTicks);
      }

      float eyeHeight = entity.getEyeHeight();
      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping()) {
         eyeHeight = (float)((double)eyeHeight + 1.0D);
         GlStateManager.translate(0.0F, 0.3F, 0.0F);
         if (!mc.gameSettings.debugCamEnable) {
            BlockPos blockpos = new BlockPos(entity);
            IBlockState iblockstate = mc.world.getBlockState(blockpos);
            ForgeHooksClient.orientBedCamera(mc.world, blockpos, iblockstate, entity);
            GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
         }
      } else if (mc.gameSettings.thirdPersonView > 0) {
         float var10000 = entityRenderer.thirdPersonDistancePrev;
         entityRenderer.getClass();
         double d3 = (double)(var10000 + (4.0F - entityRenderer.thirdPersonDistancePrev) * partialTicks);
         if (mc.gameSettings.debugCamEnable) {
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
         } else {
            float f1 = entity.rotationYaw;
            float f2 = entity.rotationPitch;
            if (mc.gameSettings.thirdPersonView == 2) {
               f2 += 180.0F;
            }

            if (entityRenderer.mc.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.translate(0.0F, 0.0F, 0.05F);
      }

      if (!mc.gameSettings.debugCamEnable) {
         float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
         float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
         float roll = getRollFactor(renderLevel, partialTicks);
         if (entity instanceof EntityAnimal) {
            EntityAnimal entityanimal = (EntityAnimal)entity;
            yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
         }

         IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(entityRenderer.mc.world, entity, partialTicks);
         CameraSetup event = new CameraSetup(entityRenderer, entity, state, (double)partialTicks, yaw, pitch, roll);
         MinecraftForge.EVENT_BUS.post(event);
         GlStateManager.rotate(event.getRoll(), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);
      }

      GlStateManager.translate(0.0F, -eyeHeight, 0.0F);
      double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
      double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)eyeHeight;
      double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
      entityRenderer.cloudFog = mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
      if (entityRenderer.debugView) {
         switch(entityRenderer.debugViewDirection) {
         case 0:
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            break;
         case 1:
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            break;
         case 2:
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            break;
         case 3:
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            break;
         case 4:
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
         }
      }

   }

   private static boolean canDrawParticle(Particle ent, EnumFacing face, BlockPos pos) {
      return (face.getFrontOffsetX() >= 0 || ent.posX <= (double)(pos.getX() + 2)) && (face.getFrontOffsetX() <= 0 || ent.posX >= (double)(pos.getX() - 1)) && (face.getFrontOffsetY() >= 0 || (ent.getBoundingBox().maxY + ent.getBoundingBox().minY) / 2.0D <= (double)(pos.getY() + 2)) && (face.getFrontOffsetY() <= 0 || (ent.getBoundingBox().maxY + ent.getBoundingBox().minY) / 2.0D >= (double)(pos.getY() - 1)) && (face.getFrontOffsetZ() >= 0 || ent.posZ <= (double)(pos.getZ() + 2)) && (face.getFrontOffsetZ() <= 0 || ent.posZ >= (double)(pos.getZ() - 1));
   }
}
