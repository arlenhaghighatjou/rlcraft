package vazkii.quark.base.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public final class BeaconReplacementHandler {
   private static Potion[][] defaultEffectsList = (Potion[][])null;
   private static List<BeaconReplacementHandler.Replacer> replacers;

   public BeaconReplacementHandler() {
   }

   public static void parse(String[] lines) {
      replacers = new LinkedList();

      for(String s : lines) {
         BeaconReplacementHandler.Replacer r = BeaconReplacementHandler.Replacer.fromString(s);
         if (r != null) {
            replacers.add(r);
         }
      }

      commit();
   }

   private static void commit() {
      if (defaultEffectsList == null) {
         defaultEffectsList = new Potion[TileEntityBeacon.EFFECTS_LIST.length][2];

         for(int i = 0; i < TileEntityBeacon.EFFECTS_LIST.length; ++i) {
            Potion[] a = TileEntityBeacon.EFFECTS_LIST[i];

            for(int j = 0; j < a.length && j < defaultEffectsList[i].length; ++j) {
               defaultEffectsList[i][j] = a[j];
            }
         }
      }

      Set<Potion> validEffects = (Set)ObfuscationReflectionHelper.getPrivateValue(TileEntityBeacon.class, (Object)null, "VALID_EFFECTS");

      for(BeaconReplacementHandler.Replacer r : replacers) {
         validEffects.add(r.potion);
      }

   }

   public static void update(TileEntityBeacon beacon) {
      for(int i = 0; i < TileEntityBeacon.EFFECTS_LIST.length; ++i) {
         Potion[] a = TileEntityBeacon.EFFECTS_LIST[i];

         for(int j = 0; j < a.length && j < defaultEffectsList[i].length; ++j) {
            a[j] = defaultEffectsList[i][j];
         }
      }

      BlockPos pos = beacon.getPos();
      World world = beacon.getWorld();

      for(int i = -1; i < 2; ++i) {
         for(int j = -1; j < 2; ++j) {
            if (i != 0 || j != 0) {
               BlockPos targetPos = pos.add(i, 0, j);
               IBlockState state = world.getBlockState(targetPos);
               replacers.forEach((r) -> {
                  r.replace(state);
               });
            }
         }
      }

   }

   private static class Replacer {
      private final Block block;
      private final int meta;
      private final int layer;
      private final int effect;
      private final Potion potion;

      public Replacer(Block block, int meta, int layer, int effect, Potion potion) {
         this.block = block;
         this.meta = meta;
         this.layer = layer;
         this.effect = effect;
         this.potion = potion;
      }

      private static BeaconReplacementHandler.Replacer fromString(String s) {
         String[] tokens = s.split(",");
         if (tokens.length != 5) {
            return null;
         } else {
            try {
               Block block = Block.getBlockFromName(tokens[0]);
               int meta = Integer.parseInt(tokens[1]);
               int layer = Integer.parseInt(tokens[2]);
               int effect = Integer.parseInt(tokens[3]);
               Potion potion = Potion.getPotionFromResourceLocation(tokens[4]);
               return potion != null && effect >= 0 && effect <= 1 && layer >= 0 && layer <= 3 ? new BeaconReplacementHandler.Replacer(block, meta, layer, effect, potion) : null;
            } catch (NumberFormatException var7) {
               return null;
            }
         }
      }

      public void replace(IBlockState stateAt) {
         if (this.block == null || stateAt.getBlock() == this.block && (this.meta == -1 || this.block.getMetaFromState(stateAt) == this.meta)) {
            TileEntityBeacon.EFFECTS_LIST[this.layer][this.effect] = this.potion;
         }

      }
   }
}
