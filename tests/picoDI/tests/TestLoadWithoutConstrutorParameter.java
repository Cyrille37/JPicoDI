package picoDI.tests;

import org.junit.Test;

import picoDI.PicoDI;
import picoDI.tests.fixtures.Bidon01;

public class TestLoadWithoutConstrutorParameter {

	@SuppressWarnings("unused")
	@Test
	public void test() {
		PicoDI pdi = new PicoDI();
		pdi.load("tests/picoDI/tests/fixtures/config01.json");
		Bidon01 b = (Bidon01) pdi.getObject("Bidon01");
		//fail("Not yet implemented");
	}

}
