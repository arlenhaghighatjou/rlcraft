package ejektaflex.bountiful.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.api.logic.pickable.PickableEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack;
import ejektaflex.bountiful.registry.BountyRegistry;
import ejektaflex.bountiful.registry.RewardRegistry;
import ejektaflex.bountiful.registry.ValueRegistry;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007J\u009c\u0001\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\n0\t\"\b\b\u0000\u0010\u000b*\u0002H\f\"\b\b\u0001\u0010\n*\u0002H\f\"\b\b\u0002\u0010\f*\u00020\u00012\u0006\u0010\r\u001a\u00020\u00072\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u0002H\n0\u000f2\u0012\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u000b0\u00120\u00112!\u0010\u0013\u001a\u001d\u0012\u0013\u0012\u0011H\u000b\u00a2\u0006\f\b\u0015\u0012\b\b\u0006\u0012\u0004\b\b(\u0016\u0012\u0004\u0012\u0002H\n0\u00142!\u0010\u0017\u001a\u001d\u0012\u0013\u0012\u0011H\n\u00a2\u0006\f\b\u0015\u0012\b\b\u0006\u0012\u0004\b\b(\u0016\u0012\u0004\u0012\u00020\u00180\u0014H\u0002J\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\tJ\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\tJ$\u0010\u001d\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u00042\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\u0006\u0010\r\u001a\u00020\u0007J\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a8\u0006!"},
   d2 = {"Lejektaflex/bountiful/config/BountifulIO;", "", "()V", "ensureDirectory", "Ljava/io/File;", "base", "name", "", "hotReload", "", "U", "T", "COMMON", "fileName", "registry", "Lejektaflex/bountiful/registry/ValueRegistry;", "fileContentType", "Ljava/lang/Class;", "", "transform", "Lkotlin/Function1;", "Lkotlin/ParameterName;", "it", "validity", "", "hotReloadBounties", "Lejektaflex/bountiful/api/logic/pickable/PickableEntry;", "hotReloadRewards", "Lejektaflex/bountiful/api/logic/picked/PickedEntryStack;", "populateConfigFolder", "folder", "defaultData", "safeHotReloadAll", "Bountiful"}
)
public final class BountifulIO {
   public static final BountifulIO INSTANCE;

   @NotNull
   public final File ensureDirectory(@NotNull File base, @NotNull String name) {
      Intrinsics.checkParameterIsNotNull(base, "base");
      Intrinsics.checkParameterIsNotNull(name, "name");
      File newDir = new File(base, name);
      if (!newDir.exists()) {
         newDir.mkdirs();
      }

      return newDir;
   }

   @NotNull
   public final File populateConfigFolder(@NotNull File folder, @NotNull List<? extends Object> defaultData, @NotNull String fileName) {
      Intrinsics.checkParameterIsNotNull(folder, "folder");
      Intrinsics.checkParameterIsNotNull(defaultData, "defaultData");
      Intrinsics.checkParameterIsNotNull(fileName, "fileName");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      File fileToPopulate = new File(folder, fileName);
      if (!fileToPopulate.exists()) {
         fileToPopulate.createNewFile();
         String content = "Going to serialize content..";
         System.out.println(content);
         Collection $receiver$iv = (Collection)defaultData;
         Object[] var10000 = $receiver$iv.toArray(new Object[0]);
         if (var10000 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
         }

         Object[] var12 = var10000;
         content = gson.toJson(var12);
         Intrinsics.checkExpressionValueIsNotNull(content, "content");
         FilesKt.writeText$default(fileToPopulate, content, (Charset)null, 2, (Object)null);
      }

      return fileToPopulate;
   }

   @NotNull
   public final List<String> safeHotReloadAll() {
      List retMsg = (List)(new ArrayList());

      try {
         List oldBounties = BountyRegistry.INSTANCE.backup();
         List var22 = this.hotReloadBounties();
         Collection var31 = (Collection)var22;
         if (!var31.isEmpty()) {
            for(Object element$iv : (Iterable)var22) {
               PickableEntry it = (PickableEntry)element$iv;
               Collection $i$a$4$map = (Collection)retMsg;
               String $i$f$mapTo = "\u00a74Invalid bounty: " + it.getContent() + ". Reverting to original data.";
               $i$a$4$map.add($i$f$mapTo);
            }
         }

         int numValidBounties = oldBounties.size() - var22.size();
         if (numValidBounties < Bountiful.INSTANCE.getConfig().getBountyAmountRange().getLast()) {
            Collection var37 = (Collection)retMsg;
            String var41 = "\u00a74Your config file ('bounties.json') does not contain enough valid bounties.";
            var37.add(var41);
            BountyRegistry.INSTANCE.restore(oldBounties);
         }

         Collection var23 = (Collection)retMsg;
         String var28 = "Bounties Reloaded.";
         var23.add(var28);
      } catch (Exception var18) {
         Collection $receiver$iv = (Collection)retMsg;
         String $receiver$iv$iv = "\u00a74Invalid bounty json data. Details: ";
         $receiver$iv.add($receiver$iv$iv);
         String var10001 = var18.getMessage();
         if (var10001 == null) {
            Intrinsics.throwNpe();
         }

         Iterable var21 = (Iterable)StringsKt.split$default((CharSequence)var10001, new String[]{"\n"}, false, 0, 6, (Object)null);
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(var21, 10)));

         for(Object item$iv$iv : var21) {
            String it = (String)item$iv$iv;
            String var16 = "\u00a74" + it;
            destination$iv$iv.add(var16);
         }

         List var15 = (List)destination$iv$iv;
         retMsg.addAll((Collection)var15);
      }

      try {
         for(Object element$iv : (Iterable)this.hotReloadRewards()) {
            PickedEntryStack it = (PickedEntryStack)element$iv;
            Collection var39 = (Collection)retMsg;
            String var43 = "\u00a74Invalid reward: " + it.getContent() + ". Skipping.";
            var39.add(var43);
         }

         Collection var20 = (Collection)retMsg;
         String var27 = "Rewards Reloaded.";
         var20.add(var27);
      } catch (Exception var17) {
         Collection var24 = (Collection)retMsg;
         String var29 = "\u00a74Invalid reward json data. Details: ";
         var24.add(var29);
         String var48 = var17.getMessage();
         if (var48 == null) {
            Intrinsics.throwNpe();
         }

         Iterable var25 = (Iterable)StringsKt.split$default((CharSequence)var48, new String[]{"\n"}, false, 0, 6, (Object)null);
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(var25, 10)));

         for(Object item$iv$iv : var25) {
            String var45 = (String)item$iv$iv;
            String var47 = "\u00a74" + var45;
            destination$iv$iv.add(var47);
         }

         List var46 = (List)destination$iv$iv;
         retMsg.addAll((Collection)var46);
      }

      return retMsg;
   }

   private final <T extends COMMON, U extends COMMON, COMMON> List<U> hotReload(String fileName, ValueRegistry<U> registry, Class<T[]> fileContentType, Function1<? super T, ? extends U> transform, Function1<? super U, Boolean> validity) {
      List $receiver$iv;
      try {
         Object var10000 = (new Gson()).fromJson(FilesKt.readText$default(new File(Bountiful.INSTANCE.getConfigDir(), fileName), (Charset)null, 1, (Object)null), fileContentType);
         Intrinsics.checkExpressionValueIsNotNull(var10000, "Gson().fromJson(\n       \u2026ContentType\n            )");
         $receiver$iv = ArraysKt.toList(var10000);
      } catch (Exception var16) {
         throw (Throwable)(new Exception("JSON Structure of '" + fileName + "' is incorrect! Reverting to previous data. Details: " + var16.getMessage()));
      }

      Iterable var17 = (Iterable)CollectionsKt.filterNotNull((Iterable)$receiver$iv);
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(var17, 10)));

      for(Object item$iv$iv : var17) {
         destination$iv$iv.add(transform.invoke(item$iv$iv));
      }

      List var15 = (List)destination$iv$iv;
      return registry.replace(var15, validity);
   }

   @NotNull
   public final List<PickableEntry> hotReloadBounties() {
      return this.hotReload("bounties.json", BountyRegistry.INSTANCE, PickableEntry[].class, null.INSTANCE, null.INSTANCE);
   }

   @NotNull
   public final List<PickedEntryStack> hotReloadRewards() {
      return this.hotReload("rewards.json", RewardRegistry.INSTANCE, PickedEntry[].class, null.INSTANCE, null.INSTANCE);
   }

   private BountifulIO() {
   }

   static {
      BountifulIO var0 = new BountifulIO();
      INSTANCE = var0;
   }
}
