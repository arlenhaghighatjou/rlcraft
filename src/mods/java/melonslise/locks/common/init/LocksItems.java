package melonslise.locks.common.init;

import java.util.ArrayList;
import java.util.List;
import melonslise.locks.common.item.KeyItem;
import melonslise.locks.common.item.KeyRingItem;
import melonslise.locks.common.item.LockItem;
import melonslise.locks.common.item.LockPickItem;
import melonslise.locks.common.item.MasterKeyItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.oredict.OreDictionary;

public final class LocksItems {
   public static final List<Item> ITEMS = new ArrayList(18);
   public static final String OREDICT_LOCK = "locksLock";
   public static final String OREDICT_LOCKPICK = "locksLockPick";
   public static final Item SPRING = add("spring", (new Item()).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item WOOD_LOCK_MECHANISM = add("wood_lock_mechanism", (new Item()).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item IRON_LOCK_MECHANISM = add("iron_lock_mechanism", (new Item()).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item STEEL_LOCK_MECHANISM = add("steel_lock_mechanism", (new Item()).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item KEY_BLANK = add("key_blank", (new Item()).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item WOOD_LOCK = add("wood_lock", (new LockItem(5, 15, 4.0F)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item IRON_LOCK = add("iron_lock", (new LockItem(7, 14, 12.0F)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item STEEL_LOCK = add("steel_lock", (new LockItem(9, 12, 20.0F)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item GOLD_LOCK = add("gold_lock", (new LockItem(6, 22, 6.0F)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item DIAMOND_LOCK = add("diamond_lock", (new LockItem(11, 10, 100.0F)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item KEY = add("key", (new KeyItem()).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item MASTER_KEY = add("master_key", (new MasterKeyItem()).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item KEY_RING = add("key_ring", (new KeyRingItem(1)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item WOOD_LOCK_PICK = add("wood_lock_pick", (new LockPickItem(0.2F)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item IRON_LOCK_PICK = add("iron_lock_pick", (new LockPickItem(0.35F)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item STEEL_LOCK_PICK = add("steel_lock_pick", (new LockPickItem(0.7F)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item GOLD_LOCK_PICK = add("gold_lock_pick", (new LockPickItem(0.25F)).setCreativeTab(LocksCreativeTabs.TAB));
   public static final Item DIAMOND_LOCK_PICK = add("diamond_lock_pick", (new LockPickItem(0.85F)).setCreativeTab(LocksCreativeTabs.TAB));

   private LocksItems() {
   }

   public static void register(Register<Item> event) {
      for(Item item : ITEMS) {
         event.getRegistry().register(item);
      }

      OreDictionary.registerOre("locksLock", WOOD_LOCK);
      OreDictionary.registerOre("locksLock", IRON_LOCK);
      OreDictionary.registerOre("locksLock", STEEL_LOCK);
      OreDictionary.registerOre("locksLock", GOLD_LOCK);
      OreDictionary.registerOre("locksLock", DIAMOND_LOCK);
      OreDictionary.registerOre("locksLockPick", WOOD_LOCK_PICK);
      OreDictionary.registerOre("locksLockPick", IRON_LOCK_PICK);
      OreDictionary.registerOre("locksLockPick", STEEL_LOCK_PICK);
      OreDictionary.registerOre("locksLockPick", GOLD_LOCK_PICK);
      OreDictionary.registerOre("locksLockPick", DIAMOND_LOCK_PICK);
   }

   public static Item add(String name, Item item) {
      ITEMS.add(((Item)item.setRegistryName("locks", name)).setUnlocalizedName("locks." + name));
      return item;
   }
}
