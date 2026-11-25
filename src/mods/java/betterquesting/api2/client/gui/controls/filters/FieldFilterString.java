package betterquesting.api2.client.gui.controls.filters;

import betterquesting.api2.client.gui.controls.IFieldFilter;

public class FieldFilterString implements IFieldFilter<String> {
   public static final FieldFilterString INSTANCE = new FieldFilterString((String)null);
   private final String regex = null;

   public FieldFilterString(String regex) {
   }

   public boolean isValid(String input) {
      return this.regex != null ? input.matches(this.regex) : true;
   }

   public String filterText(String input) {
      return this.regex != null ? input.replaceAll(this.regex, "") : input;
   }

   public String parseValue(String input) {
      return input;
   }
}
