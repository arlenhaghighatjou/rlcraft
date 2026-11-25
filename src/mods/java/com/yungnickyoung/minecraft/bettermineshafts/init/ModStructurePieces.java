package com.yungnickyoung.minecraft.bettermineshafts.init;

import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.BigTunnel;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.LayeredIntersection4;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.LayeredIntersection5;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.OreDeposit;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SideRoom;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SideRoomDungeon;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SmallTunnel;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SmallTunnelStairs;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SmallTunnelTurn;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.VerticalEntrance;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.ZombieVillagerRoom;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;

public class ModStructurePieces {
   public ModStructurePieces() {
   }

   public static void init() {
      registerPieces();
   }

   private static void registerPieces() {
      register(VerticalEntrance.class, "BMSVerticalEntrance");
      register(BigTunnel.class, "BMSBigTunnel");
      register(SmallTunnel.class, "BMSSmallTunnel");
      register(SmallTunnelTurn.class, "BMSSmallTunnelTurn");
      register(SmallTunnelStairs.class, "BMSSmallTunnelStairs");
      register(SideRoom.class, "BMSSideRoom");
      register(SideRoomDungeon.class, "BMSSideRoomDungeon");
      register(OreDeposit.class, "BMSOreDeposit");
      register(LayeredIntersection4.class, "BMSLayeredIntersection4");
      register(LayeredIntersection5.class, "BMSLayeredIntersection5");
      register(ZombieVillagerRoom.class, "BMSZombieVillagerRoom");
   }

   private static void register(Class<? extends StructureComponent> clazz, String pieceName) {
      MapGenStructureIO.registerStructureComponent(clazz, (new ResourceLocation("bettermineshafts", pieceName)).toString());
   }
}
