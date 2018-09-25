/**
 * This skeleton is just an exmple.
 * Feel free to change this skeleton or using better ideas to implement.
 */

import java.util.*;

// implement the class of block if necessary
class Block {
    int blockType;
    ArrayList<Integer> yBlock = new ArrayList<>();
    ArrayList<Integer> xBlock = new ArrayList<>();
    GameState g;
    public Block(GameState g, int y, int x) {
        this.g = g;
        blockType = g.board[y][x];
        defineBlock(y, x);
    }
    public void defineBlock(int y, int x) {
        if(blockType == 0) {
            return;
        }
        if(blockType == 4) {
            yBlock.add(y);
            xBlock.add(x);
            return;
        }
        if(blockType == 3) {
            for(int i = 0; i < 5; i++) {
                for (int j = 0; j < 4; j++) {
                    if(g.board[i][j] == 3) {
                        yBlock.add(i);
                        xBlock.add(j);
                    }
                }
            }
            return;
        }
        if(blockType == 1) {
            for(int i = 0; i < 5; i++) {
                for (int j = 0; j < 4; j++) {
                    if(g.board[i][j] == 1) {
                        yBlock.add(i);
                        xBlock.add(j);
                    }
                }
            }
        }
        if(blockType == 2) {
            yBlock.add(y);
            xBlock.add(x);
            // block connects upward
            if(y - 1 >= 0 && g.board[y-1][x] == 2 && y + 1 < g.board.length && g.board[y+1][x] != 2) {
                yBlock.add(y-1);
                xBlock.add(x);
            }
            // block connects downward
            if(y - 1 >= 0 && g.board[y-1][x] != 2 && y + 1 < g.board.length && g.board[y+1][x] == 2) {
                yBlock.add(y+1);
                xBlock.add(x);
            }
            // both top and bottom are similar
            if(y - 1 >= 0 && g.board[y-1][x] == 2 && y + 1 < g.board.length && g.board[y+1][x] == 2) {
                if(y - 2 >= 0 && g.board[y-2][x] == 2) {
                    // connects downward
                    yBlock.add(y+1);
                    xBlock.add(x);
                }
                else {
                    yBlock.add(y-1);
                    xBlock.add(x);
                }
            }
        }
    }
    public boolean canMove(int dy, int dx) {
        if(blockType == 0 || blockType == 4) {
            return false;
        }
        if(blockType == 2 && yBlock.size() != 2) {
            return false;
        }
        if(blockType == 3 && yBlock.size() != 2) {
            return false;
        }
        if(blockType == 1 && yBlock.size() != 4) {
            return false;
        }
        ArrayList<Integer> yRef = new ArrayList<>();
        ArrayList<Integer> xRef = new ArrayList<>();
        if(dy == -1) {
            int yMin = Collections.min(yBlock);
            for(int i = 0; i < yBlock.size(); i++) {
                if(yBlock.get(i) == yMin) {
                    yRef.add(yMin);
                    xRef.add(xBlock.get(i));
                }
            }
        }
        if(dy == 1) {
            int yMax = Collections.max(yBlock);
            for(int i = 0; i < yBlock.size(); i++) {
                if(yBlock.get(i) == yMax) {
                    yRef.add(yMax);
                    xRef.add(xBlock.get(i));
                }
            }
        }
        if(dx == -1) {
            int xMin = Collections.min(xBlock);
            for(int i = 0; i < xBlock.size(); i++) {
                if(xBlock.get(i) == xMin) {
                    yRef.add(yBlock.get(i));
                    xRef.add(xMin);
                }
            }
        }
        if(dx == 1) {
            int xMax = Collections.max(xBlock);
            for(int i = 0; i < xBlock.size(); i++) {
                if(xBlock.get(i) == xMax) {
                    yRef.add(yBlock.get(i));
                    xRef.add(xMax);
                }
            }
        }
        for(int m = 0; m < yRef.size(); m++) {
            int yCoord = yRef.get(m);
            int xCoord = xRef.get(m);
            boolean a = yCoord + dy >= 0;
            boolean b = yCoord + dy < g.board.length;
            boolean c = xCoord + dx >= 0 && xCoord + dx < g.board[0].length;
            boolean d = g.board[yCoord+dy][xCoord+dx] == 0;
            if(!(a && b && c && d)) {
                return false;
            }
        }
        return true;
    }
    public GameState move(int dy, int dx) {
        for(int i = 0; i < yBlock.size(); i++) {
            g.board[yBlock.get(i)][xBlock.get(i)] = 0;
        }
        for(int i = 0; i < yBlock.size(); i++) {
            g.board[yBlock.get(i) + dy][xBlock.get(i) + dx] = blockType;
        }
        return g;
    }
}

class GameState implements Comparable<GameState> {
    public int[][] board = new int[5][4];
    public GameState parent = null;
    public int cost = 0;
    public int steps = 0;

    public GameState(int [][] inputBoard, int steps) {
        for(int i = 0; i < 5; i++)
            for(int j = 0; j < 4; j++)
                this.board[i][j] = inputBoard[i][j];
        this.steps = steps;
    }

    public GameState(int [][] inputBoard) {
        for(int i = 0; i < 5; i++)
            for(int j = 0; j < 4; j++)
                this.board[i][j] = inputBoard[i][j];
    }


    // get all successors and return them in sorted order
    public List<GameState> getNextStates() {
        ArrayList<String> visited = new ArrayList<>();
        List<GameState> successors = new ArrayList<>();
        ArrayList<Integer> yEmpty = new ArrayList<>();
        ArrayList<Integer> xEmpty = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                if(board[i][j] == 0) {
                    yEmpty.add(i);
                    xEmpty.add(j);
                }
            }
        }

        // one block movements
        for(int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j] == 4) {
                    for (int emptyIndex = 0; emptyIndex < yEmpty.size(); emptyIndex++) {
                        int yCoord = yEmpty.get(emptyIndex);
                        int xCoord = xEmpty.get(emptyIndex);
                        GameState child = new GameState(board);
                        child.board[yCoord][xCoord] = 4;
                        child.board[i][j] = 0;
                        if(!visited.contains(child.getStateID())) {
                            visited.add(child.getStateID());
                            successors.add(child);
                        }
                    }
                }
            }
        }

        // adjacent block movement to empty
        for (int emptyIndex = 0; emptyIndex < yEmpty.size(); emptyIndex++) {
            int yCoord = yEmpty.get(emptyIndex);
            int xCoord = xEmpty.get(emptyIndex);
            if(yCoord - 1 >= 0) {
                Block top = new Block(new GameState(this.board), yCoord - 1, xCoord);
                if(top.canMove(1, 0)) {
                    GameState child = top.move(1, 0);
                    if(!visited.contains(child.getStateID())) {
                        visited.add(child.getStateID());
                        successors.add(child);
                    }
                }
            }
            if(yCoord + 1 < board.length) {
                Block bottom = new Block(new GameState(this.board), yCoord + 1, xCoord);
                if(bottom.canMove(-1, 0)) {
                    GameState child = bottom.move(-1, 0);
                    if(!visited.contains(child.getStateID())) {
                        visited.add(child.getStateID());
                        successors.add(child);
                    }
                }
            }
            if(xCoord - 1 >= 0) {
                Block left = new Block(new GameState(this.board), yCoord, xCoord - 1);
                if(left.canMove(0, 1)) {
                    GameState child = left.move(0, 1);
                    if(!visited.contains(child.getStateID())) {
                        visited.add(child.getStateID());
                        successors.add(child);
                    }
                }
            }
            if(xCoord + 1 < board[0].length) {
                Block right = new Block(new GameState(this.board), yCoord, xCoord + 1);
                if(right.canMove(0, -1)) {
                    GameState child = right.move(0, -1);
                    if(!visited.contains(child.getStateID())) {
                        visited.add(child.getStateID());
                        successors.add(child);
                    }
                }
            }
        }
        // sort GameState
        Collections.sort(successors);
        return successors;
    }

    // return the 20-digit number as ID
    public String getStateID() {
        String s = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++)
                s += this.board[i][j];
        }
        return s;
    }

    public void printBoard() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++)
                System.out.print(this.board[i][j]);
            System.out.println();
        }
    }

    // check whether the current state is the goal
    public boolean goalCheck() {
        return (board[3][1] == 1 && board[3][2] == 1 &&
                board[4][1] == 1 && board[4][2] == 1);
    }

    public int compareTo(GameState other) {
        return getStateID().compareTo(other.getStateID());
    }
}

class AStarSearch{
    Queue<GameState> openSet;
    Set<GameState> closedSet;

    //Comparator for the GameState
    public Comparator<GameState> stateComparator = new Comparator<GameState>() {
        @Override
        public int compare(GameState o1, GameState o2) {
            if (o1.cost - o2.cost != 0)
                return o1.cost - o2.cost;
            else
                return o1.getStateID().compareTo(o2.getStateID());
        }
    };

    // print the states of board in open set
    public void printOpenList(int flag, GameState state) {
        System.out.println("OPEN");
        for(GameState game : openSet) {
            System.out.println(game.getStateID());
            game.printBoard();
            System.out.println(game.steps + " " + game.steps + " " + "0");
            System.out.println(game.parent.getStateID());
        }
    }

    public void printClosedList(int flag, GameState state) {
        System.out.println("CLOSED");
        for(GameState game : closedSet) {
            System.out.println(game.getStateID());
            game.printBoard();
            System.out.println(game.steps + " " + game.steps + " " + "0");
            if(game.parent != null) {
                System.out.println(game.parent.getStateID());
            }
            else {
                System.out.println("null");
            }
        }
    }

    // implement the A* search
    public GameState aStarSearch(int flag, GameState state) {
        // feel free to using other data structures if necessary
        openSet = new PriorityQueue<>(stateComparator);
        closedSet = new HashSet<>();
        int goalCheck = 0;
        int maxOPEN = -1;
        int maxCLOSED = -1;
        int steps = 0;

        openSet.add(state);
        while(!openSet.isEmpty()) {
            GameState g = openSet.poll();
            closedSet.add(g);
            steps++;
            if (flag == 200 || flag == 400) {
                if (steps == 12)
                    printClosedList(flag, g);
                System.out.println("iteration " + steps);
                System.out.println(g.getStateID());
                g.printBoard();
//                // eventually cost will be steps+heuristic
                int f = g.steps + getHeuristic(flag, g);
                System.out.println(f + " " + g.steps + " " + getHeuristic(flag, g));
                if (g.parent != null) {
                    System.out.println(g.parent.getStateID());
                } else {
                    System.out.println("null");
                }
            }
            goalCheck++;
            if (g.goalCheck()) {
                //print info and exit
                if (flag == 300 || flag == 500) {
                    ArrayList<GameState> solution = new ArrayList<>();
                    solution.add(g);
                    GameState path = g.parent;
                    while (!path.getStateID().equals(state.getStateID())) {
                        solution.add(path);
                        path = path.parent;
                    }
                    Collections.reverse(solution);
                    for (GameState game : solution) {
                        game.printBoard();
                        System.out.println();
                    }
                    System.out.println("goalCheckTimes " + goalCheck);
                    System.out.println("maxOPENSize " + maxOPEN);
                    System.out.println("maxCLOSEDSize " + maxCLOSED);
                    System.out.println("steps " + g.steps);
                }
                return g;
            }
            List<GameState> children = g.getNextStates();
            for (GameState child : children) {
                // eventually cost will be steps+heuristic
                child.parent = g;
                child.steps = g.steps + 1;
                child.cost = g.steps + getHeuristic(flag, child);

                String childStateId = child.getStateID();
                boolean inOpen = false;
                boolean inClosed = false;
                for (GameState game : openSet) {
                    if (game.getStateID().equals(childStateId)) {
                        inOpen = true;
                    }
                }
                for (GameState game : closedSet) {
                    if (game.getStateID().equals(childStateId)) {
                        inClosed = true;
                    }
                }
                if (!inOpen && !inClosed) {
                    openSet.add(child);
                } else {
                    boolean insertStatus = false;
                    if (openSet.contains(child)) {
                        for (GameState game : openSet) {
                            if (game.getStateID().equals(childStateId) && child.steps < game.steps) {
                                game.parent = child.parent;
                                insertStatus = true;
                            }
                        }
                    }
                    if (closedSet.contains(child)) {
                        for (GameState game : closedSet) {
                            if (game.getStateID().equals(childStateId) && child.steps < game.steps) {
                                game.parent = child.parent;
                                insertStatus = true;
                            }
                        }
                    }
                    if (insertStatus) {
                       openSet.add(child);
                    }
                }
            }
            if (openSet.size() > maxOPEN) {
                maxOPEN = openSet.size();
            }
            if (closedSet.size() > maxCLOSED) {
                maxCLOSED = closedSet.size();
            }
            if(flag == 200) {
                printOpenList(flag, g);
                printClosedList(flag, g);
            }

        }

        System.out.println("PQ is empty. No solution found.");

        return state;
    }

    public int manhattanHeuristic(GameState g) {
        int yBottomRight = -1;
        int xBottomRight = -1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                if(g.board[i][j] == 1) {
                    if(i > yBottomRight) {
                        yBottomRight = i;
                    }
                    if(j > xBottomRight) {
                        xBottomRight = j;
                    }
                }
            }
        }
        return Math.abs(yBottomRight - 4) + Math.abs(xBottomRight - 2);
    }

    public int getHeuristic(int flag, GameState g) {
        if(flag == 400 || flag == 500) {
            return manhattanHeuristic(g);
        }
        return 0;
    }
}

public class Klotski {
    public static void printNextStates(GameState s) {
        List<GameState> states = s.getNextStates();
        for (GameState state: states) {
            state.printBoard();
            System.out.println();
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length < 21) {
            return;
        }
        int flag = Integer.parseInt(args[0]);
        int[][] board = new int[5][4];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = Integer.parseInt(args[i * 4 + j + 1]);
            }
        }
        GameState s = new GameState(board, 0);

        s.printBoard();
        System.out.println("---");

        if (flag == 100) {
            printNextStates(s);
            return;
        }

        AStarSearch search = new AStarSearch();
        search.aStarSearch(flag, s);

    }

}
