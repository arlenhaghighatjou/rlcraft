package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class SRPCommandEvolution implements ICommand {
   private final List aliases = new ArrayList();

   public SRPCommandEvolution() {
      this.aliases.add("srpevolution");
   }

   public int compareTo(ICommand arg0) {
      return 0;
   }

   public String getName() {
      return "srpevolution";
   }

   public String getUsage(ICommandSender sender) {
      return "srpevolution <text>";
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
      World world = sender.getEntityWorld();
      if (!world.isRemote) {
         if (!SRPConfigSystems.useEvolution) {
            sender.sendMessage(new TextComponentString("Evolution phases is not active"));
            return;
         }

         if (argString.length == 0) {
            sender.sendMessage(new TextComponentString("Invalid argument"));
            return;
         }

         if (argString[0].equals("toggle_evolutionloss")) {
            SRPWorldData data = SRPWorldData.get(world);
            boolean current = !data.getCanLoss();
            data.setLoss(current);
            sender.sendMessage(new TextComponentString("Current srpevolutionloss is now " + current));
            return;
         }

         if (argString[0].equals("toggle_evolutiongaining")) {
            SRPWorldData data = SRPWorldData.get(world);
            boolean current = !data.getCanGain();
            data.setGaining(current);
            sender.sendMessage(new TextComponentString("Current srpevolutiongaining is now " + current));
            return;
         }

         if (argString[0].equals("getphase")) {
            List<Entity> serverList = world.loadedEntityList;
            int count = 0;

            for(int x = 0; x < serverList.size(); ++x) {
               if (serverList.get(x) instanceof EntityParasiteBase) {
                  ++count;
               }
            }

            SRPWorldData data = SRPWorldData.get(world);
            sender.sendMessage(new TextComponentString(" ======> \n -> Current Evolution Phase: " + data.getEvolutionPhase() + " \n -> Total points: " + data.getTotalKills() + " \n -> Points required for the next phase: " + getNeededPoints((byte)(data.getEvolutionPhase() + 1)) + " \n -> Progress: " + (double)data.getTotalKills() / (double)getNeededPoints((byte)(data.getEvolutionPhase() + 1)) * 100.0D + "%  \n -> Phase cooldown: " + data.getCooldown(world) + " second(s) remaining \n -> srpevolutiongaining: " + data.getCanGain() + " (can gain points) \n -> srpevolutionloss: " + data.getCanLoss() + " (cannot lose points) \n -> Number of current parasites: " + count));
            return;
         }

         if (argString[0].equals("evolutionlock_reset")) {
            SRPSaveData data = SRPSaveData.get(world);
            data.resetLock();
            sender.sendMessage(new TextComponentString("The list has been reset"));
            return;
         }

         if (argString[0].equals("evolutionlock_getlist")) {
            SRPSaveData data = SRPSaveData.get(world);
            ArrayList<Integer> nodesX = data.getLockedList();
            String out = "Current list: ";

            for(int i = 0; i < nodesX.size(); ++i) {
               out = out + "" + nodesX.get(i) + " ";
            }

            sender.sendMessage(new TextComponentString(out));
            return;
         }

         if (argString[0].equals("addpoints")) {
            try {
               String nani = argString[1];
               int var25 = Integer.parseInt(nani);
            } catch (NumberFormatException var9) {
               sender.sendMessage(new TextComponentString("Invalid arg"));
               return;
            }

            SRPWorldData data = SRPWorldData.get(world);
            if (data.getEvolutionPhase() == -2) {
               sender.sendMessage(new TextComponentString("Current phase -2, cannot add points"));
               return;
            }

            if (!data.getCanGain()) {
               sender.sendMessage(new TextComponentString("Current dimension cannot gain points"));
               return;
            }

            String nani = argString[1];
            int option = Integer.parseInt(nani);
            if (data.getCanLoss() && option < 0) {
               sender.sendMessage(new TextComponentString("Current dimension cannot lose points"));
               return;
            }

            if (data.getCooldown(world) != 0 && option > 0) {
               sender.sendMessage(new TextComponentString("Current phase is in cooldown, cannot add points"));
               return;
            }

            data.setTotalKills(option, true, world, true);
            String po = Math.abs(option) > 1 ? " points" : " point";
            sender.sendMessage(new TextComponentString("Added " + option + po));
            return;
         }

         if (argString[0].equals("setcooldown")) {
            try {
               String nani = argString[1];
               int var23 = Integer.parseInt(nani);
            } catch (NumberFormatException var10) {
               sender.sendMessage(new TextComponentString("Invalid arg"));
               return;
            }

            String nani = argString[1];
            int option = Integer.parseInt(nani);
            if (option < 0) {
               sender.sendMessage(new TextComponentString("Cooldown value must be positive or 0"));
               return;
            }

            SRPWorldData data = SRPWorldData.get(world);
            sender.sendMessage(new TextComponentString("The cooldown was set to " + option + " second(s)"));
            data.setCooldown(option, world);
            return;
         }

         if (argString[0].equals("setphase")) {
            try {
               String nani = argString[1];
               int option = Integer.parseInt(nani);
            } catch (NumberFormatException var11) {
               sender.sendMessage(new TextComponentString("Invalid arg"));
               return;
            }

            String nani = argString[1];
            int option = Integer.parseInt(nani);
            SRPWorldData data = SRPWorldData.get(world);
            if (option >= 9) {
               sender.sendMessage(new TextComponentString("Invalid argument: phase too high"));
               return;
            }

            if (option <= -3) {
               sender.sendMessage(new TextComponentString("Invalid argument: phase too low"));
               return;
            }

            if (option == -1) {
               data.setEvolutionPhase((byte)option, true, world, true);
               data.setTotalKills(100 * option, false, world, true);
            } else if (option == -2) {
               data.setTotalKills(100 * option, false, world, true);
               data.setEvolutionPhase((byte)option, true, world, true);
            } else {
               data.setEvolutionPhase((byte)option, true, world, true);
            }

            sender.sendMessage(new TextComponentString("Changed Evolution Phase for Parasites to " + argString[1]));
            return;
         }

         sender.sendMessage(new TextComponentString("Invalid command"));
      }

   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return sender.canUseCommand(2, this.getName());
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      for(int i = 0; i < args.length; ++i) {
         ;
      }

      List<String> atm = new ArrayList();
      atm.add("evolutionlock_reset");
      atm.add("evolutionlock_getlist");
      atm.add("toggle_evolutiongaining");
      atm.add("toggle_evolutionloss");
      atm.add("addpoints");
      atm.add("setcooldown");
      atm.add("getphase");
      atm.add("setphase");
      return atm;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }

   public static int getNeededPoints(byte in) {
      switch(in) {
      case 1:
         return SRPConfigSystems.phaseKillsOne;
      case 2:
         return SRPConfigSystems.phaseKillsTwo;
      case 3:
         return SRPConfigSystems.phaseKillsThree;
      case 4:
         return SRPConfigSystems.phaseKillsFour;
      case 5:
         return SRPConfigSystems.phaseKillsFive;
      case 6:
         return SRPConfigSystems.phaseKillsSix;
      case 7:
         return SRPConfigSystems.phaseKillsSeven;
      case 8:
         return SRPConfigSystems.phaseKillsEight;
      default:
         return 0;
      }
   }
}
