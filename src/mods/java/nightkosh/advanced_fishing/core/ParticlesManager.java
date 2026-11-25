package nightkosh.advanced_fishing.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import nightkosh.advanced_fishing.api.particles.IParticlesManager;
import nightkosh.advanced_fishing.api.particles.ISpawnBubbleParticles;
import nightkosh.advanced_fishing.api.particles.ISpawnSplashParticles;
import nightkosh.advanced_fishing.api.particles.ISpawnWakeParticles;

public class ParticlesManager implements IParticlesManager {
   public static final ParticlesManager INSTANCE = new ParticlesManager();
   private static final Map<Block, ISpawnSplashParticles> SPLASH_PARTICLES = new HashMap();
   private static final Map<Block, ISpawnBubbleParticles> BUBBLE_PARTICLES = new HashMap();
   private static final Map<Block, ISpawnWakeParticles> WAKE_PARTICLES = new HashMap();

   public ParticlesManager() {
   }

   public void addSplashParticles(Block block, ISpawnSplashParticles particles) {
      SPLASH_PARTICLES.put(block, particles);
   }

   public ISpawnSplashParticles getSplashParticles(Block block) {
      return (ISpawnSplashParticles)SPLASH_PARTICLES.getOrDefault(block, ParticlesManager::spawnWaterSplashParticles);
   }

   public void addBubbleParticles(Block block, ISpawnBubbleParticles particles) {
      BUBBLE_PARTICLES.put(block, particles);
   }

   public ISpawnBubbleParticles getBubbleParticles(Block block) {
      return (ISpawnBubbleParticles)BUBBLE_PARTICLES.getOrDefault(block, ParticlesManager::spawnWaterBubbleParticles);
   }

   public void addWakeParticles(Block block, ISpawnWakeParticles particles) {
      WAKE_PARTICLES.put(block, particles);
   }

   public ISpawnWakeParticles getWakeParticles(Block block) {
      return (ISpawnWakeParticles)WAKE_PARTICLES.getOrDefault(block, ParticlesManager::spawnWaterWakeParticles);
   }

   private static void spawnWaterSplashParticles(WorldServer world, Random rand, double x, double y, double z) {
      world.spawnParticle(EnumParticleTypes.WATER_SPLASH, x, y, z, 2 + rand.nextInt(2), 0.1D, 0.0D, 0.1D, 0.0D, new int[0]);
   }

   private static void spawnLavaSplashParticles(WorldServer world, Random rand, double x, double y, double z) {
      int num = 2 + rand.nextInt(2);
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, num, 0.1D, 0.0D, 0.1D, 0.0D, new int[0]);
      world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, num, 0.1D, 0.0D, 0.1D, 0.0D, new int[0]);
   }

   private static void spawnWaterBubbleParticles(WorldServer world, double x, double y, double z, int num, double xOffset, double yOffset, double zOffset, double speed) {
      world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z, num, xOffset, yOffset, zOffset, speed, new int[0]);
   }

   private static void spawnLavaBubbleParticles(WorldServer world, double x, double y, double z, int num, double xOffset, double yOffset, double zOffset, double speed) {
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, num, xOffset, yOffset, zOffset, speed, new int[0]);
   }

   private static void spawnWaterWakeParticles(WorldServer world, double x, double y, double z, int num, double xOffset, double yOffset, double zOffset, double speed) {
      world.spawnParticle(EnumParticleTypes.WATER_WAKE, x, y, z, num, xOffset, yOffset, zOffset, speed, new int[0]);
   }

   private static void spawnLavaWakeParticles(WorldServer world, double x, double y, double z, int num, double xOffset, double yOffset, double zOffset, double speed) {
      world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, num, xOffset, yOffset, zOffset, speed, new int[0]);
      world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, num, xOffset, yOffset, zOffset, speed, new int[0]);
   }

   static {
      SPLASH_PARTICLES.put(Blocks.WATER, ParticlesManager::spawnWaterSplashParticles);
      SPLASH_PARTICLES.put(Blocks.FLOWING_WATER, ParticlesManager::spawnWaterSplashParticles);
      SPLASH_PARTICLES.put(Blocks.LAVA, ParticlesManager::spawnLavaSplashParticles);
      SPLASH_PARTICLES.put(Blocks.FLOWING_LAVA, ParticlesManager::spawnLavaSplashParticles);
      BUBBLE_PARTICLES.put(Blocks.WATER, ParticlesManager::spawnWaterBubbleParticles);
      BUBBLE_PARTICLES.put(Blocks.FLOWING_WATER, ParticlesManager::spawnWaterBubbleParticles);
      BUBBLE_PARTICLES.put(Blocks.LAVA, ParticlesManager::spawnLavaBubbleParticles);
      BUBBLE_PARTICLES.put(Blocks.FLOWING_LAVA, ParticlesManager::spawnLavaBubbleParticles);
      WAKE_PARTICLES.put(Blocks.WATER, ParticlesManager::spawnWaterWakeParticles);
      WAKE_PARTICLES.put(Blocks.FLOWING_WATER, ParticlesManager::spawnWaterWakeParticles);
      WAKE_PARTICLES.put(Blocks.LAVA, ParticlesManager::spawnLavaWakeParticles);
      WAKE_PARTICLES.put(Blocks.FLOWING_LAVA, ParticlesManager::spawnLavaWakeParticles);
   }
}
