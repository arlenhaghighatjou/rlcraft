package lumien.custommainmenu.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.gui.GuiCustomButton;
import lumien.custommainmenu.gui.GuiCustomWrappedButton;
import lumien.custommainmenu.gui.GuiFakeMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class CMMEventHandler {
   public long displayMs = -1L;
   Field guiField;
   GuiCustom actualGui;

   public CMMEventHandler() {
      try {
         this.guiField = GuiScreenEvent.class.getDeclaredField("gui");
         this.guiField.setAccessible(true);
      } catch (NoSuchFieldException var2) {
         var2.printStackTrace();
      } catch (SecurityException var3) {
         var3.printStackTrace();
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void openGui(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiMainMenu) {
         GuiCustom customMainMenu = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
         if (customMainMenu != null) {
            event.setGui(customMainMenu);
         }
      } else if (event.getGui() instanceof GuiCustom) {
         GuiCustom custom = (GuiCustom)event.getGui();
         GuiCustom target = CustomMainMenu.INSTANCE.config.getGUI(custom.guiConfig.name);
         if (target != custom) {
            event.setGui(target);
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void initGuiPostEarly(Post event) {
      if (event.getGui() instanceof GuiCustom) {
         GuiCustom custom = (GuiCustom)event.getGui();
         if (custom.guiConfig.name.equals("mainmenu")) {
            event.setButtonList(new ArrayList());
            this.actualGui = custom;

            try {
               this.guiField.set(event, new GuiFakeMain());
            } catch (IllegalArgumentException var4) {
               var4.printStackTrace();
            } catch (IllegalAccessException var5) {
               var5.printStackTrace();
            }
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void initGuiPost(Post event) {
      if (event.getGui() instanceof GuiFakeMain) {
         GuiFakeMain fake = (GuiFakeMain)event.getGui();
         HashMap<Integer, GuiButton> removedButtons = new HashMap();
         Iterator<GuiButton> iterator = event.getButtonList().iterator();

         while(iterator.hasNext()) {
            Object o = iterator.next();
            GuiButton b = (GuiButton)o;
            if (!(b instanceof GuiCustomButton)) {
               iterator.remove();
               removedButtons.put(Integer.valueOf(b.id), b);
               if (b.id == 666 && Loader.isModLoaded("OpenEye")) {
                  CustomMainMenu.INSTANCE.logger.log(Level.DEBUG, "Found OpenEye button, use a wrapped button to config this. (" + b.id + ")");
               } else if (b.id == 404 && Loader.isModLoaded("VersionChecker")) {
                  CustomMainMenu.INSTANCE.logger.log(Level.DEBUG, "Found VersionChecker button, use a wrapped button to config this. (" + b.id + ")");
               } else {
                  CustomMainMenu.INSTANCE.logger.log(Level.DEBUG, "Found unsupported button, use a wrapped button to config this. (" + b.id + ")");
               }
            }
         }

         for(GuiButton o : this.actualGui.getButtonList()) {
            if (o instanceof GuiCustomWrappedButton) {
               GuiCustomWrappedButton b = (GuiCustomWrappedButton)o;
               CustomMainMenu.INSTANCE.logger.log(Level.DEBUG, "Initiating Wrapped Button " + b.wrappedButtonID + " with " + removedButtons.get(Integer.valueOf(b.wrappedButtonID)));
               b.init((GuiButton)removedButtons.get(Integer.valueOf(b.wrappedButtonID)));
            }
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void renderScreenPost(net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post event) {
      if (this.displayMs != -1L) {
         if (System.currentTimeMillis() - this.displayMs < 5000L) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Error loading config file, see console for more information", 0.0F, 80.0F, 16711680);
         } else {
            this.displayMs = -1L;
         }
      }

   }

   @SubscribeEvent
   public void tick(ClientTickEvent event) {
      if (event.phase == Phase.END && CustomMainMenu.INSTANCE.config != null) {
         CustomMainMenu.INSTANCE.config.tick();
      }

   }
}
