package vazkii.quark.tweaks.block;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.tweaks.feature.SpringySlime;

public class BlockSpringySlime extends BlockSlime {
   public BlockSpringySlime() {
      this.setSoundType(SoundType.SLIME);
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
      SpringySlime.collideWithSlimeBlock(pos, entity);
   }

   public void onLanded(@Nonnull World worldIn, Entity entityIn) {
      entityIn.motionY = 0.0D;
   }
}
