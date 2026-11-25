package org.orecruncher.dsurround.client.fx.particle;

import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDrip;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.ParticleCollections;
import org.orecruncher.dsurround.client.handlers.SoundEffectHandler;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.client.sound.Sounds;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.chunk.IBlockAccessEx;
import org.orecruncher.lib.gfx.ParticleHelper;

@SideOnly(Side.CLIENT)
public class ParticleDripOverride extends ParticleDrip {
   private boolean firstTime = true;
   private final Material materialType;
   private final MutableBlockPos pos = new MutableBlockPos();
   private int bobTimer = 40;

   protected ParticleDripOverride(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, Material materialType) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, materialType);
      this.materialType = materialType;
   }

   private boolean doSteamHiss(@Nonnull IBlockState state) {
      if (this.materialType == Material.LAVA) {
         return state.getMaterial() == Material.WATER;
      } else {
         return ParticleSteamCloud.isHotBlock(state);
      }
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.materialType == Material.WATER) {
         this.particleRed = 0.2F;
         this.particleGreen = 0.3F;
         this.particleBlue = 1.0F;
      } else {
         this.particleRed = 1.0F;
         this.particleGreen = 16.0F / (float)(40 - this.bobTimer + 16);
         this.particleBlue = 4.0F / (float)(40 - this.bobTimer + 8);
      }

      this.motionY -= (double)this.particleGravity;
      if (this.bobTimer-- > 0) {
         this.motionX *= 0.02D;
         this.motionY *= 0.02D;
         this.motionZ *= 0.02D;
         this.setParticleTextureIndex(113);
      } else {
         this.setParticleTextureIndex(112);
      }

      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863D;
      this.motionY *= 0.9800000190734863D;
      this.motionZ *= 0.9800000190734863D;
      if (this.particleMaxAge-- <= 0) {
         this.setExpired();
      }

      if (this.onGround) {
         this.pos.setPos(this.posX, this.posY, this.posZ);
         IBlockState state = ClientChunkCache.instance().getBlockState(this.pos);
         boolean doSteam;
         if (state.getBlock() == Blocks.LAVA) {
            doSteam = true;
         } else if (!state.getMaterial().isLiquid()) {
            this.pos.move(EnumFacing.DOWN);
            state = ClientChunkCache.instance().getBlockState(this.pos);
            doSteam = ParticleSteamCloud.isHotBlock(state);
         } else {
            doSteam = ParticleCollections.addWaterRipple(this.world, this.posX, (double)(this.pos.getY() + 1), this.posZ) != null && this.materialType == Material.LAVA;
         }

         if (doSteam) {
            ParticleHelper.addParticle(new ParticleSteamCloud(this.world, this.posX, (double)(this.pos.getY() + 1), this.posZ, 0.01D));
         } else if (this.materialType != Material.LAVA) {
            this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         this.setExpired();
      }

      if (this.posY < 1.0D) {
         this.setExpired();
      }

      if (this.isAlive() && this.firstTime) {
         this.firstTime();
      }

   }

   protected void firstTime() {
      this.firstTime = false;
      IBlockAccessEx provider = ClientChunkCache.instance();
      this.pos.setPos(this.posX, this.posY, this.posZ);
      IBlockState state = provider.getBlockState(this.pos);
      if (!WorldUtils.isAirBlock(state)) {
         this.setExpired();
      } else {
         this.pos.setPos(this.posX, this.posY + 0.3D, this.posZ);
         int y = this.pos.getY();
         state = provider.getBlockState(this.pos);
         if (!WorldUtils.isAirBlock(state) && !WorldUtils.isLeaves(state)) {
            while(true) {
               this.pos.move(EnumFacing.DOWN, 1);
               state = provider.getBlockState(this.pos);
               if (this.pos.getY() <= 0 || !WorldUtils.isAirBlock(state)) {
                  break;
               }
            }

            if (this.pos.getY() < 1) {
               return;
            }

            int delay = 40 + (y - this.pos.getY()) * 2;
            this.pos.move(EnumFacing.UP, 1);
            SoundEffect effect;
            if (this.doSteamHiss(state)) {
               effect = Sounds.STEAM_HISS;
            } else if (state.getMaterial().isSolid()) {
               effect = Sounds.WATER_DROP;
            } else if (this.materialType == Material.WATER) {
               effect = Sounds.WATER_DRIP;
            } else {
               effect = Sounds.WATER_DROP;
            }

            SoundEffectHandler.INSTANCE.playSoundAt(this.pos, effect, delay);
         }
      }

   }

   public static void register() {
      Minecraft.getMinecraft().effectRenderer.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new ParticleDripOverride.WaterFactory());
      Minecraft.getMinecraft().effectRenderer.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new ParticleDripOverride.LavaFactory());
   }

   public static class LavaFactory implements IParticleFactory {
      public LavaFactory() {
      }

      public Particle createParticle(int particleID, @Nonnull World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, @Nonnull int... p_178902_15_) {
         return new ParticleDripOverride(worldIn, xCoordIn, yCoordIn, zCoordIn, Material.LAVA);
      }
   }

   public static class WaterFactory implements IParticleFactory {
      public WaterFactory() {
      }

      public Particle createParticle(int particleID, @Nonnull World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, @Nonnull int... p_178902_15_) {
         return new ParticleDripOverride(worldIn, xCoordIn, yCoordIn, zCoordIn, Material.WATER);
      }
   }
}
