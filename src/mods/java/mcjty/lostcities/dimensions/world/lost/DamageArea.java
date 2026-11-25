package mcjty.lostcities.dimensions.world.lost;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.lost.cityassets.CompiledPalette;
import mcjty.lostcities.dimensions.world.terraingen.LostCitiesTerrainGenerator;
import mcjty.lostcities.varia.GeometryTools;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class DamageArea {
   public static final float BLOCK_DAMAGE_CHANCE = 0.7F;
   private final long seed;
   private final int chunkX;
   private final int chunkZ;
   private final List<Explosion> explosions = new ArrayList();
   private final AxisAlignedBB chunkBox;
   private final LostCityProfile profile;

   public DamageArea(int chunkX, int chunkZ, LostCityChunkGenerator provider, BuildingInfo info) {
      this.seed = provider.seed;
      this.profile = info.profile;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.chunkBox = new AxisAlignedBB((double)(chunkX * 16), 0.0D, (double)(chunkZ * 16), (double)(chunkX * 16 + 15), 256.0D, (double)(chunkZ * 16 + 15));
      Random rand = new Random(this.seed + (long)chunkZ * 295075153L + (long)chunkX * 899826547L);
      rand.nextFloat();
      rand.nextFloat();
      int offset = (Math.max(info.profile.EXPLOSION_MAXRADIUS, info.profile.MINI_EXPLOSION_MAXRADIUS) + 15) / 16;

      for(int cx = chunkX - offset; cx <= chunkX + offset; ++cx) {
         for(int cz = chunkZ - offset; cz <= chunkZ + offset; ++cz) {
            if (!info.profile.EXPLOSIONS_IN_CITIES_ONLY || BuildingInfo.isCity(cx, cz, provider)) {
               Explosion explosion = this.getExplosionAt(cx, cz, provider);
               if (explosion != null && this.intersectsWith(explosion.getCenter(), explosion.getRadius())) {
                  BuildingInfo.getBuildingInfo(cx, cz, provider);
                  Float chance = BuildingInfo.getChunkCharacteristics(cx, cz, provider).cityStyle.getExplosionChance();
                  if (chance == null || rand.nextFloat() < chance.floatValue()) {
                     this.explosions.add(explosion);
                  }
               }

               explosion = this.getMiniExplosionAt(cx, cz, provider);
               if (explosion != null && this.intersectsWith(explosion.getCenter(), explosion.getRadius())) {
                  BuildingInfo.getBuildingInfo(cx, cz, provider);
                  Float chance = BuildingInfo.getChunkCharacteristics(cx, cz, provider).cityStyle.getExplosionChance();
                  if (chance == null || rand.nextFloat() < chance.floatValue()) {
                     this.explosions.add(explosion);
                  }
               }
            }
         }
      }

   }

   public Character damageBlock(Character b, LostCityChunkGenerator provider, int y, float damage, CompiledPalette palette, char liquidChar) {
      if (b.charValue() != LostCitiesTerrainGenerator.bedrockChar && b.charValue() != LostCitiesTerrainGenerator.endportalChar && b.charValue() != LostCitiesTerrainGenerator.endportalFrameChar) {
         if (LostCitiesTerrainGenerator.getGlassChars().contains(b)) {
            damage *= 2.5F;
         }

         if (provider.rand.nextFloat() <= damage) {
            Character damaged = palette.canBeDamagedToIronBars(b);
            int waterlevel = this.profile.GROUNDLEVEL - this.profile.WATERLEVEL_OFFSET;
            if (damage < 0.7F && damaged != null) {
               if (provider.rand.nextFloat() < 0.7F) {
                  b = damaged;
               } else {
                  b = y <= waterlevel ? liquidChar : LostCitiesTerrainGenerator.airChar;
               }
            } else {
               b = y <= waterlevel ? liquidChar : LostCitiesTerrainGenerator.airChar;
            }
         }

         return b;
      } else {
         return b;
      }
   }

   private boolean intersectsWith(BlockPos center, int radius) {
      double dmin = GeometryTools.squaredDistanceBoxPoint(this.chunkBox, center);
      return dmin <= (double)(radius * radius);
   }

   private Explosion getExplosionAt(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      Random rand = new Random(this.seed + (long)chunkZ * 295075153L + (long)chunkX * 797003437L);
      rand.nextFloat();
      rand.nextFloat();
      return rand.nextFloat() < this.profile.EXPLOSION_CHANCE ? new Explosion(this.profile.EXPLOSION_MINRADIUS + rand.nextInt(this.profile.EXPLOSION_MAXRADIUS - this.profile.EXPLOSION_MINRADIUS), new BlockPos(chunkX * 16 + rand.nextInt(16), BuildingInfo.getBuildingInfo(chunkX, chunkZ, provider).cityLevel * 6 + this.profile.EXPLOSION_MINHEIGHT + rand.nextInt(this.profile.EXPLOSION_MAXHEIGHT - this.profile.EXPLOSION_MINHEIGHT), chunkZ * 16 + rand.nextInt(16))) : null;
   }

   private Explosion getMiniExplosionAt(int chunkX, int chunkZ, LostCityChunkGenerator provider) {
      Random rand = new Random(this.seed + (long)chunkZ * 1400305337L + (long)chunkX * 573259391L);
      rand.nextFloat();
      rand.nextFloat();
      return rand.nextFloat() < this.profile.MINI_EXPLOSION_CHANCE ? new Explosion(this.profile.MINI_EXPLOSION_MINRADIUS + rand.nextInt(this.profile.MINI_EXPLOSION_MAXRADIUS - this.profile.MINI_EXPLOSION_MINRADIUS), new BlockPos(chunkX * 16 + rand.nextInt(16), BuildingInfo.getBuildingInfo(chunkX, chunkZ, provider).cityLevel * 6 + this.profile.MINI_EXPLOSION_MINHEIGHT + rand.nextInt(this.profile.MINI_EXPLOSION_MAXHEIGHT - this.profile.MINI_EXPLOSION_MINHEIGHT), chunkZ * 16 + rand.nextInt(16))) : null;
   }

   public boolean hasExplosions() {
      return !this.explosions.isEmpty();
   }

   public List<Explosion> getExplosions() {
      return this.explosions;
   }

   public boolean hasExplosions(int y) {
      AxisAlignedBB box = new AxisAlignedBB((double)(this.chunkX * 16), (double)(y * 16), (double)(this.chunkZ * 16), (double)(this.chunkX * 16 + 15), (double)(y * 16 + 15), (double)(this.chunkZ * 16 + 15));

      for(Explosion explosion : this.explosions) {
         double dmin = GeometryTools.squaredDistanceBoxPoint(box, explosion.getCenter());
         if (dmin <= (double)(explosion.getRadius() * explosion.getRadius())) {
            return true;
         }
      }

      return false;
   }

   public boolean isCompletelyDestroyed(int y) {
      AxisAlignedBB box = new AxisAlignedBB((double)(this.chunkX * 16), (double)(y * 16), (double)(this.chunkZ * 16), (double)(this.chunkX * 16 + 15), (double)(y * 16 + 15), (double)(this.chunkZ * 16 + 15));

      for(Explosion explosion : this.explosions) {
         double dmax = GeometryTools.maxSquaredDistanceBoxPoint(box, explosion.getCenter());
         int sqdist = explosion.getRadius() * explosion.getRadius();
         if (dmax <= (double)sqdist) {
            double dist = ((double)explosion.getRadius() - 3.0D * (double)explosion.getRadius()) / -3.0D;
            dist = dist * dist;
            if (dmax <= dist) {
               return true;
            }
         }
      }

      return false;
   }

   public int getLowestExplosionHeight() {
      for(int yy = 0; yy < 16; ++yy) {
         if (this.hasExplosions(yy)) {
            return yy * 16;
         }
      }

      return -1;
   }

   public int getHighestExplosionHeight() {
      for(int yy = 15; yy >= 0; --yy) {
         if (this.hasExplosions(yy)) {
            return yy * 16 + 15;
         }
      }

      return -1;
   }

   public float getDamageFactor() {
      float damage = 0.0F;

      for(Explosion explosion : this.explosions) {
         double sq = explosion.getCenter().distanceSq((double)(this.chunkX * 16), (double)explosion.getCenter().getY(), (double)(this.chunkZ * 16));
         if (sq < (double)explosion.getSqradius()) {
            double d = Math.sqrt(sq);
            damage = (float)((double)damage + 3.0D * ((double)explosion.getRadius() - d) / (double)explosion.getRadius());
         }
      }

      return damage;
   }

   public float getDamage(int x, int y, int z) {
      float damage = 0.0F;

      for(Explosion explosion : this.explosions) {
         double sq = explosion.getCenter().distanceSq((double)x, (double)y, (double)z);
         if (sq < (double)explosion.getSqradius()) {
            double d = Math.sqrt(sq);
            damage = (float)((double)damage + 3.0D * ((double)explosion.getRadius() - d) / (double)explosion.getRadius());
         }
      }

      return damage;
   }
}
