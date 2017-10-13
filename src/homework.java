import java.io.*;
import java.util.*;

/**
 * Created by adi on 03/10/17.
 */
class MoveToPassUpstream {
    Integer position, score;

    public MoveToPassUpstream(Integer position, Integer score) {
        this.position = position;
        this.score = score;
    }
}

class Move {
    String board = "?";  //TODO optimise String comparison in equals by storing string as hash(string)
    int position = -1, fruitsConsumed = -1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (position != move.position) return false;
        if (fruitsConsumed != move.fruitsConsumed) return false;
        return board != null ? board.equals(move.board) : move.board == null;
    }

    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + fruitsConsumed;
        result = 31 * result + (board != null ? board.hashCode() : 0);
        return result;
    }

    public Move(int position, int fruitsConsumed, String board) {
        this.position = position;
        this.fruitsConsumed = fruitsConsumed;
        this.board = board;
    }
}

class ObjectCloner {
    // so that nobody can accidentally create an ObjectCloner object
    private ObjectCloner() {
    }

    // returns a deep copy of an object
    static public Object deepCopy(Object oldObj) throws Exception {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B
            // serialize and pass the object
            oos.writeObject(oldObj);   // C
            oos.flush();               // D
            ByteArrayInputStream bin =
                    new ByteArrayInputStream(bos.toByteArray()); // E
            ois = new ObjectInputStream(bin);                  // F
            // return the new object
            return ois.readObject(); // G
        } catch (Exception e) {
            System.out.println("Exception in ObjectCloner = " + e);
            throw (e);
        } finally {
            oos.close();
            ois.close();
        }
    }
}

public class homework {

    public static final int MAX = 1, MIN = -1;
    int boardDimension, typesOfFruits, boardSize, leafCount = 0, cuts = 0, temp = 0, recursiveCalls = 0;
    public HashMap<Move, Move> moveMap;
    public HashMap<String, TreeMap> generatedPossibleMoves;
    public HashMap<String, String> gravitatedMatrices;
    String time, absolutePath, board; // The main board denoting all the fruits;
    double currentTime;

    public int addAdjacentPositions(Queue<Integer> possibleNodes, HashSet<Integer> visitedNodes, int currentPosition, StringBuilder boardBuilder) {
        int left, right, up, down, boardSize = boardDimension * boardDimension, score = 0;
        left = currentPosition - 1;
        right = currentPosition + 1;
        up = currentPosition - boardDimension;
        down = currentPosition + boardDimension;
        if (0 <= up && up < boardSize && !visitedNodes.contains(up))
            if (boardBuilder.charAt(up) == boardBuilder.charAt(currentPosition) && boardBuilder.charAt(up) != '*') {
                possibleNodes.add(up);
                visitedNodes.add(up);
                score++;
            }
        if (0 <= down && down < boardSize && !visitedNodes.contains(down))
            if (boardBuilder.charAt(down) == boardBuilder.charAt(currentPosition) && boardBuilder.charAt(down) != '*') {
                possibleNodes.add(down);
                visitedNodes.add(down);
                score++;
            }
        if (0 <= left && left < boardSize && !visitedNodes.contains(left) && (left % boardDimension != boardDimension - 1))
            if (boardBuilder.charAt(left) == boardBuilder.charAt(currentPosition) && boardBuilder.charAt(left) != '*') {
                possibleNodes.add(left);
                visitedNodes.add(left);
                score++;
            }
        if (0 <= right && right < boardSize && !visitedNodes.contains(right) && (right % boardDimension != 0))
            if (boardBuilder.charAt(right) == boardBuilder.charAt(currentPosition) && boardBuilder.charAt(right) != '*') {
                possibleNodes.add(right);
                visitedNodes.add(right);
                score++;
            }
        return score;
    }

    public TreeMap<Integer, Integer> generatePossibleMoves(String board) {
        if(generatedPossibleMoves.containsKey(board)) // If computed before, return;
            return generatedPossibleMoves.get(board);

        TreeMap<Integer, Integer> possibleMoveMap = new TreeMap<>(Collections.reverseOrder());
        for (int i = 0; i < boardDimension; i++)
            for (int j = 0; j < boardDimension; j++){
                if (board.charAt(i * boardDimension + j) != '*') {
                    Move move = performMove(i * boardDimension + j, board);
                    board = move.board;
                    possibleMoveMap.put(move.fruitsConsumed, i * boardDimension + j); //TODO wrong map configuration entries get overwritten
                }
            }
        generatedPossibleMoves.put(board, possibleMoveMap);
        return possibleMoveMap;
    }

    public void getInputs() throws IOException {
        absolutePath = new File("").getAbsolutePath();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(absolutePath + "/input.txt"));
        boardDimension = Integer.parseInt(bufferedReader.readLine());
        typesOfFruits = Integer.parseInt(bufferedReader.readLine());
        time = bufferedReader.readLine();
        board = new String();
        String line;
        while ((line = bufferedReader.readLine()) != null)
            board = board + line;
    }

    public String gravitateMatrix(String board) {
        if(gravitatedMatrices.containsKey(board))
            return gravitatedMatrices.get(board);
        String row, newBoard = "";
        char[] emptySpaces;
        int emptySpaceCount;
        board = transposeMatrix(board);
        for (int i = 0; i < boardSize; i += boardDimension) {
            row = board.substring(i, i + boardDimension);
            row = row.replace("*", "");
            emptySpaceCount = boardDimension - row.length();
            emptySpaces = new char[emptySpaceCount];
            Arrays.fill(emptySpaces, '*');
            row = new String(emptySpaces) + row;
            newBoard += row;
        }
        String transposedMatrix = transposeMatrix(newBoard);
        gravitatedMatrices.put(board, transposedMatrix);
        return transposedMatrix;
    }

    public void initializeDataMembers() {
        currentTime = System.currentTimeMillis();
        boardSize = boardDimension * boardDimension;
        moveMap = new HashMap<>();
        generatedPossibleMoves = new HashMap<>();
        gravitatedMatrices = new HashMap<>();
    }

    public Move performMove(int startPosition, String board) {
        Move move = new Move(startPosition, -1, board), resultedMove;
        if (moveMap.containsKey(move))
            return moveMap.get(move);

        int fruitsConsumed = 0;
        StringBuilder boardBuilder = new StringBuilder(board);
        Queue<Integer> possibleNodes = new ArrayDeque<>();
        HashSet<Integer> visitedNodes = new HashSet<>();

        possibleNodes.add(startPosition);
        fruitsConsumed++;
        visitedNodes.add(startPosition);
        int currentPosition;
        while (!possibleNodes.isEmpty()) {
            currentPosition = possibleNodes.element();
            fruitsConsumed += addAdjacentPositions(possibleNodes, visitedNodes, currentPosition, boardBuilder);
            boardBuilder.setCharAt(currentPosition, '*');
            possibleNodes.remove();
        }
        resultedMove = new Move(startPosition, fruitsConsumed, boardBuilder.toString());
        moveMap.put(move, resultedMove);
        return resultedMove;
    }

    public MoveToPassUpstream playTurn(int playerTurn, String board, int score, int depth, int alpha, int beta) throws Exception {
        recursiveCalls++;
        int moveStartPosition;
        TreeMap<Integer, Integer> possibleMoveMap = generatePossibleMoves(board);
        if (possibleMoveMap.isEmpty() || depth == 0) {  // Max depth reached or board is all *.
            leafCount++;
            return new MoveToPassUpstream(null, score);
        }

        Integer currentScore, scoreAfterMove, bestMove = null;
        Iterator<Map.Entry<Integer, Integer>> it = possibleMoveMap.entrySet().iterator();
        while(it.hasNext()) {// for all possible moves on current board
            moveStartPosition = (int) ((Map.Entry)it.next()).getValue();
            Move currentMove = performMove(moveStartPosition, board);
            scoreAfterMove = currentMove.fruitsConsumed * currentMove.fruitsConsumed * (playerTurn) + score;
            currentScore = playTurn(-playerTurn, gravitateMatrix(currentMove.board),   // Recursive call for children
                    scoreAfterMove, depth - 1, alpha, beta).score;

            //Updating Best Score
            if (shouldUpdateBestScore(currentScore, alpha, beta, playerTurn)) {
                bestMove = moveStartPosition;
                if (playerTurn == 1)
                    alpha = currentScore;
                else
                    beta = currentScore;
            }
            // PRUNE the rest in the for loop and in possibleMoveMap
            if (alpha >= beta) {
                while(it.hasNext()) {
                    temp = (int) ((Map.Entry) it.next()).getKey();
                    break;
                }
                TreeMap<Integer, Integer>  prunedPossibleMoveMap = (TreeMap<Integer, Integer>) ObjectCloner.deepCopy(possibleMoveMap);
                prunedPossibleMoveMap.tailMap(temp).clear();
                generatedPossibleMoves.put(board, prunedPossibleMoveMap);
                cuts++;
                return new MoveToPassUpstream(bestMove, playerTurn == 1 ? beta : alpha);
            }
        }
        return new MoveToPassUpstream(bestMove, playerTurn == 1 ? alpha : beta);
    }

    public void printMatrix(String board) {
        int boardLength = boardDimension * boardDimension;
        for (int i = 0; i < boardLength; i++) {
            System.out.print(board.charAt(i));
            if (((i + 1) % boardDimension) == 0)
                System.out.println();
        }
    }

    public boolean shouldUpdateBestScore(int currentScore, int alpha, int beta, int playerTurn) {
        if (playerTurn == 1) {
            if (currentScore > alpha)
                return true;
            else
                return false;
        } else {
            if (currentScore < beta)
                return true;
            else
                return false;
        }
    }

    public String transposeMatrix(String board) {
        char stringArray[] = board.toCharArray(), temp;
        for (int i = 0; i < boardDimension; i++)
            for (int j = 0; j < i; j++) {
                temp = stringArray[boardDimension * j + i];
                stringArray[boardDimension * j + i] = stringArray[boardDimension * i + j];
                stringArray[boardDimension * i + j] = temp;
            }
        return String.valueOf(stringArray);
    }


    private void printOutput(int position) throws FileNotFoundException {
        PrintStream printStreamForOutputFile = new PrintStream(new FileOutputStream(absolutePath + "/output.txt"));
        PrintStream streamForTimeFile = new PrintStream(new FileOutputStream(absolutePath + "/time.txt", true));
        PrintStream streamForScoreFile = new PrintStream(new FileOutputStream(absolutePath + "/score.txt", true));
        System.setOut(printStreamForOutputFile);
        //System.out.println(boardDimension + "\n" + typesOfFruits + "\n" + time);
        Move bestMove = performMove(position, board);
        printMatrix(gravitateMatrix(bestMove.board));
        System.setOut(streamForScoreFile);
        System.out.print(bestMove.fruitsConsumed * bestMove.fruitsConsumed +"  ");
        System.setOut(streamForTimeFile);
        System.out.print(System.currentTimeMillis() - currentTime +"  ");
    }

    public static void main(String[] args) throws Exception {
        homework hw = new homework();
        hw.getInputs();
        hw.initializeDataMembers();
        MoveToPassUpstream move = hw.playTurn(MAX, hw.board, 0, Integer.parseInt(args[0]) * 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        hw.printOutput(move.position);
        //System.out.println("\n Leaf count is " + hw.leafCount + " Cuts " + hw.cuts);
        //System.out.println(" \n Move" + move.position + " score " + move.score + " recursive calls " + hw.recursiveCalls);
    }

}
