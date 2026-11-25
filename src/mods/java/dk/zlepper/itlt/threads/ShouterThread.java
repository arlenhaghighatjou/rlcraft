package dk.zlepper.itlt.threads;

import dk.zlepper.itlt.Itlt;
import dk.zlepper.itlt.proxies.ClientProxy;
import java.awt.Component;
import javax.swing.JOptionPane;

public final class ShouterThread extends Thread {
   private String message;

   public ShouterThread(String message) {
      this.message = message;
   }

   public void run() {
      if (Itlt.proxy instanceof ClientProxy) {
         System.setProperty("java.awt.headless", "false");
         JOptionPane.showMessageDialog((Component)null, this.message, "Java version issue", 2);
      } else {
         Itlt.logger.warn(this.message);
      }

   }
}
