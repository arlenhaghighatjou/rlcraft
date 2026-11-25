package noppes.vc.items;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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

public class ItemMusket extends ItemBasic {
   public ItemMusket() {
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
               return itemstack != null && itemstack.getItem() == ItemMusket.this ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
            }
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
            return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
         }
      });
      this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
         }
      });
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldObj, EntityLivingBase entity, int count) {
      if (!worldObj.isRemote) {
         if (entity instanceof EntityPlayer) {
            if (!stack.hasTagCompound()) {
               return;
            }

            EntityPlayer player = (EntityPlayer)entity;
            NBTTagCompound compound = stack.getTagCompound();
            if (!compound.getBoolean("IsLoaded2") && !player.capabilities.isCreativeMode) {
               playSound(player, VCSounds.GUN_EMPTY, 1.0F, 1.0F);
               return;
            }

            if (compound.getBoolean("Reloading2") && !player.capabilities.isCreativeMode) {
               compound.setBoolean("Reloading2", false);
               return;
            }

            if (!player.capabilities.isCreativeMode) {
               this.consumeItem(player, VCWeapons.bullet);
            }

            compound.setBoolean("IsLoaded2", false);
         }

         stack.damageItem(1, entity);
         EntityProjectile projectile = new EntityProjectile(worldObj, entity, new ItemStack(VCWeapons.bullet, 1, 0));
         projectile.damage = (float)VariedCommodities.MusketDamage;
         projectile.punch = VariedCommodities.MusketKnockback;
         projectile.setSpeed(50);
         projectile.shoot(2.0F);
         worldObj.spawnEntity(projectile);
         playSound(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 0.9F, itemRand.nextFloat() * 0.3F + 1.8F);
         playSound(entity, SoundEvents.ENTITY_LIGHTNING_THUNDER, 2.0F, itemRand.nextFloat() * 0.3F + 1.8F);
      }
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
      if (!entity.world.isRemote && entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         int ticks = this.getMaxItemUseDuration(stack) - count;
         if (!player.capabilities.isCreativeMode && stack.hasTagCompound() && stack.getTagCompound().getBoolean("Reloading2") && this.hasItem(player, VCWeapons.bullet) && ticks == 60) {
            playSound(player, VCSounds.GUN_AK47_LOAD, 1.0F, 1.0F);
            stack.setTagInfo("IsLoaded2", new NBTTagByte((byte)1));
         }

      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      NBTTagCompound compound = itemstack.getTagCompound();
      if (compound == null) {
         itemstack.setTagCompound(compound = new NBTTagCompound());
      }

      if (!player.capabilities.isCreativeMode && this.hasItem(player, VCWeapons.bullet) && !compound.getBoolean("IsLoaded2")) {
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
