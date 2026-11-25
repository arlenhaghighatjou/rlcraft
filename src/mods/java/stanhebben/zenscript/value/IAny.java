package stanhebben.zenscript.value;

import java.util.Iterator;

public interface IAny {
   String NAME = "stanhebben/zenscript/value/IAny";
   int NUM_BYTE = 1;
   int NUM_SHORT = 2;
   int NUM_INT = 3;
   int NUM_LONG = 4;
   int NUM_FLOAT = 5;
   int NUM_DOUBLE = 6;

   IAny not();

   IAny neg();

   IAny add(IAny var1);

   IAny sub(IAny var1);

   IAny cat(IAny var1);

   IAny mul(IAny var1);

   IAny div(IAny var1);

   IAny mod(IAny var1);

   IAny and(IAny var1);

   IAny or(IAny var1);

   IAny xor(IAny var1);

   IAny range(IAny var1);

   int compareTo(IAny var1);

   boolean contains(IAny var1);

   IAny memberGet(String var1);

   void memberSet(String var1, IAny var2);

   IAny memberCall(String var1, IAny... var2);

   IAny indexGet(IAny var1);

   void indexSet(IAny var1, IAny var2);

   IAny call(IAny... var1);

   boolean asBool();

   byte asByte();

   short asShort();

   int asInt();

   long asLong();

   float asFloat();

   double asDouble();

   String asString();

   <T> T as(Class<T> var1);

   boolean is(Class<?> var1);

   boolean canCastImplicit(Class<?> var1);

   int getNumberType();

   Iterator<IAny> iteratorSingle();

   Iterator<IAny[]> iteratorMulti(int var1);
}
