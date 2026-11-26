package atomicstryker.battletowers.common;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.HashSet;
import java.util.Set;

import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

import atomicstryker.battletowers.common.network.ChestAttackedPacket;
import atomicstryker.battletowers.common.network.LoginPacket;
import atomicstryker.battletowers.common.network.NetworkHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = "battletowers", name = "Battle Towers", version = "1.6.5")
public class AS_BattleTowersCore
{
    public void handleModStateEvent(FMLEvent event)
    {
        if (event instanceof FMLConstructionEvent)
        {
            instance = this;
            proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new atomicstryker.battletowers.client.ClientProxy() : new atomicstryker.battletowers.common.CommonProxy();
            atomicstryker.battletowers.common.AS_BattleTowersCore.registerHolders();
            atomicstryker.battletowers.common.AS_BattleTowersCore.RegistrationHandler.register(MinecraftForge.EVENT_BUS);
        }
        else if (event instanceof FMLPreInitializationEvent)
        {
            this.preInit((FMLPreInitializationEvent)event);
        }
        else if (event instanceof FMLInitializationEvent)
        {
            this.load((FMLInitializationEvent)event);
        }
        else if (event instanceof FMLPostInitializationEvent)
        {
            this.modsLoaded((FMLPostInitializationEvent)event);
        }
        else if (event instanceof FMLServerStartingEvent)
        {
            this.serverStarted((FMLServerStartingEvent)event);
        }
        else if (event instanceof FMLServerStoppedEvent)
        {
            this.serverStopped((FMLServerStoppedEvent)event);
        }
    }

    private Set<AS_TowerDestroyer> towerDestroyers;
    public int minDistanceFromSpawn;
    public int minDistanceBetweenTowers;
    public int towerDestroyerEnabled;
    public int itemGenerateAttemptsPerFloor;
    public int chanceTowerIsUnderGround;
    public boolean noGolemExplosions;
    public boolean towerFallDestroysMobSpawners;
    // private int golemEntityID;

    public static AS_BattleTowersCore instance;

    public static CommonProxy proxy;
    
    public static Logger LOGGER;

    public TowerStageItemManager[] floorItemManagers = new TowerStageItemManager[10];
    public Configuration configuration;

    public NetworkHelper networkHelper;

    public static SoundEvent soundTowerBreakStart = createSoundEvent("towerbreakstart");

    public static SoundEvent soundTowerCrumble = createSoundEvent("towercrumble");

    public static SoundEvent soundGolemAwaken = createSoundEvent("golemawaken");

    public static SoundEvent soundGolemSpecial = createSoundEvent("golemspecial");

    public static SoundEvent soundGolemCharge = createSoundEvent("golemcharge");

    public static SoundEvent soundGolem = createSoundEvent("golem");

    public static SoundEvent soundGolemHurt = createSoundEvent("golemhurt");

    public static SoundEvent soundGolemDeath = createSoundEvent("golemdeath");

    public void preInit(FMLPreInitializationEvent event)
    {
        configuration = new Configuration(event.getSuggestedConfigurationFile(), false);
        loadForgeConfig();

        proxy.preInit();

        networkHelper = new NetworkHelper("AS_BT", LoginPacket::new, ChestAttackedPacket::new);

        this.register(MinecraftForge.EVENT_BUS);
        (new ServerTickHandler()).register(MinecraftForge.EVENT_BUS);
        
        LOGGER = event.getModLog();
    }

    public void register(EventBus bus)
    {
        bus.register(ClientConnectedToServerEvent.class, this::onClientConnected);
        bus.register(ClientDisconnectionFromServerEvent.class, this::onClientDisconnected);
        bus.register(PlayerLoggedInEvent.class, this::onPlayerLogin);
    }

    public void onClientConnected(ClientConnectedToServerEvent event)
    {
        System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " registered ClientConnectedToServerEvent, sending packet to server");
        networkHelper.sendPacketToServer(new LoginPacket());
    }

    public void onClientDisconnected(ClientDisconnectionFromServerEvent event)
    {
        //Tell the client's WorldGenHandler to clear the world map on the next world load
        WorldGenHandler.shouldClearWorldMap = true;
    }

    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        networkHelper.sendPacketToPlayer(new LoginPacket(), (EntityPlayerMP) event.player);
    }

    public void load(FMLInitializationEvent evt)
    {
        proxy.load();

        // EntityRegistry.registerGlobalEntityID(AS_EntityGolem.class,
        // "Battletower Golem", golemEntityID, 0xA0A0A0, 0x808080);
        EntityRegistry.registerModEntity(new ResourceLocation("battletowers", "golem"), AS_EntityGolem.class, "Battletower Golem", 1, this, 32, 3, false, AS_EntityGolem::new);

        EntityRegistry.registerModEntity(new ResourceLocation("battletowers", "golemfireball"), AS_EntityGolemFireball.class, "Golem Fireball", 2, this, 32, 3, false, AS_EntityGolemFireball::new);

        towerDestroyers = new HashSet<AS_TowerDestroyer>();

        GameRegistry.registerWorldGenerator(new WorldGenHandler(), 0);
    }

    /**
     * Create a {@link SoundEvent}.
     *
     * @param soundName
     *            The SoundEvent's name without the modid prefix
     * @return The SoundEvent
     */
    private static SoundEvent createSoundEvent(String soundName)
    {
        final ResourceLocation soundID = new ResourceLocation("battletowers", soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }

    public static class RegistrationHandler
    {
        public static void register(EventBus bus) {
           bus.registerGeneric(RegistryEvent.Register.class, SoundEvent.class, RegistrationHandler::registerSoundEvents);
        }

        public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event)
        {
            event.getRegistry().registerAll(soundTowerBreakStart, soundTowerCrumble, soundGolemAwaken, soundGolemSpecial, soundGolemCharge, soundGolem, soundGolemHurt, soundGolemDeath);
        }
    }

    public void modsLoaded(FMLPostInitializationEvent evt)
    {
        configuration.load();
        configuration.addCustomCategoryComment("BattleTowerChestItems", "Syntax for each Item entry is 'Name-Meta-Spawnchance-minAmount-maxAmount', entries are seperated by ';'");

        // stick-0-50-5-6 sticks
        // wheat_seeds-0-50-3-5 seeds
        // planks-0-50-5-6 planks
        // reeds-0-50-3-5 sugarcane
        floorItemManagers[0] = new TowerStageItemManager(configuration.get("BattleTowerChestItems", "Floor 1", "stick-0-75-5-6;wheat_seeds-0-75-3-5;planks-0-75-5-6;reeds-0-75-3-5").getString());

        // stone_pickaxe-0-25-1-1 stone pick
        // stone_axe-0-25-1-1 stone axe
        // torch-0-75-3-3 torches
        // stone_button-0-50-2-2 stone button
        floorItemManagers[1] = new TowerStageItemManager(
                configuration.get("BattleTowerChestItems", "Floor 2", "stone_pickaxe-0-50-1-1;stone_axe-0-50-1-1;torch-0-80-3-3;stone_button-0-50-2-2").getString());

        // bowl-0-50-2-4 wooden bowl
        // coal-0-75-4-4 coal
        // string-0-50-5-5 string
        // wool-0-25-2-2 wool
        floorItemManagers[2] = new TowerStageItemManager(configuration.get("BattleTowerChestItems", "Floor 3", "bowl-0-75-2-4;coal-0-90-4-4;string-0-80-5-5;wool-0-75-2-2").getString());

        // glass-0-50-3-3 glass
        // feather-0-25-4-4 feather
        // bread-0-50-2-2 bread
        // apple-0-75-2-2 apple
        floorItemManagers[3] = new TowerStageItemManager(configuration.get("BattleTowerChestItems", "Floor 4", "glass-0-75-3-3;feather-0-75-4-4;bread-0-75-2-2;apple-0-75-2-2").getString());

        // brown_mushroom-0-50-2-2 brown mushroom
        // red_mushroom-0-50-2-2 red mushroom
        // sapling-0-75-3-3 saplings
        // wheat-0-25-4-4 wheat
        floorItemManagers[4] = new TowerStageItemManager(
                configuration.get("BattleTowerChestItems", "Floor 5", "brown_mushroom-0-75-2-2;red_mushroom-0-75-2-2;sapling-0-90-3-3;wheat-0-75-4-4").getString());

        // sign-0-50-1-2 sign
        // fishing_rod-0-75-1-1 fishing rod
        // pumpkin_seeds-0-25-2-2 pumpkin seeds
        // melon_seeds-0-25-3-3 Melon Seeds
        floorItemManagers[5] = new TowerStageItemManager(
                configuration.get("BattleTowerChestItems", "Floor 6", "sign-0-50-1-2;fishing_rod-0-75-1-1;pumpkin_seeds-0-60-2-2;melon_seeds-0-60-3-3").getString());

        // iron_sword-0-25-1-1 iron sword
        // gunpowder-0-25-3-3 gunpowder
        // leather-0-50-4-4 leather
        // fish-0-75-3-3 raw fish
        // dye-0-50-1-2 dye
        floorItemManagers[6] = new TowerStageItemManager(
                configuration.get("BattleTowerChestItems", "Floor 7", "iron_sword-0-60-1-1;gunpowder-0-75-3-3;leather-0-75-4-4;fish-0-75-3-3;dye-0-60-1-2").getString());

        // chainmail_helmet-0-25-1-1 chain helmet
        // chainmail_chestplate-0-25-1-1 chain chestplate
        // chainmail_leggings-0-25-1-1 chain leggings
        // chainmail_boots-0-25-1-1 chain boots
        floorItemManagers[7] = new TowerStageItemManager(configuration
                .get("BattleTowerChestItems", "Floor 8", "chainmail_helmet-0-40-1-1;chainmail_chestplate-0-40-1-1;chainmail_leggings-0-40-1-1;chainmail_boots-0-40-1-1;ChestGenHook:chests/simple_dungeon:3")
                .getString());

        // bookshelf-0-50-1-3 bookshelf
        // redstone_lamp-0-25-2-2 redstone lamp
        // waterlily-0-75-3-3 Lily Plants
        // brewing_stand-0-25-1-1 brewing stand
        floorItemManagers[8] = new TowerStageItemManager(
                configuration.get("BattleTowerChestItems", "Floor 9", "bookshelf-0-70-1-3;redstone_lamp-0-60-2-2;waterlily-0-75-3-3;brewing_stand-0-50-1-1;ChestGenHook:chests/simple_dungeon:5").getString());

        // ender_pearl-0-50-2-2 ender pearl
        // diamond-0-50-2-2 diamond
        // redstone-0-75-5-5 redstone dust
        // gold_ingot-0-75-8-8 gold ingot
        floorItemManagers[9] = new TowerStageItemManager(
                configuration.get("BattleTowerChestItems", "Top Floor", "ender_pearl-0-50-2-2;diamond-0-70-2-2;redstone-0-75-5-5;gold_ingot-0-90-8-8;ChestGenHook:chests/simple_dungeon:7").getString());
        configuration.save();
    }

    public void serverStarted(FMLServerStartingEvent evt)
    {
        evt.registerServerCommand(new CommandSpawnBattleTower());
        evt.registerServerCommand(new CommandDeleteBattleTower());
        evt.registerServerCommand(new CommandRegenerateBattleTower());
        evt.registerServerCommand(new CommandRegenerateAllBattleTowers());
        evt.registerServerCommand(new CommandDeleteAllBattleTowers());
    }
    
    public void serverStopped(FMLServerStoppedEvent evt)
    {
    	//Wipe world handles to avoid save folder conflicts
    	WorldGenHandler.wipeWorldHandles();
    }

    public void loadForgeConfig()
    {
        configuration.load();
        minDistanceFromSpawn = configuration.get("MainOptions", "Minimum Distance of Battletowers from Spawn", 96).getInt();
        minDistanceBetweenTowers = Integer.parseInt(configuration.get("MainOptions", "Minimum Distance between 2 BattleTowers", 196).getString());
        towerDestroyerEnabled = Integer.parseInt(configuration.get("MainOptions", "Tower Destroying Enabled", 1).getString());
        itemGenerateAttemptsPerFloor = configuration.get("BattleTowerChestItems", "Item Generations per Floor", "7").getInt();
        chanceTowerIsUnderGround = configuration.get("MainOptions", "chanceTowerIsUnderGround", 15).getInt();
        noGolemExplosions = configuration.get("MainOptions", "noGolemExplosions", false).getBoolean(false);
        towerFallDestroysMobSpawners = configuration.get("MainOptions", "towerFallDestroysMobSpawners", false, "Destroy all Mob Spawners in Tower Area upon Tower Fall?").getBoolean(false);
        // golemEntityID = configuration.get(Configuration.CATEGORY_GENERAL,
        // "Golem Entity ID", 186).getInt();
        configuration.save();
    }

    public static synchronized void onBattleTowerDestroyed(AS_TowerDestroyer td)
    {
        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
                .sendPacketToAllPlayers(new SPacketChat(new TextComponentTranslation("A Battletower's Guardian has fallen! Without it's power, the Tower will collapse...")));
        instance.towerDestroyers.add(td);
    }

    public static synchronized Set<AS_TowerDestroyer> getTowerDestroyers()
    {
        return instance.towerDestroyers;
    }

    public static void registerHolders()
    {
        ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("battletowers:towerbreakstart"), v -> soundTowerBreakStart = v);
        ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("battletowers:towercrumble"), v -> soundTowerCrumble = v);
        ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("battletowers:golemawaken"), v -> soundGolemAwaken = v);
        ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("battletowers:golemspecial"), v -> soundGolemSpecial = v);
        ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("battletowers:golemcharge"), v -> soundGolemCharge = v);
        ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("battletowers:golem"), v -> soundGolem = v);
        ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("battletowers:golemhurt"), v -> soundGolemHurt = v);
        ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("battletowers:golemdeath"), v -> soundGolemDeath = v);
    }
}
