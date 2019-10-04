import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.StarImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

class ExamplesGame {

  GamePiece g1;
  GamePiece g2;
  GamePiece g3;
  GamePiece g4;
  GamePiece g5;
  GamePiece g6;
  GamePiece g7;
  GamePiece g8;
  GamePiece g9;
  GamePiece g10;

  ArrayList<GamePiece> ag1;
  ArrayList<GamePiece> ag2;
  ArrayList<GamePiece> ag3;
  ArrayList<GamePiece> ag4;
  ArrayList<GamePiece> ag5;

  ArrayList<ArrayList<GamePiece>> b1;
  ArrayList<ArrayList<GamePiece>> b2;
  ArrayList<ArrayList<GamePiece>> b3;

  LightEmAll l1;
  LightEmAll l2;
  LightEmAll l3;
  LightEmAll l4;
  LightEmAll l5;
  LightEmAll l6;

  Edge e1;
  Edge e2;
  Edge e3;
  Edge e4;

  final WorldImage piece = new OverlayImage(
      new RectangleImage(GamePiece.PIECE_SIZE, GamePiece.PIECE_SIZE, OutlineMode.OUTLINE,
          Color.BLACK),
      new RectangleImage(GamePiece.PIECE_SIZE, GamePiece.PIECE_SIZE, OutlineMode.SOLID,
          Color.DARK_GRAY));
  final WorldImage horizontalRec = new RectangleImage(GamePiece.PIECE_SIZE / 2,
      GamePiece.PIECE_SIZE / 20, OutlineMode.SOLID, Color.LIGHT_GRAY);
  final WorldImage verticalRec = new RectangleImage(GamePiece.PIECE_SIZE / 20,
      GamePiece.PIECE_SIZE / 2, OutlineMode.SOLID, Color.LIGHT_GRAY);
  final WorldImage star = new OverlayImage(
      new StarImage(GamePiece.PIECE_SIZE / 3, 7, OutlineMode.OUTLINE, Color.ORANGE),
      new StarImage(GamePiece.PIECE_SIZE / 3, 7, OutlineMode.SOLID, Color.CYAN));

  void initData() {
    g1 = new GamePiece(false, false, false, false);
    g2 = new GamePiece(false, true, false, false);
    g3 = new GamePiece(true, false, false, false);
    g4 = new GamePiece(false, false, true, false);
    g5 = new GamePiece(false, false, false, true);
    g6 = new GamePiece(false, false, true, false);
    g7 = new GamePiece();
    g8 = new GamePiece();
    g9 = new GamePiece();
    g10 = new GamePiece();

    ag1 = new ArrayList<GamePiece>(Arrays.asList(g1, g2));
    ag2 = new ArrayList<GamePiece>(Arrays.asList(g3, g4));
    ag3 = new ArrayList<GamePiece>(Arrays.asList(g5, g6));
    ag4 = new ArrayList<GamePiece>(Arrays.asList(g7, g8));
    ag5 = new ArrayList<GamePiece>(Arrays.asList(g9, g10));

    b1 = new ArrayList<ArrayList<GamePiece>>(Arrays.asList(ag1, ag2));
    b2 = new ArrayList<ArrayList<GamePiece>>(Arrays.asList(ag3));
    b3 = new ArrayList<ArrayList<GamePiece>>(Arrays.asList(ag4, ag5));

    l1 = new LightEmAll(b1);
    l2 = new LightEmAll(b2);
    l3 = new LightEmAll(1, 2);
    l4 = new LightEmAll(2, 2, false, false);
    l5 = new LightEmAll(b3);
    l6 = new LightEmAll(4, 4);

    e1 = new Edge(g7, g8, 1);
    e2 = new Edge(g7, g8, 2);
    e3 = new Edge(g7, g8, 3);
    e4 = new Edge(g7, g8, 4);

    l1.mst = new ArrayList<Edge>(Arrays.asList(e3, e4, e2, e1));

  }

  // the big bang generated from this!
  void testBigBang(Tester t) {
    LightEmAll world = new LightEmAll(18, 9, true, false);
    int worldWidth = GamePiece.PIECE_SIZE * world.width;
    int worldHeight = GamePiece.PIECE_SIZE * world.height + 50;
    double tickRate = 1.0;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }

  void testDraw(Tester t) {
    this.initData();
    t.checkExpect(g1.draw(), new OverlayImage(star, piece));
    t.checkExpect(g2.draw(),
        new OverlayImage(horizontalRec.movePinhole(-GamePiece.PIECE_SIZE / 4, 0), piece));
    t.checkExpect(g3.draw(),
        new OverlayImage(horizontalRec.movePinhole(GamePiece.PIECE_SIZE / 4, 0), piece));
    t.checkExpect(g4.draw(),
        new OverlayImage(verticalRec.movePinhole(0, GamePiece.PIECE_SIZE / 4), piece));
    l1.onMouseReleased(new Posn(120, 170), "LeftButton");
    t.checkExpect(g1.draw(), new OverlayImage(star, piece));
    t.checkExpect(g2.draw(),
        new OverlayImage(horizontalRec.movePinhole(-GamePiece.PIECE_SIZE / 4, 0), piece));
    t.checkExpect(g3.draw(),
        new OverlayImage(horizontalRec.movePinhole(GamePiece.PIECE_SIZE / 4, 0), piece));
    t.checkExpect(g4.draw(),
        new OverlayImage(horizontalRec.movePinhole(-GamePiece.PIECE_SIZE / 4, 0), piece));
  }

  void testLink(Tester t) {
    this.initData();
    t.checkExpect(g1.neighbours, new ArrayList<GamePiece>());
    t.checkExpect(g2.neighbours, new ArrayList<GamePiece>());
    t.checkExpect(g3.neighbours, new ArrayList<GamePiece>());
    t.checkExpect(g4.neighbours, new ArrayList<GamePiece>());
    t.checkExpect(g5.neighbours, new ArrayList<GamePiece>(Arrays.asList(g6)));
    t.checkExpect(g6.neighbours, new ArrayList<GamePiece>(Arrays.asList(g5)));
    t.checkExpect(g5.connectedToBottom, true);
    t.checkExpect(g6.connectedToTop, true);

    l2.onMouseReleased(new Posn(20, 70), "RightButton");

    t.checkExpect(g5.neighbours, new ArrayList<GamePiece>());
    t.checkExpect(g6.neighbours, new ArrayList<GamePiece>());
    t.checkExpect(g5.connectedToBottom, false);
    t.checkExpect(g6.connectedToTop, false);

    l2.onMouseReleased(new Posn(20, 70), "LeftButton");

    t.checkExpect(g5.neighbours, new ArrayList<GamePiece>(Arrays.asList(g6)));
    t.checkExpect(g6.neighbours, new ArrayList<GamePiece>(Arrays.asList(g5)));
    t.checkExpect(g5.connectedToBottom, true);
    t.checkExpect(g6.connectedToTop, true);
  }

  void testOnMouseReleased(Tester t) {
    this.initData();
    t.checkExpect(g1.right, false);
    t.checkExpect(g1.left, false);
    t.checkExpect(g1.top, false);
    t.checkExpect(g1.bottom, false);
    t.checkExpect(g2.right, true);
    t.checkExpect(g2.left, false);
    t.checkExpect(g2.top, false);
    t.checkExpect(g2.bottom, false);
    t.checkExpect(g3.right, false);
    t.checkExpect(g3.left, true);
    t.checkExpect(g3.top, false);
    t.checkExpect(g3.bottom, false);
    t.checkExpect(g4.right, false);
    t.checkExpect(g4.left, false);
    t.checkExpect(g4.top, true);
    t.checkExpect(g4.bottom, false);

    l1.onMouseReleased(new Posn(120, 170), "LeftButton");

    t.checkExpect(g1.right, false);
    t.checkExpect(g1.left, false);
    t.checkExpect(g1.top, false);
    t.checkExpect(g1.bottom, false);
    t.checkExpect(g2.right, true);
    t.checkExpect(g2.left, false);
    t.checkExpect(g2.top, false);
    t.checkExpect(g2.bottom, false);
    t.checkExpect(g3.right, false);
    t.checkExpect(g3.left, true);
    t.checkExpect(g3.top, false);
    t.checkExpect(g3.bottom, false);
    t.checkExpect(g4.right, true);
    t.checkExpect(g4.left, false);
    t.checkExpect(g4.top, false);
    t.checkExpect(g4.bottom, false);

    l1.onMouseReleased(new Posn(120, 170), "RightButton");

    t.checkExpect(g1.right, false);
    t.checkExpect(g1.left, false);
    t.checkExpect(g1.top, false);
    t.checkExpect(g1.bottom, false);
    t.checkExpect(g2.right, true);
    t.checkExpect(g2.left, false);
    t.checkExpect(g2.top, false);
    t.checkExpect(g2.bottom, false);
    t.checkExpect(g3.right, false);
    t.checkExpect(g3.left, true);
    t.checkExpect(g3.top, false);
    t.checkExpect(g3.bottom, false);
    t.checkExpect(g4.right, false);
    t.checkExpect(g4.left, false);
    t.checkExpect(g4.top, true);
    t.checkExpect(g4.bottom, false);
  }

  void testOnKeyEvent(Tester t) {
    this.initData();
    t.checkExpect(l2.powerCol, 0);
    t.checkExpect(l2.powerRow, 0);

    l2.onKeyEvent("down");

    t.checkExpect(l2.powerCol, 0);
    t.checkExpect(l2.powerRow, 1);

    l2.onKeyEvent("up");

    t.checkExpect(l2.powerCol, 0);
    t.checkExpect(l2.powerRow, 0);
  }

  void testWin(Tester t) {
    this.initData();
    l4.rand = new Random(1);
    l5.rand = new Random(1);
    l5.kruskalsCreate(false, false);
    t.checkExpect(l3.win, true);
    t.checkExpect(l4.win, false);
    t.checkExpect(l5.win, false);

  }

  void testPowerUp(Tester t) {
    this.initData();
    t.checkExpect(g5.power, 1);
    t.checkExpect(g6.power, 0);

    l2.onKeyEvent("down");

    t.checkExpect(g5.power, 0);
    t.checkExpect(g6.power, 1);

    l2.onKeyEvent("up");

    t.checkExpect(g5.power, 1);
    t.checkExpect(g6.power, 0);
  }

  void testFactracalCreate(Tester t) {
    this.initData();
    t.checkExpect(l3.board.size(), 1);
    t.checkExpect(l3.board.get(0).get(0).powerStation, true);
    t.checkExpect(l3.board.get(0).get(0).power, 2);
    t.checkExpect(l3.board.get(0).get(0).bottom, true);
    t.checkExpect(l3.board.get(0).get(1).powerStation, false);
    t.checkExpect(l3.board.get(0).get(1).power, 1);
    t.checkExpect(l3.board.get(0).get(1).bottom, false);
  }

  void testSortEdges(Tester t) {
    this.initData();
    l1.sortEdges();
    t.checkExpect(l1.mst, new ArrayList<Edge>(Arrays.asList(e1, e2, e3, e4)));
  }

  void testKruskalsCreate(Tester t) {
    this.initData();
    l5.rand = new Random(1);
    l5.kruskalsCreate(false, false);
    t.checkExpect(l5.mst, new ArrayList<Edge>(
        Arrays.asList(new Edge(g7, g8, 0), new Edge(g9, g10, 1), new Edge(g8, g10, 1))));
  }

  void testBiasedWires(Tester t) {
    this.initData();
    l5.rand = new Random(1);
    // this is where I create biased wires!
    l5.kruskalsCreate(true, false);
    t.checkExpect(l5.mst, new ArrayList<Edge>(
        Arrays.asList(new Edge(g7, g8, 0), new Edge(g7, g9, 0), new Edge(g8, g10, 1))));

    this.initData();
    l5.rand = new Random(1);
    l5.kruskalsCreate(false, true);
    t.checkExpect(l5.mst, new ArrayList<Edge>(
        Arrays.asList(new Edge(g7, g8, 0), new Edge(g9, g10, 1), new Edge(g7, g9, 185))));
  }

  void testFindRadius(Tester t) {
    this.initData();
    t.checkExpect(l3.radius, 2);
    t.checkExpect(l6.radius, 7);
  }

  void testShuffleBoard(Tester t) {
    this.initData();
    l3.rand = new Random(1);
    l3.shuffleBoard();
    GamePiece top = l3.board.get(0).get(0);
    GamePiece bottom = l3.board.get(0).get(1);
    t.checkExpect(top.left, false);
    t.checkExpect(top.right, false);
    t.checkExpect(top.top, false);
    t.checkExpect(top.bottom, true);
    t.checkExpect(bottom.left, false);
    t.checkExpect(bottom.right, true);
    t.checkExpect(bottom.top, false);
    t.checkExpect(bottom.bottom, false);
  }

}
