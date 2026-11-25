package org.jwaresoftware.mcmods.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.jwaresoftware.mcmods.lib.api.ICurative;

public final class Potions {
   public static final PotionEffect[] EMPTY_EFFECTS = new PotionEffect[0];
   public static final int MAX_AMPLIFY_LEVEL = 4;
   public static final Potion NO_POTION = new Potion(false, 0) {
      public String getName() {
         return "nopotion";
      }

      public boolean isReady(int d, int a) {
         return false;
      }

      public void performEffect(EntityLivingBase le, int amplifier) {
      }

      public void affectEntity(@Nullable Entity e, @Nullable Entity es, EntityLivingBase le, int a, double h) {
      }
   };
   private static final int _FOREVER_THRESHOLD_DURATION = 32000;
   static final String _CUSTOM_ELIXIR_EFFECTS_TAGNAME = "ElixirEffects";

   public static final void registerSharedDictIds(@Nonnull String dictKey) {
      OreDictionary.registerOre(dictKey, Items.POTIONITEM);
      OreDictionary.registerOre(dictKey, Items.SPLASH_POTION);
      OreDictionary.registerOre(dictKey, Items.LINGERING_POTION);
   }

   public static final void registerSharedDictIds() {
      registerSharedDictIds("itemPotionBottle");
   }

   public static final boolean isEmpty(@Nullable Potion p) {
      return p == null || p == NO_POTION;
   }

   public static final boolean isLikelyForever(PotionEffect e) {
      return e != null && e.getDuration() > 32000;
   }

   public static final ItemStack newPotionHeldItem(PotionType potion) {
      ItemStack potionitem = new ItemStack(Items.POTIONITEM);
      if (!PotionType.REGISTRY.containsKey(potion.getRegistryName())) {
         potionitem = PotionUtils.appendEffects(potionitem, potion.getEffects());
      } else {
         potionitem = PotionUtils.addPotionToItemStack(potionitem, potion);
      }

      return potionitem;
   }

   public static final PotionEffect newPotionEffect(Potion potion, int duration, int amplifier, boolean ambient, boolean particles) {
      return new PotionEffect(potion, duration, amplifier, ambient, particles);
   }

   public static final PotionEffect newPotionEffect(Potion potion, int duration, int amplifier) {
      return new PotionEffect(potion, duration, amplifier);
   }

   public static final PotionEffect newPotionTypeEffect(Potion potion, int duration_secs, int amplifier) {
      return new PotionEffect(potion, duration_secs * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER(), amplifier, false, true);
   }

   public static final PotionEffect newHiddenPotionTypeEffect(Potion potion, int duration_secs, int amplifier) {
      return new PotionEffect(potion, duration_secs * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER(), amplifier, false, false);
   }

   public static final PotionEffect newStubbornPotionEffect(Potion potion, int duration, int amplifier, boolean ambient, boolean particles) {
      return new StubbornPotionEffect(potion, duration, amplifier, ambient, particles);
   }

   public static final PotionEffect newForeverEffect(Potion potion, int amplifier) {
      return new PotionEffect(potion, Integer.MAX_VALUE, amplifier, false, false);
   }

   public static final PotionEffect copyPotionEffect(PotionEffect potioneffect) {
      if (potioneffect instanceof MagnifyingPotionEffect) {
         return new MagnifyingPotionEffect((MagnifyingPotionEffect)potioneffect);
      } else {
         return (PotionEffect)(potioneffect instanceof StubbornPotionEffect ? new StubbornPotionEffect((StubbornPotionEffect)potioneffect) : new PotionEffect(potioneffect));
      }
   }

   public static final boolean removePotionEffect(@Nonnull EntityLivingBase entity, @Nonnull Potion potion) {
      boolean tried = entity.isPotionActive(potion);
      entity.removePotionEffect(potion);
      return tried;
   }

   public static final void removePotionEffect(@Nonnull EntityLivingBase entity, @Nonnull PotionType potion) {
      for(PotionEffect e : potion.getEffects()) {
         entity.removePotionEffect(e.getPotion());
      }

   }

   public static final void curePotionEffects(EntityLivingBase player, ItemStack with) {
      player.curePotionEffects(with);
   }

   public static final void addPotionEffect(EntityLivingBase entity, Potion potion, int duration_secs, int amplifier, boolean ambient, boolean particles) {
      entity.addPotionEffect(newPotionEffect(potion, duration_secs * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER(), amplifier, ambient, particles));
   }

   public static final void addPotionEffect(EntityLivingBase entity, Potion potion, int duration_secs, int amplifier) {
      entity.addPotionEffect(newPotionEffect(potion, duration_secs * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER(), amplifier));
   }

   public static final void addSecondaryPotionEffect(EntityLivingBase entity, Potion newpotion, Potion oldpotion, int amplifier) {
      PotionEffect current = entity.getActivePotionEffect(oldpotion);
      if (current != null) {
         if (amplifier < 0) {
            amplifier = current.getAmplifier();
         }

         entity.addPotionEffect(newPotionEffect(newpotion, current.getDuration(), amplifier, false, false));
      }

   }

   public static final void addPotionEffect(ItemFood food, Potion potion, int duration_secs, int amplifier, float chance) {
      food.setPotionEffect(newPotionEffect(potion, duration_secs * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER(), amplifier), chance);
   }

   public static final void applyPotionEffect(EntityLivingBase victim, Potion potion, int duration_secs, int amplifier) {
      if (potion.isInstant() && duration_secs <= 0) {
         potion.affectEntity((Entity)null, (Entity)null, victim, amplifier, 1.0D);
      } else {
         addPotionEffect(victim, potion, duration_secs, amplifier);
      }

   }

   public static final void applyEffect(@Nonnull PotionEffect effect, @Nonnull EntityLivingBase victim) {
      if (effect.getPotion().isInstant() && effect.getPotion() != SharedGlue.Potion_saturation) {
         effect.getPotion().affectEntity((Entity)null, (Entity)null, victim, effect.getAmplifier(), 1.0D);
      } else {
         victim.addPotionEffect(copyPotionEffect(effect));
      }

   }

   public static final void addPotionEffects(@Nonnull EntityLivingBase victim, @Nonnull PotionType potion) {
      for(PotionEffect e : potion.getEffects()) {
         applyEffect(e, victim);
      }

   }

   public static final void addPotionEffects(@Nonnull EntityLivingBase victim, @Nonnull ItemStack potion_source) {
      for(PotionEffect e : getEffects(potion_source, false)) {
         applyEffect(e, victim);
      }

   }

   public static final void addPotionEffect(EntityLivingBase victim, Potion potion, int duration_secs, int amplifier, Object... cures) {
      PotionEffect potioneffect = newPotionEffect(potion, duration_secs * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER(), amplifier);
      if (cures != null) {
         for(Object cure : cures) {
            potioneffect.addCurativeItem(cure instanceof ItemStack ? (ItemStack)cure : new ItemStack((Item)cure));
         }
      }

      victim.addPotionEffect(potioneffect);
   }

   public static final void addStubbornPotionEffect(EntityLivingBase entity, Potion potion, int duration_secs, int amplifier, boolean ambient, boolean particles) {
      entity.addPotionEffect(newStubbornPotionEffect(potion, duration_secs * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER(), amplifier, ambient, particles));
   }

   public static final void addStubbornPotionEffect(EntityLivingBase entity, Potion potion, int duration_secs, int amplifier, Object... cures) {
      PotionEffect potioneffect = newStubbornPotionEffect(potion, duration_secs * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER(), amplifier, false, false);
      if (cures != null) {
         for(Object cure : cures) {
            potioneffect.addCurativeItem(cure instanceof ItemStack ? (ItemStack)cure : new ItemStack((Item)cure));
         }
      }

      entity.addPotionEffect(potioneffect);
   }

   public static final void addMagniferPotionEffect(EntityLivingBase entity, Potion potion, int duration_secs, int amplifier, boolean ambient, boolean particles) {
      PotionEffect e = new MagnifyingPotionEffect(potion, duration_secs * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER(), amplifier, ambient, particles);
      entity.addPotionEffect(e);
   }

   public static final boolean isMilk(ItemStack stack, boolean modded) {
      Item item = !ItemStacks.isEmpty(stack) ? stack.getItem() : null;
      if (item != null) {
         if (item == Items.MILK_BUCKET) {
            return true;
         }

         FluidStack fluidstack = FluidUtil.getFluidContained(stack);
         if (fluidstack != null) {
            String fluidName = fluidstack.getFluid().getName();
            if (Fluids.isMilk(fluidName)) {
               if (ModIntegration.isUniversalBucket(stack)) {
                  return true;
               }

               EnumAction useaction = stack.getItemUseAction();
               if (useaction == EnumAction.DRINK || useaction == EnumAction.EAT) {
                  return true;
               }
            } else if (modded && (fluidName.startsWith("milk_") || fluidName.endsWith("_milk"))) {
               return true;
            }
         } else if (modded) {
            EnumAction useaction = stack.getItemUseAction();
            if (useaction == EnumAction.DRINK || useaction == EnumAction.EAT) {
               return ModIntegration.isCustomMilkBottle(stack);
            }
         }
      }

      return false;
   }

   public static final boolean isCurative(@Nonnull EntityPlayer player, ItemStack item) {
      return isCurative(player, item, false);
   }

   public static final boolean isCurative(@Nonnull EntityPlayer player, ItemStack item, boolean general) {
      if (!ItemStacks.isEmpty(item)) {
         if (general && item.getItem() instanceof ICurative) {
            return true;
         }

         for(PotionEffect potioneffect : player.getActivePotionEffects()) {
            if (potioneffect.isCurativeItem(item)) {
               return true;
            }
         }
      }

      return false;
   }

   public static final void addMilkBasedRegenEffect(ItemFood food, int duration, int amplifier) {
      addPotionEffect(food, SharedGlue.Potion_regeneration, duration, amplifier, 1.0F);
   }

   public static final void addMilkBasedRegenEffect(ItemFood food) {
      addMilkBasedRegenEffect(food, 15, 2);
   }

   public static final void removeAllEffects(@Nullable EntityLivingBase entity) {
      if (entity != null && entity.isEntityAlive()) {
         for(PotionEffect goodOrBad : new ArrayList(entity.getActivePotionEffects())) {
            entity.removePotionEffect(goodOrBad.getPotion());
         }
      }

   }

   public static final boolean isEffectivelyBad(PotionEffect effect) {
      Potion potion = effect.getPotion();
      boolean is = potion.isBadEffect();
      if (!is) {
         boolean inversed = effect.getAmplifier() < 0;
         if (inversed && (potion == SharedGlue.Potion_healthBoost || potion == SharedGlue.Potion_moveSpeed || potion == SharedGlue.Potion_resistance || potion == SharedGlue.Potion_jump || potion == SharedGlue.Potion_digSpeed)) {
            is = true;
         }
      }

      return is;
   }

   public static final boolean hasAtLeastOneNegativeEffect(@Nullable EntityPlayer player) {
      if (player != null) {
         for(PotionEffect effect : player.getActivePotionEffects()) {
            if (isEffectivelyBad(effect)) {
               return true;
            }
         }
      }

      return false;
   }

   public static final boolean cureAllBadEffects(@Nullable EntityLivingBase entity) {
      int affected = 0;
      if (entity != null) {
         Collection<PotionEffect> effects = entity.getActivePotionEffects();
         List<Potion> remove = null;

         for(PotionEffect effect : effects) {
            if (isEffectivelyBad(effect)) {
               if (remove == null) {
                  remove = new ArrayList();
               }

               remove.add(effect.getPotion());
            }
         }

         if (remove != null) {
            for(Potion badpotion : remove) {
               entity.removePotionEffect(badpotion);
               ++affected;
            }
         }
      }

      return affected > 0;
   }

   public static final void removeAllHelpfulEffects(@Nullable EntityLivingBase entity) {
      if (entity != null) {
         Collection<PotionEffect> effects = entity.getActivePotionEffects();
         List<Potion> remove = null;

         for(PotionEffect effect : effects) {
            if (!isEffectivelyBad(effect)) {
               if (remove == null) {
                  remove = new ArrayList();
               }

               remove.add(effect.getPotion());
            }
         }

         if (remove != null) {
            for(Potion goodpotion : remove) {
               entity.removePotionEffect(goodpotion);
            }
         }
      }

   }

   public static final boolean cureJumpFailImplEffect(@Nonnull EntityPlayer player) {
      boolean done = false;
      PotionEffect e = player.getActivePotionEffect(SharedGlue.Potion_jump);
      if (e != null && e.getAmplifier() < 0) {
         player.removePotionEffect(SharedGlue.Potion_jump);
         done = true;
      }

      return done;
   }

   public static final void cureBadPotionEffectsWithMilk(@Nullable EntityPlayer player, boolean combatant, boolean muscle_boosted) {
      if (player != null) {
         player.removePotionEffect(SharedGlue.Potion_harm);
         player.removePotionEffect(SharedGlue.Potion_blindness);
         player.removePotionEffect(SharedGlue.Potion_nausea);
         player.removePotionEffect(SharedGlue.Potion_poison);
         player.removePotionEffect(SharedGlue.Potion_hunger);
         cureJumpFailImplEffect(player);
         if (combatant) {
            player.removePotionEffect(SharedGlue.Potion_wither);
            PotionEffect e = player.getActivePotionEffect(SharedGlue.Potion_resistance);
            if (e != null && e.getAmplifier() < 0) {
               player.removePotionEffect(SharedGlue.Potion_resistance);
            }
         }

         if (muscle_boosted) {
            cureBadPotionEffectsWithMuscleBoost(player);
         }
      }

   }

   public static final void cureBadPotionEffectsWithMilkBucket(@Nullable EntityPlayer player) {
      if (player != null) {
         cureAllBadEffects(player);
         addPotionEffect(player, SharedGlue.Potion_regeneration, 10, 2, false, false);
      }

   }

   public static final void cureBadPotionEffectsWithMuscleBoost(EntityPlayer player) {
      if (player != null) {
         player.removePotionEffect(SharedGlue.Potion_weakness);
         player.removePotionEffect(SharedGlue.Potion_moveSlowdown);
         player.removePotionEffect(SharedGlue.Potion_digSlowdown);
         cureJumpFailImplEffect(player);
      }

   }

   public static final void addMuscleBoostPotionEffect(EntityPlayer player, int duration_mins) {
      if (player != null) {
         if (duration_mins <= 0) {
            duration_mins = 2;
         }

         int duration = 60 * duration_mins;
         addPotionEffect(player, SharedGlue.Potion_healthBoost, duration, 1, false, false);
         addPotionEffect(player, SharedGlue.Potion_strength, duration, 1, false, false);
         addPotionEffect(player, SharedGlue.Potion_resistance, duration, 1, false, false);
      }

   }

   public static final void onMuscleFoodEaten(EntityPlayer player, int duration_mins) {
      cureBadPotionEffectsWithMuscleBoost(player);
      addMuscleBoostPotionEffect(player, duration_mins);
   }

   public static final void cureBadPotionEffectsWithSmellingSalts(@Nullable EntityPlayer player) {
      if (SharedGlue.hasPotionsInEffect(player)) {
         player.removePotionEffect(SharedGlue.Potion_blindness);
         player.removePotionEffect(SharedGlue.Potion_nausea);
         player.removePotionEffect(SharedGlue.Potion_hunger);
         cureJumpFailImplEffect(player);
         cureBadPotionEffectsWithMuscleBoost(player);
      }

   }

   public static final ItemStack newTippedArrow(PotionType effect) {
      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      return PotionUtils.addPotionToItemStack(arrow, effect);
   }

   public static final void cure(@Nonnull EntityPlayer player, @Nonnull ModIntegration mod, @Nonnull String potion_name) {
      Potion p = mod.potion(potion_name);
      if (p != null) {
         player.removeActivePotionEffect(p);
      }

   }

   public static final void cureChokingEffects(EntityPlayer player) {
      cure(player, ModIntegration.GRAVESTONE_EXT, "gs_choke_potion");
   }

   public static final void cureFireBurningEffects(@Nonnull EntityPlayer player, boolean immediate) {
      if (SharedGlue.hasPotionsInEffect(player)) {
         if (ModIntegration.POTION_CORE.isLoaded()) {
            cure(player, ModIntegration.POTION_CORE, "fire");
         }

         if (ModIntegration.GRAVESTONE_EXT.isLoaded()) {
            cure(player, ModIntegration.GRAVESTONE_EXT, "gs_burning_potion");
            cure(player, ModIntegration.GRAVESTONE_EXT, "gs_inferno_potion");
         }

         if (ModIntegration.EXTRA_ALCHEMY.isLoaded()) {
            cure(player, ModIntegration.EXTRA_ALCHEMY, "effect.combustion");
         }

         if (immediate && player.isBurning()) {
            player.extinguish();
            Effects.playExtinguishedBurning(player.getEntityWorld(), player);
         }
      }

   }

   public static final void cureFreezingEffects(@Nonnull EntityPlayer player, boolean immediate) {
      if (SharedGlue.hasPotionsInEffect(player)) {
         if (ModIntegration.TOUGH_AS_NAILS.isLoaded()) {
            cure(player, ModIntegration.TOUGH_AS_NAILS, "hypothermia");
         }

         if (ModIntegration.ELECTROBLOB_WIZARDRY.isLoaded()) {
            cure(player, ModIntegration.ELECTROBLOB_WIZARDRY, "frost");
         }

         if (ModIntegration.NETHER_EX.isLoaded()) {
            cure(player, ModIntegration.NETHER_EX, "freeze");
            cure(player, ModIntegration.NETHER_EX, "frostbite");
         }

         if (ModIntegration.SHOTGUNS_AND_GLITTER.isLoaded()) {
            cure(player, ModIntegration.SHOTGUNS_AND_GLITTER, "frost");
         }

         if (ModIntegration.EXTRA_ALCHEMY.isLoaded()) {
            cure(player, ModIntegration.EXTRA_ALCHEMY, "freezing");
         }
      }

   }

   public static final boolean hasCustomElixirEffects(@Nonnull ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound().hasKey("ElixirEffects", 9);
   }

   static final List<PotionEffect> mergeCustomElixirEffects(@Nonnull NBTTagCompound from, @Nullable List<PotionEffect> effects) {
      if (effects == null) {
         effects = new ArrayList();
      }

      if (from.hasKey("ElixirEffects", 9)) {
         NBTTagList tagList = from.getTagList("ElixirEffects", 10);

         for(int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound effectData = tagList.getCompoundTagAt(i);
            ResourceLocation effectName = new ResourceLocation(effectData.getString("Effect"));
            Potion potion = (Potion)ForgeRegistries.POTIONS.getValue(effectName);
            if (potion != null) {
               int duration = effectData.getInteger("Duration");
               int amplifier = effectData.getInteger("Amplifier");
               if (duration > 0 || duration == 0 && potion.isInstant()) {
                  effects.add(new PotionEffect(potion, duration, amplifier));
               }
            }
         }
      }

      return effects;
   }

   public static final List<PotionEffect> getEffects(@Nonnull ItemStack stack, boolean any) {
      List<PotionEffect> effects = PotionUtils.getEffectsFromStack(stack);
      if (any && hasCustomElixirEffects(stack)) {
         mergeCustomElixirEffects(stack.getTagCompound(), effects);
      }

      return effects;
   }

   public static final boolean matches(@Nonnull ItemStack stack, PotionType ingredient, boolean any) {
      if (isReal(ingredient)) {
         List<PotionEffect> have = getEffects(stack, any);
         List<PotionEffect> want = ingredient.getEffects();
         if (have.size() == want.size()) {
            if (want.size() == 1) {
               return ((PotionEffect)want.get(0)).equals(have.get(0));
            }

            for(PotionEffect e : want) {
               if (!have.contains(e)) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   public static final void addCustomEffects(@Nonnull NBTTagCompound to, boolean replace, @Nullable Collection<PotionEffect> effects) {
      if (replace) {
         to.removeTag(SharedGlue.CUSTOM_POTION_EFFECTS_TAGNAME());
      }

      if (effects != null && !effects.isEmpty()) {
         NBTTagList eList = to.getTagList(SharedGlue.CUSTOM_POTION_EFFECTS_TAGNAME(), 10);

         for(PotionEffect effect : effects) {
            eList.appendTag(effect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         to.setTag(SharedGlue.CUSTOM_POTION_EFFECTS_TAGNAME(), eList);
      }

   }

   @SideOnly(Side.CLIENT)
   private static final void trackAttributeModifiers(PotionEffect effect, List<Tuple<String, AttributeModifier>> local_copies) {
      Potion potion = effect.getPotion();
      Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();
      if (!map.isEmpty()) {
         for(Entry<IAttribute, AttributeModifier> iattrib : map.entrySet()) {
            AttributeModifier modifier = (AttributeModifier)iattrib.getValue();
            AttributeModifier local_copy = new AttributeModifier(modifier.getName(), potion.getAttributeModifierAmount(effect.getAmplifier(), modifier), modifier.getOperation());
            local_copies.add(new Tuple(((IAttribute)iattrib.getKey()).getName(), local_copy));
         }
      }

   }

   @SideOnly(Side.CLIENT)
   private static final void addModifierInformation(List<Tuple<String, AttributeModifier>> attribs, List<String> tooltip) {
      tooltip.add("");
      tooltip.add(TextFormatting.DARK_PURPLE + Strings.translate("potion.whenDrank"));

      for(Tuple<String, AttributeModifier> tuple : attribs) {
         AttributeModifier modifier = (AttributeModifier)tuple.getSecond();
         double amount = modifier.getAmount();
         int op = modifier.getOperation();
         double value;
         if (op != 1 && op != 2) {
            value = modifier.getAmount();
         } else {
            value = modifier.getAmount() * 100.0D;
         }

         if (amount > 0.0D) {
            tooltip.add(TextFormatting.BLUE + Strings.translateFormatted("attribute.modifier.plus." + op, ItemStack.DECIMALFORMAT.format(value), Strings.translate("attribute.name." + (String)tuple.getFirst())));
         } else if (amount < 0.0D) {
            value = value * -1.0D;
            tooltip.add(TextFormatting.RED + Strings.translateFormatted("attribute.modifier.take." + op, ItemStack.DECIMALFORMAT.format(value), Strings.translate("attribute.name." + (String)tuple.getFirst())));
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static final void addPotionInformation(@Nonnull Collection<PotionEffect> effects, @Nonnull List<String> tooltip, int durationOverride, boolean full, boolean indent) {
      List<Tuple<String, AttributeModifier>> attribs = null;
      String _PS = indent ? " " : "";
      if (full) {
         if (effects.isEmpty()) {
            String displayString = Strings.translate("effect.none").trim();
            tooltip.add(_PS + TextFormatting.GRAY + displayString);
            return;
         }

         attribs = new ArrayList();
      }

      for(PotionEffect effect : effects) {
         if (full) {
            trackAttributeModifiers(effect, attribs);
         }

         boolean bad = isEffectivelyBad(effect);
         String displayString = _PS + Strings.translate(effect.getEffectName()).trim();
         if (effect.getAmplifier() != 0) {
            displayString = displayString + " " + Strings.translate("potion.potency." + Math.abs(effect.getAmplifier())).trim();
         }

         if (effect.getDuration() > SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER()) {
            if (durationOverride > 0) {
               effect = new PotionEffect(effect.getPotion(), durationOverride);
            }

            displayString = displayString + " (" + Potion.getPotionDurationString(effect, 1.0F) + ")";
         }

         tooltip.add((bad ? TextFormatting.RED : TextFormatting.BLUE) + displayString);
      }

      if (full && attribs != null && !attribs.isEmpty()) {
         addModifierInformation(attribs, tooltip);
      }

   }

   @SideOnly(Side.CLIENT)
   public static final void addPotionInformation(@Nonnull Collection<PotionEffect> effects, @Nonnull List<String> tooltip) {
      addPotionInformation(effects, tooltip, -1, false, false);
   }

   private static final boolean isdefined(ItemStack stack) {
      if (!stack.hasTagCompound()) {
         return false;
      } else {
         PotionType type = PotionUtils.getPotionTypeFromNBT(stack.getTagCompound());
         return type != null && type != PotionTypes.EMPTY || stack.getTagCompound().hasKey(SharedGlue.CUSTOM_POTION_EFFECTS_TAGNAME(), 9) || hasCustomElixirEffects(stack);
      }
   }

   public static final boolean isa(@Nullable ItemStack stack, boolean any) {
      boolean is = false;
      if (!ItemStacks.isEmpty(stack)) {
         Item item = stack.getItem();
         if (item != Items.POTIONITEM && !any) {
            item = null;
         }

         is = item != null && isdefined(stack);
      }

      return is;
   }

   public static final boolean isap(@Nullable ItemStack stack, boolean any) {
      boolean is = false;
      if (!ItemStacks.isEmpty(stack)) {
         Item item = stack.getItem();
         if (item != Items.POTIONITEM && item != Items.SPLASH_POTION && item != Items.LINGERING_POTION && (!any || !RID.matches(stack, "itemPotionBottle"))) {
            item = null;
         }

         is = item != null && isdefined(stack);
      }

      return is;
   }

   public static final void eraseDefinition(ItemStack stack) {
      if (!ItemStacks.isEmpty(stack) && stack.hasTagCompound()) {
         NBTTagCompound current = stack.getTagCompound();
         current.removeTag("Potion");
         current.removeTag(SharedGlue.CUSTOM_POTION_EFFECTS_TAGNAME());
         current.removeTag(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME());
      }

   }

   public static final boolean copyDefinition(ItemStack from, @Nonnull ItemStack to) {
      boolean copied = false;
      if (isa(from, true)) {
         eraseDefinition(to);
         NBTTagCompound data = from.getTagCompound();
         boolean is_registered = data.hasKey("Potion");
         if (is_registered) {
            to.setTagInfo("Potion", data.getTag("Potion").copy());
         }

         if (data.hasKey(SharedGlue.CUSTOM_POTION_EFFECTS_TAGNAME())) {
            to.setTagInfo(SharedGlue.CUSTOM_POTION_EFFECTS_TAGNAME(), data.getTag(SharedGlue.CUSTOM_POTION_EFFECTS_TAGNAME()).copy());
         }

         if (data.hasKey(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME(), 99)) {
            to.setTagInfo(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME(), data.getTag(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME()).copy());
         } else if (!is_registered && to.getItem() instanceof ItemTippedArrow) {
            int color = PotionUtils.getPotionColorFromEffectList(getEffects(from, false));
            if (color != 3694022) {
               to.setTagInfo(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME(), new NBTTagInt(color));
            }
         }

         copied = true;
      }

      return copied;
   }

   public static final void copyDefinition(@Nonnull ItemStack to, @Nonnull Collection<PotionEffect> effects, @Nullable Integer customColor) {
      eraseDefinition(to);
      addCustomEffects(ItemStacks.getTagCompoundNonNull(to), false, effects);
      if (customColor != null) {
         to.setTagInfo(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME(), new NBTTagInt(customColor.intValue()));
      }

   }

   public static final boolean isReal(@Nullable PotionType type) {
      return type == null ? false : type != PotionTypes.EMPTY && type != PotionTypes.WATER && type != PotionTypes.MUNDANE && !type.getEffects().isEmpty();
   }

   @Nullable
   private static final String getCustomUnlocalizedName(ItemStack stack) {
      String name = null;
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey(SharedGlue.CUSTOM_POTION_UNLOCALIZED_TAGNAME(), 8)) {
         name = stack.getTagCompound().getString(SharedGlue.CUSTOM_POTION_UNLOCALIZED_TAGNAME());
      }

      return name;
   }

   @Nullable
   public static final String getTypeDisplayNameOrNull(@Nonnull ItemStack stack) {
      String name = getCustomUnlocalizedName(stack);
      if (name != null) {
         name = Strings.translate(name) + "*";
      } else {
         PotionType type = PotionUtils.getPotionFromItem(stack);
         if (isReal(type)) {
            name = Strings.translate(type.getNamePrefixed("potion.effect."));
         }
      }

      return name;
   }

   @Nullable
   public static final String getTypeTranslationKeyOrNull(@Nonnull ItemStack stack, String prefix) {
      String lookup = null;
      PotionType type = PotionUtils.getPotionFromItem(stack);
      if (type == PotionTypes.EMPTY) {
         lookup = getCustomUnlocalizedName(stack);
      } else if (isReal(type)) {
         if (prefix == null) {
            prefix = "";
         }

         lookup = type.getNamePrefixed(prefix);
         if (!Strings.hasTranslation(lookup)) {
            lookup = null;
         }
      }

      return lookup;
   }

   @Nullable
   public static final Integer getColorOrNull(ItemStack stack, @Nonnull Collection<PotionEffect> effects) {
      Integer color_num = null;
      if (!ItemStacks.isEmpty(stack) && stack.hasTagCompound()) {
         NBTTagCompound data = stack.getTagCompound();
         if (data.hasKey(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME(), 99)) {
            color_num = data.getInteger(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME());
         }
      }

      if (color_num == null && !effects.isEmpty()) {
         color_num = PotionUtils.getPotionColorFromEffectList(effects);
      }

      return color_num;
   }

   @Nullable
   public static final Integer getColorOrNull(ItemStack stack) {
      Integer color_num = null;
      if (!ItemStacks.isEmpty(stack) && stack.hasTagCompound()) {
         NBTTagCompound data = stack.getTagCompound();
         if (data.hasKey(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME(), 99)) {
            color_num = data.getInteger(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME());
         } else {
            color_num = PotionUtils.getPotionColorFromEffectList(PotionUtils.getEffectsFromStack(stack));
         }
      }

      return color_num;
   }

   protected Potions() {
   }

   public static final void init0() {
   }
}
