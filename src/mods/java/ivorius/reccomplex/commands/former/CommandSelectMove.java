package ivorius.reccomplex.commands.former;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.parameters.IvP;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.operation.Operation;
import ivorius.reccomplex.operation.OperationClearArea;
import ivorius.reccomplex.operation.OperationGenerateStructure;
import ivorius.reccomplex.operation.OperationMulti;
import ivorius.reccomplex.operation.OperationRegistry;
import ivorius.reccomplex.utils.RCBlockAreas;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandSelectMove extends CommandExpecting {
   public CommandSelectMove() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "move";
   }

   public void expect(Expect expect) {
      expect.then(MCE.pos("x", "y", "z")).named("rotation", "r").then(MCE::rotation).flag("mirror", "m").flag("noselect", "s").flag("duplicate", "c").named("times", "t").any(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      SelectionOwner selectionOwner = RCCommands.getSelectionOwner(commandSender, (EnumFacing)null, true);
      RCCommands.assertSize(commandSender, selectionOwner);
      BlockPos move = (BlockPos)parameters.get(RCP.pos("x", "y", "z", BlockPos.ORIGIN, false)).require();
      AxisAlignedTransform2D transform = (AxisAlignedTransform2D)parameters.get(IvP.transform("rotation", "mirror")).optional().orElse(AxisAlignedTransform2D.ORIGINAL);
      boolean noselect = parameters.has("noselect");
      boolean duplicate = parameters.has("duplicate");
      int times = ((Integer)parameters.get("times").to(NaP::asInt).optional().orElse(Integer.valueOf(1))).intValue();
      BlockArea area = selectionOwner.getSelection();
      IvWorldData worldData = IvWorldData.capture(commandSender.getEntityWorld(), area, true);
      NBTTagCompound worldDataCompound = worldData.createTagCompound();
      GenericStructure structure = GenericStructure.createDefaultStructure();
      structure.worldDataCompound = worldDataCompound;
      BlockPos pos = selectionOwner.getSelection().getLowerCorner();

      for(int i = 0; i < times; ++i) {
         pos = pos.add(move);
         if (duplicate) {
            OperationRegistry.queueOperation((new OperationGenerateStructure(structure, (String)null, transform, pos, true)).prepare((WorldServer)commandSender.getEntityWorld()), commandSender);
         } else {
            OperationRegistry.queueOperation(new OperationMulti(new Operation[]{new OperationClearArea(area), (new OperationGenerateStructure(structure, (String)null, transform, pos, true)).prepare((WorldServer)commandSender.getEntityWorld())}), commandSender);
         }
      }

      if (!noselect) {
         StructureGenerator<GenericStructure.InstanceData> generator = (new StructureGenerator<GenericStructure.InstanceData>(structure)).transform(transform).lowerCoord(pos);
         StructureBoundingBox boundingBox = (StructureBoundingBox)generator.boundingBox().get();
         selectionOwner.setSelection(RCBlockAreas.from(boundingBox));
      }

   }
}
