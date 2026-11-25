package rtg.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;
import rtg.RTG;
import rtg.api.RTGAPI;
import rtg.api.util.Logger;
import rtg.api.world.RTGWorld;
import rtg.world.biome.BiomeProviderRTG;
import rtg.world.gen.ChunkGeneratorRTG;

public final class WorldTypeRTG extends WorldType {
   private static WorldTypeRTG INSTANCE;

   private WorldTypeRTG() {
      super("RTG");
   }

   public static WorldTypeRTG getInstance() {
      if (INSTANCE == null) {
         init();
      }

      return INSTANCE;
   }

   public static void init() {
      INSTANCE = new WorldTypeRTG();
   }

   public BiomeProvider getBiomeProvider(World world) {
      if (!RTGAPI.isAllowedDimensionType(world.provider.getDimension()) && !DimensionManager.getProviderType(world.provider.getDimension()).getSuffix().equals("_rtg") && !DimensionManager.getProviderType(world.provider.getDimension()).name().startsWith("jed_surface")) {
         Logger.debug("WorldType#getBiomeProvider: DimensionType not in whitelist (ID:{}, Type:{}, Suffix:{}).. returning BiomeProvider", world.provider.getDimension(), world.provider.getDimensionType(), DimensionManager.getProviderType(world.provider.getDimension()).getSuffix());
         return new BiomeProvider(world.getWorldInfo());
      } else {
         Logger.debug("WorldType#getBiomeProvider: Allowed DimensionType detected (ID:{}, Type:{}, Suffix:{}).. returning BiomeProviderRTG", world.provider.getDimension(), world.provider.getDimensionType(), DimensionManager.getProviderType(world.provider.getDimension()).getSuffix());
         return new BiomeProviderRTG(RTGWorld.getInstance(world));
      }
   }

   public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
      if (!RTGAPI.isAllowedDimensionType(world.provider.getDimension()) && !DimensionManager.getProviderType(world.provider.getDimension()).getSuffix().equals("_rtg") && !DimensionManager.getProviderType(world.provider.getDimension()).name().startsWith("jed_surface")) {
         Logger.debug("WorldType#getChunkGenerator: DimensionType not in whitelist (ID:{}, Type:{}, Suffix:{}).. returning ChunkGeneratorOverworld", world.provider.getDimension(), world.provider.getDimensionType(), DimensionManager.getProviderType(world.provider.getDimension()).getSuffix());
         return new ChunkGeneratorOverworld(world, world.getWorldInfo().getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().getGeneratorOptions());
      } else {
         Logger.debug("WorldType#getChunkGenerator: Allowed DimensionType detected (ID:{}, Type:{}, Suffix:{}).. returning ChunkGeneratorRTG", world.provider.getDimension(), world.provider.getDimensionType(), DimensionManager.getProviderType(world.provider.getDimension()).getSuffix());
         return new ChunkGeneratorRTG(RTGWorld.getInstance(world));
      }
   }

   public float getCloudHeight() {
      return 256.0F;
   }

   public boolean isCustomizable() {
      return true;
   }

   public String getTranslationKey() {
      return "gui.createWorld.worldtypename";
   }

   public void onCustomizeButton(Minecraft mc, GuiCreateWorld guiCreateWorld) {
      RTG.getProxy().displayCustomizeWorldScreen(guiCreateWorld);
   }
}
