import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServletTest {

    private LoginServlet loginServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @Before
    public void setup() throws Exception {
        // Initialize the servlet
        loginServlet = new LoginServlet();
        
        // Mock the request and response objects
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        // Mock the PrintWriter to capture the response output
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        
        // Configure the mock response to return the mocked PrintWriter
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        // Simulate request parameters for a successful login
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("password123");

        // Call the servlet method
        loginServlet.doPost(request, response);

        // Verify that the content type was set to "text/html"
        verify(response).setContentType("text/html");

        // Verify that the status code was set to 200 OK
        verify(response).setStatus(HttpServletResponse.SC_OK);

        // Capture and check the output
        String result = stringWriter.toString();
        assertTrue(result.contains("Login Successful!"));
    }

    @Test
    public void testFailedLogin() throws Exception {
        // Simulate request parameters for a failed login
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("wrongpassword");

        // Call the servlet method
        loginServlet.doPost(request, response);

        // Verify that the content type was set to "text/html"
        verify(response).setContentType("text/html");

        // Verify that the status code was set to 401 Unauthorized
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Capture and check the output
        String result = stringWriter.toString();
        assertTrue(result.contains("Login Failed"));
    }

    @Test
    public void testNullUsername() throws Exception {
        // Simulate a null username
        when(request.getParameter("username")).thenReturn(null);
        when(request.getParameter("password")).thenReturn("password123");

        // Call the servlet method
        loginServlet.doPost(request, response);

        // Verify the response for unauthorized status due to null username
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testNullPassword() throws Exception {
        // Simulate a null password
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn(null);

        // Call the servlet method
        loginServlet.doPost(request, response);

        // Verify the response for unauthorized status due to null password
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testEmptyUsername() throws Exception {
        // Simulate an empty username
        when(request.getParameter("username")).thenReturn("");
        when(request.getParameter("password")).thenReturn("password123");

        // Call the servlet method
        loginServlet.doPost(request, response);

        // Verify the response for unauthorized status due to empty username
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testEmptyPassword() throws Exception {
        // Simulate an empty password
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("");

        // Call the servlet method
        loginServlet.doPost(request, response);

        // Verify the response for unauthorized status due to empty password
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
