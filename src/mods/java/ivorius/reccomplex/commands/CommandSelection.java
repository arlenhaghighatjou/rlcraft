package ivorius.reccomplex.commands;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.ivtoolkit.math.IvVecMathHelper;
import ivorius.ivtoolkit.world.MockWorld;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandSplit;
import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class CommandSelection extends CommandSplit implements CommandVirtual {
   public ICommand set;

   public CommandSelection() {
      this.add(new CommandSelection.Command("clear") {
         public void execute(MockWorld world, ICommandSender sender, Parameters parameters, SelectionOwner owner) throws CommandException {
            owner.setSelection((BlockArea)null);
         }
      });
      this.add(new CommandSelection.Command("get") {
         public void execute(MockWorld world, ICommandSender sender, Parameters parameters, SelectionOwner owner) throws CommandException {
            sender.sendMessage(RecurrentComplex.translations.format("commands.selectSet.get", RCTextStyle.area(owner.getSelection())));
            if (owner.hasValidSelection()) {
               sender.sendMessage(RecurrentComplex.translations.format("commands.selectSet.size", RCTextStyle.size(owner.getSelection().areaSize()), IvVecMathHelper.product(owner.getSelection().areaSize())));
            }

         }
      });
      this.add(this.set = new CommandSelection.Command("set", (expect) -> {
         expect.then(MCE::xyz).then(MCE::xyz).required().flag("first").flag("second");
      }) {
         public void execute(MockWorld world, ICommandSender sender, Parameters parameters, SelectionOwner owner) throws CommandException {
            boolean first = !parameters.has("second");
            boolean second = !parameters.has("first");
            boolean shiftSecond = false;
            if (!first && !second) {
               shiftSecond = true;
               first = true;
               second = true;
            }

            if (first) {
               if (owner.getSelectedPoint1() == null) {
                  owner.setSelectedPoint1(sender.getPosition());
               }

               owner.setSelectedPoint1((BlockPos)parameters.get(0).to(MCP.pos(owner.getSelectedPoint1(), false)).require());
            }

            if (second) {
               if (owner.getSelectedPoint2() == null) {
                  owner.setSelectedPoint2(sender.getPosition());
               }

               owner.setSelectedPoint2((BlockPos)parameters.get(shiftSecond ? 3 : 0).to(MCP.pos(owner.getSelectedPoint2(), false)).require());
            }

         }
      });
      this.add(new CommandSelection.Command("crop", (expect) -> {
         expect.then(MCE::block).descriptionU("positioned block expression");
      }) {
         public void execute(MockWorld world, ICommandSender sender, Parameters parameters, SelectionOwner owner) throws CommandException {
            RCCommands.ensureValidSelection(owner, false);
            BlockArea area = owner.getSelection();
            PositionedBlockExpression matcher = (PositionedBlockExpression)parameters.get(0).rest(NaP::join).orElse("is:air").to(RCP::expression, new PositionedBlockExpression(RecurrentComplex.specialRegistry)).require();
            EnumFacing[] var7 = EnumFacing.VALUES;
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               for(EnumFacing direction = var7[var9]; area != null && CommandSelection.sideStream(area, direction).allMatch((p) -> {
                  return matcher.test(PositionedBlockExpression.Argument.at(world, p));
               }); area = BlockAreas.shrink(area, direction, 1)) {
                  ;
               }
            }

            owner.setSelection(area);
         }
      });
      this.add(new CommandSelection.Command("wand", (expect) -> {
         expect.then(MCE::block).descriptionU("positioned block expression");
      }) {
         public void execute(MockWorld world, ICommandSender sender, Parameters parameters, SelectionOwner owner) throws CommandException {
            RCCommands.ensureValidSelection(owner, true);
            BlockArea area = owner.getSelection();
            boolean changed = true;
            int total = 0;

            while(changed) {
               changed = false;
               PositionedBlockExpression matcher = (PositionedBlockExpression)parameters.get(0).rest(NaP::join).orElse("!is:air").to(RCP::expression, new PositionedBlockExpression(RecurrentComplex.specialRegistry)).require();
               EnumFacing[] var9 = EnumFacing.VALUES;
               int var10 = var9.length;

               BlockArea expand;
               for(int var11 = 0; var11 < var10; ++var11) {
                  for(EnumFacing direction = var9[var11]; CommandSelection.sideStream(expand = BlockAreas.expand(area, direction, 1), direction).anyMatch((p) -> {
                     return matcher.test(PositionedBlockExpression.Argument.at(world, p));
                  }) && total++ < 300; changed = true) {
                     area = expand;
                  }
               }
            }

            owner.setSelection(area);
         }
      });
      this.add(new CommandSelection.Command("shrink", (expect) -> {
         expect.any("1", "2", "3").descriptionU("all").named("x").any("1", "2", "3").descriptionU("x").named("y").any("1", "2", "3").descriptionU("y").named("z").any("1", "2", "3").descriptionU("z");
      }) {
         public void execute(MockWorld world, ICommandSender sender, Parameters parameters, SelectionOwner owner) throws CommandException {
            RCCommands.ensureValidSelection(owner, false);
            BlockPos base = (BlockPos)parameters.get(0).to(MCP.pos(parameters.get(0), parameters.get(0), BlockPos.ORIGIN, false)).require();
            BlockPos shrink = (BlockPos)parameters.get(MCP.pos("x", "y", "z", base, false)).require();
            owner.setSelection(BlockAreas.shrink(owner.getSelection(), shrink, shrink));
         }
      });
      this.add(new CommandSelection.Command("expand", (expect) -> {
         expect.any("1", "2", "3").descriptionU("all").named("x").any("1", "2", "3").descriptionU("x").named("y").any("1", "2", "3").descriptionU("y").named("z").any("1", "2", "3").descriptionU("z");
      }) {
         public void execute(MockWorld world, ICommandSender sender, Parameters parameters, SelectionOwner owner) throws CommandException {
            RCCommands.ensureValidSelection(owner, true);
            BlockPos base = (BlockPos)parameters.get(0).to(MCP.pos(parameters.get(0), parameters.get(0), BlockPos.ORIGIN, false)).require();
            BlockPos shrink = (BlockPos)parameters.get(MCP.pos("x", "y", "z", base, false)).require();
            owner.setSelection(BlockAreas.expand(owner.getSelection(), shrink, shrink));
         }
      });
      this.permitFor(2);
   }

   @Nonnull
   protected static Stream<BlockPos> sideStream(BlockArea area, EnumFacing direction) {
      return StreamSupport.stream(BlockAreas.side(area, direction).spliterator(), false);
   }

   public String getName() {
      return RCConfig.commandPrefix + "selection";
   }

   public void execute(MockWorld world, ICommandSender sender, String[] args) throws CommandException {
      MinecraftServer server = sender.getServer();
      ICommand command = this.validatedCommand(server, sender, args);
      if (!(command instanceof CommandVirtual)) {
         throw RecurrentComplex.translations.commandException("commands.rcmap.nonvirtual");
      } else {
         CommandVirtual virtual = (CommandVirtual)command;
         virtual.execute(world, sender, this.splitParameters(args));
      }
   }

   public abstract static class Command extends SimpleCommand implements CommandVirtual {
      public Command(String name) {
         super(name);
      }

      public Command(String name, Consumer<Expect> expector) {
         super(name, expector);
      }

      public Command(String name, String usage, Consumer<Expect> expector) {
         super(name, usage, expector);
      }

      public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
         SelectionOwner owner = RCCommands.getSelectionOwner(sender, (EnumFacing)null, false);
         Expect var10001 = this.expect();
         var10001.getClass();
         Parameters parameters = Parameters.of(args, var10001::declare);
         this.execute(MockWorld.of(sender.getEntityWorld()), sender, parameters, owner);
      }

      public void execute(MockWorld world, ICommandSender sender, String[] args) throws CommandException {
         SelectionOwner owner = RCCommands.getSelectionOwner(sender, (EnumFacing)null, false);
         Expect var10001 = this.expect();
         var10001.getClass();
         Parameters parameters = Parameters.of(args, var10001::declare);
         this.execute(world, sender, parameters, owner);
      }

      public abstract void execute(MockWorld var1, ICommandSender var2, Parameters var3, SelectionOwner var4) throws CommandException;
   }
}
