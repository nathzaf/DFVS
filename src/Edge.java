public class Edge {
    private final int i;

    private final int j;

    private boolean isArriere;

    public Edge(int x, int y) {
        this.i = x;
        this.j = y;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public boolean isArriere() {
        return isArriere;
    }

    public void setArriere(boolean arriere) {
        isArriere = arriere;
    }
}
