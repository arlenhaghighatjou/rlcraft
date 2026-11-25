package crafttweaker.mc1120.formatting;

import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.text.ITextComponent;
import java.util.Arrays;
import net.minecraft.util.text.TextFormatting;

public class FormattedStringJoin implements IMCFormattedString {
   private final IMCFormattedString[] values;

   public FormattedStringJoin(IMCFormattedString first, IMCFormattedString second) {
      this.values = new IMCFormattedString[]{first, second};
   }

   public FormattedStringJoin(IMCFormattedString[] values) {
      this.values = values;
   }

   public String getTooltipString() {
      StringBuilder result = new StringBuilder();

      for(IMCFormattedString value : this.values) {
         result.append(value.getTooltipString());
      }

      return result.toString();
   }

   public String getTooltipString(String context) {
      boolean first = true;
      StringBuilder result = new StringBuilder();

      for(IMCFormattedString value : this.values) {
         if (first) {
            first = false;
         } else {
            result.append(context);
         }

         result.append(value.getTooltipString());
         result.append(TextFormatting.RESET);
      }

      return result.toString();
   }

   public IFormattedText add(IFormattedText other) {
      return this.cat(other);
   }

   public IFormattedText cat(IFormattedText other) {
      IMCFormattedString[] newValues = (IMCFormattedString[])Arrays.copyOf(this.values, this.values.length + 1);
      newValues[this.values.length] = (IMCFormattedString)other;
      return new FormattedStringJoin(newValues);
   }

   public String getText() {
      return this.getTooltipString();
   }

   public ITextComponent asTextComponent() {
      boolean first = true;
      ITextComponent textComponent = null;

      for(IMCFormattedString value : this.values) {
         if (first) {
            textComponent = value.asTextComponent();
            first = false;
         } else {
            textComponent = textComponent.append(value.asTextComponent());
         }
      }

      return textComponent;
   }
}
