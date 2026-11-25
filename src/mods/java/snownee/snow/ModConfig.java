package snownee.snow;

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
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Config(
   modid = "snowrealmagic"
)
@EventBusSubscriber(
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

   @SubscribeEvent
   public static void onConfigReload(OnConfigChangedEvent event) {
      if (event.getModID().equals("snowrealmagic")) {
         ConfigManager.sync("snowrealmagic", Type.INSTANCE);
      }

   }

   public static void postInit() {
      (new Thread(() -> {
         filterBlocks(snowLoggableBlocksSet, snowLoggableBlocks);
         filterBlocks(yOffsetBlocksSet, yOffsetBlocks);
      })).start();
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
}
