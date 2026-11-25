package vazkii.quark.automation.tile;

import net.minecraft.util.ITickable;
import vazkii.arl.block.tile.TileMod;
import vazkii.quark.automation.block.BlockRainDetector;

public class TileRainDetector extends TileMod implements ITickable {
   public TileRainDetector() {
   }

   public void update() {
      if (!this.getWorld().isRemote && this.getWorld().getTotalWorldTime() % 20L == 0L) {
         this.blockType = this.getBlockType();
         if (this.blockType instanceof BlockRainDetector) {
            ((BlockRainDetector)this.blockType).updatePower(this.getWorld(), this.pos);
         }
      }

   }
}
