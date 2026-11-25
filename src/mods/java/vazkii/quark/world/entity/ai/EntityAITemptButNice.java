package vazkii.quark.world.entity.ai;

import java.util.Calendar;
import java.util.Set;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.quark.world.feature.Frogs;

public class EntityAITemptButNice extends EntityAITempt {
   private final Set<Item> temptItemNice;
   private final EntityCreature nice;

   public EntityAITemptButNice(EntityCreature temptedEntityIn, double speedIn, boolean scaredByPlayerMovementIn, Set<Item> temptItemIn, Set<Item> temptItemNice) {
      super(temptedEntityIn, speedIn, scaredByPlayerMovementIn, temptItemIn);
      this.temptItemNice = temptItemNice;
      this.nice = temptedEntityIn;
   }

   protected boolean isTempting(ItemStack stack) {
      Calendar calendar = this.nice.world.getCurrentDate();
      return Frogs.frogsDoTheFunny && calendar.get(7) == 4 ? this.temptItemNice.contains(stack.getItem()) : super.isTempting(stack);
   }
}
