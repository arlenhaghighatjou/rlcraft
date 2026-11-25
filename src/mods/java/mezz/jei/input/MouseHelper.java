package mezz.jei.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.Mouse;

@EventBusSubscriber(
   modid = "jei",
   value = {Side.CLIENT}
)
public final class MouseHelper {
   private static MouseHelper.DisplayInfo INFO = new MouseHelper.DisplayInfo();

   private MouseHelper() {
   }

   public static int getX() {
      return INFO.getX();
   }

   public static int getY() {
      return INFO.getY();
   }

   @SubscribeEvent
   public static void onGuiInit(Post event) {
      INFO = new MouseHelper.DisplayInfo();
   }

   private static class DisplayInfo {
      private final ScaledResolution scaledresolution;
      private final int displayWidth;
      private final int displayHeight;

      public DisplayInfo() {
         Minecraft minecraft = Minecraft.getMinecraft();
         this.displayWidth = minecraft.displayWidth;
         this.displayHeight = minecraft.displayHeight;
         this.scaledresolution = new ScaledResolution(minecraft);
      }

      public int getX() {
         int i = this.scaledresolution.getScaledWidth();
         return Mouse.getX() * i / this.displayWidth;
      }

      public int getY() {
         int j = this.scaledresolution.getScaledHeight();
         return j - Mouse.getY() * j / this.displayHeight - 1;
      }
   }
}
