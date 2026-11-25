package com.github.fnar.roguelike.command;

import greymerk.roguelike.dungeon.Dungeon;
import java.util.function.Consumer;

public class ReloadSettingsCommand implements RoguelikeCommand {
   private final Runnable onSuccess;
   private final Consumer<Exception> onException;

   public ReloadSettingsCommand(Runnable onSuccess, Consumer<Exception> onException) {
      this.onSuccess = onSuccess;
      this.onException = onException;
   }

   public void run() {
      try {
         Dungeon.initResolver();
      } catch (Exception var2) {
         this.onException(var2);
         return;
      }

      this.onSuccess();
   }

   public void onSuccess() {
      this.onSuccess.run();
   }

   public void onException(Exception exception) {
      this.onException.accept(exception);
   }
}
