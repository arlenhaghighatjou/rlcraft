package crafttweaker.mc1120.text;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.text.IStyle;
import java.util.Objects;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class MCStyle implements IStyle {
   private final Style style;

   public MCStyle(Style style) {
      this.style = style;
   }

   public IStyle getParent() {
      return CraftTweakerMC.getIStyle(this.style.getParent());
   }

   public void setParent(IStyle style) {
      this.style.setParentStyle(CraftTweakerMC.getStyle(style));
   }

   public boolean getBold() {
      return this.style.getBold();
   }

   public void setBold(boolean bold) {
      this.style.setBold(Boolean.valueOf(bold));
   }

   public boolean getItalic() {
      return this.style.getItalic();
   }

   public void setItalic(boolean italic) {
      this.style.setItalic(Boolean.valueOf(italic));
   }

   public boolean getUnderline() {
      return this.style.getUnderlined();
   }

   public void setUnderline(boolean underline) {
      this.style.setUnderlined(Boolean.valueOf(underline));
   }

   public boolean getStrikethrough() {
      return this.style.getStrikethrough();
   }

   public void setStrikethrough(boolean strikethrough) {
      this.style.setStrikethrough(Boolean.valueOf(strikethrough));
   }

   public boolean getObfuscated() {
      return this.style.getObfuscated();
   }

   public void setObfuscated(boolean obfuscated) {
      this.style.setObfuscated(Boolean.valueOf(obfuscated));
   }

   public String getInsertion() {
      return this.style.getInsertion();
   }

   public void setInsertion(String insertion) {
      this.style.setInsertion(insertion);
   }

   public String getColor() {
      return this.style.getColor() == null ? "null" : this.style.getColor().name();
   }

   public void setColor(String color) {
      TextFormatting formatting = TextFormatting.getValueByName(color);
      if (formatting == null) {
         CraftTweakerAPI.logError("Invalid Color Name: " + color);
      } else {
         this.style.setColor(formatting);
      }

   }

   public IStyle createDeepCopy() {
      return CraftTweakerMC.getIStyle(this.style.createDeepCopy());
   }

   public IStyle createShallowCopy() {
      return CraftTweakerMC.getIStyle(this.style.createShallowCopy());
   }

   public Object getInternal() {
      return this.style;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCStyle mcStyle = (MCStyle)o;
         return Objects.equals(this.style, mcStyle.style);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.style});
   }
}
