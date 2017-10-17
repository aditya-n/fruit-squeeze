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

class ValueComparator implements Comparator<Integer>, Serializable{
    HashMap<Integer, Integer> hashMap = new HashMap<>();
    public ValueComparator(HashMap<Integer, Integer> hashMap) {
        this.hashMap.putAll(hashMap);
    }
    @Override
    public int compare(Integer x, Integer y){
        if(this.hashMap.get(x) == null || this.hashMap.get(y) == null)
            return 1;
        if(this.hashMap.get(x) <= this.hashMap.get(y))
            return 1;
        else
            return -1;
    }
}

class Move {
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
    long startTime, timeForThisTurn;
    int depth[];

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

        TreeMap<Integer, Integer> sortedMapByValue = new TreeMap<>();
        HashMap<Integer, Integer> possibleMoveMap = new HashMap<>();
        for (int i = 0; i < boardDimension; i++)
            for (int j = 0; j < boardDimension; j++){
                if (board.charAt(i * boardDimension + j) != '*') {
                    Move move = performMove(i * boardDimension + j, board);
                    board = move.board;
                    //possibleMoveMap.put(move.fruitsConsumed, i * boardDimension + j); //
                    possibleMoveMap.put(i * boardDimension + j, move.fruitsConsumed);
                }
            }
        // Sort possibleMoveMap based no. of fruitsConsumed
        if(!possibleMoveMap.isEmpty())
            sortedMapByValue = sortMapByValue(possibleMoveMap);
        generatedPossibleMoves.put(board, sortedMapByValue);
        return sortedMapByValue;
    }

    private TreeMap<Integer,Integer> sortMapByValue(HashMap<Integer, Integer> possibleMoveMap) {
        Comparator<Integer> comparator = new ValueComparator(possibleMoveMap);
        TreeMap<Integer,Integer> sortedMap = new TreeMap(comparator);
        sortedMap.putAll(possibleMoveMap);
        return sortedMap;
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
        //if(gravitatedMatrices.containsKey(board))
            //return gravitatedMatrices.get(board);
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
        //gravitatedMatrices.put(board, transposedMatrix);
        return transposedMatrix;
    }

    public void initializeDataMembers() {
        startTime = System.currentTimeMillis();
        boardSize = boardDimension * boardDimension;
        moveMap = new HashMap<>();
        generatedPossibleMoves = new HashMap<>();
        gravitatedMatrices = new HashMap<>();
        depth = new int[100];
        timeForThisTurn = (long)(Double.parseDouble(time) * 2)/generatePossibleMoves(board).size();
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
        //if(timeReached(System.currentTimeMillis()))
          //  return new MoveToPassUpstream(null, score);
        //this.depth[depth]++;
        recursiveCalls++;
        int moveStartPosition;
        TreeMap<Integer, Integer> possibleMoveMap = generatePossibleMoves(board);
        int currentScore = 0, scoreAfterMove, bestMove = 0;
        Iterator<Map.Entry<Integer, Integer>> it = possibleMoveMap.entrySet().iterator();
        while(it.hasNext()) {// for all possible moves on current board
            moveStartPosition = (int) ((Map.Entry)it.next()).getKey();
            Move currentMove = performMove(moveStartPosition, board);
            scoreAfterMove = currentMove.fruitsConsumed * currentMove.fruitsConsumed * (playerTurn) + score;
            String boardAfterCurrentMove = gravitateMatrix(currentMove.board);
            if(!gameOver(depth, boardAfterCurrentMove))
                currentScore = playTurn(-playerTurn, gravitateMatrix(currentMove.board),   // Recursive call for children
                    scoreAfterMove, depth - 1, alpha, beta).score;
            else {
                leafCount++;
                currentScore = scoreAfterMove;          // GAME OVER. Current Node is a leaf. Return back and not calling children
            }
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
                // Prune Lookup map for generate Possible Moves(board)
                TreeMap<Integer, Integer>  prunedPossibleMoveMap = (TreeMap<Integer, Integer>) ObjectCloner.deepCopy(possibleMoveMap);
                prunedPossibleMoveMap.tailMap(temp).clear();
                generatedPossibleMoves.put(board, prunedPossibleMoveMap);
                cuts++;
                return new MoveToPassUpstream(bestMove, playerTurn == 1 ? beta : alpha);
            }
        }
        return new MoveToPassUpstream(bestMove, playerTurn == 1 ? alpha : beta);
    }

    private boolean timeReached(long currentTime) {
        if(currentTime - startTime > (timeForThisTurn*1000)) {
            //System.out.println("\nTime :" + currentTime + "-" + startTime);
            return true;
        }
        return false;
    }

    private boolean gameOver(int depth, String board) {
        if(depth == 1)
            return true;
        if(board.replace("*", "").length() == 0)
            return true;
        return false;
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


    public void printOutput(int position) throws FileNotFoundException {
        PrintStream printStreamForOutputFile = new PrintStream(new FileOutputStream(absolutePath + "/output.txt"));
        //PrintStream streamForTimeFile = new PrintStream(new FileOutputStream(absolutePath + "/time.txt", true));
        //PrintStream streamForScoreFile = new PrintStream(new FileOutputStream(absolutePath + "/score.txt"));
        System.setOut(printStreamForOutputFile);
        Move bestMove = performMove(position, board);
        System.out.println((char)(position%boardDimension + 65) +""+ (position/boardDimension + 1));
        printMatrix(gravitateMatrix(bestMove.board));
        //System.setOut(streamForScoreFile);
        //System.out.print(bestMove.fruitsConsumed +"  ");
        //System.setOut(streamForTimeFile);
    }

    public static void main(String[] args) throws Exception {
        homework hw = new homework();
        hw.getInputs();
        hw.initializeDataMembers();
        int possibleMoves = hw.generatePossibleMoves(hw.board).size();
        // System.out.println("\n Generated possible moves " + possibleMoves);
        double timePerMove = (Double.parseDouble(hw.time) * 2) / possibleMoves;
        int depth = possibleMoves > 35 ? 3:6;
        if(timePerMove < 0.1)
            depth = 2;
        if(timePerMove < 0.05)
            depth = 1;
        MoveToPassUpstream move = hw.playTurn(MAX, hw.board, 0, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        hw.printOutput(move.position);
        //System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        //System.out.println("\n Leaf count is " + hw.leafCount + " Cuts " + hw.cuts);
        //System.out.println(" \n Move Position" + move.position + " score " + move.score + " recursive calls " + hw.recursiveCalls);
    }
}
