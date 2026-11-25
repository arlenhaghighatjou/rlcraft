package ejektaflex.bountiful.proxy;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\tH\u0016¨\u0006\n"},
   d2 = {"Lejektaflex/bountiful/proxy/IProxy;", "", "init", "", "e", "Lnet/minecraftforge/fml/common/event/FMLInitializationEvent;", "postInit", "Lnet/minecraftforge/fml/common/event/FMLPostInitializationEvent;", "preInit", "Lnet/minecraftforge/fml/common/event/FMLPreInitializationEvent;", "Bountiful"}
)
public interface IProxy {
   void preInit(@NotNull FMLPreInitializationEvent var1);

   void init(@NotNull FMLInitializationEvent var1);

   void postInit(@NotNull FMLPostInitializationEvent var1);

   @Metadata(
      mv = {1, 1, 13},
      bv = {1, 0, 3},
      k = 3
   )
   public static final class DefaultImpls {
      public static void preInit(IProxy $this, @NotNull FMLPreInitializationEvent e) {
         Intrinsics.checkParameterIsNotNull(e, "e");
      }

      public static void init(IProxy $this, @NotNull FMLInitializationEvent e) {
         Intrinsics.checkParameterIsNotNull(e, "e");
      }

      public static void postInit(IProxy $this, @NotNull FMLPostInitializationEvent e) {
         Intrinsics.checkParameterIsNotNull(e, "e");
      }
   }
}
