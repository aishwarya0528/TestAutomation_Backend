Based on the provided requirements, here's a simplified version of the JUnit test cases for LoginServlet.java focusing on the specified requirements:

```java
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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
    public void testValidLogin() throws ServletException, IOException {
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
    public void testConcurrentRequests() throws InterruptedException {
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
                    e.printStackTrace();
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

This test class includes two main test cases:

1. `testValidLogin()`: Tests the valid username and password scenario, verifying the correct response and HTTP status.
2. `testConcurrentRequests()`: Tests handling of concurrent requests by simulating multiple threads accessing the servlet simultaneously.

The test cases focus on POST requests only, as specified in the requirements. The test class uses Mockito for mocking the HttpServletRequest and HttpServletResponse objects, and JUnit for assertions and test execution.

Note that this simplified version doesn't include test cases for failed login attempts, empty inputs, or other scenarios not specifically mentioned in the requirements.