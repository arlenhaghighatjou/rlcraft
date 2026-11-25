package sereneseasons.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.block.BlockSeasonSensor;

public class TileEntitySeasonSensor extends TileEntity implements ITickable {
   public TileEntitySeasonSensor() {
   }

   public void update() {
      if (this.world != null && !this.world.isRemote && (long)SeasonHelper.getSeasonState(this.world).getSeasonCycleTicks() % 20L == 0L) {
         ((BlockSeasonSensor)this.getBlockType()).updatePower(this.world, this.pos);
      }

   }
}
