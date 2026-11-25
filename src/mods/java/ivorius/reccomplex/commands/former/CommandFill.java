package ivorius.reccomplex.commands.former;

import com.google.common.collect.Sets;
import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockStates;
import ivorius.ivtoolkit.math.IvShapeHelper;
import ivorius.ivtoolkit.world.MockWorld;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.commands.CommandVirtual;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class CommandFill extends CommandExpecting implements CommandVirtual {
   public static final int MAX_FLOOD = 125000;

   public CommandFill() {
   }

   public static void runShape(RCP.Shape shape, BlockArea area, Consumer<BlockPos> consumer) throws CommandException {
      BlockPos p1 = area.getPoint1();
      BlockPos p2 = area.getPoint2();
      switch(shape) {
      case cube:
         for(BlockPos pos : area) {
            consumer.accept(pos);
         }

         return;
      case sphere:
         double[] spheroidOrigin = new double[]{(double)(p1.getX() + p2.getX()) * 0.5D, (double)(p1.getY() + p2.getY()) * 0.5D, (double)(p1.getZ() + p2.getZ()) * 0.5D};
         int[] areaSize = area.areaSize();
         double[] spheroidSize = new double[]{(double)areaSize[0] * 0.5D, (double)areaSize[1] * 0.5D, (double)areaSize[2] * 0.5D};

         for(BlockPos pos : area) {
            double[] coordPoint = new double[]{(double)pos.getX(), (double)pos.getY(), (double)pos.getZ()};
            if (IvShapeHelper.isPointInSpheroid(coordPoint, spheroidOrigin, spheroidSize)) {
               consumer.accept(pos);
            }
         }

         return;
      default:
         throw new InternalError();
      }
   }

   public static void setFrom(MockWorld world, List<IBlockState> dst, BlockPos pos) {
      IBlockState state = (IBlockState)dst.get(world.rand().nextInt(dst.size()));
      world.setBlockState(pos, state, 2);
   }

   public String getName() {
      return RCConfig.commandPrefix + "fill";
   }

   public void expect(Expect expect) {
      expect.then(MCE::block).then(MCE::block).descriptionU("source expression").optional().repeat().named("metadata", "m").then(RCE::metadata).named("shape", "s").then(RCE::shape).named("flood", "f").words(RCE::directionExpression).descriptionU("direction expression");
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MockWorld world, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      Block dstBlock = (Block)parameters.get(0).to(MCP::block, sender).require();
      int[] dstMeta = (int[])parameters.get("metadata").to(RCP::metadatas).optional().orElse(new int[1]);
      List<IBlockState> dst = (List)IntStream.of(dstMeta).mapToObj((m) -> {
         return BlockStates.fromMetadata(dstBlock, m);
      }).collect(Collectors.toList());
      RCP.Shape shape = (RCP.Shape)parameters.get("shape").to(RCP::shape).optional().orElse(RCP.Shape.cube);
      PositionedBlockExpression matcher = (PositionedBlockExpression)parameters.get(1).rest(NaP::join).orElse("").to(RCP::expression, new PositionedBlockExpression(RecurrentComplex.specialRegistry)).require();
      SelectionOwner selectionOwner = RCCommands.getSelectionOwner(sender, (EnumFacing)null, true);
      RCCommands.assertSize(sender, selectionOwner);
      if (parameters.has("flood")) {
         List<EnumFacing> directions = (List)parameters.get("flood").orElse("").<EnumFacing>to(RCP::directions).require();
         List<BlockPos> dirty = new ArrayList();
         Set<BlockPos> visited = Sets.newHashSet(dirty);
         runShape(shape, selectionOwner.getSelection(), dirty::add);

         while(!dirty.isEmpty()) {
            BlockPos pos = (BlockPos)dirty.remove(dirty.size() - 1);

            for(EnumFacing facing : directions) {
               BlockPos offset = pos.offset(facing);
               if (((Boolean)matcher.evaluate(() -> {
                  return PositionedBlockExpression.Argument.at(world, offset);
               })).booleanValue() && visited.add(offset)) {
                  dirty.add(offset);
               }
            }

            if (visited.size() > 125000) {
               throw new CommandException("Area too big to flood!", new Object[0]);
            }
         }

         visited.forEach((p) -> {
            setFrom(world, dst, p);
         });
      } else {
         runShape(shape, selectionOwner.getSelection(), (posx) -> {
            if (((Boolean)matcher.evaluate(() -> {
               return PositionedBlockExpression.Argument.at(world, pos);
            })).booleanValue()) {
               setFrom(world, dst, pos);
            }

         });
      }

   }
}
