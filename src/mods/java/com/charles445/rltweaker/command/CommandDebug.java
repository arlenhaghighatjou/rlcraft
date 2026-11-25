package com.charles445.rltweaker.command;

import com.charles445.rltweaker.util.CollisionUtil;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandDebug extends CommandBase {
   public CommandDebug() {
   }

   public String getName() {
      return "rldebugserver";
   }

   public String getUsage(ICommandSender sender) {
      return "/rldebugserver";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
   }

   private void addLessCollision(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length > 1) {
         try {
            double val = Double.parseDouble(args[1]);
            if (val < 2.0D) {
               val = 2.0D;
            }

            if (val > 100.0D) {
               val = 100.0D;
            }

            String name = args[0];
            this.inform("Adding " + name + " as " + val, sender);
            CollisionUtil.instance.refreshCollisionMaps();
            Map<String, Double> strMap = new HashMap();
            strMap.put(name, Double.valueOf(val));
            CollisionUtil.instance.addToStringReference(strMap);
         } catch (NumberFormatException var8) {
            this.inform("Bad number", sender);
         }
      }

   }

   private void setMER(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length > 0) {
         try {
            double val = Double.parseDouble(args[0]);
            if (val < 2.0D) {
               val = 2.0D;
            }

            if (val > 100.0D) {
               val = 100.0D;
            }

            this.inform("Setting MAX_ENTITY_RADIUS to " + val, sender);
            sender.getEntityWorld();
            World.MAX_ENTITY_RADIUS = val;
         } catch (NumberFormatException var6) {
            ;
         }
      }

   }

   public void inform(String s, ICommandSender sender) {
      sender.sendMessage(new TextComponentString(s));
   }
}
