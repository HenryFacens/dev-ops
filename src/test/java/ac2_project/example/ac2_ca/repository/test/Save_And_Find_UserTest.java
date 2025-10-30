package ac2_project.example.ac2_ca.repository.test;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

import ac2_project.example.ac2_ca.entity.User;
import ac2_project.example.ac2_ca.repository.User_Repository;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class Save_And_Find_UserTest {
	  @Autowired
	    private User_Repository userRepository;

	    @Test
	    public void testSaveAndFindUser() {
	        // Cria um novo usuário
	        User user = new User();
	        user.setUsername("testUser1");

	        // Salva no banco de dados
	        User savedUser = userRepository.save(user);
	        assertNotNull(savedUser.getId());
	        // Busca o usuário pelo ID
	        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
	        assertThat(retrievedUser).isPresent();
	        assertThat(retrievedUser.get().getUsername()).isEqualTo("testUser1");
	    }

		

}
