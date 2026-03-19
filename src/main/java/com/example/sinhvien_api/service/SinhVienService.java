package com.example.sinhvien_api.service;

import com.example.sinhvien_api.model.SinhVien;
import com.example.sinhvien_api.repository.SinhVienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class SinhVienService {

    private final SinhVienRepository repo;

    public SinhVienService(SinhVienRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<SinhVien> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SinhVien> findById(Integer id) {
        return repo.findById(id);
    }

    public SinhVien save(SinhVien sv) {
        if (repo.findByEmail(sv.getEmail()).isPresent())
            throw new IllegalArgumentException("Email '" + sv.getEmail() + "' đã tồn tại");
        return repo.save(sv);
    }

    public SinhVien update(Integer id, SinhVien sv) {
        SinhVien existing = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sinh viên ID: " + id));

        if (repo.existsByEmailAndIdNot(sv.getEmail(), id))
            throw new IllegalArgumentException("Email '" + sv.getEmail() + "' đã tồn tại");

        existing.setHoTen(sv.getHoTen());
        existing.setEmail(sv.getEmail());
        return repo.save(existing);
    }

    public void deleteById(Integer id) {
        if (!repo.existsById(id))
            throw new NoSuchElementException("Không tìm thấy sinh viên ID: " + id);
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SinhVien> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return repo.findAll();
        return repo.search(keyword.trim());
    }
}