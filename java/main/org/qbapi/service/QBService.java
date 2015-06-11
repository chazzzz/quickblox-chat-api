package org.qbapi.service;

import org.qbapi.bean.ApiUser;
import org.qbapi.bean.Dialog;
import org.qbapi.bean.Session;
import org.qbapi.error.QBException;

import java.io.IOException;

/**
 * Created by chazz on 6/9/2015.
 */
public interface QBService {

    Dialog createDialog();

    ApiUser registerApiUser(ApiUser apiUser) throws QBException;

    ApiUser deleteApiUser(ApiUser apiUser) throws QBException;

    Session createSession(ApiUser apiUser) throws QBException;

    Session createUnauthenticatedSession() throws QBException;

	ApiUser getApiUserByLogin(String login) throws QBException;
}
