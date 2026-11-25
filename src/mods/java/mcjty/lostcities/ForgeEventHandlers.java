package mcjty.lostcities;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import mcjty.lostcities.config.LostCityConfiguration;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.WorldTypeTools;
import mcjty.lostcities.dimensions.world.lost.BuildingInfo;
import mcjty.lostcities.dimensions.world.lost.CitySphere;
import mcjty.lostcities.dimensions.world.lost.cityassets.AssetRegistries;
import mcjty.lostcities.dimensions.world.lost.cityassets.PredefinedCity;
import mcjty.lostcities.dimensions.world.lost.cityassets.PredefinedSphere;
import mcjty.lostcities.varia.CustomTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent.CreateSpawnPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ForgeEventHandlers {
   public ForgeEventHandlers() {
   }

   @SubscribeEvent
   public void onCreateSpawnPoint(CreateSpawnPosition event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         if (!WorldTypeTools.isLostCities(world)) {
            return;
         }

         LostCityProfile profile = WorldTypeTools.getProfile(world);
         if (profile == null) {
            return;
         }

         LostCityChunkGenerator provider = WorldTypeTools.getLostCityChunkGenerator(world);
         Predicate<BlockPos> isSuitable = (pos) -> {
            return true;
         };
         boolean needsCheck = false;
         if (!profile.SPAWN_BIOME.isEmpty()) {
            Biome spawnBiome = (Biome)ForgeRegistries.BIOMES.getValue(new ResourceLocation(profile.SPAWN_BIOME));
            if (spawnBiome == null) {
               LostCities.setup.getLogger().error("Cannot find biome '" + profile.SPAWN_BIOME + "' for the player to spawn in !");
            } else {
               isSuitable = (blockPos) -> {
                  return world.getBiome(blockPos) == spawnBiome;
               };
               needsCheck = true;
            }
         } else if (!profile.SPAWN_CITY.isEmpty()) {
            PredefinedCity city = AssetRegistries.PREDEFINED_CITIES.get(profile.SPAWN_CITY);
            if (city == null) {
               LostCities.setup.getLogger().error("Cannot find city '" + profile.SPAWN_CITY + "' for the player to spawn in !");
            } else {
               float sqradius = (float)this.getSqRadius(city.getRadius(), 0.8F);
               isSuitable = (blockPos) -> {
                  return city.getDimension() == world.provider.getDimension() && CitySphere.squaredDistance(city.getChunkX() * 16 + 8, city.getChunkZ() * 16 + 8, blockPos.getX(), blockPos.getZ()) < (double)sqradius;
               };
               needsCheck = true;
            }
         } else if (!profile.SPAWN_SPHERE.isEmpty()) {
            if ("<in>".equals(profile.SPAWN_SPHERE)) {
               isSuitable = (blockPos) -> {
                  CitySphere sphere = CitySphere.getCitySphere(blockPos.getX() >> 4, blockPos.getZ() >> 4, provider);
                  if (!sphere.isEnabled()) {
                     return false;
                  } else {
                     float sqradius = (float)this.getSqRadius((int)sphere.getRadius(), 0.8F);
                     return sphere.getCenterPos().distanceSq(blockPos) < (double)sqradius;
                  }
               };
               needsCheck = true;
            } else if ("<out>".equals(profile.SPAWN_SPHERE)) {
               isSuitable = (blockPos) -> {
                  CitySphere sphere = CitySphere.getCitySphere(blockPos.getX() >> 4, blockPos.getZ() >> 4, provider);
                  if (!sphere.isEnabled()) {
                     return true;
                  } else {
                     float sqradius = sphere.getRadius() * sphere.getRadius();
                     return sphere.getCenterPos().distanceSq(new BlockPos(blockPos.getX(), sphere.getCenterPos().getY(), blockPos.getZ())) > (double)sqradius;
                  }
               };
               needsCheck = true;
            } else {
               PredefinedSphere sphere = AssetRegistries.PREDEFINED_SPHERES.get(profile.SPAWN_SPHERE);
               if (sphere == null) {
                  LostCities.setup.getLogger().error("Cannot find sphere '" + profile.SPAWN_SPHERE + "' for the player to spawn in !");
               } else {
                  float sqradius = (float)this.getSqRadius(sphere.getRadius(), 0.8F);
                  isSuitable = (blockPos) -> {
                     return sphere.getDimension() == world.provider.getDimension() && CitySphere.squaredDistance(sphere.getChunkX() * 16 + 8, sphere.getChunkZ() * 16 + 8, blockPos.getX(), blockPos.getZ()) < (double)sqradius;
                  };
                  needsCheck = true;
               }
            }
         }

         if (profile.SPAWN_NOT_IN_BUILDING) {
            isSuitable = isSuitable.and((blockPos) -> {
               return this.isOutsideBuilding(provider, blockPos);
            });
            needsCheck = true;
         }

         switch(profile.LANDSCAPE_TYPE) {
         case DEFAULT:
         case CAVERN:
            if (needsCheck) {
               this.findSafeSpawnPoint(world, provider, isSuitable);
               event.setCanceled(true);
            }
            break;
         case FLOATING:
         case SPACE:
            this.findSafeSpawnPoint(world, provider, isSuitable);
            event.setCanceled(true);
         }
      }

   }

   private boolean isOutsideBuilding(LostCityChunkGenerator provider, BlockPos pos) {
      BuildingInfo info = BuildingInfo.getBuildingInfo(pos.getX() >> 4, pos.getZ() >> 4, provider);
      return !info.isCity() || !info.hasBuilding;
   }

   private int getSqRadius(int radius, float pct) {
      return (int)((float)radius * pct * (float)radius * pct);
   }

   private void findSafeSpawnPoint(World world, LostCityChunkGenerator provider, @Nonnull Predicate<BlockPos> isSuitable) {
      Random rand = new Random(world.getSeed());
      rand.nextFloat();
      rand.nextFloat();
      int radius = 200;
      int attempts = 0;

      while(true) {
         for(int i = 0; i < 200; ++i) {
            int x = rand.nextInt(radius * 2) - radius;
            int z = rand.nextInt(radius * 2) - radius;
            ++attempts;
            if (isSuitable.test(new BlockPos(x, 128, z))) {
               LostCityProfile profile = BuildingInfo.getProfile(x >> 4, z >> 4, provider);

               for(int y = profile.GROUNDLEVEL - 5; y < 125; ++y) {
                  BlockPos pos = new BlockPos(x, y, z);
                  if (this.isValidStandingPosition(world, pos)) {
                     world.setSpawnPoint(pos.up());
                     return;
                  }
               }
            }
         }

         radius += 100;
         if (attempts > 10000) {
            break;
         }
      }

      LostCities.setup.getLogger().error("Can't find a valid spawn position!");
      throw new RuntimeException("Can't find a valid spawn position!");
   }

   private boolean isValidStandingPosition(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return state.getBlock().isTopSolid(state) && state.getBlock().isFullCube(state) && state.getBlock().isOpaqueCube(state) && world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2));
   }

   private boolean isValidSpawnBed(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      if (!(state.getBlock() instanceof BlockBed)) {
         return false;
      } else {
         EnumFacing direction = Blocks.BED.getBedDirection(state, world, pos);
         Block b1 = world.getBlockState(pos.down()).getBlock();
         Block b2 = world.getBlockState(pos.offset(direction.getOpposite()).down()).getBlock();
         Block b = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(LostCityConfiguration.SPECIAL_BED_BLOCK));
         if (b1 == b && b2 == b) {
            if (world.getBlockState(pos.offset(direction)).getBlock() != Blocks.SKULL) {
               return false;
            } else if (world.getBlockState(pos.offset(direction.rotateY())).getBlock() != Blocks.SKULL) {
               return false;
            } else if (world.getBlockState(pos.offset(direction.rotateYCCW())).getBlock() != Blocks.SKULL) {
               return false;
            } else if (world.getBlockState(pos.offset(direction.getOpposite(), 2)).getBlock() != Blocks.SKULL) {
               return false;
            } else if (world.getBlockState(pos.offset(direction.getOpposite()).offset(direction.getOpposite().rotateY())).getBlock() != Blocks.SKULL) {
               return false;
            } else {
               return world.getBlockState(pos.offset(direction.getOpposite()).offset(direction.getOpposite().rotateYCCW())).getBlock() == Blocks.SKULL;
            }
         } else {
            return false;
         }
      }
   }

   private BlockPos findValidTeleportLocation(World world, BlockPos start) {
      int chunkX = start.getX() >> 4;
      int chunkZ = start.getZ() >> 4;
      int y = start.getY();
      BlockPos pos = this.findValidTeleportLocation(world, chunkX, chunkZ, y);
      if (pos != null) {
         return pos;
      } else {
         for(int r = 1; r < 50; ++r) {
            for(int i = -r; i < r; ++i) {
               pos = this.findValidTeleportLocation(world, chunkX + i, chunkZ - r, y);
               if (pos != null) {
                  return pos;
               }

               pos = this.findValidTeleportLocation(world, chunkX + r, chunkZ + i, y);
               if (pos != null) {
                  return pos;
               }

               pos = this.findValidTeleportLocation(world, chunkX + r - i, chunkZ + r, y);
               if (pos != null) {
                  return pos;
               }

               pos = this.findValidTeleportLocation(world, chunkX - r, chunkZ + r - i, y);
               if (pos != null) {
                  return pos;
               }
            }
         }

         return null;
      }
   }

   private BlockPos findValidTeleportLocation(World world, int chunkX, int chunkZ, int y) {
      BlockPos bestSpot = null;

      for(int dy = 0; dy < 255; ++dy) {
         for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
               if (y + dy < 250) {
                  BlockPos p = new BlockPos(chunkX * 16 + x, y + dy, chunkZ * 16 + z);
                  if (this.isValidSpawnBed(world, p)) {
                     return p.up();
                  }

                  if (bestSpot == null && this.isValidStandingPosition(world, p)) {
                     bestSpot = p.up();
                  }
               }

               if (y - dy > 1) {
                  BlockPos p = new BlockPos(chunkX * 16 + x, y - dy, chunkZ * 16 + z);
                  if (this.isValidSpawnBed(world, p)) {
                     return p.up();
                  }

                  if (bestSpot == null && this.isValidStandingPosition(world, p)) {
                     bestSpot = p.up();
                  }
               }
            }
         }
      }

      return bestSpot;
   }

   @SubscribeEvent
   public void onPlayerSleepInBedEvent(PlayerSleepInBedEvent event) {
      if (LostCityConfiguration.DIMENSION_ID != -1) {
         World world = event.getEntityPlayer().getEntityWorld();
         if (!world.isRemote) {
            BlockPos bedLocation = event.getPos();
            if (this.isValidSpawnBed(world, bedLocation)) {
               if (world.provider.getDimension() == LostCityConfiguration.DIMENSION_ID) {
                  event.setResult(Result.DENY);
                  WorldServer destWorld = DimensionManager.getWorld(0);
                  BlockPos location = this.findLocation(bedLocation, destWorld);
                  CustomTeleporter.teleportToDimension(event.getEntityPlayer(), 0, location);
               } else {
                  event.setResult(Result.DENY);
                  WorldServer destWorld = event.getEntity().getEntityWorld().getMinecraftServer().getWorld(LostCityConfiguration.DIMENSION_ID);
                  BlockPos location = this.findLocation(bedLocation, destWorld);
                  CustomTeleporter.teleportToDimension(event.getEntityPlayer(), LostCityConfiguration.DIMENSION_ID, location);
               }

            }
         }
      }
   }

   private BlockPos findLocation(BlockPos bedLocation, WorldServer destWorld) {
      BlockPos top = destWorld.getTopSolidOrLiquidBlock(bedLocation);
      BlockPos location = this.findValidTeleportLocation(destWorld, top);
      if (location == null) {
         location = top;
         if (destWorld.isAirBlock(top.down())) {
            destWorld.setBlockState(bedLocation, Blocks.COBBLESTONE.getDefaultState());
            location = bedLocation.up();
         }
      }

      return location;
   }
}
