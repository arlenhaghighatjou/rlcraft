package knightminer.inspirations.plugins.jei.texture;

import knightminer.inspirations.library.util.TextureBlockUtil;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

public class TextureSubtypeInterpreter implements ISubtypeInterpreter {
   public TextureSubtypeInterpreter() {
   }

   public String apply(ItemStack stack) {
      String meta = stack.getMetadata() + ":";
      ItemStack textureStack = TextureBlockUtil.getStackTexture(stack);
      return !textureStack.isEmpty() ? meta + textureStack.getItem().getRegistryName() + ":" + textureStack.getMetadata() : meta;
   }
}
