package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Point;
import java.util.*;

public class Engine {
    TERenderer ter = new TERenderer();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static final List<Point> roomMidPoints = new ArrayList<>();

    public static void main(String[] args) {
        Engine engine = new Engine();
        TETile[][] world = engine.interactWithInputString("n5197880843569031643s");
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
    }

    public void interactWithKeyboard() {
        // Implementation for keyboard interaction
    }

    public TETile[][] interactWithInputString(String input) {
        Random rand = new Random(getSeed(input));
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        initializeWorld(finalWorldFrame);
        generateRooms(finalWorldFrame, rand);
        generateHallways(finalWorldFrame, rand);
        addWalls(finalWorldFrame);
        return finalWorldFrame;
    }

    private long getSeed(String input) {
        StringBuilder str = new StringBuilder();
        boolean read = false;
        for (char c : input.toCharArray()) {
            if (c == 'N' || c == 'n') {
                read = true;
            } else if (c == 'S' || c == 's') {
                break;
            } else if (read && Character.isDigit(c)) {
                str.append(c);
            }
        }
        return Long.parseLong(str.toString());
    }

    private void generateRooms(TETile[][] world, Random rand) {
        int roomNum = RandomUtils.uniform(rand, 100, 200);
        int attempts = 0;
        int maxAttempts = 100;

        while (roomMidPoints.size() < roomNum && attempts < maxAttempts) {
            int w = RandomUtils.uniform(rand, 4, 8);
            int h = RandomUtils.uniform(rand, 4, 8);
            int x = RandomUtils.uniform(rand, 2, WIDTH - w - 2);
            int y = RandomUtils.uniform(rand, 2, HEIGHT - h - 2);

            if (canPlaceRoom(x, y, w, h, world)) {
                placeRoom(x, y, w, h, world);
            }
            attempts++;
        }
    }

    private boolean canPlaceRoom(int x, int y, int w, int h, TETile[][] world) {
        if (x + w + 1 >= WIDTH || y + h + 1 >= HEIGHT || x <= 0 || y <= 0) {
            return false;
        }

        // Check for overlap with existing rooms including a buffer zone
        for (int dx = x - 2; dx <= x + w + 2; dx++) {
            for (int dy = y - 2; dy <= y + h + 2; dy++) {
                if (dx >= 0 && dx < WIDTH && dy >= 0 && dy < HEIGHT) {
                    if (!world[dx][dy].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeRoom(int x, int y, int w, int h, TETile[][] world) {
        Point midPoint = new Point(x + w/2, y + h/2);
        roomMidPoints.add(midPoint);

        // Place floor tiles
        for (int dx = x; dx < x + w; dx++) {
            for (int dy = y; dy < y + h; dy++) {
                world[dx][dy] = Tileset.FLOOR;
            }
        }
    }

    private void generateHallways(TETile[][] world, Random rand) {
        // Create a minimum spanning tree to ensure connectivity
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < roomMidPoints.size(); i++) {
            for (int j = i + 1; j < roomMidPoints.size(); j++) {
                Point p1 = roomMidPoints.get(i);
                Point p2 = roomMidPoints.get(j);
                int distance = Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
                edges.add(new Edge(i, j, distance));
            }
        }

        // Sort edges by distance
        Collections.sort(edges);

        //DisJoint set
        DisjointSet ds = new DisjointSet();

        // Create hallways based on MST
        for (Edge edge : edges) {
            //如果還沒連接，連接兩個點
            if (!ds.isConnected(edge.from, edge.to)) {
                createHallway(roomMidPoints.get(edge.from), roomMidPoints.get(edge.to), world, rand);
                ds.connect(edge.from, edge.to);
            } else if (rand.nextDouble() < 0.15) {//如果已經連接，隨機新增路徑
                createHallway(roomMidPoints.get(edge.from), roomMidPoints.get(edge.to), world, rand);
            }
        }
    }


    private void createHallway(Point start, Point end, TETile[][] world, Random rand) {
        int x = start.x;
        int y = start.y;

        // Decide whether to go horizontal or vertical first
        boolean horizontalFirst = rand.nextBoolean();

        if (horizontalFirst) {
            while (x != end.x) {
                x += (x < end.x) ? 1 : -1;
                world[x][y] = Tileset.FLOOR;
            }
            while (y != end.y) {
                y += (y < end.y) ? 1 : -1;
                world[x][y] = Tileset.FLOOR;
            }
        } else {
            while (y != end.y) {
                y += (y < end.y) ? 1 : -1;
                world[x][y] = Tileset.FLOOR;
            }
            while (x != end.x) {
                x += (x < end.x) ? 1 : -1;
                world[x][y] = Tileset.FLOOR;
            }
        }
    }

    private void addWalls(TETile[][] world) {
        // Add walls around all floor tiles
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y].equals(Tileset.FLOOR)) {
                    addWallsAround(x, y, world);
                }
            }
        }
    }

    private void addWallsAround(int x, int y, TETile[][] world) {
        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isWithinBounds(newX, newY) && world[newX][newY].equals(Tileset.NOTHING)) {
                world[newX][newY] = Tileset.WALL;
            }
        }
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    private void initializeWorld(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static class Edge implements Comparable<Edge> {
        int from, to, weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }
    }


    //DisJoint set
    //提供union功能
    private class DisjointSet{
        int[] parents = new int[roomMidPoints.size()];
        private DisjointSet(){
            for(int i = 0; i < roomMidPoints.size(); i++){
                parents[i] = -1;
            }
        }

        private int find(int pointIndex){
            if(parents[pointIndex] >= 0){
                return find(parents[pointIndex]);
            }
            return pointIndex;

        }

        private int findSize(int pointIndex){
            return Math.abs(parents[pointIndex]);
        }

        private boolean isConnected(int pointPos1, int pointPos2){
            int root1 = find(pointPos1), root2 = find(pointPos2);
            return root1 == root2;
        }

        private  void connect(int pointPos1, int pointPos2){
            int root1 = find(pointPos1), root2 = find(pointPos2);
            if(findSize(root1) > findSize(root2)){
                parents[root2] = root1;
            }
            else{
                parents[root1] = root2;
            }
        }


    }
}

