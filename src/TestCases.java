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
    void setUp() throws FileNotFoundException {
        hw = new homework();
        String path = new File("").getAbsolutePath();
        reader = new BufferedReader(new FileReader(path + "/input.txt"));
        hw.boardDimension = 4;
        board = "123*12*456785*78";
        hw.initializeDataMembers();
    }
    @Test
    public void testConcatenate(){
        String result = "one" + "two";
        assertEquals("onetwo", result);
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
        assertEquals((Arrays.asList(new Integer[]{7})), hw.generatePossibleMoves(board));
    }

    @Test
    public void checkTreeGenerationForMinMax(){
        hw.boardDimension = 2;
        hw.boardSize = 4;
        move = hw.playTurn(1, "0121", 0, 1);
        assertEquals(1, (int)move.position);
        hw.boardDimension = 3;
        hw.boardSize = 9;
        move = hw.playTurn(1, "*******0*", 0, 1);
        assertEquals(7, (int)move.position);
        move = hw.playTurn(1, "444444444", 0, 1);
        assertEquals(0, (int)move.position);
    }

    @Test
    public void checkTreeForInput() throws IOException {
        hw.boardDimension = Integer.parseInt(reader.readLine());
        hw.boardSize = hw.boardDimension * hw.boardDimension;
        int ummy = Integer.parseInt(reader.readLine());
        double time = Double.parseDouble(reader.readLine());
        String board = "";
        for(int i=0; i<hw.boardDimension; i++)
            board += reader.readLine();
        move = hw.playTurn(1, board, 0, 1);
        assertEquals(0, (int)move.position);
    }
}
