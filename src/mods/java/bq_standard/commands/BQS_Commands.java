package bq_standard.commands;

import betterquesting.api.utils.JsonHelper;
import betterquesting.api.utils.NBTConverter;
import bq_standard.network.handlers.NetLootSync;
import bq_standard.rewards.loot.LootRegistry;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class BQS_Commands extends CommandBase {
   public BQS_Commands() {
   }

   public String getName() {
      return "bqs_loot";
   }

   public String getUsage(ICommandSender sender) {
      return "/bqs_loot default [save|load], /bqs_loot delete [all|<loot_id>]";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length != 2) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         if (args[0].equalsIgnoreCase("default")) {
            if (args[1].equalsIgnoreCase("save")) {
               NBTTagCompound jsonQ = new NBTTagCompound();
               LootRegistry.INSTANCE.writeToNBT(jsonQ, (List)null);
               JsonHelper.WriteToFile(new File(server.getFile("config/betterquesting/"), "DefaultLoot.json"), NBTConverter.NBTtoJSON_Compound(jsonQ, new JsonObject(), true));
               sender.sendMessage(new TextComponentString("Loot database set as global default"));
            } else {
               if (!args[1].equalsIgnoreCase("load")) {
                  throw new WrongUsageException(this.getUsage(sender), new Object[0]);
               }

               File f1 = new File("config/betterquesting/DefaultLoot.json");
               if (f1.exists()) {
                  NBTTagCompound j1 = NBTConverter.JSONtoNBT_Object(JsonHelper.ReadFromFile(f1), new NBTTagCompound(), true);
                  LootRegistry.INSTANCE.readFromNBT(j1, false);
                  NetLootSync.sendSync((EntityPlayerMP)null);
                  sender.sendMessage(new TextComponentString("Reloaded default loot database"));
               } else {
                  sender.sendMessage(new TextComponentString(TextFormatting.RED + "No default loot currently set"));
               }
            }
         } else {
            if (!args[0].equalsIgnoreCase("delete")) {
               throw new WrongUsageException(this.getUsage(sender), new Object[0]);
            }

            if (args[1].equalsIgnoreCase("all")) {
               LootRegistry.INSTANCE.reset();
               NetLootSync.sendSync((EntityPlayerMP)null);
               sender.sendMessage(new TextComponentString("Deleted all loot groups"));
            } else {
               try {
                  int idx = Integer.parseInt(args[1]);
                  if (LootRegistry.INSTANCE.removeID(idx)) {
                     NetLootSync.sendSync((EntityPlayerMP)null);
                     sender.sendMessage(new TextComponentString("Deleted loot group with ID " + idx));
                  } else {
                     sender.sendMessage(new TextComponentString("Unable to find loot group with ID " + idx));
                  }
               } catch (Exception var6) {
                  throw new WrongUsageException(this.getUsage(sender), new Object[0]);
               }
            }
         }

      }
   }
}
