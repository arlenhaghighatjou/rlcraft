package org.orecruncher.dsurround.registry.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lain.mods.cos.api.CosArmorAPI;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.client.sound.Sounds;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;
import org.orecruncher.dsurround.registry.RegistryManager;

@SideOnly(Side.CLIENT)
public enum ItemClass {
   EMPTY((SoundEffect)null),
   NONE((SoundEffect)null, (SoundEffect)null, Sounds.UTILITY_EQUIP, false),
   LEATHER(Sounds.LEATHER_ARMOR_EQUIP, true),
   CHAIN(Sounds.CHAIN_ARMOR_EQUIP, true),
   CRYSTAL(Sounds.CRYSTAL_ARMOR_EQUIP, true),
   PLATE(Sounds.PLATE_ARMOR_EQUIP, true),
   SHIELD(Sounds.TOOL_SWING, Sounds.SHIELD_USE, Sounds.SHIELD_EQUIP, false),
   SWORD(Sounds.SWORD_SWING, (SoundEffect)null, ModOptions.sound.swordEquipAsTool ? Sounds.TOOL_EQUIP : Sounds.SWORD_EQUIP, false),
   AXE(Sounds.AXE_SWING, (SoundEffect)null, Sounds.AXE_EQUIP, false),
   BOW(Sounds.TOOL_SWING, Sounds.BOW_PULL, Sounds.BOW_EQUIP, false),
   TOOL(Sounds.TOOL_SWING, (SoundEffect)null, Sounds.TOOL_EQUIP, false),
   FOOD((SoundEffect)null, (SoundEffect)null, Sounds.FOOD_EQUIP, false),
   BOOK(Sounds.BOOK_EQUIP, Sounds.BOOK_EQUIP, Sounds.BOOK_EQUIP, false),
   POTION(Sounds.POTION_EQUIP, Sounds.POTION_EQUIP, Sounds.POTION_EQUIP, false);

   private final SoundEffect swing;
   private final SoundEffect use;
   private final SoundEffect equip;
   private final boolean isArmor;

   private ItemClass(@Nullable SoundEffect sound) {
      this(sound, sound, sound, false);
   }

   private ItemClass(@Nullable SoundEffect sound, boolean isArmor) {
      this(sound, sound, sound, isArmor);
   }

   private ItemClass(@Nullable SoundEffect swing, @Nullable SoundEffect use, @Nullable SoundEffect equip, boolean isArmor) {
      this.swing = swing;
      this.use = use;
      this.equip = equip;
      this.isArmor = isArmor;
   }

   @Nullable
   SoundEffect getSwingSound() {
      return this.swing;
   }

   @Nullable
   SoundEffect getUseSound() {
      return this.use;
   }

   @Nullable
   SoundEffect getEquipSound() {
      return this.equip;
   }

   public boolean isArmor() {
      return this.isArmor;
   }

   @Nonnull
   private static ItemStack resolveSlot(@Nonnull EntityLivingBase e, @Nonnull EntityEquipmentSlot slot) {
      if (ModEnvironment.CosmeticArmorReworked.isLoaded()) {
         CAStacksBase slots = CosArmorAPI.getCAStacksClient(e.getPersistentID());
         if (slots != null) {
            ItemStack stack = slots.getStackInSlot(slot.getIndex());
            if (!stack.isEmpty()) {
               return stack;
            }
         }
      }

      return e.getItemStackFromSlot(slot);
   }

   public static ItemStack effectiveArmorStack(@Nonnull EntityLivingBase entity) {
      ItemStack chest = resolveSlot(entity, EntityEquipmentSlot.CHEST);
      ItemStack legs = resolveSlot(entity, EntityEquipmentSlot.LEGS);
      ItemClass chestic = RegistryManager.ITEMS.getItemClass(chest).getItemClass();
      ItemClass legsic = RegistryManager.ITEMS.getItemClass(legs).getItemClass();
      return chestic.compareTo(legsic) > 0 ? chest.copy() : legs.copy();
   }

   public static ItemStack footArmorStack(@Nonnull EntityLivingBase entity) {
      return resolveSlot(entity, EntityEquipmentSlot.FEET);
   }
}
