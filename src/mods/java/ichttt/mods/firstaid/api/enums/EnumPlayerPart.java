package ichttt.mods.firstaid.api.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.inventory.EntityEquipmentSlot;

public enum EnumPlayerPart {
   HEAD(EntityEquipmentSlot.HEAD),
   LEFT_ARM(EntityEquipmentSlot.CHEST),
   LEFT_LEG(EntityEquipmentSlot.LEGS),
   LEFT_FOOT(EntityEquipmentSlot.FEET),
   BODY(EntityEquipmentSlot.CHEST),
   RIGHT_ARM(EntityEquipmentSlot.CHEST),
   RIGHT_LEG(EntityEquipmentSlot.LEGS),
   RIGHT_FOOT(EntityEquipmentSlot.FEET);

   public static final EnumPlayerPart[] VALUES = values();
   public final byte id = (byte)(this.ordinal() + 1);
   private ImmutableList<EnumPlayerPart> neighbours;
   public final EntityEquipmentSlot slot;

   private EnumPlayerPart(EntityEquipmentSlot slot) {
      this.slot = slot;
   }

   public ImmutableList<EnumPlayerPart> getNeighbours() {
      if (this.neighbours == null) {
         synchronized(this) {
            if (this.neighbours == null) {
               Builder<EnumPlayerPart> builder = ImmutableList.builder();
               builder.addAll(this.getNeighboursDown());
               builder.addAll(this.getNeighboursUp());
               builder.addAll(this.getNeighboursLeft());
               builder.addAll(this.getNeighboursRight());
               this.neighbours = builder.build();
            }
         }
      }

      return this.neighbours;
   }

   public static EnumPlayerPart fromID(int id) {
      switch(id) {
      case 1:
         return HEAD;
      case 2:
         return LEFT_ARM;
      case 3:
         return LEFT_LEG;
      case 4:
         return LEFT_FOOT;
      case 5:
         return BODY;
      case 6:
         return RIGHT_ARM;
      case 7:
         return RIGHT_LEG;
      case 8:
         return RIGHT_FOOT;
      default:
         throw new IndexOutOfBoundsException("Invalid id " + id);
      }
   }

   @Nonnull
   private List<EnumPlayerPart> getNeighboursUp() {
      switch(this) {
      case BODY:
         return Collections.singletonList(HEAD);
      case LEFT_LEG:
      case RIGHT_LEG:
         return Collections.singletonList(BODY);
      case LEFT_FOOT:
         return Collections.singletonList(LEFT_LEG);
      case RIGHT_FOOT:
         return Collections.singletonList(RIGHT_LEG);
      default:
         return Collections.emptyList();
      }
   }

   @Nonnull
   private List<EnumPlayerPart> getNeighboursDown() {
      switch(this) {
      case BODY:
         return Arrays.asList(LEFT_LEG, RIGHT_LEG);
      case LEFT_LEG:
         return Collections.singletonList(LEFT_FOOT);
      case RIGHT_LEG:
         return Collections.singletonList(RIGHT_FOOT);
      case LEFT_FOOT:
      case RIGHT_FOOT:
      default:
         return Collections.emptyList();
      case HEAD:
         return Collections.singletonList(BODY);
      }
   }

   @Nonnull
   private List<EnumPlayerPart> getNeighboursLeft() {
      switch(this) {
      case BODY:
         return Collections.singletonList(LEFT_ARM);
      case LEFT_LEG:
      case LEFT_FOOT:
      case HEAD:
      default:
         return Collections.emptyList();
      case RIGHT_LEG:
         return Collections.singletonList(LEFT_LEG);
      case RIGHT_FOOT:
         return Collections.singletonList(LEFT_FOOT);
      case RIGHT_ARM:
         return Collections.singletonList(BODY);
      }
   }

   @Nonnull
   private List<EnumPlayerPart> getNeighboursRight() {
      switch(this) {
      case BODY:
         return Collections.singletonList(RIGHT_ARM);
      case LEFT_LEG:
         return Collections.singletonList(RIGHT_LEG);
      case RIGHT_LEG:
      case RIGHT_FOOT:
      case HEAD:
      case RIGHT_ARM:
      default:
         return Collections.emptyList();
      case LEFT_FOOT:
         return Collections.singletonList(RIGHT_FOOT);
      case LEFT_ARM:
         return Collections.singletonList(BODY);
      }
   }

   /** @deprecated */
   @Deprecated
   public EnumPlayerPart getUp() {
      if (this.id == 5) {
         throw new IndexOutOfBoundsException("There is no part up from " + this.id);
      } else {
         return fromID(this.id - 1);
      }
   }

   /** @deprecated */
   @Deprecated
   public EnumPlayerPart getDown() {
      if (this.id == 4) {
         throw new IndexOutOfBoundsException("There is no part down from " + this.id);
      } else {
         return fromID(this.id + 1);
      }
   }

   /** @deprecated */
   @Deprecated
   public EnumPlayerPart getLeft() {
      return fromID(this.id - 4);
   }

   /** @deprecated */
   @Deprecated
   public EnumPlayerPart getRight() {
      return fromID(this.id + 4);
   }

   static {
      for(EnumPlayerPart value : VALUES) {
         List<EnumPlayerPart> neighbours = value.getNeighbours();
         if (neighbours.contains(value)) {
            throw new RuntimeException(value + " contains itself as a neighbour!");
         }

         if (neighbours.isEmpty()) {
            throw new RuntimeException(value + " does not have any neighbours!");
         }

         if (EnumSet.copyOf(neighbours).size() != neighbours.size()) {
            throw new RuntimeException(value + " neighbours contain the same part multiple times!");
         }

         Set<EnumPlayerPart> hopefullyAllParts = EnumSet.copyOf(neighbours);
         int oldSize = -1;

         while(oldSize != hopefullyAllParts.size()) {
            oldSize = hopefullyAllParts.size();
            Set<EnumPlayerPart> neighboursOfNeighbours = EnumSet.noneOf(EnumPlayerPart.class);

            for(EnumPlayerPart part : hopefullyAllParts) {
               neighboursOfNeighbours.addAll(part.getNeighbours());
            }

            hopefullyAllParts.addAll(neighboursOfNeighbours);
         }

         if (hopefullyAllParts.size() != VALUES.length) {
            throw new RuntimeException(value + " could not read all player parts " + Arrays.toString(hopefullyAllParts.toArray(new EnumPlayerPart[0])));
         }
      }

   }
}
