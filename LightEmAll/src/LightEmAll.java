import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.AboveImage;
import javalib.worldimages.BesideImage;
import javalib.worldimages.EmptyImage;
import javalib.worldimages.Posn;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;

class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;
  // the current location of the power station,
  // as well as its effective radius
  // y
  int powerRow;
  // x
  int powerCol;
  int radius;
  int steps;
  int time;
  Random rand;
  boolean win;
  boolean hBiased;
  boolean vBiased;
  static final int PIECE_SIZE = 100;

  // constructor for fractalCreate a board!
  LightEmAll(int width, int height) {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.nodes = new ArrayList<GamePiece>();
    this.mst = new ArrayList<Edge>();
    this.width = width;
    this.height = height;
    this.powerRow = 0;
    this.powerCol = width / 2;
    this.radius = 0;
    this.createBoard();
    this.fractalCreate(0, width - 1, 0, height - 1);
    this.link();
    this.findRadius();
    this.setPowerStation();
    this.powerUp();
    this.win();

  }

  // constructor for using kruskal's algorithm to create a board!
  LightEmAll(int width, int height, boolean horizontallyBiased, boolean verticallyBiased) {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.nodes = new ArrayList<GamePiece>();
    this.mst = new ArrayList<Edge>();
    this.width = width;
    this.height = height;
    this.powerRow = 0;
    this.powerCol = 0;
    this.radius = 0;
    this.steps = 0;
    this.time = 0;
    this.win = false;
    this.hBiased = horizontallyBiased;
    this.vBiased = verticallyBiased;
    this.rand = new Random();
    this.createBoard();
    this.kruskalsCreate(hBiased, vBiased);
    this.findRadius();
    this.shuffleBoard();
    this.link();
    this.setPowerStation();
    this.powerUp();
    // in case the game wins as the game is set up!
    this.win();
  }

  // convenient constructor for tests!
  LightEmAll(ArrayList<ArrayList<GamePiece>> board) {
    this.board = board;
    this.nodes = new ArrayList<GamePiece>();
    this.mst = new ArrayList<Edge>();
    this.width = board.size();
    this.height = board.get(0).size();
    this.powerRow = 0;
    this.powerCol = 0;
    this.radius = 1;
    this.win = false;
    this.rand = new Random();
    this.link();
    this.setPowerStation();
    this.powerUp();
    this.win();
  }

  // link all the GamePieces on the board if they are connected!
  void link() {
    for (int j = 0; j < width; j++) {
      for (int i = 0; i < height; i++) {
        int left = j - 1;
        int right = j + 1;
        int up = i - 1;
        int down = i + 1;

        GamePiece g = board.get(j).get(i);
        g.radius = this.radius;
        g.neighbours = new ArrayList<GamePiece>();
        g.connectedToRight = false;
        g.connectedToLeft = false;
        g.connectedToTop = false;
        g.connectedToBottom = false;

        if (right < width) {
          GamePiece gRight = board.get(right).get(i);
          if (g.right && gRight.left) {
            g.neighbours.add(board.get(right).get(i));
            g.connectedToRight = true;
          }
        }

        if (left >= 0) {
          GamePiece gLeft = board.get(left).get(i);
          if (g.left && gLeft.right) {
            g.neighbours.add(board.get(left).get(i));
            g.connectedToLeft = true;
          }
        }

        if (down < height) {
          GamePiece gDown = board.get(j).get(down);
          if (g.bottom && gDown.top) {
            g.neighbours.add(board.get(j).get(down));
            g.connectedToBottom = true;
          }
        }

        if (up >= 0) {
          GamePiece gUp = board.get(j).get(up);
          if (g.top && gUp.bottom) {
            g.neighbours.add(board.get(j).get(up));
            g.connectedToTop = true;
          }
        }

      }
    }

  }

  // create a new board!
  void createBoard() {
    for (int j = 0; j < width; j++) {
      ArrayList<GamePiece> ag = new ArrayList<GamePiece>();
      for (int i = 0; i < height; i++) {
        GamePiece g = new GamePiece();
        g.col = j;
        g.row = i;
        ag.add(g);
      }
      board.add(ag);
    }
  }

  // draw the board!
  public WorldImage drawBoard() {
    WorldImage boardImage = new EmptyImage();
    WorldImage columnImage = new EmptyImage();
    for (int j = 0; j < width; j++) {
      for (int i = 0; i < height; i++) {
        columnImage = new AboveImage(columnImage, board.get(j).get(i).draw());
      }
      boardImage = new BesideImage(boardImage, columnImage);
      columnImage = new EmptyImage();
    }
    return boardImage;
  }

  // find the power station and set the game piece field to true!
  void setPowerStation() {
    GamePiece g = board.get(powerCol).get(powerRow);
    g.powerStation = true;
  }

  // power up the road that is lighted by the power station!
  void powerUp() {
    for (int j = 0; j < width; j++) {
      for (int i = 0; i < height; i++) {
        GamePiece g = board.get(j).get(i);
        g.power = 0;
      }
    }

    GamePiece pS = board.get(powerCol).get(powerRow);
    pS.powerUp(radius);
    this.link();

  }

  // find the farthest GamePiece using BFS search!
  GamePiece farthest() {
    Queue<GamePiece> q = new LinkedList<>();
    GamePiece start = this.board.get(this.width / 2).get(this.height / 2);
    ArrayList<GamePiece> workList = new ArrayList<GamePiece>();
    q.add(start);
    GamePiece farthestPiece = new GamePiece();
    while (!q.isEmpty()) {
      GamePiece current = q.poll();
      for (int i = 0; i < current.neighbours.size(); i++) {
        if (!workList.contains(current.neighbours.get(i))) {
          q.add(current.neighbours.get(i));
          workList.add(current);
        }
      }
      farthestPiece = current;
    }
    return farthestPiece;
  }

  // find the radius using BFS search, based on the farthest we find so far.
  void findRadius() {
    this.link();
    Queue<GamePiece> q = new LinkedList<>();
    GamePiece start = this.farthest();
    ArrayList<GamePiece> workList = new ArrayList<GamePiece>();
    q.add(start);
    int p = 0;
    while (!q.isEmpty()) {
      GamePiece current = q.poll();
      for (int i = 0; i < current.neighbours.size(); i++) {
        if (!workList.contains(current.neighbours.get(i))) {
          current.neighbours.get(i).bfsCounter = current.bfsCounter + 1;
          q.add(current.neighbours.get(i));
          workList.add(current);
        }
      }
      p = current.bfsCounter;
    }
    radius = p / 2 + 1;
  }

  // generate edges to connects every GamePiece(including biased part!)!
  void generateEdges(boolean hBiased, boolean vBiased) {

    if (hBiased && !vBiased) {
      for (int j = 0; j < width; j++) {
        for (int i = 0; i < height; i++) {
          GamePiece g = board.get(j).get(i);
          if (j < width - 1) {
            GamePiece gRight = board.get(j + 1).get(i);
            this.mst.add(new Edge(g, gRight, rand.nextInt(this.width * this.height)));
          }
          if (i < height - 1) {
            GamePiece gBottom = board.get(j).get(i + 1);
            this.mst.add(new Edge(g, gBottom, rand.nextInt(this.width * this.height * 100)));
          }
        }
      }
    }

    if (!hBiased && vBiased) {
      for (int j = 0; j < width; j++) {
        for (int i = 0; i < height; i++) {
          GamePiece g = board.get(j).get(i);
          if (j < width - 1) {
            GamePiece gRight = board.get(j + 1).get(i);
            this.mst.add(new Edge(g, gRight, rand.nextInt(this.width * this.height * 100)));
          }
          if (i < height - 1) {
            GamePiece gBottom = board.get(j).get(i + 1);
            this.mst.add(new Edge(g, gBottom, rand.nextInt(this.width * this.height)));
          }
        }
      }
    }

    else {
      for (int j = 0; j < width; j++) {
        for (int i = 0; i < height; i++) {
          GamePiece g = board.get(j).get(i);
          if (j < width - 1) {
            GamePiece gRight = board.get(j + 1).get(i);
            this.mst.add(new Edge(g, gRight, rand.nextInt(this.width * this.height)));
          }
          if (i < height - 1) {
            GamePiece gBottom = board.get(j).get(i + 1);
            this.mst.add(new Edge(g, gBottom, rand.nextInt(this.width * this.height)));
          }
        }
      }
    }
  }

  // sort edges!
  void sortEdges() {
    ArrayList<Edge> result = new ArrayList<Edge>();
    while (!mst.isEmpty()) {
      int temp = mst.get(0).weight;
      Edge tempE = mst.get(0);
      for (int i = 0; i < mst.size(); i++) {
        Edge current = mst.get(i);
        if (current.weight <= temp) {
          temp = current.weight;
          tempE = current;
        }
      }
      mst.remove(tempE);
      result.add(tempE);
    }
    mst = result;
  }

  // helper for kruskal's algorithm
  GamePiece find(HashMap<GamePiece, GamePiece> representatives, GamePiece g) {
    if (representatives.get(g).equals(g)) {
      return g;
    }

    else {
      return this.find(representatives, representatives.get(g));
    }
  }

  // helper for kruskal's algorithm
  void union(HashMap<GamePiece, GamePiece> representatives, GamePiece to, GamePiece from) {
    representatives.put(to, representatives.get(from));
  }

  // using Kruskals' method to create minimum spanning tree
  void kruskals(boolean hBiased, boolean vBiased) {

    this.generateEdges(hBiased, vBiased);
    this.sortEdges();

    HashMap<GamePiece, GamePiece> representatives = new HashMap<GamePiece, GamePiece>();
    ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
    ArrayList<Edge> worklist = mst;

    for (int j = 0; j < width; j++) {
      for (int i = 0; i < height; i++) {
        GamePiece g = board.get(j).get(i);
        representatives.put(g, g);
      }
    }

    while (edgesInTree.size() < width * height - 1) {
      Edge temp = worklist.remove(0);
      GamePiece from = temp.fromNode;
      GamePiece to = temp.toNode;

      if (!find(representatives, to).equals(find(representatives, from))) {
        edgesInTree.add(temp);
        union(representatives, find(representatives, to), find(representatives, from));
      }
    }
    mst = edgesInTree;
  }

  // generates the board using kruskal's algorithm
  void kruskalsCreate(boolean hBiased, boolean vBiased) {
    this.kruskals(hBiased, vBiased);
    for (int i = 0; i < mst.size(); i++) {
      GamePiece from = mst.get(i).fromNode;
      GamePiece to = mst.get(i).toNode;
      if (from.col == to.col && from.row == to.row + 1) {
        from.top = true;
        from.connectedToTop = true;
        to.bottom = true;
        to.connectedToBottom = true;
      }
      if (from.col == to.col && from.row + 1 == to.row) {
        from.bottom = true;
        from.connectedToBottom = true;
        to.top = true;
        to.connectedToTop = true;
      }

      if (from.col + 1 == to.col && from.row == to.row) {
        from.right = true;
        from.connectedToRight = true;
        to.left = true;
        to.connectedToLeft = true;
      }

      if (from.col == to.col + 1 && from.row == to.row) {
        from.left = true;
        from.connectedToLeft = true;
        to.right = true;
        to.connectedToRight = true;
      }
    }
  }

  // shuffle the board into random game state!
  void shuffleBoard() {
    for (int j = 0; j < width; j++) {
      for (int i = 0; i < height; i++) {
        GamePiece g = board.get(j).get(i);
        if (rand.nextInt(4) == 1) {
          g.leftClick();
        }
        if (rand.nextInt(4) == 2) {
          g.leftClick();
          g.leftClick();
        }
        if (rand.nextInt(4) == 3) {
          g.rightClick();
        }
      }
    }
  }

  // check if player win the game!
  void win() {
    boolean result = true;
    for (int j = 0; j < width; j++) {
      for (int i = 0; i < height; i++) {
        if (board.get(j).get(i).power == 0) {
          result = false;
        }
      }
    }
    this.win = result;
  }

  // manually generates a board!
  void fractalCreate(int widthLo, int widthHi, int heightLo, int heightHi) {

    // when it is width 1, height 2
    if (widthHi == widthLo && heightHi - heightLo == 1) {
      GamePiece gT = board.get(widthHi).get(heightLo);
      GamePiece gB = board.get(widthHi).get(heightHi);
      gT.bottom = true;
      gB.top = true;
    }

    // when it is width 2, height 1
    if (widthHi - widthLo == 1 && heightHi == heightLo) {
      GamePiece gL = board.get(widthLo).get(heightHi);
      GamePiece gR = board.get(widthHi).get(heightHi);
      gL.right = true;
      gR.left = true;
    }

    // when it is width 2, height 2
    if (widthHi - widthLo == 1 && heightHi - heightLo == 1) {
      GamePiece g1 = board.get(widthLo).get(heightLo);
      GamePiece g2 = board.get(widthHi).get(heightLo);
      GamePiece g3 = board.get(widthLo).get(heightHi);
      GamePiece g4 = board.get(widthHi).get(heightHi);
      g1.bottom = true;
      g2.bottom = true;
      g3.top = true;
      g3.right = true;
      g4.left = true;
      g4.top = true;
    }

    // when height equals 2, width is bigger than 2
    if (widthHi - widthLo > 1 && heightHi - heightLo == 1) {
      int widthMi = (widthHi + widthLo) / 2;

      for (int j = widthLo; j < widthHi + 1; j++) {
        for (int i = heightLo; i < heightHi + 1; i++) {
          GamePiece g = board.get(j).get(i);

          if ((j == widthLo || j == widthHi) && i == heightLo) {
            g.bottom = true;
          }

          if (j == widthLo && i == heightHi) {
            g.top = true;
            g.right = true;
          }

          if (j == widthHi && i == heightHi) {
            g.top = true;
            g.left = true;
          }

          if (i == heightHi && j != widthLo && j != widthHi) {
            g.left = true;
            g.right = true;
          }
        }
      }
      this.fractalCreate(widthLo, widthMi, heightLo, heightHi);
      this.fractalCreate(widthMi + 1, widthHi, heightLo, heightHi);
    }

    // when width equals 2, height is bigger than 2
    if (widthHi - widthLo == 1 && heightHi - heightLo > 1) {
      for (int j = widthLo; j < widthHi + 1; j++) {
        for (int i = heightLo; i < heightHi + 1; i++) {
          GamePiece g = board.get(j).get(i);

          if ((j == widthLo || j == widthHi) && i == heightLo) {
            g.bottom = true;
          }

          if (j == widthLo && i == heightHi) {
            g.top = true;
            g.right = true;
          }

          if (j == widthHi && i == heightHi) {
            g.top = true;
            g.left = true;
          }

          if ((j == widthLo || j == widthHi) && i != heightLo && i != heightHi) {
            g.top = true;
            g.bottom = true;
          }
        }
      }
    }

    // when width and height are both bigger than 2
    if (widthHi - widthLo > 1 && heightHi - heightLo > 1) {
      int widthMi = (widthHi + widthLo) / 2;
      int heightMi = (heightHi + heightLo) / 2;

      for (int j = widthLo; j < widthHi + 1; j++) {
        for (int i = heightLo; i < heightHi + 1; i++) {
          GamePiece g = board.get(j).get(i);

          if ((j == widthLo || j == widthHi) && i == heightLo) {
            g.bottom = true;
          }

          if (j == widthLo && i == heightHi) {
            g.top = true;
            g.right = true;
          }

          if (j == widthHi && i == heightHi) {
            g.top = true;
            g.left = true;
          }

          if ((j == widthLo || j == widthHi) && i != heightLo && i != heightHi) {
            g.top = true;
            g.bottom = true;
          }

          if (i == heightHi && j != widthLo && j != widthHi) {
            g.left = true;
            g.right = true;
          }
        }
      }

      this.fractalCreate(widthLo, widthMi, heightLo, heightMi);
      this.fractalCreate(widthLo, widthMi, heightMi + 1, heightHi);
      this.fractalCreate(widthMi + 1, widthHi, heightLo, heightMi);
      this.fractalCreate(widthMi + 1, widthHi, heightMi + 1, heightHi);
    }

  }

  // make scene for the game
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(PIECE_SIZE * this.width, PIECE_SIZE * this.height + 50);
    final WorldImage endText = new AboveImage(
        new TextImage("You own the galaxy!", 5 * this.width, Color.red),
        new TextImage("Press space or click restart to try again!", 5 * this.width, Color.yellow));
    final WorldImage columnText = new TextImage(
        "Steps:" + Integer.toString(steps) + "  Restart" + "  Time:" + Integer.toString(time), 50,
        Color.MAGENTA);
    scene.placeImageXY(this.drawBoard(), (PIECE_SIZE * this.width) / 2,
        ((PIECE_SIZE * this.height) / 2) + 50);
    scene.placeImageXY(columnText, (PIECE_SIZE * this.width) / 2, 25);
    if (this.win) {
      scene.placeImageXY(endText, (PIECE_SIZE * this.width) / 2, (PIECE_SIZE * this.height) / 2);
    }
    return scene;
  }

  // implement rotation behavior for the click
  public void onMouseReleased(Posn position, String button) {
    if (position.y >= 50) {
      int x = (position.x / PIECE_SIZE);
      int y = ((position.y - 50) / PIECE_SIZE);
      GamePiece g = board.get(x).get(y);

      if (!this.win) {
        if (button.equals("LeftButton")) {
          g.leftClick();
        }

        if (button.equals("RightButton")) {
          g.rightClick();
        }

        this.link();
        this.powerUp();
        this.win();
        steps = steps + 1;
      }

    }

    if (position.y < 50 && position.x < (PIECE_SIZE * this.width) / 2 + 75
        && position.x > (PIECE_SIZE * this.width) / 2 - 75) {
      this.board = new ArrayList<ArrayList<GamePiece>>();
      this.nodes = new ArrayList<GamePiece>();
      this.mst = new ArrayList<Edge>();
      this.powerRow = 0;
      this.powerCol = 0;
      this.radius = 0;
      this.steps = 0;
      this.time = 0;
      this.win = false;
      this.createBoard();
      this.kruskalsCreate(hBiased, vBiased);
      this.findRadius();
      this.shuffleBoard();
      this.link();
      this.setPowerStation();
      this.powerUp();
    }
  }

  // move the PowerStation if up, down, left, or right arrow is pressed!
  public void onKeyEvent(String key) {
    GamePiece g = board.get(powerCol).get(powerRow);

    if (!this.win) {

      if (key.equals("left") && g.connectedToLeft) {
        g.powerStation = false;
        powerCol = powerCol - 1;
        steps = steps + 1;

      }

      if (key.equals("right") && g.connectedToRight) {
        g.powerStation = false;
        powerCol = powerCol + 1;
        steps = steps + 1;

      }

      if (key.equals("up") && g.connectedToTop) {
        g.powerStation = false;
        powerRow = powerRow - 1;
        steps = steps + 1;

      }

      if (key.equals("down") && g.connectedToBottom) {
        g.powerStation = false;
        powerRow = powerRow + 1;
        steps = steps + 1;
      }

      this.setPowerStation();
      this.powerUp();
      this.win();
    }

    else {
      if (key.equals(" ")) {
        this.board = new ArrayList<ArrayList<GamePiece>>();
        this.nodes = new ArrayList<GamePiece>();
        this.mst = new ArrayList<Edge>();
        this.powerRow = 0;
        this.powerCol = 0;
        this.radius = 0;
        this.steps = 0;
        this.time = 0;
        this.win = false;
        this.createBoard();
        this.kruskalsCreate(hBiased, vBiased);
        this.findRadius();
        this.shuffleBoard();
        this.link();
        this.setPowerStation();
        this.powerUp();

      }
    }
  }

  // count the time according to each tick!
  public void onTick() {
    if (!this.win) {
      time = time + 1;
    }
  }

}