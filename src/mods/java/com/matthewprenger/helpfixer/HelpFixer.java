package com.matthewprenger.helpfixer;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "helpfixer",
   name = "HelpFixer",
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.1]"
)
public final class HelpFixer {
   private static final Logger log = LogManager.getLogger();
   static final String MOD_ID = "helpfixer";
   static final String MOD_NAME = "HelpFixer";
   private static final ICommand testCmd1 = new CommandBase() {
      public String getName() {
         return "a";
      }

      public String getUsage(ICommandSender sender) {
         return null;
      }

      public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      }
   };
   private static final ICommand testCmd2 = new CommandBase() {
      public String getName() {
         return "z";
      }

      public String getUsage(ICommandSender sender) {
         return null;
      }

      public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      }
   };

   public HelpFixer() {
   }

   @EventHandler
   public void onServerStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandHelp() {
         protected List<ICommand> getSortedPossibleCommands(ICommandSender sender, MinecraftServer server) {
            List<ICommand> list = server.getCommandManager().getPossibleCommands(sender);
            Iterator<ICommand> iterator = list.iterator();

            while(iterator.hasNext()) {
               ICommand command = (ICommand)iterator.next();

               try {
                  if (command.getName() == null) {
                     HelpFixer.log.warn("Identified command with null name, Ignoring: {}", command.getClass().getName());
                     iterator.remove();
                  } else if (command.getUsage(sender) == null) {
                     HelpFixer.log.warn("Identified command with null usage, Ignoring: {}", command.getClass().getName());
                     iterator.remove();
                  }
               } catch (Exception var7) {
                  HelpFixer.log.warn("Failed to test command '{}'", command, var7);
               }
            }

            list.sort(Comparator.comparing(ICommand::getName));
            return list;
         }
      });
   }

   @EventHandler
   public void onServerStarted(FMLServerStartedEvent event) {
      for(ICommand command : FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().values()) {
         if (!validCompareTo(command)) {
            log.warn("Command {} incorrectly overrides compareTo: %s", command.getName(), command.getClass().getName());
         }
      }

   }

   static boolean validCompareTo(@Nonnull ICommand command) {
      try {
         return command.compareTo(testCmd1) != command.compareTo(testCmd2);
      } catch (Exception var2) {
         log.warn("Failed to test command '{}' for a valid compareTo", command, var2);
         return true;
      }
   }
}
