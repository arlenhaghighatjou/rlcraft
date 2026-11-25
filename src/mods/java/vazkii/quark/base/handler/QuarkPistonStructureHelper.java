package vazkii.quark.base.handler;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.api.ICollateralMover;
import vazkii.quark.api.INonSticky;
import vazkii.quark.base.module.GlobalConfig;

public class QuarkPistonStructureHelper extends BlockPistonStructureHelper {
   private final BlockPistonStructureHelper parent;
   private final World world;
   private final BlockPos pistonPos;
   private final BlockPos blockToMove;
   private final EnumFacing moveDirection;
   private final boolean extending;
   private final List<BlockPos> toMove = Lists.newArrayList();
   private final List<BlockPos> toDestroy = Lists.newArrayList();

   public QuarkPistonStructureHelper(BlockPistonStructureHelper parent, World worldIn, BlockPos posIn, EnumFacing pistonFacing, boolean extending) {
      super(worldIn, posIn, pistonFacing, extending);
      this.parent = parent;
      this.world = worldIn;
      this.pistonPos = posIn;
      if (extending) {
         this.moveDirection = pistonFacing;
         this.blockToMove = posIn.offset(pistonFacing);
      } else {
         this.moveDirection = pistonFacing.getOpposite();
         this.blockToMove = posIn.offset(pistonFacing, 2);
      }

      this.extending = extending;
   }

   public boolean canMove() {
      if (!GlobalConfig.usePistonLogicRepl) {
         return this.parent.canMove();
      } else {
         this.toMove.clear();
         this.toDestroy.clear();
         IBlockState iblockstate = this.world.getBlockState(this.blockToMove);
         if (!BlockPistonBase.canPush(iblockstate, this.world, this.blockToMove, this.moveDirection, false, this.moveDirection)) {
            if (iblockstate.getMobilityFlag() == EnumPushReaction.DESTROY) {
               this.toDestroy.add(this.blockToMove);
               return true;
            } else {
               return false;
            }
         } else if (!this.addBlockLine(this.blockToMove, this.moveDirection)) {
            return false;
         } else {
            for(int i = 0; i < this.toMove.size(); ++i) {
               BlockPos blockpos = (BlockPos)this.toMove.get(i);
               if (this.isBlockBranching(this.world, blockpos) && this.addBranchingBlocks(this.world, blockpos) == ICollateralMover.MoveResult.PREVENT) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private boolean addBlockLine(BlockPos origin, EnumFacing face) {
      int max = GlobalConfig.pistonPushLimit;
      BlockPos target = origin;
      IBlockState iblockstate = this.world.getBlockState(origin);
      Block block = iblockstate.getBlock();
      if (!iblockstate.getBlock().isAir(iblockstate, this.world, origin) && BlockPistonBase.canPush(iblockstate, this.world, origin, this.moveDirection, false, face) && !origin.equals(this.pistonPos) && !this.toMove.contains(origin)) {
         int lineLen = 1;
         if (lineLen + this.toMove.size() > max) {
            return false;
         } else {
            BlockPos oldPos = origin;
            IBlockState oldState = this.world.getBlockState(origin);
            boolean skippingNext = false;

            while(this.isBlockBranching(this.world, target)) {
               ICollateralMover.MoveResult res = this.getBranchResult(this.world, target);
               if (res == ICollateralMover.MoveResult.PREVENT) {
                  return false;
               }

               if (res != ICollateralMover.MoveResult.MOVE) {
                  skippingNext = true;
                  break;
               }

               target = origin.offset(this.moveDirection.getOpposite(), lineLen);
               iblockstate = this.world.getBlockState(target);
               block = iblockstate.getBlock();
               if (iblockstate.getBlock().isAir(iblockstate, this.world, target) || !BlockPistonBase.canPush(iblockstate, this.world, target, this.moveDirection, false, this.moveDirection.getOpposite()) || target.equals(this.pistonPos) || this.getStickCompatibility(this.world, iblockstate, oldState, target, oldPos, this.moveDirection) != ICollateralMover.MoveResult.MOVE) {
                  break;
               }

               oldState = iblockstate;
               oldPos = target;
               ++lineLen;
               if (lineLen + this.toMove.size() > max) {
                  return false;
               }
            }

            int i1 = 0;

            for(int j = lineLen - 1; j >= 0; --j) {
               BlockPos movePos = origin.offset(this.moveDirection.getOpposite(), j);
               if (this.toDestroy.contains(movePos)) {
                  break;
               }

               this.toMove.add(movePos);
               ++i1;
            }

            if (skippingNext) {
               return true;
            } else {
               int j1 = 1;

               while(true) {
                  BlockPos blockpos1 = origin.offset(this.moveDirection, j1);
                  int k = this.toMove.indexOf(blockpos1);
                  ICollateralMover.MoveResult res = ICollateralMover.MoveResult.MOVE;
                  if (k > -1) {
                     this.reorderListAtCollision(i1, k);

                     for(int l = 0; l <= k + i1; ++l) {
                        BlockPos blockpos2 = (BlockPos)this.toMove.get(l);
                        if (this.isBlockBranching(this.world, blockpos2)) {
                           res = this.addBranchingBlocks(this.world, blockpos2);
                           if (res == ICollateralMover.MoveResult.PREVENT) {
                              return false;
                           }
                        }
                     }

                     return true;
                  }

                  if (res == ICollateralMover.MoveResult.MOVE) {
                     iblockstate = this.world.getBlockState(blockpos1);
                     if (iblockstate.getBlock().isAir(iblockstate, this.world, blockpos1)) {
                        return true;
                     }

                     if (!BlockPistonBase.canPush(iblockstate, this.world, blockpos1, this.moveDirection, true, this.moveDirection) || blockpos1.equals(this.pistonPos)) {
                        return false;
                     }

                     if (iblockstate.getMobilityFlag() == EnumPushReaction.DESTROY) {
                        this.toDestroy.add(blockpos1);
                        this.toMove.remove(blockpos1);
                        return true;
                     }

                     boolean doneFinding = false;
                     if (this.isBlockBranching(this.world, blockpos1)) {
                        res = this.getBranchResult(this.world, blockpos1);
                        if (res == ICollateralMover.MoveResult.PREVENT) {
                           return false;
                        }

                        if (res != ICollateralMover.MoveResult.MOVE) {
                           doneFinding = true;
                        }
                     }

                     if (this.toMove.size() >= max) {
                        return false;
                     }

                     this.toMove.add(blockpos1);
                     ++i1;
                     ++j1;
                     if (doneFinding) {
                        return true;
                     }
                  }
               }
            }
         }
      } else {
         return true;
      }
   }

   private void reorderListAtCollision(int p_177255_1_, int p_177255_2_) {
      List<BlockPos> list = Lists.newArrayList();
      List<BlockPos> list1 = Lists.newArrayList();
      List<BlockPos> list2 = Lists.newArrayList();
      list.addAll(this.toMove.subList(0, p_177255_2_));
      list1.addAll(this.toMove.subList(this.toMove.size() - p_177255_1_, this.toMove.size()));
      list2.addAll(this.toMove.subList(p_177255_2_, this.toMove.size() - p_177255_1_));
      this.toMove.clear();
      this.toMove.addAll(list);
      this.toMove.addAll(list1);
      this.toMove.addAll(list2);
   }

   private ICollateralMover.MoveResult addBranchingBlocks(World world, BlockPos fromPos) {
      IBlockState state = world.getBlockState(fromPos);
      Block block = state.getBlock();
      EnumFacing opposite = this.moveDirection.getOpposite();
      ICollateralMover.MoveResult retResult = ICollateralMover.MoveResult.SKIP;
      EnumFacing[] var7 = EnumFacing.values();
      int var8 = var7.length;
      int var9 = 0;

      while(var9 < var8) {
         EnumFacing face = var7[var9];
         ICollateralMover.MoveResult res = ICollateralMover.MoveResult.MOVE;
         BlockPos targetPos = fromPos.offset(face);
         IBlockState targetState = world.getBlockState(targetPos);
         if (block instanceof ICollateralMover) {
            res = ((ICollateralMover)block).getCollateralMovement(world, this.pistonPos, this.moveDirection, face, fromPos);
         } else {
            res = this.getStickCompatibility(world, state, targetState, fromPos, targetPos, face);
         }

         switch(res) {
         case PREVENT:
            return ICollateralMover.MoveResult.PREVENT;
         case MOVE:
            if (!this.addBlockLine(targetPos, face)) {
               return ICollateralMover.MoveResult.PREVENT;
            }
         default:
            if (face == opposite) {
               retResult = res;
            }

            ++var9;
            break;
         case BREAK:
            if (BlockPistonBase.canPush(targetState, world, targetPos, this.moveDirection, true, this.moveDirection)) {
               this.toDestroy.add(targetPos);
               this.toMove.remove(targetPos);
               return ICollateralMover.MoveResult.BREAK;
            }

            return ICollateralMover.MoveResult.PREVENT;
         }
      }

      return retResult;
   }

   private boolean isBlockBranching(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      return block instanceof ICollateralMover ? ((ICollateralMover)block).isCollateralMover(world, this.pistonPos, this.moveDirection, pos) : state.getBlock().isStickyBlock(state);
   }

   private ICollateralMover.MoveResult getBranchResult(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      return block instanceof ICollateralMover ? ((ICollateralMover)block).getCollateralMovement(world, this.pistonPos, this.moveDirection, this.moveDirection, pos) : ICollateralMover.MoveResult.MOVE;
   }

   private ICollateralMover.MoveResult getStickCompatibility(World world, IBlockState state1, IBlockState state2, BlockPos pos1, BlockPos pos2, EnumFacing face) {
      Block block = state1.getBlock();
      if (block instanceof INonSticky && !((INonSticky)block).canStickToBlock(world, this.pistonPos, pos1, pos2, state1, state2, this.moveDirection)) {
         return ICollateralMover.MoveResult.SKIP;
      } else {
         block = state2.getBlock();
         return block instanceof INonSticky && !((INonSticky)block).canStickToBlock(world, this.pistonPos, pos2, pos1, state2, state1, this.moveDirection) ? ICollateralMover.MoveResult.SKIP : ICollateralMover.MoveResult.MOVE;
      }
   }

   @Nonnull
   public List<BlockPos> getBlocksToMove() {
      return !GlobalConfig.usePistonLogicRepl ? this.parent.getBlocksToMove() : this.toMove;
   }

   @Nonnull
   public List<BlockPos> getBlocksToDestroy() {
      return !GlobalConfig.usePistonLogicRepl ? this.parent.getBlocksToDestroy() : this.toDestroy;
   }
}
