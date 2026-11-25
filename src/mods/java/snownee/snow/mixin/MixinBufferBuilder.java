package snownee.snow.mixin;

import net.minecraft.client.renderer.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import snownee.snow.BufferBuilderDuck;

@Mixin({BufferBuilder.class})
public class MixinBufferBuilder implements BufferBuilderDuck {
   @Shadow
   private double yOffset;

   public MixinBufferBuilder() {
   }

   public void translateY(double y) {
      this.yOffset += y;
   }
}
