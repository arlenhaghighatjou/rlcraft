package crafttweaker.api.text;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("crafttweaker.text.IStyle")
public interface IStyle {
   @ZenGetter("parent")
   IStyle getParent();

   @ZenSetter("parent")
   void setParent(IStyle var1);

   @ZenGetter("bold")
   boolean getBold();

   @ZenSetter("bold")
   void setBold(boolean var1);

   @ZenGetter("italic")
   boolean getItalic();

   @ZenSetter("italic")
   void setItalic(boolean var1);

   @ZenGetter("underline")
   boolean getUnderline();

   @ZenSetter("underline")
   void setUnderline(boolean var1);

   @ZenGetter("strikethrough")
   boolean getStrikethrough();

   @ZenSetter("strikethrough")
   void setStrikethrough(boolean var1);

   @ZenGetter("obfuscated")
   boolean getObfuscated();

   @ZenSetter("obfuscated")
   void setObfuscated(boolean var1);

   @ZenGetter("insertion")
   String getInsertion();

   @ZenSetter("insertion")
   void setInsertion(String var1);

   @ZenGetter("color")
   String getColor();

   @ZenSetter("color")
   void setColor(String var1);

   @ZenMethod
   IStyle createShallowCopy();

   @ZenMethod
   IStyle createDeepCopy();

   Object getInternal();
}
