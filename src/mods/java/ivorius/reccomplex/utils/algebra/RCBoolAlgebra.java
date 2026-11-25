package ivorius.reccomplex.utils.algebra;

public class RCBoolAlgebra {
   private static Algebra<Boolean> algebra;

   public RCBoolAlgebra() {
   }

   public static Algebra<Boolean> algebra() {
      return algebra != null ? algebra : (algebra = new Algebra<Boolean>(new Algebra.Operator[]{BoolAlgebra.parentheses("(", ")"), BoolAlgebra.conditional("??", "::"), BoolAlgebra.or("|"), BoolAlgebra.and("&"), BoolAlgebra.not("!")}));
   }
}
