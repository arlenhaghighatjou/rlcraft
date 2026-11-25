package lumien.custommainmenu.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lumien.custommainmenu.lib.texts.TextURL;

public class LoadStringURL extends Thread {
   TextURL text;

   public LoadStringURL(TextURL text) {
      this.text = text;
      this.setDaemon(true);
   }

   public void run() {
      BufferedReader in = null;

      try {
         in = new BufferedReader(new InputStreamReader(this.text.getURL().openStream()));
      } catch (IOException var10) {
         var10.printStackTrace();
      }

      StringBuilder builder = new StringBuilder();
      String inputLine = null;

      while(true) {
         if (inputLine != null) {
            builder.append(inputLine);
         }

         String newInput = null;

         try {
            newInput = in.readLine();
         } catch (IOException var9) {
            var9.printStackTrace();
         }

         if (inputLine != null) {
            builder.append("\n");
         }

         inputLine = newInput;
         if (newInput == null) {
            break;
         }
      }

      try {
         in.close();
      } catch (IOException var8) {
         var8.printStackTrace();
      }

      synchronized(this.text.string) {
         this.text.string = builder.toString();
      }
   }
}
