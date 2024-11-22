package byow.Core;

public class Rectangle {
    // the x and y are the left bottom axis
    private final int x;
    private final int y;
    private final int w;
    private final int h;
    public Rectangle(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public Rectangle(Rectangle r) {
        this.x = r.x;
        this.y = r.y;
        this.w = r.w;
        this.h = r.h;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    /**
     * Judge if the two rectangle are overlapped
     * @param r the other rectangle
     * @return if the two rectangle is overlapped
     */
    public boolean isOverlapped(Rectangle r) {
        if (x <= r.getX() && x + w >= r.getX()) {
            if (y < r.getY() && y + h > r.getY()) {
                return true;
            }
            if (y >= r.getY() && y <= r.getY() + r.getH()) {
                return true;
            }
        }
        if (x >= r.getX() && x <= r.getX() + r.getW()) {
            if (y < r.getY() && y + h > r.getY()) {
                return true;
            }
            if (y >= r.getY() && y <= r.getY() + r.getH()) {
                return true;
            }
        }
        return false;
    }

    public Position center() {
        return new Position(x + w / 2, y + h / 2);
    }
}
