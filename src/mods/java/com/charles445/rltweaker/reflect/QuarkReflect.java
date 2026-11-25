package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class QuarkReflect {
   public final Class c_BlockCustomChest = Class.forName("vazkii.quark.decoration.block.BlockCustomChest");
   public final Method m_BlockCustomChest_getCustomType;
   public final Method m_BlockCustomChest_isDoubleChest;
   public final Class c_VariedChests$ChestType;
   public final Field f_VariedChests$ChestType_NAME_TO_TYPE;
   public final Class c_AncientTomes;
   public final Field f_AncientTomes_ancient_tome;

   public QuarkReflect() throws Exception {
      this.m_BlockCustomChest_getCustomType = ReflectUtil.findMethod(this.c_BlockCustomChest, "getCustomType", IBlockAccess.class, BlockPos.class);
      this.m_BlockCustomChest_isDoubleChest = ReflectUtil.findMethod(this.c_BlockCustomChest, "isDoubleChest");
      this.c_VariedChests$ChestType = Class.forName("vazkii.quark.decoration.feature.VariedChests$ChestType");
      this.f_VariedChests$ChestType_NAME_TO_TYPE = ReflectUtil.findField(this.c_VariedChests$ChestType, "NAME_TO_TYPE");
      this.c_AncientTomes = Class.forName("vazkii.quark.misc.feature.AncientTomes");
      this.f_AncientTomes_ancient_tome = ReflectUtil.findField(this.c_AncientTomes, "ancient_tome");
   }

   public Object getCustomChestType(Block blockCustomChest, IBlockAccess access, BlockPos pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_BlockCustomChest_getCustomType.invoke(blockCustomChest, access, pos);
   }

   @Nullable
   public Object getChestTypeFromString(String typeTag) throws IllegalArgumentException, IllegalAccessException {
      Map nameMap = (Map)this.f_VariedChests$ChestType_NAME_TO_TYPE.get((Object)null);
      return nameMap == null ? null : nameMap.get(typeTag);
   }

   public boolean isDoubleChest(Block blockCustomChest, World world, BlockPos pos, Object chestType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_BlockCustomChest_isDoubleChest.invoke(blockCustomChest, world, pos, chestType)).booleanValue();
   }

   public boolean isBlockCustomChest(Block block) {
      return this.c_BlockCustomChest.isInstance(block);
   }

   @Nullable
   public Item getAncientTomeItem() throws IllegalArgumentException, IllegalAccessException {
      return (Item)this.f_AncientTomes_ancient_tome.get((Object)null);
   }
}
