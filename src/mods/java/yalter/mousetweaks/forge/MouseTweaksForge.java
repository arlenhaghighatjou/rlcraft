package yalter.mousetweaks.forge;

import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import yalter.mousetweaks.Constants;
import yalter.mousetweaks.Main;
import yalter.mousetweaks.OnTickMethod;

@Mod(
   modid = "mousetweaks",
   version = "2.10.1",
   updateJSON = "http://play.sourceruns.org/yalter/MouseTweaks/update.json",
   useMetadata = true,
   clientSideOnly = true,
   guiFactory = "yalter.mousetweaks.forge.ConfigGuiFactory"
)
public class MouseTweaksForge {
   public MouseTweaksForge() {
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      Main.initialize(Constants.EntryPoint.FORGE);
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onRenderTick(RenderTickEvent event) {
      if (Main.onTickMethod == OnTickMethod.FORGE && event.phase == Phase.START) {
         Main.onUpdateInGame();
      }

   }

   @SubscribeEvent
   public void onGuiMouseInput(Post event) {
      if (Main.onTickMethod == OnTickMethod.FORGE && event.getGui() instanceof bmg) {
         Main.onMouseInput();
      }

   }
}
