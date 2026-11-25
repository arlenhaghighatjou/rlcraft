package com.teamacronymcoders.base.multiblocksystem;

import com.teamacronymcoders.base.util.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.FMLLog;

final class MultiblockWorldRegistry {
   private World worldObj;
   private final Set<MultiblockControllerBase> controllers;
   private final Set<MultiblockControllerBase> dirtyControllers;
   private final Set<MultiblockControllerBase> deadControllers;
   private Set<IMultiblockPart> orphanedParts;
   private final Set<IMultiblockPart> detachedParts;
   private final HashMap<Long, Set<IMultiblockPart>> partsAwaitingChunkLoad;
   private final Object partsAwaitingChunkLoadMutex;
   private final Object orphanedPartsMutex;

   public MultiblockWorldRegistry(World world) {
      this.worldObj = world;
      this.controllers = new HashSet();
      this.deadControllers = new HashSet();
      this.dirtyControllers = new HashSet();
      this.detachedParts = new HashSet();
      this.orphanedParts = new HashSet();
      this.partsAwaitingChunkLoad = new HashMap();
      this.partsAwaitingChunkLoadMutex = new Object();
      this.orphanedPartsMutex = new Object();
   }

   public void tickStart() {
      if (this.controllers.size() > 0) {
         for(MultiblockControllerBase controller : this.controllers) {
            if (controller.WORLD == this.worldObj && controller.WORLD.isRemote == this.worldObj.isRemote) {
               if (controller.isEmpty()) {
                  this.deadControllers.add(controller);
               } else {
                  controller.updateMultiblockEntity();
               }
            }
         }
      }

   }

   public void processMultiblockChanges() {
      List<Set<MultiblockControllerBase>> mergePools = null;
      if (this.orphanedParts.size() > 0) {
         Set<IMultiblockPart> orphansToProcess = null;
         synchronized(this.orphanedPartsMutex) {
            if (this.orphanedParts.size() > 0) {
               orphansToProcess = this.orphanedParts;
               this.orphanedParts = new HashSet();
            }
         }

         if (orphansToProcess != null && orphansToProcess.size() > 0) {
            IChunkProvider chunkProvider = this.worldObj.getChunkProvider();

            for(IMultiblockPart orphan : orphansToProcess) {
               BlockPos coord = orphan.getWorldPosition();
               if (this.worldObj.isBlockLoaded(coord) && !orphan.isPartInvalid() && this.getMultiblockPartFromWorld(this.worldObj, coord) == orphan) {
                  Set<MultiblockControllerBase> compatibleControllers = orphan.attachToNeighbors();
                  if (compatibleControllers == null) {
                     MultiblockControllerBase newController = orphan.createNewMultiblock();
                     newController.attachBlock(orphan);
                     this.controllers.add(newController);
                  } else if (compatibleControllers.size() > 1) {
                     if (mergePools == null) {
                        mergePools = new ArrayList();
                     }

                     boolean hasAddedToPool = false;
                     List<Set<MultiblockControllerBase>> candidatePools = new ArrayList();

                     for(Set<MultiblockControllerBase> candidatePool : mergePools) {
                        if (!Collections.disjoint(candidatePool, compatibleControllers)) {
                           candidatePools.add(candidatePool);
                        }
                     }

                     if (candidatePools.size() <= 0) {
                        mergePools.add(compatibleControllers);
                     } else if (candidatePools.size() == 1) {
                        ((Set)candidatePools.get(0)).addAll(compatibleControllers);
                     } else {
                        Set<MultiblockControllerBase> masterPool = (Set)candidatePools.get(0);

                        for(int i = 1; i < candidatePools.size(); ++i) {
                           Set<MultiblockControllerBase> consumedPool = (Set)candidatePools.get(i);
                           masterPool.addAll(consumedPool);
                           mergePools.remove(consumedPool);
                        }

                        masterPool.addAll(compatibleControllers);
                     }
                  }
               }
            }
         }
      }

      if (mergePools != null && mergePools.size() > 0) {
         for(Set<MultiblockControllerBase> mergePool : mergePools) {
            MultiblockControllerBase newMaster = null;

            for(MultiblockControllerBase controller : mergePool) {
               if (newMaster == null || controller.shouldConsume(newMaster)) {
                  newMaster = controller;
               }
            }

            if (newMaster == null) {
               FMLLog.severe("Multiblock system checked a merge pool of size %d, found no master candidates. This should never happen.", new Object[]{mergePool.size()});
            } else {
               this.addDirtyController(newMaster);

               for(MultiblockControllerBase controller : mergePool) {
                  if (controller != newMaster) {
                     newMaster.assimilate(controller);
                     this.addDeadController(controller);
                     this.addDirtyController(newMaster);
                  }
               }
            }
         }
      }

      if (this.dirtyControllers.size() > 0) {
         Set<IMultiblockPart> newlyDetachedParts = null;

         for(MultiblockControllerBase controller : this.dirtyControllers) {
            newlyDetachedParts = controller.checkForDisconnections();
            if (!controller.isEmpty()) {
               controller.recalculateMinMaxCoords();
               controller.checkIfMachineIsWhole();
            } else {
               this.addDeadController(controller);
            }

            if (newlyDetachedParts != null && newlyDetachedParts.size() > 0) {
               this.detachedParts.addAll(newlyDetachedParts);
            }
         }

         this.dirtyControllers.clear();
      }

      if (this.deadControllers.size() > 0) {
         for(MultiblockControllerBase controller : this.deadControllers) {
            if (!controller.isEmpty()) {
               FMLLog.severe("Found a non-empty controller. Forcing it to shed its blocks and die. This should never happen!", new Object[0]);
               this.detachedParts.addAll(controller.detachAllBlocks());
            }

            this.controllers.remove(controller);
         }

         this.deadControllers.clear();
      }

      for(IMultiblockPart part : this.detachedParts) {
         part.assertDetached();
      }

      this.addAllOrphanedPartsThreadsafe(this.detachedParts);
      this.detachedParts.clear();
   }

   public void onPartAdded(IMultiblockPart part) {
      BlockPos worldLocation = part.getWorldPosition();
      if (!this.worldObj.isBlockLoaded(worldLocation)) {
         long chunkHash = Utils.getChunkXZHashFromBlock(worldLocation);
         synchronized(this.partsAwaitingChunkLoadMutex) {
            Set<IMultiblockPart> partSet;
            if (!this.partsAwaitingChunkLoad.containsKey(Long.valueOf(chunkHash))) {
               partSet = new HashSet();
               this.partsAwaitingChunkLoad.put(Long.valueOf(chunkHash), partSet);
            } else {
               partSet = (Set)this.partsAwaitingChunkLoad.get(Long.valueOf(chunkHash));
            }

            partSet.add(part);
         }
      } else {
         this.addOrphanedPartThreadsafe(part);
      }

   }

   public void onPartRemovedFromWorld(IMultiblockPart part) {
      BlockPos coord = part.getWorldPosition();
      if (coord != null) {
         long hash = Utils.getChunkXZHashFromBlock(coord);
         if (this.partsAwaitingChunkLoad.containsKey(Long.valueOf(hash))) {
            synchronized(this.partsAwaitingChunkLoadMutex) {
               if (this.partsAwaitingChunkLoad.containsKey(Long.valueOf(hash))) {
                  ((Set)this.partsAwaitingChunkLoad.get(Long.valueOf(hash))).remove(part);
                  if (((Set)this.partsAwaitingChunkLoad.get(Long.valueOf(hash))).size() <= 0) {
                     this.partsAwaitingChunkLoad.remove(Long.valueOf(hash));
                  }
               }
            }
         }
      }

      this.detachedParts.remove(part);
      if (this.orphanedParts.contains(part)) {
         synchronized(this.orphanedPartsMutex) {
            this.orphanedParts.remove(part);
         }
      }

      part.assertDetached();
   }

   public void onWorldUnloaded() {
      this.controllers.clear();
      this.deadControllers.clear();
      this.dirtyControllers.clear();
      this.detachedParts.clear();
      synchronized(this.partsAwaitingChunkLoadMutex) {
         this.partsAwaitingChunkLoad.clear();
      }

      synchronized(this.orphanedPartsMutex) {
         this.orphanedParts.clear();
      }

      this.worldObj = null;
   }

   public void onChunkLoaded(int chunkX, int chunkZ) {
      long chunkHash = ChunkPos.asLong(chunkX, chunkZ);
      if (this.partsAwaitingChunkLoad.containsKey(Long.valueOf(chunkHash))) {
         synchronized(this.partsAwaitingChunkLoadMutex) {
            if (this.partsAwaitingChunkLoad.containsKey(Long.valueOf(chunkHash))) {
               this.addAllOrphanedPartsThreadsafe((Collection)this.partsAwaitingChunkLoad.get(Long.valueOf(chunkHash)));
               this.partsAwaitingChunkLoad.remove(Long.valueOf(chunkHash));
            }
         }
      }

   }

   public void addDeadController(MultiblockControllerBase deadController) {
      this.deadControllers.add(deadController);
   }

   public void addDirtyController(MultiblockControllerBase dirtyController) {
      this.dirtyControllers.add(dirtyController);
   }

   public Set<MultiblockControllerBase> getControllers() {
      return Collections.unmodifiableSet(this.controllers);
   }

   protected IMultiblockPart getMultiblockPartFromWorld(World world, BlockPos position) {
      TileEntity te = world.getTileEntity(position);
      return te instanceof IMultiblockPart ? (IMultiblockPart)te : null;
   }

   private void addOrphanedPartThreadsafe(IMultiblockPart part) {
      synchronized(this.orphanedPartsMutex) {
         this.orphanedParts.add(part);
      }
   }

   private void addAllOrphanedPartsThreadsafe(Collection<? extends IMultiblockPart> parts) {
      synchronized(this.orphanedPartsMutex) {
         this.orphanedParts.addAll(parts);
      }
   }
}
