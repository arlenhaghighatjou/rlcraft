package ivorius.ivtoolkit.maze.components;

import com.google.common.collect.Multimap;
import ivorius.ivtoolkit.random.WeightedSelector;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.function.ToDoubleFunction;

public class MultiMazeComponent<M extends WeightedMazeComponent<C>, C> implements WeightedMazeComponent<C> {
   private final List<M> components;
   private final double weight;

   public MultiMazeComponent(List<M> components) {
      this.components = components;
      this.weight = components.stream().mapToDouble(WeightedSelector.Item::getWeight).sum();
   }

   public M first() {
      return (M)(this.components.get(0));
   }

   public Set<MazeRoom> rooms() {
      return this.first().rooms();
   }

   public Map<MazePassage, C> exits() {
      return this.first().exits();
   }

   public Multimap<MazePassage, MazePassage> reachability() {
      return this.first().reachability();
   }

   public double getWeight() {
      return this.weight;
   }

   public PlacedMazeComponent<M, C> place(MazeRoom shift, EaglercraftRandom random) {
      return new PlacedMazeComponent<M, C>((WeightedMazeComponent)WeightedSelector.selectItem(random, this.components), shift);
   }
}
