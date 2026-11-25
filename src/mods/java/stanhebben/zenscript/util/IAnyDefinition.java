package stanhebben.zenscript.util;

import org.objectweb.asm.ClassVisitor;

public interface IAnyDefinition {
   void defineMembers(ClassVisitor var1);

   void defineStaticCanCastImplicit(MethodOutput var1);

   void defineStaticAs(MethodOutput var1);

   void defineNot(MethodOutput var1);

   void defineNeg(MethodOutput var1);

   void defineAdd(MethodOutput var1);

   void defineSub(MethodOutput var1);

   void defineCat(MethodOutput var1);

   void defineMul(MethodOutput var1);

   void defineDiv(MethodOutput var1);

   void defineMod(MethodOutput var1);

   void defineAnd(MethodOutput var1);

   void defineOr(MethodOutput var1);

   void defineXor(MethodOutput var1);

   void defineRange(MethodOutput var1);

   void defineCompareTo(MethodOutput var1);

   void defineContains(MethodOutput var1);

   void defineMemberGet(MethodOutput var1);

   void defineMemberSet(MethodOutput var1);

   void defineMemberCall(MethodOutput var1);

   void defineIndexGet(MethodOutput var1);

   void defineIndexSet(MethodOutput var1);

   void defineCall(MethodOutput var1);

   void defineAsBool(MethodOutput var1);

   void defineAsByte(MethodOutput var1);

   void defineAsShort(MethodOutput var1);

   void defineAsInt(MethodOutput var1);

   void defineAsLong(MethodOutput var1);

   void defineAsFloat(MethodOutput var1);

   void defineAsDouble(MethodOutput var1);

   void defineAsString(MethodOutput var1);

   void defineAs(MethodOutput var1);

   void defineIs(MethodOutput var1);

   void defineGetNumberType(MethodOutput var1);

   void defineIteratorSingle(MethodOutput var1);

   void defineIteratorMulti(MethodOutput var1);

   void defineEquals(MethodOutput var1);

   void defineHashCode(MethodOutput var1);
}
