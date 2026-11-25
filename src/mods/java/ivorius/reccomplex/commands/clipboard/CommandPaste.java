package ivorius.reccomplex.commands.clipboard;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.capability.RCEntityInfo;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.parameters.IvP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.operation.OperationGenerateStructure;
import ivorius.reccomplex.operation.OperationRegistry;
import ivorius.reccomplex.utils.RCBlockAreas;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandPaste extends CommandExpecting {
   public CommandPaste() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "paste";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.then(MCE.pos("x", "y", "z")).named("rotation", "r").then(MCE::rotation).named("seed").words(RCE::randomString).flag("mirror", "m").flag("generate", "g").flag("select", "s");
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      EntityPlayerMP entityPlayerMP = getCommandSenderAsPlayer(sender);
      RCEntityInfo entityInfo = RCCommands.getStructureEntityInfo(entityPlayerMP, (EnumFacing)null);
      NBTTagCompound worldData = entityInfo.getWorldDataClipboard();
      if (worldData == null) {
         throw RecurrentComplex.translations.commandException("commands.strucPaste.noClipboard");
      } else {
         WorldServer world = (WorldServer)sender.getEntityWorld();
         BlockPos pos = (BlockPos)parameters.get(MCP.pos("x", "y", "z", sender.getPosition(), false)).require();
         AxisAlignedTransform2D transform = (AxisAlignedTransform2D)parameters.get(IvP.transform("rotation", "mirror")).optional().orElse(AxisAlignedTransform2D.ORIGINAL);
         String seed = (String)parameters.get("seed").optional().orElse((Object)null);
         boolean generate = parameters.has("generate");
         GenericStructure structure = GenericStructure.createDefaultStructure();
         structure.worldDataCompound = worldData;
         OperationRegistry.queueOperation((new OperationGenerateStructure(structure, (String)null, transform, pos, !generate)).withSeed(seed).prepare(world), sender);
         if (parameters.has("select")) {
            StructureGenerator<?> generator = (new StructureGenerator(structure)).transform(transform).lowerCoord(pos);
            RCCommands.select(sender, RCBlockAreas.from((StructureBoundingBox)generator.boundingBox().get()));
         }

      }
   }
}
