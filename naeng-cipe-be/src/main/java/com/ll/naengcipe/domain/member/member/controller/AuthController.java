package com.ll.naengcipe.domain.member.member.controller;

import com.ll.naengcipe.domain.member.member.dto.JoinRequestDto;
import com.ll.naengcipe.domain.member.member.exception.PasswordNotMatchException;
import com.ll.naengcipe.domain.member.member.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<?> memberAdd(@Valid @RequestBody JoinRequestDto joinDto) {
        if (!joinDto.isPasswordCheck()) {
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.addMember(joinDto));
    }
}