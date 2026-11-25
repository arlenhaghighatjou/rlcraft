package baubles.common.event;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandBaubles extends CommandBase {
   private List<String> aliases = new ArrayList();

   public CommandBaubles() {
      this.aliases.add("baub");
      this.aliases.add("bau");
   }

   public String getName() {
      return "baubles";
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public String getUsage(ICommandSender icommandsender) {
      return "/baubles <action> [<player> [<params>]]";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public boolean isUsernameIndex(String[] astring, int i) {
      return i == 1;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length >= 2 && !args[0].equalsIgnoreCase("help")) {
         if (args.length >= 2) {
            EntityPlayerMP entityplayermp = getPlayer(server, sender, args[1]);
            if (entityplayermp == null) {
               sender.sendMessage(new TextComponentTranslation("§c" + args[1] + " not found", new Object[0]));
               return;
            }

            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(entityplayermp);
            if (args[0].equalsIgnoreCase("view")) {
               sender.sendMessage(new TextComponentTranslation("§3Showing baubles for " + entityplayermp.getName(), new Object[0]));

               for(int a = 0; a < baubles.getSlots(); ++a) {
                  ItemStack st = baubles.getStackInSlot(a);
                  if (!st.isEmpty() && st.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
                     IBauble bauble = (IBauble)st.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null);
                     BaubleType bt = bauble.getBaubleType(st);
                     sender.sendMessage(new TextComponentTranslation("§3 [Slot " + a + "] " + bt + " " + st.getDisplayName(), new Object[0]));
                  }
               }
            } else if (args[0].equalsIgnoreCase("clear")) {
               if (args.length >= 3) {
                  int slot = -1;

                  try {
                     slot = Integer.parseInt(args[2]);
                  } catch (Exception var10) {
                     ;
                  }

                  if (slot >= 0 && slot < baubles.getSlots()) {
                     baubles.setStackInSlot(slot, ItemStack.EMPTY);
                     sender.sendMessage(new TextComponentTranslation("§3Cleared baubles slot " + slot + " for " + entityplayermp.getName(), new Object[0]));
                     entityplayermp.sendMessage(new TextComponentTranslation("§4Your baubles slot " + slot + " has been cleared by admin " + sender.getName(), new Object[0]));
                  } else {
                     sender.sendMessage(new TextComponentTranslation("§cInvalid arguments", new Object[0]));
                     sender.sendMessage(new TextComponentTranslation("§cUse /baubles help to get help", new Object[0]));
                  }
               } else {
                  for(int a = 0; a < baubles.getSlots(); ++a) {
                     baubles.setStackInSlot(a, ItemStack.EMPTY);
                  }

                  sender.sendMessage(new TextComponentTranslation("§3Cleared all baubles slots for " + entityplayermp.getName(), new Object[0]));
                  entityplayermp.sendMessage(new TextComponentTranslation("§4All your baubles slots have been cleared by admin " + sender.getName(), new Object[0]));
               }
            } else {
               sender.sendMessage(new TextComponentTranslation("§cInvalid arguments", new Object[0]));
               sender.sendMessage(new TextComponentTranslation("§cUse /baubles help to get help", new Object[0]));
            }
         } else {
            sender.sendMessage(new TextComponentTranslation("§cInvalid arguments", new Object[0]));
            sender.sendMessage(new TextComponentTranslation("§cUse /baubles help to get help", new Object[0]));
         }
      } else {
         sender.sendMessage(new TextComponentTranslation("§3You can also use /baub or /bau instead of /baubles.", new Object[0]));
         sender.sendMessage(new TextComponentTranslation("§3Use this to view the baubles inventory of a player.", new Object[0]));
         sender.sendMessage(new TextComponentTranslation("  /baubles view <player>", new Object[0]));
         sender.sendMessage(new TextComponentTranslation("§3Use this to clear a players baubles inventory. Default is everything or you can give a slot number", new Object[0]));
         sender.sendMessage(new TextComponentTranslation("  /baubles clear <player> [<slot>]", new Object[0]));
      }

   }
}
