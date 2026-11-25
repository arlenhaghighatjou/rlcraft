package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.api.cells.ICellKit;
import com.ferreusveritas.dynamictrees.api.treedata.IDropCreator;
import com.ferreusveritas.dynamictrees.api.treedata.IDropCreatorStorage;
import com.ferreusveritas.dynamictrees.growthlogic.IGrowthLogicKit;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorStorage;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class TreeRegistry {
   public static final IDropCreatorStorage globalDropCreatorStorage = new DropCreatorStorage();
   private static HashMap<ResourceLocation, ICellKit> cellKitRegistry = new HashMap();
   private static HashMap<ResourceLocation, IGrowthLogicKit> growthLogicKitRegistry = new HashMap();
   public static Map<IBlockState, Species> saplingReplacers = new HashMap();
   public static final ResourceLocation globalName = new ResourceLocation("dynamictrees", "global");

   public TreeRegistry() {
   }

   public static Species findSpecies(String name) {
      return findSpecies(new ResourceLocation(name));
   }

   public static Species findSpecies(ResourceLocation name) {
      return (Species)Species.REGISTRY.getValue(name);
   }

   public static Species findSpeciesSloppy(String name) {
      ResourceLocation resloc = new ResourceLocation(name);
      if ("minecraft".equals(resloc.getResourceDomain())) {
         resloc = new ResourceLocation("dynamictrees", resloc.getResourcePath());
      }

      if (Species.REGISTRY.containsKey(resloc)) {
         return (Species)Species.REGISTRY.getValue(resloc);
      } else {
         for(Species species : Species.REGISTRY) {
            if (species.getRegistryName().getResourcePath().equals(resloc.getResourcePath())) {
               return species;
            }
         }

         return Species.NULLSPECIES;
      }
   }

   public static List<ResourceLocation> getSpeciesDirectory() {
      return new ArrayList(Species.REGISTRY.getKeys());
   }

   public static List<ResourceLocation> getTransformableSpeciesLocs() {
      List<ResourceLocation> species = getSpeciesDirectory();
      species.removeIf((resLoc) -> {
         return !findSpecies(resLoc).isTransformable();
      });
      return species;
   }

   public static List<Species> getTransformableSpecies() {
      List<Species> species = new ArrayList();
      getTransformableSpeciesLocs().forEach((speciesLoc) -> {
         species.add(findSpecies(speciesLoc));
      });
      return species;
   }

   public static List<Species> getPotionTransformableSpecies() {
      List<Species> speciesList = getTransformableSpecies();
      speciesList.removeIf((species) -> {
         TreeFamily family = species.getFamily();
         return species.getSeedStack(1) == null || species.getSeed() == Seed.NULLSEED || species != family.getCommonSpecies() && species.getSeed() == family.getCommonSpecies().getSeed();
      });
      return speciesList;
   }

   public static void registerSaplingReplacer(IBlockState state, Species species) {
      saplingReplacers.put(state, species);
   }

   public static boolean registerDropCreator(ResourceLocation speciesName, IDropCreator dropCreator) {
      return speciesName != null && !speciesName.equals(globalName) ? findSpecies(speciesName).addDropCreator(dropCreator) : globalDropCreatorStorage.addDropCreator(dropCreator);
   }

   public static boolean removeDropCreator(ResourceLocation speciesName, ResourceLocation dropCreatorName) {
      return speciesName != null && !speciesName.equals(globalName) ? findSpecies(speciesName).remDropCreator(dropCreatorName) : globalDropCreatorStorage.remDropCreator(dropCreatorName);
   }

   public static Map<ResourceLocation, Map<ResourceLocation, IDropCreator>> getDropCreatorsMap() {
      Map<ResourceLocation, Map<ResourceLocation, IDropCreator>> dir = new HashMap();
      dir.put(globalName, globalDropCreatorStorage.getDropCreators());
      Species.REGISTRY.forEach((species) -> {
         Map var10000 = (Map)dir.put(species.getRegistryName(), species.getDropCreators());
      });
      return dir;
   }

   public static ICellKit registerCellKit(ResourceLocation name, ICellKit kit) {
      return (ICellKit)cellKitRegistry.computeIfAbsent(name, (k) -> {
         return kit;
      });
   }

   public static ICellKit findCellKit(ResourceLocation name) {
      return (ICellKit)cellKitRegistry.get(name);
   }

   public static ICellKit findCellKit(String name) {
      ResourceLocation kitLocation = new ResourceLocation(name);
      if ("minecraft".equals(kitLocation.getResourceDomain())) {
         kitLocation = new ResourceLocation("dynamictrees", kitLocation.getResourcePath());
      }

      return findCellKit(kitLocation);
   }

   public static void cleanupCellKit() {
      cellKitRegistry = new HashMap();
   }

   public static IGrowthLogicKit registerGrowthLogicKit(ResourceLocation name, IGrowthLogicKit kit) {
      return (IGrowthLogicKit)growthLogicKitRegistry.computeIfAbsent(name, (k) -> {
         return kit;
      });
   }

   public static IGrowthLogicKit findGrowthLogicKit(ResourceLocation name) {
      return (IGrowthLogicKit)growthLogicKitRegistry.get(name);
   }

   public static IGrowthLogicKit findGrowthLogicKit(String name) {
      ResourceLocation kitLocation = new ResourceLocation(name);
      if ("minecraft".equals(kitLocation.getResourceDomain())) {
         kitLocation = new ResourceLocation("dynamictrees", kitLocation.getResourcePath());
      }

      return findGrowthLogicKit(kitLocation);
   }

   public static void cleanupGrowthLogicKit() {
      growthLogicKitRegistry = new HashMap();
   }
}
