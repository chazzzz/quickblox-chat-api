/**
 * KeynectUp WebChat - QuickBlox Chat Integration
 * @author chazz
 *
 */
var QBChat = (function($, QB){
	if(!$ || !QB) {
		console.error('QBChat integration requires jQuery or Zepto and QuickBlox JS SDK v1.3 or higher');
		return null;
	}

	var CONFIG = {
		debug	: false,
		history	: false
		/*, these configs must be initialized via WebChat.init(config)
		 appId		: null,
		 authKey		: null,
		 authSecret	: null,
		 user		: {
		 id 		: null,
		 jid		: null,
		 login	: null
		 },
		 recipient	: {
		 id 		: null,
		 jid		: null
		 },
		 dialogId    : null*/
	};

	var EVENTS = {
		CONNECTED	: 'CONNECTED',
		DISCONNECTED : 'DISCONNECTED',
		CONNECTING: 'CONNECTING',
		MESSAGE: 'MESSAGE',
		CONTACT_ONLINE: 'CONTACT_ONLINE',
		CONTACT_OFFLINE: 'CONTACT_OFFLINE',
		MESSAGE_LIST: 'MESSAGE_LIST'
	};

	var pendingMessages = []
	var isReady = false;

	this.callbacks = {};

	/**
	 * Callback for creating session
	 */
	var onSessionCreated = function(err, result) {
		if(typeof err == 'undefined' || err == null) {
			trace('A session has been successfully created. ', result);

			// store the session token
			CONFIG.token = result.token;

			// init more configs and callbacks
			initCallbacksAndJIDs();

			// query if there's a dialog existing between these two people
			// this is a procedure for chat history retrieval
			if(CONFIG.history && CONFIG.dialogId){
				retrieveMessages();
			}
			else {
				beginConnection();
			}
		}
		else {
			traceError(err);
		}
	}

	var initCallbacksAndJIDs = function() {
		var resolveJid = function(userId) {
			return QB.chat.helpers.getUserJid(userId, CONFIG.appId);
		}

		CONFIG.user.jid = resolveJid(CONFIG.user.id);
		CONFIG.recipient.jid = resolveJid(CONFIG.recipient.id);

		/**
		 * Event callback for receiving a message
		 */
		QB.chat.onMessageListener = function(userId, message) {
			trace('A message has been received', arguments);

			trigger(EVENTS.MESSAGE, userId, message);
		};

		/**
		 * Event callback when a user's request to be added in roster
		 */
		QB.chat.onSubscribeListener = function(userId) {
			trace(userId + ' wants to be in your roster. Confirming...');

			// will confirm all that subscribes
			var jid = resolveJid(userId);
			QB.chat.roster.confirm(jid, function() {
				trace('Successfully confirmed the request of ' + userId);
				trigger(EVENTS.CONNECTED);
			});
		}

		/**
		 * Event callback when user confirmed the subscription/roster request
		 */
		QB.chat.onConfirmSubscribeListener = function(userId) {
			trace(userId +  'has confirmed your request.');

			trigger(EVENTS.CONNECTED);
		};

		QB.chat.onDisconnectingListener = function() {
			trigger(EVENTS.DISCONNECTED);
		};

		QB.chat.onContactListListener = function(userId, type) {
			if(type === 'unavailable') {
				trigger(EVENTS.CONTACT_OFFLINE);
			}
			else {
				trigger(EVENTS.CONTACT_ONLINE);
			}
		};

	}

	var retrieveMessages = function() {
		trace('Fetching messages from dialog ' + CONFIG.dialogId);

		QB.chat.message.list({
			chat_dialog_id: CONFIG.dialogId
		}, function(err, result) {
			if(typeof err == 'undefined' || err == null) {
				trace('Past messages has been successfully retrieved.', result);

				var messages = result.items;
				for(var i in messages) {
					var message = messages[i];

					var createdAt = message.created_at;
					var createdAtElapsed = "just now";

					var listenerObj = {
						body : message.message,
						createdAt: createdAt,
						createdAtElapsed: jQuery.timeago(createdAt)
					};

					QB.chat.onMessageListener(message.sender_id, listenerObj);
				}

				//Then let's begin connecting to websocket! yeah!
				beginConnection();
			}
			else {
				traceError(err);
			}
		});
	}

	var beginConnection = function() {
		trace('Connecting to QuickBlox server...');

		trigger(EVENTS.CONNECTING);

		QB.chat.connect({
			jid 	: CONFIG.user.jid,
			password: CONFIG.user.login

		}, function(err, rosters) {
			if(typeof err == 'undefined' || err == null) {
				trace('Connection has been established. Listing rosters...', rosters);

				// check if this account has already made connections with recipient user
				if($.isEmptyObject(rosters)) {
					addRecipientInRoster();
				}
				else {
					trigger(EVENTS.CONNECTED);
				}
			}
			else {
				traceError(err);
			}
		});
	}

	var addRecipientInRoster = function() {
		trace('Sending request for adding the recipient in user\'s roster.');

		QB.chat.roster.add(CONFIG.recipient.jid, function(err, result) {
			if(typeof err == 'undefined' || err == null) {
				trace('The request has been successfully sent.');
			}
			else {
				traceError(err);
			}
		});
	}

	var trigger = function(event, data1, data2) {
		trace("Triggering events " + event);
		handleEvents(event, data1, data2);
		this.callbacks[event](data1, data2);
	}

	var addEventListener = function(event, callback) {
		this.callbacks[event] = callback;
	}

	var handleEvents = function(event, data1, data2) {
		if(event === EVENTS.CONNECTED) {
			isReady = true;

			trace('sending pending messages...', pendingMessages);
			for(var i in pendingMessages) {
				var message = pendingMessages[i];
				sendMessage(message.body);
			}

			pendingMessages = [];
		}
		else if(event === EVENTS.DISCONNECTED) {
			isReady = false;
		}
	};

	var sendMessage = function(message) {
		trace('sending message... : ', message);

		if(isReady) {
			var extension = {};

			if(CONFIG.history) {
				extension.save_to_history = 1;
			}

			QB.chat.send(CONFIG.recipient.jid, {
				type        : 'chat',
				body        : message,
				dialog_id   : CONFIG.dialogId,
				extension   : extension
			});
		}
		else {
			trace('QBChat not ready yet... saving message to temporary storage.');
			pendingMessages.push({body: message});
		}
	};

	var trace = function(message, obj) {
		if(CONFIG.debug) {
			console.log(message, obj);
		}
	}

	var traceError = function(err) {
		console.error('An org.qbapi.error has occured: ', err);
	}

	return {
		init	: function(extendedConfig) {
			CONFIG = $.extend({}, CONFIG, extendedConfig);

			trace('initializing webchat with config', CONFIG);

			// initialize
			QB.init(CONFIG.appId, CONFIG.authKey, CONFIG.authSecret);

			var loginParams = {};
			loginParams.appId = CONFIG.appId;
			loginParams.authKey = CONFIG.authKey;
			loginParams.authSecret = CONFIG.authSecret;
			loginParams.login = CONFIG.user.login;
			loginParams.password = CONFIG.user.login;

			trace('Creating session for user ', loginParams.login);

			QB.createSession(loginParams, onSessionCreated);
		},

		sendMessage : function(message) {
			sendMessage(message);
		},

		onConnected	: function(callback) {
			addEventListener(EVENTS.CONNECTED, callback);
		},

		onConnecting : function(callback) {
			addEventListener(EVENTS.CONNECTING, callback);
		},

		onDisconnected	: function(callback) {
			addEventListener(EVENTS.DISCONNECTED, callback);
		},

		onContactOffline: function(callback) {
			addEventListener(EVENTS.CONTACT_OFFLINE, callback);
		},

		onContactOnline: function(callback) {
			addEventListener(EVENTS.CONTACT_ONLINE, callback);
		},

		onMessage 		: function(callback) {
			addEventListener(EVENTS.MESSAGE, callback);
		},

		onMessageList 	: function(callback) {
			addEventListener(EVENTS.MESSAGE_LIST, callback);
		}

	}
})($, QB);