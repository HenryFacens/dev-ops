package ac2_project.example.ac2_ca.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ac2_project.example.ac2_ca.dto.UserDTO;
import ac2_project.example.ac2_ca.entity.User;
import ac2_project.example.ac2_ca.repository.User_Repository;
@Service
public class UserService {

    @Autowired
    private User_Repository userRepository;

    // Método para listar todos os usuários
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                    .map(UserDTO::fromEntity)
                    .collect(Collectors.toList());
    }

    // Outros métodos de lógica de negócio podem ser adicionados aqui
}