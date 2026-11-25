package artifacts.common.item;

import artifacts.common.ModConfig;
import artifacts.common.util.BaubleHelper;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class AttributeModifierBauble extends BaubleBase {
   private final Set<AttributeModifierBauble.ExtendedAttributeModifier> attributeModifiers;

   public AttributeModifierBauble(String name, BaubleType type, AttributeModifierBauble.ExtendedAttributeModifier... attributeModifiers) {
      super(name, type);
      this.attributeModifiers = new HashSet(Arrays.asList(attributeModifiers));
   }

   public void onEquipped(ItemStack stack, EntityLivingBase player) {
      super.onEquipped(stack, player);
      if (player instanceof EntityPlayer) {
         this.applyModifiers((ItemStack)null, (EntityPlayer)player);
      }

   }

   public void onUnequipped(ItemStack stack, EntityLivingBase player) {
      super.onUnequipped(stack, player);
      if (player instanceof EntityPlayer) {
         this.applyModifiers(stack, (EntityPlayer)player);
      }

   }

   private void applyModifiers(@Nullable ItemStack excludedStack, EntityPlayer player) {
      IBaublesItemHandler baublesHandler = BaublesApi.getBaublesHandler(player);
      Set<AttributeModifierBauble.ExtendedAttributeModifier> modifiers = new HashSet();

      for(int slot : BaubleType.TRINKET.getValidSlots()) {
         ItemStack stack = baublesHandler.getStackInSlot(slot);
         if (stack.getItem() instanceof AttributeModifierBauble && stack != excludedStack) {
            modifiers.addAll(((AttributeModifierBauble)stack.getItem()).attributeModifiers);
         }
      }

      modifiers.retainAll(this.attributeModifiers);

      for(AttributeModifierBauble.ExtendedAttributeModifier modifier : this.attributeModifiers) {
         IAttributeInstance instance = player.getAttributeMap().getAttributeInstance(modifier.affectedAttribute);
         if (instance.getModifier(modifier.id) != null) {
            instance.removeModifier(modifier.id);
         }
      }

      int amount = BaubleHelper.getAmountBaubleEquipped(player, this);

      for(AttributeModifierBauble.ExtendedAttributeModifier modifier : modifiers) {
         player.getAttributeMap().getAttributeInstance(modifier.affectedAttribute).applyModifier(new AttributeModifier(modifier.id, modifier.name, modifier.getValue() * (double)amount, modifier.getOperation()));
      }

   }

   public static class ExtendedAttributeModifier {
      public final UUID id;
      public final String name;
      public final IAttribute affectedAttribute;

      public ExtendedAttributeModifier(UUID id, String nameIn, IAttribute affectedAttribute) {
         this.id = id;
         this.name = nameIn;
         this.affectedAttribute = affectedAttribute;
      }

      public double getValue() {
         if (this.affectedAttribute == SharedMonsterAttributes.ATTACK_DAMAGE) {
            return ModConfig.general.attackDamageBoost;
         } else if (this.affectedAttribute == SharedMonsterAttributes.ATTACK_SPEED) {
            return ModConfig.general.attackSpeedBoost;
         } else {
            return this.affectedAttribute == SharedMonsterAttributes.LUCK ? ModConfig.general.luckBoost : 0.0D;
         }
      }

      public int getOperation() {
         if (this.affectedAttribute == SharedMonsterAttributes.ATTACK_DAMAGE) {
            return ModConfig.general.attackDamageOperation;
         } else if (this.affectedAttribute == SharedMonsterAttributes.ATTACK_SPEED) {
            return ModConfig.general.attackSpeedOperation;
         } else {
            return this.affectedAttribute == SharedMonsterAttributes.LUCK ? ModConfig.general.luckOperation : 0;
         }
      }
   }
}
