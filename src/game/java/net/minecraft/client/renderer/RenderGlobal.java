package net.minecraft.client.renderer;

import bl4ckscor3.mod.particleculling.ParticleCulling;
import bl4ckscor3.mod.particleculling.CameraHolder;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.EffectPipelineEntityOutline;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.minecraft.ChunkUpdateManager;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerCloudRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.DeferredStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.EaglerDeferredConfig;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.EaglerDeferredPipeline;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.program.SharedPipelineShaders;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.DynamicLightManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.dynamiclights.DynamicLightsStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.optifine.CustomSky;
import dev.redstudio.alfheim.utils.DeduplicatedLongQueue;

@SideOnly(Side.CLIENT)
public class RenderGlobal implements IWorldEventListener, IResourceManagerReloadListener, CameraHolder
{
    private ICamera camera;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation CLOUDS_TEXTURES = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation FORCEFIELD_TEXTURES = new ResourceLocation("textures/misc/forcefield.png");
    private final Minecraft mc;
    private final TextureManager renderEngine;
    private final RenderManager renderManager;
    private WorldClient world;
    private Set<RenderChunk> chunksToUpdate = Sets.<RenderChunk>newLinkedHashSet();
    private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos = Lists.<RenderGlobal.ContainerLocalRenderInformation>newArrayListWithCapacity(69696);
    private final Set<TileEntity> setTileEntities = Sets.<TileEntity>newHashSet();
    public ViewFrustum viewFrustum;
    private int glSunList = -1;
    private int moonPhase = -1;
    private int glMoonList = -1;
    private int glHorizonList = -1;
    private int starGLCallList = -1;
    private int glSkyList = -1;
    private int glSkyList2 = -1;
    private int cloudTickCounter;
    public final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.<Integer, DestroyBlockProgress>newHashMap();
    private final Map<BlockPos, ISound> mapSoundPositions = Maps.<BlockPos, ISound>newHashMap();
    private final EaglerTextureAtlasSprite[] destroyBlockIcons = new EaglerTextureAtlasSprite[10];
    private double frustumUpdatePosX = Double.MIN_VALUE;
    private double frustumUpdatePosY = Double.MIN_VALUE;
    private double frustumUpdatePosZ = Double.MIN_VALUE;
    private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
    private double lastViewEntityX = Double.MIN_VALUE;
    private double lastViewEntityY = Double.MIN_VALUE;
    private double lastViewEntityZ = Double.MIN_VALUE;
    private double lastViewEntityPitch = Double.MIN_VALUE;
    private double lastViewEntityYaw = Double.MIN_VALUE;
    private float lastViewProjMatrixFOV = Float.MIN_VALUE;
    private final ChunkUpdateManager renderDispatcher = new ChunkUpdateManager();
    private ChunkRenderContainer renderContainer;
    private int renderDistanceChunks = -1;
    private int renderEntitiesStartupCounter = 2;
    private int countEntitiesTotal;
    private int countEntitiesRendered;
    private int countEntitiesHidden;
    private boolean debugFixTerrainFrustum;
    private ClippingHelper debugFixedClippingHelper;
    private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
    private final Vector3d debugTerrainFrustumPosition = new Vector3d();
    private boolean vboEnabled;
    IRenderChunkFactory renderChunkFactory;
    private double prevRenderSortX;
    private double prevRenderSortY;
    private double prevRenderSortZ;
    private boolean displayListEntitiesDirty = true;
    private final DeduplicatedLongQueue alfheim$lightUpdatesQueue = new DeduplicatedLongQueue(8192);
    public final EaglerCloudRenderer cloudRenderer;

    public RenderGlobal(Minecraft mcIn)
    {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.renderEngine = mcIn.getTextureManager();
        this.renderEngine.bindTexture(FORCEFIELD_TEXTURES);
        EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        GlStateManager.bindTexture(0);
        this.updateDestroyBlockIcons();
        this.vboEnabled = false;
        this.renderContainer = new RenderList();
        this.renderChunkFactory = new ListChunkFactory();
        this.cloudRenderer = new EaglerCloudRenderer(mcIn);
        this.generateSun();
        this.generateHorizon();
        this.generateStars();
        this.generateSky();
        this.generateSky2();
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.updateDestroyBlockIcons();
    }

    private void updateDestroyBlockIcons()
    {
        TextureMap texturemap = this.mc.getTextureMapBlocks();

        for (int i = 0; i < this.destroyBlockIcons.length; ++i)
        {
            this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
        }
    }

	private final List<Entity> outlineEntities = new java.util.ArrayList<>();
	private boolean entityOutlineReady;

    public void makeEntityOutlineShader()
    {
		EffectPipelineEntityOutline.initialize();
    }

    public void renderEntityOutlineFramebuffer()
    {
		if (this.isRenderEntityOutlines() && this.entityOutlineReady) {
			EffectPipelineEntityOutline.composite();
			this.entityOutlineReady = false;
		}
    }

    protected boolean isRenderEntityOutlines()
    {
		return this.mc.player != null && this.world != null;
    }

	public void resetEntityOutlines() {
		this.outlineEntities.clear();
		this.entityOutlineReady = false;
	}

	public void prepareEntityOutlines(float partialTicks) {
		if (!this.outlineEntities.isEmpty()) {
			try {
				EffectPipelineEntityOutline.capture(this.mc.displayWidth, this.mc.displayHeight, () -> {
					RenderHelper.disableStandardItemLighting();
					this.renderManager.setRenderOutlines(true);
					try {
						for (Entity glowing : this.outlineEntities) {
							this.renderManager.renderEntityStatic(glowing, partialTicks, false);
						}
					} finally {
						this.renderManager.setRenderOutlines(false);
						GlStateManager.disableOutlineMode();
					}
				});
				this.entityOutlineReady = true;
			} finally {
				this.outlineEntities.clear();
			}
		}
	}

	private boolean shouldOutline(Entity entity, Entity viewer, ICamera camera) {
		return entity.isGlowing() || this.mc.player.isSpectator()
				&& this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown() && entity instanceof EntityPlayer
				&& (entity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox())
						|| entity.isRidingOrBeingRiddenBy(this.mc.player));
	}

    private void generateSun()
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();

        if (this.glSunList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSunList);
            this.glSunList = -1;
        }

        this.glSunList = GLAllocation.generateDisplayLists();
        EaglercraftGPU.glNewList(this.glSunList, GL_COMPILE);
        float f17 = 30.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(-f17), 100.0D, (double)(-f17)).tex(0.0D, 0.0D).endVertex();
        bufferbuilder.pos((double)f17, 100.0D, (double)(-f17)).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos((double)f17, 100.0D, (double)f17).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos((double)(-f17), 100.0D, (double)f17).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
        EaglercraftGPU.glEndList();
    }

    private int getMoonList(int phase)
    {
        if (phase != this.moonPhase)
        {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer bufferbuilder = tessellator.getBuffer();

            if (this.glMoonList == -1)
            {
                this.glMoonList = GLAllocation.generateDisplayLists();
            }

            EaglercraftGPU.glNewList(this.glMoonList, GL_COMPILE);
            float f17 = 20.0F;
            int j = phase % 4;
            int l = phase / 4 % 2;
            float f22 = (float)(j + 0) / 4.0F;
            float f23 = (float)(l + 0) / 2.0F;
            float f24 = (float)(j + 1) / 4.0F;
            float f14 = (float)(l + 1) / 2.0F;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos((double)(-f17), -100.0D, (double)f17).tex((double)f24, (double)f14).endVertex();
            bufferbuilder.pos((double)f17, -100.0D, (double)f17).tex((double)f22, (double)f14).endVertex();
            bufferbuilder.pos((double)f17, -100.0D, (double)(-f17)).tex((double)f22, (double)f23).endVertex();
            bufferbuilder.pos((double)(-f17), -100.0D, (double)(-f17)).tex((double)f24, (double)f23).endVertex();
            tessellator.draw();
            EaglercraftGPU.glEndList();
            this.moonPhase = phase;
        }

        return this.glMoonList;
    }

    private void generateHorizon()
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();

        if (this.glHorizonList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glHorizonList);
            this.glHorizonList = -1;
        }

        this.glHorizonList = GLAllocation.generateDisplayLists();
        EaglercraftGPU.glNewList(this.glHorizonList, GL_COMPILE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(-1.0D, 0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, 0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, 0.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(-1.0D, 0.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, 0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, 0.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(-1.0D, 0.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(-1.0D, 0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        tessellator.draw();
        EaglercraftGPU.glEndList();
    }

    private void generateSky2()
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();

        if (this.glSkyList2 >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }

        this.glSkyList2 = GLAllocation.generateDisplayLists();
        EaglercraftGPU.glNewList(this.glSkyList2, GL_COMPILE);
        this.renderSky(bufferbuilder, -16.0F, true);
        tessellator.draw();
        EaglercraftGPU.glEndList();
    }

    private void generateSky()
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();

        if (this.glSkyList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }

        this.glSkyList = GLAllocation.generateDisplayLists();
        EaglercraftGPU.glNewList(this.glSkyList, GL_COMPILE);
        this.renderSky(bufferbuilder, 16.0F, false);
        tessellator.draw();
        EaglercraftGPU.glEndList();
    }

    private void renderSky(WorldRenderer bufferBuilderIn, float posY, boolean reverseX)
    {
        int i = 64;
        int j = 6;
        bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

        for (int k = -384; k <= 384; k += 64)
        {
            for (int l = -384; l <= 384; l += 64)
            {
                float f = (float)k;
                float f1 = (float)(k + 64);

                if (reverseX)
                {
                    f1 = (float)k;
                    f = (float)(k + 64);
                }

                bufferBuilderIn.pos((double)f, (double)posY, (double)l).endVertex();
                bufferBuilderIn.pos((double)f1, (double)posY, (double)l).endVertex();
                bufferBuilderIn.pos((double)f1, (double)posY, (double)(l + 64)).endVertex();
                bufferBuilderIn.pos((double)f, (double)posY, (double)(l + 64)).endVertex();
            }
        }
    }

    private void generateStars()
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();

        if (this.starGLCallList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.starGLCallList);
            this.starGLCallList = -1;
        }

        this.starGLCallList = GLAllocation.generateDisplayLists();
        GlStateManager.pushMatrix();
        EaglercraftGPU.glNewList(this.starGLCallList, GL_COMPILE);
        this.renderStars(bufferbuilder);
        tessellator.draw();
        EaglercraftGPU.glEndList();
        GlStateManager.popMatrix();
    }

    private void renderStars(WorldRenderer bufferBuilderIn)
    {
        EaglercraftRandom random = new EaglercraftRandom(10842L);
        bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

        for (int i = 0; i < 1500; ++i)
        {
            double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D)
            {
                d4 = 1.0D / Math.sqrt(d4);
                d0 = d0 * d4;
                d1 = d1 * d4;
                d2 = d2 * d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j)
                {
                    double d17 = 0.0D;
                    double d18 = (double)((j & 2) - 1) * d3;
                    double d19 = (double)((j + 1 & 2) - 1) * d3;
                    double d20 = 0.0D;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    bufferBuilderIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
                }
            }
        }
    }

    public void setWorldAndLoadRenderers(@Nullable WorldClient worldClientIn)
    {
		this.resetEntityOutlines();
        if (this.world != null)
        {
            this.world.removeEventListener(this);
        }

        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.renderManager.setWorld(worldClientIn);
        this.world = worldClientIn;

        if (worldClientIn != null)
        {
            worldClientIn.addEventListener(this);
            this.loadRenderers();
        }
        else
        {
            this.chunksToUpdate.clear();
            this.renderInfos.clear();

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
                this.viewFrustum = null;
            }

            this.renderDispatcher.stopChunkUpdates();
        }
    }

    public void loadRenderers()
    {
        if (this.world != null)
        {
            this.displayListEntitiesDirty = true;

            if (this.mc.gameSettings.shaders && !EaglerDeferredPipeline.isSupported())
            {
                this.mc.gameSettings.shaders = false;
            }

            Blocks.LEAVES.setGraphicsLevel(this.mc.gameSettings.shaders || this.mc.gameSettings.fancyGraphics);
            Blocks.LEAVES2.setGraphicsLevel(this.mc.gameSettings.shaders || this.mc.gameSettings.fancyGraphics);
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
            }

            this.stopChunkUpdates();

            synchronized (this.setTileEntities)
            {
                this.setTileEntities.clear();
            }

            this.viewFrustum = new ViewFrustum(this.world, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);

            if (this.world != null)
            {
                Entity entity = this.mc.getRenderViewEntity();

                if (entity != null)
                {
                    this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
                }
            }

            this.renderEntitiesStartupCounter = 2;

            if (this.mc.gameSettings.shaders)
            {
                EaglerDeferredConfig dfc = this.mc.gameSettings.deferredShaderConf;
                dfc.updateConfig();

                if (!this.world.provider.hasSkyLight())
                {
                    dfc.is_rendering_shadowsSun_clamped = 0;
                    dfc.is_rendering_lightShafts = false;
                    dfc.is_rendering_subsurfaceScattering = false;
                }
                else
                {
                    int maxDist = this.renderDistanceChunks << 4;
                    int ss = dfc.is_rendering_shadowsSun;

                    while (ss > 1 && (1 << (ss + 3)) > maxDist)
                    {
                        --ss;
                    }

                    dfc.is_rendering_shadowsSun_clamped = ss;
                    dfc.is_rendering_lightShafts = ss > 0 && dfc.lightShafts && dfc.shaderPackInfo.LIGHT_SHAFTS;
                    dfc.is_rendering_subsurfaceScattering = ss > 0 && dfc.subsurfaceScattering && dfc.shaderPackInfo.SUBSURFACE_SCATTERING;
                }

                boolean flag = false;

                if (EaglerDeferredPipeline.instance == null)
                {
                    EaglerDeferredPipeline.instance = new EaglerDeferredPipeline(this.mc);
                    flag = true;
                }

                try
                {
                    SharedPipelineShaders.init();
                    EaglerDeferredPipeline.instance.rebuild(dfc);
                    EaglerDeferredPipeline.isSuspended = false;
                }
                catch (Throwable ex)
                {
                    LOGGER.error("Could not enable shaders!");
                    LOGGER.error(ex);
                    EaglerDeferredPipeline.isSuspended = true;
                }

                if (flag && !EaglerDeferredPipeline.isSuspended)
                {
                    TextComponentString shaderF4Msg = new TextComponentString("[EaglercraftX] ");
                    shaderF4Msg.getStyle().setColor(TextFormatting.GOLD);
                    TextComponentTranslation shaderF4Msg2 = new TextComponentTranslation("shaders.debugMenuTip", Keyboard.getKeyName(this.mc.gameSettings.keyBindFunction.getKeyCode()));
                    shaderF4Msg2.getStyle().setColor(TextFormatting.AQUA);
                    shaderF4Msg.appendSibling(shaderF4Msg2);
                    this.mc.ingameGUI.getChatGUI().printChatMessage(shaderF4Msg);
                }
            }

            this.mc.gameSettings.shadersAODisable = this.mc.gameSettings.shaders && this.mc.gameSettings.deferredShaderConf.is_rendering_ssao;

            if (!this.mc.gameSettings.shaders || EaglerDeferredPipeline.isSuspended)
            {
                try
                {
                    if (EaglerDeferredPipeline.instance != null)
                    {
                        EaglerDeferredPipeline.instance.destroy();
                        EaglerDeferredPipeline.instance = null;
                    }
                }
                catch (Throwable ex)
                {
                    LOGGER.error("Could not safely disable shaders!");
                    LOGGER.error(ex);
                }

                SharedPipelineShaders.free();
            }

            if (DeferredStateManager.isDeferredRenderer())
            {
                DynamicLightsStateManager.disableDynamicLightsRender(false);
            }
            else if (this.mc.gameSettings.enableDynamicLights)
            {
                DynamicLightsStateManager.enableDynamicLightsRender();
            }
            else
            {
                DynamicLightsStateManager.disableDynamicLightsRender(true);
            }
        }
    }

    protected void stopChunkUpdates()
    {
        this.chunksToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
    }

    public void createBindEntityOutlineFbs(int width, int height)
    {
    }

    public ICamera getCamera()
    {
        return this.camera;
    }

    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks)
    {
        int pass = net.minecraftforge.client.MinecraftForgeClient.getRenderPass();
        if (this.renderEntitiesStartupCounter > 0)
        {
            if (pass > 0) return;
            --this.renderEntitiesStartupCounter;
        }
        else
        {
            boolean light = DynamicLightManager.isRenderingLights();
            double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
            double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
            double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
            this.world.profiler.startSection("prepare");
            TileEntityRendererDispatcher.instance.prepare(this.world, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.getRenderViewEntity(), this.mc.objectMouseOver, partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.world, this.mc.fontRenderer, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            if(pass == 0)
            {
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
            this.world.profiler.endStartSection("global");
            List<Entity> list = this.world.getLoadedEntityList();
            if (pass == 0)
            {
            this.countEntitiesTotal = list.size();
            }

            if (!DeferredStateManager.isDeferredRenderer())
            {
                for (int i = 0; i < this.world.weatherEffects.size(); ++i)
                {
                    Entity entity1 = this.world.weatherEffects.get(i);
                    if (!entity1.shouldRenderInPass(pass)) continue;
                    ++this.countEntitiesRendered;

                    if (entity1.isInRangeToRender3d(d0, d1, d2))
                    {
                        if (light)
                        {
                            entity1.renderDynamicLightsEagler(partialTicks, true);
                        }

                        this.renderManager.renderEntityStatic(entity1, partialTicks, false);
                    }
                }
            }

            this.world.profiler.endStartSection("entities");
            List<Entity> list2 = Lists.<Entity>newArrayList();
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

            for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos)
            {
                Chunk chunk = this.world.getChunkFromBlockCoords(renderglobal$containerlocalrenderinformation.renderChunk.getPosition());
                ClassInheritanceMultiMap<Entity> classinheritancemultimap = chunk.getEntityLists()[renderglobal$containerlocalrenderinformation.renderChunk.getPosition().getY() / 16];

                if (!classinheritancemultimap.isEmpty())
                {
                    for (Entity entity2 : classinheritancemultimap)
                    {
                        if(!entity2.shouldRenderInPass(pass)) continue;
                        boolean flag = this.renderManager.shouldRender(entity2, camera, d0, d1, d2) || entity2.isRidingOrBeingRiddenBy(this.mc.player);

                        if (light)
                        {
                            entity2.renderDynamicLightsEagler(partialTicks, flag);
                        }

                        if (flag)
                        {
                            boolean flag1 = this.mc.getRenderViewEntity() instanceof EntityLivingBase ? ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping() : false;

                            if ((entity2 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || flag1) && (entity2.posY < 0.0D || entity2.posY >= 256.0D || this.world.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(entity2))))
                            {
                                ++this.countEntitiesRendered;
                                this.renderManager.renderEntityStatic(entity2, partialTicks, false);
								if (pass == 0 && this.isRenderEntityOutlines() && this.shouldOutline(entity2, entity, camera)) {
									this.outlineEntities.add(entity2);
								}

                                if (this.renderManager.isRenderMultipass(entity2))
                                {
                                    list2.add(entity2);
                                }
                            }
                        }
                    }
                }
            }

            blockpos$pooledmutableblockpos.release();
			if (pass == 0 && !DeferredStateManager.isDeferredRenderer()) {
				this.prepareEntityOutlines(partialTicks);
			}

            if (!list2.isEmpty())
            {
                for (Entity entity3 : list2)
                {
                    this.renderManager.renderMultipass(entity3, partialTicks);
                }
            }

            this.world.profiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();

            TileEntityRendererDispatcher.instance.preDrawBatch();
            for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 : this.renderInfos)
            {
                List<TileEntity> list3 = renderglobal$containerlocalrenderinformation1.renderChunk.getCompiledChunk().getTileEntities();

                if (!list3.isEmpty())
                {
                    for (TileEntity tileentity2 : list3)
                    {
                        if (!tileentity2.shouldRenderInPass(pass) || !camera.isBoundingBoxInFrustum(tileentity2.getRenderBoundingBox())) continue;
                        TileEntityRendererDispatcher.instance.render(tileentity2, partialTicks, -1);
                    }
                }
            }

            synchronized (this.setTileEntities)
            {
                for (TileEntity tileentity : this.setTileEntities)
                {
                    if (!tileentity.shouldRenderInPass(pass) || !camera.isBoundingBoxInFrustum(tileentity.getRenderBoundingBox())) continue;
                    TileEntityRendererDispatcher.instance.render(tileentity, partialTicks, -1);
                }
            }
            TileEntityRendererDispatcher.instance.drawBatch(pass);

            this.preRenderDamagedBlocks();

            for (DestroyBlockProgress destroyblockprogress : this.damagedBlocks.values())
            {
                BlockPos blockpos = destroyblockprogress.getPosition();

                if (this.world.getBlockState(blockpos).getBlock().hasTileEntity())
                {
                    TileEntity tileentity1 = this.world.getTileEntity(blockpos);

                    if (tileentity1 instanceof TileEntityChest)
                    {
                        TileEntityChest tileentitychest = (TileEntityChest)tileentity1;

                        if (tileentitychest.adjacentChestXNeg != null)
                        {
                            blockpos = blockpos.offset(EnumFacing.WEST);
                            tileentity1 = this.world.getTileEntity(blockpos);
                        }
                        else if (tileentitychest.adjacentChestZNeg != null)
                        {
                            blockpos = blockpos.offset(EnumFacing.NORTH);
                            tileentity1 = this.world.getTileEntity(blockpos);
                        }
                    }

                    IBlockState iblockstate = this.world.getBlockState(blockpos);

                    if (tileentity1 != null && iblockstate.hasCustomBreakingProgress())
                    {
                        TileEntityRendererDispatcher.instance.render(tileentity1, partialTicks, destroyblockprogress.getPartialBlockDamage());
                    }
                }
            }

            this.postRenderDamagedBlocks();
            this.mc.entityRenderer.disableLightmap();
            this.mc.mcProfiler.endSection();
        }
    }

    public static interface EntityChunkCullAdapter
    {
        boolean shouldCull(RenderChunk renderChunk);
    }

    public static interface EntityObjectCullAdapter
    {
        boolean shouldCull(RenderChunk renderChunk, RenderManager renderManager, Entity entity);
    }

    public void renderShadowLODEntities(Entity renderViewEntity, float partialTicks, RenderGlobal.EntityChunkCullAdapter entityChunkCull, RenderGlobal.EntityObjectCullAdapter entityObjectCull)
    {
        if (this.renderEntitiesStartupCounter <= 0)
        {
            TileEntityRendererDispatcher.instance.prepare(this.world, this.mc.getTextureManager(), this.mc.fontRenderer, renderViewEntity, this.mc.objectMouseOver, partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.world, this.mc.fontRenderer, renderViewEntity, this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            double d3 = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * (double)partialTicks;
            double d4 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * (double)partialTicks;
            double d5 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * (double)partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d3;
            TileEntityRendererDispatcher.staticPlayerY = d4;
            TileEntityRendererDispatcher.staticPlayerZ = d5;
            this.renderManager.setRenderPosition(d3, d4, d5);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int ii = 0, ll = this.renderInfos.size(); ii < ll; ++ii)
            {
                RenderChunk currentRenderChunk = this.renderInfos.get(ii).renderChunk;

                if (!entityChunkCull.shouldCull(currentRenderChunk))
                {
                    Chunk chunk = this.world.getChunkFromBlockCoords(currentRenderChunk.getPosition());
                    ClassInheritanceMultiMap<Entity> classinheritancemultimap = chunk.getEntityLists()[currentRenderChunk.getPosition().getY() / 16];

                    if (!classinheritancemultimap.isEmpty())
                    {
                        for (Entity entity2 : classinheritancemultimap)
                        {
                            if ((!entityObjectCull.shouldCull(currentRenderChunk, this.renderManager, entity2) || entity2.isRidingOrBeingRiddenBy(this.mc.player)) && (entity2.posY < 0.0D || entity2.posY >= 256.0D || this.world.isBlockLoaded(blockpos$mutableblockpos.setPos(entity2))))
                            {
                                ++this.countEntitiesRendered;
                                this.renderManager.renderEntityStatic(entity2, partialTicks, false);
                                this.mc.entityRenderer.disableLightmap();
                                GlStateManager.disableShaderBlendAdd();
                                GlStateManager.disableBlend();
                                GlStateManager.depthMask(true);
                            }
                        }
                    }

                    List<TileEntity> tileEntities = currentRenderChunk.compiledChunk.getTileEntities();

                    for (int i = 0, l = tileEntities.size(); i < l; ++i)
                    {
                        TileEntityRendererDispatcher.instance.render(tileEntities.get(i), partialTicks, -1);
                        this.mc.entityRenderer.disableLightmap();
                        GlStateManager.disableShaderBlendAdd();
                        GlStateManager.disableBlend();
                        GlStateManager.depthMask(true);
                    }
                }
            }

            synchronized (this.setTileEntities)
            {
                for (TileEntity tileentity : this.setTileEntities)
                {
                    TileEntityRendererDispatcher.instance.render(tileentity, partialTicks, -1);
                    this.mc.entityRenderer.disableLightmap();
                    GlStateManager.disableShaderBlendAdd();
                    GlStateManager.disableBlend();
                    GlStateManager.depthMask(true);
                }
            }
        }
    }

    public void renderParaboloidTileEntities(Entity renderViewEntity, float partialTicks, int up)
    {
        if (this.renderEntitiesStartupCounter <= 0)
        {
            TileEntityRendererDispatcher.instance.prepare(this.world, this.mc.getTextureManager(), this.mc.fontRenderer, renderViewEntity, this.mc.objectMouseOver, partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.world, this.mc.fontRenderer, renderViewEntity, this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            double d3 = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * (double)partialTicks;
            double d4 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * (double)partialTicks;
            double d5 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * (double)partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d3;
            TileEntityRendererDispatcher.staticPlayerY = d4;
            TileEntityRendererDispatcher.staticPlayerZ = d5;
            this.renderManager.setRenderPosition(d3, d4, d5);
            double rad = 8.0D;
            int minX = (int)(d3 - rad);
            int minY = (int)d4;

            if (up == -1)
            {
                minY = (int)((double)minY - rad);
            }

            int minZ = (int)(d5 - rad);
            int maxX = (int)(d3 + rad);
            int maxY = (int)d4;

            if (up == 1)
            {
                maxY = (int)((double)maxY + rad);
            }

            int maxZ = (int)(d5 + rad);
            BlockPos.MutableBlockPos tmp = new BlockPos.MutableBlockPos();
            minX = MathHelper.floor((double)minX / 16.0D) * 16;
            minY = MathHelper.floor((double)minY / 16.0D) * 16;
            minZ = MathHelper.floor((double)minZ / 16.0D) * 16;
            maxX = MathHelper.floor((double)maxX / 16.0D) * 16;
            maxY = MathHelper.floor((double)maxY / 16.0D) * 16;
            maxZ = MathHelper.floor((double)maxZ / 16.0D) * 16;

            for (int cx = minX; cx <= maxX; cx += 16)
            {
                for (int cz = minZ; cz <= maxZ; cz += 16)
                {
                    for (int cy = minY; cy <= maxY; cy += 16)
                    {
                        RenderChunk ch = this.viewFrustum.getRenderChunk(tmp.setPos(cx, cy, cz));
                        CompiledChunk cch;

                        if (ch != null && (cch = ch.compiledChunk) != null)
                        {
                            List<TileEntity> tileEntities = cch.getTileEntities();

                            for (int i = 0, l = tileEntities.size(); i < l; ++i)
                            {
                                this.mc.entityRenderer.enableLightmap();
                                TileEntityRendererDispatcher.instance.render(tileEntities.get(i), partialTicks, -1);
                                GlStateManager.disableShaderBlendAdd();
                                GlStateManager.disableBlend();
                                GlStateManager.depthMask(true);
                            }
                        }
                    }
                }
            }

            this.mc.entityRenderer.disableLightmap();
        }
    }

    public String getDebugInfoRenders()
    {
        int i = this.viewFrustum.renderChunks.length;
        int j = this.getRenderedChunks();
        return HString.format("C: %d/%d %sD: %d, %s", j, i, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.renderDispatcher.getDebugInfo());
    }

    protected int getRenderedChunks()
    {
        int i = 0;

        for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos)
        {
            CompiledChunk compiledchunk = renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk;

            if (compiledchunk != CompiledChunk.DUMMY && !compiledchunk.isEmpty())
            {
                ++i;
            }
        }

        return i;
    }

    public String getDebugInfoEntities()
    {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ", B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
    }

    public String getDebugInfoShort()
    {
        int i = this.viewFrustum.renderChunks.length;
        int j = 0;
        int k = 0;

        for (int ii = 0, ll = this.renderInfos.size(); ii < ll; ++ii)
        {
            CompiledChunk compiledchunk = this.renderInfos.get(ii).renderChunk.compiledChunk;

            if (compiledchunk != CompiledChunk.DUMMY && !compiledchunk.isEmpty())
            {
                ++j;
                k += compiledchunk.getTileEntities().size();
            }
        }

        return "" + Minecraft.getDebugFPS() + "fps | C: " + j + "/" + i + ", E: " + this.countEntitiesRendered + "+" + k + ", " + this.renderDispatcher.getDebugInfo();
    }

    public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator)
    {
        this.camera = camera;
        if (ParticleCulling.cullThread != null)
        {
            ParticleCulling.cullThread.run();
        }

        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks)
        {
            this.loadRenderers();
        }

        this.world.profiler.startSection("camera");
        double d0 = viewEntity.posX - this.frustumUpdatePosX;
        double d1 = viewEntity.posY - this.frustumUpdatePosY;
        double d2 = viewEntity.posZ - this.frustumUpdatePosZ;

        if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D)
        {
            this.frustumUpdatePosX = viewEntity.posX;
            this.frustumUpdatePosY = viewEntity.posY;
            this.frustumUpdatePosZ = viewEntity.posZ;
            this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
            this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
            this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
            this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
        }

        this.world.profiler.endStartSection("renderlistcamera");
        double d3 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        double d4 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        double d5 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        this.renderContainer.initialize(d3, d4, d5);
        this.world.profiler.endStartSection("cull");

        if (this.debugFixedClippingHelper != null)
        {
            Frustum frustum = new Frustum(this.debugFixedClippingHelper);
            frustum.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
            camera = frustum;
        }

        this.mc.mcProfiler.endStartSection("culling");
        BlockPos blockpos1 = new BlockPos(d3, d4 + (double)viewEntity.getEyeHeight(), d5);
        RenderChunk renderchunk = this.viewFrustum.getRenderChunk(blockpos1);
        BlockPos blockpos = new BlockPos(MathHelper.floor(d3 / 16.0D) * 16, MathHelper.floor(d4 / 16.0D) * 16, MathHelper.floor(d5 / 16.0D) * 16);
        this.displayListEntitiesDirty = this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || viewEntity.posX != this.lastViewEntityX || viewEntity.posY != this.lastViewEntityY || viewEntity.posZ != this.lastViewEntityZ || (double)viewEntity.rotationPitch != this.lastViewEntityPitch || (double)viewEntity.rotationYaw != this.lastViewEntityYaw || this.mc.entityRenderer.currentProjMatrixFOV != this.lastViewProjMatrixFOV;
        this.lastViewEntityX = viewEntity.posX;
        this.lastViewEntityY = viewEntity.posY;
        this.lastViewEntityZ = viewEntity.posZ;
        this.lastViewEntityPitch = (double)viewEntity.rotationPitch;
        this.lastViewEntityYaw = (double)viewEntity.rotationYaw;
        this.lastViewProjMatrixFOV = this.mc.entityRenderer.currentProjMatrixFOV;
        boolean flag = this.debugFixedClippingHelper != null;
        this.mc.mcProfiler.endStartSection("update");

        if (!flag && this.displayListEntitiesDirty)
        {
            this.displayListEntitiesDirty = false;
            this.renderInfos = Lists.<RenderGlobal.ContainerLocalRenderInformation>newArrayList();
            Queue<RenderGlobal.ContainerLocalRenderInformation> queue = new ArrayDeque<>();
            Entity.setRenderDistanceWeight(MathHelper.clamp((double)this.mc.gameSettings.renderDistanceChunks / 8.0D, 1.0D, 2.5D));
            boolean flag1 = this.mc.renderChunksMany;

            if (renderchunk != null)
            {
                boolean flag2 = false;
                RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation3 = new RenderGlobal.ContainerLocalRenderInformation(renderchunk, (EnumFacing)null, 0);
                Set<EnumFacing> set1 = this.getVisibleFacings(blockpos1);

                if (set1.size() == 1)
                {
                    Vector3f vector3f = this.getViewVector(viewEntity, partialTicks);
                    EnumFacing enumfacing = EnumFacing.getFacingFromVector(vector3f.x, vector3f.y, vector3f.z).getOpposite();
                    set1.remove(enumfacing);
                }

                if (set1.isEmpty())
                {
                    flag2 = true;
                }

                if (flag2 && !playerSpectator)
                {
                    this.renderInfos.add(renderglobal$containerlocalrenderinformation3);
                }
                else
                {
                    if (playerSpectator && this.world.getBlockState(blockpos1).isOpaqueCube())
                    {
                        flag1 = false;
                    }

                    renderchunk.setFrameIndex(frameCount);
                    queue.add(renderglobal$containerlocalrenderinformation3);
                }
            }
            else
            {
                int i = blockpos1.getY() > 0 ? 248 : 8;

                for (int j = -this.renderDistanceChunks; j <= this.renderDistanceChunks; ++j)
                {
                    for (int k = -this.renderDistanceChunks; k <= this.renderDistanceChunks; ++k)
                    {
                        RenderChunk renderchunk1 = this.viewFrustum.getRenderChunk(new BlockPos((j << 4) + 8, i, (k << 4) + 8));

                        if (renderchunk1 != null && camera.isBoundingBoxInFrustum(renderchunk1.boundingBox.expand(0.0, blockpos1.getY() > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY, 0.0))) // Forge: fix MC-73139
                        {
                            renderchunk1.setFrameIndex(frameCount);
                            queue.add(new RenderGlobal.ContainerLocalRenderInformation(renderchunk1, (EnumFacing)null, 0));
                        }
                    }
                }
            }

            this.mc.mcProfiler.startSection("iteration");

            while (!queue.isEmpty())
            {
                RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 = queue.poll();
                RenderChunk renderchunk3 = renderglobal$containerlocalrenderinformation1.renderChunk;
                EnumFacing enumfacing2 = renderglobal$containerlocalrenderinformation1.facing;
                this.renderInfos.add(renderglobal$containerlocalrenderinformation1);
                EnumFacing[] facings = EnumFacing._VALUES;

                for (int ii = 0; ii < facings.length; ++ii)
                {
                    EnumFacing enumfacing1 = facings[ii];
                    RenderChunk renderchunk2 = this.getRenderChunkOffset(blockpos, renderchunk3, enumfacing1);

                    if ((!flag1 || !renderglobal$containerlocalrenderinformation1.hasDirection(enumfacing1.getOpposite())) && (!flag1 || enumfacing2 == null || renderchunk3.getCompiledChunk().isVisible(enumfacing2.getOpposite(), enumfacing1)) && renderchunk2 != null && renderchunk2.setFrameIndex(frameCount) && camera.isBoundingBoxInFrustum(renderchunk2.boundingBox))
                    {
                        RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = new RenderGlobal.ContainerLocalRenderInformation(renderchunk2, enumfacing1, renderglobal$containerlocalrenderinformation1.counter + 1);
                        renderglobal$containerlocalrenderinformation.setDirection(renderglobal$containerlocalrenderinformation1.setFacing, enumfacing1);
                        queue.add(renderglobal$containerlocalrenderinformation);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.mcProfiler.endStartSection("captureFrustum");

        if (this.debugFixTerrainFrustum)
        {
            this.fixTerrainFrustum(d3, d4, d5);
            this.debugFixTerrainFrustum = false;
        }

        this.mc.mcProfiler.endStartSection("rebuildNear");
        Set<RenderChunk> set = this.chunksToUpdate;
        this.chunksToUpdate = Sets.<RenderChunk>newLinkedHashSet();

        for (int ii = 0, ll = this.renderInfos.size(); ii < ll; ++ii)
        {
            RenderChunk renderchunk4 = this.renderInfos.get(ii).renderChunk;

            if (renderchunk4.needsUpdate() || set.contains(renderchunk4))
            {
                this.displayListEntitiesDirty = true;

                if (renderchunk4.needsImmediateUpdate() || (this.mc.gameSettings.chunkFix ? this.isPositionInRenderChunkHack(blockpos1, renderchunk4) : this.isPositionInRenderChunk(blockpos, renderchunk4)))
                {
                    this.mc.mcProfiler.startSection("build near");
                    this.renderDispatcher.updateChunkNow(renderchunk4);
                    renderchunk4.clearNeedsUpdate();
                    this.mc.mcProfiler.endSection();
                }
                else
                {
                    this.chunksToUpdate.add(renderchunk4);
                }
            }
        }

        this.chunksToUpdate.addAll(set);
        this.mc.mcProfiler.endSection();
    }

    private boolean isPositionInRenderChunk(BlockPos pos, RenderChunk renderChunkIn)
    {
        BlockPos blockpos = renderChunkIn.getPosition();
        return MathHelper.abs(pos.getX() - blockpos.getX()) > 16 ? false : (MathHelper.abs(pos.getY() - blockpos.getY()) > 16 ? false : MathHelper.abs(pos.getZ() - blockpos.getZ()) <= 16);
    }

    private boolean isPositionInRenderChunkHack(BlockPos pos, RenderChunk renderChunkIn)
    {
        BlockPos blockpos = renderChunkIn.getPosition();
        return MathHelper.abs(pos.getX() - blockpos.getX() - 8) > 11 ? false : (MathHelper.abs(pos.getY() - blockpos.getY() - 8) > 11 ? false : MathHelper.abs(pos.getZ() - blockpos.getZ() - 8) <= 11);
    }

    private Set<EnumFacing> getVisibleFacings(BlockPos pos)
    {
        VisGraph visgraph = new VisGraph();
        BlockPos blockpos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
        Chunk chunk = this.world.getChunkFromBlockCoords(blockpos);

        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos.add(15, 15, 15)))
        {
            if (chunk.getBlockState(blockpos$mutableblockpos).isOpaqueCube())
            {
                visgraph.setOpaqueCube(blockpos$mutableblockpos);
            }
        }

        return visgraph.getVisibleFacings(pos);
    }

    @Nullable
    private RenderChunk getRenderChunkOffset(BlockPos playerPos, RenderChunk renderChunkBase, EnumFacing facing)
    {
        BlockPos blockpos = renderChunkBase.getBlockPosOffset16(facing);

        if (MathHelper.abs(playerPos.getX() - blockpos.getX()) > this.renderDistanceChunks * 16)
        {
            return null;
        }
        else if (blockpos.getY() >= 0 && blockpos.getY() < 256)
        {
            return MathHelper.abs(playerPos.getZ() - blockpos.getZ()) > this.renderDistanceChunks * 16 ? null : this.viewFrustum.getRenderChunk(blockpos);
        }
        else
        {
            return null;
        }
    }

    private void fixTerrainFrustum(double x, double y, double z)
    {
        this.debugFixedClippingHelper = new ClippingHelperImpl();
        ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
        Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
        matrix4f.transpose();
        Matrix4f matrix4f1 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
        matrix4f1.transpose();
        Matrix4f matrix4f2 = new Matrix4f();
        Matrix4f.mul(matrix4f1, matrix4f, matrix4f2);
        matrix4f2.invert();
        this.debugTerrainFrustumPosition.x = x;
        this.debugTerrainFrustumPosition.y = y;
        this.debugTerrainFrustumPosition.z = z;
        this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 8; ++i)
        {
            Matrix4f.transform(matrix4f2, this.debugTerrainMatrix[i], this.debugTerrainMatrix[i]);
            this.debugTerrainMatrix[i].x /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].y /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].z /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].w = 1.0F;
        }
    }

    protected Vector3f getViewVector(Entity entityIn, double partialTicks)
    {
        float f = (float)((double)entityIn.prevRotationPitch + (double)(entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
        float f1 = (float)((double)entityIn.prevRotationYaw + (double)(entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);

        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2)
        {
            f += 180.0F;
        }

        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        return new Vector3f(f3 * f4, f5, f2 * f4);
    }

    public int renderBlockLayer(BlockRenderLayer blockLayerIn, double partialTicks, int pass, Entity entityIn)
    {
        RenderHelper.disableStandardItemLighting();

        if (blockLayerIn == BlockRenderLayer.TRANSLUCENT)
        {
            this.mc.mcProfiler.startSection("translucent_sort");
            double d0 = entityIn.posX - this.prevRenderSortX;
            double d1 = entityIn.posY - this.prevRenderSortY;
            double d2 = entityIn.posZ - this.prevRenderSortZ;

            if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)
            {
                this.prevRenderSortX = entityIn.posX;
                this.prevRenderSortY = entityIn.posY;
                this.prevRenderSortZ = entityIn.posZ;
                int k = 0;

                for (int ii = 0, ll = this.renderInfos.size(); ii < ll; ++ii)
                {
                    RenderChunk renderchunk = this.renderInfos.get(ii).renderChunk;

                    if (renderchunk.compiledChunk.isLayerStarted(blockLayerIn) && k++ < 15)
                    {
                        this.renderDispatcher.updateTransparencyLater(renderchunk);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.mcProfiler.startSection("filterempty");
        int l = 0;
        boolean flag = blockLayerIn == BlockRenderLayer.TRANSLUCENT;
        int i1 = flag ? this.renderInfos.size() - 1 : 0;
        int i = flag ? -1 : this.renderInfos.size();
        int j1 = flag ? -1 : 1;

        for (int j = i1; j != i; j += j1)
        {
            RenderChunk renderchunk = (this.renderInfos.get(j)).renderChunk;

            if (!renderchunk.getCompiledChunk().isLayerEmpty(blockLayerIn))
            {
                ++l;
                this.renderContainer.addRenderChunk(renderchunk, blockLayerIn);
            }
        }

        this.mc.mcProfiler.endStartSection("render");
        this.renderBlockLayer(blockLayerIn);
        this.mc.mcProfiler.endSection();
        return l;
    }

    public static interface ChunkCullAdapter
    {
        boolean shouldCull(RenderChunk chunk);
    }

    public int renderBlockLayerShadow(BlockRenderLayer blockLayerIn, AxisAlignedBB boundingBox, RenderGlobal.ChunkCullAdapter cullAdapter)
    {
        int i = 0;
        BlockPos.MutableBlockPos tmp = new BlockPos.MutableBlockPos();
        int minXChunk = MathHelper.floor(boundingBox.minX / 16.0D) * 16;
        int minYChunk = MathHelper.floor(boundingBox.minY / 16.0D) * 16;
        int minZChunk = MathHelper.floor(boundingBox.minZ / 16.0D) * 16;
        int maxXChunk = MathHelper.floor(boundingBox.maxX / 16.0D) * 16;
        int maxYChunk = MathHelper.floor(boundingBox.maxY / 16.0D) * 16;
        int maxZChunk = MathHelper.floor(boundingBox.maxZ / 16.0D) * 16;

        for (int cx = minXChunk; cx <= maxXChunk; cx += 16)
        {
            for (int cz = minZChunk; cz <= maxZChunk; cz += 16)
            {
                for (int cy = minYChunk; cy <= maxYChunk; cy += 16)
                {
                    RenderChunk ch = this.viewFrustum.getRenderChunk(tmp.setPos(cx, cy, cz));
                    CompiledChunk cch;

                    if (ch != null && (cch = ch.getCompiledChunk()) != null && !cch.isLayerEmpty(blockLayerIn) && !cullAdapter.shouldCull(ch))
                    {
                        this.renderContainer.addRenderChunk(ch, blockLayerIn);
                        ++i;
                    }
                }
            }
        }

        if (i > 0)
        {
            this.renderContainer.renderChunkLayer(blockLayerIn);
        }

        return i;
    }

    private void renderBlockLayer(BlockRenderLayer blockLayerIn)
    {
        this.mc.entityRenderer.enableLightmap();
        if (blockLayerIn == BlockRenderLayer.SOLID && meldexun.blockoverlayfix.BlockOverlayFix.isEnabled())
        {
            meldexun.blockoverlayfix.BlockOverlayFix.renderNearbyBlocks(this.mc.getRenderPartialTicks());
        }
        this.renderContainer.renderChunkLayer(blockLayerIn);
        this.mc.entityRenderer.disableLightmap();
    }

    public int renderParaboloidBlockLayer(BlockRenderLayer blockLayerIn, double partialTicks, int up, Entity entityIn)
    {
        double rad = 8.0D;
        int minX = (int)(entityIn.posX - rad);
        int minY = (int)entityIn.posY;

        if (up == -1)
        {
            minY = (int)((double)minY - rad * 0.75D);
        }
        else
        {
            minY = (int)((double)minY + 1.0D);
        }

        int minZ = (int)(entityIn.posZ - rad);
        int maxX = (int)(entityIn.posX + rad);
        int maxY = (int)entityIn.posY;

        if (up == 1)
        {
            maxY = (int)((double)maxY + rad);
        }
        else
        {
            maxY = (int)((double)maxY + 2.0D);
        }

        int maxZ = (int)(entityIn.posZ + rad);
        BlockPos.MutableBlockPos tmp = new BlockPos.MutableBlockPos();
        minX = MathHelper.floor((double)minX / 16.0D) * 16;
        minY = MathHelper.floor((double)minY / 16.0D) * 16;
        minZ = MathHelper.floor((double)minZ / 16.0D) * 16;
        maxX = MathHelper.floor((double)maxX / 16.0D) * 16;
        maxY = MathHelper.floor((double)maxY / 16.0D) * 16;
        maxZ = MathHelper.floor((double)maxZ / 16.0D) * 16;
        int i = 0;

        for (int cx = minX; cx <= maxX; cx += 16)
        {
            for (int cz = minZ; cz <= maxZ; cz += 16)
            {
                for (int cy = minY; cy <= maxY; cy += 16)
                {
                    RenderChunk ch = this.viewFrustum.getRenderChunk(tmp.setPos(cx, cy, cz));
                    CompiledChunk cch;

                    if (ch != null && (cch = ch.getCompiledChunk()) != null && !cch.isLayerEmpty(blockLayerIn))
                    {
                        this.renderContainer.addRenderChunk(ch, blockLayerIn);
                        ++i;
                    }
                }
            }
        }

        if (i > 0)
        {
            this.mc.entityRenderer.enableLightmap();
            this.renderContainer.renderChunkLayer(blockLayerIn);
            this.mc.entityRenderer.disableLightmap();
        }

        return i;
    }

    private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn)
    {
        while (iteratorIn.hasNext())
        {
            DestroyBlockProgress destroyblockprogress = iteratorIn.next();
            int k1 = destroyblockprogress.getCreationCloudUpdateTick();

            if (this.cloudTickCounter - k1 > 400)
            {
                iteratorIn.remove();
            }
        }
    }

    public void updateClouds()
    {
        ++this.cloudTickCounter;

        if (this.cloudTickCounter % 20 == 0)
        {
            this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
        }

        this.alfheim$processLightUpdates();
    }

    private void renderSkyEnd()
    {
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);
        this.renderEngine.bindTexture(END_SKY_TEXTURES);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();

        for (int k1 = 0; k1 < 6; ++k1)
        {
            GlStateManager.pushMatrix();

            if (k1 == 1)
            {
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k1 == 2)
            {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k1 == 3)
            {
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k1 == 4)
            {
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (k1 == 5)
            {
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
            bufferbuilder.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
            bufferbuilder.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
            bufferbuilder.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
    }

    public void renderSky(float partialTicks, int pass)
    {
        net.minecraftforge.client.IRenderHandler renderer = this.world.provider.getSkyRenderer();
        if (renderer != null)
        {
            renderer.render(partialTicks, world, mc);
            return;
        }

        if (this.mc.world.provider.getDimensionType().getId() == 1)
        {
            this.renderSkyEnd();
        }
        else if (this.mc.world.provider.isSurfaceWorld())
        {
            GlStateManager.disableTexture2D();
            Vec3d vec3d = this.world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
            float f = (float)vec3d.x;
            float f1 = (float)vec3d.y;
            float f2 = (float)vec3d.z;

            if (pass != 2)
            {
                float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                f = f3;
                f1 = f4;
                f2 = f5;
            }

            GlStateManager.color(f, f1, f2);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer bufferbuilder = tessellator.getBuffer();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableFog();
            GlStateManager.color(f, f1, f2);
            GlStateManager.callList(this.glSkyList);

            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
            RenderHelper.disableStandardItemLighting();
            float[] afloat = this.world.provider.calcSunriseSunsetColors(this.world.getCelestialAngle(partialTicks), partialTicks);

            if (afloat != null)
            {
                GlStateManager.disableTexture2D();
                GlStateManager.shadeModel(GL_SMOOTH);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(MathHelper.sin(this.world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                float f6 = afloat[0];
                float f7 = afloat[1];
                float f8 = afloat[2];

                if (pass != 2)
                {
                    float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                    float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
                    float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
                    f6 = f9;
                    f7 = f10;
                    f8 = f11;
                }

                bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
                int l1 = 16;

                for (int j2 = 0; j2 <= 16; ++j2)
                {
                    float f21 = (float)j2 * ((float)Math.PI * 2F) / 16.0F;
                    float f12 = MathHelper.sin(f21);
                    float f13 = MathHelper.cos(f21);
                    bufferbuilder.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
                }

                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(GL_FLAT);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_ONE, GL_ZERO);
            GlStateManager.pushMatrix();
            float f16 = 1.0F - this.world.getRainStrength(partialTicks);
            GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            CustomSky.renderSky(this.world, this.renderEngine, partialTicks);
            GlStateManager.rotate(this.world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
            this.renderEngine.bindTexture(SUN_TEXTURES);
            GlStateManager.callList(this.glSunList);
            this.renderEngine.bindTexture(MOON_PHASES_TEXTURES);
            GlStateManager.callList(this.getMoonList(this.world.getMoonPhase()));
            GlStateManager.disableTexture2D();
            float f15 = this.world.getStarBrightness(partialTicks) * f16;
            boolean b = !CustomSky.hasSkyLayers(this.world);

            if (f15 > 0.0F && b)
            {
                GlStateManager.color(f15, f15, f15, f15);
                GlStateManager.callList(this.starGLCallList);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();
            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double d3 = this.mc.player.getPositionEyes(partialTicks).y - this.world.getHorizon();

            if (d3 < 0.0D && b)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 12.0F, 0.0F);
                GlStateManager.callList(this.glSkyList2);
                GlStateManager.popMatrix();
                float f19 = -((float)(d3 + 65.0D));
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, f19, 0.0F);
                GlStateManager.scale(1.0F, 1.0F - f19, 1.0F);
                GlStateManager.callList(this.glHorizonList);
                GlStateManager.popMatrix();
            }

            if (this.world.provider.isSkyColored())
            {
                GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
            }
            else
            {
                GlStateManager.color(f, f1, f2);
            }

            if (b)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, -((float)(d3 - 16.0D)), 0.0F);
                GlStateManager.callList(this.glSkyList2);
                GlStateManager.popMatrix();
            }

            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
    }

    public void renderClouds(float partialTicks, int pass, double p_180447_3_, double p_180447_5_, double p_180447_7_)
    {
        net.minecraftforge.client.IRenderHandler renderer = this.world.provider.getCloudRenderer();

        if (renderer != null)
        {
            renderer.render(partialTicks, this.world, this.mc);
        }
        else if (this.mc.world.provider.isSurfaceWorld())
        {
            this.cloudRenderer.renderClouds(partialTicks, pass);
        }
    }

    public boolean hasCloudFog(double x, double y, double z, float partialTicks)
    {
        return false;
    }

    public void updateChunks(long finishTimeNano)
    {
        this.displayListEntitiesDirty |= this.renderDispatcher.updateChunks(finishTimeNano);

        if (!this.chunksToUpdate.isEmpty())
        {
            Iterator<RenderChunk> iterator = this.chunksToUpdate.iterator();

            while (iterator.hasNext())
            {
                RenderChunk renderchunk1 = iterator.next();
                boolean flag1;

                if (renderchunk1.needsImmediateUpdate())
                {
                    flag1 = this.renderDispatcher.updateChunkNow(renderchunk1);
                }
                else
                {
                    flag1 = this.renderDispatcher.updateChunkLater(renderchunk1);
                }

                if (!flag1)
                {
                    break;
                }

                renderchunk1.clearNeedsUpdate();
                iterator.remove();
                long k1 = finishTimeNano - EagRuntime.nanoTime();

                if (k1 < 0L)
                {
                    break;
                }
            }
        }
    }

    public void renderWorldBorder(Entity entityIn, float partialTicks)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();
        WorldBorder worldborder = this.world.getWorldBorder();
        double d3 = (double)(this.mc.gameSettings.renderDistanceChunks * 16);

        if (entityIn.posX >= worldborder.maxX() - d3 || entityIn.posX <= worldborder.minX() + d3 || entityIn.posZ >= worldborder.maxZ() - d3 || entityIn.posZ <= worldborder.minZ() + d3)
        {
            double d4 = 1.0D - worldborder.getClosestDistance(entityIn) / d3;
            d4 = Math.pow(d4, 4.0D);
            double d5 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
            double d6 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
            double d7 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_ONE, GL_ZERO);
            this.renderEngine.bindTexture(FORCEFIELD_TEXTURES);
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            int k1 = worldborder.getStatus().getColor();
            float f = (float)(k1 >> 16 & 255) / 255.0F;
            float f1 = (float)(k1 >> 8 & 255) / 255.0F;
            float f2 = (float)(k1 & 255) / 255.0F;
            GlStateManager.color(f, f1, f2, (float)d4);
            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableAlpha();
            GlStateManager.disableCull();
            float f3 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
            float f4 = 0.0F;
            float f5 = 0.0F;
            float f6 = 128.0F;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.setTranslation(-d5, -d6, -d7);
            double d8 = Math.max((double)MathHelper.floor(d7 - d3), worldborder.minZ());
            double d9 = Math.min((double)MathHelper.ceil(d7 + d3), worldborder.maxZ());

            if (d5 > worldborder.maxX() - d3)
            {
                float f7 = 0.0F;

                for (double d10 = d8; d10 < d9; f7 += 0.5F)
                {
                    double d11 = Math.min(1.0D, d9 - d10);
                    float f8 = (float)d11 * 0.5F;
                    bufferbuilder.pos(worldborder.maxX(), 256.0D, d10).tex((double)(f3 + f7), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 256.0D, d10 + d11).tex((double)(f3 + f8 + f7), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0D, d10 + d11).tex((double)(f3 + f8 + f7), (double)(f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0D, d10).tex((double)(f3 + f7), (double)(f3 + 128.0F)).endVertex();
                    ++d10;
                }
            }

            if (d5 < worldborder.minX() + d3)
            {
                float f9 = 0.0F;

                for (double d12 = d8; d12 < d9; f9 += 0.5F)
                {
                    double d15 = Math.min(1.0D, d9 - d12);
                    float f12 = (float)d15 * 0.5F;
                    bufferbuilder.pos(worldborder.minX(), 256.0D, d12).tex((double)(f3 + f9), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 256.0D, d12 + d15).tex((double)(f3 + f12 + f9), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0D, d12 + d15).tex((double)(f3 + f12 + f9), (double)(f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0D, d12).tex((double)(f3 + f9), (double)(f3 + 128.0F)).endVertex();
                    ++d12;
                }
            }

            d8 = Math.max((double)MathHelper.floor(d5 - d3), worldborder.minX());
            d9 = Math.min((double)MathHelper.ceil(d5 + d3), worldborder.maxX());

            if (d7 > worldborder.maxZ() - d3)
            {
                float f10 = 0.0F;

                for (double d13 = d8; d13 < d9; f10 += 0.5F)
                {
                    double d16 = Math.min(1.0D, d9 - d13);
                    float f13 = (float)d16 * 0.5F;
                    bufferbuilder.pos(d13, 256.0D, worldborder.maxZ()).tex((double)(f3 + f10), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d13 + d16, 256.0D, worldborder.maxZ()).tex((double)(f3 + f13 + f10), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d13 + d16, 0.0D, worldborder.maxZ()).tex((double)(f3 + f13 + f10), (double)(f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(d13, 0.0D, worldborder.maxZ()).tex((double)(f3 + f10), (double)(f3 + 128.0F)).endVertex();
                    ++d13;
                }
            }

            if (d7 < worldborder.minZ() + d3)
            {
                float f11 = 0.0F;

                for (double d14 = d8; d14 < d9; f11 += 0.5F)
                {
                    double d17 = Math.min(1.0D, d9 - d14);
                    float f14 = (float)d17 * 0.5F;
                    bufferbuilder.pos(d14, 256.0D, worldborder.minZ()).tex((double)(f3 + f11), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d14 + d17, 256.0D, worldborder.minZ()).tex((double)(f3 + f14 + f11), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d14 + d17, 0.0D, worldborder.minZ()).tex((double)(f3 + f14 + f11), (double)(f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(d14, 0.0D, worldborder.minZ()).tex((double)(f3 + f11), (double)(f3 + 128.0F)).endVertex();
                    ++d14;
                }
            }

            tessellator.draw();
            bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
            GlStateManager.enableCull();
            GlStateManager.disableAlpha();
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
        }
    }

    public void preRenderDamagedBlocks()
    {
        GlStateManager.tryBlendFuncSeparate(GL_DST_COLOR, GL_SRC_COLOR, GL_ONE, GL_ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        // FORGE: Fix MC-234
        GlStateManager.doPolygonOffset(-1.0F, -10.0F);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
    }

    public void postRenderDamagedBlocks()
    {
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    public void drawBlockDamageTexture(Tessellator tessellatorIn, WorldRenderer bufferBuilderIn, Entity entityIn, float partialTicks)
    {
        double d3 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        double d4 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        double d5 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;

        if (!this.damagedBlocks.isEmpty())
        {
            this.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.preRenderDamagedBlocks();
            bufferBuilderIn.begin(7, DeferredStateManager.isDeferredRenderer() ? VertexFormat.BLOCK_SHADERS : DefaultVertexFormats.BLOCK);
            bufferBuilderIn.setTranslation(-d3, -d4, -d5);
            bufferBuilderIn.markDirty();
            Iterator<DestroyBlockProgress> iterator = this.damagedBlocks.values().iterator();

            while (iterator.hasNext())
            {
                DestroyBlockProgress destroyblockprogress = iterator.next();
                BlockPos blockpos = destroyblockprogress.getPosition();
                double d6 = (double)blockpos.getX() - d3;
                double d7 = (double)blockpos.getY() - d4;
                double d8 = (double)blockpos.getZ() - d5;
                Block block = this.world.getBlockState(blockpos).getBlock();
                TileEntity te = this.world.getTileEntity(blockpos);
                boolean hasBreak = block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull;
                if (!hasBreak) hasBreak = te != null && te.canRenderBreaking();

                if (!hasBreak)
                {
                    if (d6 * d6 + d7 * d7 + d8 * d8 > 1024.0D)
                    {
                        iterator.remove();
                    }
                    else
                    {
                        IBlockState iblockstate = this.world.getBlockState(blockpos);

                        if (iblockstate.getMaterial() != Material.AIR)
                        {
                            int k1 = destroyblockprogress.getPartialBlockDamage();
                            EaglerTextureAtlasSprite textureatlassprite = this.destroyBlockIcons[k1];
                            BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();

                            if (DynamicLightsStateManager.isInDynamicLightsPass())
                            {
                                DynamicLightsStateManager.reportForwardRenderObjectPosition2(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                            }

                            blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, this.world);
                        }
                    }
                }
            }

            tessellatorIn.draw();
            bufferBuilderIn.setTranslation(0.0D, 0.0D, 0.0D);
            this.postRenderDamagedBlocks();
        }
    }

    public void drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks)
    {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
            EaglercraftGPU.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            BlockPos blockpos = movingObjectPositionIn.getBlockPos();
            IBlockState iblockstate = this.world.getBlockState(blockpos);

            if (iblockstate.getMaterial() != Material.AIR && this.world.getWorldBorder().contains(blockpos))
            {
                double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
                double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
                double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
                drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(this.world, blockpos).grow(0.0020000000949949026D).offset(-d3, -d4, -d5), 0.0F, 0.0F, 0.0F, 0.4F);
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha)
    {
        drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void drawBoundingBox(WorldRenderer buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        buffer.pos(minX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
    }

    public static void renderFilledBox(AxisAlignedBB aabb, float red, float green, float blue, float alpha)
    {
        renderFilledBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, red, green, blue, alpha);
    }

    public static void renderFilledBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        addChainedFilledBoxVertices(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void addChainedFilledBoxVertices(WorldRenderer builder, double p_189693_1_, double p_189693_3_, double p_189693_5_, double p_189693_7_, double p_189693_9_, double p_189693_11_, float red, float green, float blue, float alpha)
    {
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
    }

    private void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean updateImmediately)
    {
        this.viewFrustum.markBlocksForUpdate(minX, minY, minZ, maxX, maxY, maxZ, updateImmediately);
    }

    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {
        int k1 = pos.getX();
        int l1 = pos.getY();
        int i2 = pos.getZ();
        this.markBlocksForUpdate(k1 - 1, l1 - 1, i2 - 1, k1 + 1, l1 + 1, i2 + 1, (flags & 8) != 0);
    }

    public void notifyLightSet(BlockPos pos)
    {
        this.alfheim$lightUpdatesQueue.enqueue(pos.toLong());
    }

    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1, false);
    }

    public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos)
    {
        ISound isound = this.mapSoundPositions.get(pos);

        if (isound != null)
        {
            this.mc.getSoundHandler().stopSound(isound);
            this.mapSoundPositions.remove(pos);
        }

        if (soundIn != null)
        {
            ItemRecord itemrecord = ItemRecord.getBySound(soundIn);

            if (itemrecord != null)
            {
                this.mc.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordNameLocal());
            }

            ISound positionedsoundrecord = PositionedSoundRecord.getRecordSoundRecord(soundIn, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
            this.mapSoundPositions.put(pos, positionedsoundrecord);
            this.mc.getSoundHandler().playSound(positionedsoundrecord);
        }

        this.setPartying(this.world, pos, soundIn != null);
    }

    private void setPartying(World p_193054_1_, BlockPos pos, boolean p_193054_3_)
    {
        for (EntityLivingBase entitylivingbase : p_193054_1_.getEntitiesWithinAABB(EntityLivingBase.class, (new AxisAlignedBB(pos)).grow(3.0D)))
        {
            entitylivingbase.setPartying(pos, p_193054_3_);
        }
    }

    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch)
    {
    }

    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        this.spawnParticle(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, final double x, final double y, final double z, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        try
        {
            this.spawnParticle0(id, ignoreRange, p_190570_3_, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
            crashreportcategory.addCrashSection("ID", Integer.valueOf(id));

            if (parameters != null)
            {
                crashreportcategory.addCrashSection("Parameters", parameters);
            }

            crashreportcategory.addDetail("Position", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return CrashReportCategory.getCoordinateInfo(x, y, z);
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    private void spawnParticle(EnumParticleTypes particleIn, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Nullable
    private Particle spawnParticle0(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        return this.spawnParticle0(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Nullable
    private Particle spawnParticle0(int particleID, boolean ignoreRange, boolean minParticles, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        Entity entity = this.mc.getRenderViewEntity();

        if (this.mc != null && entity != null && this.mc.effectRenderer != null)
        {
            int k1 = this.calculateParticleLevel(minParticles);
            double d3 = entity.posX - xCoord;
            double d4 = entity.posY - yCoord;
            double d5 = entity.posZ - zCoord;

            if (ignoreRange)
            {
                return this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
            }
            else if (d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D)
            {
                return null;
            }
            else
            {
                return k1 > 1 ? null : this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
            }
        }
        else
        {
            return null;
        }
    }

    private int calculateParticleLevel(boolean p_190572_1_)
    {
        int k1 = this.mc.gameSettings.particleSetting;

        if (p_190572_1_ && k1 == 2 && this.world.rand.nextInt(10) == 0)
        {
            k1 = 1;
        }

        if (k1 == 1 && this.world.rand.nextInt(3) == 0)
        {
            k1 = 2;
        }

        return k1;
    }

    public void onEntityAdded(Entity entityIn)
    {
    }

    public void onEntityRemoved(Entity entityIn)
    {
    }

    public void deleteAllDisplayLists()
    {
    }

    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
        switch (soundID)
        {
            case 1023:
            case 1028:
            case 1038:
                Entity entity = this.mc.getRenderViewEntity();

                if (entity != null)
                {
                    double d3 = (double)pos.getX() - entity.posX;
                    double d4 = (double)pos.getY() - entity.posY;
                    double d5 = (double)pos.getZ() - entity.posZ;
                    double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                    double d7 = entity.posX;
                    double d8 = entity.posY;
                    double d9 = entity.posZ;

                    if (d6 > 0.0D)
                    {
                        d7 += d3 / d6 * 2.0D;
                        d8 += d4 / d6 * 2.0D;
                        d9 += d5 / d6 * 2.0D;
                    }

                    if (soundID == 1023)
                    {
                        this.world.playSound(d7, d8, d9, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
                    }
                    else if (soundID == 1038)
                    {
                        this.world.playSound(d7, d8, d9, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
                    }
                    else
                    {
                        this.world.playSound(d7, d8, d9, SoundEvents.ENTITY_ENDERDRAGON_DEATH, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
                    }
                }

            default:
        }
    }

    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
    {
        EaglercraftRandom random = this.world.rand;

        switch (type)
        {
            case 1000:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;
            case 1001:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
                break;
            case 1002:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
                break;
            case 1003:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
                break;
            case 1004:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_FIREWORK_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
                break;
            case 1005:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1006:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1007:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1008:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1009:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
                break;
            case 1010:

                if (Item.getItemById(data) instanceof ItemRecord)
                {
                    this.world.playRecord(blockPosIn, ((ItemRecord)Item.getItemById(data)).getSound());
                }
                else
                {
                    this.world.playRecord(blockPosIn, (SoundEvent)null);
                }

                break;
            case 1011:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1012:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1013:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1014:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1015:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1016:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1017:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1018:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1019:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1020:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1021:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1022:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1024:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1025:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1026:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1027:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1029:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1030:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1031:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1032:
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F));
                break;
            case 1033:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;
            case 1034:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;
            case 1035:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;
            case 1036:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1037:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 2000:
                int j2 = data % 3 - 1;
                int k1 = data / 3 % 3 - 1;
                double d11 = (double)blockPosIn.getX() + (double)j2 * 0.6D + 0.5D;
                double d13 = (double)blockPosIn.getY() + 0.5D;
                double d15 = (double)blockPosIn.getZ() + (double)k1 * 0.6D + 0.5D;

                for (int l2 = 0; l2 < 10; ++l2)
                {
                    double d16 = random.nextDouble() * 0.2D + 0.01D;
                    double d19 = d11 + (double)j2 * 0.01D + (random.nextDouble() - 0.5D) * (double)k1 * 0.5D;
                    double d22 = d13 + (random.nextDouble() - 0.5D) * 0.5D;
                    double d25 = d15 + (double)k1 * 0.01D + (random.nextDouble() - 0.5D) * (double)j2 * 0.5D;
                    double d27 = (double)j2 * d16 + random.nextGaussian() * 0.01D;
                    double d29 = -0.03D + random.nextGaussian() * 0.01D;
                    double d30 = (double)k1 * d16 + random.nextGaussian() * 0.01D;
                    this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d19, d22, d25, d27, d29, d30);
                }

                return;
            case 2001:
                Block block = Block.getBlockById(data & 4095);

                if (block.getDefaultState().getMaterial() != Material.AIR)
                {
                    SoundType soundtype = block.getSoundType(Block.getStateById(data), world, blockPosIn, null);
                    this.world.playSound(blockPosIn, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
                }

                this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 255));
                break;
            case 2002:
            case 2007:
                double d9 = (double)blockPosIn.getX();
                double d10 = (double)blockPosIn.getY();
                double d12 = (double)blockPosIn.getZ();

                for (int k2 = 0; k2 < 8; ++k2)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d9, d10, d12, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, Item.getIdFromItem(Items.SPLASH_POTION));
                }

                float f5 = (float)(data >> 16 & 255) / 255.0F;
                float f = (float)(data >> 8 & 255) / 255.0F;
                float f1 = (float)(data >> 0 & 255) / 255.0F;
                EnumParticleTypes enumparticletypes = type == 2007 ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;

                for (int j3 = 0; j3 < 100; ++j3)
                {
                    double d18 = random.nextDouble() * 4.0D;
                    double d21 = random.nextDouble() * Math.PI * 2.0D;
                    double d24 = Math.cos(d21) * d18;
                    double d26 = 0.01D + random.nextDouble() * 0.5D;
                    double d28 = Math.sin(d21) * d18;
                    Particle particle1 = this.spawnParticle0(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d9 + d24 * 0.1D, d10 + 0.3D, d12 + d28 * 0.1D, d24, d26, d28);

                    if (particle1 != null)
                    {
                        float f4 = 0.75F + random.nextFloat() * 0.25F;
                        particle1.setRBGColorF(f5 * f4, f * f4, f1 * f4);
                        particle1.multiplyVelocity((float)d18);
                    }
                }

                this.world.playSound(blockPosIn, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 2003:
                double d3 = (double)blockPosIn.getX() + 0.5D;
                double d4 = (double)blockPosIn.getY();
                double d5 = (double)blockPosIn.getZ() + 0.5D;

                for (int l1 = 0; l1 < 8; ++l1)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d3, d4, d5, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, Item.getIdFromItem(Items.ENDER_EYE));
                }

                for (double d14 = 0.0D; d14 < (Math.PI * 2D); d14 += 0.15707963267948966D)
                {
                    this.spawnParticle(EnumParticleTypes.PORTAL, d3 + Math.cos(d14) * 5.0D, d4 - 0.4D, d5 + Math.sin(d14) * 5.0D, Math.cos(d14) * -5.0D, 0.0D, Math.sin(d14) * -5.0D);
                    this.spawnParticle(EnumParticleTypes.PORTAL, d3 + Math.cos(d14) * 5.0D, d4 - 0.4D, d5 + Math.sin(d14) * 5.0D, Math.cos(d14) * -7.0D, 0.0D, Math.sin(d14) * -7.0D);
                }

                return;
            case 2004:

                for (int i3 = 0; i3 < 20; ++i3)
                {
                    double d17 = (double)blockPosIn.getX() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
                    double d20 = (double)blockPosIn.getY() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
                    double d23 = (double)blockPosIn.getZ() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
                    this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d17, d20, d23, 0.0D, 0.0D, 0.0D, new int[0]);
                    this.world.spawnParticle(EnumParticleTypes.FLAME, d17, d20, d23, 0.0D, 0.0D, 0.0D, new int[0]);
                }

                return;
            case 2005:
                ItemDye.spawnBonemealParticles(this.world, blockPosIn, data);
                break;
            case 2006:

                for (int i2 = 0; i2 < 200; ++i2)
                {
                    float f2 = random.nextFloat() * 4.0F;
                    float f3 = random.nextFloat() * ((float)Math.PI * 2F);
                    double d6 = (double)(MathHelper.cos(f3) * f2);
                    double d7 = 0.01D + random.nextDouble() * 0.5D;
                    double d8 = (double)(MathHelper.sin(f3) * f2);
                    Particle particle = this.spawnParticle0(EnumParticleTypes.DRAGON_BREATH.getParticleID(), false, (double)blockPosIn.getX() + d6 * 0.1D, (double)blockPosIn.getY() + 0.3D, (double)blockPosIn.getZ() + d8 * 0.1D, d6, d7, d8);

                    if (particle != null)
                    {
                        particle.multiplyVelocity(f2);
                    }
                }

                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_FIREBALL_EPLD, SoundCategory.HOSTILE, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 3000:
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, true, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
                break;
            case 3001:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.world.rand.nextFloat() * 0.3F, false);
        }
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        if (progress >= 0 && progress < 10)
        {
            DestroyBlockProgress destroyblockprogress = this.damagedBlocks.get(Integer.valueOf(breakerId));

            if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != pos.getX() || destroyblockprogress.getPosition().getY() != pos.getY() || destroyblockprogress.getPosition().getZ() != pos.getZ())
            {
                destroyblockprogress = new DestroyBlockProgress(breakerId, pos);
                this.damagedBlocks.put(Integer.valueOf(breakerId), destroyblockprogress);
            }

            destroyblockprogress.setPartialBlockDamage(progress);
            destroyblockprogress.setCloudUpdateTick(this.cloudTickCounter);
        }
        else
        {
            this.damagedBlocks.remove(Integer.valueOf(breakerId));
        }
    }

    public boolean hasNoChunkUpdates()
    {
        return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasNoChunkUpdates();
    }

    public void setDisplayListEntitiesDirty()
    {
        this.displayListEntitiesDirty = true;
    }

    public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd)
    {
        synchronized (this.setTileEntities)
        {
            this.setTileEntities.removeAll(tileEntitiesToRemove);
            this.setTileEntities.addAll(tileEntitiesToAdd);
        }
    }

    public void alfheim$processLightUpdates()
    {
        if (this.alfheim$lightUpdatesQueue.isEmpty())
        {
            return;
        }

        do
        {
            final long longPos = this.alfheim$lightUpdatesQueue.dequeue();
            final int x = (int)(longPos << 64 - BlockPos.X_SHIFT - BlockPos.NUM_X_BITS >> 64 - BlockPos.NUM_X_BITS);
            final int y = (int)(longPos << 64 - BlockPos.Y_SHIFT - BlockPos.NUM_Y_BITS >> 64 - BlockPos.NUM_Y_BITS);
            final int z = (int)(longPos << 64 - BlockPos.NUM_Z_BITS >> 64 - BlockPos.NUM_Z_BITS);
            this.markBlocksForUpdate(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1, false);
        }
        while (!this.alfheim$lightUpdatesQueue.isEmpty());

        this.alfheim$lightUpdatesQueue.newDeduplicationSet();
    }

    public double getCloudCounter(float partialTicks)
    {
        return (double)this.cloudTickCounter + (double)partialTicks;
    }

    @SideOnly(Side.CLIENT)
    class ContainerLocalRenderInformation
    {
        final RenderChunk renderChunk;
        final EnumFacing facing;
        byte setFacing;
        final int counter;

        private ContainerLocalRenderInformation(RenderChunk renderChunkIn, @Nullable EnumFacing facingIn, int counterIn)
        {
            this.renderChunk = renderChunkIn;
            this.facing = facingIn;
            this.counter = counterIn;
        }

        public void setDirection(byte p_189561_1_, EnumFacing p_189561_2_)
        {
            this.setFacing = (byte)(this.setFacing | p_189561_1_ | 1 << p_189561_2_.ordinal());
        }

        public boolean hasDirection(EnumFacing p_189560_1_)
        {
            return (this.setFacing & 1 << p_189560_1_.ordinal()) > 0;
        }
    }
}
