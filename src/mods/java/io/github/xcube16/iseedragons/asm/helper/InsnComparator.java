package io.github.xcube16.iseedragons.asm.helper;

import java.io.Serializable;
import java.util.Comparator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class InsnComparator implements Comparator<AbstractInsnNode>, Serializable {
   private static final long serialVersionUID = 408241651446425342L;
   public static final int INT_WILDCARD = -1;
   public static final String WILDCARD = "*";

   public InsnComparator() {
   }

   public int compare(AbstractInsnNode a, AbstractInsnNode b) {
      return this.areInsnsEqual(a, b) ? 0 : 1;
   }

   public boolean areInsnsEqual(AbstractInsnNode a, AbstractInsnNode b) {
      if (a == b) {
         return true;
      } else if (a != null && b != null) {
         if (a.equals(b)) {
            return true;
         } else if (a.getOpcode() != b.getOpcode()) {
            return false;
         } else {
            switch(a.getType()) {
            case 1:
               return this.areIntInsnsEqual((IntInsnNode)a, (IntInsnNode)b);
            case 2:
               return this.areVarInsnsEqual((VarInsnNode)a, (VarInsnNode)b);
            case 3:
               return this.areTypeInsnsEqual((TypeInsnNode)a, (TypeInsnNode)b);
            case 4:
               return this.areFieldInsnsEqual((FieldInsnNode)a, (FieldInsnNode)b);
            case 5:
               return this.areMethodInsnsEqual((MethodInsnNode)a, (MethodInsnNode)b);
            case 6:
            case 7:
            case 8:
            default:
               return true;
            case 9:
               return this.areLdcInsnsEqual((LdcInsnNode)a, (LdcInsnNode)b);
            case 10:
               return this.areIincInsnsEqual((IincInsnNode)a, (IincInsnNode)b);
            }
         }
      } else {
         return false;
      }
   }

   private boolean areVarInsnsEqual(VarInsnNode a, VarInsnNode b) {
      return this.intValuesMatch(a.var, b.var);
   }

   private boolean areTypeInsnsEqual(TypeInsnNode a, TypeInsnNode b) {
      return this.valuesMatch(a.desc, b.desc);
   }

   private boolean areFieldInsnsEqual(FieldInsnNode a, FieldInsnNode b) {
      return this.valuesMatch(a.owner, b.owner) && this.valuesMatch(a.name, b.name) && this.valuesMatch(a.desc, b.desc);
   }

   private boolean areMethodInsnsEqual(MethodInsnNode a, MethodInsnNode b) {
      return this.valuesMatch(a.owner, b.owner) && this.valuesMatch(a.name, b.name) && this.valuesMatch(a.desc, b.desc);
   }

   private boolean areIntInsnsEqual(IntInsnNode a, IntInsnNode b) {
      return this.intValuesMatch(a.operand, b.operand);
   }

   private boolean areIincInsnsEqual(IincInsnNode a, IincInsnNode b) {
      return this.intValuesMatch(a.var, b.var) && this.intValuesMatch(a.incr, b.incr);
   }

   private boolean areLdcInsnsEqual(LdcInsnNode a, LdcInsnNode b) {
      return this.valuesMatch(a.cst, b.cst);
   }

   private boolean intValuesMatch(int a, int b) {
      return a == b || a == -1 || b == -1;
   }

   private boolean valuesMatch(Object a, Object b) {
      return a.equals(b) || a.equals("*") || b.equals("*");
   }
}
