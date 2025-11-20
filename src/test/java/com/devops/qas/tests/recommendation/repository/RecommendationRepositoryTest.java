package com.devops.qas.tests.recommendation.repository;

import com.devops.qas.tests.recommendation.domain.entity.Recommendation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RecommendationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecommendationRepository repository;

    @Test
    void findByStudentId_ShouldReturnRecommendations() {
        Recommendation rec1 = Recommendation.builder()
                .studentId(1L)
                .courseName("Course 1")
                .category("Tech")
                .build();
        Recommendation rec2 = Recommendation.builder()
                .studentId(1L)
                .courseName("Course 2")
                .category("Tech")
                .build();
        entityManager.persist(rec1);
        entityManager.persist(rec2);
        entityManager.flush();

        List<Recommendation> found = repository.findByStudentId(1L);

        assertThat(found).hasSize(2);
        assertThat(found).extracting(Recommendation::getCourseName)
                .containsExactlyInAnyOrder("Course 1", "Course 2");
    }

    @Test
    void findByStudentIdAndCategoryContainingIgnoreCase_ShouldReturnFiltered() {
        Recommendation rec1 = Recommendation.builder()
                .studentId(1L)
                .courseName("DevOps Basics")
                .category("Technology")
                .build();
        Recommendation rec2 = Recommendation.builder()
                .studentId(1L)
                .courseName("Management 101")
                .category("Business")
                .build();
        entityManager.persist(rec1);
        entityManager.persist(rec2);
        entityManager.flush();

        List<Recommendation> found = repository.findByStudentIdAndCategoryContainingIgnoreCase(1L, "tech");

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getCourseName()).isEqualTo("DevOps Basics");
    }
}

