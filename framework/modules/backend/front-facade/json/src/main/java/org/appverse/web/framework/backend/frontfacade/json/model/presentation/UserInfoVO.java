package org.appverse.web.framework.backend.frontfacade.json.model.presentation;

import org.appverse.web.framework.backend.core.beans.AbstractPresentationBean;

public class UserInfoVO extends AbstractPresentationBean {

	private static final long serialVersionUID = 7431357982859482224L;

	private String user;
	private String password;

	public UserInfoVO() {
		super();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
