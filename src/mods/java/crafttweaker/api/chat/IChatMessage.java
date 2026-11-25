package crafttweaker.api.chat;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.chat.IChatMessage")
@ZenRegister
public interface IChatMessage {
   @ZenOperator(OperatorType.ADD)
   IChatMessage add(IChatMessage var1);

   Object getInternal();
}
