package svenhjol.charm.smithing.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.meson.Feature;

public class ExtractEnchantments extends Feature {
   public static int baseCost;
   public static int weaken;

   public ExtractEnchantments() {
   }

   public String getDescription() {
      return "Extract enchantments from any enchanted item into an empty book.\nThe enchantments are weakened in the process, and the original item is destroyed.";
   }

   public void configure() {
      super.configure();
      baseCost = this.propInt("Base XP cost", "The minimum XP cost before adding XP equivalent to the enchantment level(s) of the item.", 1);
      weaken = this.propInt("Weaken by amount", "Number of levels that enchantments are weakened when converting.", 1);
   }

   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack in = event.getLeft();
      ItemStack combine = event.getRight();
      if (!in.isEmpty() && !combine.isEmpty() && combine.getItem() == Items.BOOK) {
         NBTTagList inTags = in.getEnchantmentTagList();
         NBTTagList combineTags = combine.getEnchantmentTagList();
         if (!inTags.hasNoTags() && combineTags.hasNoTags()) {
            int cost = baseCost;
            int numEnchantments = 0;
            Map<Enchantment, Integer> inEnchants = EnchantmentHelper.getEnchantments(in);
            Map<Enchantment, Integer> outEnchants = new HashMap();

            for(Entry<Enchantment, Integer> entry : inEnchants.entrySet()) {
               Enchantment enchantment = (Enchantment)entry.getKey();
               if (enchantment != null) {
                  int level = ((Integer)entry.getValue()).intValue();
                  int w = level - weaken;
                  if (w > 0) {
                     ++numEnchantments;
                     outEnchants.put(enchantment, Integer.valueOf(w));
                     cost += w;
                  }
               }
            }

            if (numEnchantments == 0) {
               event.setCanceled(true);
               return;
            }

            if (in.getTagCompound() != null && !in.getTagCompound().hasNoTags()) {
               cost += in.getTagCompound().getInteger("RepairCost");
            }

            ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.setEnchantments(outEnchants, out);
            String name = event.getName();
            if (!name.isEmpty()) {
               out.setStackDisplayName(name);
            }

            event.setCost(cost);
            event.setMaterialCost(1);
            event.setOutput(out);
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
