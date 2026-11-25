package cursedflames.bountifulbaubles.item.throwable;

import cursedflames.bountifulbaubles.entity.EntityTerrariaThrowable;
import cursedflames.bountifulbaubles.item.base.GenericItemBB;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public abstract class ItemTerrariaThrowable extends GenericItemBB {
   public final int cooldownTicks;
   protected float velocity = 0.6F;
   protected float inaccuracy = 0.0F;

   public ItemTerrariaThrowable(String name, CreativeTabs tab, int useSpeed) {
      super(name, tab);
      this.setMaxStackSize(16);
      this.cooldownTicks = useSpeed;
   }

   public abstract EntityTerrariaThrowable getThrownEntity(World var1, EntityPlayer var2);

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (playerIn.capabilities.isCreativeMode) {
         itemstack.copy();
      } else {
         itemstack.splitStack(1);
      }

      worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      playerIn.getCooldownTracker().setCooldown(this, this.cooldownTicks);
      if (!worldIn.isRemote) {
         EntityTerrariaThrowable entityThrow = this.getThrownEntity(worldIn, playerIn);
         entityThrow.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, this.velocity, this.inaccuracy);
         worldIn.spawnEntity(entityThrow);
      }

      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }
}
