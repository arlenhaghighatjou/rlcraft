package slimeknights.mantle.util;
import net.lax1dude.eaglercraft.v1_8.HString;

public class SlimeknightException extends RuntimeException {
   public SlimeknightException() {
   }

   public SlimeknightException(String message) {
      super(message);
   }

   public SlimeknightException(String message, Object... params) {
      super(HString.format(message, params));
   }

   public SlimeknightException(String message, Throwable cause) {
      super(message, cause);
   }

   public SlimeknightException(Throwable cause) {
      super(cause);
   }

   public SlimeknightException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}
