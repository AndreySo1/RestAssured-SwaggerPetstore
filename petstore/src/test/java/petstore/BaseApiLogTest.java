package petstore;

import java.io.PrintStream;
import java.io.StringWriter;

import org.apache.commons.io.output.WriterOutputStream;
import org.testng.annotations.BeforeTest;

public class BaseApiLogTest {
   public static StringWriter requestWriter;
   public static PrintStream requestCapture;

   public static StringWriter responseWriter;
   public static PrintStream responseCapture;

   public static StringWriter errorWriter;
   public static PrintStream errorCapture;

   
   @BeforeTest
   public void beforeTest(){
       requestWriter = new StringWriter();
       requestCapture = new PrintStream(new WriterOutputStream(requestWriter), true);

       responseWriter = new StringWriter();
       responseCapture = new PrintStream(new WriterOutputStream(responseWriter), true);

       errorWriter = new StringWriter();
       errorCapture = new PrintStream(new WriterOutputStream(errorWriter), true);
   }
}
