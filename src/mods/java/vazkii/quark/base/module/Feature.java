package vazkii.quark.base.module;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.text.WordUtils;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.api.module.IFeature;
import vazkii.quark.api.module.IModule;

public class Feature implements IFeature {
   public Module module;
   public boolean loadtimeDone;
   public boolean enabledAtLoadtime;
   public boolean enabledByDefault;
   public boolean enabled;
   public boolean prevEnabled;
   public String configCategory;
   public String configName;
   public Property prop;
   public boolean forceLoad;

   public Feature() {
   }

   public final void setupConstantConfig() {
      String[] incompat = this.getIncompatibleMods();
      if (incompat != null && incompat.length > 0) {
         StringBuilder desc = new StringBuilder("This feature disables itself if any of the following mods are loaded: \n");

         for(String s : incompat) {
            desc.append(" - ").append(s).append("\n");
         }

         desc.append("This is done to prevent content overlap.\nYou can turn this on to force the feature to be loaded even if the above mods are also loaded.");
         ConfigHelper.needsRestart = true;
         this.forceLoad = this.loadPropBool("Force Enabled", desc.toString(), false);
      }

   }

   public void setupConfig() {
   }

   public void onEnabled() {
   }

   public void onDisabled() {
   }

   public void preInit(FMLPreInitializationEvent event) {
   }

   public void postPreInit() {
   }

   public void init() {
   }

   public void postInit() {
   }

   public void finalInit() {
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
   }

   @SideOnly(Side.CLIENT)
   public void initClient() {
   }

   @SideOnly(Side.CLIENT)
   public void postInitClient() {
   }

   public void serverStarting() {
   }

   public String[] getIncompatibleMods() {
      return null;
   }

   public boolean hasSubscriptions() {
      return false;
   }

   public boolean hasTerrainSubscriptions() {
      return false;
   }

   public boolean hasOreGenSubscriptions() {
      return false;
   }

   public String getFeatureDescription() {
      return "";
   }

   public String getFeatureIngameConfigName() {
      return WordUtils.capitalizeFully(this.configName);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return false;
   }

   public static void registerTile(Class<? extends TileEntity> clazz, String key) {
      GameRegistry.registerTileEntity(clazz, new ResourceLocation("quark", key));
   }

   public static void addOreDict(String key, Item value) {
      addOreDict(key, ProxyRegistry.newStack(value));
   }

   public static void addOreDict(String key, Block value) {
      addOreDict(key, ProxyRegistry.newStack(value));
   }

   public static void addOreDict(String key, ItemStack value) {
      ModuleLoader.lazyOreDictRegisters.add(() -> {
         OreDictionary.registerOre(key, value);
      });
   }

   public final boolean isClient() {
      return FMLCommonHandler.instance().getSide().isClient();
   }

   public final boolean hasConfigKey(String propName) {
      return ConfigHelper.hasConfigKey(propName, this.configCategory);
   }

   public final void removeLegacyKey(String propName) {
      ConfigHelper.removeKey(propName, this.configCategory);
   }

   public final int loadPropInt(String propName, String desc, int default_) {
      return ConfigHelper.loadPropInt(propName, this.configCategory, desc, default_);
   }

   public final double loadPropDouble(String propName, String desc, double default_) {
      return ConfigHelper.loadPropDouble(propName, this.configCategory, desc, default_);
   }

   public final boolean loadPropBool(String propName, String desc, boolean default_) {
      return ConfigHelper.loadPropBool(propName, this.configCategory, desc, default_);
   }

   public final String loadPropString(String propName, String desc, String default_) {
      return ConfigHelper.loadPropString(propName, this.configCategory, desc, default_);
   }

   public final String[] loadPropStringList(String propName, String desc, String[] default_) {
      return ConfigHelper.loadPropStringList(propName, this.configCategory, desc, default_);
   }

   public final double loadPropChance(String propName, String desc, double default_) {
      return ConfigHelper.loadPropChance(propName, this.configCategory, desc, default_);
   }

   public final double loadLegacyPropChance(String propName, String oldName, String desc, double default_) {
      return ConfigHelper.loadLegacyPropChance(propName, this.configCategory, oldName, desc, default_);
   }

   public final void initializeEnchantmentList(String[] enchantNames, List<Enchantment> enchants) {
      enchants.clear();

      for(String s : enchantNames) {
         ResourceLocation r = new ResourceLocation(s);
         Enchantment e = (Enchantment)Enchantment.REGISTRY.getObject(r);
         if (e != null) {
            enchants.add(e);
         }
      }

   }

   public static boolean isVanilla(IForgeRegistryEntry entry) {
      ResourceLocation loc = entry.getRegistryName();
      return loc != null && loc.getResourceDomain().equals("minecraft");
   }

   public IModule getModule() {
      return this.module;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String getName() {
      return this.configName;
   }
}
