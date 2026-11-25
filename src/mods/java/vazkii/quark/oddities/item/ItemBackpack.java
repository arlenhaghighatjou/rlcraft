package vazkii.quark.oddities.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.item.ItemModArmor;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.handler.ProxiedItemStackHandler;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.oddities.client.model.ModelBackpack;
import vazkii.quark.oddities.feature.Backpacks;

public class ItemBackpack extends ItemModArmor implements IQuarkItem, IItemColorProvider {
   private static final String WORN_TEXTURE = "quark:textures/misc/backpack_worn.png";
   private static final String WORN_OVERLAY_TEXTURE = "quark:textures/misc/backpack_worn_overlay.png";
   @SideOnly(Side.CLIENT)
   public static ModelBiped model;

   public ItemBackpack() {
      super("backpack", ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST);
      this.setCreativeTab(CreativeTabs.TOOLS);
      this.setMaxDamage(0);
      this.addPropertyOverride(new ResourceLocation("has_items"), (stack, world, entity) -> {
         return !Backpacks.superOpMode && doesBackpackHaveItems(stack) ? 1.0F : 0.0F;
      });
   }

   public static boolean doesBackpackHaveItems(ItemStack stack) {
      IItemHandler handler = (IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      if (handler == null) {
         return false;
      } else {
         for(int i = 0; i < handler.getSlots(); ++i) {
            if (!handler.getStackInSlot(i).isEmpty()) {
               return true;
            }
         }

         return false;
      }
   }

   @Nonnull
   public Multimap<String, AttributeModifier> getItemAttributeModifiers(@Nonnull EntityEquipmentSlot equipmentSlot) {
      return HashMultimap.create();
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!worldIn.isRemote) {
         boolean hasItems = !Backpacks.superOpMode && doesBackpackHaveItems(stack);
         Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
         boolean isCursed = enchants.containsKey(Enchantments.BINDING_CURSE);
         boolean changedEnchants = false;
         if (hasItems) {
            if (Backpacks.isEntityWearingBackpack(entityIn, stack)) {
               if (!isCursed) {
                  enchants.put(Enchantments.BINDING_CURSE, Integer.valueOf(1));
                  changedEnchants = true;
               }
            } else {
               ItemStack copy = stack.copy();
               stack.setCount(0);
               entityIn.entityDropItem(copy, 0.0F);
            }
         } else if (isCursed) {
            enchants.remove(Enchantments.BINDING_CURSE);
            changedEnchants = true;
         }

         if (changedEnchants) {
            EnchantmentHelper.setEnchantments(enchants, stack);
         }

      }
   }

   public boolean onEntityItemUpdate(EntityItem entityItem) {
      if (!Backpacks.superOpMode && !entityItem.world.isRemote) {
         ItemStack stack = entityItem.getItem();
         if (!ItemNBTHelper.detectNBT(stack)) {
            return false;
         } else {
            IItemHandler handler = (IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
            if (handler == null) {
               return false;
            } else {
               for(int i = 0; i < handler.getSlots(); ++i) {
                  ItemStack stackAt = handler.getStackInSlot(i);
                  if (!stackAt.isEmpty()) {
                     ItemStack copy = stackAt.copy();
                     InventoryHelper.spawnItemStack(entityItem.world, entityItem.posX, entityItem.posY, entityItem.posZ, copy);
                  }
               }

               NBTTagCompound comp = ItemNBTHelper.getNBT(stack);
               comp.removeTag("Inventory");
               if (comp.getSize() == 0) {
                  stack.setTagCompound((NBTTagCompound)null);
               }

               return false;
            }
         }
      } else {
         return false;
      }
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return type != null && type.equals("overlay") ? "quark:textures/misc/backpack_worn_overlay.png" : "quark:textures/misc/backpack_worn.png";
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      if (model == null) {
         model = new ModelBackpack();
      }

      return model;
   }

   public boolean hasEffect(ItemStack stack) {
      return false;
   }

   @Nonnull
   public IRarity getForgeRarity(@Nonnull ItemStack stack) {
      return EnumRarity.COMMON;
   }

   public boolean isEnchantable(@Nonnull ItemStack stack) {
      return false;
   }

   @Nonnull
   public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound oldCapNbt) {
      ProxiedItemStackHandler handler = new ProxiedItemStackHandler(stack, 27);
      if (oldCapNbt != null && oldCapNbt.hasKey("Parent")) {
         NBTTagCompound itemData = oldCapNbt.getCompoundTag("Parent");
         ItemStackHandler stacks = new ItemStackHandler();
         stacks.deserializeNBT(itemData);

         for(int i = 0; i < stacks.getSlots(); ++i) {
            handler.setStackInSlot(i, stacks.getStackInSlot(i));
         }

         oldCapNbt.removeTag("Parent");
      }

      return handler;
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return (stack, i) -> {
         return i == 1 ? ((ItemArmor)stack.getItem()).getColor(stack) : -1;
      };
   }
}
