import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.qbapi.bean.ApiUser;
import org.qbapi.bean.Session;
import org.qbapi.error.QBException;
import org.qbapi.service.QBService;
import org.qbapi.service.impl.QBServiceImpl;

import java.util.logging.Logger;

/**
 * Created by chazz on 6/10/2015.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

		testApiUser.setId(apiUser.getId());

		Assert.assertNotNull(apiUser.getId());
	}

	@Test
	public void testApiUserDeletion() throws QBException {
		_logger.info("Testing deletion of API User");
		_logger.info("Fetching API User to delete...");

		ApiUser existingUser = qbService.getApiUserByLogin(testApiUser.getLogin());
		_logger.info("Fetched user: " + existingUser.getRawResponse());
		existingUser.setLogin(testApiUser.getLogin());
		existingUser.setPassword(testApiUser.getPassword());

		if (existingUser.getId() != null) {
			ApiUser apiUser = qbService.deleteApiUser(existingUser);

			Assert.assertFalse(apiUser.isRegistered());
		} else {
			_logger.warning("Test API User can not be found. No test will be done.");
		}
	}

	@Test
	public void testUnauthenticatedSession() throws QBException {
		_logger.info("Testing creation of Session ");

		Session session = qbService.createUnauthenticatedSession();

		_logger.info("Session successfully created. ");
		_logger.info("Session: " + session.getRawResponse());

		Assert.assertNotNull(session.getId());
		Assert.assertNotNull(session.getToken());
	}

	@Test
	public void testAuthenticatedSession() throws QBException {
		_logger.info("Testing creation of authenticated Session ");

		Session session = qbService.createSession(testApiUser);

		_logger.info("Session successfully created. ");
		_logger.info("Session: " + session.getRawResponse());

		Assert.assertNotNull(session.getId());
		Assert.assertNotNull(session.getToken());
	}

}
