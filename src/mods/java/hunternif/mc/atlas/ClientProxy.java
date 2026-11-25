package hunternif.mc.atlas;

import hunternif.mc.atlas.client.BiomeTextureConfig;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.KeyHandler;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.client.TextureSetConfig;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.client.gui.ExportProgressOverlay;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.ext.ExtTileTextureConfig;
import hunternif.mc.atlas.ext.ExtTileTextureMap;
import hunternif.mc.atlas.marker.MarkerTextureConfig;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {
   private TextureSetMap textureSetMap;
   private TextureSetConfig textureSetConfig;
   private BiomeTextureMap biomeTextureMap;
   private BiomeTextureConfig biomeTextureConfig;
   private ExtTileTextureMap tileTextureMap;
   private ExtTileTextureConfig tileTextureConfig;
   private MarkerTextureConfig markerTextureConfig;
   private GuiAtlas guiAtlas;

   public ClientProxy() {
   }

   public MinecraftServer getServer() {
      return FMLClientHandler.instance().getServer();
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      MinecraftForge.EVENT_BUS.register(ExportProgressOverlay.INSTANCE);
      this.textureSetMap = TextureSetMap.instance();
      this.textureSetConfig = new TextureSetConfig(new File(this.configDir, "texture_sets.json"));
      this.registerDefaultTextureSets(this.textureSetMap);
      this.textureSetConfig.load(this.textureSetMap);
      this.textureSetMap.setDirty(false);
      this.textureSetMap.register(TextureSet.TEST);
      File biomeTextureConfigFile = new File(this.configDir, "textures.json");
      if (biomeTextureConfigFile.exists()) {
         biomeTextureConfigFile.renameTo(new File(this.configDir, "biome_textures.json"));
      }

      this.tileTextureMap = ExtTileTextureMap.instance();
      this.tileTextureConfig = new ExtTileTextureConfig(new File(this.configDir, "tile_textures.json"), this.textureSetMap);
      this.tileTextureConfig.load(this.tileTextureMap);
      this.tileTextureMap.setDirty(false);
      this.registerVanillaCustomTileTextures();
      if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager) {
         ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
      }

   }

   public void init(FMLInitializationEvent event) {
      super.init(event);
      this.biomeTextureMap = BiomeTextureMap.instance();
      this.biomeTextureConfig = new BiomeTextureConfig(new File(this.configDir, "biome_textures.json"), this.textureSetMap);
      this.biomeTextureConfig.load(this.biomeTextureMap);
      this.biomeTextureMap.setDirty(false);
      this.assignVanillaBiomeTextures();
      this.markerTextureConfig = new MarkerTextureConfig(new File(this.configDir, "markers.json"));
      this.markerTextureConfig.load(MarkerRegistry.INSTANCE);
      MarkerRegistry.INSTANCE.setDirty(true);
      this.guiAtlas = new GuiAtlas();

      for(MarkerType type : MarkerRegistry.getValues()) {
         type.initMips();
      }

      if (!SettingsConfig.gameplay.itemNeeded) {
         KeyHandler.registerBindings();
         MinecraftForge.EVENT_BUS.register(new KeyHandler());
      }

      MinecraftForge.EVENT_BUS.register(this);
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      this.guiAtlas.setMapScale(SettingsConfig.userInterface.defaultScale);
   }

   public void openAtlasGUI(ItemStack stack) {
      this.openAtlasGUI(this.guiAtlas.prepareToOpen(stack));
   }

   public void openAtlasGUI() {
      this.openAtlasGUI(this.guiAtlas.prepareToOpen());
   }

   private void openAtlasGUI(GuiAtlas gui) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen == null) {
         this.guiAtlas.updateL18n();
         mc.displayGuiScreen(gui);
      }

   }

   private void registerDefaultTextureSets(TextureSetMap map) {
      map.register(TextureSet.ICE);
      map.register(TextureSet.SHORE);
      map.register(TextureSet.ROCK_SHORE);
      map.register(TextureSet.DESERT);
      map.register(TextureSet.PLAINS);
      map.register(TextureSet.SUNFLOWERS);
      map.register(TextureSet.HILLS);
      map.register(TextureSet.DESERT_HILLS);
      map.register(TextureSet.ICE_SPIKES);
      map.register(TextureSet.SNOW_PINES);
      map.register(TextureSet.SNOW_PINES_HILLS);
      map.register(TextureSet.SNOW_HILLS);
      map.register(TextureSet.SNOW);
      map.register(TextureSet.MOUNTAINS_NAKED);
      map.register(TextureSet.MOUNTAINS);
      map.register(TextureSet.MOUNTAINS_SNOW_CAPS);
      map.register(TextureSet.MOUNTAINS_ALL);
      map.register(TextureSet.FOREST);
      map.register(TextureSet.FOREST_HILLS);
      map.register(TextureSet.FOREST_FLOWERS);
      map.register(TextureSet.DENSE_FOREST);
      map.register(TextureSet.DENSE_FOREST_HILLS);
      map.register(TextureSet.BIRCH);
      map.register(TextureSet.BIRCH_HILLS);
      map.register(TextureSet.TALL_BIRCH);
      map.register(TextureSet.TALL_BIRCH_HILLS);
      map.register(TextureSet.DENSE_BIRCH);
      map.register(TextureSet.JUNGLE);
      map.register(TextureSet.JUNGLE_HILLS);
      map.register(TextureSet.JUNGLE_CLIFFS);
      map.register(TextureSet.JUNGLE_EDGE);
      map.register(TextureSet.JUNGLE_EDGE_HILLS);
      map.register(TextureSet.PINES);
      map.register(TextureSet.PINES_HILLS);
      map.register(TextureSet.SAVANNA);
      map.register(TextureSet.SAVANNA_CLIFFS);
      map.register(TextureSet.PLATEAU_SAVANNA_M);
      map.register(TextureSet.MESA);
      map.register(TextureSet.BRYCE);
      map.register(TextureSet.PLATEAU_MESA);
      map.register(TextureSet.PLATEAU_MESA_LOW);
      map.register(TextureSet.PLATEAU_MESA_TREES);
      map.register(TextureSet.PLATEAU_MESA_TREES_LOW);
      map.register(TextureSet.PLATEAU_SAVANNA);
      map.register(TextureSet.MEGA_SPRUCE);
      map.register(TextureSet.MEGA_SPRUCE_HILLS);
      map.register(TextureSet.MEGA_TAIGA);
      map.register(TextureSet.MEGA_TAIGA_HILLS);
      map.register(TextureSet.SWAMP);
      map.register(TextureSet.SWAMP_HILLS);
      map.register(TextureSet.MUSHROOM);
      map.register(TextureSet.WATER);
      map.register(TextureSet.LAVA);
      map.register(TextureSet.LAVA_SHORE);
      map.register(TextureSet.CAVE_WALLS);
      map.register(TextureSet.RAVINE);
      map.register(TextureSet.HOUSE);
      map.register(TextureSet.FENCE);
      map.register(TextureSet.LIBRARY);
      map.register(TextureSet.L_HOUSE);
      map.register(TextureSet.SMITHY);
      map.register(TextureSet.FARMLAND_LARGE);
      map.register(TextureSet.FARMLAND_SMALL);
      map.register(TextureSet.WELL);
      map.register(TextureSet.VILLAGE_TORCH);
      map.register(TextureSet.HUT);
      map.register(TextureSet.HOUSE_SMALL);
      map.register(TextureSet.BUTCHERS_SHOP);
      map.register(TextureSet.CHURCH);
      map.register(TextureSet.NETHER_BRIDGE);
      map.register(TextureSet.NETHER_BRIDGE_X);
      map.register(TextureSet.NETHER_BRIDGE_Z);
      map.register(TextureSet.NETHER_BRIDGE_END_X);
      map.register(TextureSet.NETHER_BRIDGE_END_Z);
      map.register(TextureSet.NETHER_BRIDGE_GATE);
      map.register(TextureSet.NETHER_TOWER);
      map.register(TextureSet.NETHER_WALL);
      map.register(TextureSet.NETHER_HALL);
      map.register(TextureSet.NETHER_FORT_STAIRS);
      map.register(TextureSet.NETHER_THRONE);
      map.register(TextureSet.END_ISLAND);
      map.register(TextureSet.END_ISLAND_PLANTS);
      map.register(TextureSet.END_VOID);
   }

   private void assignVanillaBiomeTextures() {
      this.setBiomeTextureIfNone(Biomes.OCEAN, TextureSet.WATER);
      this.setBiomeTextureIfNone(Biomes.DEEP_OCEAN, TextureSet.WATER);
      this.setBiomeTextureIfNone(Biomes.RIVER, TextureSet.WATER);
      this.setBiomeTextureIfNone(Biomes.FROZEN_OCEAN, TextureSet.ICE);
      this.setBiomeTextureIfNone(Biomes.FROZEN_RIVER, TextureSet.ICE);
      this.setBiomeTextureIfNone(Biomes.BEACH, TextureSet.SHORE);
      this.setBiomeTextureIfNone(Biomes.COLD_BEACH, TextureSet.SHORE);
      this.setBiomeTextureIfNone(Biomes.STONE_BEACH, TextureSet.ROCK_SHORE);
      this.setBiomeTextureIfNone(Biomes.DESERT, TextureSet.DESERT);
      this.setBiomeTextureIfNone(Biomes.MUTATED_DESERT, TextureSet.DESERT);
      this.setBiomeTextureIfNone(Biomes.DESERT_HILLS, TextureSet.DESERT_HILLS);
      this.setBiomeTextureIfNone(Biomes.PLAINS, TextureSet.PLAINS);
      this.setBiomeTextureIfNone(Biomes.MUTATED_PLAINS, TextureSet.SUNFLOWERS);
      this.setBiomeTextureIfNone(Biomes.ICE_PLAINS, TextureSet.SNOW);
      this.setBiomeTextureIfNone(Biomes.MUTATED_ICE_FLATS, TextureSet.ICE_SPIKES);
      this.setBiomeTextureIfNone(Biomes.ICE_MOUNTAINS, TextureSet.SNOW_HILLS);
      this.setBiomeTextureIfNone(Biomes.EXTREME_HILLS, TextureSet.MOUNTAINS);
      this.setBiomeTextureIfNone(Biomes.EXTREME_HILLS_EDGE, TextureSet.MOUNTAINS);
      this.setBiomeTextureIfNone(Biomes.MUTATED_EXTREME_HILLS, TextureSet.MOUNTAINS_SNOW_CAPS);
      this.setBiomeTextureIfNone(Biomes.EXTREME_HILLS_WITH_TREES, TextureSet.MOUNTAINS_ALL);
      this.setBiomeTextureIfNone(Biomes.MUTATED_EXTREME_HILLS_WITH_TREES, TextureSet.MOUNTAINS_SNOW_CAPS);
      this.setBiomeTextureIfNone(Biomes.FOREST, TextureSet.FOREST);
      this.setBiomeTextureIfNone(Biomes.MUTATED_FOREST, TextureSet.FOREST_FLOWERS);
      this.setBiomeTextureIfNone(Biomes.FOREST_HILLS, TextureSet.FOREST_HILLS);
      this.setBiomeTextureIfNone(Biomes.ROOFED_FOREST, TextureSet.DENSE_FOREST);
      this.setBiomeTextureIfNone(Biomes.MUTATED_ROOFED_FOREST, TextureSet.DENSE_FOREST_HILLS);
      this.setBiomeTextureIfNone(Biomes.BIRCH_FOREST, TextureSet.BIRCH);
      this.setBiomeTextureIfNone(Biomes.MUTATED_BIRCH_FOREST, TextureSet.TALL_BIRCH);
      this.setBiomeTextureIfNone(Biomes.BIRCH_FOREST_HILLS, TextureSet.BIRCH_HILLS);
      this.setBiomeTextureIfNone(Biomes.MUTATED_BIRCH_FOREST_HILLS, TextureSet.TALL_BIRCH_HILLS);
      this.setBiomeTextureIfNone(Biomes.JUNGLE, TextureSet.JUNGLE);
      this.setBiomeTextureIfNone(Biomes.MUTATED_JUNGLE, TextureSet.JUNGLE_CLIFFS);
      this.setBiomeTextureIfNone(Biomes.JUNGLE_HILLS, TextureSet.JUNGLE_HILLS);
      this.setBiomeTextureIfNone(Biomes.JUNGLE_EDGE, TextureSet.JUNGLE_EDGE);
      this.setBiomeTextureIfNone(Biomes.MUTATED_JUNGLE_EDGE, TextureSet.JUNGLE_EDGE_HILLS);
      this.setBiomeTextureIfNone(Biomes.TAIGA, TextureSet.PINES);
      this.setBiomeTextureIfNone(Biomes.MUTATED_TAIGA, TextureSet.PINES_HILLS);
      this.setBiomeTextureIfNone(Biomes.TAIGA_HILLS, TextureSet.PINES_HILLS);
      this.setBiomeTextureIfNone(Biomes.COLD_TAIGA, TextureSet.SNOW_PINES);
      this.setBiomeTextureIfNone(Biomes.MUTATED_TAIGA_COLD, TextureSet.SNOW_PINES_HILLS);
      this.setBiomeTextureIfNone(Biomes.COLD_TAIGA_HILLS, TextureSet.SNOW_PINES_HILLS);
      this.setBiomeTextureIfNone(Biomes.REDWOOD_TAIGA, TextureSet.MEGA_TAIGA);
      this.setBiomeTextureIfNone(Biomes.MUTATED_REDWOOD_TAIGA, TextureSet.MEGA_SPRUCE);
      this.setBiomeTextureIfNone(Biomes.REDWOOD_TAIGA_HILLS, TextureSet.MEGA_TAIGA_HILLS);
      this.setBiomeTextureIfNone(Biomes.MUTATED_REDWOOD_TAIGA_HILLS, TextureSet.MEGA_SPRUCE_HILLS);
      this.setBiomeTextureIfNone(Biomes.SWAMPLAND, TextureSet.SWAMP);
      this.setBiomeTextureIfNone(Biomes.MUTATED_SWAMPLAND, TextureSet.SWAMP_HILLS);
      this.setBiomeTextureIfNone(Biomes.SKY, TextureSet.SHORE);
      this.setBiomeTextureIfNone(Biomes.HELL, TextureSet.CAVE_WALLS);
      this.setBiomeTextureIfNone(Biomes.VOID, TextureSet.END_VOID);
      this.setBiomeTextureIfNone(Biomes.MUSHROOM_ISLAND, TextureSet.MUSHROOM);
      this.setBiomeTextureIfNone(Biomes.MUSHROOM_ISLAND_SHORE, TextureSet.SHORE);
      this.setBiomeTextureIfNone(Biomes.SAVANNA, TextureSet.SAVANNA);
      this.setBiomeTextureIfNone(Biomes.MUTATED_SAVANNA, TextureSet.SAVANNA_CLIFFS);
      this.setBiomeTextureIfNone(Biomes.MESA, TextureSet.MESA);
      this.setBiomeTextureIfNone(Biomes.MUTATED_MESA, TextureSet.BRYCE);
      this.setBiomeTextureIfNone(Biomes.MESA_CLEAR_ROCK, TextureSet.PLATEAU_MESA);
      this.setBiomeTextureIfNone(Biomes.MESA_ROCK, TextureSet.PLATEAU_MESA_TREES);
      this.setBiomeTextureIfNone(Biomes.MUTATED_MESA_CLEAR_ROCK, TextureSet.PLATEAU_MESA_LOW);
      this.setBiomeTextureIfNone(Biomes.MUTATED_MESA_ROCK, TextureSet.PLATEAU_MESA_TREES_LOW);
      this.setBiomeTextureIfNone(Biomes.SAVANNA_PLATEAU, TextureSet.PLATEAU_SAVANNA);
      this.setBiomeTextureIfNone(Biomes.MUTATED_SAVANNA_ROCK, TextureSet.PLATEAU_SAVANNA_M);

      for(Biome biome : Biome.REGISTRY) {
         BiomeTextureMap.instance().checkRegistration(biome);
      }

   }

   private void setBiomeTextureIfNone(Biome biome, TextureSet textureSet) {
      if (!this.biomeTextureMap.isRegistered(biome)) {
         this.biomeTextureMap.setTexture(biome, textureSet);
      }

   }

   private void registerVanillaCustomTileTextures() {
      this.setCustomTileTextureIfNone("npcVillageLibrary", TextureSet.LIBRARY);
      this.setCustomTileTextureIfNone("npcVillageSmithy", TextureSet.SMITHY);
      this.setCustomTileTextureIfNone("npcVillageLHouse", TextureSet.L_HOUSE);
      this.setCustomTileTextureIfNone("npcVillageFarmlandLarge", TextureSet.FARMLAND_LARGE);
      this.setCustomTileTextureIfNone("npcVillageFarmlandSmall", TextureSet.FARMLAND_SMALL);
      this.setCustomTileTextureIfNone("npcVillageWell", TextureSet.WELL);
      this.setCustomTileTextureIfNone("npcVillageTorch", TextureSet.VILLAGE_TORCH);
      this.setCustomTileTextureIfNone("npcVillageHut", TextureSet.HUT);
      this.setCustomTileTextureIfNone("npcVillageSmallHouse", TextureSet.HOUSE_SMALL);
      this.setCustomTileTextureIfNone("npcVillageButchersShop", TextureSet.BUTCHERS_SHOP);
      this.setCustomTileTextureIfNone("npcVillageChurch", TextureSet.CHURCH);
      this.setCustomTileTextureIfNone("lava", TextureSet.LAVA);
      this.setCustomTileTextureIfNone("lavaShore", TextureSet.LAVA_SHORE);
      this.setCustomTileTextureIfNone("netherBridge", TextureSet.NETHER_BRIDGE);
      this.setCustomTileTextureIfNone("netherBridgeX", TextureSet.NETHER_BRIDGE_X);
      this.setCustomTileTextureIfNone("netherBridgeZ", TextureSet.NETHER_BRIDGE_Z);
      this.setCustomTileTextureIfNone("netherBridgeEndX", TextureSet.NETHER_BRIDGE_END_X);
      this.setCustomTileTextureIfNone("netherBridgeEndZ", TextureSet.NETHER_BRIDGE_END_Z);
      this.setCustomTileTextureIfNone("netherBridgeGate", TextureSet.NETHER_BRIDGE_GATE);
      this.setCustomTileTextureIfNone("netherTower", TextureSet.NETHER_TOWER);
      this.setCustomTileTextureIfNone("netherWall", TextureSet.NETHER_WALL);
      this.setCustomTileTextureIfNone("netherHall", TextureSet.NETHER_HALL);
      this.setCustomTileTextureIfNone("netherFortStairs", TextureSet.NETHER_FORT_STAIRS);
      this.setCustomTileTextureIfNone("netherThrone", TextureSet.NETHER_THRONE);
      this.setCustomTileTextureIfNone("endIsland", TextureSet.END_ISLAND);
      this.setCustomTileTextureIfNone("endIslandPlants", TextureSet.END_ISLAND_PLANTS);
      this.setCustomTileTextureIfNone("endVoid", TextureSet.END_VOID);
      this.setCustomTileTextureIfNone("ravine", TextureSet.RAVINE);
   }

   private void setCustomTileTextureIfNone(String tileName, TextureSet textureSet) {
      if (!this.tileTextureMap.isRegistered(tileName)) {
         this.tileTextureMap.setTexture(tileName, textureSet);
      }

   }

   public EntityPlayer getPlayerEntity(MessageContext ctx) {
      return (EntityPlayer)(ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
   }

   public IThreadListener getThreadFromContext(MessageContext ctx) {
      return (IThreadListener)(ctx.side.isClient() ? Minecraft.getMinecraft() : super.getThreadFromContext(ctx));
   }

   public File getConfigDir() {
      return this.configDir;
   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if (this.textureSetMap.isDirty()) {
         Log.debug("Saving texture set config");
         this.textureSetConfig.save(this.textureSetMap);
         this.textureSetMap.setDirty(false);
      }

      if (this.biomeTextureMap.isDirty()) {
         Log.debug("Saving biome texture config");
         this.biomeTextureConfig.save(this.biomeTextureMap);
         this.biomeTextureMap.setDirty(false);
      }

      if (this.tileTextureMap.isDirty()) {
         Log.debug("Saving tile texture config");
         this.tileTextureConfig.save(this.tileTextureMap);
         this.tileTextureMap.setDirty(false);
      }

      if (MarkerRegistry.INSTANCE.isDirty()) {
         Log.debug("Saving marker config");
         this.markerTextureConfig.save(MarkerRegistry.INSTANCE);
         MarkerRegistry.INSTANCE.setDirty(false);
      }

   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      for(MarkerType type : MarkerRegistry.getValues()) {
         type.initMips();
      }

   }
}
