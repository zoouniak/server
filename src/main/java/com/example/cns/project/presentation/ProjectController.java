package com.example.cns.project.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProjectController {

    /*
    프로젝트 생성
     */
    @PostMapping("/project")
    public ResponseEntity createProject(){
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 수정
     */
    @PatchMapping("/project")
    public ResponseEntity patchProject(){
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 삭제
     */
    @DeleteMapping("/project")
    public ResponseEntity deleteProject(){
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 나가기
     */
    @DeleteMapping("/project/exit")
    public ResponseEntity exitProject(){
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 전체 목록 조회
     */
    @GetMapping("/project/list")
    public ResponseEntity getAllProject(){
        return ResponseEntity.ok().build();
    }

    /*
    특정 프로젝트 조회
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity getSpecificProject(@PathVariable Long projectId){
        return ResponseEntity.ok().build();
    }
}
