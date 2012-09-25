import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.*;

public class TestFractionCalculator {

  private static Process childP;	
  private static ProcessBuilder ps;
  private static BufferedReader fromChild;
  private static BufferedWriter toChild;
  private final int DELAY_FOR_PROCESSING = 500;
  private final boolean DEBUG = false;

  @Before
  public void setUp() throws Exception 
  {
    ps = new ProcessBuilder("java","-cp",System.getProperty("java.class.path"),"FractionCalculator");
    if(DEBUG){ System.out.println("Now running: java -cp " + System.getProperty("java.class.path") + " FractionCalculator"); }

    ps.redirectErrorStream(true);
    childP = ps.start();
    fromChild = new BufferedReader(new InputStreamReader(childP.getInputStream()));
    toChild = new BufferedWriter(new OutputStreamWriter(childP.getOutputStream()));
  }

  private void sleepForProcessing()
  {
	try{
		Thread.sleep(DELAY_FOR_PROCESSING);
	}
	catch(Exception e){ fail("There was an error in sleep for processsing"); }
  }

  private void sleepForProcessing(int delay)
  {
	try{
		Thread.sleep(delay);
	}
	catch(Exception e){ fail("There was an error in sleep for processsing"); }
  }


  private void writeToProgram(String data)
  {    
    boolean error = false;
    try
    {
	if(DEBUG){ System.out.println("Writing '" + data + "' to program"); }
    	toChild.write(data);
	toChild.flush();
    }
    catch(Exception e){ if(DEBUG){ System.out.println(e); } fail("There was an error writing data to the program"); }
  }

  private void readAllFromProgram()
  {
	if(DEBUG){ System.out.println("Reading all from program. Getting this back:'"); }
	try{
		while(fromChild.ready())
		{
			char c = (char)fromChild.read();
			if(DEBUG){ System.out.print(c); }
		}
	}
	catch(Exception e){ fail("There was an error reading from the program"); }
	if(DEBUG){ System.out.print("'"); }
  }

  private String readFirstLine()
  {
	String line = "";
	try
	{
		char c = 'a';
		while(fromChild.ready() && c != '\n')
		{
			c = (char)fromChild.read();
			if(DEBUG){ System.out.println(c); }
			line += c;
		}
	}
	catch(Exception e){ fail("There was an error reading from the program."); }
	if(DEBUG){ System.out.println("Reading '" + line + "' from program"); } 
	return line;

  }

  private String readLastLine()
  {
	String line = "";
	try
	{
		while(fromChild.ready())
		{
			char c = (char)fromChild.read();
			if(c == '\n')
			{
				line = "";
			}
			line += c;
		}
	}
	catch(Exception e){ fail("There was an error reading from the program."); }
	if(DEBUG){ System.out.println("Reading '" + line + "' from program"); } 
	return line;
  }

  private void testInAndOut(String input, String expectedOutput)
  {
	sleepForProcessing();
	readAllFromProgram();
	sleepForProcessing();
	if(DEBUG){ System.out.println("111: about to write: " + input);}
	writeToProgram(input);
	/*
	try
	{
		sleepForProcessing();
	}
	catch(Exception e){;}
	*/
	sleepForProcessing();
	assertRunStatus(true);
	sleepForProcessing();
	assertEquals(expectedOutput, readFirstLine());
	if(DEBUG){ System.out.println("Expected: " + expectedOutput); }
  }

  private void assertRunStatus(boolean isExpectedToBeRunning)
  {
    boolean isRunning = false;
    try
    {
    	childP.exitValue();
    }
    catch(IllegalThreadStateException e)
    {
	isRunning = true;
    }
    String message = "Expected the program to be ";
    if(isExpectedToBeRunning)
    {
	message += "running but it was terminated.";
    }
    else
    {
	message += "terminated but it was running.";
    }
    assertEquals(message, isExpectedToBeRunning, isRunning);
  }

  private void checkExitValue(boolean expectedToBeRunning, int expectedExitVal, String errorMessage)
  {
    boolean isRunning = false;
    int exitVal = 0;
    try
    {
    	exitVal = childP.exitValue();
    }
    catch(IllegalThreadStateException e)
    {
	isRunning = true;
    }
    assertEquals("Program wasn't expected to terminate but it did.", expectedToBeRunning, isRunning);
    if(!expectedToBeRunning)
    {
      assertEquals(expectedExitVal, exitVal);
    }
  }

  @Test
  public void testBasicAddition() 
  {
    testInAndOut("1/2 + 1/4\n", "3/4\n");
  }

  @Test
  public synchronized void testBasicSubtraction() 
  {
    testInAndOut("1/900 - 1/1800\n", "1/1800\n");
  }

  @Test
  public synchronized void testBasicMultiplication() 
  {
    testInAndOut("3/4 * 4/4\n", "3/4\n");
  }

  @Test
  public synchronized void testBasicDivision() 
  {
    testInAndOut("1/2 % 1/2\n", "1/1\n");
  }

  @Test
  public synchronized void testExit() 
  {
    writeToProgram("exit\n");
    sleepForProcessing();
    assertRunStatus(false);
  }

  @After
  public synchronized void cleanUp() throws Exception
  {
    toChild.close();
    fromChild.close();
    childP.destroy();
  }

}
