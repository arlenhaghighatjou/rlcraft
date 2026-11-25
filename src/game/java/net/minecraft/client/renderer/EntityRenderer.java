package net.minecraft.client.renderer;

import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.BetterFrustum;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.DebugFramebufferView;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.EaglerDeferredConfig;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.EaglerDeferredPipeline;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.NameTagRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.ShadersRenderPassFuture;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.gui.GuiShaderConfig;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;
import net.lax1dude.eaglercraft.v1_8.vector.Matrix4f;
import net.minecraft.client.renderer.RenderGlobal.ChunkCullAdapter;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.Render;

import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.EffectPipelineFXAA;
import net.lax1dude.eaglercraft.v1_8.opengl.GameOverlayFramebuffer;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.DeferredStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.DynamicLightManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.texture.EmissiveItems;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.dynamiclights.DynamicLightsStateManager;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.Display;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.PointerInputAbstraction;
import net.lax1dude.eaglercraft.v1_8.voice.VoiceTagRenderer;
import net.optifine.Config;

@SideOnly(Side.CLIENT)
public class EntityRenderer implements IResourceManagerReloadListener
{
    private static final Logger LOGGER = LogManager.getLogger();
	private static final ResourceLocation DEFERRED_RAIN_TEXTURE = new ResourceLocation("eagler:glsl/deferred/rain.png");
    private static final ResourceLocation RAIN_TEXTURES = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation SNOW_TEXTURES = new ResourceLocation("textures/environment/snow.png");
    public static boolean anaglyphEnable;
    public static int anaglyphField;
    private final Minecraft mc;
    private final IResourceManager resourceManager;
    private final EaglercraftRandom random = new EaglercraftRandom();
    private float farPlaneDistance;
    public final ItemRenderer itemRenderer;
    private final MapItemRenderer mapItemRenderer;
    private int rendererUpdateCount;
    private Entity pointedEntity;
    private final MouseFilter mouseFilterXAxis = new MouseFilter();
    private final MouseFilter mouseFilterYAxis = new MouseFilter();
    private final float thirdPersonDistance = 4.0F;
    private float thirdPersonDistancePrev = 4.0F;
    private float smoothCamYaw;
    private float smoothCamPitch;
    private float smoothCamFilterX;
    private float smoothCamFilterY;
    private float smoothCamPartialTicks;
    private float fovModifierHand;
    private float fovModifierHandPrev;
    private float bossColorModifier;
    private float bossColorModifierPrev;
    private boolean cloudFog;
    private boolean renderHand = true;
    private boolean drawBlockOutline = true;
    private long timeWorldIcon;
    private long prevFrameTime = Minecraft.getSystemTime();
    private long renderEndNanoTime;
    private final DynamicTexture lightmapTexture;
    private final int[] lightmapColors;
    private final ResourceLocation locationLightMap;
    private boolean lightmapUpdateNeeded;
    private float torchFlickerX;
    private float torchFlickerDX;
    private int rainSoundCounter;
    private final float[] rainXCoords = new float[1024];
    private final float[] rainYCoords = new float[1024];
    private final FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
    private float fogColorRed;
    private float fogColorGreen;
    private float fogColorBlue;
    private float fogColor2;
    private float fogColor1;
    private int debugViewDirection;
    private boolean debugView;
    private double cameraZoom = 1.0D;
    private double cameraYaw;
    private double cameraPitch;
    private ItemStack itemActivationItem;
    private int itemActivationTicks;
    private float itemActivationOffX;
    private float itemActivationOffY;
    public static final int SHADER_COUNT = 0;
    private int shaderIndex;
    private boolean useShader;
    private int frameCount;
    private final GameOverlayFramebuffer overlayFramebuffer;
    public float currentProjMatrixFOV = 0.0F;
    private float eagPartialTicks = 0.0F;
    private boolean initializedOF = false;

    public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn)
    {
        this.shaderIndex = SHADER_COUNT;
        this.mc = mcIn;
        this.resourceManager = resourceManagerIn;
        this.itemRenderer = mcIn.getItemRenderer();
        this.mapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
        this.lightmapTexture = new DynamicTexture(16, 16);
        this.locationLightMap = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
        this.lightmapColors = this.lightmapTexture.getTextureData();
        this.overlayFramebuffer = new GameOverlayFramebuffer();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.matrixMode(GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.scale(0.00390625F, 0.00390625F, 0.00390625F);
        GlStateManager.translate(8.0F, 8.0F, 8.0F);
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        for (int i = 0; i < 32; ++i)
        {
            for (int j = 0; j < 32; ++j)
            {
                float f = (float)(j - 16);
                float f1 = (float)(i - 16);
                float f2 = MathHelper.sqrt(f * f + f1 * f1);
                this.rainXCoords[i << 5 | j] = -f1 / f2;
                this.rainYCoords[i << 5 | j] = f / f2;
            }
        }
    }

    public boolean isShaderActive()
    {
        return false;
    }

    public void stopUseShader()
    {
        this.shaderIndex = SHADER_COUNT;
    }

    public void switchUseShader()
    {
        this.useShader = !this.useShader;
    }

    public void loadEntityShader(@Nullable Entity entityIn)
    {
    }

    public void loadShader(ResourceLocation resourceLocationIn)
    {
        this.useShader = false;
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
    }

    public void updateRenderer()
    {
        this.updateFovModifierHand();
        this.updateTorchFlicker();
        this.fogColor2 = this.fogColor1;
        this.thirdPersonDistancePrev = 4.0F;

        if (this.mc.gameSettings.smoothCamera)
        {
            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f1);
            this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f1);
            this.smoothCamPartialTicks = 0.0F;
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
        }
        else
        {
            this.smoothCamFilterX = 0.0F;
            this.smoothCamFilterY = 0.0F;
            this.mouseFilterXAxis.reset();
            this.mouseFilterYAxis.reset();
        }

        if (this.mc.getRenderViewEntity() == null)
        {
            this.mc.setRenderViewEntity(this.mc.player);
        }

        float f3 = this.mc.world.getLightBrightness(new BlockPos(this.mc.getRenderViewEntity().getPositionEyes(1F))); // Forge: fix MC-51150
        float f4 = (float)this.mc.gameSettings.renderDistanceChunks / 32.0F;
        float f2 = f3 * (1.0F - f4) + f4;
        this.fogColor1 += (f2 - this.fogColor1) * 0.1F;
        ++this.rendererUpdateCount;
        this.itemRenderer.updateEquippedItem();
        this.addRainParticles();
        this.bossColorModifierPrev = this.bossColorModifier;

        if (this.mc.ingameGUI.getBossOverlay().shouldDarkenSky())
        {
            this.bossColorModifier += 0.05F;

            if (this.bossColorModifier > 1.0F)
            {
                this.bossColorModifier = 1.0F;
            }
        }
        else if (this.bossColorModifier > 0.0F)
        {
            this.bossColorModifier -= 0.0125F;
        }

        if (this.itemActivationTicks > 0)
        {
            --this.itemActivationTicks;

            if (this.itemActivationTicks == 0)
            {
                this.itemActivationItem = null;
            }
        }
    }

    public void updateShaderGroupSize(int width, int height)
    {
    }

    public void getMouseOver(float partialTicks)
    {
        Entity entity = this.mc.getRenderViewEntity();

        if (entity != null)
        {
            if (this.mc.world != null)
            {
                this.mc.mcProfiler.startSection("pick");
                this.mc.pointedEntity = null;
                double d0 = (double)this.mc.playerController.getBlockReachDistance();
                this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
                Vec3d vec3d = entity.getPositionEyes(partialTicks);
                boolean flag = false;
                int i = 3;
                double d1 = d0;

                if (this.mc.playerController.extendedReach())
                {
                    d1 = 6.0D;
                    d0 = d1;
                }
                else
                {
                    if (d0 > 3.0D)
                    {
                        flag = true;
                    }
                }

                if (this.mc.objectMouseOver != null)
                {
                    d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3d);
                }

                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.addVector(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
                this.pointedEntity = null;
                Vec3d vec3d3 = null;
                float f = 1.0F;
                List<Entity> list = this.mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
                {
                    public boolean apply(@Nullable Entity p_apply_1_)
                    {
                        return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
                    }
                }));
                double d2 = d1;

                for (int j = 0; j < list.size(); ++j)
                {
                    Entity entity1 = list.get(j);
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

                    if (axisalignedbb.contains(vec3d))
                    {
                        if (d2 >= 0.0D)
                        {
                            this.pointedEntity = entity1;
                            vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                            d2 = 0.0D;
                        }
                    }
                    else if (raytraceresult != null)
                    {
                        double d3 = vec3d.distanceTo(raytraceresult.hitVec);

                        if (d3 < d2 || d2 == 0.0D)
                        {
                            if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract())
                            {
                                if (d2 == 0.0D)
                                {
                                    this.pointedEntity = entity1;
                                    vec3d3 = raytraceresult.hitVec;
                                }
                            }
                            else
                            {
                                this.pointedEntity = entity1;
                                vec3d3 = raytraceresult.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (this.pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > 3.0D)
                {
                    this.pointedEntity = null;
                    this.mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, (EnumFacing)null, new BlockPos(vec3d3));
                }

                if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null))
                {
                    this.mc.objectMouseOver = new RayTraceResult(this.pointedEntity, vec3d3);

                    if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame)
                    {
                        this.mc.pointedEntity = this.pointedEntity;
                    }
                }

                this.mc.mcProfiler.endSection();
            }
        }
    }

    private void updateFovModifierHand()
    {
        float f = 1.0F;

        if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer)
        {
            AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)this.mc.getRenderViewEntity();
            f = abstractclientplayer.getFovModifier();
        }

        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand += (f - this.fovModifierHand) * 0.5F;

        if (this.fovModifierHand > 1.5F)
        {
            this.fovModifierHand = 1.5F;
        }

        if (this.fovModifierHand < 0.1F)
        {
            this.fovModifierHand = 0.1F;
        }
    }

    public float getFOVModifier(float partialTicks, boolean useFOVSetting)
    {
        if (this.debugView)
        {
            return 90.0F;
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            float f = 70.0F;

            if (useFOVSetting)
            {
                f = this.mc.isZoomKey ? this.mc.adjustedZoomValue : this.mc.gameSettings.fovSetting;
                f = f * (this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks);
            }

            if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHealth() <= 0.0F)
            {
                float f1 = (float)((EntityLivingBase)entity).deathTime + partialTicks;
                f /= (1.0F - 500.0F / (f1 + 500.0F)) * 2.0F + 1.0F;
            }

            IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);

            if (iblockstate.getMaterial() == Material.WATER)
            {
                f = f * 60.0F / 70.0F;
            }

            return net.minecraftforge.client.ForgeHooksClient.getFOVModifier(this, entity, iblockstate, partialTicks, f);
        }
    }

    private void hurtCameraEffect(float partialTicks)
    {
        if (this.mc.getRenderViewEntity() instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)this.mc.getRenderViewEntity();
            float f = (float)entitylivingbase.hurtTime - partialTicks;

            if (entitylivingbase.getHealth() <= 0.0F)
            {
                float f1 = (float)entitylivingbase.deathTime + partialTicks;
                GlStateManager.rotate(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
            }

            if (f < 0.0F)
            {
                return;
            }

            f = f / (float)entitylivingbase.maxHurtTime;
            f = MathHelper.sin(f * f * f * f * (float)Math.PI);
            float f2 = entitylivingbase.attackedAtYaw;
            GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-f * 14.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
        }
    }

    private void applyBobbing(float partialTicks)
    {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            float f1 = -(entityplayer.distanceWalkedModified + f * partialTicks);
            float f2 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTicks;
            float f3 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTicks;
            GlStateManager.translate(MathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2), 0.0F);
            GlStateManager.rotate(MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
        }
    }

    private void orientCamera(float partialTicks)
    {
        Entity entity = this.mc.getRenderViewEntity();
        float f = entity.getEyeHeight();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;

        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping())
        {
            f = (float)((double)f + 1.0D);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);

            if (!this.mc.gameSettings.debugCamEnable)
            {
                BlockPos blockpos = new BlockPos(entity);
                IBlockState iblockstate = this.mc.world.getBlockState(blockpos);
                net.minecraftforge.client.ForgeHooksClient.orientBedCamera(this.mc.world, blockpos, iblockstate, entity);

                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            }
        }
        else if (this.mc.gameSettings.thirdPersonView > 0)
        {
            double d3 = (double)(this.thirdPersonDistancePrev + (4.0F - this.thirdPersonDistancePrev) * partialTicks);

            if (this.mc.gameSettings.debugCamEnable)
            {
                GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            }
            else
            {
                float f1 = entity.rotationYaw;
                float f2 = entity.rotationPitch;

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    f2 += 180.0F;
                }

                double d4 = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d5 = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d6 = (double)(-MathHelper.sin(f2 * 0.017453292F)) * d3;

                for (int i = 0; i < 8; ++i)
                {
                    float f3 = (float)((i & 1) * 2 - 1);
                    float f4 = (float)((i >> 1 & 1) * 2 - 1);
                    float f5 = (float)((i >> 2 & 1) * 2 - 1);
                    f3 = f3 * 0.1F;
                    f4 = f4 * 0.1F;
                    f5 = f5 * 0.1F;
                    RayTraceResult raytraceresult = this.mc.world.rayTraceBlocks(new Vec3d(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3d(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));

                    if (raytraceresult != null)
                    {
                        double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));

                        if (d7 < d3)
                        {
                            d3 = d7;
                        }
                    }
                }

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
                GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        }
        else
        {
            GlStateManager.translate(0.0F, 0.0F, 0.05F);
        }

        if (!this.mc.gameSettings.debugCamEnable)
        {
            float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
            float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            float roll = 0.0F;
            if (entity instanceof EntityAnimal)
            {
                EntityAnimal entityanimal = (EntityAnimal)entity;
                yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
            }
            IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
            net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup event = new net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup(this, entity, state, partialTicks, yaw, pitch, roll);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            GlStateManager.rotate(event.getRoll(), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);
        }

        GlStateManager.translate(0.0F, -f, 0.0F);
        d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
        d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
    }

    private void setupCameraTransform(float partialTicks, int pass)
    {
        this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16 + 16);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        float f = 0.07F;

        if (this.mc.gameSettings.anaglyph)
        {
            GlStateManager.translate((float)(-(pass * 2 - 1)) * 0.07F, 0.0F, 0.0F);
        }

        if (this.cameraZoom != 1.0D)
        {
            GlStateManager.translate((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
            GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0D);
        }

        float farPlane = this.farPlaneDistance * 2.0F * MathHelper.SQRT_2;
        GlStateManager.gluPerspective(this.currentProjMatrixFOV = this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, farPlane);
        DeferredStateManager.setGBufferNearFarPlanes(0.05F, farPlane);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();

        if (this.mc.gameSettings.anaglyph)
        {
            GlStateManager.translate((float)(pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        this.hurtCameraEffect(partialTicks);

        if (this.mc.gameSettings.viewBobbing)
        {
            this.applyBobbing(partialTicks);
        }

        float f1 = this.mc.player.prevTimeInPortal + (this.mc.player.timeInPortal - this.mc.player.prevTimeInPortal) * partialTicks;

        if (f1 > 0.0F)
        {
            int i = 20;

            if (this.mc.player.isPotionActive(MobEffects.NAUSEA))
            {
                i = 7;
            }

            float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
            f2 = f2 * f2;
            GlStateManager.rotate(((float)this.rendererUpdateCount + partialTicks) * (float)i, 0.0F, 1.0F, 1.0F);
            GlStateManager.scale(1.0F / f2, 1.0F, 1.0F);
            GlStateManager.rotate(-((float)this.rendererUpdateCount + partialTicks) * (float)i, 0.0F, 1.0F, 1.0F);
        }

        this.orientCamera(partialTicks);

        if (this.debugView)
        {
            switch (this.debugViewDirection)
            {
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

    private void renderHand(float partialTicks, int pass)
    {
        if (!this.debugView)
        {
            if (DynamicLightsStateManager.isInDynamicLightsPass())
            {
                DynamicLightsStateManager.reportForwardRenderObjectPosition2(0.0F, 0.0F, 0.0F);
            }

            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            float f = 0.07F;

            if (this.mc.gameSettings.anaglyph)
            {
                GlStateManager.translate((float)(-(pass * 2 - 1)) * 0.07F, 0.0F, 0.0F);
            }

            GlStateManager.gluPerspective(this.getFOVModifier(partialTicks, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();

            if (this.mc.gameSettings.anaglyph)
            {
                GlStateManager.translate((float)(pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GlStateManager.pushMatrix();
            this.hurtCameraEffect(partialTicks);

            if (this.mc.gameSettings.viewBobbing)
            {
                this.applyBobbing(partialTicks);
            }

            boolean flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();

            if (!net.minecraftforge.client.ForgeHooksClient.renderFirstPersonHand(mc.renderGlobal, partialTicks, pass))
            if (this.mc.gameSettings.thirdPersonView == 0 && !flag && !this.mc.gameSettings.hideGUI && !this.mc.playerController.isSpectator())
            {
                this.enableLightmap();
                this.itemRenderer.renderItemInFirstPerson(partialTicks);
                this.disableLightmap();
            }

            GlStateManager.popMatrix();

            if (this.mc.gameSettings.thirdPersonView == 0 && !flag)
            {
                this.itemRenderer.renderOverlays(partialTicks);
                this.hurtCameraEffect(partialTicks);
            }

            if (this.mc.gameSettings.viewBobbing)
            {
                this.applyBobbing(partialTicks);
            }
        }
    }

    public void disableLightmap()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void enableLightmap()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void disableLightmapStatic()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void enableLightmapStatic()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private void updateTorchFlicker()
    {
        this.torchFlickerDX = (float)((double)this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.torchFlickerDX = (float)((double)this.torchFlickerDX * 0.9D);
        this.torchFlickerX += this.torchFlickerDX - this.torchFlickerX;
        this.lightmapUpdateNeeded = true;
    }

    private void updateLightmap(float partialTicks)
    {
        if (this.lightmapUpdateNeeded)
        {
            this.mc.mcProfiler.startSection("lightTex");
            World world = this.mc.world;

            if (world != null)
            {
                float f = world.getSunBrightness(1.0F);
                float f1 = f * 0.95F + 0.05F;

                for (int i = 0; i < 256; ++i)
                {
                    float f2 = world.provider.getLightBrightnessTable()[i / 16] * f1;
                    float f3 = world.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);

                    if (world.getLastLightningBolt() > 0)
                    {
                        f2 = world.provider.getLightBrightnessTable()[i / 16];
                    }

                    float f4 = f2 * (f * 0.65F + 0.35F);
                    float f5 = f2 * (f * 0.65F + 0.35F);
                    float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
                    float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
                    float f8 = f4 + f3;
                    float f9 = f5 + f6;
                    float f10 = f2 + f7;
                    f8 = f8 * 0.96F + 0.03F;
                    f9 = f9 * 0.96F + 0.03F;
                    f10 = f10 * 0.96F + 0.03F;

                    if (this.bossColorModifier > 0.0F)
                    {
                        float f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
                        f8 = f8 * (1.0F - f11) + f8 * 0.7F * f11;
                        f9 = f9 * (1.0F - f11) + f9 * 0.6F * f11;
                        f10 = f10 * (1.0F - f11) + f10 * 0.6F * f11;
                    }

                    if (world.provider.getDimensionType().getId() == 1)
                    {
                        f8 = 0.22F + f3 * 0.75F;
                        f9 = 0.28F + f6 * 0.75F;
                        f10 = 0.25F + f7 * 0.75F;
                    }

                    float[] colors = {f8, f9, f10};
                    world.provider.getLightmapColors(partialTicks, f, f2, f3, colors);
                    f8 = colors[0]; f9 = colors[1]; f10 = colors[2];

                    // Forge: fix MC-58177
                    f8 = MathHelper.clamp(f8, 0f, 1f);
                    f9 = MathHelper.clamp(f9, 0f, 1f);
                    f10 = MathHelper.clamp(f10, 0f, 1f);

                    if (this.mc.player.isPotionActive(MobEffects.NIGHT_VISION))
                    {
                        float f15 = this.getNightVisionBrightness(this.mc.player, partialTicks);
                        float f12 = 1.0F / f8;

                        if (f12 > 1.0F / f9)
                        {
                            f12 = 1.0F / f9;
                        }

                        if (f12 > 1.0F / f10)
                        {
                            f12 = 1.0F / f10;
                        }

                        f8 = f8 * (1.0F - f15) + f8 * f12 * f15;
                        f9 = f9 * (1.0F - f15) + f9 * f12 * f15;
                        f10 = f10 * (1.0F - f15) + f10 * f12 * f15;
                    }

                    if (f8 > 1.0F)
                    {
                        f8 = 1.0F;
                    }

                    if (f9 > 1.0F)
                    {
                        f9 = 1.0F;
                    }

                    if (f10 > 1.0F)
                    {
                        f10 = 1.0F;
                    }

                    float f16 = this.mc.gameSettings.gammaSetting;
                    float f17 = 1.0F - f8;
                    float f13 = 1.0F - f9;
                    float f14 = 1.0F - f10;
                    f17 = 1.0F - f17 * f17 * f17 * f17;
                    f13 = 1.0F - f13 * f13 * f13 * f13;
                    f14 = 1.0F - f14 * f14 * f14 * f14;
                    f8 = f8 * (1.0F - f16) + f17 * f16;
                    f9 = f9 * (1.0F - f16) + f13 * f16;
                    f10 = f10 * (1.0F - f16) + f14 * f16;
                    f8 = f8 * 0.96F + 0.03F;
                    f9 = f9 * 0.96F + 0.03F;
                    f10 = f10 * 0.96F + 0.03F;

                    if (f8 > 1.0F)
                    {
                        f8 = 1.0F;
                    }

                    if (f9 > 1.0F)
                    {
                        f9 = 1.0F;
                    }

                    if (f10 > 1.0F)
                    {
                        f10 = 1.0F;
                    }

                    if (f8 < 0.0F)
                    {
                        f8 = 0.0F;
                    }

                    if (f9 < 0.0F)
                    {
                        f9 = 0.0F;
                    }

                    if (f10 < 0.0F)
                    {
                        f10 = 0.0F;
                    }

                    int j = 255;
                    int k = (int)(f8 * 255.0F);
                    int l = (int)(f9 * 255.0F);
                    int i1 = (int)(f10 * 255.0F);
                    this.lightmapColors[i] = -16777216 | k << 16 | l << 8 | i1;
                }

                this.lightmapTexture.updateDynamicTexture();
                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                this.mc.getTextureManager().bindTexture(this.locationLightMap);

                if (this.mc.gameSettings.fancyGraphics || this.mc.gameSettings.ambientOcclusion > 0 || DynamicLightsStateManager.isDynamicLightsRender())
                {
                    EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                    EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                }
                else
                {
                    EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                    EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                }

                EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                this.lightmapUpdateNeeded = false;
                this.mc.mcProfiler.endSection();
            }
        }
    }

    private float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks)
    {
        int i = entitylivingbaseIn.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();
        return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - partialTicks) * (float)Math.PI * 0.2F) * 0.3F;
    }

    public void updateCameraAndRender(float partialTicks, long nanoTime)
    {
        if (!this.initializedOF)
        {
            Config.frameInitHook();
            this.initializedOF = true;
        }

        boolean flag = Display.isActive() || this.mc.gameSettings.touchscreen;

        if (!flag && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !PointerInputAbstraction.getVCursorButtonDown(1)))
        {
            if (Minecraft.getSystemTime() - this.prevFrameTime > 500L)
            {
                this.mc.displayInGameMenu();
            }
        }
        else
        {
            this.prevFrameTime = Minecraft.getSystemTime();
        }

        this.mc.mcProfiler.startSection("mouse");

        if (this.mc.inGameHasFocus && flag && this.mc.player != null)
        {
            this.mc.mouseHelper.mouseXYChange();
            this.mc.getTutorial().handleMouse(this.mc.mouseHelper);
            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;

            if (this.mc.gameSettings.keyBindZoomCamera.isKeyDown())
            {
                f *= 0.7F;
            }

            float f1 = f * f * f * 8.0F;
            float f2 = (float)this.mc.mouseHelper.deltaX * f1;
            float f3 = (float)this.mc.mouseHelper.deltaY * f1;
            int i = 1;

            if (this.mc.gameSettings.invertMouse)
            {
                i = -1;
            }

            if (this.mc.gameSettings.smoothCamera)
            {
                this.smoothCamYaw += f2;
                this.smoothCamPitch += f3;
                float f4 = partialTicks - this.smoothCamPartialTicks;
                this.smoothCamPartialTicks = partialTicks;
                f2 = this.smoothCamFilterX * f4;
                f3 = this.smoothCamFilterY * f4;
                this.mc.player.turn(f2, f3 * (float)i);
            }
            else
            {
                this.smoothCamYaw = 0.0F;
                this.smoothCamPitch = 0.0F;
                this.mc.player.turn(f2, f3 * (float)i);
            }
        }

        this.mc.mcProfiler.endSection();

        if (!this.mc.skipRenderWorld)
        {
            anaglyphEnable = this.mc.gameSettings.anaglyph;
            final ScaledResolution scaledresolution = this.mc.scaledResolution;
            int i1 = scaledresolution.getScaledWidth();
            int j1 = scaledresolution.getScaledHeight();
            final int k1 = PointerInputAbstraction.getVCursorX() * i1 / this.mc.displayWidth;
            final int l1 = j1 - PointerInputAbstraction.getVCursorY() * j1 / this.mc.displayHeight - 1;
            int i2 = this.mc.gameSettings.limitFramerate;

            if (this.mc.world != null)
            {
                this.mc.mcProfiler.startSection("level");
                int j = Math.min(Minecraft.getDebugFPS(), i2);
                j = Math.max(j, 60);
                long k = EagRuntime.nanoTime() - nanoTime;
                long l = Math.max((long)(1000000000 / j / 4) - k, 0L);
                this.renderWorld(partialTicks, EagRuntime.nanoTime() + l);
                this.mc.renderGlobal.renderEntityOutlineFramebuffer();
                this.renderEndNanoTime = EagRuntime.nanoTime();
                this.mc.mcProfiler.endStartSection("gui");

                if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null)
                {
                    GlStateManager.alphaFunc(GL_GREATER, 0.1F);
                    long framebufferAge = this.overlayFramebuffer.getAge();

                    if (framebufferAge == -1L || framebufferAge > (Minecraft.getDebugFPS() < 25 ? 125L : 75L))
                    {
                        this.overlayFramebuffer.beginRender(this.mc.displayWidth, this.mc.displayHeight);
                        GlStateManager.colorMask(true, true, true, true);
                        GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
                        GlStateManager.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                        GlStateManager.enableOverlayFramebufferBlending();
                        this.setupOverlayRendering();
                        this.mc.ingameGUI.renderCachedGameOverlay(partialTicks);
                        GlStateManager.disableOverlayFramebufferBlending();
                        this.overlayFramebuffer.endRender();
                    }

                    this.setupOverlayRendering();
                    GlStateManager.disableLighting();
                    this.mc.ingameGUI.renderWorldOverlay(partialTicks, scaledresolution);
                    this.renderItemActivation(i1, j1, partialTicks);
                    GlStateManager.bindTexture(this.overlayFramebuffer.getTexture());
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
                    GlStateManager.enableAlpha();
                    GlStateManager.disableDepth();
                    GlStateManager.depthMask(false);
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer bufferbuilder = tessellator.getBuffer();
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                    bufferbuilder.pos(0.0D, (double)j1, -90.0D).tex(0.0D, 0.0D).endVertex();
                    bufferbuilder.pos((double)i1, (double)j1, -90.0D).tex(1.0D, 0.0D).endVertex();
                    bufferbuilder.pos((double)i1, 0.0D, -90.0D).tex(1.0D, 1.0D).endVertex();
                    bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 1.0D).endVertex();
                    tessellator.draw();
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.disableBlend();

                    if (this.mc.gameSettings.hudPlayer)
                    {
                        this.mc.ingameGUI.drawEaglerPlayerOverlay(i1 - 3, 3 + this.mc.ingameGUI.overlayDebug.playerOffset, partialTicks);
                    }
                }

                this.mc.mcProfiler.endSection();
            }
            else
            {
                GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                this.setupOverlayRendering();
                this.renderEndNanoTime = EagRuntime.nanoTime();
                // Forge: Fix MC-112292
                net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance.renderEngine = this.mc.getTextureManager();
                // Forge: also fix rendering text before entering world (not part of MC-112292, but the same reason)
                net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance.fontRenderer = this.mc.fontRenderer;
            }

            this.mc.notifRenderer.renderOverlay(k1, l1);

            if (this.mc.currentScreen != null)
            {
                GlStateManager.clear(256);
                float f = 1.0F;
                final float[] ff = new float[] {1.0F};

                try
                {
                    f = this.mc.currentScreen.getEaglerScale();
                    int mx;
                    int my;

                    if (f == 1.0F)
                    {
                        mx = k1;
                        my = l1;
                    }
                    else
                    {
                        mx = GuiScreen.applyEaglerScale(f, k1, i1);
                        my = GuiScreen.applyEaglerScale(f, l1, j1);
                        GlStateManager.pushMatrix();
                        float fff = (1.0F - f) * 0.5F;
                        GlStateManager.translate(fff * (float)i1, fff * (float)j1, 0.0F);
                        GlStateManager.scale(f, f, f);
                    }

                    ff[0] = f;
                    net.minecraftforge.client.ForgeHooksClient.drawScreen(this.mc.currentScreen, mx, my, this.mc.getTickLength());

                    if (f != 1.0F)
                    {
                        GlStateManager.popMatrix();
                    }
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
                    crashreportcategory.addDetail("Screen name", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    crashreportcategory.addDetail("Mouse location", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            return HString.format("Scaled: (%d, %d). Absolute: (%d, %d)", k1, l1, PointerInputAbstraction.getVCursorX(), PointerInputAbstraction.getVCursorY());
                        }
                    });
                    crashreportcategory.addDetail("Screen size", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            return HString.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), EntityRenderer.this.mc.displayWidth, EntityRenderer.this.mc.displayHeight, scaledresolution.getScaleFactor());
                        }
                    });
                    crashreportcategory.addDetail("Eagler Scale", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            return "" + ff[0];
                        }
                    });
                    throw new ReportedException(crashreport);
                }

                this.mc.voiceOverlay.drawOverlay();
            }
        }
    }

    public void renderStreamIndicator(float partialTicks)
    {
        this.setupOverlayRendering();
    }

    private boolean isDrawBlockOutline()
    {
        if (!this.drawBlockOutline)
        {
            return false;
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            boolean flag = entity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;

            if (flag && !((EntityPlayer)entity).capabilities.allowEdit)
            {
                ItemStack itemstack = ((EntityPlayer)entity).getHeldItemMainhand();

                if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
                    Block block = this.mc.world.getBlockState(blockpos).getBlock();

                    if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR)
                    {
                        flag = block.hasTileEntity(this.mc.world.getBlockState(blockpos)) && this.mc.world.getTileEntity(blockpos) instanceof IInventory;
                    }
                    else
                    {
                        flag = !itemstack.isEmpty() && (itemstack.canDestroy(block) || itemstack.canPlaceOn(block));
                    }
                }
            }

            return flag;
        }
    }

    public void renderWorld(float partialTicks, long finishTimeNano)
    {
        this.eagPartialTicks = partialTicks;
        this.updateLightmap(partialTicks);

        if (this.mc.getRenderViewEntity() == null)
        {
            this.mc.setRenderViewEntity(this.mc.player);
        }

        this.getMouseOver(partialTicks);
		this.mc.renderGlobal.resetEntityOutlines();
		if (this.mc.gameSettings.shaders) {
			VoiceTagRenderer.clearTagsDrawnSet();
			this.mc.mcProfiler.startSection("deferred");
			try {
				this.eaglercraftShaders(partialTicks, finishTimeNano);
			} finally {
				this.mc.mcProfiler.endSection();
			}
			return;
		}
        boolean fxaa = !this.mc.gameSettings.shaders && ((this.mc.gameSettings.fxaa == 0 && this.mc.gameSettings.fancyGraphics) || this.mc.gameSettings.fxaa == 1);

        if (fxaa)
        {
            EffectPipelineFXAA.begin(this.mc.displayWidth, this.mc.displayHeight);
        }

        VoiceTagRenderer.clearTagsDrawnSet();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, 0.5F);
        this.mc.mcProfiler.startSection("center");
        boolean dlights = DynamicLightsStateManager.isDynamicLightsRender();

        if (dlights)
        {
            this.updateDynamicLightListEagler(partialTicks);
        }

        if (dlights)
        {
            GlStateManager.enableExtensionPipeline();
        }

        try
        {
            if (this.mc.gameSettings.anaglyph)
            {
                anaglyphField = 0;
                GlStateManager.colorMask(false, true, true, false);
                this.renderWorldPass(0, partialTicks, finishTimeNano);
				this.mc.renderGlobal.renderEntityOutlineFramebuffer();
                anaglyphField = 1;
                GlStateManager.colorMask(true, false, false, false);
                this.renderWorldPass(1, partialTicks, finishTimeNano);
				this.mc.renderGlobal.renderEntityOutlineFramebuffer();
                GlStateManager.colorMask(true, true, true, false);
            }
            else
            {
                this.mc.effectRenderer.acceleratedParticleRenderer = EaglercraftGPU.checkInstancingCapable() ? ParticleManager.vanillaAcceleratedParticleRenderer : null;
                this.renderWorldPass(2, partialTicks, finishTimeNano);
            }
        }
        finally
        {
            if (dlights)
            {
                GlStateManager.disableExtensionPipeline();
            }
        }

        if (fxaa)
        {
            EffectPipelineFXAA.end();
        }

        this.mc.mcProfiler.endSection();
    }

    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano)
    {
        RenderGlobal renderglobal = this.mc.renderGlobal;
        ParticleManager particlemanager = this.mc.effectRenderer;
        boolean flag = this.isDrawBlockOutline();
        GlStateManager.enableCull();
        this.mc.mcProfiler.endStartSection("clear");
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        this.updateFogColor(partialTicks);
        GlStateManager.clear(16640);
        this.mc.mcProfiler.endStartSection("camera");
        this.setupCameraTransform(partialTicks, pass);
        boolean isDynamicLights = DynamicLightsStateManager.isDynamicLightsRender();

        if (isDynamicLights)
        {
            DynamicLightsStateManager.setupInverseViewMatrix();
        }

        ActiveRenderInfo.updateRenderInfo(this.mc.getRenderViewEntity(), this.mc.gameSettings.thirdPersonView == 2); //Forge: MC-46445 Spectator mode particles and sounds computed from where you have been before
        this.mc.mcProfiler.endStartSection("frustum");
        ClippingHelperImpl.getInstance();
        this.mc.mcProfiler.endStartSection("culling");
        ICamera icamera = new Frustum();
        Entity entity = this.mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        TileEntityRendererDispatcher.staticPlayerX = d0;
        TileEntityRendererDispatcher.staticPlayerY = d1;
        TileEntityRendererDispatcher.staticPlayerZ = d2;
        icamera.setPosition(d0, d1, d2);

        if (this.mc.gameSettings.renderDistanceChunks >= 4)
        {
            this.setupFog(-1, partialTicks);
            this.mc.mcProfiler.endStartSection("sky");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 4.0F);
            GlStateManager.matrixMode(5888);
            renderglobal.renderSky(partialTicks, pass);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }

        this.setupFog(0, partialTicks);
        GlStateManager.shadeModel(7425);

        if (entity.posY + (double)entity.getEyeHeight() < 128.0D)
        {
            this.renderCloudsCheck(renderglobal, partialTicks, pass, d0, d1, d2);
        }

        this.mc.mcProfiler.endStartSection("prepareterrain");
        this.setupFog(0, partialTicks);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        this.mc.mcProfiler.endStartSection("terrain_setup");
        renderglobal.setupTerrain(entity, (double)partialTicks, icamera, this.frameCount++, this.mc.player.isSpectator());

        if (pass == 0 || pass == 2)
        {
            this.mc.mcProfiler.endStartSection("updatechunks");
            this.mc.renderGlobal.updateChunks(finishTimeNano);
        }

        this.mc.mcProfiler.endStartSection("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        renderglobal.renderBlockLayer(BlockRenderLayer.SOLID, (double)partialTicks, pass, entity);
        GlStateManager.enableAlpha();
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, this.mc.gameSettings.mipmapLevels > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
        renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double)partialTicks, pass, entity);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT, (double)partialTicks, pass, entity);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1F);

        if (!this.debugView)
        {
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("entities");
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
            renderglobal.renderEntities(entity, icamera, partialTicks);
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
            RenderHelper.disableStandardItemLighting();
            this.disableLightmap();
        }

        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();

        if (flag && this.mc.objectMouseOver != null && !entity.isInsideOfMaterial(Material.WATER))
        {
            EntityPlayer entityplayer = (EntityPlayer)entity;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");

            if (isDynamicLights)
            {
                GlStateManager.disableExtensionPipeline();
            }

            if (!net.minecraftforge.client.ForgeHooksClient.onDrawBlockHighlight(renderglobal, entityplayer, mc.objectMouseOver, 0, partialTicks))
            renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, partialTicks);
            GlStateManager.enableAlpha();

            if (isDynamicLights)
            {
                GlStateManager.enableExtensionPipeline();
            }
        }

        if (this.mc.debugRenderer.shouldRender())
        {
            this.mc.debugRenderer.renderDebug(partialTicks, finishTimeNano);
        }

        this.mc.mcProfiler.endStartSection("destroyProgress");
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_ONE, GL_ZERO);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, partialTicks);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.disableBlend();

        if (!this.debugView)
        {
            this.enableLightmap();
            this.mc.mcProfiler.endStartSection("litParticles");
            particlemanager.renderLitParticles(entity, partialTicks);
            RenderHelper.disableStandardItemLighting();
            this.setupFog(0, partialTicks);
            this.mc.mcProfiler.endStartSection("particles");

            if (isDynamicLights)
            {
                DynamicLightsStateManager.bindAcceleratedEffectRenderer(particlemanager);
                DynamicLightsStateManager.reportForwardRenderObjectPosition2(0.0F, 0.0F, 0.0F);
            }

            particlemanager.renderParticles(entity, partialTicks, 2);

            if (isDynamicLights)
            {
                particlemanager.acceleratedParticleRenderer = null;
            }

            this.disableLightmap();
        }

        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        this.mc.mcProfiler.endStartSection("weather");
        this.renderRainSnow(partialTicks);
        GlStateManager.depthMask(true);
        renderglobal.renderWorldBorder(entity, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        GlStateManager.alphaFunc(516, 0.1F);
        this.setupFog(0, partialTicks);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.shadeModel(7425);
        this.mc.mcProfiler.endStartSection("translucent");
        renderglobal.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, (double)partialTicks, pass, entity);
        if (!this.debugView) //Only render if render pass 0 happens as well.
        {
            RenderHelper.enableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("entities");
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(1);
            renderglobal.renderEntities(entity, icamera, partialTicks);
            // restore blending function changed by RenderGlobal.preRenderDamagedBlocks
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(-1);
            RenderHelper.disableStandardItemLighting();
        }
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();

        if (entity.posY + (double)entity.getEyeHeight() >= 128.0D)
        {
            this.mc.mcProfiler.endStartSection("aboveClouds");
            this.renderCloudsCheck(renderglobal, partialTicks, pass, d0, d1, d2);
        }

        this.mc.mcProfiler.endStartSection("forge_render_last");
        net.minecraftforge.client.ForgeHooksClient.dispatchRenderLast(renderglobal, partialTicks);

        this.mc.mcProfiler.endStartSection("hand");

        if (this.renderHand)
        {
            GlStateManager.clear(256);
            this.renderHand(partialTicks, pass);
        }
    }

    private void updateDynamicLightListEagler(float partialTicks)
    {
        DynamicLightsStateManager.clearRenderList();
        Entity entity = this.mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        AxisAlignedBB entityAABB = new AxisAlignedBB(d0 - 48.0D, d1 - 32.0D, d2 - 48.0D, d0 + 48.0D, d1 + 32.0D, d2 + 48.0D);
        List<Entity> entities = this.mc.world.getEntitiesWithinAABB(Entity.class, entityAABB);

        for (int i = 0, l = entities.size(); i < l; ++i)
        {
            entities.get(i).renderDynamicLightsEaglerSimple(partialTicks);
        }

        DynamicLightsStateManager.commitLightSourceBuckets(d0, d1, d2);
    }

    private void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass, double x, double y, double z)
    {
        if (this.mc.gameSettings.shouldRenderClouds() != 0)
        {
            this.mc.mcProfiler.endStartSection("clouds");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 4.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            this.setupFog(0, partialTicks);
            renderGlobalIn.renderClouds(partialTicks, pass, x, y, z);
            GlStateManager.disableFog();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }
    }

    private void addRainParticles()
    {
        float f = this.mc.world.getRainStrength(1.0F);

        if (!this.mc.gameSettings.fancyGraphics)
        {
            f /= 2.0F;
        }

        if (f != 0.0F)
        {
            this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
            Entity entity = this.mc.getRenderViewEntity();
            World world = this.mc.world;
            BlockPos blockpos = new BlockPos(entity);
            int i = 10;
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 0.0D;
            int j = 0;
            int k = (int)(100.0F * f * f);

            if (this.mc.gameSettings.particleSetting == 1)
            {
                k >>= 1;
            }
            else if (this.mc.gameSettings.particleSetting == 2)
            {
                k = 0;
            }

            for (int l = 0; l < k; ++l)
            {
                BlockPos blockpos1 = world.getPrecipitationHeight(blockpos.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10)));
                Biome biome = world.getBiome(blockpos1);
                BlockPos blockpos2 = blockpos1.down();
                IBlockState iblockstate = world.getBlockState(blockpos2);

                if (blockpos1.getY() <= blockpos.getY() + 10 && blockpos1.getY() >= blockpos.getY() - 10 && sereneseasons.season.SeasonASMHelper.shouldAddRainParticles(world, biome) && sereneseasons.season.SeasonASMHelper.getFloatTemperature(world, biome, blockpos1) >= 0.15F)
                {
                    double d3 = this.random.nextDouble();
                    double d4 = this.random.nextDouble();
                    AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(world, blockpos2);

                    if (iblockstate.getMaterial() != Material.LAVA && iblockstate.getBlock() != Blocks.MAGMA)
                    {
                        if (iblockstate.getMaterial() != Material.AIR)
                        {
                            ++j;

                            if (this.random.nextInt(j) == 0)
                            {
                                d0 = (double)blockpos2.getX() + d3;
                                d1 = (double)((float)blockpos2.getY() + 0.1F) + axisalignedbb.maxY - 1.0D;
                                d2 = (double)blockpos2.getZ() + d4;
                            }

                            this.mc.world.spawnParticle(EnumParticleTypes.WATER_DROP, (double)blockpos2.getX() + d3, (double)((float)blockpos2.getY() + 0.1F) + axisalignedbb.maxY, (double)blockpos2.getZ() + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                        }
                    }
                    else
                    {
                        this.mc.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)blockpos1.getX() + d3, (double)((float)blockpos1.getY() + 0.1F) - axisalignedbb.minY, (double)blockpos1.getZ() + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
            }

            if (j > 0 && this.random.nextInt(3) < this.rainSoundCounter++)
            {
                this.rainSoundCounter = 0;

                if (d1 > (double)(blockpos.getY() + 1) && world.getPrecipitationHeight(blockpos).getY() > MathHelper.floor((float)blockpos.getY()))
                {
                    this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
                }
                else
                {
                    this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
                }
            }
        }
    }

    protected void renderRainSnow(float partialTicks)
    {
        net.minecraftforge.client.IRenderHandler renderer = this.mc.world.provider.getWeatherRenderer();
        if (renderer != null)
        {
            renderer.render(partialTicks, this.mc.world, mc);
            return;
        }

        float f = this.mc.world.getRainStrength(partialTicks);

        if (f > 0.0F)
        {
			boolean deferred = DeferredStateManager.isInDeferredPass();
            this.enableLightmap();
            Entity entity = this.mc.getRenderViewEntity();
            World world = this.mc.world;
            int i = MathHelper.floor(entity.posX);
            int j = MathHelper.floor(entity.posY);
            int k = MathHelper.floor(entity.posZ);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer bufferbuilder = tessellator.getBuffer();
            GlStateManager.disableCull();
            EaglercraftGPU.glNormal3f(0.0F, 1.0F, 0.0F);
			if (deferred) {
				GlStateManager.enableAlpha();
				DeferredStateManager.setHDRTranslucentPassBlendFunc();
				DeferredStateManager.reportForwardRenderObjectPosition2(0.0F, 0.0F, 0.0F);
				GlStateManager.alphaFunc(GL_GREATER, 0.01F);
				GlStateManager.depthMask(false);
				GlStateManager.enableDepth();
				EaglerDeferredPipeline.instance.setForwardRenderLightFactors(0.65F,
						4.75F - MathHelper.clamp(DeferredStateManager.getSunHeight() * 8.0F - 3.0F, 0.0F, 4.0F),
						1.0F, 0.03F);
			} else {
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
				GlStateManager.alphaFunc(GL_GREATER, 0.1F);
				if (DynamicLightsStateManager.isInDynamicLightsPass()) {
					DynamicLightsStateManager.reportForwardRenderObjectPosition2(0.0F, 0.0F, 0.0F);
				}
			}
            double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
            double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
            double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
            int l = MathHelper.floor(d1);
            int i1 = deferred ? 8 : this.mc.gameSettings.fancyGraphics ? 10 : 5;

            int j1 = -1;
            float f1 = (float)this.rendererUpdateCount + partialTicks;
            bufferbuilder.setTranslation(-d0, -d1, -d2);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k1 = k - i1; k1 <= k + i1; ++k1)
            {
                for (int l1 = i - i1; l1 <= i + i1; ++l1)
                {
                    int i2 = (k1 - k + 16) * 32 + l1 - i + 16;
                    double d3 = (double)this.rainXCoords[i2] * 0.5D;
                    double d4 = (double)this.rainYCoords[i2] * 0.5D;
                    blockpos$mutableblockpos.setPos(l1, 0, k1);
                    Biome biome = world.getBiome(blockpos$mutableblockpos);

                    if (sereneseasons.season.SeasonASMHelper.shouldRenderRainSnow(world, biome))
                    {
                        int j2 = world.getPrecipitationHeight(blockpos$mutableblockpos).getY();
                        int k2 = j - i1;
                        int l2 = j + i1;

                        if (k2 < j2)
                        {
                            k2 = j2;
                        }

                        if (l2 < j2)
                        {
                            l2 = j2;
                        }

                        int i3 = j2;

                        if (j2 < l)
                        {
                            i3 = l;
                        }

                        if (k2 != l2)
                        {
                            this.random.setSeed((long)(l1 * l1 * 3121 + l1 * 45238971 ^ k1 * k1 * 418711 + k1 * 13761));
                            blockpos$mutableblockpos.setPos(l1, k2, k1);
                            float f2 = sereneseasons.season.SeasonASMHelper.getFloatTemperature(world, biome, blockpos$mutableblockpos);

                            if (world.getBiomeProvider().getTemperatureAtHeight(f2, j2) >= 0.15F)
                            {
                                if (j1 != 0)
                                {
                                    if (j1 >= 0)
                                    {
                                        tessellator.draw();
                                    }

                                    j1 = 0;
                                    this.mc.getTextureManager().bindTexture(deferred ? DEFERRED_RAIN_TEXTURE : RAIN_TEXTURES);
									if (deferred) {
										DeferredStateManager.setRoughnessConstant(0.5F);
										DeferredStateManager.setMetalnessConstant(0.05F);
										DeferredStateManager.setEmissionConstant(1.0F);
										GlStateManager.color(0.8F, 0.8F, 1.0F, 0.25F);
									}
                                    bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }

                                double d5 = -((double)(this.rendererUpdateCount + l1 * l1 * 3121 + l1 * 45238971 + k1 * k1 * 418711 + k1 * 13761 & 31) + (double)partialTicks) / 32.0D * (3.0D + this.random.nextDouble());
                                double d6 = (double)((float)l1 + 0.5F) - entity.posX;
                                double d7 = (double)((float)k1 + 0.5F) - entity.posZ;
                                float f3 = MathHelper.sqrt(d6 * d6 + d7 * d7) / (float)i1;
                                float f4 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * f;
                                blockpos$mutableblockpos.setPos(l1, i3, k1);
                                int j3 = world.getCombinedLight(blockpos$mutableblockpos, 0);
                                int k3 = j3 >> 16 & 65535;
                                int l3 = j3 & 65535;
                                bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                                bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                                bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                                bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                            }
                            else
                            {
                                if (j1 != 1)
                                {
                                    if (j1 >= 0)
                                    {
                                        tessellator.draw();
                                    }

                                    j1 = 1;
                                    this.mc.getTextureManager().bindTexture(SNOW_TEXTURES);
									if (deferred) {
										DeferredStateManager.setRoughnessConstant(0.7F);
										DeferredStateManager.setMetalnessConstant(0.05F);
										DeferredStateManager.setEmissionConstant(1.0F);
										GlStateManager.color(1.3F, 1.3F, 1.3F, 0.5F);
									}
                                    bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }

                                double d8 = (double)(-((float)(this.rendererUpdateCount & 511) + partialTicks) / 512.0F);
                                double d9 = this.random.nextDouble() + (double)f1 * 0.01D * (double)((float)this.random.nextGaussian());
                                double d10 = this.random.nextDouble() + (double)(f1 * (float)this.random.nextGaussian()) * 0.001D;
                                double d11 = (double)((float)l1 + 0.5F) - entity.posX;
                                double d12 = (double)((float)k1 + 0.5F) - entity.posZ;
                                float f6 = MathHelper.sqrt(d11 * d11 + d12 * d12) / (float)i1;
                                float f5 = ((1.0F - f6 * f6) * 0.3F + 0.5F) * f;
                                blockpos$mutableblockpos.setPos(l1, i3, k1);
                                int i4 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4;
                                int j4 = i4 >> 16 & 65535;
                                int k4 = i4 & 65535;
                                bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                                bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                                bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                                bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                            }
                        }
                    }
                }
            }

            if (j1 >= 0)
            {
                tessellator.draw();
            }

            bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
            GlStateManager.enableCull();
			if (deferred) {
				GlStateManager.disableAlpha();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				DeferredStateManager.setDefaultMaterialConstants();
				GlStateManager.depthMask(true);
				GlStateManager.disableDepth();
				EaglerDeferredPipeline.instance.setForwardRenderLightFactors(1.0F, 1.0F, 1.0F, 1.0F);
			} else {
				GlStateManager.disableBlend();
			}
            GlStateManager.alphaFunc(516, 0.1F);
            this.disableLightmap();
        }
    }

    public void setupOverlayRendering()
    {
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    private void updateFogColor(float partialTicks)
    {
        World world = this.mc.world;
        Entity entity = this.mc.getRenderViewEntity();
        float f = 0.25F + 0.75F * (float)this.mc.gameSettings.renderDistanceChunks / 32.0F;
        f = 1.0F - (float)Math.pow((double)f, 0.25D);
        Vec3d vec3d = world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
        float f1 = (float)vec3d.x;
        float f2 = (float)vec3d.y;
        float f3 = (float)vec3d.z;
        Vec3d vec3d1 = world.getFogColor(partialTicks);
        this.fogColorRed = (float)vec3d1.x;
        this.fogColorGreen = (float)vec3d1.y;
        this.fogColorBlue = (float)vec3d1.z;

        if (this.mc.gameSettings.renderDistanceChunks >= 4)
        {
            double d0 = MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) > 0.0F ? -1.0D : 1.0D;
            Vec3d vec3d2 = new Vec3d(d0, 0.0D, 0.0D);
            float f5 = (float)entity.getLook(partialTicks).dotProduct(vec3d2);

            if (f5 < 0.0F)
            {
                f5 = 0.0F;
            }

            if (f5 > 0.0F)
            {
                float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);

                if (afloat != null)
                {
                    f5 = f5 * afloat[3];
                    this.fogColorRed = this.fogColorRed * (1.0F - f5) + afloat[0] * f5;
                    this.fogColorGreen = this.fogColorGreen * (1.0F - f5) + afloat[1] * f5;
                    this.fogColorBlue = this.fogColorBlue * (1.0F - f5) + afloat[2] * f5;
                }
            }
        }

        this.fogColorRed += (f1 - this.fogColorRed) * f;
        this.fogColorGreen += (f2 - this.fogColorGreen) * f;
        this.fogColorBlue += (f3 - this.fogColorBlue) * f;
        float f8 = world.getRainStrength(partialTicks);

        if (f8 > 0.0F)
        {
            float f4 = 1.0F - f8 * 0.5F;
            float f10 = 1.0F - f8 * 0.4F;
            this.fogColorRed *= f4;
            this.fogColorGreen *= f4;
            this.fogColorBlue *= f10;
        }

        float f9 = world.getThunderStrength(partialTicks);

        if (f9 > 0.0F)
        {
            float f11 = 1.0F - f9 * 0.5F;
            this.fogColorRed *= f11;
            this.fogColorGreen *= f11;
            this.fogColorBlue *= f11;
        }

        IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);

        if (this.cloudFog)
        {
            Vec3d vec3d3 = world.getCloudColour(partialTicks);
            this.fogColorRed = (float)vec3d3.x;
            this.fogColorGreen = (float)vec3d3.y;
            this.fogColorBlue = (float)vec3d3.z;
        }
        else
        {
            //Forge Moved to Block.
            Vec3d viewport = ActiveRenderInfo.projectViewFromEntity(entity, partialTicks);
            BlockPos viewportPos = new BlockPos(viewport);
            IBlockState viewportState = this.mc.world.getBlockState(viewportPos);
            Vec3d inMaterialColor = viewportState.getBlock().getFogColor(this.mc.world, viewportPos, viewportState, entity, new Vec3d(fogColorRed, fogColorGreen, fogColorBlue), partialTicks);
            this.fogColorRed = (float)inMaterialColor.x;
            this.fogColorGreen = (float)inMaterialColor.y;
            this.fogColorBlue = (float)inMaterialColor.z;
        }

        float f13 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;
        this.fogColorRed *= f13;
        this.fogColorGreen *= f13;
        this.fogColorBlue *= f13;
        double d1 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks) * world.provider.getVoidFogYFactor();

        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.BLINDNESS))
        {
            int i = ((EntityLivingBase)entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();

            if (i < 20)
            {
                d1 *= (double)(1.0F - (float)i / 20.0F);
            }
            else
            {
                d1 = 0.0D;
            }
        }

        if (d1 < 1.0D)
        {
            if (d1 < 0.0D)
            {
                d1 = 0.0D;
            }

            d1 = d1 * d1;
            this.fogColorRed = (float)((double)this.fogColorRed * d1);
            this.fogColorGreen = (float)((double)this.fogColorGreen * d1);
            this.fogColorBlue = (float)((double)this.fogColorBlue * d1);
        }

        if (this.bossColorModifier > 0.0F)
        {
            float f14 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
            this.fogColorRed = this.fogColorRed * (1.0F - f14) + this.fogColorRed * 0.7F * f14;
            this.fogColorGreen = this.fogColorGreen * (1.0F - f14) + this.fogColorGreen * 0.6F * f14;
            this.fogColorBlue = this.fogColorBlue * (1.0F - f14) + this.fogColorBlue * 0.6F * f14;
        }

        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.NIGHT_VISION))
        {
            float f15 = this.getNightVisionBrightness((EntityLivingBase)entity, partialTicks);
            float f6 = 1.0F / this.fogColorRed;

            if (f6 > 1.0F / this.fogColorGreen)
            {
                f6 = 1.0F / this.fogColorGreen;
            }

            if (f6 > 1.0F / this.fogColorBlue)
            {
                f6 = 1.0F / this.fogColorBlue;
            }

            // Forge: fix MC-4647 and MC-10480
            if (Float.isInfinite(f6)) f6 = Math.nextAfter(f6, 0.0);

            this.fogColorRed = this.fogColorRed * (1.0F - f15) + this.fogColorRed * f6 * f15;
            this.fogColorGreen = this.fogColorGreen * (1.0F - f15) + this.fogColorGreen * f6 * f15;
            this.fogColorBlue = this.fogColorBlue * (1.0F - f15) + this.fogColorBlue * f6 * f15;
        }

        if (this.mc.gameSettings.anaglyph)
        {
            float f16 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
            float f17 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
            float f7 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
            this.fogColorRed = f16;
            this.fogColorGreen = f17;
            this.fogColorBlue = f7;
        }

        net.minecraftforge.client.event.EntityViewRenderEvent.FogColors event = new net.minecraftforge.client.event.EntityViewRenderEvent.FogColors(this, entity, iblockstate, partialTicks, this.fogColorRed, this.fogColorGreen, this.fogColorBlue);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);

        this.fogColorRed = event.getRed();
        this.fogColorGreen = event.getGreen();
        this.fogColorBlue = event.getBlue();

        GlStateManager.clearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
    }

    private void setupFog(int startCoords, float partialTicks)
    {
        Entity entity = this.mc.getRenderViewEntity();
        this.setupFogColor(false);
        EaglercraftGPU.glNormal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
        float hook = net.minecraftforge.client.ForgeHooksClient.getFogDensity(this, entity, iblockstate, partialTicks, 0.1F);
        if (hook >= 0) GlStateManager.setFogDensity(hook);
        else
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.BLINDNESS))
        {
            float f1 = 5.0F;
            int i = ((EntityLivingBase)entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();

            if (i < 20)
            {
                f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)i / 20.0F);
            }

            GlStateManager.setFog(GL_LINEAR);

            if (startCoords == -1)
            {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f1 * 0.8F);
            }
            else
            {
                GlStateManager.setFogStart(f1 * 0.25F);
                GlStateManager.setFogEnd(f1);
            }
        }
        else if (this.cloudFog)
        {
            GlStateManager.setFog(GL_EXP);
            GlStateManager.setFogDensity(0.1F);
        }
        else if (iblockstate.getMaterial() == Material.WATER)
        {
            GlStateManager.setFog(GL_EXP);

            if (entity instanceof EntityLivingBase)
            {
                if (((EntityLivingBase)entity).isPotionActive(MobEffects.WATER_BREATHING))
                {
                    GlStateManager.setFogDensity(0.01F);
                }
                else
                {
                    GlStateManager.setFogDensity(0.1F - (float)EnchantmentHelper.getRespirationModifier((EntityLivingBase)entity) * 0.03F);
                }
            }
            else
            {
                GlStateManager.setFogDensity(0.1F);
            }
        }
        else if (iblockstate.getMaterial() == Material.LAVA)
        {
            GlStateManager.setFog(GL_EXP);
            GlStateManager.setFogDensity(2.0F);
        }
        else if (startCoords != -1 && !this.mc.gameSettings.fog)
        {
            GlStateManager.setFog(GL_EXP);
            GlStateManager.setFogDensity(0.0F);
        }
        else
        {
            GlStateManager.setFogDensity(0.001F);
            float f = this.farPlaneDistance;
            GlStateManager.setFog(GL_LINEAR);

            if (startCoords == -1)
            {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f);
            }
            else
            {
                GlStateManager.setFogStart(f * 0.75F);
                GlStateManager.setFogEnd(f);
            }

            if (this.mc.world.provider.doesXZShowFog((int)entity.posX, (int)entity.posZ) || this.mc.ingameGUI.getBossOverlay().shouldCreateFog())
            {
                GlStateManager.setFogStart(f * 0.05F);
                GlStateManager.setFogEnd(Math.min(f, 192.0F) * 0.5F);
            }
            net.minecraftforge.client.ForgeHooksClient.onFogRender(this, entity, iblockstate, partialTicks, startCoords, f);
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
    }

    public void setupFogColor(boolean black)
    {
        if (black)
        {
            EaglercraftGPU.glFog(GL_FOG_COLOR, this.setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        }
        else
        {
            EaglercraftGPU.glFog(GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
        }
    }

    private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha)
    {
        this.fogColorBuffer.clear();
        this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
        this.fogColorBuffer.flip();
        return this.fogColorBuffer;
    }

    public void resetData()
    {
        this.itemActivationItem = null;
        this.mapItemRenderer.clearLoadedMaps();
    }

    public MapItemRenderer getMapItemRenderer()
    {
        return this.mapItemRenderer;
    }

    public static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        EaglercraftGPU.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        if (!isSneaking)
        {
            GlStateManager.disableDepth();
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        if (!isSneaking)
        {
            fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 553648127);
            GlStateManager.enableDepth();
        }

        GlStateManager.depthMask(true);
        fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, isSneaking ? 553648127 : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public void displayItemActivation(ItemStack stack)
    {
        this.itemActivationItem = stack;
        this.itemActivationTicks = 40;
        this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
        this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
    }

    private void renderItemActivation(int p_190563_1_, int p_190563_2_, float p_190563_3_)
    {
        if (this.itemActivationItem != null && this.itemActivationTicks > 0)
        {
            int i = 40 - this.itemActivationTicks;
            float f = ((float)i + p_190563_3_) / 40.0F;
            float f1 = f * f;
            float f2 = f * f1;
            float f3 = 10.25F * f2 * f1 + -24.95F * f1 * f1 + 25.5F * f2 + -13.8F * f1 + 4.0F * f;
            float f4 = f3 * (float)Math.PI;
            float f5 = this.itemActivationOffX * (float)(p_190563_1_ / 4);
            float f6 = this.itemActivationOffY * (float)(p_190563_2_ / 4);
            GlStateManager.enableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            GlStateManager.disableCull();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.translate((float)(p_190563_1_ / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), (float)(p_190563_2_ / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), -50.0F);
            float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
            GlStateManager.scale(f7, -f7, f7);
            GlStateManager.rotate(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
            this.mc.getRenderItem().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableCull();
            GlStateManager.disableDepth();
        }
    }

    public boolean renderHeldItemLight(EntityLivingBase entityLiving, float mag)
    {
        if (DynamicLightManager.isRenderingLights())
        {
            ItemStack itemStack = entityLiving.getHeldItemMainhand();

            if (itemStack.isEmpty())
            {
                itemStack = entityLiving.getHeldItemOffhand();
            }

            if (!itemStack.isEmpty())
            {
                float[] emission = EmissiveItems.getItemEmission(itemStack);

                if (emission != null)
                {
                    double d0 = entityLiving.prevPosX + (entityLiving.posX - entityLiving.prevPosX) * (double)this.eagPartialTicks;
                    double d1 = entityLiving.prevPosY + (entityLiving.posY - entityLiving.prevPosY) * (double)this.eagPartialTicks;
                    double d2 = entityLiving.prevPosZ + (entityLiving.posZ - entityLiving.prevPosZ) * (double)this.eagPartialTicks;
                    float yaw = entityLiving.prevRotationYaw + (entityLiving.rotationYaw - entityLiving.prevRotationYaw) * this.eagPartialTicks;
                    yaw *= 0.017453293F;
                    float s = 0.5F;
                    d0 -= (double)(MathHelper.sin(yaw) * s);
                    d2 += (double)(MathHelper.cos(yaw) * s);
                    mag *= 0.5F;
                    DynamicLightManager.renderDynamicLight("entity_" + entityLiving.getEntityId() + "_holding", d0, d1 + (double)(entityLiving.getEyeHeight() * 0.63F), d2, emission[0] * mag, emission[1] * mag, emission[2] * mag, false);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean renderItemEntityLight(Entity entity, float mag)
    {
        if (DynamicLightManager.isRenderingLights())
        {
            ItemStack itemStack = ItemStack.EMPTY;
            float offsetX = 0.0F;
            float offsetY = 0.0F;
            float offsetZ = 0.0F;

            if (entity instanceof EntityItem)
            {
                EntityItem ei = (EntityItem)entity;
                itemStack = ei.getItem();
                offsetY = MathHelper.sin(((float)ei.getAge() + this.eagPartialTicks) / 10.0F + ei.hoverStart) * 0.1F + 0.3F;
            }
            else if (entity instanceof EntityItemFrame)
            {
                itemStack = ((EntityItemFrame)entity).getDisplayedItem();
                Vec3i facingVec = ((EntityItemFrame)entity).facingDirection.getDirectionVec();
                offsetX = (float)facingVec.getX() * 0.1F;
                offsetZ = (float)facingVec.getZ() * 0.1F;
            }

            if (!itemStack.isEmpty())
            {
                float[] emission = EmissiveItems.getItemEmission(itemStack);

                if (emission != null)
                {
                    double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)this.eagPartialTicks;
                    double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)this.eagPartialTicks;
                    double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)this.eagPartialTicks;
                    DynamicLightManager.renderDynamicLight("entity_" + entity.getEntityId() + "_item", d0 + (double)offsetX, d1 + (double)offsetY, d2 + (double)offsetZ, emission[0] * mag, emission[1] * mag, emission[2] * mag, false);
                    return true;
                }
            }
        }

        return false;
    }
	private static final Vector4f tmpVec4f_1 = new Vector4f();
	private static final Matrix4f tmpMat4f_1 = new Matrix4f();
	private int shadowFrameIndex = 0;

	private double blockWaveOffsetX = 0.0;
	private double blockWaveOffsetY = 0.0;
	private double blockWaveOffsetZ = 0.0;

	private void eaglercraftShaders(float partialTicks, long finishTimeNano) {
		if ((EaglerDeferredPipeline.isSuspended || EaglerDeferredPipeline.instance == null)
				|| (mc.currentScreen != null && mc.currentScreen instanceof GuiShaderConfig)) {
			EaglerDeferredPipeline.renderSuspended();
			return;
		}
		EaglerDeferredPipeline.instance.setPartialTicks(partialTicks);
		eagPartialTicks = partialTicks;
		EaglerDeferredConfig conf = mc.gameSettings.deferredShaderConf;
		boolean flag = isDrawBlockOutline();
		GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
		setupCameraTransform(partialTicks, 2);
		EaglerDeferredPipeline.instance.loadViewMatrix();
		ActiveRenderInfo.updateRenderInfo(mc.player, mc.gameSettings.thirdPersonView == 2);
		Frustum frustum = new Frustum();
		Entity entity = mc.getRenderViewEntity();
		if (entity == null) {
			entity = mc.player;
		}
		double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		EaglerDeferredPipeline.instance.setRenderPosGlobal(d0, d1, d2);
		EaglerDeferredPipeline.instance.updateReprojectionCoordinates(d0, d1, d2);
		float eyeHeight = entity.getEyeHeight();
		frustum.setPosition(d0, d1, d2);

		float waveTimer = (float) ((EagRuntime.steadyTimeMillis() % 600000l) * 0.001);
		DeferredStateManager.setWaterWindOffset(waveTimer * 0.25f, waveTimer * 0.13f, waveTimer * 1.45f, waveTimer);

		float blockWaveDistX = (float) (d0 - blockWaveOffsetX);
		float blockWaveDistY = (float) (d1 - blockWaveOffsetY);
		float blockWaveDistZ = (float) (d2 - blockWaveOffsetZ);
		if (blockWaveDistX * blockWaveDistX + blockWaveDistY * blockWaveDistY + blockWaveDistZ * blockWaveDistZ > 128.0f
				* 128.0f) {
			blockWaveOffsetX = MathHelper.floor(d0);
			blockWaveOffsetY = MathHelper.floor(d1);
			blockWaveOffsetZ = MathHelper.floor(d2);
			blockWaveDistX = (float) (d0 - blockWaveOffsetX);
			blockWaveDistY = (float) (d1 - blockWaveOffsetY);
			blockWaveDistZ = (float) (d2 - blockWaveOffsetZ);
		}

		boolean wavingBlocks = conf.is_rendering_wavingBlocks;

		DeferredStateManager.setWavingBlockOffset(blockWaveDistX, blockWaveDistY, blockWaveDistZ);
		if (wavingBlocks) {
			DeferredStateManager.setWavingBlockParams(1.0f * waveTimer, 200.0f * waveTimer, 0.0f, 0.0f);
		}

		mc.renderGlobal.setupTerrain(entity, (double) partialTicks, frustum, frameCount++, mc.player.isSpectator());

		GlStateManager.enableCull();
		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();

		EaglerDeferredPipeline.instance.beginDrawDeferred();
		EaglerDeferredPipeline.instance.beginDrawMainGBuffer();

		EaglerDeferredPipeline.instance.beginDrawMainGBufferTerrain();

		mc.renderGlobal.updateChunks(finishTimeNano);

		mc.renderGlobal.renderBlockLayer(BlockRenderLayer.SOLID, (double) partialTicks, 2, entity);
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL_GREATER, 0.5F);
		if (wavingBlocks)
			DeferredStateManager.enableDrawWavingBlocks();
		mc.renderGlobal.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double) partialTicks, 2, entity);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		mc.renderGlobal.renderBlockLayer(BlockRenderLayer.CUTOUT, (double) partialTicks, 2, entity);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		GlStateManager.alphaFunc(GL_GREATER, 0.1F);
		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.popMatrix();
		if (wavingBlocks)
			DeferredStateManager.disableDrawWavingBlocks();

		EaglerDeferredPipeline.instance.beginDrawMainGBufferEntities();
		if (conf.is_rendering_dynamicLights) {
			DynamicLightManager.setIsRenderingLights(true);
		}

		DeferredStateManager.forwardCallbackHandler = DeferredStateManager.forwardCallbackGBuffer;
		DeferredStateManager.forwardCallbackHandler.reset();

		NameTagRenderer.doRenderNameTags = true;
		NameTagRenderer.nameTagsCount = 0;
		GlStateManager.pushMatrix();
		DeferredStateManager.setDefaultMaterialConstants();
		DeferredStateManager.startUsingEnvMap();
		net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
		try {
			mc.renderGlobal.renderEntities(entity, frustum, partialTicks);
		} finally {
			net.minecraftforge.client.ForgeHooksClient.setRenderPass(-1);
		}
		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.popMatrix();
		net.minecraft.client.particle.Particle.interpPosX = d0;
		net.minecraft.client.particle.Particle.interpPosY = d1;
		net.minecraft.client.particle.Particle.interpPosZ = d2;
		enableLightmap();
		GlStateManager.pushMatrix();
		mc.effectRenderer.renderLitParticles(entity, partialTicks);
		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		mc.effectRenderer.acceleratedParticleRenderer = EaglerDeferredPipeline.instance.gbufferEffectRenderer;
		mc.effectRenderer.renderParticles(entity, partialTicks, 1);
		mc.effectRenderer.acceleratedParticleRenderer = ParticleManager.vanillaAcceleratedParticleRenderer;
		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.popMatrix();
		DeferredStateManager.endUsingEnvMap();
		disableLightmap();
		DynamicLightManager.setIsRenderingLights(false);
		NameTagRenderer.doRenderNameTags = false;

		EaglerDeferredPipeline.instance.endDrawMainGBuffer();

		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
		float celestialAngle = mc.world.getCelestialAngle(partialTicks) * 360.0F;
		GlStateManager.rotate(DeferredStateManager.sunAngle, 0.0F, 1.0F, 0.0F);

		if (mc.world.provider.getDimension() == 0) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(celestialAngle + 90.0f, 1.0F, 0.0F, 0.0F);
			tmpVec4f_1.set(0.0f, 0.0f, 1.0f);
			GlStateManager.transform(tmpVec4f_1, tmpVec4f_1);
			tmpVec4f_1.normalise();
			DeferredStateManager.setCurrentSunAngle(tmpVec4f_1);
			if (tmpVec4f_1.y > 0.1f) {
				celestialAngle += 180.0f;
			}
			GlStateManager.popMatrix();
		} else {
			tmpVec4f_1.set(0.0f, 1.0f, 0.0f);
			DeferredStateManager.setCurrentSunAngle(tmpVec4f_1);
			celestialAngle = 270.0f;
		}

		if (conf.is_rendering_shadowsSun_clamped > 0) {
			if (conf.is_rendering_shadowsColored) {
				DeferredStateManager.forwardCallbackHandler = DeferredStateManager.forwardCallbackSun;
				DeferredStateManager.forwardCallbackHandler.reset();
			} else {
				DeferredStateManager.forwardCallbackHandler = null;
			}
			EaglerDeferredPipeline.instance.beginDrawMainShadowMap();
			++shadowFrameIndex;
			EaglerDeferredPipeline.instance.beginDrawMainShadowMapLOD(0);
			GlStateManager.enableCull();
			GlStateManager.matrixMode(GL_PROJECTION);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			int shadowMapDist = 16;
			GlStateManager.ortho(-shadowMapDist, shadowMapDist, -shadowMapDist, shadowMapDist, -64.0f, 64.0f);

			setupSunCameraTransform(celestialAngle);

			DeferredStateManager.loadShadowPassViewMatrix();
			DeferredStateManager.loadSunShadowMatrixLOD0();

			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();

			GlStateManager.matrixMode(GL_MODELVIEW);
			GlStateManager.loadIdentity();

			final AxisAlignedBB aabb = matrixToBounds(DeferredStateManager.getSunShadowMatrixLOD0(), d0, d1 + eyeHeight,
					d2);
			DeferredStateManager.setShadowMapBounds(aabb);

			final BetterFrustum shadowLOD0Frustrum = new BetterFrustum(DeferredStateManager.getSunShadowMatrixLOD0());

			ChunkCullAdapter shadowCullAdapter = (renderChunk) -> {
				if (renderChunk.shadowLOD0FrameIndex != shadowFrameIndex) {
					renderChunk.shadowLOD0FrameIndex = shadowFrameIndex;
					AxisAlignedBB aabb2 = renderChunk.boundingBox;
					if (aabb.intersects(aabb2)) {
						int shadowVisRet = shadowLOD0Frustrum.intersectAab((float) (aabb2.minX - d0),
								(float) (aabb2.minY - d1 - eyeHeight), (float) (aabb2.minZ - d2),
								(float) (aabb2.maxX - d0), (float) (aabb2.maxY - d1 - eyeHeight),
								(float) (aabb2.maxZ - d2));
						renderChunk.shadowLOD0InFrustum = shadowVisRet == BetterFrustum.INSIDE
								? RenderChunk.ShadowFrustumState.INSIDE
								: (shadowVisRet == BetterFrustum.INTERSECT ? RenderChunk.ShadowFrustumState.INTERSECT
										: RenderChunk.ShadowFrustumState.OUTSIDE);
					} else {
						renderChunk.shadowLOD0InFrustum = RenderChunk.ShadowFrustumState.OUTSIDE_BB;
						return true;
					}
				}
				return renderChunk.shadowLOD0InFrustum == RenderChunk.ShadowFrustumState.OUTSIDE;
			};

			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
			mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.SOLID, aabb, shadowCullAdapter);
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL_GREATER, 0.5F);
			if (wavingBlocks) {
				DeferredStateManager.enableDrawWavingBlocks();
				enableLightmap();
			}
			mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.CUTOUT_MIPPED, aabb, shadowCullAdapter);
			mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.CUTOUT, aabb, shadowCullAdapter);
			mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
			GlStateManager.alphaFunc(GL_GREATER, 0.1F);
			if (wavingBlocks) {
				DeferredStateManager.disableDrawWavingBlocks();
				disableLightmap();
			}

			mc.renderGlobal.renderShadowLODEntities(entity, partialTicks, (renderChunk) -> {
				return renderChunk.shadowLOD0FrameIndex == shadowFrameIndex
						&& (renderChunk.shadowLOD0InFrustum == RenderChunk.ShadowFrustumState.OUTSIDE
								|| renderChunk.shadowLOD0InFrustum == RenderChunk.ShadowFrustumState.OUTSIDE_BB);
			}, (renderChunk, renderManager, renderEntity) -> {
				boolean b;
				if (renderEntity.ignoreFrustumCheck) {
					return false;
				} else if (!renderEntity.isInRangeToRender3d(d0, d1, d2)) {
					return true;
				} else if (renderChunk.shadowLOD0FrameIndex == shadowFrameIndex
						&& ((b = renderChunk.shadowLOD0InFrustum == RenderChunk.ShadowFrustumState.OUTSIDE)
								|| renderChunk.shadowLOD0InFrustum == RenderChunk.ShadowFrustumState.INSIDE)) {
					return b;
				} else {
					AxisAlignedBB aabbEntity = renderEntity.getEntityBoundingBox();
					if (aabbEntity.hasNaN() || aabbEntity.getAverageEdgeLength() == 0.0) {
						aabbEntity = new AxisAlignedBB(d0 - 2.0, d1 - 2.0, d2 - 2.0, d0 + 2.0, d1 + 2.0, d2 + 2.0);
					}
					if (shadowLOD0Frustrum.testAab((float) (aabbEntity.minX - d0),
							(float) (aabbEntity.minY - d1 - eyeHeight), (float) (aabbEntity.minZ - d2),
							(float) (aabbEntity.maxX - d0), (float) (aabbEntity.maxY - d1 - eyeHeight),
							(float) (aabbEntity.maxZ - d2))) {
						return !renderManager.shouldRender(renderEntity, frustum, d0, d1, d2);
					} else {
						return true;
					}
				}
			});
			disableLightmap();

			if (conf.is_rendering_shadowsColored) {
				EaglerDeferredPipeline.instance.beginDrawColoredShadows();
				List<ShadersRenderPassFuture> lst = DeferredStateManager.forwardCallbackHandler.renderPassList;
				for (int i = 0, l = lst.size(); i < l; ++i) {
					lst.get(i).draw(ShadersRenderPassFuture.PassType.SHADOW);
				}
				DeferredStateManager.forwardCallbackHandler.reset();
				mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
				mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.TRANSLUCENT, aabb, shadowCullAdapter);
				mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
				if (conf.is_rendering_realisticWater) {
					GlStateManager.disableTexture2D();
					GlStateManager.color(0.173f, 0.239f, 0.957f, 0.25f);
					mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.REALISTIC_WATER, aabb,
							shadowCullAdapter);
					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
					GlStateManager.enableTexture2D();
				}
				EaglerDeferredPipeline.instance.endDrawColoredShadows();
			}
			disableLightmap();

			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			GlStateManager.disableAlpha();
			GlStateManager.matrixMode(GL_PROJECTION);
			GlStateManager.popMatrix();

			if (conf.is_rendering_shadowsSun_clamped > 1) {
				EaglerDeferredPipeline.instance.beginDrawMainShadowMapLOD(1);
				if (conf.is_rendering_shadowsColored) {
					DeferredStateManager.forwardCallbackHandler.reset();
				}

				GlStateManager.enableCull();
				GlStateManager.matrixMode(GL_PROJECTION);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				shadowMapDist = 32;
				GlStateManager.ortho(-shadowMapDist, shadowMapDist, -shadowMapDist, shadowMapDist, -64.0f, 64.0f);

				setupSunCameraTransform(celestialAngle);

				DeferredStateManager.loadShadowPassViewMatrix();
				DeferredStateManager.loadSunShadowMatrixLOD1();

				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();

				GlStateManager.matrixMode(GL_MODELVIEW);
				GlStateManager.loadIdentity();

				final AxisAlignedBB aabb2 = matrixToBounds(DeferredStateManager.getSunShadowMatrixLOD1(), d0,
						d1 + eyeHeight, d2);
				DeferredStateManager.setShadowMapBounds(aabb2);

				BetterFrustum shadowLOD1Frustrum = new BetterFrustum(DeferredStateManager.getSunShadowMatrixLOD1());

				ChunkCullAdapter shadowCullAdapter2 = (renderChunk) -> {
					if (renderChunk.shadowLOD1FrameIndex != shadowFrameIndex) {
						renderChunk.shadowLOD1FrameIndex = shadowFrameIndex;
						if (renderChunk.shadowLOD0FrameIndex == shadowFrameIndex
								&& renderChunk.shadowLOD0InFrustum == RenderChunk.ShadowFrustumState.INSIDE) {
							renderChunk.shadowLOD1InFrustum = RenderChunk.ShadowFrustumState.OUTSIDE;
							return true;
						} else {
							AxisAlignedBB aabb3 = renderChunk.boundingBox;
							if (aabb2.intersects(aabb3)) {
								int shadowVisRet = shadowLOD1Frustrum.intersectAab((float) (aabb3.minX - d0),
										(float) (aabb3.minY - d1 - eyeHeight), (float) (aabb3.minZ - d2),
										(float) (aabb3.maxX - d0), (float) (aabb3.maxY - d1 - eyeHeight),
										(float) (aabb3.maxZ - d2));
								renderChunk.shadowLOD1InFrustum = shadowVisRet == BetterFrustum.INSIDE
										? RenderChunk.ShadowFrustumState.INSIDE
										: (shadowVisRet == BetterFrustum.INTERSECT
												? RenderChunk.ShadowFrustumState.INTERSECT
												: RenderChunk.ShadowFrustumState.OUTSIDE);
							} else {
								renderChunk.shadowLOD1InFrustum = RenderChunk.ShadowFrustumState.OUTSIDE_BB;
								return true;
							}
						}
					}
					return renderChunk.shadowLOD1InFrustum == RenderChunk.ShadowFrustumState.OUTSIDE;
				};

				mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
				mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.SOLID, aabb2, shadowCullAdapter2);
				GlStateManager.enableAlpha();
				GlStateManager.alphaFunc(GL_GREATER, 0.5F);
				if (wavingBlocks) {
					DeferredStateManager.enableDrawWavingBlocks();
					enableLightmap();
				}
				mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.CUTOUT_MIPPED, aabb2, shadowCullAdapter2);
				mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.CUTOUT, aabb2, shadowCullAdapter2);
				mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
				GlStateManager.alphaFunc(GL_GREATER, 0.1F);
				if (wavingBlocks) {
					DeferredStateManager.disableDrawWavingBlocks();
					disableLightmap();
				}

				mc.renderGlobal.renderShadowLODEntities(entity, partialTicks, (renderChunk) -> {
					return renderChunk.shadowLOD1FrameIndex == shadowFrameIndex
							&& (renderChunk.shadowLOD1InFrustum == RenderChunk.ShadowFrustumState.OUTSIDE
									|| renderChunk.shadowLOD1InFrustum == RenderChunk.ShadowFrustumState.OUTSIDE_BB);
				}, (renderChunk, renderManager, renderEntity) -> {
					boolean b;
					if (renderEntity.ignoreFrustumCheck) {
						return false;
					} else if (!renderEntity.isInRangeToRender3d(d0, d1, d2)) {
						return true;
					} else if (renderChunk.shadowLOD1FrameIndex == shadowFrameIndex
							&& (b = renderChunk.shadowLOD1InFrustum == RenderChunk.ShadowFrustumState.OUTSIDE)) {
						return b;
					} else {
						AxisAlignedBB aabbEntity = renderEntity.getEntityBoundingBox();
						if (aabbEntity.hasNaN() || aabbEntity.getAverageEdgeLength() == 0.0) {
							aabbEntity = new AxisAlignedBB(d0 - 2.0, d1 - 2.0, d2 - 2.0, d0 + 2.0, d1 + 2.0, d2 + 2.0);
						}
						if (shadowLOD1Frustrum.testAab((float) (aabbEntity.minX - d0),
								(float) (aabbEntity.minY - d1 - eyeHeight), (float) (aabbEntity.minZ - d2),
								(float) (aabbEntity.maxX - d0), (float) (aabbEntity.maxY - d1 - eyeHeight),
								(float) (aabbEntity.maxZ - d2))) {
							return !renderManager.shouldRender(renderEntity, frustum, d0, d1, d2);
						} else {
							return true;
						}
					}
				});
				disableLightmap();

				if (conf.is_rendering_shadowsColored) {
					EaglerDeferredPipeline.instance.beginDrawColoredShadows();
					List<ShadersRenderPassFuture> lst = DeferredStateManager.forwardCallbackHandler.renderPassList;
					for (int i = 0, l = lst.size(); i < l; ++i) {
						lst.get(i).draw(ShadersRenderPassFuture.PassType.SHADOW);
					}
					DeferredStateManager.forwardCallbackHandler.reset();
					mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
					mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.TRANSLUCENT, aabb2, shadowCullAdapter2);
					mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
					if (conf.is_rendering_realisticWater) {
						GlStateManager.disableTexture2D();
						GlStateManager.color(0.173f, 0.239f, 0.957f, 0.25f);
						mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.REALISTIC_WATER, aabb2,
								shadowCullAdapter2);
						GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
						GlStateManager.enableTexture2D();
					}
					EaglerDeferredPipeline.instance.endDrawColoredShadows();
				}
				disableLightmap();

				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				GlStateManager.disableAlpha();
				GlStateManager.matrixMode(GL_PROJECTION);
				GlStateManager.popMatrix();

				if (conf.is_rendering_shadowsSun_clamped > 2) {
					EaglerDeferredPipeline.instance.beginDrawMainShadowMapLOD(2);

					GlStateManager.enableCull();
					GlStateManager.matrixMode(GL_PROJECTION);
					GlStateManager.pushMatrix();
					GlStateManager.loadIdentity();
					shadowMapDist = 1 << (conf.is_rendering_shadowsSun_clamped + 3);
					GlStateManager.ortho(-shadowMapDist, shadowMapDist, -shadowMapDist, shadowMapDist, -64.0f, 64.0f);

					setupSunCameraTransform(celestialAngle);

					DeferredStateManager.loadShadowPassViewMatrix();
					DeferredStateManager.loadSunShadowMatrixLOD2();

					GlStateManager.disableAlpha();
					GlStateManager.disableBlend();

					GlStateManager.matrixMode(GL_MODELVIEW);
					GlStateManager.loadIdentity();

					DeferredStateManager.loadPassViewMatrix();
					DeferredStateManager.loadPassProjectionMatrix();

					AxisAlignedBB aabb3 = matrixToBounds(DeferredStateManager.getSunShadowMatrixLOD2(), d0,
							d1 + eyeHeight, d2);
					DeferredStateManager.setShadowMapBounds(aabb3);

					BetterFrustum shadowLOD2Frustum = new BetterFrustum(DeferredStateManager.getSunShadowMatrixLOD2());

					ChunkCullAdapter shadowCullAdapter3 = (renderChunk) -> {
						if (renderChunk.shadowLOD2FrameIndex != shadowFrameIndex) {
							renderChunk.shadowLOD2FrameIndex = shadowFrameIndex;
							if (renderChunk.shadowLOD0FrameIndex == shadowFrameIndex
									&& renderChunk.shadowLOD0InFrustum == RenderChunk.ShadowFrustumState.INSIDE) {
								renderChunk.shadowLOD2InFrustum = RenderChunk.ShadowFrustumState.OUTSIDE;
								return true;
							} else if (renderChunk.shadowLOD1FrameIndex == shadowFrameIndex
									&& renderChunk.shadowLOD1InFrustum == RenderChunk.ShadowFrustumState.INSIDE) {
								renderChunk.shadowLOD2InFrustum = RenderChunk.ShadowFrustumState.OUTSIDE;
								return true;
							} else {
								AxisAlignedBB aabb4 = renderChunk.boundingBox;
								if (aabb3.intersects(aabb4)) {
									int shadowVisRet = shadowLOD2Frustum.intersectAab((float) (aabb4.minX - d0),
											(float) (aabb4.minY - d1 - eyeHeight), (float) (aabb4.minZ - d2),
											(float) (aabb4.maxX - d0), (float) (aabb4.maxY - d1 - eyeHeight),
											(float) (aabb4.maxZ - d2));
									renderChunk.shadowLOD2InFrustum = shadowVisRet == BetterFrustum.INSIDE
											? RenderChunk.ShadowFrustumState.INSIDE
											: (shadowVisRet == BetterFrustum.INTERSECT
													? RenderChunk.ShadowFrustumState.INTERSECT
													: RenderChunk.ShadowFrustumState.OUTSIDE);
								} else {
									renderChunk.shadowLOD2InFrustum = RenderChunk.ShadowFrustumState.OUTSIDE_BB;
									return true;
								}
							}
						}
						return renderChunk.shadowLOD2InFrustum == RenderChunk.ShadowFrustumState.OUTSIDE;
					};

					mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
					mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.SOLID, aabb3, shadowCullAdapter3);
					GlStateManager.enableAlpha();
					mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.CUTOUT_MIPPED, aabb3,
							shadowCullAdapter3);
					mc.renderGlobal.renderBlockLayerShadow(BlockRenderLayer.CUTOUT, aabb3, shadowCullAdapter3);
					mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
					disableLightmap();

					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
					GlStateManager.disableAlpha();
					GlStateManager.matrixMode(GL_PROJECTION);
					GlStateManager.popMatrix();
				}
			}

			EaglerDeferredPipeline.instance.endDrawMainShadowMap();
			if (conf.is_rendering_shadowsColored) {
				DeferredStateManager.forwardCallbackHandler.reset();
			}
		}

		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.popMatrix();

		if (conf.is_rendering_dynamicLights && entity != null && mc.gameSettings.thirdPersonView == 0) {
			if (entity instanceof EntityLivingBase) {
				DynamicLightManager.setIsRenderingLights(true);
				ItemStack itemStack = ((EntityLivingBase) entity).getHeldItemMainhand();
				if (itemStack != null) {
					float[] emission = EmissiveItems.getItemEmission(itemStack);
					if (emission != null) {
						float yaw = entity.prevRotationYaw
								+ (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
						yaw *= 0.017453293f;
						float s = 0.2f;
						double d02 = d0 + MathHelper.sin(yaw) * s;
						double d22 = d2 + MathHelper.cos(yaw) * s;
						float mag = 0.7f;
						DynamicLightManager.renderDynamicLight("render_view_entity_holding", d02,
								d1 + entity.getEyeHeight(), d22, emission[0] * mag, emission[1] * mag,
								emission[2] * mag, false);
					}
				}
				DynamicLightManager.setIsRenderingLights(false);
			}
		}

		EaglerDeferredPipeline.instance.combineGBuffersAndIlluminate();

		if (conf.is_rendering_useEnvMap) {
			DeferredStateManager.forwardCallbackHandler = null;
			EaglerDeferredPipeline.instance.beginDrawEnvMap();
			GlStateManager.enableCull();

			EaglerDeferredPipeline.instance.beginDrawEnvMapTop(entity.getEyeHeight());
			EaglerDeferredPipeline.instance.beginDrawEnvMapSolid();
			mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
			mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.SOLID, (double) partialTicks, 1, entity);
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL_GREATER, 0.5F);
			mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.CUTOUT, (double) partialTicks, 1, entity);
			mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double) partialTicks, 1,
					entity);
			DeferredStateManager.setDefaultMaterialConstants();
			mc.renderGlobal.renderParaboloidTileEntities(entity, partialTicks, 1);
			GlStateManager.alphaFunc(GL_GREATER, 0.1F);
			EaglerDeferredPipeline.instance.beginDrawEnvMapTranslucent();
			if (conf.is_rendering_realisticWater) {
				GlStateManager.disableTexture2D();
				DeferredStateManager.disableMaterialTexture();
				DeferredStateManager.setRoughnessConstant(0.117f);
				DeferredStateManager.setMetalnessConstant(0.067f);
				DeferredStateManager.setEmissionConstant(0.0f);
				GlStateManager.color(0.173f, 0.239f, 0.957f, 0.65f);
				mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.REALISTIC_WATER, (double) partialTicks,
						1, entity);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				GlStateManager.enableTexture2D();
				DeferredStateManager.enableMaterialTexture();
			}
			mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.TRANSLUCENT, (double) partialTicks, 1,
					entity);
			mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
			GlStateManager.disableAlpha();

			EaglerDeferredPipeline.instance.beginDrawEnvMapBottom(entity.getEyeHeight());
			EaglerDeferredPipeline.instance.beginDrawEnvMapSolid();
			mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
			mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.SOLID, (double) partialTicks, -1, entity);
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL_GREATER, 0.5F);
			mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.CUTOUT, (double) partialTicks, -1, entity);
			mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double) partialTicks, -1,
					entity);
			DeferredStateManager.setDefaultMaterialConstants();
			mc.renderGlobal.renderParaboloidTileEntities(entity, partialTicks, -1);
			GlStateManager.alphaFunc(GL_GREATER, 0.1F);
			EaglerDeferredPipeline.instance.beginDrawEnvMapTranslucent();
			if (conf.is_rendering_realisticWater) {
				GlStateManager.disableTexture2D();
				DeferredStateManager.disableMaterialTexture();
				DeferredStateManager.setRoughnessConstant(0.117f);
				DeferredStateManager.setMetalnessConstant(0.067f);
				DeferredStateManager.setEmissionConstant(0.0f);
				GlStateManager.color(0.173f, 0.239f, 0.957f, 0.65f);
				mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.REALISTIC_WATER, (double) partialTicks,
						-1, entity);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				GlStateManager.enableTexture2D();
				DeferredStateManager.enableMaterialTexture();
			}
			mc.renderGlobal.renderParaboloidBlockLayer(BlockRenderLayer.TRANSLUCENT, (double) partialTicks, -1,
					entity);
			mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
			GlStateManager.disableAlpha();

			EaglerDeferredPipeline.instance.endDrawEnvMap();
		}

		if (conf.is_rendering_realisticWater) {
			EaglerDeferredPipeline.instance.beginDrawRealisticWaterMask();
			enableLightmap();
			mc.renderGlobal.renderBlockLayer(BlockRenderLayer.REALISTIC_WATER, (double) partialTicks, 2, entity);
			disableLightmap();
			EaglerDeferredPipeline.instance.endDrawRealisticWaterMask();
		}

		int dim = mc.world.provider.getDimension();
		float ff;
		if (dim == 0) {
			ff = (this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks) * 4.8f - 2.8f;
			if (ff < 0.0f)
				ff = 0.0f;
			if (ff > 1.0f)
				ff = 1.0f;
		} else {
			ff = 1.0f;
		}

		IBlockState viewState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);
		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.BLINDNESS)) {
			float f1 = 5.0F;
			int i = ((EntityLivingBase) entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
			if (i < 20) {
				f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float) i / 20.0F);
			}
			DeferredStateManager.enableFogLinear(f1 * 0.25F, f1, false, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
					1.0f);
		} else if (viewState.getMaterial() == Material.WATER) {
			updateFogColor(partialTicks);
			ff *= 0.2f;
			ff += 0.8f;
			fogColorRed *= 0.5f;
			fogColorGreen *= 0.5f;
			fogColorBlue *= 0.5f;
			if (entity instanceof EntityLivingBase
					&& ((EntityLivingBase) entity).isPotionActive(MobEffects.WATER_BREATHING)) {
				DeferredStateManager.enableFogExp(0.01F, false, fogColorRed, fogColorGreen, fogColorBlue, 1.0f,
						fogColorRed * ff, fogColorGreen * ff, fogColorBlue * ff, 1.0f);
			} else {
				DeferredStateManager.enableFogExp(0.1F - (float) (entity instanceof EntityLivingBase ? EnchantmentHelper.getRespirationModifier((EntityLivingBase) entity) : 0) * 0.03F,
						false, fogColorRed, fogColorGreen, fogColorBlue, 1.0f, fogColorRed * ff, fogColorGreen * ff,
						fogColorBlue * ff, 1.0f);
			}
		} else if (viewState.getMaterial() == Material.LAVA) {
			updateFogColor(partialTicks);
			DeferredStateManager.enableFogExp(2.0F, false, fogColorRed, fogColorGreen, fogColorBlue, 1.0f, fogColorRed,
					fogColorGreen, fogColorBlue, 1.0f);
		} else {
			float ds = 0.0005f;
			if (mc.gameSettings.renderDistanceChunks < 6) {
				ds *= 3.0f - mc.gameSettings.renderDistanceChunks * 0.33f;
			}
			ds *= 1.5f + mc.world.getRainStrength(partialTicks) * 10.0f
					+ mc.world.getThunderStrength(partialTicks) * 5.0f;
			ds *= MathHelper.clamp(6.0f - DeferredStateManager.getSunHeight() * 17.0f, 1.0f, 3.0f);
			if (conf.is_rendering_lightShafts) {
				ds *= Math.max(2.5f - Math.abs(DeferredStateManager.getSunHeight()) * 5.0f, 2.5f);
			}
			DeferredStateManager.enableFogExp(ds, true, 1.0f, 1.0f, 1.0f, 1.0f, ff, ff, ff, 1.0f);
		}

		EaglerDeferredPipeline.instance.beginDrawHDRTranslucent();
		DeferredStateManager.setDefaultMaterialConstants();

		if (conf.is_rendering_realisticWater) {
			EaglerDeferredPipeline.instance.beginDrawRealisticWaterSurface();
			mc.renderGlobal.renderBlockLayer(BlockRenderLayer.REALISTIC_WATER, (double) partialTicks, 2, entity);
			EaglerDeferredPipeline.instance.endDrawRealisticWaterSurface();
		}

		EaglerDeferredPipeline.instance.applyGBufferFog();

		EaglerDeferredPipeline.instance.beginDrawTranslucentEntities();

		TileEntityRendererDispatcher.staticPlayerX = d0;
		TileEntityRendererDispatcher.staticPlayerY = d1;
		TileEntityRendererDispatcher.staticPlayerZ = d2;
		mc.getRenderManager().setRenderPosition(d0, d1, d2);

		for (int i = 0; i < mc.world.weatherEffects.size(); ++i) {
			Entity entity1 = (Entity) mc.world.weatherEffects.get(i);
			if (entity1.isInRangeToRender3d(d0, d1, d2)) {
				mc.getRenderManager().renderEntityStatic(entity1, partialTicks, false);
			}
		}
		net.minecraftforge.client.ForgeHooksClient.setRenderPass(1);
		try {
			mc.renderGlobal.renderEntities(entity, frustum, partialTicks);
		} finally {
			net.minecraftforge.client.ForgeHooksClient.setRenderPass(-1);
		}
		disableLightmap();

		DeferredStateManager.forwardCallbackGBuffer.sort(0.0f, 0.0f, 0.0f);
		List<ShadersRenderPassFuture> lst = DeferredStateManager.forwardCallbackGBuffer.renderPassList;
		for (int i = 0, l = lst.size(); i < l; ++i) {
			lst.get(i).draw(ShadersRenderPassFuture.PassType.MAIN);
		}
		DeferredStateManager.forwardCallbackGBuffer.reset();

		EaglerDeferredPipeline.instance.beginDrawTranslucentBlocks();
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		mc.renderGlobal.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, (double) partialTicks, 2, entity);

		EaglerDeferredPipeline.instance.beginDrawMainGBufferDestroyProgress();

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(0, GL_SRC_ALPHA, 0, 0);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		mc.renderGlobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(),
				entity, partialTicks);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

		EaglerDeferredPipeline.instance.endDrawMainGBufferDestroyProgress();

		if (mc.effectRenderer.hasParticlesInAlphaLayer()) {
			GlStateManager.pushMatrix();
			mc.effectRenderer.acceleratedParticleRenderer = EaglerDeferredPipeline.instance.forwardEffectRenderer;
			DeferredStateManager.setHDRTranslucentPassBlendFunc();
			DeferredStateManager.reportForwardRenderObjectPosition2(0.0f, 0.0f, 0.0f);
			GlStateManager.enableBlend();
			GlStateManager.depthMask(false);
			mc.effectRenderer.renderParticles(entity, partialTicks, 0);
			mc.effectRenderer.acceleratedParticleRenderer = ParticleManager.vanillaAcceleratedParticleRenderer;
			GlStateManager.matrixMode(GL_MODELVIEW);
			GlStateManager.popMatrix();
			GlStateManager.enableBlend();
			GlStateManager.depthMask(true);
		}

		if (conf.is_rendering_useEnvMap) {
			EaglerDeferredPipeline.instance.beginDrawGlassHighlights();
			mc.renderGlobal.renderBlockLayer(BlockRenderLayer.GLASS_HIGHLIGHTS, (double) partialTicks, 2, entity);
			EaglerDeferredPipeline.instance.endDrawGlassHighlights();
		}

		EaglerDeferredPipeline.instance.saveReprojData();

		renderRainSnow(partialTicks);

		GlStateManager.disableBlend();

		if (renderHand) {
			EaglerDeferredPipeline.instance.beginDrawHandOverlay();
			DeferredStateManager.reportForwardRenderObjectPosition2(0.0f, 0.0f, 0.0f);
			DeferredStateManager.forwardCallbackHandler = DeferredStateManager.forwardCallbackGBuffer;
			GlStateManager.matrixMode(GL_MODELVIEW);
			GlStateManager.pushMatrix();
			GlStateManager.matrixMode(GL_PROJECTION);
			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();
			renderHand(partialTicks, 2);
			DeferredStateManager.forwardCallbackHandler = null;
			GlStateManager.enableBlend();
			DeferredStateManager.setHDRTranslucentPassBlendFunc();
			lst = DeferredStateManager.forwardCallbackGBuffer.renderPassList;
			for (int i = 0, l = lst.size(); i < l; ++i) {
				lst.get(i).draw(ShadersRenderPassFuture.PassType.MAIN);
			}
			DeferredStateManager.forwardCallbackGBuffer.reset();
			GlStateManager.matrixMode(GL_PROJECTION);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL_MODELVIEW);
			GlStateManager.popMatrix();
			EaglerDeferredPipeline.instance.endDrawHandOverlay();
			GlStateManager.disableBlend();
			GlStateManager.disableAlpha();
		}

		EaglerDeferredPipeline.instance.endDrawHDRTranslucent();

		EaglerDeferredPipeline.instance.endDrawDeferred();
		this.setupCameraTransform(partialTicks, 2);
		this.mc.renderGlobal.prepareEntityOutlines(partialTicks);
		net.minecraftforge.client.ForgeHooksClient.dispatchRenderLast(mc.renderGlobal, partialTicks);

		GlStateManager.setActiveTexture(GL_TEXTURE1);
		this.mc.getTextureManager().bindTexture(this.locationLightMap);

		GlStateManager.setActiveTexture(GL_TEXTURE0);
		GlStateManager.matrixMode(GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL_MODELVIEW);

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);

		if (!DebugFramebufferView.debugViewShown) {
			GlStateManager.disableAlpha();
			if (isDrawBlockOutline() && this.mc.objectMouseOver != null
					&& !net.minecraftforge.client.ForgeHooksClient.onDrawBlockHighlight(mc.renderGlobal, mc.player,
							this.mc.objectMouseOver, 0, partialTicks)) {
				mc.renderGlobal.drawSelectionBox(mc.player, this.mc.objectMouseOver, 0, partialTicks);
			}
			GlStateManager.enableAlpha();
			if (NameTagRenderer.nameTagsCount > 0) {
				enableLightmap();
				Arrays.sort(NameTagRenderer.nameTagsThisFrame, 0, NameTagRenderer.nameTagsCount, (n1, n2) -> {
					return n1.dst2 < n2.dst2 ? 1 : (n1.dst2 > n2.dst2 ? -1 : 0);
				});
				for (int i = 0; i < NameTagRenderer.nameTagsCount; ++i) {
					NameTagRenderer n = NameTagRenderer.nameTagsThisFrame[i];
					int ii = n.entityIn.getBrightnessForRender();
					int j = ii % 65536;
					int k = ii / 65536;
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F,
							(float) k / 1.0F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					if (n.maxDistance == -69) {

						Render.renderNameAdapter(mc.getRenderManager().getEntityRenderObject(n.entityIn), n.entityIn,
								n.x, n.y, n.z);
					} else {
						mc.getRenderManager().getEntityRenderObject(n.entityIn).renderLivingLabel(n.entityIn, n.str,
								n.x, n.y, n.z, n.maxDistance);
					}
				}
				NameTagRenderer.nameTagsCount = 0;
			}
			disableLightmap();
			GlStateManager.disableLighting();
			mc.renderGlobal.renderWorldBorder(entity, partialTicks);
		}
	}

	private static final Matrix4f matrixToBounds_tmpMat4f_1 = new Matrix4f();
	private static final Vector4f matrixToBounds_tmpVec4f_1 = new Vector4f();
	private static final Vector4f[] matrixToBounds_tmpVec4f_array = new Vector4f[] { new Vector4f(-1, -1, -1, 1),
			new Vector4f(-1, -1, 1, 1), new Vector4f(-1, 1, -1, 1), new Vector4f(-1, 1, 1, 1),
			new Vector4f(1, -1, -1, 1), new Vector4f(1, -1, 1, 1), new Vector4f(1, 1, -1, 1),
			new Vector4f(1, 1, 1, 1) };

	private static AxisAlignedBB matrixToBounds(Matrix4f matrixIn, double x, double y, double z) {
		Matrix4f.invert(matrixIn, matrixToBounds_tmpMat4f_1);

		float minX = Integer.MAX_VALUE;
		float minY = Integer.MAX_VALUE;
		float minZ = Integer.MAX_VALUE;
		float maxX = Integer.MIN_VALUE;
		float maxY = Integer.MIN_VALUE;
		float maxZ = Integer.MIN_VALUE;
		Vector4f tmpVec = matrixToBounds_tmpVec4f_1;
		float vx, vy, vz;
		for (int i = 0; i < 8; ++i) {
			Matrix4f.transform(matrixToBounds_tmpMat4f_1, matrixToBounds_tmpVec4f_array[i], tmpVec);
			vx = tmpVec.x;
			vy = tmpVec.y;
			vz = tmpVec.z;
			if (vx < minX)
				minX = vx;
			if (vy < minY)
				minY = vy;
			if (vz < minZ)
				minZ = vz;
			if (vx > maxX)
				maxX = vx;
			if (vy > maxY)
				maxY = vy;
			if (vz > maxZ)
				maxZ = vz;
		}

		return new AxisAlignedBB(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z);
	}

	public static void setupSunCameraTransform(float celestialAngle) {
		GlStateManager.rotate(celestialAngle + 90.0f, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-DeferredStateManager.sunAngle, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
	}

}
