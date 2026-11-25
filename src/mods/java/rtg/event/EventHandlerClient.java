package rtg.event;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import rtg.RTGConfig;
import rtg.client.WorldTypeMessageGUI;
import rtg.world.WorldTypeRTG;

@EventBusSubscriber(
   modid = "rtg",
   value = {Side.CLIENT}
)
public final class EventHandlerClient {
   private EventHandlerClient() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onGuiCreateWorld(GuiOpenEvent event) {
      GuiScreen gui = event.getGui();
      if (gui instanceof GuiCreateWorld) {
         String seed = ((GuiCreateWorld)gui).worldSeed;
         if (seed.isEmpty()) {
            if (RTGConfig.rtgWorldTypeByDefault()) {
               ((GuiCreateWorld)gui).selectedIndex = WorldTypeRTG.getInstance().getId();
            } else if (RTGConfig.worldTypeNotification()) {
               RTGConfig.toggleWorldTypeNotification();
               event.setGui(new WorldTypeMessageGUI(gui));
            }
         }
      }

   }
}
