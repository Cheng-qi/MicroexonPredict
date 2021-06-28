package ccbb.hrbeu.exonimpact.util;

public class Tris<T1, T2, T3> {
	public Tris() {
		super();
	}

	public Tris(T1 t1, T2 t2, T3 t3) {
		value1 = t1;
		value2 = t2;
		value3 = t3;
	}

	T1 value1;
	T2 value2;
	T3 value3;

	public String toString() {
		return value1 + ":" + value2 + "-" + value3;
	}

	public T1 getValue1() {
		return value1;
	}

	public T2 getValue2() {
		return value2;
	}

	public T3 getValue3() {
		return value3;
	}

	public void setValue1(T1 t1) {
		value1 = t1;
	}

	public void setValue2(T2 t2) {
		value2 = t2;
	}

	public void setValue3(T3 t3) {
		value3 = t3;
	}

}
