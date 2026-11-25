package mods.betterfoliage.client.integration;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.Client;
import mods.betterfoliage.client.render.LogRegistry;
import mods.octarinecore.metaprog.ClassRef;
import mods.octarinecore.metaprog.Reflection;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"},
   d2 = {"Lmods/betterfoliage/client/integration/IC2RubberIntegration;", "", "()V", "BlockRubWood", "Lmods/octarinecore/metaprog/ClassRef;", "getBlockRubWood", "()Lmods/octarinecore/metaprog/ClassRef;", "BetterFoliage-MC1.12"}
)
public final class IC2RubberIntegration {
   @NotNull
   private static final ClassRef BlockRubWood = new ClassRef("ic2.core.block.BlockRubWood");
   public static final IC2RubberIntegration INSTANCE;

   @NotNull
   public final ClassRef getBlockRubWood() {
      return BlockRubWood;
   }

   private IC2RubberIntegration() {
   }

   static {
      IC2RubberIntegration var0 = new IC2RubberIntegration();
      INSTANCE = var0;
      if (Loader.isModLoaded("ic2") && Reflection.allAvailable(BlockRubWood)) {
         Client var10000 = Client.INSTANCE;
         Level var10001 = Level.INFO;
         Intrinsics.checkExpressionValueIsNotNull(Level.INFO, "Level.INFO");
         var10000.log(var10001, "IC2 rubber support initialized");
         LogRegistry.INSTANCE.addRegistry(IC2LogSupport.INSTANCE);
      }

   }
}
