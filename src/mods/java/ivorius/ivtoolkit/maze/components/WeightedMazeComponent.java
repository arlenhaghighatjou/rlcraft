package ivorius.ivtoolkit.maze.components;

import ivorius.ivtoolkit.random.WeightedSelector;

public interface WeightedMazeComponent<C> extends MazeComponent<C>, WeightedSelector.Item {
}
