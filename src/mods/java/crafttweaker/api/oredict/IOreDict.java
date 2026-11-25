package crafttweaker.api.oredict;

import crafttweaker.annotations.ZenRegister;
import java.util.List;
import stanhebben.zenscript.annotations.IterableSimple;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMemberGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.oredict.IOreDict")
@IterableSimple("crafttweaker.oredict.IOreDictEntry")
@ZenRegister
public interface IOreDict extends Iterable<IOreDictEntry> {
   @ZenMemberGetter
   @ZenOperator(OperatorType.INDEXGET)
   @ZenMethod
   IOreDictEntry get(String var1);

   @ZenGetter("entries")
   List<IOreDictEntry> getEntries();

   @ZenOperator(OperatorType.CONTAINS)
   @ZenMethod
   boolean contains(String var1);
}
