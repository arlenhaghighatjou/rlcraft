package net.silentchaos512.lib.util;

import net.silentchaos512.lib.base.IModBase;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

/** @deprecated */
@Deprecated
public class DebugLog {
   private static final int SPAM_COUNTER_LIMIT = 100;
   private static final int SPAM_TIME_LIMIT = 5;
   private final Logger logger;
   private final IModBase mod;
   private String lastOutput = "";
   private long lastSpamTimer = 0L;
   private int spamCounter = 0;
   private boolean userInformedOfSpam = false;
   private boolean allowStdOut = true;

   public DebugLog(Logger logger, IModBase mod) {
      this.logger = logger;
      this.mod = mod;
   }

   public void log(String msg, Object... params) {
      String output = this.logger.getMessageFactory().newMessage(msg, params).getFormattedMessage();
      boolean isSameAsLastOutput = output.equals(this.lastOutput);
      this.logger.debug(output);
      this.spamDetection(isSameAsLastOutput);
      if (this.mod.isDevBuild() && this.allowStdOut && !isSameAsLastOutput) {
         System.out.println(output);
         this.lastOutput = output;
      }

   }

   private void spamDetection(boolean isSameAsLastOutput) {
      if (isSameAsLastOutput) {
         long currentTime = System.currentTimeMillis();
         if (currentTime - this.lastSpamTimer > 5L) {
            ++this.spamCounter;
            this.lastSpamTimer = currentTime;
            if (this.spamCounter > 100 && !this.userInformedOfSpam) {
               this.logger.warn("Mod '{}' ({}) seems to be spamming the debug log. This is probably a bug! Include your debug.log when reporting this.", this.mod.getModId(), this.mod.getModName());
               this.userInformedOfSpam = true;
            }
         } else {
            this.lastSpamTimer = currentTime;
         }
      } else {
         this.spamCounter = 0;
      }

   }

   public void setAllowStdOut(boolean allowStdOut) {
      this.allowStdOut = allowStdOut;
   }
}
