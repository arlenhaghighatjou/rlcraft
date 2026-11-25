package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.base.AGenericItemBauble;
import cursedflames.bountifulbaubles.item.base.IItemAttributeModifier;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemCharmHermesWings extends AGenericItemBauble implements IItemAttributeModifier {
   public static final EaglercraftUUID SPEED_UUID = EaglercraftUUID.fromString("ca402d96-9780-4c4c-81b9-c06a001d9a11");
   private static final Map<IAttribute, AttributeModifier> modMap = new HashMap();

   public ItemCharmHermesWings() {
      super("charmHermesWings", BountifulBaubles.TAB);
      BountifulBaubles.registryHelper.addItemModel(this);
      this.setMaxStackSize(1);
   }

   public BaubleType getBaubleType(ItemStack stack) {
      return BaubleType.CHARM;
   }

   public Map<IAttribute, AttributeModifier> getModifiers(ItemStack stack, EntityPlayer player) {
      return modMap;
   }

   static {
      modMap.put(SharedMonsterAttributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_UUID, "Hermes Wing Charm Speed", 0.2D, 2));
   }
}
