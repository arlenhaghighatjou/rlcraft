package ivorius.reccomplex.commands.former;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.world.MockWorld;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.commands.CommandVirtual;
import ivorius.reccomplex.commands.RCCommands;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumFacing;

public class CommandNaturalAll extends CommandExpecting implements CommandVirtual {
   public CommandNaturalAll() {
   }

   public String getName() {
      return "smart";
   }

   public void expect(Expect expect) {
      expect.named("floor-expansion", "e").any("0", "1", "2").named("space-distance-to-floor", "f").any("3", "2", "1").named("space-max-closed-sides", "s").any("3", "4", "5");
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MockWorld world, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      SelectionOwner selectionOwner = RCCommands.getSelectionOwner(sender, (EnumFacing)null, true);
      RCCommands.assertSize(sender, selectionOwner);
      BlockArea area = selectionOwner.getSelection();
      double expandFloor = ((Double)parameters.get("floor-expansion").to(NaP::asDouble).optional().orElse(Double.valueOf(1.0D))).doubleValue();
      int floorDistance = ((Integer)parameters.get("space-distance-to-floor").to(NaP::asInt).optional().orElse(Integer.valueOf(2))).intValue() + 1;
      int maxClosedSides = ((Integer)parameters.get("space-max-closed-sides").to(NaP::asInt).optional().orElse(Integer.valueOf(3))).intValue();
      CommandNaturalFloor.placeNaturalFloor(world, area, expandFloor);
      CommandNaturalSpace.placeNaturalAir(world, area, floorDistance, maxClosedSides);
   }
}
