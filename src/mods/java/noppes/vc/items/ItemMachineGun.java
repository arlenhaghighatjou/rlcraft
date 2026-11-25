package noppes.vc.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.EntityProjectile;
import noppes.vc.VCTabs;
import noppes.vc.VCWeapons;
import noppes.vc.VariedCommodities;
import noppes.vc.client.VCSounds;

public class ItemMachineGun extends ItemBasic {
   public ItemMachineGun() {
      this.setMaxDamage(80);
      this.setCreativeTab(VCTabs.WEAPONS);
      this.setFull3D();
      this.setMaxStackSize(1);
      this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
            if (entityIn == null) {
               return 0.0F;
            } else {
               ItemStack itemstack = entityIn.getActiveItemStack();
               return itemstack != null && itemstack.getItem() == ItemMachineGun.this ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
            }
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
            return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
         }
      });
   }

   public void onPlayerStoppedUsing(ItemStack stack, World par2World, EntityLivingBase entity, int count) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (!player.capabilities.isCreativeMode) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null) {
               stack.setTagCompound(compound = new NBTTagCompound());
            }

            int ticks = this.getMaxItemUseDuration(stack) - count;
            int shotsleft = compound.getInteger("ShotsLeft") - ticks / 6;
            if (compound.getBoolean("Reloading2")) {
               shotsleft = ticks / 5;
               if (ticks > 40) {
                  shotsleft = 8;
               }

               if (shotsleft > 1) {
                  compound.setInteger("ShotsLeft", shotsleft);
                  compound.setBoolean("Reloading2", false);
               }
            } else if (shotsleft <= 0) {
               compound.setBoolean("Reloading2", true);
               stack.damageItem(1, player);
            } else {
               compound.setInteger("ShotsLeft", shotsleft);
            }

         }
      }
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
      if (!entity.world.isRemote) {
         int ticks = this.getMaxItemUseDuration(stack) - count;
         if (ticks % 6 == 0) {
            if (entity instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)entity;
               NBTTagCompound compound = stack.getTagCompound();
               if (compound == null) {
                  stack.setTagCompound(compound = new NBTTagCompound());
               }

               int shotsleft = compound.getInteger("ShotsLeft") - ticks / 6;
               if (!player.capabilities.isCreativeMode) {
                  if (compound.getBoolean("Reloading2") && this.hasItem(player, VCWeapons.bullet)) {
                     if (ticks > 0 && ticks <= 24) {
                        playSound(player, VCSounds.GUN_AK47_LOAD, 1.0F, 1.0F);
                     }

                     return;
                  }

                  if (shotsleft <= 0 || !this.hasItem(player, VCWeapons.bullet)) {
                     playSound(player, VCSounds.GUN_EMPTY, 1.0F, 1.0F);
                     return;
                  }
               }

               if (!player.capabilities.isCreativeMode) {
                  this.consumeItem(player, VCWeapons.bullet);
               }

               compound.removeTag("Reloading2");
            }

            EntityProjectile projectile = new EntityProjectile(entity.world, entity, new ItemStack(VCWeapons.bullet, 1, 0));
            projectile.damage = (float)VariedCommodities.MachineGunDamage;
            projectile.setSpeed(40);
            projectile.shoot(2.0F);
            playSound(entity, VCSounds.GUN_PISTAL_SHOT, 0.9F, itemRand.nextFloat() * 0.3F + 0.8F);
            entity.world.spawnEntity(projectile);
         }
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (!player.capabilities.isCreativeMode && !this.hasItem(player, VCWeapons.bullet)) {
         itemstack.setTagInfo("Reloading2", new NBTTagByte((byte)1));
      }

      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound().getBoolean("Reloading2") ? EnumAction.NONE : EnumAction.BOW;
   }
}
