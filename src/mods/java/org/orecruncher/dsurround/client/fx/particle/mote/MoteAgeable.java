package org.orecruncher.dsurround.client.fx.particle.mote;

import javax.annotation.Nonnull;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class MoteAgeable extends MoteParticle {
   protected int age = 0;
   protected int maxAge;

   protected MoteAgeable(@Nonnull World world, double x, double y, double z) {
      super(world, x, y, z);
   }

   protected boolean advanceAge() {
      return this.age++ >= this.maxAge;
   }

   public void onUpdate() {
      if (this.advanceAge()) {
         this.kill();
      }

      super.onUpdate();
   }
}
