package org.recap.repository;

import org.recap.model.jpa.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by SheikS on 8/8/2016.
 */
public interface ReportDetailRepository extends JpaRepository<ReportEntity, Integer> {

    List<ReportEntity> findByFileName(String fileName);

    List<ReportEntity> findByFileNameAndType(String fileName, String type);

    List<ReportEntity> findByFileNameAndInstitutionName(String fileName, String institutionName);

    List<ReportEntity> findByFileNameAndInstitutionNameAndType(String fileName, String institutionName, String type);

    @Query(value = "select * from report_t where FILE_NAME=?1 and CREATED_DATE >= ?2 and CREATED_DATE <= ?3",  nativeQuery = true)
    List<ReportEntity> findByFileAndDateRange(String fileName, Date from, Date to);

    @Query(value = "select * from report_t where TYPE=?1 and CREATED_DATE >= ?2 and CREATED_DATE <= ?3", nativeQuery = true)
    List<ReportEntity> findByTypeAndDateRange(String type, Date from, Date to);

    @Query(value = "select * from report_t where FILE_NAME=?1 and TYPE=?2 and CREATED_DATE >= ?3 and CREATED_DATE <= ?4", nativeQuery = true)
    List<ReportEntity> findByFileAndTypeAndDateRange(String fileName, String type, Date from, Date to);

    @Query(value = "select * from report_t where FILE_NAME=?1 and INSTITUTION_NAME=?2 and TYPE=?3 and CREATED_DATE >= ?4 and CREATED_DATE <= ?5", nativeQuery = true)
    List<ReportEntity> findByFileAndInstitutionAndTypeAndDateRange(String fileName, String institutionName, String type, Date from, Date to);

    @Query(value = "select * from report_t where INSTITUTION_NAME=?1 and TYPE=?2 and CREATED_DATE >= ?3 and CREATED_DATE <= ?4", nativeQuery = true)
    List<ReportEntity> findByInstitutionAndTypeAndDateRange(String institutionName, String type, Date from, Date to);
}
