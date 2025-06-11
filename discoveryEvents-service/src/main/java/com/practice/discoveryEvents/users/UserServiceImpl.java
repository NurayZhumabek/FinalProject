package com.practice.discoveryEvents.users;


import com.practice.discoveryEvents.util.AlreadyExistsException;
import com.practice.discoveryEvents.util.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new AlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getAllUsers(List<Integer> ids, int from, int size) {

        Pageable pageable = PageRequest.of((from / size), size, Sort.by(Sort.Direction.ASC, "id"));

        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).getContent();
        }

        Specification<User> spec = UserSpecifications.byIds(ids);
        return userRepository.findAll(spec, pageable).getContent();
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
