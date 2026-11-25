package mods.betterfoliage.client.texture;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.octarinecore.client.resource.ModelRenderRegistryConfigurable;
import mods.octarinecore.common.config.ConfigurableBlockMatcher;
import mods.octarinecore.common.config.ModelTextureList;
import net.minecraft.block.state.IBlockState;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u001e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\rH\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\t8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0017"},
   d2 = {"Lmods/betterfoliage/client/texture/StandardGrassRegistry;", "Lmods/octarinecore/client/resource/ModelRenderRegistryConfigurable;", "Lmods/betterfoliage/client/texture/GrassInfo;", "()V", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "matchClasses", "Lmods/octarinecore/common/config/ConfigurableBlockMatcher;", "getMatchClasses", "()Lmods/octarinecore/common/config/ConfigurableBlockMatcher;", "modelTextures", "", "Lmods/octarinecore/common/config/ModelTextureList;", "getModelTextures", "()Ljava/util/List;", "processModel", "Lmods/betterfoliage/client/texture/StandardGrassKey;", "state", "Lnet/minecraft/block/state/IBlockState;", "textures", "", "BetterFoliage-MC1.12"}
)
public final class StandardGrassRegistry extends ModelRenderRegistryConfigurable<GrassInfo> {
   @NotNull
   private static final Logger logger = BetterFoliageMod.INSTANCE.getLogDetail();
   public static final StandardGrassRegistry INSTANCE;

   @NotNull
   public Logger getLogger() {
      return logger;
   }

   @NotNull
   public ConfigurableBlockMatcher getMatchClasses() {
      return Config.blocks.INSTANCE.getGrassClasses();
   }

   @NotNull
   public List<ModelTextureList> getModelTextures() {
      return Config.blocks.INSTANCE.getGrassModels().getList();
   }

   @NotNull
   public StandardGrassKey processModel(@NotNull IBlockState state, @NotNull List<String> textures) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(textures, "textures");
      return new StandardGrassKey(this.getLogger(), (String)textures.get(0));
   }

   private StandardGrassRegistry() {
   }

   static {
      StandardGrassRegistry var0 = new StandardGrassRegistry();
      INSTANCE = var0;
   }
}
