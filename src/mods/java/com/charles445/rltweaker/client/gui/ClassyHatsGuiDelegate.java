package com.charles445.rltweaker.client.gui;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ClassyHatsGuiDelegate implements IGuiDelegate {
   public final Class c_GuiHatBag = Class.forName("wiresegal.classyhats.client.gui.GuiHatBag");
   public final Field f_GuiHatBag_slotPos;
   public final Method m_GuiHatBag_onGuiClosed;
   public final Class c_ContainerHatBag;
   public final Field f_ContainerHatBag_slotPos;
   public final Class c_PacketHandler;
   public final Field f_PacketHandler_NETWORK;
   public final SimpleNetworkWrapper o_PacketHandler_NETWORK;
   public final Class c_PacketHatGuiOpen;
   public final Constructor con_PacketHatGuiOpen;
   private int savedSlot = -1;
   private int currentSlot = 0;

   public ClassyHatsGuiDelegate() throws Exception {
      this.f_GuiHatBag_slotPos = ReflectUtil.findField(this.c_GuiHatBag, "slotPos");
      this.m_GuiHatBag_onGuiClosed = ReflectUtil.findMethodAny(this.c_GuiHatBag, "onGuiClosed", "onGuiClosed");
      this.c_ContainerHatBag = Class.forName("wiresegal.classyhats.container.ContainerHatBag");
      this.f_ContainerHatBag_slotPos = ReflectUtil.findField(this.c_ContainerHatBag, "slotPos");
      this.c_PacketHandler = Class.forName("com.teamwizardry.librarianlib.features.network.PacketHandler");
      this.f_PacketHandler_NETWORK = ReflectUtil.findField(this.c_PacketHandler, "NETWORK");
      this.o_PacketHandler_NETWORK = (SimpleNetworkWrapper)this.f_PacketHandler_NETWORK.get((Object)null);
      this.c_PacketHatGuiOpen = Class.forName("wiresegal.classyhats.network.PacketHatGuiOpen");
      this.con_PacketHatGuiOpen = this.c_PacketHatGuiOpen.getDeclaredConstructor(Integer.TYPE);
   }

   public boolean pollMousePre(GuiScreen guiScreen, int width, int height, int x, int y, int button, boolean state) {
      try {
         this.currentSlot = this.getGuiSlotPos(guiScreen);
         if (this.savedSlot == -1) {
            this.savedSlot = this.currentSlot;
         }

         if (this.currentSlot != this.savedSlot) {
            this.savedSlot = this.currentSlot;
            Container container = ((GuiContainer)guiScreen).inventorySlots;
            this.getContainerSlotpos(container);
            this.m_GuiHatBag_onGuiClosed.invoke(guiScreen);
            this.savedSlot = -1;
            this.o_PacketHandler_NETWORK.sendToServer(this.newPacketOpenBag());
         }
      } catch (Exception var10) {
         ErrorUtil.logSilent("Classy Hats Gui Delegate Invocation");
         var10.printStackTrace();
      }

      return false;
   }

   public int getGuiSlotPos(Object guiHatBag) throws IllegalArgumentException, IllegalAccessException {
      return this.f_GuiHatBag_slotPos.getInt(guiHatBag);
   }

   public int getContainerSlotpos(Object containerHatBag) throws IllegalArgumentException, IllegalAccessException {
      return this.f_ContainerHatBag_slotPos.getInt(containerHatBag);
   }

   public IMessage newPacketOpenBag() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return (IMessage)this.con_PacketHatGuiOpen.newInstance(Integer.valueOf(1));
   }
}
