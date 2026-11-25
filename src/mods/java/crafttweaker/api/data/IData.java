package crafttweaker.api.data;

import crafttweaker.annotations.ZenRegister;
import java.util.List;
import java.util.Map;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMemberGetter;
import stanhebben.zenscript.annotations.ZenMemberSetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.data.IData")
@ZenRegister
public interface IData {
   @ZenOperator(OperatorType.ADD)
   IData add(IData var1);

   @ZenOperator(OperatorType.SUB)
   IData sub(IData var1);

   @ZenOperator(OperatorType.MUL)
   IData mul(IData var1);

   @ZenOperator(OperatorType.DIV)
   IData div(IData var1);

   @ZenOperator(OperatorType.MOD)
   IData mod(IData var1);

   @ZenOperator(OperatorType.AND)
   IData and(IData var1);

   @ZenOperator(OperatorType.OR)
   IData or(IData var1);

   @ZenOperator(OperatorType.XOR)
   IData xor(IData var1);

   @ZenOperator(OperatorType.NEG)
   IData neg();

   @ZenOperator(OperatorType.NOT)
   IData not();

   @ZenCaster
   @ZenMethod
   boolean asBool();

   @ZenCaster
   @ZenMethod
   byte asByte();

   @ZenCaster
   @ZenMethod
   short asShort();

   @ZenCaster
   @ZenMethod
   int asInt();

   @ZenCaster
   @ZenMethod
   long asLong();

   @ZenCaster
   @ZenMethod
   float asFloat();

   @ZenCaster
   @ZenMethod
   double asDouble();

   @ZenCaster
   @ZenMethod
   String asString();

   @ZenCaster
   @ZenMethod
   List<IData> asList();

   @ZenCaster
   @ZenMethod
   Map<String, IData> asMap();

   @ZenCaster
   @ZenMethod
   byte[] asByteArray();

   @ZenCaster
   @ZenMethod
   int[] asIntArray();

   @ZenOperator(OperatorType.INDEXGET)
   IData getAt(int var1);

   @ZenOperator(OperatorType.INDEXSET)
   void setAt(int var1, IData var2);

   @ZenMethod
   @ZenMemberGetter
   IData memberGet(String var1);

   @ZenMethod
   @ZenMemberSetter
   void memberSet(String var1, IData var2);

   @ZenGetter
   int length();

   @ZenOperator(OperatorType.CONTAINS)
   boolean contains(IData var1);

   @ZenOperator(OperatorType.COMPARE)
   int compareTo(IData var1);

   @ZenOperator(OperatorType.EQUALS)
   boolean equals(IData var1);

   @ZenGetter
   IData immutable();

   @ZenMethod
   IData update(IData var1);

   <T> T convert(IDataConverter<T> var1);

   String toString();
}
