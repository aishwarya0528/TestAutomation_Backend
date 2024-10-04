Here's the JUnit test code for the LoginServlet based on the provided test cases:

```java
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

public class LoginServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private LoginServlet loginServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loginServlet = new LoginServlet();
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("password123");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(stringWriter.toString().contains("Welcome, admin!"));
    }

    @Test
    public void testFailedLogin() throws Exception {
        when(request.getParameter("username")).thenReturn("invaliduser");
        when(request.getParameter("password")).thenReturn("invalidpass");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(stringWriter.toString().contains("Invalid username or password"));
    }

    @Test
    public void testNullCredentials() throws Exception {
        when(request.getParameter("username")).thenReturn(null);
        when(request.getParameter("password")).thenReturn(null);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(stringWriter.toString().contains("Invalid username or password"));
    }

    @Test
    public void testEmptyCredentials() throws Exception {
        when(request.getParameter("username")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(stringWriter.toString().contains("Invalid username or password"));
    }

    @Test
    public void testConcurrentRequests() throws Exception {
        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    when(request.getParameter("username")).thenReturn("admin");
                    when(request.getParameter("password")).thenReturn("password123");
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter writer = new PrintWriter(stringWriter);
                    when(response.getWriter()).thenReturn(writer);

                    loginServlet.doPost(request, response);

                    verify(response).setStatus(HttpServletResponse.SC_OK);
                    assertTrue(stringWriter.toString().contains("Welcome, admin!"));
                } catch (Exception e) {
                    fail("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }
}
```

This test class includes all the specified test cases for the LoginServlet. It uses Mockito for mocking the HttpServletRequest and HttpServletResponse objects, and JUnit for assertions and test execution. The concurrent requests test case uses an ExecutorService to simulate multiple threads accessing the servlet simultaneously.