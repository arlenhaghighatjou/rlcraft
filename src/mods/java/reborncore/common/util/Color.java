package reborncore.common.util;

/** @deprecated */
@Deprecated
public enum Color {
   BLACK("§0"),
   DARK_BLUE("§1"),
   DARK_GREEN("§2"),
   DARK_AQUA("§3"),
   DARK_RED("§4"),
   DARK_PURPLE("§5"),
   GOLD("§6"),
   GRAY("§7"),
   DARK_GRAY("§8"),
   BLUE("§9"),
   GREEN("§a"),
   AQUA("§b"),
   RED("§c"),
   LIGHT_PURPLE("§d"),
   YELLOW("§e"),
   WHITE("§f"),
   STRIKE_THROUGH("§m"),
   UNDERLINE("§n"),
   BOLD("§l"),
   RANDOM("§k"),
   ITALIC("§o"),
   RESET("§r");

   public String code = "";

   private Color(String code) {
      this.code = code;
   }

   public String toString() {
      return this.code;
   }
}
