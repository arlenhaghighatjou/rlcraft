package reborncore.common.util;

/** @deprecated */
@Deprecated
public enum Color {
   BLACK("\u00a70"),
   DARK_BLUE("\u00a71"),
   DARK_GREEN("\u00a72"),
   DARK_AQUA("\u00a73"),
   DARK_RED("\u00a74"),
   DARK_PURPLE("\u00a75"),
   GOLD("\u00a76"),
   GRAY("\u00a77"),
   DARK_GRAY("\u00a78"),
   BLUE("\u00a79"),
   GREEN("\u00a7a"),
   AQUA("\u00a7b"),
   RED("\u00a7c"),
   LIGHT_PURPLE("\u00a7d"),
   YELLOW("\u00a7e"),
   WHITE("\u00a7f"),
   STRIKE_THROUGH("\u00a7m"),
   UNDERLINE("\u00a7n"),
   BOLD("\u00a7l"),
   RANDOM("\u00a7k"),
   ITALIC("\u00a7o"),
   RESET("\u00a7r");

   public String code = "";

   private Color(String code) {
      this.code = code;
   }

   public String toString() {
      return this.code;
   }
}
