package svenhjol.charm.world.decorator.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import svenhjol.charm.base.CharmDecoratorTheme;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonInnerDecorator;

public class VillageSmithTheme extends CharmDecoratorTheme {
   public VillageSmithTheme(MesonInnerDecorator structure) {
      super(structure);
   }

   public IBlockState getFunctionalBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(Blocks.CRAFTING_TABLE.getDefaultState());
      states.add(Blocks.FURNACE.getDefaultState());
      states.add(Blocks.HOPPER.getDefaultState());
      states.add(Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.DAMAGE, Integer.valueOf(this.getRand().nextInt(2) + 1)));
      states.add(Blocks.CAULDRON.getDefaultState().withProperty(BlockCauldron.LEVEL, Integer.valueOf(this.getRand().nextInt(3))));
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public IBlockState getDecorationBlock() {
      List<IBlockState> states = new ArrayList();
      if (this.uncommon()) {
         states.add(Blocks.IRON_BLOCK.getDefaultState());
      }

      if (this.uncommon()) {
         states.add(Blocks.GOLD_BLOCK.getDefaultState());
      }

      if (this.common()) {
         states.add(Blocks.IRON_ORE.getDefaultState());
      }

      if (this.common()) {
         states.add(Blocks.GOLD_ORE.getDefaultState());
      }

      states.add(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE));
      states.add(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE_SMOOTH));
      states.add(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.DIORITE));
      states.add(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.DIORITE_SMOOTH));
      states.add(Blocks.STONE.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public ItemStack getFramedItem() {
      List<Item> items = new ArrayList();
      if (this.valuable()) {
         items.add(Items.DIAMOND);
      }

      if (this.uncommon()) {
         items.add(Items.CHAINMAIL_HELMET);
      }

      if (this.uncommon()) {
         items.add(Items.CHAINMAIL_CHESTPLATE);
      }

      if (this.uncommon()) {
         items.add(Items.CHAINMAIL_LEGGINGS);
      }

      if (this.uncommon()) {
         items.add(Items.CHAINMAIL_HELMET);
      }

      if (VillageDecorations.vanillaTools) {
         items.add(Items.IRON_SWORD);
      }

      if (VillageDecorations.vanillaTools) {
         items.add(Items.IRON_AXE);
      }

      items.add(Items.IRON_HELMET);
      items.add(Items.IRON_CHESTPLATE);
      items.add(Items.IRON_LEGGINGS);
      items.add(Items.IRON_BOOTS);
      items.add(Items.CLOCK);
      return new ItemStack((Item)items.get(this.getRand().nextInt(items.size())));
   }

   public Map<EntityEquipmentSlot, ItemStack> getWearables() {
      List<Item> head = new ArrayList();
      List<Item> chest = new ArrayList();
      List<Item> legs = new ArrayList();
      List<Item> feet = new ArrayList();
      List<Item> mainhand = new ArrayList();
      List<Item> offhand = new ArrayList();
      if (this.valuable()) {
         head.add(Items.DIAMOND_HELMET);
      }

      if (this.valuable()) {
         chest.add(Items.DIAMOND_CHESTPLATE);
      }

      if (this.valuable()) {
         legs.add(Items.DIAMOND_LEGGINGS);
      }

      if (this.valuable()) {
         feet.add(Items.DIAMOND_BOOTS);
      }

      if (this.uncommon()) {
         head.add(Items.CHAINMAIL_HELMET);
      }

      if (this.uncommon()) {
         chest.add(Items.CHAINMAIL_CHESTPLATE);
      }

      if (this.uncommon()) {
         legs.add(Items.CHAINMAIL_LEGGINGS);
      }

      if (this.uncommon()) {
         feet.add(Items.CHAINMAIL_BOOTS);
      }

      if (this.common()) {
         head.add(Items.IRON_HELMET);
      }

      if (this.common()) {
         chest.add(Items.IRON_CHESTPLATE);
      }

      if (this.common()) {
         legs.add(Items.IRON_LEGGINGS);
      }

      if (this.common()) {
         feet.add(Items.IRON_BOOTS);
      }

      if (this.common() && VillageDecorations.vanillaTools) {
         offhand.add(Items.SHIELD);
      }

      if (VillageDecorations.vanillaTools) {
         mainhand.add(Items.STONE_SWORD);
      }

      if (VillageDecorations.vanillaTools) {
         mainhand.add(Items.STONE_AXE);
      }

      if (VillageDecorations.vanillaTools) {
         mainhand.add(Items.IRON_SWORD);
      }

      if (VillageDecorations.vanillaTools) {
         mainhand.add(Items.IRON_AXE);
      }

      Map<EntityEquipmentSlot, ItemStack> map = new HashMap();
      this.putWearableInSlot(map, head, EntityEquipmentSlot.HEAD);
      this.putWearableInSlot(map, chest, EntityEquipmentSlot.CHEST);
      this.putWearableInSlot(map, legs, EntityEquipmentSlot.LEGS);
      this.putWearableInSlot(map, feet, EntityEquipmentSlot.FEET);
      this.putWearableInSlot(map, mainhand, EntityEquipmentSlot.MAINHAND);
      this.putWearableInSlot(map, offhand, EntityEquipmentSlot.OFFHAND);
      return map;
   }

   public ResourceLocation getLootTable() {
      List<ResourceLocation> locations = new ArrayList();
      if (this.valuable()) {
         locations.add(LootTableList.CHESTS_ABANDONED_MINESHAFT);
      }

      locations.add(CharmLootTables.VILLAGE_SMITH);
      return (ResourceLocation)locations.get(this.getRand().nextInt(locations.size()));
   }
}
