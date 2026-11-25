package ivorius.reccomplex.commands.former;

import ivorius.ivtoolkit.world.MockWorld;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.commands.CommandVirtual;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerProperty;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class CommandSetProperty extends SimpleCommand implements CommandVirtual {
   public CommandSetProperty() {
      super(RCConfig.commandPrefix + "property");
      this.permitFor(2);
   }

   public void expect(Expect expect) {
      expect.next(TransformerProperty.propertyNameStream().collect(Collectors.toSet())).descriptionU("key").required().next((params) -> {
         return params.get(0).tryGet().map(TransformerProperty::propertyValueStream);
      }).descriptionU("value").required().named("exp").words(MCE::block).descriptionU("positioned block expression").named("shape", "s").then(RCE::shape);
   }

   public void execute(MockWorld world, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      PositionedBlockExpression matcher = (PositionedBlockExpression)parameters.get("exp").orElse("").to(RCP::expression, new PositionedBlockExpression(RecurrentComplex.specialRegistry)).require();
      String propertyName = (String)parameters.get(0).require();
      String propertyValue = (String)parameters.get(1).require();
      RCP.Shape shape = (RCP.Shape)parameters.get("shape").to(RCP::shape).optional().orElse(RCP.Shape.cube);
      Consumer<BlockPos> consumer = (pos) -> {
         PositionedBlockExpression.Argument at = PositionedBlockExpression.Argument.at(world, pos);
         if (matcher.test(at)) {
            TransformerProperty.withProperty(at.state, propertyName, propertyValue).ifPresent((state) -> {
               world.setBlockState(pos, state, 3);
            });
         }

      };
      SelectionOwner selectionOwner = RCCommands.getSelectionOwner(sender, (EnumFacing)null, true);
      RCCommands.assertSize(sender, selectionOwner);
      CommandFill.runShape(shape, selectionOwner.getSelection(), consumer);
   }
}
