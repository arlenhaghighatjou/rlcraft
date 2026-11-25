package crafttweaker.api.logger;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.player.IPlayer;
import crafttweaker.runtime.ILogger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MTLogger implements ILogger {
   private final List<ILogger> loggers = new ArrayList();
   private final List<IPlayer> players = new ArrayList();
   private final List<MTLogger.ErrorMessage> unprocessed = new ArrayList();
   private boolean isDefaultDisabled = false;

   public MTLogger() {
   }

   public void addLogger(ILogger logger) {
      this.loggers.add(logger);
   }

   public void removeLogger(ILogger logger) {
      this.loggers.remove(logger);
   }

   public void addPlayer(IPlayer player) {
      this.players.add(player);
      this.logPlayer(player);
   }

   public void removePlayer(IPlayer player) {
      this.players.remove(player);
   }

   public void clear() {
      this.unprocessed.clear();
   }

   public void logCommand(String message) {
      for(ILogger logger : this.loggers) {
         logger.logCommand(message);
      }

   }

   public void logInfo(String message) {
      for(ILogger logger : this.loggers) {
         logger.logInfo(message);
      }

   }

   public void logWarning(String message) {
      for(ILogger logger : this.loggers) {
         logger.logWarning(message);
      }

      String message2 = "\u00a7eWARNING: " + message;
      if (this.players.isEmpty()) {
         this.unprocessed.add(new MTLogger.ErrorMessage(message2, true));
      } else if (!CraftTweakerAPI.isSuppressingWarnings()) {
         this.players.forEach((player) -> {
            player.sendChat(message2);
         });
      }

   }

   public void logError(String message) {
      this.logError(message, (Throwable)null);
   }

   public void logError(String message, Throwable exception) {
      for(ILogger logger : this.loggers) {
         logger.logError(message, exception);
      }

      String message2 = "\u00a7cERROR: " + this.getMessageToSendPlayer(message, exception);
      if (this.players.isEmpty()) {
         this.unprocessed.add(new MTLogger.ErrorMessage(message2, false));
      } else if (!CraftTweakerAPI.isSuppressingErrors()) {
         this.players.forEach((player) -> {
            player.sendChat(message2);
         });
      }

   }

   public void logPlayer(IPlayer player) {
      for(MTLogger.ErrorMessage errorMessage : this.unprocessed) {
         if (errorMessage.isWarning && !CraftTweakerAPI.isSuppressingWarnings()) {
            player.sendChat(errorMessage.message);
         } else if (!errorMessage.isWarning && !CraftTweakerAPI.isSuppressingErrors()) {
            player.sendChat(errorMessage.message);
         }
      }

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

   public String getMessageToSendPlayer(String message, Throwable exception) {
      if (exception == null) {
         return message;
      } else {
         return message == null ? String.valueOf(exception) : message + ", caused by " + exception;
      }
   }

   private static class ErrorMessage {
      final String message;
      final boolean isWarning;

      public ErrorMessage(String message, boolean isWarning) {
         this.message = message;
         this.isWarning = isWarning;
      }
   }
}
