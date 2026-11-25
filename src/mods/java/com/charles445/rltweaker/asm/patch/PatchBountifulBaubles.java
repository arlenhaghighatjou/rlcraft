package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.ASMUtil;
import java.util.function.Predicate;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchBountifulBaubles extends PatchManager {
   public PatchBountifulBaubles() {
      this.add(new Patch(this, "cursedflames.bountifulbaubles.recipe.AnvilRecipes", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_onAnvilUpdate = this.findMethod(clazzNode, new String[]{"onAnvilUpdate"});
            this.insert(m_onAnvilUpdate, ASMUtil.findMethodInsn(m_onAnvilUpdate, 184, "cursedflames/bountifulbaubles/recipe/AnvilRecipes", "getResultModifierBook", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", 1), new InsnNode(177));
            MethodNode m_getResultModifierBook = this.findMethod(clazzNode, new String[]{"getResultModifierBook"});
            this.insert(m_getResultModifierBook, ASMUtil.findInsn(m_getResultModifierBook, VarInsnNode.class, (insn) -> {
               return insn.var == 1;
            }, 1), ASMUtil.listOf(new InsnNode(87), new VarInsnNode(25, 2)));
            this.insert(m_getResultModifierBook, ASMUtil.findInsn(m_getResultModifierBook, VarInsnNode.class, (insn) -> {
               return insn.var == 1;
            }, 2), ASMUtil.listOf(new InsnNode(87), new VarInsnNode(25, 2)));
            this.insert(m_getResultModifierBook, ASMUtil.findInsn(m_getResultModifierBook, VarInsnNode.class, (insn) -> {
               return insn.var == 1;
            }, 3), ASMUtil.listOf(new InsnNode(87), new VarInsnNode(25, 2)));
         }
      });
   }
}
