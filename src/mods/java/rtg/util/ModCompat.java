package rtg.util;

import com.google.common.collect.Lists;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rtg.RTGConfig;
import rtg.api.RTGAPI;
import rtg.api.util.Logger;

public final class ModCompat {
   private static final int ID_LENGTH = 3;
   private static final String ID_FORMAT = "%3s";
   private static final int NAME_LENGTH = 32;
   private static final String NAME_FORMAT = "%-32s";
   private static final int RESLOC_LENGTH = 50;
   private static final String RESLOC_FORMAT = "%-50s";
   private static final int BIOME_NAME_LENGTH = 24;
   private static final String BIOME_NAME_FORMAT = "%-24s";
   private static final int BIOME_CLASS_LENGTH = 32;
   private static final String BIOME_CLASS_FORMAT = "%-32s";
   private static final int BIOME_RESLOC_LENGTH = 44;
   private static final String BIOME_RESLOC_FORMAT = "%-44s";
   private static final int BEACH_NAME_LENGTH = 24;
   private static final String BEACH_NAME_FORMAT = "%-24s";

   private ModCompat() {
   }

   public static void doBiomeCheck() {
      List<Biome> invalidBiomes = Lists.newArrayList(new Biome[]{Biomes.HELL, Biomes.SKY, Biomes.VOID});
      if (ModCompat.Mods.abyssalcraft.isLoaded()) {
         invalidBiomes.addAll(Arrays.asList(ACBiomes.abyssal_wastelands, ACBiomes.dark_realm, ACBiomes.dreadlands, ACBiomes.dreadlands_forest, ACBiomes.dreadlands_mountains, ACBiomes.omothol, ACBiomes.purified_dreadlands));
      }

      if (ModCompat.Mods.biomesoplenty.isLoaded()) {
         String modid = ModCompat.Mods.biomesoplenty.name();
         Stream var10000 = Stream.of(new ResourceLocation(modid, "corrupted_sands"), new ResourceLocation(modid, "fungi_forest"), new ResourceLocation(modid, "phantasmagoric_inferno"), new ResourceLocation(modid, "undergarden"), new ResourceLocation(modid, "visceral_heap"));
         RegistryNamespaced var10001 = Biome.REGISTRY;
         Biome.REGISTRY.getClass();
         var10000.map(var10001::getObject).filter(Objects::nonNull).forEach(invalidBiomes::add);
      }

      if (ModCompat.Mods.byg.isLoaded()) {
         String modid = ModCompat.Mods.byg.name();
         Stream var9 = Stream.of(new ResourceLocation(modid, "babyssalbog"), new ResourceLocation(modid, "bastralisle"), new ResourceLocation(modid, "bcosmicocean"), new ResourceLocation(modid, "bshattereddesert"));
         RegistryNamespaced var12 = Biome.REGISTRY;
         Biome.REGISTRY.getClass();
         var9.map(var12::getObject).filter(Objects::nonNull).forEach(invalidBiomes::add);
      }

      if (ModCompat.Mods.fyrecraft.isLoaded()) {
         String modid = ModCompat.Mods.fyrecraft.name();
         Stream var10 = Stream.of(new ResourceLocation(modid, "miner's caves"), new ResourceLocation(modid, "waterfalls"));
         RegistryNamespaced var13 = Biome.REGISTRY;
         Biome.REGISTRY.getClass();
         var10.map(var13::getObject).filter(Objects::nonNull).forEach(invalidBiomes::add);
      }

      if (ModCompat.Mods.thaumcraft.isLoaded()) {
         String modid = ModCompat.Mods.thaumcraft.name();
         Stream var11 = Stream.of(new ResourceLocation(modid, "eldritch"));
         RegistryNamespaced var14 = Biome.REGISTRY;
         Biome.REGISTRY.getClass();
         var11.map(var14::getObject).filter(Objects::nonNull).forEach(invalidBiomes::add);
      }

      Collection<Biome> unsupportedBiomes = ForgeRegistries.BIOMES.getValuesCollection();
      List<Biome> supportedBiomes = Lists.newArrayList(ForgeRegistries.BIOMES.getValuesCollection());
      String[] supported = new String[]{""};
      unsupportedBiomes = (Collection)unsupportedBiomes.stream().filter((b) -> {
         return !invalidBiomes.contains(b) && !RTGAPI.RTG_BIOMES.containsKey(b);
      }).sorted(Comparator.comparingInt(Biome::getIdForBiome)).collect(Collectors.toList());
      supportedBiomes.removeAll(invalidBiomes);
      supportedBiomes.removeAll(unsupportedBiomes);
      if (!unsupportedBiomes.isEmpty()) {
         Logger.warn(".= " + String.format("%3s", (new String(new char[3])).replace('\u0000', '=')) + " = " + String.format("%-32s", (new String(new char[32])).replace('\u0000', '=')) + " = " + String.format("%-50s", (new String(new char[50])).replace('\u0000', '=')) + " =.");
         Logger.warn("|| " + String.format("%-91s", "                RTG could not find realistic versions of the following biomes") + " ||");
         Logger.warn("|| " + String.format("%3s", (new String(new char[3])).replace('\u0000', '=')) + " = " + String.format("%-32s", (new String(new char[32])).replace('\u0000', '=')) + " = " + String.format("%-50s", (new String(new char[50])).replace('\u0000', '=')) + " ||");
         Logger.warn("|| " + String.format("%3s", "ID") + " | " + String.format("%-32s", "Biome Class") + " | " + String.format("%-50s", "Registry Name") + " ||");
         Logger.warn("|| " + String.format("%3s", (new String(new char[3])).replace('\u0000', '-')) + " - " + String.format("%-32s", (new String(new char[32])).replace('\u0000', '-')) + " - " + String.format("%-50s", (new String(new char[50])).replace('\u0000', '-')) + " ||");
         unsupportedBiomes.forEach((b) -> {
            Logger.warn("|| " + String.format("%3s", Biome.getIdForBiome(b)) + " | " + String.format("%-32s", b.getBiomeClass().getSimpleName()) + " | " + String.format("%-50s", b.getRegistryName()) + " ||");
         });
         Logger.warn("`= " + String.format("%3s", (new String(new char[3])).replace('\u0000', '=')) + " | " + String.format("%-32s", (new String(new char[32])).replace('\u0000', '=')) + " | " + String.format("%-50s", (new String(new char[50])).replace('\u0000', '=')) + " =`");
      }

      if (RTGConfig.additionalBiomeInfo() && !supportedBiomes.isEmpty()) {
         supported[0] = supported[0] + System.lineSeparator() + ".= " + String.format("%3s", (new String(new char[3])).replace('\u0000', '=')) + " = " + String.format("%-24s", (new String(new char[24])).replace('\u0000', '=')) + " = " + String.format("%-32s", (new String(new char[32])).replace('\u0000', '=')) + " = " + String.format("%-44s", (new String(new char[44])).replace('\u0000', '=')) + " = " + String.format("%-24s", (new String(new char[24])).replace('\u0000', '=')) + " =.";
         supported[0] = supported[0] + System.lineSeparator() + "|| " + String.format("%-139s", "                                           RTG found realistic versions of the following biomes") + " ||";
         supported[0] = supported[0] + System.lineSeparator() + "|| " + String.format("%3s", (new String(new char[3])).replace('\u0000', '=')) + " = " + String.format("%-24s", (new String(new char[24])).replace('\u0000', '=')) + " = " + String.format("%-32s", (new String(new char[32])).replace('\u0000', '=')) + " = " + String.format("%-44s", (new String(new char[44])).replace('\u0000', '=')) + " = " + String.format("%-24s", (new String(new char[24])).replace('\u0000', '=')) + " ||";
         supported[0] = supported[0] + System.lineSeparator() + "|| " + String.format("%3s", "ID") + " | " + String.format("%-24s", "Biome Name") + " | " + String.format("%-32s", "Class Name") + " | " + String.format("%-44s", "Registry Name") + " | " + String.format("%-24s", "Beach Name") + " ||";
         supported[0] = supported[0] + System.lineSeparator() + "|| " + String.format("%3s", (new String(new char[3])).replace('\u0000', '-')) + " - " + String.format("%-24s", (new String(new char[24])).replace('\u0000', '-')) + " - " + String.format("%-32s", (new String(new char[32])).replace('\u0000', '-')) + " - " + String.format("%-44s", (new String(new char[44])).replace('\u0000', '-')) + " - " + String.format("%-24s", (new String(new char[24])).replace('\u0000', '-')) + " ||";
         supportedBiomes.forEach((b) -> {
            supported[0] = supported[0] + System.lineSeparator() + "|| " + String.format("%3s", Biome.getIdForBiome(b)) + " | " + String.format("%-24s", b.biomeName) + " | " + String.format("%-32s", b.getBiomeClass().getSimpleName()) + " | " + String.format("%-44s", b.getRegistryName()) + " | " + String.format("%-24s", RTGAPI.getRTGBiome(b).getBeachBiome().baseBiome().biomeName) + " ||";
         });
         supported[0] = supported[0] + System.lineSeparator() + ".= " + String.format("%3s", (new String(new char[3])).replace('\u0000', '=')) + " = " + String.format("%-24s", (new String(new char[24])).replace('\u0000', '=')) + " = " + String.format("%-32s", (new String(new char[32])).replace('\u0000', '=')) + " = " + String.format("%-44s", (new String(new char[44])).replace('\u0000', '=')) + " = " + String.format("%-24s", (new String(new char[24])).replace('\u0000', '=')) + " =." + System.lineSeparator();
         Logger.info(supported[0]);
      }

   }

   public static void init() {
      ModCompat.Mods.init();
   }

   public static enum Mods {
      abyssalcraft,
      auxbiomes,
      betteragriculture,
      biomesoplenty,
      byg,
      bionisation3,
      buildcraftenergy,
      candymod,
      defiledlands,
      douglas_forest,
      environs,
      explorercraft,
      floricraft,
      fyrecraft,
      gravityfalls,
      minecraft("vanilla"),
      mistbiomes,
      nt("novamterram"),
      odioitamod,
      plants2,
      pvj,
      realworld,
      redwoods,
      rockhounding_surface,
      spookybiomes,
      sugiforest,
      terscraft,
      thaumcraft,
      traverse,
      vampirism,
      valoegheses_be("zoesteria");

      private final String prettyName;
      private boolean loaded;

      private Mods() {
         this("");
      }

      private Mods(String name) {
         this.prettyName = !name.isEmpty() ? name : this.name();
      }

      public static void init() {
         Arrays.stream(values()).forEach((mod) -> {
            mod.loaded = Loader.isModLoaded(mod.name());
         });
      }

      public static ModCompat.Mods get(String modId) {
         return (ModCompat.Mods)Arrays.stream(values()).filter((mod) -> {
            return mod.name().equals(modId);
         }).findFirst().orElseThrow(() -> {
            return new NullPointerException("Mod does not exist in Mods enum.");
         });
      }

      public boolean isLoaded() {
         return this.loaded;
      }

      public String getPrettyName() {
         return this.prettyName;
      }
   }
}
