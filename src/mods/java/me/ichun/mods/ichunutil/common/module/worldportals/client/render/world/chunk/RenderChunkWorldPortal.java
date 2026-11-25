package me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.chunk;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator.Status;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator.Type;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.FMLClientHandler;

public class RenderChunkWorldPortal extends RenderChunk implements IRenderChunkWorldPortal {
   public BlockPos pos = BlockPos.ORIGIN;
   public EnumFacing face = EnumFacing.UP;
   public boolean noCull;
   public boolean wasCulled;
   private ChunkCompileTaskGenerator optiCompileTask;
   public ChunkCache optiWorldView;

   public RenderChunkWorldPortal(World worldIn, RenderGlobal renderGlobalIn, int indexIn) {
      super(worldIn, renderGlobalIn, indexIn);
   }

   public void setCurrentPositionsAndFaces(BlockPos pos, EnumFacing face) {
      this.pos = pos;
      this.face = face;
   }

   public void setNoCull(boolean flag) {
      if (flag && this.noCull != flag && this.wasCulled == flag) {
         this.setNeedsUpdate(false);
      }

      this.noCull = flag;
   }

   public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator) {
      this.wasCulled = false;
      CompiledChunk compiledchunk = new CompiledChunk();
      int i = 1;
      BlockPos blockpos = this.position;
      BlockPos blockpos1 = blockpos.add(15, 15, 15);
      generator.getLock().lock();

      try {
         if (generator.getStatus() != Status.COMPILING) {
            return;
         }

         generator.setCompiledChunk(compiledchunk);
      } finally {
         generator.getLock().unlock();
      }

      VisGraph lvt_9_1_ = new VisGraph();
      HashSet lvt_10_1_ = Sets.newHashSet();
      ChunkCache worldView = this.getWorldView();
      if (!worldView.isEmpty()) {
         ++renderChunksUpdated;
         boolean[] aboolean = new boolean[BlockRenderLayer.values().length];
         BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

         for(MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos1)) {
            boolean noRender = !this.noCull && (this.face.getFrontOffsetX() < 0 && blockpos$mutableblockpos.getX() > this.pos.getX() || this.face.getFrontOffsetX() > 0 && blockpos$mutableblockpos.getX() < this.pos.getX() || this.face.getFrontOffsetY() < 0 && blockpos$mutableblockpos.getY() > this.pos.getY() || this.face.getFrontOffsetY() > 0 && blockpos$mutableblockpos.getY() < this.pos.getY() || this.face.getFrontOffsetZ() < 0 && blockpos$mutableblockpos.getZ() > this.pos.getZ() || this.face.getFrontOffsetZ() > 0 && blockpos$mutableblockpos.getZ() < this.pos.getZ());
            if (noRender) {
               this.wasCulled = true;
            }

            IBlockState iblockstate = worldView.getBlockState(blockpos$mutableblockpos);
            Block block = iblockstate.getBlock();
            if (iblockstate.isOpaqueCube()) {
               lvt_9_1_.setOpaqueCube(blockpos$mutableblockpos);
            }

            if (!noRender && block.hasTileEntity(iblockstate)) {
               TileEntity tileentity = worldView.getTileEntity(blockpos$mutableblockpos, EnumCreateEntityType.CHECK);
               if (tileentity != null) {
                  TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileentity);
                  if (tileentityspecialrenderer != null) {
                     compiledchunk.addTileEntity(tileentity);
                     if (tileentityspecialrenderer.isGlobalRenderer(tileentity)) {
                        lvt_10_1_.add(tileentity);
                     }
                  }
               }
            }

            for(BlockRenderLayer blockrenderlayer1 : BlockRenderLayer.values()) {
               if (block.canRenderInLayer(iblockstate, blockrenderlayer1)) {
                  ForgeHooksClient.setRenderLayer(blockrenderlayer1);
                  int j = blockrenderlayer1.ordinal();
                  if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE) {
                     BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);
                     if (!compiledchunk.isLayerStarted(blockrenderlayer1)) {
                        compiledchunk.setLayerStarted(blockrenderlayer1);
                        this.preRenderBlocks(bufferbuilder, blockpos);
                     }

                     if (!noRender && blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, worldView, bufferbuilder)) {
                        aboolean[j] |= true;
                     }
                  }
               }
            }

            ForgeHooksClient.setRenderLayer((BlockRenderLayer)null);
         }

         for(BlockRenderLayer blockrenderlayer : BlockRenderLayer.values()) {
            if (aboolean[blockrenderlayer.ordinal()]) {
               compiledchunk.setLayerUsed(blockrenderlayer);
            }

            if (compiledchunk.isLayerStarted(blockrenderlayer)) {
               this.postRenderBlocks(blockrenderlayer, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer), compiledchunk);
            }
         }
      }

      compiledchunk.setVisibility(lvt_9_1_.computeVisibility());
      this.lockCompileTask.lock();

      try {
         Set<TileEntity> set = Sets.newHashSet(lvt_10_1_);
         Set<TileEntity> set1 = Sets.newHashSet(this.setTileEntities);
         set.removeAll(this.setTileEntities);
         set1.removeAll(lvt_10_1_);
         this.setTileEntities.clear();
         this.setTileEntities.addAll(lvt_10_1_);
         this.renderGlobal.updateTileEntities(set1, set);
      } finally {
         this.lockCompileTask.unlock();
      }

   }

   public ChunkCache getWorldView() {
      return FMLClientHandler.instance().hasOptifine() ? this.optiWorldView : this.worldView;
   }

   protected void finishCompileTask() {
      if (!FMLClientHandler.instance().hasOptifine()) {
         super.finishCompileTask();
      } else {
         this.lockCompileTask.lock();

         try {
            if (this.optiCompileTask != null && this.optiCompileTask.getStatus() != Status.DONE) {
               this.optiCompileTask.finish();
               this.optiCompileTask = null;
            }
         } finally {
            this.lockCompileTask.unlock();
         }

      }
   }

   public ChunkCompileTaskGenerator makeCompileTaskChunk() {
      if (!FMLClientHandler.instance().hasOptifine()) {
         return super.makeCompileTaskChunk();
      } else {
         this.lockCompileTask.lock();

         ChunkCompileTaskGenerator chunkcompiletaskgenerator;
         try {
            this.finishCompileTask();
            this.optiCompileTask = new ChunkCompileTaskGenerator(this, Type.REBUILD_CHUNK, this.getDistanceSq());
            this.rebuildWorldView();
            chunkcompiletaskgenerator = this.optiCompileTask;
         } finally {
            this.lockCompileTask.unlock();
         }

         return chunkcompiletaskgenerator;
      }
   }

   private void rebuildWorldView() {
      int i = 1;
      ChunkCache cache = this.createRegionRenderCache(this.world, this.position.add(-1, -1, -1), this.position.add(16, 16, 16), 1);
      MinecraftForgeClient.onRebuildChunk(this.world, this.position, cache);
      this.optiWorldView = cache;
   }

   @Nullable
   public ChunkCompileTaskGenerator makeCompileTaskTransparency() {
      if (!FMLClientHandler.instance().hasOptifine()) {
         return super.makeCompileTaskTransparency();
      } else {
         this.lockCompileTask.lock();

         ChunkCompileTaskGenerator var2;
         try {
            if (this.optiCompileTask != null && this.optiCompileTask.getStatus() == Status.PENDING) {
               ChunkCompileTaskGenerator chunkcompiletaskgenerator = null;
               return chunkcompiletaskgenerator;
            }

            if (this.optiCompileTask != null && this.optiCompileTask.getStatus() != Status.DONE) {
               this.optiCompileTask.finish();
               this.optiCompileTask = null;
            }

            this.optiCompileTask = new ChunkCompileTaskGenerator(this, Type.RESORT_TRANSPARENCY, this.getDistanceSq());
            this.optiCompileTask.setCompiledChunk(this.compiledChunk);
            ChunkCompileTaskGenerator chunkcompiletaskgenerator = this.optiCompileTask;
            var2 = chunkcompiletaskgenerator;
         } finally {
            this.lockCompileTask.unlock();
         }

         return var2;
      }
   }
}
