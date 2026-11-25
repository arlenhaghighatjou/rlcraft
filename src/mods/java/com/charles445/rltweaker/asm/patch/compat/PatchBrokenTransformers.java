package com.charles445.rltweaker.asm.patch.compat;

import com.charles445.rltweaker.asm.patch.IPatchManager;
import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ModTransformer;
import javax.annotation.Nullable;
import org.objectweb.asm.tree.ClassNode;

public class PatchBrokenTransformers extends PatchManager {
   public PatchBrokenTransformers() {
      super("Broken Transformers");
      this.add(new PatchBrokenTransformers.RecomputePatch(this, "net.minecraft.client.renderer.RenderItem", ModTransformer.LIBRARIANLIB));
      this.add(new PatchBrokenTransformers.RecomputePatch(this, "net.minecraft.client.renderer.entity.layers.LayerArmorBase", ModTransformer.LIBRARIANLIB));
      this.add(new PatchBrokenTransformers.RecomputePatch(this, "net.minecraft.client.renderer.BlockRendererDispatcher", ModTransformer.LIBRARIANLIB));
      this.add(new PatchBrokenTransformers.RecomputePatch(this, "net.minecraft.client.particle.Particle", ModTransformer.LIBRARIANLIB));
      this.add(new PatchBrokenTransformers.RecomputePatch(this, "net.minecraft.world.World", ModTransformer.LIBRARIANLIB));
      this.add(new PatchBrokenTransformers.RecomputePatch(this, "net.minecraft.network.NetHandlerPlayServer", ModTransformer.LIBRARIANLIB));
   }

   public class RecomputePatch extends Patch {
      @Nullable
      private ModTransformer mod;

      public RecomputePatch(IPatchManager manager, String target, @Nullable ModTransformer mod) {
         super(manager, target, 3);
         this.mod = mod;
      }

      public void patch(ClassNode clazzNode) {
         if (this.mod != null && !this.hasModTransformer(this.mod)) {
            this.cancelled = true;
         }
      }
   }
}
