package dk.zlepper.itlt.proxies;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class ClientProxy extends CommonProxy {
   public static boolean changed = false;

   public ClientProxy() {
   }

   public void changeScreen() {
      if (!changed) {
         try {
            Display.setLocation(-7, 0);
            Display.setDisplayMode(Display.getDesktopDisplayMode());
         } catch (LWJGLException var2) {
            ;
         } catch (IllegalStateException var3) {
            ;
         }

         changed = true;
      }

   }

   public void setWindowDisplayTitle(String title) {
      Display.setTitle(title);
   }
}
