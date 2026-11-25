package vazkii.arl.item;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IModBlock;
import vazkii.arl.interf.IVariantHolder;

public class ItemModBlock extends ItemBlock implements IVariantHolder {
   private IModBlock modBlock;

   public ItemModBlock(Block block, ResourceLocation res) {
      super(block);
      this.modBlock = (IModBlock)block;
      if (this.getVariants().length > 1) {
         this.setHasSubtypes(true);
      }

      this.setRegistryName(res);
   }

   public int getMetadata(int damage) {
      return damage;
   }

   @Nonnull
   public ItemBlock setTranslationKey(@Nonnull String par1Str) {
      return (ItemBlock)super.setUnlocalizedName(par1Str);
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack par1ItemStack) {
      int dmg = par1ItemStack.getItemDamage();
      String[] variants = this.getVariants();
      String name;
      if (dmg >= variants.length) {
         name = this.modBlock.getBareName();
      } else {
         name = variants[dmg];
      }

      return "tile." + this.getPrefix() + name;
   }

   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
      String[] variants = this.getVariants();
      if (this.isInCreativeTab(tab)) {
         for(int i = 0; i < variants.length; ++i) {
            if (this.modBlock.shouldDisplayVariant(i)) {
               subItems.add(new ItemStack(this, 1, i));
            }
         }
      }

   }

   public String[] getVariants() {
      return this.modBlock.getVariants();
   }

   @SideOnly(Side.CLIENT)
   public ItemMeshDefinition getCustomMeshDefinition() {
      return this.modBlock.getCustomMeshDefinition();
   }

   @Nonnull
   public EnumRarity getRarity(ItemStack stack) {
      return this.modBlock.getBlockRarity(stack);
   }

   public String getModNamespace() {
      return this.modBlock.getModNamespace();
   }
}
