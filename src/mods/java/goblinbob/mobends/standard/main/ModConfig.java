package goblinbob.mobends.standard.main;

import goblinbob.mobends.core.util.ErrorReporter;
import goblinbob.mobends.core.util.WildcardPattern;
import goblinbob.mobends.standard.AttackActionType;
import goblinbob.mobends.standard.UseActionType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "mobends"
)
public class ModConfig {
   @LangKey("mobends.config.show_arrow_trails")
   public static boolean showArrowTrails = true;
   @LangKey("mobends.config.show_sword_trails")
   public static boolean showSwordTrail = true;
   @LangKey("mobends.config.perform_spin_attack")
   public static boolean performSpinAttack = true;
   @LangKey("mobends.config.item_use_classifications")
   public static String[] itemUseClassifications = new String[0];
   @LangKey("mobends.config.item_attack_classifications")
   public static String[] itemAttackClassifications = new String[0];
   @LangKey("mobends.config.keep_armor_as_vanilla")
   public static String[] keepArmorAsVanilla = new String[0];
   @LangKey("mobends.config.keep_entity_as_vanilla")
   public static String[] keepEntityAsVanilla = new String[0];
   @Ignore
   private static Map<Item, Boolean> keepArmorAsVanillaCache;
   @Ignore
   private static Map<Entity, Boolean> keepEntityAsVanillaCache;
   @Ignore
   private static Map<Item, UseActionType> itemUseClassificationCache;
   @Ignore
   private static Map<Item, AttackActionType> itemAttackClassificationCache;
   @Ignore
   private static LinkedList<ModConfig.ItemClassificationEntry<UseActionType>> itemUseClassificationEntries = new LinkedList();
   @Ignore
   private static LinkedList<ModConfig.ItemClassificationEntry<AttackActionType>> itemAttackClassificationEntries = new LinkedList();
   @Ignore
   private static List<Map<?, ?>> caches;

   public ModConfig() {
   }

   private static <T> LinkedList<ModConfig.ItemClassificationEntry<T>> getOrMakeEntries(LinkedList<ModConfig.ItemClassificationEntry<T>> entries, String[] rawEntries, Function<String, T> parseFunction) {
      if (rawEntries.length == 0) {
         return entries;
      } else {
         if (entries.size() == 0) {
            for(String rawEntry : rawEntries) {
               try {
                  entries.addFirst(ModConfig.ItemClassificationEntry.parse(rawEntry, parseFunction));
               } catch (MalformedConfigException var8) {
                  ErrorReporter.showErrorToPlayer(String.format("Invalid configuration! %s", var8.getMessage()));
               }
            }
         }

         return entries;
      }
   }

   private static boolean doesLocationMatchPattern(ResourceLocation resourceLocation, String pattern) {
      ResourceLocation patternLocation = new ResourceLocation(pattern);
      if (resourceLocation.equals(patternLocation)) {
         return true;
      } else {
         WildcardPattern domainPattern = new WildcardPattern(patternLocation.getResourceDomain());
         WildcardPattern pathPattern = new WildcardPattern(patternLocation.getResourcePath());
         return domainPattern.matches(resourceLocation.getResourceDomain()) && pathPattern.matches(resourceLocation.getResourcePath());
      }
   }

   private static boolean checkForPatterns(ResourceLocation resourceLocation, String[] patterns) {
      String resourceDomain = resourceLocation.getResourceDomain();
      String resourcePath = resourceLocation.getResourcePath();

      for(String pattern : patterns) {
         ResourceLocation patternLocation = new ResourceLocation(pattern);
         if (resourceLocation.equals(patternLocation)) {
            return true;
         }

         WildcardPattern domainPattern = new WildcardPattern(patternLocation.getResourceDomain());
         WildcardPattern pathPattern = new WildcardPattern(patternLocation.getResourcePath());
         if (domainPattern.matches(resourceDomain) && pathPattern.matches(resourcePath)) {
            return true;
         }
      }

      return false;
   }

   public static UseActionType getItemUseAction(Item item) {
      return (UseActionType)itemUseClassificationCache.computeIfAbsent(item, (i) -> {
         ResourceLocation location = item.getRegistryName();
         if (location != null) {
            for(ModConfig.ItemClassificationEntry<UseActionType> e : getOrMakeEntries(itemUseClassificationEntries, itemUseClassifications, UseActionType::valueOf)) {
               if (doesLocationMatchPattern(location, e.pattern)) {
                  return (UseActionType)e.classification;
               }
            }
         }

         return null;
      });
   }

   public static AttackActionType getItemAttackAction(Item item) {
      return (AttackActionType)itemAttackClassificationCache.computeIfAbsent(item, (i) -> {
         ResourceLocation location = item.getRegistryName();
         if (location != null) {
            for(ModConfig.ItemClassificationEntry<AttackActionType> e : getOrMakeEntries(itemAttackClassificationEntries, itemAttackClassifications, AttackActionType::valueOf)) {
               if (doesLocationMatchPattern(location, e.pattern)) {
                  return (AttackActionType)e.classification;
               }
            }
         }

         return null;
      });
   }

   public static boolean shouldKeepArmorAsVanilla(Item item) {
      return ((Boolean)keepArmorAsVanillaCache.computeIfAbsent(item, (i) -> {
         return checkForPatterns(i.getRegistryName(), keepArmorAsVanilla);
      })).booleanValue();
   }

   public static boolean shouldKeepEntityAsVanilla(Entity entity) {
      return ((Boolean)keepEntityAsVanillaCache.computeIfAbsent(entity, (e) -> {
         ResourceLocation location = EntityList.getKey(entity);
         return location != null && checkForPatterns(location, keepEntityAsVanilla);
      })).booleanValue();
   }

   static {
      caches = Arrays.asList(keepArmorAsVanillaCache = new HashMap(), keepEntityAsVanillaCache = new HashMap(), itemUseClassificationCache = new HashMap(), itemAttackClassificationCache = new HashMap());
   }

   @EventBusSubscriber(
      modid = "mobends"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("mobends")) {
            ConfigManager.sync("mobends", Type.INSTANCE);

            for(Map<?, ?> cache : ModConfig.caches) {
               cache.clear();
            }

            ModConfig.itemUseClassificationEntries.clear();
            ModConfig.getOrMakeEntries(ModConfig.itemUseClassificationEntries, ModConfig.itemUseClassifications, UseActionType::valueOf);
            ModConfig.itemAttackClassificationEntries.clear();
            ModConfig.getOrMakeEntries(ModConfig.itemAttackClassificationEntries, ModConfig.itemAttackClassifications, AttackActionType::valueOf);
            MoBends.refreshSystems();
         }

      }
   }

   private static class ItemClassificationEntry<T> {
      public final String pattern;
      public final T classification;

      public ItemClassificationEntry(String pattern, T classification) {
         this.pattern = pattern;
         this.classification = classification;
      }

      public static <T> ModConfig.ItemClassificationEntry<T> parse(String encoded, Function<String, T> parsingFunction) {
         int indexOfEquals = encoded.indexOf("=");
         if (indexOfEquals == -1) {
            throw new IllegalArgumentException(String.format("No equals sign found in the item classification entry: %s", encoded));
         } else {
            String pattern = encoded.substring(0, indexOfEquals);
            String encodedActionType = encoded.substring(indexOfEquals + 1).toUpperCase();

            try {
               T actionType = (T)parsingFunction.apply(encodedActionType);
               return new ModConfig.ItemClassificationEntry<T>(pattern, actionType);
            } catch (IllegalArgumentException var6) {
               throw new MalformedConfigException(String.format("Unknown action type: %s", encodedActionType), var6);
            }
         }
      }
   }
}
