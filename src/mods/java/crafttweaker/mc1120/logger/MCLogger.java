package crafttweaker.mc1120.logger;

import crafttweaker.api.player.IPlayer;
import crafttweaker.runtime.ILogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.regex.Pattern;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

public class MCLogger implements ILogger {
   private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
   private Writer writer;
   private PrintWriter printWriter;
   private boolean isDefaultDisabled = false;

   public MCLogger(File output) {
      try {
         if (output.isDirectory()) {
            boolean success = output.delete();
            if (!success) {
               throw new RuntimeException("Failed to delete log file-as-directory " + output);
            }
         }

         this.writer = new OutputStreamWriter(new FileOutputStream(output), "utf-8");
         this.printWriter = new PrintWriter(this.writer);
      } catch (UnsupportedEncodingException var3) {
         throw new RuntimeException("What the heck?");
      } catch (FileNotFoundException var4) {
         throw new RuntimeException("Could not open log file " + output);
      }
   }

   public void logCommand(String message) {
      try {
         this.writer.write(this.stripMessage(message) + "\n");
         this.writer.flush();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void logInfo(String message) {
      try {
         this.writer.write("[" + Loader.instance().getLoaderState() + "][" + FMLCommonHandler.instance().getEffectiveSide() + "][INFO] " + this.stripMessage(message) + "\n");
         this.writer.flush();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void logWarning(String message) {
      try {
         this.writer.write("[" + Loader.instance().getLoaderState() + "][" + FMLCommonHandler.instance().getEffectiveSide() + "][WARNING] " + this.stripMessage(message) + "\n");
         this.writer.flush();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void logError(String message) {
      this.logError(message, (Throwable)null);
   }

   public void logError(String message, Throwable exception) {
      try {
         this.writer.write("[" + Loader.instance().getLoaderState() + "][" + FMLCommonHandler.instance().getEffectiveSide() + "][ERROR] " + this.stripMessage(message) + "\n");
         if (exception != null) {
            exception.printStackTrace(this.printWriter);
         }

         this.writer.flush();
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   public void logPlayer(IPlayer player) {
   }

   public String stripMessage(String message) {
      return message == null ? null : FORMATTING_CODE_PATTERN.matcher(message).replaceAll("");
   }

   public void logDefault(String message) {
      if (!this.isLogDisabled()) {
         this.logInfo(message);
      }

   }

   public boolean isLogDisabled() {
      return this.isDefaultDisabled;
   }

   public void setLogDisabled(boolean logDisabled) {
      this.isDefaultDisabled = logDisabled;
   }
}
