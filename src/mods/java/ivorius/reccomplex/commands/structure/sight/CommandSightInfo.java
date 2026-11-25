package ivorius.reccomplex.commands.structure.sight;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.RCTextStyle;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.RCBlockAreas;
import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandSightInfo extends SimpleCommand {
   public CommandSightInfo() {
      super("info", (expect) -> {
         expect.skip().descriptionU("id").required();
      });
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      WorldStructureGenerationData generationData = WorldStructureGenerationData.get(sender.getEntityWorld());
      WorldStructureGenerationData.Entry entry = generationData.getEntry(UUID.fromString((String)parameters.get(0).require()));
      if (entry == null) {
         throw RecurrentComplex.translations.commandException("commands.rcsightinfo.unknown");
      } else {
         ITextComponent area = RCTextStyle.area(RCBlockAreas.from(entry.getBoundingBox()));
         ITextComponent sight = RCTextStyle.sight(entry, true);
         if (entry instanceof WorldStructureGenerationData.StructureEntry) {
            WorldStructureGenerationData.StructureEntry structureEntry = (WorldStructureGenerationData.StructureEntry)entry;
            sender.sendMessage(new TextComponentTranslation("commands.rcsightinfo.structure", new Object[]{RCTextStyle.structure(structureEntry.getStructureID()), area, RCTextStyle.copy(String.valueOf(structureEntry.getSeed())), sight}));
            Structure structure = (Structure)StructureRegistry.INSTANCE.get(structureEntry.getStructureID());
            if (structure != null) {
               NBTStorable instanceData = structure.loadInstanceData(new StructureLoadContext(structureEntry.getTransform(), entry.getBoundingBox(), false), structureEntry.getInstanceData(), RCConfig.getUniversalTransformer());

               for(TextComponentBase s : structure.instanceDataInfo(instanceData)) {
                  sender.sendMessage(s);
               }
            }
         } else {
            sender.sendMessage(new TextComponentTranslation("commands.rcsightinfo.get", new Object[]{entry.description(), area, sight}));
         }

      }
   }
}
