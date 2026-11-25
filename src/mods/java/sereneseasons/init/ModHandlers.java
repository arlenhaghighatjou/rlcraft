package sereneseasons.init;

import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.biome.BiomeColorHelper.ColorResolver;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.handler.PacketHandler;
import sereneseasons.handler.season.BirchColorHandler;
import sereneseasons.handler.season.RandomUpdateHandler;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.handler.season.SeasonSleepHandler;
import sereneseasons.handler.season.SeasonalCropGrowthHandler;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.SeasonColourUtil;

public class ModHandlers {
   private static final SeasonHandler SEASON_HANDLER = new SeasonHandler();
   @SideOnly(Side.CLIENT)
   private static ColorResolver originalGrassColorResolver;
   @SideOnly(Side.CLIENT)
   private static ColorResolver originalFoliageColorResolver;

   public ModHandlers() {
   }

   public static void init() {
      PacketHandler.init();
      SEASON_HANDLER.register(MinecraftForge.EVENT_BUS);
      SEASON_HANDLER.register(MinecraftForge.TERRAIN_GEN_BUS);
      SeasonHelper.dataProvider = SEASON_HANDLER;
      (new RandomUpdateHandler()).register(MinecraftForge.EVENT_BUS);
      (new SeasonSleepHandler()).register(MinecraftForge.EVENT_BUS);
      (new SeasonalCropGrowthHandler()).register(MinecraftForge.EVENT_BUS);
      if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
         registerSeasonColourHandlers();
      }

   }

   @SideOnly(Side.CLIENT)
   private static void registerSeasonColourHandlers() {
      originalGrassColorResolver = BiomeColorHelper.GRASS_COLOR;
      originalFoliageColorResolver = BiomeColorHelper.FOLIAGE_COLOR;
      BiomeColorHelper.GRASS_COLOR = (biome, blockPosition) -> {
         SeasonTime calendar = SeasonHandler.getClientSeasonTime();
         ISeasonColorProvider colorProvider = (ISeasonColorProvider)(BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason());
         return SeasonColourUtil.applySeasonalGrassColouring(colorProvider, biome, originalGrassColorResolver.getColorAtPos(biome, blockPosition));
      };
      BiomeColorHelper.FOLIAGE_COLOR = (biome, blockPosition) -> {
         SeasonTime calendar = SeasonHandler.getClientSeasonTime();
         ISeasonColorProvider colorProvider = (ISeasonColorProvider)(BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason());
         return SeasonColourUtil.applySeasonalFoliageColouring(colorProvider, biome, originalFoliageColorResolver.getColorAtPos(biome, blockPosition));
      };
   }

   public static void postInit() {
      if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
         BirchColorHandler.init();
      }

   }
}
