package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.core.block.building.BlockPropolis;
import com.lycanitesmobs.core.block.building.BlockVeswax;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.dungeon.definition.ThemeBlock;
import com.lycanitesmobs.core.dungeon.instance.SectorInstance;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.IFluidBlock;

public class CreatureStructure {
   protected BaseCreatureEntity owner;
   protected DungeonTheme dungeonTheme;
   protected Map<Integer, Map<BlockPos, CreatureBuildTask>> buildTasks = new HashMap();
   protected BlockPos origin;
   protected BlockPos startPos;

   public CreatureStructure(BaseCreatureEntity creatureEntity, DungeonTheme dungeonTheme) {
      this.owner = creatureEntity;
      this.dungeonTheme = dungeonTheme;
      this.origin = creatureEntity.getPosition();
      if (this.dungeonTheme == null) {
         throw new RuntimeException("Unable to find the vespid_hive dungeon theme!");
      }
   }

   public BlockPos getOrigin() {
      return this.origin;
   }

   public void setOrigin(BlockPos blockPos) {
      this.origin = blockPos;
      this.startPos = blockPos.add(0, 8, 0);
   }

   public void refreshBuildTasks() {
      this.buildTasks.clear();
      int radius = 8;

      for(int x = -radius; x <= radius; ++x) {
         for(int y = -radius; y <= radius; ++y) {
            for(int z = -radius; z <= radius; ++z) {
               if (Math.abs(x) != radius || Math.abs(z) != radius) {
                  BlockPos blockPos = this.origin.add(x, y, z);
                  if (this.shouldBuildAt(blockPos)) {
                     IBlockState blockState;
                     if (Math.abs(y) > 1 || Math.abs(x) > 1 && Math.abs(z) > 1) {
                        if (Math.abs(x) < radius && Math.abs(y) < radius && Math.abs(z) < radius) {
                           blockState = Blocks.AIR.getDefaultState();
                        } else if (y == radius) {
                           blockState = this.dungeonTheme.getCeiling((SectorInstance)null, '1', this.owner.getRNG());
                        } else if (y == -radius) {
                           blockState = this.dungeonTheme.getFloor((SectorInstance)null, '1', this.owner.getRNG());
                        } else {
                           blockState = this.dungeonTheme.getWall((SectorInstance)null, '1', this.owner.getRNG());
                        }
                     } else {
                        blockState = Blocks.AIR.getDefaultState();
                     }

                     this.createBuildTask(blockState, blockPos, 1);
                  }
               }
            }
         }
      }

      for(int x = -radius; x <= radius; ++x) {
         for(int y = -radius; y <= radius; ++y) {
            for(int z = -radius; z <= radius; ++z) {
               if (Math.abs(x) >= radius / 4 + 1 || Math.abs(z) >= radius / 4 + 1) {
                  BlockPos blockPos = this.origin.add(x, y, z);
                  if (this.shouldBuildAt(blockPos)) {
                     for(int pitLayer = 1; pitLayer <= radius / 2; ++pitLayer) {
                        if (y == radius - pitLayer && Math.abs(x) < radius - pitLayer && Math.abs(z) < radius - pitLayer) {
                           IBlockState blockState = this.dungeonTheme.getPit('1', this.owner.getRNG());
                           this.createBuildTask(blockState, blockPos, 1);
                        }
                     }

                     for(int pitLayer = 1; pitLayer <= radius / 2; ++pitLayer) {
                        if (y == -radius + pitLayer && Math.abs(x) < radius - pitLayer && Math.abs(z) < radius - pitLayer) {
                           IBlockState blockState = this.dungeonTheme.getPit('1', this.owner.getRNG());
                           this.createBuildTask(blockState, blockPos, 1);
                        }
                     }
                  }
               }
            }
         }
      }

      for(int x = -radius; x <= radius; ++x) {
         for(int y = -radius; y <= radius; ++y) {
            for(int z = -radius; z <= radius; ++z) {
               if (Math.abs(x) < radius / 4 + 1 && y <= -(radius / 2) && y > -radius && Math.abs(z) < radius / 4 + 1) {
                  BlockPos blockPos = this.origin.add(x, y, z);
                  if (this.shouldBuildAt(blockPos)) {
                     IBlockState blockState = this.dungeonTheme.getPit('2', this.owner.getRNG());
                     this.createBuildTask(blockState, blockPos, 1);
                  }
               }
            }
         }
      }

   }

   public void createBuildTask(IBlockState blockState, BlockPos blockPos, int phase) {
      IBlockState targetBlockState = this.owner.getEntityWorld().getBlockState(blockPos);
      if (blockState == targetBlockState || !(blockState.getBlock() instanceof IFluidBlock) && blockState.getBlock() == targetBlockState.getBlock()) {
         for(Map<BlockPos, CreatureBuildTask> phasedBuildTasks : this.buildTasks.values()) {
            phasedBuildTasks.remove(blockPos);
         }

      } else {
         CreatureBuildTask buildTask = new CreatureBuildTask(blockState, blockPos, phase);
         if (!this.buildTasks.containsKey(Integer.valueOf(phase))) {
            this.buildTasks.put(Integer.valueOf(phase), new HashMap());
         }

         ((Map)this.buildTasks.get(Integer.valueOf(phase))).put(blockPos, buildTask);
      }
   }

   protected boolean shouldBuildAt(BlockPos pos) {
      IBlockState targetState = this.owner.getEntityWorld().getBlockState(pos);
      Block targetBlock = targetState.getBlock();
      if (targetBlock == Blocks.AIR) {
         return true;
      } else {
         return this.owner.getEntityWorld().getTileEntity(pos) == null;
      }
   }

   @Nullable
   public CreatureBuildTask takeBuildTask(EntityLivingBase builder) {
      if (this.buildTasks.isEmpty()) {
         return null;
      } else {
         Iterator var2 = this.buildTasks.keySet().iterator();

         while(var2.hasNext()) {
            int phase = ((Integer)var2.next()).intValue();
            if (!((Map)this.buildTasks.get(Integer.valueOf(phase))).isEmpty()) {
               CreatureBuildTask[] phasedBuildTasks = (CreatureBuildTask[])((Map)this.buildTasks.get(Integer.valueOf(phase))).values().toArray(new CreatureBuildTask[0]);
               if (phasedBuildTasks.length == 1) {
                  return phasedBuildTasks[0];
               }

               return phasedBuildTasks[builder.getRNG().nextInt(phasedBuildTasks.length)];
            }
         }

         return null;
      }
   }

   public void completeBuildTask(CreatureBuildTask buildTask) {
      Iterator var2 = this.buildTasks.keySet().iterator();

      while(var2.hasNext()) {
         int phase = ((Integer)var2.next()).intValue();
         if (!((Map)this.buildTasks.get(Integer.valueOf(phase))).isEmpty()) {
            ((Map)this.buildTasks.get(Integer.valueOf(phase))).remove(buildTask.pos);
            this.owner.onBuildTaskComplete(buildTask);
         }
      }

   }

   public boolean isPhaseComplete(int phase) {
      return this.getBuildTaskSize(phase) == 0;
   }

   public int getBuildTaskSize(int phase) {
      return !this.buildTasks.containsKey(Integer.valueOf(phase)) ? 0 : ((Map)this.buildTasks.get(Integer.valueOf(phase))).size();
   }

   public int getFinalPhaseBuildTaskSize() {
      return this.getBuildTaskSize(this.buildTasks.size() - 1);
   }

   public void removePitBLocks() {
      int radius = 10;

      for(int x = -radius; x <= radius; ++x) {
         for(int y = -radius; y <= radius; ++y) {
            for(int z = -radius; z <= radius; ++z) {
               BlockPos blockPos = this.getOrigin().add(x, y, z);
               IBlockState blockState = this.owner.getEntityWorld().getBlockState(blockPos);
               if (!(blockState.getBlock() instanceof BlockPropolis) && !(blockState.getBlock() instanceof BlockVeswax)) {
                  if (blockState.getBlock() == this.dungeonTheme.getPit('1', this.owner.getRNG()).getBlock()) {
                     this.owner.getEntityWorld().setBlockToAir(blockPos);
                  } else if (blockState.getBlock() == this.dungeonTheme.getPit('2', this.owner.getRNG()).getBlock()) {
                     this.owner.getEntityWorld().setBlockToAir(blockPos);
                  }
               }
            }
         }
      }

   }

   protected boolean isStructureBlock(BlockPos blockPos) {
      IBlockState targetState = this.owner.getEntityWorld().getBlockState(blockPos);
      Block targetBlock = targetState.getBlock();
      if (targetBlock == Blocks.AIR) {
         return false;
      } else {
         for(ThemeBlock themeBlock : this.dungeonTheme.ceilingBlocks) {
            if (targetBlock == themeBlock.getBlockState().getBlock()) {
               return true;
            }
         }

         for(ThemeBlock themeBlock : this.dungeonTheme.floorBlocks) {
            if (targetBlock == themeBlock.getBlockState().getBlock()) {
               return true;
            }
         }

         for(ThemeBlock themeBlock : this.dungeonTheme.wallBlocks) {
            if (targetBlock == themeBlock.getBlockState().getBlock()) {
               return true;
            }
         }

         return false;
      }
   }
}
