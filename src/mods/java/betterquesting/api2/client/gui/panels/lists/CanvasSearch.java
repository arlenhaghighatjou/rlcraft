package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api2.client.gui.misc.IGuiRect;
import com.google.common.base.Stopwatch;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class CanvasSearch<T, E> extends CanvasScrolling {
   private String searchTerm = "";
   private Iterator<E> searching = null;
   private final Stopwatch searchTime = Stopwatch.createStarted();
   private int resultWidth = 256;
   private int searchIdx = 0;
   private final ArrayDeque<T> pendingResults = new ArrayDeque();
   private final List<T> savedResults = new ArrayList();

   public CanvasSearch(IGuiRect rect) {
      super(rect);
   }

   public void setSearchFilter(String text) {
      this.searchTerm = text.toLowerCase();
      this.refreshSearch();
   }

   public void initPanel() {
      super.initPanel();
      this.refreshSearch();
   }

   public void drawPanel(int mx, int my, float partialTick) {
      this.updateSearch();
      this.updateResults();
      super.drawPanel(mx, my, partialTick);
   }

   public void refreshSearch() {
      this.resetCanvas();
      this.searchIdx = 0;
      this.searching = this.getIterator();
      this.resultWidth = this.getTransform().getWidth();
      this.pendingResults.clear();
      this.savedResults.clear();
   }

   private void updateSearch() {
      if (this.searching != null) {
         if (!this.searching.hasNext()) {
            this.searching = null;
         } else {
            this.searchTime.reset().start();
            ArrayDeque<T> tmp = new ArrayDeque();

            while(this.searching.hasNext() && this.searchTime.elapsed(TimeUnit.MILLISECONDS) < 10L) {
               E entry = (E)this.searching.next();
               if (entry != null) {
                  this.queryMatches(entry, this.searchTerm, tmp);
               }
            }

            this.pendingResults.addAll(tmp);
            this.savedResults.addAll(tmp);
            this.searchTime.stop();
         }
      }
   }

   private void updateResults() {
      if (!this.pendingResults.isEmpty()) {
         this.searchTime.reset().start();

         while(!this.pendingResults.isEmpty() && this.searchTime.elapsed(TimeUnit.MILLISECONDS) < 100L) {
            if (this.addResult(this.pendingResults.poll(), this.searchIdx, this.resultWidth)) {
               ++this.searchIdx;
            }
         }

         this.searchTime.stop();
      }
   }

   public List<T> getResults() {
      return Collections.unmodifiableList(this.savedResults);
   }

   protected abstract Iterator<E> getIterator();

   protected abstract void queryMatches(E var1, String var2, ArrayDeque<T> var3);

   protected abstract boolean addResult(T var1, int var2, int var3);
}
