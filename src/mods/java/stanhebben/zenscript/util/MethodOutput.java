package stanhebben.zenscript.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class MethodOutput {
   private final LocalVariablesSorter visitor;
   private boolean debug = false;
   private int labelIndex = 1;
   private Map<Label, String> labelNames;

   public MethodOutput(ClassVisitor cls, int access, String name, String descriptor, String signature, String[] exceptions) {
      MethodVisitor methodVisitor = cls.visitMethod(access, name, descriptor, signature, exceptions);
      this.visitor = new LocalVariablesSorter(access, descriptor, methodVisitor);
   }

   public MethodOutput(ClassVisitor cls, int access, String name, String descriptor, String signature, String[] exceptions, String[] annotations) {
      MethodVisitor methodVisitor = cls.visitMethod(access, name, descriptor, signature, exceptions);

      for(String annotation : annotations) {
         methodVisitor.visitAnnotation(annotation, true);
      }

      this.visitor = new LocalVariablesSorter(access, descriptor, methodVisitor);
   }

   public MethodOutput(LocalVariablesSorter visitor) {
      this.visitor = visitor;
   }

   public void enableDebug() {
      this.debug = true;
   }

   public LocalVariablesSorter getVisitor() {
      return this.visitor;
   }

   public void start() {
      if (this.debug) {
         System.out.println("--start--");
      }

      this.visitor.visitCode();
   }

   public void end() {
      if (this.debug) {
         System.out.println("--end--");
      }

      this.visitor.visitMaxs(0, 0);
      this.visitor.visitEnd();
   }

   public void label(Label label) {
      if (this.debug) {
         System.out.println("Label " + this.getLabelName(label));
      }

      this.visitor.visitLabel(label);
   }

   public int local(Type type) {
      return this.visitor.newLocal(type);
   }

   public int local(Class cls) {
      return this.visitor.newLocal(Type.getType(cls));
   }

   public void iConst0() {
      if (this.debug) {
         System.out.println("iconst0");
      }

      this.visitor.visitInsn(3);
   }

   public void iConst1() {
      if (this.debug) {
         System.out.println("iconst1");
      }

      this.visitor.visitInsn(4);
   }

   public void biPush(byte value) {
      if (this.debug) {
         System.out.println("bipush");
      }

      this.visitor.visitIntInsn(16, value);
   }

   public void siPush(short value) {
      if (this.debug) {
         System.out.println("sipush");
      }

      this.visitor.visitIntInsn(17, value);
   }

   public void aConstNull() {
      if (this.debug) {
         System.out.println("null");
      }

      this.visitor.visitInsn(1);
   }

   public void constant(Object value) {
      if (this.debug) {
         System.out.println("ldc " + value);
      }

      this.visitor.visitLdcInsn(value);
   }

   public void pop() {
      if (this.debug) {
         System.out.println("pop");
      }

      this.visitor.visitInsn(87);
   }

   public void pop(boolean large) {
      if (this.debug) {
         System.out.println("pop");
      }

      this.visitor.visitInsn(large ? 88 : 87);
   }

   public void dup() {
      if (this.debug) {
         System.out.println("dup");
      }

      this.visitor.visitInsn(89);
   }

   public void dup(boolean large) {
      if (this.debug) {
         System.out.println("dup");
      }

      this.visitor.visitInsn(large ? 92 : 89);
   }

   public void dup2() {
      if (this.debug) {
         System.out.println("dup2");
      }

      this.visitor.visitInsn(92);
   }

   public void dupX1() {
      if (this.debug) {
         System.out.println("dupx1");
      }

      this.visitor.visitInsn(90);
   }

   public void dupX2() {
      if (this.debug) {
         System.out.println("dupx2");
      }

      this.visitor.visitInsn(91);
   }

   public void dup2X1() {
      if (this.debug) {
         System.out.println("dup2_x1");
      }

      this.visitor.visitInsn(93);
   }

   public void dup2X2() {
      if (this.debug) {
         System.out.println("dup2_x2");
      }

      this.visitor.visitInsn(94);
   }

   public void store(Type type, int local) {
      if (this.debug) {
         System.out.println("store " + local);
      }

      this.visitor.visitVarInsn(type.getOpcode(54), local);
   }

   public void load(Type type, int local) {
      if (this.debug) {
         System.out.println("load " + local);
      }

      this.visitor.visitVarInsn(type.getOpcode(21), local);
   }

   public void storeInt(int local) {
      if (this.debug) {
         System.out.println("storeInt " + local);
      }

      this.visitor.visitVarInsn(54, local);
   }

   public void loadInt(int local) {
      if (this.debug) {
         System.out.println("loadInt " + local);
      }

      this.visitor.visitVarInsn(21, local);
   }

   public void storeObject(int local) {
      if (this.debug) {
         System.out.println("storeObject " + local);
      }

      this.visitor.visitVarInsn(58, local);
   }

   public void loadObject(int local) {
      if (this.debug) {
         System.out.println("loadObject " + local);
      }

      this.visitor.visitVarInsn(25, local);
   }

   public void arrayLength() {
      if (this.debug) {
         System.out.println("arrayLength");
      }

      this.visitor.visitInsn(190);
   }

   public void arrayLoad(Type type) {
      if (this.debug) {
         System.out.println("arrayLoad");
      }

      this.visitor.visitInsn(type.getOpcode(46));
   }

   public void arrayStore(Type type) {
      if (this.debug) {
         System.out.println("arrayStore");
      }

      this.visitor.visitInsn(type.getOpcode(79));
   }

   public void newArray(Type componentType) {
      if (this.debug) {
         System.out.println("newArray");
      }

      int sort = componentType.getSort();
      if (sort == 11) {
         throw new RuntimeException("Unsupported array type: " + componentType);
      } else {
         if (sort != 10 && sort != 9) {
            int type;
            switch(sort) {
            case 1:
               type = 4;
               break;
            case 2:
            default:
               throw new RuntimeException("Unsupported array type: " + componentType);
            case 3:
               type = 8;
               break;
            case 4:
               type = 9;
               break;
            case 5:
               type = 10;
               break;
            case 6:
               type = 6;
               break;
            case 7:
               type = 11;
               break;
            case 8:
               type = 7;
            }

            this.visitor.visitIntInsn(188, type);
         } else {
            this.visitor.visitTypeInsn(189, componentType.getInternalName());
         }

      }
   }

   public void newArray(Class componentType) {
      if (this.debug) {
         System.out.println("newArray " + componentType.getName());
      }

      this.visitor.visitTypeInsn(188, ZenTypeUtil.internal(componentType));
   }

   public void checkCast(Class newClass) {
      if (this.debug) {
         System.out.println("checkCast " + newClass.getName());
      }

      this.visitor.visitTypeInsn(192, ZenTypeUtil.signature(newClass));
   }

   public void checkCast(String newClass) {
      if (this.debug) {
         System.out.println("checkCast " + newClass);
      }

      this.visitor.visitTypeInsn(192, newClass);
   }

   public void iNeg() {
      if (this.debug) {
         System.out.println("iNeg");
      }

      this.visitor.visitInsn(116);
   }

   public void iAdd() {
      if (this.debug) {
         System.out.println("iAdd");
      }

      this.visitor.visitInsn(96);
   }

   public void iSub() {
      if (this.debug) {
         System.out.println("iSub");
      }

      this.visitor.visitInsn(100);
   }

   public void iMul() {
      if (this.debug) {
         System.out.println("iMul");
      }

      this.visitor.visitInsn(104);
   }

   public void iDiv() {
      if (this.debug) {
         System.out.println("iDiv");
      }

      this.visitor.visitInsn(108);
   }

   public void iRem() {
      if (this.debug) {
         System.out.println("iRem");
      }

      this.visitor.visitInsn(112);
   }

   public void iAnd() {
      if (this.debug) {
         System.out.println("iAnd");
      }

      this.visitor.visitInsn(126);
   }

   public void iOr() {
      if (this.debug) {
         System.out.println("iOr");
      }

      this.visitor.visitInsn(128);
   }

   public void iXor() {
      if (this.debug) {
         System.out.println("iXor");
      }

      this.visitor.visitInsn(130);
   }

   public void iNot() {
      if (this.debug) {
         System.out.println("iNot");
      }

      this.visitor.visitInsn(2);
      this.visitor.visitInsn(130);
   }

   public void iXorVs1() {
      if (this.debug) {
         System.out.println("iXor against '1'");
      }

      this.visitor.visitInsn(4);
      this.visitor.visitInsn(130);
   }

   public void iShr() {
      if (this.debug) {
         System.out.println("iShr");
      }

      this.visitor.visitInsn(122);
   }

   public void iShl() {
      if (this.debug) {
         System.out.println("iShl");
      }

      this.visitor.visitInsn(120);
   }

   public void lNeg() {
      if (this.debug) {
         System.out.println("lNeg");
      }

      this.visitor.visitInsn(117);
   }

   public void lAdd() {
      if (this.debug) {
         System.out.println("lAdd");
      }

      this.visitor.visitInsn(97);
   }

   public void lSub() {
      if (this.debug) {
         System.out.println("lSub");
      }

      this.visitor.visitInsn(101);
   }

   public void lMul() {
      if (this.debug) {
         System.out.println("lMul");
      }

      this.visitor.visitInsn(105);
   }

   public void lDiv() {
      if (this.debug) {
         System.out.println("lDiv");
      }

      this.visitor.visitInsn(109);
   }

   public void lRem() {
      if (this.debug) {
         System.out.println("lRem");
      }

      this.visitor.visitInsn(113);
   }

   public void lAnd() {
      if (this.debug) {
         System.out.println("lAnd");
      }

      this.visitor.visitInsn(127);
   }

   public void lOr() {
      if (this.debug) {
         System.out.println("lOr");
      }

      this.visitor.visitInsn(129);
   }

   public void lXor() {
      if (this.debug) {
         System.out.println("lXor");
      }

      this.visitor.visitInsn(131);
   }

   public void lNot() {
      if (this.debug) {
         System.out.println("lNot");
      }

      this.constant(Long.valueOf(-1L));
      this.lXor();
   }

   public void lShr() {
      if (this.debug) {
         System.out.println("lShr");
      }

      this.visitor.visitInsn(123);
   }

   public void lShl() {
      if (this.debug) {
         System.out.println("lShl");
      }

      this.visitor.visitInsn(121);
   }

   public void fNeg() {
      if (this.debug) {
         System.out.println("fNeg");
      }

      this.visitor.visitInsn(118);
   }

   public void fAdd() {
      if (this.debug) {
         System.out.println("fAdd");
      }

      this.visitor.visitInsn(98);
   }

   public void fSub() {
      if (this.debug) {
         System.out.println("fSub");
      }

      this.visitor.visitInsn(102);
   }

   public void fMul() {
      if (this.debug) {
         System.out.println("fMul");
      }

      this.visitor.visitInsn(106);
   }

   public void fDiv() {
      if (this.debug) {
         System.out.println("fDiv");
      }

      this.visitor.visitInsn(110);
   }

   public void fRem() {
      if (this.debug) {
         System.out.println("fRem");
      }

      this.visitor.visitInsn(114);
   }

   public void dNeg() {
      if (this.debug) {
         System.out.println("dNeg");
      }

      this.visitor.visitInsn(119);
   }

   public void dAdd() {
      if (this.debug) {
         System.out.println("dAdd");
      }

      this.visitor.visitInsn(99);
   }

   public void dSub() {
      if (this.debug) {
         System.out.println("dSub");
      }

      this.visitor.visitInsn(103);
   }

   public void dMul() {
      if (this.debug) {
         System.out.println("dMul");
      }

      this.visitor.visitInsn(107);
   }

   public void dDiv() {
      if (this.debug) {
         System.out.println("dDiv");
      }

      this.visitor.visitInsn(111);
   }

   public void dRem() {
      if (this.debug) {
         System.out.println("dRem");
      }

      this.visitor.visitInsn(115);
   }

   public void iinc(int local) {
      this.iinc(local, 1);
   }

   public void iinc(int local, int increment) {
      if (this.debug) {
         System.out.println("iinc " + local + " + " + increment);
      }

      this.visitor.visitIincInsn(local, increment);
   }

   public void i2b() {
      if (this.debug) {
         System.out.println("i2b");
      }

      this.visitor.visitInsn(145);
   }

   public void i2s() {
      if (this.debug) {
         System.out.println("i2s");
      }

      this.visitor.visitInsn(147);
   }

   public void i2l() {
      if (this.debug) {
         System.out.println("i2l");
      }

      this.visitor.visitInsn(133);
   }

   public void i2f() {
      if (this.debug) {
         System.out.println("i2f");
      }

      this.visitor.visitInsn(134);
   }

   public void i2d() {
      if (this.debug) {
         System.out.println("i2d");
      }

      this.visitor.visitInsn(135);
   }

   public void l2i() {
      if (this.debug) {
         System.out.println("l2i");
      }

      this.visitor.visitInsn(136);
   }

   public void l2f() {
      if (this.debug) {
         System.out.println("l2f");
      }

      this.visitor.visitInsn(137);
   }

   public void l2d() {
      if (this.debug) {
         System.out.println("l2d");
      }

      this.visitor.visitInsn(138);
   }

   public void f2i() {
      if (this.debug) {
         System.out.println("f2i");
      }

      this.visitor.visitInsn(139);
   }

   public void f2l() {
      if (this.debug) {
         System.out.println("f2l");
      }

      this.visitor.visitInsn(140);
   }

   public void f2d() {
      if (this.debug) {
         System.out.println("f2d");
      }

      this.visitor.visitInsn(141);
   }

   public void d2i() {
      if (this.debug) {
         System.out.println("d2i");
      }

      this.visitor.visitInsn(142);
   }

   public void d2l() {
      if (this.debug) {
         System.out.println("d2l");
      }

      this.visitor.visitInsn(143);
   }

   public void d2f() {
      if (this.debug) {
         System.out.println("d2f");
      }

      this.visitor.visitInsn(144);
   }

   public void lCmp() {
      if (this.debug) {
         System.out.println("lCmp");
      }

      this.visitor.visitInsn(148);
   }

   public void fCmp() {
      if (this.debug) {
         System.out.println("fCmp");
      }

      this.visitor.visitInsn(149);
   }

   public void dCmp() {
      if (this.debug) {
         System.out.println("dCmp");
      }

      this.visitor.visitInsn(151);
   }

   public void instanceOf(String clsName) {
      if (this.debug) {
         System.out.println("instanceOf " + clsName);
      }

      this.visitor.visitTypeInsn(193, clsName);
   }

   public void invokeStatic(String owner, String name, String descriptor) {
      if (this.debug) {
         System.out.println("invokeStatic " + owner + '.' + name + descriptor);
      }

      if (owner == null) {
         throw new IllegalArgumentException("owner cannot be null");
      } else if (name == null) {
         throw new IllegalArgumentException("name cannot be null");
      } else if (descriptor == null) {
         throw new IllegalArgumentException("descriptor cannot be null");
      } else {
         this.visitor.visitMethodInsn(184, owner, name, descriptor, false);
      }
   }

   public void invokeStatic(Class owner, String name, Class result, Class... arguments) {
      StringBuilder descriptor = new StringBuilder();
      descriptor.append('(');

      for(Class argument : arguments) {
         descriptor.append(ZenTypeUtil.signature(argument));
      }

      descriptor.append(')');
      descriptor.append(result == null ? 'V' : ZenTypeUtil.signature(result));
      if (this.debug) {
         System.out.println("invokeStatic " + ZenTypeUtil.internal(owner) + '.' + name + descriptor);
      }

      this.visitor.visitMethodInsn(184, ZenTypeUtil.internal(owner), name, descriptor.toString(), false);
   }

   public void invokeSpecial(String owner, String name, String descriptor) {
      if (this.debug) {
         System.out.println("invokeSpecial " + owner + '.' + name + descriptor);
      }

      this.visitor.visitMethodInsn(183, owner, name, descriptor, false);
   }

   public void invoke(Class owner, String name, Class result, Class... arguments) {
      if (owner.isInterface()) {
         this.invokeInterface(owner, name, result, arguments);
      } else {
         this.invokeVirtual(owner, name, result, arguments);
      }

   }

   public void invokeVirtual(String owner, String name, String descriptor) {
      if (this.debug) {
         System.out.println("invokeVirtual " + owner + '.' + name + descriptor);
      }

      this.visitor.visitMethodInsn(182, owner, name, descriptor, false);
   }

   public void invokeVirtual(Class owner, String name, Class result, Class... arguments) {
      StringBuilder descriptor = new StringBuilder();
      descriptor.append('(');

      for(Class argument : arguments) {
         descriptor.append(ZenTypeUtil.signature(argument));
      }

      descriptor.append(')');
      descriptor.append(result == null ? 'V' : ZenTypeUtil.signature(result));
      if (this.debug) {
         System.out.println("invokeVirtual " + owner + '.' + name + descriptor);
      }

      this.visitor.visitMethodInsn(182, ZenTypeUtil.internal(owner), name, descriptor.toString(), false);
   }

   public void invokeInterface(String owner, String name, String descriptor) {
      if (this.debug) {
         System.out.println("invokeInterface " + owner + '.' + name + descriptor);
      }

      this.visitor.visitMethodInsn(185, owner, name, descriptor, true);
   }

   public void invokeInterface(Class owner, String name, Class result, Class... arguments) {
      StringBuilder descriptor = new StringBuilder();
      descriptor.append('(');

      for(Class argument : arguments) {
         descriptor.append(ZenTypeUtil.signature(argument));
      }

      descriptor.append(')');
      descriptor.append(result == null ? 'V' : ZenTypeUtil.signature(result));
      if (this.debug) {
         System.out.println("invokeInterface " + owner + '.' + name + descriptor);
      }

      this.visitor.visitMethodInsn(185, ZenTypeUtil.internal(owner), name, descriptor.toString(), true);
   }

   public void newObject(Class type) {
      if (this.debug) {
         System.out.println("newObject " + type.getName());
      }

      this.visitor.visitTypeInsn(187, ZenTypeUtil.internal(type));
   }

   public void newObject(String type) {
      if (this.debug) {
         System.out.println("newObject " + type);
      }

      this.visitor.visitTypeInsn(187, type);
   }

   public void construct(Class type, Class... arguments) {
      StringBuilder descriptor = new StringBuilder();
      descriptor.append('(');

      for(Class argument : arguments) {
         descriptor.append(ZenTypeUtil.signature(argument));
      }

      descriptor.append(")V");
      if (this.debug) {
         System.out.println("invokeSpecial " + ZenTypeUtil.internal(type) + ".<init>" + descriptor);
      }

      this.visitor.visitMethodInsn(183, ZenTypeUtil.internal(type), "<init>", descriptor.toString(), false);
   }

   public void construct(String type, String... arguments) {
      StringBuilder descriptor = new StringBuilder();
      descriptor.append('(');

      for(String argument : arguments) {
         descriptor.append(argument);
      }

      descriptor.append(")V");
      if (this.debug) {
         System.out.println("invokeSpecial " + type + ".<init>" + descriptor);
      }

      this.visitor.visitMethodInsn(183, type, "<init>", descriptor.toString(), false);
   }

   public void goTo(Label lbl) {
      if (this.debug) {
         System.out.println("goTo " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(167, lbl);
   }

   public void ifEQ(Label lbl) {
      if (this.debug) {
         System.out.println("ifEQ " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(153, lbl);
   }

   public void ifNE(Label lbl) {
      if (this.debug) {
         System.out.println("ifNE " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(154, lbl);
   }

   public void ifLT(Label lbl) {
      if (this.debug) {
         System.out.println("ifLT " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(155, lbl);
   }

   public void ifGT(Label lbl) {
      if (this.debug) {
         System.out.println("ifGT " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(157, lbl);
   }

   public void ifGE(Label lbl) {
      if (this.debug) {
         System.out.println("ifGE " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(156, lbl);
   }

   public void ifLE(Label lbl) {
      if (this.debug) {
         System.out.println("ifLE " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(158, lbl);
   }

   public void ifICmpLE(Label lbl) {
      if (this.debug) {
         System.out.println("ifICmpLE " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(164, lbl);
   }

   public void ifICmpGE(Label lbl) {
      if (this.debug) {
         System.out.println("ifICmpGE " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(162, lbl);
   }

   public void ifICmpEQ(Label lbl) {
      if (this.debug) {
         System.out.println("ifICmpEQ " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(159, lbl);
   }

   public void ifICmpNE(Label lbl) {
      if (this.debug) {
         System.out.println("ifICmpNE " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(160, lbl);
   }

   public void ifICmpGT(Label lbl) {
      if (this.debug) {
         System.out.println("ifICmpGT " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(163, lbl);
   }

   public void ifICmpLT(Label lbl) {
      if (this.debug) {
         System.out.println("ifICmpLT " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(161, lbl);
   }

   public void ifACmpEq(Label lbl) {
      if (this.debug) {
         System.out.println("ifICmpEQ " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(165, lbl);
   }

   public void ifACmpNe(Label lbl) {
      if (this.debug) {
         System.out.println("ifACmpNE " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(166, lbl);
   }

   public void ifNull(Label lbl) {
      if (this.debug) {
         System.out.println("ifNull " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(198, lbl);
   }

   public void ifNonNull(Label lbl) {
      if (this.debug) {
         System.out.println("ifNonNull " + this.getLabelName(lbl));
      }

      this.visitor.visitJumpInsn(199, lbl);
   }

   public void ret() {
      if (this.debug) {
         System.out.println("ret");
      }

      this.visitor.visitInsn(177);
   }

   public void returnType(Type type) {
      if (this.debug) {
         System.out.println("return " + type.getDescriptor());
      }

      this.visitor.visitInsn(type.getOpcode(172));
   }

   public void returnInt() {
      if (this.debug) {
         System.out.println("ireturn");
      }

      this.visitor.visitInsn(172);
   }

   public void returnObject() {
      if (this.debug) {
         System.out.println("areturn");
      }

      this.visitor.visitInsn(176);
   }

   public void getField(String owner, String name, String descriptor) {
      if (this.debug) {
         System.out.println("getField " + owner + '.' + name + ":" + descriptor);
      }

      this.visitor.visitFieldInsn(180, owner, name, descriptor);
   }

   public void getField(Class owner, String name, Class descriptor) {
      if (this.debug) {
         System.out.println("getField " + owner.getName() + '.' + name + ":" + descriptor.getName());
      }

      this.visitor.visitFieldInsn(180, ZenTypeUtil.internal(owner), name, ZenTypeUtil.signature(descriptor));
   }

   public void putField(String owner, String name, String descriptor) {
      if (this.debug) {
         System.out.println("putField " + owner + '.' + name + ":" + descriptor);
      }

      this.visitor.visitFieldInsn(181, owner, name, descriptor);
   }

   public void putField(Class owner, String name, Class descriptor) {
      if (this.debug) {
         System.out.println("putField " + owner.getName() + '.' + name + ":" + descriptor.getName());
      }

      this.visitor.visitFieldInsn(181, ZenTypeUtil.internal(owner), name, ZenTypeUtil.signature(descriptor));
   }

   public void getStaticField(String owner, String name, String descriptor) {
      if (this.debug) {
         System.out.println("getStatic " + owner + '.' + name + ":" + descriptor);
      }

      this.visitor.visitFieldInsn(178, owner, name, descriptor);
   }

   public void getStaticField(Class owner, Field field) {
      if (this.debug) {
         System.out.println("getField " + owner.getName() + '.' + field.getName() + ":" + ZenTypeUtil.signature(field.getType()));
      }

      this.visitor.visitFieldInsn(178, ZenTypeUtil.internal(owner), field.getName(), ZenTypeUtil.signature(field.getType()));
   }

   public void putStaticField(String owner, String name, String descriptor) {
      if (this.debug) {
         System.out.println("putStatic " + owner + '.' + name + ":" + descriptor);
      }

      this.visitor.visitFieldInsn(179, owner, name, descriptor);
   }

   public void putStaticField(Class owner, Field field) {
      if (this.debug) {
         System.out.println("putStatic " + owner.getName() + '.' + field.getName() + ":" + ZenTypeUtil.signature(field.getType()));
      }

      this.visitor.visitFieldInsn(179, ZenTypeUtil.internal(owner), field.getName(), ZenTypeUtil.signature(field.getType()));
   }

   public void aThrow() {
      this.visitor.visitInsn(191);
   }

   public void position(ZenPosition position) {
      Label label = new Label();
      this.visitor.visitLabel(label);
      this.visitor.visitLineNumber(position.getLine(), label);
   }

   public void swap() {
      if (this.debug) {
         System.out.println("swap");
      }

      this.visitor.visitInsn(95);
   }

   public void swapLargeLower() {
      if (this.debug) {
         System.out.println("Custom: swapLargeLower");
      }

      this.dupX2();
      this.pop();
   }

   private String getLabelName(Label lbl) {
      if (this.labelNames == null) {
         this.labelNames = new HashMap();
      }

      if (!this.labelNames.containsKey(lbl)) {
         this.labelNames.put(lbl, "L" + this.labelIndex++);
      }

      return (String)this.labelNames.get(lbl);
   }
}
