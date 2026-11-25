package com.lycanitesmobs.core;

import com.google.common.base.Objects;

public class ValuePair<A, B> {
   private A valueA;
   private B valueB;

   public ValuePair(A valueA, B valueB) {
      this.valueA = valueA;
      this.valueB = valueB;
   }

   public void setValueA(A value) {
      this.valueA = value;
   }

   public void setValueB(B value) {
      this.valueB = value;
   }

   public A getValueA() {
      return this.valueA;
   }

   public B getValueB() {
      return this.valueB;
   }

   public boolean equals(Object compareObject) {
      if (!(compareObject instanceof ValuePair)) {
         return false;
      } else {
         ValuePair<?, ?> compareValuePair = (ValuePair)compareObject;
         return Objects.equal(this.getValueA(), compareValuePair.getValueA()) && Objects.equal(this.getValueB(), compareValuePair.getValueB());
      }
   }

   public int hashCode() {
      return (this.valueA == null ? 0 : this.valueA.hashCode()) ^ (this.valueB == null ? 0 : this.valueB.hashCode());
   }

   public String toString() {
      return "Value Pair (" + (this.valueA == null ? "null" : this.valueA.toString()) + ", " + (this.valueB == null ? "null" : this.valueB.toString()) + ")";
   }
}
