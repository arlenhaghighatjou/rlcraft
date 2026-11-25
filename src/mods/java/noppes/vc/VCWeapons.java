package noppes.vc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.items.ItemBasic;
import noppes.vc.items.ItemCrossbow;
import noppes.vc.items.ItemGun;
import noppes.vc.items.ItemHolyHandGrenade;
import noppes.vc.items.ItemKunai;
import noppes.vc.items.ItemMachineGun;
import noppes.vc.items.ItemMusket;
import noppes.vc.items.ItemShieldBasic;
import noppes.vc.items.ItemSlingshot;
import noppes.vc.items.ItemStaff;
import noppes.vc.items.ItemStaffElemental;
import noppes.vc.items.ItemThrowingShuriken;
import noppes.vc.items.ItemThrowingWeapon;
import noppes.vc.items.ItemWeaponBasic;
import noppes.vc.items.VCToolMaterial;

@ObjectHolder("variedcommodities")
public class VCWeapons {
   @ObjectHolder("bullet")
   public static Item bullet;
   @ObjectHolder("wooden_gun")
   public static Item wooden_gun;
   @ObjectHolder("stone_gun")
   public static Item stone_gun;
   @ObjectHolder("iron_gun")
   public static Item iron_gun;
   @ObjectHolder("golden_gun")
   public static Item golden_gun;
   @ObjectHolder("diamond_gun")
   public static Item diamond_gun;
   @ObjectHolder("bronze_gun")
   public static Item bronze_gun;
   @ObjectHolder("emerald_gun")
   public static Item emerald_gun;
   @ObjectHolder("machine_gun")
   public static Item machine_gun;
   @ObjectHolder("mana")
   public static Item mana;
   @ObjectHolder("wooden_staff")
   public static Item wooden_staff;
   @ObjectHolder("stone_staff")
   public static Item stone_staff;
   @ObjectHolder("iron_staff")
   public static Item iron_staff;
   @ObjectHolder("golden_staff")
   public static Item golden_staff;
   @ObjectHolder("diamond_staff")
   public static Item diamond_staff;
   @ObjectHolder("bronze_staff")
   public static Item bronze_staff;
   @ObjectHolder("emerald_staff")
   public static Item emerald_staff;
   @ObjectHolder("demonic_staff")
   public static Item demonic_staff;
   @ObjectHolder("frost_staff")
   public static Item frost_staff;
   @ObjectHolder("mithril_staff")
   public static Item mithril_staff;
   @ObjectHolder("elemental_staff")
   public static Item elemental_staff;
   @ObjectHolder("wooden_trident")
   public static Item wooden_trident;
   @ObjectHolder("stone_trident")
   public static Item stone_trident;
   @ObjectHolder("iron_trident")
   public static Item iron_trident;
   @ObjectHolder("golden_trident")
   public static Item golden_trident;
   @ObjectHolder("diamond_trident")
   public static Item diamond_trident;
   @ObjectHolder("bronze_trident")
   public static Item bronze_trident;
   @ObjectHolder("emerald_trident")
   public static Item emerald_trident;
   @ObjectHolder("cursed_trident")
   public static Item cursed_trident;
   @ObjectHolder("demonic_trident")
   public static Item demonic_trident;
   @ObjectHolder("frost_trident")
   public static Item frost_trident;
   @ObjectHolder("mithril_trident")
   public static Item mithril_trident;
   @ObjectHolder("kunai")
   public static Item kunai;
   @ObjectHolder("kunai_reversed")
   public static Item kunai_reversed;
   @ObjectHolder("shuriken")
   public static Item shuriken;
   @ObjectHolder("shuriken_giant")
   public static Item shuriken_giant;
   @ObjectHolder("katana")
   public static Item katana;
   @ObjectHolder("kukri")
   public static Item kukri;
   @ObjectHolder("ninja_claw")
   public static Item ninja_claw;
   @ObjectHolder("steel_claw")
   public static Item steel_claw;
   @ObjectHolder("bear_claw")
   public static Item bear_claw;
   @ObjectHolder("katar")
   public static Item katar;
   @ObjectHolder("bronze_sword")
   public static Item bronze_sword;
   @ObjectHolder("emerald_sword")
   public static Item emerald_sword;
   @ObjectHolder("demonic_sword")
   public static Item demonic_sword;
   @ObjectHolder("frost_sword")
   public static Item frost_sword;
   @ObjectHolder("mithril_sword")
   public static Item mithril_sword;
   @ObjectHolder("leaf_sword")
   public static Item leaf_sword;
   @ObjectHolder("golf_club")
   public static Item golf_club;
   @ObjectHolder("hammer")
   public static Item hammer;
   @ObjectHolder("baseball_bat")
   public static Item baseball_bat;
   @ObjectHolder("lead_pipe")
   public static Item lead_pipe;
   @ObjectHolder("cleaver")
   public static Item cleaver;
   @ObjectHolder("saber")
   public static Item saber;
   @ObjectHolder("hockey_stick")
   public static Item hockey_stick;
   @ObjectHolder("sledge_hammer")
   public static Item sledge_hammer;
   @ObjectHolder("broken_bottle")
   public static Item broken_bottle;
   @ObjectHolder("combat_knive")
   public static Item combat_knive;
   @ObjectHolder("macuahuitl")
   public static Item macuahuitl;
   @ObjectHolder("bo_staff")
   public static Item bo_staff;
   @ObjectHolder("sai")
   public static Item sai;
   @ObjectHolder("slingshot")
   public static Item slingshot;
   @ObjectHolder("musket")
   public static Item musket;
   @ObjectHolder("crossbow")
   public static Item crossbow;
   @ObjectHolder("crossbow_bolt")
   public static Item crossbow_bolt;
   @ObjectHolder("batton")
   public static Item batton;
   @ObjectHolder("chainsaw_gun")
   public static Item chainsaw_gun;
   @ObjectHolder("rapier")
   public static Item rapier;
   @ObjectHolder("pipe_wrench")
   public static Item pipe_wrench;
   @ObjectHolder("crowbar")
   public static Item crowbar;
   @ObjectHolder("swiss_army_knife")
   public static Item swiss_army_knife;
   @ObjectHolder("wrench")
   public static Item wrench;
   @ObjectHolder("excalibur")
   public static Item excalibur;
   @ObjectHolder("wooden_shield_round")
   public static Item wooden_shield_round;
   @ObjectHolder("stone_shield_round")
   public static Item stone_shield_round;
   @ObjectHolder("iron_shield_round")
   public static Item iron_shield_round;
   @ObjectHolder("golden_shield_round")
   public static Item golden_shield_round;
   @ObjectHolder("diamond_shield_round")
   public static Item diamond_shield_round;
   @ObjectHolder("bronze_shield_round")
   public static Item bronze_shield_round;
   @ObjectHolder("emerald_shield_round")
   public static Item emerald_shield_round;
   @ObjectHolder("demonic_shield_round")
   public static Item demonic_shield_round;
   @ObjectHolder("mithril_shield_round")
   public static Item mithril_shield_round;
   @ObjectHolder("wooden_shield")
   public static Item wooden_shield;
   @ObjectHolder("stone_shield")
   public static Item stone_shield;
   @ObjectHolder("iron_shield")
   public static Item iron_shield;
   @ObjectHolder("golden_shield")
   public static Item golden_shield;
   @ObjectHolder("diamond_shield")
   public static Item diamond_shield;
   @ObjectHolder("bronze_shield")
   public static Item bronze_shield;
   @ObjectHolder("emerald_shield")
   public static Item emerald_shield;
   @ObjectHolder("frost_shield")
   public static Item frost_shield;
   @ObjectHolder("heater_shield")
   public static Item heater_shield;
   @ObjectHolder("crescent_shield")
   public static Item crescent_shield;
   @ObjectHolder("scutum_shield")
   public static Item scutum_shield;
   @ObjectHolder("tower_shield")
   public static Item tower_shield;
   @ObjectHolder("wooden_spear")
   public static Item wooden_spear;
   @ObjectHolder("stone_spear")
   public static Item stone_spear;
   @ObjectHolder("iron_spear")
   public static Item iron_spear;
   @ObjectHolder("golden_spear")
   public static Item golden_spear;
   @ObjectHolder("diamond_spear")
   public static Item diamond_spear;
   @ObjectHolder("bronze_spear")
   public static Item bronze_spear;
   @ObjectHolder("emerald_spear")
   public static Item emerald_spear;
   @ObjectHolder("demonic_spear")
   public static Item demonic_spear;
   @ObjectHolder("frost_spear")
   public static Item frost_spear;
   @ObjectHolder("mithril_spear")
   public static Item mithril_spear;
   @ObjectHolder("wooden_halberd")
   public static Item wooden_halberd;
   @ObjectHolder("stone_halberd")
   public static Item stone_halberd;
   @ObjectHolder("iron_halberd")
   public static Item iron_halberd;
   @ObjectHolder("golden_halberd")
   public static Item golden_halberd;
   @ObjectHolder("diamond_halberd")
   public static Item diamond_halberd;
   @ObjectHolder("bronze_halberd")
   public static Item bronze_halberd;
   @ObjectHolder("emerald_halberd")
   public static Item emerald_halberd;
   @ObjectHolder("demonic_halberd")
   public static Item demonic_halberd;
   @ObjectHolder("frost_halberd")
   public static Item frost_halberd;
   @ObjectHolder("mithril_halberd")
   public static Item mithril_halberd;
   @ObjectHolder("wooden_battleaxe")
   public static Item wooden_battleaxe;
   @ObjectHolder("stone_battleaxe")
   public static Item stone_battleaxe;
   @ObjectHolder("iron_battleaxe")
   public static Item iron_battleaxe;
   @ObjectHolder("golden_battleaxe")
   public static Item golden_battleaxe;
   @ObjectHolder("diamond_battleaxe")
   public static Item diamond_battleaxe;
   @ObjectHolder("bronze_battleaxe")
   public static Item bronze_battleaxe;
   @ObjectHolder("emerald_battleaxe")
   public static Item emerald_battleaxe;
   @ObjectHolder("demonic_battleaxe")
   public static Item demonic_battleaxe;
   @ObjectHolder("frost_battleaxe")
   public static Item frost_battleaxe;
   @ObjectHolder("mithril_battleaxe")
   public static Item mithril_battleaxe;
   @ObjectHolder("wooden_warhammer")
   public static Item wooden_warhammer;
   @ObjectHolder("stone_warhammer")
   public static Item stone_warhammer;
   @ObjectHolder("iron_warhammer")
   public static Item iron_warhammer;
   @ObjectHolder("golden_warhammer")
   public static Item golden_warhammer;
   @ObjectHolder("diamond_warhammer")
   public static Item diamond_warhammer;
   @ObjectHolder("bronze_warhammer")
   public static Item bronze_warhammer;
   @ObjectHolder("emerald_warhammer")
   public static Item emerald_warhammer;
   @ObjectHolder("demonic_warhammer")
   public static Item demonic_warhammer;
   @ObjectHolder("frost_warhammer")
   public static Item frost_warhammer;
   @ObjectHolder("mithril_warhammer")
   public static Item mithril_warhammer;
   @ObjectHolder("wooden_mace")
   public static Item wooden_mace;
   @ObjectHolder("stone_mace")
   public static Item stone_mace;
   @ObjectHolder("iron_mace")
   public static Item iron_mace;
   @ObjectHolder("golden_mace")
   public static Item golden_mace;
   @ObjectHolder("diamond_mace")
   public static Item diamond_mace;
   @ObjectHolder("bronze_mace")
   public static Item bronze_mace;
   @ObjectHolder("emerald_mace")
   public static Item emerald_mace;
   @ObjectHolder("demonic_mace")
   public static Item demonic_mace;
   @ObjectHolder("frost_mace")
   public static Item frost_mace;
   @ObjectHolder("mithril_mace")
   public static Item mithril_mace;
   @ObjectHolder("wooden_dagger")
   public static Item wooden_dagger;
   @ObjectHolder("wooden_dagger_reversed")
   public static Item wooden_dagger_reversed;
   @ObjectHolder("stone_dagger")
   public static Item stone_dagger;
   @ObjectHolder("stone_dagger_reversed")
   public static Item stone_dagger_reversed;
   @ObjectHolder("iron_dagger")
   public static Item iron_dagger;
   @ObjectHolder("iron_dagger_reversed")
   public static Item iron_dagger_reversed;
   @ObjectHolder("golden_dagger")
   public static Item golden_dagger;
   @ObjectHolder("golden_dagger_reversed")
   public static Item golden_dagger_reversed;
   @ObjectHolder("diamond_dagger")
   public static Item diamond_dagger;
   @ObjectHolder("diamond_dagger_reversed")
   public static Item diamond_dagger_reversed;
   @ObjectHolder("bronze_dagger")
   public static Item bronze_dagger;
   @ObjectHolder("bronze_dagger_reversed")
   public static Item bronze_dagger_reversed;
   @ObjectHolder("emerald_dagger")
   public static Item emerald_dagger;
   @ObjectHolder("emerald_dagger_reversed")
   public static Item emerald_dagger_reversed;
   @ObjectHolder("demonic_dagger")
   public static Item demonic_dagger;
   @ObjectHolder("demonic_dagger_reversed")
   public static Item demonic_dagger_reversed;
   @ObjectHolder("frost_dagger")
   public static Item frost_dagger;
   @ObjectHolder("frost_dagger_reversed")
   public static Item frost_dagger_reversed;
   @ObjectHolder("mithril_dagger")
   public static Item mithril_dagger;
   @ObjectHolder("mithril_dagger_reversed")
   public static Item mithril_dagger_reversed;
   @ObjectHolder("wooden_scythe")
   public static Item wooden_scythe;
   @ObjectHolder("stone_scythe")
   public static Item stone_scythe;
   @ObjectHolder("iron_scythe")
   public static Item iron_scythe;
   @ObjectHolder("golden_scythe")
   public static Item golden_scythe;
   @ObjectHolder("diamond_scythe")
   public static Item diamond_scythe;
   @ObjectHolder("bronze_scythe")
   public static Item bronze_scythe;
   @ObjectHolder("emerald_scythe")
   public static Item emerald_scythe;
   @ObjectHolder("demonic_scythe")
   public static Item demonic_scythe;
   @ObjectHolder("frost_scythe")
   public static Item frost_scythe;
   @ObjectHolder("mithril_scythe")
   public static Item mithril_scythe;
   @ObjectHolder("wooden_glaive")
   public static Item wooden_glaive;
   @ObjectHolder("stone_glaive")
   public static Item stone_glaive;
   @ObjectHolder("iron_glaive")
   public static Item iron_glaive;
   @ObjectHolder("golden_glaive")
   public static Item golden_glaive;
   @ObjectHolder("diamond_glaive")
   public static Item diamond_glaive;
   @ObjectHolder("bronze_glaive")
   public static Item bronze_glaive;
   @ObjectHolder("emerald_glaive")
   public static Item emerald_glaive;
   @ObjectHolder("demonic_glaive")
   public static Item demonic_glaive;
   @ObjectHolder("frost_glaive")
   public static Item frost_glaive;
   @ObjectHolder("mithril_glaive")
   public static Item mithril_glaive;
   @ObjectHolder("wooden_broadsword")
   public static Item wooden_broadsword;
   @ObjectHolder("stone_broadsword")
   public static Item stone_broadsword;
   @ObjectHolder("iron_broadsword")
   public static Item iron_broadsword;
   @ObjectHolder("golden_broadsword")
   public static Item golden_broadsword;
   @ObjectHolder("diamond_broadsword")
   public static Item diamond_broadsword;
   @ObjectHolder("bronze_broadsword")
   public static Item bronze_broadsword;
   @ObjectHolder("emerald_broadsword")
   public static Item emerald_broadsword;
   @ObjectHolder("demonic_broadsword")
   public static Item demonic_broadsword;
   @ObjectHolder("frost_broadsword")
   public static Item frost_broadsword;
   @ObjectHolder("mithril_broadsword")
   public static Item mithril_broadsword;
   @ObjectHolder("holyhandgrenade")
   public static Item holy_hand_grenade;
   private List<Item> list = new ArrayList();

   public VCWeapons() {
   }

   @SubscribeEvent
   public void registerItems(Register<Item> event) {
      this.list.clear();
      ToolMaterial bronze = EnumHelper.addToolMaterial("BRONZE", 2, 170, 5.0F, 2.0F, 15);
      ToolMaterial emerald = EnumHelper.addToolMaterial("REALEMERALD", 3, 1000, 8.0F, 4.0F, 10);
      ToolMaterial demonic = EnumHelper.addToolMaterial("DEMONIC", 3, 100, 8.0F, 6.0F, 22);
      ToolMaterial frost = EnumHelper.addToolMaterial("FROST", 2, 59, 6.0F, 3.0F, 5);
      ToolMaterial mithril = EnumHelper.addToolMaterial("MITHRIL", 3, 3000, 8.0F, 3.0F, 10);
      bronze.setRepairItem(new ItemStack(VCItems.ingot_bronze));
      emerald.setRepairItem(new ItemStack(Items.EMERALD));
      demonic.setRepairItem(new ItemStack(VCItems.ingot_demonic));
      frost.setRepairItem(new ItemStack(Blocks.ICE));
      mithril.setRepairItem(new ItemStack(VCItems.ingot_mithril));
      this.register(event, (new ItemBasic()).register("bullet").setCreativeTab(VCTabs.WEAPONS));
      this.register(event, (new ItemGun(VCToolMaterial.WOOD)).register("wooden_gun"));
      this.register(event, (new ItemGun(VCToolMaterial.STONE)).register("stone_gun"));
      this.register(event, (new ItemGun(VCToolMaterial.IRON)).register("iron_gun"));
      this.register(event, (new ItemGun(VCToolMaterial.GOLD)).register("golden_gun"));
      this.register(event, (new ItemGun(VCToolMaterial.DIA)).register("diamond_gun"));
      this.register(event, (new ItemGun(VCToolMaterial.BRONZE)).register("bronze_gun"));
      this.register(event, (new ItemGun(VCToolMaterial.EMERALD)).register("emerald_gun"));
      this.register(event, (new ItemMachineGun()).register("machine_gun"));
      this.register(event, (new ItemBasic()).register("mana").setCreativeTab(VCTabs.WEAPONS));
      this.register(event, (new ItemStaff(VCToolMaterial.WOOD)).register("wooden_staff"));
      this.register(event, (new ItemStaff(VCToolMaterial.STONE)).register("stone_staff"));
      this.register(event, (new ItemStaff(VCToolMaterial.IRON)).register("iron_staff"));
      this.register(event, (new ItemStaff(VCToolMaterial.GOLD)).register("golden_staff"));
      this.register(event, (new ItemStaff(VCToolMaterial.DIA)).register("diamond_staff"));
      this.register(event, (new ItemStaff(VCToolMaterial.BRONZE)).register("bronze_staff"));
      this.register(event, (new ItemStaff(VCToolMaterial.EMERALD)).register("emerald_staff"));
      this.register(event, (new ItemStaff(VCToolMaterial.DEMONIC)).register("demonic_staff"));
      this.register(event, (new ItemStaff(VCToolMaterial.FROST)).register("frost_staff"));
      this.register(event, (new ItemStaff(VCToolMaterial.MITHRIL)).register("mithril_staff"));
      this.register(event, (new ItemStaffElemental(VCToolMaterial.IRON)).register("elemental_staff"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD)).register("wooden_trident"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("stone_trident"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("iron_trident"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD)).register("golden_trident"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("diamond_trident"));
      this.register(event, (new ItemWeaponBasic(bronze)).register("bronze_trident"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("emerald_trident"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("cursed_trident"));
      this.register(event, (new ItemWeaponBasic(demonic)).register("demonic_trident"));
      this.register(event, (new ItemWeaponBasic(frost)).register("frost_trident"));
      this.register(event, (new ItemWeaponBasic(mithril)).register("mithril_trident"));
      this.register(event, (new ItemKunai(ToolMaterial.IRON)).register("kunai").setMaxStackSize(1));
      this.register(event, (new ItemKunai(ToolMaterial.IRON)).register("kunai_reversed").setMaxStackSize(1));
      this.register(event, (new ItemThrowingShuriken()).setRotating().setDropItem().register("shuriken"));
      this.register(event, (new ItemThrowingWeapon()).setDamage(4).setRotating().setDropItem().register("shuriken_giant"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("katana"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("kukri"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("ninja_claw"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("steel_claw"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("bear_claw"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("katar"));
      this.register(event, (new ItemWeaponBasic(bronze)).register("bronze_sword"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("emerald_sword"));
      this.register(event, (new ItemWeaponBasic(demonic)).register("demonic_sword"));
      this.register(event, (new ItemWeaponBasic(frost)).register("frost_sword"));
      this.register(event, (new ItemWeaponBasic(mithril)).register("mithril_sword"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("leaf_sword"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("golf_club"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("hammer"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("baseball_bat"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("lead_pipe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("cleaver"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("saber"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("hockey_stick"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("sledge_hammer"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("broken_bottle"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("combat_knive"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("macuahuitl"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("bo_staff"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("sai"));
      this.register(event, (new ItemSlingshot()).register("slingshot"));
      this.register(event, (new ItemMusket()).register("musket"));
      this.register(event, (new ItemCrossbow()).register("crossbow"));
      this.register(event, (new ItemBasic()).register("crossbow_bolt"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("batton"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("chainsaw_gun"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("rapier"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("crowbar"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("pipe_wrench"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("swiss_army_knife"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("wrench"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("excalibur"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.WOOD)).register("wooden_shield_round"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.STONE)).register("stone_shield_round"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.IRON)).register("iron_shield_round"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.GOLD)).register("golden_shield_round"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.DIA)).register("diamond_shield_round"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.BRONZE)).register("bronze_shield_round"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.EMERALD)).register("emerald_shield_round"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.DEMONIC)).register("demonic_shield_round"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.MITHRIL)).register("mithril_shield_round"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.WOOD)).register("wooden_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.STONE)).register("stone_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.IRON)).register("iron_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.GOLD)).register("golden_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.DIA)).register("diamond_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.BRONZE)).register("bronze_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.EMERALD)).register("emerald_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.FROST)).register("frost_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.IRON)).register("heater_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.IRON)).register("crescent_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.IRON)).register("scutum_shield"));
      this.register(event, (new ItemShieldBasic(VCToolMaterial.IRON)).register("tower_shield"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD, 2)).register("wooden_spear"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE, 2)).register("stone_spear"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON, 2)).register("iron_spear"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD, 2)).register("golden_spear"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND, 2)).register("diamond_spear"));
      this.register(event, (new ItemWeaponBasic(bronze, 2)).register("bronze_spear"));
      this.register(event, (new ItemWeaponBasic(emerald, 2)).register("emerald_spear"));
      this.register(event, (new ItemWeaponBasic(demonic, 2)).register("demonic_spear"));
      this.register(event, (new ItemWeaponBasic(frost, 2)).register("frost_spear"));
      this.register(event, (new ItemWeaponBasic(mithril, 2)).register("mithril_spear"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD)).register("wooden_halberd"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("stone_halberd"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("iron_halberd"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD)).register("golden_halberd"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("diamond_halberd"));
      this.register(event, (new ItemWeaponBasic(bronze)).register("bronze_halberd"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("emerald_halberd"));
      this.register(event, (new ItemWeaponBasic(demonic)).register("demonic_halberd"));
      this.register(event, (new ItemWeaponBasic(frost)).register("frost_halberd"));
      this.register(event, (new ItemWeaponBasic(mithril)).register("mithril_halberd"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD)).register("wooden_battleaxe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("stone_battleaxe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("iron_battleaxe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD)).register("golden_battleaxe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("diamond_battleaxe"));
      this.register(event, (new ItemWeaponBasic(bronze)).register("bronze_battleaxe"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("emerald_battleaxe"));
      this.register(event, (new ItemWeaponBasic(demonic)).register("demonic_battleaxe"));
      this.register(event, (new ItemWeaponBasic(frost)).register("frost_battleaxe"));
      this.register(event, (new ItemWeaponBasic(mithril)).register("mithril_battleaxe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD)).register("wooden_warhammer"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("stone_warhammer"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("iron_warhammer"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD)).register("golden_warhammer"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("diamond_warhammer"));
      this.register(event, (new ItemWeaponBasic(bronze)).register("bronze_warhammer"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("emerald_warhammer"));
      this.register(event, (new ItemWeaponBasic(demonic)).register("demonic_warhammer"));
      this.register(event, (new ItemWeaponBasic(frost)).register("frost_warhammer"));
      this.register(event, (new ItemWeaponBasic(mithril)).register("mithril_warhammer"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD)).register("wooden_mace"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("stone_mace"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("iron_mace"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD)).register("golden_mace"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("diamond_mace"));
      this.register(event, (new ItemWeaponBasic(bronze)).register("bronze_mace"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("emerald_mace"));
      this.register(event, (new ItemWeaponBasic(demonic)).register("demonic_mace"));
      this.register(event, (new ItemWeaponBasic(frost)).register("frost_mace"));
      this.register(event, (new ItemWeaponBasic(mithril)).register("mithril_mace"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD, 2)).register("wooden_dagger"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD, 2)).register("wooden_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE, 2)).register("stone_dagger"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE, 2)).register("stone_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON, 2)).register("iron_dagger"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON, 2)).register("iron_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD, 2)).register("golden_dagger"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD, 2)).register("golden_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND, 2)).register("diamond_dagger"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND, 2)).register("diamond_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(bronze, 2)).register("bronze_dagger"));
      this.register(event, (new ItemWeaponBasic(bronze, 2)).register("bronze_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(emerald, 2)).register("emerald_dagger"));
      this.register(event, (new ItemWeaponBasic(emerald, 2)).register("emerald_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(demonic, 2)).register("demonic_dagger"));
      this.register(event, (new ItemWeaponBasic(demonic, 2)).register("demonic_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(frost, 2)).register("frost_dagger"));
      this.register(event, (new ItemWeaponBasic(frost, 2)).register("frost_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(mithril, 2)).register("mithril_dagger"));
      this.register(event, (new ItemWeaponBasic(frost, 2)).register("mithril_dagger_reversed"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD)).register("wooden_scythe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("stone_scythe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("iron_scythe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD)).register("golden_scythe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("diamond_scythe"));
      this.register(event, (new ItemWeaponBasic(bronze)).register("bronze_scythe"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("emerald_scythe"));
      this.register(event, (new ItemWeaponBasic(demonic)).register("demonic_scythe"));
      this.register(event, (new ItemWeaponBasic(frost)).register("frost_scythe"));
      this.register(event, (new ItemWeaponBasic(mithril)).register("mithril_scythe"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD)).register("wooden_glaive"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("stone_glaive"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("iron_glaive"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD)).register("golden_glaive"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("diamond_glaive"));
      this.register(event, (new ItemWeaponBasic(bronze)).register("bronze_glaive"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("emerald_glaive"));
      this.register(event, (new ItemWeaponBasic(demonic)).register("demonic_glaive"));
      this.register(event, (new ItemWeaponBasic(frost)).register("frost_glaive"));
      this.register(event, (new ItemWeaponBasic(mithril)).register("mithril_glaive"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.WOOD)).register("wooden_broadsword"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.STONE)).register("stone_broadsword"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.IRON)).register("iron_broadsword"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.GOLD)).register("golden_broadsword"));
      this.register(event, (new ItemWeaponBasic(ToolMaterial.DIAMOND)).register("diamond_broadsword"));
      this.register(event, (new ItemWeaponBasic(bronze)).register("bronze_broadsword"));
      this.register(event, (new ItemWeaponBasic(emerald)).register("emerald_broadsword"));
      this.register(event, (new ItemWeaponBasic(demonic)).register("demonic_broadsword"));
      this.register(event, (new ItemWeaponBasic(frost)).register("frost_broadsword"));
      this.register(event, (new ItemWeaponBasic(mithril)).register("mithril_broadsword"));
      this.register(event, (new ItemHolyHandGrenade()).register("holyhandgrenade"));
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
         ModelLoader.setCustomModelResourceLocation(elemental_staff, i, new ModelResourceLocation(elemental_staff.getRegistryName(), "inventory"));
      }

   }
}
