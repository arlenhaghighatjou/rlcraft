package net.minecraft.client.network;

import com.google.common.collect.Maps;
import net.lax1dude.eaglercraft.v1_8.futures.Futures;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.renderer.debug.DebugRendererNeighborsUpdate;
import net.minecraft.client.renderer.debug.DebugRendererPathfinding;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.ClientUUIDLoadingCache;
import net.lax1dude.eaglercraft.v1_8.notifications.ServerNotificationManager;
import net.lax1dude.eaglercraft.v1_8.skin_cache.ServerTextureCache;
import net.lax1dude.eaglercraft.v1_8.socket.EaglercraftNetworkManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageConstants;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.client.ClientMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.message.InjectedMessageController;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.message.LegacyMessageController;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.message.MessageController;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.handshake.StandardCaps;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANClientNetworkManager;
import net.lax1dude.eaglercraft.v1_8.sp.socket.ClientIntegratedServerNetworkManager;
import net.lax1dude.eaglercraft.v1_8.voice.VoiceClientController;
import net.lax1dude.eaglercraft.v1_8.webview.WebViewOverlayController;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerFolderResourcePack;

@SideOnly(Side.CLIENT)
public class NetHandlerPlayClient implements INetHandlerPlayClient
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final EaglercraftNetworkManager netManager;
    private final GameProfile profile;
    private final GuiScreen guiScreenServer;
    private Minecraft gameController;
    private WorldClient clientWorldController;
    private boolean doneLoadingTerrain;
    private final Map<EaglercraftUUID, NetworkPlayerInfo> playerInfoMap = Maps.<EaglercraftUUID, NetworkPlayerInfo>newHashMap();
    public int currentServerMaxPlayers = 20;
    private boolean hasStatistics;
    private final ClientAdvancementManager advancementManager;
    private final EaglercraftRandom avRandomizer = new EaglercraftRandom();
    private boolean isIntegratedServer = false;
    private final ServerTextureCache textureCache;
    private final ServerNotificationManager notifManager;
    public boolean currentFNAWSkinAllowedState = true;
    public boolean currentFNAWSkinForcedState = true;
    private final MessageController eaglerMessageController;
    public byte[] cachedServerInfoHash = null;
    public byte[] cachedServerInfoData = null;
    public boolean allowedDisplayWebview = false;
    public boolean allowedDisplayWebviewYes = false;

    public NetHandlerPlayClient(Minecraft mcIn, GuiScreen p_i46300_2_, EaglercraftNetworkManager networkManagerIn, GameProfile profileIn, GamePluginMessageProtocol eaglerProtocol)
    {
        this.gameController = mcIn;
        this.guiScreenServer = p_i46300_2_;
        this.netManager = networkManagerIn;
        this.netManager.setNetHandler(this);
        this.profile = profileIn;
        this.advancementManager = new ClientAdvancementManager(mcIn);
        this.isIntegratedServer = (networkManagerIn instanceof ClientIntegratedServerNetworkManager) || (networkManagerIn instanceof LANClientNetworkManager);
        ClientMessageHandler handler = ClientMessageHandler.createClientHandler(eaglerProtocol.ver, this);

        if (eaglerProtocol.ver >= 5)
        {
            this.eaglerMessageController = new InjectedMessageController(eaglerProtocol, handler, GamePluginMessageConstants.CLIENT_TO_SERVER, networkManagerIn::injectRawFrame);
            networkManagerIn.setInjectedMessageController((InjectedMessageController)this.eaglerMessageController);
        }
        else
        {
            this.eaglerMessageController = new LegacyMessageController(eaglerProtocol, handler, GamePluginMessageConstants.CLIENT_TO_SERVER, (ch, msg) -> this.sendPacket(new CPacketCustomPayload(ch, msg)));
        }

        this.textureCache = ServerTextureCache.create(this, mcIn.getTextureManager());
        this.notifManager = new ServerNotificationManager(mcIn.getTextureManager());
        net.minecraftforge.fml.client.FMLClientHandler.instance().setPlayClient(this);
        net.minecraftforge.fml.common.network.internal.FMLNetworkHandler.fmlClientHandshake(networkManagerIn);
    }

    public void cleanup()
    {
        this.clientWorldController = null;
        this.textureCache.destroy();
        this.notifManager.destroy();
    }

    public ServerTextureCache getTextureCache()
    {
        return this.textureCache;
    }

    public ServerNotificationManager getNotifManager()
    {
        return this.notifManager;
    }

    public MessageController getEaglerMessageController()
    {
        return this.eaglerMessageController;
    }

    public GamePluginMessageProtocol getEaglerMessageProtocol()
    {
        return this.eaglerMessageController != null ? this.eaglerMessageController.getProtocol() : null;
    }

    public void sendEaglerMessage(GameMessagePacket packet)
    {
        this.eaglerMessageController.sendPacket(packet);
    }

    public boolean webViewSendHandler(GameMessagePacket pkt)
    {
        if (this.eaglerMessageController == null)
        {
            return false;
        }
        else if (this.gameController.player == null || this.gameController.player.connection != this)
        {
            LOGGER.error("WebView sent message on a dead handler!");
            return false;
        }
        else if (this.eaglerMessageController.getProtocol().ver >= 4)
        {
            this.sendEaglerMessage(pkt);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isClientInEaglerSingleplayerOrLAN()
    {
        return this.isIntegratedServer;
    }

    public void handleJoinGame(SPacketJoinGame packetIn)
    {
        this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
        net.minecraftforge.fml.common.network.handshake.NetworkDispatcher dispatcher = net.minecraftforge.fml.common.network.handshake.NetworkDispatcher.get(this.netManager);
        this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()), dispatcher != null ? dispatcher.getOverrideDimension(packetIn) : packetIn.getDimension(), packetIn.getDifficulty(), this.gameController.mcProfiler);
        this.gameController.gameSettings.difficulty = packetIn.getDifficulty();
        this.gameController.loadWorld(this.clientWorldController);
        this.gameController.player.dimension = packetIn.getDimension();
        this.gameController.displayGuiScreen(new GuiDownloadTerrain());
        this.gameController.player.setEntityId(packetIn.getPlayerId());
        this.currentServerMaxPlayers = packetIn.getMaxPlayers();
        this.gameController.player.setReducedDebug(packetIn.isReducedDebugInfo());
        this.gameController.playerController.setGameType(packetIn.getGameType());
        this.gameController.gameSettings.sendSettingsToServer();
        this.netManager.sendPacket(new CPacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));

        if (VoiceClientController.isClientSupported())
        {
            if (this.netManager.getServerCapabilities().hasCapability(StandardCaps.VOICE, 0))
            {
                VoiceClientController.initializeVoiceClient(this::sendEaglerMessage, this.eaglerMessageController.getProtocol().ver);
            }
            else
            {
                VoiceClientController.initializeVoiceClient(null, -1);
            }
        }

        if (this.netManager.getServerCapabilities().hasCapability(StandardCaps.WEBVIEW, 0))
        {
            WebViewOverlayController.setPacketSendCallback(this::webViewSendHandler);
        }
        else
        {
            WebViewOverlayController.setPacketSendCallback(null);
        }
    }

    public void handleSpawnObject(SPacketSpawnObject packetIn)
    {
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        Entity entity = null;

        if (packetIn.getType() == 10)
        {
            entity = EntityMinecart.create(this.clientWorldController, d0, d1, d2, EntityMinecart.Type.getById(packetIn.getData()));
        }
        else if (packetIn.getType() == 90)
        {
            Entity entity1 = this.clientWorldController.getEntityByID(packetIn.getData());

            if (entity1 instanceof EntityPlayer)
            {
                entity = new EntityFishHook(this.clientWorldController, (EntityPlayer)entity1, d0, d1, d2);
            }

            packetIn.setData(0);
        }
        else if (packetIn.getType() == 60)
        {
            entity = new EntityTippedArrow(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 91)
        {
            entity = new EntitySpectralArrow(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 61)
        {
            entity = new EntitySnowball(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 68)
        {
            entity = new EntityLlamaSpit(this.clientWorldController, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
        }
        else if (packetIn.getType() == 71)
        {
            entity = new EntityItemFrame(this.clientWorldController, new BlockPos(d0, d1, d2), EnumFacing.getHorizontal(packetIn.getData()));
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 77)
        {
            entity = new EntityLeashKnot(this.clientWorldController, new BlockPos(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2)));
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 65)
        {
            entity = new EntityEnderPearl(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 72)
        {
            entity = new EntityEnderEye(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 76)
        {
            entity = new EntityFireworkRocket(this.clientWorldController, d0, d1, d2, ItemStack.EMPTY);
        }
        else if (packetIn.getType() == 63)
        {
            entity = new EntityLargeFireball(this.clientWorldController, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 93)
        {
            entity = new EntityDragonFireball(this.clientWorldController, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 64)
        {
            entity = new EntitySmallFireball(this.clientWorldController, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 66)
        {
            entity = new EntityWitherSkull(this.clientWorldController, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 67)
        {
            entity = new EntityShulkerBullet(this.clientWorldController, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 62)
        {
            entity = new EntityEgg(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 79)
        {
            entity = new EntityEvokerFangs(this.clientWorldController, d0, d1, d2, 0.0F, 0, (EntityLivingBase)null);
        }
        else if (packetIn.getType() == 73)
        {
            entity = new EntityPotion(this.clientWorldController, d0, d1, d2, ItemStack.EMPTY);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 75)
        {
            entity = new EntityExpBottle(this.clientWorldController, d0, d1, d2);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 1)
        {
            entity = new EntityBoat(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 50)
        {
            entity = new EntityTNTPrimed(this.clientWorldController, d0, d1, d2, (EntityLivingBase)null);
        }
        else if (packetIn.getType() == 78)
        {
            entity = new EntityArmorStand(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 51)
        {
            entity = new EntityEnderCrystal(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 2)
        {
            entity = new EntityItem(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.getType() == 70)
        {
            entity = new EntityFallingBlock(this.clientWorldController, d0, d1, d2, Block.getStateById(packetIn.getData() & 65535));
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 3)
        {
            entity = new EntityAreaEffectCloud(this.clientWorldController, d0, d1, d2);
        }

        if (entity != null)
        {
            EntityTracker.updateServerPosition(entity, d0, d1, d2);
            entity.rotationPitch = (float)(packetIn.getPitch() * 360) / 256.0F;
            entity.rotationYaw = (float)(packetIn.getYaw() * 360) / 256.0F;
            Entity[] aentity = entity.getParts();

            if (aentity != null)
            {
                int i = packetIn.getEntityID() - entity.getEntityId();

                for (Entity entity2 : aentity)
                {
                    entity2.setEntityId(entity2.getEntityId() + i);
                }
            }

            entity.setEntityId(packetIn.getEntityID());
            entity.setUniqueId(packetIn.getUniqueId());
            this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entity);

            if (packetIn.getData() > 0)
            {
                if (packetIn.getType() == 60 || packetIn.getType() == 91)
                {
                    Entity entity3 = this.clientWorldController.getEntityByID(packetIn.getData() - 1);

                    if (entity3 instanceof EntityLivingBase && entity instanceof EntityArrow)
                    {
                        ((EntityArrow)entity).shootingEntity = entity3;
                    }
                }

                entity.setVelocity((double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            }
        }
    }

    public void handleSpawnExperienceOrb(SPacketSpawnExperienceOrb packetIn)
    {
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        Entity entity = new EntityXPOrb(this.clientWorldController, d0, d1, d2, packetIn.getXPValue());
        EntityTracker.updateServerPosition(entity, d0, d1, d2);
        entity.rotationYaw = 0.0F;
        entity.rotationPitch = 0.0F;
        entity.setEntityId(packetIn.getEntityID());
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entity);
    }

    public void handleSpawnGlobalEntity(SPacketSpawnGlobalEntity packetIn)
    {
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        Entity entity = null;

        if (packetIn.getType() == 1)
        {
            entity = new EntityLightningBolt(this.clientWorldController, d0, d1, d2, false);
        }

        if (entity != null)
        {
            EntityTracker.updateServerPosition(entity, d0, d1, d2);
            entity.rotationYaw = 0.0F;
            entity.rotationPitch = 0.0F;
            entity.setEntityId(packetIn.getEntityId());
            this.clientWorldController.addWeatherEffect(entity);
        }
    }

    public void handleSpawnPainting(SPacketSpawnPainting packetIn)
    {
        EntityPainting entitypainting = new EntityPainting(this.clientWorldController, packetIn.getPosition(), packetIn.getFacing(), packetIn.getTitle());
        entitypainting.setUniqueId(packetIn.getUniqueId());
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entitypainting);
    }

    public void handleEntityVelocity(SPacketEntityVelocity packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (entity != null)
        {
            entity.setVelocity((double)packetIn.getMotionX() / 8000.0D, (double)packetIn.getMotionY() / 8000.0D, (double)packetIn.getMotionZ() / 8000.0D);
        }
    }

    public void handleEntityMetadata(SPacketEntityMetadata packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity != null && packetIn.getDataManagerEntries() != null)
        {
            entity.getDataManager().setEntryValues(packetIn.getDataManagerEntries());
        }
    }

    public void handleSpawnPlayer(SPacketSpawnPlayer packetIn)
    {
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        float f = (float)(packetIn.getYaw() * 360) / 256.0F;
        float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;
        EntityOtherPlayerMP entityotherplayermp = new EntityOtherPlayerMP(this.gameController.world, this.getPlayerInfo(packetIn.getUniqueId()).getGameProfile());
        entityotherplayermp.prevPosX = d0;
        entityotherplayermp.lastTickPosX = d0;
        entityotherplayermp.prevPosY = d1;
        entityotherplayermp.lastTickPosY = d1;
        entityotherplayermp.prevPosZ = d2;
        entityotherplayermp.lastTickPosZ = d2;
        EntityTracker.updateServerPosition(entityotherplayermp, d0, d1, d2);
        entityotherplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityotherplayermp);
        List < EntityDataManager.DataEntry<? >> list = packetIn.getDataManagerEntries();

        if (list != null)
        {
            entityotherplayermp.getDataManager().setEntryValues(list);
        }
    }

    public void handleEntityTeleport(SPacketEntityTeleport packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity != null)
        {
            double d0 = packetIn.getX();
            double d1 = packetIn.getY();
            double d2 = packetIn.getZ();
            EntityTracker.updateServerPosition(entity, d0, d1, d2);

            if (!entity.canPassengerSteer())
            {
                float f = (float)(packetIn.getYaw() * 360) / 256.0F;
                float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;

                if (Math.abs(entity.posX - d0) < 0.03125D && Math.abs(entity.posY - d1) < 0.015625D && Math.abs(entity.posZ - d2) < 0.03125D)
                {
                    entity.setPositionAndRotationDirect(entity.posX, entity.posY, entity.posZ, f, f1, 0, true);
                }
                else
                {
                    entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, true);
                }

                entity.onGround = packetIn.getOnGround();
            }
        }
    }

    public void handleHeldItemChange(SPacketHeldItemChange packetIn)
    {

        if (InventoryPlayer.isHotbar(packetIn.getHeldItemHotbarIndex()))
        {
            this.gameController.player.inventory.currentItem = packetIn.getHeldItemHotbarIndex();
        }
    }

    public void handleEntityMovement(SPacketEntity packetIn)
    {
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity != null)
        {
            entity.serverPosX += (long)packetIn.getX();
            entity.serverPosY += (long)packetIn.getY();
            entity.serverPosZ += (long)packetIn.getZ();
            double d0 = (double)entity.serverPosX / 4096.0D;
            double d1 = (double)entity.serverPosY / 4096.0D;
            double d2 = (double)entity.serverPosZ / 4096.0D;

            if (!entity.canPassengerSteer())
            {
                float f = packetIn.isRotating() ? (float)(packetIn.getYaw() * 360) / 256.0F : entity.rotationYaw;
                float f1 = packetIn.isRotating() ? (float)(packetIn.getPitch() * 360) / 256.0F : entity.rotationPitch;
                entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, false);
                entity.onGround = packetIn.getOnGround();
            }
        }
    }

    public void handleEntityHeadLook(SPacketEntityHeadLook packetIn)
    {
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity != null)
        {
            float f = (float)(packetIn.getYaw() * 360) / 256.0F;
            entity.setRotationYawHead(f);
        }
    }

    public void handleDestroyEntities(SPacketDestroyEntities packetIn)
    {

        for (int i = 0; i < packetIn.getEntityIDs().length; ++i)
        {
            this.clientWorldController.removeEntityFromWorld(packetIn.getEntityIDs()[i]);
        }
    }

    public void handlePlayerPosLook(SPacketPlayerPosLook packetIn)
    {
        EntityPlayer entityplayer = this.gameController.player;
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        float f = packetIn.getYaw();
        float f1 = packetIn.getPitch();

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X))
        {
            d0 += entityplayer.posX;
        }
        else
        {
            entityplayer.motionX = 0.0D;
        }

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y))
        {
            d1 += entityplayer.posY;
        }
        else
        {
            entityplayer.motionY = 0.0D;
        }

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z))
        {
            d2 += entityplayer.posZ;
        }
        else
        {
            entityplayer.motionZ = 0.0D;
        }

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT))
        {
            f1 += entityplayer.rotationPitch;
        }

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT))
        {
            f += entityplayer.rotationYaw;
        }

        entityplayer.setPositionAndRotation(d0, d1, d2, f, f1);
        this.netManager.sendPacket(new CPacketConfirmTeleport(packetIn.getTeleportId()));
        this.netManager.sendPacket(new CPacketPlayer.PositionRotation(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false));

        if (!this.doneLoadingTerrain)
        {
            this.gameController.player.prevPosX = this.gameController.player.posX;
            this.gameController.player.prevPosY = this.gameController.player.posY;
            this.gameController.player.prevPosZ = this.gameController.player.posZ;
            this.doneLoadingTerrain = true;
            this.gameController.displayGuiScreen((GuiScreen)null);
        }
    }

    public void handleMultiBlockChange(SPacketMultiBlockChange packetIn)
    {

        for (SPacketMultiBlockChange.BlockUpdateData spacketmultiblockchange$blockupdatedata : packetIn.getChangedBlocks())
        {
            this.clientWorldController.invalidateRegionAndSetBlock(spacketmultiblockchange$blockupdatedata.getPos(), spacketmultiblockchange$blockupdatedata.getBlockState());
        }
    }

    public void handleChunkData(SPacketChunkData packetIn)
    {

        if (packetIn.isFullChunk())
        {
            this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), true);
        }

        this.clientWorldController.invalidateBlockReceiveRegion(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);
        Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(packetIn.getChunkX(), packetIn.getChunkZ());
        chunk.read(packetIn.getReadBuffer(), packetIn.getExtractedSize(), packetIn.isFullChunk());
        this.clientWorldController.markBlockRangeForRenderUpdate(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);

        if (!packetIn.isFullChunk() || this.clientWorldController.provider.shouldClientCheckLighting())
        {
            chunk.resetRelightChecks();
        }

        for (NBTTagCompound nbttagcompound : packetIn.getTileEntityTags())
        {
            BlockPos blockpos = new BlockPos(nbttagcompound.getInteger("x"), nbttagcompound.getInteger("y"), nbttagcompound.getInteger("z"));
            TileEntity tileentity = this.clientWorldController.getTileEntity(blockpos);

            if (tileentity != null)
            {
                tileentity.handleUpdateTag(nbttagcompound);
            }
        }
    }

    public void processChunkUnload(SPacketUnloadChunk packetIn)
    {
        this.clientWorldController.doPreChunk(packetIn.getX(), packetIn.getZ(), false);
    }

    public void handleBlockChange(SPacketBlockChange packetIn)
    {
        this.clientWorldController.invalidateRegionAndSetBlock(packetIn.getBlockPosition(), packetIn.getBlockState());
    }

    public void handleDisconnect(SPacketDisconnect packetIn)
    {
        this.netManager.closeChannel(packetIn.getReason());
    }

    public void onDisconnect(ITextComponent reason)
    {
        VoiceClientController.handleServerDisconnect();
        Minecraft.getMinecraft().getRenderManager().setEnableFNAWSkins(this.gameController.gameSettings.enableFNAWSkins);

        if (this.gameController.world != null)
        {
            this.gameController.loadWorld((WorldClient)null);
        }

        if (this.guiScreenServer != null)
        {
            this.gameController.shutdownIntegratedServer(new GuiDisconnected(this.guiScreenServer, "disconnect.lost", reason));
        }
        else
        {
            this.gameController.shutdownIntegratedServer(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason));
        }
    }

    public void sendPacket(Packet<?> packetIn)
    {
        this.netManager.sendPacket(packetIn);
    }

    public void handleCollectItem(SPacketCollectItem packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getCollectedItemEntityID());
        EntityLivingBase entitylivingbase = (EntityLivingBase)this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (entitylivingbase == null)
        {
            entitylivingbase = this.gameController.player;
        }

        if (entity != null)
        {
            if (entity instanceof EntityXPOrb)
            {
                this.clientWorldController.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.35F + 0.9F, false);
            }
            else
            {
                this.clientWorldController.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 1.4F + 2.0F, false);
            }

            if (entity instanceof EntityItem)
            {
                ((EntityItem)entity).getItem().setCount(packetIn.getAmount());
            }

            this.gameController.effectRenderer.addEffect(new ParticleItemPickup(this.clientWorldController, entity, entitylivingbase, 0.5F));
            this.clientWorldController.removeEntityFromWorld(packetIn.getCollectedItemEntityID());
        }
    }

    public void handleChat(SPacketChat packetIn)
    {
        net.minecraft.util.text.ITextComponent message = net.minecraftforge.event.ForgeEventFactory.onClientChat(packetIn.getType(), packetIn.getChatComponent());
        if (message == null) return;
        this.gameController.ingameGUI.addChatMessage(packetIn.getType(), message);
    }

    public void handleAnimation(SPacketAnimation packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (entity != null)
        {
            if (packetIn.getAnimationType() == 0)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
                entitylivingbase.swingArm(EnumHand.MAIN_HAND);
            }
            else if (packetIn.getAnimationType() == 3)
            {
                EntityLivingBase entitylivingbase1 = (EntityLivingBase)entity;
                entitylivingbase1.swingArm(EnumHand.OFF_HAND);
            }
            else if (packetIn.getAnimationType() == 1)
            {
                entity.performHurtAnimation();
            }
            else if (packetIn.getAnimationType() == 2)
            {
                EntityPlayer entityplayer = (EntityPlayer)entity;
                entityplayer.wakeUpPlayer(false, false, false);
            }
            else if (packetIn.getAnimationType() == 4)
            {
                this.gameController.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
            }
            else if (packetIn.getAnimationType() == 5)
            {
                this.gameController.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC);
            }
        }
    }

    public void handleUseBed(SPacketUseBed packetIn)
    {
        packetIn.getPlayer(this.clientWorldController).trySleep(packetIn.getBedPosition());
    }

    public void handleSpawnMob(SPacketSpawnMob packetIn)
    {
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        float f = (float)(packetIn.getYaw() * 360) / 256.0F;
        float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;
        EntityLivingBase entitylivingbase = (EntityLivingBase)EntityList.createEntityByID(packetIn.getEntityType(), this.gameController.world);

        if (entitylivingbase != null)
        {
            EntityTracker.updateServerPosition(entitylivingbase, d0, d1, d2);
            entitylivingbase.renderYawOffset = (float)(packetIn.getHeadPitch() * 360) / 256.0F;
            entitylivingbase.rotationYawHead = (float)(packetIn.getHeadPitch() * 360) / 256.0F;
            Entity[] aentity = entitylivingbase.getParts();

            if (aentity != null)
            {
                int i = packetIn.getEntityID() - entitylivingbase.getEntityId();

                for (Entity entity : aentity)
                {
                    entity.setEntityId(entity.getEntityId() + i);
                }
            }

            entitylivingbase.setEntityId(packetIn.getEntityID());
            entitylivingbase.setUniqueId(packetIn.getUniqueId());
            entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
            entitylivingbase.motionX = (double)((float)packetIn.getVelocityX() / 8000.0F);
            entitylivingbase.motionY = (double)((float)packetIn.getVelocityY() / 8000.0F);
            entitylivingbase.motionZ = (double)((float)packetIn.getVelocityZ() / 8000.0F);
            this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entitylivingbase);
            List < EntityDataManager.DataEntry<? >> list = packetIn.getDataManagerEntries();

            if (list != null)
            {
                entitylivingbase.getDataManager().setEntryValues(list);
            }
        }
        else
        {
            LOGGER.warn("Skipping Entity with id {}", (int)packetIn.getEntityType());
        }
    }

    public void handleTimeUpdate(SPacketTimeUpdate packetIn)
    {
        this.gameController.world.setTotalWorldTime(packetIn.getTotalWorldTime());
        this.gameController.world.setWorldTime(packetIn.getWorldTime());
    }

    public void handleSpawnPosition(SPacketSpawnPosition packetIn)
    {
        this.gameController.player.setSpawnPoint(packetIn.getSpawnPos(), true);
        this.gameController.world.getWorldInfo().setSpawn(packetIn.getSpawnPos());
    }

    public void handleSetPassengers(SPacketSetPassengers packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity == null)
        {
            LOGGER.warn("Received passengers for unknown entity");
        }
        else
        {
            boolean flag = entity.isRidingOrBeingRiddenBy(this.gameController.player);
            entity.removePassengers();

            for (int i : packetIn.getPassengerIds())
            {
                Entity entity1 = this.clientWorldController.getEntityByID(i);

                if (entity1 != null)
                {
                    entity1.startRiding(entity, true);

                    if (entity1 == this.gameController.player && !flag)
                    {
                        this.gameController.ingameGUI.setOverlayMessage(I18n.format("mount.onboard", this.gameController.gameSettings.keyBindSneak.getDisplayName()), false);
                    }
                }
            }
        }
    }

    public void handleEntityAttach(SPacketEntityAttach packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
        Entity entity1 = this.clientWorldController.getEntityByID(packetIn.getVehicleEntityId());

        if (entity instanceof EntityLiving)
        {
            if (entity1 != null)
            {
                ((EntityLiving)entity).setLeashHolder(entity1, false);
            }
            else
            {
                ((EntityLiving)entity).clearLeashed(false, false);
            }
        }
    }

    public void handleEntityStatus(SPacketEntityStatus packetIn)
    {
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity != null)
        {
            if (packetIn.getOpCode() == 21)
            {
                this.gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian)entity));
            }
            else if (packetIn.getOpCode() == 35)
            {
                int i = 40;
                this.gameController.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.TOTEM, 30);
                this.clientWorldController.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);

                if (entity == this.gameController.player)
                {
                    this.gameController.entityRenderer.displayItemActivation(new ItemStack(Items.TOTEM_OF_UNDYING));
                }
            }
            else
            {
                entity.handleStatusUpdate(packetIn.getOpCode());
            }
        }
    }

    public void handleUpdateHealth(SPacketUpdateHealth packetIn)
    {
        this.gameController.player.setPlayerSPHealth(packetIn.getHealth());
        this.gameController.player.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
        this.gameController.player.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
    }

    public void handleSetExperience(SPacketSetExperience packetIn)
    {
        this.gameController.player.setXPStats(packetIn.getExperienceBar(), packetIn.getTotalExperience(), packetIn.getLevel());
    }

    public void handleRespawn(SPacketRespawn packetIn)
    {

        if (packetIn.getDimensionID() != this.gameController.player.dimension)
        {
            this.doneLoadingTerrain = false;
            Scoreboard scoreboard = this.clientWorldController.getScoreboard();
            this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packetIn.getGameType(), false, this.gameController.world.getWorldInfo().isHardcoreModeEnabled(), packetIn.getWorldType()), packetIn.getDimensionID(), packetIn.getDifficulty(), this.gameController.mcProfiler);
            this.clientWorldController.setWorldScoreboard(scoreboard);
            this.gameController.loadWorld(this.clientWorldController);
            this.gameController.player.dimension = packetIn.getDimensionID();
            this.gameController.displayGuiScreen(new GuiDownloadTerrain());
        }

        this.gameController.setDimensionAndSpawnPlayer(packetIn.getDimensionID());
        this.gameController.playerController.setGameType(packetIn.getGameType());
    }

    public void handleExplosion(SPacketExplosion packetIn)
    {
        Explosion explosion = new Explosion(this.gameController.world, (Entity)null, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getStrength(), packetIn.getAffectedBlockPositions());
        explosion.doExplosionB(true);
        this.gameController.player.motionX += (double)packetIn.getMotionX();
        this.gameController.player.motionY += (double)packetIn.getMotionY();
        this.gameController.player.motionZ += (double)packetIn.getMotionZ();
    }

    public void handleOpenWindow(SPacketOpenWindow packetIn)
    {
        EntityPlayerSP entityplayersp = this.gameController.player;

        if ("minecraft:container".equals(packetIn.getGuiId()))
        {
            entityplayersp.displayGUIChest(new InventoryBasic(packetIn.getWindowTitle(), packetIn.getSlotCount()));
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        }
        else if ("minecraft:villager".equals(packetIn.getGuiId()))
        {
            entityplayersp.displayVillagerTradeGui(new NpcMerchant(entityplayersp, packetIn.getWindowTitle()));
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        }
        else if ("EntityHorse".equals(packetIn.getGuiId()))
        {
            Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

            if (entity instanceof AbstractHorse)
            {
                entityplayersp.openGuiHorseInventory((AbstractHorse)entity, new ContainerHorseChest(packetIn.getWindowTitle(), packetIn.getSlotCount()));
                entityplayersp.openContainer.windowId = packetIn.getWindowId();
            }
        }
        else if (!packetIn.hasSlots())
        {
            entityplayersp.displayGui(new LocalBlockIntercommunication(packetIn.getGuiId(), packetIn.getWindowTitle()));
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        }
        else
        {
            IInventory iinventory = new ContainerLocalMenu(packetIn.getGuiId(), packetIn.getWindowTitle(), packetIn.getSlotCount());
            entityplayersp.displayGUIChest(iinventory);
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        }
    }

    public void handleSetSlot(SPacketSetSlot packetIn)
    {
        EntityPlayer entityplayer = this.gameController.player;
        ItemStack itemstack = packetIn.getStack();
        int i = packetIn.getSlot();
        this.gameController.getTutorial().handleSetSlot(itemstack);

        if (packetIn.getWindowId() == -1)
        {
            entityplayer.inventory.setItemStack(itemstack);
        }
        else if (packetIn.getWindowId() == -2)
        {
            entityplayer.inventory.setInventorySlotContents(i, itemstack);
        }
        else
        {
            boolean flag = false;

            if (this.gameController.currentScreen instanceof GuiContainerCreative)
            {
                GuiContainerCreative guicontainercreative = (GuiContainerCreative)this.gameController.currentScreen;
                flag = guicontainercreative.getSelectedTabIndex() != CreativeTabs.INVENTORY.getTabIndex();
            }

            if (packetIn.getWindowId() == 0 && packetIn.getSlot() >= 36 && i < 45)
            {
                if (!itemstack.isEmpty())
                {
                    ItemStack itemstack1 = entityplayer.inventoryContainer.getSlot(i).getStack();

                    if (itemstack1.isEmpty() || itemstack1.getCount() < itemstack.getCount())
                    {
                        itemstack.setAnimationsToGo(5);
                    }
                }

                entityplayer.inventoryContainer.putStackInSlot(i, itemstack);
            }
            else if (packetIn.getWindowId() == entityplayer.openContainer.windowId && (packetIn.getWindowId() != 0 || !flag))
            {
                entityplayer.openContainer.putStackInSlot(i, itemstack);
            }
        }
    }

    public void handleConfirmTransaction(SPacketConfirmTransaction packetIn)
    {
        Container container = null;
        EntityPlayer entityplayer = this.gameController.player;

        if (packetIn.getWindowId() == 0)
        {
            container = entityplayer.inventoryContainer;
        }
        else if (packetIn.getWindowId() == entityplayer.openContainer.windowId)
        {
            container = entityplayer.openContainer;
        }

        if (container != null && !packetIn.wasAccepted())
        {
            this.sendPacket(new CPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
        }
    }

    public void handleWindowItems(SPacketWindowItems packetIn)
    {
        EntityPlayer entityplayer = this.gameController.player;

        if (packetIn.getWindowId() == 0)
        {
            entityplayer.inventoryContainer.setAll(packetIn.getItemStacks());
        }
        else if (packetIn.getWindowId() == entityplayer.openContainer.windowId)
        {
            entityplayer.openContainer.setAll(packetIn.getItemStacks());
        }
    }

    public void handleSignEditorOpen(SPacketSignEditorOpen packetIn)
    {
        TileEntity tileentity = this.clientWorldController.getTileEntity(packetIn.getSignPosition());

        if (!(tileentity instanceof TileEntitySign))
        {
            tileentity = new TileEntitySign();
            tileentity.setWorld(this.clientWorldController);
            tileentity.setPos(packetIn.getSignPosition());
        }

        this.gameController.player.openEditSign((TileEntitySign)tileentity);
    }

    public void handleUpdateTileEntity(SPacketUpdateTileEntity packetIn)
    {

        if (this.gameController.world.isBlockLoaded(packetIn.getPos()))
        {
            TileEntity tileentity = this.gameController.world.getTileEntity(packetIn.getPos());
            int i = packetIn.getTileEntityType();
            boolean flag = i == 2 && tileentity instanceof TileEntityCommandBlock;

            if (i == 1 && tileentity instanceof TileEntityMobSpawner || flag || i == 3 && tileentity instanceof TileEntityBeacon || i == 4 && tileentity instanceof TileEntitySkull || i == 5 && tileentity instanceof TileEntityFlowerPot || i == 6 && tileentity instanceof TileEntityBanner || i == 7 && tileentity instanceof TileEntityStructure || i == 8 && tileentity instanceof TileEntityEndGateway || i == 9 && tileentity instanceof TileEntitySign || i == 10 && tileentity instanceof TileEntityShulkerBox || i == 11 && tileentity instanceof TileEntityBed)
            {
                tileentity.readFromNBT(packetIn.getNbtCompound());
            }
            else
            {
                if(tileentity == null)
                {
                    LOGGER.error("Received invalid update packet for null tile entity at {} with data: {}", packetIn.getPos(), packetIn.getNbtCompound());
                    return;
                }
                tileentity.onDataPacket(netManager, packetIn);
            }

            if (flag && this.gameController.currentScreen instanceof GuiCommandBlock)
            {
                ((GuiCommandBlock)this.gameController.currentScreen).updateGui();
            }
        }
    }

    public void handleWindowProperty(SPacketWindowProperty packetIn)
    {
        EntityPlayer entityplayer = this.gameController.player;

        if (entityplayer.openContainer != null && entityplayer.openContainer.windowId == packetIn.getWindowId())
        {
            entityplayer.openContainer.updateProgressBar(packetIn.getProperty(), packetIn.getValue());
        }
    }

    public void handleEntityEquipment(SPacketEntityEquipment packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (entity != null)
        {
            entity.setItemStackToSlot(packetIn.getEquipmentSlot(), packetIn.getItemStack());
        }
    }

    public void handleCloseWindow(SPacketCloseWindow packetIn)
    {
        this.gameController.player.closeScreenAndDropStack();
    }

    public void handleBlockAction(SPacketBlockAction packetIn)
    {
        this.gameController.world.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
    }

    public void handleBlockBreakAnim(SPacketBlockBreakAnim packetIn)
    {
        this.gameController.world.sendBlockBreakProgress(packetIn.getBreakerId(), packetIn.getPosition(), packetIn.getProgress());
    }

    public void handleChangeGameState(SPacketChangeGameState packetIn)
    {
        EntityPlayer entityplayer = this.gameController.player;
        int i = packetIn.getGameState();
        float f = packetIn.getValue();
        int j = MathHelper.floor(f + 0.5F);

        if (i >= 0 && i < SPacketChangeGameState.MESSAGE_NAMES.length && SPacketChangeGameState.MESSAGE_NAMES[i] != null)
        {
            entityplayer.sendStatusMessage(new TextComponentTranslation(SPacketChangeGameState.MESSAGE_NAMES[i], new Object[0]), false);
        }

        if (i == 1)
        {
            this.clientWorldController.getWorldInfo().setRaining(true);
            this.clientWorldController.setRainStrength(0.0F);
        }
        else if (i == 2)
        {
            this.clientWorldController.getWorldInfo().setRaining(false);
            this.clientWorldController.setRainStrength(1.0F);
        }
        else if (i == 3)
        {
            this.gameController.playerController.setGameType(GameType.getByID(j));
        }
        else if (i == 4)
        {
            if (j == 0)
            {
                this.gameController.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
                this.gameController.displayGuiScreen(new GuiDownloadTerrain());
            }
            else if (j == 1)
            {
                this.gameController.displayGuiScreen(new GuiWinGame(true, () ->
                {
                    this.gameController.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
                }));
            }
        }
        else if (i == 5)
        {
            GameSettings gamesettings = this.gameController.gameSettings;

            if (f == 101.0F)
            {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.help.movement", new Object[] {gamesettings.keyBindForward.getDisplayName(), gamesettings.keyBindLeft.getDisplayName(), gamesettings.keyBindBack.getDisplayName(), gamesettings.keyBindRight.getDisplayName()}));
            }
            else if (f == 102.0F)
            {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.help.jump", new Object[] {gamesettings.keyBindJump.getDisplayName()}));
            }
            else if (f == 103.0F)
            {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.help.inventory", new Object[] {gamesettings.keyBindInventory.getDisplayName()}));
            }
        }
        else if (i == 6)
        {
            this.clientWorldController.playSound(entityplayer, entityplayer.posX, entityplayer.posY + (double)entityplayer.getEyeHeight(), entityplayer.posZ, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18F, 0.45F);
        }
        else if (i == 7)
        {
            this.clientWorldController.setRainStrength(f);
        }
        else if (i == 8)
        {
            this.clientWorldController.setThunderStrength(f);
        }
        else if (i == 10)
        {
            this.clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, entityplayer.posX, entityplayer.posY, entityplayer.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
            this.clientWorldController.playSound(entityplayer, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F);
        }
    }

    public void handleMaps(SPacketMaps packetIn)
    {
        MapItemRenderer mapitemrenderer = this.gameController.entityRenderer.getMapItemRenderer();
        MapData mapdata = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.world);

        if (mapdata == null)
        {
            String s = "map_" + packetIn.getMapId();
            mapdata = new MapData(s);

            if (mapitemrenderer.getMapInstanceIfExists(s) != null)
            {
                MapData mapdata1 = mapitemrenderer.getData(mapitemrenderer.getMapInstanceIfExists(s));

                if (mapdata1 != null)
                {
                    mapdata = mapdata1;
                }
            }

            this.gameController.world.setData(s, mapdata);
        }

        packetIn.setMapdataTo(mapdata);
        mapitemrenderer.updateMapTexture(mapdata);
    }

    public void handleEffect(SPacketEffect packetIn)
    {

        if (packetIn.isSoundServerwide())
        {
            this.gameController.world.playBroadcastSound(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
        }
        else
        {
            this.gameController.world.playEvent(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
        }
    }

    public void handleAdvancementInfo(SPacketAdvancementInfo packetIn)
    {
        this.advancementManager.read(packetIn);
    }

    public void handleSelectAdvancementsTab(SPacketSelectAdvancementsTab packetIn)
    {
        ResourceLocation resourcelocation = packetIn.getTab();

        if (resourcelocation == null)
        {
            this.advancementManager.setSelectedTab((Advancement)null, false);
        }
        else
        {
            Advancement advancement = this.advancementManager.getAdvancementList().getAdvancement(resourcelocation);
            this.advancementManager.setSelectedTab(advancement, false);
        }
    }

    public void handleStatistics(SPacketStatistics packetIn)
    {

        for (Entry<StatBase, Integer> entry : packetIn.getStatisticMap().entrySet())
        {
            StatBase statbase = entry.getKey();
            int k = ((Integer)entry.getValue()).intValue();
            this.gameController.player.getStatFileWriter().unlockAchievement(this.gameController.player, statbase, k);
        }

        this.hasStatistics = true;

        if (this.gameController.currentScreen instanceof IProgressMeter)
        {
            ((IProgressMeter)this.gameController.currentScreen).onStatsUpdated();
        }
    }

    public void handleRecipeBook(SPacketRecipeBook packetIn)
    {
        RecipeBook recipebook;
        recipebook = this.gameController.player.getRecipeBook();
        recipebook.setGuiOpen(packetIn.isGuiOpen());
        recipebook.setFilteringCraftable(packetIn.isFilteringCraftable());
        SPacketRecipeBook.State spacketrecipebook$state = packetIn.getState();
        label21:

        switch (spacketrecipebook$state)
        {
            case REMOVE:
                Iterator iterator = packetIn.getRecipes().iterator();

                while (true)
                {
                    if (!iterator.hasNext())
                    {
                        break label21;
                    }

                    IRecipe irecipe = (IRecipe)iterator.next();
                    recipebook.lock(irecipe);
                }

            case INIT:
                packetIn.getRecipes().forEach(recipebook::unlock);
                packetIn.getDisplayedRecipes().forEach(recipebook::markNew);
                break;
            case ADD:
                packetIn.getRecipes().forEach((p_194025_2_) ->
                {
                    recipebook.unlock(p_194025_2_);
                    recipebook.markNew(p_194025_2_);
                    RecipeToast.addOrUpdate(this.gameController.getToastGui(), p_194025_2_);
                });
        }

        RecipeBookClient.ALL_RECIPES.forEach((p_194023_1_) ->
        {
            p_194023_1_.updateKnownRecipes(recipebook);
        });

        if (this.gameController.currentScreen instanceof IRecipeShownListener)
        {
            ((IRecipeShownListener)this.gameController.currentScreen).recipesUpdated();
        }
    }

    public void handleEntityEffect(SPacketEntityEffect packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity instanceof EntityLivingBase)
        {
            Potion potion = Potion.getPotionById(packetIn.getEffectId() & 0xFF);

            if (potion != null)
            {
                PotionEffect potioneffect = new PotionEffect(potion, packetIn.getDuration(), packetIn.getAmplifier(), packetIn.getIsAmbient(), packetIn.doesShowParticles());
                potioneffect.setPotionDurationMax(packetIn.isMaxDuration());
                ((EntityLivingBase)entity).addPotionEffect(potioneffect);
            }
        }
    }

    public void handleCombatEvent(SPacketCombatEvent packetIn)
    {

        if (packetIn.eventType == SPacketCombatEvent.Event.ENTITY_DIED)
        {
            Entity entity = this.clientWorldController.getEntityByID(packetIn.playerId);

            if (entity == this.gameController.player)
            {
                this.gameController.displayGuiScreen(new GuiGameOver(packetIn.deathMessage));
            }
        }
    }

    public void handleServerDifficulty(SPacketServerDifficulty packetIn)
    {
        this.gameController.world.getWorldInfo().setDifficulty(packetIn.getDifficulty());
        this.gameController.world.getWorldInfo().setDifficultyLocked(packetIn.isDifficultyLocked());
    }

    public void handleCamera(SPacketCamera packetIn)
    {
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity != null)
        {
            this.gameController.setRenderViewEntity(entity);
        }
    }

    public void handleWorldBorder(SPacketWorldBorder packetIn)
    {
        packetIn.apply(this.clientWorldController.getWorldBorder());
    }

    @SuppressWarnings("incomplete-switch")
    public void handleTitle(SPacketTitle packetIn)
    {
        SPacketTitle.Type spackettitle$type = packetIn.getType();
        String s = null;
        String s1 = null;
        String s2 = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";

        switch (spackettitle$type)
        {
            case TITLE:
                s = s2;
                break;
            case SUBTITLE:
                s1 = s2;
                break;
            case ACTIONBAR:
                this.gameController.ingameGUI.setOverlayMessage(s2, false);
                return;
            case RESET:
                this.gameController.ingameGUI.displayTitle("", "", -1, -1, -1);
                this.gameController.ingameGUI.setDefaultTitlesTimes();
                return;
        }

        this.gameController.ingameGUI.displayTitle(s, s1, packetIn.getFadeInTime(), packetIn.getDisplayTime(), packetIn.getFadeOutTime());
    }

    public void handlePlayerListHeaderFooter(SPacketPlayerListHeaderFooter packetIn)
    {
        this.gameController.ingameGUI.getTabList().setHeader(packetIn.getHeader().getFormattedText().isEmpty() ? null : packetIn.getHeader());
        this.gameController.ingameGUI.getTabList().setFooter(packetIn.getFooter().getFormattedText().isEmpty() ? null : packetIn.getFooter());
    }

    public void handleRemoveEntityEffect(SPacketRemoveEntityEffect packetIn)
    {
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity instanceof EntityLivingBase)
        {
            ((EntityLivingBase)entity).removeActivePotionEffect(packetIn.getPotion());
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void handlePlayerListItem(SPacketPlayerListItem packetIn)
    {

        for (SPacketPlayerListItem.AddPlayerData spacketplayerlistitem$addplayerdata : packetIn.getEntries())
        {
            if (packetIn.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER)
            {
                EaglercraftUUID uuid = spacketplayerlistitem$addplayerdata.getProfile().getId();
                this.playerInfoMap.remove(uuid);
                this.textureCache.evictPlayer(uuid);
                ClientUUIDLoadingCache.evict(uuid);
            }
            else
            {
                NetworkPlayerInfo networkplayerinfo = this.playerInfoMap.get(spacketplayerlistitem$addplayerdata.getProfile().getId());

                if (packetIn.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER)
                {
                    networkplayerinfo = new NetworkPlayerInfo(spacketplayerlistitem$addplayerdata);
                    this.playerInfoMap.put(networkplayerinfo.getGameProfile().getId(), networkplayerinfo);
                }

                if (networkplayerinfo != null)
                {
                    switch (packetIn.getAction())
                    {
                        case ADD_PLAYER:
                            networkplayerinfo.setGameType(spacketplayerlistitem$addplayerdata.getGameMode());
                            networkplayerinfo.setResponseTime(spacketplayerlistitem$addplayerdata.getPing());
                            break;
                        case UPDATE_GAME_MODE:
                            networkplayerinfo.setGameType(spacketplayerlistitem$addplayerdata.getGameMode());
                            break;
                        case UPDATE_LATENCY:
                            networkplayerinfo.setResponseTime(spacketplayerlistitem$addplayerdata.getPing());
                            break;
                        case UPDATE_DISPLAY_NAME:
                            networkplayerinfo.setDisplayName(spacketplayerlistitem$addplayerdata.getDisplayName());
                    }
                }
            }
        }
    }

    public void handleKeepAlive(SPacketKeepAlive packetIn)
    {
        this.sendPacket(new CPacketKeepAlive(packetIn.getId()));
    }

    public void handlePlayerAbilities(SPacketPlayerAbilities packetIn)
    {
        EntityPlayer entityplayer1 = this.gameController.player;
        entityplayer1.capabilities.isFlying = packetIn.isFlying();
        entityplayer1.capabilities.isCreativeMode = packetIn.isCreativeMode();
        entityplayer1.capabilities.disableDamage = packetIn.isInvulnerable();
        entityplayer1.capabilities.allowFlying = packetIn.isAllowFlying();
        entityplayer1.capabilities.setFlySpeed(packetIn.getFlySpeed());
        entityplayer1.capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
    }

    public void handleTabComplete(SPacketTabComplete packetIn)
    {
        String[] astring = packetIn.getMatches();
        Arrays.sort((Object[])astring);

        if (this.gameController.currentScreen instanceof ITabCompleter)
        {
            ((ITabCompleter)this.gameController.currentScreen).setCompletions(astring);
        }
    }

    public void handleSoundEffect(SPacketSoundEffect packetIn)
    {
        this.gameController.world.playSound(this.gameController.player, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSound(), packetIn.getCategory(), packetIn.getVolume(), packetIn.getPitch());
    }

    public void handleCustomSound(SPacketCustomSound packetIn)
    {
        this.gameController.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(packetIn.getSoundName()), packetIn.getCategory(), packetIn.getVolume(), packetIn.getPitch(), false, 0, ISound.AttenuationType.LINEAR, (float)packetIn.getX(), (float)packetIn.getY(), (float)packetIn.getZ()));
    }

    public void handleResourcePack(SPacketResourcePackSend packetIn)
    {
        final String s = packetIn.getURL();
        final String s1 = packetIn.getHash();

        if (!EaglerFolderResourcePack.isSupported() || s.startsWith("level://"))
        {
            this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
            return;
        }

        ServerData serverdata = this.gameController.getCurrentServerData();

        if (serverdata != null && serverdata.getResourceMode() == ServerData.ServerResourceMode.ENABLED)
        {
            this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            this.downloadResourcePack(s, s1);
        }
        else if (serverdata != null && serverdata.getResourceMode() != ServerData.ServerResourceMode.PROMPT)
        {
            this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
        }
        else
        {
            this.gameController.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
            {
                public void confirmClicked(boolean result, int id)
                {
                    NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();
                    ServerData serverdata1 = NetHandlerPlayClient.this.gameController.getCurrentServerData();

                    if (result)
                    {
                        if (serverdata1 != null)
                        {
                            serverdata1.setResourceMode(ServerData.ServerResourceMode.ENABLED);
                        }

                        NetHandlerPlayClient.this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                        NetHandlerPlayClient.this.downloadResourcePack(s, s1);
                    }
                    else
                    {
                        if (serverdata1 != null)
                        {
                            serverdata1.setResourceMode(ServerData.ServerResourceMode.DISABLED);
                        }

                        NetHandlerPlayClient.this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
                    }

                    ServerList.saveSingleServer(serverdata1);
                    NetHandlerPlayClient.this.gameController.displayGuiScreen((GuiScreen)null);
                }
            }, I18n.format("multiplayer.texturePrompt.line1"), I18n.format("multiplayer.texturePrompt.line2"), 0));
        }
    }

    private void downloadResourcePack(String url, String hash)
    {
        this.gameController.getResourcePackRepository().downloadResourcePack(url, hash, success -> {
            this.netManager.sendPacket(new CPacketResourcePackStatus(success ? CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED : CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
        });
    }

    public void handleUpdateBossInfo(SPacketUpdateBossInfo packetIn)
    {
        this.gameController.ingameGUI.getBossOverlay().read(packetIn);
    }

    public void handleCooldown(SPacketCooldown packetIn)
    {

        if (packetIn.getTicks() == 0)
        {
            this.gameController.player.getCooldownTracker().removeCooldown(packetIn.getItem());
        }
        else
        {
            this.gameController.player.getCooldownTracker().setCooldown(packetIn.getItem(), packetIn.getTicks());
        }
    }

    public void handleMoveVehicle(SPacketMoveVehicle packetIn)
    {
        Entity entity = this.gameController.player.getLowestRidingEntity();

        if (entity != this.gameController.player && entity.canPassengerSteer())
        {
            entity.setPositionAndRotation(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getYaw(), packetIn.getPitch());
            this.netManager.sendPacket(new CPacketVehicleMove(entity));
        }
    }

    public void handleCustomPayload(SPacketCustomPayload packetIn)
    {

        if ("MC|TrList".equals(packetIn.getChannelName()))
        {
            PacketBuffer packetbuffer = packetIn.getBufferData();

            try
            {
                int k = packetbuffer.readInt();
                GuiScreen guiscreen = this.gameController.currentScreen;

                if (guiscreen != null && guiscreen instanceof GuiMerchant && k == this.gameController.player.openContainer.windowId)
                {
                    IMerchant imerchant = ((GuiMerchant)guiscreen).getMerchant();
                    MerchantRecipeList merchantrecipelist = MerchantRecipeList.readFromBuf(packetbuffer);
                    imerchant.setRecipes(merchantrecipelist);
                }
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Couldn't load trade info", (Throwable)ioexception);
            }
        }
        else if ("MC|Brand".equals(packetIn.getChannelName()))
        {
            this.gameController.player.setServerBrand(packetIn.getBufferData().readString(32767));
        }
        else if ("MC|BOpen".equals(packetIn.getChannelName()))
        {
            EnumHand enumhand = (EnumHand)packetIn.getBufferData().readEnumValue(EnumHand.class);
            ItemStack itemstack = enumhand == EnumHand.OFF_HAND ? this.gameController.player.getHeldItemOffhand() : this.gameController.player.getHeldItemMainhand();

            if (itemstack.getItem() == Items.WRITTEN_BOOK)
            {
                this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.player, itemstack, false));
            }
        }
        else if ("MC|DebugPath".equals(packetIn.getChannelName()))
        {
            PacketBuffer packetbuffer1 = packetIn.getBufferData();
            int l = packetbuffer1.readInt();
            float f1 = packetbuffer1.readFloat();
            Path path = Path.read(packetbuffer1);
            ((DebugRendererPathfinding)this.gameController.debugRenderer.pathfinding).addPath(l, path, f1);
        }
        else if ("MC|DebugNeighborsUpdate".equals(packetIn.getChannelName()))
        {
            PacketBuffer packetbuffer2 = packetIn.getBufferData();
            long i1 = packetbuffer2.readVarLong();
            BlockPos blockpos = packetbuffer2.readBlockPos();
            ((DebugRendererNeighborsUpdate)this.gameController.debugRenderer.neighborsUpdate).addUpdate(i1, blockpos);
        }
        else if ("MC|StopSound".equals(packetIn.getChannelName()))
        {
            PacketBuffer packetbuffer3 = packetIn.getBufferData();
            String s = packetbuffer3.readString(32767);
            String s1 = packetbuffer3.readString(256);
            this.gameController.getSoundHandler().stop(s1, SoundCategory.getByName(s));
        }
        else if (this.eaglerMessageController instanceof LegacyMessageController)
        {
            try
            {
                ((LegacyMessageController)this.eaglerMessageController).handlePacket(packetIn.getChannelName(), packetIn.getBufferData());
            }
            catch (IOException e)
            {
                LOGGER.error("Couldn't read \"{}\" packet as an eaglercraft plugin message!", packetIn.getChannelName());
                LOGGER.error(e);
            }
        }
    }

    public void handleScoreboardObjective(SPacketScoreboardObjective packetIn)
    {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();

        if (packetIn.getAction() == 0)
        {
            ScoreObjective scoreobjective = scoreboard.addScoreObjective(packetIn.getObjectiveName(), IScoreCriteria.DUMMY);
            scoreobjective.setDisplayName(packetIn.getObjectiveValue());
            scoreobjective.setRenderType(packetIn.getRenderType());
        }
        else
        {
            ScoreObjective scoreobjective1 = scoreboard.getObjective(packetIn.getObjectiveName());

            if (packetIn.getAction() == 1)
            {
                scoreboard.removeObjective(scoreobjective1);
            }
            else if (packetIn.getAction() == 2)
            {
                scoreobjective1.setDisplayName(packetIn.getObjectiveValue());
                scoreobjective1.setRenderType(packetIn.getRenderType());
            }
        }
    }

    public void handleUpdateScore(SPacketUpdateScore packetIn)
    {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScoreObjective scoreobjective = scoreboard.getObjective(packetIn.getObjectiveName());

        if (packetIn.getScoreAction() == SPacketUpdateScore.Action.CHANGE)
        {
            Score score = scoreboard.getOrCreateScore(packetIn.getPlayerName(), scoreobjective);
            score.setScorePoints(packetIn.getScoreValue());
        }
        else if (packetIn.getScoreAction() == SPacketUpdateScore.Action.REMOVE)
        {
            if (StringUtils.isNullOrEmpty(packetIn.getObjectiveName()))
            {
                scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), (ScoreObjective)null);
            }
            else if (scoreobjective != null)
            {
                scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), scoreobjective);
            }
        }
    }

    public void handleDisplayObjective(SPacketDisplayObjective packetIn)
    {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();

        if (packetIn.getName().isEmpty())
        {
            scoreboard.setObjectiveInDisplaySlot(packetIn.getPosition(), (ScoreObjective)null);
        }
        else
        {
            ScoreObjective scoreobjective = scoreboard.getObjective(packetIn.getName());
            scoreboard.setObjectiveInDisplaySlot(packetIn.getPosition(), scoreobjective);
        }
    }

    public void handleTeams(SPacketTeams packetIn)
    {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScorePlayerTeam scoreplayerteam;

        if (packetIn.getAction() == 0)
        {
            scoreplayerteam = scoreboard.createTeam(packetIn.getName());
        }
        else
        {
            scoreplayerteam = scoreboard.getTeam(packetIn.getName());
        }

        if (packetIn.getAction() == 0 || packetIn.getAction() == 2)
        {
            scoreplayerteam.setDisplayName(packetIn.getDisplayName());
            scoreplayerteam.setPrefix(packetIn.getPrefix());
            scoreplayerteam.setSuffix(packetIn.getSuffix());
            scoreplayerteam.setColor(TextFormatting.fromColorIndex(packetIn.getColor()));
            scoreplayerteam.setFriendlyFlags(packetIn.getFriendlyFlags());
            Team.EnumVisible team$enumvisible = Team.EnumVisible.getByName(packetIn.getNameTagVisibility());

            if (team$enumvisible != null)
            {
                scoreplayerteam.setNameTagVisibility(team$enumvisible);
            }

            Team.CollisionRule team$collisionrule = Team.CollisionRule.getByName(packetIn.getCollisionRule());

            if (team$collisionrule != null)
            {
                scoreplayerteam.setCollisionRule(team$collisionrule);
            }
        }

        if (packetIn.getAction() == 0 || packetIn.getAction() == 3)
        {
            for (String s : packetIn.getPlayers())
            {
                scoreboard.addPlayerToTeam(s, packetIn.getName());
            }
        }

        if (packetIn.getAction() == 4)
        {
            for (String s1 : packetIn.getPlayers())
            {
                scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
            }
        }

        if (packetIn.getAction() == 1)
        {
            scoreboard.removeTeam(scoreplayerteam);
        }
    }

    public void handleParticles(SPacketParticles packetIn)
    {

        if (packetIn.getParticleCount() == 0)
        {
            double d0 = (double)(packetIn.getParticleSpeed() * packetIn.getXOffset());
            double d2 = (double)(packetIn.getParticleSpeed() * packetIn.getYOffset());
            double d4 = (double)(packetIn.getParticleSpeed() * packetIn.getZOffset());

            try
            {
                this.clientWorldController.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate(), packetIn.getYCoordinate(), packetIn.getZCoordinate(), d0, d2, d4, packetIn.getParticleArgs());
            }
            catch (Throwable var17)
            {
                LOGGER.warn("Could not spawn particle effect {}", (Object)packetIn.getParticleType());
            }
        }
        else
        {
            for (int k = 0; k < packetIn.getParticleCount(); ++k)
            {
                double d1 = this.avRandomizer.nextGaussian() * (double)packetIn.getXOffset();
                double d3 = this.avRandomizer.nextGaussian() * (double)packetIn.getYOffset();
                double d5 = this.avRandomizer.nextGaussian() * (double)packetIn.getZOffset();
                double d6 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
                double d7 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
                double d8 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();

                try
                {
                    this.clientWorldController.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate() + d1, packetIn.getYCoordinate() + d3, packetIn.getZCoordinate() + d5, d6, d7, d8, packetIn.getParticleArgs());
                }
                catch (Throwable var16)
                {
                    LOGGER.warn("Could not spawn particle effect {}", (Object)packetIn.getParticleType());
                    return;
                }
            }
        }
    }

    public void handleEntityProperties(SPacketEntityProperties packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity != null)
        {
            if (!(entity instanceof EntityLivingBase))
            {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            }
            else
            {
                AbstractAttributeMap abstractattributemap = ((EntityLivingBase)entity).getAttributeMap();

                for (SPacketEntityProperties.Snapshot spacketentityproperties$snapshot : packetIn.getSnapshots())
                {
                    IAttributeInstance iattributeinstance = abstractattributemap.getAttributeInstanceByName(spacketentityproperties$snapshot.getName());

                    if (iattributeinstance == null)
                    {
                        iattributeinstance = abstractattributemap.registerAttribute(new RangedAttribute((IAttribute)null, spacketentityproperties$snapshot.getName(), 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE)); // Forge: fix invalid value range (MC-150405)
                    }

                    iattributeinstance.setBaseValue(spacketentityproperties$snapshot.getBaseValue());
                    iattributeinstance.removeAllModifiers();

                    for (AttributeModifier attributemodifier : spacketentityproperties$snapshot.getModifiers())
                    {
                        iattributeinstance.applyModifier(attributemodifier);
                    }
                }
            }
        }
    }

    public void func_194307_a(SPacketPlaceGhostRecipe p_194307_1_)
    {
        Container container = this.gameController.player.openContainer;

        if (container.windowId == p_194307_1_.func_194313_b() && container.getCanCraft(this.gameController.player))
        {
            if (this.gameController.currentScreen instanceof IRecipeShownListener)
            {
                GuiRecipeBook guirecipebook = ((IRecipeShownListener)this.gameController.currentScreen).func_194310_f();
                guirecipebook.setupGhostRecipe(p_194307_1_.func_194311_a(), container.inventorySlots);
            }
        }
    }

    public EaglercraftNetworkManager getNetworkManager()
    {
        return this.netManager;
    }

    public Collection<NetworkPlayerInfo> getPlayerInfoMap()
    {
        return this.playerInfoMap.values();
    }

    public NetworkPlayerInfo getPlayerInfo(EaglercraftUUID uniqueId)
    {
        return this.playerInfoMap.get(uniqueId);
    }

    @Nullable
    public NetworkPlayerInfo getPlayerInfo(String name)
    {
        for (NetworkPlayerInfo networkplayerinfo : this.playerInfoMap.values())
        {
            if (networkplayerinfo.getGameProfile().getName().equals(name))
            {
                return networkplayerinfo;
            }
        }

        return null;
    }

    public GameProfile getGameProfile()
    {
        return this.profile;
    }

    public ClientAdvancementManager getAdvancementManager()
    {
        return this.advancementManager;
    }
}
