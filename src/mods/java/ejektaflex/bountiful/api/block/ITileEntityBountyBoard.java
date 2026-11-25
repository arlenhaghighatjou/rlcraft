package ejektaflex.bountiful.api.block;

import ejektaflex.bountiful.api.logic.IBountyHolder;
import kotlin.Metadata;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\f\u001a\u00020\rH&J\b\u0010\u000e\u001a\u00020\u000fH&R\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0018\u0010\u0006\u001a\u00020\u0007X\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000b\u00a8\u0006\u0010"},
   d2 = {"Lejektaflex/bountiful/api/block/ITileEntityBountyBoard;", "Lnet/minecraft/util/ITickable;", "inventory", "Lejektaflex/bountiful/api/logic/IBountyHolder;", "getInventory", "()Lejektaflex/bountiful/api/logic/IBountyHolder;", "newBoard", "", "getNewBoard", "()Z", "setNewBoard", "(Z)V", "getBoardBlockPos", "Lnet/minecraft/util/math/BlockPos;", "sendRedstonePulse", "", "Bountiful_api"}
)
public interface ITileEntityBountyBoard extends ITickable {
   @NotNull
   IBountyHolder getInventory();

   boolean getNewBoard();

   void setNewBoard(boolean var1);

   void sendRedstonePulse();

   @NotNull
   BlockPos getBoardBlockPos();
}
