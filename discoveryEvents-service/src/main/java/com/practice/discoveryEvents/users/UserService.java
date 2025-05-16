package com.practice.discoveryEvents.users;



import java.util.List;

public interface UserService {

     User createUser(User user);
     void deleteUser(int userId);
     List<User> getAllUsers(List<Integer> ids,int from, int size);

     //for Event class
     User getUserById(int userId);


}
