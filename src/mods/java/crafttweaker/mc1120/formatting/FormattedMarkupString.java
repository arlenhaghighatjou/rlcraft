package crafttweaker.mc1120.formatting;

import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.text.ITextComponent;
import crafttweaker.mc1120.text.expand.ExpandTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FormattedMarkupString implements IMCFormattedString {
   private final TextFormatting markup;
   private final IMCFormattedString contents;

   public FormattedMarkupString(TextFormatting markup, IMCFormattedString contents) {
      this.markup = markup;
      this.contents = contents;
   }

   public String getTooltipString() {
      return this.markup + this.contents.getTooltipString(this.markup.toString());
   }

   public String getTooltipString(String context) {
      return this.markup + this.contents.getTooltipString(context + this.markup);
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

   public ITextComponent asTextComponent() {
      ITextComponent textComponent = ExpandTextComponent.fromString(this.getText());
      textComponent.getStyle().setColor(this.markup.name());
      return textComponent;
   }
}
