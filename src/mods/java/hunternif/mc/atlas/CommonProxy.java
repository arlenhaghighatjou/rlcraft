package hunternif.mc.atlas;

import hunternif.mc.atlas.core.BiomeDetectorBase;
import hunternif.mc.atlas.ext.ExtTileConfig;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.Log;
import java.io.File;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

public class CommonProxy {
   File configDir;
   private ExtTileIdMap extTileIdMap;
   private ExtTileConfig extTileConfig;

   public CommonProxy() {
   }

   public MinecraftServer getServer() {
      return FMLServerHandler.instance().getServer();
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.configDir = new File(event.getModConfigurationDirectory(), "antiqueatlas");
      this.configDir.mkdir();
      this.extTileIdMap = ExtTileIdMap.instance();
      this.extTileConfig = new ExtTileConfig(new File(this.configDir, "tileids.json"));
      this.extTileConfig.load(this.extTileIdMap);
      this.registerVanillaCustomTiles();
      this.checkSaveConfig();
   }

   public void init(FMLInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void postInit(FMLPostInitializationEvent event) {
      AntiqueAtlasMod.instance.jeidPresent = Loader.isModLoaded("jeid");
      BiomeDetectorBase.setBiomeArrayMethod(AntiqueAtlasMod.instance.jeidPresent);
      BiomeDetectorBase.scanBiomeTypes();
   }

   private void registerVanillaCustomTiles() {
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageLibrary");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageSmithy");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageLHouse");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageFarmlandLarge");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageFarmlandSmall");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageWell");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageTorch");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageHut");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageSmallHouse");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageButchersShop");
      this.extTileIdMap.getOrCreatePseudoBiomeID("npcVillageChurch");
      this.extTileIdMap.getOrCreatePseudoBiomeID("lava");
      this.extTileIdMap.getOrCreatePseudoBiomeID("lavaShore");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherBridge");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherBridgeX");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherBridgeZ");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherBridgeEndX");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherBridgeEndZ");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherBridgeGate");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherTower");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherWall");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherHall");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherFortStairs");
      this.extTileIdMap.getOrCreatePseudoBiomeID("netherThrone");
      this.extTileIdMap.getOrCreatePseudoBiomeID("endIsland");
      this.extTileIdMap.getOrCreatePseudoBiomeID("endIslandPlants");
      this.extTileIdMap.getOrCreatePseudoBiomeID("endVoid");
      this.extTileIdMap.getOrCreatePseudoBiomeID("ravine");
   }

   public void openAtlasGUI(ItemStack stack) {
   }

   public void openAtlasGUI() {
   }

   public File getConfigDir() {
      return this.configDir;
   }

   public EntityPlayer getPlayerEntity(MessageContext ctx) {
      return ctx.getServerHandler().player;
   }

   public IThreadListener getThreadFromContext(MessageContext ctx) {
      return ctx.getServerHandler().player.getServer();
   }

   @SubscribeEvent
   public void onWorldSave(Save event) {
      this.checkSaveConfig();
   }

   private void checkSaveConfig() {
      if (this.extTileIdMap.isDirty()) {
         Log.info("Saving ext tile id config");
         this.extTileConfig.save(this.extTileIdMap);
         this.extTileIdMap.setDirty(false);
      }

   }
}
