package ac2_project.example.ac2_ca.dto;

import ac2_project.example.ac2_ca.entity.User;

public class UserDTO {

    private Long id;
    private String username;
    private String email;

    // Construtor vazio
    public UserDTO() {}

    // Construtor com todos os parâmetros
    public UserDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Método de conversão de User para UserDTO
    public static UserDTO fromEntity(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());

        // Certifique-se de que o método getEmailAddress() existe na classe User_Email
        if (user.getEmail() != null) {
            userDTO.setEmail(user.getEmail().getEmailAddress());
        }

        return userDTO;
    }
}
