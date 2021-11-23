package net.mvpmatch.vendingmachine.data.respository;

import net.mvpmatch.vendingmachine.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    public Optional<User> findByUserName(String userName);
}
