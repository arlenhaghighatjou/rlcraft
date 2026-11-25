package crafttweaker.mc1120.commands.dumpzscommand;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import crafttweaker.zenscript.GlobalRegistry;
import java.io.File;
import java.io.IOException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.io.FileUtils;

public class TargetJson extends DumpZsTarget {
   public TargetJson() {
      super("json");
   }

   public String getDescription() {
      return "Dumps to a JSON file for IDE integration";
   }

   public void execute(ICommandSender sender, MinecraftServer Server) {
      File file = new File("zs_export.json");
      ZsDumpCollector dump = new ZsDumpCollector();
      dump.collectTypeRegistry(GlobalRegistry.getTypes());
      dump.collectBracketHandlers(GlobalRegistry.getPrioritizedBracketHandlers());
      dump.collectRoot(GlobalRegistry.getRoot());

      try {
         FileUtils.writeStringToFile(file, dump.toJson(), "utf8");
      } catch (IOException var6) {
         CraftTweakerAPI.logError("Couldn't export json", var6);
         sender.sendMessage(new TextComponentString("\u00a74Couldn't export json file."));
      }

      sender.sendMessage(SpecialMessagesChat.getFileOpenText("Dumped content of the GlobalRegistry to a json file \u00a7r[\u00a76Click here to open\u00a7r]", file.getAbsolutePath()));
   }
}
