package org.orecruncher.dsurround.client.fx.particle.mote;

import net.minecraft.util.math.Vec3d;
import org.orecruncher.lib.Color;

public interface IIlluminatedMote {
   Vec3d getPosition();

   Color getColor();

   float getAlpha();

   float getRadius();
}
