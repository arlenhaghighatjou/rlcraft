package betterquesting.api.placeholders;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPlaceholder extends Item {
   public static Item placeholder = new ItemPlaceholder();

   public ItemPlaceholder() {
      this.setUnlocalizedName("betterquesting.placeholder");
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (!stack.hasTagCompound()) {
         tooltip.add("ERROR: Original information missing!");
      } else {
         tooltip.add("Original ID: " + stack.getTagCompound().getString("orig_id") + "/" + stack.getTagCompound().getInteger("orig_meta"));
      }
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
      if (stack.hasTagCompound() && entity instanceof EntityPlayer && world.getTotalWorldTime() % 100L == 0L) {
         EntityPlayer player = (EntityPlayer)entity;
         NBTTagCompound tags = stack.getTagCompound();
         Item i = (Item)Item.REGISTRY.getObject(new ResourceLocation(tags.getString("orig_id")));
         int m = stack.getItemDamage() > 0 ? stack.getItemDamage() : tags.getInteger("orig_meta");
         NBTTagCompound t = tags.hasKey("orig_tag") ? tags.getCompoundTag("orig_tag") : null;
         if (i != null) {
            ItemStack converted = new ItemStack(i, stack.getCount(), m);
            converted.setTagCompound(t);
            player.inventory.setInventorySlotContents(slot, converted);
         }

      }
   }
}
