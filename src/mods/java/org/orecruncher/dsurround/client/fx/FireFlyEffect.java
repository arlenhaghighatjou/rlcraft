package org.orecruncher.dsurround.client.fx;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public class FireFlyEffect extends BlockEffect {
   public FireFlyEffect(int chance) {
      super(chance);
   }

   @Nonnull
   public BlockEffectType getEffectType() {
      return BlockEffectType.FIREFLY;
   }

   public void doEffect(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Random random) {
      World world = provider.getWorld();
      if (world != null) {
         AxisAlignedBB box = state.getBoundingBox(world, pos);
         Vec3d loc = box.getCenter();
         ParticleCollections.addFireFly(world, (double)pos.getX() + loc.x, (double)pos.getY() + box.maxY, (double)pos.getZ() + loc.z);
      }

   }
}
