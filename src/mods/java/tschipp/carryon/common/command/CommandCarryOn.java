package tschipp.carryon.common.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import tschipp.carryon.CarryOn;
import tschipp.carryon.common.handler.CustomPickupOverrideHandler;
import tschipp.carryon.common.handler.ModelOverridesHandler;
import tschipp.carryon.common.handler.RegistrationHandler;
import tschipp.carryon.common.item.ItemEntity;
import tschipp.carryon.common.item.ItemTile;
import tschipp.carryon.network.client.CarrySlotPacket;

public class CommandCarryOn extends CommandBase implements ICommand {
   private final List names = new ArrayList();

   public CommandCarryOn() {
      this.names.add("carryon");
   }

   public int compareTo(ICommand o) {
      return this.getName().compareTo(o.getName());
   }

   public String getName() {
      return "carryon";
   }

   public String getUsage(ICommandSender sender) {
      return "/carryon <mode>";
   }

   public List<String> getAliases() {
      return this.names;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         if (args[0].toLowerCase().equals("clear")) {
            if (sender instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)sender;
               int cleared = 0;
               cleared = cleared + player.inventory.clearMatchingItems(RegistrationHandler.itemTile, 0, 64, (NBTTagCompound)null);
               cleared = cleared + player.inventory.clearMatchingItems(RegistrationHandler.itemEntity, 0, 64, (NBTTagCompound)null);
               CarryOn.network.sendTo(new CarrySlotPacket(9, player.getEntityId()), (EntityPlayerMP)player);
               if (cleared != 1) {
                  player.sendMessage(new TextComponentString("Cleared " + cleared + " Items!"));
               } else {
                  player.sendMessage(new TextComponentString("Cleared " + cleared + " Item!"));
               }
            }
         } else {
            if (!args[0].toLowerCase().equals("debug")) {
               throw new WrongUsageException(this.getUsage(sender), new Object[0]);
            }

            if (sender instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)sender;
               ItemStack main = player.getHeldItemMainhand();
               if (!main.isEmpty() && main.getItem() == RegistrationHandler.itemTile) {
                  player.sendMessage(new TextComponentString("Block: " + ItemTile.getBlock(main)));
                  player.sendMessage(new TextComponentString("BlockState: " + ItemTile.getBlockState(main)));
                  player.sendMessage(new TextComponentString("Meta: " + ItemTile.getMeta(main)));
                  player.sendMessage(new TextComponentString("ItemStack: " + ItemTile.getItemStack(main)));
                  if (ModelOverridesHandler.hasCustomOverrideModel(ItemTile.getBlockState(main), ItemTile.getTileData(main))) {
                     player.sendMessage(new TextComponentString("Override Model: " + ModelOverridesHandler.getOverrideObject(ItemTile.getBlockState(main), ItemTile.getTileData(main))));
                  }

                  if (CustomPickupOverrideHandler.hasSpecialPickupConditions(ItemTile.getBlockState(main))) {
                     player.sendMessage(new TextComponentString("Custom Pickup Condition: " + CustomPickupOverrideHandler.getPickupCondition(ItemTile.getBlockState(main))));
                  }

                  CarryOn.LOGGER.info("Block: " + ItemTile.getBlock(main));
                  CarryOn.LOGGER.info("BlockState: " + ItemTile.getBlockState(main));
                  CarryOn.LOGGER.info("Meta: " + ItemTile.getMeta(main));
                  CarryOn.LOGGER.info("ItemStack: " + ItemTile.getItemStack(main));
                  if (ModelOverridesHandler.hasCustomOverrideModel(ItemTile.getBlockState(main), ItemTile.getTileData(main))) {
                     CarryOn.LOGGER.info("Override Model: " + ModelOverridesHandler.getOverrideObject(ItemTile.getBlockState(main), ItemTile.getTileData(main)));
                  }

                  if (CustomPickupOverrideHandler.hasSpecialPickupConditions(ItemTile.getBlockState(main))) {
                     CarryOn.LOGGER.info("Custom Pickup Condition: " + CustomPickupOverrideHandler.getPickupCondition(ItemTile.getBlockState(main)));
                  }
               } else if (!main.isEmpty() && main.getItem() == RegistrationHandler.itemEntity) {
                  player.sendMessage(new TextComponentString("Entity: " + ItemEntity.getEntity(main, server.getEntityWorld())));
                  player.sendMessage(new TextComponentString("Entity Name: " + ItemEntity.getEntityName(main)));
                  if (CustomPickupOverrideHandler.hasSpecialPickupConditions(ItemEntity.getEntity(main, server.getEntityWorld()))) {
                     player.sendMessage(new TextComponentString("Custom Pickup Condition: " + CustomPickupOverrideHandler.getPickupCondition(ItemEntity.getEntity(main, server.getEntityWorld()))));
                  }

                  CarryOn.LOGGER.info("Entity: " + ItemEntity.getEntity(main, server.getEntityWorld()));
                  CarryOn.LOGGER.info("Entity Name: " + ItemEntity.getEntityName(main));
                  if (CustomPickupOverrideHandler.hasSpecialPickupConditions(ItemEntity.getEntity(main, server.getEntityWorld()))) {
                     CarryOn.LOGGER.info("Custom Pickup Condition: " + CustomPickupOverrideHandler.getPickupCondition(ItemEntity.getEntity(main, server.getEntityWorld())));
                  }
               }
            }
         }

      }
   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return true;
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length > 0) {
         return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"debug", "clear"}) : Collections.emptyList();
      } else {
         return Collections.emptyList();
      }
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }
}
