package net.minecraft.client;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import net.lax1dude.eaglercraft.v1_8.futures.Futures;
import net.lax1dude.eaglercraft.v1_8.futures.ListenableFuture;
import net.lax1dude.eaglercraft.v1_8.futures.ListenableFutureTask;
import com.mojang.authlib.AuthenticationService;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.Charsets;
import net.lax1dude.eaglercraft.v1_8.IOUtils;
import org.apache.commons.lang3.Validate;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import net.lax1dude.eaglercraft.v1_8.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

@SideOnly(Side.CLIENT)
public class Minecraft implements IThreadListener, ISnooperInfo
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation LOCATION_MOJANG_PNG = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean IS_RUNNING_ON_MAC = Util.getOSType() == Util.EnumOS.OSX;
    public static byte[] memoryReserve = new byte[10485760];
    private static final List<DisplayMode> MAC_DISPLAY_MODES = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
    private final File fileResourcepacks;
    private final PropertyMap twitchDetails;
    private final PropertyMap profileProperties;
    private ServerData currentServerData;
    public TextureManager renderEngine;
    private static Minecraft instance;
    private final DataFixer dataFixer;
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private final boolean enableGLErrorChecking = true;
    private boolean hasCrashed;
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;
    private boolean connectedToRealms;
    private final Timer timer = new Timer(20.0F);
    private final Snooper usageSnooper = new Snooper("client", this, MinecraftServer.getCurrentTimeMillis());
    public WorldClient world;
    public RenderGlobal renderGlobal;
    private RenderManager renderManager;
    private RenderItem renderItem;
    private ItemRenderer itemRenderer;
    public EntityPlayerSP player;
    @Nullable
    private Entity renderViewEntity;
    public Entity pointedEntity;
    public ParticleManager effectRenderer;
    private SearchTreeManager searchTreeManager = new SearchTreeManager();
    private final Session session;
    private boolean isGamePaused;
    private float renderPartialTicksPaused;
    public FontRenderer fontRenderer;
    public FontRenderer standardGalacticFontRenderer;
    @Nullable
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;
    public DebugRenderer debugRenderer;
    private int leftClickCounter;
    private final int tempDisplayWidth;
    private final int tempDisplayHeight;
    @Nullable
    private IntegratedServer integratedServer;
    public GuiIngame ingameGUI;
    public boolean skipRenderWorld;
    public RayTraceResult objectMouseOver;
    public GameSettings gameSettings;
    public CreativeSettings creativeSettings;
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    private final String launchedVersion;
    private final String versionType;
    private final Proxy proxy;
    private ISaveFormat saveLoader;
    private static int debugFPS;
    private int rightClickDelayTimer;
    private String serverName;
    private int serverPort;
    public boolean inGameHasFocus;
    long systemTime = getSystemTime();
    private int joinPlayerCounter;
    public final FrameTimer frameTimer = new FrameTimer();
    long startNanoTime = System.nanoTime();
    private final boolean jvm64bit;
    private final boolean isDemo;
    @Nullable
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;
    public final Profiler mcProfiler = new Profiler();
    private long debugCrashKeyPressTime = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final MetadataSerializer metadataSerializer_ = new MetadataSerializer();
    private final List<IResourcePack> defaultResourcePacks = Lists.<IResourcePack>newArrayList();
    public final DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private BlockColors blockColors;
    private ItemColors itemColors;
    private Framebuffer framebufferMc;
    private TextureMap textureMapBlocks;
    private SoundHandler mcSoundHandler;
    private MusicTicker mcMusicTicker;
    private ResourceLocation mojangLogo;
    private final MinecraftSessionService sessionService;
    private SkinManager skinManager;
    private final Queue < FutureTask<? >> scheduledTasks = new ArrayDeque<>();
    private final Thread mcThread = Thread.currentThread();
    private ModelManager modelManager;
    private BlockRendererDispatcher blockRenderDispatcher;
    private final GuiToast toastGui;
    volatile boolean running = true;
    public String debug = "";
    public boolean renderChunksMany = true;
    private long debugUpdateTime = getSystemTime();
    private int fpsCounter;
    private boolean actionKeyF3;
    private final Tutorial tutorial;
    long prevFrameTime = -1L;
    private String debugProfilerName = "root";

    public Minecraft(GameConfiguration gameConfig)
    {
        instance = this;
        LanguageMap.initClient();
        this.launchedVersion = gameConfig.gameInfo.version;
        this.versionType = gameConfig.gameInfo.versionType;
        this.mcDefaultResourcePack = new DefaultResourcePack();
        this.session = gameConfig.userInfo.session;
        LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
        this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
        this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
        this.displayDPI = 1.0f;
        this.tempDisplayWidth = gameConfig.displayInfo.width;
        this.tempDisplayHeight = gameConfig.displayInfo.height;
        this.fullscreen = gameConfig.displayInfo.fullscreen;
        this.jvm64bit = isJvm64bit();
        this.enableGLErrorChecking = EagRuntime.getConfiguration().isCheckGLErrors();
        String serverToJoin = EagRuntime.getConfiguration().getServerToJoin();

        if (serverToJoin != null)
        {
            ServerAddress addr = AddressResolver.resolveAddressFromURI(serverToJoin);
            this.serverName = addr.getIP();
            this.serverPort = addr.getPort();
        }

        Bootstrap.register();
        TextComponentKeybind.displaySupplierFunction = KeyBinding::getDisplayString;
        this.dataFixer = DataFixesManager.createFixer();
        this.toastGui = new GuiToast(this);
        this.tutorial = new Tutorial(this);
    }

    public void run()
    {
        this.running = true;

        try
        {
            this.init();
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
            crashreport.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
            return;
        }

        try
        {
            while (this.running)
            {
                if (!this.hasCrashed || this.crashReporter == null)
                {
                    this.runGameLoop();
                }
                else
                {
                    this.displayCrashReport(this.crashReporter);
                    return;
                }
            }
        }
        catch (ContextLostError err)
        {
            throw err;
        }
        catch (MinecraftError var12)
        {
        }
        catch (ReportedException reportedexception)
        {
            this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
            LOGGER.fatal("Reported exception thrown!", (Throwable)reportedexception);
            this.displayCrashReport(reportedexception.getCrashReport());
        }
        catch (Throwable throwable1)
        {
            CrashReport crashreport1 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
            LOGGER.fatal("Unreported exception thrown!", throwable1);
            this.displayCrashReport(crashreport1);
        }
        finally
        {
            this.shutdownMinecraftApplet();
        }
    }

    private void init() throws IOException
    {
        this.gameSettings = new GameSettings(this);
        this.creativeSettings = new CreativeSettings(this);
        Config.setGameObj(this);
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0)
        {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        LOGGER.info("EagRuntime Version: {}", (Object)EagRuntime.getVersion());
        this.createDisplay();
        this.registerMetadataSerializers();
        EaglerFolderResourcePack.deleteOldResourcePacks(EaglerFolderResourcePack.SERVER_RESOURCE_PACKS, 604800000L);
        this.mcResourcePackRepository = new ResourcePackRepository(this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.scaledResolution = new ScaledResolution(this);
        net.minecraftforge.fml.client.FMLClientHandler.instance().beginMinecraftLoading(this, this.defaultResourcePacks, this.mcResourceManager, this.metadataSerializer_);
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.drawSplashScreen(this.renderEngine);
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(this);
        this.fontRenderer = EaglerFontRenderer.createSupportedFontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

        if (this.gameSettings.language != null)
        {
            this.fontRenderer.setUnicodeFlag(this.isUnicode());
            this.fontRenderer.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = EaglerFontRenderer.createSupportedFontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRenderer);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        this.mcResourceManager.registerReloadListener(new ShaderPackInfoReloadListener());
        this.mcResourceManager.registerReloadListener(PBRTextureMapUtils.blockMaterialConstants);
        this.mcResourceManager.registerReloadListener(new TemperaturesLUT());
        this.mcResourceManager.registerReloadListener(new MetalsLUT());
        this.mcResourceManager.registerReloadListener(new EmissiveItems());
        this.mcResourceManager.registerReloadListener(new BlockVertexIDs());
        this.mcResourceManager.registerReloadListener(new EaglerMeshLoader());
        this.mouseHelper = new MouseHelper();
        net.minecraftforge.fml.common.ProgressManager.ProgressBar bar= net.minecraftforge.fml.common.ProgressManager.push("Rendering Setup", 5, true);
        bar.step("GL Setup");
        this.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL_SMOOTH);
        GlStateManager.clearDepth(1.0f);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL_LEQUAL);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, 0.1F);
        GlStateManager.cullFace(GL_BACK);
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL_MODELVIEW);
        this.checkGLError("Startup");
        bar.step("Loading Texture Map");
        this.textureMapBlocks = new TextureMap("textures");
        this.textureMapBlocks.setEnablePBREagler(this.gameSettings.shaders);
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.renderEngine.loadTickableTexture(TextureMap.LOCATION_BLOCKS_TEXTURE, this.textureMapBlocks);
        this.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
        bar.step("Loading Model Manager");
        this.modelManager = new ModelManager(this.textureMapBlocks);
        this.mcResourceManager.registerReloadListener(this.modelManager);
        this.blockColors = BlockColors.init();
        this.itemColors = ItemColors.init(this.blockColors);
        bar.step("Loading Item Renderer");
        this.renderItem = new RenderItem(this.renderEngine, this.modelManager, this.itemColors);
        this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
        this.itemRenderer = new ItemRenderer(this);
        this.mcResourceManager.registerReloadListener(this.renderItem);
        bar.step("Loading Entity Renderer");
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.blockColors);
        this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
        this.renderGlobal = new RenderGlobal(this);
        this.mcResourceManager.registerReloadListener(this.renderGlobal);
        this.populateSearchTreeManager();
        this.mcResourceManager.registerReloadListener(this.searchTreeManager);
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new ParticleManager(this.world, this.renderEngine);
        SkinPreviewRenderer.initialize();
        net.minecraftforge.fml.common.ProgressManager.pop(bar);
        net.minecraftforge.fml.client.FMLClientHandler.instance().finishMinecraftLoading();
        this.checkGLError("Post startup");
        this.ingameGUI = new net.minecraftforge.client.GuiIngameForge(this);
        this.mouseGrabSupported = Mouse.isMouseGrabSupported();
        PointerInputAbstraction.init(this);
        this.eagskullCommand = new SkullCommand(this);
        this.voiceOverlay = new GuiVoiceOverlay(this);
        this.voiceOverlay.setResolution(this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight());
        this.notifRenderer = new ServerNotificationRenderer();
        this.notifRenderer.init();
        this.notifRenderer.setResolution(this, this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight(), this.scaledResolution.getScaleFactor());
        this.touchOverlayRenderer = new TouchOverlayRenderer(this);
        ServerList.initServerList(this);
        EaglerProfile.read();
        ServerCookieDataStore.load();

        GuiScreen mainMenu = new GuiMainMenu();

        if (this.serverName != null)
        {
            mainMenu = new GuiConnecting(mainMenu, this, this.serverName, this.serverPort);
        }

        mainMenu = new GuiScreenEditProfile(mainMenu);

        if (!EagRuntime.getConfiguration().isForceProfanityFilter() && !this.gameSettings.hasShownProfanityFilter)
        {
            mainMenu = new GuiScreenContentWarning(mainMenu);
        }

        boolean vsyncScreen = false;

        if (EagRuntime.getConfiguration().isEnforceVSync() && Display.isVSyncSupported() && !this.gameSettings.enableVsync)
        {
            this.gameSettings.enableVsync = true;
            this.gameSettings.saveOptions();
            vsyncScreen = true;
        }

        int vidIssues = this.gameSettings.checkBadVideoSettings();

        if (vidIssues != 0)
        {
            mainMenu = new GuiScreenVideoSettingsWarning(mainMenu, vidIssues);
        }

        if (vsyncScreen)
        {
            mainMenu = new GuiScreenVSyncReEnabled(mainMenu);
        }

        this.displayGuiScreen(mainMenu);
        this.renderEngine.deleteTexture(this.mojangLogo);
        this.mojangLogo = null;
        this.loadingScreen = new LoadingScreenRenderer(this);
        this.debugRenderer = new DebugRenderer(this);
        net.minecraftforge.fml.client.FMLClientHandler.instance().onInitializationComplete();

        while (Mouse.next())
        {
            ;
        }

        while (Keyboard.next())
        {
            ;
        }

        while (Touch.next())
        {
            ;
        }
    }

    public void populateSearchTreeManager()
    {
        SearchTree<ItemStack> searchtree = new SearchTree<ItemStack>((p_193988_0_) ->
        {
            return (List)p_193988_0_.getTooltip((EntityPlayer)null, ITooltipFlag.TooltipFlags.NORMAL).stream().map(TextFormatting::getTextWithoutFormattingCodes).map(String::trim).filter((p_193984_0_) -> {
                return !p_193984_0_.isEmpty();
            }).collect(Collectors.toList());
        }, (p_193985_0_) ->
        {
            return Collections.singleton(Item.REGISTRY.getNameForObject(p_193985_0_.getItem()));
        });
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>create();

        for (Item item : Item.REGISTRY)
        {
            item.getSubItems(CreativeTabs.SEARCH, nonnulllist);
        }

        nonnulllist.forEach(searchtree::add);
        SearchTree<RecipeList> searchtree1 = new SearchTree<RecipeList>((p_193990_0_) ->
        {
            return (List)p_193990_0_.getRecipes().stream().flatMap((p_193993_0_) -> {
                return p_193993_0_.getRecipeOutput().getTooltip((EntityPlayer)null, ITooltipFlag.TooltipFlags.NORMAL).stream();
            }).map(TextFormatting::getTextWithoutFormattingCodes).map(String::trim).filter((p_193994_0_) -> {
                return !p_193994_0_.isEmpty();
            }).collect(Collectors.toList());
        }, (p_193991_0_) ->
        {
            return (List)p_193991_0_.getRecipes().stream().map((p_193992_0_) -> {
                return Item.REGISTRY.getNameForObject(p_193992_0_.getRecipeOutput().getItem());
            }).collect(Collectors.toList());
        });
        RecipeBookClient.ALL_RECIPES.forEach(searchtree1::add);
        this.searchTreeManager.register(SearchTreeManager.ITEMS, searchtree);
        this.searchTreeManager.register(SearchTreeManager.RECIPES, searchtree1);
    }

    private void registerMetadataSerializers()
    {
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
    }

    private void createDisplay()
    {
        Display.create();
        Display.setTitle("Eaglercraft 1.12.2");
    }

    private static boolean isJvm64bit()
    {
        return true;
    }

    public Framebuffer getFramebuffer()
    {
        return this.framebufferMc;
    }

    public String getVersion()
    {
        return this.launchedVersion;
    }

    public String getVersionType()
    {
        return this.versionType;
    }

    public void crashed(CrashReport crash)
    {
        this.hasCrashed = true;
        this.crashReporter = crash;
    }

    public void displayCrashReport(CrashReport crashReportIn)
    {
        String report = crashReportIn.getCompleteReport();
        Bootstrap.printToSYSOUT(report);
        PlatformRuntime.writeCrashReport(report);

        if (PlatformRuntime.getPlatformType() == EnumPlatformType.JAVASCRIPT)
        {
            System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            System.err.println("NATIVE BROWSER EXCEPTION:");

            if (!PlatformRuntime.printJSExceptionIfBrowser(crashReportIn.getCrashCause()))
            {
                System.err.println("<undefined>");
            }

            System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        }
    }

    public boolean isUnicode()
    {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }

    @Deprecated // Forge: Use selective refreshResources method in FMLClientHandler
    public void refreshResources()
    {
        GlStateManager.recompileShaders();
        List<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);

        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries())
        {
            list.add(resourcepackrepository$entry.getResourcePack());
        }

        if (this.mcResourcePackRepository.getServerResourcePack() != null)
        {
            list.add(this.mcResourcePackRepository.getServerResourcePack());
        }

        try
        {
            this.mcResourceManager.reloadResources(list);
        }
        catch (RuntimeException runtimeexception)
        {
            LOGGER.info("Caught error stitching, removing all assigned resourcepacks", (Throwable)runtimeexception);
            list.clear();
            list.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.setRepositories(Collections.emptyList());
            this.mcResourceManager.reloadResources(list);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.incompatibleResourcePacks.clear();
            this.gameSettings.saveOptions();
        }

        ShaderSource.clearCache();
        GuiMainMenu.doResourceReloadHack();
        this.mcLanguageManager.parseLanguageMetadata(list);

        if (this.renderGlobal != null)
        {
            this.renderGlobal.loadRenderers();
        }
    }

    private void updateDisplayMode()
    {
        this.displayWidth = Display.getWidth();
        this.displayHeight = Display.getHeight();
        this.displayDPI = Display.getDPI();
        this.scaledResolution = new ScaledResolution(this);
    }

    public void drawSplashScreen(TextureManager textureManagerInstance)
    {
        Display.update();
        this.updateDisplayMode();
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double)this.scaledResolution.getScaledWidth(), (double)this.scaledResolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        try (InputStream inputstream = this.mcDefaultResourcePack.getInputStream(LOCATION_MOJANG_PNG))
        {
            this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo", new DynamicTexture(ImageData.loadImageFile(inputstream)));
            textureManagerInstance.bindTexture(this.mojangLogo);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Unable to load logo: {}", LOCATION_MOJANG_PNG, ioexception);
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double)this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos((double)this.displayWidth, (double)this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos((double)this.displayWidth, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.draw((this.scaledResolution.getScaledWidth() - 256) / 2, (this.scaledResolution.getScaledHeight() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, 0.1F);
        this.updateDisplay();
    }

    public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha)
    {
        WorldRenderer bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        bufferbuilder.pos((double)posX, (double)(posY + height), 0.0D).tex((double)((float)texU * 0.00390625F), (double)((float)(texV + height) * 0.00390625F)).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)(posX + width), (double)(posY + height), 0.0D).tex((double)((float)(texU + width) * 0.00390625F), (double)((float)(texV + height) * 0.00390625F)).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)(posX + width), (double)posY, 0.0D).tex((double)((float)(texU + width) * 0.00390625F), (double)((float)texV * 0.00390625F)).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)posX, (double)posY, 0.0D).tex((double)((float)texU * 0.00390625F), (double)((float)texV * 0.00390625F)).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    public void displayGuiScreen(@Nullable GuiScreen guiScreenIn)
    {
        if (guiScreenIn == null && this.world == null)
        {
            guiScreenIn = new GuiMainMenu();
        }
        else if (guiScreenIn == null && this.player.getHealth() <= 0.0F)
        {
            guiScreenIn = new GuiGameOver((ITextComponent)null);
        }

        GuiScreen old = this.currentScreen;
        net.minecraftforge.client.event.GuiOpenEvent event = new net.minecraftforge.client.event.GuiOpenEvent(guiScreenIn);

        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;

        guiScreenIn = event.getGui();
        if (old != null && guiScreenIn != old)
        {
            old.onGuiClosed();
        }

        if (guiScreenIn instanceof GuiMainMenu || guiScreenIn instanceof GuiMultiplayer)
        {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages(true);
        }

        this.currentScreen = guiScreenIn;
        this.scaledResolution = new ScaledResolution(this);

        if (guiScreenIn != null)
        {
            this.setIngameNotInFocus();
            KeyBinding.unPressAllKeys();

            while (Mouse.next())
            {
                ;
            }

            while (Keyboard.next())
            {
                ;
            }

            guiScreenIn.setWorldAndResolution(this, this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight());
            this.skipRenderWorld = false;
        }
        else
        {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }

        EagRuntime.getConfiguration().getHooks().callScreenChangedHook(this.currentScreen != null ? this.currentScreen.getClass().getName() : null, this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight(), this.displayWidth, this.displayHeight, this.scaledResolution.getScaleFactor());
    }

    public void shutdownIntegratedServer(GuiScreen cont)
    {
        if (SingleplayerServerController.shutdownEaglercraftServer() || SingleplayerServerController.getStatusState() == IntegratedServerState.WORLD_UNLOADING)
        {
            this.displayGuiScreen(new GuiScreenIntegratedServerBusy(cont, "singleplayer.busy.stoppingIntegratedServer", "singleplayer.failed.stoppingIntegratedServer", SingleplayerServerController::isReady));
        }
        else
        {
            this.displayGuiScreen(cont);
        }
    }

    public void checkGLError(String message)
    {
        if (this.enableGLErrorChecking)
        {
            int i = EaglercraftGPU.glGetError();

            if (i != 0)
            {
                String s = EaglercraftGPU.gluErrorString(i);
                LOGGER.error("########## GL ERROR ##########");
                LOGGER.error("@ {}", (Object)message);
                LOGGER.error("{}: {}", Integer.valueOf(i), s);
            }
        }
    }

    public void shutdownMinecraftApplet()
    {
        try
        {
            LOGGER.info("Stopping!");

            try
            {
                this.loadWorld((WorldClient)null);
            }
            catch (Throwable var5)
            {
                ;
            }

            this.mcSoundHandler.unloadSounds();

            if (SingleplayerServerController.isWorldRunning())
            {
                SingleplayerServerController.shutdownEaglercraftServer();

                while (SingleplayerServerController.getStatusState() == IntegratedServerState.WORLD_UNLOADING)
                {
                    EagUtils.sleep(50);
                    SingleplayerServerController.runTick();
                }
            }

            if (SingleplayerServerController.isIntegratedServerWorkerAlive() && SingleplayerServerController.canKillWorker())
            {
                SingleplayerServerController.killWorker();
                EagUtils.sleep(50);
            }
        }
        finally
        {
            EagRuntime.destroy();

            if (!this.hasCrashed)
            {
                EagRuntime.exit();
            }
        }
    }

    private void runGameLoop() throws IOException
    {
        long i = EagRuntime.nanoTime();

        if (Display.isCloseRequested())
        {
            this.shutdown();
        }

        Display.checkContextLost();
        PointerInputAbstraction.runGameLoop();
        this.gameSettings.touchscreen = PointerInputAbstraction.isTouchMode();

        if (this.isGamePaused && this.world != null)
        {
            float f = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = f;
        }
        else
        {
            this.timer.updateTimer();
        }

        synchronized (this.scheduledTasks)
        {
            while (!this.scheduledTasks.isEmpty())
            {
                Util.runTask(this.scheduledTasks.poll(), LOGGER);
            }
        }

        long l = EagRuntime.nanoTime();

        for (int j = 0; j < this.timer.elapsedTicks; ++j)
        {
            this.runTick();

            if (j < this.timer.elapsedTicks - 1)
            {
                PointerInputAbstraction.runGameLoop();
            }
        }

        long i1 = EagRuntime.nanoTime() - l;
        this.checkGLError("Pre render");
        this.mcSoundHandler.setListener(this.getRenderViewEntity(), this.timer.renderPartialTicks);
        EaglercraftGPU.optimize();
        _wglBindFramebuffer(0x8D40, null);
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();

        if (this.player != null && this.player.isEntityInsideOpaqueBlock())
        {
            this.gameSettings.thirdPersonView = 0;
        }

        if (!this.skipRenderWorld)
        {
            net.minecraftforge.fml.common.FMLCommonHandler.instance().onRenderTickStart(this.timer.renderPartialTicks);
            this.entityRenderer.updateCameraAndRender(this.isGamePaused ? this.renderPartialTicksPaused : this.timer.renderPartialTicks, i);
            this.toastGui.drawToast(this.scaledResolution);
            net.minecraftforge.fml.common.FMLCommonHandler.instance().onRenderTickEnd(this.timer.renderPartialTicks);
        }

        this.touchOverlayRenderer.render(this.displayWidth, this.displayHeight, this.scaledResolution);
        GlStateManager.popMatrix();
        this.updateDisplay();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        long k = EagRuntime.nanoTime();
        this.frameTimer.addFrame(k - this.startNanoTime);
        this.startNanoTime = k;

        while (getSystemTime() >= this.debugUpdateTime + 1000L)
        {
            debugFPS = this.fpsCounter;
            this.debug = HString.format("%d fps (%d chunk update%s) T: %s%s%s%s", debugFPS, RenderChunk.renderChunksUpdated, RenderChunk.renderChunksUpdated == 1 ? "" : "s", (float)this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : this.gameSettings.limitFramerate, this.gameSettings.enableVsync ? " vsync" : "", this.gameSettings.fancyGraphics ? "" : " fast", this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"));
            RenderChunk.renderChunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
        }

        Mouse.tickCursorShape();
    }

    public void updateDisplay()
    {
        if (Display.isVSyncSupported())
        {
            Display.setVSync(this.gameSettings.enableVsync);
        }
        else
        {
            this.gameSettings.enableVsync = false;
        }

        if (!this.gameSettings.enableVsync && this.isFramerateLimitBelowMax())
        {
            Display.update(this.getLimitFramerate());
        }
        else
        {
            Display.update(0);
        }

        this.checkWindowResize();
    }

    protected void checkWindowResize()
    {
        float dpiFetch = -1.0f;

        if (!this.fullscreen && (Display.wasResized() || (dpiFetch = Math.max(Display.getDPI(), 1.0f)) != this.displayDPI))
        {
            int i = this.displayWidth;
            int j = this.displayHeight;
            float f = this.displayDPI;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();
            this.displayDPI = dpiFetch == -1.0f ? Math.max(Display.getDPI(), 1.0f) : dpiFetch;

            if (this.displayWidth != i || this.displayHeight != j || this.displayDPI != f)
            {
                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate()
    {
        return this.world == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax()
    {
        return (float)this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void shutdown()
    {
        this.running = false;
    }

    public void setIngameFocus()
    {
        boolean touch = PointerInputAbstraction.isTouchMode();

        if (touch || Display.isActive())
        {
            if (!this.inGameHasFocus)
            {
                KeyBinding.updateKeyBindState();
                this.inGameHasFocus = true;

                if (!touch && this.mouseGrabSupported)
                {
                    this.mouseHelper.grabMouseCursor();
                }

                this.displayGuiScreen((GuiScreen)null);
                this.leftClickCounter = 10000;
            }
        }
    }

    public void setIngameNotInFocus()
    {
        if (this.inGameHasFocus)
        {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;

            if (!PointerInputAbstraction.isTouchMode() && this.mouseGrabSupported)
            {
                this.mouseHelper.ungrabMouseCursor();
            }
        }
    }

    public void displayInGameMenu()
    {
        if (this.currentScreen == null)
        {
            this.displayGuiScreen(new GuiIngameMenu());

            if (this.isSingleplayer() && !this.integratedServer.getPublic())
            {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    private void sendClickBlockToController(boolean leftClick)
    {
        if (!leftClick)
        {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0 && !this.player.isHandActive())
        {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();

                if (!this.world.isAirBlock(blockpos) && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit))
                {
                    this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver);
                    this.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
            else
            {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    private void clickMouse()
    {
        if (this.leftClickCounter <= 0)
        {
            if (this.objectMouseOver == null)
            {
                LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");

                if (this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else if (!this.player.isRowingBoat())
            {
                switch (this.objectMouseOver.typeOfHit)
                {
                    case ENTITY:
                        this.playerController.attackEntity(this.player, this.objectMouseOver.entityHit);
                        break;
                    case BLOCK:
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();

                        if (!this.world.isAirBlock(blockpos))
                        {
                            this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                            break;
                        }

                    case MISS:

                        if (this.playerController.isNotCreative())
                        {
                            this.leftClickCounter = 10;
                        }

                        this.player.resetCooldown();
                        net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(this.player);
                }

                this.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void rightClickMouse()
    {
        if (!this.playerController.getIsHittingBlock())
        {
            this.rightClickDelayTimer = 4;

            if (!this.player.isRowingBoat())
            {
                if (this.objectMouseOver == null)
                {
                    LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
                }

                for (EnumHand enumhand : EnumHand.values())
                {
                    ItemStack itemstack = this.player.getHeldItem(enumhand);

                    if (this.objectMouseOver != null)
                    {
                        switch (this.objectMouseOver.typeOfHit)
                        {
                            case ENTITY:

                                if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entityHit, this.objectMouseOver, enumhand) == EnumActionResult.SUCCESS)
                                {
                                    return;
                                }

                                if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entityHit, enumhand) == EnumActionResult.SUCCESS)
                                {
                                    return;
                                }

                                break;
                            case BLOCK:
                                BlockPos blockpos = this.objectMouseOver.getBlockPos();

                                if (this.world.getBlockState(blockpos).getMaterial() != Material.AIR)
                                {
                                    int i = itemstack.getCount();
                                    EnumActionResult enumactionresult = this.playerController.processRightClickBlock(this.player, this.world, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec, enumhand);

                                    if (enumactionresult == EnumActionResult.SUCCESS)
                                    {
                                        this.player.swingArm(enumhand);

                                        if (!itemstack.isEmpty() && (itemstack.getCount() != i || this.playerController.isInCreativeMode()))
                                        {
                                            this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                                        }

                                        return;
                                    }
                                }
                        }
                    }

                    if (itemstack.isEmpty() && (this.objectMouseOver == null || this.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS)) net.minecraftforge.common.ForgeHooks.onEmptyClick(this.player, enumhand);
                    if (!itemstack.isEmpty() && this.playerController.processRightClick(this.player, this.world, enumhand) == EnumActionResult.SUCCESS)
                    {
                        this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                        return;
                    }
                }
            }
        }
    }

    public void toggleFullscreen()
    {
        Display.toggleFullscreen();
    }

    public boolean isFullScreen()
    {
        return Display.isFullscreen();
    }

    private void resize(int width, int height)
    {
        this.displayWidth = Math.max(1, width);
        this.displayHeight = Math.max(1, height);
        this.scaledResolution = new ScaledResolution(this);

        if (this.currentScreen != null)
        {
            this.currentScreen.onResize(this, this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);

        if (this.voiceOverlay != null)
        {
            this.voiceOverlay.setResolution(this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight());
        }

        if (this.notifRenderer != null)
        {
            this.notifRenderer.setResolution(this, this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight(), this.scaledResolution.getScaleFactor());
        }

        EagRuntime.getConfiguration().getHooks().callScreenChangedHook(this.currentScreen != null ? this.currentScreen.getClass().getName() : null, this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight(), this.displayWidth, this.displayHeight, this.scaledResolution.getScaleFactor());
    }

    public MusicTicker getMusicTicker()
    {
        return this.mcMusicTicker;
    }

    public void runTick() throws IOException
    {
        if (this.rightClickDelayTimer > 0)
        {
            --this.rightClickDelayTimer;
        }

        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPreClientTick();
        RateLimitTracker.tick();
        boolean isHostingLAN = LANServerController.isHostingLAN();
        this.isGamePaused = !isHostingLAN && this.isSingleplayer() && this.world != null && this.player != null && this.currentScreen != null && this.currentScreen.doesGuiPauseGame();

        if (this.isLANOpen && !isHostingLAN)
        {
            this.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("lanServer.relayDisconnected"));
        }

        this.isLANOpen = isHostingLAN;

        if (this.wasPaused != this.isGamePaused)
        {
            SingleplayerServerController.setPaused(this.isGamePaused);

            if (this.isGamePaused)
            {
                this.mcSoundHandler.pauseSounds();
            }
            else
            {
                this.mcSoundHandler.resumeSounds();
            }

            this.wasPaused = this.isGamePaused;
        }

        PlatformWebRTC.runScheduledTasks();
        WebViewOverlayController.runTick();
        SingleplayerServerController.runTick();
        RelayUpdateChecker.runTick();
        UpdateResultObj update = UpdateService.getUpdateResult();

        if (update != null)
        {
            if (update.isSuccess())
            {
                UpdateDataObj updateSuccess = update.getSuccess();

                if (EagRuntime.getConfiguration().isAllowBootMenu())
                {
                    if (this.currentScreen == null || (!(this.currentScreen instanceof GuiUpdateDownloadSuccess) && !(this.currentScreen instanceof GuiUpdateInstallOptions)))
                    {
                        this.displayGuiScreen(new GuiUpdateDownloadSuccess(this.currentScreen, updateSuccess));
                    }
                }
                else
                {
                    UpdateService.quine(updateSuccess.clientSignature, updateSuccess.clientBundle);
                }
            }
            else
            {
                this.displayGuiScreen(new GuiScreenGenericErrorMessage("updateFailed.title", update.getFailure(), this.currentScreen));
            }
        }

        if (!this.isGamePaused)
        {
            this.ingameGUI.updateTick();
        }

        VoiceClientController.tickVoiceClient();
        this.entityRenderer.getMouseOver(1.0F);
        this.tutorial.onMouseHover(this.world, this.objectMouseOver);

        if (!this.isGamePaused && this.world != null)
        {
            this.playerController.updateController();
        }

        if (this.player != null && this.player.connection != null)
        {
            this.player.connection.getEaglerMessageController().flush();
        }

        if (!this.isGamePaused)
        {
            this.renderEngine.tick();
            GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
            GlStateManager.enableAlpha();
        }

        if (this.currentScreen == null && this.player != null)
        {
            if (this.player.getHealth() <= 0.0F && !(this.currentScreen instanceof GuiGameOver))
            {
                this.displayGuiScreen((GuiScreen)null);
            }
            else if (this.player.isPlayerSleeping() && this.world != null)
            {
                this.displayGuiScreen(new GuiSleepMP());
            }

            if (this.currentScreen == null && this.dontPauseTimer <= 0 && !PointerInputAbstraction.isTouchMode())
            {
                if (!Mouse.isMouseGrabbed())
                {
                    this.setIngameNotInFocus();
                    this.displayInGameMenu();
                }
            }
        }
        else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.player.isPlayerSleeping())
        {
            this.displayGuiScreen((GuiScreen)null);
        }

        if (this.currentScreen != null)
        {
            this.leftClickCounter = 10000;
            this.dontPauseTimer = 6;
        }
        else if (this.dontPauseTimer > 0)
        {
            --this.dontPauseTimer;
        }

        String pastedStr;

        while ((pastedStr = Touch.getPastedString()) != null)
        {
            if (this.currentScreen != null)
            {
                this.currentScreen.fireInputEvent(EnumInputEvent.CLIPBOARD_PASTE, pastedStr);
            }
        }

        if (this.currentScreen != null)
        {
            try
            {
                this.currentScreen.handleInput();
            }
            catch (Throwable throwable1)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.addDetail("Screen name", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return Minecraft.this.currentScreen.getClass().getCanonicalName();
                    }
                });
                throw new ReportedException(crashreport);
            }

            if (this.currentScreen != null)
            {
                try
                {
                    this.currentScreen.updateScreen();
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
                    crashreportcategory1.addDetail("Screen name", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    throw new ReportedException(crashreport1);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput)
        {
            this.runTickMouse();

            if (this.leftClickCounter > 0)
            {
                --this.leftClickCounter;
            }

            this.processTouchMine();
            this.runTickKeyboard();
        }

        if (this.world != null)
        {
            if (this.player != null)
            {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30)
                {
                    this.joinPlayerCounter = 0;
                    this.world.joinEntityInSurroundings(this.player);
                }
            }

            if (!this.isGamePaused)
            {
                this.entityRenderer.updateRenderer();
                this.renderGlobal.updateClouds();

                if (this.world.getLastLightningBolt() > 0)
                {
                    this.world.setLastLightningBolt(this.world.getLastLightningBolt() - 1);
                }

                this.world.updateEntities();
            }

            this.eagskullCommand.tick();
        }

        if (!this.isGamePaused)
        {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }

        if (this.world != null)
        {
            if (!this.isGamePaused)
            {
                this.world.setAllowedSpawnTypes(this.world.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                this.tutorial.update();

                try
                {
                    this.world.tick();
                }
                catch (Throwable throwable2)
                {
                    CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

                    if (this.world == null)
                    {
                        CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
                        crashreportcategory2.addCrashSection("Problem", "Level is null!");
                    }
                    else
                    {
                        this.world.addWorldInfoToCrashReport(crashreport2);
                    }

                    throw new ReportedException(crashreport2);
                }
            }

            if (!this.isGamePaused && this.world != null)
            {
                this.world.doVoidFogParticles(MathHelper.floor(this.player.posX), MathHelper.floor(this.player.posY), MathHelper.floor(this.player.posZ));
            }

            if (!this.isGamePaused)
            {
                this.effectRenderer.updateEffects();
            }
        }
        else if (this.myNetworkManager != null)
        {
            this.myNetworkManager.processReceivedPackets();
        }

        if (this.world != null)
        {
            ++this.joinWorldTickCounter;
        }
        else
        {
            this.joinWorldTickCounter = 0;

            if (this.currentScreen != null && this.currentScreen.shouldHangupIntegratedServer())
            {
                if (SingleplayerServerController.hangupEaglercraftServer())
                {
                    this.displayGuiScreen(new GuiScreenIntegratedServerBusy(this.currentScreen, "singleplayer.busy.stoppingIntegratedServer", "singleplayer.failed.stoppingIntegratedServer", SingleplayerServerController::isReady));
                }
            }

            TouchControls.resetSneak();
        }

        if (this.reconnectURI != null)
        {
            String reconURI = this.reconnectURI;
            this.reconnectURI = null;

            if (EagRuntime.getConfiguration().isAllowServerRedirects())
            {
                boolean enableCookies;
                boolean msg;

                if (this.currentServerData != null)
                {
                    enableCookies = this.currentServerData.enableCookies;
                    msg = false;
                }
                else
                {
                    enableCookies = EagRuntime.getConfiguration().isEnableServerCookies();
                    msg = true;
                }

                if (this.world != null)
                {
                    this.world.sendQuittingDisconnectingPacket();
                    this.loadWorld((WorldClient)null);
                }

                LOGGER.info("Recieved SPacketRedirectClientV4EAG, reconnecting to: {}", reconURI);

                if (msg)
                {
                    LOGGER.warn("No existing server connection, cookies will default to {}!", enableCookies ? "enabled" : "disabled");
                }

                ServerAddress addr = AddressResolver.resolveAddressFromURI(reconURI);
                this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, addr.getIP(), addr.getPort(), enableCookies));
            }
            else
            {
                LOGGER.warn("Server redirect blocked: {}", reconURI);
            }
        }

        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPostClientTick();
        this.systemTime = getSystemTime();
    }

    public void handleReconnectPacket(String redirectURI)
    {
        this.reconnectURI = redirectURI;
    }

    private void handlePlaceTouchStart()
    {
        if (this.placeTouchStartTime == -1L)
        {
            this.placeTouchStartTime = EagRuntime.steadyTimeMillis();
        }
    }

    private void handlePlaceTouchEnd()
    {
        if (this.placeTouchStartTime != -1L)
        {
            int len = (int)(EagRuntime.steadyTimeMillis() - this.placeTouchStartTime);

            if (len < 350L && !PointerInputAbstraction.isDraggingNotTouching())
            {
                if (this.objectMouseOver != null && this.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
                {
                    this.clickMouse();
                }
                else
                {
                    this.rightClickMouse();
                }
            }

            this.placeTouchStartTime = -1L;
        }
    }

    private boolean isMiningTouch()
    {
        if (this.mineTouchStartTime == -1L)
        {
            return false;
        }
        else
        {
            long l = EagRuntime.steadyTimeMillis();
            return (this.placeTouchStartTime == -1L || (l - this.placeTouchStartTime) >= 350L) && (l - this.mineTouchStartTime) >= 350L;
        }
    }

    private void processTouchMine()
    {
        if ((this.currentScreen == null || this.currentScreen.allowUserInput) && PointerInputAbstraction.isTouchingScreenNotButton())
        {
            if (PointerInputAbstraction.isDraggingNotTouching())
            {
                if (this.mineTouchStartTime != -1L)
                {
                    long l = EagRuntime.steadyTimeMillis();

                    if ((this.placeTouchStartTime == -1L || (l - this.placeTouchStartTime) < 350L) || (l - this.mineTouchStartTime) < 350L)
                    {
                        this.mineTouchStartTime = -1L;
                    }
                }
            }
            else if (this.mineTouchStartTime == -1L)
            {
                this.mineTouchStartTime = EagRuntime.steadyTimeMillis();
            }
        }
        else
        {
            this.mineTouchStartTime = -1L;
        }
    }

    private void runTickKeyboard() throws IOException
    {
        while (Keyboard.next())
        {
            int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

            if (i == 0x1D && (Keyboard.areKeysLocked() || this.isFullScreen()))
            {
                KeyBinding.setKeyBindState(this.gameSettings.keyBindSprint.getKeyCode(), Keyboard.getEventKeyState());
            }

            if (this.debugCrashKeyPressTime > 0L)
            {
                if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L)
                {
                    throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                }

                if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61))
                {
                    this.debugCrashKeyPressTime = -1L;
                }
            }
            else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61))
            {
                this.actionKeyF3 = true;
                this.debugCrashKeyPressTime = getSystemTime();
            }

            this.dispatchKeypresses();

            if (this.currentScreen != null)
            {
                this.currentScreen.handleKeyboardInput();
            }

            boolean flag = Keyboard.getEventKeyState();

            if (flag)
            {
                if (EaglerDeferredPipeline.instance != null)
                {
                    if (i == 62)
                    {
                        DebugFramebufferView.toggleDebugView();
                    }
                    else if (i == 0xCB || i == 0xC8)
                    {
                        DebugFramebufferView.switchView(-1);
                    }
                    else if (i == 0xCD || i == 0xD0)
                    {
                        DebugFramebufferView.switchView(1);
                    }
                }

                boolean flag1 = false;

                if (this.currentScreen == null)
                {
                    if (i == 1 || (i > -1 && i == this.gameSettings.keyBindClose.getKeyCode()))
                    {
                        this.displayInGameMenu();
                    }

                    flag1 = Keyboard.isKeyDown(61) && this.processKeyF3(i);
                    this.actionKeyF3 |= flag1;

                    if (i == 59)
                    {
                        this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                    }
                }

                if (flag1)
                {
                    KeyBinding.setKeyBindState(i, false);
                }
                else
                {
                    KeyBinding.setKeyBindState(i, true);
                    KeyBinding.onTick(i);
                }
            }
            else
            {
                KeyBinding.setKeyBindState(i, false);

                if (i == 61)
                {
                    if (this.actionKeyF3)
                    {
                        this.actionKeyF3 = false;
                    }
                    else
                    {
                        this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                        this.gameSettings.showDebugProfilerChart = this.gameSettings.showDebugInfo && GuiScreen.isShiftKeyDown();
                        this.gameSettings.showLagometer = this.gameSettings.showDebugInfo && GuiScreen.isAltKeyDown();
                    }
                }
            }

            net.minecraftforge.fml.common.FMLCommonHandler.instance().fireKeyInput();
        }

        this.processKeyBinds();
    }

    private boolean processKeyF3(int auxKey)
    {
        if (auxKey == 30)
        {
            GlStateManager.recompileShaders();
            this.renderGlobal.loadRenderers();
            this.debugFeedbackTranslated("debug.reload_chunks.message");
            return true;
        }
        else if (auxKey == 48)
        {
            boolean flag1 = !this.renderManager.isDebugBoundingBox();
            this.renderManager.setDebugBoundingBox(flag1);
            this.debugFeedbackTranslated(flag1 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
            return true;
        }
        else if (auxKey == 32)
        {
            if (this.ingameGUI != null)
            {
                this.ingameGUI.getChatGUI().clearChatMessages(false);
            }

            return true;
        }
        else if (auxKey == 33)
        {
            this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
            this.debugFeedbackTranslated("debug.cycle_renderdistance.message", this.gameSettings.renderDistanceChunks);
            return true;
        }
        else if (auxKey == 34)
        {
            boolean flag = this.debugRenderer.toggleChunkBorders();
            this.debugFeedbackTranslated(flag ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return true;
        }
        else if (auxKey == 35)
        {
            this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
            this.debugFeedbackTranslated(this.gameSettings.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
            this.gameSettings.saveOptions();
            return true;
        }
        else if (auxKey == 49)
        {
            if (!this.player.canUseCommand(2, ""))
            {
                this.debugFeedbackTranslated("debug.creative_spectator.error");
            }
            else if (this.player.isCreative())
            {
                this.player.sendChatMessage("/gamemode spectator");
            }
            else if (this.player.isSpectator())
            {
                this.player.sendChatMessage("/gamemode creative");
            }

            return true;
        }
        else if (auxKey == 25)
        {
            this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
            this.gameSettings.saveOptions();
            this.debugFeedbackTranslated(this.gameSettings.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
            return true;
        }
        else if (auxKey == 16)
        {
            this.debugFeedbackTranslated("debug.help.message");
            GuiNewChat guinewchat = this.ingameGUI.getChatGUI();
            guinewchat.printChatMessage(new TextComponentTranslation("debug.reload_chunks.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.show_hitboxes.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.clear_chat.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.cycle_renderdistance.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.chunk_boundaries.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.advanced_tooltips.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.creative_spectator.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.pause_focus.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.help.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.reload_resourcepacks.help", new Object[0]));
            return true;
        }
        else if (auxKey == 20)
        {
            this.debugFeedbackTranslated("debug.reload_resourcepacks.message");
            this.refreshResources();
            return true;
        }
        else if (auxKey == 19)
        {
            if (this.gameSettings.shaders)
            {
                ShaderSource.clearCache();
                this.renderGlobal.loadRenderers();
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private void processKeyBinds()
    {
        while (this.gameSettings.keyBindTogglePerspective.isPressed())
        {
            this.togglePerspective();
        }

        while (this.gameSettings.keyBindSmoothCamera.isPressed())
        {
            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
        }

        for (int i = 0; i < 9; ++i)
        {
            boolean flag = this.gameSettings.keyBindSaveToolbar.isKeyDown();
            boolean flag1 = this.gameSettings.keyBindLoadToolbar.isKeyDown();

            if (this.gameSettings.keyBindsHotbar[i].isPressed())
            {
                if (this.player.isSpectator())
                {
                    this.ingameGUI.getSpectatorGui().onHotbarSelected(i);
                }
                else if (!this.player.isCreative() || this.currentScreen != null || !flag1 && !flag)
                {
                    this.player.inventory.currentItem = i;
                }
                else
                {
                    GuiContainerCreative.handleHotbarSnapshots(this, i, flag1, flag);
                }
            }
        }

        boolean zoomKey = this.gameSettings.keyBindZoomCamera.isKeyDown();

        if (zoomKey != this.isZoomKey)
        {
            this.adjustedZoomValue = this.startZoomValue;
            this.isZoomKey = zoomKey;
        }

        while (this.gameSettings.keyBindInventory.isPressed())
        {
            if (this.playerController.isRidingHorse())
            {
                this.player.sendHorseInventory();
            }
            else
            {
                this.tutorial.openInventory();
                this.displayGuiScreen(new GuiInventory(this.player));
            }
        }

        while (this.gameSettings.keyBindAdvancements.isPressed())
        {
            this.displayGuiScreen(new GuiScreenAdvancements(this.player.connection.getAdvancementManager()));
        }

        while (this.gameSettings.keyBindSwapHands.isPressed())
        {
            if (!this.player.isSpectator())
            {
                this.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        }

        while (this.gameSettings.keyBindDrop.isPressed())
        {
            if (!this.player.isSpectator())
            {
                this.player.dropItem(GuiScreen.isCtrlKeyDown());
            }
        }

        boolean flag2 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

        if (flag2)
        {
            while (this.gameSettings.keyBindChat.isPressed())
            {
                this.displayGuiScreen(new GuiChat());
            }

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed())
            {
                this.displayGuiScreen(new GuiChat("/"));
            }
        }

        boolean touchMode = PointerInputAbstraction.isTouchMode();
        boolean miningTouch = touchMode && this.isMiningTouch();
        boolean useTouch = touchMode && this.player.getItemShouldUseOnTouchEagler();

        if (this.player.isHandActive())
        {
            if (!this.gameSettings.keyBindUseItem.isKeyDown() && !miningTouch)
            {
                this.playerController.onStoppedUsingItem(this.player);
            }

            label109:

            while (true)
            {
                if (!this.gameSettings.keyBindAttack.isPressed())
                {
                    while (this.gameSettings.keyBindUseItem.isPressed())
                    {
                        ;
                    }

                    while (true)
                    {
                        if (this.gameSettings.keyBindPickBlock.isPressed())
                        {
                            continue;
                        }

                        break label109;
                    }
                }
            }
        }
        else
        {
            while (this.gameSettings.keyBindAttack.isPressed())
            {
                this.clickMouse();
            }

            if (miningTouch && !this.wasMiningTouch)
            {
                if ((this.objectMouseOver != null && this.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) || useTouch)
                {
                    this.rightClickMouse();
                }
                else
                {
                    this.clickMouse();
                }

                this.wasMiningTouch = true;
            }

            while (this.gameSettings.keyBindUseItem.isPressed())
            {
                this.rightClickMouse();
            }

            while (this.gameSettings.keyBindPickBlock.isPressed())
            {
                this.middleClickMouse();
            }
        }

        this.wasMiningTouch = miningTouch;

        if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.player.isHandActive())
        {
            this.rightClickMouse();
        }

        if (miningTouch && useTouch && this.rightClickDelayTimer == 0 && !this.player.isHandActive())
        {
            this.rightClickMouse();
        }

        this.sendClickBlockToController(this.currentScreen == null && (this.gameSettings.keyBindAttack.isKeyDown() || miningTouch) && this.inGameHasFocus && !useTouch);
    }

    private void runTickMouse() throws IOException
    {
        boolean touched;
        boolean moused = false;

        while ((touched = Touch.next()) || (moused = Mouse.next()))
        {
            boolean touch = false;

            if (touched)
            {
                PointerInputAbstraction.enterTouchModeHook();
                boolean mouse = moused;
                moused = false;
                int tc = Touch.getEventTouchPointCount();

                if (tc > 0)
                {
                    for (int i = 0; i < tc; ++i)
                    {
                        int uid = Touch.getEventTouchPointUID(i);
                        int x = Touch.getEventTouchX(i);
                        int y = Touch.getEventTouchY(i);

                        switch (Touch.getEventType())
                        {
                            case TOUCHSTART:
                                if (TouchControls.handleTouchBegin(uid, x, y))
                                {
                                    break;
                                }

                                touch = true;
                                this.handlePlaceTouchStart();
                                break;
                            case TOUCHEND:
                                if (TouchControls.handleTouchEnd(uid, x, y))
                                {
                                    touch = true;
                                    break;
                                }

                                this.handlePlaceTouchEnd();
                                break;
                            default:
                                break;
                        }
                    }

                    TouchControls.handleInput();

                    if (!touch)
                    {
                        continue;
                    }
                }
                else if (!mouse)
                {
                    continue;
                }
            }

            if (!touch)
            {
                if (net.minecraftforge.client.ForgeHooksClient.postMouseEvent()) continue;

                int i = Mouse.getEventButton();
                KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState())
                {
                    PointerInputAbstraction.enterMouseModeHook();

                    if (this.player.isSpectator() && i == 2)
                    {
                        this.ingameGUI.getSpectatorGui().onMiddleClick();
                    }
                    else
                    {
                        KeyBinding.onTick(i - 100);
                    }
                }
            }

            long j = getSystemTime() - this.systemTime;

            if (j <= 200L)
            {
                if (!touch)
                {
                    int k = Mouse.getEventDWheel();

                    if (k != 0)
                    {
                        if (this.isZoomKey)
                        {
                            this.adjustedZoomValue = MathHelper.clamp(this.adjustedZoomValue - k * 4.0f, 4.0f, 32.0f);
                        }
                        else if (this.player.isSpectator())
                        {
                            k = k < 0 ? -1 : 1;

                            if (this.ingameGUI.getSpectatorGui().isMenuActive())
                            {
                                this.ingameGUI.getSpectatorGui().onMouseScroll(-k);
                            }
                            else
                            {
                                float f = MathHelper.clamp(this.player.capabilities.getFlySpeed() + (float)k * 0.005F, 0.0F, 0.2F);
                                this.player.capabilities.setFlySpeed(f);
                            }
                        }
                        else
                        {
                            this.player.inventory.changeCurrentItem(k);
                        }
                    }
                }

                if (this.currentScreen == null)
                {
                    if ((!this.inGameHasFocus || !(touch || Mouse.isActuallyGrabbed())) && (touch || Mouse.getEventButtonState()))
                    {
                        this.inGameHasFocus = false;
                        this.setIngameFocus();
                    }
                }
                else if (touch)
                {
                    this.currentScreen.handleTouchInput();
                }
                else
                {
                    this.currentScreen.handleMouseInput();
                }
            }

            net.minecraftforge.fml.common.FMLCommonHandler.instance().fireMouseInput();
        }
    }

    public void togglePerspective()
    {
        ++this.gameSettings.thirdPersonView;

        if (this.gameSettings.thirdPersonView > 2)
        {
            this.gameSettings.thirdPersonView = 0;
        }

        if (this.gameSettings.thirdPersonView == 0)
        {
            this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
        }
        else if (this.gameSettings.thirdPersonView == 1)
        {
            this.entityRenderer.loadEntityShader((Entity)null);
        }

        this.renderGlobal.setDisplayListEntitiesDirty();
    }

    private void debugFeedbackTranslated(String untranslatedTemplate, Object... objs)
    {
        this.ingameGUI.getChatGUI().printChatMessage((new TextComponentString("")).appendSibling((new TextComponentTranslation("debug.prefix", new Object[0])).setStyle((new Style()).setColor(TextFormatting.YELLOW).setBold(Boolean.valueOf(true)))).appendText(" ").appendSibling(new TextComponentTranslation(untranslatedTemplate, objs)));
    }

    public void launchIntegratedServer(String folderName, String worldName, @Nullable WorldSettings worldSettingsIn)
    {
        net.minecraftforge.fml.client.FMLClientHandler.instance().startIntegratedServer(folderName, worldName, worldSettingsIn);
        this.loadWorld((WorldClient)null);
        System.gc();
        ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();

        if (worldinfo == null && worldSettingsIn != null)
        {
            worldinfo = new WorldInfo(worldSettingsIn, folderName);
            isavehandler.saveWorldInfo(worldinfo);
        }

        if (worldSettingsIn == null)
        {
            worldSettingsIn = new WorldSettings(worldinfo);
        }

        try
        {
            YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy, EaglercraftUUID.randomUUID().toString());
            MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
            PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.mcDataDir, MinecraftServer.USER_CACHE_FILE.getName()));
            TileEntitySkull.setProfileCache(playerprofilecache);
            TileEntitySkull.setSessionService(minecraftsessionservice);
            PlayerProfileCache.setOnlineMode(false);
            this.integratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn, yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache);
            this.integratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
            crashreportcategory.addCrashSection("Level ID", folderName);
            crashreportcategory.addCrashSection("Level Name", worldName);
            throw new ReportedException(crashreport);
        }

        this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel"));

        while (!this.integratedServer.serverIsInRunLoop())
        {
            if (!net.minecraftforge.fml.common.StartupQuery.check() || this.integratedServer.isServerStopped())
            {
                loadWorld(null);
                displayGuiScreen(null);
                return;
            }
            String s = this.integratedServer.getUserMessage();

            if (s != null)
            {
                this.loadingScreen.displayLoadingString(I18n.format(s));
            }
            else
            {
                this.loadingScreen.displayLoadingString("");
            }

            try
            {
                Thread.sleep(200L);
            }
            catch (InterruptedException var10)
            {
                ;
            }
        }

        this.displayGuiScreen(new GuiScreenWorking());
        SocketAddress socketaddress = this.integratedServer.getNetworkSystem().addLocalEndpoint();
        NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
        networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, (GuiScreen)null));
        networkmanager.sendPacket(new C00Handshake(socketaddress.toString(), 0, EnumConnectionState.LOGIN, true));
        net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile gameProfile = this.getSession().getProfile();
        if (!this.getSession().hasCachedProperties())
        {
            gameProfile = sessionService.fillProfileProperties(gameProfile, true); //Forge: Fill profile properties upon game load. Fixes MC-52974.
            this.getSession().setProperties(gameProfile.getProperties());
        }
        networkmanager.sendPacket(new CPacketLoginStart(gameProfile));
        this.myNetworkManager = networkmanager;
    }

    public void loadWorld(@Nullable WorldClient worldClientIn)
    {
        this.loadWorld(worldClientIn, "");
    }

    public void loadWorld(@Nullable WorldClient worldClientIn, String loadingMessage)
    {
        if (world != null) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(world));

        if (worldClientIn == null)
        {
            NetHandlerPlayClient nethandlerplayclient = this.getConnection();

            if (nethandlerplayclient != null)
            {
                nethandlerplayclient.cleanup();
            }

            if (this.integratedServer != null && this.integratedServer.isAnvilFileSet())
            {
                this.integratedServer.initiateShutdown();
                if (loadingScreen != null && this.running)
                {
                    this.loadingScreen.displayLoadingString(I18n.format("forge.client.shutdown.internal"));
                }
                while (!integratedServer.isServerStopped())
                {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException ie) {}
                }
            }

            this.integratedServer = null;
            this.entityRenderer.resetData();
            this.playerController = null;
            NarratorChatListener.INSTANCE.clear();
        }

        this.renderViewEntity = null;
        this.myNetworkManager = null;

        if (this.loadingScreen != null)
        {
            this.loadingScreen.resetProgressAndMessage(loadingMessage);
            this.loadingScreen.displayLoadingString("");
        }

        if (worldClientIn == null && this.world != null)
        {
            this.mcResourcePackRepository.clearResourcePack();
            this.ingameGUI.resetPlayersOverlayFooterHeader();
            this.setServerData((ServerData)null);
            this.integratedServerIsRunning = false;
            net.minecraftforge.fml.client.FMLClientHandler.instance().handleClientWorldClosing(this.world);
        }

        this.mcSoundHandler.stopSounds();
        this.world = worldClientIn;

        if (this.renderGlobal != null)
        {
            this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
        }

        if (this.effectRenderer != null)
        {
            this.effectRenderer.clearEffects(worldClientIn);
        }

        TileEntityRendererDispatcher.instance.setWorld(worldClientIn);
        net.minecraftforge.client.MinecraftForgeClient.clearRenderCache();

        if (worldClientIn != null)
        {
            if (!this.integratedServerIsRunning)
            {
                AuthenticationService authenticationservice = new YggdrasilAuthenticationService(this.proxy, EaglercraftUUID.randomUUID().toString());
                MinecraftSessionService minecraftsessionservice = authenticationservice.createMinecraftSessionService();
                GameProfileRepository gameprofilerepository = authenticationservice.createProfileRepository();
                PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.mcDataDir, MinecraftServer.USER_CACHE_FILE.getName()));
                TileEntitySkull.setProfileCache(playerprofilecache);
                TileEntitySkull.setSessionService(minecraftsessionservice);
                PlayerProfileCache.setOnlineMode(false);
            }

            if (this.player == null)
            {
                this.player = this.playerController.createPlayer(worldClientIn, new StatisticsManager(), new RecipeBookClient());
                this.playerController.flipPlayer(this.player);
            }

            this.player.preparePlayerToSpawn();
            worldClientIn.spawnEntity(this.player);
            this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.player);
            this.renderViewEntity = this.player;
        }
        else
        {
            this.saveLoader.flushCache();
            this.player = null;
        }

        System.gc();
        this.systemTime = 0L;
    }

    public void setDimensionAndSpawnPlayer(int dimension)
    {
        this.world.setInitialSpawnLocation();
        this.world.removeAllEntities();
        int i = 0;
        String s = null;

        if (this.player != null)
        {
            i = this.player.getEntityId();
            this.world.removeEntity(this.player);
            s = this.player.getServerBrand();
        }

        this.renderViewEntity = null;
        EntityPlayerSP entityplayersp = this.player;
        this.player = this.playerController.createPlayer(this.world, this.player == null ? new StatisticsManager() : this.player.getStatFileWriter(), this.player == null ? new RecipeBook() : this.player.getRecipeBook());
        this.player.getDataManager().setEntryValues(entityplayersp.getDataManager().getAll());
        this.player.updateSyncFields(entityplayersp); // Forge: fix MC-10657
        this.player.dimension = dimension;
        this.renderViewEntity = this.player;
        this.player.preparePlayerToSpawn();
        this.player.setServerBrand(s);
        this.world.spawnEntity(this.player);
        this.playerController.flipPlayer(this.player);
        this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.player.setEntityId(i);
        this.playerController.setPlayerCapabilities(this.player);
        this.player.setReducedDebug(entityplayersp.hasReducedDebug());

        if (this.currentScreen instanceof GuiGameOver)
        {
            this.displayGuiScreen((GuiScreen)null);
        }
    }

    public final boolean isDemo()
    {
        return this.isDemo;
    }

    @Nullable
    public NetHandlerPlayClient getConnection()
    {
        return this.player == null ? null : this.player.connection;
    }

    public static boolean isGuiEnabled()
    {
        return instance == null || !instance.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled()
    {
        return instance != null && instance.gameSettings.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled()
    {
        return instance != null && instance.gameSettings.ambientOcclusion != 0;
    }

    private void middleClickMouse()
    {
        if (this.objectMouseOver != null && this.objectMouseOver.typeOfHit != RayTraceResult.Type.MISS)
        {
            net.minecraftforge.common.ForgeHooks.onPickBlock(this.objectMouseOver, this.player, this.world);
            // We delete this code wholly instead of commenting it out, to make sure we detect changes in it between MC versions
        }
    }

    public ItemStack storeTEInStack(ItemStack stack, TileEntity te)
    {
        NBTTagCompound nbttagcompound = te.writeToNBT(new NBTTagCompound());

        if (stack.getItem() == Items.SKULL && nbttagcompound.hasKey("Owner"))
        {
            NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
            nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
            stack.setTagCompound(nbttagcompound3);
            return stack;
        }
        else
        {
            stack.setTagInfo("BlockEntityTag", nbttagcompound);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.appendTag(new NBTTagString("(+NBT)"));
            nbttagcompound1.setTag("Lore", nbttaglist);
            stack.setTagInfo("display", nbttagcompound1);
            return stack;
        }
    }

    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash)
    {
        theCrash.getCategory().addDetail("Launched Version", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.launchedVersion;
            }
        });
        theCrash.getCategory().addDetail("LWJGL", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return Sys.getVersion();
            }
        });
        theCrash.getCategory().addDetail("OpenGL", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return EaglercraftGPU.glGetString(7937) + " GL version " + EaglercraftGPU.glGetString(7938) + ", " + EaglercraftGPU.glGetString(7936);
            }
        });
        theCrash.getCategory().addDetail("GL Caps", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return OpenGlHelper.getLogText();
            }
        });
        theCrash.getCategory().addDetail("Using VBOs", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
            }
        });
        theCrash.getCategory().addDetail("Is Modded", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                String s = ClientBrandRetriever.getClientModName();

                if (!"vanilla".equals(s))
                {
                    return "Definitely; Client brand changed to '" + s + "'";
                }
                else
                {
                    return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
                }
            }
        });
        theCrash.getCategory().addDetail("Type", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return "Client (map_client.txt)";
            }
        });
        theCrash.getCategory().addDetail("Resource Packs", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                StringBuilder stringbuilder = new StringBuilder();

                for (String s : Minecraft.this.gameSettings.resourcePacks)
                {
                    if (stringbuilder.length() > 0)
                    {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(s);

                    if (Minecraft.this.gameSettings.incompatibleResourcePacks.contains(s))
                    {
                        stringbuilder.append(" (incompatible)");
                    }
                }

                return stringbuilder.toString();
            }
        });
        theCrash.getCategory().addDetail("Current Language", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        theCrash.getCategory().addDetail("Profiler Position", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        theCrash.getCategory().addDetail("CPU", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return OpenGlHelper.getCpu();
            }
        });

        if (this.world != null)
        {
            this.world.addWorldInfoToCrashReport(theCrash);
        }

        return theCrash;
    }

    public static Minecraft getMinecraft()
    {
        return instance;
    }

    @Deprecated // Forge: Use selective scheduleResourceRefresh method in FMLClientHandler
    public ListenableFuture<Object> scheduleResourcesRefresh()
    {
        return this.addScheduledTask(new Runnable()
        {
            public void run()
            {
                Minecraft.this.refreshResources();
            }
        });
    }

    public void addServerStatsToSnooper(Snooper playerSnooper)
    {
        playerSnooper.addClientStat("fps", Integer.valueOf(debugFPS));
        playerSnooper.addClientStat("vsync_enabled", Boolean.valueOf(this.gameSettings.enableVsync));
        playerSnooper.addClientStat("display_frequency", Integer.valueOf(Display.getDisplayMode().getFrequency()));
        playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
        playerSnooper.addClientStat("run_time", Long.valueOf((MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L));
        playerSnooper.addClientStat("current_action", this.getCurrentAction());
        playerSnooper.addClientStat("language", this.gameSettings.language == null ? "en_us" : this.gameSettings.language);
        String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        playerSnooper.addClientStat("endianness", s);
        playerSnooper.addClientStat("subtitles", Boolean.valueOf(this.gameSettings.showSubtitles));
        playerSnooper.addClientStat("touch", this.gameSettings.touchscreen ? "touch" : "mouse");
        playerSnooper.addClientStat("resource_packs", Integer.valueOf(this.mcResourcePackRepository.getRepositoryEntries().size()));
        int i = 0;

        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries())
        {
            playerSnooper.addClientStat("resource_pack[" + i++ + "]", resourcepackrepository$entry.getResourcePackName());
        }

        if (this.integratedServer != null && this.integratedServer.getPlayerUsageSnooper() != null)
        {
            playerSnooper.addClientStat("snooper_partner", this.integratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    private String getCurrentAction()
    {
        if (this.integratedServer != null)
        {
            return this.integratedServer.getPublic() ? "hosting_lan" : "singleplayer";
        }
        else if (this.currentServerData != null)
        {
            return this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer";
        }
        else
        {
            return "out_of_game";
        }
    }

    public void addServerTypeToSnooper(Snooper playerSnooper)
    {
        playerSnooper.addStatToSnooper("opengl_version", EaglercraftGPU.glGetString(7938));
        playerSnooper.addStatToSnooper("opengl_vendor", EaglercraftGPU.glGetString(7936));
        playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
        playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
        ContextCapabilities contextcapabilities = GLContext.getCapabilities();
        playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", Boolean.valueOf(contextcapabilities.GL_ARB_arrays_of_arrays));
        playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", Boolean.valueOf(contextcapabilities.GL_ARB_base_instance));
        playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", Boolean.valueOf(contextcapabilities.GL_ARB_blend_func_extended));
        playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_clear_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_color_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", Boolean.valueOf(contextcapabilities.GL_ARB_compatibility));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", Boolean.valueOf(contextcapabilities.GL_ARB_compressed_texture_pixel_storage));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_compute_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_buffer));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_image));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_compute_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_buffer));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_image));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_clamp));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_texture));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_buffers));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_buffers_blend));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_elements_base_vertex));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_indirect));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_instanced));
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", Boolean.valueOf(contextcapabilities.GL_ARB_explicit_attrib_location));
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", Boolean.valueOf(contextcapabilities.GL_ARB_explicit_uniform_location));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_layer_viewport));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_program));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_program_shadow));
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_framebuffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", Boolean.valueOf(contextcapabilities.GL_ARB_framebuffer_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", Boolean.valueOf(contextcapabilities.GL_ARB_geometry_shader4));
        playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", Boolean.valueOf(contextcapabilities.GL_ARB_gpu_shader5));
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", Boolean.valueOf(contextcapabilities.GL_ARB_half_float_pixel));
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", Boolean.valueOf(contextcapabilities.GL_ARB_half_float_vertex));
        playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", Boolean.valueOf(contextcapabilities.GL_ARB_instanced_arrays));
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", Boolean.valueOf(contextcapabilities.GL_ARB_map_buffer_alignment));
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", Boolean.valueOf(contextcapabilities.GL_ARB_map_buffer_range));
        playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", Boolean.valueOf(contextcapabilities.GL_ARB_multisample));
        playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", Boolean.valueOf(contextcapabilities.GL_ARB_multitexture));
        playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", Boolean.valueOf(contextcapabilities.GL_ARB_occlusion_query2));
        playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_pixel_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", Boolean.valueOf(contextcapabilities.GL_ARB_seamless_cube_map));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_objects));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_stencil_export));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_texture_lod));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", Boolean.valueOf(contextcapabilities.GL_ARB_shadow));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", Boolean.valueOf(contextcapabilities.GL_ARB_shadow_ambient));
        playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", Boolean.valueOf(contextcapabilities.GL_ARB_stencil_texturing));
        playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", Boolean.valueOf(contextcapabilities.GL_ARB_sync));
        playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_tessellation_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_border_clamp));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_cube_map));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_cube_map_array));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_non_power_of_two));
        playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_uniform_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_blend));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_program));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_shader));
        playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", Boolean.valueOf(contextcapabilities.GL_EXT_bindable_uniform));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_equation_separate));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_func_separate));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_minmax));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_subtract));
        playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", Boolean.valueOf(contextcapabilities.GL_EXT_draw_instanced));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_multisample));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_object));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", Boolean.valueOf(contextcapabilities.GL_EXT_geometry_shader4));
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", Boolean.valueOf(contextcapabilities.GL_EXT_gpu_program_parameters));
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", Boolean.valueOf(contextcapabilities.GL_EXT_gpu_shader4));
        playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", Boolean.valueOf(contextcapabilities.GL_EXT_multi_draw_arrays));
        playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", Boolean.valueOf(contextcapabilities.GL_EXT_packed_depth_stencil));
        playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", Boolean.valueOf(contextcapabilities.GL_EXT_paletted_texture));
        playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", Boolean.valueOf(contextcapabilities.GL_EXT_rescale_normal));
        playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", Boolean.valueOf(contextcapabilities.GL_EXT_separate_shader_objects));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", Boolean.valueOf(contextcapabilities.GL_EXT_shader_image_load_store));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", Boolean.valueOf(contextcapabilities.GL_EXT_shadow_funcs));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", Boolean.valueOf(contextcapabilities.GL_EXT_shared_texture_palette));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_clear_tag));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_two_side));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_wrap));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_3d));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_array));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_integer));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_lod_bias));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", Boolean.valueOf(contextcapabilities.GL_EXT_vertex_shader));
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", Boolean.valueOf(contextcapabilities.GL_EXT_vertex_weighting));
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", Integer.valueOf(EaglercraftGPU.glGetInteger(35658)));
        EaglercraftGPU.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", Integer.valueOf(EaglercraftGPU.glGetInteger(35657)));
        EaglercraftGPU.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", Integer.valueOf(EaglercraftGPU.glGetInteger(34921)));
        EaglercraftGPU.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", Integer.valueOf(EaglercraftGPU.glGetInteger(35660)));
        EaglercraftGPU.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", Integer.valueOf(EaglercraftGPU.glGetInteger(34930)));
        EaglercraftGPU.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_array_texture_layers]", Integer.valueOf(EaglercraftGPU.glGetInteger(35071)));
        EaglercraftGPU.glGetError();
        playerSnooper.addStatToSnooper("gl_max_texture_size", Integer.valueOf(getGLMaximumTextureSize()));
        GameProfile gameprofile = this.session.getProfile();

        if (gameprofile != null && gameprofile.getId() != null)
        {
            playerSnooper.addStatToSnooper("uuid", Hashing.sha1().hashBytes(gameprofile.getId().toString().getBytes(Charsets.ISO_8859_1)).toString());
        }
    }

    public static int getGLMaximumTextureSize()
    {
        //Forge we redirect this to our code which caches the value before any splash screen stuff is done.
        return net.minecraftforge.fml.client.SplashProgress.getMaxTextureSize();
    }

    public boolean isSnooperEnabled()
    {
        return this.gameSettings.snooperEnabled;
    }

    public void setServerData(ServerData serverDataIn)
    {
        this.currentServerData = serverDataIn;
    }

    @Nullable
    public ServerData getCurrentServerData()
    {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning()
    {
        return this.integratedServerIsRunning;
    }

    public boolean isSingleplayer()
    {
        return this.integratedServerIsRunning && this.integratedServer != null;
    }

    @Nullable
    public IntegratedServer getIntegratedServer()
    {
        return this.integratedServer;
    }

    public static void stopIntegratedServer()
    {
        if (instance != null)
        {
            IntegratedServer integratedserver = instance.getIntegratedServer();

            if (integratedserver != null)
            {
                integratedserver.stopServer();
            }
        }
    }

    public Snooper getPlayerUsageSnooper()
    {
        return this.usageSnooper;
    }

    public static long getSystemTime()
    {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    public boolean isFullScreen()
    {
        return this.fullscreen;
    }

    public Session getSession()
    {
        return this.session;
    }

    public PropertyMap getProfileProperties()
    {
        if (this.profileProperties.isEmpty())
        {
            GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.profileProperties.putAll(gameprofile.getProperties());
        }

        return this.profileProperties;
    }

    public Proxy getProxy()
    {
        return this.proxy;
    }

    public TextureManager getTextureManager()
    {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager()
    {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository()
    {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager()
    {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks()
    {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit()
    {
        return this.jvm64bit;
    }

    public boolean isGamePaused()
    {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler()
    {
        return this.mcSoundHandler;
    }

    public MusicTicker.MusicType getAmbientMusicType()
    {
        if (this.currentScreen instanceof GuiWinGame)
        {
            return MusicTicker.MusicType.CREDITS;
        }
        else if (this.player != null)
        {
            MusicTicker.MusicType type = this.world.provider.getMusicType();
            if (type != null) return type;

            if (this.player.world.provider instanceof WorldProviderHell)
            {
                return MusicTicker.MusicType.NETHER;
            }
            else if (this.player.world.provider instanceof WorldProviderEnd)
            {
                return this.ingameGUI.getBossOverlay().shouldPlayEndBossMusic() ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END;
            }
            else
            {
                return this.player.capabilities.isCreativeMode && this.player.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME;
            }
        }
        else
        {
            return MusicTicker.MusicType.MENU;
        }
    }

    public void dispatchKeypresses()
    {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

        if (i != 0 && !Keyboard.isRepeatEvent())
        {
            if (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)
            {
                if (Keyboard.getEventKeyState())
                {
                    if (this.gameSettings.keyBindFullscreen.isActiveAndMatches(i))
                    {
                        this.toggleFullscreen();
                    }
                    else if (this.gameSettings.keyBindScreenshot.isActiveAndMatches(i))
                    {
                        this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot());
                    }
                    else if (i == 48 && GuiScreen.isCtrlKeyDown() && (this.currentScreen == null || this.currentScreen != null && !this.currentScreen.isFocused()))
                    {
                        this.gameSettings.setOptionValue(GameSettings.Options.NARRATOR, 1);

                        if (this.currentScreen instanceof ScreenChatOptions)
                        {
                            ((ScreenChatOptions)this.currentScreen).updateNarratorButton();
                        }
                    }
                }
                else if (this.currentScreen instanceof GuiControls) ((GuiControls)this.currentScreen).buttonId = null;
            }
        }
    }

    public MinecraftSessionService getSessionService()
    {
        return this.sessionService;
    }

    public SkinManager getSkinManager()
    {
        return this.skinManager;
    }

    @Nullable
    public Entity getRenderViewEntity()
    {
        return this.renderViewEntity;
    }

    public void setRenderViewEntity(Entity viewingEntity)
    {
        this.renderViewEntity = viewingEntity;
        this.entityRenderer.loadEntityShader(viewingEntity);
    }

    public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule)
    {
        Validate.notNull(callableToSchedule);

        if (this.isCallingFromMinecraftThread())
        {
            try
            {
                return Futures.<V>immediateFuture(callableToSchedule.call());
            }
            catch (Exception exception)
            {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
        else
        {
            ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.<V>create(callableToSchedule);

            synchronized (this.scheduledTasks)
            {
                this.scheduledTasks.add(listenablefuturetask);
                return listenablefuturetask;
            }
        }
    }

    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule)
    {
        Validate.notNull(runnableToSchedule);
        return this.<Object>addScheduledTask(Executors.callable(runnableToSchedule));
    }

    public boolean isCallingFromMinecraftThread()
    {
        return Thread.currentThread() == this.mcThread;
    }

    public BlockRendererDispatcher getBlockRendererDispatcher()
    {
        return this.blockRenderDispatcher;
    }

    public RenderManager getRenderManager()
    {
        return this.renderManager;
    }

    public RenderItem getRenderItem()
    {
        return this.renderItem;
    }

    public ItemRenderer getItemRenderer()
    {
        return this.itemRenderer;
    }

    public <T> ISearchTree<T> getSearchTree(SearchTreeManager.Key<T> key)
    {
        return this.searchTreeManager.<T>get(key);
    }

    public static int getDebugFPS()
    {
        return debugFPS;
    }

    public FrameTimer getFrameTimer()
    {
        return this.frameTimer;
    }

    public boolean isConnectedToRealms()
    {
        return this.connectedToRealms;
    }

    public void setConnectedToRealms(boolean isConnected)
    {
        this.connectedToRealms = isConnected;
    }

    public DataFixer getDataFixer()
    {
        return this.dataFixer;
    }

    public float getRenderPartialTicks()
    {
        return this.timer.renderPartialTicks;
    }

    public float getTickLength()
    {
        return this.timer.elapsedPartialTicks;
    }

    public BlockColors getBlockColors()
    {
        return this.blockColors;
    }

    // FORGE
    public ItemColors getItemColors()
    {
        return this.itemColors;
    }

    public boolean isReducedDebug()
    {
        return this.player != null && this.player.hasReducedDebug() || this.gameSettings.reducedDebugInfo;
    }

    public GuiToast getToastGui()
    {
        return this.toastGui;
    }

    public Tutorial getTutorial()
    {
        return this.tutorial;
    }

    public SearchTreeManager getSearchTreeManager()
    {
        return this.searchTreeManager;
    }
}
