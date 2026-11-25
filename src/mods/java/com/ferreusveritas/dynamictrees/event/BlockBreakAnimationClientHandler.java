package com.ferreusveritas.dynamictrees.event;

import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchThick;
import com.ferreusveritas.dynamictrees.models.ICustomDamageModel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel.Builder;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockBreakAnimationClientHandler implements ISelectiveResourceReloadListener {
   public static final BlockBreakAnimationClientHandler instance = new BlockBreakAnimationClientHandler(Minecraft.getMinecraft());
   private static final Map<Integer, DestroyBlockProgress> damagedBranches = new ConcurrentHashMap();
   private int tickCounter = 0;
   private final EaglerTextureAtlasSprite[] destroyBlockIcons = new EaglerTextureAtlasSprite[10];
   static Method preRenderMethod = ReflectionHelper.findMethod(RenderGlobal.class, "preRenderDamagedBlocks", "preRenderDamagedBlocks", new Class[0]);
   static Method postRenderMethod = ReflectionHelper.findMethod(RenderGlobal.class, "postRenderDamagedBlocks", "postRenderDamagedBlocks", new Class[0]);

   private BlockBreakAnimationClientHandler(Minecraft mc) {
      ((IReloadableResourceManager)mc.getResourceManager()).registerReloadListener(this);
   }

   @SubscribeEvent
   public void onPlayerJoinWorldEvent(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityPlayerSP && Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getEntityId() == event.getEntity().getEntityId()) {
         event.getWorld().removeEventListener(Minecraft.getMinecraft().renderGlobal);
         List<IWorldEventListener> listeners = (List)ReflectionHelper.getPrivateValue(World.class, event.getWorld(), new String[]{"eventListeners", "eventListeners"});
         if (listeners.stream().noneMatch((el) -> {
            return el instanceof BlockBreakAnimationClientHandler.RenderGlobalWrapper;
         })) {
            event.getWorld().addEventListener(new BlockBreakAnimationClientHandler.RenderGlobalWrapper(event.getWorld()));
         }
      }

   }

   @SubscribeEvent
   public void clientTick(ClientTickEvent event) {
      ++this.tickCounter;
      if (this.tickCounter % 20 == 0) {
         this.cleanupExtraDamagedBlocks();
      }

   }

   @SubscribeEvent
   public void worldUnload(Unload event) {
      damagedBranches.clear();
   }

   @SubscribeEvent
   public void worldLoad(Load event) {
      damagedBranches.clear();
   }

   @SubscribeEvent
   public void renderBlockBreakAnim(RenderWorldLastEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      TextureManager textureManager = mc.getTextureManager();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
      textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      this.drawBlockDamageTexture(mc, textureManager, Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), mc.getRenderViewEntity(), event.getPartialTicks());
      textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.disableBlend();
   }

   private void cleanupExtraDamagedBlocks() {
      Iterator<Entry<Integer, DestroyBlockProgress>> iter = damagedBranches.entrySet().iterator();

      while(iter.hasNext()) {
         DestroyBlockProgress destroyblockprogress = (DestroyBlockProgress)((Entry)iter.next()).getValue();
         int tick = destroyblockprogress.getCreationCloudUpdateTick();
         if (this.tickCounter - tick > 400) {
            iter.remove();
         }
      }

   }

   public void sendThickBranchBreakProgress(int breakerId, BlockPos pos, int progress) {
      if (progress >= 0 && progress < 10) {
         DestroyBlockProgress destroyblockprogress = (DestroyBlockProgress)damagedBranches.get(Integer.valueOf(breakerId));
         if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != pos.getX() || destroyblockprogress.getPosition().getY() != pos.getY() || destroyblockprogress.getPosition().getZ() != pos.getZ()) {
            destroyblockprogress = new DestroyBlockProgress(breakerId, pos);
            damagedBranches.put(Integer.valueOf(breakerId), destroyblockprogress);
         }

         destroyblockprogress.setPartialBlockDamage(progress);
         destroyblockprogress.setCloudUpdateTick(this.tickCounter);
      } else {
         damagedBranches.remove(Integer.valueOf(breakerId));
      }

   }

   private void preRenderDamagedBlocks() {
      try {
         preRenderMethod.invoke(Minecraft.getMinecraft().renderGlobal);
      } catch (Exception var2) {
         ;
      }

   }

   private void postRenderDamagedBlocks() {
      try {
         postRenderMethod.invoke(Minecraft.getMinecraft().renderGlobal);
      } catch (Exception var2) {
         ;
      }

   }

   private void drawBlockDamageTexture(Minecraft mc, TextureManager renderEngine, Tessellator tessellatorIn, WorldRenderer bufferBuilderIn, Entity entityIn, float partialTicks) {
      double posX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
      double posY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
      double posZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
      if (!damagedBranches.isEmpty()) {
         renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         this.preRenderDamagedBlocks();
         bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
         bufferBuilderIn.setTranslation(-posX, -posY, -posZ);
         bufferBuilderIn.noColor();
         Iterator<Entry<Integer, DestroyBlockProgress>> iter = damagedBranches.entrySet().iterator();

         while(iter.hasNext()) {
            Entry<Integer, DestroyBlockProgress> entry = (Entry)iter.next();
            DestroyBlockProgress destroyblockprogress = (DestroyBlockProgress)entry.getValue();
            BlockPos pos = destroyblockprogress.getPosition();
            double delX = (double)pos.getX() - posX;
            double delY = (double)pos.getY() - posY;
            double delZ = (double)pos.getZ() - posZ;
            if (delX * delX + delY * delY + delZ * delZ > 4096.0D) {
               iter.remove();
            } else {
               IBlockState state = mc.world.getBlockState(pos);
               if (state.getBlock() instanceof BlockBranch) {
                  int k1 = destroyblockprogress.getPartialBlockDamage();
                  EaglerTextureAtlasSprite textureatlassprite = this.destroyBlockIcons[k1];
                  BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
                  if (state.getRenderType() == EnumBlockRenderType.MODEL) {
                     state = state.getActualState(mc.world, pos);
                     IBakedModel baseModel = blockrendererdispatcher.getBlockModelShapes().getModelForState(state);
                     IBakedModel damageModel = this.getDamageModel(baseModel, textureatlassprite, state, mc.world, pos);
                     blockrendererdispatcher.getBlockModelRenderer().renderModel(mc.world, damageModel, state, pos, bufferBuilderIn, true);
                  }
               } else {
                  iter.remove();
               }
            }
         }

         tessellatorIn.draw();
         bufferBuilderIn.setTranslation(0.0D, 0.0D, 0.0D);
         this.postRenderDamagedBlocks();
      }

   }

   private IBakedModel getDamageModel(IBakedModel baseModel, EaglerTextureAtlasSprite texture, IBlockState state, IBlockAccess world, BlockPos pos) {
      state = state.getBlock().getExtendedState(state, world, pos);
      if (!(baseModel instanceof ICustomDamageModel)) {
         return (new Builder(state, baseModel, texture, pos)).makeBakedModel();
      } else {
         ICustomDamageModel customDamageModel = (ICustomDamageModel)baseModel;
         long rand = MathHelper.getPositionRandom(pos);
         List<BakedQuad> generalQuads = Lists.newArrayList();
         Map<EnumFacing, List<BakedQuad>> faceQuads = Maps.newEnumMap(EnumFacing.class);

         for(EnumFacing facing : EnumFacing.values()) {
            List<BakedQuad> quadList = Lists.newArrayList();

            for(BakedQuad quad : customDamageModel.getCustomDamageQuads(state, facing, rand)) {
               quadList.add(new BakedQuadRetextured(quad, texture));
            }

            faceQuads.put(facing, quadList);
         }

         for(BakedQuad quad : customDamageModel.getCustomDamageQuads(state, (EnumFacing)null, rand)) {
            generalQuads.add(new BakedQuadRetextured(quad, texture));
         }

         return new SimpleBakedModel(generalQuads, faceQuads, baseModel.isAmbientOcclusion(state), baseModel.isGui3d(), baseModel.getParticleTexture(), baseModel.getItemCameraTransforms(), baseModel.getOverrides());
      }
   }

   public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
      if (resourcePredicate.test(VanillaResourceType.TEXTURES)) {
         TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();

         for(int i = 0; i < this.destroyBlockIcons.length; ++i) {
            this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
         }
      }

   }

   static {
      preRenderMethod.setAccessible(true);
      postRenderMethod.setAccessible(true);
   }

   private static class RenderGlobalWrapper implements IWorldEventListener {
      private final World world;

      public RenderGlobalWrapper(World world) {
         this.world = world;
      }

      public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
         Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(worldIn, pos, oldState, newState, flags);
      }

      public void notifyLightSet(BlockPos pos) {
         Minecraft.getMinecraft().renderGlobal.notifyLightSet(pos);
      }

      public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
         Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
      }

      public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
         Minecraft.getMinecraft().renderGlobal.playSoundToAllNearExcept(player, soundIn, category, x, y, z, volume, pitch);
      }

      public void playRecord(SoundEvent soundIn, BlockPos pos) {
         Minecraft.getMinecraft().renderGlobal.playRecord(soundIn, pos);
      }

      public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
         Minecraft.getMinecraft().renderGlobal.spawnParticle(particleID, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
      }

      public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
         Minecraft.getMinecraft().renderGlobal.spawnParticle(id, ignoreRange, p_190570_3_, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
      }

      public void onEntityAdded(Entity entityIn) {
         Minecraft.getMinecraft().renderGlobal.onEntityAdded(entityIn);
      }

      public void onEntityRemoved(Entity entityIn) {
         Minecraft.getMinecraft().renderGlobal.onEntityRemoved(entityIn);
      }

      public void broadcastSound(int soundID, BlockPos pos, int data) {
         Minecraft.getMinecraft().renderGlobal.broadcastSound(soundID, pos, data);
      }

      public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
         Minecraft.getMinecraft().renderGlobal.playEvent(player, type, blockPosIn, data);
      }

      public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
         IBlockState state = this.world.getBlockState(pos);
         if (state.getBlock() instanceof BlockBranchThick) {
            BlockBreakAnimationClientHandler.instance.sendThickBranchBreakProgress(breakerId, pos, progress);
         } else {
            Minecraft.getMinecraft().renderGlobal.sendBlockBreakProgress(breakerId, pos, progress);
         }

      }
   }
}
