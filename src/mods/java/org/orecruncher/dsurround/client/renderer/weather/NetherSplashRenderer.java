package org.orecruncher.dsurround.client.renderer.weather;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.capabilities.season.ISeasonInfo;
import org.orecruncher.dsurround.capabilities.season.PrecipitationType;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.gfx.ParticleHelper;

@SideOnly(Side.CLIENT)
public class NetherSplashRenderer extends StormSplashRenderer {
   public NetherSplashRenderer() {
   }

   protected SoundEvent getBlockSoundFX(Block block, PrecipitationType pt) {
      return pt == PrecipitationType.DUST ? Weather.getWeatherProperties().getDustSound() : null;
   }

   protected void spawnBlockParticle(IBlockState state, boolean dust, World world, double x, double y, double z) {
      if (dust) {
         ParticleHelper.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z);
      }

   }

   protected BlockPos getPrecipitationHeight(ISeasonInfo season, int range, BlockPos pos) {
      int y = EnvironStateHandler.EnvironState.getPlayerPosition().getY();
      MutableBlockPos p = new MutableBlockPos(pos);
      boolean airBlockFound = false;

      for(int i = range; i >= -range; --i) {
         p.setY(y + i);
         IBlockState state = ClientChunkCache.instance().getBlockState(p);
         Material material = state.getMaterial();
         if (airBlockFound && material != Material.AIR && material.isSolid()) {
            return new BlockPos(pos.getX(), y + i + 1, pos.getZ());
         }

         if (material == Material.AIR) {
            airBlockFound = true;
         }
      }

      return new BlockPos(pos.getX(), 128, pos.getZ());
   }
}
