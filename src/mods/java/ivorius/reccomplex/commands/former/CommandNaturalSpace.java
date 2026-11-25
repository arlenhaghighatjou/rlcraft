package ivorius.reccomplex.commands.former;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockSurfaceArea;
import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.world.MockWorld;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.block.BlockGenericSpace;
import ivorius.reccomplex.block.RCBlocks;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.commands.CommandVirtual;
import ivorius.reccomplex.commands.RCCommands;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class CommandNaturalSpace extends CommandExpecting implements CommandVirtual {
   public CommandNaturalSpace() {
   }

   public static int sidesClosed(MockWorld world, BlockPos coord, BlockArea area) {
      int sides = 0;
      BlockPos lower = area.getLowerCorner();
      BlockPos higher = area.getHigherCorner();
      if (sideClosed(world, new BlockPos(lower.getX(), coord.getY(), coord.getZ()), coord.getX() - lower.getX(), 1, 0, 0)) {
         ++sides;
      }

      if (sideClosed(world, new BlockPos(higher.getX(), coord.getY(), coord.getZ()), higher.getX() - coord.getX(), -1, 0, 0)) {
         ++sides;
      }

      if (sideClosed(world, new BlockPos(coord.getX(), coord.getY(), lower.getZ()), coord.getZ() - lower.getZ(), 0, 0, 1)) {
         ++sides;
      }

      if (sideClosed(world, new BlockPos(coord.getX(), coord.getY(), higher.getZ()), higher.getZ() - coord.getZ(), 0, 0, -1)) {
         ++sides;
      }

      return sides;
   }

   public static boolean sideClosed(MockWorld world, BlockPos coord, int iterations, int xP, int yP, int zP) {
      for(int i = 0; i < iterations; ++i) {
         BlockPos pos = coord.add(xP * i, yP * i, zP * i);
         IBlockState blockState = world.getBlockState(pos);
         if (!blockState.getBlock().isReplaceable(world, pos)) {
            return true;
         }
      }

      return false;
   }

   public static void placeNaturalAir(MockWorld world, BlockArea area, int floorDistance, int maxClosedSides) {
      BlockGenericSpace spaceBlock = RCBlocks.genericSpace;
      BlockPos lowerPoint = area.getLowerCorner();
      BlockPos higherPoint = area.getHigherCorner();
      Set<BlockPos> set = new HashSet();

      for(BlockSurfacePos surfaceCoord : BlockSurfaceArea.from(area)) {
         int safePoint = lowerPoint.getY();

         for(int y = higherPoint.getY(); y >= lowerPoint.getY(); --y) {
            IBlockState blockState = world.getBlockState(surfaceCoord.blockPos(y));
            if (blockState.getMaterial() != Material.AIR && blockState.getBlock() != spaceBlock || sidesClosed(world, surfaceCoord.blockPos(y), area) >= maxClosedSides) {
               boolean isFloor = blockState == RCBlocks.genericSolid.getDefaultState();
               safePoint = y + (isFloor ? 1 : floorDistance);
               break;
            }
         }

         for(int y = safePoint; y <= higherPoint.getY(); ++y) {
            set.add(surfaceCoord.blockPos(y));
         }

         if (safePoint > lowerPoint.getY()) {
            for(int y = lowerPoint.getY(); y <= higherPoint.getY(); ++y) {
               IBlockState blockState = world.getBlockState(surfaceCoord.blockPos(y));
               if (blockState.getMaterial() != Material.AIR && blockState.getBlock() != spaceBlock || sidesClosed(world, surfaceCoord.blockPos(y), area) >= maxClosedSides) {
                  safePoint = y - 1;
                  break;
               }
            }
         }

         for(int y = lowerPoint.getY(); y <= safePoint; ++y) {
            set.add(surfaceCoord.blockPos(y));
         }
      }

      set.forEach((pos) -> {
         IBlockState var7;
         label18: {
            BlockPos down = pos.down();
            BlockPos down2 = pos.down(2);
            if (pos.getY() > lowerPoint.getY() && !set.contains(down) && world.getBlockState(down).getBlock().isReplaceable(world, down) && world.getBlockState(down2).getBlock().isReplaceable(world, down2)) {
               Stream var10002 = (new BlockArea(pos.subtract(new Vec3i(2, 0, 2)), pos.add(new Vec3i(2, 0, 2)))).stream();
               set.getClass();
               if (var10002.allMatch(set::contains)) {
                  var7 = spaceBlock.getDefaultState().withProperty(BlockGenericSpace.TYPE, Integer.valueOf(1));
                  break label18;
               }
            }

            var7 = spaceBlock.getDefaultState();
         }

         world.setBlockState(pos, var7);
      });
   }

   public String getName() {
      return "space";
   }

   public void expect(Expect expect) {
      expect.named("distance-to-floor", "d").any("3", "2", "1").named("max-closed-sides", "s").any("3", "4", "5");
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MockWorld world, ICommandSender sender, String[] args) throws CommandException {
      SelectionOwner selectionOwner = RCCommands.getSelectionOwner(sender, (EnumFacing)null, true);
      RCCommands.assertSize(sender, selectionOwner);
      BlockArea area = selectionOwner.getSelection();
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      int floorDistance = ((Integer)parameters.get("distance-to-floor").to(NaP::asInt).optional().orElse(Integer.valueOf(0))).intValue() + 1;
      int maxClosedSides = ((Integer)parameters.get("max-closed-sides").to(NaP::asInt).optional().orElse(Integer.valueOf(3))).intValue();
      placeNaturalAir(world, area, floorDistance, maxClosedSides);
   }
}
