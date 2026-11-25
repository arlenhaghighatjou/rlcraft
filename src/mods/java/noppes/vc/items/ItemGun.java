package noppes.vc.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.EntityProjectile;
import noppes.vc.VCTabs;
import noppes.vc.VCWeapons;
import noppes.vc.client.VCSounds;
import noppes.vc.enchants.VCEnchant;

public class ItemGun extends ItemBasic {
   private VCToolMaterial material;

   public ItemGun(VCToolMaterial material) {
      this.material = material;
      this.setMaxDamage(material.getMaxUses());
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
               return itemstack != null && itemstack.getItem() == ItemGun.this ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
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

   public boolean isEnchantable(ItemStack stack) {
      return true;
   }

   public void onPlayerStoppedUsing(final ItemStack stack, World worldObj, EntityLivingBase entity, int par4) {
      if (!worldObj.isRemote) {
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!this.hasBullet(player, stack)) {
               playSound(player, VCSounds.GUN_EMPTY, 1.0F, 1.0F);
               return;
            }

            int ticks = this.getMaxItemUseDuration(stack) - par4;
            if (ticks < 10) {
               return;
            }

            stack.damageItem(1, player);
            if (!player.capabilities.isCreativeMode && !this.hasInfinite(stack)) {
               this.consumeItem(player, VCWeapons.bullet);
            }
         }

         int damage = (this.material.getDamageVsEntity() + 1) / 2 + 5;
         damage = (int)((float)damage + (float)(damage * VCEnchant.getLevel(VCEnchant.Damage, stack)) * 0.5F);
         EntityProjectile projectile = new EntityProjectile(worldObj, entity, new ItemStack(VCWeapons.bullet));
         projectile.damage = (float)damage;
         projectile.callback = new EntityProjectile.IProjectileCallback() {
            public boolean onImpact(EntityProjectile entityProjectile, BlockPos pos, Entity entity) {
               if (entity != null && entity instanceof EntityLivingBase) {
                  EntityLivingBase living = (EntityLivingBase)entity;
                  int confusion = VCEnchant.getLevel(VCEnchant.Confusion, stack);
                  if (confusion > 0 && living.getRNG().nextInt(4) < confusion) {
                     living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100));
                  }

                  int poison = VCEnchant.getLevel(VCEnchant.Poison, stack);
                  if (poison > 0 && living.getRNG().nextInt(4) < poison) {
                     living.addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
                  }

                  return false;
               } else {
                  return false;
               }
            }
         };
         projectile.setSpeed(40);
         projectile.shoot((float)(this.material.getDamageVsEntity() + 1));
         playSound(entity, VCSounds.GUN_PISTAL_SHOT, 1.0F, itemRand.nextFloat() * 0.3F + 0.8F);
         worldObj.spawnEntity(projectile);
      }
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
      int ticks = this.getMaxItemUseDuration(stack) - count;
      if (ticks == 8 && !player.world.isRemote) {
         playSound(player, VCSounds.GUN_PISTAL_TRIGGER, 1.0F, 1.0F / (player.world.rand.nextFloat() * 0.4F + 0.8F));
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

   private boolean hasBullet(EntityPlayer player, ItemStack stack) {
      return !player.capabilities.isCreativeMode && !this.hasInfinite(stack) ? this.hasItem(player, VCWeapons.bullet) : true;
   }

   public boolean hasInfinite(ItemStack stack) {
      return VCEnchant.getLevel(VCEnchant.Infinite, stack) > 0;
   }

   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public EnumAction getItemUseAction(ItemStack par1ItemStack) {
      return EnumAction.BOW;
   }
}
