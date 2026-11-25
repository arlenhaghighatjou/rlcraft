package vazkii.quark.tweaks.item;

import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;

public class ItemGlassShard extends ItemMod implements IQuarkItem {
   private static final String[] VARIANTS = new String[]{"glass_shard", "glass_shard_white", "glass_shard_orange", "glass_shard_magenta", "glass_shard_light_blue", "glass_shard_yellow", "glass_shard_lime", "glass_shard_pink", "glass_shard_gray", "glass_shard_silver", "glass_shard_cyan", "glass_shard_purple", "glass_shard_blue", "glass_shard_brown", "glass_shard_green", "glass_shard_red", "glass_shard_black"};

   public ItemGlassShard() {
      super("glass_shards", VARIANTS);
      this.setCreativeTab(CreativeTabs.MATERIALS);
   }
}
