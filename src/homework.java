import java.io.*;
import java.util.*;

/**
 * Created by adi on 03/10/17.
 */

class Pair {
    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int x, y;
}

class MoveToPassUpstream{
    Integer position, score;

    public MoveToPassUpstream(Integer position, Integer score) {
        this.position = position;
        this.score = score;
    }
}

class Move {
    public Move(int position, int fruitsConsumed, String board) {
        this.position = position;
        this.fruitsConsumed = fruitsConsumed;
        this.board = board;
    }

    String board = "?";
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
    int boardDimension, typesOfFruits, boardSize;
    public HashMap<Move, Move> moveMap;
    String time;

    public int addAdjacentPositions(Queue<Integer> possibleMoves, HashSet<Integer> visitedNodes, int currentPosition, StringBuilder boardBuilder) {
        int left, right, up, down, boardSize = boardDimension * boardDimension, score = 0;
        left = currentPosition - 1;
        right = currentPosition + 1;
        up = currentPosition - boardDimension;
        down = currentPosition + boardDimension;
        if (0 <= up && up < boardSize && !visitedNodes.contains(up))
            if (boardBuilder.charAt(up) == boardBuilder.charAt(currentPosition) && boardBuilder.charAt(up)!='*') {
                possibleMoves.add(up);
                visitedNodes.add(up);
                score++;
            }
        if (0 <= down && down < boardSize && !visitedNodes.contains(down))
            if (boardBuilder.charAt(down) == boardBuilder.charAt(currentPosition) && boardBuilder.charAt(down)!='*') {
                possibleMoves.add(down);
                visitedNodes.add(down);
                score++;
            }
        if (0 <= left && left < boardSize && !visitedNodes.contains(left))
            if (boardBuilder.charAt(left) == boardBuilder.charAt(currentPosition) && boardBuilder.charAt(left)!='*') {
                possibleMoves.add(left);
                visitedNodes.add(left);
                score++;
            }
        if (0 <= right && right < boardSize && !visitedNodes.contains(right))
            if (boardBuilder.charAt(right) == boardBuilder.charAt(currentPosition) && boardBuilder.charAt(right)!='*') {
                possibleMoves.add(right);
                visitedNodes.add(right);
                score++;
            }
        return score;
    }

    public ArrayList<Integer> generatePossibleMoves(String board) {
        ArrayList<Integer> possibleMoves = new ArrayList<>();
        for (int i = 0; i < boardDimension; i++)
            for (int j = 0; j < boardDimension; j++) //TODO optimise when whole string is "******"
                if (board.charAt(i * boardDimension + j) != '*') {
                    board = performMove(i * boardDimension + j, board).board;
                    possibleMoves.add(i * boardDimension + j);
                }
        return possibleMoves;
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
    }

    public Move performMove(int startPosition, String board) {
        Move move = new Move(startPosition, -1, board), resultedMove;
        if (moveMap.containsKey(move))
            return moveMap.get(move);

        int score = 0;
        StringBuilder boardBuilder = new StringBuilder(board);
        Queue<Integer> possibleMoves = new ArrayDeque<>();
        HashSet<Integer> visitedNodes = new HashSet<>();

        possibleMoves.add(startPosition);
        score++;
        visitedNodes.add(startPosition);
        int currentPosition;
        while (!possibleMoves.isEmpty()) {
            currentPosition = possibleMoves.element();
            score += addAdjacentPositions(possibleMoves, visitedNodes, currentPosition, boardBuilder);
            boardBuilder.setCharAt(currentPosition, '*');
            possibleMoves.remove();
        }
        resultedMove = new Move(startPosition, score, boardBuilder.toString());
        moveMap.put(move, resultedMove);
        return resultedMove;
    }

    public MoveToPassUpstream playTurn(int playerTurn, String board, int score, int depth, int alpha, int beta) {
        ArrayList<Integer> possibleMoves = generatePossibleMoves(board);
        if (possibleMoves.isEmpty() || depth == 5)
            return new MoveToPassUpstream(null, score);
        Integer bestScore, currentScore, scoreAfterMove, bestMove = null;
        bestScore = playerTurn == 1 ? alpha : beta;
        for (int moveStartPosition : possibleMoves) {
            Move currentMove = performMove(moveStartPosition, board);
            scoreAfterMove = currentMove.fruitsConsumed * currentMove.fruitsConsumed * (playerTurn) + score;
            if(playerTurn == 1)
                currentScore = playTurn(-playerTurn, gravitateMatrix(currentMove.board),
                            scoreAfterMove, depth+1, bestScore, beta).score;
            else
                currentScore = playTurn(-playerTurn, gravitateMatrix(currentMove.board),
                        scoreAfterMove, depth+1, alpha, bestScore).score;
            if(depth == 1)
                System.out.println(moveStartPosition +" position. sscore "+ currentScore);
            bestScore = updateBestScore(currentScore, bestScore, playerTurn);
            if(currentScore == bestScore)
                bestMove = moveStartPosition;
            if(checkWhetherToPrune(bestScore, alpha, beta, playerTurn))
                return new MoveToPassUpstream(bestMove, playerTurn == 1 ? beta : alpha);
        }
        return new MoveToPassUpstream(bestMove, bestScore);
    }

    public boolean checkWhetherToPrune(Integer bestScore, int alpha, int beta, int playerTurn) {
        if(playerTurn == 1)
            if(bestScore >= beta)
                return true;
        else
            if(alpha >= bestScore)
                return true;
    }

    public void printMatrix(String board) {
        int boardLength = boardDimension * boardDimension;
        for (int i = 0; i < boardLength; i++) {
            System.out.print(board.charAt(i));
            if (((i + 1) % boardDimension) == 0)
                System.out.println();
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

    public int updateBestScore(int currentScore, int bestScore, int playerTurn) {
        if (playerTurn == 1) {
            if (currentScore > bestScore)
                bestScore = currentScore;
        } else {
            if (currentScore < bestScore)
                bestScore = currentScore;
        }
        return bestScore;
    }

    public static void main(String[] args) throws IOException {
        homework hw = new homework();
        hw.getInputs();
    }
}
