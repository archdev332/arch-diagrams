package com.cdc.demo;

import com.cdc.demo.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public List<User> getAllUsers() {
    return (List<User>) userRepository.findAll();
  }

  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  public Optional<User> getUserByNameAndEmail(String name, String email) {
    return userRepository.findByNameAndEmail(name, email);
  }

  @Transactional
  public User createUser(User user) {
    user.setNew(true);
    return userRepository.save(user);
  }

  @Transactional
  public List<User> createUsers(List<User> users) {
    users.forEach(user -> user.setNew(true));
    return userRepository.insertAllUnique(users);
  }

  @Transactional
  public Optional<User> updateUser(Long id, User userDetails) {
    return userRepository.findById(id)
        .map(existingUser -> {
          existingUser.setName(userDetails.getName());
          existingUser.setEmail(userDetails.getEmail());
          return userRepository.save(existingUser);
        });
  }

  @Transactional
  public boolean deleteUser(Long id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
