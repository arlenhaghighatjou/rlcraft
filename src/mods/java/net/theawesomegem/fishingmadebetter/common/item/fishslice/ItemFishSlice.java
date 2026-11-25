package net.theawesomegem.fishingmadebetter.common.item.fishslice;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.theawesomegem.fishingmadebetter.common.registry.FMBCreativeTab;

public abstract class ItemFishSlice extends ItemFood {
   public ItemFishSlice(String name, int healAmount, float saturation) {
      super(healAmount, saturation, false);
      this.setCreativeTab(FMBCreativeTab.instance);
      this.setRegistryName(name);
      this.setUnlocalizedName("fishingmadebetter." + name);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      String fishDisplayName = this.getFishDisplayName(stack);
      if (fishDisplayName != null && fishDisplayName.length() > 0) {
         tooltip.add(I18n.format("item.fishingmadebetter.fish_slice_raw.tooltip", new Object[0]) + " " + fishDisplayName);
      }

   }

   @SideOnly(Side.CLIENT)
   public void registerItemModel(Item item) {
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("fishingmadebetter:fish_slice/" + this.getRegistryName().getResourcePath(), "inventory"));
   }

   public ItemStack getItemStack(String itemId, String displayName, int amount) {
      ItemStack itemStack = new ItemStack(this, amount);
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("FishItemId", itemId);
      if (displayName != null) {
         nbt.setString("FishDisplayName", displayName);
      }

      itemStack.setTagCompound(nbt);
      return itemStack;
   }

   @Nullable
   private String getFishDisplayName(ItemStack itemStack) {
      if (!itemStack.hasTagCompound()) {
         return null;
      } else {
         return !itemStack.getTagCompound().hasKey("FishDisplayName") ? null : itemStack.getTagCompound().getString("FishDisplayName");
      }
   }

   @Nullable
   private String getFishItemId(ItemStack itemStack) {
      if (!itemStack.hasTagCompound()) {
         return null;
      } else {
         return !itemStack.getTagCompound().hasKey("FishItemId") ? null : itemStack.getTagCompound().getString("FishItemId");
      }
   }
}
