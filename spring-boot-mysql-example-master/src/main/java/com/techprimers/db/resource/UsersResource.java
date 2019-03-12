package com.techprimers.db.resource;

import com.techprimers.db.dao.UsersDao;
import com.techprimers.db.model.Users;
import com.techprimers.db.repository.UsersRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UsersResource {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersDao usersDao;




    private static final Logger log = Logger.getLogger(UsersResource.class);


    @GetMapping(value = "/users")
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @PostMapping(value = "/user")
    @CacheEvict(value = "users", allEntries = true)
    public List<Users> persist(@RequestBody final Users user) {
        log.info(user.getId());
        System.out.println(user.getName());
        usersRepository.save(user);
        clear();
        return usersRepository.findAll();
    }

    @GetMapping(value = "/user")
    public List<Users> findByName(@RequestParam String name) {
        return usersRepository.findByName(name);
    }

    @GetMapping(value = "/user/{id}")
    public Users findById(@PathVariable int id) {
        return usersDao.getUserFromId(id);
    }

    @GetMapping(value = "/clear")
    @CacheEvict(value = "users", allEntries = true)
    public void clear() {
    }




}
