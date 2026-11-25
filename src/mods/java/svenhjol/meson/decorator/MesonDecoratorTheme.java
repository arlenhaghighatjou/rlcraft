package svenhjol.meson.decorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class MesonDecoratorTheme {
   protected MesonInnerDecorator decorator;

   public MesonDecoratorTheme(MesonInnerDecorator decorator) {
      this.decorator = decorator;
   }

   public IBlockState getStorageBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(Blocks.CHEST.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public IBlockState getFunctionalBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(Blocks.CRAFTING_TABLE.getDefaultState());
      states.add(Blocks.FURNACE.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public IBlockState getDecorationBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(Blocks.STONE.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public ItemStack getFramedItem() {
      List<Item> items = new ArrayList();
      items.add(Items.IRON_SWORD);
      return new ItemStack((Item)items.get(this.getRand().nextInt(items.size())));
   }

   public ResourceLocation getLootTable() {
      return new ResourceLocation("minecraft:simple_dungeon");
   }

   public Map<EntityEquipmentSlot, ItemStack> getWearables() {
      List<Item> head = new ArrayList();
      List<Item> chest = new ArrayList();
      List<Item> legs = new ArrayList();
      List<Item> feet = new ArrayList();
      List<Item> mainhand = new ArrayList();
      List<Item> offhand = new ArrayList();
      if (this.common()) {
         head.add(Items.LEATHER_HELMET);
      }

      if (this.common()) {
         chest.add(Items.LEATHER_CHESTPLATE);
      }

      if (this.common()) {
         legs.add(Items.LEATHER_LEGGINGS);
      }

      if (this.common()) {
         feet.add(Items.LEATHER_BOOTS);
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

   protected void putWearableInSlot(Map<EntityEquipmentSlot, ItemStack> map, List<Item> wearables, EntityEquipmentSlot slot) {
      if (wearables.size() > 0) {
         Item item = (Item)wearables.get(this.getRand().nextInt(wearables.size()));
         if (item != null) {
            map.put(slot, new ItemStack(item));
         }
      }

   }

   protected Random getRand() {
      return this.decorator.getRand();
   }

   protected boolean common() {
      return this.decorator.common();
   }

   protected boolean uncommon() {
      return this.decorator.uncommon();
   }

   protected boolean valuable() {
      return this.decorator.valuable();
   }

   protected boolean rare() {
      return this.decorator.rare();
   }
}
