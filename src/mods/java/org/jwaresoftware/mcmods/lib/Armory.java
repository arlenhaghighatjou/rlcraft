package org.jwaresoftware.mcmods.lib;

import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public final class Armory {
   public static final String DICT_KEY_ARMOR = "itemArmor";
   public static final String DICT_KEY_HELMET = "itemHelmet";
   public static final String DICT_KEY_BOOTS = "itemBoots";
   public static final String DICT_KEY_CHESTPLATE = "itemChestplate";
   public static final String DICT_KEY_LEGGINGS = "itemLeggings";
   public static final String DICT_KEY_SHIELD = "itemShield";
   private static boolean _DICT_IDS_INITED = false;
   private static final DecimalFormat _VALFMT = ItemStack.DECIMALFORMAT;
   private static final int _HIDE_ATTRS_TOOLTIP_BIT = 2;
   private static final EntityEquipmentSlot[] _MAINHAND_ONLY = new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND};

   private static final void register(Item item, String key1, String key2) {
      if (SharedGlue.isDefined(item)) {
         ItemStack any = new ItemStack(item, 1, 32767);
         OreDictionary.registerOre(key1, any);
         OreDictionary.registerOre(key2, any);
      }

   }

   public static final void registerSharedDictIds() {
      if (!_DICT_IDS_INITED) {
         Item[] armor = new Item[]{Items.LEATHER_HELMET, Items.IRON_HELMET, Items.GOLDEN_HELMET, Items.DIAMOND_HELMET, Items.CHAINMAIL_HELMET, Items.LEATHER_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.LEATHER_BOOTS, Items.IRON_BOOTS, Items.GOLDEN_BOOTS, Items.DIAMOND_BOOTS, Items.CHAINMAIL_BOOTS};

         int i;
         for(i = 0; i < 5; ++i) {
            register(armor[i], "itemHelmet", "helmet");
         }

         while(i < 10) {
            register(armor[i], "itemChestplate", "chestplate");
            ++i;
         }

         while(i < 15) {
            register(armor[i], "itemLeggings", "leggings");
            ++i;
         }

         while(i < 20) {
            register(armor[i], "itemBoots", "boots");
            ++i;
         }

         for(int var2 = 0; var2 < armor.length; ++var2) {
            register(armor[var2], "itemArmor", "armor");
         }

         register(Items.SHIELD, "itemShield", "shield");
         _DICT_IDS_INITED = true;
      }
   }

   public static final boolean isLiningAllowed(ItemStack stack) {
      return !ItemStacks.isEmpty(stack) && stack.getItem() instanceof ItemArmor;
   }

   private static final boolean hasLining(ItemStack stack, String NBT_TAGNAME) {
      boolean has = false;
      if (!ItemStacks.isEmpty(stack) && stack.hasTagCompound()) {
         has = !stack.getTagCompound().getString(NBT_TAGNAME).isEmpty();
      }

      return has;
   }

   private static final void removeChecked(ItemStack armor, String NBT_TAGNAME) {
      if (armor.hasTagCompound()) {
         armor.getTagCompound().removeTag(NBT_TAGNAME);
         if (armor.getTagCompound().hasNoTags()) {
            armor.setTagCompound((NBTTagCompound)null);
         }
      }

   }

   @Nonnull
   private static final <T extends Enum<?>> ItemStack setLining(ItemStack armor, String NBT_TAGNAME, @Nullable T lining) {
      ItemStack out = ItemStack.EMPTY;
      if (!ItemStacks.isEmpty(armor)) {
         if (lining != null && !"NONE".equals(lining.name())) {
            String id = lining.name();
            ItemStacks.getTagCompoundNonNull(armor).setString(NBT_TAGNAME, id);
         } else {
            removeChecked(armor, NBT_TAGNAME);
         }

         out = armor;
      }

      return out;
   }

   public static final boolean hasXLining(ItemStack armor) {
      return hasLining(armor, Armory.XLining.NBT_TAGNAME);
   }

   @Nonnull
   public static final Armory.XLining.Def getXLining(ItemStack stack) {
      Armory.XLining.Def defn = Armory.XLining.NO_DEF;
      if (isLiningAllowed(stack) && stack.hasTagCompound()) {
         String id = stack.getTagCompound().getString(Armory.XLining.NBT_TAGNAME);
         Armory.XLining lining = Armory.XLining.findOrNone(id);
         if (lining != Armory.XLining.NONE) {
            int val = 0;
            if (lining.hasValues()) {
               val = stack.getTagCompound().getInteger(Armory.XLining.NBT_VALUE_TAGNAME);
            }

            defn = new Armory.XLining.Def(lining, val);
         }
      }

      return defn;
   }

   public static final boolean hasXLining(@Nullable Armory.XLining type, ItemStack stack) {
      Armory.XLining.Def has = getXLining(stack);
      return has.isEmpty() ? type == null || type == Armory.XLining.NONE : has.name == type;
   }

   @Nonnull
   public static final ItemStack setXLining(ItemStack armor, @Nullable Armory.XLining.Def defn) {
      if (ItemStacks.isEmpty(armor)) {
         return armor;
      } else {
         boolean removing = defn == null || defn.isEmpty();
         Armory.XLining lining = removing ? null : defn.name;
         armor = setLining(armor, Armory.XLining.NBT_TAGNAME, lining);
         if (armor.hasTagCompound()) {
            if (removing) {
               removeChecked(armor, Armory.XLining.NBT_VALUE_TAGNAME);
            } else if (lining.hasValues()) {
               armor.getTagCompound().setInteger(Armory.XLining.NBT_VALUE_TAGNAME, defn.value);
            } else {
               armor.getTagCompound().removeTag(Armory.XLining.NBT_VALUE_TAGNAME);
            }
         }

         return armor;
      }
   }

   public static final boolean hasTLining(ItemStack armor) {
      return hasLining(armor, Armory.TLining.NBT_TAGNAME);
   }

   @Nonnull
   public static final Armory.TLining getTLining(ItemStack armor) {
      Armory.TLining found = Armory.TLining.NONE;
      if (!ItemStacks.isEmpty(armor) && armor.hasTagCompound()) {
         String id = armor.getTagCompound().getString(Armory.TLining.NBT_TAGNAME);
         if (!id.isEmpty()) {
            found = Armory.TLining.findOrNone(id);
         }
      }

      return found;
   }

   @Nonnull
   public static final ItemStack setTLining(ItemStack armor, @Nullable Armory.TLining lining) {
      if (lining == Armory.TLining.NONE) {
         lining = null;
      }

      return setLining(armor, Armory.TLining.NBT_TAGNAME, lining);
   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public static final String getTLiningTooltip(ItemStack stack) {
      String tooltip = null;
      if (isLiningAllowed(stack)) {
         Armory.TLining lining = getTLining(stack);
         if (lining != Armory.TLining.NONE) {
            boolean cooling = lining.getModifier() < 0;
            String modifierstring = "" + (cooling ? "-" : "+") + Math.abs(lining.getModifier());
            String description = Strings.translateFormatted("carrots.tooltip.tan.xlining." + (cooling ? "COOL" : "WARM"), modifierstring);
            TextFormatting color = cooling ? TextFormatting.DARK_AQUA : TextFormatting.DARK_RED;
            tooltip = "" + color + description + TextFormatting.RESET;
         }
      }

      return tooltip;
   }

   public static final boolean hasLining(ItemStack armor) {
      return hasTLining(armor) || hasXLining(armor);
   }

   @Nonnull
   public static final String getArmorMaterialName(@Nullable ArmorMaterial material) {
      String name = "";
      if (material != null) {
         try {
            return material.getName();
         } catch (Throwable var3) {
            ;
         }
      }

      return name;
   }

   public static final void hideStandardModifierRender(@Nonnull ItemStack on) {
      if (on.hasTagCompound()) {
         int current = on.getTagCompound().getInteger(SharedGlue.ITEM_INFO_HIDDEN_TAGNAME());
         on.getTagCompound().setInteger(SharedGlue.ITEM_INFO_HIDDEN_TAGNAME(), current | 2);
      }

   }

   public static final String getWeaponOrToolModifierName(@Nonnull ItemStack stack) {
      return stack.getItem() instanceof ItemTool ? SharedGlue.TOOL_MODIFIER_ATTRNAME() : SharedGlue.WEAPON_MODIFIER_ATTRNAME();
   }

   public static final boolean hasModifiedAttributes(ItemStack stack) {
      return !ItemStacks.isEmpty(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(SharedGlue.CUSTOM_ATTRIBUTE_MODIFIERS_TAGNAME(), 9);
   }

   public static final boolean hasReforgedAttributes(ItemStack stack) {
      return Instructions.isLooted(stack) && hasModifiedAttributes(stack) && (stack.getTagCompound().getInteger(SharedGlue.ITEM_INFO_HIDDEN_TAGNAME()) & 2) == 2;
   }

   public static final void removeModifiedAttributes(ItemStack stack) {
      if (!ItemStacks.isEmpty(stack) && stack.hasTagCompound()) {
         stack.getTagCompound().removeTag(SharedGlue.CUSTOM_ATTRIBUTE_MODIFIERS_TAGNAME());
      }

   }

   public static final void removeModifiedAttributes(ItemStack stack, String... attributeNames) {
      if (attributeNames.length > 0 && hasModifiedAttributes(stack)) {
         NBTTagList list = stack.getTagCompound().getTagList(SharedGlue.CUSTOM_ATTRIBUTE_MODIFIERS_TAGNAME(), 10);
         Iterator<NBTBase> litr = list.iterator();

         while(litr.hasNext()) {
            NBTTagCompound modNbt = (NBTTagCompound)litr.next();
            String am_id = modNbt.getString("AttributeName");

            for(String attrid : attributeNames) {
               if (attrid.equals(am_id)) {
                  litr.remove();
                  break;
               }
            }
         }

         if (list.hasNoTags()) {
            stack.getTagCompound().removeTag(SharedGlue.CUSTOM_ATTRIBUTE_MODIFIERS_TAGNAME());
         }
      }

   }

   @Nonnull
   public static final String getModifiersProfileId(@Nonnull ItemStack stack) {
      String id = "";
      if (!stack.isEmpty() && stack.hasTagCompound()) {
         id = stack.getTagCompound().getString("StatsProfile");
         if (id.isEmpty() && Instructions.present(stack)) {
            id = Instructions.get(stack).getString("StatsProfile");
         }
      }

      return id;
   }

   @SideOnly(Side.CLIENT)
   public static final void addWeaponModifierInformation(@Nonnull ItemStack stack, @Nullable EntityPlayer player, @Nonnull List<String> tooltip) {
      if (player != null) {
         addModifierInformation(stack, player, _MAINHAND_ONLY, tooltip);
      }

   }

   @SideOnly(Side.CLIENT)
   public static final void addWeaponModifierInformation(@Nonnull ItemStack stack, @Nonnull List<String> tooltip) {
      addModifierInformation(stack, (EntityPlayer)null, _MAINHAND_ONLY, tooltip);
   }

   @SideOnly(Side.CLIENT)
   public static final void addModifierInformation(@Nonnull ItemStack stack, @Nullable EntityPlayer player, @Nonnull EntityEquipmentSlot[] slots, @Nonnull List<String> tooltip) {
      for(EntityEquipmentSlot slot : slots) {
         Multimap<String, AttributeModifier> slotattrs = stack.getAttributeModifiers(slot);
         if (!slotattrs.isEmpty()) {
            tooltip.add("");
            tooltip.add(Strings.translate("item.modifiers." + slot.getName()));
            boolean reforged_label = false;
            int reforged_label_index = -1;

            for(int pass = 0; pass < 2; ++pass) {
               for(Entry<String, AttributeModifier> entry : slotattrs.entries()) {
                  AttributeModifier modifier = (AttributeModifier)entry.getValue();
                  int op = modifier.getOperation();
                  double amount = modifier.getAmount();
                  boolean actual = false;
                  if (Armory.UUIDPeek.isAttackDamage(modifier.getID())) {
                     actual = true;
                     if (pass == 0) {
                        amount = amount + (player != null ? player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue() : 1.0D);
                        amount = amount + (double)EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
                     }
                  } else if (Armory.UUIDPeek.isAttackSpeedo(modifier.getID())) {
                     actual = true;
                     if (pass == 0) {
                        amount += player != null ? player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue() : SharedMonsterAttributes.ATTACK_SPEED.getDefaultValue();
                     }
                  }

                  double final_amount = isPercentageOp(op) ? 100.0D * amount : amount;
                  if (pass == 0 && actual) {
                     tooltip.add(" " + Strings.translateFormatted("attribute.base_modifier.equals." + op, _VALFMT.format(final_amount), Strings.translate("attribute.name." + (String)entry.getKey())));
                  } else if (pass != 0 && !actual) {
                     if (!reforged_label) {
                        reforged_label = true;
                        tooltip.add("");
                        tooltip.add(Strings.translate("tooltip.section.reforged.results"));
                        reforged_label_index = tooltip.size() - 1;
                        tooltip.add(" " + Strings.translate("item.modifiers." + slot.getName()));
                     }

                     if (amount > 0.0D) {
                        tooltip.add(TextFormatting.BLUE + "  " + Strings.translateFormatted("attribute.modifier.plus." + op, _VALFMT.format(final_amount), Strings.translate("attribute.name." + (String)entry.getKey())));
                     } else if (amount < 0.0D) {
                        final_amount = final_amount * -1.0D;
                        tooltip.add(TextFormatting.RED + "  " + Strings.translateFormatted("attribute.modifier.take." + op, _VALFMT.format(final_amount), Strings.translate("attribute.name." + (String)entry.getKey())));
                     }
                  }
               }
            }

            if (reforged_label) {
               String marker = (String)tooltip.get(reforged_label_index);
               if (marker.indexOf(45) >= 0) {
                  FontRenderer fontRenderer = stack.getItem().getFontRenderer(stack);
                  if (fontRenderer == null) {
                     fontRenderer = Minecraft.getMinecraft().fontRenderer;
                  }

                  int markerwidth = fontRenderer.getStringWidth(marker);
                  int maxwidth = markerwidth;

                  for(String line : tooltip) {
                     int width = fontRenderer.getStringWidth(line);
                     if (width > maxwidth) {
                        maxwidth = width;
                     }
                  }

                  if (maxwidth != markerwidth) {
                     int x = marker.indexOf(45);
                     StringBuilder sb = new StringBuilder(300);
                     int width = 0;
                     sb.append(marker.substring(0, x));

                     while(true) {
                        sb.append("-");
                        width = fontRenderer.getStringWidth(sb.toString());
                        if (width >= maxwidth || width >= 300) {
                           break;
                        }
                     }

                     marker = sb.toString();
                  }
               }

               tooltip.set(reforged_label_index, TextFormatting.DARK_PURPLE + marker + TextFormatting.RESET);
            }
         }
      }

   }

   private static boolean isPercentageOp(int op) {
      return op == 1 || op == 2;
   }

   public static final boolean isDefendableAgainst(@Nonnull DamageSource with) {
      String name = with.getDamageType();
      return "mob".equals(name) || "player".equals(name) || "thrown".equals(name) || with instanceof EntityDamageSource || with instanceof EntityDamageSourceIndirect;
   }

   public static final boolean isDirectPlayerAttackDamage(DamageSource ds) {
      return ds instanceof EntityDamageSource && "player".equals(ds.getDamageType()) && SharedGlue.isRealPlayer(ds.getImmediateSource());
   }

   public static final boolean isDirectMonsterAttackDamage(DamageSource ds) {
      return ds instanceof EntityDamageSource && "mob".equals(ds.getDamageType()) && SharedGlue.isMonsterOrPiOoed(ds.getImmediateSource());
   }

   public static final boolean isDirectEntityAttackDamage(DamageSource ds) {
      return isDirectPlayerAttackDamage(ds) || isDirectMonsterAttackDamage(ds);
   }

   public static final boolean isDamageAbsolute(DamageSource with) {
      boolean is = with.isDamageAbsolute();
      if (is && isDefendableAgainst(with)) {
         is = false;
      }

      return is;
   }

   public static final boolean attemptDamageItemBounded(ItemStack armor, int amount, @Nonnull EntityLivingBase bearer) {
      boolean attempted = false;
      if (!ItemStacks.isEmpty(armor)) {
         if (SharedGlue.isaPlayer(bearer)) {
            int current_damage = armor.getItemDamage();
            if (current_damage + amount >= armor.getMaxDamage()) {
               amount = armor.getMaxDamage() - current_damage - 1;
            }
         }

         if (amount > 0) {
            armor.damageItem(amount, bearer);
            attempted = true;
         }
      }

      return attempted;
   }

   private Armory() {
   }

   public static final void init() {
   }

   public static enum TLining {
      COOLEST(-3),
      COOLER(-2),
      COOL(-1),
      NONE(0),
      WARM(1),
      WARMER(2),
      WARMEST(3);

      private static final Armory.TLining[] _VALUES = values();
      public static final String NBT_TAGNAME = LibInfo.rstring("ArmorTLining");
      private final int _modifier;

      private TLining(int modifier) {
         this._modifier = modifier;
      }

      public final boolean isEmpty() {
         return this == NONE;
      }

      public int getModifier() {
         return this._modifier;
      }

      public int getOffsetModifier() {
         return this.ordinal() - NONE.ordinal();
      }

      public int getThickness() {
         return Math.abs(this.getOffsetModifier());
      }

      public int getDirection() {
         return Integer.compare(this.ordinal(), NONE.ordinal());
      }

      @Nonnull
      public static final Armory.TLining findOrNone(@Nonnull String id) {
         id = id.toUpperCase(Locale.US);

         for(Armory.TLining lining : _VALUES) {
            if (lining.name().equals(id)) {
               return lining;
            }
         }

         return NONE;
      }

      @Nonnull
      public final Armory.TLining adjust(int delta) {
         int to = this.ordinal() + delta;

         for(int i = 0; i < _VALUES.length; ++i) {
            if (to == _VALUES[i].ordinal()) {
               return _VALUES[i];
            }
         }

         return to <= COOLEST.ordinal() ? COOLEST : WARMEST;
      }
   }

   public static final class UUIDPeek extends Item {
      public static final EaglercraftUUID getAttackDamageUUID() {
         return Item.ATTACK_DAMAGE_MODIFIER;
      }

      public static final EaglercraftUUID getAttackSpeedoUUID() {
         return Item.ATTACK_SPEED_MODIFIER;
      }

      public static final boolean isAttackDamage(String uuidstring) {
         return getAttackDamageUUID().toString().equals(uuidstring);
      }

      public static final boolean isAttackDamage(EaglercraftUUID uuid) {
         return uuid != null && (uuid == Item.ATTACK_DAMAGE_MODIFIER || isAttackDamage(uuid.toString()));
      }

      public static final boolean isAttackSpeedo(String uuidstring) {
         return getAttackSpeedoUUID().toString().equals(uuidstring);
      }

      public static final boolean isAttackSpeedo(EaglercraftUUID uuid) {
         return uuid != null && (uuid == Item.ATTACK_SPEED_MODIFIER || isAttackSpeedo(uuid.toString()));
      }

      private UUIDPeek() {
      }
   }

   public static enum XLining {
      NONE(-1),
      TEMPERATURE_REGULATOR(3),
      OBSIDIAN_SHIELD(0),
      ANTIFREEZE_SHIELD(0),
      PHOTOSYNTHETIC(0),
      ANTI_CORROSIVE(0),
      SHOCK_ABSORBER(2),
      DEFRACTION_CLOAK(0);

      private static final Armory.XLining[] _VALUES = values();
      public static final String NBT_TAGNAME = LibInfo.rstring("ArmorXLining");
      public static final String NBT_VALUE_TAGNAME = LibInfo.rstring("ArmorXLining_value");
      private final int _val_count;
      public static final Armory.XLining.Def NO_DEF = new Armory.XLining.Def(NONE, 0);

      private XLining(int value_count) {
         this._val_count = value_count;
      }

      public boolean hasValues() {
         return this._val_count > 0;
      }

      @Nonnull
      public static final Armory.XLining findOrNone(@Nonnull String id) {
         if (!id.isEmpty()) {
            id = id.toUpperCase(Locale.US);

            for(Armory.XLining lining : _VALUES) {
               if (lining.name().equals(id)) {
                  return lining;
               }
            }
         }

         return NONE;
      }

      public static final class Def {
         public final Armory.XLining name;
         public final int value;

         public Def(@Nonnull Armory.XLining id, int i) {
            this.name = id == null ? Armory.XLining.NONE : id;
            this.value = i;
         }

         public Def(@Nonnull Armory.XLining id) {
            this(id, 0);
         }

         public final boolean isEmpty() {
            return this.name == Armory.XLining.NONE;
         }

         public String toString() {
            return "" + this.name + (this.isEmpty() ? "" : ":" + this.value);
         }
      }
   }
}
