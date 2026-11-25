package noppes.vc.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.EntityProjectile;
import noppes.vc.VCTabs;
import noppes.vc.VCWeapons;

public class ItemCrossbow extends ItemBasic {
   public ItemCrossbow() {
      this.setMaxDamage(129);
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
               return itemstack != null && itemstack.getItem() == ItemCrossbow.this ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
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

   public void onPlayerStoppedUsing(ItemStack stack, World worldObj, EntityLivingBase entity, int par4) {
      if (!entity.world.isRemote) {
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!stack.hasTagCompound() && !player.capabilities.isCreativeMode) {
               return;
            }

            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null) {
               stack.setTagCompound(compound = new NBTTagCompound());
            }

            if (compound.getInteger("IsLoaded") != 1 && !player.capabilities.isCreativeMode) {
               return;
            }

            if (compound.getInteger("Reloading") == 1 && !player.capabilities.isCreativeMode) {
               compound.setInteger("Reloading", 0);
               return;
            }

            if (!player.capabilities.isCreativeMode) {
               this.consumeItem(player, VCWeapons.crossbow_bolt);
            }

            compound.setInteger("IsLoaded", 0);
         }

         stack.damageItem(1, entity);
         EntityProjectile projectile = new EntityProjectile(worldObj, entity, new ItemStack(Items.ARROW, 1, 0));
         projectile.damage = 10.0F;
         projectile.setSpeed(20);
         projectile.setHasGravity(true);
         projectile.shoot(2.0F);
         worldObj.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, itemRand.nextFloat() * 0.3F + 0.8F);
         worldObj.spawnEntity(projectile);
      }
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
      if (!entity.world.isRemote && entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         int ticks = this.getMaxItemUseDuration(stack) - count;
         if (!player.capabilities.isCreativeMode && stack.hasTagCompound()) {
            if (stack.getTagCompound().getInteger("Reloading") == 1 && this.hasItem(player, VCWeapons.crossbow_bolt) && ticks == 20) {
               playSound(player, SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
               stack.getTagCompound().setInteger("IsLoaded", 1);
            }

         }
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      NBTTagCompound compound = itemstack.getTagCompound();
      if (compound == null) {
         compound = new NBTTagCompound();
      }

      if (!player.capabilities.isCreativeMode && this.hasItem(player, VCWeapons.crossbow_bolt) && compound.getInteger("IsLoaded") == 0) {
         compound.setInteger("Reloading", 1);
         itemstack.setTagCompound(compound);
      }

      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return stack.getTagCompound() != null && stack.getTagCompound().getInteger("Reloading") != 0 ? EnumAction.NONE : EnumAction.BOW;
   }
}
