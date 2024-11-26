package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;


import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Engine {
    TERenderer ter = new TERenderer();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private final List<Point> roomMidPoints = new ArrayList<>();
    private Point rolePos;

    public static void main(String[] args){
        Engine engine = new Engine();
        engine.interactWithKeyboard();

    }

    public void interactWithKeyboard() {
        // 初始化繪圖工具
        ter.initialize(WIDTH, HEIGHT + 2);

        // 主選單
        drawMainMenu();
        char choice = solicitInput();

        TETile[][] world = null;
        switch (choice) {
            case 'N':
                // 新遊戲，要求輸入種子
                long seed = solicitSeedInput();
                world = handleNewWorld("N" + seed + "S");
                break;
            case 'L':
                // 載入遊戲
                world = handleLoadWorld();
                if (world == null) {
                    System.out.println("No saved game found!");
                    System.exit(0);
                }

                handleLoadPos();
                break;
            case 'Q':
                // 離開
                System.exit(0);
                break;
            default:
                throw new IllegalArgumentException("Invalid input: " + choice);
        }
        // 遊戲迴圈
        playGame(world);
    }

    private void drawMainMenu() {
        // 繪製主選單畫面
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Welcome to BYOW!");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "Quit (Q)");
        StdDraw.show();
    }

    private char solicitInput() {
        // 等待並讀取使用者的輸入
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                input = Character.toUpperCase(input);
                if (input == 'N' || input == 'L' || input == 'Q') {
                    return input;
                }
            }
        }
    }

    private long solicitSeedInput() {
        // 要求輸入種子
        StringBuilder seedBuilder = new StringBuilder();
        StdDraw.clear(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Enter Seed (Press S to finish):");
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if (input == 'S' || input == 's') {
                    break;
                }
                if (Character.isDigit(input)) {
                    seedBuilder.append(input);
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(WIDTH / 2, HEIGHT / 2, "Enter Seed (Press S to finish):");
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, seedBuilder.toString());
                    StdDraw.show();
                }
            }
        }
        return Long.parseLong(seedBuilder.toString());
    }

    private void playGame(TETile[][] world) {
        // 遊戲主迴圈
        ter.renderFrame(world);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                input = Character.toUpperCase(input);

                if (input == ':') {
                    // 等待下一個鍵來檢查是否退出
                    while (!StdDraw.hasNextKeyTyped()) {
                        // 等待鍵盤輸入
                    }
                    char nextInput = StdDraw.nextKeyTyped();
                    if (nextInput == 'Q' || nextInput == 'q') {
                        saveWorld(world);
                        System.exit(0);
                    }
                }

                // 移動角色
                handleMovement(input, world);
                ter.renderFrame(world);

            }
        }
    }

    private void handleMovement(char input, TETile[][] world) {
        int x = rolePos.x;
        int y = rolePos.y;

        switch (input) {
            case 'W':
                if (y + 1 < HEIGHT && world[x][y + 1].equals(Tileset.FLOOR)) {
                    rolePos.setLocation(x, y + 1);
                }
                break;
            case 'A':
                if (x - 1 >= 0 && world[x - 1][y].equals(Tileset.FLOOR)) {
                    rolePos.setLocation(x - 1, y);
                }
                break;
            case 'S':
                if (y - 1 >= 0 && world[x][y - 1].equals(Tileset.FLOOR)) {
                    rolePos.setLocation(x, y - 1);
                }
                break;
            case 'D':
                if (x + 1 < WIDTH && world[x + 1][y].equals(Tileset.FLOOR)) {
                    rolePos.setLocation(x + 1, y);
                }
                break;
        }

        // 更新角色位置
        world[x][y] = Tileset.FLOOR;
        world[rolePos.x][rolePos.y] = Tileset.AVATAR;

    }



    public TETile[][] interactWithInputString(String input) {
        input = input.toUpperCase();
        char command = input.charAt(0);
        TETile[][] world;
        switch (command){
            case 'N':
                world = handleNewWorld(input);
                world = handleMovements(world, input);
                break;
            case 'L':
                handleLoadPos();
                world = handleLoadWorld();
                world = handleMovements(world, input);
                break;
//            case 'Q':
//                System.exit(0);
//                break;
            default:
                throw new IllegalArgumentException("Invalid input: " + input);
        }
        return world;
    }


    private void saveWorld(TETile[][] currWorld){
        try{
            ObjectOutputStream worldOos = new ObjectOutputStream(new FileOutputStream("world"));
            ObjectOutputStream posOos = new ObjectOutputStream(new FileOutputStream("position"));

            posOos.writeObject(rolePos);
            worldOos.writeObject(currWorld);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initializePos(TETile[][] currWorld, Random rand){
        while(true){
            int xPos = RandomUtils.uniform(rand, WIDTH);
            int yPos = RandomUtils.uniform(rand, HEIGHT);
            TETile currTile = currWorld[xPos][yPos];
            if(currTile.equals(Tileset.FLOOR)){
                currWorld[xPos][yPos] = Tileset.AVATAR;
                rolePos = new Point(xPos, yPos);
                break;
            }
        }
    }

    private TETile[][] handleNewWorld(String input){
        Random rand = new Random(getSeed(input));
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        initializeWorld(finalWorldFrame);
        generateRooms(finalWorldFrame, rand);
        generateHallways(finalWorldFrame, rand);
        addWalls(finalWorldFrame);
        initializePos(finalWorldFrame, rand);
        return finalWorldFrame;
    }

    private TETile[][] handleMovements(TETile[][] world, String input){

        Queue<Character> movements = getMovements(input);
        while(!movements.isEmpty()){
            char movement = Character.toUpperCase(movements.poll());
            int xPos = rolePos.x;
            int yPos = rolePos.y;
            switch (movement){
                case 'W':
                    if(xPos + 1 < WIDTH && world[xPos][yPos + 1].equals(Tileset.FLOOR)){
                        rolePos.setLocation(xPos, yPos + 1);
                    }
                    break;
                case 'A':
                    if(xPos - 1 >= 0 && world[xPos - 1][yPos].equals(Tileset.FLOOR)){
                        rolePos.setLocation(xPos - 1, yPos);
                    }
                    break;
                case 'S':
                    if(yPos - 1 >= 0 && world[xPos][yPos - 1].equals(Tileset.FLOOR)){
                        rolePos.setLocation(xPos, yPos - 1);
                    }
                    break;
                case 'D':
                    if(yPos + 1 < HEIGHT && world[xPos + 1][yPos].equals(Tileset.FLOOR)){
                        rolePos.setLocation(xPos + 1, yPos);
                    }
                    break;
            }
            world[xPos][yPos] = Tileset.FLOOR;
            world[rolePos.x][rolePos.y] = Tileset.AVATAR;
        }
        return world;
    }

    private void handleLoadPos(){
        try{
            ObjectInputStream posOis= new ObjectInputStream(new FileInputStream("position"));

            Object pointObj = posOis.readObject();
            Point point = (Point) pointObj;

            // 確保角色位置被設置為AVATAR
            this.rolePos= new Point(point.x,point.y);
            // 關閉輸入流
            posOis.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private TETile[][] handleLoadWorld(){
        TETile[][] world = null;
        try{
            ObjectInputStream worldOis = new ObjectInputStream(new FileInputStream("world"));

            Object worldObj = worldOis.readObject();
            world = (TETile[][]) worldObj;

            // 關閉輸入流
            worldOis.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return world;
    }

    private long getSeed(String input) {
        StringBuilder str = new StringBuilder();
        boolean read = false;
        for (char c : input.toCharArray()) {
            if (c == 'N') {
                read = true;
            } else if (c == 'S') {
                break;
            } else if (read && Character.isDigit(c)) {
                str.append(c);
            }
        }
        return Long.parseLong(str.toString());
    }

    private Queue<Character> getMovements(String input){
        Queue<Character> movements = new ArrayDeque<>();
        char[] inputChar = input.toCharArray();
        boolean read = false;
        for (int i = 0; i < inputChar.length; i++) {
            char c = inputChar[i];
            if (c == 'S') {
                read = true;
            } else if (c == 'Q') {
                if(inputChar[i - 1] == ':'){
                    read = false;
                }
            } else if (read && Character.isDigit(c)) {
                movements.offer(c);
            }
        }
        return movements;

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

    private class Edge implements Comparable<Edge> {
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

