package com.ll.naengcipe.domain.member.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "이메일은 비워둘 수 없습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;
}
