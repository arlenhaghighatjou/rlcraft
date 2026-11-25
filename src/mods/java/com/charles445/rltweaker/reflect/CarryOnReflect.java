package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CarryOnReflect {
   public final Class c_RegistrationHandler = Class.forName("tschipp.carryon.common.handler.RegistrationHandler");
   public final Field f_RegistrationHandler_itemTile;
   public final Class c_ItemTile;
   public final Method m_ItemTile_hasTileData;
   public final Method m_ItemTile_getBlock;

   public CarryOnReflect() throws Exception {
      this.f_RegistrationHandler_itemTile = ReflectUtil.findField(this.c_RegistrationHandler, "itemTile");
      this.c_ItemTile = Class.forName("tschipp.carryon.common.item.ItemTile");
      this.m_ItemTile_hasTileData = ReflectUtil.findMethod(this.c_ItemTile, "hasTileData");
      this.m_ItemTile_getBlock = ReflectUtil.findMethod(this.c_ItemTile, "getBlock", ItemStack.class);
   }

   public boolean isItemTile(Item item) throws IllegalArgumentException, IllegalAccessException {
      return item == (Item)this.f_RegistrationHandler_itemTile.get((Object)null);
   }

   public boolean hasTileData(ItemStack stack) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_ItemTile_hasTileData.invoke((Object)null, stack)).booleanValue();
   }

   public Block getItemTileBlock(ItemStack itemTileStack) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return (Block)this.m_ItemTile_getBlock.invoke((Object)null, itemTileStack);
   }
}
