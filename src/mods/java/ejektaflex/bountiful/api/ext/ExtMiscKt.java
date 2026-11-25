package ejektaflex.bountiful.api.ext;

import ejektaflex.bountiful.api.data.IWeighted;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.IntRange;
import kotlin.ranges.LongRange;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000p\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u001a\u0010\u0016\u001a\u00020\u0017*\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u0017\u001a\u0012\u0010\u0016\u001a\u00020\u001a*\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c\u001a\u0012\u0010\u0016\u001a\u00020\u001d*\u00020\u001d2\u0006\u0010\u001b\u001a\u00020\u001e\u001aA\u0010\u001f\u001a\u00020 \"\u000e\b\u0000\u0010\u0011*\b\u0012\u0004\u0012\u00020\"0!*\u00020#2\f\u0010$\u001a\b\u0012\u0004\u0012\u0002H\u00110%2\u0017\u0010&\u001a\u0013\u0012\u0004\u0012\u0002H\u0011\u0012\u0004\u0012\u00020 0'\u00a2\u0006\u0002\b(\u001a\u0012\u0010)\u001a\u00020 *\u00020*2\u0006\u0010+\u001a\u00020\n\u001a\u0012\u0010,\u001a\u00020 *\u00020*2\u0006\u0010-\u001a\u00020\n\"\u0017\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u00028F\u00a2\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0017\u0010\u0005\u001a\u0004\u0018\u00010\u0002*\u00020\u00068F\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\b\"\u0015\u0010\t\u001a\u00020\u0002*\u00020\n8F\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\f\"\u0017\u0010\r\u001a\u0004\u0018\u00010\n*\u00020\u00028F\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000f\"%\u0010\u0010\u001a\u0002H\u0011\"\b\b\u0000\u0010\u0011*\u00020\u0012*\b\u0012\u0004\u0012\u0002H\u00110\u00138F\u00a2\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006."},
   d2 = {"inputStream", "Ljava/io/InputStream;", "Lnet/minecraft/util/ResourceLocation;", "getInputStream", "(Lnet/minecraft/util/ResourceLocation;)Ljava/io/InputStream;", "registryName", "Lnet/minecraft/entity/Entity;", "getRegistryName", "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/ResourceLocation;", "rl", "", "getRl", "(Ljava/lang/String;)Lnet/minecraft/util/ResourceLocation;", "toStringContents", "getToStringContents", "(Lnet/minecraft/util/ResourceLocation;)Ljava/lang/String;", "weightedRandom", "T", "Lejektaflex/bountiful/api/data/IWeighted;", "", "getWeightedRandom", "(Ljava/util/List;)Lejektaflex/bountiful/api/data/IWeighted;", "clampTo", "", "low", "high", "", "range", "Lkotlin/ranges/IntRange;", "", "Lkotlin/ranges/LongRange;", "ifHasCapability", "", "Lnet/minecraftforge/common/util/INBTSerializable;", "Lnet/minecraft/nbt/NBTTagCompound;", "Lnet/minecraft/world/World;", "capability", "Lnet/minecraftforge/common/capabilities/Capability;", "func", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "sendMessage", "Lnet/minecraft/command/ICommandSender;", "str", "sendTranslation", "key", "Bountiful_api"}
)
public final class ExtMiscKt {
   public static final <T extends INBTSerializable<NBTTagCompound>> void ifHasCapability(@NotNull World $receiver, @NotNull Capability<T> capability, @NotNull Function1<? super T, Unit> func) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Intrinsics.checkParameterIsNotNull(capability, "capability");
      Intrinsics.checkParameterIsNotNull(func, "func");
      if ($receiver.hasCapability(capability, (EnumFacing)null)) {
         Object var10001 = $receiver.getCapability(capability, (EnumFacing)null);
         if (var10001 == null) {
            Intrinsics.throwNpe();
         }

         Intrinsics.checkExpressionValueIsNotNull(var10001, "getCapability(capability, null)!!");
         func.invoke(var10001);
      }

   }

   @NotNull
   public static final ResourceLocation getRl(@NotNull String $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      return new ResourceLocation(StringsKt.substringBefore$default($receiver, ":", (String)null, 2, (Object)null), StringsKt.substringAfter$default($receiver, ":", (String)null, 2, (Object)null));
   }

   @Nullable
   public static final InputStream getInputStream(@NotNull ResourceLocation $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Minecraft var10000 = Minecraft.getMinecraft();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "Minecraft.getMinecraft()");
      IResource var1 = var10000.getResourceManager().getResource($receiver);
      Intrinsics.checkExpressionValueIsNotNull(var1, "Minecraft.getMinecraft()\u2026Manager.getResource(this)");
      return var1.getInputStream();
   }

   @Nullable
   public static final String getToStringContents(@NotNull ResourceLocation $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      InputStream var10000 = getInputStream($receiver);
      String var6;
      if (var10000 != null) {
         InputStream var1 = var10000;
         Charset var2 = Charsets.UTF_8;
         Reader var4 = (Reader)(new InputStreamReader(var1, var2));
         short var5 = 8192;
         var6 = TextStreamsKt.readText((Reader)(var4 instanceof BufferedReader ? (BufferedReader)var4 : new BufferedReader(var4, var5)));
      } else {
         var6 = null;
      }

      return var6;
   }

   @Nullable
   public static final ResourceLocation getRegistryName(@NotNull Entity $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      IForgeRegistry var10000 = ForgeRegistries.ENTITIES;
      Intrinsics.checkExpressionValueIsNotNull(ForgeRegistries.ENTITIES, "ForgeRegistries.ENTITIES");
      Set var11 = var10000.getEntries();
      Intrinsics.checkExpressionValueIsNotNull(var11, "ForgeRegistries.ENTITIES.entries");
      Iterable $receiver$iv = (Iterable)var11;
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv : $receiver$iv) {
         label23: {
            Entry it = (Entry)element$iv$iv;
            Class var12 = $receiver.getClass();
            Object var10001 = it.getValue();
            Intrinsics.checkExpressionValueIsNotNull(var10001, "it.value");
            if (var12.isAssignableFrom(((EntityEntry)var10001).getEntityClass())) {
               var12 = (Class)it.getValue();
               Intrinsics.checkExpressionValueIsNotNull(var12, "it.value");
               if (((EntityEntry)var12).getEntityClass().isAssignableFrom($receiver.getClass())) {
                  var14 = true;
                  break label23;
               }
            }

            var14 = false;
         }

         if (var14) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      List valid = (List)destination$iv$iv;
      Entry var15 = (Entry)CollectionsKt.firstOrNull(valid);
      return var15 != null ? (ResourceLocation)var15.getKey() : null;
   }

   public static final void sendMessage(@NotNull ICommandSender $receiver, @NotNull String str) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Intrinsics.checkParameterIsNotNull(str, "str");
      $receiver.sendMessage((ITextComponent)(new TextComponentString(str)));
   }

   public static final void sendTranslation(@NotNull ICommandSender $receiver, @NotNull String key) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Intrinsics.checkParameterIsNotNull(key, "key");
      $receiver.sendMessage((ITextComponent)(new TextComponentTranslation(key, new Object[0])));
   }

   public static final int clampTo(int $receiver, @NotNull IntRange range) {
      Intrinsics.checkParameterIsNotNull(range, "range");
      int var2 = range.getFirst();
      int var3 = range.getLast();
      var3 = Math.min($receiver, var3);
      return Math.max(var2, var3);
   }

   public static final double clampTo(double $receiver, double low, double high) {
      double var6 = Math.min($receiver, high);
      return Math.max(low, var6);
   }

   public static final long clampTo(long $receiver, @NotNull LongRange range) {
      Intrinsics.checkParameterIsNotNull(range, "range");
      long var3 = range.getFirst();
      long var5 = range.getLast();
      var5 = Math.min($receiver, var5);
      return Math.max(var3, var5);
   }

   @NotNull
   public static final <T extends IWeighted> T getWeightedRandom(@NotNull List<? extends T> $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      Iterable $receiver$iv = (Iterable)$receiver;
      int sum$iv = 0;

      for(Object element$iv : $receiver$iv) {
         IWeighted it = (IWeighted)element$iv;
         int var10 = it.getWeight();
         sum$iv += var10;
      }

      int sum = sum$iv;
      sum$iv = 0;
      IntRange var13 = new IntRange(sum$iv, sum);
      int point = RangesKt.random(var13, (Random)Random.Default);

      for(IWeighted item : $receiver) {
         if (point <= item.getWeight()) {
            return (T)item;
         }

         point -= item.getWeight();
      }

      return (T)(CollectionsKt.last($receiver));
   }
}
