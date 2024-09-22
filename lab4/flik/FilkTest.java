package flik;

import org.junit.*;
import static org.junit.Assert.*;

public class FilkTest {
    @Test
    public void test() {
        for (int i = 0, j = 0; i < 500; ++i, ++j){
            assertTrue(Flik.isSameNumber(i, j));
        }
    }
}
