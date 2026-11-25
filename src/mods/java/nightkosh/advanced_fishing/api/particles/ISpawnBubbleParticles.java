package nightkosh.advanced_fishing.api.particles;

import net.minecraft.world.WorldServer;

@FunctionalInterface
public interface ISpawnBubbleParticles {
   void spawn(WorldServer var1, double var2, double var4, double var6, int var8, double var9, double var11, double var13, double var15);
}
