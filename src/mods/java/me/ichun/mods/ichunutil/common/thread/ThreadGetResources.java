package me.ichun.mods.ichunutil.common.thread;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraftforge.fml.relauncher.Side;

public class ThreadGetResources extends Thread {
   private static final String patronList = "https://raw.github.com/iChun/iChunUtil/master/src/main/resources/assets/ichunutil/mod/patrons.json";
   private static final String versionList = "https://raw.github.com/iChun/iChunUtil/master/src/main/resources/assets/ichunutil/mod/versions.json";
   private final Side side;

   public ThreadGetResources(Side side) {
      this.setName("iChunUtil Online Resource Thread");
      this.setDaemon(true);
      this.side = side;
   }

   public void run() {
      if (this.side.isClient()) {
         try {
            Gson gson = new Gson();
            Reader fileIn = new InputStreamReader((new URL("https://raw.github.com/iChun/iChunUtil/master/src/main/resources/assets/ichunutil/mod/patrons.json")).openStream());
            String[] json = (String[])gson.fromJson(fileIn, String[].class);
            fileIn.close();
            if (json != null) {
               for(String s : json) {
                  if (s.replaceAll("-", "").equalsIgnoreCase(iChunUtil.proxy.getPlayerId())) {
                     iChunUtil.userIsPatron = true;
                     iChunUtil.config.reveal(new String[]{"showPatronReward", "patronRewardType"});
                  }
               }
            }
         } catch (UnknownHostException var10) {
            iChunUtil.LOGGER.warn("Error retrieving iChunUtil patron list: UnknownHostException. Is your internet connection working?");
         } catch (Exception var11) {
            iChunUtil.LOGGER.warn("Error retrieving iChunUtil patron list.");
            var11.printStackTrace();
         }
      }

      try {
         Gson gson = new Gson();
         Reader fileIn = new InputStreamReader((new URL("https://raw.github.com/iChun/iChunUtil/master/src/main/resources/assets/ichunutil/mod/versions.json")).openStream());
         UpdateChecker.processModsList((Map)gson.fromJson(fileIn, Map.class));
      } catch (UnknownHostException var8) {
         iChunUtil.LOGGER.warn("Error retrieving mods versions list: UnknownHostException. Is your internet connection working?");
      } catch (Exception var9) {
         iChunUtil.LOGGER.warn("Error retrieving mods versions list.");
         var9.printStackTrace();
      }

   }
}
