import java.util.Arrays;
import java.util.Scanner;
import java.math.BigInteger;

public class FracCalc
{
	private static boolean debug = false;
	public static void main(String[] args)
	{
		System.out.println("Welcome to the FracCalc!");
		programLoop();
	}

	public static void programLoop()
	{
		boolean done = false;
		System.out.print("> Enter your query ['exit' to quit]: ");
		Scanner sc = new Scanner(System.in);
		do
		{
			String input = sc.nextLine();
			if(input.equals("exit"))
			{
				done = true;
				System.out.println("> Goodbye!");
			}
			else{
				System.out.println(processQuery(input));
				System.out.print("> Enter your query: ");
			}
		}
		while(!done && sc.hasNextLine());

	}

	public static String processQuery(String s)
	{
		char[] chars = s.toCharArray();

		BigInteger[] curFraction = new BigInteger[2];
		Arrays.fill(curFraction, BigInteger.valueOf(-1));

		BigInteger[] partialResultFraction = new BigInteger[2];
		Arrays.fill(partialResultFraction, BigInteger.valueOf(-1));

		BigInteger tmpWholeNum = BigInteger.valueOf(-1);

		char curOperator = '0';

		int i = 0;
		while(i < chars.length)
		{
			char c = chars[i];
			if(c == '+' || c == '-' || c == '*' || c == '%')
			{
				curOperator = c;
				Arrays.fill(curFraction, BigInteger.valueOf(-1)); // reset curFraction, which has arleady been processed
			}
			else if(c == '_') // indicates proper fraction
			{
				//this means the previous number we got as the numerator is actually part of an improper fraction
				tmpWholeNum = curFraction[0];
				curFraction[0] = BigInteger.valueOf(-1);
			}
			else if(48 <= c && c <= 57 ) // if it's a digit
			{
				// here we want to make sure we get the whole number and not just the first digit
				String fullNum = "" + c;
				while(i+1 < chars.length && 48 <= chars[i+1]  && chars[i+1] <= 57)
				{
					fullNum += chars[i+1];
					i++;
				}
				BigInteger number = new BigInteger(fullNum);
				if(curFraction[0].equals(BigInteger.valueOf(-1))) // haven't found a Numerator yet, numetator always comes before denominator
				{
					if(debug){System.out.println("Here setting numerator to: " + number);}
					curFraction[0] = number;
				}
				else
				{
					curFraction[1] = number; 
					if(debug){System.out.println("Denominator begin set. Current char is: " + c);}

					// we have come to a denominator, now we should check if we have an whole fraction and make it improper
					if(!tmpWholeNum.equals(BigInteger.valueOf(-1)))
					{
						curFraction[0] = curFraction[0].add(curFraction[1].multiply(tmpWholeNum));
					}

					// at this poBigInteger we hve a full improper fraction
					// if it has been given to us after an opoerator, we need to apply the operator on it
					if(curOperator == '0') // this is the first number, there is no operator yet
					{
						if(debug){System.out.println("Here processing the first fraction.");}
						partialResultFraction[0] = curFraction[0];
						partialResultFraction[1] = curFraction[1];
					}
					else
					{
						reduce(curFraction);
						doOperation(curOperator, partialResultFraction, curFraction);
					}
				}
			}
			// ignore all other characters
			i++;
		}
		return partialResultFraction[0] + "/" + partialResultFraction[1];
	}

	public static void reduce(BigInteger[] curFraction)
	{
		BigInteger gcd = curFraction[0].gcd(curFraction[1]);
		curFraction[0] = curFraction[0].divide(gcd);
		curFraction[1] = curFraction[1].divide(gcd);
	}

	public static void doOperation(char op, BigInteger[] partialResult, BigInteger[] curFraction)
	{
		if(debug){System.out.println("Here: " + partialResult[0] + "/" + partialResult[1] + op + curFraction[0] + "/" + curFraction[1]);}
		if(op == '+')
		{
			BigInteger lcd = lcd(partialResult[1], curFraction[1]);
			partialResult[0] = (lcd.divide(partialResult[1])).multiply(partialResult[0]).add((lcd.divide(curFraction[1])).multiply(curFraction[0])); 
			partialResult[1] = lcd;
		}
		else if(op == '-') // logic for subtract
		{
			BigInteger lcd = lcd(partialResult[1], curFraction[1]);
			partialResult[0] = (lcd.divide(partialResult[1])).multiply(partialResult[0]).subtract((lcd.divide(curFraction[1])).multiply(curFraction[0]));
			partialResult[1] = lcd;
		}
		else if(op == '*') // logic for mult
		{

			partialResult[1] = (partialResult[1]).multiply(curFraction[1]);
			partialResult[0] = (partialResult[0]).multiply(curFraction[0]);
		}
		else if(op == '%') // logic for division
		{
			partialResult[1] = (partialResult[1]).multiply(curFraction[0]);
			partialResult[0] = (partialResult[0]).multiply(curFraction[1]);

		}
		reduce(partialResult);
	}

	// lowest common multiple
	public static BigInteger lcd(BigInteger a, BigInteger b)
	{
		if(a == b)
		{
			return a;
		}
		BigInteger lcd = a.min(b);
		BigInteger max = a.max(b);
		BigInteger i = BigInteger.valueOf(2);
		while(true)
		{
			lcd = lcd.multiply(i);
			i.add(BigInteger.valueOf(1));
			if(lcd.compareTo(max) >= 0 && lcd.mod(max).equals(BigInteger.valueOf(0)))
			{
				return lcd;
			}
		}
		// at this point, lcd is divisible by max and its greater than max
	}
}
