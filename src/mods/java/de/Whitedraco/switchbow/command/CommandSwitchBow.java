package de.Whitedraco.switchbow.command;

import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.Config.WriteLoadConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandSwitchBow extends CommandBase {
   public CommandSwitchBow() {
   }

   public int getRequiredPermissionLevel() {
      return 3;
   }

   public int compareTo(ICommand o) {
      return this.getName().compareTo(o.getName());
   }

   public String getName() {
      return "switchbow";
   }

   public String getUsage(ICommandSender sender) {
      return "switchbow <Text>";
   }

   public List<String> getAliases() {
      List aliases = new ArrayList();
      aliases.add("switchbow");
      return aliases;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      World world = sender.getEntityWorld();
      if (!world.isRemote) {
         if (args.length == 0) {
            sender.sendMessage(new TextComponentString("Invalid argument"));
            return;
         }

         if (args[0].equals("spawnFire")) {
            if (args[1].equals("true")) {
               SwitchBowConfig.spawnFirewithLightning.setBool(true);

               try {
                  SwitchBowConfig.writeConfigSwitchBow();
               } catch (IOException var11) {
                  var11.printStackTrace();
               }

               sender.sendMessage(new TextComponentString("Spawn Fire to true"));
            } else if (args[1].equals("false")) {
               SwitchBowConfig.spawnFirewithLightning.setBool(false);

               try {
                  SwitchBowConfig.writeConfigSwitchBow();
               } catch (IOException var10) {
                  var10.printStackTrace();
               }

               sender.sendMessage(new TextComponentString("Spawn Fire to false"));
            }
         }

         if (args[0].equals("destroyBlockswithTNTArrow")) {
            if (args[1].equals("true")) {
               SwitchBowConfig.destroyBlockswithTNTArrow.setBool(true);

               try {
                  SwitchBowConfig.writeConfigSwitchBow();
               } catch (IOException var9) {
                  var9.printStackTrace();
               }

               sender.sendMessage(new TextComponentString("Destroy Blocks with TNT-Arrow to true"));
            } else if (args[1].equals("false")) {
               SwitchBowConfig.destroyBlockswithTNTArrow.setBool(false);

               try {
                  SwitchBowConfig.writeConfigSwitchBow();
               } catch (IOException var8) {
                  var8.printStackTrace();
               }

               sender.sendMessage(new TextComponentString("Destroy Blocks with TNT-Arrow to false"));
            }
         }

         if (args[0].equals("teleportDamage")) {
            if (args[1].equals("true")) {
               SwitchBowConfig.teleportdamage.setBool(true);

               try {
                  SwitchBowConfig.writeConfigSwitchBow();
               } catch (IOException var7) {
                  var7.printStackTrace();
               }

               sender.sendMessage(new TextComponentString("Damage from Teleporting by Teleport-Arrow to true"));
            } else if (args[1].equals("false")) {
               SwitchBowConfig.teleportdamage.setBool(false);

               try {
                  SwitchBowConfig.writeConfigSwitchBow();
               } catch (IOException var6) {
                  var6.printStackTrace();
               }

               sender.sendMessage(new TextComponentString("Damage from Teleporting by Teleport-Arrow to false"));
            }
         }

         if (args[0].equals("reloadBlacklist")) {
            WriteLoadConfig.reloadBlacklistConfig();
            WriteLoadConfig.reloadBlacklistBlocksConfig();
            sender.sendMessage(new TextComponentString("Reload EffectToMob Blacklist"));
            sender.sendMessage(new TextComponentString("Reload Burial-Blocks Blacklist"));
         }
      }

   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      if (args.length < 2) {
         List<String> returni = new ArrayList();
         returni.add("spawnFire");
         returni.add("destroyBlockswithTNTArrow");
         returni.add("teleportDamage");
         returni.add("reloadBlacklist");
         return returni;
      } else if (args.length == 2) {
         List<String> returni = new ArrayList();
         returni.add("true");
         returni.add("false");
         return returni;
      } else {
         return null;
      }
   }
}
