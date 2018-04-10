package com.security.controller;

import com.security.model.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chris on 2018/4/9.
 */
@RestController
@RequestMapping(value="/user")
public class UserController {

    @GetMapping
    public List<User> user(@Valid @RequestParam(name="username" , required = false, defaultValue = "tom") String username) {
        System.out.println(username);
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        users.add(new User());
        return users;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        // throw new UserException(id);
        return new User();
    }

    @PostMapping
    public User user(@Valid @RequestBody User user) {
        /*System.out.println(errors.hasErrors());
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach(error -> System.out.println(error.getDefaultMessage()));
        }*/
        System.out.println(user);
        return user;
    }

    @PostMapping("/upload")
    public String upload(@RequestBody MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getName());
        System.out.println(file.getSize());
        File newFile = new File("d://", new Date().getTime()+ ".txt");
        file.transferTo(newFile);
        return newFile.getAbsolutePath();
    }
}
