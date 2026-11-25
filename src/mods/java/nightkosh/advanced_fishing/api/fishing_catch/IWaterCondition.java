package nightkosh.advanced_fishing.api.fishing_catch;

import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;

@FunctionalInterface
public interface IWaterCondition {
   boolean shouldGetCatch(World var1, BlockPos var2, Biome var3, Set<Type> var4, float var5);
}
