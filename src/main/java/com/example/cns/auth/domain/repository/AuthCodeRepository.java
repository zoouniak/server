package com.example.cns.auth.domain.repository;

import com.example.cns.auth.domain.AuthCode;
import org.springframework.data.repository.CrudRepository;

public interface AuthCodeRepository extends CrudRepository<AuthCode, String> {
}
