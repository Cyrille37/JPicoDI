package picoDI.tests;

import static org.junit.Assert.fail;

import org.junit.Test;

import picoDI.PicoDI;
import picoDI.tests.fixtures.Bidon01;
import picoDI.tests.fixtures.Bidon02;

public class TestLoadWithConstructorParameters {

	@SuppressWarnings("unused")
	@Test
	public void test1() {

		PicoDI pdi = new PicoDI();
		pdi.load("tests/picoDI/tests/fixtures/config02.json");
		Bidon02 b = (Bidon02) pdi.getObject("Bidon02");
	}

	@SuppressWarnings("unused")
	@Test
	public void test2() {

		PicoDI pdi = new PicoDI();
		pdi.load("tests/picoDI/tests/fixtures/config03.json");

		Bidon01 b1 = (Bidon01) pdi.getObject("Bidon01");
		Bidon02 b2 = (Bidon02) pdi.getObject("Bidon02");
	}

	@SuppressWarnings("unused")
	@Test
	public void test3() {

		PicoDI pdi = new PicoDI();
		pdi.load("tests/picoDI/tests/fixtures/config03.json");

		try {
			Bidon02 b1 = (Bidon02) pdi.getObject("Bidon01");
			fail("Should failed !");
		} catch (Exception e) {
		}
		try {
			Bidon01 b2 = (Bidon01) pdi.getObject("Bidon02");
			fail("Should failed !");
		} catch (Exception e) {
		}
	}

}
