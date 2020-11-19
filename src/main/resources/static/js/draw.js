let canvas = document.getElementById("canvas1");
let ctx = canvas.getContext("2d");
ctx.lineWidth = 1;
ctx.fillStyle = "rgb(255, 255, 255)";
ctx.strokeStyle = "rgb(0, 0, 0)";

let mapW = parseInt(canvas.getAttribute("width"));
let mapH = parseInt(canvas.getAttribute("height"));

let cellW = mapW / 100;
let cellH = mapH / 100;

let cells = [];
let cellTypes = [];
let pathCells;

let drawType = "";
let objectSize = 1;
let startPoint = [-2, -2];
let endPoint = [-2, -2];

let isDrawing = false;

function drawMap() {
  for (let x = 0; x < 100; x++) {
    let cellRow = [];
    let typeRow = [];

    for (let y = 0; y < 100; y++) {
      let cell = new Path2D();
      cell.rect(x * cellW, y * cellH, cellW, cellH);
      cellRow.push(cell);
      typeRow.push("empty")
      ctx.stroke(cell);
    }

    cells.push(cellRow);
    cellTypes.push(typeRow);
  }
}

function drawObstacles() {
  drawType = "obstacle";
  ctx.fillStyle = "rgb(250, 130, 0)";
}

function eraseObstacles() {
  drawType = "erase";
  ctx.fillStyle = "rgb(255, 255, 255)";
}

function setStartPoint() {
  drawType = "start";
  ctx.fillStyle = "rgb(100, 255, 0)";
}

function setEndPoint() {
  drawType = "end";
  ctx.fillStyle = "rgb(255, 0, 0)";
}

function getMap() {
  if (startPoint[0] >= 0 && endPoint[0] >= 0) {
    let map = [];
    for (let i = 0; i < cellTypes.length; i++) {
      let row = [];
      for (let j = 0; j < cellTypes.length; j++) {
        row.push({"x" : i, "y" : j, "weight": 1, "type" : cellTypes[i][j]})
      }
      map.push(row);
    }
    return map;
  }
}

function submit() {
  let map = getMap();
  if (map !== null) {
    $.ajax({
      type: "POST",
      url: "/",
      data: JSON.stringify({
        "objectSize": objectSize,
        "start": {"x": startPoint[0], "y": startPoint[1], "weight": 1, "type": "start"},
        "end": {"x": endPoint[0], "y": endPoint[1], "weight": 1, "type": "end"},
        "cells": map}),
      contentType: "application/json; charset=utf-8",
      dataType: "json"
    })
    .done(function (path) {
      drawPath(path);
    });
  } else {
    alert("Set both starting and ending point!");
  }
}

function drawPath(path) {
  pathCells = path;
  ctx.fillStyle = "rgb(0, 180, 180)";
  for (let i = 0; i < path.length; i++) {
    drawPoint(path[i].x, path[i].y, 0, 0, "path")
  }
  drawType = ""
  ctx.fillStyle = "rgb(255, 255, 255)";
}

function setSize(size) {
  if (size !== objectSize) {
    if (startPoint[0] >= 0) {
      clearObject(startPoint);
      startPoint = [-2, -2];
    }
    if (endPoint[0] >= 0) {
      clearObject(endPoint);
      endPoint = [-2, -2];
    }

    objectSize = size;
  }
}

function drawPoint(x, y, i, j, type) {
  x += i;
  y += j;

  cellTypes[x][y] = type;
  ctx.fill(cells[x][y]);
  ctx.stroke(cells[x][y]);
}

function clearObject(object) {
  let initFillStyle = ctx.fillStyle;
  ctx.fillStyle = "rgb(255, 255, 255)";

  drawObject(object, "empty");

  ctx.fillStyle = initFillStyle;
}

function drawObject(click, type) {
  switch (objectSize) {
    case 1:
      drawPoint(click[0], click[1], 0, 0, type);
      break;
    case 2:
      for (let i = 0; i < 2; i++) {
        for (let j = 0; j < 2; j++) {
          drawPoint(click[0], click[1], i, j, type);
        }
      }
      break;
    case 3:
      for (let i = -1; i < 2; i++) {
        for (let j = -1; j < 2; j++) {
          drawPoint(click[0], click[1], i, j, type);
        }
      }
      break;
  }
}

function checkPointPlace(click) {
  switch (objectSize) {
    case 1:
      return true;
    case 2:
      return !(click[0] > 98 || click[1] > 98);
    case 3:
      return !((click[0] > 98 || click[1] > 98) || (click[0] < 1 || click[1]
          < 1))
  }
}

function getMousePosition(event) {
  let rect = canvas.getBoundingClientRect();
  let x = Math.floor((event.clientX - rect.left) / cellW);
  let y = Math.floor((event.clientY - rect.top) / cellH);
  return [x, y];
}

canvas.addEventListener("mousedown", function (e) {
  let click = getMousePosition(e);

  switch (drawType) {
    case "obstacle":
      if (cellTypes[click[0]][click[1]] === "empty") {
        drawPoint(click[0], click[1], 0, 0, drawType);
      }
      isDrawing = true;
      break;
    case "erase":
      if (cellTypes[click[0]][click[1]] === "obstacle") {
        drawPoint(click[0], click[1], 0, 0, "empty");
      }
      isDrawing = true;
      break;
    case "start":
      if (!checkPointPlace(click)) {
        return;
      }

      if (startPoint[0] >= 0) {
        clearObject(startPoint);
      }

      startPoint = click;
      drawObject(startPoint, drawType);
      break;
    case "end":
      if (!checkPointPlace(click)) {
        return;
      }

      if (endPoint[0] >= 0) {
        clearObject(endPoint);
      }

      endPoint = click;
      drawObject(endPoint, drawType);
      break;
  }
});

canvas.addEventListener("mousemove", function (e) {
  let click = getMousePosition(e);

  if (isDrawing) {
    switch (drawType) {
      case "obstacle":
        if (cellTypes[click[0]][click[1]] === "empty") {
          drawPoint(click[0], click[1], 0, 0, drawType);
        }
        break;
      case "erase":
        if (cellTypes[click[0]][click[1]] === "obstacle") {
          drawPoint(click[0], click[1], 0, 0, "empty");
        }
        break;
    }
  }
});

canvas.addEventListener("mouseup", function () {
  if (isDrawing === true) {
    isDrawing = false;
  }
});

drawMap();