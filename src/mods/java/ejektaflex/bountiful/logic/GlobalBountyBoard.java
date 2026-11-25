package ejektaflex.bountiful.logic;

import kotlin.Metadata;
import net.minecraftforge.items.ItemStackHandler;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
   d2 = {"Lejektaflex/bountiful/logic/GlobalBountyBoard;", "Lejektaflex/bountiful/logic/BountyHolder;", "()V", "Bountiful"}
)
public final class GlobalBountyBoard extends BountyHolder {
   public static final GlobalBountyBoard INSTANCE;

   private GlobalBountyBoard() {
      super(new ItemStackHandler(27));
   }

   static {
      GlobalBountyBoard var0 = new GlobalBountyBoard();
      INSTANCE = var0;
   }
}
