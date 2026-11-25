package rtg;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Biomes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rtg.api.RTGAPI;
import rtg.api.util.BlockUtil;
import rtg.api.util.Logger;

@EventBusSubscriber(
   modid = "rtg"
)
public final class RTGConfig {
   public static final String LANG_ROOT = "rtgconfig.";
   private static Configuration config;

   private RTGConfig() {
   }

   public static void init(FMLPreInitializationEvent event) {
      if (config == null) {
         config = new Configuration(RTGAPI.getConfigPath().resolve(event.getSuggestedConfigurationFile().getName()).toFile());
      }

      config.load();
      Arrays.stream(RTGConfig.Setting.values()).forEach(RTGConfig::generateProperty);
      sync();
   }

   private static void sync() {
      config.getCategoryNames().stream().map((c) -> {
         return config.getCategory(c);
      }).forEach((cat) -> {
         cat.setLanguageKey("rtgconfig." + cat.getQualifiedName());
      });
      if (config.hasChanged()) {
         Logger.debug("[RTGConfig#sync] Config changed; Saving...");
         config.save();
      }

      Arrays.stream(RTGConfig.Setting.values()).forEach((setting) -> {
         switch(setting) {
         case worldTypeNotification:
         case rtgWorldTypeByDefault:
         case enableDebugging:
         case additionalBiomeInfo:
         case oreGenEventCancellation:
         case lushRiverbanksInDesert:
         case rtgTreesFromSaplings:
         case treesCanGenerateOnSand:
         case shrubsBelowSurface:
         case barkCoveredLogs:
            setting.setCurVal(Boolean.valueOf(getProperty(setting).getBoolean()));
            break;
         case patchBiome:
         case shadowStoneBlock:
         case shadowDesertBlock:
            setting.setCurVal(getProperty(setting).getString());
            break;
         case surfaceBlendRadius:
         case rtgTreeChance:
            setting.setCurVal(Integer.valueOf(getProperty(setting).getInt()));
            break;
         case treeDensityMultiplier:
            setting.setCurVal(Double.valueOf(getProperty(setting).getDouble()));
         }

      });
      RTGAPI.setShadowBlocks(shadowStoneBlock(), shadowDesertBlock());
   }

   private static Property generateProperty(RTGConfig.Setting setting) {
      switch(setting.getType()) {
      case BOOLEAN:
         return config.get(setting.getCategory(), setting.name(), ((Boolean)setting.getCurVal()).booleanValue(), setting.getComment()).setLanguageKey(setting.getLangKey()).setRequiresMcRestart(setting.reqRestart());
      case STRING:
         return config.get(setting.getCategory(), setting.name(), (String)setting.getCurVal(), setting.getComment()).setLanguageKey(setting.getLangKey()).setRequiresMcRestart(setting.reqRestart());
      case INTEGER:
         return config.get(setting.getCategory(), setting.name(), ((Integer)setting.getCurVal()).intValue(), setting.getComment()).setMinValue(((Integer)setting.getMinVal()).intValue()).setMaxValue(((Integer)setting.getMaxVal()).intValue()).setLanguageKey(setting.getLangKey()).setRequiresMcRestart(setting.reqRestart());
      case DOUBLE:
         return config.get(setting.getCategory(), setting.name(), ((Double)setting.getCurVal()).doubleValue(), setting.getComment()).setMinValue(((Double)setting.getMinVal()).doubleValue()).setMaxValue(((Double)setting.getMaxVal()).doubleValue()).setLanguageKey(setting.getLangKey()).setRequiresMcRestart(setting.reqRestart());
      default:
         return config.get(setting.getCategory(), setting.name(), setting.getDefVal().toString(), setting.getComment()).setLanguageKey(setting.getLangKey()).setRequiresMcRestart(setting.reqRestart());
      }
   }

   private static Property getProperty(RTGConfig.Setting setting) {
      Property ret = null;
      if (config.hasCategory(setting.getCategory())) {
         ret = config.getCategory(setting.getCategory()).get(setting.name());
      }

      return ret != null ? ret : generateProperty(setting);
   }

   public static boolean worldTypeNotification() {
      return ((Boolean)RTGConfig.Setting.worldTypeNotification.getCurVal()).booleanValue();
   }

   public static boolean rtgWorldTypeByDefault() {
      return ((Boolean)RTGConfig.Setting.rtgWorldTypeByDefault.getCurVal()).booleanValue();
   }

   public static boolean enableDebugging() {
      return ((Boolean)RTGConfig.Setting.enableDebugging.getCurVal()).booleanValue();
   }

   public static boolean additionalBiomeInfo() {
      return ((Boolean)RTGConfig.Setting.additionalBiomeInfo.getCurVal()).booleanValue();
   }

   public static Biome patchBiome() {
      String cfgBiome = (String)RTGConfig.Setting.patchBiome.getCurVal();
      Biome biome;
      if ((biome = getBiomeFromCfgString(cfgBiome)) == null) {
         Logger.error("Erroneous patch biome set in config: {} (non-existant). Using default.", cfgBiome);
         return Biomes.PLAINS;
      } else {
         return biome;
      }
   }

   public static boolean allowOreGenEventCancel() {
      return ((Boolean)RTGConfig.Setting.oreGenEventCancellation.getCurVal()).booleanValue();
   }

   public static boolean lushRiverbanksInDesert() {
      return ((Boolean)RTGConfig.Setting.lushRiverbanksInDesert.getCurVal()).booleanValue();
   }

   public static int surfaceBlendRadius() {
      return ((Integer)RTGConfig.Setting.surfaceBlendRadius.getCurVal()).intValue();
   }

   public static IBlockState shadowStoneBlock() {
      return BlockUtil.getBlockStateFromCfgString((String)RTGConfig.Setting.shadowStoneBlock.getCurVal(), BlockUtil.getStateClay(EnumDyeColor.CYAN));
   }

   public static IBlockState shadowDesertBlock() {
      return BlockUtil.getBlockStateFromCfgString((String)RTGConfig.Setting.shadowDesertBlock.getCurVal(), BlockUtil.getStateClay(EnumDyeColor.GRAY));
   }

   public static boolean rtgTreesFromSaplings() {
      return ((Boolean)RTGConfig.Setting.rtgTreesFromSaplings.getCurVal()).booleanValue();
   }

   public static int rtgTreeChance() {
      return ((Integer)RTGConfig.Setting.rtgTreeChance.getCurVal()).intValue();
   }

   public static double treeDensityMultiplier() {
      return ((Double)RTGConfig.Setting.treeDensityMultiplier.getCurVal()).doubleValue();
   }

   public static boolean treesCanGenerateOnSand() {
      return ((Boolean)RTGConfig.Setting.treesCanGenerateOnSand.getCurVal()).booleanValue();
   }

   public static boolean shrubsBelowSurface() {
      return ((Boolean)RTGConfig.Setting.shrubsBelowSurface.getCurVal()).booleanValue();
   }

   public static boolean barkCoveredLogs() {
      return ((Boolean)RTGConfig.Setting.barkCoveredLogs.getCurVal()).booleanValue();
   }

   public static void toggleWorldTypeNotification() {
      getProperty(RTGConfig.Setting.worldTypeNotification).setValue(false);
      sync();
   }

   @SubscribeEvent
   public static void onConfigChange(OnConfigChangedEvent event) {
      if ("rtg".equals(event.getModID())) {
         sync();
      }

   }

   @Nullable
   public static Biome getBiomeFromCfgString(String cfgString) {
      ResourceLocation rl = new ResourceLocation(cfgString);
      return ForgeRegistries.BIOMES.containsKey(rl) ? (Biome)ForgeRegistries.BIOMES.getValue(rl) : null;
   }

   public static Biome getBiomeFromCfgString(String cfgString, Biome fallback) {
      Biome biome = getBiomeFromCfgString(cfgString);
      return biome != null ? biome : fallback;
   }

   public static enum Category {
      client((RTGConfig.Category)null),
      common((RTGConfig.Category)null),
      debug(common),
      surface(common),
      trees(common);

      private final RTGConfig.Category parent;

      private Category(@Nullable RTGConfig.Category parentIn) {
         this.parent = parentIn;
      }

      public String toString() {
         return this.parent != null ? this.parent.toString() + "." + this.name() : this.name();
      }
   }

   public static final class RTGGuiConfig extends GuiConfig {
      RTGGuiConfig(GuiScreen parent) {
         super(parent, getElements(), "rtg", false, false, I18n.format("rtgconfig.title", new Object[0]));
      }

      private static List<IConfigElement> getElements() {
         return (List)RTGConfig.config.getCategoryNames().stream().filter((cat) -> {
            return !RTGConfig.config.getCategory(cat).isChild();
         }).map((cat) -> {
            return new ConfigElement(RTGConfig.config.getCategory(cat));
         }).collect(Collectors.toList());
      }
   }

   public static final class RTGGuiConfigFactory implements IModGuiFactory {
      public static final String LOCATION = "rtg.RTGConfig$RTGGuiConfigFactory";

      public RTGGuiConfigFactory() {
      }

      public void initialize(Minecraft mc) {
      }

      public boolean hasConfigGui() {
         return true;
      }

      public GuiScreen createConfigGui(GuiScreen parentScreen) {
         return new RTGConfig.RTGGuiConfig(parentScreen);
      }

      @Nullable
      public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
         return null;
      }
   }

   public static enum Setting {
      worldTypeNotification(Type.BOOLEAN, RTGConfig.Category.client, "When enabled, this will display an informational message about RTG when entering the Customize World screen.\nThis will display once and automatically disable itself.", true),
      rtgWorldTypeByDefault(Type.BOOLEAN, RTGConfig.Category.client, "Automatically select the RTG world type when creating a new world", true),
      enableDebugging(Type.BOOLEAN, RTGConfig.Category.debug, "Enable extra debug logging.\n!This setting has a severe performance penalty. Only enable if you know what you are doing!", false),
      additionalBiomeInfo(Type.BOOLEAN, RTGConfig.Category.debug, "Enable the logging of additional biome information on startup.", false),
      patchBiome(Type.STRING, RTGConfig.Category.debug, "If RTG encounters an unsupported biome it will generate this biome instead.\nThis uses the standard ResourceLocation format: mod_id:biome_registry_name", "minecraft:plains"),
      oreGenEventCancellation(Type.BOOLEAN, RTGConfig.Category.debug, "\nTO BE REMOVED\n", true),
      lushRiverbanksInDesert(Type.BOOLEAN, RTGConfig.Category.surface, "Set this to FALSE to prevent RTG from generating lush river bank decorations in hot biomes,\nlike Desert and Mesa. Lush decorations consist of tallgrass, trees, shrubs, and other flora.", true, true),
      surfaceBlendRadius(Type.INTEGER, RTGConfig.Category.surface, "The maximum distance surfaces will blend into eacother if enabled for two adjacent biomes.\nBy default, surface blending is only enabled for beaches. You can control that in biome settings", Integer.valueOf(32), Integer.valueOf(8), Integer.valueOf(32)),
      shadowStoneBlock(Type.STRING, RTGConfig.Category.surface, "The block to use for stone terrain shadowing, typically seen on the cliffs of stone mountains.", "minecraft:stained_hardened_clay[color=cyan]", true),
      shadowDesertBlock(Type.STRING, RTGConfig.Category.surface, "The block to use for desert terrain shadowing, typically seen on the cliffs of desert mountains.", "minecraft:stained_hardened_clay[color=gray]", true),
      rtgTreesFromSaplings(Type.BOOLEAN, RTGConfig.Category.trees, "Set this to TRUE to allow RTG's custom trees to grow from vanilla saplings.\nRTG's custom trees can be grown only from the saplings that their leaves would\ndrop, and only in the biomes where they naturally generate.", true),
      rtgTreeChance(Type.INTEGER, RTGConfig.Category.trees, "1/x chance that a vanilla sapling will grow one of RTG's custom trees.\n1 = Always generate if possible, 2 = 50% chance, 4 = 25% chance, etc.", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(20)),
      treeDensityMultiplier(Type.DOUBLE, RTGConfig.Category.trees, "This setting allows you to increase/decrease the number of RTG trees that generate globally.\nThis setting only affects trees generated by RTG. Trees generated by a biome's decorator\nwill adhere to their own density rules.\nYou can override this setting on a per-biome basis by using the biome configs.", 1.0D, 0.0D, 5.0D),
      treesCanGenerateOnSand(Type.BOOLEAN, RTGConfig.Category.trees, "Set this to FALSE to prevent trees from generating on sand.\nThis setting only affects trees generated by RTG. Trees generated by a biome's decorator\nwill adhere to their own generation rules. (RTG's Palm Trees ignore this setting.)", true),
      shrubsBelowSurface(Type.BOOLEAN, RTGConfig.Category.trees, "Set this to FALSE to prevent shrub trunks from generating below the surface.", true),
      barkCoveredLogs(Type.BOOLEAN, RTGConfig.Category.trees, "Set this to FALSE to prevent the trunks of RTG trees from using the 'all-bark' texture model.\nFor more information, visit http://minecraft.gamepedia.com/Wood#Block_data", true);

      private final Type type;
      private final RTGConfig.Category category;
      private final String comment;
      private final boolean reqRestart;
      private final Object defVal;
      private final Object minVal;
      private final Object maxVal;
      private Object curVal;

      private Setting(Type type, RTGConfig.Category category, String comment, Object defVal) {
         this(type, category, comment, defVal, false);
      }

      private Setting(Type type, RTGConfig.Category category, String comment, Object defVal, boolean reqRestart) {
         this(type, category, comment, defVal, (Object)null, (Object)null, reqRestart);
      }

      private Setting(Type type, RTGConfig.Category category, String comment, Object defVal, @Nullable Object minVal, @Nullable Object maxVal) {
         this(type, category, comment, defVal, minVal, maxVal, false);
      }

      private Setting(Type type, RTGConfig.Category category, String comment, Object defVal, @Nullable Object minVal, @Nullable Object maxVal, boolean reqRestart) {
         this.type = type;
         this.category = category;
         this.comment = comment;
         this.reqRestart = reqRestart;
         this.defVal = defVal;
         this.minVal = minVal;
         this.maxVal = maxVal;
         this.curVal = defVal;
      }

      Type getType() {
         return this.type;
      }

      String getCategory() {
         return this.category.toString();
      }

      String getComment() {
         return this.comment;
      }

      boolean reqRestart() {
         return this.reqRestart;
      }

      Object getDefVal() {
         return this.defVal;
      }

      @Nullable
      Object getMinVal() {
         return this.minVal;
      }

      @Nullable
      Object getMaxVal() {
         return this.maxVal;
      }

      Object getCurVal() {
         return this.curVal;
      }

      void setCurVal(Object val) {
         this.curVal = val;
      }

      private String getLangKey() {
         return "rtgconfig." + this.getCategory() + "." + this.name();
      }
   }
}
