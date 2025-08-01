package com.atuluttam.M2_Ecom.service;


import com.atuluttam.M2_Ecom.dto.AddressDTO;
import com.atuluttam.M2_Ecom.dto.UserRequest;
import com.atuluttam.M2_Ecom.dto.UserResponse;
import com.atuluttam.M2_Ecom.model.Address;
import com.atuluttam.M2_Ecom.model.User;
import com.atuluttam.M2_Ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
public class UserService {

    //@Autowired
    private final UserRepository userRepository;

    public List<UserResponse> fetchAlluser()
    {
      // List<User> userList = userRepository.findAll();
     return    userRepository.findAll().stream()
             .map(this::mapToUserResponse)
             .collect(Collectors.toList());
    }

    public void addUser(UserRequest userRequest)
    {

        User user = new User();
        updateUserFromRequest(user, userRequest);
            userRepository.save(user);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
    user.setFname(userRequest.getFname());
    user.setLname(userRequest.getLname());
    user.setEmail(userRequest.getEmail());
    user.setPhone(userRequest.getPhone());
    if(userRequest.getAddress()!=null)
    {
        Address address = new Address();
        address.setCity(userRequest.getAddress().getCity());
        address.setStreet(userRequest.getAddress().getStreet());
        address.setPincode(userRequest.getAddress().getPincode());
        address.setCountry(userRequest.getAddress().getCountry());
        user.setAddress(address);
    }

    }

    public Optional<UserResponse> fetchOneUser(Long id)
    {
       return userRepository.findById(id).map(this::mapToUserResponse);
    }

    public Boolean updateUser(Long id, UserRequest updateduserRequest) {

        return userRepository.findById(id)
                .map(existingUser ->{
                    updateUserFromRequest(existingUser, updateduserRequest);
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }

    private UserResponse mapToUserResponse(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setId(String.valueOf(user.getId()));
        userResponse.setFname(user.getFname());
        userResponse.setLname(user.getLname());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setRole(user.getRole());
        if(user.getAddress()!=null)
        {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setPincode(user.getAddress().getPincode());
            addressDTO.setStreet(user.getAddress().getStreet());
            userResponse.setAddressDTO(addressDTO);
        }
        return userResponse;
    }

}
