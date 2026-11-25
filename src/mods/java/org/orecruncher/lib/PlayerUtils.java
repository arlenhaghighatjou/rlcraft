package org.orecruncher.lib;

import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.random.XorShiftRandom;

public final class PlayerUtils {
   private PlayerUtils() {
   }

   @Nullable
   public static EntityPlayer getRandomPlayer(@Nonnull World world) {
      List<EntityPlayer> players = world.getPlayers(EntityPlayer.class, Predicates.alwaysTrue());
      if (players.size() == 1) {
         return (EntityPlayer)players.get(0);
      } else {
         return players.size() > 0 ? (EntityPlayer)players.get(XorShiftRandom.current().nextInt(players.size())) : null;
      }
   }

   @SideOnly(Side.CLIENT)
   public static boolean isHolding(@Nonnull EntityPlayer player, @Nonnull Item item, @Nonnull EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      return ItemStackUtil.isValidItemStack(stack) && stack.getItem() == item;
   }

   @SideOnly(Side.CLIENT)
   public static boolean isHolding(@Nonnull EntityPlayer player, @Nonnull Item item) {
      return isHolding(player, item, EnumHand.MAIN_HAND) || isHolding(player, item, EnumHand.OFF_HAND);
   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public static Entity entityImLookingAt(@Nonnull EntityPlayer player) {
      RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
      return result != null && result.typeOfHit == Type.ENTITY ? result.entityHit : null;
   }
}
