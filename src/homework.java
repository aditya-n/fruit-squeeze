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
    String board; // The main board denoting all the fruits
    int boardDimension, typesOfFruits, boardSize, leafCount = 0, cuts = 0, temp = 0;
    public HashMap<Move, Move> moveMap;
    public HashMap<String, TreeMap> generatedPossibleMoves;
    String time;

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
                    possibleMoveMap.put(move.fruitsConsumed, i * boardDimension + j);
                }
            }
        generatedPossibleMoves.put(board, possibleMoveMap);
        return possibleMoveMap;
    }

    public void getInputs() throws IOException {
        String absolutePath = new File("").getAbsolutePath();
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
        return transposeMatrix(newBoard);
    }

    public void initializeDataMembers() {
        boardSize = boardDimension * boardDimension;
        moveMap = new HashMap<>();
        generatedPossibleMoves = new HashMap<>();
    }

    public Move performMove(int startPosition, String board) {
        Move move = new Move(startPosition, -1, board), resultedMove;
        if (moveMap.containsKey(move))
            return moveMap.get(move);

        int score = 0;
        StringBuilder boardBuilder = new StringBuilder(board);
        Queue<Integer> possibleNodes = new ArrayDeque<>();
        HashSet<Integer> visitedNodes = new HashSet<>();

        possibleNodes.add(startPosition);
        score++;
        visitedNodes.add(startPosition);
        int currentPosition;
        while (!possibleNodes.isEmpty()) {
            currentPosition = possibleNodes.element();
            score += addAdjacentPositions(possibleNodes, visitedNodes, currentPosition, boardBuilder);
            boardBuilder.setCharAt(currentPosition, '*');
            possibleNodes.remove();
        }
        resultedMove = new Move(startPosition, score, boardBuilder.toString());
        moveMap.put(move, resultedMove);
        return resultedMove;
    }

    public MoveToPassUpstream playTurn(int playerTurn, String board, int score, int depth, int alpha, int beta) {
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
            if (shouldUpdateBestScore(currentScore, alpha, beta, playerTurn)) {
                bestMove = moveStartPosition;
                if (playerTurn == 1)
                    alpha = currentScore;
                else
                    beta = currentScore;
            }
            if (alpha >= beta) { // PRUNE the rest in the for loop and in possibleMoveMap
                while(it.hasNext()) {
                    temp = (int) ((Map.Entry) it.next()).getKey();
                    break;
                }
                possibleMoveMap.tailMap(temp).clear();
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

    public static void main(String[] args) throws IOException {
        homework hw = new homework();
        hw.getInputs();
    }
}
