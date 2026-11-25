package knightminer.inspirations.common;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.ItemMetaKey;
import knightminer.inspirations.library.util.RecipeUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.config.ForgeCFG;

public class Config {
   public static ForgeCFG pulseConfig = new ForgeCFG("inspirationsModules", "Modules");
   private static Configuration configFile;
   public static boolean showAllVariants = true;
   public static boolean witherBoneDrop = true;
   public static boolean enableRope = true;
   public static boolean enableRopeLadder = true;
   public static boolean enableGlassDoor = true;
   public static boolean enableMulch = true;
   public static boolean enablePath = true;
   public static boolean enableFlowers = true;
   public static boolean enableEnlightenedBush = true;
   public static boolean enableBookshelf = true;
   public static boolean enableColoredBooks = true;
   public static boolean bookshelvesBoostEnchanting = true;
   public static float defaultEnchantingPower = 1.5F;
   private static String[] bookKeywords = new String[]{"almanac", "atlas", "book", "catalogue", "concordance", "dictionary", "directory", "encyclopedia", "guide", "journal", "lexicon", "manual", "thesaurus", "tome"};
   private static String[] bookOverrides = new String[]{"defiledlands:book_wyrm_raw->false", "defiledlands:book_wyrm_cooked->false", "defiledlands:book_wyrm_scale->false", "defiledlands:book_wyrm_scale_golden->false", "defiledlands:book_wyrm_analyzer->false", "minecraft:enchanted_book->2.5", "quark:ancient_tome->3.0", "theoneprobe:probenote->1.0"};
   public static boolean enableTorchLever = true;
   public static boolean enableRedstoneTorchLever = true;
   public static boolean enableRedstoneBook = true;
   public static boolean enableBricksButton = true;
   public static boolean enableRedstoneBarrel = true;
   public static boolean enableCarpetedTrapdoor = true;
   public static boolean enableCarpetedPressurePlate = true;
   public static boolean enableCollector = true;
   public static boolean enablePipe = true;
   public static boolean pipeUpwards = true;
   public static boolean enableDispenserFluidTanks = true;
   public static boolean milkSquids = false;
   public static short milkSquidCooldown = 300;
   public static String[] fluidContainers = new String[]{"ceramics:clay_bucket", "forge:bucketfilled", "minecraft:bucket", "minecraft:water_bucket", "minecraft:milk_bucket", "minecraft:lava_bucket"};
   public static boolean enableCauldronRecipes = true;
   public static boolean enableExtendedCauldron = true;
   public static boolean simpleCauldronRecipes = false;
   public static boolean enableBiggerCauldron = false;
   public static boolean fasterCauldronRain = true;
   public static boolean spongeEmptyCauldron = true;
   public static boolean cauldronObsidian = false;
   public static boolean spongeCauldronFull = false;
   public static boolean dropCauldronContents = false;
   public static boolean enableCauldronFluids = true;
   public static boolean enableMilk = true;
   public static boolean cauldronStew = true;
   public static boolean enableCauldronDyeing = true;
   public static boolean patchVanillaDyeRecipes = true;
   public static boolean extraBottleRecipes = true;
   public static boolean enableCauldronPotions = true;
   public static boolean enableCauldronBrewing = true;
   private static boolean expensiveCauldronBrewing = true;
   public static boolean cauldronTipArrows = true;
   private static String[] cauldronRecipes = new String[]{"minecraft:sticky_piston->minecraft:piston"};
   private static String[] cauldronFire = new String[]{"minecraft:fire"};
   public static boolean enableCauldronDispenser = true;
   public static String[] cauldronDispenserRecipes = new String[]{"inspirations:dyed_bottle", "inspirations:materials:2", "inspirations:materials:3", "minecraft:beetroot_soup", "minecraft:bowl", "minecraft:glass_bottle", "minecraft:lingering_potion", "minecraft:mushroom_stew", "minecraft:potion", "minecraft:rabbit_stew", "minecraft:splash_potion", "toughasnails:fruit_juice", "toughasnails:purified_water_bottle"};
   public static boolean enableAnvilSmashing = true;
   private static String[] anvilSmashing = new String[]{"# Stone", "minecraft:stone:0->minecraft:cobblestone", "minecraft:stonebrick->minecraft:cobblestone", "minecraft:stonebrick:1->minecraft:mossy_cobblestone", "minecraft:cobblestone->minecraft:gravel", "minecraft:stone:2->minecraft:stone:1", "minecraft:stone:4->minecraft:stone:3", "minecraft:stone:6->minecraft:stone:5", "# Sandstone", "minecraft:sandstone->minecraft:sand:0", "minecraft:red_sandstone->minecraft:sand:1", "# Ice", "minecraft:packed_ice->minecraft:ice", "minecraft:ice", "minecraft:frosted_ice", "# Plants", "minecraft:brown_mushroom_block", "minecraft:red_mushroom_block", "minecraft:leaves", "minecraft:leaves2", "minecraft:melon_block", "minecraft:pumpkin", "minecraft:lit_pumpkin", "# Concrete", "minecraft:concrete:0->minecraft:concrete_powder:0", "minecraft:concrete:1->minecraft:concrete_powder:1", "minecraft:concrete:2->minecraft:concrete_powder:2", "minecraft:concrete:3->minecraft:concrete_powder:3", "minecraft:concrete:4->minecraft:concrete_powder:4", "minecraft:concrete:5->minecraft:concrete_powder:5", "minecraft:concrete:6->minecraft:concrete_powder:6", "minecraft:concrete:7->minecraft:concrete_powder:7", "minecraft:concrete:8->minecraft:concrete_powder:8", "minecraft:concrete:9->minecraft:concrete_powder:9", "minecraft:concrete:10->minecraft:concrete_powder:10", "minecraft:concrete:11->minecraft:concrete_powder:11", "minecraft:concrete:12->minecraft:concrete_powder:12", "minecraft:concrete:13->minecraft:concrete_powder:13", "minecraft:concrete:14->minecraft:concrete_powder:14", "minecraft:concrete:15->minecraft:concrete_powder:15", "# Misc", "minecraft:planks->inspirations:mulch:0", "minecraft:prismarine:1->minecraft:prismarine:0", "minecraft:end_bricks->minecraft:end_stone", "minecraft:monster_egg"};
   public static boolean enableLock = true;
   public static boolean enableRedstoneCharge = true;
   public static boolean enableChargedArrow = true;
   public static boolean harvestHangingVines = true;
   public static boolean shearsReclaimMelons = true;
   public static boolean enableNorthCompass = true;
   public static boolean renameVanillaCompass = true;
   public static boolean enableBarometer = true;
   public static boolean enablePhotometer = true;
   public static boolean enableCrook = true;
   public static boolean separateCrook = true;
   public static boolean hoeCrook = false;
   public static int crookChance = 10;
   public static boolean netherCrooks = true;
   public static boolean enableWaypointCompass = true;
   public static boolean dyeWaypointCompass = true;
   public static boolean craftWaypointCompass = true;
   public static boolean copyWaypointCompass = true;
   public static boolean waypointCompassAdvTooltip = true;
   public static boolean waypointCompassCrossDimension = true;
   public static boolean moreShieldEnchantments = true;
   public static boolean shieldEnchantmentTable = true;
   public static boolean fixShieldTooltip = true;
   public static boolean axeWeaponEnchants = true;
   public static boolean axeEnchantmentTable = true;
   public static boolean enablePigDesaddle = true;
   public static boolean enableFittedCarpets = true;
   public static boolean betterFlowerPot = true;
   public static boolean flowerPotComparator = true;
   public static boolean coloredEnchantedRibbons = true;
   public static boolean brewMissingPotions = true;
   public static boolean coloredFireworkItems = true;
   public static boolean lilypadBreakFall = true;
   public static boolean betterCauldronItem = true;
   public static boolean unstackableRecipeAlts = true;
   public static boolean dispensersPlaceAnvils = true;
   public static boolean milkCooldown = false;
   public static short milkCooldownTime = 600;
   public static boolean caveSpiderDrops = true;
   public static boolean skeletonSkull = true;
   public static boolean customPortalColor = true;
   public static boolean enableHeartbeet = true;
   public static boolean brewHeartbeet = true;
   public static int heartbeetChance = 75;
   public static boolean enableMoreSeeds = true;
   public static boolean addGrassDrops = true;
   public static boolean nerfCarrotPotatoDrops = true;
   public static boolean bonemealMushrooms = true;
   public static boolean bonemealDeadBush = true;
   public static boolean bonemealGrassSpread = true;
   public static boolean bonemealMyceliumSpread = true;
   public static String[] flowerOverrides = new String[]{"biomesoplenty:flower_0->7", "biomesoplenty:flower_0->7", "biomesoplenty:mushroom->1", "biomesoplenty:sapling_0->12", "biomesoplenty:sapling_1->12", "biomesoplenty:sapling_2->12"};
   private static String[] milkContainersDefault = new String[]{"ceramics:clay_bucket", "minecraft:bowl", "minecraft:bucket", "simplytea:teapot"};
   public static Set<ItemMetaKey> milkContainers;
   public static boolean tanJuiceInCauldron = true;

   public Config() {
   }

   public static void preInit(FMLPreInitializationEvent event) {
      configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.4", false);
      showAllVariants = configFile.getBoolean("showAllVariants", "general", showAllVariants, "Shows all variants for dynamically textured blocks, like bookshelves. If false just the first will be shown");
      witherBoneDrop = configFile.getBoolean("witherBoneDrop", "general", witherBoneDrop, "Enables the wither bone drop. Option here in case another mod adds this. Requires either nether crooks or extra potions to be enabled");
      enableBookshelf = configFile.getBoolean("bookshelf", "building", enableBookshelf, "Enables the bookshelf: a decorative block to display books");
      enableColoredBooks = configFile.getBoolean("coloredBooks", "building.bookshelf", enableColoredBooks, "Enables colored books: basically colored versions of the vanilla book to decorate bookshelves") && enableBookshelf;
      bookshelvesBoostEnchanting = configFile.getBoolean("boostEnchanting", "building.bookshelf", bookshelvesBoostEnchanting, "If true, bookshelves will increase enchanting table power.");
      defaultEnchantingPower = configFile.getFloat("defaultEnchanting", "building.bookshelf", defaultEnchantingPower, 0.0F, 15.0F, "Default power for a book for enchanting, can be overridden in the book overrides.");
      bookKeywords = configFile.getStringList("bookKeywords", "building.bookshelf", bookKeywords, "List of keywords for valid books, used to determine valid books in the bookshelf");
      InspirationsRegistry.setBookKeywords(bookKeywords);
      enableRope = configFile.getBoolean("rope", "building", enableRope, "Enables rope: can be climbed like ladders and extended with additional rope");
      enableRopeLadder = configFile.getBoolean("ropeLadder", "building", enableRopeLadder, "Enables rope ladders: right click ropes with sticks to extend the hitbox") && enableRope;
      enableGlassDoor = configFile.getBoolean("glassDoor", "building", enableGlassDoor, "Enables glass doors and trapdoors: basically doors, but made of glass. Not sure what you would expect.");
      enableMulch = configFile.getBoolean("mulch", "building", enableMulch, "Enables mulch: a craftable falling block which supports plants such as flowers");
      enablePath = configFile.getBoolean("path", "building", enablePath, "Enables stone paths: a carpet like decorative block for making decorative paths");
      enableFlowers = configFile.getBoolean("flowers", "building", enableFlowers, "Enables additional flowers from breaking double flowers with shears.");
      enableEnlightenedBush = configFile.getBoolean("enlightenedBush", "building", enableEnlightenedBush, "Enables enlightened bushes: bushes with lights.");
      enableRedstoneBook = configFile.getBoolean("redstoneBook", "utility", enableRedstoneBook, "Enables the trapped book: will emit redstone power when placed in a bookshelf. Requires bookshelf.") && enableBookshelf;
      enableTorchLever = configFile.getBoolean("torchLever", "utility", enableTorchLever, "Enables the torch lever: basically a lever which looks like a torch");
      enableBricksButton = configFile.getBoolean("bricksButton", "utility", enableBricksButton, "Enables button blocks disguised as a full bricks or nether bricks block");
      enableRedstoneBarrel = configFile.getBoolean("redstoneBarrel", "utility", enableRedstoneBarrel, "Enables the redstone barrel: a block that gives a configurable comparator output and can be pushed by pistons");
      enableRedstoneTorchLever = configFile.getBoolean("redstoneTorchLever", "utility", enableRedstoneTorchLever, "Enables the redstone torch lever: a lever that toggles its state when the block it's on gets powered");
      enableCarpetedTrapdoor = configFile.getBoolean("carpetedTrapdoor", "utility", enableCarpetedTrapdoor, "Enables carpeted trapdoors: a trapdoor which appears to be a carpet when closed");
      enableCarpetedPressurePlate = configFile.getBoolean("carpetedPressurePlate", "utility", enableCarpetedPressurePlate, "Allows placing a carpet on a stone pressure plate to hide it");
      enableCollector = configFile.getBoolean("collector", "utility", enableCollector, "Enables the collector: extracts items from inventories or the world similar to a hopper, but can face in all 6 directions and cannot place items in inventories");
      enablePipe = configFile.getBoolean("pipe", "utility", enablePipe, "Enables pipes: a more economical hopper that only outputs items, does not pull from inventories. Both cheaper and better for performance.");
      pipeUpwards = configFile.getBoolean("upwards", "utility.pipe", pipeUpwards, "Allows pipes to output upwards. This removes a limitation on not being able to pipe items up without dropper elevators, but should be balanced alongside modded pipes.");
      enableDispenserFluidTanks = configFile.getBoolean("dispenserFluidTanks", "utility", enableDispenserFluidTanks, "Allows dispensers to fill and empty fluid tanks using fluid containers");
      fluidContainers = configFile.get("utility.dispenserFluidTanks", "containers", fluidContainers, "List of itemstacks that can be used as fluid containers to fill or empty fluid tanks").getStringList();
      configFile.moveProperty("tweaks", "anvilSmashing", "recipes");
      enableAnvilSmashing = configFile.getBoolean("anvilSmashing", "recipes", enableAnvilSmashing, "Anvils break glass blocks and transform blocks into other blocks on landing. Uses a block override, so disable if another mod replaces anvils");
      String spongeEmptyString = configFile.getString("spongeEmpty", "recipes.cauldron", "true", "Allows sponges to be used to empty the cauldron of dye, water, or potions. Can be 'true', 'false', or 'full'. If set to 'full', requires the cauldron to be full, prevents duplicating water but is less useful for removing unwanted fluids.", new String[]{"false", "full", "true"});
      spongeEmptyCauldron = !spongeEmptyString.equals("false");
      spongeCauldronFull = spongeEmptyString.equals("full");
      String extendCauldron = configFile.getString("extendCauldron", "recipes", "true", "Allows additional recipes to be performed in the cauldron. Can be 'true', 'false', or 'simple'. If true, requires a block substitution. If simple, functionality will be limited to water in cauldrons.", new String[]{"false", "simple", "true"});
      enableCauldronRecipes = !extendCauldron.equals("false");
      simpleCauldronRecipes = extendCauldron.equals("simple");
      enableExtendedCauldron = extendCauldron.equals("true");
      enableBiggerCauldron = configFile.getBoolean("bigger", "recipes.cauldron", enableBiggerCauldron, "Makes the cauldron hold 4 bottle per bucket instead of 3. Translates better to modded fluids.") && enableExtendedCauldron;
      InspirationsRegistry.setConfig("biggerCauldron", enableBiggerCauldron);
      fasterCauldronRain = configFile.getBoolean("fasterRain", "recipes.cauldron", fasterCauldronRain, "Cauldrons fill faster in the rain than vanilla painfully slow rate.") && enableExtendedCauldron;
      dropCauldronContents = configFile.getBoolean("dropContents", "recipes.cauldron", dropCauldronContents, "Cauldrons will drop their contents when broken.") && enableExtendedCauldron;
      cauldronObsidian = configFile.getBoolean("obsidian", "recipes.cauldron", cauldronObsidian, "Allows making obsidian in a cauldron by using a lava bucket on a water filled cauldron. Supports modded buckets. If cauldron fluids is enabled, you can also use a water bucket on a lava filled cauldron.");
      enableCauldronFluids = configFile.getBoolean("fluids", "recipes.cauldron", enableCauldronFluids, "Allows cauldrons to be filled with any fluid and use them in recipes") && enableExtendedCauldron;
      configFile.moveProperty("recipes.cauldron", "milk", "recipes.cauldron.fluids");
      enableMilk = configFile.getBoolean("milk", "recipes.cauldron.fluids", enableMilk, "Registers milk as a fluid so it can be used in cauldron recipes.") && enableCauldronFluids;
      cauldronStew = configFile.getBoolean("stew", "recipes.cauldron.fluids", cauldronStew, "Allows making stews and soups in cauldrons") && enableCauldronFluids;
      enableCauldronDyeing = configFile.getBoolean("dyeing", "recipes.cauldron", enableCauldronDyeing, "Allows cauldrons to be filled with dyes and dye items using cauldrons") && enableExtendedCauldron;
      patchVanillaDyeRecipes = configFile.getBoolean("patchVanillaRecipes", "recipes.cauldron.dyeing", patchVanillaDyeRecipes, "Makes crafting two dyed water bottles together produce a dyed water bottle. Requires modifying vanilla recipes to prevent a conflict") && enableCauldronDyeing;
      extraBottleRecipes = configFile.getBoolean("extraBottleRecipes", "recipes.cauldron.dyeing", extraBottleRecipes, "Adds extra dyed bottle recipes to craft green and brown") && enableCauldronDyeing;
      configFile.renameProperty("recipes.cauldron", "brewing", "potions");
      enableCauldronPotions = configFile.getBoolean("potions", "recipes.cauldron", enableCauldronPotions, "Allows cauldrons to be filled with potions and support brewing") && enableExtendedCauldron;
      enableCauldronBrewing = configFile.getBoolean("brewing", "recipes.cauldron.potions", enableCauldronBrewing, "Allows cauldrons to perform brewing recipes.") && enableCauldronPotions;
      expensiveCauldronBrewing = configFile.getBoolean("brewingExpensive", "recipes.cauldron.potions", expensiveCauldronBrewing, "Caps brewing at 2 potions per ingredient, requiring 2 ingredients for a full cauldron. Makes the brewing stand still useful and balances better against the bigger cauldron.") && enableCauldronBrewing;
      cauldronTipArrows = configFile.getBoolean("tippedArrow", "recipes.cauldron.potions", cauldronTipArrows, "Allows cauldrons to tip arrows with potions.") && enableCauldronPotions;
      InspirationsRegistry.setConfig("expensiveCauldronBrewing", expensiveCauldronBrewing);
      enableCauldronDispenser = configFile.getBoolean("dispenser", "recipes.cauldron", enableCauldronDispenser, "Allows dispensers to perform some recipes in the cauldron. Intended to be used for recipes to fill and empty fluid containers as droppers can already be used for recipes") && enableCauldronRecipes;
      cauldronDispenserRecipes = configFile.get("recipes.cauldron.dispenser", "items", cauldronDispenserRecipes, "List of itemstacks that can be used as to perform cauldron recipes in a dispenser").getStringList();
      milkSquids = configFile.getBoolean("milkSquids", "tweaks", milkSquids, "Allows milking squids with a glass bottle to get black dyed water.");
      milkSquidCooldown = (short)configFile.getInt("cooldown", "tweaks.milkSquids", milkSquidCooldown, 1, 32767, "Delay in seconds after milking a squid before it can be milked again.");
      caveSpiderDrops = configFile.getBoolean("caveSpiderWeb", "tweaks", caveSpiderDrops, "If true, cave spiders will rarely drop webs, giving them an advantage to farm over regular spiders");
      skeletonSkull = configFile.getBoolean("skeletonSkull", "tweaks", skeletonSkull, "If true, skeletons will rarely drop their skull for consistency with wither skeletons. Does not affect creeper or zombie heads.");
      configFile.moveProperty("utility", "redstoneCharge", "tools");
      enableRedstoneCharge = configFile.getBoolean("redstoneCharge", "tools", enableRedstoneCharge, "Enables the redstone charger: a quick pulse created with a flint and steel like item");
      enableChargedArrow = configFile.getBoolean("chargedArrow", "tools", enableChargedArrow, "Enables the charged arrow: places a redstone pulse where it lands");
      configFile.moveProperty("utility", "lock", "tools");
      enableLock = configFile.getBoolean("lock", "tools", enableLock, "Enables locks and keys: an item allowing you to lock a tile entity to only open for a special named item");
      spongeEmptyString = configFile.getString("crook", "tools", "true", "Enables the crook: a tool to break leaves faster and increase sapling chance. Can be 'true', 'false', or 'simple'. If true, adds a new tool. If simple, functionality will be added to hoes instead.", new String[]{"false", "simple", "true"});
      enableCrook = !spongeEmptyString.equals("false");
      separateCrook = spongeEmptyString.equals("true");
      hoeCrook = spongeEmptyString.equals("simple");
      crookChance = configFile.getInt("chance", "tools.crook", crookChance, 1, 100, "Chance of a sapling to drop when using the crook. Acts as 1 in [chance] if the initial sapling drop fails. Set to 1 to always drop saplings when using a crook.");
      netherCrooks = configFile.getBoolean("netherCrooks", "tools.crook", netherCrooks, "Enables crooks crafted from blaze rods and wither bones. They have higher stats than other crooks and inflict fire and wither on the target respectively.") && separateCrook;
      configFile.moveProperty("tweaks", "harvestHangingVines", "tools.shears");
      harvestHangingVines = configFile.getBoolean("harvestHangingVines", "tools.shears", harvestHangingVines, "When shearing vines, any supported vines will also be sheared instead of just broken");
      configFile.moveProperty("tweaks", "shearsReclaimMelons", "tools.shears");
      configFile.renameProperty("tools.shears", "shearsReclaimMelons", "reclaimMelons");
      shearsReclaimMelons = configFile.getBoolean("reclaimMelons", "tools.shears", shearsReclaimMelons, "Breaking a melon block with shears will always return 9 slices");
      enableNorthCompass = configFile.getBoolean("northCompass", "tools", enableNorthCompass, "Enables the north compass: a cheaper compass that always points north. Intended to either allow packs to replace the compass or as an alternative for F3 navigation");
      renameVanillaCompass = configFile.getBoolean("renameVanilla", "tools.northCompass", renameVanillaCompass, "Renames the vanilla compass to 'origin compass' to help clarify the difference between the two compasses.");
      enableBarometer = configFile.getBoolean("barometer", "tools", enableBarometer, "Enables the barometer: a tool to measure the player's height in world.");
      enablePhotometer = configFile.getBoolean("photometer", "tools", enablePhotometer, "Enables the photometer: a tool to measure light in world. Can be pointed at a block to measure the light level of that block.");
      enableWaypointCompass = configFile.getBoolean("waypointCompass", "tools", enableWaypointCompass, "Enables the waypoint compass: a compass which points towards a full beacon.");
      dyeWaypointCompass = configFile.getBoolean("dye", "tools.waypointCompass", dyeWaypointCompass, "If true, waypoint compasses can be dyed all vanilla colors") && enableWaypointCompass;
      craftWaypointCompass = configFile.getBoolean("craft", "tools.waypointCompass", craftWaypointCompass, "If true, waypoint compasses can be crafted using iron and a blaze rod. If false, they are obtained by using a vanilla compass on a beacon.") && enableWaypointCompass;
      waypointCompassAdvTooltip = configFile.getBoolean("advTooltip", "tools.waypointCompass", waypointCompassAdvTooltip, "If true, waypoint compasses show the position target in the advanced item tooltip. Disable for packs that disable coordinates.");
      waypointCompassCrossDimension = configFile.getBoolean("crossDimension", "tools.waypointCompass", waypointCompassCrossDimension, "If true, waypoint compasses work across dimensions. The coordinates between the overworld and nether will be adjusted, allowing for portal syncing.");
      copyWaypointCompass = configFile.getBoolean("copy", "tools.waypointCompass", copyWaypointCompass, "If true, you can copy the position of one waypoint compass to another in a crafting table, similarly to maps or compasses") && enableWaypointCompass;
      moreShieldEnchantments = configFile.getBoolean("moreShield", "tools.enchantments", moreShieldEnchantments, "If true, shields can now be enchanted with enchantments such as protection, fire aspect, knockback, and thorns");
      shieldEnchantmentTable = configFile.getBoolean("shieldTable", "tools.enchantments", shieldEnchantmentTable, "If true, shields can be enchanted in an enchantment table. Does not support modded shields as it requires a registry substitution") && moreShieldEnchantments;
      fixShieldTooltip = configFile.getBoolean("fixShieldTooltip", "tools.enchantments", fixShieldTooltip, "If true, fixes the tooltip on shield items so it looks better with both patterns and enchantments");
      axeWeaponEnchants = configFile.getBoolean("axeWeapon", "tools.enchantments", axeWeaponEnchants, "If true, axes will be able to be enchanted with weapon enchants such as looting, fire aspect, and knockback");
      axeEnchantmentTable = configFile.getBoolean("axeTable", "tools.enchantments", axeEnchantmentTable, "If true, axes can receive available weapon enchantments at the enchantment table");
      enablePigDesaddle = configFile.getBoolean("desaddlePig", "tweaks", enablePigDesaddle, "Allows pigs to be desaddled by shift-right click with an empty hand");
      enableFittedCarpets = configFile.getBoolean("fittedCarpets", "tweaks", enableFittedCarpets, "Carpets fit to stairs. Uses a block override, so disable if another mod replaces carpets");
      if (getConfigVersion() < 0.4D) {
         boolean oldValue = configFile.get("tweaks", "extraBonemeal", true).getBoolean();
         bonemealMushrooms = oldValue;
         bonemealDeadBush = oldValue;
      }

      bonemealMushrooms = configFile.getBoolean("mushrooms", "tweaks.bonemeal", bonemealMushrooms, "Bonemeal can be used on mycelium to produce mushrooms");
      bonemealDeadBush = configFile.getBoolean("deadBush", "tweaks.bonemeal", bonemealDeadBush, "Bonemeal can be used on sand to produce dead bushes");
      bonemealGrassSpread = configFile.getBoolean("grassSpread", "tweaks.bonemeal", bonemealGrassSpread, "Bonemeal can be used on dirt to produce grass if adjecent to grass");
      bonemealMyceliumSpread = configFile.getBoolean("myceliumSpread", "tweaks.bonemeal", bonemealMyceliumSpread, "Bonemeal can be used on dirt to produce mycelium if adjecent to mycelium");
      enableHeartbeet = configFile.getBoolean("heartbeet", "tweaks", enableHeartbeet, "Enables heartbeets: a rare drop from beetroots which can be eaten to restore a bit of health");
      brewHeartbeet = configFile.getBoolean("brewRegeneration", "tweaks.heartbeet", brewHeartbeet, "Allows heartbeets to be used as an alternative to ghast tears in making potions of regeneration") && enableHeartbeet;
      heartbeetChance = configFile.getInt("chance", "tweaks.heartbeet", heartbeetChance, 10, 1000, "Chance of a heartbeet to drop instead of a normal drop. Formula is two 1 in [chance] chances for it to drop each harvest");
      dispensersPlaceAnvils = configFile.getBoolean("dispensersPlaceAnvils", "tweaks", dispensersPlaceAnvils, "Dispensers will place anvils instead of dropping them. Plays well with anvil smashing.");
      betterCauldronItem = configFile.getBoolean("betterCauldronItemModel", "tweaks", betterCauldronItem, "Replaces the flat cauldron sprite with the 3D cauldron block model");
      betterFlowerPot = configFile.getBoolean("betterFlowerPot", "tweaks", betterFlowerPot, "Flower pots can hold modded flowers");
      flowerPotComparator = configFile.getBoolean("comparator", "tweaks.betterFlowerPot", flowerPotComparator, "Flower pots will emit a comparator signal if they have a flower");
      coloredEnchantedRibbons = configFile.getBoolean("coloredEnchantedRibbons", "tweaks", coloredEnchantedRibbons, "The ribbon on enchanted books colors based on the enchantment rarity");
      brewMissingPotions = configFile.getBoolean("brewMissingPotions", "tweaks", brewMissingPotions, "Adds brewing recipes for vanilla potions which are missing a recipe");
      coloredFireworkItems = configFile.getBoolean("coloredFireworkItems", "tweaks", coloredFireworkItems, "Colors the fireworks item based on the colors of the stars");
      lilypadBreakFall = configFile.getBoolean("lilypadBreakFall", "tweaks", lilypadBreakFall, "Lily pads prevent fall damage, but break in the process");
      unstackableRecipeAlts = configFile.getBoolean("unstackableRecipeAlts", "tweaks", unstackableRecipeAlts, "Adds stackable recipes to some vanilla or Inspriations items that require unstackable items to craft");
      enableMoreSeeds = configFile.getBoolean("moreSeeds", "tweaks", enableMoreSeeds, "Adds seeds for additional vanilla plants, including cactus, sugar cane, carrots, and potatoes.");
      addGrassDrops = configFile.getBoolean("grassDrops", "tweaks.moreSeeds", addGrassDrops, "Makes carrot and potato seeds drop from grass") && enableMoreSeeds;
      nerfCarrotPotatoDrops = configFile.getBoolean("nerfCarrotPotatoDrops", "tweaks.moreSeeds", nerfCarrotPotatoDrops, "Makes carrots and potatoes drop their respective seed if not fully grown") && enableMoreSeeds;
      milkCooldown = configFile.getBoolean("milkCooldown", "tweaks", milkCooldown, "Adds a cooldown to milking cows, prevents practically infinite milk in modded worlds where milk is more useful.");
      milkCooldownTime = (short)configFile.getInt("time", "tweaks.milkCooldown", milkCooldownTime, 1, 32767, "Delay in seconds after milking a cow before it can be milked again.");
      customPortalColor = configFile.getBoolean("customPortalColor", "tweaks", customPortalColor, "Allows the portal color to be changed by placing colored blocks under the portal. Any block that tints a beacon beam will work for the color.");
      tanJuiceInCauldron = configFile.getBoolean("tanJuiceInCauldron", "compatibility", tanJuiceInCauldron, "Enables making Tough as Nails juices in the cauldron. Requires enhanced cauldron") && enableCauldronFluids;
      if (configFile.hasChanged()) {
         configFile.save();
      }

   }

   public static void init(FMLInitializationEvent event) {
      double version = getConfigVersion();
      Property property = configFile.get("building.bookshelf", "bookOverrides", bookOverrides, "List of itemstacks to override book behavior. Format is modid:name[:meta][->enchantingPower].\nUnset meta will default wildcard.\n0 is a valid enchanting power, if unset uses default. Set to 'false' to mark something as not a book.");
      bookOverrides = property.getStringList();
      if (version < 0.3D) {
         bookOverrides = (String[])Stream.concat(Arrays.stream(bookOverrides).map((line) -> {
            return updateConfig(line, "1.5", "false");
         }), Stream.of("minecraft:enchanted_book->2.5", "quark:ancient_tome->3.0")).toArray((x$0) -> {
            return new String[x$0];
         });
         property.set(bookOverrides);
      }

      processBookOverrides(bookOverrides);
      configFile.moveProperty("tweaks.anvilSmashing", "recipes.anvilSmashing", "smashing");
      anvilSmashing = configFile.get("recipes.anvilSmashing", "smashing", anvilSmashing, "List of blocks to add to anvil smashing. Format is modid:input[:meta][->modid:output[:meta]]. If the output is excluded, it will default to air (breaking the block). If the meta is excluded, it will check all states for input and use the default for output").getStringList();
      processAnvilSmashing(anvilSmashing);
      configFile.moveProperty("recipes.cauldronRecipes", "recipes", "recipes.cauldron");
      cauldronRecipes = configFile.get("recipes.cauldron", "recipes", cauldronRecipes, "List of recipes to add to the cauldron on right click. Format is (modid:input:meta|oreString)->modid:output:meta[->isBoiling]. If isBoiling is excluded, it defaults to false.").getStringList();
      processCauldronRecipes(cauldronRecipes);
      property = configFile.get("tweaks.betterFlowerPot", "flowerOverrides", flowerOverrides, "List of itemstacks to override default flower behavior, default checks for BlockBush.\nFormat is 'modid:name[:meta]->power'. Unset meta will default wildcard. Power refers to comparator power, non-zero makes it valid for a flower pot. Specific values:\n* 0 - not flower, blacklists from placing in a flower pot\n* 1 - mushroom\n* 4 - fern\n* 7 - flower\n* 10 - dead bush\n* 12 - sapling\n* 15 - cactus");
      flowerOverrides = property.getStringList();
      if (version < 0.2D) {
         flowerOverrides = (String[])Arrays.stream(flowerOverrides).map((line) -> {
            return updateConfig(line, "7", "0");
         }).toArray((x$0) -> {
            return new String[x$0];
         });
         property.set(flowerOverrides);
      }

      processFlowerOverrides(flowerOverrides);
      cauldronFire = configFile.get("recipes.cauldron", "fires", cauldronFire, "List of blocks to act is fire below a cauldron. Format is modid:name[:meta]. If meta is excluded all states of the block will count as fire").getStringList();
      processCauldronFire(cauldronFire);
      milkContainersDefault = configFile.get("tweaks.milkCooldown", "containers", milkContainersDefault, "List of containers which will milk a cow when interacting. Used to prevent milking and to apply the milked tag").getStringList();
      processMilkContainers(milkContainersDefault);
      if (configFile.hasChanged()) {
         configFile.save();
      }

   }

   private static String updateConfig(String line, String t, String f) {
      String[] parts = line.split(":");
      switch(parts.length) {
      case 2:
         return line + "->" + t;
      case 3:
         if (parts[2].equals("-1")) {
            return String.format("%s:%s->%s", parts[0], parts[1], t);
         }

         return line + "->" + t;
      case 4:
         String power = "false".equals(parts[3]) ? f : t;
         if (parts[2].equals("-1")) {
            return String.format("%s:%s->%s", parts[0], parts[1], power);
         }

         return String.format("%s:%s:%s->%s", parts[0], parts[1], parts[2], power);
      default:
         return line;
      }
   }

   private static double getConfigVersion() {
      String version = configFile.getLoadedConfigVersion();
      if (version == null) {
         return Double.NaN;
      } else {
         try {
            return Double.parseDouble(version);
         } catch (NumberFormatException var2) {
            return Double.NaN;
         }
      }
   }

   private static void processBookOverrides(String[] overrides) {
      if (enableBookshelf) {
         for(String override : overrides) {
            if (!"".equals(override) && !override.startsWith("#")) {
               String[] parts = override.split("->");
               if (parts.length > 2) {
                  Inspirations.log.error("Invalid book override {}: must be in format modid:name[:meta][->power]. ", override);
               } else {
                  float power = defaultEnchantingPower;
                  if (parts.length > 1) {
                     try {
                        power = Float.parseFloat(parts[1]);
                     } catch (NumberFormatException var8) {
                        if (!parts[1].equals("false")) {
                           Inspirations.log.error("Invalid book override {}: power must be a number. ", override);
                           continue;
                        }

                        power = -1.0F;
                     }
                  }

                  if (power < 0.0F) {
                     power = -1.0F;
                  }

                  RecipeUtil.forStackInString(parts[0], (stack) -> {
                     InspirationsRegistry.registerBook(stack, enchPower);
                  });
               }
            }
         }

      }
   }

   private static void processFlowerOverrides(String[] overrides) {
      if (betterFlowerPot) {
         for(String line : overrides) {
            String[] split = line.split("->");
            if (split.length != 2) {
               Inspirations.log.error("Invalid flower pot override, expected format 'modid:name[:meta]->power'");
            } else {
               int power;
               try {
                  power = Integer.parseInt(split[1]);
               } catch (NumberFormatException var8) {
                  Inspirations.log.error("Invalid flower pot power, must be a valid number");
                  continue;
               }

               if (power >= 0 && power <= 15) {
                  RecipeUtil.forStackInString(split[0], (stack) -> {
                     InspirationsRegistry.registerFlower(stack, power);
                  });
               } else {
                  Inspirations.log.error("Invalid flower pot power, must between 0 to 15");
               }
            }
         }

      }
   }

   private static void processAnvilSmashing(String[] transformations) {
      if (enableAnvilSmashing) {
         for(String transformation : transformations) {
            if (!"".equals(transformation) && !transformation.startsWith("#")) {
               String[] transformParts = transformation.split("->");
               if (transformParts.length <= 2 && transformParts.length >= 1) {
                  IBlockState output;
                  if (transformParts.length == 1) {
                     output = Blocks.AIR.getDefaultState();
                  } else {
                     output = RecipeUtil.getBlockStateFromString(transformParts[1]);
                     if (output == null) {
                        Inspirations.log.info("Skipping anvil smashing {}: unable to find output {}", transformation, transformParts[1]);
                        continue;
                     }
                  }

                  RecipeUtil.forBlockInString(transformParts[0], (state) -> {
                     InspirationsRegistry.registerAnvilSmashing(state, output);
                  }, (block) -> {
                     InspirationsRegistry.registerAnvilSmashing(block, output);
                  });
               } else {
                  Inspirations.log.error("Invalid anvil smashing {}: must be in the format of modid:input[:meta][->modid:output[:meta]]", transformation);
               }
            }
         }

      }
   }

   private static void processCauldronRecipes(String[] cauldronRecipes) {
      if (enableCauldronRecipes) {
         for(String recipe : cauldronRecipes) {
            if (!"".equals(recipe) && !recipe.startsWith("#")) {
               String[] parts = recipe.split("->");
               if (parts.length >= 2 && parts.length <= 3) {
                  ItemStack input = null;
                  if (parts[0].contains(":")) {
                     input = RecipeUtil.getItemStackFromString(parts[0], true);
                     if (input.isEmpty()) {
                        continue;
                     }
                  }

                  ItemStack output = RecipeUtil.getItemStackFromString(parts[1], false);
                  if (!output.isEmpty()) {
                     Boolean boiling = parts.length > 2 ? parts[2].equals("true") : null;
                     if (input == null) {
                        InspirationsRegistry.addCauldronRecipe(parts[0], output, boiling);
                     } else {
                        InspirationsRegistry.addCauldronRecipe(input, output, boiling);
                     }
                  }
               } else {
                  Inspirations.log.error("Invalid cauldron recipe {}: must be in format input->output[->isBoiling]", recipe);
               }
            }
         }

      }
   }

   private static void processCauldronFire(String[] fires) {
      if (enableCauldronRecipes) {
         for(String fire : fires) {
            if (!"".equals(fire) && !fire.startsWith("#")) {
               RecipeUtil.forBlockInString(fire, InspirationsRegistry::registerCauldronFire, InspirationsRegistry::registerCauldronFire);
            }
         }

      }
   }

   private static void processMilkContainers(String[] containers) {
      if (milkCooldown) {
         Builder<ItemMetaKey> builder = ImmutableSet.builder();
         Consumer<ItemStack> callback = (stack) -> {
            builder.add(new ItemMetaKey(stack));
         };

         for(String container : containers) {
            if (!"".equals(container) && !container.startsWith("#")) {
               RecipeUtil.forStackInString(container, callback);
            }
         }

         milkContainers = builder.build();
      }
   }

   public static class ConfigProperty implements IConditionFactory {
      public ConfigProperty() {
      }

      public BooleanSupplier parse(JsonContext context, JsonObject json) {
         String prop = JsonUtils.getString(json, "prop");
         return () -> {
            return propertyEnabled(prop);
         };
      }

      private static boolean propertyEnabled(String property) {
         byte var2 = -1;
         switch(property.hashCode()) {
         case -2096372541:
            if (property.equals("dye_waypoint_compass")) {
               var2 = 20;
            }
            break;
         case -2060835014:
            if (property.equals("charged_arrow")) {
               var2 = 17;
            }
            break;
         case -2030864925:
            if (property.equals("cauldron_potions")) {
               var2 = 29;
            }
            break;
         case -1851649456:
            if (property.equals("carpeted_trapdoor")) {
               var2 = 9;
            }
            break;
         case -1560642707:
            if (property.equals("colored_books")) {
               var2 = 1;
            }
            break;
         case -1360929005:
            if (property.equals("craft_waypoint_compass")) {
               var2 = 18;
            }
            break;
         case -1351585454:
            if (property.equals("enlightened_bush")) {
               var2 = 2;
            }
            break;
         case -1144199801:
            if (property.equals("torch_lever")) {
               var2 = 15;
            }
            break;
         case -1076813167:
            if (property.equals("glass_door")) {
               var2 = 4;
            }
            break;
         case -954462609:
            if (property.equals("cauldron_dyeing")) {
               var2 = 28;
            }
            break;
         case -870880812:
            if (property.equals("redstone_book")) {
               var2 = 13;
            }
            break;
         case -865481472:
            if (property.equals("unstackable_alts")) {
               var2 = 27;
            }
            break;
         case -765800072:
            if (property.equals("flowers")) {
               var2 = 3;
            }
            break;
         case -763996200:
            if (property.equals("north_compass")) {
               var2 = 23;
            }
            break;
         case -678384485:
            if (property.equals("patch_vanilla_dye_recipes")) {
               var2 = 32;
            }
            break;
         case -495092585:
            if (property.equals("photometer")) {
               var2 = 24;
            }
            break;
         case 3327275:
            if (property.equals("lock")) {
               var2 = 21;
            }
            break;
         case 3433509:
            if (property.equals("path")) {
               var2 = 6;
            }
            break;
         case 3441070:
            if (property.equals("pipe")) {
               var2 = 11;
            }
            break;
         case 3506418:
            if (property.equals("rope")) {
               var2 = 7;
            }
            break;
         case 94934972:
            if (property.equals("crook")) {
               var2 = 19;
            }
            break;
         case 104256297:
            if (property.equals("mulch")) {
               var2 = 5;
            }
            break;
         case 380439224:
            if (property.equals("more_seeds")) {
               var2 = 26;
            }
            break;
         case 544699664:
            if (property.equals("cauldron_stew")) {
               var2 = 30;
            }
            break;
         case 589332433:
            if (property.equals("redstone_barrel")) {
               var2 = 12;
            }
            break;
         case 623919839:
            if (property.equals("redstone_charge")) {
               var2 = 25;
            }
            break;
         case 734396693:
            if (property.equals("nether_crook")) {
               var2 = 22;
            }
            break;
         case 931914687:
            if (property.equals("bricks_button")) {
               var2 = 8;
            }
            break;
         case 965224508:
            if (property.equals("redstone_torch_lever")) {
               var2 = 14;
            }
            break;
         case 1101508648:
            if (property.equals("extra_dyed_bottle_recipes")) {
               var2 = 31;
            }
            break;
         case 1883491469:
            if (property.equals("collector")) {
               var2 = 10;
            }
            break;
         case 1889827405:
            if (property.equals("barometer")) {
               var2 = 16;
            }
            break;
         case 2042924257:
            if (property.equals("bookshelf")) {
               var2 = 0;
            }
         }

         switch(var2) {
         case 0:
            return Config.enableBookshelf;
         case 1:
            return Config.enableColoredBooks;
         case 2:
            return Config.enableEnlightenedBush;
         case 3:
            return Config.enableFlowers;
         case 4:
            return Config.enableGlassDoor;
         case 5:
            return Config.enableMulch;
         case 6:
            return Config.enablePath;
         case 7:
            return Config.enableRope;
         case 8:
            return Config.enableBricksButton;
         case 9:
            return Config.enableCarpetedTrapdoor;
         case 10:
            return Config.enableCollector;
         case 11:
            return Config.enablePipe;
         case 12:
            return Config.enableRedstoneBarrel;
         case 13:
            return Config.enableRedstoneBook;
         case 14:
            return Config.enableRedstoneTorchLever;
         case 15:
            return Config.enableTorchLever;
         case 16:
            return Config.enableBarometer;
         case 17:
            return Config.enableChargedArrow;
         case 18:
            return Config.craftWaypointCompass;
         case 19:
            return Config.separateCrook;
         case 20:
            return Config.dyeWaypointCompass;
         case 21:
            return Config.enableLock;
         case 22:
            return Config.netherCrooks;
         case 23:
            return Config.enableNorthCompass;
         case 24:
            return Config.enablePhotometer;
         case 25:
            return Config.enableRedstoneCharge;
         case 26:
            return Config.enableMoreSeeds;
         case 27:
            return Config.unstackableRecipeAlts;
         case 28:
            return Config.enableCauldronDyeing;
         case 29:
            return Config.enableCauldronPotions;
         case 30:
            return Config.cauldronStew;
         case 31:
            return Config.extraBottleRecipes;
         case 32:
            return Config.patchVanillaDyeRecipes;
         default:
            throw new JsonSyntaxException("Invalid propertyname '" + property + "'");
         }
      }
   }

   public static class PulseLoaded implements IConditionFactory {
      public PulseLoaded() {
      }

      public BooleanSupplier parse(JsonContext context, JsonObject json) {
         String pulse = JsonUtils.getString(json, "pulse");
         return () -> {
            return Inspirations.pulseManager.isPulseLoaded(pulse);
         };
      }
   }
}
