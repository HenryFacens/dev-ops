package ac2_project.example.ac2_ca.entity.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ac2_project.example.ac2_ca.entity.User;
import ac2_project.example.ac2_ca.entity.User_Email;

public class UserTest {
	
	 @Test
	    void testSetAndGetValidEmail() {
	        User user = new User();
	        User_Email email = new User_Email("test@example.com");
	        user.setEmail(email);
	        
	        assertEquals(email, user.getEmail());
	    }

	    @Test
	    void testInvalidEmailThrowsException() {
	        assertThrows(IllegalArgumentException.class, () -> {
	            new User_Email("invalid-email");
	        });
	    }
	
	
	
	
	
	

}
