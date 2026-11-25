package rlmixins.potion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;

public class PotionCurseBreak extends PotionBase {
   public static final PotionCurseBreak INSTANCE = new PotionCurseBreak();

   public PotionCurseBreak() {
      super("curse_break", false, 3448033);
   }

   public boolean isInstant() {
      return true;
   }

   public boolean isReady(int duration, int amplifier) {
      return duration == 1;
   }

   public void affectEntity(Entity source, Entity indirectSource, EntityLivingBase entityLivingBaseIn, int amplifier, double health) {
      this.performEffect(entityLivingBaseIn, amplifier);
   }

   public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
      if (entityLivingBaseIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
         InventoryPlayer inv = player.inventory;
         List<ItemStack> candidates = new ArrayList();

         for(ItemStack cur : inv.offHandInventory) {
            if (!cur.isEmpty() && !(cur.getItem() instanceof ItemEnchantedBook) && cur.getTagCompound() != null) {
               candidates.add(cur);
            }
         }

         for(ItemStack cur : inv.armorInventory) {
            if (!cur.isEmpty() && !(cur.getItem() instanceof ItemEnchantedBook) && cur.getTagCompound() != null) {
               candidates.add(cur);
            }
         }

         for(ItemStack cur : inv.mainInventory) {
            if (!cur.isEmpty() && !(cur.getItem() instanceof ItemEnchantedBook) && cur.getTagCompound() != null) {
               candidates.add(cur);
            }
         }

         if (!candidates.isEmpty()) {
            Collections.shuffle(candidates);

            for(ItemStack cur : candidates) {
               Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(cur);
               Iterator<Enchantment> iter = enchants.keySet().iterator();
               boolean removed = false;

               while(iter.hasNext()) {
                  Enchantment ench = (Enchantment)iter.next();
                  if (ench != null && ench.isCurse()) {
                     iter.remove();
                     removed = true;
                     break;
                  }
               }

               if (removed) {
                  EnchantmentHelper.setEnchantments(enchants, cur);
                  return;
               }
            }

         }
      }
   }
}
