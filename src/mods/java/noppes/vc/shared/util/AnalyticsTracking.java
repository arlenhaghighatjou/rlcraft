package noppes.vc.shared.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;

public class AnalyticsTracking {
   public AnalyticsTracking() {
   }

   public static void sendData(final EntityPlayer player, final String event, final String data, final String modid, final String version) {
      (new Thread(new Runnable() {
         public void run() {
            try {
               EaglercraftUUID uuid = player.getUniqueID();
               String analyticsPostData = "v=1&tid=UA-29079943-5&cid=" + uuid.toString() + "&t=event&ec=" + modid + "_" + version + "&ea=" + event + "&el=" + data + "&ev=300";
               URL url = new URL("http://www.google-analytics.com/collect");
               HttpURLConnection connection = (HttpURLConnection)url.openConnection();
               connection.setConnectTimeout(10000);
               connection.setReadTimeout(10000);
               connection.setDoOutput(true);
               connection.setUseCaches(false);
               connection.setRequestMethod("POST");
               connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
               connection.setRequestProperty("Content-Length", Integer.toString(analyticsPostData.getBytes().length));
               OutputStream dataOutput = connection.getOutputStream();
               dataOutput.write(analyticsPostData.getBytes());
               dataOutput.close();
               connection.getInputStream().close();
            } catch (IOException var6) {
               ;
            }

         }
      })).start();
   }
}
