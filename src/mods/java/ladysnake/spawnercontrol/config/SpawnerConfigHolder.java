package ladysnake.spawnercontrol.config;

import ladysnake.spawnercontrol.SpawnerControl;
import ladysnake.spawnercontrol.controlledspawner.BlockControlledSpawner;
import ladysnake.spawnercontrol.controlledspawner.ItemBlockControlSpawner;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SpawnerConfigHolder {
   private Configuration configuration;
   private String name;
   private ResourceLocation registryName;
   private SpawnerConfig configObject;

   SpawnerConfigHolder(Configuration configuration, String name) {
      this.configuration = configuration;
      this.name = name;
      this.registryName = new ResourceLocation("spawnercontrol", name.replaceAll(" ", "_"));
      this.configObject = new SpawnerConfig();
   }

   public void sync() {
      try {
         CustomSpawnersConfig.configManager$sync.invoke(this.configuration, SpawnerConfig.class, "spawnercontrol", this.name, true, this.configObject);
         this.configObject.mobLoot.lootEntries.invalidateAll();
         this.configuration.save();
      } catch (Throwable var2) {
         SpawnerControl.LOGGER.error("Exception while synchronizing custom spawner config", var2);
      }

   }

   public Block createBlock() {
      return ((Block)(new BlockControlledSpawner(this.getConfigObject())).setRegistryName(this.registryName)).setUnlocalizedName("msc." + this.name).setCreativeTab(SpawnerControl.creativeTab);
   }

   public Item createItem() {
      return (Item)(new ItemBlockControlSpawner(this.getBlock(), this.getName())).setRegistryName(this.registryName);
   }

   public Block getBlock() {
      return (Block)ForgeRegistries.BLOCKS.getValue(this.registryName);
   }

   public Item getItem() {
      return (Item)ForgeRegistries.ITEMS.getValue(this.registryName);
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public ConfigCategory getConfigCategory() {
      return this.getConfiguration().getCategory(this.name);
   }

   public String getName() {
      return this.name;
   }

   public ResourceLocation getRegistryName() {
      return this.registryName;
   }

   public SpawnerConfig getConfigObject() {
      return this.configObject;
   }
}
