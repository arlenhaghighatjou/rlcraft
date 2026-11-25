package crafttweaker.mc1120.formatting;

import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.text.ITextComponent;
import crafttweaker.mc1120.text.expand.ExpandTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FormattedString implements IMCFormattedString {
   private final String value;

   public FormattedString(String value) {
      this.value = value;
   }

   public String getTooltipString() {
      return this.value;
   }

   public String getTooltipString(String context) {
      return this.value;
   }

   public IFormattedText add(IFormattedText other) {
      return this.cat(other);
   }

   public IFormattedText cat(IFormattedText other) {
      return new FormattedStringJoin(this, (IMCFormattedString)other);
   }

   public String getText() {
      return this.getTooltipString();
   }

   public String toString() {
      return this.getTooltipString();
   }

   public ITextComponent asTextComponent() {
      ITextComponent textComponent = ExpandTextComponent.fromString(this.getText());
      textComponent.getStyle().setColor(TextFormatting.RESET.name());
      return textComponent;
   }
}
