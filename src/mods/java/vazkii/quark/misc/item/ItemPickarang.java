package vazkii.quark.misc.item;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.misc.feature.Pickarang;
import vazkii.quark.world.entity.EntityPickarang;

public class ItemPickarang extends ItemMod implements IQuarkItem {
   public ItemPickarang() {
      super("pickarang", new String[0]);
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.TOOLS);
      this.setHarvestLevel("pickaxe", Pickarang.harvestLevel);
      this.setHarvestLevel("axe", Pickarang.harvestLevel);
      this.setHarvestLevel("shovel", Pickarang.harvestLevel);
      if (Pickarang.durability > 0) {
         this.setMaxDamage(Pickarang.durability);
      }

   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      stack.damageItem(2, attacker);
      return true;
   }

   public boolean canHarvestBlock(IBlockState blockIn) {
      switch(Pickarang.harvestLevel) {
      case 0:
         return Items.WOODEN_PICKAXE.canHarvestBlock(blockIn) || Items.WOODEN_AXE.canHarvestBlock(blockIn) || Items.WOODEN_SHOVEL.canHarvestBlock(blockIn);
      case 1:
         return Items.STONE_PICKAXE.canHarvestBlock(blockIn) || Items.STONE_AXE.canHarvestBlock(blockIn) || Items.STONE_SHOVEL.canHarvestBlock(blockIn);
      case 2:
         return Items.IRON_PICKAXE.canHarvestBlock(blockIn) || Items.IRON_AXE.canHarvestBlock(blockIn) || Items.IRON_SHOVEL.canHarvestBlock(blockIn);
      default:
         return true;
      }
   }

   public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      if (state.getBlockHardness(worldIn, pos) != 0.0F) {
         stack.damageItem(1, entityLiving);
      }

      return true;
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      playerIn.setHeldItem(handIn, ItemStack.EMPTY);
      int eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemstack);
      worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, QuarkSounds.ENTITY_PICKARANG_THROW, SoundCategory.NEUTRAL, 0.5F + (float)eff * 0.14F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!worldIn.isRemote) {
         int slot = handIn == EnumHand.OFF_HAND ? playerIn.inventory.getSizeInventory() - 1 : playerIn.inventory.currentItem;
         EntityPickarang entity = new EntityPickarang(worldIn, playerIn);
         entity.setThrowData(slot, itemstack);
         entity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F + (float)eff * 0.325F, 0.0F);
         worldIn.spawnEntity(entity);
      }

      if (!playerIn.capabilities.isCreativeMode && !Pickarang.noCooldown) {
         int cooldown = 10 - eff;
         if (cooldown > 0) {
            playerIn.getCooldownTracker().setCooldown(this, cooldown);
         }
      }

      playerIn.addStat(StatList.getObjectUseStats(this));
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   @Nonnull
   public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
      if (slot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 2.0D, 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.8D, 0));
      }

      return multimap;
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      return 0.0F;
   }

   public boolean isRepairable() {
      return true;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return repair.getItem() == Items.DIAMOND;
   }

   public int getItemEnchantability() {
      return Items.DIAMOND_PICKAXE.getItemEnchantability();
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return super.canApplyAtEnchantingTable(stack, enchantment) || ImmutableSet.of(Enchantments.FORTUNE, Enchantments.SILK_TOUCH, Enchantments.EFFICIENCY).contains(enchantment);
   }
}
