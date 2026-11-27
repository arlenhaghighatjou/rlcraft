package net.mcft.copy.backpacks.client.gui;

import net.lax1dude.eaglercraft.v1_8.HString;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ElementInfo {
   public final GuiElementBase element;
   public final int globalX;
   public final int globalY;
   public final int relX;
   public final int relY;
   public final int width;
   public final int height;

   public ElementInfo(GuiElementBase element) {
      this(element, 0, 0, 0, 0);
   }

   public ElementInfo(GuiElementBase element, int globalX, int globalY, int relX, int relY) {
      this.element = element;
      this.globalX = globalX;
      this.globalY = globalY;
      this.relX = relX;
      this.relY = relY;
      this.width = element.getWidth();
      this.height = element.getHeight();
   }

   public ElementInfo getChild(GuiElementBase child) {
      if (!(this.element instanceof GuiContainer)) {
         throw new UnsupportedOperationException("This element is not a GuiContainer");
      } else if (child.getParent() != this.element) {
         throw new IllegalArgumentException("This element is not the parent of the specified child");
      } else {
         GuiContainer container = (GuiContainer)this.element;
         int childX = container.getChildX(child);
         int childY = container.getChildY(child);
         return new ElementInfo(child, this.globalX + childX, this.globalY + childY, childX, childY);
      }
   }

   public Stream<ElementInfo> getElementsAt(int x, int y) {
      Stream<ElementInfo> stream = Stream.of(this);
      if (this.element instanceof GuiContainer) {
         stream = Stream.concat(stream, (Stream)this.getChildElements().filter((child) -> {
            return child.contains(x, y);
         }).findAny().map((child) -> {
            return child.getElementsAt(x, y);
         }).orElse(Stream.empty()));
      }

      return stream;
   }

   public static LinkedList<ElementInfo> getElementHierarchy(GuiElementBase element) {
      return getElementHierarchy(element, new LinkedList());
   }

   private static LinkedList<ElementInfo> getElementHierarchy(GuiElementBase element, LinkedList<ElementInfo> list) {
      list.addFirst(element.getParent() != null ? ((ElementInfo)getElementHierarchy(element.getParent(), list).getFirst()).getChild(element) : new ElementInfo(element));
      return list;
   }

   public Stream<ElementInfo> getChildElements() {
      if (!(this.element instanceof GuiContainer)) {
         return Stream.empty();
      } else {
         GuiContainer container = (GuiContainer)this.element;
         return container.children.stream().map((child) -> {
            return this.getChild(child);
         });
      }
   }

   public boolean contains(int x, int y) {
      return GuiElementBase.regionContains(this.globalX, this.globalY, this.width, this.height, x, y);
   }

   public String toString() {
      Class<?> elementClass = this.element.getClass();
      String name = elementClass.getSimpleName();
      if (name.isEmpty()) {
         name = elementClass.getSuperclass().getSimpleName();
      }

      Class<?> enclosingClass = elementClass.getEnclosingClass();
      if (enclosingClass != null && !enclosingClass.getSimpleName().isEmpty()) {
         name = enclosingClass.getSimpleName() + "." + name;
      }

      return HString.format("(%d,%d : %d,%d) %s", this.globalX, this.globalY, this.width, this.height, name);
   }
}
