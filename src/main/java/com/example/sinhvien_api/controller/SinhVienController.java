package com.example.sinhvien_api.controller;

import com.example.sinhvien_api.model.SinhVien;
import com.example.sinhvien_api.model.User;
import com.example.sinhvien_api.repository.UserRepository;
import com.example.sinhvien_api.service.SinhVienService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/sinhvien")
@CrossOrigin(origins = "*")
public class SinhVienController {

    private final SinhVienService service;
    private final UserRepository userRepo;

    public SinhVienController(SinhVienService service, UserRepository userRepo) {
        this.service  = service;
        this.userRepo = userRepo;
    }

    @GetMapping
    public ResponseEntity<List<SinhVien>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return service.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(error("Không tìm thấy sinh viên ID: " + id)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SinhVien>> search(
            @RequestParam(defaultValue = "") String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SinhVien sv, Principal principal) {
        try {
            User creator = userRepo.findByEmail(principal.getName()).orElse(null);
            sv.setCreatedBy(creator);
            return ResponseEntity.status(201).body(service.save(sv));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Valid @RequestBody SinhVien sv) {
        try {
            return ResponseEntity.ok(service.update(id, sv));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            service.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Xóa sinh viên thành công"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(error(e.getMessage()));
        }
    }

    private Map<String, String> error(String msg) { return Map.of("error", msg); }
}