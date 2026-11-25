package xzeroair.trinkets.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRace;

public class CommandMain extends CommandBase {
   private final List<String> tabCompletionsCommands = Arrays.asList("help", "mana", "race");
   private final String commandUsage = "/xat help";
   private final String listOfCommands = "   help <command>\nmana set <Amount>\nmana setMax <Amount>";
   private final String warn_notPlayerAdmin = "You do not have permission, or are not a player ingame!";
   private final String warn_invalidArgs = "Invalid Arguments";
   private final String warn_noItem = "Not holding an item!";
   private final String exportJsonReminder = "(Don't forget to exportJson !)";

   public CommandMain() {
   }

   public List<String> getAliases() {
      return Arrays.asList("xat");
   }

   public String getName() {
      return "Trinkets and Baubles";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public String getUsage(ICommandSender sender) {
      return "/xat <PLAYER>";
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      int length = args.length;
      if (length == 4) {
         String var10 = args[1].toLowerCase(Locale.ENGLISH);
         byte var11 = -1;
         switch(var10.hashCode()) {
         case 3343943:
            if (var10.equals("mana")) {
               var11 = 0;
            }
            break;
         case 3492561:
            if (var10.equals("race")) {
               var11 = 1;
            }
         }

         switch(var11) {
         case 0:
            String var12 = args[2].toLowerCase(Locale.ENGLISH);
            byte var13 = -1;
            switch(var12.hashCode()) {
            case 113762:
               if (var12.equals("set")) {
                  var13 = 0;
               }
            default:
               switch(var13) {
               case 0:
                  return getListOfStringsMatchingLastWord(args, new String[]{"0", "100", "200", "300"});
               default:
                  return Collections.emptyList();
               }
            }
         case 1:
            String var8 = args[2].toLowerCase(Locale.ENGLISH);
            byte var9 = -1;
            switch(var8.hashCode()) {
            case -1270970690:
               if (var8.equals("clearRace")) {
                  var9 = 0;
               }
               break;
            case -358815300:
               if (var8.equals("clearFoodRace")) {
                  var9 = 1;
               }
            }

            switch(var9) {
            case 0:
               return Collections.emptyList();
            case 1:
               return Collections.emptyList();
            default:
               return getListOfStringsMatchingLastWord(args, EntityRace.Registry.getKeys());
            }
         default:
            return Collections.emptyList();
         }
      } else if (length == 3) {
         String var6 = args[1].toLowerCase(Locale.ENGLISH);
         byte var7 = -1;
         switch(var6.hashCode()) {
         case 3343943:
            if (var6.equals("mana")) {
               var7 = 0;
            }
            break;
         case 3492561:
            if (var6.equals("race")) {
               var7 = 1;
            }
         }

         switch(var7) {
         case 0:
            return getListOfStringsMatchingLastWord(args, new String[]{"refill", "set"});
         case 1:
            return getListOfStringsMatchingLastWord(args, new String[]{"setRace", "setFoodRace", "clearRace", "clearFoodRace"});
         default:
            return Collections.emptyList();
         }
      } else if (length == 2) {
         return getListOfStringsMatchingLastWord(args, new String[]{"mana", "race"});
      } else {
         return length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
      }
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      int length = args.length;
      if (length == 0) {
         this.help(sender);
      } else {
         EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
         if (length > 1) {
            String var6 = args[1].toLowerCase(Locale.ENGLISH);
            byte var7 = -1;
            switch(var6.hashCode()) {
            case 3343943:
               if (var6.equals("mana")) {
                  var7 = 0;
               }
               break;
            case 3492561:
               if (var6.equals("race")) {
                  var7 = 1;
               }
            }

            switch(var7) {
            case 0:
               this.doMagic(entityplayer, server, sender, args);
               break;
            case 1:
               this.entityRace(entityplayer, server, sender, args);
               break;
            default:
               this.help(sender);
            }
         } else {
            this.help(sender);
         }

      }
   }

   private void helpCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length < 2) {
         this.message(sender, "   help <command>\nmana set <Amount>\nmana setMax <Amount>");
      } else {
         String var4 = args[1].toLowerCase();
         byte var5 = -1;
         switch(var4.hashCode()) {
         case 3198785:
            if (var4.equals("help")) {
               var5 = 0;
            }
         default:
            switch(var5) {
            case 0:
               this.message(sender, "If you need more help, you can contact the mod author on CurseForge or GitHub");
               return;
            default:
               this.message(sender, "/xat help <command> \n(Replace <command> with a simpledifficulty command name)");
            }
         }
      }
   }

   private void doMagic(EntityPlayer target, MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         try {
            int i = 0;
            MagicStats capability = Capabilities.getMagicStats(target);
            if (args.length > 2) {
               String var7 = args[2].toLowerCase(Locale.ENGLISH);
               byte var8 = -1;
               switch(var7.hashCode()) {
               case -934825418:
                  if (var7.equals("refill")) {
                     var8 = 0;
                  }
                  break;
               case 113762:
                  if (var7.equals("set")) {
                     var8 = 1;
                  }
               }

               switch(var8) {
               case 0:
                  capability.refillMana();
                  break;
               case 1:
                  if (args.length > 3) {
                     capability.setMana(Float.parseFloat(args[3]));
                  }
               }
            }
         } catch (NumberFormatException var9) {
            this.message(sender, "Invalid Arguments <MP>");
            return;
         }
      }

   }

   private void entityRace(EntityPlayer target, MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         try {
            int length = args.length;
            if (length < 2) {
               this.message(sender, "Invalid Arguments <modid>");
               return;
            }

            EntityProperties capability = Capabilities.getEntityProperties(target);
            String entity = args[0];
            if (length > 1) {
               String prefixCommand = args[1];
               if (length > 2) {
                  String command = args[2];
                  String race = "";
                  if (length > 3) {
                     race = args[3];
                  }

                  EntityRace r = EntityRace.getByNameOrId(race);
                  String var12 = command.toLowerCase(Locale.ENGLISH);
                  byte var13 = -1;
                  switch(var12.hashCode()) {
                  case -1989922159:
                     if (var12.equals("setfoodrace")) {
                        var13 = 1;
                     }
                     break;
                  case -1270017378:
                     if (var12.equals("clearrace")) {
                        var13 = 2;
                     }
                     break;
                  case -422506116:
                     if (var12.equals("clearfoodrace")) {
                        var13 = 3;
                     }
                     break;
                  case 1985873459:
                     if (var12.equals("setrace")) {
                        var13 = 0;
                     }
                  }

                  switch(var13) {
                  case 0:
                     if (r != null) {
                        capability.setOriginalRace(r);
                     }
                     break;
                  case 1:
                     if (r != null) {
                        capability.setImbuedRace(r);
                     }
                     break;
                  case 2:
                     capability.setOriginalRace(EntityRaces.none);
                     break;
                  case 3:
                     capability.setImbuedRace(EntityRaces.none);
                  }
               }
            }
         } catch (NumberFormatException var14) {
            this.message(sender, "Invalid Arguments <MP>");
            return;
         }
      }

   }

   private int getMetadataFromStack(ItemStack stack) {
      return stack.getHasSubtypes() ? stack.getMetadata() : -1;
   }

   private String getRegistryName(ItemStack stack) {
      return stack.getItem().getRegistryName().toString();
   }

   private boolean isAdminPlayer(ICommandSender sender) {
      return this.hasPermissionLevel(sender, 4) && sender.getCommandSenderEntity() instanceof EntityPlayer;
   }

   private void help(ICommandSender sender) {
      sender.sendMessage(new TextComponentString(this.getUsage(sender)));
   }

   private void message(ICommandSender sender, String message) {
      sender.sendMessage(new TextComponentString(message));
   }

   private boolean hasPermissionLevel(ICommandSender sender, int permLevel) {
      return sender.canUseCommand(permLevel, "Trinkets and Baubles");
   }

   private boolean hasNBTArgument(String[] input) {
      return this.hasArgument("--nbt", input);
   }

   private boolean hasClearArgument(String[] input) {
      return this.hasArgument("--clear", input);
   }

   private boolean hasArgument(String argument, String[] input) {
      if (input == null) {
         return false;
      } else {
         for(String s : input) {
            if (s.equals(argument)) {
               return true;
            }
         }

         return false;
      }
   }
}
