package mods.octarinecore.client.resource;

import java.util.Arrays;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\u0013\u0010\u001a\u001a\u0004\u0018\u00010\n2\u0006\u0010\u001b\u001a\u00020\u0010H\u0086\u0002J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0010\u0010 \u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0016R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t\u00a2\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0007R\u001a\u0010\u000f\u001a\u00020\u0010X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001b\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\t\u00a2\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006!"},
   d2 = {"Lmods/octarinecore/client/resource/IconSet;", "Lmods/octarinecore/client/resource/IStitchListener;", "domain", "", "namePattern", "(Ljava/lang/String;Ljava/lang/String;)V", "getDomain", "()Ljava/lang/String;", "icons", "", "Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "getIcons", "()[Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "[Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "getNamePattern", "num", "", "getNum", "()I", "setNum", "(I)V", "resources", "Lnet/minecraft/util/ResourceLocation;", "getResources", "()[Lnet/minecraft/util/ResourceLocation;", "[Lnet/minecraft/util/ResourceLocation;", "get", "idx", "onPostStitch", "", "atlas", "Lnet/minecraft/client/renderer/texture/TextureMap;", "onPreStitch", "BetterFoliage-MC1.12"}
)
public final class IconSet implements IStitchListener {
   @NotNull
   private final ResourceLocation[] resources;
   @NotNull
   private final EaglerTextureAtlasSprite[] icons;
   private int num;
   @NotNull
   private final String domain;
   @NotNull
   private final String namePattern;

   @NotNull
   public final ResourceLocation[] getResources() {
      return this.resources;
   }

   @NotNull
   public final EaglerTextureAtlasSprite[] getIcons() {
      return this.icons;
   }

   public final int getNum() {
      return this.num;
   }

   public final void setNum(int var1) {
      this.num = <set-?>;
   }

   public void onPreStitch(@NotNull TextureMap atlas) {
      Intrinsics.checkParameterIsNotNull(atlas, "atlas");
      this.num = 0;
      byte $this$forEach$iv = 0;
      Iterable $this$forEach$iv = (Iterable)(new IntRange($this$forEach$iv, 15));
      int $i$f$forEach = 0;
      Iterator var4 = $this$forEach$iv.iterator();

      while(var4.hasNext()) {
         int element$iv = ((IntIterator)var4).nextInt();
         int $i$a$-forEach-IconSet$onPreStitch$1 = 0;
         this.icons[element$iv] = (EaglerTextureAtlasSprite)null;
         String var10000 = this.domain;
         StringBuilder var10001 = (new StringBuilder()).append("textures/");
         String var8 = this.namePattern;
         Object[] var9 = new Object[]{element$iv};
         StringBuilder var10 = var10001;
         String var11 = var10000;
         boolean var14 = false;
         var10000 = String.format(var8, Arrays.copyOf(var9, var9.length));
         Intrinsics.checkExpressionValueIsNotNull(var10000, "java.lang.String.format(this, *args)");
         String var15 = var10000;
         String var20 = var10.append(var15).append(".png").toString();
         ResourceLocation locReal = new ResourceLocation(var11, var20);
         if (Utils.get((IResourceManager)Utils.getResourceManager(), locReal) != null) {
            ResourceLocation[] var34 = this.resources;
            int var25 = this.num++;
            int var36 = var25;
            String var10002 = this.domain;
            String var26 = this.namePattern;
            var9 = new Object[]{element$iv};
            var15 = var10002;
            int var12 = var36;
            ResourceLocation[] var13 = var34;
            var14 = false;
            String var35 = String.format(var26, Arrays.copyOf(var9, var9.length));
            Intrinsics.checkExpressionValueIsNotNull(var35, "java.lang.String.format(this, *args)");
            String var17 = var35;
            ResourceLocation var27 = new ResourceLocation(var15, var17);
            boolean var29 = false;
            var14 = false;
            int $i$a$-apply-IconSet$onPreStitch$1$1 = 0;
            atlas.registerSprite(var27);
            var13[var12] = var27;
         }
      }

   }

   public void onPostStitch(@NotNull TextureMap atlas) {
      Intrinsics.checkParameterIsNotNull(atlas, "atlas");
      Iterable $this$forEach$iv = (Iterable)RangesKt.until(0, this.num);
      int $i$f$forEach = 0;

      int element$iv;
      EaglerTextureAtlasSprite[] var10000;
      ResourceLocation var10003;
      for(Iterator var4 = $this$forEach$iv.iterator(); var4.hasNext(); var10000[element$iv] = Utils.get(atlas, var10003)) {
         element$iv = ((IntIterator)var4).nextInt();
         int $i$a$-forEach-IconSet$onPostStitch$1 = 0;
         var10000 = this.icons;
         var10003 = this.resources[element$iv];
         if (this.resources[element$iv] == null) {
            Intrinsics.throwNpe();
         }
      }

   }

   @Nullable
   public final EaglerTextureAtlasSprite get(int idx) {
      return this.num == 0 ? null : this.icons[idx % this.num];
   }

   @NotNull
   public final String getDomain() {
      return this.domain;
   }

   @NotNull
   public final String getNamePattern() {
      return this.namePattern;
   }

   public IconSet(@NotNull String domain, @NotNull String namePattern) {
      Intrinsics.checkParameterIsNotNull(domain, "domain");
      Intrinsics.checkParameterIsNotNull(namePattern, "namePattern");
      super();
      this.domain = domain;
      this.namePattern = namePattern;
      this.resources = new ResourceLocation[16];
      this.icons = new EaglerTextureAtlasSprite[16];
   }
}
