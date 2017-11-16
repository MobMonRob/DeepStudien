import static org.junit.Assert.*;

import org.junit.Test;

public class QuaternionUtilsTests {

	@Test
	public void normalize() {
		double[] q = new double[] {1, 0, 1, 0};
		QuternionUtils.normalizeQuaternion(q);
		assertArrayEquals(new double[] {0.7071, 0, 0.7071, 0}, q, 10e-4);
	}

}