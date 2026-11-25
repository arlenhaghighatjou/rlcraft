package ejektaflex.bountiful.api.ext;

import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\"\u0017\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u00028F\u00a2\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0017\u0010\u0005\u001a\u0004\u0018\u00010\u0002*\u00020\u00068F\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\b\"\u0015\u0010\t\u001a\u00020\u0006*\u00020\u00028F\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"},
   d2 = {"modOriginName", "", "Lnet/minecraft/item/ItemStack;", "getModOriginName", "(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", "toItemStack", "Lnet/minecraft/nbt/NBTTagCompound;", "getToItemStack", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/item/ItemStack;", "toNBT", "getToNBT", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NBTTagCompound;", "Bountiful_api"}
)
public final class ExtItemStackKt {
   @NotNull
   public static final NBTTagCompound getToNBT(@NotNull ItemStack $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      NBTTagCompound var1 = new NBTTagCompound();
      var1.setString("e_item", ExtStringKt.getToPretty($receiver));
      var1.setInteger("e_amt", $receiver.getCount());
      var1.setTag("e_nbt", (NBTBase)$receiver.serializeNBT());
      return var1;
   }

   @Nullable
   public static final ItemStack getToItemStack(@NotNull NBTTagCompound $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      String var10000 = $receiver.getString("e_item");
      Intrinsics.checkExpressionValueIsNotNull(var10000, "getString(\"e_item\")");
      ItemStack istack = ExtStringKt.getToItemStack(var10000);
      ItemStack var5;
      if (istack != null) {
         istack.setCount($receiver.getInteger("e_amt"));
         istack.setTagCompound($receiver.getCompoundTag("e_nbt"));
         var5 = istack;
      } else {
         var5 = null;
      }

      return var5;
   }

   @Nullable
   public static final String getModOriginName(@NotNull ItemStack $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Item var10000 = $receiver.getItem();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "item");
      ResourceLocation var8 = var10000.getRegistryName();
      String modid = var8 != null ? var8.getResourceDomain() : null;
      String var12;
      if (modid != null) {
         Loader var9 = Loader.instance();
         Intrinsics.checkExpressionValueIsNotNull(var9, "Loader.instance()");
         List var10 = var9.getModList();
         Intrinsics.checkExpressionValueIsNotNull(var10, "Loader.instance().modList");
         Iterable var2 = (Iterable)var10;
         Iterator var4 = var2.iterator();

         while(true) {
            if (!var4.hasNext()) {
               var10 = null;
               break;
            }

            Object var5 = var4.next();
            ModContainer it = (ModContainer)var5;
            Intrinsics.checkExpressionValueIsNotNull(it, "it");
            if (Intrinsics.areEqual(it.getModId(), modid)) {
               var10 = (List)var5;
               break;
            }
         }

         var12 = (ModContainer)var10 != null ? ((ModContainer)var10).getName() : null;
      } else {
         var12 = null;
      }

      return var12;
   }
}
