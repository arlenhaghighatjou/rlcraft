package vazkii.arl.item;

import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.interf.IVariantHolder;
import vazkii.arl.util.ProxyRegistry;

public abstract class ItemModTool extends ItemTool implements IVariantHolder {
   private final String[] variants;
   private final String bareName;

   protected ItemModTool(String name, float attackDamage, float speed, ToolMaterial material, Set<Block> effectiveBlocks, String... variants) {
      super(attackDamage, speed, material, effectiveBlocks);
      this.setUnlocalizedName(name);
      if (variants.length > 1) {
         this.setHasSubtypes(true);
      }

      if (variants.length == 0) {
         variants = new String[]{name};
      }

      this.bareName = name;
      this.variants = variants;
   }

   @Nonnull
   public Item setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.setRegistryName(new ResourceLocation(this.getPrefix() + name));
      ProxyRegistry.register(this);
      return this;
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack par1ItemStack) {
      int dmg = par1ItemStack.getItemDamage();
      String[] variants = this.getVariants();
      String name;
      if (dmg >= variants.length) {
         name = this.bareName;
      } else {
         name = variants[dmg];
      }

      return "item." + this.getPrefix() + name;
   }

   public String[] getVariants() {
      return this.variants;
   }
}
