package com.grocky.controller;

import com.grocky.dto.AuthDTO;
import com.grocky.dto.ResponseDTO;
import com.grocky.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<AuthDTO.AuthResponse>> register(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        return ResponseEntity.ok(ResponseDTO.success(authService.register(request), "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthDTO.AuthResponse>> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        return ResponseEntity.ok(ResponseDTO.success(authService.login(request), "Login successful"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseDTO<AuthDTO.AuthResponse>> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(ResponseDTO.success(authService.refreshToken(refreshToken), "Token refreshed successfully"));
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<ResponseDTO<Void>> logout(@PathVariable UUID userId) {
        authService.logout(userId);
        return ResponseEntity.ok(ResponseDTO.success(null, "Logout successful"));
    }
}
