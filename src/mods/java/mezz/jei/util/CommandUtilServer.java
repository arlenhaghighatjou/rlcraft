package mezz.jei.util;

import com.google.common.base.Throwables;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import mezz.jei.JustEnoughItems;
import mezz.jei.network.packets.PacketCheatPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public final class CommandUtilServer {
   private CommandUtilServer() {
   }

   public static String[] getGiveCommandParameters(EntityPlayer sender, ItemStack itemStack, int amount) {
      String senderName = sender.getName();
      Item item = itemStack.getItem();
      ResourceLocation itemResourceLocation = item.getRegistryName();
      if (itemResourceLocation == null) {
         String stackInfo = ErrorUtil.getItemStackInfo(itemStack);
         throw new IllegalArgumentException("item.getRegistryName() returned null for: " + stackInfo);
      } else {
         List<String> commandStrings = new ArrayList();
         commandStrings.add(senderName);
         commandStrings.add(itemResourceLocation.toString());
         commandStrings.add(String.valueOf(amount));
         commandStrings.add(String.valueOf(itemStack.getMetadata()));
         NBTTagCompound tagCompound = itemStack.getTagCompound();
         if (tagCompound != null) {
            commandStrings.add(tagCompound.toString());
         }

         return (String[])commandStrings.toArray(new String[0]);
      }
   }

   public static void writeChatMessage(EntityPlayer player, String translationKey, TextFormatting color) {
      TextComponentTranslation component = new TextComponentTranslation(translationKey, new Object[0]);
      component.getStyle().setColor(color);
      player.sendMessage(component);
   }

   public static boolean hasPermission(EntityPlayerMP sender, ItemStack itemStack) {
      if (sender.isCreative()) {
         return true;
      } else {
         MinecraftServer minecraftServer = sender.mcServer;
         ICommand giveCommand = getGiveCommand(sender);
         if (giveCommand != null && giveCommand.checkPermission(minecraftServer, sender)) {
            String[] commandParameters = getGiveCommandParameters(sender, itemStack, itemStack.getCount());
            CommandEvent event = new CommandEvent(giveCommand, sender, commandParameters);
            if (MinecraftForge.EVENT_BUS.post(event)) {
               Throwable exception = event.getException();
               if (exception != null) {
                  Throwables.throwIfUnchecked(exception);
               }

               return false;
            } else {
               return true;
            }
         } else {
            return sender.canUseCommand(minecraftServer.getOpPermissionLevel(), "give");
         }
      }
   }

   public static void executeGive(EntityPlayerMP sender, ItemStack itemStack, GiveMode giveMode) {
      if (hasPermission(sender, itemStack)) {
         if (giveMode == GiveMode.INVENTORY) {
            giveToInventory(sender, itemStack);
         } else if (giveMode == GiveMode.MOUSE_PICKUP) {
            mousePickupItemStack(sender, itemStack);
         }
      } else {
         JustEnoughItems.getProxy().sendPacketToClient(new PacketCheatPermission(false), sender);
      }

   }

   public static void setHotbarSlot(EntityPlayerMP sender, ItemStack itemStack, int hotbarSlot) {
      if (hasPermission(sender, itemStack)) {
         if (!InventoryPlayer.isHotbar(hotbarSlot)) {
            Log.get().error("Tried to set slot that is not in the hotbar: {}", Integer.valueOf(hotbarSlot));
            return;
         }

         ItemStack stackInSlot = sender.inventory.getStackInSlot(hotbarSlot);
         if (ItemStack.areItemStacksEqual(stackInSlot, itemStack)) {
            return;
         }

         int count = itemStack.getCount();
         ItemStack originalStack = itemStack.copy();
         sender.inventory.setInventorySlotContents(hotbarSlot, itemStack);
         sender.world.playSound((EntityPlayer)null, sender.posX, sender.posY, sender.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((sender.getRNG().nextFloat() - sender.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
         sender.inventoryContainer.detectAndSendChanges();
         sender.setCommandStat(Type.AFFECTED_ITEMS, count);
         notifyGive(sender, originalStack, count);
      } else {
         JustEnoughItems.getProxy().sendPacketToClient(new PacketCheatPermission(false), sender);
      }

   }

   public static void mousePickupItemStack(EntityPlayer sender, ItemStack itemStack) {
      ItemStack existingStack = sender.inventory.getItemStack();
      int giveCount;
      if (ItemHandlerHelper.canItemStacksStack(existingStack, itemStack)) {
         int newCount = Math.min(existingStack.getMaxStackSize(), existingStack.getCount() + itemStack.getCount());
         giveCount = newCount - existingStack.getCount();
         existingStack.setCount(newCount);
      } else {
         sender.inventory.setItemStack(itemStack);
         giveCount = itemStack.getCount();
      }

      if (sender instanceof EntityPlayerMP) {
         EntityPlayerMP playerMP = (EntityPlayerMP)sender;
         notifyGive(playerMP, itemStack, giveCount);
         playerMP.updateHeldItem();
      }

   }

   private static void giveToInventory(EntityPlayerMP sender, ItemStack itemStack) {
      int count = itemStack.getCount();
      ItemStack originalStack = itemStack.copy();
      boolean addedToInventory = sender.inventory.addItemStackToInventory(itemStack);
      if (addedToInventory) {
         sender.world.playSound((EntityPlayer)null, sender.posX, sender.posY, sender.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((sender.getRNG().nextFloat() - sender.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
         sender.inventoryContainer.detectAndSendChanges();
      }

      if (addedToInventory && itemStack.isEmpty()) {
         sender.setCommandStat(Type.AFFECTED_ITEMS, count);
      } else {
         sender.setCommandStat(Type.AFFECTED_ITEMS, count - itemStack.getCount());
         EntityItem entityitem = sender.dropItem(itemStack, false);
         if (entityitem != null) {
            entityitem.setNoPickupDelay();
            entityitem.setOwner(sender.getName());
         }
      }

      notifyGive(sender, originalStack, count);
   }

   private static void notifyGive(EntityPlayerMP sender, ItemStack itemStack, int count) {
      if (!sender.isCreative() && count > 0) {
         ICommand giveCommand = getGiveCommand(sender);
         if (giveCommand != null) {
            ItemStack copy = ItemHandlerHelper.copyStackWithSize(itemStack, 1);
            CommandBase.notifyCommandListener(sender, giveCommand, "commands.give.success", new Object[]{copy.getTextComponent(), count, sender.getName()});
         }
      }

   }

   @Nullable
   private static ICommand getGiveCommand(EntityPlayerMP sender) {
      MinecraftServer minecraftServer = sender.mcServer;
      ICommandManager commandManager = minecraftServer.getCommandManager();
      Map<String, ICommand> commands = commandManager.getCommands();
      return (ICommand)commands.get("give");
   }
}
