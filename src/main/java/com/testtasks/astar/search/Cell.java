package com.testtasks.astar.search;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Cell {

    private int x;
    private int y;
    private int weight;
    private double priority;
    private double costSoFar;
    private String type;
    private String direction;
    private Cell previous;
}
