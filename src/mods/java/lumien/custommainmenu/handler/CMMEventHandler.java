package lumien.custommainmenu.handler;

import java.util.HashMap;
import java.util.Map;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.gui.GuiCustomWrappedButton;
import lumien.custommainmenu.gui.GuiFakeMain;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class CMMEventHandler {
   public long displayMs = -1L;

   public void register(EventBus bus) {
      bus.register(GuiOpenEvent.class, EventPriority.LOWEST, this::openGui);
      bus.register(InitGuiEvent.Post.class, EventPriority.LOWEST, this::initGuiPost);
      bus.register(DrawScreenEvent.Post.class, EventPriority.HIGHEST, this::renderScreenPost);
      bus.register(ClientTickEvent.class, this::tick);
   }

   public void openGui(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiMainMenu) {
         GuiCustom main = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
         if (main != null) {
            event.setGui(main);
         }
      } else if (event.getGui() instanceof GuiCustom) {
         GuiCustom custom = (GuiCustom)event.getGui();
         GuiCustom target = CustomMainMenu.INSTANCE.config.getGUI(custom.guiConfig.name);
         if (target != null && target != custom) {
            event.setGui(target);
         }
      }
   }

   public void initGuiPost(InitGuiEvent.Post event) {
      if (!(event.getGui() instanceof GuiCustom)) {
         return;
      }
      GuiCustom custom = (GuiCustom)event.getGui();
      if (!"mainmenu".equals(custom.guiConfig.name)) {
         return;
      }
      GuiFakeMain fake = new GuiFakeMain();
      fake.setWorldAndResolution(Minecraft.getMinecraft(), custom.width, custom.height);
      Map<Integer, GuiButton> wrappedButtons = new HashMap<>();
      for (GuiButton button : fake.getButtonList()) {
         wrappedButtons.put(button.id, button);
      }
      for (GuiButton button : custom.getButtonList()) {
         if (button instanceof GuiCustomWrappedButton) {
            GuiCustomWrappedButton wrapped = (GuiCustomWrappedButton)button;
            wrapped.init(wrappedButtons.get(wrapped.wrappedButtonID));
         }
      }
   }

   public void renderScreenPost(DrawScreenEvent.Post event) {
      if (this.displayMs >= 0L) {
         if (EagRuntime.steadyTimeMillis() - this.displayMs < 5000L) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Error loading config file, see console for more information", 0.0F, 80.0F, 16711680);
         } else {
            this.displayMs = -1L;
         }
      }
   }

   public void tick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         CustomMainMenu.INSTANCE.config.tick();
      }
   }
}
