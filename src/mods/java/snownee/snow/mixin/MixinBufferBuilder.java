package snownee.snow.mixin;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import snownee.snow.BufferBuilderDuck;

@Mixin({WorldRenderer.class})
public class MixinBufferBuilder implements BufferBuilderDuck {
   @Shadow
   private double yOffset;

   public MixinBufferBuilder() {
   }

   public void translateY(double y) {
      this.yOffset += y;
   }
}
