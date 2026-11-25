package nightkosh.advanced_fishing.api.particles;

import java.util.Random;
import net.minecraft.world.WorldServer;

@FunctionalInterface
public interface ISpawnSplashParticles {
   void spawn(WorldServer var1, Random var2, double var3, double var5, double var7);
}
