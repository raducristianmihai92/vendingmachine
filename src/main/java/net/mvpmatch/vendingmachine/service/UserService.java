package net.mvpmatch.vendingmachine.service;

import net.mvpmatch.vendingmachine.data.entity.User;
import net.mvpmatch.vendingmachine.data.respository.UserRepository;
import net.mvpmatch.vendingmachine.web.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    public List<UserDTO> convertToDto(List<User> users) {
        return users.stream().map(item -> modelMapper.map(item,UserDTO.class)).collect(Collectors.toList());
    }

    public UserDTO convertToDto(User user) {
        return modelMapper.map(user,UserDTO.class);
    }

    public User convertToEntity(UserDTO user) {
        return modelMapper.map(user,User.class);
    }

}
