package com.devops.qas.tests.recommendation.repository;

import com.devops.qas.tests.recommendation.domain.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByStudentId(Long studentId);
    List<Recommendation> findByStudentIdAndCategoryContainingIgnoreCase(Long studentId, String category);
    List<Recommendation> findByCategoryContainingIgnoreCase(String category);
}

