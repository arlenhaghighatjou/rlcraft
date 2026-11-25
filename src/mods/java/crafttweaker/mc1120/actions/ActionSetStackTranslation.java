package crafttweaker.mc1120.actions;

import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import net.minecraft.util.text.translation.LanguageMap;

public class ActionSetStackTranslation implements IAction {
   private static final Charset UTF8 = Charset.forName("utf-8");
   private final IItemStack stack;
   private final String key;
   private final String newValue;

   public ActionSetStackTranslation(IItemStack stack, String key, String value) {
      this.stack = stack;
      this.key = key;
      this.newValue = value;
   }

   private static void set(IItemStack stack, String key, String value) {
      if (value.contains("\\\"")) {
         value = value.replace("\\\"", "\"");
      }

      LanguageMap.inject(new ByteArrayInputStream((key + "=" + value).getBytes(UTF8)));
   }

   public void apply() {
      set(this.stack, this.key, this.newValue);
   }

   public String describe() {
      return "Translating " + this.key + " to " + this.newValue;
   }
}
