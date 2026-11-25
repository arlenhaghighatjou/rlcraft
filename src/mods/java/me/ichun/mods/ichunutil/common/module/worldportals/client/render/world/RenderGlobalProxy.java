package me.ichun.mods.ichunutil.common.module.worldportals.client.render.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.chunk.IRenderChunkWorldPortal;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.factory.ListChunkFactory;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.factory.VboChunkFactory;
import me.ichun.mods.ichunutil.common.module.worldportals.common.portal.WorldPortal;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

public class RenderGlobalProxy extends RenderGlobal {
   public HashMap<WorldPortal, ViewFrustum> usedViewFrustums = new HashMap();
   public ArrayList<ViewFrustum> freeViewFrustums = new ArrayList();
   public float playerPrevYaw;
   public float playerPrevPitch;
   public float playerYaw;
   public float playerPitch;
   public float playerPrevHeadYaw;
   public float playerHeadYaw;
   public double playerPosX;
   public double playerPosY;
   public double playerPosZ;
   public double playerPrevPosX;
   public double playerPrevPosY;
   public double playerPrevPosZ;
   public double playerLastTickX;
   public double playerLastTickY;
   public double playerLastTickZ;
   public boolean released;

   public RenderGlobalProxy(Minecraft mcIn) {
      super(mcIn);
      if (this.vboEnabled) {
         this.renderContainer = new VboRenderList();
         this.renderChunkFactory = new VboChunkFactory();
      } else {
         this.renderContainer = new RenderList();
         this.renderChunkFactory = new ListChunkFactory();
      }

   }

   public void loadRenderers() {
      if (this.world != null) {
         if (this.renderDispatcher == null) {
            this.renderDispatcher = new ChunkRenderDispatcher();
         }

         this.displayListEntitiesDirty = true;
         Blocks.LEAVES.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
         Blocks.LEAVES2.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
         this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
         boolean flag = this.vboEnabled;
         this.vboEnabled = OpenGlHelper.useVbo();
         if (flag && !this.vboEnabled) {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
         } else if (!flag && this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
         }

         if (flag != this.vboEnabled) {
            this.generateStars();
            this.generateSky();
            this.generateSky2();
         }

         this.cleanViewFrustums();
         this.stopChunkUpdates();
         synchronized(this.setTileEntities) {
            this.setTileEntities.clear();
         }

         this.renderEntitiesStartupCounter = 2;
      }

   }

   public void cleanViewFrustums() {
      for(Entry<WorldPortal, ViewFrustum> e : this.usedViewFrustums.entrySet()) {
         ((ViewFrustum)e.getValue()).deleteGlResources();
         if (e.getValue() == this.viewFrustum) {
            this.viewFrustum = null;
         }
      }

      this.usedViewFrustums.clear();

      for(ViewFrustum frustum : this.freeViewFrustums) {
         frustum.deleteGlResources();
         if (frustum == this.viewFrustum) {
            this.viewFrustum = null;
         }
      }

      this.freeViewFrustums.clear();
      if (this.viewFrustum != null) {
         this.viewFrustum.deleteGlResources();
      }

      this.freeViewFrustums.add(new ViewFrustum(this.world, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory));
      this.viewFrustum = (ViewFrustum)this.freeViewFrustums.get(0);
   }

   public void releaseViewFrustum(WorldPortal pm) {
      ViewFrustum vf = (ViewFrustum)this.usedViewFrustums.get(pm);
      if (vf != null) {
         this.freeViewFrustums.add(vf);
         this.usedViewFrustums.remove(pm);
      }

   }

   public void bindViewFrustum(WorldPortal pm) {
      ViewFrustum vf = (ViewFrustum)this.usedViewFrustums.get(pm);
      if (vf == null) {
         if (this.freeViewFrustums.isEmpty()) {
            vf = new ViewFrustum(this.world, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
         } else {
            vf = (ViewFrustum)this.freeViewFrustums.get(0);
            this.freeViewFrustums.remove(0);
         }

         this.usedViewFrustums.put(pm, vf);
         if (this.world != null) {
            Entity entity = this.mc.getRenderViewEntity();
            if (entity != null) {
               vf.updateChunkPositions(entity.posX, entity.posZ);

               for(RenderChunk renderChunk : vf.renderChunks) {
                  renderChunk.setNeedsUpdate(false);
               }
            }
         }
      }

      this.viewFrustum = vf;

      for(RenderChunk renderChunk : this.viewFrustum.renderChunks) {
         if (renderChunk instanceof IRenderChunkWorldPortal) {
            ((IRenderChunkWorldPortal)renderChunk).setCurrentPositionsAndFaces(pm.getPos(), pm.getFaceOn());
            if (pm.getPair() != null) {
               ((IRenderChunkWorldPortal)renderChunk).setNoCull(!pm.getPair().getCullRender());
            }
         }
      }

   }

   public void storePlayerInfo() {
      this.playerPrevYaw = this.mc.player.prevRotationYaw;
      this.playerPrevPitch = this.mc.player.prevRotationPitch;
      this.playerYaw = this.mc.player.rotationYaw;
      this.playerPitch = this.mc.player.rotationPitch;
      this.playerPrevHeadYaw = this.mc.player.prevRotationYawHead;
      this.playerHeadYaw = this.mc.player.rotationYawHead;
      this.playerPosX = this.mc.player.posX;
      this.playerPosY = this.mc.player.posY;
      this.playerPosZ = this.mc.player.posZ;
      this.playerPrevPosX = this.mc.player.prevPosX;
      this.playerPrevPosY = this.mc.player.prevPosY;
      this.playerPrevPosZ = this.mc.player.prevPosZ;
      this.playerLastTickX = this.mc.player.lastTickPosX;
      this.playerLastTickY = this.mc.player.lastTickPosY;
      this.playerLastTickZ = this.mc.player.lastTickPosZ;
   }

   public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
      this.renderEntities(renderViewEntity, camera, partialTicks, (WorldPortal)null, (WorldPortal)null);
   }

   public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks, @Nullable WorldPortal portal, @Nullable WorldPortal pair) {
      int pass = MinecraftForgeClient.getRenderPass();
      if (this.renderEntitiesStartupCounter > 0) {
         if (pass > 0) {
            return;
         }

         --this.renderEntitiesStartupCounter;
      } else {
         double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
         double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
         double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
         TileEntityRendererDispatcher.instance.prepare(this.world, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.getRenderViewEntity(), this.mc.objectMouseOver, partialTicks);
         this.renderManager.cacheActiveRenderInfo(this.world, this.mc.fontRenderer, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
         if (pass == 0) {
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
         }

         Entity entity = this.mc.getRenderViewEntity();
         double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
         double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
         double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
         TileEntityRendererDispatcher.staticPlayerX = d3;
         TileEntityRendererDispatcher.staticPlayerY = d4;
         TileEntityRendererDispatcher.staticPlayerZ = d5;
         this.renderManager.setRenderPosition(d3, d4, d5);
         this.mc.entityRenderer.enableLightmap();
         List<Entity> list = this.world.getLoadedEntityList();
         if (pass == 0) {
            this.countEntitiesTotal = list.size();
         }

         for(int i = 0; i < this.world.weatherEffects.size(); ++i) {
            Entity entity1 = (Entity)this.world.weatherEffects.get(i);
            if ((pair == null || this.shouldRenderEntity(entity1, pair)) && entity1.shouldRenderInPass(pass)) {
               ++this.countEntitiesRendered;
               if (entity1.isInRangeToRender3d(d0, d1, d2)) {
                  this.renderManager.renderEntityStatic(entity1, partialTicks, false);
               }
            }
         }

         List<Entity> list1 = Lists.newArrayList();
         List<Entity> list2 = Lists.newArrayList();
         float prevYaw = this.mc.player.prevRotationYaw;
         float prevPitch = this.mc.player.prevRotationPitch;
         float yaw = this.mc.player.rotationYaw;
         float pitch = this.mc.player.rotationPitch;
         float prevYawHead = this.mc.player.prevRotationYawHead;
         float yawHead = this.mc.player.rotationYawHead;
         double posX = this.mc.player.posX;
         double posY = this.mc.player.posY;
         double posZ = this.mc.player.posZ;
         double prevPosX = this.mc.player.prevPosX;
         double prevPosY = this.mc.player.prevPosY;
         double prevPosZ = this.mc.player.prevPosZ;
         double lastX = this.mc.player.lastTickPosX;
         double lastY = this.mc.player.lastTickPosY;
         double lastZ = this.mc.player.lastTickPosZ;
         this.mc.player.prevRotationYaw = this.playerPrevYaw;
         this.mc.player.prevRotationPitch = this.playerPrevPitch;
         this.mc.player.rotationYaw = this.playerYaw;
         this.mc.player.rotationPitch = this.playerPitch;
         this.mc.player.prevRotationYawHead = this.playerPrevHeadYaw;
         this.mc.player.rotationYawHead = this.playerHeadYaw;
         this.mc.player.posX = this.playerPosX;
         this.mc.player.posY = this.playerPosY;
         this.mc.player.posZ = this.playerPosZ;
         this.mc.player.prevPosX = this.playerPrevPosX;
         this.mc.player.prevPosY = this.playerPrevPosY;
         this.mc.player.prevPosZ = this.playerPrevPosZ;
         this.mc.player.lastTickPosX = this.playerLastTickX;
         this.mc.player.lastTickPosY = this.playerLastTickY;
         this.mc.player.lastTickPosZ = this.playerLastTickZ;

         for(ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos) {
            Chunk chunk = this.world.getChunkFromBlockCoords(renderglobal$containerlocalrenderinformation.renderChunk.getPosition());
            ClassInheritanceMultiMap<Entity> classinheritancemultimap = chunk.getEntityLists()[renderglobal$containerlocalrenderinformation.renderChunk.getPosition().getY() / 16];
            if (!classinheritancemultimap.isEmpty()) {
               for(Entity entity2 : classinheritancemultimap) {
                  if (portal != null && pair != null && (entity2 != this.mc.player || this.mc.gameSettings.thirdPersonView != 0) && portal.lastScanEntities.contains(entity2) && portal.getPortalInsides(entity2).intersects(entity2.getEntityBoundingBox())) {
                     double eePosX = entity2.lastTickPosX + (entity2.posX - entity2.lastTickPosX) * (double)partialTicks;
                     double eePosY = entity2.lastTickPosY + (entity2.posY - entity2.lastTickPosY) * (double)partialTicks;
                     double eePosZ = entity2.lastTickPosZ + (entity2.posZ - entity2.lastTickPosZ) * (double)partialTicks;
                     AxisAlignedBB flatPlane = portal.getFlatPlane();
                     double centerX = (flatPlane.maxX + flatPlane.minX) / 2.0D;
                     double centerY = (flatPlane.maxY + flatPlane.minY) / 2.0D;
                     double centerZ = (flatPlane.maxZ + flatPlane.minZ) / 2.0D;
                     AxisAlignedBB pairFlatPlane = portal.getPair().getFlatPlane();
                     double destX = (pairFlatPlane.maxX + pairFlatPlane.minX) / 2.0D;
                     double destY = (pairFlatPlane.maxY + pairFlatPlane.minY) / 2.0D;
                     double destZ = (pairFlatPlane.maxZ + pairFlatPlane.minZ) / 2.0D;
                     GlStateManager.pushMatrix();
                     double rotX = eePosX - d3;
                     double rotY = eePosY - d4;
                     double rotZ = eePosZ - d5;
                     float[] appliedOffset = portal.getQuaternionFormula().applyPositionalRotation(new float[]{(float)(eePosX - centerX), (float)(eePosY - centerY), (float)(eePosZ - centerZ)});
                     float[] appliedRot = portal.getQuaternionFormula().applyRotationalRotation(new float[]{180.0F, 0.0F, 0.0F});
                     GlStateManager.translate(destX - eePosX + (double)appliedOffset[0], destY - eePosY + (double)appliedOffset[1], destZ - eePosZ + (double)appliedOffset[2]);
                     GlStateManager.translate(rotX, rotY, rotZ);
                     GlStateManager.rotate(-appliedRot[0], 0.0F, 1.0F, 0.0F);
                     GlStateManager.rotate(-appliedRot[1], 1.0F, 0.0F, 0.0F);
                     GlStateManager.rotate(-appliedRot[2], 0.0F, 0.0F, 1.0F);
                     GlStateManager.translate(-rotX, -rotY, -rotZ);
                     this.renderManager.renderEntityStatic(entity2, partialTicks, false);
                     GlStateManager.popMatrix();
                  }

                  if ((pair == null || this.shouldRenderEntity(entity2, pair)) && entity2.shouldRenderInPass(pass)) {
                     boolean flag = this.renderManager.shouldRender(entity2, camera, d0, d1, d2) || entity2 == this.mc.player || entity2.isRidingOrBeingRiddenBy(this.mc.player);
                     if (flag && (entity2.posY < 0.0D || entity2.posY >= 256.0D || this.world.isBlockLoaded(new BlockPos(entity2)))) {
                        ++this.countEntitiesRendered;
                        boolean disableStencil = false;
                        if (pair != null && pair.lastScanEntities.contains(entity2) && pair.portalInsides.intersects(entity2.getEntityBoundingBox())) {
                           disableStencil = true;
                        }

                        if (disableStencil) {
                           GL11.glDisable(2960);
                        }

                        this.renderManager.renderEntityStatic(entity2, partialTicks, false);
                        if (disableStencil) {
                           GL11.glEnable(2960);
                        }

                        if (this.isOutlineActive(entity2, entity, camera)) {
                           list1.add(entity2);
                        }

                        if (this.renderManager.isRenderMultipass(entity2)) {
                           list2.add(entity2);
                        }
                     }
                  }
               }
            }
         }

         if (!list2.isEmpty()) {
            for(Entity entity3 : list2) {
               this.renderManager.renderMultipass(entity3, partialTicks);
            }
         }

         if (this.isRenderEntityOutlines() && (!list1.isEmpty() || this.entityOutlinesRendered)) {
            this.entityOutlineFramebuffer.framebufferClear();
            this.entityOutlinesRendered = !list1.isEmpty();
            if (!list1.isEmpty()) {
               GlStateManager.depthFunc(519);
               GlStateManager.disableFog();
               this.entityOutlineFramebuffer.bindFramebuffer(false);
               RenderHelper.disableStandardItemLighting();
               this.renderManager.setRenderOutlines(true);

               for(int j = 0; j < list1.size(); ++j) {
                  this.renderManager.renderEntityStatic((Entity)list1.get(j), partialTicks, false);
               }

               this.renderManager.setRenderOutlines(false);
               RenderHelper.enableStandardItemLighting();
               GlStateManager.depthMask(false);
               this.entityOutlineShader.render(partialTicks);
               GlStateManager.enableLighting();
               GlStateManager.depthMask(true);
               GlStateManager.enableFog();
               GlStateManager.enableBlend();
               GlStateManager.enableColorMaterial();
               GlStateManager.depthFunc(515);
               GlStateManager.enableDepth();
               GlStateManager.enableAlpha();
            }

            this.mc.getFramebuffer().bindFramebuffer(false);
         }

         this.mc.player.prevRotationYaw = prevYaw;
         this.mc.player.prevRotationPitch = prevPitch;
         this.mc.player.rotationYaw = yaw;
         this.mc.player.rotationPitch = pitch;
         this.mc.player.prevRotationYawHead = prevYawHead;
         this.mc.player.rotationYawHead = yawHead;
         this.mc.player.posX = posX;
         this.mc.player.posY = posY;
         this.mc.player.posZ = posZ;
         this.mc.player.prevPosX = prevPosX;
         this.mc.player.prevPosY = prevPosY;
         this.mc.player.prevPosZ = prevPosZ;
         this.mc.player.lastTickPosX = lastX;
         this.mc.player.lastTickPosY = lastY;
         this.mc.player.lastTickPosZ = lastZ;
         RenderHelper.enableStandardItemLighting();
         TileEntityRendererDispatcher.instance.preDrawBatch();

         for(ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 : this.renderInfos) {
            List<TileEntity> list3 = renderglobal$containerlocalrenderinformation1.renderChunk.getCompiledChunk().getTileEntities();
            if (!list3.isEmpty()) {
               for(TileEntity tileentity2 : list3) {
                  if (tileentity2.shouldRenderInPass(pass) && camera.isBoundingBoxInFrustum(tileentity2.getRenderBoundingBox())) {
                     TileEntityRendererDispatcher.instance.render(tileentity2, partialTicks, -1);
                  }
               }
            }
         }

         synchronized(this.setTileEntities) {
            for(TileEntity tileentity : this.setTileEntities) {
               if (tileentity.shouldRenderInPass(pass) && camera.isBoundingBoxInFrustum(tileentity.getRenderBoundingBox())) {
                  TileEntityRendererDispatcher.instance.render(tileentity, partialTicks, -1);
               }
            }
         }

         TileEntityRendererDispatcher.instance.drawBatch(pass);
         this.preRenderDamagedBlocks();

         for(DestroyBlockProgress destroyblockprogress : this.damagedBlocks.values()) {
            BlockPos blockpos = destroyblockprogress.getPosition();
            TileEntity tileentity1 = this.world.getTileEntity(blockpos);
            if (tileentity1 instanceof TileEntityChest) {
               TileEntityChest tileentitychest = (TileEntityChest)tileentity1;
               if (tileentitychest.adjacentChestXNeg != null) {
                  blockpos = blockpos.offset(EnumFacing.WEST);
                  tileentity1 = this.world.getTileEntity(blockpos);
               } else if (tileentitychest.adjacentChestZNeg != null) {
                  blockpos = blockpos.offset(EnumFacing.NORTH);
                  tileentity1 = this.world.getTileEntity(blockpos);
               }
            }

            Block block = this.world.getBlockState(blockpos).getBlock();
            if (tileentity1 != null && tileentity1.shouldRenderInPass(pass) && tileentity1.canRenderBreaking() && camera.isBoundingBoxInFrustum(tileentity1.getRenderBoundingBox())) {
               TileEntityRendererDispatcher.instance.render(tileentity1, partialTicks, destroyblockprogress.getPartialBlockDamage());
            }
         }

         this.postRenderDamagedBlocks();
         this.mc.entityRenderer.disableLightmap();
      }

   }

   public boolean shouldRenderEntity(Entity ent, WorldPortal portal) {
      EnumFacing faceOn = portal.getFaceOn();
      return portal.getPair() != null && !portal.getPair().getCullRender() || (faceOn.getFrontOffsetX() >= 0 || ent.getEntityBoundingBox().minX <= portal.getFlatPlane().minX) && (faceOn.getFrontOffsetX() <= 0 || ent.getEntityBoundingBox().maxX >= portal.getFlatPlane().minX) && (faceOn.getFrontOffsetY() >= 0 || (ent.getEntityBoundingBox().maxY + ent.getEntityBoundingBox().minY) / 2.0D <= portal.getFlatPlane().minY) && (faceOn.getFrontOffsetY() <= 0 || (ent.getEntityBoundingBox().maxY + ent.getEntityBoundingBox().minY) / 2.0D >= portal.getFlatPlane().minY) && (faceOn.getFrontOffsetZ() >= 0 || ent.getEntityBoundingBox().minZ <= portal.getFlatPlane().minZ) && (faceOn.getFrontOffsetZ() <= 0 || ent.getEntityBoundingBox().maxZ >= portal.getFlatPlane().minZ);
   }

   public void markBlocksForUpdate(int p_184385_1_, int p_184385_2_, int p_184385_3_, int p_184385_4_, int p_184385_5_, int p_184385_6_, boolean p_184385_7_) {
      for(Entry<WorldPortal, ViewFrustum> e : this.usedViewFrustums.entrySet()) {
         ((ViewFrustum)e.getValue()).markBlocksForUpdate(p_184385_1_, p_184385_2_, p_184385_3_, p_184385_4_, p_184385_5_, p_184385_6_, p_184385_7_);
      }

      for(ViewFrustum frustum : this.freeViewFrustums) {
         frustum.markBlocksForUpdate(p_184385_1_, p_184385_2_, p_184385_3_, p_184385_4_, p_184385_5_, p_184385_6_, p_184385_7_);
      }

   }

   public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos) {
   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
   }

   public void broadcastSound(int soundID, BlockPos pos, int data) {
   }

   public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
   }
}
