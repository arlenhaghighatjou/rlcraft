package nightkosh.advanced_fishing.api.particles;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.world.WorldServer;

@FunctionalInterface
public interface ISpawnSplashParticles {
   void spawn(WorldServer var1, EaglercraftRandom var2, double var3, double var5, double var7);
}
