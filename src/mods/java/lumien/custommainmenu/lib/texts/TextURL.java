package lumien.custommainmenu.lib.texts;

import java.nio.charset.StandardCharsets;
import lumien.custommainmenu.lib.StringReplacer;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformRuntime;

public class TextURL implements IText {
   private final String url;
   private final int refreshInterval;
   private String text = "";
   private boolean loading;
   private int refreshCounter;

   public TextURL(String url, int refreshInterval) {
      this.url = StringReplacer.replacePlaceholders(url);
      this.refreshInterval = refreshInterval;
      this.request();
   }

   private void request() {
      if (!this.url.startsWith("https://") && !this.url.startsWith("http://")) {
         return;
      }
      this.loading = true;
      PlatformRuntime.downloadRemoteURIByteArray(this.url, bytes -> {
         if (bytes != null) {
            this.text = new String(bytes, StandardCharsets.UTF_8);
         }
         this.loading = false;
      });
   }

   public void tick() {
      if (this.refreshInterval >= 60 && ++this.refreshCounter >= this.refreshInterval && !this.loading) {
         this.refreshCounter = 0;
         this.request();
      }
   }

   public String get() {
      return this.text;
   }
}
