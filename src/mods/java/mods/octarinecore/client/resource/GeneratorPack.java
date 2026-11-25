package mods.octarinecore.client.resource;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0001\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00060\u0005\"\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\u0014\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0016J\n\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0016J/\u0010\u0013\u001a\u0004\u0018\u0001H\u0014\"\n\b\u0000\u0010\u0014*\u0004\u0018\u00010\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0003H\u0016\u00a2\u0006\u0002\u0010\u0019J\b\u0010\u001a\u001a\u00020\u0003H\u0016J\u0016\u0010\u001b\u001a\u0010\u0012\f\u0012\n \u001d*\u0004\u0018\u00010\u00030\u00030\u001cH\u0016J\u0006\u0010\u001e\u001a\u00020\u001fJ\u0012\u0010 \u001a\u00020!2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0016R\u001b\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00060\u0005\u00a2\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\""},
   d2 = {"Lmods/octarinecore/client/resource/GeneratorPack;", "Lnet/minecraft/client/resources/IResourcePack;", "name", "", "generators", "", "Lmods/octarinecore/client/resource/GeneratorBase;", "(Ljava/lang/String;[Lmods/octarinecore/client/resource/GeneratorBase;)V", "getGenerators", "()[Lmods/octarinecore/client/resource/GeneratorBase;", "[Lmods/octarinecore/client/resource/GeneratorBase;", "getName", "()Ljava/lang/String;", "getInputStream", "Ljava/io/InputStream;", "location", "Lnet/minecraft/util/ResourceLocation;", "getPackImage", "", "getPackMetadata", "T", "Lnet/minecraft/client/resources/data/IMetadataSection;", "serializer", "Lnet/minecraft/client/resources/data/MetadataSerializer;", "sectionName", "(Lnet/minecraft/client/resources/data/MetadataSerializer;Ljava/lang/String;)Lnet/minecraft/client/resources/data/IMetadataSection;", "getPackName", "getResourceDomains", "Ljava/util/HashSet;", "kotlin.jvm.PlatformType", "inject", "", "resourceExists", "", "BetterFoliage-MC1.12"}
)
public final class GeneratorPack implements IResourcePack {
   @NotNull
   private final String name;
   @NotNull
   private final GeneratorBase[] generators;

   public final void inject() {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public String getPackName() {
      return this.name;
   }

   @Nullable
   public Void getPackImage() {
      return null;
   }

   @NotNull
   public HashSet<String> getResourceDomains() {
      Object[] $this$map$iv = this.generators;
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList($this$map$iv.length));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         int $i$a$-map-GeneratorPack$getResourceDomains$1 = 0;
         String var15 = ((GeneratorBase)item$iv$iv).getDomain();
         destination$iv$iv.add(var15);
      }

      List var14 = (List)destination$iv$iv;
      Collection var16 = (Collection)var14;
      return new HashSet(var16);
   }

   @Nullable
   public <T extends IMetadataSection> T getPackMetadata(@Nullable MetadataSerializer serializer, @Nullable String sectionName) {
      IMetadataSection var3;
      if (Intrinsics.areEqual(sectionName, "pack")) {
         PackMetadataSection var10000 = new PackMetadataSection((ITextComponent)(new TextComponentString("Generated resources")), 1);
         if (!(var10000 instanceof IMetadataSection)) {
            var10000 = null;
         }

         var3 = (IMetadataSection)var10000;
      } else {
         var3 = null;
      }

      return (T)var3;
   }

   public boolean resourceExists(@Nullable ResourceLocation location) {
      boolean var10000;
      if (location == null) {
         var10000 = false;
      } else {
         GeneratorBase[] var2 = this.generators;
         boolean var3 = false;
         boolean var5 = false;
         GeneratorBase[] var6 = var2;
         int var7 = var2.length;
         int var8 = 0;

         while(true) {
            if (var8 >= var7) {
               var12 = null;
               break;
            }

            GeneratorBase var9 = var6[var8];
            int $i$a$-find-GeneratorPack$resourceExists$1 = 0;
            if (Intrinsics.areEqual(var9.getDomain(), location.getResourceDomain()) && var9.resourceExists(location)) {
               var12 = var9;
               break;
            }

            ++var8;
         }

         var10000 = var12 != null;
      }

      return var10000;
   }

   @Nullable
   public InputStream getInputStream(@Nullable ResourceLocation location) {
      InputStream var10000;
      if (location == null) {
         var10000 = null;
      } else {
         Object[] $this$filter$iv = this.generators;
         int $i$f$filter = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList());
         int $i$f$filterTo = 0;

         for(Object element$iv$iv : $this$filter$iv) {
            int $i$a$-filter-GeneratorPack$getInputStream$1 = 0;
            if (Intrinsics.areEqual(((GeneratorBase)element$iv$iv).getDomain(), location.getResourceDomain()) && ((GeneratorBase)element$iv$iv).resourceExists(location)) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         $this$filter$iv = (Iterable)((List)destination$iv$iv);
         $i$f$filter = 0;
         destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$filter$iv, 10)));
         $i$f$filterTo = 0;

         for(Object item$iv$iv : $this$filter$iv) {
            GeneratorBase var21 = (GeneratorBase)item$iv$iv;
            int $i$a$-map-GeneratorPack$getInputStream$2 = 0;
            InputStream var14 = var21.getInputStream(location);
            destination$iv$iv.add(var14);
         }

         var10000 = (InputStream)CollectionsKt.first(CollectionsKt.filterNotNull((Iterable)((List)destination$iv$iv)));
      }

      return var10000;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @NotNull
   public final GeneratorBase[] getGenerators() {
      return this.generators;
   }

   public GeneratorPack(@NotNull String name, @NotNull GeneratorBase... generators) {
      Intrinsics.checkParameterIsNotNull(name, "name");
      Intrinsics.checkParameterIsNotNull(generators, "generators");
      super();
      this.name = name;
      this.generators = generators;
   }
}
