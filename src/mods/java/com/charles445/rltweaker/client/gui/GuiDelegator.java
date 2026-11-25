package com.charles445.rltweaker.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiDelegator {
   public static Map<String, List<IGuiDelegate>> delegates = new ConcurrentHashMap();
   private Minecraft mc = Minecraft.getMinecraft();

   public GuiDelegator() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   public static void addDelegate(String guiClazz, IGuiDelegate delegate) {
      List<IGuiDelegate> delegateList = (List)delegates.get(guiClazz);
      if (delegateList == null) {
         delegateList = new ArrayList();
         delegates.put(guiClazz, delegateList);
      }

      delegateList.add(delegate);
   }

   @SubscribeEvent
   public void onMouseInputPre(Pre event) {
      try {
         this.pollMousePre(event);
      } catch (IOException var3) {
         ;
      }

   }

   public void pollMousePre(Pre event) throws IOException {
      GuiScreen guiScreen = event.getGui();
      List<IGuiDelegate> delegateList = (List)delegates.get(guiScreen.getClass().getName());
      if (delegateList != null) {
         int width = guiScreen.width;
         int height = guiScreen.height;
         int x = Mouse.getEventX() * width / this.mc.displayWidth;
         int y = height - Mouse.getEventY() * height / this.mc.displayHeight - 1;
         int button = Mouse.getEventButton();
         boolean state = Mouse.getEventButtonState();

         for(IGuiDelegate delegate : delegateList) {
            if (delegate.pollMousePre(guiScreen, width, height, x, y, button, state)) {
               event.setCanceled(true);
               return;
            }
         }

      }
   }

   private void message(String s) {
      if (this.mc.player != null) {
         this.mc.player.sendMessage(new TextComponentString(s));
      }

   }
}
