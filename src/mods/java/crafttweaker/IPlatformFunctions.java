package crafttweaker;

import crafttweaker.api.chat.IChatMessage;
import crafttweaker.api.item.IItemDefinition;

public interface IPlatformFunctions {
   IChatMessage getMessage(String var1);

   IItemDefinition getItemDefinition(int var1);
}
