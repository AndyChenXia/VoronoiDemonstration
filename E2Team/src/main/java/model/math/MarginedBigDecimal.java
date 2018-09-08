package model.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A BigDecimal class which calculates equality based on a threshold
 * 
 * @author mxw449
 */
public class MarginedBigDecimal {
	/** Precision values */
	private static final int PRECISION_STORE = 20, PRECISION_COMPARISON = 5;

	/** How to round */
	private static final RoundingMode ROUNDING_MODE = RoundingMode.FLOOR;

	/** Context for division and other operations */
	private static final MathContext mc = new MathContext(PRECISION_STORE, ROUNDING_MODE);

	/** How to format the number when printing */
	private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.00000");

	/** Margin of error for comparisons */
	private static final MarginedBigDecimal MARGIN_OF_ERROR = new MarginedBigDecimal(1).pow(-PRECISION_COMPARISON);

	/** Constants for ease of use */
	public static final MarginedBigDecimal ZERO = new MarginedBigDecimal(0);
	public static final MarginedBigDecimal TWO = new MarginedBigDecimal(2);
	
	private BigDecimal bd;

	private MarginedBigDecimal(BigDecimal val) {
		this.bd = val.setScale(PRECISION_STORE, ROUNDING_MODE);
	}

	public MarginedBigDecimal(double val) {
		this.bd = new BigDecimal(val, mc);
	}

	public MarginedBigDecimal(int val) {
		this.bd = new BigDecimal(val, mc);
	}

	/**
	 * Add two MarginedBigDecimals
	 */
	public MarginedBigDecimal add(MarginedBigDecimal other) {
		return new MarginedBigDecimal(this.bd.add(other.bd));
	}

	/**
	 * Subtract two MarginedBigDecimals
	 */
	public MarginedBigDecimal subtract(MarginedBigDecimal other) {
		return new MarginedBigDecimal(this.bd.subtract(other.bd));
	}

	/**
	 * Multiply two MarginedBigDecimals
	 */
	public MarginedBigDecimal multiply(MarginedBigDecimal other) {
		return new MarginedBigDecimal(this.bd.multiply(other.bd));
	}

	/**
	 * Divide two MarginedBigDecimals
	 */
	public MarginedBigDecimal divide(MarginedBigDecimal other) {
		return new MarginedBigDecimal(this.bd.divide(other.bd, PRECISION_STORE, ROUNDING_MODE));
	}

	/**
	 * Negate a MarginedBigDecimal
	 */
	public MarginedBigDecimal negate() {
		return new MarginedBigDecimal(this.bd.negate());
	}

	/**
	 * (this ^ n) as a MarginedBigDecimal 
	 */
	public MarginedBigDecimal pow(int n) {
		return new MarginedBigDecimal(this.bd.pow(n, mc));
	}

	/**
	 * The absolute value of a MarginedBigDecimal
	 */
	public MarginedBigDecimal abs() {
		return new MarginedBigDecimal(this.bd.abs());
	}

	/**
	 * Compare a MarginedBigDecimal to this object.
	 * @return 0 if equal, negative if other > this, positive if this > other
	 */
	public int compareTo(MarginedBigDecimal other) {
		return this.bd.compareTo(other.bd);
	}

	/**
	 * @return The double value of this MarginedBigDecimal
	 */
	public double value() {
		return this.bd.doubleValue();
	}

	/**
	 * @return The integer value of this MarginedBigDecimal
	 */
	public int intValue() {
		return (int) value();
	}

	public boolean equals(Object x) {
		if (!(x instanceof MarginedBigDecimal)) {
			return false;
		}

		MarginedBigDecimal other = (MarginedBigDecimal) x;
		MarginedBigDecimal difference = subtract(other).abs();

		return difference.compareTo(MARGIN_OF_ERROR) < 0;
	}

	public String toString() {
		return NUMBER_FORMAT.format(bd);
	}
}
