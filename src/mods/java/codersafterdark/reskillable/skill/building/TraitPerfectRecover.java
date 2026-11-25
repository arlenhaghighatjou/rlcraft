package codersafterdark.reskillable.skill.building;

import codersafterdark.reskillable.api.unlockable.Trait;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TraitPerfectRecover extends Trait {
   private Item glowstone;
   private Item lantern;

   public TraitPerfectRecover() {
      super(new ResourceLocation("reskillable", "perfect_recover"), 1, 1, new ResourceLocation("reskillable", "building"), 4, "reskillable:building|8", "reskillable:gathering|4", "reskillable:mining|6");
   }

   public void onBlockDrops(HarvestDropsEvent event) {
      if (event.getState().getBlock() == Blocks.GLOWSTONE) {
         if (!((Boolean)event.getDrops().stream().map((stack) -> {
            return stack.getItem() == this.getGlowstone();
         }).reduce(Boolean.valueOf(false), (a, b) -> {
            return a.booleanValue() || b.booleanValue();
         })).booleanValue()) {
            event.getDrops().removeIf((s) -> {
               return s.getItem() == Items.GLOWSTONE_DUST;
            });
            event.getDrops().add(new ItemStack(Items.GLOWSTONE_DUST, 4));
         }
      } else if (event.getState().getBlock() == Blocks.SEA_LANTERN && !((Boolean)event.getDrops().stream().map((stack) -> {
         return stack.getItem() == this.getSeaLantern();
      }).reduce(Boolean.valueOf(false), (a, b) -> {
         return a.booleanValue() || b.booleanValue();
      })).booleanValue()) {
         event.getDrops().removeIf((s) -> {
            return s.getItem() == Items.PRISMARINE_CRYSTALS;
         });
         event.getDrops().add(new ItemStack(Items.PRISMARINE_CRYSTALS, 5));
         event.getDrops().add(new ItemStack(Items.PRISMARINE_SHARD, 4));
      }

   }

   private Item getGlowstone() {
      if (this.glowstone == null) {
         this.glowstone = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:glowstone"));
      }

      return this.glowstone;
   }

   private Item getSeaLantern() {
      if (this.lantern == null) {
         this.lantern = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:sea_lantern"));
      }

      return this.lantern;
   }
}
