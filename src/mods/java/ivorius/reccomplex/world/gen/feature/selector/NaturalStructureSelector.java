package ivorius.reccomplex.world.gen.feature.selector;

import com.google.gson.annotations.SerializedName;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.files.SimpleLeveledRegistry;
import ivorius.reccomplex.utils.expression.BiomeExpression;
import ivorius.reccomplex.utils.expression.DimensionExpression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.DoublePredicate;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;

public class NaturalStructureSelector {
   public static final double STRUCTURE_MIN_CAP_DEFAULT = 20.0D;
   public static SimpleLeveledRegistry<NaturalStructureSelector.Category> CATEGORY_REGISTRY = new SimpleLeveledRegistry<NaturalStructureSelector.Category>("natural generation category");

   public NaturalStructureSelector() {
   }

   public interface Category extends MixingStructureSelector.Category {
      boolean selectableInGUI();

      String title();

      List<String> tooltip();
   }

   public static class GenerationInfo {
      @SerializedName("spawnChance")
      public double spawnChance;
      public double[] spawnChances;
      @SerializedName("biomeMatcher")
      public BiomeExpression biomeExpression;
      @SerializedName("dimensionMatcher")
      public DimensionExpression dimensionExpression;

      public GenerationInfo(double[] spawnChance, BiomeExpression biomeExpression, DimensionExpression dimensionExpression) {
         this.spawnChances = spawnChance;
         this.biomeExpression = biomeExpression;
         this.dimensionExpression = dimensionExpression;
      }
   }

   public static class SimpleCategory implements NaturalStructureSelector.Category {
      @SerializedName("generationInfos")
      public final List<NaturalStructureSelector.GenerationInfo> generationInfos = new ArrayList();
      @SerializedName("defaultSpawnChance")
      public double defaultSpawnChance;
      @SerializedName("defaultSpawnChances")
      public double[] defaultSpawnChances;
      @SerializedName("structureMinCap")
      public Double structureMinCap = null;
      @SerializedName("spawnDistanceMultiplier")
      public float spawnDistanceMultiplier = 0.0F;
      @SerializedName("spawnDistanceMultiplierCap")
      public float spawnDistanceMultiplierCap = 1.0F;
      @SerializedName("selectableInGUI")
      public boolean selectableInGUI = true;
      @SerializedName("title")
      public String title = "";
      @SerializedName("tooltip")
      public final List<String> tooltip = new ArrayList();

      public SimpleCategory() {
      }

      public int structuresInBiome(Biome biome, WorldProvider worldProvider, double totalWeight, Float distanceToSpawn, EaglercraftRandom random) {
         return (int)Arrays.stream(this.spawnChance(biome, worldProvider)).filter((chance) -> {
            return random.nextDouble() < chance * this.amountMultiplier(totalWeight) * this.distanceMultiplier(distanceToSpawn) * (double)RCConfig.structureSpawnChanceModifier;
         }).count();
      }

      public double amountMultiplier(double totalWeight) {
         return Math.min(totalWeight / this.getActiveStructureMinCap().doubleValue(), 1.0D);
      }

      public double[] spawnChance(Biome biome, WorldProvider worldProvider) {
         double[] am = this.defaultSpawnChances != null ? this.defaultSpawnChances : new double[]{this.defaultSpawnChance};

         for(NaturalStructureSelector.GenerationInfo info : this.generationInfos) {
            if (info.biomeExpression.test(biome) && info.dimensionExpression.test(worldProvider)) {
               am = info.spawnChances != null ? info.spawnChances : new double[]{info.spawnChance};
            }
         }

         return am;
      }

      public double distanceMultiplier(Float distance) {
         return distance == null ? 1.0D : (this.spawnDistanceMultiplier > 1.0F ? (double)Math.min(1.0F + distance.floatValue() * this.spawnDistanceMultiplier, Math.max(this.spawnDistanceMultiplierCap, 1.0F)) : (double)Math.max(1.0F + distance.floatValue() * this.spawnDistanceMultiplier, Math.min(this.spawnDistanceMultiplierCap, 1.0F)));
      }

      public Double getActiveStructureMinCap() {
         return this.structureMinCap != null ? this.structureMinCap.doubleValue() : 20.0D;
      }

      public boolean selectableInGUI() {
         return this.selectableInGUI;
      }

      public String title() {
         return this.title;
      }

      public List<String> tooltip() {
         return this.tooltip;
      }
   }
}
