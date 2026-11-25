package nightkosh.advanced_fishing.api.particles;

import net.minecraft.block.Block;

public interface IParticlesManager {
   void addSplashParticles(Block var1, ISpawnSplashParticles var2);

   ISpawnSplashParticles getSplashParticles(Block var1);

   void addBubbleParticles(Block var1, ISpawnBubbleParticles var2);

   ISpawnBubbleParticles getBubbleParticles(Block var1);

   void addWakeParticles(Block var1, ISpawnWakeParticles var2);

   ISpawnWakeParticles getWakeParticles(Block var1);
}
