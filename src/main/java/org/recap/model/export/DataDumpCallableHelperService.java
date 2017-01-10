package org.recap.model.export;

import org.recap.ReCAPConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.util.DateUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by premkb on 23/9/16.
 */
@Service
@Scope("prototype")
public class DataDumpCallableHelperService {

    public List<BibliographicEntity> getFullDataDumpRecords(int page, int size, DataDumpRequest dataDumpRequest
            , BibliographicDetailsRepository bibliographicDetailsRepository) {
        Page<BibliographicEntity> bibliographicEntities;
        bibliographicEntities = bibliographicDetailsRepository.getRecordsForFullDump(new PageRequest(page, size)
                    , dataDumpRequest.getCollectionGroupIds(), dataDumpRequest.getInstitutionCodes());
        return bibliographicEntities.getContent();
    }

    public List<BibliographicEntity> getIncrementalDataDumpRecords(int page, int size, DataDumpRequest dataDumpRequest, BibliographicDetailsRepository bibliographicDetailsRepository) {
        Date inputDate = DateUtil.getDateFromString(dataDumpRequest.getDate(), ReCAPConstants.DATE_FORMAT_YYYYMMDDHHMM);
        Page<BibliographicEntity> bibliographicEntities;
        bibliographicEntities = bibliographicDetailsRepository.getRecordsForIncrementalDump(new PageRequest(page, size)
                    , dataDumpRequest.getCollectionGroupIds(), dataDumpRequest.getInstitutionCodes(), inputDate);
        return bibliographicEntities.getContent();
    }

    public List<BibliographicEntity> getDeletedRecords(int page, int size, DataDumpRequest dataDumpRequest, BibliographicDetailsRepository bibliographicDetailsRepository) {
        Page<BibliographicEntity> bibliographicEntities;
        Date inputDate = DateUtil.getDateFromString(dataDumpRequest.getDate(), ReCAPConstants.DATE_FORMAT_YYYYMMDDHHMM);
        if(dataDumpRequest.getDate()==null){
            bibliographicEntities = bibliographicDetailsRepository.getDeletedRecordsForFullDump(new PageRequest(page, size)
                    , dataDumpRequest.getCollectionGroupIds(), dataDumpRequest.getInstitutionCodes());
        }else{
            bibliographicEntities = bibliographicDetailsRepository.getDeletedRecordsForIncrementalDump(new PageRequest(page, size)
                    , dataDumpRequest.getCollectionGroupIds(), dataDumpRequest.getInstitutionCodes(), inputDate);
        }
        return bibliographicEntities.getContent();
    }

}