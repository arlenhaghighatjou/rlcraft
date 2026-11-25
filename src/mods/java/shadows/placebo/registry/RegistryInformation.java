package shadows.placebo.registry;

import java.util.List;
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

public class RegistryInformation {
   protected RegistryList<Block> blocks = new RegistryList<Block>();
   protected RegistryList<Item> items = new RegistryList<Item>();
   protected RegistryList<Potion> potions = new RegistryList<Potion>();
   protected RegistryList<Biome> biomes = new RegistryList<Biome>();
   protected RegistryList<SoundEvent> sounds = new RegistryList<SoundEvent>();
   protected RegistryList<PotionType> potionTypes = new RegistryList<PotionType>();
   protected RegistryList<Enchantment> enchants = new RegistryList<Enchantment>();
   protected RegistryList<VillagerProfession> professions = new RegistryList<VillagerProfession>();
   protected RegistryList<EntityEntry> entities = new RegistryList<EntityEntry>();
   protected RegistryList<IRecipe> recipes = new RegistryList<IRecipe>();
   protected String MODID;
   protected CreativeTabs DEFAULT_TAB;

   public RegistryInformation(String modid, CreativeTabs tab) {
      this.MODID = modid;
      this.DEFAULT_TAB = tab;
   }

   public List<Block> getBlockList() {
      return this.blocks;
   }

   public List<Item> getItemList() {
      return this.items;
   }

   public List<Potion> getPotionList() {
      return this.potions;
   }

   public List<Biome> getBiomeList() {
      return this.biomes;
   }

   public List<SoundEvent> getSoundList() {
      return this.sounds;
   }

   public List<PotionType> getPotionTypeList() {
      return this.potionTypes;
   }

   public List<Enchantment> getEnchantmentList() {
      return this.enchants;
   }

   public List<VillagerProfession> getProfessionList() {
      return this.professions;
   }

   public List<EntityEntry> getEntityEntryList() {
      return this.entities;
   }

   public List<IRecipe> getRecipeList() {
      return this.recipes;
   }

   public String getID() {
      return this.MODID;
   }

   public CreativeTabs getDefaultTab() {
      return this.DEFAULT_TAB;
   }

   public void purge() {
      this.blocks = null;
      this.items = null;
      this.potions = null;
      this.biomes = null;
      this.sounds = null;
      this.potionTypes = null;
      this.enchants = null;
      this.professions = null;
      this.entities = null;
      this.recipes = null;
   }
}
