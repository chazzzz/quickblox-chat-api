import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.qbapi.bean.ApiUser;
import org.qbapi.bean.Session;
import org.qbapi.error.QBException;
import org.qbapi.service.QBService;
import org.qbapi.service.impl.QBServiceImpl;

import java.util.logging.Logger;

/**
 * Created by chazz on 6/10/2015.
 */
public class QBProcessTest {

    private ApiUser testApiUser;

    private QBService qbService;

    private static final Logger _logger = Logger.getLogger("QBTest");

    @Before
    public void init() {
        qbService = new QBServiceImpl();

        testApiUser = new ApiUser();
        testApiUser.setEmail("jimmy@qbchatapi.com");
        testApiUser.setLogin("jimmy");
        testApiUser.setPassword("j1mmyr0cks");
        testApiUser.setFullName("Jimmy Newb");
    }

    @Test
    public void testApiUserCreation() throws QBException {
        _logger.info("Testing creation of API User []");

        ApiUser apiUser = qbService.registerApiUser(testApiUser);

        _logger.info("Successfully created new User");
        _logger.info("User: " + apiUser.getRawResponse());

        qbService.deleteApiUser(apiUser);
    }

    @Test
    public void testUnauthenticatedSession() {
        _logger.info("Testing creation of Session ");

        Session session = qbService.createUnauthenticatedSession();

        _logger.info("Session successfully created. ");
        _logger.info("Session: " + session.getRawResponse());

        Assert.assertNotNull(session.getId());
        Assert.assertNotNull(session.getToken());
    }

}
