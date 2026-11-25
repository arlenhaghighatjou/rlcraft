package crafttweaker.api.creativetabs;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.creativetabs.ICreativeTab")
@ZenRegister
public interface ICreativeTab {
   @ZenMethod
   void setBackgroundImageName(String var1);

   @ZenGetter("searchBarWidth")
   int getSearchBarWidth();

   @ZenGetter("tabLabel")
   String getTabLabel();

   @ZenMethod
   void setNoScrollBar();

   @ZenMethod
   void setNoTitle();

   Object getInternal();
}
