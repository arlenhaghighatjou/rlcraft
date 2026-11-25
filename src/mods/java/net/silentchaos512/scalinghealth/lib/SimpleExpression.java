package net.silentchaos512.scalinghealth.lib;

import java.util.Optional;

public final class SimpleExpression {
   private final SimpleExpression.Operation operation;
   private final float value;

   private SimpleExpression(String str) {
      char c = str.charAt(0);
      if (c != '*' && c != 'x') {
         if (c == '/') {
            this.operation = SimpleExpression.Operation.DIV;
         } else if (c == '-') {
            this.operation = SimpleExpression.Operation.SUB;
         } else {
            this.operation = SimpleExpression.Operation.ADD;
         }
      } else {
         this.operation = SimpleExpression.Operation.MUL;
      }

      this.value = Float.parseFloat(str.substring(1));
   }

   public static Optional<SimpleExpression> from(String str) {
      try {
         return Optional.of(new SimpleExpression(str));
      } catch (NumberFormatException var2) {
         return Optional.empty();
      }
   }

   public double apply(double value) {
      switch(this.operation) {
      case ADD:
         return value + (double)this.value;
      case SUB:
         return value - (double)this.value;
      case MUL:
         return value * (double)this.value;
      case DIV:
         return value / (double)this.value;
      default:
         return value;
      }
   }

   private static enum Operation {
      ADD,
      SUB,
      MUL,
      DIV;

      private Operation() {
      }
   }
}
