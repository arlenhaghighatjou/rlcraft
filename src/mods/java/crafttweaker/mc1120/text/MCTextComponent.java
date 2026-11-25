package crafttweaker.mc1120.text;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.text.IStyle;
import crafttweaker.api.text.ITextComponent;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MCTextComponent implements ITextComponent {
   private final net.minecraft.util.text.ITextComponent text;

   public MCTextComponent(net.minecraft.util.text.ITextComponent text) {
      this.text = text;
   }

   public ITextComponent append(ITextComponent text) {
      net.minecraft.util.text.ITextComponent copy = this.text.createCopy();
      copy.appendSibling(CraftTweakerMC.getITextComponent(text));
      return CraftTweakerMC.getITextComponent(copy);
   }

   public void setStyle(IStyle style) {
      this.text.setStyle(CraftTweakerMC.getStyle(style));
   }

   public IStyle getStyle() {
      return CraftTweakerMC.getIStyle(this.text.getStyle());
   }

   public String getUnformattedComponentText() {
      return this.text.getUnformattedComponentText();
   }

   public String getUnformattedText() {
      return this.text.getUnformattedText();
   }

   public String getFormattedText() {
      return this.text.getFormattedText();
   }

   public List<ITextComponent> getSiblings() {
      return (List)this.text.getSiblings().stream().map(CraftTweakerMC::getITextComponent).collect(Collectors.toList());
   }

   public ITextComponent createCopy() {
      return CraftTweakerMC.getITextComponent(this.text.createCopy());
   }

   public Object getInternal() {
      return this.text;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCTextComponent that = (MCTextComponent)o;
         return Objects.equals(this.text, that.text);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.text});
   }
}
