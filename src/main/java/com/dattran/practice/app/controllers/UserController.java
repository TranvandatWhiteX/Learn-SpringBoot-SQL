package com.dattran.practice.app.controllers;

import com.dattran.practice.app.dtos.UserDto;
import com.dattran.practice.domain.entities.User;
import com.dattran.practice.domain.services.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {
    UserService userService;

    @PostMapping
    public ResponseEntity<Long> createUser(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(userService.createUserProcedure2(userDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserByIdFunc(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
