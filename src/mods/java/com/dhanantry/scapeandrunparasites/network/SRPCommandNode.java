package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class SRPCommandNode implements ICommand {
   private final List aliases = new ArrayList();

   public SRPCommandNode() {
      this.aliases.add("srpnodes");
   }

   public int compareTo(ICommand arg0) {
      return 0;
   }

   public String getName() {
      return "srpnodes";
   }

   public String getUsage(ICommandSender sender) {
      return "srpnodes <text>";
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
      World world = sender.getEntityWorld();
      if (!world.isRemote) {
         if (!SRPConfigWorld.nodesActivated) {
            sender.sendMessage(new TextComponentString("Nodes are not activated"));
            return;
         }

         if (!SRPConfigWorld.biomeRegster) {
            sender.sendMessage(new TextComponentString("Biome is not activated"));
            return;
         }

         if (argString.length == 0) {
            sender.sendMessage(new TextComponentString("Invalid argument"));
            return;
         }

         if (argString[0].equals("viewall")) {
            SRPWorldData data = SRPWorldData.get(world);
            ArrayList<Integer> nodesX = data.getNodes("x");
            ArrayList<Integer> nodesY = data.getNodes("y");
            ArrayList<Integer> nodesZ = data.getNodes("z");
            ArrayList<Integer> nodesA = data.getNodes("a");
            String out = "Current nodes in the world (x, y, z, age): ";

            for(int i = 0; i < nodesX.size(); ++i) {
               out = out + "[" + nodesX.get(i) + ", " + nodesY.get(i) + ", " + nodesZ.get(i) + ", " + nodesA.get(i) + "] ";
            }

            sender.sendMessage(new TextComponentString(out));
            return;
         }

         if (argString[0].equals("clearworld")) {
            SRPWorldData data = SRPWorldData.get(world);
            data.clearNodeList();
            sender.sendMessage(new TextComponentString("There are no longer nodes in this world"));
            return;
         }

         if (argString.length != 4) {
            sender.sendMessage(new TextComponentString("Invalid argument"));
            return;
         }

         int x;
         int y;
         int z;
         try {
            x = Integer.parseInt(argString[1]);
            y = Integer.parseInt(argString[2]);
            z = Integer.parseInt(argString[3]);
         } catch (NumberFormatException var12) {
            sender.sendMessage(new TextComponentString("Invalid argument"));
            return;
         }

         if (argString[0].equals("setnode")) {
            switch(ParasiteEventWorld.placeHeartInWorld(world, new BlockPos(x, y, z))) {
            case 1:
               sender.sendMessage(new TextComponentString("Node placed at " + x + " " + y + " " + z));
               break;
            case 2:
               sender.sendMessage(new TextComponentString("Nodes cannot be placed in this dimension"));
               break;
            case 3:
               sender.sendMessage(new TextComponentString("Nodes are not activated"));
               break;
            case 4:
               sender.sendMessage(new TextComponentString("Biome is not activated"));
               break;
            case 5:
               sender.sendMessage(new TextComponentString("Node is too close to spawn point"));
               break;
            case 6:
               sender.sendMessage(new TextComponentString("Evolution Phase is not high enough to place a Node "));
               break;
            case 7:
               sender.sendMessage(new TextComponentString("Unable to find a place for the Node"));
               break;
            case 8:
               sender.sendMessage(new TextComponentString("Node is too close to another Node"));
               break;
            case 9:
               sender.sendMessage(new TextComponentString("Maximum number of Nodes reached"));
               break;
            default:
               sender.sendMessage(new TextComponentString("Unknown - Nodes "));
            }

            return;
         }

         if (argString[0].equals("removenode")) {
            if (ParasiteEventWorld.removeHeartInWorld(world, new BlockPos(x, y, z))) {
               sender.sendMessage(new TextComponentString("Node removed at " + x + " " + y + " " + z));
            } else {
               sender.sendMessage(new TextComponentString("Node cannot be removed at " + x + " " + y + " " + z));
            }

            return;
         }
      }

   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return sender.canUseCommand(2, this.getName());
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      List<String> atm = new ArrayList();
      if (args.length == 1) {
         atm.add("viewall");
         atm.add("clearworld");
         atm.add("setnode");
         atm.add("removenode");
      }

      if ((args[0].equals("setnode") || args[0].equals("removenode")) && args.length == 2) {
         atm.add("" + sender.getPosition().getX());
      }

      if (args.length == 3) {
         atm.add("" + sender.getPosition().getY());
      }

      if (args.length == 4) {
         atm.add("" + sender.getPosition().getZ());
      }

      return atm;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }
}
