package snownee.snow;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Config(
   modid = "snowrealmagic"
)
public final class ModConfig {
   @Name("PlaceSnowInBlock")
   @RequiresMcRestart
   public static boolean placeSnowInBlock = true;
   @Name("ReplaceSnowWorldGen")
   public static boolean replaceSnowWorldGen = true;
   @Name("SnowWorldGenPriority")
   public static int snowWorldGenPriority = 5;
   @Name("SnowGravity")
   public static boolean snowGravity = true;
   @Name("SnowAlwaysReplaceable")
   public static boolean snowAlwaysReplaceable = true;
   @Name("SnowAccumulationDuringSnowstorm")
   public static boolean snowAccumulationDuringSnowstorm = true;
   @Name("SnowAccumulationDuringSnowfall")
   public static boolean snowAccumulationDuringSnowfall = false;
   @Name("ThinnerBoundingBox")
   public static boolean thinnerBoundingBox = true;
   @Name("ParticleThroughLeaves")
   public static boolean particleThroughLeaves = true;
   @Name("SnowMakingIce")
   public static boolean snowMakingIce = true;
   @Name("SnowOnIce")
   public static boolean snowOnIce = false;
   @Name("SnowNeverMelt")
   public static boolean snowNeverMelt = false;
   @Ignore
   public static final Set<ResourceLocation> snowLoggableBlocksSet = Sets.newHashSet();
   @Ignore
   public static final Set<ResourceLocation> yOffsetBlocksSet = Sets.newHashSet();
   @RequiresMcRestart
   @Name("snowLoggableBlocksV2")
   public static String[] snowLoggableBlocks = new String[]{"/(biomesoplenty|xlfoodmod|weeeflowers):.+(flower|mushroom|grass|plant|bamboo|vanilla)/", "defiledlands:scuronotte", "defiledlands:blastem", "/ferdinandsflowers:.+(flower|double|desert|ouch|fungus|dark)/", "/greenery:plant\\/upland\\/.+/", "/harvestcraft:.*garden.*/", "/notreepunching:loose_rock\\/.+/", "/(pyrotech|pyrotech_compat):.*rock.*/", "/plants2:.*(cosmetic|desert|double|harvest|bushling).*/"};
   @RequiresMcRestart
   public static String[] yOffsetBlocks = new String[]{"/notreepunching:loose_rock\\/.+/", "/(pyrotech|pyrotech_compat):.*rock.*/"};

   private ModConfig() {
      throw new UnsupportedOperationException("No instance for you");
   }

   public static void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, ModConfig::onConfigReload);
   }

   public static void onConfigReload(OnConfigChangedEvent event) {
      if (event.getModID().equals("snowrealmagic")) {
         ConfigManager.sync("snowrealmagic", Type.INSTANCE);
      }

   }

   public static void postInit() {
      filterBlocks(snowLoggableBlocksSet, snowLoggableBlocks);
      filterBlocks(yOffsetBlocksSet, yOffsetBlocks);
   }

   private static void filterBlocks(Set<ResourceLocation> set, String[] array) {
      set.clear();

      for(String s : array) {
         if (s.startsWith("/") && s.endsWith("/")) {
            Predicate<String> pattern = Pattern.compile("^" + s.substring(1, s.length() - 1) + "$").asPredicate();

            for(Block block : ForgeRegistries.BLOCKS) {
               if (pattern.test(block.getRegistryName().toString())) {
                  set.add(block.getRegistryName());
               }
            }
         } else {
            ResourceLocation id = new ResourceLocation(s);
            if (ForgeRegistries.BLOCKS.containsKey(id)) {
               set.add(id);
            }
         }
      }

   }

   public static void register() {
      ConfigManager.register("snowrealmagic", "", "general", null,
         ConfigEntry.value("PlaceSnowInBlock", boolean.class, () -> ModConfig.placeSnowInBlock, v -> ModConfig.placeSnowInBlock = (Boolean)v).langKey("snowrealmagic.general.placesnowinblock").requiresMcRestart(),
         ConfigEntry.value("ReplaceSnowWorldGen", boolean.class, () -> ModConfig.replaceSnowWorldGen, v -> ModConfig.replaceSnowWorldGen = (Boolean)v).langKey("snowrealmagic.general.replacesnowworldgen"),
         ConfigEntry.value("SnowWorldGenPriority", int.class, () -> ModConfig.snowWorldGenPriority, v -> ModConfig.snowWorldGenPriority = (Integer)v).langKey("snowrealmagic.general.snowworldgenpriority"),
         ConfigEntry.value("SnowGravity", boolean.class, () -> ModConfig.snowGravity, v -> ModConfig.snowGravity = (Boolean)v).langKey("snowrealmagic.general.snowgravity"),
         ConfigEntry.value("SnowAlwaysReplaceable", boolean.class, () -> ModConfig.snowAlwaysReplaceable, v -> ModConfig.snowAlwaysReplaceable = (Boolean)v).langKey("snowrealmagic.general.snowalwaysreplaceable"),
         ConfigEntry.value("SnowAccumulationDuringSnowstorm", boolean.class, () -> ModConfig.snowAccumulationDuringSnowstorm, v -> ModConfig.snowAccumulationDuringSnowstorm = (Boolean)v).langKey("snowrealmagic.general.snowaccumulationduringsnowstorm"),
         ConfigEntry.value("SnowAccumulationDuringSnowfall", boolean.class, () -> ModConfig.snowAccumulationDuringSnowfall, v -> ModConfig.snowAccumulationDuringSnowfall = (Boolean)v).langKey("snowrealmagic.general.snowaccumulationduringsnowfall"),
         ConfigEntry.value("ThinnerBoundingBox", boolean.class, () -> ModConfig.thinnerBoundingBox, v -> ModConfig.thinnerBoundingBox = (Boolean)v).langKey("snowrealmagic.general.thinnerboundingbox"),
         ConfigEntry.value("ParticleThroughLeaves", boolean.class, () -> ModConfig.particleThroughLeaves, v -> ModConfig.particleThroughLeaves = (Boolean)v).langKey("snowrealmagic.general.particlethroughleaves"),
         ConfigEntry.value("SnowMakingIce", boolean.class, () -> ModConfig.snowMakingIce, v -> ModConfig.snowMakingIce = (Boolean)v).langKey("snowrealmagic.general.snowmakingice"),
         ConfigEntry.value("SnowOnIce", boolean.class, () -> ModConfig.snowOnIce, v -> ModConfig.snowOnIce = (Boolean)v).langKey("snowrealmagic.general.snowonice"),
         ConfigEntry.value("SnowNeverMelt", boolean.class, () -> ModConfig.snowNeverMelt, v -> ModConfig.snowNeverMelt = (Boolean)v).langKey("snowrealmagic.general.snownevermelt"),
         ConfigEntry.value("snowLoggableBlocksV2", String[].class, () -> ModConfig.snowLoggableBlocks, v -> ModConfig.snowLoggableBlocks = (String[])v).langKey("snowrealmagic.general.snowloggableblocks").requiresMcRestart(),
         ConfigEntry.value("yOffsetBlocks", String[].class, () -> ModConfig.yOffsetBlocks, v -> ModConfig.yOffsetBlocks = (String[])v).requiresMcRestart());
   }
}
