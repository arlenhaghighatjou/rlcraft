package ejektaflex.bountiful.api;

import ejektaflex.bountiful.api.block.ITileEntityBountyBoard;
import ejektaflex.bountiful.api.config.IBountifulConfig;
import ejektaflex.bountiful.api.data.IBountyData;
import ejektaflex.bountiful.api.enum.EnumBountyRarity;
import ejektaflex.bountiful.api.logic.pickable.PickableEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack;
import ejektaflex.bountiful.api.registry.IValueRegistry;
import java.util.List;
import kotlin.Metadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H&J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000fH&J \u0010\u0017\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u00182\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001aH&J\u001a\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001aH&J\u0010\u0010\u001d\u001a\u00020\u000f2\u0006\u0010\u001e\u001a\u00020\u0015H&R\u0018\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u0012\u0010\u0007\u001a\u00020\bX\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0018\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u0006\u00a8\u0006\u001f"},
   d2 = {"Lejektaflex/bountiful/api/IBountifulAPI;", "", "bountyRegistry", "Lejektaflex/bountiful/api/registry/IValueRegistry;", "Lejektaflex/bountiful/api/logic/pickable/PickableEntry;", "getBountyRegistry", "()Lejektaflex/bountiful/api/registry/IValueRegistry;", "config", "Lejektaflex/bountiful/api/config/IBountifulConfig;", "getConfig", "()Lejektaflex/bountiful/api/config/IBountifulConfig;", "rewardRegistry", "Lejektaflex/bountiful/api/logic/picked/PickedEntryStack;", "getRewardRegistry", "createBountyData", "Lejektaflex/bountiful/api/data/IBountyData;", "worldIn", "Lnet/minecraft/world/World;", "rarity", "Lejektaflex/bountiful/api/enum/EnumBountyRarity;", "dataToStack", "Lnet/minecraft/item/ItemStack;", "data", "getBountiesAt", "", "pos", "Lnet/minecraft/util/math/BlockPos;", "getBountyBoard", "Lejektaflex/bountiful/api/block/ITileEntityBountyBoard;", "toBountyData", "stack", "Bountiful_api"}
)
public interface IBountifulAPI {
   @NotNull
   IValueRegistry<PickableEntry> getBountyRegistry();

   @NotNull
   IValueRegistry<PickedEntryStack> getRewardRegistry();

   @Nullable
   ITileEntityBountyBoard getBountyBoard(@NotNull World var1, @NotNull BlockPos var2);

   @Nullable
   List<ItemStack> getBountiesAt(@NotNull World var1, @NotNull BlockPos var2);

   @NotNull
   IBountyData toBountyData(@NotNull ItemStack var1);

   @NotNull
   ItemStack dataToStack(@NotNull IBountyData var1);

   @NotNull
   IBountifulConfig getConfig();

   @Nullable
   IBountyData createBountyData(@NotNull World var1, @Nullable EnumBountyRarity var2);
}
