package com.polus.core.adminportal.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.polus.core.adminportal.dto.SponsorHierarchyDto;
import com.polus.core.common.dto.ResponseData;
import com.polus.core.vo.SearchResult;

/**
 * SponsorHierarchy Service
 *
 * @createdBy Ajin
 * @date 24 Nov 2021
 */

@Service
public interface SponsorHierarchyService {

    /**
     * Create Sponsor Hierarchy
     *
     * @param sponsorHierarchy
     * @return
     */
    ResponseEntity<ResponseData> createSponsorHierarchy(SponsorHierarchyDto sponsorHierarchy);

    /**
     * Get Sponsor Hierarchy either by root sponsor hierarchy or by group name or sponsor name
     *
     * @param searchResult
     * @return
     */
    ResponseEntity<ResponseData> getSponsorHierarchy(SearchResult searchResult);

    /**
     * Update Sponsor Hierarchy
     *
     * @param sponsorHierarchyList
     * @return
     */
    ResponseEntity<ResponseData> updateSponsorHierarchy(SponsorHierarchyDto sponsorHierarchyList);

    /**
     * Delete Sponsor Hierarchy
     *
     * @param sponsorGroupId
     * @return
     */
    ResponseEntity<ResponseData> deleteSponsorHierarchy(Integer sponsorGroupId);

    /**
      * Get All Sponsors
      *
      * @param rootGroupId
      * @param voObj
      * @return
      */
    ResponseEntity<Object> getSponsors(Map<String, String> voObj);

    /**
     * Get Sponsor Hierarchies
     *
     * @return
     */
    ResponseEntity<ResponseData> getSponsorHierarchies();

    /**
     * Get All Sponsor hierarchy groups
     *
     * @param searchWord
     * @return
     */
    ResponseEntity<Object> getAllSponsorHierarchyGroups(String searchWord);
}
