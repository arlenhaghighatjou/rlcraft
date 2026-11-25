package org.orecruncher.dsurround.commands;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.lib.expression.Expression;
import org.orecruncher.lib.expression.ExpressionException;
import org.orecruncher.lib.expression.IVariant;

@SideOnly(Side.CLIENT)
public class CommandCalc extends CommandBase {
   private static final String COMMAND = ModOptions.commands.calc.commandNameCalc;
   private static final String COMMAND_OPTION_HELP = "help";
   private static final String COMMAND_OPTION_FUNCS = "funcs";
   private static final String COMMAND_OPTION_VARS = "vars";
   private static final String COMMAND_OPTION_OPS = "ops";
   private static final List<String> ALIAS = ImmutableList.builder().add(ModOptions.commands.calc.commandAliasCalc.split(" ")).build();
   private static final List<String> HELP = ImmutableList.builder().add(TextFormatting.GOLD + "Calculator command help:").add(TextFormatting.YELLOW + "/" + COMMAND + " <expression>").add(TextFormatting.YELLOW + "/" + COMMAND + " funcs").add(TextFormatting.YELLOW + "/" + COMMAND + " vars").add(TextFormatting.YELLOW + "/" + COMMAND + " ops").build();

   public CommandCalc() {
   }

   @Nonnull
   public String getName() {
      return COMMAND;
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Nonnull
   public List<String> getAliases() {
      return ALIAS;
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return TextFormatting.GOLD + "/" + COMMAND + " help" + TextFormatting.BLUE + " -- Help for Calculator";
   }

   public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] parms) {
      try {
         boolean showHelp = false;
         if (parms.length == 0) {
            showHelp = true;
         } else if ("help".compareToIgnoreCase(parms[0]) == 0) {
            showHelp = true;
         } else if ("funcs".compareToIgnoreCase(parms[0]) == 0) {
            Expression exp = new Expression("0");

            for(String line : exp.getDeclaredFunctions()) {
               sender.sendMessage(new TextComponentString(line));
            }
         } else if ("vars".compareToIgnoreCase(parms[0]) == 0) {
            Expression exp = new Expression("0");

            for(String line : exp.getDeclaredVariables()) {
               sender.sendMessage(new TextComponentString(line));
            }
         } else if ("ops".compareToIgnoreCase(parms[0]) == 0) {
            Expression exp = new Expression("0");

            for(String line : exp.getDeclaredOperators()) {
               sender.sendMessage(new TextComponentString(line));
            }
         } else {
            try {
               Expression exp = new Expression(buildString(parms, 0));
               IVariant result = exp.eval();
               sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "-> " + result.asString()));
            } catch (ExpressionException var8) {
               sender.sendMessage(new TextComponentString(TextFormatting.RED + var8.getMessage()));
            } catch (Throwable var9) {
               sender.sendMessage(new TextComponentString(TextFormatting.RED + "Internal error"));
               showHelp = true;
            }
         }

         if (showHelp) {
            for(String line : HELP) {
               sender.sendMessage(new TextComponentString(line));
            }
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }

   }
}
