package com.mujmajnkraft.bettersurvival.items;

import com.google.common.collect.Multimap;
import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.entities.projectiles.EntityFlyingSpear;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSpear extends ItemCustomWeapon {
   private static final EaglercraftUUID REACH_MODIFIER_UUID = EaglercraftUUID.fromString("f14d3a86-ef0c-48a7-a59f-b6650e6132f5");
   private static final String REACH_MODIFIER_STRING = "bettersurvival:spear_reach";
   private final float reach;

   public ItemSpear(ToolMaterial material) {
      super(material, ForgeConfigHandler.weapons.spearDmgMod, ForgeConfigHandler.weapons.spearSpd);
      this.setRegistryName("mujmajnkraftsbettersurvival", "item" + material.name().toLowerCase() + "spear");
      this.setUnlocalizedName(material.name().toLowerCase() + "spear");
      this.maxStackSize = 16;
      this.setMaxDamage(0);
      this.reach = ForgeConfigHandler.weapons.spearReachBonus;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      boolean flag = playerIn.capabilities.isCreativeMode;
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (itemstack.getItem() instanceof ItemSpear || flag) {
         float f = 1.0F;
         if (!worldIn.isRemote) {
            EntityFlyingSpear entityspear = new EntityFlyingSpear(worldIn, playerIn);
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            entityspear.setSpear(itemstack1);
            entityspear.setShooter(playerIn);
            entityspear.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, f * 2.0F, 1.0F);
            ItemSpear spear = (ItemSpear)itemstack.getItem();
            entityspear.setDamage((double)spear.getAttackDamage());
            if (flag) {
               entityspear.pickupStatus = PickupStatus.CREATIVE_ONLY;
            } else {
               entityspear.pickupStatus = PickupStatus.ALLOWED;
            }

            worldIn.spawnEntity(entityspear);
         }

         worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
         if (!flag) {
            playerIn.getHeldItem(handIn).shrink(1);
         }

         StatBase stats = StatList.getObjectUseStats(this);
         if (stats != null) {
            playerIn.addStat(stats);
         }
      }

      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
      if (slot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_MODIFIER_UUID, "bettersurvival:spear_reach", (double)this.reach, 0));
      }

      return multimap;
   }

   public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      return false;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return false;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.NONE;
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      return super.hitEntity(stack, target, attacker);
   }

   public float breakChance() {
      return 32.0F / (float)this.getMaterial().getMaxUses();
   }

   public boolean isDamageable() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      String s = I18n.format("mujmajnkraftsbettersurvival.spear.desc", new Object[0]);
      tooltip.add(TextFormatting.AQUA + s);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }
}
