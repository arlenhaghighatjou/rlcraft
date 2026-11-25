package ejektaflex.bountiful.api.logic;

import ejektaflex.bountiful.api.data.IBountyData;
import ejektaflex.bountiful.api.enum.EnumBountyRarity;
import kotlin.Metadata;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u001e\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003H&J\u001e\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003H&¨\u0006\f"},
   d2 = {"Lejektaflex/bountiful/api/logic/IBountyCreator;", "", "calcRarity", "Lejektaflex/bountiful/api/enum/EnumBountyRarity;", "create", "Lejektaflex/bountiful/api/data/IBountyData;", "world", "Lnet/minecraft/world/World;", "inRarity", "createStack", "Lnet/minecraft/item/ItemStack;", "rarity", "Bountiful_api"}
)
public interface IBountyCreator {
   @Nullable
   ItemStack createStack(@NotNull World var1, @Nullable EnumBountyRarity var2);

   @NotNull
   EnumBountyRarity calcRarity();

   @Nullable
   IBountyData create(@NotNull World var1, @Nullable EnumBountyRarity var2);

   @Metadata(
      mv = {1, 1, 13},
      bv = {1, 0, 3},
      k = 3
   )
   public static final class DefaultImpls {
   }
}
