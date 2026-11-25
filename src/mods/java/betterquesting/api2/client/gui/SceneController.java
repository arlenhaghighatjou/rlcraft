package betterquesting.api2.client.gui;

import javax.annotation.Nullable;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class SceneController {
   private static IScene curScene = null;

   public SceneController() {
   }

   @Nullable
   public static IScene getActiveScene() {
      return curScene;
   }

   public static void setActiveScene(@Nullable IScene scene) {
      curScene = scene;
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void onGuiOpened(GuiOpenEvent event) {
      if (event.getGui() instanceof IScene) {
         curScene = (IScene)event.getGui();
      }

   }
}
