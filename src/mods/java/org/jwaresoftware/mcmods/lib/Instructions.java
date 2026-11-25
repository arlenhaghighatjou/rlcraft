package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public final class Instructions {
   public Instructions() {
   }

   @Nonnull
   public static final NBTTagCompound createshell(@Nonnull ItemStack stack) {
      return ItemStacks.getTagCompoundNonNull(stack, "_Hord_Instructions");
   }

   @Nonnull
   public static final NBTTagCompound createshell(@Nonnull ItemStack stack, @Nonnull String modid) {
      NBTTagCompound nbt_instr = ItemStacks.getTagCompoundNonNull(stack, "_Hord_Instructions");
      if (!modid.isEmpty()) {
         nbt_instr.setString("ModId", modid);
      }

      return nbt_instr;
   }

   @Nonnull
   public static final NBTTagCompound create(@Nonnull ItemStack stack, int currentVersion) {
      Validate.isTrue(currentVersion > 0 && currentVersion < 128, "Invalid version", new Object[0]);
      NBTTagCompound nbt_instr = ItemStacks.getTagCompoundNonNull(stack, "_Hord_Instructions");
      nbt_instr.setByte("Version", (byte)currentVersion);
      return nbt_instr;
   }

   @Nonnull
   public static final NBTTagCompound create(@Nonnull ItemStack stack, int currentVersion, @Nonnull String modid) {
      assert modid != null;

      NBTTagCompound nbt_instr = create(stack, currentVersion);
      if (!modid.isEmpty()) {
         nbt_instr.setString("ModId", modid);
      }

      return nbt_instr;
   }

   public static final boolean present(@Nullable ItemStack stack) {
      return !ItemStacks.isEmpty(stack) ? stack.getSubCompound("_Hord_Instructions") != null : false;
   }

   @Nullable
   public static final NBTTagCompound get(@Nullable ItemStack stack) {
      NBTTagCompound nbt_instr = null;
      if (!ItemStacks.isEmpty(stack)) {
         nbt_instr = stack.getSubCompound("_Hord_Instructions");
      }

      return nbt_instr;
   }

   @Nullable
   public static final NBTTagCompound get(@Nullable ItemStack stack, int requiredVersion, boolean olderOk) {
      Validate.isTrue(requiredVersion > 0 && requiredVersion < 128, "Invalid version (not allowed to be lte 0", new Object[0]);
      NBTTagCompound nbt_instr = null;
      NBTTagCompound tst_instr = get(stack);
      if (tst_instr != null) {
         if (tst_instr.hasKey("Version", 1)) {
            byte v = tst_instr.getByte("Version");
            if (v != 0 && (requiredVersion == v || olderOk && v < requiredVersion)) {
               nbt_instr = tst_instr;
            }
         } else if (olderOk && 0 <= requiredVersion) {
            nbt_instr = tst_instr;
         }
      }

      return nbt_instr;
   }

   @Nullable
   public static final NBTTagCompound getraw(@Nullable ItemStack stack) {
      NBTTagCompound nbt_instr = null;
      if (stack != null && stack != ItemStack.EMPTY) {
         nbt_instr = stack.getSubCompound("_Hord_Instructions");
      }

      return nbt_instr;
   }

   public static final void remove(@Nonnull ItemStack stack) {
      stack.removeSubCompound("_Hord_Instructions");
   }

   public static final boolean copyinit(@Nonnull ItemStack from, @Nonnull ItemStack to) {
      boolean changed = false;
      NBTTagCompound nbt_instr = get(from);
      to.removeSubCompound("_Hord_Instructions");
      if (nbt_instr != null) {
         NBTTagCompound cpy_instr = nbt_instr.copy();
         to.setTagInfo("_Hord_Instructions", cpy_instr);
         changed = true;
      }

      return changed;
   }

   public static final boolean copyinit(@Nonnull ItemStack from, @Nonnull ItemStack to, int currentVersion) {
      boolean changed = copyinit(from, to);
      if (changed) {
         Validate.isTrue(currentVersion > 0 && currentVersion < 128, "Invalid version", new Object[0]);
         get(to).setByte("Version", (byte)currentVersion);
      } else {
         create(to, currentVersion);
         changed = true;
      }

      return changed;
   }

   public static final boolean setVersion(@Nonnull ItemStack to, int currentVersion) {
      Validate.isTrue(currentVersion > 0 && currentVersion < 128, "Invalid version", new Object[0]);
      NBTTagCompound nbt_instr = getraw(to);
      if (nbt_instr != null) {
         nbt_instr.setByte("Version", (byte)currentVersion);
      }

      return nbt_instr != null;
   }

   public static final boolean isCustomFor(@Nullable ItemStack stack, @Nonnull String modid) {
      assert modid != null;

      NBTTagCompound instr = get(stack);
      return instr != null && modid.equals(instr.getString("ModId"));
   }

   public static final boolean isXEnchanted(@Nullable ItemStack stack, @Nonnull String modid) {
      assert modid != null;

      NBTTagCompound nbt_instr = get(stack);
      return nbt_instr != null && modid.equals(nbt_instr.getString("ModId")) && nbt_instr.getInteger("PrizeEnchant") > 0;
   }

   public static final boolean isLooted(@Nullable ItemStack stack, @Nullable String modid) {
      boolean is = false;
      NBTTagCompound nbt_instr = get(stack);
      if (nbt_instr != null && (modid == null || modid.equals(nbt_instr.getString("ModId")))) {
         is = nbt_instr.getBoolean("Looted");
      }

      return is;
   }

   public static final boolean isLooted(@Nullable ItemStack stack) {
      return isLooted(stack, (String)null);
   }

   public static final boolean isUnvanishing(@Nullable ItemStack stack, @Nonnull String modid) {
      assert modid != null;

      NBTTagCompound nbt_instr = get(stack);
      return isXEnchanted(stack, modid) && nbt_instr.getBoolean("Unvanishing");
   }

   public static final boolean hasXGearFlag(@Nullable ItemStack stack, @Nonnull String modid) {
      assert modid != null;

      NBTTagCompound nbt_instr = get(stack);
      return isXEnchanted(stack, modid) && nbt_instr.getBoolean("XGear");
   }

   public static final boolean hasXFoodFlag(@Nullable ItemStack stack) {
      NBTTagCompound nbt_instr = get(stack);
      return nbt_instr != null && nbt_instr.hasKey("XFood") && nbt_instr.getBoolean("XFood");
   }

   public static final boolean hasLootTable(@Nullable ItemStack stack, boolean topLevel) {
      NBTTagCompound nbt = get(stack);
      if (nbt != null && nbt.hasKey(SharedGlue.DEFAULT_LOOT_TABLE_TAGNAME(), 8)) {
         return true;
      } else {
         nbt = stack.getTagCompound();
         return topLevel && nbt != null && (nbt.hasKey(SharedGlue.DEFAULT_LOOT_TABLE_TAGNAME()) || nbt.hasKey("withLoot"));
      }
   }

   public static final boolean hasUnrepairableFlag(@Nullable ItemStack stack) {
      NBTTagCompound nbt_instr = get(stack);
      return nbt_instr != null && nbt_instr.hasKey("Unrepairable") && nbt_instr.getBoolean("Unrepairable");
   }

   public static final int getInteger(@Nullable ItemStack stack, @Nonnull String tagid, int valueIfAbsent) {
      NBTTagCompound nbt_instr = get(stack);
      return nbt_instr != null && nbt_instr.hasKey(tagid, 99) ? nbt_instr.getInteger(tagid) : valueIfAbsent;
   }

   static final NBTTagCompound setInteger_(@Nonnull ItemStack stack, @Nonnull String tagid, int value) {
      NBTTagCompound nbt_instr = createshell(stack);
      nbt_instr.setInteger(tagid, value);
      return nbt_instr;
   }

   public static final int setInteger(@Nullable ItemStack stack, @Nonnull String tagid, int value, int nullValue) {
      int oldValue = getInteger(stack, tagid, nullValue);
      setInteger_(stack, tagid, value);
      return oldValue;
   }

   public static final int getIntegerRaw(@Nullable ItemStack stack, @Nonnull String tagid, int valueIfAbsent) {
      NBTTagCompound nbt_instr = getraw(stack);
      return nbt_instr != null && nbt_instr.hasKey(tagid, 99) ? nbt_instr.getInteger(tagid) : valueIfAbsent;
   }

   public static final boolean getFlag(@Nullable ItemStack stack, @Nonnull String tagid) {
      NBTTagCompound nbt_instr = get(stack);
      return nbt_instr != null && nbt_instr.hasKey(tagid, 1) ? nbt_instr.getBoolean(tagid) : false;
   }

   public static final boolean setFlag(@Nullable ItemStack stack, @Nonnull String tagid, Boolean value) {
      boolean oldValue = getFlag(stack, tagid);
      NBTTagCompound nbt_instr = createshell(stack);
      if (value == null) {
         nbt_instr.removeTag(tagid);
      } else {
         nbt_instr.setBoolean(tagid, Boolean.TRUE.equals(value));
      }

      return oldValue;
   }

   public static final int getUsed(@Nullable ItemStack stack, int valueIfAbsent) {
      return getInteger(stack, "Used", valueIfAbsent);
   }

   public static final int getCheckedUsed(@Nullable ItemStack stack, int valueIfAbsent, int maxValue) {
      int n = getUsed(stack, valueIfAbsent);
      if (n < 0) {
         n = valueIfAbsent;
      }

      return Math.min(n, maxValue);
   }

   public static final void setUsed(@Nonnull ItemStack stack, int value, int capacityIfAbsent) {
      NBTTagCompound nbt_instr = setInteger_(stack, "Used", value);
      if (capacityIfAbsent > 0) {
         if (!nbt_instr.hasKey("Capacity", 99)) {
            setCapacity(stack, capacityIfAbsent);
         }

         if (value > capacityIfAbsent) {
            setInteger_(stack, "Used", capacityIfAbsent);
         }
      }

   }

   public static final int getCapacity(@Nullable ItemStack stack, int valueIfAbsent) {
      return getInteger(stack, "Capacity", valueIfAbsent);
   }

   public static final int getCheckedCapacity(@Nullable ItemStack stack, int valueIfAbsent, int maxValue) {
      int n = getCapacity(stack, valueIfAbsent);
      if (n < 0) {
         n = valueIfAbsent;
      }

      return Math.min(n, maxValue);
   }

   public static final void setCapacity(@Nonnull ItemStack stack, int capacity) {
      setInteger_(stack, "Capacity", capacity);
   }

   public static final int getRemaining(@Nullable ItemStack stack, int capacityIfAbsent, int maxCapacity) {
      NBTTagCompound nbt_instr = get(stack);
      if (nbt_instr == null) {
         return capacityIfAbsent;
      } else {
         int m = nbt_instr.hasKey("Capacity", 99) ? nbt_instr.getInteger("Capacity") : capacityIfAbsent;
         if (m < 0) {
            m = capacityIfAbsent;
         }

         int n = nbt_instr.hasKey("Used", 99) ? nbt_instr.getInteger("Used") : 0;
         if (n < 0) {
            n = 0;
         }

         return MathHelper.clamp(m - n, 0, maxCapacity);
      }
   }

   public static final int getColorRgb(@Nonnull ItemStack stack, int valueIfAbsent) {
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey(SharedGlue.CUSTOM_COLOR_TAGNAME(), 3)) {
         return stack.getTagCompound().getInteger(SharedGlue.CUSTOM_COLOR_TAGNAME());
      } else {
         NBTTagCompound nbt_instr = get(stack);
         return nbt_instr != null && nbt_instr.hasKey("ColorRGB", 3) ? nbt_instr.getInteger("ColorRGB") : valueIfAbsent;
      }
   }

   public static final int renameLikeAnvil(@Nonnull ItemStack target, String newname) {
      int cost = 0;
      if (StringUtils.isBlank(newname)) {
         if (target.hasDisplayName()) {
            target.clearCustomName();
            ++cost;
         }
      } else if (!newname.equals(target.getDisplayName())) {
         target.setStackDisplayName(newname);
         ++cost;
      }

      return cost;
   }

   public static final int namedOnAvil(@Nonnull ItemStack creation, @Nonnull ItemStack original, String newname) {
      int cost = 0;
      if (!StringUtils.isBlank(newname) && !newname.equals(original.getDisplayName())) {
         creation.setStackDisplayName(newname);
         ++cost;
      }

      return cost;
   }

   public static final boolean setOwner(ItemStack stack, EntityPlayer player, boolean forced) {
      if (!ItemStacks.isEmpty(stack) && SharedGlue.isRealPlayer(player)) {
         boolean setted = false;
         String uuid = player.getCachedUniqueIdString();
         if (!StringUtils.isBlank(uuid)) {
            NBTTagCompound instr = createshell(stack);
            if (!forced && instr.hasKey("OwnerUUID", 8)) {
               String uuid_present = instr.getString("OwnerUUID");
               if (!uuid_present.isEmpty() && !uuid.equals(uuid_present)) {
                  return false;
               }
            }

            instr.setString("OwnerUUID", uuid);
            instr.setString("OwnerName", player.getName());
            setted = true;
         }

         return setted;
      } else {
         return false;
      }
   }

   public static final boolean setOwner(ItemStack stack, String uuid) {
      if (ItemStacks.isEmpty(stack)) {
         return false;
      } else {
         NBTTagCompound instr = createshell(stack);
         instr.setString("OwnerUUID", uuid);
         instr.removeTag("OwnerName");
         return true;
      }
   }

   public static final boolean setOwner(ItemStack stack, @Nonnull OwnerDef owner) {
      if (!ItemStacks.isEmpty(stack) && !StringUtils.isBlank(owner.uuid)) {
         NBTTagCompound instr = createshell(stack);
         instr.setString("OwnerUUID", owner.uuid);
         if (StringUtils.isBlank(owner.name)) {
            instr.removeTag("OwnerName");
         } else {
            instr.setString("OwnerName", owner.name);
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public static String getOwner(ItemStack stack) {
      OwnerDef owner = getOwnerDef(stack);
      return owner != null ? owner.uuid : null;
   }

   @Nullable
   public static OwnerDef getOwnerDef(ItemStack stack) {
      OwnerDef owner = null;
      if (!ItemStacks.isEmpty(stack)) {
         NBTTagCompound instr = get(stack);
         if (instr != null) {
            String uuid = StringUtils.trimToNull(instr.getString("OwnerUUID"));
            if (uuid != null) {
               owner = new OwnerDef();
               owner.uuid = uuid;
               if (instr.hasKey("OwnerName", 8)) {
                  owner.name = instr.getString("OwnerName");
               }
            }
         }
      }

      return owner;
   }

   public static final boolean isOwner(ItemStack stack, EntityPlayer player, boolean emptyIsMatch) {
      NBTTagCompound instr = get(stack);
      boolean is = instr != null;
      if (is) {
         String uuid = instr.getString("OwnerUUID");
         if (uuid.isEmpty()) {
            is = emptyIsMatch;
         } else {
            is = uuid.equals(player.getCachedUniqueIdString());
         }
      } else {
         is = emptyIsMatch;
      }

      return is;
   }

   public static final boolean isOwnerOrUnowned(ItemStack stack, EntityPlayer player) {
      return isOwner(stack, player, true);
   }

   public static final void clearOwner(ItemStack stack) {
      if (!ItemStacks.isEmpty(stack)) {
         NBTTagCompound instr = get(stack);
         if (instr != null) {
            instr.removeTag("OwnerUUID");
            instr.removeTag("OwnerName");
         }
      }

   }

   public static final boolean hasOwner(ItemStack stack) {
      NBTTagCompound instr = get(stack);
      return instr != null ? !instr.getString("OwnerUUID").isEmpty() : false;
   }
}
