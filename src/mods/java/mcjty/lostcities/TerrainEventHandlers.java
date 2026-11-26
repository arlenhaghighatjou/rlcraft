package mcjty.lostcities;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.WorldTypeTools;
import mcjty.lostcities.dimensions.world.lost.BuildingInfo;
import mcjty.lostcities.dimensions.world.lost.CitySphere;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class TerrainEventHandlers {
   public TerrainEventHandlers() {
   }

   public void register(EventBus bus) {
      bus.register(Decorate.class, this::onCreateDecorate);
   }

   public void onCreateDecorate(Decorate event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         if (!WorldTypeTools.isLostCities(world)) {
            return;
         }

         LostCityChunkGenerator provider = WorldTypeTools.getLostCityChunkGenerator(world);
         if (provider == null) {
            return;
         }

         switch(event.getType()) {
         case CLAY:
         case DEAD_BUSH:
         case ICE:
         case LAKE_LAVA:
         case ROCK:
         case SAND:
         case SAND_PASS2:
         case CUSTOM:
         default:
            break;
         case FOSSIL:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_FOSSILS) {
               event.setResult(Result.DENY);
            }
            break;
         case DESERT_WELL:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_DESERT_WELL) {
               event.setResult(Result.DENY);
            }
            break;
         case LAKE_WATER:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_LAKE_WATER) {
               event.setResult(Result.DENY);
            }
            break;
         case PUMPKIN:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_PUMPKINS) {
               event.setResult(Result.DENY);
            }
            break;
         case GRASS:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_GRASS) {
               event.setResult(Result.DENY);
            }
            break;
         case SHROOM:
         case BIG_SHROOM:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_MUSHROOMS) {
               event.setResult(Result.DENY);
            }
            break;
         case CACTUS:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_CACTII) {
               event.setResult(Result.DENY);
            }
            break;
         case REED:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_REEDS) {
               event.setResult(Result.DENY);
            }
            break;
         case LILYPAD:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_LILYPADS) {
               event.setResult(Result.DENY);
            }
            break;
         case FLOWERS:
            if (this.getProfile(event, (WorldServer)world).AVOID_GENERATED_FLOWERS) {
               event.setResult(Result.DENY);
            }
            break;
         case TREE:
            LostCityProfile profile = WorldTypeTools.getProfile(world);
            if (profile.AVOID_GENERATED_TREES) {
               event.setResult(Result.DENY);
            } else if (profile.isSpace() && profile.CITYSPHERE_LANDSCAPE_OUTSIDE) {
               int chunkX = event.getPos().getX() >> 4;
               int chunkZ = event.getPos().getZ() >> 4;
               CitySphere sphere = CitySphere.getCitySphere(chunkX, chunkZ, provider);
               if (!sphere.isEnabled()) {
                  return;
               }

               if (CitySphere.onCitySphereBorder(chunkX, chunkZ, provider)) {
                  float radius = sphere.getRadius();
                  double sqradiusOffset = (double)((radius - 2.0F) * (radius - 2.0F));
                  BlockPos cc = sphere.getCenterPos();
                  int cx = cc.getX();
                  int cz = cc.getZ();
                  Biome biome = world.getBiomeForCoordsBody(event.getPos());
                  BiomeDecorator decorator = biome.decorator;
                  int treesPerChunk = decorator.treesPerChunk;
                  if (world.rand.nextFloat() < decorator.extraTreeChance) {
                     ++treesPerChunk;
                  }

                  for(int i = 0; i < treesPerChunk; ++i) {
                     int treex = world.rand.nextInt(16) + 8;
                     int treez = world.rand.nextInt(16) + 8;
                     WorldGenAbstractTree worldgenabstracttree = biome.getRandomTreeFeature(world.rand);
                     worldgenabstracttree.setDecorationDefaults();
                     BlockPos blockpos = world.getHeight(event.getPos().add(treex, 0, treez));
                     if (CitySphere.squaredDistance(cx, cz, treex, treez) < sqradiusOffset && worldgenabstracttree.generate(world, world.rand, blockpos)) {
                        worldgenabstracttree.generateSaplings(world, world.rand, blockpos);
                     }
                  }

                  event.setResult(Result.DENY);
               }
            }
         }
      }

   }

   private LostCityProfile getProfile(Decorate event, WorldServer world) {
      LostCityChunkGenerator provider = WorldTypeTools.getChunkGenerator(world.provider.getDimension());
      int chunkX = event.getPos().getX() >> 4;
      int chunkZ = event.getPos().getZ() >> 4;
      return BuildingInfo.getProfile(chunkX, chunkZ, provider);
   }
}
