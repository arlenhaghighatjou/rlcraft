package ejektaflex.bountiful.api.ext;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000.\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0017\u0010\u0005\u001a\u0004\u0018\u00010\u0006*\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b\"\u0017\u0010\t\u001a\u0004\u0018\u00010\n*\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\f\"\u0017\u0010\r\u001a\u0004\u0018\u00010\u000e*\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010\"\u0015\u0010\u0011\u001a\u00020\u0012*\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014\"\u0015\u0010\u0015\u001a\u00020\u0001*\u00020\u000e8F¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017¨\u0006\u0018"},
   d2 = {"pretty", "", "Lnet/minecraft/block/state/IBlockState;", "getPretty", "(Lnet/minecraft/block/state/IBlockState;)Ljava/lang/String;", "toEntityEntry", "Lnet/minecraftforge/fml/common/registry/EntityEntry;", "getToEntityEntry", "(Ljava/lang/String;)Lnet/minecraftforge/fml/common/registry/EntityEntry;", "toItem", "Lnet/minecraft/item/Item;", "getToItem", "(Ljava/lang/String;)Lnet/minecraft/item/Item;", "toItemStack", "Lnet/minecraft/item/ItemStack;", "getToItemStack", "(Ljava/lang/String;)Lnet/minecraft/item/ItemStack;", "toMeta", "", "getToMeta", "(Ljava/lang/String;)I", "toPretty", "getToPretty", "(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", "Bountiful_api"}
)
public final class ExtStringKt {
   @Nullable
   public static final EntityEntry getToEntityEntry(@NotNull String $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      if (!StringsKt.contains$default((CharSequence)$receiver, (CharSequence)":", false, 2, (Object)null)) {
         return null;
      } else if (!Intrinsics.areEqual(StringsKt.substringBefore$default($receiver, ":", (String)null, 2, (Object)null), "entity")) {
         return null;
      } else {
         Object id = StringsKt.substringAfter$default($receiver, ":", (String)null, 2, (Object)null);
         if (!StringsKt.contains$default((CharSequence)id, (CharSequence)":", false, 2, (Object)null)) {
            id = "minecraft:" + id;
         }

         IForgeRegistry var10000 = ForgeRegistries.ENTITIES;
         Intrinsics.checkExpressionValueIsNotNull(ForgeRegistries.ENTITIES, "ForgeRegistries.ENTITIES");
         Set var8 = var10000.getEntries();
         Intrinsics.checkExpressionValueIsNotNull(var8, "ForgeRegistries.ENTITIES.entries");
         Iterable var2 = (Iterable)var8;
         Iterator var4 = var2.iterator();

         while(true) {
            if (var4.hasNext()) {
               Object var5 = var4.next();
               Entry it = (Entry)var5;
               if (!Intrinsics.areEqual(((ResourceLocation)it.getKey()).toString(), id)) {
                  continue;
               }

               var8 = (Set)var5;
               break;
            }

            var8 = null;
            break;
         }

         return (Entry)var8 != null ? (EntityEntry)((Entry)var8).getValue() : null;
      }
   }

   public static final int getToMeta(@NotNull String $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      int var10000;
      if (Intrinsics.areEqual($receiver, "*")) {
         var10000 = 32767;
      } else {
         int var1;
         try {
            var1 = Integer.parseInt($receiver);
         } catch (Exception var3) {
            var3.printStackTrace();
            var1 = 0;
         }

         var10000 = var1;
      }

      return var10000;
   }

   @Nullable
   public static final ItemStack getToItemStack(@NotNull String $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      List sect = CollectionsKt.toMutableList((Collection)StringsKt.split$default((CharSequence)$receiver, new String[]{":"}, false, 0, 6, (Object)null));
      int item = sect.size();
      if (2 <= item) {
         if (3 >= item) {
            if (sect.size() == 2) {
               Collection var4 = (Collection)sect;
               String var3 = "0";
               var4.add(var3);
            }

            Item item = Item.getByNameOrId((String)sect.get(0) + ':' + (String)sect.get(1));
            return item != null ? new ItemStack(item, 1, getToMeta((String)sect.get(2))) : null;
         }
      }

      return null;
   }

   @NotNull
   public static final String getToPretty(@NotNull ItemStack $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Item var10000 = $receiver.getItem();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "item");
      String proto = String.valueOf(var10000.getRegistryName());
      int meta = $receiver.getMetadata();
      if (meta != 0) {
         proto = proto + ':' + meta;
      }

      return proto;
   }

   @NotNull
   public static final String getPretty(@NotNull IBlockState $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Block var10000 = $receiver.getBlock();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "block");
      String proto = String.valueOf(var10000.getRegistryName());
      int meta = $receiver.getBlock().getMetaFromState($receiver);
      if (meta != 0) {
         proto = proto + ':' + meta;
      }

      return proto;
   }

   @Nullable
   public static final Item getToItem(@NotNull String $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      ItemStack var10000 = getToItemStack($receiver);
      return var10000 != null ? var10000.getItem() : null;
   }
}
