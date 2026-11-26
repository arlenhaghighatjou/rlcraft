package com.dhanantry.scapeandrunparasites.util.handlers;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.dhanantry.scapeandrunparasites.block.BlockEvolutionLure;
import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSapling;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.block.BlockWebBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;

public class SRPRegisterModels {
   public SRPRegisterModels() {
   }

   public static void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, SRPRegisterModels::registerModels);
   }

   public static void registerModels(ModelRegistryEvent event) {
      registerItemModels();
   }

   private static void registerItemModels() {
      registerItemModel(SRPItems.acanra_drop);
      registerItemModel(SRPItems.aemana_drop);
      registerItemModel(SRPItems.ahull_drop);
      registerItemModel(SRPItems.anogla_drop);
      registerItemModel(SRPItems.ashyco_drop);
      registerItemModel(SRPItems.abano_drop);
      registerItemModel(SRPItems.aranrac_drop);
      registerItemModel(SRPItems.alum_drop);
      registerItemModel(SRPItems.infected_drop);
      registerItemModel(SRPItems.venkrol_drop);
      registerItemModel(SRPItems.dod_drop);
      registerItemModel(SRPItems.itembase);
      registerItemModel(SRPItems.itemthrow);
      registerItemModel(SRPItems.itemEVClock);
      registerItemModel(SRPItems.tendrons);
      registerItemModel(SRPItems.hardbone);
      registerItemModel(SRPItems.infblade);
      registerItemModel(SRPItems.livingcore);
      registerItemModel(SRPItems.vileshell);
      registerItemModel(SRPItems.bone);
      registerItemModel(SRPItems.itemlurecomponent1);
      registerItemModel(SRPItems.itemlurecomponent2);
      registerItemModel(SRPItems.itemlurecomponent3);
      registerItemModel(SRPItems.itemlurecomponent4);
      registerItemModel(SRPItems.itemlurecomponent5);
      registerItemModel(SRPItems.itemlurecomponent6);
      registerItemModel(SRPItems.itemmobspawner_rathol);
      registerItemModel(SRPItems.itemmobspawner_lodo);
      registerItemModel(SRPItems.itemmobspawner_mudo);
      registerItemModel(SRPItems.itemmobspawner_nuuh);
      registerItemModel(SRPItems.itemmobspawner_buthol);
      registerItemModel(SRPItems.itemmobspawner_anged);
      registerItemModel(SRPItems.itemmobspawner_lesh);
      registerItemModel(SRPItems.itemmobspawner_venkrol);
      registerItemModel(SRPItems.itemmobspawner_venkrolsii);
      registerItemModel(SRPItems.itemmobspawner_venkrolsiii);
      registerItemModel(SRPItems.itemmobspawner_venkrolsiv);
      registerItemModel(SRPItems.itemmobspawner_tonro);
      registerItemModel(SRPItems.itemmobspawner_unvo);
      registerItemModel(SRPItems.itemmobspawner_nak);
      registerItemModel(SRPItems.itemmobspawner_dod);
      registerItemModel(SRPItems.itemmobspawner_dodsii);
      registerItemModel(SRPItems.itemmobspawner_dodsiii);
      registerItemModel(SRPItems.itemmobspawner_dodsiv);
      registerItemModel(SRPItems.itemmobspawner_alafha);
      registerItemModel(SRPItems.itemmobspawner_ganro);
      registerItemModel(SRPItems.itemmobspawner_omboo);
      registerItemModel(SRPItems.itemmobspawner_esor);
      registerItemModel(SRPItems.itemmobspawner_orch);
      registerItemModel(SRPItems.itemmobspawner_flog);
      registerItemModel(SRPItems.itemmobspawner_jinjo);
      registerItemModel(SRPItems.itemmobspawner_vesta);
      registerItemModel(SRPItems.itemmobspawner_pheon);
      registerItemModel(SRPItems.itemmobspawner_lencia);
      registerItemModel(SRPItems.itemmobspawner_elvia);
      registerItemModel(SRPItems.itemmobspawner_dorpa);
      registerItemModel(SRPItems.itemmobspawner_infbear);
      registerItemModel(SRPItems.itemmobspawner_infhuman);
      registerItemModel(SRPItems.itemmobspawner_infhumanhead);
      registerItemModel(SRPItems.itemmobspawner_infenderman);
      registerItemModel(SRPItems.itemmobspawner_infendermanhead);
      registerItemModel(SRPItems.itemmobspawner_infcow);
      registerItemModel(SRPItems.itemmobspawner_infcowhead);
      registerItemModel(SRPItems.itemmobspawner_infsheep);
      registerItemModel(SRPItems.itemmobspawner_infsheephead);
      registerItemModel(SRPItems.itemmobspawner_infwolf);
      registerItemModel(SRPItems.itemmobspawner_infwolfhead);
      registerItemModel(SRPItems.itemmobspawner_infpig);
      registerItemModel(SRPItems.itemmobspawner_infpighead);
      registerItemModel(SRPItems.itemmobspawner_infvillager);
      registerItemModel(SRPItems.itemmobspawner_infvillagerhead);
      registerItemModel(SRPItems.itemmobspawner_inhooS);
      registerItemModel(SRPItems.itemmobspawner_inhooM);
      registerItemModel(SRPItems.itemmobspawner_infhorse);
      registerItemModel(SRPItems.itemmobspawner_infhorsehead);
      registerItemModel(SRPItems.itemmobspawner_infplayer);
      registerItemModel(SRPItems.itemmobspawner_infplayerhead);
      registerItemModel(SRPItems.itemmobspawner_infdragone);
      registerItemModel(SRPItems.itemmobspawner_infdragonehead);
      registerItemModel(SRPItems.itemmobspawner_host);
      registerItemModel(SRPItems.itemmobspawner_hostii);
      registerItemModel(SRPItems.itemmobspawner_heed);
      registerItemModel(SRPItems.itemmobspawner_cruxa);
      registerItemModel(SRPItems.itemmobspawner_mes);
      registerItemModel(SRPItems.itemmobspawner_shyco);
      registerItemModel(SRPItems.itemmobspawner_hull);
      registerItemModel(SRPItems.itemmobspawner_canra);
      registerItemModel(SRPItems.itemmobspawner_emana);
      registerItemModel(SRPItems.itemmobspawner_nogla);
      registerItemModel(SRPItems.itemmobspawner_bano);
      registerItemModel(SRPItems.itemmobspawner_ranrac);
      registerItemModel(SRPItems.itemmobspawner_lum);
      registerItemModel(SRPItems.itemmobspawner_shycoadapted);
      registerItemModel(SRPItems.itemmobspawner_canraadapted);
      registerItemModel(SRPItems.itemmobspawner_noglaadapted);
      registerItemModel(SRPItems.itemmobspawner_hulladapted);
      registerItemModel(SRPItems.itemmobspawner_emanaadapted);
      registerItemModel(SRPItems.itemmobspawner_banoadapted);
      registerItemModel(SRPItems.itemmobspawner_ranracadapted);
      registerItemModel(SRPItems.itemmobspawner_oronco);
      registerItemModel(SRPItems.itemmobspawner_terla);
      registerItemModel(SRPItems.itemmobspawner_pod);
      registerItemModel(SRPItems.weapon_scythe);
      registerItemModel(SRPItems.weapon_scytheSentient);
      registerItemModel(SRPItems.weapon_axe);
      registerItemModel(SRPItems.weapon_axeSentient);
      registerItemModel(SRPItems.weapon_sword);
      registerItemModel(SRPItems.weapon_swordSentient);
      registerItemModel(SRPItems.weapon_cleaver);
      registerItemModel(SRPItems.weapon_cleaverSentient);
      registerItemModel(SRPItems.weapon_bow);
      registerItemModel(SRPItems.weapon_bow_sentient);
      registerItemModel(SRPItems.armor_helmet);
      registerItemModel(SRPItems.armor_chest);
      registerItemModel(SRPItems.armor_pants);
      registerItemModel(SRPItems.armor_boots);
      registerItemModel(SRPItems.armor_helmetSentient);
      registerItemModel(SRPItems.armor_chestSentient);
      registerItemModel(SRPItems.armor_pantsSentient);
      registerItemModel(SRPItems.armor_bootsSentient);
      registerItemModel(SRPBlocks.InfestedStain);
      registerItemModel(SRPBlocks.InfestedRubble);
      registerItemModel(SRPBlocks.InfestedTrunk);
      registerItemModel(SRPBlocks.InfestRemain);
      registerItemModel(SRPBlocks.BiomeHeart);
      registerItemModel(SRPBlocks.ColonyHeart);
      registerItemModel(SRPBlocks.BiomePurifier);
      registerItemModel(SRPBlocks.SRPWeb);
      registerItemModel(SRPBlocks.ParasiteStructure);
      registerItemModel(SRPBlocks.buglin);
      registerItemModel(SRPBlocks.ParasiteThin);
      registerItemModel(SRPBlocks.ParasiteRubbleBoneStair);
      registerItemModel(SRPBlocks.ParasiteStainFleshStair);
      registerItemModel(SRPBlocks.ParasiteRubbleSlabHalf);
      registerItemModel(SRPBlocks.ParasiteVine);
      registerItemModel(SRPBlocks.ParasiteFog);
      registerItemModel(SRPBlocks.ParasiteMouth);
      registerItemModel(SRPBlocks.ParasiteCanisterActive);
      registerItemModel(SRPBlocks.dodN);
      registerBlockVariants();
      RegisterCustomMashesAndStates();
   }

   private static void registerItemModel(Item item) {
      ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName().toString(), "inventory");
      ModelLoader.setCustomModelResourceLocation(item, 0, location);
   }

   private static void registerItemModel(Block block) {
      Item item = Item.getItemFromBlock(block);
      ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName().toString(), "inventory");
      ModelLoader.setCustomModelResourceLocation(item, 0, location);
   }

   private static void registerItemModel(Block block, int meta, String fileName) {
      ModelResourceLocation location = new ModelResourceLocation(new ResourceLocation("srparasites", fileName), "inventory");
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, location);
   }

   private static void registerBlockVariants() {
      for(int i = 0; i < BlockWebBase.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.SRPWeb, i, "web_" + BlockWebBase.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockGore.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.Gore, i, "gore_" + BlockGore.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockEvolutionLure.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.evolutionLure, i, "lure_" + BlockEvolutionLure.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockInfestedBush.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.InfestedBush, i, "infestedbush_" + BlockInfestedBush.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockParasiteCanister.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.ParasiteCanister, i, "canister_" + BlockParasiteCanister.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockParasiteRubble.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.ParasiteRubble, i, "rubble_" + BlockParasiteRubble.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockParasiteStain.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.ParasiteStain, i, "stain_" + BlockParasiteStain.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockParasiteTrunk.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.ParasiteTrunk, i, "trunk_" + BlockParasiteTrunk.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockParasiteBush.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.ParasiteBush, i, "parasitebush_" + BlockParasiteBush.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockParasiteSapling.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.ParasiteSapling, i, "sapling_" + BlockParasiteSapling.EnumType.values()[i].getName());
      }

      for(int i = 0; i < BlockParasiteRubbleDense.EnumType.values().length; ++i) {
         registerItemModel(SRPBlocks.ParasiteRubbleDense, i, "rubbledense_" + BlockParasiteRubbleDense.EnumType.values()[i].getName());
      }

   }

   public static void RegisterCustomMashesAndStates() {
      ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(SRPBlocks.DeadBlood), new ItemMeshDefinition() {
         public ModelResourceLocation getModelLocation(ItemStack stack) {
            return new ModelResourceLocation("srparasites:deadblood", "fluid");
         }
      });
      ModelLoader.setCustomStateMapper(SRPBlocks.DeadBlood, new StateMapperBase() {
         protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return new ModelResourceLocation("srparasites:deadblood", "fluid");
         }
      });
   }
}
