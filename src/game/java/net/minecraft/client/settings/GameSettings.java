package net.minecraft.client.settings;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.ArrayUtils;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerZLIB;
import net.lax1dude.eaglercraft.v1_8.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenVideoSettingsWarning;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.EaglerDeferredConfig;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.EaglerDeferredPipeline;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.dynamiclights.DynamicLightsStateManager;
import net.lax1dude.eaglercraft.v1_8.recording.EnumScreenRecordingCodec;
import net.lax1dude.eaglercraft.v1_8.recording.ScreenRecordingController;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayManager;
import net.lax1dude.eaglercraft.v1_8.voice.VoiceClientController;
import net.optifine.CustomSky;
import org.json.JSONArray;

@SideOnly(Side.CLIENT)
public class GameSettings
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Splitter COLON_SPLITTER = Splitter.on(':');
    private static final String[] GUISCALES = new String[] {"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] PARTICLES = new String[] {"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
    private static final String[] AMBIENT_OCCLUSIONS = new String[] {"options.ao.off", "options.ao.min", "options.ao.max"};
    private static final String[] CLOUDS_TYPES = new String[] {"options.off", "options.clouds.fast", "options.clouds.fancy"};
    private static final String[] ATTACK_INDICATORS = new String[] {"options.off", "options.attack.crosshair", "options.attack.hotbar"};
    public static final String[] NARRATOR_MODES = new String[] {"options.narrator.off", "options.narrator.all", "options.narrator.chat", "options.narrator.system"};
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse;
    public int renderDistanceChunks = -1;
    public boolean viewBobbing = true;
    public boolean anaglyph;
    public boolean fboEnable = true;
    public int limitFramerate = 120;
    public int clouds = 2;
    public boolean fancyGraphics = true;
    public int ambientOcclusion = 2;
    public List<String> resourcePacks = Lists.<String>newArrayList();
    public List<String> incompatibleResourcePacks = Lists.<String>newArrayList();
    public EntityPlayer.EnumChatVisibility chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
    public boolean chatColours = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public float chatOpacity = 1.0F;
    public boolean snooperEnabled = true;
    public boolean fullScreen;
    public boolean enableVsync = EagRuntime.getPlatformType() != EnumPlatformType.DESKTOP;
    public boolean allowBlockAlternatives = true;
    public boolean useVbo = true;
    public boolean reducedDebugInfo;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
    public boolean touchscreen;
    public EnumHandSide mainHand = EnumHandSide.RIGHT;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public float chatScale = 1.0F;
    public float chatWidth = 1.0F;
    public float chatHeightUnfocused = 0.44366196F;
    public float chatHeightFocused = 1.0F;
    public int mipmapLevels = 4;
    private final Map<SoundCategory, Float> soundLevels = Maps.newEnumMap(SoundCategory.class);
    public boolean useNativeTransport = true;
    public boolean entityShadows = true;
    public int attackIndicator = 1;
    public boolean enableWeakAttacks;
    public boolean showSubtitles;
    public boolean realmsNotifications = true;
    public boolean autoJump = true;
    public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
    public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
    public KeyBinding keyBindSwapHands = new KeyBinding("key.swapHands", 33, "key.categories.inventory");
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.inventory");
    public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
    public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
    public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
    public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
    public KeyBinding keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
    public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
    public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
    public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", KeyboardConstants.KEY_M, "key.categories.misc");
    public KeyBinding keyBindZoomCamera = new KeyBinding("key.zoomCamera", KeyboardConstants.KEY_C, "key.categories.misc");
    public KeyBinding keyBindFunction = new KeyBinding("key.function", KeyboardConstants.KEY_F, "key.categories.misc");
    public KeyBinding keyBindClose = new KeyBinding("key.close", KeyboardConstants.KEY_GRAVE, "key.categories.misc");
    public KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
    public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
    public KeyBinding keyBindAdvancements = new KeyBinding("key.advancements", 38, "key.categories.misc");
    public KeyBinding[] keyBindsHotbar = new KeyBinding[] {new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
    public KeyBinding keyBindSaveToolbar = new KeyBinding("key.saveToolbarActivator", 46, "key.categories.creative");
    public KeyBinding keyBindLoadToolbar = new KeyBinding("key.loadToolbarActivator", 45, "key.categories.creative");
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    public EnumDifficulty difficulty;
    public boolean hideGUI;
    public int thirdPersonView;
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public boolean showLagometer;
    public String lastServer;
    public boolean smoothCamera;
    public boolean debugCamEnable;
    public float fovSetting;
    public float gammaSetting;
    public float saturation;
    public int guiScale;
    public int particleSetting;
    public int narrator;
    public String language;
    public boolean forceUnicodeFont;
    public boolean hasCreatedDemoWorld;
    public int relayTimeout;
    public boolean hideJoinCode;
    public boolean hudFps = true;
    public boolean hudCoords = true;
    public boolean hudPlayer = false;
    public boolean hudWorld = false;
    public boolean hudStats = false;
    public boolean hud24h = false;
    public boolean chunkFix = true;
    public boolean fog = true;
    public int fxaa = 0;
    public boolean shaders = false;
    public boolean shadersAODisable = false;
    public EaglerDeferredConfig deferredShaderConf = new EaglerDeferredConfig();
    public boolean enableUpdateSvc = true;
    public boolean enableFNAWSkins = true;
    public boolean enableDynamicLights = false;
    public boolean hasHiddenPhishWarning = false;
    public boolean enableProfanityFilter = false;
    public boolean hasShownProfanityFilter = false;
    public float touchControlOpacity = 1.0f;
    public boolean hideDefaultUsernameWarning = false;
    public boolean hideVideoSettingsWarning = EagRuntime.getPlatformType() == EnumPlatformType.DESKTOP;

    public int voiceListenRadius = 16;
    public float voiceListenVolume = 0.5f;
    public float voiceSpeakVolume = 0.5f;
    public int voicePTTKey = 47;

    public EnumScreenRecordingCodec screenRecordCodec;
    public int screenRecordFPS = ScreenRecordingController.DEFAULT_FPS;
    public int screenRecordResolution = ScreenRecordingController.DEFAULT_RESOLUTION;
    public int screenRecordAudioBitrate = ScreenRecordingController.DEFAULT_AUDIO_BITRATE;
    public int screenRecordVideoBitrate = ScreenRecordingController.DEFAULT_VIDEO_BITRATE;
    public float screenRecordGameVolume = ScreenRecordingController.DEFAULT_GAME_VOLUME;
    public float screenRecordMicVolume = ScreenRecordingController.DEFAULT_MIC_VOLUME;

    public int betterGrassOF = 0;
    public int connectedTexturesOF = 1;
    public boolean customSkyOF = true;
    public boolean smartLeavesOF = false;
    public boolean customItemsOF = true;

    public GameSettings(Minecraft mcIn)
    {
        setForgeKeybindProperties();
        this.keyBindings = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindZoomCamera, this.keyBindFunction, this.keyBindClose, this.keyBindFullscreen, this.keyBindSpectatorOutlines, this.keyBindSwapHands, this.keyBindSaveToolbar, this.keyBindLoadToolbar, this.keyBindAdvancements}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.relayTimeout = 4;
        this.hideJoinCode = false;
        this.lastServer = "";
        this.fovSetting = 70.0F;
        this.language = EagRuntime.getConfiguration().getDefaultLocale();
        this.mc = mcIn;
        GameSettings.Options.RENDER_DISTANCE.setValueMax(18.0F);
        this.renderDistanceChunks = 4;
        this.screenRecordCodec = ScreenRecordingController.getDefaultCodec();
        this.loadOptions();
    }

    public static String getKeyDisplayString(int key)
    {
        if (key < 0)
        {
            switch (key)
            {
                case -100:
                    return I18n.format("key.mouse.left");
                case -99:
                    return I18n.format("key.mouse.right");
                case -98:
                    return I18n.format("key.mouse.middle");
                default:
                    return I18n.format("key.mouseButton", key + 101);
            }
        }
        else
        {
            return key < 256 ? Keyboard.getKeyName(key) : String.format("%c", (char)(key - 256)).toUpperCase();
        }
    }

    public static boolean isKeyDown(KeyBinding key)
    {
        int i = key.getKeyCode();

        if (i != 0 && i < 256)
        {
            return i < 0 ? Mouse.isButtonDown(i + 100) : Keyboard.isKeyDown(i);
        }
        else
        {
            return false;
        }
    }

    public void setOptionKeyBinding(KeyBinding key, int keyCode)
    {
        key.setKeyCode(keyCode);
        this.saveOptions();
    }

    public void setOptionFloatValue(GameSettings.Options settingsOption, float value)
    {
        if (settingsOption == GameSettings.Options.SENSITIVITY)
        {
            this.mouseSensitivity = value;
        }

        if (settingsOption == GameSettings.Options.FOV)
        {
            this.fovSetting = value;
        }

        if (settingsOption == GameSettings.Options.GAMMA)
        {
            this.gammaSetting = value;
        }

        if (settingsOption == GameSettings.Options.FRAMERATE_LIMIT)
        {
            this.limitFramerate = (int)value;
        }

        if (settingsOption == GameSettings.Options.CHAT_OPACITY)
        {
            this.chatOpacity = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED)
        {
            this.chatHeightFocused = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED)
        {
            this.chatHeightUnfocused = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_WIDTH)
        {
            this.chatWidth = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_SCALE)
        {
            this.chatScale = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.MIPMAP_LEVELS)
        {
            int i = this.mipmapLevels;
            this.mipmapLevels = (int)value;

            if ((float)i != value)
            {
                this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
                this.needsBlockModelRefresh = true; // FORGE: fix for MC-64581 very laggy mipmap slider
            }
        }

        if (settingsOption == GameSettings.Options.RENDER_DISTANCE)
        {
            this.renderDistanceChunks = (int)value;
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }

        if (settingsOption == GameSettings.Options.EAGLER_TOUCH_CONTROL_OPACITY)
        {
            this.touchControlOpacity = value;
        }
    }

    public void setOptionValue(GameSettings.Options settingsOption, int value)
    {
        if (settingsOption == GameSettings.Options.RENDER_DISTANCE)
        {
            this.setOptionFloatValue(settingsOption, MathHelper.clamp((float)(this.renderDistanceChunks + value), settingsOption.getValueMin(), settingsOption.getValueMax()));
        }

        if (settingsOption == GameSettings.Options.MAIN_HAND)
        {
            this.mainHand = this.mainHand.opposite();
        }

        if (settingsOption == GameSettings.Options.INVERT_MOUSE)
        {
            this.invertMouse = !this.invertMouse;
        }

        if (settingsOption == GameSettings.Options.GUI_SCALE)
        {
            this.guiScale = this.guiScale + value & 3;
        }

        if (settingsOption == GameSettings.Options.PARTICLES)
        {
            this.particleSetting = (this.particleSetting + value) % 3;
        }

        if (settingsOption == GameSettings.Options.VIEW_BOBBING)
        {
            this.viewBobbing = !this.viewBobbing;
        }

        if (settingsOption == GameSettings.Options.RENDER_CLOUDS)
        {
            this.clouds = (this.clouds + value) % 3;
        }

        if (settingsOption == GameSettings.Options.FORCE_UNICODE_FONT)
        {
            this.forceUnicodeFont = !this.forceUnicodeFont;
            this.mc.fontRenderer.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
        }

        if (settingsOption == GameSettings.Options.FBO_ENABLE)
        {
            this.fboEnable = !this.fboEnable;
        }

        if (settingsOption == GameSettings.Options.ANAGLYPH)
        {
            this.anaglyph = !this.anaglyph;
            net.minecraftforge.fml.client.FMLClientHandler.instance().refreshResources(net.minecraftforge.client.resource.VanillaResourceType.TEXTURES);
        }

        if (settingsOption == GameSettings.Options.GRAPHICS)
        {
            this.fancyGraphics = !this.fancyGraphics;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.AMBIENT_OCCLUSION)
        {
            this.ambientOcclusion = (this.ambientOcclusion + value) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.CHAT_VISIBILITY)
        {
            this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + value) % 3);
        }

        if (settingsOption == GameSettings.Options.CHAT_COLOR)
        {
            this.chatColours = !this.chatColours;
        }

        if (settingsOption == GameSettings.Options.CHAT_LINKS)
        {
            this.chatLinks = !this.chatLinks;
        }

        if (settingsOption == GameSettings.Options.CHAT_LINKS_PROMPT)
        {
            this.chatLinksPrompt = !this.chatLinksPrompt;
        }

        if (settingsOption == GameSettings.Options.SNOOPER_ENABLED)
        {
            this.snooperEnabled = !this.snooperEnabled;
        }

        if (settingsOption == GameSettings.Options.TOUCHSCREEN)
        {
            this.touchscreen = !this.touchscreen;
        }

        if (settingsOption == GameSettings.Options.USE_FULLSCREEN)
        {
            this.fullScreen = !this.fullScreen;

            if (this.mc.isFullScreen() != this.fullScreen)
            {
                this.mc.toggleFullscreen();
            }
        }

        if (settingsOption == GameSettings.Options.ENABLE_VSYNC)
        {
            this.enableVsync = !this.enableVsync;
            Display.setVSync(this.enableVsync);
        }

        if (settingsOption == GameSettings.Options.USE_VBO)
        {
            this.useVbo = !this.useVbo;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.REDUCED_DEBUG_INFO)
        {
            this.reducedDebugInfo = !this.reducedDebugInfo;
        }

        if (settingsOption == GameSettings.Options.ENTITY_SHADOWS)
        {
            this.entityShadows = !this.entityShadows;
        }

        if (settingsOption == GameSettings.Options.ATTACK_INDICATOR)
        {
            this.attackIndicator = (this.attackIndicator + value) % 3;
        }

        if (settingsOption == GameSettings.Options.SHOW_SUBTITLES)
        {
            this.showSubtitles = !this.showSubtitles;
        }

        if (settingsOption == GameSettings.Options.REALMS_NOTIFICATIONS)
        {
            this.realmsNotifications = !this.realmsNotifications;
        }

        if (settingsOption == GameSettings.Options.AUTO_JUMP)
        {
            this.autoJump = !this.autoJump;
        }

        if (settingsOption == GameSettings.Options.NARRATOR)
        {
            if (NarratorChatListener.INSTANCE.isActive())
            {
                this.narrator = (this.narrator + value) % NARRATOR_MODES.length;
            }
            else
            {
                this.narrator = 0;
            }

            NarratorChatListener.INSTANCE.announceMode(this.narrator);
        }

        if (settingsOption == GameSettings.Options.BLOCK_ALTERNATIVES)
        {
            this.allowBlockAlternatives = !this.allowBlockAlternatives;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.HUD_FPS)
        {
            this.hudFps = !this.hudFps;
        }

        if (settingsOption == GameSettings.Options.HUD_COORDS)
        {
            this.hudCoords = !this.hudCoords;
        }

        if (settingsOption == GameSettings.Options.HUD_PLAYER)
        {
            this.hudPlayer = !this.hudPlayer;
        }

        if (settingsOption == GameSettings.Options.HUD_STATS)
        {
            this.hudStats = !this.hudStats;
        }

        if (settingsOption == GameSettings.Options.HUD_WORLD)
        {
            this.hudWorld = !this.hudWorld;
        }

        if (settingsOption == GameSettings.Options.HUD_24H)
        {
            this.hud24h = !this.hud24h;
        }

        if (settingsOption == GameSettings.Options.CHUNK_FIX)
        {
            this.chunkFix = !this.chunkFix;
        }

        if (settingsOption == GameSettings.Options.FOG)
        {
            this.fog = !this.fog;
        }

        if (settingsOption == GameSettings.Options.FXAA)
        {
            this.fxaa = (this.fxaa + value) % 3;
        }

        if (settingsOption == GameSettings.Options.FULLSCREEN)
        {
            this.mc.toggleFullscreen();
        }

        if (settingsOption == GameSettings.Options.FNAW_SKINS)
        {
            this.enableFNAWSkins = !this.enableFNAWSkins;
            this.mc.getRenderManager().setEnableFNAWSkins(this.mc.getEnableFNAWSkins());
        }

        if (settingsOption == GameSettings.Options.EAGLER_VSYNC)
        {
            this.enableVsync = !this.enableVsync;
        }

        if (settingsOption == GameSettings.Options.EAGLER_DYNAMIC_LIGHTS)
        {
            this.enableDynamicLights = !this.enableDynamicLights;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.EAGLER_PROFANITY_FILTER)
        {
            this.enableProfanityFilter = !this.enableProfanityFilter;
        }

        if (settingsOption == GameSettings.Options.OF_CONNECTED_TEXTURES)
        {
            this.connectedTexturesOF = (this.connectedTexturesOF + 1) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.OF_BETTER_GRASS)
        {
            this.betterGrassOF = (this.betterGrassOF + 1) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.OF_CUSTOM_SKIES)
        {
            this.customSkyOF = !this.customSkyOF;
            CustomSky.update();
        }

        if (settingsOption == GameSettings.Options.OF_SMART_LEAVES)
        {
            this.smartLeavesOF = !this.smartLeavesOF;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.OF_CUSTOM_ITEMS)
        {
            this.customItemsOF = !this.customItemsOF;
            this.mc.renderGlobal.loadRenderers();
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(GameSettings.Options settingOption)
    {
        if (settingOption == GameSettings.Options.FOV)
        {
            return this.fovSetting;
        }
        else if (settingOption == GameSettings.Options.GAMMA)
        {
            return this.gammaSetting;
        }
        else if (settingOption == GameSettings.Options.SATURATION)
        {
            return this.saturation;
        }
        else if (settingOption == GameSettings.Options.SENSITIVITY)
        {
            return this.mouseSensitivity;
        }
        else if (settingOption == GameSettings.Options.CHAT_OPACITY)
        {
            return this.chatOpacity;
        }
        else if (settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED)
        {
            return this.chatHeightFocused;
        }
        else if (settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED)
        {
            return this.chatHeightUnfocused;
        }
        else if (settingOption == GameSettings.Options.CHAT_SCALE)
        {
            return this.chatScale;
        }
        else if (settingOption == GameSettings.Options.CHAT_WIDTH)
        {
            return this.chatWidth;
        }
        else if (settingOption == GameSettings.Options.FRAMERATE_LIMIT)
        {
            return (float)this.limitFramerate;
        }
        else if (settingOption == GameSettings.Options.MIPMAP_LEVELS)
        {
            return (float)this.mipmapLevels;
        }
        else
        {
            return settingOption == GameSettings.Options.RENDER_DISTANCE ? (float)this.renderDistanceChunks : (settingOption == GameSettings.Options.EAGLER_TOUCH_CONTROL_OPACITY ? this.touchControlOpacity : 0.0F);
        }
    }

    public boolean getOptionOrdinalValue(GameSettings.Options settingOption)
    {
        switch (settingOption)
        {
            case INVERT_MOUSE:
                return this.invertMouse;
            case VIEW_BOBBING:
                return this.viewBobbing;
            case ANAGLYPH:
                return this.anaglyph;
            case FBO_ENABLE:
                return this.fboEnable;
            case CHAT_COLOR:
                return this.chatColours;
            case CHAT_LINKS:
                return this.chatLinks;
            case CHAT_LINKS_PROMPT:
                return this.chatLinksPrompt;
            case SNOOPER_ENABLED:
                return this.snooperEnabled;
            case USE_FULLSCREEN:
                return this.fullScreen;
            case ENABLE_VSYNC:
                return this.enableVsync;
            case USE_VBO:
                return this.useVbo;
            case TOUCHSCREEN:
                return this.touchscreen;
            case FORCE_UNICODE_FONT:
                return this.forceUnicodeFont;
            case REDUCED_DEBUG_INFO:
                return this.reducedDebugInfo;
            case ENTITY_SHADOWS:
                return this.entityShadows;
            case SHOW_SUBTITLES:
                return this.showSubtitles;
            case REALMS_NOTIFICATIONS:
                return this.realmsNotifications;
            case ENABLE_WEAK_ATTACKS:
                return this.enableWeakAttacks;
            case AUTO_JUMP:
                return this.autoJump;
            case BLOCK_ALTERNATIVES:
                return this.allowBlockAlternatives;
            case HUD_COORDS:
                return this.hudCoords;
            case HUD_FPS:
                return this.hudFps;
            case HUD_PLAYER:
                return this.hudPlayer;
            case HUD_STATS:
                return this.hudStats;
            case HUD_WORLD:
                return this.hudWorld;
            case HUD_24H:
                return this.hud24h;
            case CHUNK_FIX:
                return this.chunkFix;
            case FOG:
                return this.fog;
            case FULLSCREEN:
                return this.mc.isFullScreen();
            case FNAW_SKINS:
                return this.enableFNAWSkins;
            case EAGLER_VSYNC:
                return this.enableVsync;
            case EAGLER_DYNAMIC_LIGHTS:
                return this.enableDynamicLights;
            case EAGLER_PROFANITY_FILTER:
                return this.enableProfanityFilter;
            case OF_CUSTOM_SKIES:
                return this.customSkyOF;
            case OF_SMART_LEAVES:
                return this.smartLeavesOF;
            case OF_CUSTOM_ITEMS:
                return this.customItemsOF;
            default:
                return false;
        }
    }

    private static String getTranslation(String[] strArray, int index)
    {
        if (index < 0 || index >= strArray.length)
        {
            index = 0;
        }

        return I18n.format(strArray[index]);
    }

    public String getKeyBinding(GameSettings.Options settingOption)
    {
        String s = I18n.format(settingOption.getTranslation()) + ": ";

        if (settingOption.isFloat())
        {
            float f1 = this.getOptionFloatValue(settingOption);
            float f = settingOption.normalizeValue(f1);

            if (settingOption == GameSettings.Options.SENSITIVITY)
            {
                if (f == 0.0F)
                {
                    return s + I18n.format("options.sensitivity.min");
                }
                else
                {
                    return f == 1.0F ? s + I18n.format("options.sensitivity.max") : s + (int)(f * 200.0F) + "%";
                }
            }
            else if (settingOption == GameSettings.Options.FOV)
            {
                if (f1 == 70.0F)
                {
                    return s + I18n.format("options.fov.min");
                }
                else
                {
                    return f1 == 110.0F ? s + I18n.format("options.fov.max") : s + (int)f1;
                }
            }
            else if (settingOption == GameSettings.Options.FRAMERATE_LIMIT)
            {
                return f1 == settingOption.valueMax ? s + I18n.format("options.framerateLimit.max") : s + I18n.format("options.framerate", (int)f1);
            }
            else if (settingOption == GameSettings.Options.RENDER_CLOUDS)
            {
                return f1 == settingOption.valueMin ? s + I18n.format("options.cloudHeight.min") : s + ((int)f1 + 128);
            }
            else if (settingOption == GameSettings.Options.GAMMA)
            {
                if (f == 0.0F)
                {
                    return s + I18n.format("options.gamma.min");
                }
                else
                {
                    return f == 1.0F ? s + I18n.format("options.gamma.max") : s + "+" + (int)(f * 100.0F) + "%";
                }
            }
            else if (settingOption == GameSettings.Options.SATURATION)
            {
                return s + (int)(f * 400.0F) + "%";
            }
            else if (settingOption == GameSettings.Options.CHAT_OPACITY)
            {
                return s + (int)(f * 90.0F + 10.0F) + "%";
            }
            else if (settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED)
            {
                return s + GuiNewChat.calculateChatboxHeight(f) + "px";
            }
            else if (settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED)
            {
                return s + GuiNewChat.calculateChatboxHeight(f) + "px";
            }
            else if (settingOption == GameSettings.Options.CHAT_WIDTH)
            {
                return s + GuiNewChat.calculateChatboxWidth(f) + "px";
            }
            else if (settingOption == GameSettings.Options.RENDER_DISTANCE)
            {
                return s + I18n.format("options.chunks", (int)f1);
            }
            else if (settingOption == GameSettings.Options.MIPMAP_LEVELS)
            {
                return f1 == 0.0F ? s + I18n.format("options.off") : s + (int)f1;
            }
            else
            {
                return f == 0.0F ? s + I18n.format("options.off") : s + (int)(f * 100.0F) + "%";
            }
        }
        else if (settingOption.isBoolean())
        {
            boolean flag = this.getOptionOrdinalValue(settingOption);
            return flag ? s + I18n.format("options.on") : s + I18n.format("options.off");
        }
        else if (settingOption == GameSettings.Options.MAIN_HAND)
        {
            return s + this.mainHand;
        }
        else if (settingOption == GameSettings.Options.GUI_SCALE)
        {
            return s + getTranslation(GUISCALES, this.guiScale);
        }
        else if (settingOption == GameSettings.Options.CHAT_VISIBILITY)
        {
            return s + I18n.format(this.chatVisibility.getResourceKey());
        }
        else if (settingOption == GameSettings.Options.PARTICLES)
        {
            return s + getTranslation(PARTICLES, this.particleSetting);
        }
        else if (settingOption == GameSettings.Options.AMBIENT_OCCLUSION)
        {
            return s + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
        }
        else if (settingOption == GameSettings.Options.RENDER_CLOUDS)
        {
            return s + getTranslation(CLOUDS_TYPES, this.clouds);
        }
        else if (settingOption == GameSettings.Options.GRAPHICS)
        {
            if (this.fancyGraphics)
            {
                return s + I18n.format("options.graphics.fancy");
            }
            else
            {
                String s1 = "options.graphics.fast";
                return s + I18n.format("options.graphics.fast");
            }
        }
        else if (settingOption == GameSettings.Options.ATTACK_INDICATOR)
        {
            return s + getTranslation(ATTACK_INDICATORS, this.attackIndicator);
        }
        else if (settingOption == GameSettings.Options.NARRATOR)
        {
            return NarratorChatListener.INSTANCE.isActive() ? s + getTranslation(NARRATOR_MODES, this.narrator) : s + I18n.format("options.narrator.notavailable");
        }
        else if (settingOption == GameSettings.Options.FXAA)
        {
            if (this.fxaa == 0)
            {
                return s + I18n.format("options.fxaa.auto");
            }
            else if (this.fxaa == 1)
            {
                return s + I18n.format("options.on");
            }
            else
            {
                return s + I18n.format("options.off");
            }
        }
        else if (settingOption == GameSettings.Options.OF_CONNECTED_TEXTURES)
        {
            return s + I18n.format(this.connectedTexturesOF == 0 ? "options.off" : (this.connectedTexturesOF == 1 ? "options.graphics.fast" : "options.graphics.fancy"));
        }
        else if (settingOption == GameSettings.Options.OF_BETTER_GRASS)
        {
            return s + I18n.format(this.betterGrassOF == 0 ? "options.off" : (this.betterGrassOF == 1 ? "options.graphics.fast" : "options.graphics.fancy"));
        }
        else
        {
            return s;
        }
    }

    public void loadOptions()
    {
        byte[] options = EagRuntime.getStorage("g");
        if (options == null)
        {
            return;
        }
        loadOptions(options);
    }

    public void loadOptions(byte[] data)
    {
        try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(EaglerZLIB.newGZIPInputStream(new EaglerInputStream(data)), StandardCharsets.UTF_8)))
        {
            this.soundLevels.clear();
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            String s;

            while ((s = bufferedreader.readLine()) != null)
            {
                try
                {
                    Iterator<String> iterator = COLON_SPLITTER.omitEmptyStrings().limit(2).split(s).iterator();
                    nbttagcompound.setString(iterator.next(), iterator.next());
                }
                catch (Exception var10)
                {
                    LOGGER.warn("Skipping bad option: {}", (Object)s);
                }
            }

            nbttagcompound = this.dataFix(nbttagcompound);

            for (String s1 : nbttagcompound.getKeySet())
            {
                String s2 = nbttagcompound.getString(s1);

                try
                {
                    if ("mouseSensitivity".equals(s1))
                    {
                        this.mouseSensitivity = this.parseFloat(s2);
                    }

                    if ("fov".equals(s1))
                    {
                        this.fovSetting = this.parseFloat(s2) * 40.0F + 70.0F;
                    }

                    if ("gamma".equals(s1))
                    {
                        this.gammaSetting = this.parseFloat(s2);
                    }

                    if ("saturation".equals(s1))
                    {
                        this.saturation = this.parseFloat(s2);
                    }

                    if ("invertYMouse".equals(s1))
                    {
                        this.invertMouse = "true".equals(s2);
                    }

                    if ("renderDistance".equals(s1))
                    {
                        this.renderDistanceChunks = Integer.parseInt(s2);
                    }

                    if ("guiScale".equals(s1))
                    {
                        this.guiScale = Integer.parseInt(s2);
                    }

                    if ("particles".equals(s1))
                    {
                        this.particleSetting = Integer.parseInt(s2);
                    }

                    if ("bobView".equals(s1))
                    {
                        this.viewBobbing = "true".equals(s2);
                    }

                    if ("anaglyph3d".equals(s1))
                    {
                        this.anaglyph = "true".equals(s2);
                    }

                    if ("maxFps".equals(s1))
                    {
                        this.limitFramerate = Integer.parseInt(s2);
                    }

                    if ("fboEnable".equals(s1))
                    {
                        this.fboEnable = "true".equals(s2);
                    }

                    if ("difficulty".equals(s1))
                    {
                        this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(s2));
                    }

                    if ("fancyGraphics".equals(s1))
                    {
                        this.fancyGraphics = "true".equals(s2);
                    }

                    if ("tutorialStep".equals(s1))
                    {
                        this.tutorialStep = TutorialSteps.getTutorial(s2);
                    }

                    if ("ao".equals(s1))
                    {
                        if ("true".equals(s2))
                        {
                            this.ambientOcclusion = 2;
                        }
                        else if ("false".equals(s2))
                        {
                            this.ambientOcclusion = 0;
                        }
                        else
                        {
                            this.ambientOcclusion = Integer.parseInt(s2);
                        }
                    }

                    if ("renderClouds".equals(s1))
                    {
                        if ("true".equals(s2))
                        {
                            this.clouds = 2;
                        }
                        else if ("false".equals(s2))
                        {
                            this.clouds = 0;
                        }
                        else if ("fast".equals(s2))
                        {
                            this.clouds = 1;
                        }
                    }

                    if ("attackIndicator".equals(s1))
                    {
                        if ("0".equals(s2))
                        {
                            this.attackIndicator = 0;
                        }
                        else if ("1".equals(s2))
                        {
                            this.attackIndicator = 1;
                        }
                        else if ("2".equals(s2))
                        {
                            this.attackIndicator = 2;
                        }
                    }

                    if ("resourcePacks".equals(s1))
                    {
                        this.resourcePacks = fromJSONArray(s2);
                    }

                    if ("incompatibleResourcePacks".equals(s1))
                    {
                        this.incompatibleResourcePacks = fromJSONArray(s2);
                    }

                    if ("lastServer".equals(s1))
                    {
                        this.lastServer = s2;
                    }

                    if ("lang".equals(s1))
                    {
                        this.language = s2;
                    }

                    if ("chatVisibility".equals(s1))
                    {
                        this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(s2));
                    }

                    if ("chatColors".equals(s1))
                    {
                        this.chatColours = "true".equals(s2);
                    }

                    if ("chatLinks".equals(s1))
                    {
                        this.chatLinks = "true".equals(s2);
                    }

                    if ("chatLinksPrompt".equals(s1))
                    {
                        this.chatLinksPrompt = "true".equals(s2);
                    }

                    if ("chatOpacity".equals(s1))
                    {
                        this.chatOpacity = this.parseFloat(s2);
                    }

                    if ("snooperEnabled".equals(s1))
                    {
                        this.snooperEnabled = "true".equals(s2);
                    }

                    if ("fullscreen".equals(s1))
                    {
                        this.fullScreen = "true".equals(s2);
                    }

                    if ("enableVsync".equals(s1))
                    {
                        this.enableVsync = "true".equals(s2);
                    }

                    if ("useVbo".equals(s1))
                    {
                        this.useVbo = "true".equals(s2);
                    }

                    if ("hideServerAddress".equals(s1))
                    {
                        this.hideServerAddress = "true".equals(s2);
                    }

                    if ("advancedItemTooltips".equals(s1))
                    {
                        this.advancedItemTooltips = "true".equals(s2);
                    }

                    if ("pauseOnLostFocus".equals(s1))
                    {
                        this.pauseOnLostFocus = "true".equals(s2);
                    }

                    if ("touchscreen".equals(s1))
                    {
                        this.touchscreen = "true".equals(s2);
                    }

                    if ("overrideHeight".equals(s1))
                    {
                        this.overrideHeight = Integer.parseInt(s2);
                    }

                    if ("overrideWidth".equals(s1))
                    {
                        this.overrideWidth = Integer.parseInt(s2);
                    }

                    if ("heldItemTooltips".equals(s1))
                    {
                        this.heldItemTooltips = "true".equals(s2);
                    }

                    if ("chatHeightFocused".equals(s1))
                    {
                        this.chatHeightFocused = this.parseFloat(s2);
                    }

                    if ("chatHeightUnfocused".equals(s1))
                    {
                        this.chatHeightUnfocused = this.parseFloat(s2);
                    }

                    if ("chatScale".equals(s1))
                    {
                        this.chatScale = this.parseFloat(s2);
                    }

                    if ("chatWidth".equals(s1))
                    {
                        this.chatWidth = this.parseFloat(s2);
                    }

                    if ("mipmapLevels".equals(s1))
                    {
                        this.mipmapLevels = Integer.parseInt(s2);
                    }

                    if ("forceUnicodeFont".equals(s1))
                    {
                        this.forceUnicodeFont = "true".equals(s2);
                    }

                    if ("reducedDebugInfo".equals(s1))
                    {
                        this.reducedDebugInfo = "true".equals(s2);
                    }

                    if ("useNativeTransport".equals(s1))
                    {
                        this.useNativeTransport = "true".equals(s2);
                    }

                    if ("entityShadows".equals(s1))
                    {
                        this.entityShadows = "true".equals(s2);
                    }

                    if ("mainHand".equals(s1))
                    {
                        this.mainHand = "left".equals(s2) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
                    }

                    if ("showSubtitles".equals(s1))
                    {
                        this.showSubtitles = "true".equals(s2);
                    }

                    if ("realmsNotifications".equals(s1))
                    {
                        this.realmsNotifications = "true".equals(s2);
                    }

                    if ("enableWeakAttacks".equals(s1))
                    {
                        this.enableWeakAttacks = "true".equals(s2);
                    }

                    if ("autoJump".equals(s1))
                    {
                        this.autoJump = "true".equals(s2);
                    }

                    if ("narrator".equals(s1))
                    {
                        this.narrator = Integer.parseInt(s2);
                    }

                    if ("hasCreatedDemoWorld".equals(s1))
                    {
                        this.hasCreatedDemoWorld = "true".equals(s2);
                    }

                    if ("relayTimeout".equals(s1))
                    {
                        this.relayTimeout = Integer.parseInt(s2);
                    }

                    if ("hideJoinCode".equals(s1))
                    {
                        this.hideJoinCode = "true".equals(s2);
                    }

                    if ("enableVsyncEag".equals(s1))
                    {
                        this.enableVsync = "true".equals(s2);
                    }

                    if ("allowBlockAlternatives".equals(s1))
                    {
                        this.allowBlockAlternatives = "true".equals(s2);
                    }

                    if ("hudFps".equals(s1))
                    {
                        this.hudFps = "true".equals(s2);
                    }

                    if ("hudWorld".equals(s1))
                    {
                        this.hudWorld = "true".equals(s2);
                    }

                    if ("hudCoords".equals(s1))
                    {
                        this.hudCoords = "true".equals(s2);
                    }

                    if ("hudPlayer".equals(s1))
                    {
                        this.hudPlayer = "true".equals(s2);
                    }

                    if ("hudStats".equals(s1))
                    {
                        this.hudStats = "true".equals(s2);
                    }

                    if ("hud24h".equals(s1))
                    {
                        this.hud24h = "true".equals(s2);
                    }

                    if ("chunkFix".equals(s1))
                    {
                        this.chunkFix = "true".equals(s2);
                    }

                    if ("fog".equals(s1))
                    {
                        this.fog = "true".equals(s2);
                    }

                    if ("fxaa".equals(s1))
                    {
                        this.fxaa = ("true".equals(s2) || "false".equals(s2)) ? 0 : Math.max(Math.min(Integer.parseInt(s2), 2), 0);
                    }

                    if ("shaders".equals(s1))
                    {
                        this.shaders = "true".equals(s2);
                    }

                    if ("enableUpdateSvc".equals(s1))
                    {
                        this.enableUpdateSvc = "true".equals(s2);
                    }

                    if ("voiceListenRadius".equals(s1))
                    {
                        this.voiceListenRadius = Integer.parseInt(s2);
                    }

                    if ("voiceListenVolume".equals(s1))
                    {
                        this.voiceListenVolume = this.parseFloat(s2);
                    }

                    if ("voiceSpeakVolume".equals(s1))
                    {
                        this.voiceSpeakVolume = this.parseFloat(s2);
                    }

                    if ("voicePTTKey".equals(s1))
                    {
                        this.voicePTTKey = Integer.parseInt(s2);
                    }

                    if ("enableFNAWSkins".equals(s1))
                    {
                        this.enableFNAWSkins = "true".equals(s2);
                    }

                    if ("enableDynamicLights".equals(s1))
                    {
                        this.enableDynamicLights = "true".equals(s2);
                    }

                    if ("hasHiddenPhishWarning".equals(s1))
                    {
                        this.hasHiddenPhishWarning = "true".equals(s2);
                    }

                    if ("enableProfanityFilter".equals(s1))
                    {
                        this.enableProfanityFilter = "true".equals(s2);
                    }

                    if ("hasShownProfanityFilter".equals(s1))
                    {
                        this.hasShownProfanityFilter = "true".equals(s2);
                    }

                    if ("screenRecordCodec".equals(s1))
                    {
                        EnumScreenRecordingCodec codec = EnumScreenRecordingCodec.valueOf(s2);
                        if (!ScreenRecordingController.codecs.contains(codec))
                        {
                            throw new IllegalStateException("Selected codec is not supported: " + codec.name);
                        }
                        this.screenRecordCodec = codec;
                    }

                    if ("screenRecordFPS".equals(s1))
                    {
                        this.screenRecordFPS = Integer.parseInt(s2);
                    }

                    if ("screenRecordResolution".equals(s1))
                    {
                        this.screenRecordResolution = Integer.parseInt(s2);
                    }

                    if ("screenRecordAudioBitrate".equals(s1))
                    {
                        this.screenRecordAudioBitrate = Integer.parseInt(s2);
                    }

                    if ("screenRecordVideoBitrate".equals(s1))
                    {
                        this.screenRecordVideoBitrate = Integer.parseInt(s2);
                    }

                    if ("screenRecordGameVolume".equals(s1))
                    {
                        this.screenRecordGameVolume = this.parseFloat(s2);
                    }

                    if ("screenRecordMicVolume".equals(s1))
                    {
                        this.screenRecordMicVolume = this.parseFloat(s2);
                    }

                    if ("touchControlOpacity".equals(s1))
                    {
                        this.touchControlOpacity = this.parseFloat(s2);
                    }

                    if ("hideDefaultUsernameWarning".equals(s1))
                    {
                        this.hideDefaultUsernameWarning = "true".equals(s2);
                    }

                    if ("hideVideoSettingsWarning".equals(s1))
                    {
                        this.hideVideoSettingsWarning = "true".equals(s2);
                    }

                    if ("betterGrassOF".equals(s1))
                    {
                        this.betterGrassOF = Integer.parseInt(s2);
                    }

                    if ("connectedTexturesOF".equals(s1))
                    {
                        this.connectedTexturesOF = Integer.parseInt(s2);
                    }

                    if ("customSkyOF".equals(s1))
                    {
                        this.customSkyOF = "true".equals(s2);
                    }

                    if ("smartLeavesOF".equals(s1))
                    {
                        this.smartLeavesOF = "true".equals(s2);
                    }

                    if ("customItemsOF".equals(s1))
                    {
                        this.customItemsOF = "true".equals(s2);
                    }

                    for (KeyBinding keybinding : this.keyBindings)
                    {
                        if (s1.equals("key_" + keybinding.getKeyDescription()))
                        {
                            if (s2.indexOf(':') != -1)
                            {
                                String[] t = s2.split(":");
                                keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.valueFromString(t[1]), Integer.parseInt(t[0]));
                            } else
                            keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, Integer.parseInt(s2));
                        }
                    }

                    for (SoundCategory soundcategory : SoundCategory.values())
                    {
                        if (s1.equals("soundCategory_" + soundcategory.getName()))
                        {
                            this.soundLevels.put(soundcategory, Float.valueOf(this.parseFloat(s2)));
                        }
                    }

                    for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values())
                    {
                        if (s1.equals("modelPart_" + enumplayermodelparts.getPartName()))
                        {
                            this.setModelPartEnabled(enumplayermodelparts, "true".equals(s2));
                        }
                    }

                    this.deferredShaderConf.readOption(s1, s2);
                }
                catch (Exception var11)
                {
                    LOGGER.warn("Skipping bad option: {}:{}", s1, s2);
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
            Keyboard.setFunctionKeyModifier(this.keyBindFunction.getKeyCode());
            VoiceClientController.setVoiceListenVolume(this.voiceListenVolume);
            VoiceClientController.setVoiceSpeakVolume(this.voiceSpeakVolume);
            VoiceClientController.setVoiceProximity(this.voiceListenRadius);
            ScreenRecordingController.setGameVolume(this.screenRecordGameVolume);
            ScreenRecordingController.setMicrophoneVolume(this.screenRecordMicVolume);

            if (this.mc.getRenderManager() != null)
            {
                this.mc.getRenderManager().setEnableFNAWSkins(this.enableFNAWSkins);
            }

            if (this.shaders && !EaglerDeferredPipeline.isSupported())
            {
                LOGGER.error("Setting shaders to false because they are not supported");
                this.shaders = false;
            }

            if (this.enableDynamicLights && !DynamicLightsStateManager.isSupported())
            {
                LOGGER.error("Setting dynamic lights to false because they are not supported");
                this.enableDynamicLights = false;
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Failed to load options", (Throwable)exception);
        }
    }

    private NBTTagCompound dataFix(NBTTagCompound p_189988_1_)
    {
        int i = 0;

        try
        {
            i = Integer.parseInt(p_189988_1_.getString("version"));
        }
        catch (RuntimeException var4)
        {
            ;
        }

        return this.mc.getDataFixer().process(FixTypes.OPTIONS, p_189988_1_, i);
    }

    private float parseFloat(String str)
    {
        if ("true".equals(str))
        {
            return 1.0F;
        }
        else
        {
            return "false".equals(str) ? 0.0F : Float.parseFloat(str);
        }
    }

    public void saveOptions()
    {
        if (net.minecraftforge.fml.client.FMLClientHandler.instance().isLoading()) return;
        byte[] data = writeOptions();

        if (data != null)
        {
            EagRuntime.setStorage("g", data);
        }

        RelayManager.relayManager.save();
        this.sendSettingsToServer();
    }

    public byte[] writeOptions()
    {
        EaglerOutputStream bao = new EaglerOutputStream();

        try (PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(EaglerZLIB.newGZIPOutputStream(bao), StandardCharsets.UTF_8)))
        {
            printwriter.println("version:1343");
            printwriter.println("invertYMouse:" + this.invertMouse);
            printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
            printwriter.println("fov:" + (this.fovSetting - 70.0F) / 40.0F);
            printwriter.println("gamma:" + this.gammaSetting);
            printwriter.println("saturation:" + this.saturation);
            printwriter.println("renderDistance:" + this.renderDistanceChunks);
            printwriter.println("guiScale:" + this.guiScale);
            printwriter.println("particles:" + this.particleSetting);
            printwriter.println("bobView:" + this.viewBobbing);
            printwriter.println("anaglyph3d:" + this.anaglyph);
            printwriter.println("maxFps:" + this.limitFramerate);
            printwriter.println("fboEnable:" + this.fboEnable);
            printwriter.println("difficulty:" + this.difficulty.getDifficultyId());
            printwriter.println("fancyGraphics:" + this.fancyGraphics);
            printwriter.println("ao:" + this.ambientOcclusion);

            switch (this.clouds)
            {
                case 0:
                    printwriter.println("renderClouds:false");
                    break;
                case 1:
                    printwriter.println("renderClouds:fast");
                    break;
                case 2:
                    printwriter.println("renderClouds:true");
            }

            printwriter.println("resourcePacks:" + toJSONArray(this.resourcePacks));
            printwriter.println("incompatibleResourcePacks:" + toJSONArray(this.incompatibleResourcePacks));
            printwriter.println("lastServer:" + this.lastServer);
            printwriter.println("lang:" + this.language);
            printwriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
            printwriter.println("chatColors:" + this.chatColours);
            printwriter.println("chatLinks:" + this.chatLinks);
            printwriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
            printwriter.println("chatOpacity:" + this.chatOpacity);
            printwriter.println("snooperEnabled:" + this.snooperEnabled);
            printwriter.println("fullscreen:" + this.fullScreen);
            printwriter.println("enableVsync:" + this.enableVsync);
            printwriter.println("useVbo:" + this.useVbo);
            printwriter.println("hideServerAddress:" + this.hideServerAddress);
            printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
            printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            printwriter.println("touchscreen:" + this.touchscreen);
            printwriter.println("overrideWidth:" + this.overrideWidth);
            printwriter.println("overrideHeight:" + this.overrideHeight);
            printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
            printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
            printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            printwriter.println("chatScale:" + this.chatScale);
            printwriter.println("chatWidth:" + this.chatWidth);
            printwriter.println("mipmapLevels:" + this.mipmapLevels);
            printwriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
            printwriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
            printwriter.println("useNativeTransport:" + this.useNativeTransport);
            printwriter.println("entityShadows:" + this.entityShadows);
            printwriter.println("mainHand:" + (this.mainHand == EnumHandSide.LEFT ? "left" : "right"));
            printwriter.println("attackIndicator:" + this.attackIndicator);
            printwriter.println("showSubtitles:" + this.showSubtitles);
            printwriter.println("realmsNotifications:" + this.realmsNotifications);
            printwriter.println("enableWeakAttacks:" + this.enableWeakAttacks);
            printwriter.println("autoJump:" + this.autoJump);
            printwriter.println("narrator:" + this.narrator);
            printwriter.println("tutorialStep:" + this.tutorialStep.getName());

            printwriter.println("hasCreatedDemoWorld:" + this.hasCreatedDemoWorld);
            printwriter.println("relayTimeout:" + this.relayTimeout);
            printwriter.println("hideJoinCode:" + this.hideJoinCode);
            printwriter.println("enableVsyncEag:" + this.enableVsync);
            printwriter.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
            printwriter.println("hudFps:" + this.hudFps);
            printwriter.println("hudWorld:" + this.hudWorld);
            printwriter.println("hudCoords:" + this.hudCoords);
            printwriter.println("hudPlayer:" + this.hudPlayer);
            printwriter.println("hudStats:" + this.hudStats);
            printwriter.println("hud24h:" + this.hud24h);
            printwriter.println("chunkFix:" + this.chunkFix);
            printwriter.println("fog:" + this.fog);
            printwriter.println("fxaa:" + this.fxaa);
            printwriter.println("shaders:" + this.shaders);
            printwriter.println("enableUpdateSvc:" + this.enableUpdateSvc);
            printwriter.println("voiceListenRadius:" + this.voiceListenRadius);
            printwriter.println("voiceListenVolume:" + this.voiceListenVolume);
            printwriter.println("voiceSpeakVolume:" + this.voiceSpeakVolume);
            printwriter.println("voicePTTKey:" + this.voicePTTKey);
            printwriter.println("enableFNAWSkins:" + this.enableFNAWSkins);
            printwriter.println("enableDynamicLights:" + this.enableDynamicLights);
            printwriter.println("hasHiddenPhishWarning:" + this.hasHiddenPhishWarning);
            printwriter.println("enableProfanityFilter:" + this.enableProfanityFilter);
            printwriter.println("hasShownProfanityFilter:" + this.hasShownProfanityFilter);

            if (this.screenRecordCodec != null)
            {
                printwriter.println("screenRecordCodec:" + this.screenRecordCodec);
            }

            printwriter.println("screenRecordFPS:" + this.screenRecordFPS);
            printwriter.println("screenRecordResolution:" + this.screenRecordResolution);
            printwriter.println("screenRecordAudioBitrate:" + this.screenRecordAudioBitrate);
            printwriter.println("screenRecordVideoBitrate:" + this.screenRecordVideoBitrate);
            printwriter.println("screenRecordGameVolume:" + this.screenRecordGameVolume);
            printwriter.println("screenRecordMicVolume:" + this.screenRecordMicVolume);
            printwriter.println("touchControlOpacity:" + this.touchControlOpacity);
            printwriter.println("hideDefaultUsernameWarning:" + this.hideDefaultUsernameWarning);
            printwriter.println("hideVideoSettingsWarning:" + this.hideVideoSettingsWarning);
            printwriter.println("betterGrassOF:" + this.betterGrassOF);
            printwriter.println("connectedTexturesOF:" + this.connectedTexturesOF);
            printwriter.println("customSkyOF:" + this.customSkyOF);
            printwriter.println("smartLeavesOF:" + this.smartLeavesOF);
            printwriter.println("customItemsOF:" + this.customItemsOF);

            for (KeyBinding keybinding : this.keyBindings)
            {
                String keyString = "key_" + keybinding.getKeyDescription() + ":" + keybinding.getKeyCode();
                printwriter.println(keybinding.getKeyModifier() != net.minecraftforge.client.settings.KeyModifier.NONE ? keyString + ":" + keybinding.getKeyModifier() : keyString);
            }

            for (SoundCategory soundcategory : SoundCategory.values())
            {
                printwriter.println("soundCategory_" + soundcategory.getName() + ":" + this.getSoundLevel(soundcategory));
            }

            for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values())
            {
                printwriter.println("modelPart_" + enumplayermodelparts.getPartName() + ":" + this.setModelParts.contains(enumplayermodelparts));
            }

            Keyboard.setFunctionKeyModifier(this.keyBindFunction.getKeyCode());
            this.deferredShaderConf.writeOptions(printwriter);
        }
        catch (Exception exception)
        {
            LOGGER.error("Failed to save options", (Throwable)exception);
            return null;
        }

        return bao.toByteArray();
    }

    private static String toJSONArray(List<String> list)
    {
        JSONArray arr = new JSONArray();

        for (int i = 0, l = list.size(); i < l; ++i)
        {
            arr.put(list.get(i));
        }

        return arr.toString();
    }

    private static List<String> fromJSONArray(String json)
    {
        JSONArray arr = new JSONArray(json);
        List<String> ret = Lists.<String>newArrayListWithCapacity(arr.length());

        for (int i = 0, l = arr.length(); i < l; ++i)
        {
            ret.add(arr.getString(i));
        }

        return ret;
    }

    public int checkBadVideoSettings()
    {
        return this.hideVideoSettingsWarning ? 0 : ((this.renderDistanceChunks > 6 ? GuiScreenVideoSettingsWarning.WARNING_RENDER_DISTANCE : 0) | (!this.enableVsync ? GuiScreenVideoSettingsWarning.WARNING_VSYNC : 0) | (this.limitFramerate < 30 ? GuiScreenVideoSettingsWarning.WARNING_FRAME_LIMIT : 0));
    }

    public void fixBadVideoSettings()
    {
        if (this.renderDistanceChunks > 6)
        {
            this.renderDistanceChunks = 4;
        }

        if (!this.enableVsync)
        {
            this.enableVsync = true;
        }

        if (this.limitFramerate < 30)
        {
            this.limitFramerate = 260;
        }
    }

    public float getSoundLevel(SoundCategory category)
    {
        return this.soundLevels.containsKey(category) ? ((Float)this.soundLevels.get(category)).floatValue() : 1.0F;
    }

    public void setSoundLevel(SoundCategory category, float volume)
    {
        this.mc.getSoundHandler().setSoundLevel(category, volume);
        this.soundLevels.put(category, Float.valueOf(volume));
    }

    public void sendSettingsToServer()
    {
        if (this.mc.player != null)
        {
            int i = 0;

            for (EnumPlayerModelParts enumplayermodelparts : this.setModelParts)
            {
                i |= enumplayermodelparts.getPartMask();
            }

            this.mc.player.connection.sendPacket(new CPacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i, this.mainHand));
        }
    }

    public Set<EnumPlayerModelParts> getModelParts()
    {
        return ImmutableSet.copyOf(this.setModelParts);
    }

    public void setModelPartEnabled(EnumPlayerModelParts modelPart, boolean enable)
    {
        if (enable)
        {
            this.setModelParts.add(modelPart);
        }
        else
        {
            this.setModelParts.remove(modelPart);
        }

        this.sendSettingsToServer();
    }

    public void switchModelPartEnabled(EnumPlayerModelParts modelPart)
    {
        if (this.getModelParts().contains(modelPart))
        {
            this.setModelParts.remove(modelPart);
        }
        else
        {
            this.setModelParts.add(modelPart);
        }

        this.sendSettingsToServer();
    }

    public int shouldRenderClouds()
    {
        return this.renderDistanceChunks >= 4 ? this.clouds : 0;
    }

    public boolean isUsingNativeTransport()
    {
        return this.useNativeTransport;
    }

    @SideOnly(Side.CLIENT)
    public static enum Options
    {
        INVERT_MOUSE("options.invertMouse", false, true),
        SENSITIVITY("options.sensitivity", true, false),
        FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
        GAMMA("options.gamma", true, false),
        SATURATION("options.saturation", true, false),
        RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
        VIEW_BOBBING("options.viewBobbing", false, true),
        ANAGLYPH("options.anaglyph", false, true),
        FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0F, 260.0F, 10.0F),
        FBO_ENABLE("options.fboEnable", false, true),
        RENDER_CLOUDS("options.renderClouds", false, false),
        GRAPHICS("options.graphics", false, false),
        AMBIENT_OCCLUSION("options.ao", false, false),
        GUI_SCALE("options.guiScale", false, false),
        PARTICLES("options.particles", false, false),
        CHAT_VISIBILITY("options.chat.visibility", false, false),
        CHAT_COLOR("options.chat.color", false, true),
        CHAT_LINKS("options.chat.links", false, true),
        CHAT_OPACITY("options.chat.opacity", true, false),
        CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
        SNOOPER_ENABLED("options.snooper", false, true),
        USE_FULLSCREEN("options.fullscreen", false, true),
        ENABLE_VSYNC("options.vsync", false, true),
        USE_VBO("options.vbo", false, true),
        TOUCHSCREEN("options.touchscreen", false, true),
        CHAT_SCALE("options.chat.scale", true, false),
        CHAT_WIDTH("options.chat.width", true, false),
        CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
        CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
        MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
        FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
        REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
        ENTITY_SHADOWS("options.entityShadows", false, true),
        MAIN_HAND("options.mainHand", false, false),
        ATTACK_INDICATOR("options.attackIndicator", false, false),
        ENABLE_WEAK_ATTACKS("options.enableWeakAttacks", false, true),
        SHOW_SUBTITLES("options.showSubtitles", false, true),
        REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
        AUTO_JUMP("options.autoJump", false, true),
        NARRATOR("options.narrator", false, false),
        BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
        HUD_FPS("options.hud.fps", false, true),
        HUD_COORDS("options.hud.coords", false, true),
        HUD_STATS("options.hud.stats", false, true),
        HUD_WORLD("options.hud.world", false, true),
        HUD_PLAYER("options.hud.player", false, true),
        HUD_24H("options.hud.24h", false, true),
        CHUNK_FIX("options.chunkFix", false, true),
        FOG("options.fog", false, true),
        FXAA("options.fxaa", false, false),
        FULLSCREEN("options.fullscreen", false, true),
        FNAW_SKINS("options.skinCustomisation.enableFNAWSkins", false, true),
        EAGLER_VSYNC("options.vsync", false, true),
        EAGLER_DYNAMIC_LIGHTS("options.dynamicLights", false, true),
        EAGLER_PROFANITY_FILTER("options.profanityFilterButton", false, true),
        EAGLER_TOUCH_CONTROL_OPACITY("options.touchControlOpacity", true, false),
        OF_CONNECTED_TEXTURES("options.connectedTexturesOF", false, false),
        OF_BETTER_GRASS("options.betterGrassOF", false, false),
        OF_CUSTOM_SKIES("options.customSkiesOF", false, true),
        OF_SMART_LEAVES("options.smartLeavesOF", false, true),
        OF_CUSTOM_ITEMS("options.customItemsOF", false, true);

        private final boolean isFloat;
        private final boolean isBoolean;
        private final String translation;
        private final float valueStep;
        private float valueMin;
        private float valueMax;

        public static GameSettings.Options byOrdinal(int ordinal)
        {
            for (GameSettings.Options gamesettings$options : values())
            {
                if (gamesettings$options.getOrdinal() == ordinal)
                {
                    return gamesettings$options;
                }
            }

            return null;
        }

        private Options(String translation, boolean isFloat, boolean isBoolean)
        {
            this(translation, isFloat, isBoolean, 0.0F, 1.0F, 0.0F);
        }

        private Options(String translation, boolean isFloat, boolean isBoolean, float valMin, float valMax, float valStep)
        {
            this.translation = translation;
            this.isFloat = isFloat;
            this.isBoolean = isBoolean;
            this.valueMin = valMin;
            this.valueMax = valMax;
            this.valueStep = valStep;
        }

        public boolean isFloat()
        {
            return this.isFloat;
        }

        public boolean isBoolean()
        {
            return this.isBoolean;
        }

        public int getOrdinal()
        {
            return this.ordinal();
        }

        public String getTranslation()
        {
            return this.translation;
        }

        public float getValueMin()
        {
            return this.valueMin;
        }

        public float getValueMax()
        {
            return this.valueMax;
        }

        public void setValueMax(float value)
        {
            this.valueMax = value;
        }

        public float normalizeValue(float value)
        {
            return MathHelper.clamp((this.snapToStepClamp(value) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }

        public float denormalizeValue(float value)
        {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp(value, 0.0F, 1.0F));
        }

        public float snapToStepClamp(float value)
        {
            value = this.snapToStep(value);
            return MathHelper.clamp(value, this.valueMin, this.valueMax);
        }

        private float snapToStep(float value)
        {
            if (this.valueStep > 0.0F)
            {
                value = this.valueStep * (float)Math.round(value / this.valueStep);
            }

            return value;
        }
    }

    /******* Forge Start ***********/
    private void setForgeKeybindProperties() {
        net.minecraftforge.client.settings.KeyConflictContext inGame = net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;
        keyBindForward.setKeyConflictContext(inGame);
        keyBindLeft.setKeyConflictContext(inGame);
        keyBindBack.setKeyConflictContext(inGame);
        keyBindRight.setKeyConflictContext(inGame);
        keyBindJump.setKeyConflictContext(inGame);
        keyBindSneak.setKeyConflictContext(inGame);
        keyBindSprint.setKeyConflictContext(inGame);
        keyBindAttack.setKeyConflictContext(inGame);
        keyBindChat.setKeyConflictContext(inGame);
        keyBindPlayerList.setKeyConflictContext(inGame);
        keyBindCommand.setKeyConflictContext(inGame);
        keyBindTogglePerspective.setKeyConflictContext(inGame);
        keyBindSmoothCamera.setKeyConflictContext(inGame);
        keyBindSwapHands.setKeyConflictContext(inGame);
    }

    // FORGE: fix for MC-64581 very laggy mipmap slider
    private boolean needsBlockModelRefresh = false;
    public void onGuiClosed()
    {
        if (needsBlockModelRefresh)
        {
            net.minecraftforge.fml.client.FMLClientHandler.instance().scheduleResourcesRefresh(net.minecraftforge.client.resource.VanillaResourceType.MODELS);
            this.needsBlockModelRefresh = false;
        }
    }
    /******* Forge End ***********/
}
