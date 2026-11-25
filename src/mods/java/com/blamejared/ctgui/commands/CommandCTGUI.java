package com.blamejared.ctgui.commands;

import com.blamejared.ctgui.api.GuiRegistry;
import com.blamejared.ctgui.api.events.CTGUIEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

public class CommandCTGUI extends CommandBase {
   public CommandCTGUI() {
   }

   public String getName() {
      return "CTGUI";
   }

   public List<String> getAliases() {
      return Collections.singletonList(this.getName().toLowerCase());
   }

   public String getUsage(ICommandSender sender) {
      return "CTGUI <id>";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return args.length != 1 ? super.getTabCompletions(server, sender, args, targetPos) : getListOfStringsMatchingLastWord(args, new ArrayList(GuiRegistry.getGuiMap().keySet()));
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length != 0 && GuiRegistry.getID(args[0]) != Integer.MIN_VALUE) {
         if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)sender;
            MinecraftForge.EVENT_BUS.post(new CTGUIEvent(player, player.world, args[0]));
         }

      } else {
         sender.sendMessage(new TextComponentString("Invalid ID!"));
      }
   }
}
