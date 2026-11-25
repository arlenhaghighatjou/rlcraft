package mods.octarinecore.client.resource;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0016J\u0010\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001c\u0010\b\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0007¨\u0006\u0018"},
   d2 = {"Lmods/octarinecore/client/resource/IconHolder;", "Lmods/octarinecore/client/resource/IStitchListener;", "domain", "", "name", "(Ljava/lang/String;Ljava/lang/String;)V", "getDomain", "()Ljava/lang/String;", "icon", "Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "getIcon", "()Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;", "setIcon", "(Lnet/minecraft/client/renderer/texture/EaglerTextureAtlasSprite;)V", "iconRes", "Lnet/minecraft/util/ResourceLocation;", "getIconRes", "()Lnet/minecraft/util/ResourceLocation;", "getName", "onPostStitch", "", "atlas", "Lnet/minecraft/client/renderer/texture/TextureMap;", "onPreStitch", "BetterFoliage-MC1.12"}
)
public final class IconHolder implements IStitchListener {
   @NotNull
   private final ResourceLocation iconRes;
   @Nullable
   private EaglerTextureAtlasSprite icon;
   @NotNull
   private final String domain;
   @NotNull
   private final String name;

   @NotNull
   public final ResourceLocation getIconRes() {
      return this.iconRes;
   }

   @Nullable
   public final EaglerTextureAtlasSprite getIcon() {
      return this.icon;
   }

   public final void setIcon(@Nullable EaglerTextureAtlasSprite var1) {
      this.icon = <set-?>;
   }

   public void onPreStitch(@NotNull TextureMap atlas) {
      Intrinsics.checkParameterIsNotNull(atlas, "atlas");
      atlas.registerSprite(this.iconRes);
   }

   public void onPostStitch(@NotNull TextureMap atlas) {
      Intrinsics.checkParameterIsNotNull(atlas, "atlas");
      this.icon = Utils.get(atlas, this.iconRes);
   }

   @NotNull
   public final String getDomain() {
      return this.domain;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   public IconHolder(@NotNull String domain, @NotNull String name) {
      Intrinsics.checkParameterIsNotNull(domain, "domain");
      Intrinsics.checkParameterIsNotNull(name, "name");
      super();
      this.domain = domain;
      this.name = name;
      this.iconRes = new ResourceLocation(this.domain, this.name);
   }
}
