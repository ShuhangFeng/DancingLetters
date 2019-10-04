import java.util.*;
import java.awt.Color;
import javalib.worldimages.*;

// Extra Credits done:
// Enhancing the graphics with gradient coloring
// Allowing the player to start a new puzzle without restarting the program.
// Keeping score, keeping time
// both left click and right click will work to minimize steps
// Construct wiring with a bias in a particular direction

class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  boolean connectedToLeft;
  boolean connectedToRight;
  boolean connectedToTop;
  boolean connectedToBottom;
  // whether the power station is on this piece
  boolean powerStation;
  int power;
  int radius;
  int bfsCounter;
  ArrayList<GamePiece> neighbours;
  static final int PIECE_SIZE = 100;

  GamePiece() {
    this.row = 0;
    this.col = 0;
    this.right = false;
    this.left = false;
    this.top = false;
    this.bottom = false;
    this.connectedToRight = false;
    this.connectedToLeft = false;
    this.connectedToTop = false;
    this.connectedToBottom = false;
    this.powerStation = false;
    this.power = 0;
    this.radius = 0;
    this.bfsCounter = 1;
    this.neighbours = new ArrayList<GamePiece>();
  }

  GamePiece(boolean left, boolean right, boolean top, boolean bottom) {
    this.row = 0;
    this.col = 0;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.connectedToRight = false;
    this.connectedToLeft = false;
    this.connectedToTop = false;
    this.connectedToBottom = false;
    this.powerStation = false;
    this.power = 0;
    this.neighbours = new ArrayList<GamePiece>();
  }

  // draw image for each piece
  WorldImage draw() {
    final WorldImage piece = new OverlayImage(
        new RectangleImage(PIECE_SIZE, PIECE_SIZE, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(PIECE_SIZE, PIECE_SIZE, OutlineMode.SOLID, Color.DARK_GRAY));
    Color c = Color.LIGHT_GRAY;

    if (this.power > 0) {
      c = new Color(0, (255 * power) / radius, 0);
    }

    WorldImage horizontalRec = new RectangleImage(PIECE_SIZE / 2, PIECE_SIZE / 20,
        OutlineMode.SOLID, c);
    WorldImage verticalRec = new RectangleImage(PIECE_SIZE / 20, PIECE_SIZE / 2, OutlineMode.SOLID,
        c);
    final WorldImage star = new OverlayImage(
        new StarImage(PIECE_SIZE / 3, 7, OutlineMode.OUTLINE, Color.ORANGE),
        new StarImage(PIECE_SIZE / 3, 7, OutlineMode.SOLID, Color.CYAN));
    WorldImage result = piece;

    if (this.right) {
      result = new OverlayImage(horizontalRec.movePinhole(-PIECE_SIZE / 4, 0), result);
    }

    if (this.left) {
      result = new OverlayImage(horizontalRec.movePinhole(PIECE_SIZE / 4, 0), result);
    }

    if (this.top) {
      result = new OverlayImage(verticalRec.movePinhole(0, PIECE_SIZE / 4), result);
    }

    if (this.bottom) {
      result = new OverlayImage(verticalRec.movePinhole(0, -PIECE_SIZE / 4), result);
    }

    if (this.powerStation) {
      result = new OverlayImage(star, result);
    }
    return result;
  }

  // power up for one piece!
  void powerUp(int p) {
    if (p > this.power) {
      this.power = p;
      for (int i = 0; i < this.neighbours.size(); i++) {
        GamePiece g = this.neighbours.get(i);
        g.neighbours.remove(this);
        g.powerUp(p - 1);
      }
    }
  }

  // implement the clockwise rotation behavior!
  void leftClick() {
    boolean temp = this.left;
    this.left = this.bottom;
    this.bottom = this.right;
    this.right = this.top;
    this.top = temp;

  }

  // implement the counter-clockwise rotation behavior!
  void rightClick() {
    boolean temp = this.left;
    this.left = this.top;
    this.top = this.right;
    this.right = this.bottom;
    this.bottom = temp;
  }
}




