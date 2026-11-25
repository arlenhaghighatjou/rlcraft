package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.google.common.base.Throwables;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPParticleManager extends ParticleManager {
   private static MethodHandle getBlockDamage;
   private static MethodHandle getParticleScale;
   private static MethodHandle getParticleTexture;
   private static MethodHandle getParticleTypes;
   private static MethodHandle getSourceState;
   private static MethodHandle getParticleMaxAge;
   private static MethodHandle X;
   private static MethodHandle Y;
   private static MethodHandle Z;
   private static MethodHandle mX;
   private static MethodHandle mY;
   private static MethodHandle mZ;
   private static IParticleFactory particleFactory;
   private static IBlockState blockState;
   private static TextureAtlasSprite white;
   private static Lookup lookup;
   Minecraft mc;

   public FBPParticleManager(World worldIn, TextureManager rendererIn, IParticleFactory particleFactory) {
      super(worldIn, rendererIn);
      particleFactory = particleFactory;
      this.mc = Minecraft.getMinecraft();
      white = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.SNOW.getDefaultState());
      lookup = MethodHandles.publicLookup();

      try {
         getParticleTypes = lookup.unreflectGetter(ReflectionHelper.findField(ParticleManager.class, new String[]{"particleTypes", "particleTypes"}));
         X = lookup.unreflectGetter(ReflectionHelper.findField(Particle.class, new String[]{"posX", "posX"}));
         Y = lookup.unreflectGetter(ReflectionHelper.findField(Particle.class, new String[]{"posY", "posY"}));
         Z = lookup.unreflectGetter(ReflectionHelper.findField(Particle.class, new String[]{"posZ", "posZ"}));
         mX = lookup.unreflectGetter(ReflectionHelper.findField(Particle.class, new String[]{"motionX", "motionX"}));
         mY = lookup.unreflectGetter(ReflectionHelper.findField(Particle.class, new String[]{"motionY", "motionY"}));
         mZ = lookup.unreflectGetter(ReflectionHelper.findField(Particle.class, new String[]{"motionZ", "motionZ"}));
         getParticleScale = lookup.unreflectGetter(ReflectionHelper.findField(Particle.class, new String[]{"particleScale", "particleScale"}));
         getParticleTexture = lookup.unreflectGetter(ReflectionHelper.findField(Particle.class, new String[]{"particleTexture", "particleTexture"}));
         getParticleMaxAge = lookup.unreflectGetter(ReflectionHelper.findField(Particle.class, new String[]{"particleMaxAge", "particleMaxAge"}));
         getSourceState = lookup.unreflectGetter(ReflectionHelper.findField(ParticleDigging.class, new String[]{"sourceState", "sourceState"}));
         getBlockDamage = lookup.unreflectGetter(ReflectionHelper.findField(RenderGlobal.class, new String[]{"damagedBlocks", "damagedBlocks"}));
      } catch (Throwable var5) {
         throw Throwables.propagate(var5);
      }
   }

   public void carryOver() {
      if (Minecraft.getMinecraft().effectRenderer != this) {
         Field f1 = ReflectionHelper.findField(ParticleManager.class, new String[]{"fxLayers", "fxLayers"});
         Field f2 = ReflectionHelper.findField(ParticleManager.class, new String[]{"particleEmitters", "particleEmitters"});
         Field f3 = ReflectionHelper.findField(ParticleManager.class, new String[]{"queue", "queue"});

         try {
            MethodHandle getF1 = lookup.unreflectGetter(f1);
            MethodHandle setF1 = lookup.unreflectSetter(f1);
            MethodHandle getF2 = lookup.unreflectGetter(f2);
            MethodHandle setF2 = lookup.unreflectSetter(f2);
            MethodHandle getF3 = lookup.unreflectGetter(f3);
            MethodHandle setF3 = lookup.unreflectSetter(f3);
            setF1.invokeExact(this, getF1.invokeExact(this.mc.effectRenderer));
            setF2.invokeExact(this, getF2.invokeExact(this.mc.effectRenderer));
            setF3.invokeExact(this, getF3.invokeExact(this.mc.effectRenderer));
         } catch (Throwable var10) {
            var10.printStackTrace();
         }

      }
   }

   public void addEffect(Particle effect) {
      Particle toAdd = effect;
      if (effect != null && !(effect instanceof FBPParticleSnow) && !(effect instanceof FBPParticleRain)) {
         if (FBP.fancyFlame && effect instanceof ParticleFlame && !(effect instanceof FBPParticleFlame) && Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
            try {
               toAdd = new FBPParticleFlame(this.world, X.invokeExact(effect), Y.invokeExact(effect), Z.invokeExact(effect), 0.0D, FBP.random.nextDouble() * 0.25D, 0.0D, true);
               effect.setExpired();
            } catch (Throwable var6) {
               var6.printStackTrace();
            }
         } else if (FBP.fancySmoke && effect instanceof ParticleSmokeNormal && !(effect instanceof FBPParticleSmokeNormal) && Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
            ParticleSmokeNormal p = (ParticleSmokeNormal)effect;

            try {
               toAdd = new FBPParticleSmokeNormal(this.world, X.invokeExact(effect), Y.invokeExact(effect), Z.invokeExact(effect), mX.invokeExact(effect), mY.invokeExact(effect), mZ.invokeExact(effect), getParticleScale.invokeExact(effect), true, white, p);
               toAdd.setRBGColorF(MathHelper.clamp(effect.getRedColorF() + 0.1F, 0.1F, 1.0F), MathHelper.clamp(effect.getGreenColorF() + 0.1F, 0.1F, 1.0F), MathHelper.clamp(effect.getBlueColorF() + 0.1F, 0.1F, 1.0F));
               toAdd.setMaxAge(getParticleMaxAge.invokeExact(effect));
            } catch (Throwable var5) {
               var5.printStackTrace();
            }
         } else if (FBP.fancyRain && effect instanceof ParticleRain) {
            effect.setAlphaF(0.0F);
         } else if (effect instanceof ParticleDigging && !(effect instanceof FBPParticleDigging)) {
            try {
               blockState = getSourceState.invokeExact((ParticleDigging)effect);
               if (blockState != null && (!FBP.frozen || FBP.spawnWhileFrozen) && (FBP.spawnRedstoneBlockParticles || blockState.getBlock() != Blocks.REDSTONE_BLOCK)) {
                  effect.setExpired();
                  if (blockState.getBlock() instanceof BlockLiquid || FBP.INSTANCE.isBlacklisted(blockState.getBlock(), true)) {
                     return;
                  }

                  toAdd = new FBPParticleDigging(this.world, X.invokeExact(effect), Y.invokeExact(effect) - 0.10000000149011612D, Z.invokeExact(effect), 0.0D, 0.0D, 0.0D, getParticleScale.invokeExact(effect), toAdd.getRedColorF(), toAdd.getGreenColorF(), toAdd.getBlueColorF(), blockState, (EnumFacing)null, getParticleTexture.invokeExact(effect));
               }
            } catch (Throwable var7) {
               var7.printStackTrace();
            }
         } else if (effect instanceof FBPParticleDigging) {
            try {
               blockState = getSourceState.invokeExact((ParticleDigging)effect);
               if (blockState != null && (!FBP.frozen || FBP.spawnWhileFrozen) && (FBP.spawnRedstoneBlockParticles || blockState.getBlock() != Blocks.REDSTONE_BLOCK) && (blockState.getBlock() instanceof BlockLiquid || FBP.INSTANCE.isBlacklisted(blockState.getBlock(), true))) {
                  effect.setExpired();
                  return;
               }
            } catch (Throwable var8) {
               var8.printStackTrace();
            }
         }
      }

      if (toAdd != effect) {
         effect.setExpired();
      }

      super.addEffect(toAdd);
   }

   @Nullable
   public Particle spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      IParticleFactory iparticlefactory = null;

      try {
         iparticlefactory = (IParticleFactory)getParticleTypes.invokeExact(this).get(Integer.valueOf(particleId));
      } catch (Throwable var18) {
         var18.printStackTrace();
      }

      if (iparticlefactory == null) {
         return null;
      } else {
         Particle particle = iparticlefactory.createParticle(particleId, this.world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
         Particle toSpawn = particle;
         if (particle instanceof ParticleDigging && !(particle instanceof FBPParticleDigging)) {
            blockState = Block.getStateById(parameters[0]);
            if (blockState != null && (!FBP.frozen || FBP.spawnWhileFrozen) && (FBP.spawnRedstoneBlockParticles || blockState.getBlock() != Blocks.REDSTONE_BLOCK)) {
               if (!(blockState.getBlock() instanceof BlockLiquid) && !FBP.INSTANCE.isBlacklisted(blockState.getBlock(), true)) {
                  toSpawn = (new FBPParticleDigging(this.world, xCoord, yCoord + 0.10000000149011612D, zCoord, xSpeed, ySpeed, zSpeed, -1.0F, 1.0F, 1.0F, 1.0F, blockState, (EnumFacing)null, (TextureAtlasSprite)null)).init().multipleParticleScaleBy(0.6F);
               } else {
                  toSpawn = particle;
               }
            }
         }

         this.addEffect(toSpawn);
         return toSpawn;
      }
   }

   public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
      Block b = state.getBlock();
      if (!b.isAir(state, this.world, pos) && !b.addDestroyEffects(this.world, pos, this) && b != FBP.FBPBlock) {
         state = state.getActualState(this.world, pos);
         b = state.getBlock();
         int i = 4;
         TextureAtlasSprite texture = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);

         for(int j = 0; j < FBP.particlesPerAxis; ++j) {
            for(int k = 0; k < FBP.particlesPerAxis; ++k) {
               for(int l = 0; l < FBP.particlesPerAxis; ++l) {
                  double d0 = (double)pos.getX() + ((double)j + 0.5D) / (double)FBP.particlesPerAxis;
                  double d1 = (double)pos.getY() + ((double)k + 0.5D) / (double)FBP.particlesPerAxis;
                  double d2 = (double)pos.getZ() + ((double)l + 0.5D) / (double)FBP.particlesPerAxis;
                  if (state != null && !(b instanceof BlockLiquid) && (!FBP.frozen || FBP.spawnWhileFrozen) && (FBP.spawnRedstoneBlockParticles || b != Blocks.REDSTONE_BLOCK) && !FBP.INSTANCE.isBlacklisted(b, true)) {
                     float scale = (float)FBP.random.nextDouble(0.75D, 1.0D);
                     FBPParticleDigging toSpawn = (new FBPParticleDigging(this.world, d0, d1, d2, d0 - (double)pos.getX() - 0.5D, -0.001D, d2 - (double)pos.getZ() - 0.5D, scale, 1.0F, 1.0F, 1.0F, state, (EnumFacing)null, texture)).setBlockPos(pos);
                     this.addEffect(toSpawn);
                  }
               }
            }
         }
      }

   }

   public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = this.world.getBlockState(pos);
      if (iblockstate.getBlock() != FBP.FBPBlock) {
         if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            float f = 0.1F;
            AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(this.world, pos);
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 0.0D;
            RayTraceResult obj = Minecraft.getMinecraft().objectMouseOver;
            if (obj == null || obj.hitVec == null) {
               obj = new RayTraceResult((Entity)null, new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D));
            }

            if (!FBP.smartBreaking || iblockstate == null || iblockstate.getBlock() instanceof BlockLiquid || FBP.frozen && !FBP.spawnWhileFrozen || !FBP.spawnRedstoneBlockParticles && iblockstate.getBlock() == Blocks.REDSTONE_BLOCK) {
               d0 = (double)i + this.world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
               d1 = (double)j + this.world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
               d2 = (double)k + this.world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;
            } else {
               d0 = obj.hitVec.x + FBP.random.nextDouble(-0.21D, 0.21D) * Math.abs(axisalignedbb.maxX - axisalignedbb.minX);
               d1 = obj.hitVec.y + FBP.random.nextDouble(-0.21D, 0.21D) * Math.abs(axisalignedbb.maxY - axisalignedbb.minY);
               d2 = obj.hitVec.z + FBP.random.nextDouble(-0.21D, 0.21D) * Math.abs(axisalignedbb.maxZ - axisalignedbb.minZ);
            }

            switch(side) {
            case DOWN:
               d1 = (double)j + axisalignedbb.minY - 0.10000000149011612D;
               break;
            case EAST:
               d0 = (double)i + axisalignedbb.maxX + 0.10000000149011612D;
               break;
            case NORTH:
               d2 = (double)k + axisalignedbb.minZ - 0.10000000149011612D;
               break;
            case SOUTH:
               d2 = (double)k + axisalignedbb.maxZ + 0.10000000149011612D;
               break;
            case UP:
               d1 = (double)j + axisalignedbb.maxY + 0.08000000119D;
               break;
            case WEST:
               d0 = (double)i + axisalignedbb.minX - 0.10000000149011612D;
            }

            if (iblockstate != null && !(iblockstate.getBlock() instanceof BlockLiquid) && (!FBP.frozen || FBP.spawnWhileFrozen) && (FBP.spawnRedstoneBlockParticles || iblockstate.getBlock() != Blocks.REDSTONE_BLOCK)) {
               int damage = 0;

               try {
                  DestroyBlockProgress progress = null;
                  Map mp = getBlockDamage.invokeExact(Minecraft.getMinecraft().renderGlobal);
                  if (!mp.isEmpty()) {
                     for(DestroyBlockProgress var24 : mp.values()) {
                        if (var24.getPosition().equals(pos)) {
                           damage = var24.getPartialBlockDamage();
                           break;
                        }
                     }
                  }
               } catch (Throwable var20) {
                  ;
               }

               if (!FBP.INSTANCE.isBlacklisted(iblockstate.getBlock(), true)) {
                  Particle toSpawn = (new FBPParticleDigging(this.world, d0, d1, d2, 0.0D, 0.0D, 0.0D, -2.0F, 1.0F, 1.0F, 1.0F, iblockstate, side, (TextureAtlasSprite)null)).setBlockPos(pos);
                  if (FBP.smartBreaking) {
                     toSpawn = ((FBPParticleDigging)toSpawn).MultiplyVelocity(side == EnumFacing.UP ? 0.7F : 0.15F);
                     toSpawn = toSpawn.multipleParticleScaleBy(0.325F + (float)damage / 10.0F * 0.5F);
                  } else {
                     toSpawn = ((FBPParticleDigging)toSpawn).MultiplyVelocity(0.2F);
                     toSpawn = toSpawn.multipleParticleScaleBy(0.6F);
                  }

                  this.addEffect(toSpawn);
               }
            }
         }

      }
   }
}
