package stanhebben.zenscript.annotations;

public enum OperatorType {
   ADD,
   SUB,
   MUL,
   DIV,
   MOD,
   CAT,
   OR,
   AND,
   XOR,
   NEG,
   NOT,
   INDEXSET,
   INDEXGET,
   RANGE,
   CONTAINS,
   COMPARE,
   MEMBERGETTER,
   MEMBERSETTER,
   EQUALS;

   private OperatorType() {
   }
}
