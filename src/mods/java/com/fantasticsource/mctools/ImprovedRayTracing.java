package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import java.util.ArrayList;
import java.util.HashSet;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ImprovedRayTracing {
   protected static HashSet<IBlockState> transparentBlockstates = new HashSet();
   protected static HashSet<IBlockState> nonTransparentBlockstates = new HashSet();
   protected static HashSet<Block> transparentBlocks = new HashSet();
   protected static HashSet<Block> nonTransparentBlocks = new HashSet();
   protected static HashSet<Class<? extends Block>> transparentBlockSuperclasses = new HashSet();
   protected static HashSet<Class<? extends Block>> nonTransparentBlockSuperclasses = new HashSet();
   protected static HashSet<Class<? extends Block>> transparentBlockClasses = new HashSet();
   protected static HashSet<Class<? extends Block>> nonTransparentBlockClasses = new HashSet();
   protected static HashSet<Class<? extends Block>> ignoredBlockClasses = new HashSet();
   protected static HashSet<Material> transparentMaterials = new HashSet();
   protected static HashSet<Material> nonTransparentMaterials = new HashSet();
   protected static final int ITERATION_WARNING_THRESHOLD = 200;
   protected static long lastWarning = -1L;
   protected static int errorCount = 0;

   public ImprovedRayTracing() {
   }

   public static void reloadConfigs() {
      transparentBlockstates.clear();
      nonTransparentBlockstates.clear();
      transparentBlocks.clear();
      nonTransparentBlocks.clear();
      transparentBlockSuperclasses.clear();
      nonTransparentBlockSuperclasses.clear();
      transparentBlockClasses.clear();
      nonTransparentBlockClasses.clear();
      ignoredBlockClasses.clear();
      transparentMaterials.clear();
      nonTransparentMaterials.clear();

      for(String s : FantasticConfig.raytraceSettings.rayBlockstateFilter) {
         if (!s.trim().equals("")) {
            String[] tokens = Tools.fixedSplit(s, ",");
            if (tokens.length != 2) {
               System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter: " + s);
            } else {
               String[] tokens2 = Tools.fixedSplit(tokens[0].trim(), ":");
               if (tokens2.length != 3) {
                  System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter: " + s);
               } else {
                  Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tokens2[0].trim(), tokens2[1].trim()));
                  if (block == null) {
                     System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter; block not found: " + s);
                  } else {
                     int meta;
                     try {
                        meta = Integer.parseInt(tokens2[2].trim());
                     } catch (NumberFormatException var9) {
                        System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter: " + s);
                        continue;
                     }

                     IBlockState blockState = block.getStateFromMeta(meta);
                     if (block.getMetaFromState(blockState) != meta) {
                        System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter; state not found for meta: " + s);
                     } else if (Boolean.parseBoolean(tokens[1].trim())) {
                        transparentBlockstates.add(blockState);
                     } else {
                        nonTransparentBlockstates.add(blockState);
                     }
                  }
               }
            }
         }
      }

      for(String s : FantasticConfig.raytraceSettings.rayBlockFilter) {
         if (!s.trim().equals("")) {
            String[] tokens = Tools.fixedSplit(s, ",");
            if (tokens.length != 2) {
               System.err.println(TextFormatting.RED + "Invalid raytrace block filter: " + s);
            } else {
               String[] tokens2 = Tools.fixedSplit(tokens[0].trim(), ":");
               if (tokens2.length != 2) {
                  System.err.println(TextFormatting.RED + "Invalid raytrace block filter: " + s);
               } else {
                  Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tokens2[0].trim(), tokens2[1].trim()));
                  if (block == null) {
                     System.err.println(TextFormatting.RED + "Invalid raytrace block filter; block not found: " + s);
                  } else if (Boolean.parseBoolean(tokens[1].trim())) {
                     transparentBlocks.add(block);
                  } else {
                     nonTransparentBlocks.add(block);
                  }
               }
            }
         }
      }

      for(String s : FantasticConfig.raytraceSettings.rayBlockSuperclassFilter) {
         if (!s.trim().equals("")) {
            String[] tokens = Tools.fixedSplit(s, ",");
            if (tokens.length != 2) {
               System.err.println(TextFormatting.RED + "Invalid raytrace block superclass filter: " + s);
            } else {
               Class<? extends Block> cls = ReflectionTool.getClassByName(tokens[0].trim());
               if (cls == null) {
                  System.err.println(TextFormatting.RED + "Invalid raytrace block superclass filter; class not found: " + s);
               } else if (Boolean.parseBoolean(tokens[1].trim())) {
                  transparentBlockSuperclasses.add(cls);
               } else {
                  nonTransparentBlockSuperclasses.add(cls);
               }
            }
         }
      }

      for(String s : FantasticConfig.raytraceSettings.rayMaterialFilter) {
         if (!s.trim().equals("")) {
            String[] tokens = Tools.fixedSplit(s, ",");
            if (tokens.length != 2) {
               System.err.println(TextFormatting.RED + "Invalid raytrace material filter: " + s);
            } else {
               String var29 = tokens[0].trim().toLowerCase();
               byte var30 = -1;
               switch(var29.hashCode()) {
               case -1953127677:
                  if (var29.equals("crafted_snow")) {
                     var30 = 25;
                  }
                  break;
               case -1498088022:
                  if (var29.equals("circuits")) {
                     var30 = 16;
                  }
                  break;
               case -1368035283:
                  if (var29.equals("cactus")) {
                     var30 = 26;
                  }
                  break;
               case -1367592757:
                  if (var29.equals("carpet")) {
                     var30 = 17;
                  }
                  break;
               case -1237460601:
                  if (var29.equals("ground")) {
                     var30 = 2;
                  }
                  break;
               case -1218452597:
                  if (var29.equals("redstone_light")) {
                     var30 = 19;
                  }
                  break;
               case -1106736996:
                  if (var29.equals("leaves")) {
                     var30 = 9;
                  }
                  break;
               case -987991687:
                  if (var29.equals("piston")) {
                     var30 = 33;
                  }
                  break;
               case -985762968:
                  if (var29.equals("plants")) {
                     var30 = 10;
                  }
                  break;
               case -982480788:
                  if (var29.equals("portal")) {
                     var30 = 30;
                  }
                  break;
               case -895764774:
                  if (var29.equals("sponge")) {
                     var30 = 12;
                  }
                  break;
               case -608029735:
                  if (var29.equals("dragon_egg")) {
                     var30 = 29;
                  }
                  break;
               case -333143113:
                  if (var29.equals("barrier")) {
                     var30 = 34;
                  }
                  break;
               case 96586:
                  if (var29.equals("air")) {
                     var30 = 0;
                  }
                  break;
               case 104075:
                  if (var29.equals("ice")) {
                     var30 = 22;
                  }
                  break;
               case 115002:
                  if (var29.equals("tnt")) {
                     var30 = 20;
                  }
                  break;
               case 117588:
                  if (var29.equals("web")) {
                     var30 = 32;
                  }
                  break;
               case 3045944:
                  if (var29.equals("cake")) {
                     var30 = 31;
                  }
                  break;
               case 3056225:
                  if (var29.equals("clay")) {
                     var30 = 27;
                  }
                  break;
               case 3143222:
                  if (var29.equals("fire")) {
                     var30 = 14;
                  }
                  break;
               case 3241160:
                  if (var29.equals("iron")) {
                     var30 = 5;
                  }
                  break;
               case 3314400:
                  if (var29.equals("lava")) {
                     var30 = 8;
                  }
                  break;
               case 3506021:
                  if (var29.equals("rock")) {
                     var30 = 4;
                  }
                  break;
               case 3522692:
                  if (var29.equals("sand")) {
                     var30 = 15;
                  }
                  break;
               case 3535235:
                  if (var29.equals("snow")) {
                     var30 = 24;
                  }
                  break;
               case 3619754:
                  if (var29.equals("vine")) {
                     var30 = 11;
                  }
                  break;
               case 3655341:
                  if (var29.equals("wood")) {
                     var30 = 3;
                  }
                  break;
               case 92975308:
                  if (var29.equals("anvil")) {
                     var30 = 6;
                  }
                  break;
               case 94756378:
                  if (var29.equals("cloth")) {
                     var30 = 13;
                  }
                  break;
               case 94848049:
                  if (var29.equals("coral")) {
                     var30 = 21;
                  }
                  break;
               case 98436988:
                  if (var29.equals("glass")) {
                     var30 = 18;
                  }
                  break;
               case 98545535:
                  if (var29.equals("gourd")) {
                     var30 = 28;
                  }
                  break;
               case 98615734:
                  if (var29.equals("grass")) {
                     var30 = 1;
                  }
                  break;
               case 112903447:
                  if (var29.equals("water")) {
                     var30 = 7;
                  }
                  break;
               case 140908100:
                  if (var29.equals("packed_ice")) {
                     var30 = 23;
                  }
                  break;
               case 1699106016:
                  if (var29.equals("structure_void")) {
                     var30 = 35;
                  }
               }

               Material material;
               switch(var30) {
               case 0:
                  material = Material.AIR;
                  break;
               case 1:
                  material = Material.GRASS;
                  break;
               case 2:
                  material = Material.GROUND;
                  break;
               case 3:
                  material = Material.WOOD;
                  break;
               case 4:
                  material = Material.ROCK;
                  break;
               case 5:
                  material = Material.IRON;
                  break;
               case 6:
                  material = Material.ANVIL;
                  break;
               case 7:
                  material = Material.WATER;
                  break;
               case 8:
                  material = Material.LAVA;
                  break;
               case 9:
                  material = Material.LEAVES;
                  break;
               case 10:
                  material = Material.PLANTS;
                  break;
               case 11:
                  material = Material.VINE;
                  break;
               case 12:
                  material = Material.SPONGE;
                  break;
               case 13:
                  material = Material.CLOTH;
                  break;
               case 14:
                  material = Material.FIRE;
                  break;
               case 15:
                  material = Material.SAND;
                  break;
               case 16:
                  material = Material.CIRCUITS;
                  break;
               case 17:
                  material = Material.CARPET;
                  break;
               case 18:
                  material = Material.GLASS;
                  break;
               case 19:
                  material = Material.REDSTONE_LIGHT;
                  break;
               case 20:
                  material = Material.TNT;
                  break;
               case 21:
                  material = Material.CORAL;
                  break;
               case 22:
                  material = Material.ICE;
                  break;
               case 23:
                  material = Material.PACKED_ICE;
                  break;
               case 24:
                  material = Material.SNOW;
                  break;
               case 25:
                  material = Material.CRAFTED_SNOW;
                  break;
               case 26:
                  material = Material.CACTUS;
                  break;
               case 27:
                  material = Material.CLAY;
                  break;
               case 28:
                  material = Material.GOURD;
                  break;
               case 29:
                  material = Material.DRAGON_EGG;
                  break;
               case 30:
                  material = Material.PORTAL;
                  break;
               case 31:
                  material = Material.CAKE;
                  break;
               case 32:
                  material = Material.WEB;
                  break;
               case 33:
                  material = Material.PISTON;
                  break;
               case 34:
                  material = Material.BARRIER;
                  break;
               case 35:
                  material = Material.STRUCTURE_VOID;
                  break;
               default:
                  System.err.println(TextFormatting.RED + "Invalid raytrace material filter; material not found: " + s);
                  continue;
               }

               if (Boolean.parseBoolean(tokens[1].trim())) {
                  transparentMaterials.add(material);
               } else {
                  nonTransparentMaterials.add(material);
               }
            }
         }
      }

   }

   public static double entityPenetration(Entity fromEyesOf, double maxDistance, Entity target, boolean collideOnAllSolids) {
      if (fromEyesOf.world != target.world) {
         return Double.NaN;
      } else {
         Vec3d eyes = fromEyesOf.getPositionVector().addVector(0.0D, (double)fromEyesOf.getEyeHeight(), 0.0D);
         return entityPenetration(target, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), collideOnAllSolids);
      }
   }

   public static double entityPenetration(Entity target, Vec3d vecStart, Vec3d vecEnd, double maxDistance, boolean collideOnAllSolids) {
      return entityPenetration(target, vecStart, vecStart.add(vecEnd.subtract(vecStart).normalize().scale(maxDistance)), collideOnAllSolids);
   }

   public static double entityPenetration(Entity target, Vec3d vecStart, Vec3d vecEnd, boolean collideOnAllSolids) {
      RayTraceResult entityEnd = rayTraceEntity(target, vecEnd, vecStart);
      if (entityEnd != null && entityEnd.typeOfHit != Type.MISS) {
         RayTraceResult entityStart = rayTraceEntity(target, vecStart, vecEnd);
         RayTraceResult blockHit = rayTraceBlocks(target.world, vecStart, entityEnd.hitVec, collideOnAllSolids);
         if (blockHit.typeOfHit == Type.MISS) {
            return entityStart.hitVec.distanceTo(entityEnd.hitVec);
         } else {
            return blockHit.hitVec == null ? -vecStart.distanceTo(entityStart.hitVec) : vecStart.distanceTo(blockHit.hitVec) - vecStart.distanceTo(entityStart.hitVec);
         }
      } else {
         return Double.NaN;
      }
   }

   public static RayTraceResult rayTraceEntity(Entity fromEyesOf, double maxDistance, Entity target) {
      if (fromEyesOf.world != target.world) {
         return null;
      } else {
         Vec3d eyes = fromEyesOf.getPositionVector().addVector(0.0D, (double)fromEyesOf.getEyeHeight(), 0.0D);
         return rayTraceEntity(target, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)));
      }
   }

   public static RayTraceResult rayTraceEntity(Entity target, Vec3d vecStart, Vec3d vecEnd, double maxDistance) {
      return rayTraceEntity(target, vecStart, vecStart.add(vecEnd.subtract(vecStart).normalize().scale(maxDistance)));
   }

   public static RayTraceResult rayTraceEntity(Entity target, Vec3d vecStart, Vec3d vecEnd) {
      return target.getEntityBoundingBox().calculateIntercept(vecStart, vecEnd);
   }

   public static boolean isUnobstructed(Entity fromEyesOf, double maxDistance, boolean collideOnAllSolids) {
      Vec3d eyes = fromEyesOf.getPositionVector().addVector(0.0D, (double)fromEyesOf.getEyeHeight(), 0.0D);
      return isUnobstructed(fromEyesOf.world, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), collideOnAllSolids);
   }

   public static boolean isUnobstructed(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, boolean collideOnAllSolids) {
      return isUnobstructed(world, vecStart, vecStart.add(vecEnd.subtract(vecStart).normalize().scale(maxDistance)), collideOnAllSolids);
   }

   public static boolean isUnobstructed(World world, Vec3d vecStart, Vec3d vecEnd, boolean collideOnAllSolids) {
      return rayTraceBlocks(world, vecStart, vecEnd, collideOnAllSolids).typeOfHit == Type.MISS;
   }

   @Nonnull
   public static BlockPos[] blocksInRay(Entity fromEyesOf, double maxDistance, boolean collideOnAllSolids) {
      return blocksInRay(fromEyesOf, maxDistance, 200, collideOnAllSolids);
   }

   @Nonnull
   public static BlockPos[] blocksInRay(Entity fromEyesOf, double maxDistance, int maxBlocks, boolean collideOnAllSolids) {
      Vec3d eyes = fromEyesOf.getPositionVector().addVector(0.0D, (double)fromEyesOf.getEyeHeight(), 0.0D);
      return blocksInRay(fromEyesOf.world, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), maxBlocks, collideOnAllSolids);
   }

   @Nonnull
   public static BlockPos[] blocksInRay(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, boolean collideOnAllSolids) {
      return blocksInRay(world, vecStart, vecEnd, maxDistance, 200, collideOnAllSolids);
   }

   @Nonnull
   public static BlockPos[] blocksInRay(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, int maxBlocks, boolean collideOnAllSolids) {
      return blocksInRay(world, vecStart, vecStart.add(vecEnd.subtract(vecStart).normalize().scale(maxDistance)), maxBlocks, collideOnAllSolids);
   }

   @Nonnull
   public static BlockPos[] blocksInRay(World world, Vec3d vecStart, Vec3d vecEnd, boolean collideOnAllSolids) {
      return blocksInRay(world, vecStart, vecEnd, 200, collideOnAllSolids);
   }

   @Nonnull
   public static BlockPos[] blocksInRay(World world, Vec3d vecStart, Vec3d vecEnd, int maxBlocks, boolean collideOnAllSolids) {
      world.profiler.startSection("Fantastic Lib: Blocks In Ray");
      ArrayList<BlockPos> blocks = new ArrayList();
      BlockPos pos = new BlockPos(vecStart);
      BlockPos endPos = new BlockPos(vecEnd);
      if (vecEnd.x > vecStart.x && vecEnd.x == (double)((int)vecEnd.x)) {
         endPos = new BlockPos(endPos.getX() - 1, endPos.getY(), endPos.getZ());
      }

      if (vecEnd.y > vecStart.y && vecEnd.y == (double)((int)vecEnd.y)) {
         endPos = new BlockPos(endPos.getX(), endPos.getY() - 1, endPos.getZ());
      }

      if (vecEnd.z > vecStart.z && vecEnd.z == (double)((int)vecEnd.z)) {
         endPos = new BlockPos(endPos.getX(), endPos.getY(), endPos.getZ() - 1);
      }

      if (!world.isBlockLoaded(pos)) {
         world.profiler.endSection();
         return new BlockPos[0];
      } else {
         IBlockState state = world.getBlockState(pos);
         if ((collideOnAllSolids || !canSeeThrough(state)) && state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB) {
            RayTraceResult result = state.collisionRayTrace(world, pos, vecStart, vecEnd);
            if (result != null) {
               world.profiler.endSection();
               return new BlockPos[]{pos};
            }
         }

         blocks.add(pos);
         if (pos.getX() == endPos.getX() && pos.getY() == endPos.getY() && pos.getZ() == endPos.getZ()) {
            world.profiler.endSection();
            return (BlockPos[])blocks.toArray(new BlockPos[0]);
         } else {
            double xStart = vecStart.x;
            double yStart = vecStart.y;
            double zStart = vecStart.z;
            double xRange = vecEnd.x - xStart;
            double yRange = vecEnd.y - yStart;
            double zRange = vecEnd.z - zStart;
            int xDir = xRange > 0.0D ? 1 : (xRange < 0.0D ? -1 : 0);
            int yDir = yRange > 0.0D ? 1 : (yRange < 0.0D ? -1 : 0);
            int zDir = zRange > 0.0D ? 1 : (zRange < 0.0D ? -1 : 0);
            double xInverseRange = xDir == 0 ? Double.NaN : 1.0D / xRange;
            double yInverseRange = yDir == 0 ? Double.NaN : 1.0D / yRange;
            double zInverseRange = zDir == 0 ? Double.NaN : 1.0D / zRange;
            int nextXStop = xDir == 0 ? Integer.MAX_VALUE : pos.getX();
            if (xDir == 1) {
               ++nextXStop;
            }

            int nextYStop = yDir == 0 ? Integer.MAX_VALUE : pos.getY();
            if (yDir == 1) {
               ++nextYStop;
            }

            int nextZStop = zDir == 0 ? Integer.MAX_VALUE : pos.getZ();
            if (zDir == 1) {
               ++nextZStop;
            }

            double normalizedXDistToStop = 7777777.0D;
            double normalizedYDistToStop = 7777777.0D;

            for(int i = 1; i <= maxBlocks; ++i) {
               int mininumNormalizedDistance = 0;
               if (xDir != 0) {
                  double xDistToStop = (double)nextXStop - xStart;
                  normalizedXDistToStop = xDistToStop * xInverseRange;
                  mininumNormalizedDistance = 1;
               }

               if (yDir != 0) {
                  double yDistToStop = (double)nextYStop - yStart;
                  normalizedYDistToStop = yDistToStop * yInverseRange;
                  if (normalizedYDistToStop < normalizedXDistToStop) {
                     mininumNormalizedDistance = 2;
                  }
               }

               if (zDir != 0) {
                  double zDistToStop = (double)nextZStop - zStart;
                  double normalizedZDistToStop = zDistToStop * zInverseRange;
                  if (normalizedZDistToStop < normalizedXDistToStop && normalizedZDistToStop < normalizedYDistToStop) {
                     mininumNormalizedDistance = 3;
                  }
               }

               if (mininumNormalizedDistance == 1) {
                  pos = pos.east(xDir);
                  nextXStop += xDir;
               } else if (mininumNormalizedDistance == 2) {
                  pos = pos.up(yDir);
                  nextYStop += yDir;
               } else {
                  pos = pos.south(zDir);
                  nextZStop += zDir;
               }

               blocks.add(pos);
               if (!world.isBlockLoaded(pos)) {
                  world.profiler.endSection();
                  return (BlockPos[])blocks.toArray(new BlockPos[0]);
               }

               state = world.getBlockState(pos);
               if ((collideOnAllSolids || !canSeeThrough(state)) && state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB) {
                  RayTraceResult result = state.collisionRayTrace(world, pos, vecStart, vecEnd);
                  if (result != null) {
                     world.profiler.endSection();
                     return (BlockPos[])blocks.toArray(new BlockPos[0]);
                  }
               }

               if (pos.getX() == endPos.getX() && pos.getY() == endPos.getY() && pos.getZ() == endPos.getZ()) {
                  world.profiler.endSection();
                  return (BlockPos[])blocks.toArray(new BlockPos[0]);
               }
            }

            if (maxBlocks < 200 || lastWarning != -1L && System.currentTimeMillis() - lastWarning <= 300000L) {
               ++errorCount;
            } else {
               System.err.println("WARNING: BEYOND-LIMIT RAYTRACING DETECTED!  This warning will not show more than once every 5 minutes.  This is usually due to inefficient raytrace calls from another mod");
               System.err.println("This type of error has occurred " + errorCount + " additional times since the last time this message was shown");
               System.err.println("From " + vecStart + " to " + vecEnd + " (distance: " + vecStart.distanceTo(vecEnd) + ")");
               System.err.println("Limit: " + maxBlocks + " iterations (not synonymous to distance, but longer distances are generally more iterations)");
               System.err.println();
               Tools.printStackTrace();
               lastWarning = System.currentTimeMillis();
            }

            world.profiler.endSection();
            return (BlockPos[])blocks.toArray(new BlockPos[0]);
         }
      }
   }

   @Nonnull
   public static RayTraceResult rayTraceBlocks(Entity fromEyesOf, double maxDistance, boolean collideOnAllSolids) {
      return rayTraceBlocks(fromEyesOf, maxDistance, 200, collideOnAllSolids);
   }

   @Nonnull
   public static RayTraceResult rayTraceBlocks(Entity fromEyesOf, double maxDistance, int maxBlocks, boolean collideOnAllSolids) {
      Vec3d eyes = fromEyesOf.getPositionVector().addVector(0.0D, (double)fromEyesOf.getEyeHeight(), 0.0D);
      return rayTraceBlocks(fromEyesOf.world, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), maxBlocks, collideOnAllSolids);
   }

   @Nonnull
   public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, boolean collideOnAllSolids) {
      return rayTraceBlocks(world, vecStart, vecEnd, maxDistance, 200, collideOnAllSolids);
   }

   @Nonnull
   public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, int maxBlocks, boolean collideOnAllSolids) {
      return rayTraceBlocks(world, vecStart, vecStart.add(vecEnd.subtract(vecStart).normalize().scale(maxDistance)), maxBlocks, collideOnAllSolids);
   }

   @Nonnull
   public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, boolean collideOnAllSolids) {
      return rayTraceBlocks(world, vecStart, vecEnd, 200, collideOnAllSolids);
   }

   @Nonnull
   public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, int maxBlocks, boolean collideOnAllSolids) {
      world.profiler.startSection("Fantastic Lib: Improved Raytrace");
      BlockPos pos = new BlockPos(vecStart);
      BlockPos endPos = new BlockPos(vecEnd);
      if (vecEnd.x > vecStart.x && vecEnd.x == (double)((int)vecEnd.x)) {
         endPos = new BlockPos(endPos.getX() - 1, endPos.getY(), endPos.getZ());
      }

      if (vecEnd.y > vecStart.y && vecEnd.y == (double)((int)vecEnd.y)) {
         endPos = new BlockPos(endPos.getX(), endPos.getY() - 1, endPos.getZ());
      }

      if (vecEnd.z > vecStart.z && vecEnd.z == (double)((int)vecEnd.z)) {
         endPos = new BlockPos(endPos.getX(), endPos.getY(), endPos.getZ() - 1);
      }

      if (!world.isBlockLoaded(pos)) {
         world.profiler.endSection();
         return new ImprovedRayTracing.FixedRayTraceResult((Type)null, (Vec3d)null, (EnumFacing)null, pos);
      } else {
         IBlockState state = world.getBlockState(pos);
         if ((collideOnAllSolids || !canSeeThrough(state)) && state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB) {
            RayTraceResult result = state.collisionRayTrace(world, pos, vecStart, vecEnd);
            if (result != null) {
               world.profiler.endSection();
               return result;
            }
         }

         if (pos.getX() == endPos.getX() && pos.getY() == endPos.getY() && pos.getZ() == endPos.getZ()) {
            world.profiler.endSection();
            return new ImprovedRayTracing.FixedRayTraceResult(Type.MISS, vecEnd, (EnumFacing)null, pos);
         } else {
            double xStart = vecStart.x;
            double yStart = vecStart.y;
            double zStart = vecStart.z;
            double xRange = vecEnd.x - xStart;
            double yRange = vecEnd.y - yStart;
            double zRange = vecEnd.z - zStart;
            int xDir = xRange > 0.0D ? 1 : (xRange < 0.0D ? -1 : 0);
            int yDir = yRange > 0.0D ? 1 : (yRange < 0.0D ? -1 : 0);
            int zDir = zRange > 0.0D ? 1 : (zRange < 0.0D ? -1 : 0);
            double xInverseRange = xDir == 0 ? Double.NaN : 1.0D / xRange;
            double yInverseRange = yDir == 0 ? Double.NaN : 1.0D / yRange;
            double zInverseRange = zDir == 0 ? Double.NaN : 1.0D / zRange;
            int nextXStop = xDir == 0 ? Integer.MAX_VALUE : pos.getX();
            if (xDir == 1) {
               ++nextXStop;
            }

            int nextYStop = yDir == 0 ? Integer.MAX_VALUE : pos.getY();
            if (yDir == 1) {
               ++nextYStop;
            }

            int nextZStop = zDir == 0 ? Integer.MAX_VALUE : pos.getZ();
            if (zDir == 1) {
               ++nextZStop;
            }

            double normalizedXDistToStop = 7777777.0D;
            double normalizedYDistToStop = 7777777.0D;

            for(int i = 1; i <= maxBlocks; ++i) {
               int mininumNormalizedDistance = 0;
               if (xDir != 0) {
                  double xDistToStop = (double)nextXStop - xStart;
                  normalizedXDistToStop = xDistToStop * xInverseRange;
                  mininumNormalizedDistance = 1;
               }

               if (yDir != 0) {
                  double yDistToStop = (double)nextYStop - yStart;
                  normalizedYDistToStop = yDistToStop * yInverseRange;
                  if (normalizedYDistToStop < normalizedXDistToStop) {
                     mininumNormalizedDistance = 2;
                  }
               }

               if (zDir != 0) {
                  double zDistToStop = (double)nextZStop - zStart;
                  double normalizedZDistToStop = zDistToStop * zInverseRange;
                  if (normalizedZDistToStop < normalizedXDistToStop && normalizedZDistToStop < normalizedYDistToStop) {
                     mininumNormalizedDistance = 3;
                  }
               }

               if (mininumNormalizedDistance == 1) {
                  pos = pos.east(xDir);
                  nextXStop += xDir;
               } else if (mininumNormalizedDistance == 2) {
                  pos = pos.up(yDir);
                  nextYStop += yDir;
               } else {
                  pos = pos.south(zDir);
                  nextZStop += zDir;
               }

               if (!world.isBlockLoaded(pos)) {
                  world.profiler.endSection();
                  return new ImprovedRayTracing.FixedRayTraceResult((Type)null, (Vec3d)null, (EnumFacing)null, pos);
               }

               state = world.getBlockState(pos);
               if ((collideOnAllSolids || !canSeeThrough(state)) && state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB) {
                  RayTraceResult result = state.collisionRayTrace(world, pos, vecStart, vecEnd);
                  if (result != null) {
                     world.profiler.endSection();
                     return result;
                  }
               }

               if (pos.getX() == endPos.getX() && pos.getY() == endPos.getY() && pos.getZ() == endPos.getZ()) {
                  world.profiler.endSection();
                  return new ImprovedRayTracing.FixedRayTraceResult(Type.MISS, vecEnd, (EnumFacing)null, pos);
               }
            }

            if (maxBlocks < 200 || lastWarning != -1L && System.currentTimeMillis() - lastWarning <= 300000L) {
               ++errorCount;
            } else {
               System.err.println("WARNING: BEYOND-LIMIT RAYTRACING DETECTED!  This warning will not show more than once every 5 minutes.  This is usually due to inefficient raytrace calls from another mod");
               System.err.println("This type of error has occurred " + errorCount + " additional times since the last time this message was shown");
               System.err.println("From " + vecStart + " to " + vecEnd + " (distance: " + vecStart.distanceTo(vecEnd) + ")");
               System.err.println("Limit: " + maxBlocks + " iterations (not synonymous to distance, but longer distances are generally more iterations)");
               System.err.println();
               Tools.printStackTrace();
               lastWarning = System.currentTimeMillis();
            }

            world.profiler.endSection();
            return new ImprovedRayTracing.FixedRayTraceResult((Type)null, (Vec3d)null, (EnumFacing)null, (BlockPos)null);
         }
      }
   }

   public static boolean canSeeThrough(IBlockState blockState) {
      if (transparentBlockstates.contains(blockState)) {
         return true;
      } else if (nonTransparentBlockstates.contains(blockState)) {
         return false;
      } else {
         Block block = blockState.getBlock();
         if (transparentBlocks.contains(block)) {
            return true;
         } else if (nonTransparentBlocks.contains(block)) {
            return false;
         } else {
            Class cls = block.getClass();
            if (transparentBlockClasses.contains(cls)) {
               return true;
            } else if (nonTransparentBlockClasses.contains(cls)) {
               return false;
            } else {
               if (!ignoredBlockClasses.contains(cls)) {
                  for(Class<? extends Block> superClass : transparentBlockSuperclasses) {
                     if (superClass.isAssignableFrom(cls)) {
                        transparentBlockClasses.add(cls);
                        return true;
                     }
                  }

                  for(Class<? extends Block> superClass : nonTransparentBlockSuperclasses) {
                     if (superClass.isAssignableFrom(cls)) {
                        nonTransparentBlockClasses.add(cls);
                        return false;
                     }
                  }

                  ignoredBlockClasses.add(cls);
               }

               Material material = blockState.getMaterial();
               if (transparentMaterials.contains(material)) {
                  return true;
               } else if (nonTransparentMaterials.contains(material)) {
                  return false;
               } else if (material == Material.LEAVES) {
                  return true;
               } else if (material == Material.GLASS) {
                  return true;
               } else if (material == Material.ICE) {
                  return true;
               } else if (material == Material.AIR) {
                  return true;
               } else if (material == Material.WATER) {
                  return true;
               } else if (material == Material.FIRE) {
                  return true;
               } else if (material == Material.PORTAL) {
                  return !Compat.betterportals;
               } else if (material == Material.BARRIER) {
                  return true;
               } else if (material == Material.PLANTS) {
                  return true;
               } else if (material == Material.WEB) {
                  return true;
               } else if (material == Material.VINE) {
                  return true;
               } else if (block instanceof BlockSlime) {
                  return true;
               } else if (block instanceof BlockTrapDoor) {
                  return true;
               } else if (block instanceof BlockFence) {
                  return true;
               } else if (block instanceof BlockFenceGate) {
                  return true;
               } else if (block == Blocks.ACACIA_DOOR) {
                  return true;
               } else if (block == Blocks.JUNGLE_DOOR) {
                  return true;
               } else if (block == Blocks.IRON_BARS) {
                  return true;
               } else if (block != Blocks.OAK_DOOR && block != Blocks.IRON_DOOR) {
                  return false;
               } else {
                  return (block.getMetaFromState(blockState) & 8) != 0;
               }
            }
         }
      }
   }

   public static class FixedRayTraceResult extends RayTraceResult {
      public FixedRayTraceResult(Type typeIn, Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
         super(typeIn, new Vec3d(0.0D, 0.0D, 0.0D), sideHitIn, blockPosIn);
         this.hitVec = hitVecIn == null ? null : new Vec3d(hitVecIn.x, hitVecIn.y, hitVecIn.z);
      }
   }
}
