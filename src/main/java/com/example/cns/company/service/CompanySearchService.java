package com.example.cns.company.service;

import com.example.cns.auth.dto.response.CompanyEmailResponse;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.company.domain.Company;
import com.example.cns.company.domain.repository.CompanyRepository;
import com.example.cns.company.dto.CompanySearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanySearchService {
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<CompanySearchResponse> searchCompanyByName(String keyword) {
        List<Company> list = companyRepository.findAllByNameContains(keyword);
        return list.stream().map(company -> new CompanySearchResponse(company.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyEmailResponse getCompanyEmail(String companyName) {
        Company findCompany = findByCompanyName(companyName);
        return new CompanyEmailResponse(findCompany.getEmail());
    }

    @Transactional(readOnly = true)
    public Company findByCompanyName(String companyName) {
        return companyRepository.findByName(companyName)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COMPANY_NOT_EXIST));
    }
}
