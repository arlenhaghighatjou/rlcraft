package lumien.custommainmenu.lib.texts;

public class TextString implements IText {
   String string;

   public TextString(String string) {
      this.string = string;
   }

   public String get() {
      return this.string;
   }
}
