package ivorius.reccomplex.commands.clipboard;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.world.MockWorld;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.capability.RCEntityInfo;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.commands.CommandVirtual;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.RCTextStyle;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumFacing;

public class CommandSelectCopy extends CommandExpecting implements CommandVirtual {
   public CommandSelectCopy() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "copy";
   }

   public void expect(Expect expect) {
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MockWorld world, ICommandSender sender, String[] args) throws CommandException {
      RCEntityInfo RCEntityInfo = RCCommands.getStructureEntityInfo(sender, (EnumFacing)null);
      SelectionOwner selectionOwner = RCCommands.getSelectionOwner(sender, (EnumFacing)null, true);
      RCCommands.assertSize(sender, selectionOwner);
      BlockArea area = selectionOwner.getSelection();
      IvWorldData worldData = IvWorldData.capture(world, area, true);
      RCEntityInfo.setWorldDataClipboard(worldData.createTagCompound());
      sender.sendMessage(RecurrentComplex.translations.format("commands.selectCopy.success", RCTextStyle.area(area)));
   }
}
