package mods.octarinecore.client.resource;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import mods.betterfoliage.loader.Refs;
import mods.octarinecore.client.render.HSB;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000d\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\u001a\u000e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00020\u001a\u001a\u001e\u0010\u001b\u001a\u00020\u001c*\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00120\u00102\u0006\u0010\u001d\u001a\u00020\u0012\u001a\u001d\u0010\u001e\u001a\u00020\n*\u00020\u00062\u0006\u0010\u001f\u001a\u00020\n2\u0006\u0010 \u001a\u00020\nH\u0086\u0002\u001a\u0017\u0010\u001e\u001a\u0004\u0018\u00010\u000b*\u00020!2\u0006\u0010\"\u001a\u00020\u001aH\u0086\u0002\u001a\u0017\u0010\u001e\u001a\u0004\u0018\u00010\u000b*\u00020!2\u0006\u0010#\u001a\u00020\u0012H\u0086\u0002\u001a\u001f\u0010\u001e\u001a\u0004\u0018\u00010$*\u00020%2\u0006\u0010&\u001a\u00020\u001a2\u0006\u0010'\u001a\u00020\u001aH\u0086\u0002\u001a\u0017\u0010\u001e\u001a\u0004\u0018\u00010$*\u00020%2\u0006\u0010(\u001a\u00020\u0012H\u0086\u0002\u001a\u0010\u0010)\u001a\b\u0012\u0004\u0012\u00020\u001a0\u000f*\u00020$\u001a\f\u0010*\u001a\u0004\u0018\u00010\u0006*\u00020$\u001a\u0015\u0010+\u001a\u00020\u0012*\u00020\u00122\u0006\u0010,\u001a\u00020\u001aH\u0086\u0002\u001a%\u0010-\u001a\u00020.*\u00020\u00062\u0006\u0010\u001f\u001a\u00020\n2\u0006\u0010 \u001a\u00020\n2\u0006\u0010/\u001a\u00020\nH\u0086\u0002\"\u0011\u0010\u0000\u001a\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u0002\u0010\u0003\"\u0015\u0010\u0004\u001a\u00020\u0005*\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b\"\u0017\u0010\t\u001a\u0004\u0018\u00010\n*\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b\f\u0010\r\"-\u0010\u000e\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00120\u00100\u000f*\u00020\u00138F¢\u0006\f\u0012\u0004\b\u0014\u0010\u0015\u001a\u0004\b\u0016\u0010\u0017¨\u00060"},
   d2 = {"resourceManager", "Lnet/minecraft/client/resources/SimpleReloadableResourceManager;", "getResourceManager", "()Lnet/minecraft/client/resources/SimpleReloadableResourceManager;", "asStream", "Ljava/io/InputStream;", "Ljava/awt/image/BufferedImage;", "getAsStream", "(Ljava/awt/image/BufferedImage;)Ljava/io/InputStream;", "averageColor", "", "Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "getAverageColor", "(Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;)Ljava/lang/Integer;", "modelBlockAndLoc", "", "Lkotlin/Pair;", "Lnet/minecraft/client/renderer/block/model/ModelBlock;", "Lnet/minecraft/util/ResourceLocation;", "Lnet/minecraftforge/client/model/IModel;", "modelBlockAndLoc$annotations", "(Lnet/minecraftforge/client/model/IModel;)V", "getModelBlockAndLoc", "(Lnet/minecraftforge/client/model/IModel;)Ljava/util/List;", "textureLocation", "iconName", "", "derivesFrom", "", "targetLocation", "get", "x", "y", "Lnet/minecraft/client/renderer/texture/TextureMap;", "name", "res", "Lnet/minecraft/client/resources/IResource;", "Lnet/minecraft/client/resources/IResourceManager;", "domain", "path", "location", "getLines", "loadImage", "plus", "str", "set", "", "value", "BetterFoliage-MC1.12"}
)
@JvmName(
   name = "Utils"
)
public final class Utils {
   @NotNull
   public static final SimpleReloadableResourceManager getResourceManager() {
      Minecraft var10000 = Minecraft.getMinecraft();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "Minecraft.getMinecraft()");
      IResourceManager var0 = var10000.getResourceManager();
      if (var0 == null) {
         throw new TypeCastException("null cannot be cast to non-null type net.minecraft.client.resources.SimpleReloadableResourceManager");
      } else {
         return (SimpleReloadableResourceManager)var0;
      }
   }

   @NotNull
   public static final ResourceLocation plus(@NotNull ResourceLocation $this$plus, @NotNull String str) {
      Intrinsics.checkParameterIsNotNull($this$plus, "$this$plus");
      Intrinsics.checkParameterIsNotNull(str, "str");
      return new ResourceLocation($this$plus.getResourceDomain(), $this$plus.getResourcePath() + str);
   }

   @Nullable
   public static final IResource get(@NotNull IResourceManager $this$get, @NotNull String domain, @NotNull String path) {
      Intrinsics.checkParameterIsNotNull($this$get, "$this$get");
      Intrinsics.checkParameterIsNotNull(domain, "domain");
      Intrinsics.checkParameterIsNotNull(path, "path");
      return get($this$get, new ResourceLocation(domain, path));
   }

   @Nullable
   public static final IResource get(@NotNull final IResourceManager $this$get, @NotNull final ResourceLocation location) {
      Intrinsics.checkParameterIsNotNull($this$get, "$this$get");
      Intrinsics.checkParameterIsNotNull(location, "location");
      return (IResource)mods.octarinecore.Utils.tryDefault((Object)null, new Function0<IResource>() {
         public final IResource invoke() {
            return $this$get.getResource(location);
         }
      });
   }

   @Nullable
   public static final EaglerTextureAtlasSprite get(@NotNull TextureMap $this$get, @NotNull String name) {
      Intrinsics.checkParameterIsNotNull($this$get, "$this$get");
      Intrinsics.checkParameterIsNotNull(name, "name");
      return $this$get.getTextureExtry((new ResourceLocation(name)).toString());
   }

   @Nullable
   public static final EaglerTextureAtlasSprite get(@NotNull TextureMap $this$get, @NotNull ResourceLocation res) {
      Intrinsics.checkParameterIsNotNull($this$get, "$this$get");
      Intrinsics.checkParameterIsNotNull(res, "res");
      return $this$get.getTextureExtry(res.toString());
   }

   @Nullable
   public static final BufferedImage loadImage(@NotNull IResource $this$loadImage) {
      Intrinsics.checkParameterIsNotNull($this$loadImage, "$this$loadImage");
      return ImageIO.read($this$loadImage.getInputStream());
   }

   @NotNull
   public static final List<String> getLines(@NotNull IResource $this$getLines) {
      Intrinsics.checkParameterIsNotNull($this$getLines, "$this$getLines");
      boolean $this$useLines$iv = false;
      ArrayList result = new ArrayList();
      InputStream var10000 = $this$getLines.getInputStream();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "inputStream");
      InputStream var21 = var10000;
      Charset $i$f$useLines = Charsets.UTF_8;
      boolean var4 = false;
      short var6 = 0;
      Reader var5 = (Reader)(new InputStreamReader(var21, $i$f$useLines));
      var6 = 8192;
      boolean it$iv = false;
      Reader $this$useLines$iv = (Reader)(var5 instanceof BufferedReader ? (BufferedReader)var5 : new BufferedReader(var5, var6));
      int $i$f$useLines = 0;
      short var25 = 8192;
      var6 = 0;
      Closeable var24 = (Closeable)($this$useLines$iv instanceof BufferedReader ? (BufferedReader)$this$useLines$iv : new BufferedReader($this$useLines$iv, var25));
      var25 = 0;
      Throwable var29 = (Throwable)null;

      try {
         BufferedReader it$iv = (BufferedReader)var24;
         int $i$a$-use-TextStreamsKt$useLines$1 = 0;
         Sequence it = TextStreamsKt.lineSequence(it$iv);
         int $i$a$-useLines-Utils$getLines$1 = 0;
         int $i$f$forEach = 0;

         for(Object element$iv : it) {
            String it = (String)element$iv;
            int $i$a$-forEach-Utils$getLines$1$1 = 0;
            result.add(it);
         }

         Unit var31 = Unit.INSTANCE;
      } catch (Throwable var19) {
         var29 = var19;
         throw var19;
      } finally {
         CloseableKt.closeFinally(var24, var29);
      }

      return (List)result;
   }

   public static final int get(@NotNull BufferedImage $this$get, int x, int y) {
      Intrinsics.checkParameterIsNotNull($this$get, "$this$get");
      return $this$get.getRGB(x, y);
   }

   public static final void set(@NotNull BufferedImage $this$set, int x, int y, int value) {
      Intrinsics.checkParameterIsNotNull($this$set, "$this$set");
      $this$set.setRGB(x, y, value);
   }

   @NotNull
   public static final InputStream getAsStream(@NotNull BufferedImage $this$asStream) {
      Intrinsics.checkParameterIsNotNull($this$asStream, "$this$asStream");
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      boolean var2 = false;
      boolean var3 = false;
      int $i$a$-let-Utils$asStream$1 = 0;
      ImageIO.write((RenderedImage)$this$asStream, "PNG", (OutputStream)var1);
      byte[] var8 = var1.toByteArray();
      return (InputStream)(new ByteArrayInputStream(var8));
   }

   @Nullable
   public static final Integer getAverageColor(@NotNull EaglerTextureAtlasSprite $this$averageColor) {
      Intrinsics.checkParameterIsNotNull($this$averageColor, "$this$averageColor");
      ResourceLocation locationWithDirs = new ResourceLocation($this$averageColor.getIconName());
      String str$iv = "blocks/";
      int $i$f$stripStart = 0;
      String var10000 = locationWithDirs.getResourceDomain();
      String var10001 = locationWithDirs.getResourcePath();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "path");
      String sumHueX = var10001;
      String var6 = var10000;
      int $i$f$stripStart = 0;
      if (StringsKt.startsWith$default(sumHueX, str$iv, false, 2, (Object)null)) {
         int avgHue = str$iv.length();
         boolean var12 = false;
         if (sumHueX == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
         }

         var10000 = sumHueX.substring(avgHue);
         Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).substring(startIndex)");
      } else {
         var10000 = sumHueX;
      }

      String y = var10000;
      ResourceLocation locationNoDirs = new ResourceLocation(var6, y);
      var10000 = locationNoDirs.getResourceDomain();
      str$iv = "textures/blocks/%s.png";
      Object[] var29 = new Object[]{locationNoDirs.getResourcePath()};
      String var20 = var10000;
      boolean var31 = false;
      var10000 = String.format(str$iv, Arrays.copyOf(var29, var29.length));
      Intrinsics.checkExpressionValueIsNotNull(var10000, "java.lang.String.format(this, *args)");
      String var21 = var10000;
      locationWithDirs = new ResourceLocation(var20, var21);
      IResource var41 = get((IResourceManager)getResourceManager(), locationWithDirs);
      if (var41 != null) {
         BufferedImage var42 = loadImage(var41);
         if (var42 != null) {
            BufferedImage image = var42;
            int numOpaque = 0;
            double sumHueX = 0.0D;
            double sumHueY = 0.0D;
            float sumSaturation = 0.0F;
            float sumBrightness = 0.0F;
            int var34 = 0;
            int var36 = image.getWidth() - 1;
            if (var34 <= var36) {
               while(true) {
                  int var37 = 0;
                  int var14 = image.getHeight() - 1;
                  if (var37 <= var14) {
                     while(true) {
                        int pixel = get(image, var34, var37);
                        int alpha = pixel >> 24 & 255;
                        HSB hsb = HSB.Companion.fromColor(pixel);
                        if (alpha == 255) {
                           ++numOpaque;
                           sumHueX += Math.cos(((double)hsb.getHue() - 0.5D) * 6.283185307179586D);
                           sumHueY += Math.sin(((double)hsb.getHue() - 0.5D) * 6.283185307179586D);
                           sumSaturation += hsb.getSaturation();
                           sumBrightness += hsb.getBrightness();
                        }

                        if (var37 == var14) {
                           break;
                        }

                        ++var37;
                     }
                  }

                  if (var34 == var36) {
                     break;
                  }

                  ++var34;
               }
            }

            float avgHue = (float)(Math.atan2(sumHueY, sumHueX) / 6.283185307179586D + 0.5D);
            return (new HSB(avgHue, sumSaturation / (float)numOpaque, sumBrightness / (float)numOpaque)).getAsColor();
         }
      }

      return null;
   }

   @NotNull
   public static final ResourceLocation textureLocation(@NotNull String iconName) {
      Intrinsics.checkParameterIsNotNull(iconName, "iconName");
      ResourceLocation var1 = new ResourceLocation(iconName);
      boolean var2 = false;
      boolean var3 = false;
      int $i$a$-let-Utils$textureLocation$1 = 0;
      String var10000 = var1.getResourcePath();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "it.path");
      return StringsKt.startsWith$default(var10000, "mcpatcher", false, 2, (Object)null) ? var1 : new ResourceLocation(var1.getResourceDomain(), "textures/" + var1.getResourcePath());
   }

   @NotNull
   public static final List<Pair<ModelBlock, ResourceLocation>> getModelBlockAndLoc(@NotNull IModel $this$modelBlockAndLoc) {
      Intrinsics.checkParameterIsNotNull($this$modelBlockAndLoc, "$this$modelBlockAndLoc");
      if (Refs.INSTANCE.getVanillaModelWrapper().isInstance($this$modelBlockAndLoc)) {
         Pair var44 = new Pair;
         ModelBlock var10002 = (ModelBlock)Refs.INSTANCE.getModel_VMW().get($this$modelBlockAndLoc);
         if (var10002 == null) {
            throw new TypeCastException("null cannot be cast to non-null type net.minecraft.client.renderer.block.model.ModelBlock");
         } else {
            var10002 = var10002;
            Object var10003 = Refs.INSTANCE.getLocation_VMW().get($this$modelBlockAndLoc);
            if (var10003 == null) {
               throw new TypeCastException("null cannot be cast to non-null type net.minecraft.util.ResourceLocation");
            } else {
               var44.<init>(var10002, (ResourceLocation)var10003);
               return CollectionsKt.listOf(var44);
            }
         }
      } else {
         if (Refs.INSTANCE.getWeightedRandomModel().isInstance($this$modelBlockAndLoc)) {
            Object var10000 = Refs.INSTANCE.getModels_WRM().get($this$modelBlockAndLoc);
            if (var10000 != null) {
               Object var1 = var10000;
               boolean var2 = false;
               boolean var3 = false;
               int $i$a$-let-Utils$modelBlockAndLoc$1 = 0;
               if (var1 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.List<net.minecraftforge.client.model.IModel>");
               }

               Iterable $this$flatMap$iv = (Iterable)((List)var1);
               int $i$f$flatMap = 0;
               Collection destination$iv$iv = (Collection)(new ArrayList());
               int $i$f$flatMapTo = 0;

               for(Object element$iv$iv : $this$flatMap$iv) {
                  int $i$a$-unknown-Object = 0;
                  Iterable list$iv$iv = (Iterable)getModelBlockAndLoc((IModel)element$iv$iv);
                  CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
               }

               return (List)destination$iv$iv;
            }
         } else if (Refs.INSTANCE.getMultipartModel().isInstance($this$modelBlockAndLoc)) {
            Object var40 = Refs.INSTANCE.getPartModels_MPM().get($this$modelBlockAndLoc);
            if (var40 != null) {
               Object var17 = var40;
               boolean var21 = false;
               boolean var24 = false;
               int $i$a$-let-Utils$modelBlockAndLoc$2 = 0;
               if (var17 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.Map<kotlin.Any, net.minecraftforge.client.model.IModel>");
               }

               Map $this$flatMap$iv = (Map)var17;
               int $i$f$flatMap = 0;
               Collection destination$iv$iv = (Collection)(new ArrayList());
               int $i$f$flatMapTo = 0;
               boolean var37 = false;

               for(Entry element$iv$iv : $this$flatMap$iv.entrySet()) {
                  int $i$a$-flatMap-Utils$modelBlockAndLoc$2$1 = 0;
                  Iterable list$iv$iv = (Iterable)getModelBlockAndLoc((IModel)element$iv$iv.getValue());
                  CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
               }

               return (List)destination$iv$iv;
            }
         } else {
            Field[] var41 = $this$modelBlockAndLoc.getClass().getDeclaredFields();
            Intrinsics.checkExpressionValueIsNotNull(var41, "this::class.java.declaredFields");
            Field[] var18 = var41;
            boolean var22 = false;
            boolean modelField = false;
            Field[] var27 = var18;
            int var30 = var18.length;
            int var32 = 0;

            while(true) {
               if (var32 >= var30) {
                  var42 = null;
                  break;
               }

               Field $this$flatMapTo$iv$iv = var27[var32];
               int $i$a$-find-Utils$modelBlockAndLoc$3 = 0;
               Intrinsics.checkExpressionValueIsNotNull($this$flatMapTo$iv$iv, "it");
               if ($this$flatMapTo$iv$iv.getType().isInstance(IModel.class)) {
                  var42 = $this$flatMapTo$iv$iv;
                  break;
               }

               ++var32;
            }

            if (var42 != null) {
               Field var20 = var42;
               var22 = false;
               boolean var25 = false;
               int $i$a$-let-Utils$modelBlockAndLoc$4 = 0;
               var20.setAccessible(true);
               Object var43 = var20.get($this$modelBlockAndLoc);
               if (var43 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type net.minecraftforge.client.model.IModel");
               }

               return getModelBlockAndLoc((IModel)var43);
            }
         }

         boolean var19 = false;
         return CollectionsKt.emptyList();
      }
   }

   public static final boolean derivesFrom(@NotNull Pair<? extends ModelBlock, ? extends ResourceLocation> $this$derivesFrom, @NotNull ResourceLocation targetLocation) {
      Intrinsics.checkParameterIsNotNull($this$derivesFrom, "$this$derivesFrom");
      Intrinsics.checkParameterIsNotNull(targetLocation, "targetLocation");
      ResourceLocation $this$stripStart$iv = (ResourceLocation)$this$derivesFrom.getSecond();
      String str$iv = "models/";
      int $i$f$stripStart = 0;
      String var10000 = $this$stripStart$iv.getResourceDomain();
      String var10001 = $this$stripStart$iv.getResourcePath();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "path");
      String $this$stripStart$iv$iv = var10001;
      String var6 = var10000;
      int $i$f$stripStart = 0;
      if (StringsKt.startsWith$default($this$stripStart$iv$iv, str$iv, false, 2, (Object)null)) {
         int var11 = str$iv.length();
         boolean var12 = false;
         if ($this$stripStart$iv$iv == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
         }

         var10000 = $this$stripStart$iv$iv.substring(var11);
         Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).substring(startIndex)");
      } else {
         var10000 = $this$stripStart$iv$iv;
      }

      String var13 = var10000;
      if (Intrinsics.areEqual(new ResourceLocation(var6, var13), targetLocation)) {
         return true;
      } else if (((ModelBlock)$this$derivesFrom.getFirst()).parent != null && ((ModelBlock)$this$derivesFrom.getFirst()).getParentLocation() != null) {
         Pair var17 = new Pair;
         ModelBlock var10002 = ((ModelBlock)$this$derivesFrom.getFirst()).parent;
         ResourceLocation var10003 = ((ModelBlock)$this$derivesFrom.getFirst()).getParentLocation();
         if (var10003 == null) {
            Intrinsics.throwNpe();
         }

         var17.<init>(var10002, var10003);
         return derivesFrom(var17, targetLocation);
      } else {
         return false;
      }
   }
}
