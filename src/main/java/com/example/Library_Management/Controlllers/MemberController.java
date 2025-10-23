package com.example.Library_Management.Controlllers;

import com.example.Library_Management.Dtos.MemberDto;
import com.example.Library_Management.services.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping
    public List<MemberDto> getAllMembers(){
        return memberService.getAllMembers();
    }

    @GetMapping("/{id}")
    public MemberDto getMember(@PathVariable Long id){
        return memberService.getMember(id);
    }

    @PostMapping
    public ResponseEntity<MemberDto> creatMember(@Valid  @RequestBody MemberDto dto){
        return new ResponseEntity<>( memberService.createMember(dto),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public MemberDto updateMember(@PathVariable Long id,@Valid @RequestBody MemberDto dto){
        return memberService.updateMember(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delteMember(@PathVariable Long id){
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();

    }
}
