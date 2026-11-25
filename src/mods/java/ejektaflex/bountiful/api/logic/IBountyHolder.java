package ejektaflex.bountiful.api.logic;

import ejektaflex.bountiful.api.block.ITileEntityBountyBoard;
import kotlin.Metadata;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\bf\u0018\u00002\u00020\u0001J\u001a\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH&J\u0010\u0010\f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH&J\u001a\u0010\r\u001a\u00020\u000e2\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH&R\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u00a8\u0006\u000f"},
   d2 = {"Lejektaflex/bountiful/api/logic/IBountyHolder;", "", "handler", "Lnet/minecraftforge/items/ItemStackHandler;", "getHandler", "()Lnet/minecraftforge/items/ItemStackHandler;", "addSingleBounty", "", "world", "Lnet/minecraft/world/World;", "te", "Lejektaflex/bountiful/api/block/ITileEntityBountyBoard;", "tickBounties", "update", "", "Bountiful_api"}
)
public interface IBountyHolder {
   @NotNull
   ItemStackHandler getHandler();

   void tickBounties(@NotNull World var1);

   boolean update(@NotNull World var1, @Nullable ITileEntityBountyBoard var2);

   void addSingleBounty(@NotNull World var1, @Nullable ITileEntityBountyBoard var2);
}
