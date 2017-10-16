import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by adi on 03/10/17.
 */
public class TestCases{

    homework hw;
    String board;
    BufferedReader reader;
    MoveToPassUpstream move;

    @BeforeEach
    void setUp() throws IOException {
        hw = new homework();
        String path = new File("").getAbsolutePath();
        reader = new BufferedReader(new FileReader(path + "/input.txt"));
        hw.boardDimension = 4;
        board = "123*12*456785*78";
        hw.getInputs();
        hw.initializeDataMembers();
    }
    @Test
    public void performMoveTest(){
        hw.boardDimension = 3;
        hw.boardSize = 9;
        board = "*11133133";
        board = hw.performMove( 1, board).board;
        assertEquals("***133133", board);
    }
    @Test
    public void testgravityTranspose(){
        board = "123*12*456785*78";
        assertEquals("1155226*3*77*488", hw.transposeMatrix(board));
        assertEquals("1***123452785678", hw.gravitateMatrix(board));
        assertEquals((Arrays.asList(new Integer[]{0,1,2,7,8,9,10,11})), hw.generatePossibleMoves(board));
        hw.boardDimension = 2;
        hw.boardSize = 4;
        board = "*121";
        assertEquals("*121", hw.gravitateMatrix(board));
        hw.boardDimension = 3;
        board = "*******0*";
        //assertEquals((Arrays.asList(new Integer[]{7})), hw.generatePossibleMoves(board));
        board = "*11133133";
        //assertEquals((Arrays.asList(new Integer[]{1, 3, 4})), hw.generatePossibleMoves(board));
    }

    @Test
    public void checkTreeGeneration() throws Exception {
        hw.boardDimension = 2;
        hw.boardSize = 4;
        //move = hw.playTurn(1, "0121", 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        //assertEquals(1, (int)move.position);
        hw.boardDimension = 3;
        hw.boardSize = 9;
        move = hw.playTurn(1, "*******0*", 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertEquals(7, (int)move.position);
        move = hw.playTurn(1, "444444444", 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertEquals(0, (int)move.position);
        move = hw.playTurn(1, "0121", 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertEquals(0, (int)move.position);
        move = hw.playTurn(1, "111133233", 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertEquals(4, (int)move.position);
    }

    @Test
    public void checkTreeForInput() throws Exception {
        hw.leafCount = 0;
        hw.cuts = 0;
        hw.recursiveCalls = 0;
        int possibleMoves = hw.generatePossibleMoves(hw.board).size();
        System.out.println("\n Generated possible moves " + possibleMoves);
        int depth = (int)(possibleMoves/(hw.timeForThisTurn*2));
        move = hw.playTurn(hw.MAX, hw.board, 0, 6, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("\n Leaf count is " + hw.leafCount + " Cuts " + hw.cuts);
        System.out.println(" \n Move" + move.position + " score " + move.score + " recursive calls " + hw.recursiveCalls);
        System.out.println((char)(move.position%hw.boardDimension + 65) +""+ (move.position/hw.boardDimension + 1));
        //hw.printOutput(move.position);
    }
}
