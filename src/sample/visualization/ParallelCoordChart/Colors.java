package sample.visualization.ParallelCoordChart;


import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Colors {
    static final int DEFAULT_COLOR_SIZE = 10;
    int colorPosition = 0;
    static List<Color> colors;
    Random random = new Random(System.currentTimeMillis());

    static {
        colors = new ArrayList<>(DEFAULT_COLOR_SIZE);
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);
        colors.add(Color.MAGENTA);
        colors.add(Color.GRAY);
        colors.add(Color.BLACK);
        colors.add(Color.ORANGE);
        colors.add(Color.CYAN);
        colors.add(Color.DARKGRAY);
    }

    public Color nextColor (){
        if (colorPosition < DEFAULT_COLOR_SIZE)
            return colors.get(colorPosition++);
        else
            return  Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    public void reset(){
        colorPosition = 0;
    }
}
