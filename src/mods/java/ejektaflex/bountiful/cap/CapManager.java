package ejektaflex.bountiful.cap;

import kotlin.Metadata;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u001e\u0010\u0003\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u00048\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"},
   d2 = {"Lejektaflex/bountiful/cap/CapManager;", "", "()V", "CAP_BOARD", "Lnet/minecraftforge/common/capabilities/Capability;", "Lejektaflex/bountiful/cap/IGlobalBoard;", "getCAP_BOARD", "()Lnet/minecraftforge/common/capabilities/Capability;", "Bountiful"}
)
public final class CapManager {
   @CapabilityInject(IGlobalBoard.class)
   @Nullable
   private static final Capability<IGlobalBoard> CAP_BOARD;
   public static final CapManager INSTANCE;

   @Nullable
   public final Capability<IGlobalBoard> getCAP_BOARD() {
      return CAP_BOARD;
   }

   private CapManager() {
   }

   static {
      CapManager var0 = new CapManager();
      INSTANCE = var0;
   }
}
