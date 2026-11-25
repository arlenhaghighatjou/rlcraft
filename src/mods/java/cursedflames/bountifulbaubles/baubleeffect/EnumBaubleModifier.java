package cursedflames.bountifulbaubles.baubleeffect;

import baubles.api.cap.BaublesCapabilities;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public enum EnumBaubleModifier {
   NONE("none", (IAttribute)null, 0.0D, 0, 5),
   HALF_HEARTED("half_hearted", SharedMonsterAttributes.MAX_HEALTH, 1.0D, 0, 3),
   HEARTY("hearty", SharedMonsterAttributes.MAX_HEALTH, 2.0D, 0, 1),
   HARD("hard", SharedMonsterAttributes.ARMOR, 1.0D, 0, 3),
   GUARDING("guarding", SharedMonsterAttributes.ARMOR, 1.5D, 0, 2),
   ARMORED("armored", SharedMonsterAttributes.ARMOR, 2.0D, 0, 1),
   WARDING("warding", SharedMonsterAttributes.ARMOR_TOUGHNESS, 1.0D, 0, 2),
   JAGGED("jagged", SharedMonsterAttributes.ATTACK_DAMAGE, 0.02D, 2, 2),
   SPIKED("spiked", SharedMonsterAttributes.ATTACK_DAMAGE, 0.04D, 2, 2),
   ANGRY("angry", SharedMonsterAttributes.ATTACK_DAMAGE, 0.06D, 2, 1),
   MENACING("menacing", SharedMonsterAttributes.ATTACK_DAMAGE, 0.08D, 2, 1),
   BRISK("brisk", SharedMonsterAttributes.MOVEMENT_SPEED, 0.01D, 2, 2),
   FLEETING("fleeting", SharedMonsterAttributes.MOVEMENT_SPEED, 0.02D, 2, 2),
   HASTY("hasty", SharedMonsterAttributes.MOVEMENT_SPEED, 0.03D, 2, 1),
   QUICK("quick", SharedMonsterAttributes.MOVEMENT_SPEED, 0.04D, 2, 1),
   WILD("wild", SharedMonsterAttributes.ATTACK_SPEED, 0.02D, 2, 2),
   RASH("rash", SharedMonsterAttributes.ATTACK_SPEED, 0.04D, 2, 2),
   INTREPID("intrepid", SharedMonsterAttributes.ATTACK_SPEED, 0.06D, 2, 1),
   VIOLENT("violent", SharedMonsterAttributes.ATTACK_SPEED, 0.08D, 2, 1);

   public final String name;
   public final IAttribute attribute;
   public final double amount;
   public final int operation;
   public final int weight;
   public static final Set<IAttribute> attributes = new HashSet();
   private static final List<EnumBaubleModifier> VALUES;
   private static final int TOTAL_WEIGHT;
   private static final EaglercraftRandom RANDOM;

   private EnumBaubleModifier(String name, IAttribute attribute, double amount, int operation, int weight) {
      this.name = name;
      this.attribute = attribute;
      this.amount = amount;
      this.operation = operation;
      this.weight = weight;
   }

   private static final int getTotalWeight() {
      int total = 0;

      for(EnumBaubleModifier mod : VALUES) {
         total += mod.weight;
      }

      return total;
   }

   public static EnumBaubleModifier getWeightedRandom() {
      int rand = RANDOM.nextInt(TOTAL_WEIGHT);
      int currentWeight = 0;

      for(EnumBaubleModifier mod : VALUES) {
         currentWeight += mod.weight;
         if (rand < currentWeight) {
            return mod;
         }
      }

      return NONE;
   }

   public static void generateModifier(ItemStack stack) {
      if (stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
         if (stack.getMaxStackSize() <= 1) {
            getWeightedRandom().addTo(stack);
         }
      }
   }

   public void addTo(ItemStack stack) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      NBTTagCompound tag = stack.getTagCompound();
      tag.setString("baubleModifier", this.name);
   }

   public static EnumBaubleModifier get(String str) {
      try {
         return valueOf(str.toUpperCase(Locale.ENGLISH));
      } catch (Exception var2) {
         return null;
      }
   }

   static {
      for(EnumBaubleModifier mod : values()) {
         if (mod.attribute != null) {
            attributes.add(mod.attribute);
         }
      }

      VALUES = Collections.unmodifiableList(Arrays.asList(values()));
      TOTAL_WEIGHT = getTotalWeight();
      RANDOM = new EaglercraftRandom();
   }
}
