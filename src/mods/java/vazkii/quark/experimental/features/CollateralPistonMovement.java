package vazkii.quark.experimental.features;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.automation.feature.PistonsMoveTEs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class CollateralPistonMovement extends Feature {
   public static Map<ResourceLocation, CollateralPistonMovement.MoveAction> blockActions;

   public CollateralPistonMovement() {
   }

   public void setupConfig() {
      blockActions = new HashMap();
      String desc = "An array of actions to apply to blocks. Each element is in the following format:\nblockid=action\nWhere blockid is the ID of the block to apply the action to (e.g. minecraft:stone)\nAnd action is one of the following:\nabove - moves all equal blocks above it\nbelow - moves all equal blocks below it\nabove_below - moves all equal blocks above and below it\nabove_edge - moves all equal blocks above it, and one more block of any type that's above the stack\nbelow_edge - moves all equal blocks below it, and one more block of any type that's below the stack\nabove_below_edge - moves all equal blocks above and below it, and one more block of any type that's above or below the stack\ndirectional - moves the block its facing to (only works on directional blocks)\nsides - moves the blocks on all adjacent sides\nhorizontals - moves the blocks on all adjacent horizontal sides\nverticals - moves the blocks on all adjacent vertical sides";
      String[] actionArr = this.loadPropStringList("Actions", desc, new String[]{"quark:chain=below_edge"});

      for(String s : actionArr) {
         String[] tokens = s.split("=");
         if (tokens.length == 2) {
            String res = tokens[0];
            String actionStr = tokens[1];
            CollateralPistonMovement.MoveAction action = null;
            byte var12 = -1;
            switch(actionStr.hashCode()) {
            case -1771400949:
               if (actionStr.equals("below_edge")) {
                  var12 = 4;
               }
               break;
            case -1640870163:
               if (actionStr.equals("above_below_edge")) {
                  var12 = 5;
               }
               break;
            case -1631834134:
               if (actionStr.equals("directional")) {
                  var12 = 6;
               }
               break;
            case -1597888241:
               if (actionStr.equals("above_below")) {
                  var12 = 2;
               }
               break;
            case -1378842691:
               if (actionStr.equals("verticals")) {
                  var12 = 9;
               }
               break;
            case 66844879:
               if (actionStr.equals("horizontals")) {
                  var12 = 8;
               }
               break;
            case 92611485:
               if (actionStr.equals("above")) {
                  var12 = 0;
               }
               break;
            case 93621297:
               if (actionStr.equals("below")) {
                  var12 = 1;
               }
               break;
            case 109432316:
               if (actionStr.equals("sides")) {
                  var12 = 7;
               }
               break;
            case 1888206111:
               if (actionStr.equals("above_edge")) {
                  var12 = 3;
               }
            }

            switch(var12) {
            case 0:
               action = (world8, pos8, state8, facing8, extending8, list8) -> {
                  moveAllAbove(world8, pos8, state8, list8);
               };
               break;
            case 1:
               action = (world7, pos7, state7, facing7, extending7, list7) -> {
                  moveAllBelow(world7, pos7, state7, list7);
               };
               break;
            case 2:
               action = (world6, pos6, state6, facing6, extending6, list6) -> {
                  moveAllAboveBelow(world6, pos6, state6, list6);
               };
               break;
            case 3:
               action = (world5, pos5, state5, facing5, extending5, list5) -> {
                  moveAllAboveAndEdge(world5, pos5, state5, list5);
               };
               break;
            case 4:
               action = (world5, pos5, state5, facing5, extending5, list5) -> {
                  moveAllAboveAndEdge(world5, pos5, state5, list5);
               };
               break;
            case 5:
               action = (world4, pos4, state4, facing4, extending4, list4) -> {
                  moveAllAboveBelowAndEdge(world4, pos4, state4, list4);
               };
               break;
            case 6:
               action = (world3, pos3, state3, facing3, extending3, list3) -> {
                  moveNextDirectional(world3, pos3, state3, facing3, list3);
               };
               break;
            case 7:
               action = (world2, pos2, state2, facing2, extending2, list2) -> {
                  moveSides(world2, pos2, facing2, list2);
               };
               break;
            case 8:
               action = (world1, pos1, state1, facing1, extending1, list1) -> {
                  moveHorizontals(world1, pos1, facing1, list1);
               };
               break;
            case 9:
               action = (world, pos, state, facing, extending, list) -> {
                  moveVerticals(world, pos, facing, list);
               };
            }

            if (action != null) {
               blockActions.put(new ResourceLocation(res), action);
            }
         }
      }

   }

   public static void applyCollateralMovements(World world, BlockPistonStructureHelper helper, EnumFacing facing, boolean extending) {
      if (ModuleLoader.isFeatureEnabled(CollateralPistonMovement.class)) {
         List<BlockPos> moveList = helper.getBlocksToMove();
         List<BlockPos> additions = new LinkedList();

         for(BlockPos move : moveList) {
            IBlockState moveState = world.getBlockState(move);
            Block moveBlock = moveState.getBlock();
            CollateralPistonMovement.MoveAction action = (CollateralPistonMovement.MoveAction)blockActions.get(moveBlock.getRegistryName());
            if (action != null) {
               action.add(world, move, moveState, facing, extending, additions);
            }
         }

         for(BlockPos add : additions) {
            BlockPos check = add.offset(facing);
            if (!moveList.contains(check) && !additions.contains(check)) {
               IBlockState checkState = world.getBlockState(check);
               if (!checkState.getBlock().isAir(checkState, world, check) && !checkState.getBlock().isReplaceable(world, check)) {
                  return;
               }
            }
         }

         moveList.addAll(additions);
      }
   }

   private static BlockPos moveAllEqualSide(World world, BlockPos pos, IBlockState state, EnumFacing side, List<BlockPos> list) {
      BlockPos curr = pos.offset(side);

      for(IBlockState stateAt = world.getBlockState(curr); stateAt.equals(state); stateAt = world.getBlockState(curr)) {
         if (!canMove(stateAt, world, curr)) {
            return curr;
         }

         list.add(curr);
         curr = curr.offset(side);
      }

      return curr;
   }

   private static void moveAllEqualSideAndOneMore(World world, BlockPos pos, IBlockState state, EnumFacing side, List<BlockPos> list) {
      BlockPos edge = moveAllEqualSide(world, pos, state, side, list);
      IBlockState edgeState = world.getBlockState(edge);
      if (canMove(edgeState, world, edge)) {
         list.add(edge);
      }

   }

   private static void moveAllAbove(World world, BlockPos pos, IBlockState state, List<BlockPos> list) {
      moveAllEqualSide(world, pos, state, EnumFacing.UP, list);
   }

   private static void moveAllBelow(World world, BlockPos pos, IBlockState state, List<BlockPos> list) {
      moveAllEqualSide(world, pos, state, EnumFacing.DOWN, list);
   }

   private static void moveAllAboveBelow(World world, BlockPos pos, IBlockState state, List<BlockPos> list) {
      moveAllEqualSide(world, pos, state, EnumFacing.UP, list);
      moveAllEqualSide(world, pos, state, EnumFacing.DOWN, list);
   }

   private static void moveAllAboveAndEdge(World world, BlockPos pos, IBlockState state, List<BlockPos> list) {
      moveAllEqualSideAndOneMore(world, pos, state, EnumFacing.UP, list);
   }

   private static void moveAllBelowAndEdge(World world, BlockPos pos, IBlockState state, List<BlockPos> list) {
      moveAllEqualSideAndOneMore(world, pos, state, EnumFacing.DOWN, list);
   }

   private static void moveAllAboveBelowAndEdge(World world, BlockPos pos, IBlockState state, List<BlockPos> list) {
      moveAllEqualSideAndOneMore(world, pos, state, EnumFacing.UP, list);
      moveAllEqualSideAndOneMore(world, pos, state, EnumFacing.DOWN, list);
   }

   private static void moveNextDirectional(World world, BlockPos pos, IBlockState state, EnumFacing facing, List<BlockPos> list) {
      EnumFacing direction = getStateFacing(state);
      if (direction != null) {
         moveSideIterable(world, pos, facing, list, new EnumFacing[]{direction});
      }

   }

   private static void moveSides(World world, BlockPos pos, EnumFacing facing, List<BlockPos> list) {
      moveSideIterable(world, pos, facing, list, EnumFacing.VALUES);
   }

   private static void moveHorizontals(World world, BlockPos pos, EnumFacing facing, List<BlockPos> list) {
      moveSideIterable(world, pos, facing, list, EnumFacing.HORIZONTALS);
   }

   private static void moveVerticals(World world, BlockPos pos, EnumFacing facing, List<BlockPos> list) {
      moveSideIterable(world, pos, facing, list, new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN});
   }

   private static void moveSideIterable(World world, BlockPos pos, EnumFacing facing, List<BlockPos> list, EnumFacing[] directions) {
      for(EnumFacing direction : directions) {
         if (direction != facing.getOpposite()) {
            BlockPos nextPos = pos.offset(direction);
            IBlockState nextState = world.getBlockState(nextPos);
            if (canMove(nextState, world, nextPos)) {
               list.add(nextPos);
            }
         }
      }

   }

   private static boolean canMove(IBlockState state, World world, BlockPos pos) {
      Block block = state.getBlock();
      if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
         return !block.isAir(state, world, pos) && state.getMobilityFlag() == EnumPushReaction.NORMAL && (!block.hasTileEntity() || !PistonsMoveTEs.shouldMoveTE(true, state));
      } else {
         return !((Boolean)state.getValue(BlockPistonBase.EXTENDED)).booleanValue();
      }
   }

   private static EnumFacing getStateFacing(IBlockState state) {
      for(IProperty prop : state.getPropertyKeys()) {
         if (prop.getName().equals("facing")) {
            Object obj = state.getValue(prop);
            if (obj instanceof EnumFacing) {
               return (EnumFacing)obj;
            }
         }
      }

      return null;
   }

   private interface MoveAction {
      void add(World var1, BlockPos var2, IBlockState var3, EnumFacing var4, boolean var5, List<BlockPos> var6);
   }
}
