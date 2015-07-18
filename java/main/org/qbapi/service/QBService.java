package org.qbapi.service;

import org.qbapi.bean.QBApiUser;
import org.qbapi.bean.QBDialog;
import org.qbapi.bean.QBSession;
import org.qbapi.error.QBException;

/**
 * Created by chazz on 6/9/2015.
 */
public interface QBService {

    QBDialog createDialog(QBSession session, QBApiUser owner, QBApiUser recipient, String className, String direction) throws QBException;

	QBDialog createDialog(QBSession session, QBApiUser owner, QBApiUser recipient) throws QBException;

    QBApiUser registerApiUser(QBSession session, QBApiUser apiUser) throws QBException;

    QBApiUser deleteApiUser(QBSession session, QBApiUser apiUser) throws QBException;

    QBSession createSession(QBApiUser apiUser) throws QBException;

    QBSession createUnauthenticatedSession() throws QBException;

	QBApiUser getApiUserByLogin(QBSession session, String login) throws QBException;
}
