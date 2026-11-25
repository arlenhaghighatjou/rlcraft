package reborncore.client.containerBuilder.builder;

import java.util.function.Predicate;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.apache.commons.lang3.Range;
import reborncore.client.IconSupplier;
import reborncore.client.containerBuilder.builder.slot.SpriteSlot;
import reborncore.client.gui.slots.BaseSlot;

public final class ContainerPlayerInventoryBuilder {
   private final InventoryPlayer player;
   private final ContainerBuilder parent;
   private Range<Integer> main;
   private Range<Integer> hotbar;
   private Range<Integer> armor;

   ContainerPlayerInventoryBuilder(ContainerBuilder parent, InventoryPlayer player) {
      this.player = player;
      this.parent = parent;
   }

   public ContainerPlayerInventoryBuilder inventory(int xStart, int yStart) {
      int startIndex = this.parent.slots.size();

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.parent.slots.add(new BaseSlot(this.player, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
         }
      }

      this.main = Range.between(Integer.valueOf(startIndex), Integer.valueOf(this.parent.slots.size() - 1));
      return this;
   }

   public ContainerPlayerInventoryBuilder hotbar(int xStart, int yStart) {
      int startIndex = this.parent.slots.size();

      for(int i = 0; i < 9; ++i) {
         this.parent.slots.add(new BaseSlot(this.player, i, xStart + i * 18, yStart));
      }

      this.hotbar = Range.between(Integer.valueOf(startIndex), Integer.valueOf(this.parent.slots.size() - 1));
      return this;
   }

   public ContainerPlayerInventoryBuilder inventory() {
      return this.inventory(8, 94);
   }

   public ContainerPlayerInventoryBuilder hotbar() {
      return this.hotbar(8, 152);
   }

   public ContainerPlayerInventoryBuilder.ContainerPlayerArmorInventoryBuilder armor() {
      return new ContainerPlayerInventoryBuilder.ContainerPlayerArmorInventoryBuilder(this);
   }

   public ContainerBuilder addInventory() {
      if (this.hotbar != null) {
         this.parent.addPlayerInventoryRange(this.hotbar);
      }

      if (this.main != null) {
         this.parent.addPlayerInventoryRange(this.main);
      }

      if (this.armor != null) {
         this.parent.addTileInventoryRange(this.armor);
      }

      return this.parent;
   }

   public static final class ContainerPlayerArmorInventoryBuilder {
      private final ContainerPlayerInventoryBuilder parent;
      private final int startIndex;

      public ContainerPlayerArmorInventoryBuilder(ContainerPlayerInventoryBuilder parent) {
         this.parent = parent;
         this.startIndex = parent.parent.slots.size();
      }

      private ContainerPlayerInventoryBuilder.ContainerPlayerArmorInventoryBuilder armor(int index, int xStart, int yStart, EntityEquipmentSlot slotType, String sprite) {
         this.parent.parent.slots.add((new SpriteSlot(this.parent.player, index, xStart, yStart, sprite, 1)).setFilter((stack) -> {
            return stack.getItem().isValidArmor(stack, slotType, this.parent.player.player);
         }));
         return this;
      }

      public ContainerPlayerInventoryBuilder.ContainerPlayerArmorInventoryBuilder helmet(int xStart, int yStart) {
         return this.armor(this.parent.player.getSizeInventory() - 2, xStart, yStart, EntityEquipmentSlot.HEAD, IconSupplier.armour_head_name);
      }

      public ContainerPlayerInventoryBuilder.ContainerPlayerArmorInventoryBuilder chestplate(int xStart, int yStart) {
         return this.armor(this.parent.player.getSizeInventory() - 3, xStart, yStart, EntityEquipmentSlot.CHEST, IconSupplier.armour_chest_name);
      }

      public ContainerPlayerInventoryBuilder.ContainerPlayerArmorInventoryBuilder leggings(int xStart, int yStart) {
         return this.armor(this.parent.player.getSizeInventory() - 4, xStart, yStart, EntityEquipmentSlot.LEGS, IconSupplier.armour_legs_name);
      }

      public ContainerPlayerInventoryBuilder.ContainerPlayerArmorInventoryBuilder boots(int xStart, int yStart) {
         return this.armor(this.parent.player.getSizeInventory() - 5, xStart, yStart, EntityEquipmentSlot.FEET, IconSupplier.armour_feet_name);
      }

      public ContainerPlayerInventoryBuilder.ContainerPlayerArmorInventoryBuilder complete(int xStart, int yStart) {
         return this.helmet(xStart, yStart).chestplate(xStart, yStart + 18).leggings(xStart, yStart + 18 + 18).boots(xStart, yStart + 18 + 18 + 18);
      }

      public ContainerPlayerInventoryBuilder addArmor() {
         this.parent.armor = Range.between(Integer.valueOf(this.startIndex), Integer.valueOf(this.parent.parent.slots.size() - 1));
         return this.parent;
      }
   }
}
