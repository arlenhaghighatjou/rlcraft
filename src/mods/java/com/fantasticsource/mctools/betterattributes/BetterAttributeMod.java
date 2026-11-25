package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.component.NBTSerializableComponent;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class BetterAttributeMod extends NBTSerializableComponent {
   public static final int OPERATION_ADD_AMOUNT = 0;
   public static final int OPERATION_ADD_OLD_TIER_TIMES_AMOUNT = 1;
   public static final int OPERATION_MULT = 2;
   public String name;
   public String betterAttributeName;
   public String parentAttributeName;
   public int priority;
   public int operation;
   public double amount;

   public BetterAttributeMod() {
      this.name = "";
      this.betterAttributeName = "";
      this.parentAttributeName = "";
      this.priority = 0;
      this.operation = 0;
      this.amount = 0.0D;
   }

   public BetterAttributeMod(String name, BetterAttribute attribute, double amount) {
      this(name, attribute, 0, amount);
   }

   public BetterAttributeMod(String name, BetterAttribute attribute, int operation, double amount) {
      this(name, attribute, 0, operation, amount);
   }

   public BetterAttributeMod(String name, BetterAttribute attribute, int priority, int operation, double amount) {
      this(name, attribute.name, priority, operation, amount);
   }

   public BetterAttributeMod(String name, String betterAttributeName, double amount) {
      this(name, betterAttributeName, 0, amount);
   }

   public BetterAttributeMod(String name, String betterAttributeName, int operation, double amount) {
      this(name, betterAttributeName, 0, operation, amount);
   }

   public BetterAttributeMod(String name, String betterAttributeName, int priority, int operation, double amount) {
      this.name = "";
      this.betterAttributeName = "";
      this.parentAttributeName = "";
      this.priority = 0;
      this.operation = 0;
      this.amount = 0.0D;
      this.name = name;
      this.betterAttributeName = betterAttributeName;
      this.priority = priority;
      this.operation = operation;
      this.amount = amount;
   }

   public String toString() {
      BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(this.betterAttributeName);
      boolean good = attribute == null || attribute.isGood;
      switch(this.operation) {
      case 0:
      case 1:
         if (this.amount < 0.0D) {
            good = !good;
         }
         break;
      case 2:
         if (this.amount < 1.0D) {
            good = !good;
         }
      }

      String result = "" + (good ? TextFormatting.GREEN : TextFormatting.RED);
      switch(this.operation) {
      case 0:
      case 1:
         if (this.amount >= 0.0D) {
            result = result + "+" + Tools.formatNicely(this.amount);
         }
         break;
      case 2:
         result = result + Tools.formatNicely(this.amount) + "x";
      }

      result = result + TextFormatting.GRAY + " " + I18n.translateToLocal("attribute.name." + this.betterAttributeName);
      return result;
   }

   public BetterAttributeMod write(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.name);
      ByteBufUtils.writeUTF8String(buf, this.betterAttributeName);
      ByteBufUtils.writeUTF8String(buf, this.parentAttributeName);
      buf.writeInt(this.priority);
      buf.writeInt(this.operation);
      buf.writeDouble(this.amount);
      return this;
   }

   public BetterAttributeMod read(ByteBuf buf) {
      this.name = ByteBufUtils.readUTF8String(buf);
      this.betterAttributeName = ByteBufUtils.readUTF8String(buf);
      this.parentAttributeName = ByteBufUtils.readUTF8String(buf);
      this.priority = buf.readInt();
      this.operation = buf.readInt();
      this.amount = buf.readDouble();
      return this;
   }

   public BetterAttributeMod save(OutputStream stream) {
      (new CStringUTF8()).set(this.name).save(stream).set(this.betterAttributeName).save(stream).set(this.parentAttributeName).save(stream);
      (new CInt()).set(this.priority).save(stream).set(this.operation).save(stream);
      (new CDouble()).set(this.amount).save(stream);
      return this;
   }

   public BetterAttributeMod load(InputStream stream) {
      CStringUTF8 cs = new CStringUTF8();
      CInt ci = new CInt();
      this.name = cs.load(stream).value;
      this.betterAttributeName = cs.load(stream).value;
      this.parentAttributeName = cs.load(stream).value;
      this.priority = ci.load(stream).value;
      this.operation = ci.load(stream).value;
      this.amount = (new CDouble()).load(stream).value;
      return this;
   }

   public NBTBase serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("name", this.name);
      compound.setString("betterAttributeName", this.betterAttributeName);
      compound.setString("parentAttributeName", this.parentAttributeName);
      compound.setInteger("priority", this.priority);
      compound.setInteger("operation", this.operation);
      compound.setDouble("amount", this.amount);
      return compound;
   }

   public void deserializeNBT(NBTBase nbt) {
      NBTTagCompound compound = (NBTTagCompound)nbt;
      this.name = compound.getString("name");
      this.betterAttributeName = compound.getString("betterAttributeName");
      this.parentAttributeName = compound.getString("parentAttributeName");
      this.priority = compound.getInteger("priority");
      this.operation = compound.getInteger("operation");
      this.amount = compound.getDouble("amount");
   }

   @SubscribeEvent
   public static void inventoryChanged(InventoryChangedEvent event) {
      Entity entity = event.getEntity();

      for(BetterAttribute attribute : BetterAttribute.BETTER_ATTRIBUTES.values()) {
         attribute.calculateTotal(entity);
      }

   }

   @SubscribeEvent
   public static void betterAttributeCalc(BetterAttribute.BetterAttributeCalcEvent event) {
      String attributeName = event.attribute.name;
      Entity entity = event.entity;

      for(BetterAttributeMod parentMod : event.attribute.parentMods) {
         applyMod(entity, event.functions, parentMod);
      }

      ArrayList<NBTTagCompound> compounds = new ArrayList();
      compounds.add(entity.getEntityData());

      for(ItemStack stack : GlobalInventory.getValidEquippedItems(entity)) {
         if (stack.hasTagCompound()) {
            compounds.add(stack.getTagCompound());
         }
      }

      for(NBTTagCompound compound : compounds) {
         compound = MCTools.getSubCompoundIfExists(compound, "fantasticlib", "betterAttributeMods", attributeName);
         if (compound != null) {
            for(String key : compound.getKeySet()) {
               BetterAttributeMod mod = new BetterAttributeMod();
               mod.deserializeNBT(compound.getCompoundTag(key));
               applyMod(entity, event.functions, mod);
            }
         }
      }

   }

   protected static void applyMod(Entity entity, ExplicitPriorityQueue<Predicate<double[]>> functions, BetterAttributeMod mod) {
      functions.add((d) -> {
         BetterAttribute parent = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(mod.parentAttributeName);
         switch(mod.operation) {
         case 0:
            d[0] += parent != null ? parent.getTotalAmount(entity) * mod.amount : mod.amount;
            break;
         case 1:
            d[0] += d[1] * (parent != null ? parent.getTotalAmount(entity) * mod.amount : mod.amount);
            break;
         case 2:
            d[0] *= parent != null ? parent.getTotalAmount(entity) * mod.amount : mod.amount;
         }

         return (void)true;
      }, (double)mod.priority);
   }

   public static void addMods(Entity entity, BetterAttributeMod... mods) {
      NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), "fantasticlib", "betterAttributeMods");
      HashSet<String> attributesToRecalc = new HashSet();

      for(BetterAttributeMod mod : mods) {
         MCTools.getOrGenerateSubCompound(compound, mod.betterAttributeName).setTag(mod.name, mod.serializeNBT());
         attributesToRecalc.add(mod.betterAttributeName);
      }

      for(String attributeName : attributesToRecalc) {
         BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
         if (attribute != null) {
            attribute.calculateTotal(entity);
         }
      }

   }

   public static void addMods(Entity entity, ItemStack stack, BetterAttributeMod... mods) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      NBTTagCompound compound = MCTools.getOrGenerateSubCompound(stack.getTagCompound(), "fantasticlib", "betterAttributeMods");
      HashSet<String> attributesToRecalc = new HashSet();

      for(BetterAttributeMod mod : mods) {
         MCTools.getOrGenerateSubCompound(compound, mod.betterAttributeName).setTag(mod.name, mod.serializeNBT());
         attributesToRecalc.add(mod.betterAttributeName);
      }

      if (entity != null) {
         for(String attributeName : attributesToRecalc) {
            BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) {
               attribute.calculateTotal(entity);
            }
         }
      }

   }

   public static void removeMods(Entity entity, BetterAttributeMod... mods) {
      NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), "fantasticlib", "betterAttributeMods");
      if (compound != null) {
         HashSet<String> attributesToRecalc = new HashSet();

         for(BetterAttributeMod mod : mods) {
            NBTTagCompound compound2 = MCTools.getSubCompoundIfExists(compound, mod.betterAttributeName);
            if (compound2 != null && compound2.hasKey(mod.name)) {
               compound2.removeTag(mod.name);
               attributesToRecalc.add(mod.betterAttributeName);
            }
         }

         for(String attributeName : attributesToRecalc) {
            BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) {
               attribute.calculateTotal(entity);
            }
         }

      }
   }

   public static void removeModsWithName(Entity entity, String modName) {
      NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), "fantasticlib", "betterAttributeMods");
      if (compound != null) {
         HashSet<String> attributesToRecalc = new HashSet();

         for(String attributeName : compound.getKeySet()) {
            NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
            if (compound2.hasKey(modName)) {
               compound2.removeTag(modName);
               attributesToRecalc.add(attributeName);
            }
         }

         for(String attributeName : attributesToRecalc) {
            BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) {
               attribute.calculateTotal(entity);
            }
         }

      }
   }

   public static void removeModsWithNameContaining(Entity entity, String partialModName, boolean ignored) {
      NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), "fantasticlib", "betterAttributeMods");
      if (compound != null) {
         HashSet<String> attributesToRecalc = new HashSet();

         for(String attributeName : compound.getKeySet()) {
            NBTTagCompound compound2 = compound.getCompoundTag(attributeName);

            for(String foundModName : (String[])compound2.getKeySet().toArray(new String[0])) {
               if (foundModName.contains(partialModName)) {
                  compound2.removeTag(foundModName);
                  attributesToRecalc.add(attributeName);
               }
            }
         }

         for(String attributeName : attributesToRecalc) {
            BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) {
               attribute.calculateTotal(entity);
            }
         }

      }
   }

   public static void removeModsIf(Entity entity, Predicate<BetterAttributeMod> condition) {
      NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), "fantasticlib", "betterAttributeMods");
      if (compound != null) {
         HashSet<String> attributesToRecalc = new HashSet();

         for(String attributeName : compound.getKeySet()) {
            NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
            BetterAttributeMod mod = new BetterAttributeMod();

            for(String key2 : (String[])compound2.getKeySet().toArray(new String[0])) {
               mod.deserializeNBT(compound2.getCompoundTag(key2));
               if (condition.test(mod)) {
                  compound2.removeTag(key2);
                  attributesToRecalc.add(attributeName);
               }
            }
         }

         for(String attributeName : attributesToRecalc) {
            BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) {
               attribute.calculateTotal(entity);
            }
         }

      }
   }

   public static void removeMods(Entity entity, ItemStack stack, BetterAttributeMod... mods) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), "fantasticlib", "betterAttributeMods");
         if (compound != null) {
            HashSet<String> attributesToRecalc = new HashSet();

            for(BetterAttributeMod mod : mods) {
               NBTTagCompound compound2 = MCTools.getSubCompoundIfExists(compound, mod.betterAttributeName);
               if (compound2 != null && compound2.hasKey(mod.name)) {
                  compound2.removeTag(mod.name);
                  attributesToRecalc.add(mod.betterAttributeName);
               }
            }

            if (entity != null) {
               for(String attributeName : attributesToRecalc) {
                  BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
                  if (attribute != null) {
                     attribute.calculateTotal(entity);
                  }
               }
            }

         }
      }
   }

   public static void removeModsWithName(Entity entity, ItemStack stack, String modName) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), "fantasticlib", "betterAttributeMods");
         if (compound != null) {
            HashSet<String> attributesToRecalc = new HashSet();

            for(String attributeName : compound.getKeySet()) {
               NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
               if (compound2.hasKey(modName)) {
                  compound2.removeTag(modName);
                  attributesToRecalc.add(attributeName);
               }
            }

            if (entity != null) {
               for(String attributeName : attributesToRecalc) {
                  BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
                  if (attribute != null) {
                     attribute.calculateTotal(entity);
                  }
               }
            }

         }
      }
   }

   public static void removeModsWithNameContaining(Entity entity, ItemStack stack, String partialModName, boolean ignored) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), "fantasticlib", "betterAttributeMods");
         if (compound != null) {
            HashSet<String> attributesToRecalc = new HashSet();

            for(String attributeName : compound.getKeySet()) {
               NBTTagCompound compound2 = compound.getCompoundTag(attributeName);

               for(String foundModName : (String[])compound2.getKeySet().toArray(new String[0])) {
                  if (foundModName.contains(partialModName)) {
                     compound2.removeTag(foundModName);
                     attributesToRecalc.add(attributeName);
                  }
               }
            }

            if (entity != null) {
               for(String attributeName : attributesToRecalc) {
                  BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
                  if (attribute != null) {
                     attribute.calculateTotal(entity);
                  }
               }
            }

         }
      }
   }

   public static void removeModsIf(Entity entity, ItemStack stack, Predicate<BetterAttributeMod> condition) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), "fantasticlib", "betterAttributeMods");
         if (compound != null) {
            HashSet<String> attributesToRecalc = new HashSet();

            for(String attributeName : compound.getKeySet()) {
               NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
               BetterAttributeMod mod = new BetterAttributeMod();

               for(String key2 : (String[])compound2.getKeySet().toArray(new String[0])) {
                  mod.deserializeNBT(compound2.getCompoundTag(key2));
                  if (condition.test(mod)) {
                     compound2.removeTag(key2);
                     attributesToRecalc.add(attributeName);
                  }
               }
            }

            if (entity != null) {
               for(String attributeName : attributesToRecalc) {
                  BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
                  if (attribute != null) {
                     attribute.calculateTotal(entity);
                  }
               }
            }

         }
      }
   }
}
