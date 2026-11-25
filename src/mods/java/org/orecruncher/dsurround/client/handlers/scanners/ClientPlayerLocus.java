package org.orecruncher.dsurround.client.handlers.scanners;

import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.lib.scanner.ScanLocus;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public class ClientPlayerLocus extends ScanLocus {
   public ClientPlayerLocus() {
   }

   @Nonnull
   public IBlockAccessEx getWorld() {
      return ClientChunkCache.instance();
   }

   public BlockPos getCenter() {
      return EnvironStateHandler.EnvironState.getPlayerPosition();
   }
}
