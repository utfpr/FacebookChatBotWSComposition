/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundex;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author marcelo
 */
public class SoundexTest {

    public SoundexTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of comparacaoFonetica method, of class Soundex.
     */
    @Test
    public void testComparacaoFonetica_String_String() {
        System.out.println("Teste da comparacaoFonetica (Soundex)");
        comparar("vc", "Você");
        comparar("caqui", "caki");
        comparar("sumir", "somir");
        comparar("canto", "camto");
        comparar("defenda", "dê fenda");
        comparar("amigo", "amg");
        comparar("beleza", "blz");
        comparar("kely", "kelly");
        comparar("catia", "cátia");
        comparar("mal", "mau");
        comparar("nmr", "numero");
    }

    private void comparar(String a, String b) {
        boolean expResult = true;
        boolean result = Soundex.comparacaoFonetica(a, b);
        System.out.println(String.format("Comparando '%s' com '%s' = %s", a, b, result));
        assertEquals(expResult, result);
    }
}



