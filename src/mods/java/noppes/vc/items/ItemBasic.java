package noppes.vc.items;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import noppes.vc.VCTabs;

public class ItemBasic extends Item {
   private boolean damageAble = true;

   public ItemBasic() {
      this.setCreativeTab(VCTabs.ITEMS);
   }

   public void setUnDamageable() {
      this.damageAble = false;
   }

   public Item register(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName(new ResourceLocation("variedcommodities", name));
      return this;
   }

   public static void playSound(Entity entity, SoundEvent sound, float volume, float pitch) {
      if (entity.world.isRemote) {
         entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, sound, SoundCategory.NEUTRAL, volume, pitch);
      } else if (sound != null && sound.getRegistryName() != null) {
         SPacketCustomSound packet = new SPacketCustomSound(sound.getRegistryName().toString(), SoundCategory.NEUTRAL, entity.posX, entity.posY, entity.posZ, volume, pitch);
         Optional.ofNullable(entity.getServer()).ifPresent((server) -> {
            server.getPlayerList().sendToAllNearExcept((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, volume > 1.0F ? (double)(16.0F * volume) : 16.0D, entity.world.provider.getDimension(), packet);
         });
      }

   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         subItems.add(new ItemStack(this, 1, 0));
      }

   }

   public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLiving, EntityLivingBase par3EntityLiving) {
      if (par2EntityLiving.getHealth() <= 0.0F) {
         return false;
      } else {
         if (this.damageAble) {
            par1ItemStack.damageItem(1, par3EntityLiving);
         }

         return true;
      }
   }

   public boolean hasItem(EntityPlayer player, Item item) {
      return this.getItemStack(player, item) != null;
   }

   public boolean consumeItem(EntityPlayer player, Item item) {
      ItemStack itemstack = this.getItemStack(player, item);
      if (itemstack == null) {
         return false;
      } else {
         itemstack.shrink(1);
         if (itemstack.getCount() == 0) {
            player.inventory.deleteStack(itemstack);
         }

         return true;
      }
   }

   private ItemStack getItemStack(EntityPlayer player, Item item) {
      if (player.getHeldItem(EnumHand.OFF_HAND).getItem() == item) {
         return player.getHeldItem(EnumHand.OFF_HAND);
      } else if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == item) {
         return player.getHeldItem(EnumHand.MAIN_HAND);
      } else {
         for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (itemstack.getItem() == item) {
               return itemstack;
            }
         }

         return null;
      }
   }
}
