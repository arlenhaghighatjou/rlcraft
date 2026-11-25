package svenhjol.charm.world.decorator.outer;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonOuterDecorator;
import svenhjol.meson.helper.EntityHelper;
import svenhjol.meson.helper.WorldHelper;

public class Mobs extends MesonOuterDecorator {
   public Mobs(World world, BlockPos pos, EaglercraftRandom rand, List<ChunkPos> chunks) {
      super(world, pos, rand, chunks);
   }

   public void generate() {
      Biome biome = this.world.getBiome(this.pos);
      int max = 1;

      for(int i = 0; i < max; ++i) {
         if ((double)this.rand.nextFloat() <= VillageDecorations.mobsWeight) {
            return;
         }

         int xx = this.rand.nextInt(16) + 8;
         int zz = this.rand.nextInt(16) + 8;
         BlockPos posForMob = this.world.getHeight(this.pos.add(xx, 0, zz));
         boolean airAbove = this.world.getBlockState(posForMob) == Blocks.AIR.getDefaultState();
         boolean grassBelow = this.world.getBlockState(posForMob.offset(EnumFacing.DOWN)) == Blocks.GRASS.getDefaultState();
         if (airAbove || grassBelow || EntityHelper.canMobsSpawnInPos(this.world, posForMob)) {
            if (VillageDecorations.villageInfested.contains(WorldHelper.getChunkPos(this.pos))) {
               String horseType;
               if ((double)this.rand.nextFloat() < 0.5D) {
                  horseType = "minecraft:zombie_horse";
               } else {
                  horseType = "minecraft:skeleton_horse";
               }

               EntityHelper.spawnEntity(new ResourceLocation(horseType), this.world, posForMob);
            } else {
               int mobType = this.rand.nextInt(4);
               if (biome.isSnowyBiome() && (double)this.rand.nextFloat() <= VillageDecorations.golemsWeight) {
                  EntitySnowman snowGolem = new EntitySnowman(this.world);
                  snowGolem.setPosition((double)posForMob.getX(), (double)posForMob.getY(), (double)posForMob.getZ());
                  this.world.spawnEntity(snowGolem);
               }

               if (mobType == 0) {
                  Entity entity = EntityHelper.spawnEntity(new ResourceLocation("minecraft:ocelot"), this.world, posForMob);
                  if (entity != null) {
                     EntityOcelot cat = (EntityOcelot)entity;
                     cat.setTameSkin(this.rand.nextInt(4));
                     cat.setSitting(false);
                     if (this.rand.nextFloat() < 0.2F) {
                        cat.setTamed(true);
                        cat.setOwnerId(cat.getUniqueID());
                     }
                  }
               } else if (mobType == 1) {
                  Entity entity = EntityHelper.spawnEntity(new ResourceLocation("minecraft:wolf"), this.world, posForMob);
                  if (entity != null) {
                     EntityWolf wolf = (EntityWolf)entity;
                     wolf.setSitting(false);
                     if (this.rand.nextFloat() < 0.2F) {
                        wolf.setTamed(true);
                        wolf.setOwnerId(wolf.getUniqueID());
                     }
                  }
               } else if (mobType == 2 && (double)this.rand.nextFloat() <= VillageDecorations.golemsWeight) {
                  EntityIronGolem golem = new EntityIronGolem(this.world);
                  golem.setPosition((double)posForMob.getX(), (double)posForMob.getY(), (double)posForMob.getZ());
                  this.world.spawnEntity(golem);
               } else if (mobType == 3) {
                  Entity entity = EntityHelper.spawnEntity(new ResourceLocation("minecraft:horse"), this.world, posForMob);
                  if (entity instanceof EntityHorse) {
                     EntityHorse horse = (EntityHorse)entity;
                     horse.setHorseVariant(this.rand.nextInt(7));
                  }
               }
            }
         }
      }

   }
}
