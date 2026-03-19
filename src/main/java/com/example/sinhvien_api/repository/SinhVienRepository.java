package com.example.sinhvien_api.repository;

import com.example.sinhvien_api.model.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SinhVienRepository extends JpaRepository<SinhVien, Integer> {

    Optional<SinhVien> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    @Query("SELECT sv FROM SinhVien sv WHERE " +
           "LOWER(sv.hoTen) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(sv.email) LIKE LOWER(CONCAT('%',:kw,'%'))")
    List<SinhVien> search(@Param("kw") String keyword);

    long countByCreatedById(Integer userId);
}