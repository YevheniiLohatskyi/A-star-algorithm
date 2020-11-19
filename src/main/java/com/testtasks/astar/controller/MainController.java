package com.testtasks.astar.controller;

import com.testtasks.astar.search.Cell;
import com.testtasks.astar.service.SearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MainController {

    private final SearchService searchService;

    @GetMapping(path = "/")
    public String getDrawPage() {
        return "draw";
    }

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Cell>> findPath(@RequestBody String jsonMap) {
        return new ResponseEntity<>(searchService.findPath(jsonMap), HttpStatus.OK);
    }
}
