package com.github.fnar.roguelike.command;

import java.util.function.Consumer;

public class SimpleRoguelikeCommand implements RoguelikeCommand {
   private final Runnable onRun;
   private final Runnable onSuccess;
   private final Consumer<Exception> onException;

   public SimpleRoguelikeCommand(Runnable onRun, Runnable onSuccess, Consumer<Exception> onException) {
      this.onRun = onRun;
      this.onSuccess = onSuccess;
      this.onException = onException;
   }

   public void onSuccess() {
      this.onSuccess.run();
   }

   public void onException(Exception exception) {
      this.onException.accept(exception);
   }

   public void run() {
      try {
         this.onRun.run();
      } catch (Exception var2) {
         this.onException(var2);
         return;
      }

      this.onSuccess();
   }
}
