package org.orecruncher.dsurround.client.weather.compat;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.ReflectedMethod;

@SideOnly(Side.CLIENT)
public final class RandomThings {
   private static final ReflectedMethod<Boolean> shouldRain = new ReflectedMethod("lumien.randomthings.tileentity.TileEntityRainShield", "shouldRain", (String)null, new Class[]{World.class, BlockPos.class});

   private RandomThings() {
   }

   public static boolean shouldRain(World world, BlockPos pos) {
      return shouldRain.isAvailable() && !((Boolean)shouldRain.invoke((Object)null, new Object[]{world, pos})).booleanValue();
   }
}
