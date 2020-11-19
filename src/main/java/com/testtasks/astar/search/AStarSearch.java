package com.testtasks.astar.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AStarSearch {

    private int objectSize;
    private Cell start;
    private Cell end;
    private Cell[][] cells;
    private List<Cell> pathCells;

    public void findPath() {
        PriorityQueue<Cell> queue = new PriorityQueue<>(Comparator.comparingDouble(Cell::getPriority));
        pathCells = new ArrayList<>();
        start.setPriority(0);
        queue.add(start);
        Cell current = new Cell();

        while (!queue.isEmpty()) {
            current = queue.poll();

            if (current.getType().equals("end")) {
                break;
            }

            for (Cell next : getVisitableNeighbours(current)) {
                double newCost = current.getCostSoFar() + next.getWeight();
                if (next.getCostSoFar() == 0. || newCost < next.getCostSoFar()) {
                    next.setCostSoFar(newCost);
                    next.setPriority(newCost + heuristic(next, end));
                    next.setPrevious(current);
                    queue.add(next);
                }
            }
        }

        traceBackPath(current);
    }

    private void traceBackPath(Cell current) {
        while (current.getPrevious() != null) {
            current = current.getPrevious();

            List<Cell> objectCells = new ArrayList<>();

            switch (objectSize) {
                case 3:
                    addCellIfExists(objectCells, current.getX(), current.getY() - 1);
                    addCellIfExists(objectCells, current.getX() - 1, current.getY());
                    addCellIfExists(objectCells, current.getX() - 1, current.getY() - 1);
                    addCellIfExists(objectCells, current.getX() - 1, current.getY() + 1);
                    addCellIfExists(objectCells, current.getX() + 1, current.getY() - 1);
                case 2:
                    addCellIfExists(objectCells, current.getX(), current.getY() + 1);
                    addCellIfExists(objectCells, current.getX() + 1, current.getY());
                    addCellIfExists(objectCells, current.getX() + 1, current.getY() + 1);
                case 1:
                    objectCells.add(current);
                    break;
            }

            for (Cell cell : objectCells) {
                if (cell.getType().equals("empty")) {
                    cell.setType("path");
                    pathCells.add(cell);
                }
            }
        }
    }

    private void addCellIfExists(List<Cell> objectCells, int x, int y) {
        if ((x >= 0 && x < 100) && (y >= 0 && y < 100)) {
            objectCells.add(cells[x][y]);
        }
    }

    private double heuristic(Cell c1, Cell c2) {
        return Math.abs(c1.getX() - c2.getX()) + Math.abs(c1.getY() - c2.getY());
    }

    private List<Cell> getVisitableNeighbours(Cell cell) {
        List<Cell> neighbours = getNeighbours(cell);

        switch (objectSize) {
            case 1:
                return neighbours.stream()
                    .filter(c -> !c.getType().equals("obstacle"))
                    .collect(Collectors.toList());
            case 2:
                for (int i = 0; i < neighbours.size(); i++) {
                    Cell c = neighbours.get(i);
                    int[] increments = new int[] {0, 0, 0, 0};

                    switch (c.getDirection()) {
                        case "right":
                            increments = new int[] {1, 1, 0, 1};
                            break;
                        case "down":
                            increments = new int[] {0, 1, 1, 1};
                            break;
                    }

                    if (cells[c.getX() + increments[0]][c.getY() + increments[2]].getType().equals("obstacle") ||
                        cells[c.getX() + increments[1]][c.getY() + increments[3]].getType().equals("obstacle")) {
                        neighbours.remove(c);
                        i--;
                    }
                }
                break;
            case 3:
                for (int i = 0; i < neighbours.size(); i++) {
                    Cell c = neighbours.get(i);
                    int[] increments = new int[] {0, 0, 0, 0, 0, 0};

                    switch (c.getDirection()) {
                        case "up":
                            increments = new int[] {-1, 0, 1, -1, -1, -1};
                            break;
                        case "left":
                            increments = new int[] {-1, -1, -1, -1, 0, 1};
                            break;
                        case "right":
                            increments = new int[] {1, 1, 1, -1, 0, 1};
                            break;
                        case "down":
                            increments = new int[] {-1, 0, 1, 1, 1, 1};
                            break;
                    }

                    if (cells[c.getX() + increments[0]][c.getY() + increments[3]].getType().equals("obstacle") ||
                        cells[c.getX() + increments[1]][c.getY() + increments[4]].getType().equals("obstacle") ||
                        cells[c.getX() + increments[2]][c.getY() + increments[5]].getType().equals("obstacle")) {
                        neighbours.remove(c);
                        i--;
                    }
                }
                break;
        }

        return neighbours;
    }

    private List<Cell> getNeighbours(Cell cell) {
        List<Cell> neighbours = new ArrayList<>();

        int[] borders = new int[] {100, 0, 0, 100};

        switch (objectSize) {
            case 1:
                borders = new int[] {0, 99, 99, 0};
                break;
            case 2:
                borders = new int[] {0, 98, 98, 0};
                break;
            case 3:
                borders = new int[] {1, 98, 98, 1};
                break;
        }

        if (cell.getX() > borders[0]) {
            cells[cell.getX() - 1][cell.getY()].setDirection("left");
            neighbours.add(cells[cell.getX() - 1][cell.getY()]);
        }
        if (cell.getY() < borders[1]) {
            cells[cell.getX()][cell.getY() + 1].setDirection("down");
            neighbours.add(cells[cell.getX()][cell.getY() + 1]);
        }
        if (cell.getX() < borders[2]) {
            cells[cell.getX() + 1][cell.getY()].setDirection("right");
            neighbours.add(cells[cell.getX() + 1][cell.getY()]);
        }
        if (cell.getY() > borders[3]) {
            cells[cell.getX()][cell.getY() - 1].setDirection("up");
            neighbours.add(cells[cell.getX()][cell.getY() - 1]);
        }

        return neighbours;
    }
}
