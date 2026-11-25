package mcjty.incontrol.commands;

import java.util.Set;
import java.util.function.Consumer;
import mcjty.incontrol.InControl;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

public class CmdShowMobs extends CommandBase {
   public CmdShowMobs() {
   }

   public String getName() {
      return "ctrlshowmobs";
   }

   public String getUsage(ICommandSender sender) {
      return "ctrlshowmobs";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      Set<ResourceLocation> keys = ForgeRegistries.ENTITIES.getKeys();
      keys.forEach((s) -> {
         InControl.setup.getLogger().log(Level.INFO, "Mob:" + s);
      });
   }
}
