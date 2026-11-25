package lumien.custommainmenu.lib.texts;

import java.net.MalformedURLException;
import java.net.URL;
import lumien.custommainmenu.handler.LoadStringURL;
import lumien.custommainmenu.lib.StringReplacer;

public class TextURL implements IText {
   URL url;
   public String string;
   int refreshInterval;
   int refreshCounter;
   LoadStringURL loadThread;

   public TextURL(String url, int refreshInterval) {
      try {
         this.url = new URL(StringReplacer.replacePlaceholders(url));
      } catch (MalformedURLException var4) {
         var4.printStackTrace();
      }

      this.string = "";
      (this.loadThread = new LoadStringURL(this)).start();
      this.refreshInterval = refreshInterval;
      this.refreshCounter = 0;
   }

   public void tick() {
      if (this.refreshInterval != -1 && this.refreshInterval >= 60) {
         ++this.refreshCounter;
         if (this.refreshCounter >= this.refreshInterval && !this.loadThread.isAlive()) {
            (this.loadThread = new LoadStringURL(this)).start();
            this.refreshCounter = 0;
         }
      }

   }

   public String get() {
      synchronized(this.string) {
         return this.string;
      }
   }

   public URL getURL() {
      return this.url;
   }
}
