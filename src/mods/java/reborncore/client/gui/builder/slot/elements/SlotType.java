package reborncore.client.gui.builder.slot.elements;

public enum SlotType {
   NORMAL(1, 1, Sprite.SLOT_NORMAL, Sprite.BUTTON_SLOT_NORMAL, Sprite.BUTTON_HOVER_OVERLAY_SLOT_NORMAL);

   int slotOffsetX;
   int slotOffsetY;
   Sprite sprite;
   Sprite buttonSprite;
   Sprite buttonHoverOverlay;

   private SlotType(int slotOffsetX, int slotOffsetY, Sprite sprite, Sprite buttonSprite, Sprite buttonHoverOverlay) {
      this.slotOffsetX = slotOffsetX;
      this.slotOffsetY = slotOffsetY;
      this.sprite = sprite;
      this.buttonSprite = buttonSprite;
      this.buttonHoverOverlay = buttonHoverOverlay;
   }

   private SlotType(int slotOffset, Sprite sprite) {
      this.slotOffsetX = slotOffset;
      this.slotOffsetY = slotOffset;
      this.sprite = sprite;
   }

   public int getSlotOffsetX() {
      return this.slotOffsetX;
   }

   public int getSlotOffsetY() {
      return this.slotOffsetY;
   }

   public Sprite getSprite() {
      return this.sprite;
   }

   public Sprite getButtonSprite() {
      return this.buttonSprite;
   }

   public Sprite getButtonHoverOverlay() {
      return this.buttonHoverOverlay;
   }
}
