package net.mcft.copy.backpacks.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackDataItems implements IBackpackData {
   public static final String TAG_SIZE = "size";
   public static final String TAG_ITEMS = "items";
   public static final String TAG_LOOT_TABLE = "LootTable";
   public static final String TAG_LOOT_TABLE_SEED = "LootTableSeed";
   private BackpackSize _size;
   private ItemStackHandler _items;
   private String _lootTable;
   private long _lootTableSeed;

   public BackpackDataItems() {
      this._items = null;
      this._lootTable = null;
      this._lootTableSeed = 0L;
   }

   public BackpackDataItems(int columns, int rows) {
      this(new BackpackSize(columns, rows));
   }

   public BackpackDataItems(BackpackSize size) {
      this._items = null;
      this._lootTable = null;
      this._lootTableSeed = 0L;
      this._size = size;
   }

   public BackpackSize getSize() {
      return this._size;
   }

   public ItemStackHandler getItems() {
      return this.getItems((World)null, (EntityPlayer)null);
   }

   public ItemStackHandler getItems(World world, EntityPlayer player) {
      if (this._items == null) {
         this._items = new ItemStackHandler(this._size.getColumns() * this._size.getRows());
         if (this._lootTable != null) {
            generateLoot(this._items, this._lootTable, this._lootTableSeed, world, player);
            this._lootTable = null;
         }
      }

      return this._items;
   }

   public void setLootTable(String lootTable, long lootTableSeed) {
      if (this._items != null) {
         throw new UnsupportedOperationException();
      } else {
         this._lootTable = lootTable;
         this._lootTableSeed = lootTableSeed;
      }
   }

   public static void generateLoot(ItemStackHandler items, String tableStr, long seed, World world, EntityPlayer player) {
      EaglercraftRandom rnd = new EaglercraftRandom(seed);
      double maxFullness = 0.6D + rnd.nextDouble() * 0.2D;
      int maxOccupiedSlots = (int)Math.ceil((double)items.getSlots() * maxFullness);
      LootTableManager manager = world.getLootTableManager();
      LootTable table = manager.getLootTableFromLocation(new ResourceLocation(tableStr));
      LootContext context = new LootContext(player != null ? player.getLuck() : 0.0F, (WorldServer)world, manager, player, (EntityPlayer)null, (DamageSource)null);
      List<ItemStack> loot = table.generateLootForPools(rnd, context);
      Collections.shuffle(loot);
      List<Integer> randomizedSlots = new ArrayList(items.getSlots());

      for(int i = 0; i < items.getSlots(); ++i) {
         randomizedSlots.add(Integer.valueOf(i));
      }

      Collections.shuffle(randomizedSlots);

      for(int i = 0; i < maxOccupiedSlots && i < loot.size(); ++i) {
         ItemStack stack = (ItemStack)loot.get(i);
         int slot = ((Integer)randomizedSlots.get(i)).intValue();
         items.setStackInSlot(slot, stack);
      }

   }

   public NBTBase serializeNBT() {
      return NbtUtils.createCompound("size", this._size.serializeNBT(), "items", this._items != null ? this._items.serializeNBT() : null, "LootTable", this._lootTable != null ? this._lootTable.toString() : null, "LootTableSeed", this._lootTable != null ? this._lootTableSeed : null);
   }

   public void deserializeNBT(NBTBase nbt) {
      NBTTagCompound compound = (NBTTagCompound)nbt;
      if (compound.hasKey("size")) {
         this._size = BackpackSize.parse(compound.getTag("size"));
         if (compound.hasKey("items")) {
            this.getItems().deserializeNBT(compound.getCompoundTag("items"));
         } else if (compound.hasKey("LootTable")) {
            this._lootTable = compound.getString("LootTable");
            this._lootTableSeed = compound.getLong("LootTableSeed");
         }
      } else {
         this.getItems().deserializeNBT(compound);
         this._size = new BackpackSize(9, this._items.getSlots() / 9);
      }

   }
}
