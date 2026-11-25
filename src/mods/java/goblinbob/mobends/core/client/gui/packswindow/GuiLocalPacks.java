package goblinbob.mobends.core.client.gui.packswindow;

import goblinbob.mobends.core.client.gui.GuiDragger;
import goblinbob.mobends.core.flux.IObserver;
import goblinbob.mobends.core.flux.ISubscriber;
import goblinbob.mobends.core.flux.Subscription;
import goblinbob.mobends.core.pack.IBendsPack;
import goblinbob.mobends.core.pack.InvalidPackFormatException;
import goblinbob.mobends.core.pack.LocalBendsPack;
import goblinbob.mobends.core.pack.PackManager;
import goblinbob.mobends.core.util.ErrorReporter;
import goblinbob.mobends.core.util.IDisposable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class GuiLocalPacks extends Gui implements ISubscriber, IDisposable {
   private final GuiPackList availablePacksList;
   private final GuiPackList appliedPacksList;
   private final LinkedList<GuiPackEntry> availablePacks;
   private final LinkedList<GuiPackEntry> appliedPacks;
   private final GuiDragger<GuiPackEntry> dragger;
   private final FontRenderer fontRenderer;
   private int x;
   private int y;
   private List<Subscription<?>> subscriptions = new LinkedList();

   public GuiLocalPacks() {
      this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
      this.availablePacks = new LinkedList();
      this.appliedPacks = new LinkedList();
      this.availablePacksList = new GuiPackList(this.availablePacks);
      this.appliedPacksList = new GuiPackList(this.appliedPacks);
      this.dragger = new GuiDragger<GuiPackEntry>();
      this.resetPackLists();
      this.trackSubscription(this.availablePacksList.elementClickedObservable.subscribe((element) -> {
         this.appliedPacksList.getListElements().forEach((e) -> {
            e.setSelected(false);
         });
         this.dragger.setDraggedElement(element);
      }));
      this.trackSubscription(this.appliedPacksList.elementClickedObservable.subscribe((element) -> {
         this.availablePacksList.getListElements().forEach((e) -> {
            e.setSelected(false);
         });
         this.dragger.setDraggedElement(element);
      }));
   }

   public void dispose() {
      this.removeSubscriptions();
   }

   public List<Subscription<?>> getSubscriptions() {
      return this.subscriptions;
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
      this.availablePacksList.initGui(x + 9, y + 23);
      this.appliedPacksList.initGui(x + 280 - 126 - 1, y + 23);
   }

   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      boolean eventHandled = false;
      eventHandled = eventHandled | this.availablePacksList.handleMouseClicked(mouseX, mouseY, button);
      eventHandled = eventHandled | this.appliedPacksList.handleMouseClicked(mouseX, mouseY, button);
      return eventHandled;
   }

   public void mouseReleased(int mouseX, int mouseY, int button) {
      this.availablePacksList.handleMouseReleased(mouseX, mouseY, button);
      this.appliedPacksList.handleMouseReleased(mouseX, mouseY, button);
      this.dragger.stopDragging();
      this.resolveAppliedPacks();
   }

   public boolean handleMouseInput() {
      boolean handled = this.availablePacksList.handleMouseInput();
      handled = handled | this.appliedPacksList.handleMouseInput();
      return handled;
   }

   public void update(int mouseX, int mouseY) {
      this.availablePacksList.update(mouseX, mouseY);
      this.appliedPacksList.update(mouseX, mouseY);
      this.dragger.update(mouseX, mouseY);
      GuiPackEntry element = this.dragger.getDraggedElement();
      if (element != null) {
         GuiPackList list = mouseX < this.x + 140 ? this.availablePacksList : this.appliedPacksList;
         int y = element.getDragY() - element.getDragPivotY() - list.getY() + list.getScrollAmount() + element.getHeight() / 2;
         int order = y / (element.getHeight() + list.getSpacing());
         if (list != element.getParentList()) {
            element.getParentList().removeElement(element);
            list.insertOrMoveElement(element, order);
            element.setParentList(list);
         } else if (order != element.getOrder()) {
            list.insertOrMoveElement(element, order);
         }
      }

   }

   public void draw(float partialTicks) {
      this.availablePacksList.draw(partialTicks);
      this.appliedPacksList.draw(partialTicks);
      this.drawCenteredString(this.fontRenderer, I18n.format("mobends.gui.unusedpacks", new Object[0]), this.x + 70, this.y + 8, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("mobends.gui.appliedpacks", new Object[0]), this.x + 210 + 6, this.y + 8, 16777215);
      GuiPackEntry element = this.dragger.getDraggedElement();
      if (element != null) {
         element.draw(partialTicks);
      }

   }

   private void resetPackLists() {
      this.availablePacksList.clearElements();
      this.appliedPacksList.clearElements();
      Collection<IBendsPack> appliedPacks = PackManager.INSTANCE.getAppliedPacks();

      for(LocalBendsPack pack : PackManager.INSTANCE.getLocalPacks()) {
         if (!appliedPacks.stream().anyMatch((p) -> {
            return p.getKey().equals(pack.getKey());
         })) {
            GuiPackEntry entry = new GuiPackEntry(pack);
            entry.setParentList(this.availablePacksList);
            this.availablePacksList.addElement(entry);
         }
      }

      for(IBendsPack pack : appliedPacks) {
         GuiPackEntry entry = new GuiPackEntry(pack);
         entry.setParentList(this.appliedPacksList);
         this.appliedPacksList.addElement(entry);
      }

   }

   private void resolveAppliedPacks() {
      List<String> packNames = new ArrayList();

      for(GuiPackEntry entry : this.appliedPacks) {
         packNames.add(entry.name);
      }

      try {
         PackManager.INSTANCE.setAppliedPacks(packNames, true);
      } catch (InvalidPackFormatException var4) {
         var4.printStackTrace();
         this.resetPackLists();
         ErrorReporter.showErrorToPlayer(var4);
      }

   }
}
