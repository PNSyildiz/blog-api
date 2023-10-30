package com.app.blog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody User credentials) throws JsonProcessingException {
        Optional<User> user = userService.signIn(credentials.getEmail(), credentials.getPassword());
        if(user.isPresent()){
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.get().getId());
            response.put("name", user.get().getName());
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(response);
            return ResponseEntity.ok(json);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id){
        return userRepository.findById(id).orElseThrow();
    }

    @PostMapping("/user")
    public User createUser(@RequestBody User user){
        return userRepository.save(user);
    }

    @PutMapping("/user/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user){
        User existingUser = userRepository.findById(id).orElseThrow();
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable Long id){
        userRepository.deleteById(id);
    }
}
