package vazkii.quark.misc.item;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import vazkii.arl.interf.IVariantHolder;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.item.IQuarkItem;

public class ItemModArrow extends ItemArrow implements IVariantHolder, IQuarkItem {
   private final String bareName;
   private final ItemModArrow.ArrowProvider provider;

   public ItemModArrow(String name, ItemModArrow.ArrowProvider provider) {
      this.setUnlocalizedName(name);
      this.bareName = name;
      this.provider = provider;
      ItemMod.variantHolders.add(this);
   }

   @Nonnull
   public EntityArrow createArrow(@Nonnull World worldIn, @Nonnull ItemStack stack, EntityLivingBase shooter) {
      return this.provider == null ? super.createArrow(worldIn, stack, shooter) : this.provider.provide(worldIn, stack, shooter);
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player) {
      return false;
   }

   @Nonnull
   public Item setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.setRegistryName(new ResourceLocation("quark:" + name));
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

      return "item.quark:" + name;
   }

   public String[] getVariants() {
      return new String[]{this.bareName};
   }

   public ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }

   public interface ArrowProvider {
      EntityArrow provide(World var1, ItemStack var2, EntityLivingBase var3);
   }
}
