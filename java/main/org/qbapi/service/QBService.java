package org.qbapi.service;

import org.qbapi.bean.QBApiUser;
import org.qbapi.bean.QBDialog;
import org.qbapi.bean.QBSession;
import org.qbapi.error.QBException;

/**
 * Created by chazz on 6/9/2015.
 */
public interface QBService {

    QBDialog createDialog(QBApiUser owner, QBApiUser recipient, String className, String direction) throws QBException;

	QBDialog createDialog(QBApiUser owner, QBApiUser recipient) throws QBException;

    QBApiUser registerApiUser(QBApiUser apiUser) throws QBException;

    QBApiUser deleteApiUser(QBApiUser apiUser) throws QBException;

    QBSession createSession(QBApiUser apiUser) throws QBException;

    QBSession createUnauthenticatedSession() throws QBException;

	QBApiUser getApiUserByLogin(String login) throws QBException;
}
