package ejektaflex.bountiful.api.item;

import ejektaflex.bountiful.api.data.IBountyData;
import ejektaflex.bountiful.api.enum.EnumBountyRarity;
import kotlin.Metadata;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\bf\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001J$\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\nH&J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u0006H&J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u0006H&J\u0010\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\u0010"},
   d2 = {"Lejektaflex/bountiful/api/item/IItemBounty;", "Lnet/minecraftforge/registries/IForgeRegistryEntry;", "Lnet/minecraft/item/Item;", "ensureBounty", "", "stack", "Lnet/minecraft/item/ItemStack;", "worldIn", "Lnet/minecraft/world/World;", "rarity", "Lejektaflex/bountiful/api/enum/EnumBountyRarity;", "getBountyData", "Lejektaflex/bountiful/api/data/IBountyData;", "tickBoardTime", "", "tryExpireBountyTime", "Bountiful_api"}
)
public interface IItemBounty extends IForgeRegistryEntry<Item> {
   @NotNull
   IBountyData getBountyData(@NotNull ItemStack var1);

   void tryExpireBountyTime(@NotNull ItemStack var1);

   boolean tickBoardTime(@NotNull ItemStack var1);

   void ensureBounty(@NotNull ItemStack var1, @NotNull World var2, @Nullable EnumBountyRarity var3);

   @Metadata(
      mv = {1, 1, 13},
      bv = {1, 0, 3},
      k = 3
   )
   public static final class DefaultImpls {
   }
}
