package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;
    private static Random random  = new Random(234234);
    public static void main(String[] args){
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);


        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        addHexagon(3, 30, 0, world);
        addHexagon(3, 20, 6, world);
        addHexagon(3, 20, 12, world);
        addHexagon(3, 20, 18, world);




        addHexagon(3, 25, 3, world);
        addHexagon(3, 25, 9, world);
        addHexagon(3, 25, 15, world);
        addHexagon(3, 25, 21, world);

        addHexagon(3, 30, 6, world);
        addHexagon(3, 30, 12, world);
        addHexagon(3, 30, 18, world);
        addHexagon(3, 30, 6, world);
        addHexagon(3, 30, 24, world);

        addHexagon(3, 35, 3, world);
        addHexagon(3, 35, 9, world);
        addHexagon(3, 35, 15, world);
        addHexagon(3, 35, 21, world);

        addHexagon(3, 40, 6, world);
        addHexagon(3, 40, 12, world);
        addHexagon(3, 40, 18, world);







        ter.renderFrame(world);

    }

    public static void addHexagon(int s, int posX, int posY, TETile[][] world) {
        TETile tile = TETile.colorVariant(Tileset.WALL, 1000, 33242, 24343 , random);
        // 下半部分
        for (int y = 0; y < s; y++) {
            // 每一行的起始X位置 = posX - y
            int startX = posX - y;
            // 每一行的终止X位置 = posX + s + y - 1
            int endX = posX + s + y;
            for (int x = startX; x < endX; x++) {
                world[x][posY + y] = tile; // 使用Tileset的WALL来填充
            }
        }

        // 上半部分
        for (int y = 0; y < s; y++) {

            // 每一行的起始X位置 = posX - (s - y - 1)
            int startX = posX + (y - s + 1);
            // 每一行的终止X位置 = posX + s + (s - y - 2)
            int endX = posX + s + (s - y - 1);
            for (int x = startX; x < endX; x++) {
                world[x][posY + s + y] = tile;
            }
        }
    }


    }