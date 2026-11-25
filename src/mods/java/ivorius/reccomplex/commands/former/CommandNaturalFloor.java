package ivorius.reccomplex.commands.former;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockSurfaceArea;
import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.world.MockWorld;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.block.RCBlocks;
import ivorius.reccomplex.commands.CommandVirtual;
import ivorius.reccomplex.commands.RCCommands;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class CommandNaturalFloor extends CommandExpecting implements CommandVirtual {
   public CommandNaturalFloor() {
   }

   public static void placeNaturalFloor(MockWorld world, BlockArea area, double lowerExpansion) {
      lowerExpansion = lowerExpansion + 0.01D;
      IBlockState floorBlock = RCBlocks.genericSolid.getDefaultState();
      Block airBlock1 = RCBlocks.genericSpace;
      BlockPos lowerPoint = area.getLowerCorner();
      BlockPos higherPoint = area.getHigherCorner();
      Set<BlockSurfacePos> stopped = new HashSet();
      Set<BlockSurfacePos> stopping = new HashSet();

      for(int y = lowerPoint.getY() + 1; y <= higherPoint.getY(); ++y) {
         Stream var10000 = BlockSurfaceArea.from(area).stream().filter((o) -> {
            return !stopped.contains(o);
         });
         var10000.getClass();

         for(BlockSurfacePos surfaceCoord : var10000::iterator) {
            IBlockState block = world.getBlockState(surfaceCoord.blockPos(y));
            if (block.getMaterial() != Material.AIR && block.getBlock() != airBlock1) {
               stopping.add(surfaceCoord);
               if (block != floorBlock && block.getMaterial().isOpaque()) {
                  setBlockIfAirInArea(world, surfaceCoord.blockPos(y - 1), floorBlock, area);
                  fillSurface(world, area, lowerExpansion, floorBlock, surfaceCoord.blockPos(y), stopping);
               }
            }
         }

         stopped.addAll(stopping);
         stopping.clear();
      }

   }

   private static void fillSurface(MockWorld world, BlockArea area, double expansion, IBlockState floorBlock, BlockPos pos, Set<BlockSurfacePos> coords) {
      BlockSurfacePos surfacePos = BlockSurfacePos.from(pos);

      for(int expX = MathHelper.ceil(-expansion); (double)expX <= expansion; ++expX) {
         for(int expZ = MathHelper.ceil(-expansion); (double)expZ <= expansion; ++expZ) {
            if ((double)(expX * expX + expZ * expZ) <= expansion * expansion) {
               BlockSurfacePos scoord = surfacePos.add(expX, expZ);
               setBlockIfAirInArea(world, scoord.blockPos(pos.getY()), floorBlock, area);
               coords.add(scoord);
            }
         }
      }

   }

   public static void setBlockIfAirInArea(MockWorld world, BlockPos coord, IBlockState block, BlockArea area) {
      if (area.contains(coord)) {
         IBlockState prevBlock = world.getBlockState(coord);
         if (prevBlock.getMaterial() == Material.AIR || prevBlock.getBlock() == RCBlocks.genericSpace) {
            world.setBlockState(coord, block);
         }
      }

   }

   public String getName() {
      return "floor";
   }

   public void expect(Expect expect) {
      expect.named("expansion", "e").any("0", "1", "2");
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MockWorld world, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      BlockArea area = RCCommands.getSelectionOwner(sender, (EnumFacing)null, true).getSelection();
      double expandFloor = ((Double)parameters.get("expansion").to(NaP::asDouble).optional().orElse(Double.valueOf(1.0D))).doubleValue();
      placeNaturalFloor(world, area, expandFloor);
   }
}
