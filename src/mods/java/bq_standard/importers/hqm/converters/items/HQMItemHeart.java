package bq_standard.importers.hqm.converters.items;

import betterquesting.api.utils.BigItemStack;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class HQMItemHeart implements HQMItem {
   private final Item bqHeart = (Item)Item.REGISTRY.getObject(new ResourceLocation("betterquesting:extra_life"));

   public HQMItemHeart() {
   }

   public BigItemStack convertItem(int damage, int amount, NBTTagCompound tags) {
      int amt = amount;
      int dmg = 0;
      switch(damage) {
      case 0:
         dmg = 2;
         break;
      case 1:
         dmg = 1;
         break;
      case 2:
         dmg = 2;
         amt = amount * 3;
      }

      return new BigItemStack(this.bqHeart, amt, dmg);
   }
}
