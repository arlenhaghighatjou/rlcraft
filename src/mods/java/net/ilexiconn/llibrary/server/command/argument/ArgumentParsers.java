package net.ilexiconn.llibrary.server.command.argument;

import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public enum ArgumentParsers implements IArgumentParser {
   INTEGER {
      public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
         return CommandBase.parseInt(argument);
      }

      public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
         return Collections.emptyList();
      }
   },
   BOOLEAN {
      public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
         return CommandBase.parseBoolean(argument);
      }

      public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
         return CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"true", "false"});
      }
   },
   STRING {
      public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
         return argument;
      }

      public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
         return Collections.emptyList();
      }
   },
   FLOAT {
      public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
         return (float)CommandBase.parseDouble(argument);
      }

      public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
         return Collections.emptyList();
      }
   },
   DOUBLE {
      public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
         return CommandBase.parseDouble(argument);
      }

      public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
         return Collections.emptyList();
      }
   },
   PLAYER {
      public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
         return CommandBase.getPlayer(server, sender, argument);
      }

      public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
         return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
      }
   },
   ITEM_STACK {
      public Object parseArgument(MinecraftServer server, ICommandSender sender, String argument) throws CommandException {
         return new ItemStack(CommandBase.getItemByText(sender, argument));
      }

      public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
         return CommandBase.getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
      }
   };

   private ArgumentParsers() {
   }

   public static <T> IArgumentParser<T> getBuiltinParser(Class<T> type) {
      if (!Integer.class.isAssignableFrom(type) && !Integer.TYPE.isAssignableFrom(type)) {
         if (!Boolean.class.isAssignableFrom(type) && !Boolean.TYPE.isAssignableFrom(type)) {
            if (String.class.isAssignableFrom(type)) {
               return STRING;
            } else if (!Float.class.isAssignableFrom(type) && !Float.TYPE.isAssignableFrom(type)) {
               if (!Double.class.isAssignableFrom(type) && !Double.TYPE.isAssignableFrom(type)) {
                  if (EntityPlayer.class.isAssignableFrom(type)) {
                     return PLAYER;
                  } else {
                     return ItemStack.class.isAssignableFrom(type) ? ITEM_STACK : null;
                  }
               } else {
                  return DOUBLE;
               }
            } else {
               return FLOAT;
            }
         } else {
            return BOOLEAN;
         }
      } else {
         return INTEGER;
      }
   }
}
