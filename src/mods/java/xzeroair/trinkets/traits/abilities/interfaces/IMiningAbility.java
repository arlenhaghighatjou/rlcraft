package xzeroair.trinkets.traits.abilities.interfaces;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMiningAbility extends IAbilityInterface {
   default float breakingBlock(EntityLivingBase entity, IBlockState state, BlockPos pos, float originalSpeed, float newSpeed) {
      return newSpeed;
   }

   default int brokeBlock(EntityLivingBase entity, World world, IBlockState state, BlockPos pos, int expToDrop) {
      return expToDrop;
   }

   default float blockDrops(EntityLivingBase entity, World world, IBlockState state, BlockPos pos, List<ItemStack> drops, float dropChance, boolean silkTouching, int fortuneLevel) {
      return dropChance;
   }
}
