package noppes.vc;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import noppes.vc.enchants.EnchantConfusion;
import noppes.vc.enchants.EnchantDamage;
import noppes.vc.enchants.EnchantInfinite;
import noppes.vc.enchants.EnchantPoison;
import noppes.vc.enchants.VCEnchant;
import noppes.vc.items.ItemArmorBasic;
import noppes.vc.items.ItemArmorBasicColorable;
import noppes.vc.items.ItemBasic;
import noppes.vc.items.ItemMusic;
import noppes.vc.items.ItemMusicClarinet;
import noppes.vc.items.ItemMusicOracina;
import noppes.vc.items.ItemMusicViolin;
import noppes.vc.items.ItemOrb;
import noppes.vc.items.ItemVCRecipesBook;
import noppes.vc.items.ItemWand;

@ObjectHolder("variedcommodities")
public class VCItems {
   @ObjectHolder("gem_sapphire")
   public static Item gem_sapphire;
   @ObjectHolder("gem_ruby")
   public static Item gem_ruby;
   @ObjectHolder("gem_amethyst")
   public static Item gem_amethyst;
   @ObjectHolder("ingot_bronze")
   public static Item ingot_bronze;
   @ObjectHolder("ingot_steel")
   public static Item ingot_steel;
   @ObjectHolder("ingot_demonic")
   public static Item ingot_demonic;
   @ObjectHolder("ingot_mithril")
   public static Item ingot_mithril;
   @ObjectHolder("banjo")
   public static Item banjo;
   @ObjectHolder("violin")
   public static Item violin;
   @ObjectHolder("violin_bow")
   public static Item violin_bow;
   @ObjectHolder("harp")
   public static Item harp;
   @ObjectHolder("guitar")
   public static Item guitar;
   @ObjectHolder("french_horn")
   public static Item french_horn;
   @ObjectHolder("clarinet")
   public static Item clarinet;
   @ObjectHolder("ocarina")
   public static Item ocarina;
   @ObjectHolder("coin_wood")
   public static Item coin_wood;
   @ObjectHolder("coin_stone")
   public static Item coin_stone;
   @ObjectHolder("coin_iron")
   public static Item coin_iron;
   @ObjectHolder("coin_gold")
   public static Item coin_gold;
   @ObjectHolder("coin_diamond")
   public static Item coin_diamond;
   @ObjectHolder("coin_bronze")
   public static Item coin_bronze;
   @ObjectHolder("coin_emerald")
   public static Item coin_emerald;
   @ObjectHolder("element_earth")
   public static Item element_earth;
   @ObjectHolder("element_water")
   public static Item element_water;
   @ObjectHolder("element_fire")
   public static Item element_fire;
   @ObjectHolder("element_air")
   public static Item element_air;
   @ObjectHolder("ancient_scroll")
   public static Item ancient_scroll;
   @ObjectHolder("ancient_coin")
   public static Item ancient_coin;
   @ObjectHolder("artifact")
   public static Item artifact;
   @ObjectHolder("locket")
   public static Item locket;
   @ObjectHolder("silk")
   public static Item silk;
   @ObjectHolder("statuette")
   public static Item statuette;
   @ObjectHolder("tablet")
   public static Item tablet;
   @ObjectHolder("heart")
   public static Item heart;
   @ObjectHolder("money")
   public static Item money;
   @ObjectHolder("necklace")
   public static Item necklace;
   @ObjectHolder("usb_stick")
   public static Item usb_stick;
   @ObjectHolder("letter")
   public static Item letter;
   @ObjectHolder("plans")
   public static Item plans;
   @ObjectHolder("satchel")
   public static Item satchel;
   @ObjectHolder("bag")
   public static Item bag;
   @ObjectHolder("crystal")
   public static Item crystal;
   @ObjectHolder("severed_ear")
   public static Item severed_ear;
   @ObjectHolder("phone")
   public static Item phone;
   @ObjectHolder("bandit_mask")
   public static Item bandit_mask;
   @ObjectHolder("paper_crown")
   public static Item paper_crown;
   @ObjectHolder("broken_arrow")
   public static Item broken_arrow;
   @ObjectHolder("car_key")
   public static Item car_key;
   @ObjectHolder("key")
   public static Item key;
   @ObjectHolder("key2")
   public static Item key2;
   @ObjectHolder("pendant")
   public static Item pendant;
   @ObjectHolder("blueprint")
   public static Item blueprint;
   @ObjectHolder("ring")
   public static Item ring;
   @ObjectHolder("skull")
   public static Item skull;
   @ObjectHolder("spell_nature")
   public static Item spell_nature;
   @ObjectHolder("spell_arcane")
   public static Item spell_arcane;
   @ObjectHolder("spell_lightning")
   public static Item spell_lightning;
   @ObjectHolder("spell_ice")
   public static Item spell_ice;
   @ObjectHolder("spell_fire")
   public static Item spell_fire;
   @ObjectHolder("spell_dark")
   public static Item spell_dark;
   @ObjectHolder("spell_holy")
   public static Item spell_holy;
   @ObjectHolder("orb")
   public static Item orb;
   @ObjectHolder("orb_broken")
   public static Item orb_broken;
   @ObjectHolder("lighter")
   public static Item lighter;
   @ObjectHolder("chicken_sword")
   public static Item chicken_sword;
   @ObjectHolder("handcuffs")
   public static Item handcuffs;
   @ObjectHolder("magic_wand")
   public static Item magic_wand;
   @ObjectHolder("vcrecipes_book")
   public static Item vcrecipes_book;
   private List<Item> list = new ArrayList();

   public VCItems() {
   }

   @SubscribeEvent
   public void registerItems(Register<Item> event) {
      this.list.clear();
      this.register(event, (new ItemVCRecipesBook()).register("vcrecipes_book"));
      this.register(event, (new ItemBasic()).register("coin_wood"));
      this.register(event, (new ItemBasic()).register("coin_stone"));
      this.register(event, (new ItemBasic()).register("coin_bronze"));
      this.register(event, (new ItemBasic()).register("coin_iron"));
      this.register(event, (new ItemBasic()).register("coin_gold"));
      this.register(event, (new ItemBasic()).register("coin_diamond"));
      this.register(event, (new ItemBasic()).register("coin_emerald"));
      Item gem_sapphire = (new ItemBasic()).register("gem_sapphire");
      Item gem_ruby = (new ItemBasic()).register("gem_ruby");
      Item gem_amethyst = (new ItemBasic()).register("gem_amethyst");
      Item ingot_bronze = (new ItemBasic()).register("ingot_bronze");
      Item ingot_steel = (new ItemBasic()).register("ingot_steel");
      Item ingot_demonic = (new ItemBasic()).register("ingot_demonic");
      Item ingot_mithril = (new ItemBasic()).register("ingot_mithril");
      this.register(event, gem_sapphire);
      this.register(event, gem_ruby);
      this.register(event, gem_amethyst);
      this.register(event, ingot_bronze);
      this.register(event, ingot_steel);
      this.register(event, ingot_demonic);
      this.register(event, ingot_mithril);
      OreDictionary.registerOre("gemSaphire", gem_sapphire);
      OreDictionary.registerOre("gemRuby", gem_ruby);
      OreDictionary.registerOre("gemAmethyst", gem_amethyst);
      OreDictionary.registerOre("ingotBronze", ingot_bronze);
      OreDictionary.registerOre("ingotSteel", ingot_steel);
      OreDictionary.registerOre("ingotDemonic", ingot_demonic);
      OreDictionary.registerOre("ingotMithril", ingot_mithril);
      this.register(event, (new ItemMusic()).register("banjo"));
      this.register(event, (new ItemMusicViolin()).register("violin"));
      this.register(event, (new ItemBasic()).register("violin_bow").setFull3D().setMaxStackSize(1));
      this.register(event, (new ItemMusicViolin()).register("harp"));
      this.register(event, (new ItemMusic()).register("guitar"));
      this.register(event, (new ItemMusic()).setRotated().register("french_horn"));
      this.register(event, (new ItemMusicOracina()).register("ocarina"));
      this.register(event, (new ItemMusicClarinet()).register("clarinet"));
      this.register(event, (new ItemBasic()).register("element_earth"));
      this.register(event, (new ItemBasic()).register("element_water"));
      this.register(event, (new ItemBasic()).register("element_fire"));
      this.register(event, (new ItemBasic()).register("element_air"));
      this.register(event, (new ItemBasic()).register("spell_nature"));
      this.register(event, (new ItemBasic()).register("spell_arcane"));
      this.register(event, (new ItemBasic()).register("spell_lightning"));
      this.register(event, (new ItemBasic()).register("spell_ice"));
      this.register(event, (new ItemBasic()).register("spell_fire"));
      this.register(event, (new ItemBasic()).register("spell_dark"));
      this.register(event, (new ItemBasic()).register("spell_holy"));
      this.register(event, (new ItemOrb()).register("orb"));
      this.register(event, (new ItemOrb()).register("orb_broken"));
      this.register(event, (new ItemBasic()).register("ancient_scroll"));
      this.register(event, (new ItemBasic()).register("artifact"));
      this.register(event, (new ItemBasic()).register("locket"));
      this.register(event, (new ItemBasic()).register("silk"));
      this.register(event, (new ItemBasic()).register("statuette"));
      this.register(event, (new ItemBasic()).register("tablet"));
      this.register(event, (new ItemBasic()).register("heart"));
      this.register(event, (new ItemBasic()).register("money"));
      this.register(event, (new ItemBasic()).register("necklace"));
      this.register(event, (new ItemBasic()).register("usb_stick"));
      this.register(event, (new ItemBasic()).register("ancient_coin"));
      this.register(event, (new ItemBasic()).register("letter"));
      this.register(event, (new ItemBasic()).register("plans"));
      this.register(event, (new ItemBasic()).register("satchel"));
      this.register(event, (new ItemBasic()).register("bag"));
      this.register(event, (new ItemBasic()).register("crystal"));
      this.register(event, (new ItemBasic()).register("severed_ear"));
      this.register(event, (new ItemBasic()).register("phone"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, "bandit_mask")).register("bandit_mask").setMaxStackSize(64).setCreativeTab(VCTabs.ITEMS));
      this.register(event, (new ItemArmorBasicColorable(ArmorMaterial.LEATHER, EntityEquipmentSlot.HEAD, "paper_crown")).register("paper_crown").setMaxStackSize(1).setCreativeTab(VCTabs.ITEMS));
      this.register(event, (new ItemBasic()).register("broken_arrow"));
      this.register(event, (new ItemBasic()).register("car_key"));
      this.register(event, (new ItemBasic()).register("key"));
      this.register(event, (new ItemBasic()).register("key2"));
      this.register(event, (new ItemBasic()).register("pendant"));
      this.register(event, (new ItemBasic()).register("blueprint"));
      this.register(event, (new ItemBasic()).register("ring"));
      this.register(event, (new ItemBasic()).register("skull"));
      this.register(event, ((Item)(new ItemFlintAndSteel()).setUnlocalizedName("lighter").setRegistryName("variedcommodities", "lighter")).setMaxStackSize(1));
      this.register(event, (new ItemBasic()).register("chicken_sword").setFull3D().setMaxStackSize(1));
      this.register(event, (new ItemBasic()).register("handcuffs").setFull3D().setMaxStackSize(1));
      this.register(event, (new ItemWand()).register("magic_wand").setFull3D().setMaxStackSize(1));
   }

   @SubscribeEvent
   public void registerEnchants(Register<Enchantment> event) {
      if (!VariedCommodities.DisableEnchantments) {
         VCEnchant.CustomNpcsType = EnumHelper.addEnchantmentType("vc_enchants", (Predicate)null);
         event.getRegistry().registerAll(new Enchantment[]{VCEnchant.Damage = new EnchantDamage(), VCEnchant.Poison = new EnchantPoison(), VCEnchant.Confusion = new EnchantConfusion(), VCEnchant.Infinite = new EnchantInfinite()});
      }

   }

   private void register(Register<Item> event, Item item) {
      event.getRegistry().register(item);
      this.list.add(item);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void registerModels(ModelRegistryEvent event) {
      for(Item item : this.list) {
         ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
      }

      for(int i = 1; i < 16; ++i) {
         ModelLoader.setCustomModelResourceLocation(orb, i, new ModelResourceLocation(orb.getRegistryName(), "inventory"));
         ModelLoader.setCustomModelResourceLocation(orb_broken, i, new ModelResourceLocation(orb_broken.getRegistryName(), "inventory"));
      }

   }
}
