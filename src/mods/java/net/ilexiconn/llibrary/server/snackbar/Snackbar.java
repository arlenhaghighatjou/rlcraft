package net.ilexiconn.llibrary.server.snackbar;

public class Snackbar {
   private String message;
   private int duration;
   private int color;
   private SnackbarPosition position;

   private Snackbar(String message) {
      this.message = message;
      this.duration = 0;
      this.color = -13421773;
      this.position = SnackbarPosition.DOWN;
   }

   public static Snackbar create(String message) {
      return new Snackbar(message);
   }

   public String getMessage() {
      return this.message;
   }

   public int getDuration() {
      return this.duration;
   }

   public Snackbar setDuration(int duration) {
      this.duration = duration;
      return this;
   }

   public int getColor() {
      return this.color;
   }

   public SnackbarPosition getPosition() {
      return this.position;
   }

   public Snackbar setColor(int color) {
      this.color = color;
      return this;
   }

   public Snackbar setPosition(SnackbarPosition position) {
      this.position = position;
      return this;
   }
}
