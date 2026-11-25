package svenhjol.charm.crafting.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import svenhjol.charm.crafting.feature.SuspiciousSoup;
import svenhjol.charm.crafting.potion.SuspiciousEffects;
import svenhjol.meson.iface.IMesonItem;

public class ItemSuspiciousSoup extends ItemFood implements IMesonItem, IMesonItem.IItemCustomModel {
   public ItemSuspiciousSoup() {
      super(SuspiciousSoup.heal, (float)SuspiciousSoup.saturation, false);
      this.register("suspicious_soup");
      this.setMaxStackSize(SuspiciousSoup.maxStackSize);
      this.setCreativeTab(CreativeTabs.FOOD);
      this.setHasSubtypes(true);
   }

   public String getModId() {
      return "charm";
   }

   public void registerModels(Item item) {
      for(int i = 0; i < SuspiciousSoup.maxTypes; ++i) {
         if (item != null && item.getRegistryName() != null) {
            ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
            ModelLoader.setCustomModelResourceLocation(item, i, loc);
         }
      }

   }

   protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
      if (!world.isRemote) {
         int meta = stack.getMetadata();
         ((SuspiciousEffects.CustomEffect)SuspiciousSoup.effects.get(meta)).performEffect(player, SuspiciousSoup.duration * 20, SuspiciousSoup.amplifier);
      }

   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      super.onItemUseFinish(stack, worldIn, entityLiving);
      return new ItemStack(Items.BOWL);
   }
}
