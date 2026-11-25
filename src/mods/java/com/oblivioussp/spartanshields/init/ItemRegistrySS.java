package com.oblivioussp.spartanshields.init;

import com.oblivioussp.spartanshields.item.ItemShieldBase;
import com.oblivioussp.spartanshields.item.ItemShieldBasic;
import com.oblivioussp.spartanshields.item.ItemShieldBotania;
import com.oblivioussp.spartanshields.item.ItemShieldElementium;
import com.oblivioussp.spartanshields.item.ItemShieldEnderium;
import com.oblivioussp.spartanshields.item.ItemShieldExtraMaterial;
import com.oblivioussp.spartanshields.item.ItemShieldExtraMod;
import com.oblivioussp.spartanshields.item.ItemShieldLumium;
import com.oblivioussp.spartanshields.item.ItemShieldObsidian;
import com.oblivioussp.spartanshields.item.ItemShieldPoweredFE;
import com.oblivioussp.spartanshields.item.ItemShieldSignalum;
import com.oblivioussp.spartanshields.item.ItemShieldSilver;
import com.oblivioussp.spartanshields.item.ItemShieldVoid;
import com.oblivioussp.spartanshields.util.ConfigHandler;
import com.oblivioussp.spartanshields.util.EnumPowerUnit;
import com.oblivioussp.spartanshields.util.LogHelper;
import com.oblivioussp.spartanshields.util.ModHelper;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class ItemRegistrySS {
   public static ItemShieldBase shieldWood;
   public static ItemShieldBase shieldStone;
   public static ItemShieldBase shieldIron;
   public static ItemShieldBase shieldGold;
   public static ItemShieldBase shieldDiamond;
   public static ItemShieldExtraMaterial shieldBronze;
   public static ItemShieldExtraMaterial shieldSteel;
   public static ItemShieldExtraMaterial shieldCopper;
   public static ItemShieldExtraMaterial shieldTin;
   public static ItemShieldExtraMaterial shieldSilver;
   public static ItemShieldBase shieldObsidian;
   public static ItemShieldExtraMaterial shieldEnderium;
   public static ItemShieldExtraMaterial shieldInvar;
   public static ItemShieldExtraMaterial shieldPlatinum;
   public static ItemShieldExtraMaterial shieldElectrum;
   public static ItemShieldExtraMaterial shieldNickel;
   public static ItemShieldExtraMaterial shieldLead;
   public static ItemShieldExtraMaterial shieldSignalum;
   public static ItemShieldExtraMaterial shieldLumium;
   public static ItemShieldExtraMaterial shieldConstantan;
   public static ItemShieldBasic shieldDarkstone;
   public static ItemShieldBasic shieldAbyssalnite;
   public static ItemShieldBasic shieldCoralium;
   public static ItemShieldBasic shieldDreadium;
   public static ItemShieldBasic shieldEthaxium;
   public static ItemShieldBasic shieldSoulforgedSteel;
   public static ItemShieldBasic shieldManasteel;
   public static ItemShieldBasic shieldTerrasteel;
   public static ItemShieldBasic shieldElementium;
   public static ItemShieldBasic shieldThaumium;
   public static ItemShieldBasic shieldVoid;
   public static ItemShieldBase shieldFEEnderIO;
   public static ItemShieldBase shieldFERFTools;
   public static ItemShieldBase shieldFERedstoneArsenal;
   public static ItemShieldBase shieldTowerWood;
   public static ItemShieldBase shieldTowerStone;
   public static ItemShieldBase shieldTowerIron;
   public static ItemShieldBase shieldTowerGold;
   public static ItemShieldBase shieldTowerDiamond;
   public static ItemShieldBase shieldTowerObsidian;
   public static ItemShieldBase shieldTowerBronze;
   public static ItemShieldBase shieldTowerSteel;
   public static ItemShieldBase shieldTowerCopper;
   public static ItemShieldBase shieldTowerTin;
   public static ItemShieldBase shieldTowerSilver;
   public static ItemShieldBase shieldTowerInvar;
   public static ItemShieldBase shieldTowerPlatinum;
   public static ItemShieldBase shieldTowerElectrum;
   public static ItemShieldBase shieldTowerNickel;
   public static ItemShieldBase shieldTowerLead;
   public static ItemShieldBase shieldTowerConstantan;

   public ItemRegistrySS() {
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> ev) {
      IForgeRegistry<Item> reg = ev.getRegistry();
      if (!ConfigHandler.disableStandardShields) {
         shieldWood = new ItemShieldBasic("shield_basic_wood", ConfigHandler.durabilityWoodShield, ToolMaterial.WOOD);
         shieldStone = new ItemShieldBasic("shield_basic_stone", ConfigHandler.durabilityStoneShield, ToolMaterial.STONE);
         shieldIron = new ItemShieldBasic("shield_basic_iron", ConfigHandler.durabilityIronShield, ToolMaterial.IRON);
         shieldGold = new ItemShieldBasic("shield_basic_gold", ConfigHandler.durabilityGoldShield, ToolMaterial.GOLD);
         shieldDiamond = new ItemShieldBasic("shield_basic_diamond", ConfigHandler.durabilityDiamondShield, ToolMaterial.DIAMOND);
         reg.registerAll(new Item[]{shieldWood, shieldStone, shieldIron, shieldGold, shieldDiamond});
         if (ConfigHandler.enableObsidianShield) {
            shieldObsidian = new ItemShieldObsidian("shield_basic_obsidian", ConfigHandler.durabilityObsidianShield, ModHelper.materialObsidian);
            reg.register(shieldObsidian);
         }
      }

      if (!ConfigHandler.disableTowerShields) {
         shieldTowerWood = new ItemShieldBasic("shield_tower_wood", ConfigHandler.durabilityWoodShield, ToolMaterial.WOOD);
         shieldTowerStone = new ItemShieldBasic("shield_tower_stone", ConfigHandler.durabilityStoneShield, ToolMaterial.STONE);
         shieldTowerIron = new ItemShieldBasic("shield_tower_iron", ConfigHandler.durabilityIronShield, ToolMaterial.IRON);
         shieldTowerGold = new ItemShieldBasic("shield_tower_gold", ConfigHandler.durabilityGoldShield, ToolMaterial.GOLD);
         shieldTowerDiamond = new ItemShieldBasic("shield_tower_diamond", ConfigHandler.durabilityDiamondShield, ToolMaterial.DIAMOND);
         reg.registerAll(new Item[]{shieldTowerWood, shieldTowerStone, shieldTowerIron, shieldTowerGold, shieldTowerDiamond});
         if (ConfigHandler.enableObsidianShield) {
            shieldTowerObsidian = new ItemShieldObsidian("shield_tower_obsidian", ConfigHandler.durabilityObsidianShield, ModHelper.materialObsidian);
            reg.register(shieldTowerObsidian);
         }
      }

      if (!ConfigHandler.vanillaOnly) {
         if (ConfigHandler.enableBronzeShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldBronze = new ItemShieldExtraMaterial("shield_basic_bronze", ConfigHandler.durabilityBronzeShield, ModHelper.materialBronze, "ingotBronze");
               reg.register(shieldBronze);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerBronze = new ItemShieldExtraMaterial("shield_tower_bronze", ConfigHandler.durabilityBronzeShield, ModHelper.materialBronze, "ingotBronze");
               reg.register(shieldTowerBronze);
            }
         }

         if (ConfigHandler.enableSteelShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldSteel = new ItemShieldExtraMaterial("shield_basic_steel", ConfigHandler.durabilitySteelShield, ModHelper.materialSteel, "ingotSteel");
               reg.register(shieldSteel);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerSteel = new ItemShieldExtraMaterial("shield_tower_steel", ConfigHandler.durabilitySteelShield, ModHelper.materialSteel, "ingotSteel");
               reg.register(shieldTowerSteel);
            }
         }

         if (ConfigHandler.enableCopperShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldCopper = new ItemShieldExtraMaterial("shield_basic_copper", ConfigHandler.durabilityCopperShield, ModHelper.materialCopper, "ingotCopper");
               reg.register(shieldCopper);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerCopper = new ItemShieldExtraMaterial("shield_tower_copper", ConfigHandler.durabilityCopperShield, ModHelper.materialCopper, "ingotCopper");
               reg.register(shieldTowerCopper);
            }
         }

         if (ConfigHandler.enableTinShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldTin = new ItemShieldExtraMaterial("shield_basic_tin", ConfigHandler.durabilityTinShield, ModHelper.materialTin, "ingotTin");
               reg.register(shieldTin);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerTin = new ItemShieldExtraMaterial("shield_tower_tin", ConfigHandler.durabilityTinShield, ModHelper.materialTin, "ingotTin");
               reg.register(shieldTowerTin);
            }
         }

         if (ConfigHandler.enableSilverShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldSilver = new ItemShieldSilver("shield_basic_silver", ConfigHandler.durabilitySilverShield, ModHelper.materialSilver, "ingotSilver");
               reg.register(shieldSilver);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerSilver = new ItemShieldSilver("shield_tower_silver", ConfigHandler.durabilitySilverShield, ModHelper.materialSilver, "ingotSilver");
               reg.register(shieldTowerSilver);
            }
         }

         if (ConfigHandler.enableEnderiumShield) {
            shieldEnderium = new ItemShieldEnderium("shield_basic_enderium", ConfigHandler.durabilityEnderiumShield, ModHelper.materialEnderium, "ingotEnderium");
            reg.register(shieldEnderium);
         }

         if (ConfigHandler.enableInvarShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldInvar = new ItemShieldExtraMaterial("shield_basic_invar", ConfigHandler.durabilityInvarShield, ModHelper.materialInvar, "ingotInvar");
               reg.register(shieldInvar);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerInvar = new ItemShieldExtraMaterial("shield_tower_invar", ConfigHandler.durabilityInvarShield, ModHelper.materialInvar, "ingotInvar");
               reg.register(shieldTowerInvar);
            }
         }

         if (ConfigHandler.enablePlatinumShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldPlatinum = new ItemShieldExtraMaterial("shield_basic_platinum", ConfigHandler.durabilityPlatinumShield, ModHelper.materialPlatinum, "ingotPlatinum");
               reg.register(shieldPlatinum);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerPlatinum = new ItemShieldExtraMaterial("shield_tower_platinum", ConfigHandler.durabilityPlatinumShield, ModHelper.materialPlatinum, "ingotPlatinum");
               reg.register(shieldTowerPlatinum);
            }
         }

         if (ConfigHandler.enableElectrumShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldElectrum = new ItemShieldExtraMaterial("shield_basic_electrum", ConfigHandler.durabilityElectrumShield, ModHelper.materialElectrum, "ingotElectrum");
               reg.register(shieldElectrum);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerElectrum = new ItemShieldExtraMaterial("shield_tower_electrum", ConfigHandler.durabilityElectrumShield, ModHelper.materialElectrum, "ingotElectrum");
               reg.register(shieldTowerElectrum);
            }
         }

         if (ConfigHandler.enableNickelShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldNickel = new ItemShieldExtraMaterial("shield_basic_nickel", ConfigHandler.durabilityNickelShield, ModHelper.materialNickel, "ingotNickel");
               reg.register(shieldNickel);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerNickel = new ItemShieldExtraMaterial("shield_tower_nickel", ConfigHandler.durabilityNickelShield, ModHelper.materialNickel, "ingotNickel");
               reg.register(shieldTowerNickel);
            }
         }

         if (ConfigHandler.enableLeadShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldLead = new ItemShieldExtraMaterial("shield_basic_lead", ConfigHandler.durabilityLeadShield, ModHelper.materialLead, "ingotLead");
               reg.register(shieldLead);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerLead = new ItemShieldExtraMaterial("shield_tower_lead", ConfigHandler.durabilityLeadShield, ModHelper.materialLead, "ingotLead");
               reg.register(shieldTowerLead);
            }
         }

         if (ConfigHandler.enableSignalumShield) {
            shieldSignalum = new ItemShieldSignalum("shield_basic_signalum", ConfigHandler.durabilitySignalumShield, ModHelper.materialSignalum, "ingotSignalum");
            reg.register(shieldSignalum);
         }

         if (ConfigHandler.enableLumiumShield) {
            shieldLumium = new ItemShieldLumium("shield_basic_lumium", ConfigHandler.durabilityLumiumShield, ModHelper.materialLumium, "ingotLumium");
            reg.register(shieldLumium);
         }

         if (ConfigHandler.enableManasteelShield) {
            shieldManasteel = new ItemShieldBotania("shield_botania_manasteel", ConfigHandler.durabilityManasteelShield, ModHelper.materialManasteel);
            if (shieldManasteel != null) {
               reg.register(shieldManasteel);
            }
         }

         if (ConfigHandler.enableTerrasteelShield) {
            shieldTerrasteel = new ItemShieldBotania("shield_botania_terrasteel", ConfigHandler.durabilityTerrasteelShield, ModHelper.materialTerrasteel, 100);
            if (shieldTerrasteel != null) {
               reg.register(shieldTerrasteel);
            }
         }

         if (ConfigHandler.enableElementiumShield) {
            shieldElementium = new ItemShieldElementium("shield_botania_elementium", ConfigHandler.durabilityElementiumShield, ModHelper.materialElementium);
            if (shieldElementium != null) {
               reg.register(shieldElementium);
            }
         }

         if (ConfigHandler.enableConstantanShield) {
            if (!ConfigHandler.disableStandardShields) {
               shieldConstantan = new ItemShieldExtraMaterial("shield_basic_constantan", ConfigHandler.durabilityConstantanShield, ModHelper.materialConstantan, "ingotConstantan");
               reg.register(shieldConstantan);
            }

            if (!ConfigHandler.disableTowerShields) {
               shieldTowerConstantan = new ItemShieldExtraMaterial("shield_tower_constantan", ConfigHandler.durabilityConstantanShield, ModHelper.materialConstantan, "ingotConstantan");
               reg.register(shieldTowerConstantan);
            }
         }

         if (ConfigHandler.enableDarkstoneShield) {
            shieldDarkstone = new ItemShieldExtraMod("shield_abyssalcraft_darkstone", ConfigHandler.durabilityDarkstoneShield, ModHelper.materialDarkstone, "darkstone", "abyssalcraft");
            reg.register(shieldDarkstone);
         }

         if (ConfigHandler.enableAbyssalniteShield) {
            shieldAbyssalnite = new ItemShieldExtraMod("shield_abyssalcraft_abyssalnite", ConfigHandler.durabilityAbyssalniteShield, ModHelper.materialAbyssalnite, "ingotAbyssalnite", "abyssalcraft");
            reg.register(shieldAbyssalnite);
         }

         if (ConfigHandler.enableCoraliumShield) {
            shieldCoralium = new ItemShieldExtraMod("shield_abyssalcraft_coralium", ConfigHandler.durabilityCoraliumShield, ModHelper.materialCoralium, "ingotCoralium", "abyssalcraft");
            reg.register(shieldCoralium);
         }

         if (ConfigHandler.enableDreadiumShield) {
            shieldDreadium = new ItemShieldExtraMod("shield_abyssalcraft_dreadium", ConfigHandler.durabilityDreadiumShield, ModHelper.materialDreadium, "ingotDreadium", "abyssalcraft");
            reg.register(shieldDreadium);
         }

         if (ConfigHandler.enableEthaxiumShield) {
            shieldEthaxium = new ItemShieldExtraMod("shield_abyssalcraft_ethaxium", ConfigHandler.durabilityEthaxiumShield, ModHelper.materialEthaxium, "ingotEthaxium", "abyssalcraft");
            reg.register(shieldEthaxium);
         }

         if (ConfigHandler.enableSoulforgedSteelShield) {
            shieldSoulforgedSteel = new ItemShieldExtraMod("shield_basic_soulforged_steel", ConfigHandler.durabilitySoulforgedSteelShield, ModHelper.materialSoulforgedSteel, "ingotSoulforgedSteel", "betterwithmods");
            reg.register(shieldSoulforgedSteel);
         }

         if (ConfigHandler.enableThaumiumShield) {
            shieldThaumium = new ItemShieldExtraMod("shield_tc_thaumium", ConfigHandler.durabilityThaumiumShield, ModHelper.materialThaumium, "ingotThaumium", "thaumcraft");
            reg.register(shieldThaumium);
         }

         if (ConfigHandler.enableVoidShield) {
            shieldVoid = new ItemShieldVoid("shield_tc_void", ConfigHandler.durabilityVoidShield, ModHelper.materialVoid, "ingotVoid", "thaumcraft");
            if (shieldVoid != null) {
               reg.register(shieldVoid);
            }
         }

         if (ConfigHandler.enableEnderIOShield) {
            shieldFEEnderIO = new ItemShieldPoweredFE("shield_riot_enderio", 1000000, 2048, "enderio", EnumPowerUnit.MicroInfinity);
            reg.register(shieldFEEnderIO);
         }

         if (ConfigHandler.enableRFToolsShield) {
            shieldFERFTools = new ItemShieldPoweredFE("shield_riot_rftools", 1000000, 2048, "rftools", EnumPowerUnit.RedstoneFlux);
            reg.register(shieldFERFTools);
         }

         if (ConfigHandler.enableRAShield) {
            shieldFERedstoneArsenal = new ItemShieldPoweredFE("shield_flux_ra", 1000000, 2048, "redstonearsenal", EnumPowerUnit.RedstoneFlux);
            reg.register(shieldFERedstoneArsenal);
         }
      }

      LogHelper.info("Items Registered!");
   }

   @Method(
      modid = "botania"
   )
   public static void initBotaniaShields() {
   }
}
