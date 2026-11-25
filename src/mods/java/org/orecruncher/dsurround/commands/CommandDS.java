package org.orecruncher.dsurround.commands;

import com.google.common.collect.ImmutableList;
import java.text.DecimalFormat;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityDimensionInfo;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfoEx;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.lib.Localization;

public final class CommandDS extends CommandBase {
   private static final String COMMAND = ModOptions.commands.ds.commandNameDS;
   private static final String COMMAND_OPTION_RAIN = "rain";
   private static final String COMMAND_OPTION_THUNDER = "thunder";
   private static final String COMMAND_OPTION_STATUS = "status";
   private static final String COMMAND_OPTION_RESET = "reset";
   private static final String COMMAND_OPTION_RELOAD = "reload";
   private static final String COMMAND_OPTION_SETMIN = "setmin";
   private static final String COMMAND_OPTION_SETMAX = "setmax";
   private static final String COMMAND_OPTION_SETSTRENGTH = "setstr";
   private static final String COMMAND_OPTION_SETTIME = "settime";
   private static final String COMMAND_OPTION_CONFIG = "config";
   private static final List<String> ALIAS = ImmutableList.builder().add(ModOptions.commands.ds.commandAliasDS.split(" ")).build();
   private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
   private static final List<String> HELP = ImmutableList.builder().add(TextFormatting.GOLD + "Dynamic Surroundings command help:").add(TextFormatting.YELLOW + "/" + COMMAND + " reset").add(TextFormatting.YELLOW + "/" + COMMAND + " reload").add(TextFormatting.YELLOW + "/" + COMMAND + " config").add(TextFormatting.YELLOW + "/" + COMMAND + " status <rain|thunder|aurora>").add(TextFormatting.YELLOW + "/" + COMMAND + " settime <rain|thunder> 0-1000").add(TextFormatting.YELLOW + "/" + COMMAND + " setstr rain 0-100").add(TextFormatting.YELLOW + "/" + COMMAND + " setmin rain 0-100").add(TextFormatting.YELLOW + "/" + COMMAND + " setmax rain 0-100").build();

   public CommandDS() {
   }

   public static String rainStatusOutput(World world, IDimensionInfoEx data) {
      StringBuilder builder = new StringBuilder();
      float minutes = (float)world.getWorldInfo().getRainTime() / 20.0F / 60.0F;
      builder.append(data.toString());
      builder.append("; isRaining: ").append(world.isRaining());
      builder.append("; isSurface: ").append(world.provider.isSurfaceWorld());
      builder.append("; strength: ").append(FORMATTER.format((double)(world.getRainStrength(1.0F) * 100.0F)));
      builder.append("; timer: ").append(FORMATTER.format((double)minutes)).append(" minutes");
      return builder.toString();
   }

   public static String thunderStatusOutput(World world, IDimensionInfoEx data) {
      StringBuilder builder = new StringBuilder();
      float minutes = (float)world.getWorldInfo().getThunderTime() / 20.0F / 60.0F;
      builder.append("dim ").append(data.getId());
      builder.append("; isThundering: ").append(world.isThundering());
      builder.append("; isSurface: ").append(world.provider.isSurfaceWorld());
      builder.append("; strength: ").append(FORMATTER.format((double)(world.getThunderStrength(1.0F) * 100.0F)));
      builder.append("; timer: ").append(FORMATTER.format((double)minutes)).append(" minutes");
      return builder.toString();
   }

   public static String config(IDimensionInfoEx data) {
      return data.configString();
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Nonnull
   public String getName() {
      return COMMAND;
   }

   @Nonnull
   public List<String> getAliases() {
      return ALIAS;
   }

   public String getUsage(@Nonnull ICommandSender sender) {
      return TextFormatting.GOLD + "/" + COMMAND + " help" + TextFormatting.BLUE + " -- Help for Dynamic Surroundings";
   }

   public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] parms) {
      try {
         EntityPlayerMP player = getCommandSenderAsPlayer(sender);
         World world = player.world;
         IDimensionInfoEx data = (IDimensionInfoEx)CapabilityDimensionInfo.getCapability(world);
         boolean showHelp = false;
         TextComponentString feedback = null;
         if (parms.length == 0) {
            showHelp = true;
         } else if ("reset".compareToIgnoreCase(parms[0]) == 0) {
            world.provider.resetRainAndThunder();
            feedback = new TextComponentString(Localization.format("dsurround.msg.RainReset", new Object[0]));
         } else if ("reload".compareToIgnoreCase(parms[0]) == 0) {
            RegistryManager.doReload();
            feedback = new TextComponentString(Localization.format("dsurround.msg.BiomeReload", new Object[0]));
         } else if ("config".compareToIgnoreCase(parms[0]) == 0) {
            if (data != null) {
               feedback = new TextComponentString(config(data));
            }
         } else if ("status".compareToIgnoreCase(parms[0]) == 0) {
            if (parms.length < 2) {
               showHelp = true;
            } else if ("rain".compareToIgnoreCase(parms[1]) == 0) {
               if (data != null) {
                  feedback = new TextComponentString(rainStatusOutput(world, data));
               }
            } else if ("thunder".compareToIgnoreCase(parms[1]) == 0 && data != null) {
               feedback = new TextComponentString(thunderStatusOutput(world, data));
            }
         } else if ("settime".compareToIgnoreCase(parms[0]) == 0) {
            if (parms.length < 3) {
               showHelp = true;
            } else {
               double d = parseDouble(parms[2], 0.0D, 1000.0D) * 20.0D * 60.0D;
               if ("rain".compareToIgnoreCase(parms[1]) == 0) {
                  world.getWorldInfo().setRainTime((int)d);
                  feedback = new TextComponentString(Localization.format("dsurround.msg.RainTimeSet", new Object[]{FORMATTER.format(d)}));
               } else if ("thunder".compareToIgnoreCase(parms[1]) == 0) {
                  world.getWorldInfo().setThunderTime((int)d);
                  feedback = new TextComponentString(Localization.format("dsurround.msg.ThunderTimeSet", new Object[]{FORMATTER.format(d)}));
               } else {
                  showHelp = true;
               }
            }
         } else if ("setstr".compareToIgnoreCase(parms[0]) == 0) {
            if (parms.length < 3) {
               showHelp = true;
            } else {
               double d = parseDouble(parms[2], 0.0D, 100.0D) / 100.0D;
               if ("rain".compareToIgnoreCase(parms[1]) == 0) {
                  if (data != null) {
                     data.setRainIntensity((float)d);
                     feedback = new TextComponentString(Localization.format("dsurround.msg.RainIntensitySet", new Object[]{FORMATTER.format((double)(data.getRainIntensity() * 100.0F))}));
                  }
               } else {
                  showHelp = true;
               }
            }
         } else if ("setmin".compareToIgnoreCase(parms[0]) == 0) {
            if (parms.length < 3) {
               showHelp = true;
            } else {
               double d = parseDouble(parms[2], 0.0D, 100.0D) / 100.0D;
               if ("rain".compareToIgnoreCase(parms[1]) == 0) {
                  if (data != null) {
                     data.setMinRainIntensity((float)d);
                     feedback = new TextComponentString(Localization.format("dsurround.msg.MinRainIntensitySet", new Object[]{FORMATTER.format((double)(data.getMinRainIntensity() * 100.0F))}));
                  }
               } else {
                  showHelp = true;
               }
            }
         } else if ("setmax".compareToIgnoreCase(parms[0]) == 0) {
            if (parms.length < 3) {
               showHelp = true;
            } else {
               double d = parseDouble(parms[2], 0.0D, 100.0D) / 100.0D;
               if ("rain".compareToIgnoreCase(parms[1]) == 0) {
                  if (data != null) {
                     data.setMaxRainIntensity((float)d);
                     feedback = new TextComponentString(Localization.format("dsurround.msg.MaxRainIntensitySet", new Object[]{FORMATTER.format((double)(data.getMaxRainIntensity() * 100.0F))}));
                  }
               } else {
                  showHelp = true;
               }
            }
         } else {
            showHelp = true;
         }

         if (showHelp) {
            for(String line : HELP) {
               sender.sendMessage(new TextComponentString(line));
            }
         } else if (feedback != null) {
            player.sendMessage(feedback);
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }
}
