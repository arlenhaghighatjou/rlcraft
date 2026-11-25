package com.creativemd.creativecore.common.utils.stack;

import com.creativemd.creativecore.common.config.converation.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.gui.GuiInfoStackButton;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public abstract class InfoStack extends Ingredient {
   public static HashMap<String, Class<? extends InfoStack>> types = new HashMap();
   public static ArrayList<InfoStack.InfoStackObjectParser> objectParser = new ArrayList();
   public int stackSize = 0;
   protected static Field displayOnCreativeTab = ReflectionHelper.findField(Block.class, new String[]{"displayOnCreativeTab", "displayOnCreativeTab"});

   public static void registerObjectParser(InfoStack.InfoStackObjectParser parser) {
      objectParser.add(parser);
   }

   public static void registerType(String id, Class<? extends InfoStack> classType) {
      if (types.containsKey(id)) {
         throw new IllegalArgumentException("Id '" + id + "' is already taken!");
      } else {
         try {
            classType.getConstructor();
         } catch (SecurityException | NoSuchMethodException var3) {
            throw new InvalidParameterException("The class does not contain an empty constructor!");
         }

         types.put(id, classType);
      }
   }

   public static String getIDFromClass(Class<? extends InfoStack> classType) {
      for(Entry<String, Class<? extends InfoStack>> entry : types.entrySet()) {
         if (entry.getValue() == classType) {
            return (String)entry.getKey();
         }
      }

      System.out.println("Could not find type for class " + classType.getName());
      return "";
   }

   public static InfoStack parseObject(Object stack) {
      if (stack == null) {
         return null;
      } else if (stack instanceof InfoStack) {
         return (InfoStack)stack;
      } else {
         for(int i = 0; i < objectParser.size(); ++i) {
            InfoStack temp = null;

            try {
               temp = ((InfoStack.InfoStackObjectParser)objectParser.get(i)).parseObject(stack);
            } catch (Exception var4) {
               temp = null;
            }

            if (temp != null) {
               return temp;
            }
         }

         return null;
      }
   }

   public static InfoStack parseNBT(NBTTagCompound nbt) {
      String id = nbt.getString("id");
      Class<? extends InfoStack> classType = (Class)types.get(id);
      if (classType == null) {
         System.out.println("Missing class type=" + id);
      }

      try {
         InfoStack info = (InfoStack)classType.getConstructor().newInstance();
         info.loadFromNBT(nbt);
         return info;
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public String getID() {
      return getIDFromClass(this.getClass());
   }

   public InfoStack(int stackSize) {
      super(0);
      if (stackSize < 0) {
         stackSize = 0;
      }

      this.stackSize = stackSize;
   }

   public InfoStack() {
      super(0);
   }

   public ItemStack[] getMatchingStacks() {
      return (ItemStack[])this.getAllPossibleItemStacks().toArray(new ItemStack[0]);
   }

   public boolean apply(@Nullable ItemStack stack) {
      return stack == null ? false : this.isInstance(stack);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setString("id", this.getID());
      nbt.setInteger("count", this.stackSize);
      this.writeToNBTExtra(nbt);
      return nbt;
   }

   protected abstract void writeToNBTExtra(NBTTagCompound var1);

   public void loadFromNBT(NBTTagCompound nbt) {
      this.stackSize = nbt.getInteger("count");
      this.loadFromNBTExtra(nbt);
   }

   protected abstract void loadFromNBTExtra(NBTTagCompound var1);

   public int getAmount(ItemStack stack) {
      return this.stackSize == 0 ? Integer.MAX_VALUE : stack.getCount() / this.stackSize;
   }

   public boolean isInstance(ItemStack stack) {
      return this.isInstanceIgnoreSize(stack) && this.stackSize <= stack.getCount();
   }

   public boolean isInstance(InfoStack info) {
      return this.isInstanceIgnoreSize(info) && this.stackSize < info.stackSize ? false : false;
   }

   public abstract boolean isInstanceIgnoreSize(InfoStack var1);

   public abstract InfoStack copy();

   public ItemStack getItemStack() {
      return this.getItemStack(this.stackSize);
   }

   public abstract ArrayList<ItemStack> getAllPossibleItemStacks();

   public abstract ItemStack getItemStack(int var1);

   protected abstract boolean isStackInstanceIgnoreSize(ItemStack var1);

   public boolean isInstanceIgnoreSize(ItemStack stack) {
      return stack.isEmpty() ? false : this.isStackInstanceIgnoreSize(stack);
   }

   public boolean equals(Object object) {
      return object instanceof InfoStack && ((InfoStack)object).stackSize == this.stackSize && this.equalsIgnoreSize(object);
   }

   public abstract boolean equalsIgnoreSize(Object var1);

   protected static List<ItemStack> getAllExistingItems() {
      NonNullList<ItemStack> stacks = NonNullList.create();

      for(Item item : Item.REGISTRY) {
         item.getSubItems(item.getCreativeTab(), stacks);
      }

      for(Block block : Block.REGISTRY) {
         try {
            block.getSubBlocks((CreativeTabs)displayOnCreativeTab.get(block), stacks);
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            var4.printStackTrace();
         }
      }

      return stacks;
   }

   static {
      registerType("block", InfoBlock.class);
      registerObjectParser(new InfoStack.InfoStackObjectParser() {
         public InfoStack parseObject(Object object) {
            Block block = null;
            if (object instanceof Block) {
               block = (Block)object;
            }

            if (object instanceof ItemBlock) {
               block = Block.getBlockFromItem((Item)object);
            }

            return block != null && !(block instanceof BlockAir) ? new InfoBlock(block) : null;
         }
      });
      registerType("item", InfoItem.class);
      registerObjectParser(new InfoStack.InfoStackObjectParser() {
         public InfoStack parseObject(Object object) {
            return object instanceof Item && !(object instanceof ItemBlock) ? new InfoItem((Item)object) : null;
         }
      });
      registerType("itemstack", InfoItemStack.class);
      registerObjectParser(new InfoStack.InfoStackObjectParser() {
         public InfoStack parseObject(Object object) {
            if (object instanceof ItemStack) {
               if (((ItemStack)object).getMetadata() == 32767) {
                  return (InfoStack)(((ItemStack)object).getItem() instanceof ItemBlock ? new InfoBlock(Block.getBlockFromItem(((ItemStack)object).getItem())) : new InfoItem(((ItemStack)object).getItem()));
               } else {
                  return new InfoItemStack((ItemStack)object);
               }
            } else {
               return null;
            }
         }
      });
      registerType("material", InfoMaterial.class);
      registerObjectParser(new InfoStack.InfoStackObjectParser() {
         public InfoStack parseObject(Object object) {
            return object instanceof Material ? new InfoMaterial((Material)object) : null;
         }
      });
      registerType("ore", InfoOre.class);
      registerObjectParser(new InfoStack.InfoStackObjectParser() {
         public InfoStack parseObject(Object object) {
            if (object instanceof String) {
               return new InfoOre((String)object);
            } else {
               if (object instanceof ArrayList) {
                  ArrayList<Integer> oresIDs = new ArrayList();
                  ArrayList ores = (ArrayList)object;

                  for(int i = 0; i < ores.size(); ++i) {
                     ArrayList<Integer> neworesIDs = new ArrayList();
                     int[] oreIDsofStack = OreDictionary.getOreIDs((ItemStack)ores.get(i));

                     for(int j = 0; j < oreIDsofStack.length; ++j) {
                        if (i == 0 || oresIDs.contains(Integer.valueOf(oreIDsofStack[j]))) {
                           neworesIDs.add(Integer.valueOf(oreIDsofStack[j]));
                        }
                     }

                     oresIDs = neworesIDs;
                  }

                  if (oresIDs.size() == 1) {
                     return new InfoOre(OreDictionary.getOreName(((Integer)oresIDs.get(0)).intValue()));
                  }
               }

               return null;
            }
         }
      });
      registerType("name", InfoName.class);
      registerType("fuel", InfoFuel.class);
      ConfigTypeConveration.registerSpecialType((x) -> {
         return InfoStack.class.isAssignableFrom(x);
      }, new ConfigTypeConveration.SimpleConfigTypeConveration<InfoStack>() {
         public InfoStack readElement(InfoStack defaultValue, boolean loadDefault, JsonElement element) {
            if (element.isJsonPrimitive() && ((JsonPrimitive)element).isString()) {
               try {
                  return InfoStack.parseNBT(JsonToNBT.getTagFromJson(element.getAsString()));
               } catch (NBTException var5) {
                  var5.printStackTrace();
               }
            }

            return defaultValue;
         }

         public JsonElement writeElement(InfoStack value, InfoStack defaultValue, boolean saveDefault) {
            return new JsonPrimitive(value.writeToNBT(new NBTTagCompound()).toString());
         }

         @SideOnly(Side.CLIENT)
         public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
            parent.addControl(new GuiInfoStackButton("data", 0, 0, Math.min(150, parent.width - 50), 14, new InfoBlock(Blocks.DIRT)));
         }

         @SideOnly(Side.CLIENT)
         public void loadValue(InfoStack value, GuiParent parent) {
            GuiInfoStackButton button = (GuiInfoStackButton)parent.get("data");
            button.set(value);
         }

         @SideOnly(Side.CLIENT)
         protected InfoStack saveValue(GuiParent parent, Class clazz) {
            GuiInfoStackButton button = (GuiInfoStackButton)parent.get("data");
            return button.get();
         }

         public InfoStack set(ConfigKey.ConfigKeyField key, InfoStack value) {
            return value;
         }
      });
   }

   public abstract static class InfoStackObjectParser {
      public InfoStackObjectParser() {
      }

      public abstract InfoStack parseObject(Object var1);
   }
}
