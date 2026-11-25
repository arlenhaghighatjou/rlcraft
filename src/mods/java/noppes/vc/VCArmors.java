package noppes.vc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.items.ItemArmorBasic;
import noppes.vc.items.ItemArmorSkirt;

@ObjectHolder("variedcommodities")
public class VCArmors {
   @ObjectHolder("cow_leather_head")
   public static Item cow_leather_head;
   @ObjectHolder("cow_leather_chest")
   public static Item cow_leather_chest;
   @ObjectHolder("cow_leather_legs")
   public static Item cow_leather_legs;
   @ObjectHolder("cow_leather_boots")
   public static Item cow_leather_boots;
   @ObjectHolder("nanorum_head")
   public static Item nanorum_head;
   @ObjectHolder("nanorum_chest")
   public static Item nanorum_chest;
   @ObjectHolder("nanorum_legs")
   public static Item nanorum_legs;
   @ObjectHolder("nanorum_boots")
   public static Item nanorum_boots;
   @ObjectHolder("tactical_head")
   public static Item tactical_head;
   @ObjectHolder("tactical_chest")
   public static Item tactical_chest;
   @ObjectHolder("full_leather_head")
   public static Item full_leather_head;
   @ObjectHolder("full_leather_chest")
   public static Item full_leather_chest;
   @ObjectHolder("full_iron_head")
   public static Item full_iron_head;
   @ObjectHolder("full_iron_chest")
   public static Item full_iron_chest;
   @ObjectHolder("full_golden_head")
   public static Item full_golden_head;
   @ObjectHolder("full_golden_chest")
   public static Item full_golden_chest;
   @ObjectHolder("full_diamond_head")
   public static Item full_diamond_head;
   @ObjectHolder("full_diamond_chest")
   public static Item full_diamond_chest;
   @ObjectHolder("full_bronze_head")
   public static Item full_bronze_head;
   @ObjectHolder("full_bronze_chest")
   public static Item full_bronze_chest;
   @ObjectHolder("full_bronze_legs")
   public static Item full_bronze_legs;
   @ObjectHolder("full_bronze_boots")
   public static Item full_bronze_boots;
   @ObjectHolder("full_emerald_head")
   public static Item full_emerald_head;
   @ObjectHolder("full_emerald_chest")
   public static Item full_emerald_chest;
   @ObjectHolder("full_emerald_legs")
   public static Item full_emerald_legs;
   @ObjectHolder("full_emerald_boots")
   public static Item full_emerald_boots;
   @ObjectHolder("full_wooden_head")
   public static Item full_wooden_head;
   @ObjectHolder("full_wooden_chest")
   public static Item full_wooden_chest;
   @ObjectHolder("full_wooden_legs")
   public static Item full_wooden_legs;
   @ObjectHolder("full_wooden_boots")
   public static Item full_wooden_boots;
   @ObjectHolder("tuxedo_chest")
   public static Item tuxedo_chest;
   @ObjectHolder("tuxedo_pants")
   public static Item tuxedo_pants;
   @ObjectHolder("tuxedo_bottom")
   public static Item tuxedo_bottom;
   @ObjectHolder("wizard_head")
   public static Item wizard_head;
   @ObjectHolder("wizard_chest")
   public static Item wizard_chest;
   @ObjectHolder("wizard_pants")
   public static Item wizard_pants;
   @ObjectHolder("assassin_head")
   public static Item assassin_head;
   @ObjectHolder("assassin_chest")
   public static Item assassin_chest;
   @ObjectHolder("assassin_legs")
   public static Item assassin_legs;
   @ObjectHolder("assassin_boots")
   public static Item assassin_boots;
   @ObjectHolder("soldier_head")
   public static Item soldier_head;
   @ObjectHolder("soldier_chest")
   public static Item soldier_chest;
   @ObjectHolder("soldier_legs")
   public static Item soldier_legs;
   @ObjectHolder("soldier_bottom")
   public static Item soldier_bottom;
   @ObjectHolder("x407_head")
   public static Item x407_head;
   @ObjectHolder("x407_chest")
   public static Item x407_chest;
   @ObjectHolder("x407_legs")
   public static Item x407_legs;
   @ObjectHolder("x407_boots")
   public static Item x407_boots;
   @ObjectHolder("mithril_head")
   public static Item mithril_head;
   @ObjectHolder("mithril_chest")
   public static Item mithril_chest;
   @ObjectHolder("mithril_legs")
   public static Item mithril_legs;
   @ObjectHolder("mithril_boots")
   public static Item mithril_boots;
   @ObjectHolder("demonic_head")
   public static Item demonic_head;
   @ObjectHolder("demonic_chest")
   public static Item demonic_chest;
   @ObjectHolder("demonic_legs")
   public static Item demonic_legs;
   @ObjectHolder("demonic_boots")
   public static Item demonic_boots;
   @ObjectHolder("commissar_head")
   public static Item commissar_head;
   @ObjectHolder("commissar_chest")
   public static Item commissar_chest;
   @ObjectHolder("commissar_legs")
   public static Item commissar_legs;
   @ObjectHolder("commissar_bottom")
   public static Item commissar_bottom;
   @ObjectHolder("infantry_helmet")
   public static Item infantry_helmet;
   @ObjectHolder("officer_chest")
   public static Item officer_chest;
   @ObjectHolder("crown")
   public static Item crown;
   @ObjectHolder("crown2")
   public static Item crown2;
   @ObjectHolder("ninja_head")
   public static Item ninja_head;
   @ObjectHolder("ninja_chest")
   public static Item ninja_chest;
   @ObjectHolder("ninja_legs")
   public static Item ninja_legs;
   @ObjectHolder("chain_skirt")
   public static Item chain_skirt;
   @ObjectHolder("leather_skirt")
   public static Item leather_skirt;
   @ObjectHolder("golden_skirt")
   public static Item golden_skirt;
   @ObjectHolder("iron_skirt")
   public static Item iron_skirt;
   @ObjectHolder("diamond_skirt")
   public static Item diamond_skirt;
   @ObjectHolder("emerald_skirt")
   public static Item emerald_skirt;
   @ObjectHolder("bronze_skirt")
   public static Item bronze_skirt;
   @ObjectHolder("demonic_skirt")
   public static Item demonic_skirt;
   @ObjectHolder("mithril_skirt")
   public static Item mithril_skirt;
   private List<Item> list = new ArrayList();

   public VCArmors() {
   }

   @SubscribeEvent
   public void registerItems(Register<Item> event) {
      this.list.clear();
      Class<?>[] types = new Class[]{String.class, Integer.TYPE, int[].class, Integer.TYPE, SoundEvent.class, Float.TYPE};
      ArmorMaterial armorMithril = (ArmorMaterial)EnumHelper.addEnum(ArmorMaterial.class, "MITHRIL", types, new Object[]{"", Integer.valueOf(40), new int[]{3, 6, 8, 3}, Integer.valueOf(20), SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F});
      ArmorMaterial armorBronze = (ArmorMaterial)EnumHelper.addEnum(ArmorMaterial.class, "BRONZE", types, new Object[]{"", Integer.valueOf(7), new int[]{2, 5, 6, 2}, Integer.valueOf(20), SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F});
      ArmorMaterial armorEmerald = (ArmorMaterial)EnumHelper.addEnum(ArmorMaterial.class, "EMERALD", types, new Object[]{"", Integer.valueOf(35), new int[]{3, 6, 8, 3}, Integer.valueOf(5), SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F});
      armorMithril.setRepairItem(new ItemStack(VCItems.ingot_mithril));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.HEAD, "cow_leather")).register("cow_leather_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST, "cow_leather")).register("cow_leather_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.LEGS, "cow_leather")).register("cow_leather_legs"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.FEET, "cow_leather")).register("cow_leather_boots"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, "nanorum")).register("nanorum_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, "nanorum")).register("nanorum_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.LEGS, "nanorum")).register("nanorum_legs"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.FEET, "nanorum")).register("nanorum_boots"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, "tactical")).register("tactical_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, "tactical")).register("tactical_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.HEAD, "full_cloth")).register("full_leather_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST, "full_cloth")).register("full_leather_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, "full_iron")).register("full_iron_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, "full_iron")).register("full_iron_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.GOLD, EntityEquipmentSlot.HEAD, "full_golden")).register("full_golden_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.GOLD, EntityEquipmentSlot.CHEST, "full_golden")).register("full_golden_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.HEAD, "full_diamond")).register("full_diamond_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.CHEST, "full_diamond")).register("full_diamond_chest"));
      this.register(event, (new ItemArmorBasic(armorBronze, EntityEquipmentSlot.HEAD, "full_bronze")).register("full_bronze_head"));
      this.register(event, (new ItemArmorBasic(armorBronze, EntityEquipmentSlot.CHEST, "full_bronze")).register("full_bronze_chest"));
      this.register(event, (new ItemArmorBasic(armorBronze, EntityEquipmentSlot.LEGS, "full_bronze")).register("full_bronze_legs"));
      this.register(event, (new ItemArmorBasic(armorBronze, EntityEquipmentSlot.FEET, "full_bronze")).register("full_bronze_boots"));
      this.register(event, (new ItemArmorBasic(armorEmerald, EntityEquipmentSlot.HEAD, "full_emerald")).register("full_emerald_head"));
      this.register(event, (new ItemArmorBasic(armorEmerald, EntityEquipmentSlot.CHEST, "full_emerald")).register("full_emerald_chest"));
      this.register(event, (new ItemArmorBasic(armorEmerald, EntityEquipmentSlot.LEGS, "full_emerald")).register("full_emerald_legs"));
      this.register(event, (new ItemArmorBasic(armorEmerald, EntityEquipmentSlot.FEET, "full_emerald")).register("full_emerald_boots"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.HEAD, "full_wooden")).register("full_wooden_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST, "full_wooden")).register("full_wooden_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.LEGS, "full_wooden")).register("full_wooden_legs"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.LEATHER, EntityEquipmentSlot.FEET, "full_wooden")).register("full_wooden_boots"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.CHAIN, EntityEquipmentSlot.CHEST, "tuxedo")).register("tuxedo_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.CHAIN, EntityEquipmentSlot.LEGS, "tuxedo")).register("tuxedo_pants"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.CHAIN, EntityEquipmentSlot.FEET, "tuxedo")).register("tuxedo_bottom"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.CHAIN, EntityEquipmentSlot.HEAD, "wizard")).register("wizard_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.CHAIN, EntityEquipmentSlot.CHEST, "wizard")).register("wizard_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.CHAIN, EntityEquipmentSlot.LEGS, "wizard")).register("wizard_pants"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, "assassin")).register("assassin_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, "assassin")).register("assassin_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.LEGS, "assassin")).register("assassin_legs"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.FEET, "assassin")).register("assassin_boots"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, "soldier")).register("soldier_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, "soldier")).register("soldier_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.LEGS, "soldier")).register("soldier_legs"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.FEET, "soldier")).register("soldier_bottom"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.HEAD, "x407")).register("x407_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.CHEST, "x407")).register("x407_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.LEGS, "x407")).register("x407_legs"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.FEET, "x407")).register("x407_boots"));
      this.register(event, (new ItemArmorBasic(armorMithril, EntityEquipmentSlot.HEAD, "mithril")).register("mithril_head"));
      this.register(event, (new ItemArmorBasic(armorMithril, EntityEquipmentSlot.CHEST, "mithril")).register("mithril_chest"));
      this.register(event, (new ItemArmorBasic(armorMithril, EntityEquipmentSlot.LEGS, "mithril")).register("mithril_legs"));
      this.register(event, (new ItemArmorBasic(armorMithril, EntityEquipmentSlot.FEET, "mithril")).register("mithril_boots"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.HEAD, "demonic")).register("demonic_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.CHEST, "demonic")).register("demonic_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.LEGS, "demonic")).register("demonic_legs"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.FEET, "demonic")).register("demonic_boots"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.GOLD, EntityEquipmentSlot.HEAD, "commissar")).register("commissar_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.GOLD, EntityEquipmentSlot.CHEST, "commissar")).register("commissar_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.GOLD, EntityEquipmentSlot.LEGS, "commissar")).register("commissar_legs"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.GOLD, EntityEquipmentSlot.FEET, "commissar")).register("commissar_bottom"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, "infantry")).register("infantry_helmet"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.CHEST, "officer")).register("officer_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.HEAD, "crown2")).register("crown"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.DIAMOND, EntityEquipmentSlot.HEAD, "crown1")).register("crown2"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, "ninja")).register("ninja_head"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, "ninja")).register("ninja_chest"));
      this.register(event, (new ItemArmorBasic(ArmorMaterial.IRON, EntityEquipmentSlot.LEGS, "ninja")).register("ninja_legs"));
      this.register(event, (new ItemArmorSkirt(ArmorMaterial.CHAIN, "chainmail_layer")).register("chain_skirt"));
      this.register(event, (new ItemArmorSkirt(ArmorMaterial.LEATHER, "leather_layer")).register("leather_skirt"));
      this.register(event, (new ItemArmorSkirt(ArmorMaterial.GOLD, "gold_layer")).register("golden_skirt"));
      this.register(event, (new ItemArmorSkirt(ArmorMaterial.IRON, "iron_layer")).register("iron_skirt"));
      this.register(event, (new ItemArmorSkirt(ArmorMaterial.DIAMOND, "diamond_layer")).register("diamond_skirt"));
      this.register(event, (new ItemArmorSkirt(armorEmerald, "variedcommodities", "fullemerald")).register("emerald_skirt"));
      this.register(event, (new ItemArmorSkirt(armorBronze, "variedcommodities", "fullbronze")).register("bronze_skirt"));
      this.register(event, (new ItemArmorSkirt(ArmorMaterial.DIAMOND, "variedcommodities", "demonic")).register("demonic_skirt"));
      this.register(event, (new ItemArmorSkirt(armorMithril, "variedcommodities", "mithril")).register("mithril_skirt"));
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

   }
}
