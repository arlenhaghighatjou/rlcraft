package snownee.snow.mixin;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ItemBlock.class})
public interface ItemBlockAccessor {
   @Accessor("block")
   void setBlock(Block var1);
}
