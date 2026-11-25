package crafttweaker.mc1120.text.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.ITextComponent.Serializer;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;

@ZenRegister
@ZenExpansion("crafttweaker.text.ITextComponent")
public class ExpandTextComponent {
   public ExpandTextComponent() {
   }

   @ZenMethodStatic
   public static ITextComponent fromString(String s) {
      return CraftTweakerMC.getITextComponent(new TextComponentString(s));
   }

   @ZenMethodStatic
   public static ITextComponent fromTranslation(String translationKey) {
      return CraftTweakerMC.getITextComponent(new TextComponentTranslation(translationKey, new Object[0]));
   }

   @ZenMethodStatic
   public static ITextComponent fromTranslation(String translationKey, Object... args) {
      return CraftTweakerMC.getITextComponent(new TextComponentTranslation(translationKey, args));
   }

   @ZenMethodStatic
   public static ITextComponent fromData(IData data) {
      if (!(data instanceof DataMap) && !(data instanceof DataList)) {
         throw new IllegalArgumentException("data argument must be DataMap or DataList!");
      } else {
         return CraftTweakerMC.getITextComponent(Serializer.fromJsonLenient(data.asString()));
      }
   }
}
