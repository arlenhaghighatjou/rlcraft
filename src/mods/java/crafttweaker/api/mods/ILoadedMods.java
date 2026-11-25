package crafttweaker.api.mods;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.IterableSimple;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.mods.ILoadedMods")
@IterableSimple("crafttweaker.mods.IMod")
@ZenRegister
public interface ILoadedMods extends Iterable<IMod> {
   @ZenOperator(OperatorType.CONTAINS)
   @ZenMethod
   boolean contains(String var1);

   @ZenOperator(OperatorType.INDEXGET)
   IMod get(String var1);
}
