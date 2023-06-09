package com.polus.core.auditreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.polus.core.auditreport.pojo.AuditReportType;

@Repository
public interface AuditReportTypeRepository extends JpaRepository<AuditReportType, Integer> {

    /**
     * This method is to get AuditReportType by Report Type
     *
     * @param reportType
     * @return AuditReportType
     */
    AuditReportType findAuditReportTypeByReportType(String reportType);
}
