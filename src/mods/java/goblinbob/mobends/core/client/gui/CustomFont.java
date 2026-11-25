package goblinbob.mobends.core.client.gui;

import java.util.HashMap;
import net.minecraft.util.ResourceLocation;

public class CustomFont {
   public static final CustomFont BOLD = new CustomFont("bold", 256, 32, new HashMap<Character, CustomFont.Symbol>() {
      {
         this.put(Character.valueOf('a'), new CustomFont.Symbol(0, 0, 9, 11));
         this.put(Character.valueOf('b'), new CustomFont.Symbol(10, 0, 9, 11));
         this.put(Character.valueOf('c'), new CustomFont.Symbol(20, 0, 9, 11));
         this.put(Character.valueOf('d'), new CustomFont.Symbol(30, 0, 8, 11));
         this.put(Character.valueOf('e'), new CustomFont.Symbol(39, 0, 9, 11));
         this.put(Character.valueOf('f'), new CustomFont.Symbol(49, 0, 9, 11));
         this.put(Character.valueOf('g'), new CustomFont.Symbol(59, 0, 9, 11));
         this.put(Character.valueOf('h'), new CustomFont.Symbol(69, 0, 9, 11));
         this.put(Character.valueOf('i'), new CustomFont.Symbol(79, 0, 3, 11));
         this.put(Character.valueOf('j'), new CustomFont.Symbol(83, 0, 8, 11));
         this.put(Character.valueOf('k'), new CustomFont.Symbol(92, 0, 10, 11));
         this.put(Character.valueOf('l'), new CustomFont.Symbol(103, 0, 9, 11));
         this.put(Character.valueOf('m'), new CustomFont.Symbol(113, 0, 10, 11));
         this.put(Character.valueOf('n'), new CustomFont.Symbol(124, 0, 9, 11));
         this.put(Character.valueOf('o'), new CustomFont.Symbol(134, 0, 9, 11));
         this.put(Character.valueOf('p'), new CustomFont.Symbol(144, 0, 9, 11));
         this.put(Character.valueOf('q'), new CustomFont.Symbol(154, 0, 10, 11));
         this.put(Character.valueOf('r'), new CustomFont.Symbol(165, 0, 9, 11));
         this.put(Character.valueOf('s'), new CustomFont.Symbol(175, 0, 9, 11));
         this.put(Character.valueOf('t'), new CustomFont.Symbol(185, 0, 9, 11));
         this.put(Character.valueOf('u'), new CustomFont.Symbol(195, 0, 9, 11));
         this.put(Character.valueOf('v'), new CustomFont.Symbol(205, 0, 10, 11));
         this.put(Character.valueOf('w'), new CustomFont.Symbol(216, 0, 10, 11));
         this.put(Character.valueOf('x'), new CustomFont.Symbol(227, 0, 11, 11));
         this.put(Character.valueOf('y'), new CustomFont.Symbol(239, 0, 11, 11));
         this.put(Character.valueOf('z'), new CustomFont.Symbol(0, 12, 9, 11));
         this.put(Character.valueOf('-'), new CustomFont.Symbol(10, 12, 9, 11));
      }
   });
   public final int atlasWidth;
   public final int atlasHeight;
   protected final ResourceLocation resourceLocation;
   protected final HashMap<Character, CustomFont.Symbol> symbolMap;

   public CustomFont(String textureName, int atlasWidth, int atlasHeight, HashMap<Character, CustomFont.Symbol> symbolMap) {
      this.atlasWidth = atlasWidth;
      this.atlasHeight = atlasHeight;
      this.resourceLocation = new ResourceLocation("mobends", "textures/gui/fonts/" + textureName + ".png");
      this.symbolMap = symbolMap;
   }

   public CustomFont.Symbol getSymbol(char charAt) {
      return this.symbolMap.containsKey(Character.valueOf(charAt)) ? (CustomFont.Symbol)this.symbolMap.get(Character.valueOf(charAt)) : null;
   }

   public static class Symbol {
      public int u;
      public int v;
      public int width;
      public int height;
      public int offsetX;
      public int offsetY;

      public Symbol(int u, int v, int width, int height, int offsetX, int offsetY) {
         this.u = u;
         this.v = v;
         this.width = width;
         this.height = height;
         this.offsetX = offsetX;
         this.offsetY = offsetY;
      }

      public Symbol(int u, int v, int width, int height) {
         this(u, v, width, height, 0, 0);
      }
   }
}
