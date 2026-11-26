package net.minecraft.enchantment;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumEnchantmentType
{
    private static final List<EnumEnchantmentType> VALUES = new ArrayList<>();
    private static final Map<String, EnumEnchantmentType> BY_NAME = new HashMap<>();
    public static final EnumEnchantmentType ALL = new EnumEnchantmentType("ALL")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            for (int i = 0, l = VALUES.size(); i < l; ++i)
            {
                EnumEnchantmentType enumenchantmenttype = VALUES.get(i);

                if (enumenchantmenttype != EnumEnchantmentType.ALL && enumenchantmenttype.canEnchantItem(itemIn))
                {
                    return true;
                }
            }

            return false;
        }
    };
    public static final EnumEnchantmentType ARMOR = new EnumEnchantmentType("ARMOR")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor;
        }
    };
    public static final EnumEnchantmentType ARMOR_FEET = new EnumEnchantmentType("ARMOR_FEET")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).armorType == EntityEquipmentSlot.FEET;
        }
    };
    public static final EnumEnchantmentType ARMOR_LEGS = new EnumEnchantmentType("ARMOR_LEGS")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).armorType == EntityEquipmentSlot.LEGS;
        }
    };
    public static final EnumEnchantmentType ARMOR_CHEST = new EnumEnchantmentType("ARMOR_CHEST")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).armorType == EntityEquipmentSlot.CHEST;
        }
    };
    public static final EnumEnchantmentType ARMOR_HEAD = new EnumEnchantmentType("ARMOR_HEAD")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).armorType == EntityEquipmentSlot.HEAD;
        }
    };
    public static final EnumEnchantmentType WEAPON = new EnumEnchantmentType("WEAPON")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemSword;
        }
    };
    public static final EnumEnchantmentType DIGGER = new EnumEnchantmentType("DIGGER")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemTool;
        }
    };
    public static final EnumEnchantmentType FISHING_ROD = new EnumEnchantmentType("FISHING_ROD")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemFishingRod;
        }
    };
    public static final EnumEnchantmentType BREAKABLE = new EnumEnchantmentType("BREAKABLE")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn.isDamageable();
        }
    };
    public static final EnumEnchantmentType BOW = new EnumEnchantmentType("BOW")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemBow;
        }
    };
    public static final EnumEnchantmentType WEARABLE = new EnumEnchantmentType("WEARABLE")
    {
        public boolean canEnchantItem(Item itemIn)
        {
            boolean flag = itemIn instanceof ItemBlock && ((ItemBlock)itemIn).getBlock() instanceof BlockPumpkin;
            return itemIn instanceof ItemArmor || itemIn instanceof ItemElytra || itemIn instanceof ItemSkull || flag;
        }
    };

    private final String name;
    private final int ordinal;
    private com.google.common.base.Predicate<Item> delegate = null;

    private EnumEnchantmentType(String name)
    {
        this.name = name;
        this.ordinal = VALUES.size();
        VALUES.add(this);
        BY_NAME.put(name, this);
    }

    private EnumEnchantmentType(String name, com.google.common.base.Predicate<Item> delegate)
    {
        this(name);
        this.delegate = delegate;
    }

    public static EnumEnchantmentType register(String name, com.google.common.base.Predicate<Item> delegate)
    {
        return new EnumEnchantmentType(name, delegate);
    }

    public static EnumEnchantmentType valueOf(String name)
    {
        EnumEnchantmentType type = BY_NAME.get(java.util.Objects.requireNonNull(name, "name"));
        if (type == null)
        {
            throw new IllegalArgumentException("No enum constant net.minecraft.enchantment.EnumEnchantmentType." + name);
        }
        return type;
    }

    public static EnumEnchantmentType[] values()
    {
        return VALUES.toArray(new EnumEnchantmentType[VALUES.size()]);
    }

    public String name()
    {
        return this.name;
    }

    public int ordinal()
    {
        return this.ordinal;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    public boolean canEnchantItem(Item itemIn)
    {
        return this.delegate == null ? false : this.delegate.apply(itemIn);
    }
}
