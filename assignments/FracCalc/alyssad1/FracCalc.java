import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Takes input of fractions and an operator and prints out the answer.
 * 
 * Note: throughout the file fractions are represented as an int[]. The format
 * of the array is [numerator, denominator]. In reality, a Fraction class
 * should be used instead, but this is assigned before OO concepts are taught.
 * 
 * @author Alyssa Caulley
 *
 */
public class FracCalc {

	private static final String QUIT_INPUT = "quit";
	private static final int NUMERATOR = 0;
	private static final int DENOMINATOR = 1;
	
	/**
	 * The main function which drives the FractionCalculator.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			runMainLoop();
		} catch (Exception exn) {
			System.out.println("An unexpected error occurred.");
			System.exit(1);
		}
	}

	/**
	 * Runs the main prompt loop. Waiting for input, printing the answer.
	 * The loop exits when QUIT_INPUT is entered.
	 * 
	 * @throws IOException
	 */
	private static void runMainLoop() throws IOException {
		System.out.println("Welcome to FractionCalculator. Enter your fraction expression:");
		BufferedReader bufferedInput;
		bufferedInput = new BufferedReader(new InputStreamReader(System.in));
		String inputLine = bufferedInput.readLine();
		while (!inputLine.equals(QUIT_INPUT)) {
			try {
			    String answer = processInput(inputLine);
			    System.out.println(answer);
			} catch (Exception exn) {
				System.out.println("Invalid input, try again.");
			}
			inputLine = bufferedInput.readLine();
		}
	}

	/**
	 * Returns the answer to the input as a formatted string to print out.
	 * 
	 * @param inputLine The input from the user.
	 * @return The answer as a formatted string.
	 */
	private static String processInput(String inputLine) {
		int[] frac1;
		int[] frac2;
		String operator;
		String[] inputParts = inputLine.split(" ");
		if (inputParts.length != 3) {
			throw new IllegalArgumentException("Invalid input");
		}
		// Parse the fractions.
		frac1 = parseFraction(inputParts[0]);
		frac2 = parseFraction(inputParts[2]);
		operator = inputParts[1];
		// Get the reduced answer as a (possibly improper) fraction.
		int[] answer = getAnswer(frac1, frac2, operator);
		// Return the formatted answer.
		return formatFraction(answer);
	}

	/**
	 * Formats the given fraction as it should be printed to the console.
	 * 
	 * @param fraction The fraction to format. It may be improper, but it must
	 * be reduced.
	 * 
	 * @return The string format of the fraction provided.
	 */
	private static String formatFraction(int[] fraction) {
		// If the fraction is X/1, just return X.
		if (fraction[DENOMINATOR] == 1) {
			return fraction[0] + "";
		}
		// If it's an improper fraction, format it as a mixed fraction.
		else if (Math.abs(fraction[NUMERATOR]) > Math.abs(fraction[DENOMINATOR])) {
			// The whole number will get the - if there is one.
			int wholeNumber = fraction[NUMERATOR] / fraction[DENOMINATOR];
			// The remaining fraction should be printed without negative signs.
			int fractionNumerator = Math.abs(fraction[NUMERATOR] % fraction[DENOMINATOR]);
			int fractionDenominator = Math.abs(fraction[DENOMINATOR]);
			return String.format("%d_%d/%d", wholeNumber, fractionNumerator, fractionDenominator);
		} else {
			// The negative sign should be by the numerator.
			int numerator;
			int denominator = Math.abs(fraction[DENOMINATOR]);
			if (fraction[NUMERATOR] * fraction[DENOMINATOR] < 0) {
				numerator = -1 * Math.abs(fraction[NUMERATOR]);
			} else {
				numerator = Math.abs(fraction[NUMERATOR]);
			}
			return String.format("%d/%d", numerator, denominator);
		}
	}
	
	/**
	 * Returns the answer as a reduced fraction. May be an improper fraction.
	 * 
	 * @param frac1 The first fraction.
	 * @param frac2 The second fraction.
	 * @param operator The operation to perform on the fractions.
	 * @return The result of the operation performed on the given fractions.
	 */
	private static int[] getAnswer(int[] frac1, int[] frac2, String operator) {
		int[] answer;
		if (operator.equals("+")) {
			answer = addFractions(frac1, frac2);
		} else if (operator.equals("-")) {
			answer = subtractFractions(frac1, frac2);
		} else if (operator.equals("*")) {
			answer = multiplyFractions(frac1, frac2);
		} else if (operator.equals("/")) {
			answer = divideFractions(frac1, frac2);
		} else {
			throw new IllegalArgumentException("Invalid operator.");
		}
		return reduceFraction(answer);
	}

	/**
	 * Reduces the given fraction and returns the result.
	 * 
	 * @param fraction The fraction to reduce.
	 * @return The result of reducing the given fraction.
	 */
	private static int[] reduceFraction(int[] fraction) {
		int gcd = getGreatestCommonDivisor(
			fraction[NUMERATOR], fraction[DENOMINATOR]);
		return new int[] {fraction[NUMERATOR]/gcd, fraction[DENOMINATOR]/gcd};
	}

	/**
	 * Returns the greatest common divisor of the given numbers.
	 * 
	 * @param num1 The first number.
	 * @param num2 The second number.
	 * @return
	 */
	private static int getGreatestCommonDivisor(int num1, int num2) {
		if (num2 == 0) {
			return num1;
		} else {
			return getGreatestCommonDivisor(num2, num1 % num2);
		}
	}

	/**
	 * Adds the given fractions and returns the unreduced result.
	 * 
	 * @param frac1 The first fraction to add.
	 * @param frac2 The second fraction to add.
	 * @return The result of adding the given fractions.
	 */
	private static int[] addFractions(int[] frac1, int[] frac2) {
		int[] fraction1WithCD = getFractionWithCommonDenominator(
		    frac1, frac2[DENOMINATOR]);
		int[] fraction2WithCD = getFractionWithCommonDenominator(
			frac2, frac1[DENOMINATOR]);
		// Simply add the numerators and keep the common denominator.
		return new int[] {
			fraction1WithCD[NUMERATOR] + fraction2WithCD[NUMERATOR],
			fraction1WithCD[DENOMINATOR]
			};
	}

	/**
	 * Subtracts the given fractions and returns the unreduced result.
	 * 
	 * @param frac1 The first fraction.
	 * @param frac2 The fraction to subtract from the first fraction.
	 * @return The result of subtracting the given fractions.
	 */
	private static int[] subtractFractions(int[] frac1, int[] frac2) {
		int[] fraction1WithCD = getFractionWithCommonDenominator(
		    frac1, frac2[DENOMINATOR]);
		int[] fraction2WithCD = getFractionWithCommonDenominator(
			frac2, frac1[DENOMINATOR]);
		// Simply add the numerators and keep the common denominator.
		return new int[] {
			fraction1WithCD[NUMERATOR] - fraction2WithCD[NUMERATOR],
			fraction1WithCD[DENOMINATOR]
		    };
	}

	/**
	 * Multiplies the given fractions and returns the unreduced result.
	 * 
	 * @param frac1 The first fraction to multiply.
	 * @param frac2 The second fraction to multiply.
	 * @return The result of multiplying the given fractions.
	 */
	private static int[] multiplyFractions(int[] frac1, int[] frac2) {
		return new int[] {
			frac1[NUMERATOR] * frac2[NUMERATOR],
			frac1[DENOMINATOR] * frac2[DENOMINATOR]
			};
	}

	/**
	 * Divides the given fractions and returns the unreduced result.
	 * 
	 * @param frac1 The first fraction to divide.
	 * @param frac2 The second fraction to divide.
	 * @return The result of dividing the given fractions.
	 */
	private static int[] divideFractions(int[] frac1, int[] frac2) {
		// Dividing is just multiplying by the reciprocal.
		return multiplyFractions(
				frac1, new int[]{frac2[DENOMINATOR], frac2[NUMERATOR]});
	}

	/**
	 * We have to reduce anyway, so this just returns the fraction with any old common denominator.
	 * The simplest way is to multiply both denominators together to get a common multiple.
	 * 
	 * @param fraction A Fraction in the form of [numerator, denominator].
	 * @param otherDenominator The denominator of the other fraction.
	 * @return The given fraction with its new denominator - the multiple of its denominator and the
	 * other fraction's denominator.
	 */
	private static int[] getFractionWithCommonDenominator(int[] fraction, int otherDenominator) {
		return new int[] {fraction[0] * otherDenominator, fraction[1] * otherDenominator};
	}
	
	/**
	 * Returns a tuple of [numerator, denominator]. Note that this should be contained in a Fraction
	 * class, but this project comes before lessons about object-oriented programming.
	 * 
	 * @param fractionString The input fraction (or whole number) as a string of the format:
	 * 1_2/4, 8/2 or 2.
	 * @return The fraction as non-reduced, possibly improper fraction of the format:
	 * [numerator, denominator].
	 * @throws IOException If the given input is not of one of the expected formats.
	 */
	private static int[] parseFraction(String fractionString) {
		String[] fractionParts = fractionString.split("_");
		if (fractionParts.length > 2) {
			throw new IllegalArgumentException("Too many '_'s in the input.");
		}
		// If there is no '_' then it is not a mixed fraction. Simply get the numerator and
		// denominator.
		else if (fractionParts.length == 1) {
			return parseSimpleFraction(fractionParts[0]);
		}
		// Otherwise it's a mixed fraction. Parse the simple fraction part and then turn the
		// whole thing into an improper fraction.
		else {
			int wholeNumber = Integer.parseInt(fractionParts[0]);
			int[] fractionPart = parseSimpleFraction(fractionParts[1]);
			// If there was a negative sign, it should apply to the fraction as well.
			if (wholeNumber < 0) {
				fractionPart[NUMERATOR] = -1 * fractionPart[NUMERATOR];
			}
			return new int[]{
				wholeNumber * fractionPart[DENOMINATOR] + fractionPart[NUMERATOR],
				fractionPart[DENOMINATOR]
				};
		}
	}

	/**
	 * Returns a tuple of [numerator, denominator] for the given simple fraction string.
	 * 
	 * @param simpleFractionString A string representing a simple fraction in the form:
	 * 4/2, or 3.
	 * @return The fraction as a non-reduced, possibly improper fraction of the format:
	 * [numerator, denominator].
	 */
	private static int[] parseSimpleFraction(String simpleFractionString) {
		String[] fractionParts = simpleFractionString.split("/");
		if (fractionParts.length > 2) {
			throw new IllegalArgumentException("Invalid simple fraction.");
		}
		int numerator = Integer.parseInt(fractionParts[0]);
		if (fractionParts.length == 1) {
			return new int[]{numerator, 1};
		} else {
			return new int[]{numerator, Integer.parseInt(fractionParts[1])};
		}
	}
}
