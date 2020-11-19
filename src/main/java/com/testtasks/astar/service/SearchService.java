package com.testtasks.astar.service;

import com.google.gson.Gson;
import com.testtasks.astar.search.AStarSearch;
import com.testtasks.astar.search.Cell;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    public List<Cell> findPath(String jsonMap) {
        AStarSearch search = new Gson().fromJson(jsonMap, AStarSearch.class);
        search.findPath();
        return search.getPathCells();
    }
}
