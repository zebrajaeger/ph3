package de.zebrajaeger.phserver.hardware;

public class Acceleration {
    int x;
    int y;
    int z;

    public Acceleration(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Acceleration{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
