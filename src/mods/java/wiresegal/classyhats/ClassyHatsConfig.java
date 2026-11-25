package wiresegal.classyhats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.teamwizardry.librarianlib.features.kotlin.JsonDsl;
import com.teamwizardry.librarianlib.features.kotlin.JsonMaker;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.util.HatData;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001d\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R \u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0013X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R$\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0018\u001a\u00020\u0019@BX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\u001f\u001a\u00020 X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u001a\u0010%\u001a\u00020 X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b&\u0010\"\"\u0004\b'\u0010$\u00a8\u0006,"},
   d2 = {"Lwiresegal/classyhats/ClassyHatsConfig;", "", "()V", "hatSpawnPercentage", "", "getHatSpawnPercentage", "()F", "setHatSpawnPercentage", "(F)V", "hats", "", "", "Lwiresegal/classyhats/util/HatData;", "getHats", "()Ljava/util/Map;", "missingno", "getMissingno", "()Lwiresegal/classyhats/util/HatData;", "names", "", "getNames", "()Ljava/util/List;", "setNames", "(Ljava/util/List;)V", "<set-?>", "Ljava/io/File;", "rpl", "getRpl", "()Ljava/io/File;", "setRpl", "(Ljava/io/File;)V", "shouldInjectLootBoss", "", "getShouldInjectLootBoss", "()Z", "setShouldInjectLootBoss", "(Z)V", "shouldInjectLootChests", "getShouldInjectLootChests", "setShouldInjectLootChests", "loadPreInit", "", "e", "Lnet/minecraftforge/fml/common/event/FMLPreInitializationEvent;", "classyhats"}
)
public final class ClassyHatsConfig {
   @NotNull
   private static final HatData missingno = new HatData("missingno", 0, true);
   @NotNull
   private static final Map<String, HatData> hats;
   @NotNull
   private static File rpl;
   private static boolean shouldInjectLootChests;
   private static boolean shouldInjectLootBoss;
   private static float hatSpawnPercentage;
   @NotNull
   private static List<String> names;
   public static final ClassyHatsConfig INSTANCE;

   @NotNull
   public final HatData getMissingno() {
      return missingno;
   }

   @NotNull
   public final Map<String, HatData> getHats() {
      return hats;
   }

   @NotNull
   public final File getRpl() {
      return rpl;
   }

   private final void setRpl(File var1) {
      rpl = <set-?>;
   }

   public final boolean getShouldInjectLootChests() {
      return shouldInjectLootChests;
   }

   public final void setShouldInjectLootChests(boolean var1) {
      shouldInjectLootChests = <set-?>;
   }

   public final boolean getShouldInjectLootBoss() {
      return shouldInjectLootBoss;
   }

   public final void setShouldInjectLootBoss(boolean var1) {
      shouldInjectLootBoss = <set-?>;
   }

   public final float getHatSpawnPercentage() {
      return hatSpawnPercentage;
   }

   public final void setHatSpawnPercentage(float var1) {
      hatSpawnPercentage = <set-?>;
   }

   @NotNull
   public final List<String> getNames() {
      return names;
   }

   public final void setNames(@NotNull List<String> var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      names = <set-?>;
   }

   public final void loadPreInit(@NotNull FMLPreInitializationEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      File path = new File(e.getModConfigurationDirectory(), "classyhats.json");
      boolean succeeded = false;
      JsonObject json = (JsonObject)null;
      if (path.exists()) {
         try {
            Map newHats = (Map)(new LinkedHashMap());
            JsonParser var19 = new JsonParser();
            Charset $receiver$iv = Charsets.UTF_8;
            InputStream v = (InputStream)(new FileInputStream(path));
            InputStreamReader var20 = new InputStreamReader(v, $receiver$iv);
            JsonElement var10000 = var19.parse((Reader)var20);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "JsonParser().parse(path.reader())");
            JsonObject load = var10000.getAsJsonObject();
            json = load;
            JsonArray blacklist = load.has("blacklist_hats") ? load.getAsJsonArray("blacklist_hats") : new JsonArray();
            Intrinsics.checkExpressionValueIsNotNull(blacklist, "blacklist");
            Iterable $receiver$iv = (Iterable)blacklist;
            Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

            for(Object item$iv$iv : $receiver$iv) {
               JsonElement $i$f$toTypedArray = (JsonElement)item$iv$iv;
               Intrinsics.checkExpressionValueIsNotNull($i$f$toTypedArray, "it");
               String var70 = $i$f$toTypedArray.getAsString();
               Intrinsics.checkExpressionValueIsNotNull(var70, "it.asString");
               String $i$a$1$jsonObject = var70;
               Locale var71 = Locale.ROOT;
               Intrinsics.checkExpressionValueIsNotNull(Locale.ROOT, "Locale.ROOT");
               Locale $i$f$create = var71;
               if ($i$a$1$jsonObject == null) {
                  throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
               }

               String var72 = $i$a$1$jsonObject.toLowerCase($i$f$create);
               Intrinsics.checkExpressionValueIsNotNull(var72, "(this as java.lang.String).toLowerCase(locale)");
               String var67 = var72;
               destination$iv$iv.add(var67);
            }

            $receiver$iv = (Iterable)((List)destination$iv$iv);
            destination$iv$iv = (Collection)(new ArrayList());

            for(Object element$iv$iv : $receiver$iv) {
               String it = (String)element$iv$iv;
               if (Intrinsics.areEqual(it, "missingno") ^ true) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            for(destination$iv$iv : (Iterable)((List)destination$iv$iv)) {
               String it = (String)destination$iv$iv;
               hats.remove(it);
            }

            boolean var74;
            if (load.has("loot_hat_from_chests")) {
               JsonPrimitive var73 = load.getAsJsonPrimitive("loot_hat_from_chests");
               Intrinsics.checkExpressionValueIsNotNull(var73, "load.getAsJsonPrimitive(\"loot_hat_from_chests\")");
               var74 = var73.getAsBoolean();
            } else {
               var74 = shouldInjectLootChests;
            }

            shouldInjectLootChests = var74;
            if (load.has("loot_hat_from_boss")) {
               JsonPrimitive var75 = load.getAsJsonPrimitive("loot_hat_from_boss");
               Intrinsics.checkExpressionValueIsNotNull(var75, "load.getAsJsonPrimitive(\"loot_hat_from_boss\")");
               var74 = var75.getAsBoolean();
            } else {
               var74 = shouldInjectLootBoss;
            }

            shouldInjectLootBoss = var74;
            List var78;
            if (!load.has("hat_wearing_entities")) {
               var78 = names;
            } else {
               JsonArray var77 = load.getAsJsonArray("hat_wearing_entities");
               Intrinsics.checkExpressionValueIsNotNull(var77, "load.getAsJsonArray(\"hat_wearing_entities\")");
               $receiver$iv = (Iterable)var77;
               destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

               for(Object item$iv$iv : $receiver$iv) {
                  JsonElement var61 = (JsonElement)item$iv$iv;
                  String var68 = var61.getAsString();
                  destination$iv$iv.add(var68);
               }

               $receiver$iv = (Iterable)((List)destination$iv$iv);
               destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

               for(Object item$iv$iv : $receiver$iv) {
                  String var62 = (String)item$iv$iv;
                  String var69 = (new ResourceLocation(var62)).toString();
                  destination$iv$iv.add(var69);
               }

               var78 = (List)destination$iv$iv;
            }

            names = var78;
            float var80;
            if (load.has("chance_of_entities_wearing_hats")) {
               JsonPrimitive var79 = load.getAsJsonPrimitive("chance_of_entities_wearing_hats");
               Intrinsics.checkExpressionValueIsNotNull(var79, "load.getAsJsonPrimitive(\u2026f_entities_wearing_hats\")");
               var80 = var79.getAsFloat();
            } else {
               var80 = hatSpawnPercentage;
            }

            hatSpawnPercentage = var80;

            for(JsonElement hat : load.has("custom_hats") ? load.getAsJsonArray("custom_hats") : new JsonArray()) {
               Intrinsics.checkExpressionValueIsNotNull(hat, "hat");
               JsonObject hatObj = hat.getAsJsonObject();
               JsonPrimitive var81 = hatObj.getAsJsonPrimitive("name");
               Intrinsics.checkExpressionValueIsNotNull(var81, "hatObj.getAsJsonPrimitive(\"name\")");
               String var82 = var81.getAsString();
               Intrinsics.checkExpressionValueIsNotNull(var82, "hatObj.getAsJsonPrimitive(\"name\").asString");
               String var63 = var82;
               Locale var83 = Locale.ROOT;
               Intrinsics.checkExpressionValueIsNotNull(Locale.ROOT, "Locale.ROOT");
               Locale var65 = var83;
               if (var63 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
               }

               String var84 = var63.toLowerCase(var65);
               Intrinsics.checkExpressionValueIsNotNull(var84, "(this as java.lang.String).toLowerCase(locale)");
               String name = var84;
               int var86;
               if (hatObj.has("weight")) {
                  JsonPrimitive var85 = hatObj.getAsJsonPrimitive("weight");
                  Intrinsics.checkExpressionValueIsNotNull(var85, "hatObj.getAsJsonPrimitive(\"weight\")");
                  var86 = var85.getAsInt();
               } else {
                  var86 = -1;
               }

               int weight = var86;
               if (hatObj.has("elusive")) {
                  JsonPrimitive var87 = hatObj.getAsJsonPrimitive("elusive");
                  Intrinsics.checkExpressionValueIsNotNull(var87, "hatObj.getAsJsonPrimitive(\"elusive\")");
                  var86 = var87.getAsBoolean();
               } else {
                  var86 = 0;
               }

               boolean elusive = (boolean)var86;
               newHats.put(name, new HatData(name, weight, elusive));
            }

            hats.putAll(newHats);
            succeeded = true;
         } catch (Exception var21) {
            ClassyHats.Companion.getLOGGER().error("Failed to parse hat config " + path);
         }
      }

      JsonDsl var23 = new JsonDsl((JsonObject)null, 1, (DefaultConstructorMarker)null);
      var23.to("__comment0", "The folder classyhats_resources will act as a resource pack for any custom hats you want to add.");
      var23.to("__comment1", "Put any hat names from the default HATS you don't want in the blacklist.");
      var23.to("__default_weight", Integer.valueOf(40));
      var23.to("__example_custom_hat", JsonMaker.array(new Object[]{(new HatData("example", 2, true)).toJson()}));
      var23.to("blacklist_hats", JsonMaker.array(new Object[0]));
      var23.to("loot_hat_from_chests", Boolean.valueOf(true));
      var23.to("loot_hat_from_boss", Boolean.valueOf(true));
      var23.to("custom_hats", JsonMaker.array(new Object[0]));
      Collection var35 = (Collection)names;
      String var40 = "hat_wearing_entities";
      if (var35 == null) {
         throw new TypeCastException("null cannot be cast to non-null type java.util.Collection<T>");
      } else {
         Object[] var89 = var35.toArray(new String[0]);
         if (var89 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
         } else {
            Object[] var59 = var89;
            var23.to(var40, JsonMaker.array(Arrays.copyOf(var59, var59.length)));
            var23.to("chance_of_entities_wearing_hats", Float.valueOf(hatSpawnPercentage));
            JsonObject baseJson = var23.getRoot();
            if (succeeded) {
               List toRemove = (List)(new ArrayList());

               for(Entry var26 : baseJson.entrySet()) {
                  var40 = (String)var26.getKey();
                  Intrinsics.checkExpressionValueIsNotNull(var40, "k");
                  if (StringsKt.startsWith$default(var40, "__", false, 2, (Object)null)) {
                     toRemove.add(var40);
                  }
               }

               for(String k : toRemove) {
                  baseJson.remove(k);
               }
            }

            JsonObject var90 = json;
            if (json == null) {
               var90 = new JsonObject();
            }

            JsonObject newJson = var90;

            for(Entry var28 : newJson.entrySet()) {
               var40 = (String)var28.getKey();
               JsonElement var49 = (JsonElement)var28.getValue();
               baseJson.add(var40, var49);
            }

            FilesKt.writeText$default(path, JsonMaker.serialize((JsonElement)baseJson), (Charset)null, 2, (Object)null);
            hats.put("missingno", missingno);
            rpl = new File(e.getModConfigurationDirectory(), "classyhats_resources");
            rpl.mkdir();
         }
      }
   }

   private ClassyHatsConfig() {
   }

   static {
      ClassyHatsConfig var0 = new ClassyHatsConfig();
      INSTANCE = var0;
      Map var1 = MapsKt.mutableMapOf(new Pair[]{TuplesKt.to("missingno", missingno)});
      Map $receiver = var1;

      for(Object element$iv : (Iterable)ClassyHatsRegistry.Companion.getHATS()) {
         HatData it = (HatData)element$iv;
         $receiver.put(it.getName(), it);
      }

      hats = var1;
      rpl = new File("");
      shouldInjectLootChests = true;
      shouldInjectLootBoss = true;
      hatSpawnPercentage = 0.05F;
      names = CollectionsKt.listOf(new String[]{"minecraft:zombie", "minecraft:zombie_villager", "minecraft:zombie_pigman", "minecraft:wither_skeleton", "minecraft:skeleton", "minecraft:evocation_illager", "minecraft:vex", "minecraft:vindication_illager", "minecraft:vindication_illager", "minecraft:witch"});
   }
}
