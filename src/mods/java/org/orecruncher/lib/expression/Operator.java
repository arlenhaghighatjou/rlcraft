package org.orecruncher.lib.expression;

public abstract class Operator {
   private final String oper;
   private final int precedence;
   private final boolean leftAssoc;
   private final boolean unary;

   public Operator(String oper, int precedence, boolean leftAssoc) {
      this(oper, precedence, leftAssoc, false);
   }

   public Operator(String oper, int precedence, boolean leftAssoc, boolean unary) {
      this.oper = oper;
      this.precedence = precedence;
      this.leftAssoc = leftAssoc;
      this.unary = unary;
   }

   public String getOper() {
      return this.oper;
   }

   public int getPrecedence() {
      return this.precedence;
   }

   public boolean isLeftAssoc() {
      return this.leftAssoc;
   }

   public boolean isUnary() {
      return this.unary;
   }

   public abstract IVariant eval(LazyVariant... var1);
}
