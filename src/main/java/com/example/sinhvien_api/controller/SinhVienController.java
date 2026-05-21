package com.example.sinhvien_api.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sinhvien_api.model.SinhVien;
import com.example.sinhvien_api.model.User;
import com.example.sinhvien_api.repository.UserRepository;
import com.example.sinhvien_api.service.SinhVienService;

import jakarta.validation.Valid;

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
    public ResponseEntity<?> delete(@PathVariable Integer id, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body(error("Chưa đăng nhập"));
            }
            var me = userRepo.findByEmail(principal.getName()).orElse(null);
            if (me == null) return ResponseEntity.status(401).body(error("Người dùng không tồn tại"));

            // Admin có quyền xóa mọi bản ghi
            if (me.getRole() != null && me.getRole().name().equals("ADMIN")) {
                service.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Xóa sinh viên thành công"));
            }

            // Người tạo có thể xóa bản ghi do mình tạo
            var opt = service.findById(id);
            if (opt.isEmpty()) return ResponseEntity.status(404).body(error("Không tìm thấy sinh viên ID: " + id));
            var sv = opt.get();
            var creator = sv.getCreatedBy();
            if (creator != null) {
                if ((creator.getEmail() != null && creator.getEmail().equalsIgnoreCase(me.getEmail()))
                        || (creator.getUsername() != null && creator.getUsername().equalsIgnoreCase(me.getUsername()))) {
                    service.deleteById(id);
                    return ResponseEntity.ok(Map.of("message", "Xóa sinh viên thành công"));
                }
            }

            return ResponseEntity.status(403).body(error("Không có quyền xóa bản ghi này"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(error(e.getMessage()));
        }
    }

    private Map<String, String> error(String msg) { return Map.of("error", msg); }
}