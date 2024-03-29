package com.ll.naengcipe.domain.member.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.naengcipe.domain.fridge.fridge.entity.Fridge;
import com.ll.naengcipe.domain.fridge.fridge.service.FridgeService;
import com.ll.naengcipe.domain.member.member.dto.MemberDto;
import com.ll.naengcipe.domain.member.member.dto.MemberModifyRequestDto;
import com.ll.naengcipe.domain.member.member.dto.MemberModifyResponseDto;
import com.ll.naengcipe.domain.member.member.dto.MemberResponseDto;
import com.ll.naengcipe.domain.member.member.dto.MemberResponseDto;
import com.ll.naengcipe.domain.member.member.dto.MyFridgeResponseDto;
import com.ll.naengcipe.domain.member.member.entity.Member;
import com.ll.naengcipe.domain.member.member.service.MemberService;
import com.ll.naengcipe.global.security.authentiation.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
	private final MemberService memberService;
	private final FridgeService fridgeService;

	@GetMapping("/{memberId}/fridge")
	public ResponseEntity<MyFridgeResponseDto> myFridge(@AuthenticationPrincipal UserPrincipal user,
		@PathVariable String memberId) {
		if (user == null || user.getUsername().isEmpty()) {
			throw new AccessDeniedException("로그인이 필요한 서비스입니다.");
		}

		MemberResponseDto memberResponseDto = memberService.findMember(user.getUsername());
		Fridge fridgeEntity = fridgeService.findByUserId(memberResponseDto.getId());

		return ResponseEntity.ok(memberService.findMyFridge(fridgeEntity));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{memberId}")
	public ResponseEntity<MemberDto> memberDetails(@AuthenticationPrincipal UserPrincipal user) {
		Member member = user.getMember();
		return ResponseEntity.ok(new MemberDto(member));
	}

	@PreAuthorize("isAuthenticated()")
	@PatchMapping("/{memberId}")
	public ResponseEntity<MemberModifyResponseDto> memberModify(@AuthenticationPrincipal UserPrincipal user,
		@RequestBody MemberModifyRequestDto memberModifyRequestDto) {
		return ResponseEntity.ok(memberService.modifyMember(user, memberModifyRequestDto));
	}
}
