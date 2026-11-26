package melonslise.locks.common.config;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.ConfigEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NavigableMap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import melonslise.locks.common.item.LockItem;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Config(
   modid = "locks"
)
public final class LocksConfig {
   public static final LocksConfig.Common COMMON = new LocksConfig.Common();
   public static final LocksConfig.Server SERVER = new LocksConfig.Server();
   protected static final LocksConfig.Server SERVER_CLIENT = new LocksConfig.Server();
   public static final LocksConfig.Client CLIENT = new LocksConfig.Client();
   @Ignore
   public static NavigableMap<Integer, Integer> weightedGeneratedPins;
   @Ignore
   public static NavigableMap<Integer, Item> weightedGeneratedLocks;
   @Ignore
   public static int weightTotal;
   @Ignore
   public static Set<String> alwaysGenerateItems;
   @Ignore
   public static Set<String> skipGeneratingItems;

   public static LocksConfig.Server getServer(World world) {
      return world.isRemote ? SERVER_CLIENT : SERVER;
   }

   public static LocksConfig.Server getServerClient() {
      return SERVER_CLIENT;
   }

   private LocksConfig() {
   }

   public static void init() {
      weightedGeneratedLocks = new TreeMap();
      weightedGeneratedPins = new TreeMap();
      alwaysGenerateItems = new HashSet();
      skipGeneratingItems = new HashSet();
      weightTotal = 0;
      String[] locks = COMMON.generatedLocks;
      int[] weights = COMMON.generatedLockWeights;
      int[] pins = COMMON.generatedLockPins;

      for(int a = 0; a < locks.length; ++a) {
         weightTotal += weights[a];
         weightedGeneratedLocks.put(Integer.valueOf(weightTotal), ForgeRegistries.ITEMS.getValue(new ResourceLocation(locks[a])));
         weightedGeneratedPins.put(Integer.valueOf(weightTotal), Integer.valueOf(pins[a]));
      }

      for(int a = 0; a < COMMON.chestItemListAlwaysGenerate.length; ++a) {
         alwaysGenerateItems.add(COMMON.chestItemListAlwaysGenerate[a].trim());
      }

      for(int a = 0; a < COMMON.chestItemListSkipGenerating.length; ++a) {
         skipGeneratingItems.add(COMMON.chestItemListSkipGenerating[a].trim());
      }

   }

   public static boolean isItemAlwaysLocked(ItemStack stack) {
      String regName = stack.getItem().getRegistryName().toString();
      String regNameMeta = regName + ":" + stack.getMetadata();
      return alwaysGenerateItems.contains(regName) || alwaysGenerateItems.contains(regNameMeta);
   }

   public static boolean isItemSkipped(ItemStack stack) {
      String regName = stack.getItem().getRegistryName().toString();
      String regNameMeta = regName + ":" + stack.getMetadata();
      return skipGeneratingItems.contains(regName) || skipGeneratingItems.contains(regNameMeta);
   }

   public static boolean canGen(EaglercraftRandom rng) {
      return LocksUtil.chance(rng, COMMON.generationChance);
   }

   public static boolean canEnchant(EaglercraftRandom rng) {
      return LocksUtil.chance(rng, COMMON.generationEnchantChance);
   }

   public static ItemStack getRandomLock(EaglercraftRandom rng) {
      int rngNumber = rng.nextInt(weightTotal) + 1;
      ItemStack stack = new ItemStack((Item)weightedGeneratedLocks.ceilingEntry(Integer.valueOf(rngNumber)).getValue());
      int pinout = MathHelper.clamp(((Integer)weightedGeneratedPins.ceilingEntry(Integer.valueOf(rngNumber)).getValue()).intValue(), 0, 30);
      if (pinout != 0) {
         boolean replacePins = true;
         if (stack.getItem() instanceof LockItem && ((LockItem)stack.getItem()).length == pinout) {
            replacePins = false;
         }

         if (replacePins) {
            NBTTagCompound nbt = LocksUtil.getTag(stack);
            nbt.setByte("Length", (byte)pinout);
         }
      }

      return canEnchant(rng) ? EnchantmentHelper.addRandomEnchantment(rng, stack, 5 + rng.nextInt(30), false) : stack;
   }

   public static class Client {
      @Name("Deaf Mode")
      @Comment({"Display visual feedback when trying to use a locked block for certain hearing impaired individuals"})
      public boolean deafMode = true;

      public Client() {
      }
   }

   public static class Common {
      @Name("Generation Chance")
      @Comment({"Chance to generate a random lock on every new chest during world generation. Set to 0 to disable"})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double generationChance = 0.85D;
      @Name("Generation Enchant Chance")
      @Comment({"Chance to randomly enchant a generated lock during world generation. Set to 0 to disable"})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double generationEnchantChance = 0.4D;
      @Name("Generated Locks")
      @Comment({"Items that can be generated as locks (must be instance of LockItem in code!)"})
      public String[] generatedLocks = new String[]{"locks:wood_lock", "locks:iron_lock", "locks:steel_lock", "locks:gold_lock", "locks:diamond_lock"};
      @Name("Generated Lock Chances")
      @Comment({"WARNING: THE AMOUNT OF NUMBERS SHOULD BE EQUAL TO THE AMOUNT OF GENERATED LOCK ITEMS!!! The relative probability that the corresponding lock item will be generated on a chest. Higher number = higher chance to generate"})
      public int[] generatedLockWeights = new int[]{3, 3, 2, 2, 1};
      @Name("Generated Lock Pins")
      @Comment({"WARNING: THE AMOUNT OF NUMBERS SHOULD BE EQUAL TO THE AMOUNT OF GENERATED LOCK ITEMS!!! The number of pins on the lock item. Overrides the defaults if not zero"})
      public int[] generatedLockPins = new int[]{0, 0, 0, 0, 0};
      @Name("Automatically Orient Placed Locks")
      @Comment({"Placed locks will try to orient themselves smartly to doors and chests regardless of how they were placed"})
      public boolean automaticallyOrientPlacedLocks = false;
      @Name("Skip Generation Empty Chests")
      @Comment({"Skip generating locks on empty chests"})
      public boolean skipGenerationEmptyChests = true;
      @Name("Chest Item List Always Generate")
      @Comment({"Always generate locks if the chests contain these items. Metadata can be specified (ex. minecraft:bed:0)"})
      public String[] chestItemListAlwaysGenerate = new String[]{"minecraft:nether_star"};
      @Name("Chest Item List Skip Generating")
      @Comment({"Worldgen assumes chests with only these items are empty. Metadata can be specified (ex. minecraft:bed:0)"})
      public String[] chestItemListSkipGenerating = new String[]{"minecraft:beetroot_seeds", "minecraft:bone", "minecraft:book", "minecraft:bowl", "minecraft:bread", "minecraft:brown_mushroom", "minecraft:clay_ball", "minecraft:cobblestone", "minecraft:dirt", "minecraft:dye", "minecraft:egg", "minecraft:gravel", "minecraft:hay_block", "minecraft:melon_seeds", "minecraft:painting", "minecraft:paper", "minecraft:pumpkin_seeds", "minecraft:red_mushroom", "minecraft:rotten_flesh", "minecraft:sapling", "minecraft:sign", "minecraft:spider_eye", "minecraft:stick", "minecraft:stone", "minecraft:stone_button", "minecraft:string", "minecraft:vine", "minecraft:waterlily", "minecraft:web", "minecraft:wheat", "minecraft:wheat_seeds", "minecraft:wooden_button"};

      public Common() {
      }
   }

   public static class Server {
      @Name("Max Lockable Volume")
      @Comment({"Maximum amount of blocks that can be locked at once"})
      public int maxLockableVolume = 6;
      @Name("Lockable Blocks")
      @Comment({"Blocks that can be locked. Each entry is the mod domain followed by the block's registry name. Can include regular expressions"})
      public String[] lockableBlocks = new String[]{"minecraft:.*chest", "minecraft:dispenser", "minecraft:dropper", "minecraft:hopper", "minecraft:.*door", "minecraft:.*trapdoor", "minecraft:.*fence_gate", "minecraft:.*shulker_box"};
      @Name("Allow Removing Locks")
      @Comment({"Open locks can be removed with an empty hand while sneaking"})
      public boolean allowRemovingLocks = true;
      @Name("Protect Lockables")
      @Comment({"Locked blocks cannot be destroyed in survival mode"})
      public boolean protectLockables = true;
      @Name("Easy Lock")
      @Comment({"Lock blocks with just one click! It's magic! (Will probably fail spectacularly with custom doors, custom double chests, etc)"})
      public boolean easyLock = true;
      @Ignore
      public Pattern[] lockableBlocksRegex;

      public Server() {
      }

      public void init() {
         this.lockableBlocksRegex = (Pattern[])Arrays.stream(this.lockableBlocks).map((regex) -> {
            return Pattern.compile(regex);
         }).toArray((x$0) -> {
            return new Pattern[x$0];
         });
      }

      public boolean canLock(World world, BlockPos pos) {
         if (this.lockableBlocksRegex == null) {
            this.init();
         }

         for(Pattern pat : this.lockableBlocksRegex) {
            if (pat.matcher(world.getBlockState(pos).getBlock().getRegistryName().toString()).matches()) {
               return true;
            }
         }

         return false;
      }
   }

   public static void register() {
      ConfigManager.register("locks", "", "general", null,
         ConfigEntry.category("COMMON",
            ConfigEntry.value("Generation Chance", double.class, () -> LocksConfig.COMMON.generationChance, v -> LocksConfig.COMMON.generationChance = (Double)v).comment("Chance to generate a random lock on every new chest during world generation. Set to 0 to disable").langKey("locks.general.common.generationchance").range(0.0, 1.0),
            ConfigEntry.value("Generation Enchant Chance", double.class, () -> LocksConfig.COMMON.generationEnchantChance, v -> LocksConfig.COMMON.generationEnchantChance = (Double)v).comment("Chance to randomly enchant a generated lock during world generation. Set to 0 to disable").langKey("locks.general.common.generationenchantchance").range(0.0, 1.0),
            ConfigEntry.value("Generated Locks", String[].class, () -> LocksConfig.COMMON.generatedLocks, v -> LocksConfig.COMMON.generatedLocks = (String[])v).comment("Items that can be generated as locks (must be instance of LockItem in code!)").langKey("locks.general.common.generatedlocks"),
            ConfigEntry.value("Generated Lock Chances", int[].class, () -> LocksConfig.COMMON.generatedLockWeights, v -> LocksConfig.COMMON.generatedLockWeights = (int[])v).comment("WARNING: THE AMOUNT OF NUMBERS SHOULD BE EQUAL TO THE AMOUNT OF GENERATED LOCK ITEMS!!! The relative probability that the corresponding lock item will be generated on a chest. Higher number = higher chance to generate").langKey("locks.general.common.generatedlockweights"),
            ConfigEntry.value("Generated Lock Pins", int[].class, () -> LocksConfig.COMMON.generatedLockPins, v -> LocksConfig.COMMON.generatedLockPins = (int[])v).comment("WARNING: THE AMOUNT OF NUMBERS SHOULD BE EQUAL TO THE AMOUNT OF GENERATED LOCK ITEMS!!! The number of pins on the lock item. Overrides the defaults if not zero").langKey("locks.general.common.generatedlockpins"),
            ConfigEntry.value("Automatically Orient Placed Locks", boolean.class, () -> LocksConfig.COMMON.automaticallyOrientPlacedLocks, v -> LocksConfig.COMMON.automaticallyOrientPlacedLocks = (Boolean)v).comment("Placed locks will try to orient themselves smartly to doors and chests regardless of how they were placed").langKey("locks.general.common.automaticallyorientplacedlocks"),
            ConfigEntry.value("Skip Generation Empty Chests", boolean.class, () -> LocksConfig.COMMON.skipGenerationEmptyChests, v -> LocksConfig.COMMON.skipGenerationEmptyChests = (Boolean)v).comment("Skip generating locks on empty chests").langKey("locks.general.common.skipgenerationemptychests"),
            ConfigEntry.value("Chest Item List Always Generate", String[].class, () -> LocksConfig.COMMON.chestItemListAlwaysGenerate, v -> LocksConfig.COMMON.chestItemListAlwaysGenerate = (String[])v).comment("Always generate locks if the chests contain these items. Metadata can be specified (ex. minecraft:bed:0)").langKey("locks.general.common.chestitemlistalwaysgenerate"),
            ConfigEntry.value("Chest Item List Skip Generating", String[].class, () -> LocksConfig.COMMON.chestItemListSkipGenerating, v -> LocksConfig.COMMON.chestItemListSkipGenerating = (String[])v).comment("Worldgen assumes chests with only these items are empty. Metadata can be specified (ex. minecraft:bed:0)").langKey("locks.general.common.chestitemlistskipgenerating")),
         ConfigEntry.category("SERVER",
            ConfigEntry.value("Max Lockable Volume", int.class, () -> LocksConfig.SERVER.maxLockableVolume, v -> LocksConfig.SERVER.maxLockableVolume = (Integer)v).comment("Maximum amount of blocks that can be locked at once").langKey("locks.general.server.maxlockablevolume"),
            ConfigEntry.value("Lockable Blocks", String[].class, () -> LocksConfig.SERVER.lockableBlocks, v -> LocksConfig.SERVER.lockableBlocks = (String[])v).comment("Blocks that can be locked. Each entry is the mod domain followed by the block's registry name. Can include regular expressions").langKey("locks.general.server.lockableblocks"),
            ConfigEntry.value("Allow Removing Locks", boolean.class, () -> LocksConfig.SERVER.allowRemovingLocks, v -> LocksConfig.SERVER.allowRemovingLocks = (Boolean)v).comment("Open locks can be removed with an empty hand while sneaking").langKey("locks.general.server.allowremovinglocks"),
            ConfigEntry.value("Protect Lockables", boolean.class, () -> LocksConfig.SERVER.protectLockables, v -> LocksConfig.SERVER.protectLockables = (Boolean)v).comment("Locked blocks cannot be destroyed in survival mode").langKey("locks.general.server.protectlockables"),
            ConfigEntry.value("Easy Lock", boolean.class, () -> LocksConfig.SERVER.easyLock, v -> LocksConfig.SERVER.easyLock = (Boolean)v).comment("Lock blocks with just one click! It's magic! (Will probably fail spectacularly with custom doors, custom double chests, etc)").langKey("locks.general.server.easylock")),
         ConfigEntry.category("CLIENT",
            ConfigEntry.value("Deaf Mode", boolean.class, () -> LocksConfig.CLIENT.deafMode, v -> LocksConfig.CLIENT.deafMode = (Boolean)v).comment("Display visual feedback when trying to use a locked block for certain hearing impaired individuals").langKey("locks.general.client.deafmode")));
   }
}
