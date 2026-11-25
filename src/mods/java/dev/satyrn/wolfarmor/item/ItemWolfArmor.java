package dev.satyrn.wolfarmor.item;

import com.google.common.collect.Multimap;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.api.item.IItemWolfArmor;
import dev.satyrn.wolfarmor.api.item.IWolfArmorMaterial;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class ItemWolfArmor extends Item implements IItemWolfArmor {
   private static final String NBT_TAG_DISPLAY = "Display";
   private static final String NBT_TAG_COLOR = "Color";
   private static final IBehaviorDispenseItem DISPENSE_ITEM = new BehaviorDefaultDispenseItem() {
      @Nonnull
      protected ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
         ItemStack itemStack = ItemWolfArmor.dispenseWolfArmor(source, stack);
         return !itemStack.isEmpty() ? itemStack : super.dispenseStack(source, stack);
      }
   };
   private final IWolfArmorMaterial material;

   public ItemWolfArmor(IWolfArmorMaterial material) {
      this.material = material;
      this.maxStackSize = 1;
      this.canRepair = true;
      this.setMaxDamage(material.getDurability());
      this.setCreativeTab(CreativeTabs.MISC);
      BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DISPENSE_ITEM);
   }

   public void removeColor(@Nonnull ItemStack stack) {
      if (!stack.isEmpty() && this.getMaterial().getCanBeDyed() && this.getHasColor(stack)) {
         NBTTagCompound stackCompound = stack.getTagCompound();
         if (stackCompound != null) {
            stackCompound.getCompoundTag("Display").removeTag("Color");
         }
      }

   }

   public IWolfArmorMaterial getMaterial() {
      return this.material;
   }

   public boolean getHasOverlay(@Nonnull ItemStack stack) {
      return this.getMaterial().getHasOverlay() || this.getColor(stack) != -1;
   }

   public boolean getHasColor(@Nonnull ItemStack stack) {
      if (!stack.isEmpty() && this.getMaterial().getCanBeDyed() && stack.hasTagCompound()) {
         NBTTagCompound tagCompound = stack.getTagCompound();
         if (tagCompound != null && tagCompound.hasKey("Display")) {
            NBTTagCompound display = tagCompound.getCompoundTag("Display");
            return display.hasKey("Color");
         }
      }

      return false;
   }

   public int getColor(@Nonnull ItemStack stack) {
      if (this.getMaterial().getCanBeDyed() && !stack.isEmpty()) {
         NBTTagCompound tagCompound = stack.getTagCompound();
         if (tagCompound != null) {
            NBTTagCompound display = tagCompound.getCompoundTag("Display");
            if (display.hasKey("Color")) {
               return display.getInteger("Color");
            }
         }

         return this.getMaterial().getDefaultColor();
      } else {
         return -1;
      }
   }

   public void setColor(@Nonnull ItemStack stack, int color) {
      if (!this.getMaterial().getCanBeDyed()) {
         throw new UnsupportedOperationException("Wolf armor material is not dyeable!");
      } else {
         if (!stack.isEmpty()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound == null) {
               tagCompound = new NBTTagCompound();
               stack.setTagCompound(tagCompound);
            }

            NBTTagCompound display = tagCompound.getCompoundTag("Display");
            if (!tagCompound.hasKey("Display")) {
               tagCompound.setTag("Display", display);
            }

            display.setInteger("Color", color);
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public double getDamageReductionAmount() {
      return this.getMaterial().getDamageReductionAmount();
   }

   @Nonnull
   private static ItemStack dispenseWolfArmor(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
      BlockPos blockPos = source.getBlockPos().offset((EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING));
      List<EntityWolf> wolves = source.getWorld().getEntitiesWithinAABB(EntityWolf.class, new AxisAlignedBB(blockPos));
      if (!wolves.isEmpty()) {
         EntityWolf wolf = null;

         for(EntityWolf entity : wolves) {
            if (entity.isTamed() && !entity.isChild()) {
               wolf = entity;
               break;
            }
         }

         if (wolf != null) {
            ItemStack copyStack = stack.copy();
            copyStack.setCount(1);
            IArmoredWolf wolfArmor = (IArmoredWolf)wolf;
            if (!wolfArmor.canEquipItem(copyStack)) {
               return stack;
            }

            wolfArmor.equipArmor(copyStack);
            stack.shrink(1);
         }
      }

      return stack;
   }

   public int getItemEnchantability() {
      return this.getMaterial().getEnchantability();
   }

   public boolean canApplyAtEnchantingTable(ItemStack itemStack, Enchantment enchantment) {
      if (enchantment == Enchantments.SWEEPING) {
         return false;
      } else {
         return enchantment.type == EnumEnchantmentType.BREAKABLE || enchantment.type == EnumEnchantmentType.WEAPON || enchantment.type == EnumEnchantmentType.ARMOR || enchantment.type == EnumEnchantmentType.ARMOR_FEET;
      }
   }

   public boolean getIsRepairable(@Nonnull ItemStack originalStack, @Nonnull ItemStack repairStack) {
      return this.getMaterial().getRepairItem() == repairStack.getItem() || super.getIsRepairable(originalStack, repairStack);
   }

   @Nonnull
   public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack stack) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);
      if (equipmentSlot == EntityEquipmentSlot.CHEST && !stack.isEmpty() && stack.getItem() instanceof ItemWolfArmor) {
         multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_UUID, "Armor modifier", this.getMaterial().getDamageReductionAmount(), 0));
         multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_UUID, "Armor toughness", (double)this.getMaterial().getToughness(), 0));
      }

      return multimap;
   }
}
