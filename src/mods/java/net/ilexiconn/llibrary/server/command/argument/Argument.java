package net.ilexiconn.llibrary.server.command.argument;

public class Argument<T> {
   private String name;
   private T value;

   public Argument(String name, T value) {
      this.name = name;
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   public T getValue() {
      return this.value;
   }
}
