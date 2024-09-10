package com.TaiNguyen.AuthenticationService.repository;

import com.TaiNguyen.AuthenticationService.Modal.UserModal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModal, Long> {
    UserModal findByEmail(String email);
}
