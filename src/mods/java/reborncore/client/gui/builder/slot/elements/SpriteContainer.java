package reborncore.client.gui.builder.slot.elements;

import java.util.ArrayList;
import java.util.List;

public class SpriteContainer {
   public List<OffsetSprite> offsetSprites = new ArrayList();

   public SpriteContainer() {
   }

   public SpriteContainer setSprite(int index, OffsetSprite sprite) {
      this.offsetSprites.set(index, sprite);
      return this;
   }

   public SpriteContainer setSprite(int index, ISprite sprite, int offsetX, int offsetY) {
      if (sprite instanceof Sprite) {
         this.offsetSprites.set(index, (new OffsetSprite(sprite)).setOffsetX(((Sprite)sprite).offsetX + offsetX).setOffsetY(((Sprite)sprite).offsetY + offsetY));
      } else {
         this.offsetSprites.set(index, new OffsetSprite(sprite, offsetX, offsetY));
      }

      return this;
   }

   public SpriteContainer setSprite(int index, ISprite sprite) {
      if (sprite instanceof Sprite) {
         this.offsetSprites.set(index, (new OffsetSprite(sprite)).setOffsetX(((Sprite)sprite).offsetX).setOffsetY(((Sprite)sprite).offsetY));
      } else {
         this.offsetSprites.add(index, new OffsetSprite(sprite));
      }

      return this;
   }

   public SpriteContainer addSprite(OffsetSprite sprite) {
      this.offsetSprites.add(sprite);
      return this;
   }

   public SpriteContainer addSprite(ISprite sprite, int offsetX, int offsetY) {
      if (sprite instanceof Sprite) {
         this.offsetSprites.add((new OffsetSprite(sprite)).setOffsetX(((Sprite)sprite).offsetX + offsetX).setOffsetY(((Sprite)sprite).offsetY + offsetY));
      } else {
         this.offsetSprites.add(new OffsetSprite(sprite, offsetX, offsetY));
      }

      return this;
   }

   public SpriteContainer addSprite(ISprite sprite) {
      if (sprite instanceof Sprite) {
         this.offsetSprites.add((new OffsetSprite(sprite)).setOffsetX(((Sprite)sprite).offsetX).setOffsetY(((Sprite)sprite).offsetY));
      } else {
         this.offsetSprites.add(new OffsetSprite(sprite));
      }

      return this;
   }
}
