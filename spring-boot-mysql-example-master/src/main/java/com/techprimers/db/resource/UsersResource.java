package com.techprimers.db.resource;

import com.techprimers.db.dao.UsersDao;
import com.techprimers.db.model.Users;
import com.techprimers.db.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/users")
public class UsersResource {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersDao usersDao;


    @GetMapping(value = "/all")
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @PostMapping(value = "/load")
    public List<Users> persist(@RequestBody final Users users) {
        usersRepository.save(users);
        return usersRepository.findAll();
    }

    @GetMapping(value = "/one")
    public Users findByName() {
        return usersRepository.findByName("James");
    }

    @GetMapping(value = "/id")
    public Users findById() {
        return usersDao.getUserFromId(1);
    }



}
