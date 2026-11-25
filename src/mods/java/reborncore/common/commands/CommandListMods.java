package reborncore.common.commands;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.util.StringJoiner;
import java.util.function.Consumer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;

public class CommandListMods extends CommandBase {
   public CommandListMods() {
   }

   public String getName() {
      return "rc_dumpmods";
   }

   public String getUsage(ICommandSender sender) {
      return this.getName();
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());
      Loader.instance().getActiveModList().forEach((modContainer) -> {
         stringJoiner.add(modContainer.getModId());
      });
      File outputFile = new File("mod_ids_export.txt");

      try {
         FileUtils.writeStringToFile(outputFile, stringJoiner.toString(), Charsets.UTF_8);
      } catch (IOException var7) {
         var7.printStackTrace();
         sender.sendMessage(new TextComponentString(var7.getMessage()));
         return;
      }

      sender.sendMessage(new TextComponentString("Mod list written to " + outputFile.getAbsolutePath()));
   }
}
