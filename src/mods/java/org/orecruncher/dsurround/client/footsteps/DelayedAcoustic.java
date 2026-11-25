package org.orecruncher.dsurround.client.footsteps;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.acoustics.IOptions;
import org.orecruncher.dsurround.registry.acoustics.SimpleAcoustic;

@SideOnly(Side.CLIENT)
public class DelayedAcoustic extends SimpleAcoustic implements IOptions {
   protected long delayMin = 0L;
   protected long delayMax = 0L;

   public DelayedAcoustic() {
      this.outputOptions = this;
   }

   public long getDelayMin() {
      return this.delayMin;
   }

   public long getDelayMax() {
      return this.delayMax;
   }

   public void setDelayMin(long delay) {
      this.delayMin = delay;
   }

   public void setDelayMax(long delay) {
      this.delayMax = delay;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(super.toString()).append(" (DELAYED min:").append(this.delayMin).append("/max:").append(this.delayMax).append(')');
      return builder.toString();
   }
}
