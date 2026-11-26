package net.silentchaos512.scalinghealth.command;

import net.lax1dude.eaglercraft.v1_8.HString;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.lib.command.CommandBaseSL;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import net.silentchaos512.scalinghealth.world.ScalingHealthSavedData;

public class CommandScalingHealth extends CommandBaseSL {
   private static final String NUMFORMAT = "%.2f";

   public CommandScalingHealth() {
   }

   public String getName() {
      return "scalinghealth";
   }

   public String getUsage(ICommandSender sender) {
      return TextFormatting.RED + "Usage: /" + this.getName() + " <difficulty|health|world_difficulty> <get|set|add|sub> [value] [player]";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 1 && "config_reload".equals(args[0])) {
         executeConfigReload(sender);
      } else if (args.length < 2) {
         tell(sender, this.getUsage(sender), false);
      } else {
         String command = args[0];
         CommandScalingHealth.SubCommand subCommand = CommandScalingHealth.SubCommand.fromArg(args[1]);
         boolean isGet = subCommand == CommandScalingHealth.SubCommand.GET;
         if (subCommand != null && (isGet || args.length >= 3)) {
            double value = isGet ? -1.0D : parseDouble(args[2]);
            List<EntityPlayerMP> targets = getTargetPlayers(server, sender, isGet, args);
            if ("difficulty".equals(command)) {
               executeDifficulty(server, sender, subCommand, value, targets);
            } else if ("health".equals(command)) {
               executeHealth(server, sender, subCommand, value, targets);
            } else if ("world_difficulty".equals(command)) {
               executeWorldDifficulty(server, sender, subCommand, value, sender.getEntityWorld());
            } else {
               tell(sender, this.getUsage(sender), false);
            }

         } else {
            tell(sender, this.getUsage(sender), false);
         }
      }
   }

   private static List<EntityPlayerMP> getTargetPlayers(MinecraftServer server, ICommandSender sender, boolean isGet, String[] args) throws PlayerNotFoundException, CommandException {
      int index = isGet ? 2 : 3;
      return (List<EntityPlayerMP>)(args.length < index + 1 ? ImmutableList.of(getCommandSenderAsPlayer(sender)) : getPlayers(server, sender, args[index]));
   }

   private static void executeDifficulty(MinecraftServer server, ICommandSender sender, @Nonnull CommandScalingHealth.SubCommand subCommand, double value, List<EntityPlayerMP> targets) {
      if (!targets.isEmpty()) {
         for(EntityPlayerMP player : targets) {
            SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
            if (data == null) {
               tell(sender, "Player data is null for " + player.getName(), false);
            } else if (subCommand == CommandScalingHealth.SubCommand.GET) {
               double current = data.getDifficulty();
               String strCurrent = HString.format("%.2f", current);
               String strMax = HString.format("%.2f", Config.Difficulty.maxValue);
               tell(sender, "showDifficulty", true, player.getName(), strCurrent, strMax);
            } else {
               double current = data.getDifficulty();
               double toSet = getValueToSet(subCommand, value, current);
               double min = getMinValue(subCommand, current, (double)Config.Difficulty.minValue, (double)Config.Difficulty.maxValue);
               double max = getMaxValue(subCommand, current, (double)Config.Difficulty.minValue, (double)Config.Difficulty.maxValue);
               if (!checkBounds(value, min, max)) {
                  tell(sender, TextFormatting.RED, "outOfBounds", true, HString.format("%.2f", min), HString.format("%.2f", max));
                  return;
               }

               data.setDifficulty(toSet);
               tell(sender, "setDifficulty", true, player.getName(), HString.format("%.2f", toSet));
            }
         }

      }
   }

   private static void executeWorldDifficulty(MinecraftServer server, ICommandSender sender, @Nonnull CommandScalingHealth.SubCommand subCommand, double value, World world) {
      ScalingHealthSavedData data = ScalingHealthSavedData.get(world);
      if (data == null) {
         tell(sender, "World data is null!", false);
      } else {
         if (subCommand == CommandScalingHealth.SubCommand.GET) {
            double current = data.difficulty;
            String strCurrent = HString.format("%.2f", current);
            String strMax = HString.format("%.2f", Config.Difficulty.maxValue);
            tell(sender, "showWorldDifficulty", true, strCurrent, strMax);
         } else {
            double current = data.difficulty;
            double toSet = getValueToSet(subCommand, value, current);
            double min = getMinValue(subCommand, current, (double)Config.Difficulty.minValue, (double)Config.Difficulty.maxValue);
            double max = getMaxValue(subCommand, current, (double)Config.Difficulty.minValue, (double)Config.Difficulty.maxValue);
            if (!checkBounds(value, min, max)) {
               tell(sender, TextFormatting.RED, "outOfBounds", true, HString.format("%.2f", min), HString.format("%.2f", max));
               return;
            }

            data.difficulty = toSet;
            data.markDirty();
            tell(sender, "setWorldDifficulty", true, HString.format("%.2f", toSet));
         }

      }
   }

   private static void executeHealth(MinecraftServer server, ICommandSender sender, @Nonnull CommandScalingHealth.SubCommand subCommand, double value, List<EntityPlayerMP> targets) {
      if (!targets.isEmpty()) {
         for(EntityPlayerMP player : targets) {
            SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
            if (data == null) {
               tell(sender, "Player data is null for " + player.getName(), false);
            } else if (subCommand == CommandScalingHealth.SubCommand.GET) {
               float current = player.getHealth();
               float max = player.getMaxHealth();
               float modValue = data.getMaxHealth() - (float)Config.Player.Health.startingHealth;
               String strCurrent = HString.format("%.2f", current);
               String strMax = HString.format("%.2f", max);
               String strMod = (modValue >= 0.0F ? "+" : "") + modValue;
               tell(sender, "showHealth", true, player.getName(), strCurrent, strMax, strMod);
            } else {
               double current = (double)data.getMaxHealth();
               double toSet = getValueToSet(subCommand, value, current);
               double hardMax = (double)Config.Player.Health.maxHealth;
               hardMax = (double)((int)(hardMax <= 0.0D ? SharedMonsterAttributes.MAX_HEALTH.clampValue(2.147483647E9D) : hardMax));
               double min = getMinValue(subCommand, current, 2.0D, hardMax);
               double max = getMaxValue(subCommand, current, 2.0D, hardMax);
               if (!checkBounds(value, min, max)) {
                  tell(sender, TextFormatting.RED, "outOfBounds", true, min, max);
                  return;
               }

               float toHeal = (float)(toSet - current);
               data.setMaxHealth((float)toSet);
               if (toHeal > 0.0F) {
                  player.heal(toHeal);
               }

               tell(sender, "setHealth", true, player.getName(), toSet);
            }
         }

      }
   }

   private static void executeConfigReload(ICommandSender sender) {
      sender.sendMessage(new TextComponentString("Attempting to reload config, check log for details"));
      Config.INSTANCE.load();
   }

   private static double getValueToSet(CommandScalingHealth.SubCommand subCommand, double value, double current) {
      double toSet = value;
      if (subCommand == CommandScalingHealth.SubCommand.ADD) {
         toSet = current + value;
      } else if (subCommand == CommandScalingHealth.SubCommand.SUB) {
         toSet = current - value;
      }

      return toSet;
   }

   private static double getMinValue(CommandScalingHealth.SubCommand subCommand, double current, double min, double max) {
      if (subCommand == CommandScalingHealth.SubCommand.ADD) {
         return min - current;
      } else {
         return subCommand == CommandScalingHealth.SubCommand.SUB ? current - max : min;
      }
   }

   private static double getMaxValue(CommandScalingHealth.SubCommand subCommand, double current, double min, double max) {
      if (subCommand == CommandScalingHealth.SubCommand.ADD) {
         return max - current;
      } else {
         return subCommand == CommandScalingHealth.SubCommand.SUB ? current - min : max;
      }
   }

   private static boolean checkBounds(double value, double min, double max) {
      return value >= min && value <= max;
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, new String[]{"difficulty", "health", "world_difficulty"});
      } else if (args.length == 2) {
         return getListOfStringsMatchingLastWord(args, new String[]{"get", "set", "add", "sub"});
      } else {
         return (List<String>)(this.isUsernameIndex(args, args.length) ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : ImmutableList.of());
      }
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return args.length > 2 && "get".equals(args[1]) ? index == 3 : index == 4;
   }

   private static void tell(ICommandSender sender, String key, boolean fromLocalizationFile, Object... args) {
      tell(sender, TextFormatting.RESET, key, fromLocalizationFile, args);
   }

   private static void tell(ICommandSender sender, TextFormatting format, String key, boolean fromLocalizationFile, Object... args) {
      key = "command.scalinghealth." + key;
      if (fromLocalizationFile) {
         sender.sendMessage((new TextComponentString(String.valueOf(format))).appendSibling(new TextComponentTranslation(key, args)));
      } else {
         sender.sendMessage(new TextComponentString(format + HString.format(key, args)));
      }

   }

   static enum SubCommand {
      GET,
      SET,
      ADD,
      SUB;

      private SubCommand() {
      }

      @Nullable
      static CommandScalingHealth.SubCommand fromArg(String arg) {
         for(CommandScalingHealth.SubCommand val : values()) {
            if (val.name().equalsIgnoreCase(arg)) {
               return val;
            }
         }

         return null;
      }
   }
}
