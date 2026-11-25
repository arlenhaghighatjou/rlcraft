package mods.betterfoliage.client.integration;

import java.lang.reflect.Field;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.Client;
import mods.betterfoliage.loader.Refs;
import mods.octarinecore.ThreadLocalDelegate;
import mods.octarinecore.Utils;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.metaprog.Reflection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\bÇ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\tR\u001b\u0010\n\u001a\u00020\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\r¨\u0006\u0014"},
   d2 = {"Lmods/betterfoliage/client/integration/OptifineCustomColors;", "", "()V", "fakeQuad", "Lnet/minecraft/client/renderer/block/model/BakedQuad;", "getFakeQuad", "()Lnet/minecraft/client/renderer/block/model/BakedQuad;", "isColorAvailable", "", "()Z", "renderEnv", "Lmods/betterfoliage/client/integration/OptifineRenderEnv;", "getRenderEnv", "()Lmods/betterfoliage/client/integration/OptifineRenderEnv;", "renderEnv$delegate", "Lmods/octarinecore/ThreadLocalDelegate;", "getBlockColor", "", "ctx", "Lmods/octarinecore/client/render/BlockContext;", "BetterFoliage-MC1.12"}
)
public final class OptifineCustomColors {
   private static final boolean isColorAvailable = Reflection.allAvailable(Refs.INSTANCE.getCustomColors(), Refs.INSTANCE.getGetColorMultiplier());
   @NotNull
   private static final ThreadLocalDelegate renderEnv$delegate = new ThreadLocalDelegate(null.INSTANCE);
   @NotNull
   private static final BakedQuad fakeQuad = new BakedQuad(new int[0], 1, EnumFacing.UP, (EaglerTextureAtlasSprite)null, true, DefaultVertexFormats.BLOCK);
   public static final OptifineCustomColors INSTANCE;

   static {
      OptifineCustomColors var0 = new OptifineCustomColors();
      INSTANCE = var0;
      Client var10000 = Client.INSTANCE;
      Level var10001 = Level.INFO;
      Intrinsics.checkExpressionValueIsNotNull(Level.INFO, "Level.INFO");
      var10000.log(var10001, "Optifine custom color support is " + (isColorAvailable ? "enabled" : "disabled"));
   }

   public final boolean isColorAvailable() {
      return isColorAvailable;
   }

   @NotNull
   public final OptifineRenderEnv getRenderEnv() {
      return (OptifineRenderEnv)renderEnv$delegate.getValue(this, $$delegatedProperties[0]);
   }

   @NotNull
   public final BakedQuad getFakeQuad() {
      return fakeQuad;
   }

   public final int getBlockColor(@NotNull BlockContext ctx) {
      // $FF: Couldn't be decompiled
   }

   private OptifineCustomColors() {
   }
}
