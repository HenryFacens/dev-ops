package ac2_project.example.ac2_ca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ac2_project.example.ac2_ca.entity.User;


@Repository
public interface User_Repository extends JpaRepository<User, Long> {
}
