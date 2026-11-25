package com.lycanitesmobs.core.worldgen.mobevents;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityAmalgalich;
import com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AmalgalichStructureBuilder extends StructureBuilder {
   public AmalgalichStructureBuilder() {
      this.name = "amalgalich";
   }

   public void build(World world, EntityPlayer player, BlockPos pos, int level, int ticks, int variant) {
      ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
      int originX = pos.getX();
      int originY = pos.getY();
      int originZ = pos.getZ();
      originX = originX + 20;
      int height = 40;
      if (originY < 5) {
         originY = 5;
      }

      if (world.getHeight() <= height) {
         originY = 5;
      } else if (originY + height >= world.getHeight()) {
         originY = Math.max(5, world.getHeight() - height - 1);
      }

      if (ticks == 1) {
         for(int i = 0; i < 5; ++i) {
            BaseProjectileEntity baseProjectileEntity = new EntityShadowfireBarrier(world, (double)originX, (double)(originY + 10 * i), (double)originZ);
            baseProjectileEntity.projectileLife = 400;
            world.spawnEntity(baseProjectileEntity);
            if (worldExt != null) {
               worldExt.bossUpdate(baseProjectileEntity);
            }
         }
      }

      if (ticks == 60) {
         this.buildArenaFloor(world, originX, originY, originZ);
      }

      if (ticks == 100) {
         this.buildObstacles(world, originX, originY, originZ);
      }

      if (ticks >= 200 && ticks % 10 == 0) {
         world.createExplosion((Entity)null, (double)(originX - 20 + world.rand.nextInt(40)), (double)(originY + 25 + world.rand.nextInt(10)), (double)(originZ - 20 + world.rand.nextInt(40)), 2.0F, true);
      }

      if (ticks == 400) {
         BaseCreatureEntity baseCreatureEntity = new EntityAmalgalich(world);
         baseCreatureEntity.setLocationAndAngles((double)originX, (double)(originY + 1), (double)originZ, 0.0F, 0.0F);
         world.spawnEntity(baseCreatureEntity);
         baseCreatureEntity.setArenaCenter(new BlockPos(originX, originY + 1, originZ));
         if (worldExt != null) {
            MobEventPlayerServer mobEventPlayerServer = worldExt.getMobEventPlayerServer(this.name);
            if (mobEventPlayerServer != null) {
               mobEventPlayerServer.mobEvent.onSpawn(baseCreatureEntity, world, player, pos, level, ticks, variant);
            }
         }
      }

   }

   public void buildArenaFloor(World world, int originX, int originY, int originZ) {
      int radius = 60;
      int height = 40;
      Block primaryBlock = ObjectManager.getBlock("shadowstonetile");
      Block secondaryBlock = ObjectManager.getBlock("shadowcrystal");
      double secondaryChance = 0.05D;
      int stripNumber = 1;

      for(int x = originX - radius; x < originX + radius; ++x) {
         float stripNormal = (float)stripNumber / (float)radius;
         if (stripNumber > radius) {
            stripNormal = (float)(radius - (stripNumber - radius)) / (float)radius;
         }

         int stripRadius = Math.round((float)radius * (float)Math.sin(Math.toRadians((double)(90.0F * stripNormal))));

         for(int z = originZ - stripRadius; z < originZ + stripRadius; ++z) {
            Block buildBlock = primaryBlock;
            if (world.rand.nextDouble() <= secondaryChance) {
               buildBlock = secondaryBlock;
            }

            world.setBlockState(new BlockPos(x, originY, z), buildBlock.getDefaultState(), 2);
            world.setBlockState(new BlockPos(x, originY - 1, z), buildBlock.getDefaultState(), 2);
            world.setBlockState(new BlockPos(x, originY - 2, z), buildBlock.getDefaultState(), 2);

            for(int y = originY + 1; y <= originY + height && y < world.getHeight(); ++y) {
               world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), 2);
            }
         }

         ++stripNumber;
      }

   }

   public void buildObstacles(World world, int originX, int originY, int originZ) {
      double angle = 0.0D;
      int radius = 50;
      List<int[]> decorationCoords = new ArrayList();

      while(angle < 360.0D) {
         angle += 5.0D + 5.0D * world.rand.nextDouble();
         double angleRadians = Math.toRadians(angle);
         double x = (double)radius * Math.cos(angleRadians) - Math.sin(angleRadians);
         double z = (double)radius * Math.sin(angleRadians) + Math.cos(angleRadians);
         decorationCoords.add(this.buildPillar(world, originX + (int)Math.ceil(x), originY, originZ + (int)Math.ceil(z)));
      }

   }

   public int[] buildPillar(World world, int originX, int originY, int originZ) {
      int radiusMax = 6;
      int height = 30;
      Block primaryBlock = ObjectManager.getBlock("shadowstonebrick");
      Block secondaryBlock = ObjectManager.getBlock("shadowstone");
      Block tetriaryBlock = ObjectManager.getBlock("shadowstonechiseled");
      Block capBlock = Blocks.OBSIDIAN;
      Block hazardBlock = ObjectManager.getBlock("shadowfire");
      double secondaryChance = 0.4D;
      double tetriaryChance = 0.05D;
      int[] decorationCoord = new int[]{originX, originY, originZ};
      int radius = radiusMax;
      int radiusHeight = height - 10;

      for(int y = originY; y <= originY + height; ++y) {
         --radiusHeight;
         boolean lowerRadius = radiusHeight <= 0;
         int stripNumber = 1;

         for(int x = originX - radius; x <= originX + radius; ++x) {
            float stripNormal = (float)stripNumber / (float)radius;
            if (stripNumber > radius) {
               stripNormal = (float)(radius - (stripNumber - radius)) / (float)radius;
            }

            int stripRadius = Math.round((float)radius * (float)Math.sin(Math.toRadians((double)(90.0F * stripNormal))));

            for(int z = originZ - stripRadius; z <= originZ + stripRadius; ++z) {
               if (!lowerRadius && y != originY + height) {
                  if (radiusHeight - 1 > 0 && y != originY + height - 1) {
                     if (world.rand.nextDouble() > secondaryChance) {
                        world.setBlockState(new BlockPos(x, y, z), primaryBlock.getDefaultState(), 2);
                     } else if (world.rand.nextDouble() > tetriaryChance) {
                        world.setBlockState(new BlockPos(x, y, z), secondaryBlock.getDefaultState(), 2);
                     } else {
                        world.setBlockState(new BlockPos(x, y, z), tetriaryBlock.getDefaultState(), 2);
                     }
                  } else {
                     world.setBlockState(new BlockPos(x, y, z), capBlock.getDefaultState(), 2);
                  }
               } else {
                  world.setBlockState(new BlockPos(x, y, z), hazardBlock.getDefaultState().withProperty(BlockFireBase.STATIC, Boolean.valueOf(true)), 2);
               }
            }

            ++stripNumber;
         }

         if (lowerRadius) {
            radiusHeight = radiusMax;
            --radius;
         }
      }

      return decorationCoord;
   }

   public void buildDecoration(World world, int originX, int originY, int originZ) {
      Block primaryBlock = Blocks.OBSIDIAN;
      Block hazardBlock = ObjectManager.getBlock("shadowfire");
      world.setBlockState(new BlockPos(originX, originY + 1, originZ), primaryBlock.getDefaultState(), 2);
      world.setBlockState(new BlockPos(originX, originY + 2, originZ), primaryBlock.getDefaultState(), 2);
      world.setBlockState(new BlockPos(originX, originY + 3, originZ), hazardBlock.getDefaultState().withProperty(BlockFireBase.STATIC, Boolean.valueOf(true)), 2);
      world.setBlockState(new BlockPos(originX + 1, originY + 1, originZ), primaryBlock.getDefaultState(), 2);
      world.setBlockState(new BlockPos(originX + 1, originY + 2, originZ), hazardBlock.getDefaultState().withProperty(BlockFireBase.STATIC, Boolean.valueOf(true)), 2);
      world.setBlockState(new BlockPos(originX - 1, originY + 1, originZ), primaryBlock.getDefaultState(), 2);
      world.setBlockState(new BlockPos(originX - 1, originY + 2, originZ), hazardBlock.getDefaultState().withProperty(BlockFireBase.STATIC, Boolean.valueOf(true)), 2);
      world.setBlockState(new BlockPos(originX, originY + 1, originZ + 1), primaryBlock.getDefaultState(), 2);
      world.setBlockState(new BlockPos(originX, originY + 2, originZ + 1), hazardBlock.getDefaultState().withProperty(BlockFireBase.STATIC, Boolean.valueOf(true)), 2);
      world.setBlockState(new BlockPos(originX, originY + 1, originZ - 1), primaryBlock.getDefaultState(), 2);
      world.setBlockState(new BlockPos(originX, originY + 2, originZ - 1), hazardBlock.getDefaultState().withProperty(BlockFireBase.STATIC, Boolean.valueOf(true)), 2);
   }
}
