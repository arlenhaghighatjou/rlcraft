package org.orecruncher.dsurround.event;

import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ThunderEvent extends Event {
   public final int dimId;
   public final boolean doFlash;
   public final BlockPos location;

   public ThunderEvent(int dim, boolean doFlash, @Nonnull BlockPos pos) {
      this.dimId = dim;
      this.doFlash = doFlash;
      this.location = pos;
   }
}
