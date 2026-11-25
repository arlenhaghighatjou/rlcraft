package com.lycanitesmobs.core.worldgen.mobevents;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityAsmodeus;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWall;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AsmodeusStructureBuilder extends StructureBuilder {
   public AsmodeusStructureBuilder() {
      this.name = "asmodeus";
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
            BaseProjectileEntity baseProjectileEntity = new EntityHellfireWall(world, (double)originX, (double)(originY + 10 * i), (double)originZ);
            baseProjectileEntity.projectileLife = 400;
            world.spawnEntity(baseProjectileEntity);
            if (worldExt != null) {
               worldExt.bossUpdate(baseProjectileEntity);
               worldExt.overrideBossRange(baseProjectileEntity, CreatureManager.getInstance().getCreature("asmodeus").bossNearbyRange);
            }
         }
      }

      if (ticks == 60) {
         this.buildArenaFloor(world, originX, originY, originZ);
      }

      if (ticks == 100) {
         this.buildArenaWalls(world, originX, originY, originZ);
      }

      if (ticks == 140) {
         this.buildObstacles(world, originX, originY, originZ);
      }

      if (ticks >= 200 && ticks % 10 == 0) {
         world.createExplosion((Entity)null, (double)(originX - 20 + world.rand.nextInt(40)), (double)(originY + 25 + world.rand.nextInt(10)), (double)(originZ - 20 + world.rand.nextInt(40)), 2.0F, true);
      }

      if (ticks == 400) {
         BaseCreatureEntity baseCreatureEntity = new EntityAsmodeus(world);
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
      int radius = 80;
      int height = 30;
      int minX = originX - radius;
      int maxX = originX + radius;
      int minY = originY;
      int maxY = originY + height;
      int minZ = originZ - radius;
      int maxZ = originZ + radius;
      IBlockState floor = ObjectManager.getBlock("aberrantstonetile").getDefaultState();
      IBlockState light = ObjectManager.getBlock("aberrantcrystal").getDefaultState();
      IBlockState trimming = ObjectManager.getBlock("aberrantstonechiseled").getDefaultState();

      for(int x = minX; x <= maxX; ++x) {
         for(int z = minZ; z <= maxZ; ++z) {
            int topY = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();

            for(int y = minY; y <= maxY; ++y) {
               BlockPos buildPos = new BlockPos(x, y, z);
               if (y == minY) {
                  if (x != minX && x != maxX && z != minZ && z != maxZ) {
                     if (x % 6 == 0 && z % 6 == 0) {
                        world.setBlockState(buildPos, light, 2);
                     } else {
                        world.setBlockState(buildPos, floor, 2);
                     }
                  } else {
                     world.setBlockState(buildPos, trimming, 2);
                  }
               } else {
                  if (y > minY + 5 && y >= topY) {
                     break;
                  }

                  world.setBlockState(buildPos, Blocks.AIR.getDefaultState(), 2);
               }
            }
         }
      }

   }

   public void buildArenaWalls(World world, int originX, int originY, int originZ) {
      int radius = 80;
      int thickness = 4;
      int height = 20;
      int minX = originX - (radius + thickness);
      int maxX = originX + radius + thickness;
      int minY = originY;
      int maxY = originY + height;
      int minZ = originZ - (radius + thickness);
      int maxZ = originZ + radius + thickness;
      IBlockState base = ObjectManager.getBlock("aberrantstonebrick").getDefaultState();
      IBlockState light = ObjectManager.getBlock("aberrantcrystal").getDefaultState();
      IBlockState trimming = ObjectManager.getBlock("aberrantstonechiseled").getDefaultState();
      IBlockState top = ObjectManager.getBlock("aberrantstonepolished").getDefaultState();
      IBlockState fireBase = Blocks.OBSIDIAN.getDefaultState();
      IBlockState fire = ObjectManager.getBlock("hellfire").getDefaultState().withProperty(BlockFireBase.STATIC, Boolean.valueOf(true));

      for(int x = minX; x <= maxX; ++x) {
         for(int z = minZ; z <= maxZ; ++z) {
            if (x <= minX + thickness || x >= maxX - thickness || z <= minZ + thickness || z >= maxZ - thickness) {
               for(int y = minY; y <= maxY; ++y) {
                  BlockPos buildPos = new BlockPos(x, y, z);
                  if (y < maxY - 2) {
                     if (y - 1 == 0) {
                        world.setBlockState(buildPos, light, 2);
                     } else if (y % 8 == 0) {
                        world.setBlockState(buildPos, trimming, 2);
                     } else {
                        world.setBlockState(buildPos, base, 2);
                     }
                  } else if (y == maxY - 2) {
                     world.setBlockState(buildPos, top, 2);
                  } else if (y == maxY - 1) {
                     world.setBlockState(buildPos, fireBase, 2);
                  } else {
                     world.setBlockState(buildPos, fire, 2);
                  }
               }
            }
         }
      }

   }

   public void buildObstacles(World world, int originX, int originY, int originZ) {
      int gap = 20;

      for(int x = -2; x <= 2; ++x) {
         for(int z = -2; z <= 2; ++z) {
            if (x < -1 || x > 1 || z < -1 || z > 1) {
               this.buildPillar(world, originX + x * gap, originY, originZ + z * gap);
            }
         }
      }

   }

   public void buildPillar(World world, int originX, int originY, int originZ) {
      int radius = 2;
      int height = 30;
      int minX = originX - radius;
      int maxX = originX + radius;
      int minY = originY;
      int maxY = originY + height;
      int minZ = originZ - radius;
      int maxZ = originZ + radius;
      IBlockState base = ObjectManager.getBlock("aberrantstonepillar").getDefaultState();
      IBlockState light = ObjectManager.getBlock("aberrantcrystal").getDefaultState();
      IBlockState trimming = ObjectManager.getBlock("aberrantstonechiseled").getDefaultState();
      IBlockState top = ObjectManager.getBlock("aberrantstonepolished").getDefaultState();
      IBlockState fireBase = Blocks.OBSIDIAN.getDefaultState();
      IBlockState fire = ObjectManager.getBlock("hellfire").getDefaultState().withProperty(BlockFireBase.STATIC, Boolean.valueOf(true));

      for(int x = minX; x <= maxX; ++x) {
         for(int z = minZ; z <= maxZ; ++z) {
            for(int y = minY; y <= maxY; ++y) {
               BlockPos buildPos = new BlockPos(x, y, z);
               if (y < maxY - 2) {
                  if (y % 7 == 0) {
                     world.setBlockState(buildPos, light, 2);
                  } else if (y % 8 == 0) {
                     world.setBlockState(buildPos, trimming, 2);
                  } else {
                     world.setBlockState(buildPos, base, 2);
                  }
               } else if (y == maxY - 2) {
                  world.setBlockState(buildPos, top, 2);
               } else if (y == maxY - 1) {
                  world.setBlockState(buildPos, fireBase, 2);
               } else {
                  world.setBlockState(buildPos, fire, 2);
               }
            }
         }
      }

   }
}
