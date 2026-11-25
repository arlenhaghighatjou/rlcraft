package crafttweaker.mc1120.enchantments;

import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.DataShort;
import crafttweaker.api.data.IData;
import crafttweaker.api.enchantments.IEnchantment;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class MCEnchantment implements IEnchantment {
   private final Enchantment definition;
   private int level;

   public MCEnchantment(int id, int lvl) {
      this(Enchantment.getEnchantmentByID(id), lvl);
   }

   public MCEnchantment(Enchantment enchantment, int level) {
      this.definition = enchantment;
      this.level = level;
   }

   public IEnchantmentDefinition getDefinition() {
      return new MCEnchantmentDefinition(this.definition);
   }

   public Map<String, IData> makeTagInternal() {
      Map<String, IData> map = new HashMap();
      map.put("id", new DataShort((short)this.getDefinition().getID()));
      map.put("lvl", new DataShort((short)this.level));
      return map;
   }

   public IData makeTag() {
      return new DataMap(Collections.singletonMap("ench", new DataList(Collections.singletonList(new DataMap(this.makeTagInternal(), false)), false)), false);
   }

   public IData makeBookTag() {
      return new DataMap(Collections.singletonMap("StoredEnchantments", new DataList(Collections.singletonList(new DataMap(this.makeTagInternal(), false)), false)), false);
   }

   public IItemStack makeBook() {
      NBTTagCompound result = new NBTTagCompound();
      result.setTag("StoredEnchantments", new NBTTagList());
      result.getTagList("StoredEnchantments", 10).appendTag(this.makeNBTInternal());
      ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
      book.setTagCompound(result);
      return new MCItemStack(book);
   }

   public NBTTagCompound makeNBTInternal() {
      NBTTagCompound enchant = new NBTTagCompound();
      enchant.setShort("id", (short)this.getDefinition().getID());
      enchant.setShort("lvl", (short)this.level);
      return enchant;
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public String displayName() {
      return this.definition.getTranslatedName(this.level);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCEnchantment that = (MCEnchantment)o;
         return this.level == that.level && Objects.equals(this.definition, that.definition);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.definition, this.level});
   }
}
