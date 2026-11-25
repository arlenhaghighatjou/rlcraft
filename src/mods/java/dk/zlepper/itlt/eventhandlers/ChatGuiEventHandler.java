package dk.zlepper.itlt.eventhandlers;

import java.lang.reflect.Field;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatGuiEventHandler {
   private GuiTextField inputField;
   private KeyPressedInChatGuiEventHandler handler = null;

   public ChatGuiEventHandler() {
      System.out.println("Registered event handler");
      this.handler = new KeyPressedInChatGuiEventHandler(this);
   }

   @SubscribeEvent
   public void onGuiInitEvent(Post event) {
      if (event.getGui() instanceof GuiChat) {
         GuiChat chat = (GuiChat)event.getGui();

         try {
            Class<?> chatClass = chat.getClass();
            Field infi = chatClass.getDeclaredField("inputField");
            infi.setAccessible(true);
            GuiTextField newInputField = (GuiTextField)infi.get(chat);
            if (this.inputField != null) {
               newInputField.setText(this.inputField.getText());
            }

            this.inputField = newInputField;
            MinecraftForge.EVENT_BUS.register(this.handler);
         } catch (NoSuchFieldException var6) {
            var6.printStackTrace();
         } catch (IllegalAccessException var7) {
            var7.printStackTrace();
         }
      }

   }

   @SubscribeEvent
   public void OnGuiOpenEvent(GuiOpenEvent e) {
      if (e.getGui() == null && this.handler != null) {
         try {
            MinecraftForge.EVENT_BUS.unregister(this.handler);
         } catch (NullPointerException var3) {
            ;
         }
      }

   }

   public void clearInputField() {
      this.inputField = null;
   }
}
