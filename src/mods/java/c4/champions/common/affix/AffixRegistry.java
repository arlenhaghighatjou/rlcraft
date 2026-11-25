package c4.champions.common.affix;

import c4.champions.Champions;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class AffixRegistry {
   private static final Map<String, AffixBase> affixMap = Maps.newHashMap();
   private static final Map<AffixCategory, Set<String>> categoryMap = Maps.newEnumMap(AffixCategory.class);

   public AffixRegistry() {
   }

   public static void registerAffix(String identifier, AffixBase affix) {
      affixMap.put(identifier, affix);
      ((Set)categoryMap.computeIfAbsent(affix.getCategory(), (k) -> {
         return Sets.newHashSet();
      })).add(identifier);
   }

   @Nullable
   public static AffixBase getAffix(String identifier) {
      return (AffixBase)affixMap.get(identifier);
   }

   public static ImmutableList<AffixBase> getAllAffixes() {
      return ImmutableList.copyOf(affixMap.values());
   }

   public static ImmutableMap<AffixCategory, Set<String>> getCategoryMap() {
      return ImmutableMap.copyOf(categoryMap);
   }

   public static Set<String> getAffixesForCategory(AffixCategory category) {
      if (categoryMap.containsKey(category)) {
         return Sets.newHashSet((Iterable)categoryMap.get(category));
      } else {
         Champions.logger.log(Level.ERROR, "No affixes found for category " + category.toString());
         return Sets.newHashSet();
      }
   }
}
