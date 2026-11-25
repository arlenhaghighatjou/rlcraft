package mods.betterfoliage.client.render;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.render.column.ColumnTextureInfo;
import mods.betterfoliage.client.render.column.SimpleColumnInfo;
import mods.octarinecore.client.resource.ModelRenderRegistryConfigurable;
import mods.octarinecore.common.config.ModelTextureList;
import mods.octarinecore.common.config.SimpleBlockMatcher;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bÆ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J\u001e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\rH\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0017"},
   d2 = {"Lmods/betterfoliage/client/render/StandardCactusRegistry;", "Lmods/octarinecore/client/resource/ModelRenderRegistryConfigurable;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "()V", "logger", "Lorg/apache/logging/log4j/Logger;", "getLogger", "()Lorg/apache/logging/log4j/Logger;", "matchClasses", "Lmods/octarinecore/common/config/SimpleBlockMatcher;", "getMatchClasses", "()Lmods/octarinecore/common/config/SimpleBlockMatcher;", "modelTextures", "", "Lmods/octarinecore/common/config/ModelTextureList;", "getModelTextures", "()Ljava/util/List;", "processModel", "Lmods/betterfoliage/client/render/column/SimpleColumnInfo$Key;", "state", "Lnet/minecraft/block/state/IBlockState;", "textures", "", "BetterFoliage-MC1.12"}
)
public final class StandardCactusRegistry extends ModelRenderRegistryConfigurable<ColumnTextureInfo> {
   @NotNull
   private static final Logger logger = BetterFoliageMod.INSTANCE.getLogDetail();
   @NotNull
   private static final SimpleBlockMatcher matchClasses = new SimpleBlockMatcher(new Class[]{BlockCactus.class});
   @NotNull
   private static final List<ModelTextureList> modelTextures = CollectionsKt.listOf(new ModelTextureList(new String[]{"block/cactus", "top", "bottom", "side"}));
   public static final StandardCactusRegistry INSTANCE;

   @NotNull
   public Logger getLogger() {
      return logger;
   }

   @NotNull
   public SimpleBlockMatcher getMatchClasses() {
      return matchClasses;
   }

   @NotNull
   public List<ModelTextureList> getModelTextures() {
      return modelTextures;
   }

   @NotNull
   public SimpleColumnInfo.Key processModel(@NotNull IBlockState state, @NotNull List<String> textures) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(textures, "textures");
      return new SimpleColumnInfo.Key(this.getLogger(), Axis.Y, textures);
   }

   private StandardCactusRegistry() {
   }

   static {
      StandardCactusRegistry var0 = new StandardCactusRegistry();
      INSTANCE = var0;
      MinecraftForge.EVENT_BUS.register(var0);
   }
}
