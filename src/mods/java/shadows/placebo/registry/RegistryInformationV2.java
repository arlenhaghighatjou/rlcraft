package shadows.placebo.registry;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import shadows.placebo.util.collections.RegistryList;

public class RegistryInformationV2 extends RegistryInformation {
   public RegistryInformationV2(String modid, CreativeTabs tab) {
      super(modid, tab);
   }

   public RegistryList<Block> getBlockList() {
      return this.blocks;
   }

   public RegistryList<Item> getItemList() {
      return this.items;
   }

   public RegistryList<Potion> getPotionList() {
      return this.potions;
   }

   public RegistryList<Biome> getBiomeList() {
      return this.biomes;
   }

   public RegistryList<SoundEvent> getSoundList() {
      return this.sounds;
   }

   public RegistryList<PotionType> getPotionTypeList() {
      return this.potionTypes;
   }

   public RegistryList<Enchantment> getEnchantmentList() {
      return this.enchants;
   }

   public RegistryList<VillagerProfession> getProfessionList() {
      return this.professions;
   }

   public RegistryList<EntityEntry> getEntityEntryList() {
      return this.entities;
   }

   public RegistryList<IRecipe> getRecipeList() {
      return this.recipes;
   }
}
