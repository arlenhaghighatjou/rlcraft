package mods.betterfoliage.client.texture;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import mods.octarinecore.client.resource.ParameterList;
import mods.octarinecore.client.resource.ResourceType;
import mods.octarinecore.client.resource.TextureGenerator;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0018\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\r¨\u0006\u000e"},
   d2 = {"Lmods/betterfoliage/client/texture/LeafGenerator;", "Lmods/octarinecore/client/resource/TextureGenerator;", "domain", "", "(Ljava/lang/String;)V", "generate", "Ljava/awt/image/BufferedImage;", "params", "Lmods/octarinecore/client/resource/ParameterList;", "getLeafMask", "Lnet/minecraft/client/resources/IResource;", "type", "maxSize", "", "BetterFoliage-MC1.12"}
)
public final class LeafGenerator extends TextureGenerator {
   @Nullable
   public BufferedImage generate(@NotNull ParameterList params) {
      Intrinsics.checkParameterIsNotNull(params, "params");
      Pair var10000 = this.targetResource(params);
      if (var10000 == null) {
         Intrinsics.throwNpe();
      }

      Pair target = var10000;
      String var58 = params.get("type");
      if (var58 == null) {
         var58 = "default";
      }

      String leafType = var58;
      ResourceLocation baseTexture = (ResourceLocation)target.getSecond();
      String str$iv = "textures/";
      int $i$f$stripStart = 0;
      var58 = baseTexture.getResourceDomain();
      String var10001 = baseTexture.getResourcePath();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "path");
      String maskTexture = var10001;
      String $fun$scale$2 = var58;
      int $i$f$stripStart = 0;
      if (StringsKt.startsWith$default(maskTexture, str$iv, false, 2, (Object)null)) {
         int baseFrame = str$iv.length();
         boolean leafFrame = false;
         if (maskTexture == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
         }

         var58 = maskTexture.substring(baseFrame);
         Intrinsics.checkExpressionValueIsNotNull(var58, "(this as java.lang.String).substring(startIndex)");
      } else {
         var58 = maskTexture;
      }

      String x = var58;
      baseTexture = new ResourceLocation($fun$scale$2, x);
      str$iv = "blocks/";
      $i$f$stripStart = 0;
      var58 = baseTexture.getResourceDomain();
      var10001 = baseTexture.getResourcePath();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "path");
      maskTexture = var10001;
      $fun$scale$2 = var58;
      $i$f$stripStart = 0;
      if (StringsKt.startsWith$default(maskTexture, str$iv, false, 2, (Object)null)) {
         int var48 = str$iv.length();
         boolean var50 = false;
         if (maskTexture == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
         }

         var58 = maskTexture.substring(var48);
         Intrinsics.checkExpressionValueIsNotNull(var58, "(this as java.lang.String).substring(startIndex)");
      } else {
         var58 = maskTexture;
      }

      x = var58;
      baseTexture = new ResourceLocation($fun$scale$2, x);
      final int var33 = 0;
      $i$f$stripStart = 0;
      int $i$a$-let-LeafGenerator$generate$handDrawnLoc$1 = 0;
      ResourceLocation handDrawnLoc = new ResourceLocation("betterfoliage", baseTexture.getResourceDomain() + "/textures/blocks/" + baseTexture.getResourcePath());
      IResource var63 = mods.octarinecore.client.resource.Utils.get((IResourceManager)mods.octarinecore.client.resource.Utils.getResourceManager(), handDrawnLoc);
      if (var63 != null) {
         BufferedImage var64 = mods.octarinecore.client.resource.Utils.loadImage(var63);
         if (var64 != null) {
            BufferedImage var31 = var64;
            var33 = (boolean)0;
            $i$f$stripStart = 0;
            $i$a$-let-LeafGenerator$generate$handDrawnLoc$1 = 0;
            return var31;
         }
      }

      var63 = mods.octarinecore.client.resource.Utils.get((IResourceManager)mods.octarinecore.client.resource.Utils.getResourceManager(), (ResourceLocation)target.getSecond());
      if (var63 != null) {
         BufferedImage var66 = mods.octarinecore.client.resource.Utils.loadImage(var63);
         if (var66 != null) {
            BufferedImage baseTexture = var66;
            size = baseTexture.getWidth();
            $i$f$stripStart = baseTexture.getHeight() / var33;
            IResource var67 = this.getLeafMask(leafType, var33 * 2);
            if (var67 == null) {
               var67 = this.getLeafMask("default", var33 * 2);
            }

            final BufferedImage maskTexture = var67 != null ? mods.octarinecore.client.resource.Utils.loadImage(var67) : null;
            <undefinedtype> $fun$scale$2 = new Function1<Integer, Integer>() {
               public final int invoke(int i) {
                  BufferedImage var10001 = maskTexture;
                  if (maskTexture == null) {
                     Intrinsics.throwNpe();
                  }

                  return i * var10001.getWidth() / (var33 * 2);
               }
            };
            BufferedImage leafTexture = new BufferedImage(var33 * 2, var33 * 2 * $i$f$stripStart, 6);
            Graphics2D graphics = leafTexture.createGraphics();
            $i$f$stripStart = 0;
            int var13 = $i$f$stripStart - 1;
            if ($i$f$stripStart <= var13) {
               while(true) {
                  BufferedImage baseFrame = baseTexture.getSubimage(0, var33 * $i$f$stripStart, var33, var33);
                  BufferedImage leafFrame = new BufferedImage(var33 * 2, var33 * 2, 6);
                  Graphics2D var53 = leafFrame.createGraphics();
                  int var17 = 0;
                  int y = 0;
                  int $i$a$-apply-LeafGenerator$generate$3 = 0;
                  var53.drawImage((Image)baseFrame, 0, 0, (ImageObserver)null);
                  var53.drawImage((Image)baseFrame, 0, var33, (ImageObserver)null);
                  var53.drawImage((Image)baseFrame, var33, 0, (ImageObserver)null);
                  var53.drawImage((Image)baseFrame, var33, var33, (ImageObserver)null);
                  if ((ResourceType)target.getFirst() == ResourceType.COLOR && maskTexture != null) {
                     int var54 = 0;
                     var17 = var33 * 2 - 1;
                     if (var54 <= var17) {
                        while(true) {
                           y = 0;
                           int $this$apply = var33 * 2 - 1;
                           if (y <= $this$apply) {
                              while(true) {
                                 long basePixel = (long)mods.octarinecore.client.resource.Utils.get(leafFrame, var54, y) & 4294967295L;
                                 long maskPixel = (long)mods.octarinecore.client.resource.Utils.get(maskTexture, $fun$scale$2.invoke(var54), $fun$scale$2.invoke(y)) & 4278190080L | 16777215L;
                                 mods.octarinecore.client.resource.Utils.set(leafFrame, var54, y, (int)(basePixel & maskPixel));
                                 if (y == $this$apply) {
                                    break;
                                 }

                                 ++y;
                              }
                           }

                           if (var54 == var17) {
                              break;
                           }

                           ++var54;
                        }
                     }
                  }

                  graphics.drawImage((Image)leafFrame, 0, var33 * $i$f$stripStart * 2, (ImageObserver)null);
                  if ($i$f$stripStart == var13) {
                     break;
                  }

                  ++$i$f$stripStart;
               }
            }

            return leafTexture;
         }
      }

      return null;
   }

   @Nullable
   public final IResource getLeafMask(@NotNull final String type, int maxSize) {
      Intrinsics.checkParameterIsNotNull(type, "type");
      return this.getMultisizeTexture(maxSize, new Function1<Integer, ResourceLocation>() {
         @NotNull
         public final ResourceLocation invoke(int size) {
            return new ResourceLocation("betterfoliage", "textures/blocks/leafmask_" + size + '_' + type + ".png");
         }
      });
   }

   public LeafGenerator(@NotNull String domain) {
      Intrinsics.checkParameterIsNotNull(domain, "domain");
      super(domain);
   }
}
