package ac2_project.example.ac2_ca.controller;

import ac2_project.example.ac2_ca.dto.UserDTO;
import ac2_project.example.ac2_ca.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getAllUsers();  // Chama o serviço para obter os dados
    }
}