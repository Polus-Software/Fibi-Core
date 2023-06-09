package com.polus.core.rolodex.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.pojo.Country;
import com.polus.core.rolodex.vo.RolodexSearchResult;
import com.polus.core.rolodex.vo.RolodexVO;
import com.polus.core.vo.CommonVO;

@Transactional
@Service
public interface RolodexService {

	/**
	 * This method is used to search the rolodex details
	 * @param vo
	 * @return list of rolodex details based on search string
	 */
	public List<RolodexSearchResult> findRolodex(CommonVO vo);

	/**
	 * This method is used to get rolodex details by id
	 * @param vo
	 * @return object with rolodex details
	 */
	public String getRolodexDetailById(RolodexVO vo);

	/**
	 * This method is used to save and update rolodex details
	 * @param vo
	 * @return object with success message and rolodex object
	 */
	public String saveOrUpdateRolodex(RolodexVO vo);

	/**
	 * This method is used get rolodex details
	 * @param vo
	 * @return object with success message and rolodex object
	 */
	public String getAllRolodexes(RolodexVO vo);

	/**
	 * @param vo
	 * @return a list of country matching the search keyword
	 */
	public List<Country> findCountryList(CommonVO vo);

}
