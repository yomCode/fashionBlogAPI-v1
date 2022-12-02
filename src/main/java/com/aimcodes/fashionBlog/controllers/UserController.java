package com.aimcodes.fashionBlog.controllers;


import com.aimcodes.fashionBlog.pojos.ApiResponse;
import com.aimcodes.fashionBlog.pojos.UserRequestDto;
import com.aimcodes.fashionBlog.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> newUser(@Valid @RequestBody UserRequestDto request){

        return userService.createUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody UserRequestDto request, HttpSession session){

        return userService.userLogin(request, session);
    }


    @GetMapping("/logout")
    public ResponseEntity<ApiResponse> user_logout(HttpSession session){
        return userService.logout(session);
    }
}
