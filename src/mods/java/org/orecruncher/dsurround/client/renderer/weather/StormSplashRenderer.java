package org.orecruncher.dsurround.client.renderer.weather;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityDimensionInfo;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfo;
import org.orecruncher.dsurround.capabilities.season.ISeasonInfo;
import org.orecruncher.dsurround.capabilities.season.PrecipitationType;
import org.orecruncher.dsurround.client.fx.ParticleCollections;
import org.orecruncher.dsurround.client.handlers.SoundEffectHandler;
import org.orecruncher.dsurround.client.sound.SoundBuilder;
import org.orecruncher.dsurround.client.sound.SoundInstance;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.client.weather.compat.RandomThings;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.gfx.ParticleHelper;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class StormSplashRenderer {
   protected static final int PARTICLE_SOUND_CHANCE = 3;
   private static final Int2ObjectOpenHashMap<StormSplashRenderer> splashRenderers = new Int2ObjectOpenHashMap();
   private static final StormSplashRenderer DEFAULT = new StormSplashRenderer();
   protected final EaglercraftRandom RANDOM = new XorShiftRandom();
   protected final NoiseGeneratorSimplex GENERATOR;
   protected final MutableBlockPos pos;
   protected int rainSoundCounter;

   public StormSplashRenderer() {
      this.GENERATOR = new NoiseGeneratorSimplex(this.RANDOM);
      this.pos = new MutableBlockPos();
      this.rainSoundCounter = 0;
   }

   public static void renderStormSplashes(int dimensionId, EntityRenderer renderer) {
      ((StormSplashRenderer)splashRenderers.get(dimensionId)).addRainParticles(renderer);
   }

   protected float calculateRainSoundVolume(World world) {
      float currentVolume = Weather.getCurrentVolume();
      float bounds = currentVolume * 0.25F;
      float adjust = MathHelper.clamp((float)(this.GENERATOR.getValue((double)(world.getWorldTime() % 24000L) / 100.0D, 1.0D) / 5.0D), -bounds, bounds);
      return MathHelper.clamp(currentVolume + adjust, 0.0F, 1.0F);
   }

   protected void spawnBlockParticle(IBlockState state, boolean dust, World world, double x, double y, double z) {
      Block block = state.getBlock();
      EnumParticleTypes particleType = null;
      if (!dust && block != Blocks.SOUL_SAND) {
         if ((block == Blocks.NETHERRACK || block == Blocks.MAGMA) && ModOptions.rain.enableNetherrackMagmaSplashEffect && this.RANDOM.nextInt(20) == 0) {
            particleType = EnumParticleTypes.LAVA;
         } else if (state.getMaterial() == Material.LAVA) {
            particleType = EnumParticleTypes.SMOKE_NORMAL;
         } else if (WorldUtils.isFullWaterBlock(state)) {
            ParticleCollections.addWaterRipple(world, x, y, z);
         } else if (state.getMaterial() != Material.AIR) {
            ParticleCollections.addRainSplash(world, x, y, z);
         }

         if (particleType != null) {
            ParticleHelper.spawnParticle(particleType, x, y, z);
         }

      }
   }

   protected SoundEvent getBlockSoundFX(Block block, PrecipitationType pt) {
      if (pt == PrecipitationType.DUST) {
         return Weather.getWeatherProperties().getDustSound();
      } else {
         return block == Blocks.NETHERRACK ? SoundEvents.BLOCK_LAVA_POP : Weather.getWeatherProperties().getStormSound();
      }
   }

   protected BlockPos getPrecipitationHeight(ISeasonInfo season, int range, BlockPos pos) {
      return season.getPrecipitationHeight(pos);
   }

   protected void playSplashSound(ISeasonInfo season, World world, Entity player, double x, double y, double z) {
      this.pos.setPos(x, y - 1.0D, z);
      PrecipitationType pt = season.getPrecipitationType(this.pos, (BiomeInfo)null);
      Block block = ClientChunkCache.instance().getBlockState(this.pos).getBlock();
      SoundEvent sound = this.getBlockSoundFX(block, pt);
      if (sound != null) {
         float volume = this.calculateRainSoundVolume(world);
         float pitch = 1.0F;
         int playerY = MathHelper.floor(player.posY);
         this.pos.setPos(player.posX, 0.0D, player.posZ);
         if (y > player.posY + 1.0D && season.getPrecipitationHeight(this.pos).getY() > playerY) {
            pitch = 0.5F;
         }

         pitch = pitch - (this.RANDOM.nextFloat() - this.RANDOM.nextFloat()) * 0.1F;
         this.pos.setPos(x, y, z);
         SoundInstance fx = SoundBuilder.builder(sound, SoundCategory.WEATHER).setVolume(volume).setPitch(pitch).setPosition(this.pos).build();
         SoundEffectHandler.INSTANCE.playSound(fx);
      }

   }

   public void addRainParticles(EntityRenderer theThis) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.gameSettings.particleSetting != 2) {
         Entity entity = mc.getRenderViewEntity();
         if (entity != null) {
            World world = mc.world;
            IDimensionInfo info = CapabilityDimensionInfo.getCapability(world);
            if (info == null || info.hasWeather()) {
               float rainStrengthFactor = Weather.getIntensityLevel();
               if (!mc.gameSettings.fancyGraphics) {
                  rainStrengthFactor /= 2.0F;
               }

               if (rainStrengthFactor > 0.0F) {
                  this.RANDOM.setSeed((long)RenderWeather.getRendererUpdateCount() * 312987231L);
                  int playerX = MathHelper.floor(entity.posX);
                  int playerY = MathHelper.floor(entity.posY);
                  int playerZ = MathHelper.floor(entity.posZ);
                  double spawnX = 0.0D;
                  double spawnY = 0.0D;
                  double spawnZ = 0.0D;
                  int particlesSpawned = 0;
                  int RANGE = Math.max((ModOptions.effects.specialEffectRange + 1) / 2, 10);
                  float rangeFactor = (float)RANGE / 10.0F;
                  int particleCount = (int)(100.0F * rainStrengthFactor * rainStrengthFactor * rangeFactor);
                  if (mc.gameSettings.particleSetting == 1) {
                     particleCount >>= 1;
                  }

                  ISeasonInfo season = CapabilitySeasonInfo.getCapability(world);

                  for(int j1 = 0; j1 < particleCount; ++j1) {
                     int locX = playerX + this.RANDOM.nextInt(RANGE) - this.RANDOM.nextInt(RANGE);
                     int locZ = playerZ + this.RANDOM.nextInt(RANGE) - this.RANDOM.nextInt(RANGE);
                     this.pos.setPos(locX, 0, locZ);
                     if (!RandomThings.shouldRain(world, this.pos)) {
                        BlockPos precipHeight = this.getPrecipitationHeight(season, RANGE / 2, this.pos);
                        PrecipitationType pt = season.getPrecipitationType(precipHeight, (BiomeInfo)null);
                        boolean hasDust = pt == PrecipitationType.DUST;
                        if ((hasDust || pt == PrecipitationType.RAIN) && precipHeight.getY() <= playerY + RANGE && precipHeight.getY() >= playerY - RANGE) {
                           BlockPos blockPos = precipHeight.down();
                           IBlockState state = ClientChunkCache.instance().getBlockState(blockPos);
                           double posX = (double)((float)locX + this.RANDOM.nextFloat());
                           double posY = (double)((float)precipHeight.getY() + 0.1F) - state.getBoundingBox(world, blockPos).minY;
                           double posZ = (double)((float)locZ + this.RANDOM.nextFloat());
                           this.spawnBlockParticle(state, hasDust, world, posX, posY, posZ);
                           ++particlesSpawned;
                           if (this.RANDOM.nextInt(particlesSpawned) == 0) {
                              spawnX = posX;
                              spawnY = posY;
                              spawnZ = posZ;
                           }
                        }
                     }
                  }

                  if (particlesSpawned > 0 && this.RANDOM.nextInt(3) < this.rainSoundCounter++) {
                     this.rainSoundCounter = 0;
                     this.playSplashSound(season, world, entity, spawnX, spawnY, spawnZ);
                  }

               }
            }
         }
      }
   }

   static {
      splashRenderers.defaultReturnValue(DEFAULT);
      splashRenderers.put(-1, new NetherSplashRenderer());
      splashRenderers.put(1, new NullSplashRenderer());
   }
}
