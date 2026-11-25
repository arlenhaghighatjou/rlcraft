package com.tmtravlr.soundfilters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import paulscode.sound.Source;

public class SoundTickHandler {
   private Minecraft mc = Minecraft.getMinecraft();
   private static int profileTickCountdown = 13;
   private static float prevDecayFactor = 0.0F;
   private static float prevRoomFactor = 0.0F;
   private static float prevSkyFactor = 0.0F;
   public static float targetLowPassGain = 1.0F;
   public static float targetLowPassGainHF = 1.0F;
   public static float baseLowPassGain = 1.0F;
   public static float baseLowPassGainHF = 1.0F;
   public static boolean waterSound = false;
   public static boolean lavaSound = false;
   public static Comparator<SoundTickHandler.ComparablePosition> CPcomparator = new Comparator<SoundTickHandler.ComparablePosition>() {
      public int compare(SoundTickHandler.ComparablePosition first, SoundTickHandler.ComparablePosition second) {
         return first.compareTo(second);
      }
   };
   public static ConcurrentSkipListMap<SoundTickHandler.ComparablePosition, SoundTickHandler.DoubleWithTimeout> sourceOcclusionMap = new ConcurrentSkipListMap(CPcomparator);

   public SoundTickHandler() {
   }

   @SubscribeEvent
   public void tick(ClientTickEvent event) {
      if (event.phase == Phase.START) {
         if (this.mc != null && this.mc.world != null && this.mc.player != null && !this.mc.isGamePaused()) {
            if (this.mc.player.isInsideOfMaterial(Material.WATER)) {
               if (!waterSound) {
                  if (SoundFiltersMod.DEBUG) {
                     SoundFiltersMod.logger.debug("[SoundFilters] Applying water sound low pass.");
                  }

                  targetLowPassGain = SoundFiltersMod.waterVolume;
                  targetLowPassGainHF = SoundFiltersMod.waterLowPassAmount;
                  lavaSound = false;
                  waterSound = true;
               }
            } else if (waterSound) {
               if (SoundFiltersMod.DEBUG) {
                  SoundFiltersMod.logger.debug("[SoundFilters] Stopping water sound low pass.");
               }

               targetLowPassGain = 1.0F;
               targetLowPassGainHF = 1.0F;
               waterSound = false;
            }

            if (this.mc.player.isInsideOfMaterial(Material.LAVA)) {
               if (!lavaSound) {
                  if (SoundFiltersMod.DEBUG) {
                     SoundFiltersMod.logger.debug("[SoundFilters] Applying lava sound low pass.");
                  }

                  targetLowPassGain = SoundFiltersMod.lavaVolume;
                  targetLowPassGainHF = SoundFiltersMod.lavaLowPassAmount;
                  lavaSound = true;
                  waterSound = false;
               }
            } else if (lavaSound) {
               if (SoundFiltersMod.DEBUG) {
                  SoundFiltersMod.logger.debug("[SoundFilters] Stopping lava sound low pass.");
               }

               targetLowPassGain = 1.0F;
               targetLowPassGainHF = 1.0F;
               lavaSound = false;
            }

            if (Math.abs(targetLowPassGain - baseLowPassGain) > 0.001F) {
               baseLowPassGain = (targetLowPassGain + baseLowPassGain) / 2.0F;
            }

            if (Math.abs(targetLowPassGainHF - baseLowPassGainHF) > 0.001F) {
               baseLowPassGainHF = (targetLowPassGainHF + baseLowPassGainHF) / 2.0F;
            }
         } else {
            baseLowPassGain = 1.0F;
            baseLowPassGainHF = 1.0F;
            SoundFiltersMod.reverbFilter.decayTime = 0.1F;
            SoundFiltersMod.reverbFilter.reflectionsDelay = 0.0F;
            SoundFiltersMod.reverbFilter.lateReverbDelay = 0.0F;
            lavaSound = false;
            waterSound = false;
         }
      } else {
         ArrayList<SoundTickHandler.ComparablePosition> toRemove = new ArrayList();
         if (SoundFiltersMod.doOcclusion) {
            for(SoundTickHandler.ComparablePosition sourcePosition : sourceOcclusionMap.keySet()) {
               SoundTickHandler.DoubleWithTimeout sourceAndAmount = (SoundTickHandler.DoubleWithTimeout)sourceOcclusionMap.get(sourcePosition);
               if (sourceAndAmount != null) {
                  if (sourceAndAmount.source != null && sourceAndAmount.source.position != null && sourceAndAmount.source.active()) {
                     try {
                        if (this.mc != null && this.mc.world != null && this.mc.player != null) {
                           Vec3d roomSize = new Vec3d(this.mc.player.posX, this.mc.player.posY + (double)this.mc.player.getEyeHeight(), this.mc.player.posZ);
                           sourceAndAmount.amount = (sourceAndAmount.amount * 3.0D + (double)SoundFiltersMod.occlusionPercent * getSoundOcclusion(this.mc.world, new Vec3d((double)sourceAndAmount.source.position.x, (double)sourceAndAmount.source.position.y, (double)sourceAndAmount.source.position.z), roomSize)) / 4.0D;
                        } else {
                           sourceAndAmount.amount = 0.0D;
                        }
                     } catch (NullPointerException var23) {
                        if (SoundFiltersMod.DEBUG) {
                           SoundFiltersMod.logger.warn("Caught null pointer exception while updating sound occlusion. This happens sometimes because a sound is modified at the same time in another thread.");
                        }
                     }
                  } else {
                     toRemove.add(sourcePosition);
                  }
               }
            }

            for(SoundTickHandler.ComparablePosition positionToRemove : toRemove) {
               if (SoundFiltersMod.SUPER_DUPER_DEBUG) {
                  SoundFiltersMod.logger.debug("[Sound Filters] Removing " + positionToRemove + ", " + positionToRemove.hashCode() + ", " + profileTickCountdown);
               }

               sourceOcclusionMap.remove(positionToRemove);
            }
         }

         if (this.mc != null && this.mc.world != null && this.mc.player != null && SoundFiltersMod.doReverb) {
            --profileTickCountdown;
            if (profileTickCountdown <= 0) {
               profileTickCountdown = 13;
               EaglercraftRandom rand = new EaglercraftRandom();
               TreeSet<SoundTickHandler.ComparablePosition> visited = new TreeSet(CPcomparator);
               ArrayList<IBlockState> blocksFound = new ArrayList();
               LinkedList<SoundTickHandler.ComparablePosition> toVisit = new LinkedList();
               toVisit.add(new SoundTickHandler.ComparablePosition((float)MathHelper.floor(this.mc.player.posX), (float)MathHelper.floor(this.mc.player.posY), (float)MathHelper.floor(this.mc.player.posZ)));

               int i;
               for(i = 0; i < SoundFiltersMod.profileSize && !toVisit.isEmpty(); ++i) {
                  SoundTickHandler.ComparablePosition current = (SoundTickHandler.ComparablePosition)toVisit.remove(rand.nextInt(toVisit.size()));
                  visited.add(current);
                  SoundTickHandler.ComparablePosition pos = new SoundTickHandler.ComparablePosition(current.x, current.y, current.z + 1.0F);
                  IBlockState state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
                  Block block = state.getBlock();
                  Material material = state.getMaterial();
                  if (!material.blocksMovement()) {
                     if (!visited.contains(pos) && !toVisit.contains(pos)) {
                        toVisit.add(pos);
                     }

                     if (material != Material.AIR) {
                        blocksFound.add(state);
                     }
                  } else {
                     blocksFound.add(state);
                  }

                  pos = new SoundTickHandler.ComparablePosition(current.x, current.y, current.z - 1.0F);
                  state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
                  block = state.getBlock();
                  material = state.getMaterial();
                  if (!material.blocksMovement()) {
                     if (!visited.contains(pos) && !toVisit.contains(pos)) {
                        toVisit.add(pos);
                     }

                     if (material != Material.AIR) {
                        blocksFound.add(state);
                     }
                  } else {
                     blocksFound.add(state);
                  }

                  pos = new SoundTickHandler.ComparablePosition(current.x, current.y + 1.0F, current.z);
                  state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
                  block = state.getBlock();
                  material = state.getMaterial();
                  if (!material.blocksMovement()) {
                     if (!visited.contains(pos) && !toVisit.contains(pos)) {
                        toVisit.add(pos);
                     }

                     if (material != Material.AIR) {
                        blocksFound.add(state);
                     }
                  } else {
                     blocksFound.add(state);
                  }

                  pos = new SoundTickHandler.ComparablePosition(current.x, current.y - 1.0F, current.z);
                  state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
                  block = state.getBlock();
                  material = state.getMaterial();
                  if (!material.blocksMovement()) {
                     if (!visited.contains(pos) && !toVisit.contains(pos)) {
                        toVisit.add(pos);
                     }

                     if (material != Material.AIR) {
                        blocksFound.add(state);
                     }
                  } else {
                     blocksFound.add(state);
                  }

                  pos = new SoundTickHandler.ComparablePosition(current.x + 1.0F, current.y, current.z);
                  state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
                  block = state.getBlock();
                  material = state.getMaterial();
                  if (!material.blocksMovement()) {
                     if (!visited.contains(pos) && !toVisit.contains(pos)) {
                        toVisit.add(pos);
                     }

                     if (material != Material.AIR) {
                        blocksFound.add(state);
                     }
                  } else {
                     blocksFound.add(state);
                  }

                  pos = new SoundTickHandler.ComparablePosition(current.x - 1.0F, current.y, current.z);
                  state = this.mc.world.getBlockState(new BlockPos(pos.x(), pos.y(), pos.z()));
                  block = state.getBlock();
                  material = state.getMaterial();
                  if (!material.blocksMovement()) {
                     if (!visited.contains(pos) && !toVisit.contains(pos)) {
                        toVisit.add(pos);
                     }

                     if (material != Material.AIR) {
                        blocksFound.add(state);
                     }
                  } else {
                     blocksFound.add(state);
                  }
               }

               int roomSize = visited.size();
               double highReverb = 0.0D;
               double midReverb = 0.0D;
               double lowReverb = 0.0D;

               for(IBlockState s : blocksFound) {
                  Block b = s.getBlock();
                  SoundFiltersMod.BlockMeta blockInfo = new SoundFiltersMod.BlockMeta(b, 16);
                  if (!SoundFiltersMod.customReverb.containsKey(blockInfo)) {
                     blockInfo = new SoundFiltersMod.BlockMeta(b, b.getMetaFromState(s));
                  }

                  if (SoundFiltersMod.customReverb.containsKey(blockInfo)) {
                     double factor = ((Double)SoundFiltersMod.customReverb.get(blockInfo)).doubleValue();
                     lowReverb += factor < 1.0D && factor >= 0.0D ? 1.0D - factor : 0.0D;
                     midReverb += factor < 2.0D && factor > 0.0D ? 1.0D - Math.abs(factor - 1.0D) : 0.0D;
                     highReverb += factor <= 1.0D ? 0.0D : (factor > 2.0D ? 1.0D : factor - 1.0D);
                  } else if (s.getMaterial() != Material.ROCK && s.getMaterial() != Material.GLASS && s.getMaterial() != Material.ICE && s.getMaterial() != Material.IRON) {
                     if (s.getMaterial() != Material.CACTUS && s.getMaterial() != Material.CAKE && s.getMaterial() != Material.CLOTH && s.getMaterial() != Material.CORAL && s.getMaterial() != Material.GRASS && s.getMaterial() != Material.LEAVES && s.getMaterial() != Material.CARPET && s.getMaterial() != Material.PLANTS && s.getMaterial() != Material.GOURD && s.getMaterial() != Material.SNOW && s.getMaterial() != Material.SPONGE && s.getMaterial() != Material.VINE && s.getMaterial() != Material.WEB) {
                        ++midReverb;
                     } else {
                        ++lowReverb;
                     }
                  } else {
                     ++highReverb;
                  }
               }

               float skyFactor = 0.0F;
               if (SoundFiltersMod.doSkyChecks && roomSize == SoundFiltersMod.profileSize) {
                  int x = MathHelper.floor(this.mc.player.posX);
                  int y = MathHelper.floor(this.mc.player.posY);
                  int z = MathHelper.floor(this.mc.player.posZ);
                  if (onlySkyAboveBlock(this.mc.world, x, y, z)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x + rand.nextInt(5) + 5, y, z)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x - rand.nextInt(5) - 5, y, z)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x, y, z + rand.nextInt(5) + 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x, y, z - rand.nextInt(5) - 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x + rand.nextInt(5) + 5, y, z + rand.nextInt(5) + 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x - rand.nextInt(5) - 5, y, z + rand.nextInt(5) + 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x + rand.nextInt(5) + 5, y, z - rand.nextInt(5) - 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x - rand.nextInt(5) - 5, y, z - rand.nextInt(5) - 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x + rand.nextInt(5) + 5, y + 5, z)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x - rand.nextInt(5) - 5, y + 5, z)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x, y + 5, z + rand.nextInt(5) + 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x, y + 5, z - rand.nextInt(5) - 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x + rand.nextInt(5) + 5, y + 5, z + rand.nextInt(5) + 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x - rand.nextInt(5) - 5, y + 5, z + rand.nextInt(5) + 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x + rand.nextInt(5) + 5, y + 5, z - rand.nextInt(5) - 5)) {
                     ++skyFactor;
                  }

                  if (onlySkyAboveBlock(this.mc.world, x - rand.nextInt(5) - 5, y + 5, z - rand.nextInt(5) - 5)) {
                     ++skyFactor;
                  }
               }

               skyFactor = 1.0F - Math.min(skyFactor, 12.0F) / 12.0F;
               float decayFactor = 0.0F;
               float roomFactor = (float)roomSize / (float)SoundFiltersMod.profileSize;
               if (highReverb + midReverb + lowReverb > 0.0D) {
                  decayFactor += (float)(highReverb - lowReverb) / (float)(highReverb + midReverb + lowReverb);
               }

               if (decayFactor < 0.0F) {
                  decayFactor = 0.0F;
               }

               if (decayFactor > 1.0F) {
                  decayFactor = 1.0F;
               }

               if (SoundFiltersMod.SUPER_DUPER_DEBUG) {
                  SoundFiltersMod.logger.debug("[Sound Filters] Reverb Profile - Room Size: " + roomSize + ", Looked at: " + i + ", Sky Factor: " + skyFactor + ", High, Mid, and Low Reverb: (" + highReverb + ", " + midReverb + ", " + lowReverb + ")");
               }

               decayFactor = (decayFactor + prevDecayFactor) / 2.0F;
               roomFactor = (roomFactor + prevRoomFactor) / 2.0F;
               skyFactor = (skyFactor + prevSkyFactor) / 2.0F;
               prevDecayFactor = decayFactor;
               prevRoomFactor = roomFactor;
               prevSkyFactor = skyFactor;
               SoundFiltersMod.reverbFilter.decayTime = SoundFiltersMod.reverbPercent * 8.0F * decayFactor * roomFactor * skyFactor;
               if (SoundFiltersMod.reverbFilter.decayTime < 0.1F) {
                  SoundFiltersMod.reverbFilter.decayTime = 0.1F;
               }

               SoundFiltersMod.reverbFilter.reflectionsGain = SoundFiltersMod.reverbPercent * (0.05F + 0.05F * roomFactor);
               SoundFiltersMod.reverbFilter.reflectionsDelay = 0.025F * roomFactor;
               SoundFiltersMod.reverbFilter.lateReverbGain = SoundFiltersMod.reverbPercent * (1.26F + 0.1F * roomFactor);
               SoundFiltersMod.reverbFilter.lateReverbDelay = 0.01F * roomFactor;
            }
         }
      }

   }

   private static boolean onlySkyAboveBlock(World world, int x, int y, int z) {
      for(int i = y; i < 256; ++i) {
         IBlockState state = world.getBlockState(new BlockPos(x, i, z));
         Block block = state.getBlock();
         if (state.getMaterial().blocksMovement()) {
            return false;
         }
      }

      return true;
   }

   public static double getSoundOcclusion(World world, Vec3d sound, Vec3d listener) {
      double occludedPercent = 0.0D;
      sound = sound.addVector(0.1D, 0.1D, 0.1D);
      if (!Double.isNaN(sound.x) && !Double.isNaN(sound.y) && !Double.isNaN(sound.z)) {
         if (!Double.isNaN(listener.x) && !Double.isNaN(listener.y) && !Double.isNaN(listener.z)) {
            int listenerX = MathHelper.floor(listener.x);
            int listenerY = MathHelper.floor(listener.y);
            int listenerZ = MathHelper.floor(listener.z);
            int soundX = MathHelper.floor(sound.x);
            int soundY = MathHelper.floor(sound.y);
            int soundZ = MathHelper.floor(sound.z);
            int countDown = 200;

            while(countDown-- >= 0) {
               if (Double.isNaN(sound.x) || Double.isNaN(sound.y) || Double.isNaN(sound.z)) {
                  return occludedPercent;
               }

               if (soundX == listenerX && soundY == listenerY && soundZ == listenerZ) {
                  return occludedPercent;
               }

               boolean shouldChangeX = true;
               boolean shouldChangeY = true;
               boolean shouldChangeZ = true;
               double newX = 999.0D;
               double newY = 999.0D;
               double newZ = 999.0D;
               if (listenerX == soundX) {
                  shouldChangeX = false;
               }

               if (shouldChangeX) {
                  newX = (double)soundX + (listenerX > soundX ? 1.0D : 0.0D);
               }

               if (listenerY == soundY) {
                  shouldChangeY = false;
               }

               if (shouldChangeY) {
                  newY = (double)soundY + (listenerY > soundY ? 1.0D : 0.0D);
               }

               if (listenerZ == soundZ) {
                  shouldChangeZ = false;
               }

               if (shouldChangeZ) {
                  newZ = (double)soundZ + (listenerZ > soundZ ? 1.0D : 0.0D);
               }

               double xPercentChange = 999.0D;
               double yPercentChange = 999.0D;
               double zPercentChange = 999.0D;
               double xDifference = listener.x - sound.x;
               double yDifference = listener.y - sound.y;
               double zDifference = listener.z - sound.z;
               if (shouldChangeX) {
                  xPercentChange = (newX - sound.x) / xDifference;
               }

               if (shouldChangeY) {
                  yPercentChange = (newY - sound.y) / yDifference;
               }

               if (shouldChangeZ) {
                  zPercentChange = (newZ - sound.z) / zDifference;
               }

               byte whichToChange;
               if (xPercentChange < yPercentChange && xPercentChange < zPercentChange) {
                  if (listenerX > soundX) {
                     whichToChange = 4;
                  } else {
                     whichToChange = 5;
                  }

                  sound = new Vec3d(newX, sound.y + yDifference * xPercentChange, sound.z + zDifference * xPercentChange);
               } else if (yPercentChange < zPercentChange) {
                  if (listenerY > soundY) {
                     whichToChange = 0;
                  } else {
                     whichToChange = 1;
                  }

                  sound = new Vec3d(sound.x + xDifference * yPercentChange, newY, sound.z + zDifference * yPercentChange);
               } else {
                  if (listenerZ > soundZ) {
                     whichToChange = 2;
                  } else {
                     whichToChange = 3;
                  }

                  sound = new Vec3d(sound.x + xDifference * zPercentChange, sound.y + yDifference * zPercentChange, newZ);
               }

               Vec3d vec32 = new Vec3d((double)MathHelper.floor(sound.x), (double)MathHelper.floor(sound.y), (double)MathHelper.floor(sound.z));
               soundX = (int)vec32.x;
               if (whichToChange == 5) {
                  --soundX;
                  vec32 = vec32.addVector(1.0D, 0.0D, 0.0D);
               }

               soundY = (int)vec32.y;
               if (whichToChange == 1) {
                  --soundY;
                  vec32 = vec32.addVector(0.0D, 1.0D, 0.0D);
               }

               soundZ = (int)vec32.z;
               if (whichToChange == 3) {
                  --soundZ;
                  vec32 = vec32.addVector(0.0D, 0.0D, 1.0D);
               }

               BlockPos pos = new BlockPos(soundX, soundY, soundZ);
               IBlockState state = world.getBlockState(pos);
               Block block = world.getBlockState(pos).getBlock();
               int meta = block.getMetaFromState(state);
               Material material = state.getMaterial();
               if (block != null && block != Blocks.AIR && state.getBoundingBox(world, new BlockPos(soundX, soundY, soundZ)) != Block.NULL_AABB && block.canCollideCheck(state, false)) {
                  RayTraceResult rayTrace = state.collisionRayTrace(world, pos, sound, listener);
                  if (rayTrace != null) {
                     SoundFiltersMod.BlockMeta blockInfo = new SoundFiltersMod.BlockMeta(block, 16);
                     if (!SoundFiltersMod.customOcclusion.containsKey(blockInfo)) {
                        blockInfo = new SoundFiltersMod.BlockMeta(block, meta);
                     }

                     if (SoundFiltersMod.customOcclusion.containsKey(blockInfo)) {
                        occludedPercent += ((Double)SoundFiltersMod.customOcclusion.get(blockInfo)).doubleValue() * 0.1D;
                     } else if (occludedPercent < 0.7D) {
                        occludedPercent += material.isOpaque() ? 0.1D : 0.05D;
                     }

                     if (occludedPercent > 0.98D) {
                        return 0.98D;
                     }
                  }
               }
            }

            return occludedPercent;
         } else {
            return occludedPercent;
         }
      } else {
         return occludedPercent;
      }
   }

   public static class ComparablePosition implements Comparable {
      float x;
      float y;
      float z;

      ComparablePosition(float xToSet, float yToSet, float zToSet) {
         this.x = xToSet;
         this.y = yToSet;
         this.z = zToSet;
      }

      public int x() {
         return MathHelper.floor(this.x);
      }

      public int y() {
         return MathHelper.floor(this.y);
      }

      public int z() {
         return MathHelper.floor(this.z);
      }

      public boolean equals(Object object) {
         if (!(object instanceof SoundTickHandler.ComparablePosition)) {
            return false;
         } else {
            SoundTickHandler.ComparablePosition toCompare = (SoundTickHandler.ComparablePosition)object;
            return toCompare.compareTo(this) == 0;
         }
      }

      public int compareTo(Object object) {
         if (!(object instanceof SoundTickHandler.ComparablePosition)) {
            return 0;
         } else {
            SoundTickHandler.ComparablePosition toCompare = (SoundTickHandler.ComparablePosition)object;
            if ((double)(toCompare.x - this.x) > 0.1D) {
               return 1;
            } else if ((double)(toCompare.x - this.x) < -0.1D) {
               return -1;
            } else if ((double)(toCompare.y - this.y) > 0.1D) {
               return 1;
            } else if ((double)(toCompare.y - this.y) < -0.1D) {
               return -1;
            } else if ((double)(toCompare.z - this.z) > 0.1D) {
               return 1;
            } else {
               return (double)(toCompare.z - this.z) < -0.1D ? -1 : 0;
            }
         }
      }

      public String toString() {
         return "[" + this.x + ", " + this.y + ", " + this.z + "]";
      }
   }

   public static class DoubleWithTimeout {
      public Source source;
      public double amount;
      public int timeout;

      DoubleWithTimeout() {
         this((Source)null, 0.0D, 10);
      }

      DoubleWithTimeout(Source sToSet, double dToSet, int iToSet) {
         this.source = sToSet;
         this.amount = dToSet;
         this.timeout = iToSet;
      }
   }
}
