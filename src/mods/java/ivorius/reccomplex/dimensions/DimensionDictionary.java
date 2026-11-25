package ivorius.reccomplex.dimensions;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.world.WorldProvider;

public class DimensionDictionary {
   public static final String UNCATEGORIZED = "UNCATEGORIZED";
   public static final String MC_DEFAULT = "MC_DEFAULT";
   public static final String REAL = "REAL";
   public static final String UNREAL = "UNREAL";
   public static final String IMAGINARY = "IMAGINARY";
   public static final String SIMULATED = "SIMULATED";
   public static final String ABSTRACT = "ABSTRACT";
   public static final String TOP_LIMIT = "TOP_LIMIT";
   public static final String NO_TOP_LIMIT = "NO_TOP_LIMIT";
   public static final String BOTTOM_LIMIT = "BOTTOM_LIMIT";
   public static final String NO_BOTTOM_LIMIT = "NO_BOTTOM_LIMIT";
   public static final String FINITE = "FINITE";
   public static final String INFINITE = "INFINITE";
   public static final String PLANET_SURFACE = "PLANET_SURFACE";
   public static final String EARTH = "EARTH";
   public static final String HELL = "HELL";
   public static final String ENDER = "ENDER";
   public static final String BOSS_ARENA = "BOSS_ARENA";
   private static final TIntObjectMap<Set<String>> dimensionTypes = new TIntObjectHashMap();
   private static final Map<String, DimensionDictionary.Type> types = new HashMap();
   private static final Set<String> SET_UNCATEGORIZED = Collections.singleton("UNCATEGORIZED");

   public DimensionDictionary() {
   }

   public static void registerDimensionTypes(int dimensionID, Collection<String> types) {
      Set<String> dTypes = (Set)dimensionTypes.get(dimensionID);
      if (dTypes == null) {
         dTypes = new HashSet();
         dimensionTypes.put(dimensionID, dTypes);
      }

      dTypes.addAll(types);
      types.forEach(DimensionDictionary::registerType);
   }

   public static void unregisterDimensionTypes(int dimensionID, Collection<String> types) {
      Set<String> dTypes = (Set)dimensionTypes.get(dimensionID);
      if (dTypes != null) {
         if (types == null) {
            dTypes.clear();
         } else {
            dTypes.removeAll(types);
         }
      }

   }

   public static void registerType(String type) {
      if (!types.containsKey(type)) {
         types.put(type, new DimensionDictionary.Type());
      }

   }

   public static void registerTypes(Collection<String> types) {
      types.forEach(DimensionDictionary::registerType);
   }

   public static void registerSubtypes(String type, Collection<String> subtypes) {
      registerGetType(type).subtypes.addAll(subtypes);

      for(String sub : subtypes) {
         registerGetType(sub).supertypes.add(type);
      }

   }

   public static void registerSupertypes(String type, Collection<String> supertypes) {
      registerGetType(type).supertypes.addAll(supertypes);

      for(String supertype : supertypes) {
         registerGetType(supertype).subtypes.add(type);
      }

   }

   public static Set<String> getDimensionTypes(WorldProvider provider) {
      if (provider instanceof DimensionDictionary.Handler) {
         return ((DimensionDictionary.Handler)provider).getDimensionTypes();
      } else {
         Set<String> types = (Set)dimensionTypes.get(provider.getDimension());
         return types != null ? Collections.unmodifiableSet(types) : SET_UNCATEGORIZED;
      }
   }

   public static boolean dimensionMatchesAllTypes(WorldProvider provider, Collection<String> types) {
      for(String type : types) {
         if (!dimensionMatchesType(provider, type)) {
            return false;
         }
      }

      return true;
   }

   public static boolean dimensionMatchesType(WorldProvider provider, String type) {
      Set<String> dimTypes = getDimensionTypes(provider);
      Queue<String> curTypes = new ArrayDeque();

      while(!dimTypes.contains(type)) {
         DimensionDictionary.Type curT = (DimensionDictionary.Type)types.get(type);
         if (curT != null) {
            curTypes.addAll(curT.subtypes);
         }

         if ((type = (String)curTypes.poll()) == null) {
            return false;
         }
      }

      return true;
   }

   public static Set<String> getSupertypes(String type) {
      DimensionDictionary.Type t = (DimensionDictionary.Type)types.get(type);
      return t == null ? Collections.emptySet() : Collections.unmodifiableSet(t.supertypes);
   }

   public static Set<String> getSubtypes(String type) {
      DimensionDictionary.Type t = (DimensionDictionary.Type)types.get(type);
      return t == null ? Collections.emptySet() : Collections.unmodifiableSet(t.subtypes);
   }

   public static Set<String> allRegisteredTypes() {
      return Collections.unmodifiableSet(types.keySet());
   }

   private static DimensionDictionary.Type registerGetType(String type) {
      registerType(type);
      return (DimensionDictionary.Type)types.get(type);
   }

   static {
      registerType("UNCATEGORIZED");
      registerSubtypes("UNREAL", Arrays.asList("IMAGINARY", "SIMULATED", "ABSTRACT"));
      registerDimensionTypes(0, Arrays.asList("MC_DEFAULT", "REAL", "INFINITE", "NO_TOP_LIMIT", "BOTTOM_LIMIT", "PLANET_SURFACE", "EARTH"));
      registerDimensionTypes(-1, Arrays.asList("MC_DEFAULT", "REAL", "INFINITE", "TOP_LIMIT", "BOTTOM_LIMIT", "HELL"));
      registerDimensionTypes(1, Arrays.asList("MC_DEFAULT", "REAL", "FINITE", "NO_TOP_LIMIT", "NO_BOTTOM_LIMIT", "BOSS_ARENA", "ENDER"));
   }

   public interface Handler {
      Set<String> getDimensionTypes();
   }

   private static class Type {
      public final Set<String> supertypes;
      public final Set<String> subtypes;

      private Type() {
         this.supertypes = new HashSet();
         this.subtypes = new HashSet();
      }
   }
}
