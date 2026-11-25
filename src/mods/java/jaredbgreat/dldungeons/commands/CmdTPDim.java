package jaredbgreat.dldungeons.commands;

import com.google.common.collect.Lists;
import jaredbgreat.dldungeons.tputils.Teleportation;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CmdTPDim extends CommandBase {
   private static final String NAME = "tptodim";
   private final List<String> aliases = Lists.newArrayList(new String[]{"dldungeonsjbg", "tptodim", "tpdim", "todim"});

   public CmdTPDim() {
   }

   public String getName() {
      return "tptodim";
   }

   public String getUsage(ICommandSender sender) {
      return "/tptodim [dimension ID] (optional: [x] [y] [z])";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (sender instanceof EntityPlayer) {
         if (args.length < 1) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Not enougn arguments; use \"tptodim [dimension ID]\"."));
         } else {
            int dimID;
            try {
               dimID = Integer.parseInt(args[0]);
            } catch (NumberFormatException var10) {
               sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid Dimension ID (must be an integer)"));
               return;
            }

            if (args.length < 4) {
               BlockPos pos = sender.getPosition();
               Teleportation.tpToBlockPos((EntityPlayer)sender, dimID, pos.getX(), pos.getY(), pos.getZ());
            } else {
               int y;
               int z;
               int x;
               try {
                  x = Integer.parseInt(args[1]);
                  y = Integer.parseInt(args[2]);
                  z = Integer.parseInt(args[3]);
               } catch (NumberFormatException var9) {
                  sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid coordinates (must by integers)"));
                  return;
               }

               Teleportation.tpToBlockPos((EntityPlayer)sender, dimID, x, y, z);
            }

         }
      }
   }
}
