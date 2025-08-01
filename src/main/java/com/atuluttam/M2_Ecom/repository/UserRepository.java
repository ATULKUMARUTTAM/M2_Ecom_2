package com.atuluttam.M2_Ecom.repository;

import com.atuluttam.M2_Ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


}
