package noppes.vc.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.EntityProjectile;
import noppes.vc.VCTabs;

public class ItemSlingshot extends ItemBasic {
   public ItemSlingshot() {
      this.setMaxDamage(384);
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
               return itemstack != null && itemstack.getItem() == ItemSlingshot.this ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
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

   public void onPlayerStoppedUsing(ItemStack par1ItemStack, World worldObj, EntityLivingBase entity, int par4) {
      if (!worldObj.isRemote) {
         int ticks = this.getMaxItemUseDuration(par1ItemStack) - par4;
         if (ticks >= 6) {
            if (entity instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)entity;
               if (!player.capabilities.isCreativeMode && !this.consumeItem(player, Item.getItemFromBlock(Blocks.COBBLESTONE))) {
                  return;
               }
            }

            par1ItemStack.damageItem(1, entity);
            EntityProjectile projectile = new EntityProjectile(worldObj, entity, new ItemStack(Blocks.COBBLESTONE));
            projectile.damage = 4.0F;
            projectile.punch = 1;
            projectile.setRotating(true);
            if (ticks > 24) {
               projectile.punch = 2;
            }

            projectile.setHasGravity(true);
            projectile.setSpeed(14);
            projectile.shoot(1.0F);
            playSound(entity, SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, itemRand.nextFloat() * 0.3F + 0.8F);
            worldObj.spawnEntity(projectile);
         }
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public EnumAction getItemUseAction(ItemStack par1ItemStack) {
      return EnumAction.BOW;
   }
}
