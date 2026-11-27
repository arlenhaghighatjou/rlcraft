package mezz.jei.config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.arlenh7.eaglercraft.awt.Color;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.color.ColorGetter;
import mezz.jei.color.ColorNamer;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientListElementFactory;
import mezz.jei.network.packets.PacketRequestCheatPermission;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.startup.IModIdHelper;
import mezz.jei.util.GiveMode;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class Config {
   private static final String configKeyPrefix = "config.jei";
   public static final String CATEGORY_SEARCH = "search";
   public static final String CATEGORY_ADVANCED = "advanced";
   public static final String CATEGORY_SEARCH_COLORS = "searchColors";
   public static final String defaultModNameFormatFriendly = "blue italic";
   public static final int smallestNumColumns = 4;
   public static final int largestNumColumns = 100;
   public static final int minRecipeGuiHeight = 175;
   public static final int maxRecipeGuiHeight = 5000;
   @Nullable
   private static LocalizedConfiguration config;
   @Nullable
   private static Configuration worldConfig;
   @Nullable
   private static LocalizedConfiguration itemBlacklistConfig;
   @Nullable
   private static LocalizedConfiguration searchColorsConfig;
   @Nullable
   private static VFile2 bookmarkFile;
   private static final ConfigValues defaultValues = new ConfigValues();
   private static final ConfigValues values = new ConfigValues();
   @Nullable
   private static String modNameFormatOverride;
   private static final Set<String> itemBlacklist = new HashSet();
   private static final String[] defaultItemBlacklist = new String[0];

   private Config() {
   }

   public static boolean isOverlayEnabled() {
      return values.overlayEnabled || KeyBindings.toggleOverlay.getKeyCode() == 0;
   }

   public static void toggleOverlayEnabled() {
      values.overlayEnabled = !values.overlayEnabled;
      if (worldConfig != null) {
         INetworkManager networkManager = FMLClientHandler.instance().getClientToServerNetworkManager();
         String worldCategory = ServerInfo.getWorldUid(networkManager);
         Property property = worldConfig.get(worldCategory, "overlayEnabled", defaultValues.overlayEnabled);
         property.set(values.overlayEnabled);
         if (worldConfig.hasChanged()) {
            worldConfig.save();
         }
      }

      MinecraftForge.EVENT_BUS.post(new OverlayToggleEvent(values.overlayEnabled));
   }

   public static boolean isBookmarkOverlayEnabled() {
      return isOverlayEnabled() && values.bookmarkOverlayEnabled;
   }

   public static void toggleBookmarkEnabled() {
      values.bookmarkOverlayEnabled = !values.bookmarkOverlayEnabled;
      if (worldConfig != null) {
         INetworkManager networkManager = FMLClientHandler.instance().getClientToServerNetworkManager();
         String worldCategory = ServerInfo.getWorldUid(networkManager);
         Property property = worldConfig.get(worldCategory, "bookmarkOverlayEnabled", defaultValues.bookmarkOverlayEnabled);
         property.set(values.bookmarkOverlayEnabled);
         if (worldConfig.hasChanged()) {
            worldConfig.save();
         }
      }

      MinecraftForge.EVENT_BUS.post(new BookmarkOverlayToggleEvent(values.bookmarkOverlayEnabled));
   }

   public static boolean isCheatItemsEnabled() {
      return values.cheatItemsEnabled;
   }

   public static void toggleCheatItemsEnabled() {
      setCheatItemsEnabled(!values.cheatItemsEnabled);
   }

   public static void setCheatItemsEnabled(boolean value) {
      if (values.cheatItemsEnabled != value) {
         values.cheatItemsEnabled = value;
         if (worldConfig != null) {
            INetworkManager networkManager = FMLClientHandler.instance().getClientToServerNetworkManager();
            String worldCategory = ServerInfo.getWorldUid(networkManager);
            Property property = worldConfig.get(worldCategory, "cheatItemsEnabled", defaultValues.cheatItemsEnabled);
            property.set(values.cheatItemsEnabled);
            if (worldConfig.hasChanged()) {
               worldConfig.save();
            }
         }

         if (values.cheatItemsEnabled && ServerInfo.isJeiOnServer()) {
            JustEnoughItems.getProxy().sendPacketToServer(new PacketRequestCheatPermission());
         }
      }

   }

   public static boolean isEditModeEnabled() {
      return values.editModeEnabled;
   }

   public static void toggleEditModeEnabled() {
      values.editModeEnabled = !values.editModeEnabled;
      if (worldConfig != null) {
         INetworkManager networkManager = FMLClientHandler.instance().getClientToServerNetworkManager();
         String worldCategory = ServerInfo.getWorldUid(networkManager);
         Property property = worldConfig.get(worldCategory, "editEnabled", defaultValues.editModeEnabled);
         property.set(values.editModeEnabled);
         if (worldConfig.hasChanged()) {
            worldConfig.save();
         }
      }

      MinecraftForge.EVENT_BUS.post(new EditModeToggleEvent(values.editModeEnabled));
   }

   public static boolean isDebugModeEnabled() {
      return values.debugModeEnabled;
   }

   public static boolean isDeleteItemsInCheatModeActive() {
      return values.cheatItemsEnabled && ServerInfo.isJeiOnServer();
   }

   public static boolean isCenterSearchBarEnabled() {
      return values.centerSearchBarEnabled;
   }

   public static boolean isOptimizeMemoryUsage() {
      return values.optimizeMemoryUsage;
   }

   public static GiveMode getGiveMode() {
      return values.giveMode;
   }

   public static String getModNameFormat() {
      String override = modNameFormatOverride;
      return override != null ? override : values.modNameFormat;
   }

   public static boolean isModNameFormatOverrideActive() {
      return modNameFormatOverride != null;
   }

   public static void checkForModNameFormatOverride() {
      IModIdHelper modIdHelper = ForgeModIdHelper.getInstance();
      modNameFormatOverride = modIdHelper.getModNameTooltipFormatting();
      if (config != null) {
         updateModNameFormat(config);
      }

   }

   public static int getMaxColumns() {
      return values.maxColumns;
   }

   public static int getMaxRecipeGuiHeight() {
      return values.maxRecipeGuiHeight;
   }

   public static Config.SearchMode getModNameSearchMode() {
      return values.modNameSearchMode;
   }

   public static Config.SearchMode getTooltipSearchMode() {
      return values.tooltipSearchMode;
   }

   public static Config.SearchMode getOreDictSearchMode() {
      return values.oreDictSearchMode;
   }

   public static Config.SearchMode getCreativeTabSearchMode() {
      return values.creativeTabSearchMode;
   }

   public static Config.SearchMode getColorSearchMode() {
      return values.colorSearchMode;
   }

   public static Config.SearchMode getResourceIdSearchMode() {
      return values.resourceIdSearchMode;
   }

   public static boolean getSearchAdvancedTooltips() {
      return values.searchAdvancedTooltips;
   }

   public static boolean setFilterText(String filterText) {
      if (values.filterText.equals(filterText)) {
         return false;
      } else {
         values.filterText = filterText;
         return true;
      }
   }

   public static String getFilterText() {
      return values.filterText;
   }

   public static void saveFilterText() {
      if (worldConfig != null) {
         INetworkManager networkManager = FMLClientHandler.instance().getClientToServerNetworkManager();
         String worldCategory = ServerInfo.getWorldUid(networkManager);
         Property property = worldConfig.get(worldCategory, "filterText", defaultValues.filterText);
         property.set(values.filterText);
         if (worldConfig.hasChanged()) {
            worldConfig.save();
         }
      }

   }

   @Nullable
   public static LocalizedConfiguration getConfig() {
      return config;
   }

   @Nullable
   public static Configuration getWorldConfig() {
      return worldConfig;
   }

   @Nullable
   public static VFile2 getBookmarkFile() {
      return bookmarkFile;
   }

   public static void preInit(FMLPreInitializationEvent event) {
      VFile2 jeiConfigurationDir = new VFile2(event.getModConfigurationDirectory(), "jei");
      VFile2 configFile = new VFile2(jeiConfigurationDir, "jei.cfg");
      VFile2 itemBlacklistConfigFile = new VFile2(jeiConfigurationDir, "itemBlacklist.cfg");
      VFile2 searchColorsConfigFile = new VFile2(jeiConfigurationDir, "searchColors.cfg");
      VFile2 worldConfigFile = new VFile2(jeiConfigurationDir, "worldSettings.cfg");
      bookmarkFile = new VFile2(jeiConfigurationDir, "bookmarks.ini");
      worldConfig = new Configuration(worldConfigFile, "0.1.0");
      config = new LocalizedConfiguration("config.jei", configFile, "0.4.0");
      itemBlacklistConfig = new LocalizedConfiguration("config.jei", itemBlacklistConfigFile, "0.1.0");
      searchColorsConfig = new LocalizedConfiguration("config.jei", searchColorsConfigFile, "0.1.0");
      syncConfig();
      syncItemBlacklistConfig();
      syncSearchColorsConfig();
   }

   public static boolean syncAllConfig() {
      boolean needsReload = false;
      if (syncConfig()) {
         needsReload = true;
      }

      if (syncItemBlacklistConfig()) {
         needsReload = true;
      }

      INetworkManager networkManager = FMLClientHandler.instance().getClientToServerNetworkManager();
      if (syncWorldConfig(networkManager)) {
         needsReload = true;
      }

      if (syncSearchColorsConfig()) {
         needsReload = true;
      }

      return needsReload;
   }

   private static boolean syncConfig() {
      if (config == null) {
         return false;
      } else {
         boolean needsReload = false;
         config.addCategory("search");
         config.addCategory("advanced");
         ConfigCategory modeCategory = config.getCategory("mode");
         if (modeCategory != null) {
            config.removeCategory(modeCategory);
         }

         ConfigCategory addonsCategory = config.getCategory("addons");
         if (addonsCategory != null) {
            config.removeCategory(addonsCategory);
         }

         ConfigCategory interfaceCategory = config.getCategory("interface");
         if (interfaceCategory != null) {
            config.removeCategory(interfaceCategory);
         }

         ConfigCategory searchCategory = config.getCategory("search");
         searchCategory.remove("atPrefixRequiredForModName");
         searchCategory.remove("prefixRequiredForTooltipSearch");
         searchCategory.remove("prefixRequiredForOreDictSearch");
         searchCategory.remove("prefixRequiredForCreativeTabSearch");
         searchCategory.remove("prefixRequiredForColorSearch");
         Config.SearchMode[] searchModes = Config.SearchMode.values();
         String loadedConfigVersion = config.getLoadedConfigVersion();
         if (loadedConfigVersion != null && versionCompare(loadedConfigVersion, "0.3.0") < 0) {
            config.setEnum("creativeTabSearchMode", "search", defaultValues.creativeTabSearchMode, searchModes);
            config.setEnum("oreDictSearchMode", "search", defaultValues.oreDictSearchMode, searchModes);
         }

         values.modNameSearchMode = (Config.SearchMode)config.getEnum("modNameSearchMode", "search", defaultValues.modNameSearchMode, searchModes);
         values.tooltipSearchMode = (Config.SearchMode)config.getEnum("tooltipSearchMode", "search", defaultValues.tooltipSearchMode, searchModes);
         values.oreDictSearchMode = (Config.SearchMode)config.getEnum("oreDictSearchMode", "search", defaultValues.oreDictSearchMode, searchModes);
         values.creativeTabSearchMode = (Config.SearchMode)config.getEnum("creativeTabSearchMode", "search", defaultValues.creativeTabSearchMode, searchModes);
         values.colorSearchMode = (Config.SearchMode)config.getEnum("colorSearchMode", "search", defaultValues.colorSearchMode, searchModes);
         values.resourceIdSearchMode = (Config.SearchMode)config.getEnum("resourceIdSearchMode", "search", defaultValues.resourceIdSearchMode, searchModes);
         if (config.getCategory("search").hasChanged()) {
            needsReload = true;
         }

         values.searchAdvancedTooltips = config.getBoolean("searchAdvancedTooltips", "search", defaultValues.searchAdvancedTooltips);
         ConfigCategory categoryAdvanced = config.getCategory("advanced");
         categoryAdvanced.remove("nbtKeyIgnoreList");
         categoryAdvanced.remove("deleteItemsInCheatModeEnabled");
         categoryAdvanced.remove("hideLaggyModelsEnabled");
         categoryAdvanced.remove("hideMissingModelsEnabled");
         categoryAdvanced.remove("debugItemEnabled");
         categoryAdvanced.remove("colorSearchEnabled");
         categoryAdvanced.remove("maxSubtypes");
         values.centerSearchBarEnabled = config.getBoolean("advanced", "centerSearchBarEnabled", defaultValues.centerSearchBarEnabled);
         values.optimizeMemoryUsage = config.getBoolean("advanced", "optimizeMemoryUsage", defaultValues.optimizeMemoryUsage);
         values.giveMode = (GiveMode)config.getEnum("giveMode", "advanced", defaultValues.giveMode, GiveMode.values());
         values.maxColumns = config.getInt("maxColumns", "advanced", defaultValues.maxColumns, 4, 100);
         values.maxRecipeGuiHeight = config.getInt("maxRecipeGuiHeight", "advanced", defaultValues.maxRecipeGuiHeight, 175, 5000);
         updateModNameFormat(config);
         Property property = config.get("advanced", "debugModeEnabled", defaultValues.debugModeEnabled);
         property.setShowInGui(false);
         values.debugModeEnabled = property.getBoolean();
         boolean configChanged = config.hasChanged();
         if (configChanged) {
            config.save();
         }

         return needsReload;
      }
   }

   private static void updateModNameFormat(LocalizedConfiguration config) {
      EnumSet<TextFormatting> validFormatting = EnumSet.allOf(TextFormatting.class);
      validFormatting.remove(TextFormatting.RESET);
      String[] validValues = new String[validFormatting.size()];
      int i = 0;

      for(TextFormatting formatting : validFormatting) {
         validValues[i] = formatting.getFriendlyName().toLowerCase(Locale.ENGLISH);
         ++i;
      }

      Property property = config.getString("modNameFormat", "advanced", "blue italic", validValues);
      boolean showInGui = !isModNameFormatOverrideActive();
      property.setShowInGui(showInGui);
      String modNameFormatFriendly = property.getString();
      values.modNameFormat = parseFriendlyModNameFormat(modNameFormatFriendly);
   }

   public static String parseFriendlyModNameFormat(String formatWithEnumNames) {
      if (formatWithEnumNames.isEmpty()) {
         return "";
      } else {
         StringBuilder format = new StringBuilder();
         String[] strings = formatWithEnumNames.split(" ");

         for(String string : strings) {
            TextFormatting valueByName = TextFormatting.getValueByName(string);
            if (valueByName != null) {
               format.append(valueByName.toString());
            } else {
               Log.get().error("Invalid format: {}", string);
            }
         }

         return format.toString();
      }
   }

   private static boolean syncItemBlacklistConfig() {
      if (itemBlacklistConfig == null) {
         return false;
      } else {
         itemBlacklistConfig.addCategory("advanced");
         String[] itemBlacklistArray = itemBlacklistConfig.getStringList("itemBlacklist", "advanced", defaultItemBlacklist);
         itemBlacklist.clear();
         Collections.addAll(itemBlacklist, itemBlacklistArray);
         boolean configChanged = itemBlacklistConfig.hasChanged();
         if (configChanged) {
            itemBlacklistConfig.save();
         }

         return configChanged;
      }
   }

   public static boolean syncWorldConfig(@Nullable INetworkManager networkManager) {
      if (worldConfig == null) {
         return false;
      } else {
         String worldCategory = ServerInfo.getWorldUid(networkManager);
         Property property = worldConfig.get(worldCategory, "overlayEnabled", defaultValues.overlayEnabled);
         property.setLanguageKey("config.jei.interface.overlayEnabled");
         property.setComment(Translator.translateToLocal("config.jei.interface.overlayEnabled.comment"));
         property.setShowInGui(false);
         values.overlayEnabled = property.getBoolean();
         property = worldConfig.get(worldCategory, "cheatItemsEnabled", defaultValues.cheatItemsEnabled);
         property.setLanguageKey("config.jei.mode.cheatItemsEnabled");
         property.setComment(Translator.translateToLocal("config.jei.mode.cheatItemsEnabled.comment"));
         values.cheatItemsEnabled = property.getBoolean();
         property = worldConfig.get(worldCategory, "editEnabled", defaultValues.editModeEnabled);
         property.setLanguageKey("config.jei.mode.editEnabled");
         property.setComment(Translator.translateToLocal("config.jei.mode.editEnabled.comment"));
         values.editModeEnabled = property.getBoolean();
         if (property.hasChanged()) {
            MinecraftForge.EVENT_BUS.post(new EditModeToggleEvent(values.editModeEnabled));
         }

         property = worldConfig.get(worldCategory, "bookmarkOverlayEnabled", defaultValues.bookmarkOverlayEnabled);
         property.setLanguageKey("config.jei.interface.bookmarkOverlayEnabled");
         property.setComment(Translator.translateToLocal("config.jei.interface.bookmarkOverlayEnabled.comment"));
         property.setShowInGui(false);
         values.bookmarkOverlayEnabled = property.getBoolean();
         property = worldConfig.get(worldCategory, "filterText", defaultValues.filterText);
         property.setShowInGui(false);
         values.filterText = property.getString();
         boolean configChanged = worldConfig.hasChanged();
         if (configChanged) {
            worldConfig.save();
         }

         return false;
      }
   }

   private static boolean syncSearchColorsConfig() {
      if (searchColorsConfig == null) {
         return false;
      } else {
         searchColorsConfig.addCategory("searchColors");
         String[] searchColorDefaults = ColorGetter.getColorDefaults();
         String[] searchColors = searchColorsConfig.getStringList("searchColors", "searchColors", searchColorDefaults);
         Builder<Color, String> searchColorsMapBuilder = ImmutableMap.builder();

         for(String entry : searchColors) {
            String[] values = entry.split(":");
            if (values.length != 2) {
               Log.get().error("Invalid format for searchColor entry: {}", entry);
            } else {
               try {
                  String name = values[0];
                  int colorValue = Integer.decode("0x" + values[1]).intValue();
                  Color color = new Color(colorValue);
                  searchColorsMapBuilder.put(color, name);
               } catch (NumberFormatException var11) {
                  Log.get().error("Invalid number format for searchColor entry: {}", entry, var11);
               }
            }
         }

         ColorNamer colorNamer = new ColorNamer(searchColorsMapBuilder.build());
         Internal.setColorNamer(colorNamer);
         boolean configChanged = searchColorsConfig.hasChanged();
         if (configChanged) {
            searchColorsConfig.save();
         }

         return configChanged;
      }
   }

   private static void updateBlacklist() {
      if (itemBlacklistConfig != null) {
         Property property = itemBlacklistConfig.get("advanced", "itemBlacklist", defaultItemBlacklist);
         String[] currentBlacklist = (String[])itemBlacklist.toArray(new String[0]);
         property.set(currentBlacklist);
         boolean changed = itemBlacklistConfig.hasChanged();
         if (changed) {
            itemBlacklistConfig.save();
         }

      }
   }

   public static <V> void addIngredientToConfigBlacklist(IngredientFilter ingredientFilter, IIngredientRegistry ingredientRegistry, V ingredient, IngredientBlacklistType blacklistType, IIngredientHelper<V> ingredientHelper) {
      IIngredientType<V> ingredientType = ingredientRegistry.getIngredientType(ingredient);
      IIngredientListElement<V> element = IngredientListElementFactory.<V>createUnorderedElement(ingredientRegistry, ingredientType, ingredient, ForgeModIdHelper.getInstance());
      Preconditions.checkNotNull(element, "Failed to create element for blacklist");
      if (blacklistType == IngredientBlacklistType.ITEM) {
         String uid = getIngredientUid(ingredient, IngredientBlacklistType.ITEM, ingredientHelper);
         List<IIngredientListElement<V>> elementsToBeBlacklisted = ingredientFilter.getMatches(element, (input) -> {
            return getIngredientUid(input, IngredientBlacklistType.WILDCARD);
         });
         if (areAllBlacklisted(elementsToBeBlacklisted, uid, IngredientBlacklistType.ITEM)) {
            if (addIngredientToConfigBlacklist(ingredientFilter, element, ingredient, IngredientBlacklistType.WILDCARD, ingredientHelper)) {
               updateBlacklist();
            }

            return;
         }
      }

      if (addIngredientToConfigBlacklist(ingredientFilter, element, ingredient, blacklistType, ingredientHelper)) {
         updateBlacklist();
      }

   }

   private static <V> boolean addIngredientToConfigBlacklist(IngredientFilter ingredientFilter, IIngredientListElement<V> element, V ingredient, IngredientBlacklistType blacklistType, IIngredientHelper<V> ingredientHelper) {
      boolean updated = false;
      if (blacklistType == IngredientBlacklistType.WILDCARD) {
         for(IIngredientListElement<V> elementToBeBlacklisted : ingredientFilter.getMatches(element, (input) -> {
            return getIngredientUid(input, blacklistType);
         })) {
            String uid = getIngredientUid(elementToBeBlacklisted, IngredientBlacklistType.ITEM);
            updated |= itemBlacklist.remove(uid);
         }
      }

      String uid = getIngredientUid(ingredient, blacklistType, ingredientHelper);
      updated = updated | itemBlacklist.add(uid);
      return updated;
   }

   private static <V> boolean areAllBlacklisted(List<IIngredientListElement<V>> elements, String newUid, IngredientBlacklistType blacklistType) {
      for(IIngredientListElement<V> element : elements) {
         String uid = getIngredientUid(element, blacklistType);
         if (!uid.equals(newUid) && !itemBlacklist.contains(uid)) {
            return false;
         }
      }

      return true;
   }

   public static <V> void removeIngredientFromConfigBlacklist(IngredientFilter ingredientFilter, IIngredientRegistry ingredientRegistry, V ingredient, IngredientBlacklistType blacklistType, IIngredientHelper<V> ingredientHelper) {
      IIngredientType<V> ingredientType = ingredientRegistry.getIngredientType(ingredient);
      IIngredientListElement<V> element = IngredientListElementFactory.<V>createUnorderedElement(ingredientRegistry, ingredientType, ingredient, ForgeModIdHelper.getInstance());
      Preconditions.checkNotNull(element, "Failed to create element for blacklist");
      boolean updated = false;
      String modUid = getIngredientUid(ingredient, IngredientBlacklistType.MOD_ID, ingredientHelper);
      if (itemBlacklist.contains(modUid)) {
         updated = true;
         itemBlacklist.remove(modUid);

         for(IIngredientListElement<V> modMatch : ingredientFilter.getMatches(element, (input) -> {
            return getIngredientUid(input, IngredientBlacklistType.MOD_ID);
         })) {
            addIngredientToConfigBlacklist(ingredientFilter, modMatch, modMatch.getIngredient(), IngredientBlacklistType.ITEM, ingredientHelper);
         }
      }

      if (blacklistType == IngredientBlacklistType.ITEM) {
         modUid = getIngredientUid(ingredient, IngredientBlacklistType.WILDCARD, ingredientHelper);
         if (itemBlacklist.contains(modUid)) {
            updated = true;
            itemBlacklist.remove(modUid);

            for(IIngredientListElement<V> modMatch : ingredientFilter.getMatches(element, (input) -> {
               return getIngredientUid(input, IngredientBlacklistType.WILDCARD);
            })) {
               addIngredientToConfigBlacklist(ingredientFilter, modMatch, modMatch.getIngredient(), IngredientBlacklistType.ITEM, ingredientHelper);
            }
         }
      } else if (blacklistType == IngredientBlacklistType.WILDCARD) {
         for(IIngredientListElement<V> modMatch : ingredientFilter.getMatches(element, (input) -> {
            return getIngredientUid(input, IngredientBlacklistType.WILDCARD);
         })) {
            String uid = getIngredientUid(modMatch, IngredientBlacklistType.ITEM);
            updated |= itemBlacklist.remove(uid);
         }
      }

      modUid = getIngredientUid(ingredient, blacklistType, ingredientHelper);
      updated = updated | itemBlacklist.remove(modUid);
      if (updated) {
         updateBlacklist();
      }

   }

   public static <V> boolean isIngredientOnConfigBlacklist(V ingredient, IIngredientHelper<V> ingredientHelper) {
      for(IngredientBlacklistType ingredientBlacklistType : IngredientBlacklistType.VALUES) {
         if (isIngredientOnConfigBlacklist(ingredient, ingredientBlacklistType, ingredientHelper)) {
            return true;
         }
      }

      return false;
   }

   public static <V> boolean isIngredientOnConfigBlacklist(V ingredient, IngredientBlacklistType blacklistType, IIngredientHelper<V> ingredientHelper) {
      String uid = getIngredientUid(ingredient, blacklistType, ingredientHelper);
      return itemBlacklist.contains(uid);
   }

   private static <V> String getIngredientUid(@Nullable IIngredientListElement<V> element, IngredientBlacklistType blacklistType) {
      if (element == null) {
         return "";
      } else {
         V ingredient = element.getIngredient();
         IIngredientHelper<V> ingredientHelper = element.getIngredientHelper();
         return getIngredientUid(ingredient, blacklistType, ingredientHelper);
      }
   }

   private static <V> String getIngredientUid(V ingredient, IngredientBlacklistType blacklistType, IIngredientHelper<V> ingredientHelper) {
      switch(blacklistType) {
      case ITEM:
         return ingredientHelper.getUniqueId(ingredient);
      case WILDCARD:
         return ingredientHelper.getWildcardId(ingredient);
      case MOD_ID:
         return ingredientHelper.getModId(ingredient);
      default:
         throw new IllegalStateException("Unknown blacklist type: " + blacklistType);
      }
   }

   private static int versionCompare(String str1, String str2) {
      String[] vals1 = str1.split("\\.");
      String[] vals2 = str2.split("\\.");

      int i;
      for(i = 0; i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i]); ++i) {
         ;
      }

      if (i < vals1.length && i < vals2.length) {
         int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
         return Integer.signum(diff);
      } else {
         return Integer.signum(vals1.length - vals2.length);
      }
   }

   public static enum SearchMode {
      ENABLED,
      REQUIRE_PREFIX,
      DISABLED;

      private SearchMode() {
      }
   }
}
