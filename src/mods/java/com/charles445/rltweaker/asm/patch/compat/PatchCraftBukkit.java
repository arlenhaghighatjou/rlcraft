package com.charles445.rltweaker.asm.patch.compat;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ModTransformer;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchCraftBukkit extends PatchManager {
   public PatchCraftBukkit() {
      super("CraftBukkit");
      this.add(new Patch(this, "net.minecraft.tileentity.TileEntityBeacon", 1) {
         public void patch(ClassNode clazzNode) {
            if (!this.hasModTransformer(ModTransformer.CHARM)) {
               this.cancelled = true;
            } else {
               MethodNode m_addEffectsToPlayers = this.findMethod(clazzNode, new String[]{"addEffectsToPlayers", "addEffectsToPlayers"});
               if (this.methodReferencesOwner(m_addEffectsToPlayers, "svenhjol/charm/base/ASMHooks")) {
                  this.announce("Charm succeeded at patching already, skipping...");
                  this.cancelled = true;
               } else {
                  AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_addEffectsToPlayers), 182, "getHumansInRange");
                  if (anchor == null) {
                     this.announce("CraftBukkit was not located in TileEntityBeacon, skipping...");
                     this.cancelled = true;
                  } else {
                     anchor = this.nextInsn(anchor);
                     InsnList inject = new InsnList();
                     inject.add(new VarInsnNode(25, 0));
                     inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/compat/HookCraftBukkit$Charm", "handleAnimalBeacon", "(Lnet/minecraft/tileentity/TileEntityBeacon;)V", false));
                     this.insert(m_addEffectsToPlayers, anchor, inject);
                     this.announce("TileEntityBeacon was patched to make Charm and CraftBukkit compatible");
                  }
               }
            }
         }
      });
   }
}
