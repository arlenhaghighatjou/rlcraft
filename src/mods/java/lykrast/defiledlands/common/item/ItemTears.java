package lykrast.defiledlands.common.item;

import java.util.List;
import javax.annotation.Nullable;
import lykrast.defiledlands.common.init.ModItems;
import lykrast.defiledlands.common.util.LocUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public abstract class ItemTears extends Item {
   protected int warmup;
   protected int tickRate;

   public ItemTears(int durability, int warmup, int tickRate) {
      this.maxStackSize = 1;
      this.setMaxDamage(durability);
      this.warmup = 72000 - warmup;
      this.tickRate = tickRate;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 72000;
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
      if (count <= this.warmup && (this.warmup - count) % this.tickRate == 0) {
         this.fire(stack, player, this.warmup - count);
      }

   }

   protected abstract void fire(ItemStack var1, EntityLivingBase var2, int var3);

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      playerIn.setActiveHand(handIn);
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   public int getItemEnchantability() {
      return 1;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return repair.getItem() == ModItems.remorsefulGem;
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + I18n.format(super.getUnlocalizedName(stack) + ".tooltip", new Object[0])));
   }

   public static class Flame extends ItemTears {
      public Flame(int durability) {
         super(durability, 20, 5);
      }

      protected void fire(ItemStack stack, EntityLivingBase player, int time) {
         World world = player.world;
         world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!world.isRemote) {
            Vec3d vec3d = player.getLook(1.0F);
            EntitySmallFireball projectile = new EntitySmallFireball(world, player, 0.0D, 0.0D, 0.0D);
            projectile.accelerationX = vec3d.x * 0.2D;
            projectile.accelerationY = vec3d.y * 0.2D;
            projectile.accelerationZ = vec3d.z * 0.2D;
            projectile.posY = player.posY + (double)player.getEyeHeight();
            world.spawnEntity(projectile);
            if (time % 25 == 0) {
               stack.damageItem(1, player);
            }
         }

      }

      public boolean hasEffect(ItemStack stack) {
         return true;
      }
   }

   public static class Shulker extends ItemTears {
      public Shulker(int durability) {
         super(durability, 20, 20);
      }

      protected void fire(ItemStack stack, EntityLivingBase player, int time) {
         World world = player.world;
         if (!world.isRemote) {
            AxisAlignedBB aabb = player.getEntityBoundingBox().grow(16.0D, 8.0D, 16.0D);
            List<Entity> targets = world.getEntitiesInAABBexcluding(player, aabb, IMob.VISIBLE_MOB_SELECTOR);
            if (!targets.isEmpty()) {
               world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SHULKER_SHOOT, SoundCategory.NEUTRAL, 2.0F, (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2F + 1.0F);
               stack.damageItem(1, player);
            }

            for(Entity e : targets) {
               Axis axis = Axis.values()[itemRand.nextInt(3)];
               EntityShulkerBullet entityshulkerbullet = new EntityShulkerBullet(world, player, e, axis);
               entityshulkerbullet.posY += (double)player.getEyeHeight();
               world.spawnEntity(entityshulkerbullet);
            }
         }

      }

      public boolean hasEffect(ItemStack stack) {
         return true;
      }
   }
}
