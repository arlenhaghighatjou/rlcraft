package noppes.vc;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
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

public class VCItems {
   public static Item gem_sapphire;
   public static Item gem_ruby;
   public static Item gem_amethyst;
   public static Item ingot_bronze;
   public static Item ingot_steel;
   public static Item ingot_demonic;
   public static Item ingot_mithril;
   public static Item banjo;
   public static Item violin;
   public static Item violin_bow;
   public static Item harp;
   public static Item guitar;
   public static Item french_horn;
   public static Item clarinet;
   public static Item ocarina;
   public static Item coin_wood;
   public static Item coin_stone;
   public static Item coin_iron;
   public static Item coin_gold;
   public static Item coin_diamond;
   public static Item coin_bronze;
   public static Item coin_emerald;
   public static Item element_earth;
   public static Item element_water;
   public static Item element_fire;
   public static Item element_air;
   public static Item ancient_scroll;
   public static Item ancient_coin;
   public static Item artifact;
   public static Item locket;
   public static Item silk;
   public static Item statuette;
   public static Item tablet;
   public static Item heart;
   public static Item money;
   public static Item necklace;
   public static Item usb_stick;
   public static Item letter;
   public static Item plans;
   public static Item satchel;
   public static Item bag;
   public static Item crystal;
   public static Item severed_ear;
   public static Item phone;
   public static Item bandit_mask;
   public static Item paper_crown;
   public static Item broken_arrow;
   public static Item car_key;
   public static Item key;
   public static Item key2;
   public static Item pendant;
   public static Item blueprint;
   public static Item ring;
   public static Item skull;
   public static Item spell_nature;
   public static Item spell_arcane;
   public static Item spell_lightning;
   public static Item spell_ice;
   public static Item spell_fire;
   public static Item spell_dark;
   public static Item spell_holy;
   public static Item orb;
   public static Item orb_broken;
   public static Item lighter;
   public static Item chicken_sword;
   public static Item handcuffs;
   public static Item magic_wand;
   public static Item vcrecipes_book;
   private List<Item> list = new ArrayList();

   public VCItems() {
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, this::registerItems);
      bus.registerGeneric(Register.class, Enchantment.class, this::registerEnchants);
      bus.register(ModelRegistryEvent.class, this::registerModels);
   }

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
   public void registerModels(ModelRegistryEvent event) {
      for(Item item : this.list) {
         ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
      }

      for(int i = 1; i < 16; ++i) {
         ModelLoader.setCustomModelResourceLocation(orb, i, new ModelResourceLocation(orb.getRegistryName(), "inventory"));
         ModelLoader.setCustomModelResourceLocation(orb_broken, i, new ModelResourceLocation(orb_broken.getRegistryName(), "inventory"));
      }

   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:gem_sapphire"), v -> gem_sapphire = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:gem_ruby"), v -> gem_ruby = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:gem_amethyst"), v -> gem_amethyst = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:ingot_bronze"), v -> ingot_bronze = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:ingot_steel"), v -> ingot_steel = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:ingot_demonic"), v -> ingot_demonic = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:ingot_mithril"), v -> ingot_mithril = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:banjo"), v -> banjo = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:violin"), v -> violin = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:violin_bow"), v -> violin_bow = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:harp"), v -> harp = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:guitar"), v -> guitar = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:french_horn"), v -> french_horn = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:clarinet"), v -> clarinet = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:ocarina"), v -> ocarina = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:coin_wood"), v -> coin_wood = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:coin_stone"), v -> coin_stone = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:coin_iron"), v -> coin_iron = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:coin_gold"), v -> coin_gold = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:coin_diamond"), v -> coin_diamond = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:coin_bronze"), v -> coin_bronze = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:coin_emerald"), v -> coin_emerald = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:element_earth"), v -> element_earth = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:element_water"), v -> element_water = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:element_fire"), v -> element_fire = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:element_air"), v -> element_air = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:ancient_scroll"), v -> ancient_scroll = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:ancient_coin"), v -> ancient_coin = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:artifact"), v -> artifact = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:locket"), v -> locket = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:silk"), v -> silk = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:statuette"), v -> statuette = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:tablet"), v -> tablet = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:heart"), v -> heart = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:money"), v -> money = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:necklace"), v -> necklace = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:usb_stick"), v -> usb_stick = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:letter"), v -> letter = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:plans"), v -> plans = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:satchel"), v -> satchel = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:bag"), v -> bag = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:crystal"), v -> crystal = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:severed_ear"), v -> severed_ear = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:phone"), v -> phone = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:bandit_mask"), v -> bandit_mask = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:paper_crown"), v -> paper_crown = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:broken_arrow"), v -> broken_arrow = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:car_key"), v -> car_key = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:key"), v -> key = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:key2"), v -> key2 = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:pendant"), v -> pendant = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:blueprint"), v -> blueprint = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:ring"), v -> ring = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:skull"), v -> skull = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:spell_nature"), v -> spell_nature = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:spell_arcane"), v -> spell_arcane = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:spell_lightning"), v -> spell_lightning = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:spell_ice"), v -> spell_ice = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:spell_fire"), v -> spell_fire = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:spell_dark"), v -> spell_dark = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:spell_holy"), v -> spell_holy = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:orb"), v -> orb = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:orb_broken"), v -> orb_broken = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:lighter"), v -> lighter = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:chicken_sword"), v -> chicken_sword = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:handcuffs"), v -> handcuffs = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:magic_wand"), v -> magic_wand = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("variedcommodities:vcrecipes_book"), v -> vcrecipes_book = v);
   }
}
