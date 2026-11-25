package ladysnake.spawnercontrol.controlledspawner;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class ItemBlockControlSpawner extends ItemBlock {
   private static final String DEFAULT_LANG_KEY = "tile.msc.controlled_spawner.name";
   private String name;

   public ItemBlockControlSpawner(Block block, String name) {
      super(block);
      this.name = name;
   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack stack) {
      return I18n.canTranslate(this.getUnlocalizedName(stack) + ".name") ? super.getItemStackDisplayName(stack) : I18n.translateToLocalFormatted("tile.msc.controlled_spawner.name", new Object[]{this.name}).trim();
   }
}
