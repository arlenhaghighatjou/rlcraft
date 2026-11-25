package mods.betterfoliage.client;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.chunk.ChunkOverlayManager;
import mods.betterfoliage.client.integration.ForestryIntegration;
import mods.betterfoliage.client.integration.IC2RubberIntegration;
import mods.betterfoliage.client.integration.OptifineCustomColors;
import mods.betterfoliage.client.integration.ShadersModIntegration;
import mods.betterfoliage.client.integration.TechRebornRubberIntegration;
import mods.betterfoliage.client.render.LeafWindTracker;
import mods.betterfoliage.client.render.LogRegistry;
import mods.betterfoliage.client.render.RenderAlgae;
import mods.betterfoliage.client.render.RenderCactus;
import mods.betterfoliage.client.render.RenderConnectedGrass;
import mods.betterfoliage.client.render.RenderConnectedGrassLog;
import mods.betterfoliage.client.render.RenderCoral;
import mods.betterfoliage.client.render.RenderGrass;
import mods.betterfoliage.client.render.RenderLeaves;
import mods.betterfoliage.client.render.RenderLilypad;
import mods.betterfoliage.client.render.RenderLog;
import mods.betterfoliage.client.render.RenderMycelium;
import mods.betterfoliage.client.render.RenderNetherrack;
import mods.betterfoliage.client.render.RenderReeds;
import mods.betterfoliage.client.render.RisingSoulTextures;
import mods.betterfoliage.client.render.StandardCactusRegistry;
import mods.betterfoliage.client.render.StandardLogRegistry;
import mods.betterfoliage.client.texture.GrassGenerator;
import mods.betterfoliage.client.texture.GrassRegistry;
import mods.betterfoliage.client.texture.LeafGenerator;
import mods.betterfoliage.client.texture.LeafParticleRegistry;
import mods.betterfoliage.client.texture.LeafRegistry;
import mods.betterfoliage.client.texture.StandardGrassRegistry;
import mods.betterfoliage.client.texture.StandardLeafRegistry;
import mods.octarinecore.client.KeyHandler;
import mods.octarinecore.client.gui.Utils;
import mods.octarinecore.client.render.AbstractBlockRenderingHandler;
import mods.octarinecore.client.resource.CenteringTextureGenerator;
import mods.octarinecore.client.resource.GeneratorBase;
import mods.octarinecore.client.resource.GeneratorPack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u001f\u001a\u00020 J\u0016\u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%J\u000e\u0010&\u001a\u00020 2\u0006\u0010$\u001a\u00020%J\u0016\u0010'\u001a\u00020 2\u0006\u0010(\u001a\u00020\u001c2\u0006\u0010)\u001a\u00020*R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R \u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001e\u00a8\u0006+"},
   d2 = {"Lmods/betterfoliage/client/Client;", "", "()V", "genGrass", "Lmods/betterfoliage/client/texture/GrassGenerator;", "getGenGrass", "()Lmods/betterfoliage/client/texture/GrassGenerator;", "genLeaves", "Lmods/betterfoliage/client/texture/LeafGenerator;", "getGenLeaves", "()Lmods/betterfoliage/client/texture/LeafGenerator;", "genReeds", "Lmods/octarinecore/client/resource/CenteringTextureGenerator;", "getGenReeds", "()Lmods/octarinecore/client/resource/CenteringTextureGenerator;", "generatorPack", "Lmods/octarinecore/client/resource/GeneratorPack;", "getGeneratorPack", "()Lmods/octarinecore/client/resource/GeneratorPack;", "renderers", "", "Lmods/octarinecore/client/render/AbstractBlockRenderingHandler;", "getRenderers", "()Ljava/util/List;", "setRenderers", "(Ljava/util/List;)V", "suppressRenderErrors", "", "Lnet/minecraft/block/state/IBlockState;", "getSuppressRenderErrors", "()Ljava/util/Set;", "init", "", "log", "level", "Lorg/apache/logging/log4j/Level;", "msg", "", "logDetail", "logRenderError", "state", "location", "Lnet/minecraft/util/math/BlockPos;", "BetterFoliage-MC1.12"}
)
public final class Client {
   @NotNull
   public static List<? extends AbstractBlockRenderingHandler> renderers;
   @NotNull
   private static final Set<IBlockState> suppressRenderErrors = (Set)(new LinkedHashSet());
   @NotNull
   private static final GrassGenerator genGrass = new GrassGenerator("bf_gen_grass");
   @NotNull
   private static final LeafGenerator genLeaves = new LeafGenerator("bf_gen_leaves");
   @NotNull
   private static final CenteringTextureGenerator genReeds = new CenteringTextureGenerator("bf_gen_reeds", 1, 2);
   @NotNull
   private static final GeneratorPack generatorPack = new GeneratorPack("Better Foliage generated", new GeneratorBase[]{genGrass, genLeaves, genReeds});
   public static final Client INSTANCE;

   @NotNull
   public final List<AbstractBlockRenderingHandler> getRenderers() {
      List var10000 = renderers;
      if (renderers == null) {
         Intrinsics.throwUninitializedPropertyAccessException("renderers");
      }

      return var10000;
   }

   public final void setRenderers(@NotNull List<? extends AbstractBlockRenderingHandler> var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      renderers = <set-?>;
   }

   @NotNull
   public final Set<IBlockState> getSuppressRenderErrors() {
      return suppressRenderErrors;
   }

   @NotNull
   public final GrassGenerator getGenGrass() {
      return genGrass;
   }

   @NotNull
   public final LeafGenerator getGenLeaves() {
      return genLeaves;
   }

   @NotNull
   public final CenteringTextureGenerator getGenReeds() {
      return genReeds;
   }

   @NotNull
   public final GeneratorPack getGeneratorPack() {
      return generatorPack;
   }

   public final void init() {
      renderers = CollectionsKt.listOf(new AbstractBlockRenderingHandler[]{new RenderGrass(), new RenderMycelium(), new RenderLeaves(), new RenderCactus(), new RenderLilypad(), new RenderReeds(), new RenderAlgae(), new RenderCoral(), new RenderLog(), new RenderNetherrack(), new RenderConnectedGrass(), new RenderConnectedGrassLog()});
      List singletons = CollectionsKt.listOf(new Object[]{StandardCactusRegistry.INSTANCE, LeafParticleRegistry.INSTANCE, ChunkOverlayManager.INSTANCE, LeafWindTracker.INSTANCE, RisingSoulTextures.INSTANCE});
      List integrations = CollectionsKt.listOf(new Object[]{ShadersModIntegration.INSTANCE, OptifineCustomColors.INSTANCE, ForestryIntegration.INSTANCE, IC2RubberIntegration.INSTANCE, TechRebornRubberIntegration.INSTANCE});
      GrassRegistry.INSTANCE.addRegistry(StandardGrassRegistry.INSTANCE);
      LeafRegistry.INSTANCE.addRegistry(StandardLeafRegistry.INSTANCE);
      LogRegistry.INSTANCE.addRegistry(StandardLogRegistry.INSTANCE);
      new KeyHandler("Better Foliage", 66, "key.betterfoliage.gui", null.INSTANCE);
   }

   public final void log(@NotNull Level level, @NotNull String msg) {
      Intrinsics.checkParameterIsNotNull(level, "level");
      Intrinsics.checkParameterIsNotNull(msg, "msg");
      BetterFoliageMod.INSTANCE.getLog().log(level, "[BetterFoliage] " + msg);
      BetterFoliageMod.INSTANCE.getLogDetail().log(level, msg);
   }

   public final void logDetail(@NotNull String msg) {
      Intrinsics.checkParameterIsNotNull(msg, "msg");
      BetterFoliageMod.INSTANCE.getLogDetail().log(Level.DEBUG, msg);
   }

   public final void logRenderError(@NotNull IBlockState state, @NotNull BlockPos location) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(location, "location");
      if (!suppressRenderErrors.contains(state)) {
         suppressRenderErrors.add(state);
         String var10000 = ((ResourceLocation)Block.REGISTRY.getNameForObject(state.getBlock())).toString();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "Block.REGISTRY.getNameFo\u2026t(state.block).toString()");
         String blockName = var10000;
         String blockLoc = "" + location.getX() + ',' + location.getY() + ',' + location.getZ();
         GuiIngame var5 = Minecraft.getMinecraft().ingameGUI;
         Intrinsics.checkExpressionValueIsNotNull(var5, "Minecraft.getMinecraft().ingameGUI");
         var5.getChatGUI().printChatMessage((ITextComponent)(new TextComponentTranslation("betterfoliage.rendererror", new Object[]{Utils.textComponent(blockName, TextFormatting.GOLD), Utils.textComponent(blockLoc, TextFormatting.GOLD)})));
         this.logDetail("Error rendering block " + state + " at " + blockLoc);
      }
   }

   private Client() {
   }

   static {
      Client var0 = new Client();
      INSTANCE = var0;
      boolean var1 = false;
   }
}
