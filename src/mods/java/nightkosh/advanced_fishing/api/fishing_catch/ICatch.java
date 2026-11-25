package nightkosh.advanced_fishing.api.fishing_catch;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface ICatch {
   List<ItemStack> getCatch(World var1, BlockPos var2, float var3);
}
