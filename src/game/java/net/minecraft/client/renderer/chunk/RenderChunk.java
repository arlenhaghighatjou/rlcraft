package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.DeferredStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderChunk
{
    public static final long NO_ANIMATION = -2L;
    public static final long ANIMATION_PENDING = -1L;
    public long animationStart = NO_ANIMATION;
    public EnumFacing animationFacing;
    public static enum ShadowFrustumState
    {
        OUTSIDE,
        OUTSIDE_BB,
        INTERSECT,
        INSIDE;
    }

    private World world;
    private final RenderGlobal renderGlobal;
    public static int renderChunksUpdated;
    public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
    private ChunkCompileTaskGenerator compileTask;
    private final Set<TileEntity> setTileEntities = Sets.<TileEntity>newHashSet();
    private final int index;
    public AxisAlignedBB boundingBox;
    private int frameIndex = -1;
    private boolean needsUpdate = true;
    private final BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(-1, -1, -1);
    private final BlockPos.MutableBlockPos[] mapEnumFacing = new BlockPos.MutableBlockPos[6];
    private boolean needsImmediateUpdate;
    private ChunkCache worldView;
    public int shadowLOD0FrameIndex = -1;
    public int shadowLOD1FrameIndex = -1;
    public int shadowLOD2FrameIndex = -1;
    public RenderChunk.ShadowFrustumState shadowLOD0InFrustum = RenderChunk.ShadowFrustumState.OUTSIDE;
    public RenderChunk.ShadowFrustumState shadowLOD1InFrustum = RenderChunk.ShadowFrustumState.OUTSIDE;
    public RenderChunk.ShadowFrustumState shadowLOD2InFrustum = RenderChunk.ShadowFrustumState.OUTSIDE;

    public RenderChunk(World worldIn, RenderGlobal renderGlobalIn, int indexIn)
    {
        for (int i = 0; i < this.mapEnumFacing.length; ++i)
        {
            this.mapEnumFacing[i] = new BlockPos.MutableBlockPos();
        }

        this.world = worldIn;
        this.renderGlobal = renderGlobalIn;
        this.index = indexIn;
    }

    public boolean setFrameIndex(int frameIndexIn)
    {
        if (this.frameIndex == frameIndexIn)
        {
            return false;
        }
        else
        {
            this.frameIndex = frameIndexIn;
            return true;
        }
    }

    public void setPosition(int x, int y, int z)
    {
        if (x != this.position.getX() || y != this.position.getY() || z != this.position.getZ())
        {
            this.stopCompileTask();
            this.position.setPos(x, y, z);
            if (lumien.chunkanimator.ChunkAnimator.INSTANCE != null)
            {
                lumien.chunkanimator.ChunkAnimator.INSTANCE.animationHandler.setOrigin(this);
            }
            this.boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 16), (double)(y + 16), (double)(z + 16));
            EnumFacing[] facings = EnumFacing._VALUES;

            for (int i = 0; i < facings.length; ++i)
            {
                EnumFacing enumfacing = facings[i];
                this.mapEnumFacing[enumfacing.ordinal()].setPos(this.position).move(enumfacing, 16);
            }
        }
    }

    public void resortTransparency(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        CompiledChunk compiledchunk = generator.getCompiledChunk();

        if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(BlockRenderLayer.TRANSLUCENT))
        {
            WorldRenderer worldrenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT);
            this.preRenderBlocks(worldrenderer, this.position);
            worldrenderer.setVertexState(compiledchunk.getState());
            this.postRenderBlocks(BlockRenderLayer.TRANSLUCENT, x, y, z, worldrenderer, compiledchunk);
        }

        if (DeferredStateManager.isRenderingRealisticWater() && compiledchunk.getStateRealisticWater() != null && !compiledchunk.isLayerEmpty(BlockRenderLayer.REALISTIC_WATER))
        {
            WorldRenderer worldrenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.REALISTIC_WATER);
            this.preRenderBlocks(worldrenderer, this.position);
            worldrenderer.setVertexState(compiledchunk.getStateRealisticWater());
            this.postRenderBlocks(BlockRenderLayer.REALISTIC_WATER, x, y, z, worldrenderer, compiledchunk);
        }
    }

    public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        if (this.compiledChunk == CompiledChunk.DUMMY)
        {
            this.compiledChunk = new CompiledChunk(this);
        }
        else
        {
            this.compiledChunk.reset();
        }

        CompiledChunk compiledchunk = this.compiledChunk;
        BlockPos blockpos = this.position;
        BlockPos blockpos1 = blockpos.add(15, 15, 15);

        if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
        {
            return;
        }

        generator.setCompiledChunk(compiledchunk);
        VisGraph lvt_9_1_ = new VisGraph();
        HashSet lvt_10_1_ = Sets.newHashSet();

        if (!this.worldView.isEmpty())
        {
            ++renderChunksUpdated;
            BlockRenderLayer[] layers = BlockRenderLayer._VALUES;
            boolean[] aboolean = new boolean[layers.length];
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos1))
            {
                IBlockState iblockstate = this.worldView.getBlockState(blockpos$mutableblockpos);
                Block block = iblockstate.getBlock();

                if (iblockstate.isOpaqueCube())
                {
                    lvt_9_1_.setOpaqueCube(blockpos$mutableblockpos);
                }

                if (block.hasTileEntity(iblockstate))
                {
                    TileEntity tileentity = this.worldView.getTileEntity(blockpos$mutableblockpos, Chunk.EnumCreateEntityType.CHECK);

                    if (tileentity != null)
                    {
                        TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance.<TileEntity>getRenderer(tileentity);

                        if (tileentityspecialrenderer != null)
                        {

                            if (tileentityspecialrenderer.isGlobalRenderer(tileentity))
                            {
                                lvt_10_1_.add(tileentity);
                            }
                            else compiledchunk.addTileEntity(tileentity); // FORGE: Fix MC-112730
                        }
                    }
                }

                if (block.getDefaultState().getRenderType() == EnumBlockRenderType.INVISIBLE)
                {
                    continue;
                }

                for (int k = 0; k < layers.length; ++k)
                {
                    BlockRenderLayer blockrenderlayer1 = layers[k];

                    if (!block.canRenderInLayer(iblockstate, blockrenderlayer1))
                    {
                        continue;
                    }

                    net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockrenderlayer1);
                    WorldRenderer bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(k);

                    if (!compiledchunk.isLayerStarted(blockrenderlayer1))
                    {
                        compiledchunk.setLayerStarted(blockrenderlayer1);
                        this.preRenderBlocks(bufferbuilder, blockpos);
                    }

                    aboolean[k] |= blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, this.worldView, bufferbuilder);
                }

                if (block.eaglerShadersShouldRenderGlassHighlights())
                {
                    BlockRenderLayer blockrenderlayer2 = BlockRenderLayer.GLASS_HIGHLIGHTS;
                    net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockrenderlayer2);
                    WorldRenderer bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(blockrenderlayer2.ordinal());

                    if (!compiledchunk.isLayerStarted(blockrenderlayer2))
                    {
                        compiledchunk.setLayerStarted(blockrenderlayer2);
                        this.preRenderBlocks(bufferbuilder, blockpos);
                    }

                    aboolean[blockrenderlayer2.ordinal()] |= blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, this.worldView, bufferbuilder);
                }

                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
            }

            for (int k = 0; k < layers.length; ++k)
            {
                BlockRenderLayer blockrenderlayer = layers[k];

                if (aboolean[k])
                {
                    compiledchunk.setLayerUsed(blockrenderlayer);
                }

                if (compiledchunk.isLayerStarted(blockrenderlayer))
                {
                    this.postRenderBlocks(blockrenderlayer, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer), compiledchunk);
                }
            }
        }

        compiledchunk.setVisibility(lvt_9_1_.computeVisibility());
        Set<TileEntity> set = Sets.newHashSet(lvt_10_1_);
        Set<TileEntity> set1 = Sets.newHashSet(this.setTileEntities);
        set.removeAll(this.setTileEntities);
        set1.removeAll(lvt_10_1_);
        this.setTileEntities.clear();
        this.setTileEntities.addAll(lvt_10_1_);
        this.renderGlobal.updateTileEntities(set1, set);
    }

    protected void finishCompileTask()
    {
        if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE)
        {
            this.compileTask.finish();
            this.compileTask = null;
        }
    }

    public ChunkCompileTaskGenerator makeCompileTaskChunk()
    {
        this.finishCompileTask();
        this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK, this.getDistanceSq());
        this.rebuildWorldView();
        return this.compileTask;
    }

    private void rebuildWorldView()
    {
        ChunkCache cache = createRegionRenderCache(this.world, this.position.add(-1, -1, -1), this.position.add(16, 16, 16), 1);
        net.minecraftforge.client.MinecraftForgeClient.onRebuildChunk(this.world, this.position, cache);
        this.worldView = cache;
    }

    @Nullable
    public ChunkCompileTaskGenerator makeCompileTaskTransparency()
    {
        this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY, this.getDistanceSq());
        this.compileTask.setCompiledChunk(this.compiledChunk);
        return this.compileTask;
    }

    protected double getDistanceSq()
    {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
        double d0 = this.boundingBox.minX + 8.0D - entityplayersp.posX;
        double d1 = this.boundingBox.minY + 8.0D - entityplayersp.posY;
        double d2 = this.boundingBox.minZ + 8.0D - entityplayersp.posZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    private void preRenderBlocks(WorldRenderer bufferBuilderIn, BlockPos pos)
    {
        bufferBuilderIn.begin(7, DeferredStateManager.isDeferredRenderer() ? VertexFormat.BLOCK_SHADERS : DefaultVertexFormats.BLOCK);
        bufferBuilderIn.setTranslation((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
    }

    private void postRenderBlocks(BlockRenderLayer layer, float x, float y, float z, WorldRenderer bufferBuilderIn, CompiledChunk compiledChunkIn)
    {
        if ((layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.REALISTIC_WATER) && !compiledChunkIn.isLayerEmpty(layer))
        {
            bufferBuilderIn.sortVertexData(x, y, z);

            if (layer == BlockRenderLayer.REALISTIC_WATER)
            {
                compiledChunkIn.setStateRealisticWater(bufferBuilderIn.getVertexState());
            }
            else
            {
                compiledChunkIn.setState(bufferBuilderIn.getVertexState());
            }
        }

        bufferBuilderIn.finishDrawing();
    }

    public CompiledChunk getCompiledChunk()
    {
        return this.compiledChunk;
    }

    public void setCompiledChunk(CompiledChunk compiledChunkIn)
    {
        this.compiledChunk = compiledChunkIn;
    }

    public void stopCompileTask()
    {
        this.finishCompileTask();

        if (this.compiledChunk != CompiledChunk.DUMMY)
        {
            this.compiledChunk.setState((WorldRenderer.State)null);
            this.compiledChunk.setStateRealisticWater((WorldRenderer.State)null);
            this.compiledChunk = CompiledChunk.DUMMY;
        }
    }

    public void deleteGlResources()
    {
        this.stopCompileTask();
        this.world = null;
    }

    public BlockPos getPosition()
    {
        return this.position;
    }

    public void setNeedsUpdate(boolean immediate)
    {
        if (this.needsUpdate)
        {
            immediate |= this.needsImmediateUpdate;
        }

        this.needsUpdate = true;
        this.needsImmediateUpdate = immediate;
    }

    public void clearNeedsUpdate()
    {
        this.needsUpdate = false;
        this.needsImmediateUpdate = false;
    }

    public boolean needsUpdate()
    {
        return this.needsUpdate;
    }

    public boolean needsImmediateUpdate()
    {
        return this.needsUpdate && this.needsImmediateUpdate;
    }

    /* ======================================== FORGE START =====================================*/
    /**
     * Creates a new RegionRenderCache instance.<br>
     * Extending classes can change the behavior of the cache, allowing to visually change
     * blocks (schematics etc).
     *
     * @see RegionRenderCache
     * @param world The world to cache.
     * @param from The starting position of the chunk minus one on each axis.
     * @param to The ending position of the chunk plus one on each axis.
     * @param subtract Padding used internally by the RegionRenderCache constructor to make
     *                 the cache a 20x20x20 cube, for a total of 8000 states in the cache.
     * @return new RegionRenderCache instance
     */
    protected ChunkCache createRegionRenderCache(World world, BlockPos from, BlockPos to, int subtract)
    {
        return new ChunkCache(world, from, to, subtract);
    }
    /* ========================================= FORGE END ======================================*/

    public BlockPos getBlockPosOffset16(EnumFacing facing)
    {
        return this.mapEnumFacing[facing.ordinal()];
    }

    public World getWorld()
    {
        return this.world;
    }
}
