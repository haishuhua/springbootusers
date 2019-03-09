package com.techprimers.db.resource;

import com.techprimers.db.dao.UsersDao;
import com.techprimers.db.model.Users;
import com.techprimers.db.repository.UsersRepository;
import com.techprimers.db.services.WeatherService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import com.techprimers.db.services.MailService;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/users")
public class UsersResource {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersDao usersDao;

    @Autowired
    WeatherService weatherService;

    @Autowired
    MailService mailService;

    private static final Logger log = Logger.getLogger(UsersResource.class);


    @GetMapping(value = "/all")
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @PostMapping(value = "/load")
    @CacheEvict(value = "users", allEntries = true)
    public List<Users> persist(@RequestBody final Users users) {
        log.info(users.getId());
        System.out.println(users.getName());
        usersRepository.save(users);
        clear();
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

    @GetMapping(value = "/clear")
    @CacheEvict(value = "users", allEntries = true)
    public void clear() {
    }


    @GetMapping(value = "/weather/{zipCode}")
    public String weather(@PathVariable String zipCode) throws IOException {
       return weatherService.getWeatherByZip(zipCode);
    }

    @GetMapping(value = "/weather/{zipCode}/sendemail/{mailto}/")
    public String sendEmail(@PathVariable String mailto,@PathVariable String zipCode) throws MessagingException {
        String weather = null;
        try {
            weather = weatherService.getWeatherByZip(zipCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mailService.sendmail(mailto,weather);

        return "Email sent successfully to: " +mailto;
    }

}
