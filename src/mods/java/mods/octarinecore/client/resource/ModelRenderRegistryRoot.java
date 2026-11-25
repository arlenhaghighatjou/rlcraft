package mods.octarinecore.client.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.client.render.BlockContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0014\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00028\u00000\u0002J(\u0010\u000b\u001a\u0004\u0018\u00018\u00002\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0096\u0002\u00a2\u0006\u0002\u0010\u0012R\u001d\u0010\u0004\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0013"},
   d2 = {"Lmods/octarinecore/client/resource/ModelRenderRegistryRoot;", "T", "Lmods/octarinecore/client/resource/ModelRenderRegistry;", "()V", "subRegistries", "", "getSubRegistries", "()Ljava/util/List;", "addRegistry", "", "registry", "get", "state", "Lnet/minecraft/block/state/IBlockState;", "world", "Lnet/minecraft/world/IBlockAccess;", "pos", "Lnet/minecraft/util/math/BlockPos;", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Object;", "BetterFoliage-MC1.12"}
)
public abstract class ModelRenderRegistryRoot<T> implements ModelRenderRegistry<T> {
   @NotNull
   private final List<ModelRenderRegistry<T>> subRegistries;

   @NotNull
   public final List<ModelRenderRegistry<T>> getSubRegistries() {
      return this.subRegistries;
   }

   @Nullable
   public T get(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      Iterable $this$findFirst$iv = (Iterable)this.subRegistries;
      int $i$f$findFirst = 0;
      int $i$f$forEach = 0;
      Iterator var8 = $this$findFirst$iv.iterator();

      Object var19;
      while(true) {
         if (!var8.hasNext()) {
            var19 = null;
            break;
         }

         Object element$iv$iv = var8.next();
         int $i$a$-forEach-Utils$findFirst$1 = 0;
         ModelRenderRegistry it = (ModelRenderRegistry)element$iv$iv;
         int $i$a$-findFirst-ModelRenderRegistryRoot$get$1 = 0;
         var19 = it.get(state, world, pos);
         if (var19 != null) {
            Object var14 = var19;
            boolean var15 = false;
            boolean var16 = false;
            int $i$a$-let-Utils$findFirst$1$1 = 0;
            var19 = var14;
            break;
         }
      }

      return (T)var19;
   }

   public final void addRegistry(@NotNull ModelRenderRegistry<T> registry) {
      Intrinsics.checkParameterIsNotNull(registry, "registry");
      this.subRegistries.add(registry);
      MinecraftForge.EVENT_BUS.register(registry);
   }

   public ModelRenderRegistryRoot() {
      boolean var1 = false;
      List var3 = (List)(new ArrayList());
      this.subRegistries = var3;
   }

   @Nullable
   public T get(@NotNull BlockContext ctx) {
      Intrinsics.checkParameterIsNotNull(ctx, "ctx");
      return (T)ModelRenderRegistry.DefaultImpls.get(this, ctx);
   }
}
