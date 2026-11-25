package org.orecruncher.dsurround.client.fx.particle.system;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.fx.ParticleCollections;
import org.orecruncher.dsurround.client.fx.WaterSplashJetEffect;
import org.orecruncher.dsurround.client.fx.particle.mote.IParticleMote;
import org.orecruncher.dsurround.client.sound.PositionedEmitter;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.client.sound.Sounds;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class ParticleWaterSplash extends ParticleJet {
   private static final SoundEffect[] fallSounds = new SoundEffect[11];
   private static final MutableBlockPos pos = new MutableBlockPos();
   private final BlockPos location;
   private PositionedEmitter emitter;
   protected int particleLimit;

   public ParticleWaterSplash(int strength, World world, BlockPos loc, double x, double y, double z) {
      super(0, strength, world, x, y, z, 4);
      this.location = loc.toImmutable();
      this.setSpawnCount((int)((float)strength * 2.5F));
   }

   public void setSpawnCount(int limit) {
      this.particleLimit = MathStuff.clamp(limit, 5, 20);
   }

   public int getSpawnCount() {
      switch(SETTINGS.particleSetting) {
      case 0:
         return this.particleLimit;
      case 2:
         return 0;
      default:
         return this.particleLimit / 2;
      }
   }

   public boolean shouldDie() {
      return this.particleAge % 10 == 0 && !WaterSplashJetEffect.isValidSpawnBlock(WorldUtils.getDefaultBlockStateProvider(), this.location);
   }

   private boolean setupSound() {
      return this.emitter == null && this.isAlive() && this.jetStrength >= ModOptions.effects.waterfallCutoff && RANDOM.nextInt(6) == 0;
   }

   protected void soundUpdate() {
      if (this.setupSound()) {
         pos.setPos(this.posX, this.posY, this.posZ);
         int idx = MathStuff.clamp(this.jetStrength, 0, fallSounds.length - 1);
         this.emitter = new PositionedEmitter(fallSounds[idx], pos);
         this.emitter.setPitch(1.0F + 0.2F * (RANDOM.nextFloat() - RANDOM.nextFloat()));
      }

      if (this.emitter != null) {
         this.emitter.update();
      }

   }

   protected void cleanUp() {
      if (this.emitter != null) {
         this.emitter.stop();
      }

      this.emitter = null;
      super.cleanUp();
   }

   protected void spawnJetParticle() {
      if (ParticleCollections.canFitWaterSpray()) {
         int splashCount = this.getSpawnCount();

         for(int j = 0; (float)j < (float)splashCount; ++j) {
            double xOffset = (double)(RANDOM.nextFloat() * 2.0F - 1.0F);
            double zOffset = (double)(RANDOM.nextFloat() * 2.0F - 1.0F);
            if (!ClientChunkCache.instance().getBlockState(pos.setPos(this.posX + xOffset, this.posY, this.posZ + zOffset)).getMaterial().isSolid()) {
               int motionStr = this.jetStrength + 3;
               double motionX = xOffset * ((double)motionStr / 20.0D);
               double motionZ = zOffset * ((double)motionStr / 20.0D);
               double motionY = 0.1D + (double)(RANDOM.nextFloat() * (float)motionStr) / 20.0D;
               IParticleMote particle = ParticleCollections.addWaterSpray(this.world, this.posX + xOffset, this.posY, this.posZ + zOffset, motionX, motionY, motionZ);
               if (particle == null) {
                  break;
               }
            }
         }
      }

   }

   static {
      fallSounds[0] = Sounds.WATERFALL0;
      fallSounds[1] = Sounds.WATERFALL0;
      fallSounds[2] = Sounds.WATERFALL1;
      fallSounds[3] = Sounds.WATERFALL1;
      fallSounds[4] = Sounds.WATERFALL2;
      fallSounds[5] = Sounds.WATERFALL3;
      fallSounds[6] = Sounds.WATERFALL3;
      fallSounds[7] = Sounds.WATERFALL4;
      fallSounds[8] = Sounds.WATERFALL4;
      fallSounds[9] = Sounds.WATERFALL5;
      fallSounds[10] = Sounds.WATERFALL5;
   }
}
