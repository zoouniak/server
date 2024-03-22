package com.example.cns.company.domain.repository;

import com.example.cns.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findAllByNameContains(String keyword);

    Optional<Company> findByName(String companyName);
}
