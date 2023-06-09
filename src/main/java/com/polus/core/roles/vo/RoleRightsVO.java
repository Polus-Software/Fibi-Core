package com.polus.core.roles.vo;

import java.util.List;

import com.polus.core.roles.pojo.Rights;

public class RoleRightsVO extends RoleVO{
	
	private List<Rights> rights;

	public List<Rights> getRights() {
		return rights;
	}

	public void setRights(List<Rights> rights) {
		this.rights = rights;
	}
		
}
