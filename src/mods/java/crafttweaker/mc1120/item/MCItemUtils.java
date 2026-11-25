package crafttweaker.mc1120.item;

import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.api.enchantments.IEnchantment;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IItemUtils;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.potions.IPotionEffect;
import crafttweaker.mc1120.brackets.BracketHandlerItem;
import crafttweaker.mc1120.commands.Commands;
import crafttweaker.mc1120.data.NBTConverter;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.Short2ShortOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import stanhebben.zenscript.annotations.Optional;

public class MCItemUtils implements IItemUtils {
   private static ArrayList<ItemStack> ITEMLIST;

   public MCItemUtils() {
   }

   public static void createItemList() {
      ArrayList<ItemStack> itemList = new ArrayList();
      List<Item> items = new ArrayList(BracketHandlerItem.getItemNames().values());
      items.sort(Commands.ITEM_COMPARATOR);

      for(Item item : items) {
         if (item != null) {
            NonNullList<ItemStack> list = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, list);
            if (!list.isEmpty()) {
               itemList.addAll(list);
            } else {
               ItemStack stack = new ItemStack(item, 1, 0);
               itemList.add(stack);
            }
         }
      }

      ITEMLIST = itemList;
   }

   public IItemStack createPotion(Object[]... params) {
      ItemStack item = new ItemStack(Items.POTIONITEM, 1, 0);
      List<PotionEffect> potionEffects = new ArrayList();

      for(Object[] param : params) {
         if (param.length == 3 && param[0] instanceof IPotion) {
            IPotion iPotion = (IPotion)param[0];
            if (param[1] instanceof Integer) {
               int amplifier = ((Integer)param[1]).intValue();
               if (param[2] instanceof Integer) {
                  int duration = ((Integer)param[2]).intValue();
                  PotionEffect effect = new PotionEffect((Potion)iPotion.getInternal(), duration, amplifier);
                  potionEffects.add(effect);
               }
            }
         }
      }

      PotionUtils.appendEffects(item, potionEffects);
      return new MCItemStack(item);
   }

   public IItemStack createPotion(IPotionEffect... params) {
      ItemStack item = new ItemStack(Items.POTIONITEM, 1, 0);
      List<PotionEffect> potionEffects = (List)Stream.of(params).map((k) -> {
         return (PotionEffect)k.getInternal();
      }).collect(Collectors.toList());
      PotionUtils.appendEffects(item, potionEffects);
      return new MCItemStack(item);
   }

   public IItemStack enchantItem(IItemStack item, IEnchantment... enchantments) {
      ItemStack base = (ItemStack)item.getInternal();
      NBTTagCompound tag = base.getTagCompound();
      Short2ShortOpenHashMap enchantmentMap = new Short2ShortOpenHashMap();
      String baseKey;
      if (base.getItem() != Items.ENCHANTED_BOOK && base.getItem() != Items.BOOK) {
         baseKey = "ench";
      } else {
         if (base.getItem() == Items.BOOK) {
            base = new ItemStack(Items.ENCHANTED_BOOK);
            base.setTagCompound(tag);
         }

         baseKey = "StoredEnchantments";
      }

      if (tag != null && tag.hasKey(baseKey)) {
         NBTTagList list = tag.getTagList(baseKey, 10);

         for(int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound ench = list.getCompoundTagAt(i);
            enchantmentMap.put(ench.getShort("id"), ench.getShort("lvl"));
         }
      } else if (tag == null) {
         tag = new NBTTagCompound();
         base.setTagCompound(tag);
      }

      for(IEnchantment ench : enchantments) {
         NBTTagCompound eTag = (NBTTagCompound)ench.makeNBTInternal();
         enchantmentMap.put(eTag.getShort("id"), eTag.getShort("lvl"));
      }

      NBTTagList result = new NBTTagList();
      ObjectIterator var15 = enchantmentMap.entrySet().iterator();

      while(var15.hasNext()) {
         Entry<Short, Short> i = (Entry)var15.next();
         NBTTagCompound eTag = new NBTTagCompound();
         eTag.setShort("id", ((Short)i.getKey()).shortValue());
         eTag.setShort("lvl", ((Short)i.getValue()).shortValue());
         result.appendTag(eTag);
      }

      tag.setTag(baseKey, result);
      return new MCItemStack(base);
   }

   public IItemStack createEnchantedBook(IEnchantment... enchantments) {
      NBTTagList enchants = new NBTTagList();

      for(IEnchantment ench : enchantments) {
         enchants.appendTag((NBTTagCompound)ench.makeNBTInternal());
      }

      ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
      NBTTagCompound result = new NBTTagCompound();
      result.setTag("StoredEnchantments", enchants);
      book.setTagCompound(result);
      return new MCItemStack(book);
   }

   public IData combineEnchantments(String baseKey, IEnchantment... enchantments) {
      List<IData> enchantmentsList = new ArrayList();

      for(IEnchantment enchDef : enchantments) {
         enchantmentsList.add(new DataMap(enchDef.makeTagInternal(), false));
      }

      return new DataMap(Collections.singletonMap(baseKey, new DataList(enchantmentsList, false)), false);
   }

   public IData combineEnchantments(IEnchantment... enchantments) {
      return this.combineEnchantments("ench", enchantments);
   }

   public IItemStack[] getItemsByRegexRegistryName(String regex) {
      HashSet<String> alreadyChecked = new HashSet();
      Predicate<String> predicate = Pattern.compile(regex).asPredicate();
      return (IItemStack[])ITEMLIST.stream().filter((stack) -> {
         String currentRegName = Objects.toString(stack.getItem().getRegistryName(), (String)null);
         return currentRegName != null && alreadyChecked.add(currentRegName) && predicate.test(currentRegName);
      }).map((stack) -> {
         return stack.hasTagCompound() ? new ItemStack(stack.getItem(), 1, stack.getMetadata()) : stack;
      }).map(CraftTweakerMC::getIItemStack).filter(Objects::nonNull).toArray((x$0) -> {
         return new IItemStack[x$0];
      });
   }

   public IItemStack getItem(String location, @Optional int meta) {
      return BracketHandlerItem.getItem(location, meta);
   }

   public IItemStack[] getItemsByRegexUnlocalizedName(String regex) {
      Predicate<String> predicate = Pattern.compile(regex).asPredicate();
      return (IItemStack[])ITEMLIST.stream().filter((stack) -> {
         return predicate.test(stack.getUnlocalizedName());
      }).map(CraftTweakerMC::getIItemStack).filter(Objects::nonNull).toArray((x$0) -> {
         return new IItemStack[x$0];
      });
   }

   public IItemStack createSpawnEgg(IEntityDefinition entity, @Optional IData customNBT) {
      ItemStack item = new ItemStack(Items.SPAWN_EGG, 1, 0);
      NBTTagCompound baseTag;
      if (customNBT != null) {
         baseTag = (NBTTagCompound)NBTConverter.from(customNBT);
      } else {
         baseTag = new NBTTagCompound();
      }

      NBTTagCompound entityTag;
      if (baseTag.hasKey("EntityTag")) {
         entityTag = baseTag.getCompoundTag("EntityTag");
      } else {
         entityTag = new NBTTagCompound();
      }

      entityTag.setString("id", entity.getId());
      baseTag.setTag("EntityTag", entityTag);
      item.setTagCompound(baseTag);
      return new MCItemStack(item);
   }
}
