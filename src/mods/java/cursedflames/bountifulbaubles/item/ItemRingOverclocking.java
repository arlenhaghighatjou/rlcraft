package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.baubleeffect.PotionNegation;
import cursedflames.bountifulbaubles.item.base.AGenericItemBauble;
import cursedflames.bountifulbaubles.item.base.IItemAttributeModifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

public class ItemRingOverclocking extends AGenericItemBauble implements IItemAttributeModifier, PotionNegation.IPotionNegateItem {
   public static final UUID SPEED_UUID = UUID.fromString("067d9c52-5ffb-4fad-b581-f17ecc799549");
   private static final Map<IAttribute, AttributeModifier> modMap = new HashMap();

   public ItemRingOverclocking() {
      super("ringOverclocking", BountifulBaubles.TAB);
      BountifulBaubles.registryHelper.addItemModel(this);
   }

   public BaubleType getBaubleType(ItemStack stack) {
      return BaubleType.RING;
   }

   public Map<IAttribute, AttributeModifier> getModifiers(ItemStack stack, EntityPlayer player) {
      return modMap;
   }

   public void onWornTick(ItemStack stack, EntityLivingBase player) {
      PotionNegation.negatePotion(player, Potion.getPotionFromResourceLocation("minecraft:slowness"));
   }

   public List<String> getCureEffects() {
      return Arrays.asList("minecraft:slowness");
   }

   static {
      modMap.put(SharedMonsterAttributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_UUID, "Ring of Overclocking speed", 0.07D, 2));
   }
}
