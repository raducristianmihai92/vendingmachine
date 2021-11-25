package net.mvpmatch.vendingmachine.web.controller;

import lombok.RequiredArgsConstructor;
import net.mvpmatch.vendingmachine.data.entity.User;
import net.mvpmatch.vendingmachine.data.enums.CentCoin;
import net.mvpmatch.vendingmachine.service.UserService;
import net.mvpmatch.vendingmachine.web.dto.CreateUpdateUserDTO;
import net.mvpmatch.vendingmachine.web.dto.DepositDTO;
import net.mvpmatch.vendingmachine.web.dto.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/users", produces = "application/json")
    public List<UserDTO> getAllUsers() {
        return userService.convertToDto(userService.findAll());
    }

    @GetMapping(value = "/users/id/{id}", produces = "application/json")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        Optional<User> user =  userService.findById(id);
        if(user.isPresent()){
            return new ResponseEntity<UserDTO>(userService.convertToDto(user.get()), HttpStatus.OK);
        }

        return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/users", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody CreateUpdateUserDTO user){

        String message = "";
        if (StringUtils.isEmpty(user.getUserName())  || StringUtils.isEmpty(user.getPassword())
                || StringUtils.isEmpty(user.getRole())) {
            message = "The user name, password or role is missing !";
            return new ResponseEntity<String>(message, HttpStatus.PARTIAL_CONTENT);
        }

        Optional<User> foundUser = userService.findByUserName(user.getUserName());
        if (foundUser.isPresent()) {
            message = "User with user name " + user.getUserName() + " already exists !";
            return new ResponseEntity<String>(message, HttpStatus.CONFLICT);
        }

        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setPassword(user.getPassword());
        newUser.setRole(user.getRole());
        newUser.setDeposit(user.getDeposit());

        userService.save(newUser);

        Optional<User> createdUser =  userService.findByUserName(user.getUserName());
        return new ResponseEntity<UserDTO>(userService.convertToDto(createdUser.get()), HttpStatus.CREATED);
    }

    @PutMapping(value = "/users", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> update(@RequestBody CreateUpdateUserDTO user) {

        String errorMessage = "";
        if (user.getId() == null) {
            errorMessage = "The user can not be updated because id is missing !";
            return new ResponseEntity<String>(errorMessage, HttpStatus.PARTIAL_CONTENT);
        }

        Optional<User> userFound = userService.findById(user.getId());
        if (!userFound.isPresent()) {
            errorMessage = "The user is not found! ";
            return new ResponseEntity<String>(errorMessage, HttpStatus.NOT_FOUND);
        }

        if(user.getUserName() != null) {
            Optional<User> userByUserName =  userService.findByUserName(user.getUserName());
            if(userByUserName.isPresent() && userByUserName.get().getId() != userFound.get().getId()) {
                errorMessage = "The user could not be updated because the user name is already taken!";
                return new ResponseEntity<String>(errorMessage, HttpStatus.CONFLICT);
            }

            userFound.get().setUserName(user.getUserName());
        }

        if(user.getPassword() != null) {
            userFound.get().setPassword(user.getPassword());
        }

        if(user.getRole() != null){
            userFound.get().setRole(user.getRole());
        }

        if(user.getDeposit() != null) {
            userFound.get().setDeposit(user.getDeposit());
        }

        userFound.get().setId(user.getId());
        userService.save(userFound.get());

        Optional<User> userUpdated = userService.findById(user.getId());

        if (userUpdated.isPresent()) {
            return new ResponseEntity<>(userService.convertToDto(userUpdated.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping(value = "/users/id/{userId}")
    public ResponseEntity<?> deleteById(@PathVariable Integer userId) {

        String message = "";
        Optional<User> user = userService.findById(userId);
        if (user.isPresent()) {
            userService.deleteById(userId);

            message = "The specified user with id  "+ userId + " was deleted with success !";
            return new ResponseEntity<String>(message, HttpStatus.OK);
        }

        message = "The specified user with id " + userId + " was not deleted. Reason: could not be found.";
        return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/deposit")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<?> deposit(@RequestBody DepositDTO depositDTO, Principal principal) {

        Optional<User> user = userService.findByUserName(principal.getName());

        if (!CentCoin.contains(depositDTO.getCoin())) {
            return new ResponseEntity<String>("The coin entered is not acceptable!", HttpStatus.BAD_REQUEST);
        }

        if(user.isPresent()) {
            if(user.get().getDeposit() == null) {
                user.get().setDeposit(depositDTO.getCoin());
            } else {
                user.get().setDeposit(user.get().getDeposit() + depositDTO.getCoin());
            }

            userService.save(user.get());
        }

        return new ResponseEntity<String>("The deposit is successfully into your account!", HttpStatus.OK);
    }

    @GetMapping(value = "/reset")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<?> resetDeposit(Principal principal) {

        String message = "";
        Optional<User> user = userService.findByUserName(principal.getName());
        if(!user.isPresent()) {
            message = "Your account could not be found !";
            return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
        }

        user.get().setDeposit(0);
        userService.save(user.get());
        message = "Your deposit was successfully reset!";
        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
}
