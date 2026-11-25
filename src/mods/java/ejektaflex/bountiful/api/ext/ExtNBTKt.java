package ejektaflex.bountiful.api.ext;

import ejektaflex.bountiful.api.logic.ItemRange;
import ejektaflex.bountiful.api.logic.picked.IPickedEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\u0018\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u0007\u001a6\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\t0\u0004\"\u000e\b\u0000\u0010\t*\b\u0012\u0004\u0012\u00020\u00020\n*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\t0\f\u001a\u0018\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u0007\u001a&\u0010\u000e\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00020\n0\u0004\u001a \u0010\u0010\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004\u00a8\u0006\u0011"},
   d2 = {"clear", "", "Lnet/minecraft/nbt/NBTTagCompound;", "getPickedEntryList", "", "Lejektaflex/bountiful/api/logic/picked/IPickedEntry;", "key", "", "getUnsortedList", "T", "Lnet/minecraftforge/common/util/INBTSerializable;", "itemGen", "Lkotlin/Function0;", "getUnsortedStringSet", "setUnsortedList", "items", "setUnsortedStringSet", "Bountiful_api"}
)
public final class ExtNBTKt {
   public static final void clear(@NotNull NBTTagCompound $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");

      for(String key : $receiver.getKeySet()) {
         $receiver.removeTag(key);
      }

   }

   public static final void setUnsortedStringSet(@NotNull NBTTagCompound $receiver, @NotNull String key, @NotNull Set<String> items) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Intrinsics.checkParameterIsNotNull(key, "key");
      Intrinsics.checkParameterIsNotNull(items, "items");
      NBTTagCompound var4 = new NBTTagCompound();
      NBTTagCompound $receiver = var4;
      Iterable $receiver$iv = (Iterable)items;
      int index$iv = 0;

      for(Object item$iv : $receiver$iv) {
         int var10 = index$iv++;
         if (var10 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         String item = (String)item$iv;
         $receiver.setString(String.valueOf(var10), item);
      }

      $receiver.setTag(key, (NBTBase)var4);
   }

   @NotNull
   public static final Set<String> getUnsortedStringSet(@NotNull NBTTagCompound $receiver, @NotNull String key) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Intrinsics.checkParameterIsNotNull(key, "key");
      NBTTagCompound listTag = $receiver.getCompoundTag(key);
      Intrinsics.checkExpressionValueIsNotNull(listTag, "listTag");
      Set var10000 = listTag.getKeySet();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "listTag.keySet");
      Iterable $receiver$iv = (Iterable)var10000;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

      for(Object item$iv$iv : $receiver$iv) {
         String index = (String)item$iv$iv;
         String var13 = listTag.getString(index);
         destination$iv$iv.add(var13);
      }

      return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
   }

   public static final void setUnsortedList(@NotNull NBTTagCompound $receiver, @NotNull String key, @NotNull Set<? extends INBTSerializable<NBTTagCompound>> items) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Intrinsics.checkParameterIsNotNull(key, "key");
      Intrinsics.checkParameterIsNotNull(items, "items");
      NBTTagCompound var4 = new NBTTagCompound();
      NBTTagCompound $receiver = var4;
      Iterable $receiver$iv = (Iterable)items;
      int index$iv = 0;

      for(Object item$iv : $receiver$iv) {
         int var10 = index$iv++;
         if (var10 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         INBTSerializable item = (INBTSerializable)item$iv;
         $receiver.setTag(String.valueOf(var10), item.serializeNBT());
      }

      $receiver.setTag(key, (NBTBase)var4);
   }

   @NotNull
   public static final <T extends INBTSerializable<NBTTagCompound>> Set<T> getUnsortedList(@NotNull NBTTagCompound $receiver, @NotNull String key, @NotNull Function0<? extends T> itemGen) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Intrinsics.checkParameterIsNotNull(key, "key");
      Intrinsics.checkParameterIsNotNull(itemGen, "itemGen");
      NBTTagCompound listTag = $receiver.getCompoundTag(key);
      Intrinsics.checkExpressionValueIsNotNull(listTag, "listTag");
      Set var10000 = listTag.getKeySet();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "listTag.keySet");
      Iterable $receiver$iv = (Iterable)var10000;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

      for(Object item$iv$iv : $receiver$iv) {
         String index = (String)item$iv$iv;
         NBTTagCompound itag = listTag.getCompoundTag(index);
         Object var11 = itemGen.invoke();
         INBTSerializable $receiver = (INBTSerializable)var11;
         $receiver.deserializeNBT((NBTBase)itag);
         INBTSerializable var18 = (INBTSerializable)var11;
         destination$iv$iv.add(var18);
      }

      return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
   }

   @NotNull
   public static final Set<IPickedEntry> getPickedEntryList(@NotNull NBTTagCompound $receiver, @NotNull String key) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Intrinsics.checkParameterIsNotNull(key, "key");
      NBTTagCompound listTag = $receiver.getCompoundTag(key);
      Intrinsics.checkExpressionValueIsNotNull(listTag, "listTag");
      Set var10000 = listTag.getKeySet();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "listTag.keySet");
      Iterable $receiver$iv = (Iterable)var10000;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

      for(Object item$iv$iv : $receiver$iv) {
         String index = (String)item$iv$iv;
         NBTTagCompound itag = listTag.getCompoundTag(index);
         PickedEntry var10 = new PickedEntry((String)null, 0, 0, (String)null, (List)null, (ItemRange)null, 63, (DefaultConstructorMarker)null);
         Intrinsics.checkExpressionValueIsNotNull(itag, "itag");
         var10.deserializeNBT(itag);
         IPickedEntry var18 = var10.typed();
         var18.deserializeNBT((NBTBase)itag);
         destination$iv$iv.add(var18);
      }

      return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
   }
}
