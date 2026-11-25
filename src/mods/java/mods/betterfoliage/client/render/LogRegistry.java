package mods.betterfoliage.client.render;

import kotlin.Metadata;
import mods.betterfoliage.client.render.column.ColumnTextureInfo;
import mods.octarinecore.client.resource.ModelRenderRegistryRoot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÇ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003¨\u0006\u0004"},
   d2 = {"Lmods/betterfoliage/client/render/LogRegistry;", "Lmods/octarinecore/client/resource/ModelRenderRegistryRoot;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "()V", "BetterFoliage-MC1.12"}
)
public final class LogRegistry extends ModelRenderRegistryRoot<ColumnTextureInfo> {
   public static final LogRegistry INSTANCE;

   private LogRegistry() {
   }

   static {
      LogRegistry var0 = new LogRegistry();
      INSTANCE = var0;
   }
}
