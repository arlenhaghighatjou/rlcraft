package vazkii.arl.util;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import vazkii.arl.interf.IDropInItem;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.network.message.MessageDropIn;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "autoreglib"
)
public final class DropInHandler {
   public DropInHandler() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(IDropInItem.class, DropInHandler.CapabilityFactory.INSTANCE, DropInHandler.CapabilityFactory.INSTANCE);
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void onDrawScreen(Post event) {
      Minecraft mc = Minecraft.getMinecraft();
      GuiScreen gui = mc.currentScreen;
      if (gui instanceof GuiContainer) {
         GuiContainer container = (GuiContainer)gui;
         ItemStack held = mc.player.inventory.getItemStack();
         if (!held.isEmpty()) {
            Slot under = container.getSlotUnderMouse();

            for(Slot s : container.inventorySlots.inventorySlots) {
               ItemStack stack = s.getStack();
               IDropInItem dropin = getDropInHandler(stack);
               if (dropin != null) {
                  if (s == under) {
                     int x = event.getMouseX();
                     int y = event.getMouseY();
                     RenderHelper.renderTooltip(x, y, dropin.getDropInTooltip(stack));
                  } else {
                     int x = container.getGuiLeft() + s.xPos;
                     int y = container.getGuiTop() + s.yPos;
                     GlStateManager.disableDepth();
                     mc.fontRenderer.drawStringWithShadow("+", (float)(x + 10), (float)(y + 8), 16776960);
                     GlStateManager.enableDepth();
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void onRightClick(Pre event) {
      Minecraft mc = Minecraft.getMinecraft();
      GuiScreen gui = mc.currentScreen;
      if (gui instanceof GuiContainer && Mouse.getEventButton() == 1) {
         GuiContainer container = (GuiContainer)gui;
         Slot under = container.getSlotUnderMouse();
         ItemStack held = mc.player.inventory.getItemStack();
         if (under != null && !held.isEmpty()) {
            ItemStack stack = under.getStack();
            IDropInItem dropin = getDropInHandler(stack);
            if (dropin != null) {
               NetworkHandler.INSTANCE.sendToServer(new MessageDropIn(under.getSlotIndex(), held));
               event.setCanceled(true);
            }
         }
      }

   }

   public static void executeDropIn(EntityPlayer player, int slot, ItemStack stack) {
      Container container = player.openContainer;
      Slot slotObj = (Slot)container.inventorySlots.get(slot);
      ItemStack target = slotObj.getStack();
      IDropInItem dropin = getDropInHandler(target);
      if (dropin != null && dropin.canDropItemIn(player, target, stack)) {
         ItemStack held = player.inventory.getItemStack();
         if (player.isCreative() && !stack.isEmpty()) {
            held = stack;
         }

         ItemStack result = dropin.dropItemIn(player, target, held);
         slotObj.putStack(result);
         player.inventory.setItemStack(ItemStack.EMPTY);
         if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).isChangingQuantityOnly = false;
            ((EntityPlayerMP)player).updateHeldItem();
         }
      }

   }

   public static IDropInItem getDropInHandler(ItemStack stack) {
      if (stack.hasCapability(IDropInItem.DROP_IN_CAPABILITY, (EnumFacing)null)) {
         IDropInItem item = (IDropInItem)stack.getCapability(IDropInItem.DROP_IN_CAPABILITY, (EnumFacing)null);
         if (item != null) {
            return item;
         }
      }

      return stack.getItem() instanceof IDropInItem ? (IDropInItem)stack.getItem() : null;
   }

   private static class CapabilityFactory implements IStorage<IDropInItem>, Callable<IDropInItem> {
      private static DropInHandler.CapabilityFactory INSTANCE = new DropInHandler.CapabilityFactory();

      private CapabilityFactory() {
      }

      public NBTBase writeNBT(Capability<IDropInItem> capability, IDropInItem instance, EnumFacing side) {
         return null;
      }

      public void readNBT(Capability<IDropInItem> capability, IDropInItem instance, EnumFacing side, NBTBase nbt) {
      }

      public IDropInItem call() {
         return new DropInHandler.CapabilityFactory.DefaultImpl();
      }

      private static class DefaultImpl implements IDropInItem {
         private DefaultImpl() {
         }

         public boolean canDropItemIn(EntityPlayer player, ItemStack stack, ItemStack incoming) {
            return false;
         }

         public ItemStack dropItemIn(EntityPlayer player, ItemStack stack, ItemStack incoming) {
            return incoming;
         }
      }
   }
}
