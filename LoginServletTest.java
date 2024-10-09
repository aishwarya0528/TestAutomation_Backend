
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

import static org.junit.Assert.assertEquals;
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
    public void testValidCredentials() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("password123");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals("<html><body><h1>Login successful!</h1></body></html>", stringWriter.toString().trim());
    }

    @Test
    public void testInvalidCredentials() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("wronguser");
        when(request.getParameter("password")).thenReturn("wrongpassword");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertEquals("<html><body><h1>Login failed. Invalid username or password.</h1></body></html>", stringWriter.toString().trim());
    }

    @Test
    public void testConcurrentRequests() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final boolean validCredentials = i % 2 == 0;
            executorService.submit(() -> {
                try {
                    when(request.getParameter("username")).thenReturn(validCredentials ? "admin" : "wronguser");
                    when(request.getParameter("password")).thenReturn(validCredentials ? "password123" : "wrongpassword");

                    StringWriter stringWriter = new StringWriter();
                    PrintWriter writer = new PrintWriter(stringWriter);
                    when(response.getWriter()).thenReturn(writer);

                    loginServlet.doPost(request, response);

                    if (validCredentials) {
                        verify(response).setStatus(HttpServletResponse.SC_OK);
                        assertEquals("<html><body><h1>Login successful!</h1></body></html>", stringWriter.toString().trim());
                    } else {
                        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        assertEquals("<html><body><h1>Login failed. Invalid username or password.</h1></body></html>", stringWriter.toString().trim());
                    }
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

    @Test
    public void testEmptyCredentials() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertEquals("<html><body><h1>Login failed. Invalid username or password.</h1></body></html>", stringWriter.toString().trim());
    }
}
