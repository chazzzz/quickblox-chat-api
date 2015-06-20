import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.qbapi.bean.QBApiUser;
import org.qbapi.bean.QBDialog;
import org.qbapi.bean.QBSession;
import org.qbapi.error.QBException;
import org.qbapi.service.QBService;
import org.qbapi.service.impl.QBServiceImpl;

import java.util.logging.Logger;

/**
 * Created by chazz on 6/10/2015.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QBProcessTest {

	private QBApiUser testApiUser;

	private QBApiUser dialogRecipient;

	private QBApiUser dialogOwner;

	private QBService qbService;

	private static final Logger _logger = Logger.getLogger("QBTest");

	@Before
	public void init() {
		_logger.info("Initializing test...");

		qbService = new QBServiceImpl();

		testApiUser = new QBApiUser();
		testApiUser.setEmail("jimmy@qbchatapi.com");
		testApiUser.setLogin("jimmy");
		testApiUser.setPassword("j1mmyr0cks");
		testApiUser.setFullName("Jimmy Jay");

		dialogRecipient = new QBApiUser();
		dialogRecipient.setEmail("johnny@qbchatapi.com");
		dialogRecipient.setLogin("johnny");
		dialogRecipient.setPassword("j0hnny123");
		dialogRecipient.setFullName("Johnny Joe");

		dialogOwner = new QBApiUser();
		dialogOwner.setFullName("jenny@qbchatapi.com");
		dialogOwner.setLogin("jenny");
		dialogOwner.setPassword("j3nn12331");
		dialogOwner.setFullName("Jenny Jacobs");
	}

	@Test
	public void requestUnauthenticatedSession() throws QBException {
		_logger.info("Testing creation of Session ");

		QBSession session = qbService.createUnauthenticatedSession();

		_logger.info("Session successfully created. ");
		_logger.info("Session: " + session.getRawInfo());

		Assert.assertNotNull(session.getId());
		Assert.assertNotNull(session.getToken());
	}

	@Test
	public void createApiUser() {
		_logger.info("Creating test api user...");

		try {
			QBApiUser apiUser = qbService.registerApiUser(testApiUser);
			_logger.info("Successfully created new User");
			_logger.info("User: " + apiUser.getRawInfo());

			Assert.assertNotNull(apiUser.getId());
		} catch (QBException e) {

			if (e.getErrorCode() == QBException.ERROR_API) {
				_logger.warning("API ERROR: " + e.getRawResponse());
			} else {
				_logger.warning("Exception occurred." + e.getMessage());
			}

			Assert.fail();
		}
	}

	@Test
	public void requestAuthenticatedSession() {
		_logger.info("Testing creation of authenticated Session ");

		try {
			QBSession session = qbService.createSession(testApiUser);

			_logger.info("Session successfully created. ");
			_logger.info("Session: " + session.getRawInfo());

			Assert.assertNotNull(session.getId());
			Assert.assertNotNull(session.getToken());
		} catch (QBException e) {
			if (e.getErrorCode() == QBException.ERROR_API) {
				_logger.warning("API ERROR: " + e.getRawResponse());
			} else {
				_logger.warning("Exception occurred." + e.getMessage());
			}

			Assert.fail();
		}
	}

	@Test
	public void deleteApiUser() {
		_logger.info("Testing deletion of API User");
		_logger.info("Fetching API User to delete...");

		try {
			QBApiUser existingUser = qbService.getApiUserByLogin(testApiUser.getLogin());
			_logger.info("Fetched user: " + existingUser.getRawInfo());

			if (existingUser.getId() != null) {
				testApiUser.setId(existingUser.getId());

				QBApiUser apiUser = qbService.deleteApiUser(existingUser);

				Assert.assertFalse(apiUser.isRegistered());
			} else {
				Assert.fail("Test API User can not be found. No test will be done.");
			}
		} catch (QBException e) {
			if (e.getErrorCode() == QBException.ERROR_API) {
				_logger.warning("API ERROR: " + e.getRawResponse());
			} else {
				_logger.warning("Exception occurred." + e.getMessage());
			}

			Assert.fail();
		}
	}

	@Test
	public void createDialog() throws QBException {
		_logger.info("Testing creation of Dialog");

		try {
			_logger.info("Resolving 2 api users for dialog");
			QBApiUser apiUser1 = qbService.getApiUserByLogin(dialogOwner.getLogin());
			if (apiUser1 == null) {
				_logger.info("Registering dialog owner as api user...");
				apiUser1 = qbService.registerApiUser(dialogOwner);

				_logger.info("Successfully registered. Owner: " + apiUser1.getRawInfo());
			} else {
				apiUser1.setPassword(dialogOwner.getPassword());
			}

			QBApiUser apiUser2 = qbService.getApiUserByLogin(dialogRecipient.getLogin());
			if (apiUser2 == null) {
				_logger.info("Registering dialog recipient as api user...");

				apiUser2 = qbService.registerApiUser(dialogRecipient);

				_logger.info("Successfully registered. Recipient: " + apiUser1.getRawInfo());
			} else {
				apiUser2.setPassword(dialogRecipient.getPassword());
			}

			if (apiUser1.isRegistered() && apiUser2.isRegistered()) {
				QBDialog dialog = qbService.createDialog(apiUser1, apiUser2, "clasname", "owner -> recipient");

				_logger.info("Dialog created. Raw info: " + dialog.getRawInfo());

			} else {
				_logger.warning("Unable to create api users... skipping dialog creation tests... ");
			}
		} catch (QBException e) {
			if (e.getErrorCode() == QBException.ERROR_API) {
				_logger.warning("API ERROR: " + e.getRawResponse());
			} else {
				_logger.warning("Exception occurred." + e.getMessage());
			}

			Assert.fail();
		}
	}
}
