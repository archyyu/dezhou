package com.yl.container;

import java.util.ArrayList;
import java.util.logging.Level;

public class ConfigData
{

	public ConfigData()
	{
	}

	public static int restartCount = 0;
	public static boolean debugInfo = false;
	public static int apiMinVersion;
	public static String SERVER_ADDR;
	public static int CONNECTION_CLEANER_INTERVAL = 20;
	public static final int MAX_ROOMS_PER_ZONE = 0x10000;
	public static int SERVER_PORT = 9339;
	public static int OUT_QUEUE_THREADS = 1;
	public static int MAX_CHANNEL_QUEUE = 60;
	public static int MAX_DROPPED_PACKETS = -1;
	public static int MAX_CLIENTS;
	public static int MAX_MSG_LEN = 4096;
	public static int MAX_USER_IDLETIME = 180;
	public static int MAX_SOCKET_IDLETIME = 30;
	public static int MIN_MSG_TIME;
	public static int LOG_MAX_SIZE;
	public static int LOG_MAX_FILES;
	public static int SYS_HANDLER_THREADS = 1;
	public static int EXT_HANDLER_THREADS = 1;
	public static int MAX_USERS_PER_IP = 10;
	public static int MAX_ROOM_VARS = -1;
	public static int MAX_USER_VARS = -1;
	public static int MAX_INCOMING_QUEUE = 10000;
	public static boolean EXTENSION_REMOTE_DEBUGGING = true;
	public static boolean EXTENSIONS_AUTORELOAD = false;
	public static long SCHEDULER_RESOLUTION = 5L;
	public static int BUDDY_LIST_AUTOSAVE_INTERVAL = 60;
	public static int BUDDY_LIST_PERMISSION_CLEAN_INTERVAL = 10;
	public static int BUDDY_LIST_PERMISSION_REQUEST_DURATION = 25000;
	public static boolean ALLOW_ANON_XT_MESSAGES = false;
	public static boolean ALLOW_ZONE_INFO = false;
	public static String BAN_CLEAN_MODE;
	public static long BAN_DURATION;
	public static String BANNED_LOGIN_MESSAGE;
	public static boolean AUTO_CROSSDOMAIN;
	public static ArrayList POLICY_ALLOWED_DOMAINS;
	public static boolean EXTERNAL_CROSSDOMAIN_POLICY = false;
	public static Level FILE_LOGGIN_LEVEL;
	public static Level CONSOLE_LOGGIN_LEVEL;
	public static int BAN_MODE = 1;
	public static boolean ANTIFLOOD_ACTIVE;
	public static int ANTIFLOOD_MIN_MSG_TIME;
	public static int ANTIFLOOD_TOLERANCE;
	public static int ANTIFLOOD_MAX_REPEATED;
	public static int ANTIFLOOD_WARNINGS;
	public static String ANTIFLOOD_WARNING_MSG;
	public static String ANTIFLOOD_KICK_MSG;
	public static int ANTIFLOOD_BAN_AFTER_KICKS;
	public static int ANTIFLOOD_BAN_AFTER_TIMESPAN;
	public static String ANTIFLOOD_BAN_MSG;
	public static boolean BADWORDS_ACTIVE;
	public static boolean BADWORDS_WARNINGS;
	public static boolean BADWORDS_ROOM_NAMES;
	public static boolean BADWORDS_USER_NAMES;
	public static int BADWORDS_FILTER_MODE;
	public static int BADWORDS_WARNINGS_BEFORE_KICK;
	public static int BADWORDS_BAN_AFTER_KICKS;
	public static int BADWORDS_BAN_AFTER_TIMESPAN;
	public static String BADWORDS_WARNING_MSG;
	public static String BADWORDS_KICK_MSG;
	public static String BADWORDS_BAN_MSG;
	public static final int KICK_REASON_FLOOD = 1;
	public static final int KICK_REASON_BADWORDS = 2;
	public static boolean DB_MANAGER = false;
	public static final char EOF = 0;
	public static String STR_DELIMITER = "%";
	public static final String ADMIN_ZONE_NAME = "$dmn";
	public static final String BANNED_LIST_PATH = "bannedUsers.sfs";
	public static long dataIN;
	public static long dataOUT;
	public static long outGoingDroppedMessages;
	public static long inComingDroppedMessages;
	public static int maxSimultanousConnections;
	public static boolean USE_WEBSERVER = false;
	public static String WEBSERVER_CFG_PATH;
	public static boolean PM_MISSING_RECIPIENT_ALERT = false;
	public static final int CHANNEL_POLICY_NORMAL = 0;
	public static final int CHANNEL_POLICY_STRICT = 1;
	public static final int CHANNEL_POLICY_SEVERE = 2;
	public static int DEAD_CHANNELS_POLICY = 1;
	public static boolean DISCONNECT_IDLE_SPECTATORS = true;
	public static final String CHANNEL_POLICIES[] =
	{ "normal", "strict", "severe" };
	public static int H2_PORT_CONFIG = 3;
	public static String H2_ARGS[] =
	{ "-baseDir", "./datastore/", "-tcpPort", "9009" };
	public static final String H2_DRIVER_NAME = "org.h2.Driver";
	public static String H2_CONN_STRING = "jdbc:h2:tcp://localhost:9009/__sfscore__";
	public static final String H2_USRNAME = "sa";
	public static final String H2_PWORD = "";
	public static final String H2_CONN_NAME = "__h2db_core__";
	public static final int H2_MAX_ACTIVE = 10;
	public static final int H2_MAX_IDLE = 10;
	public static final String H2_EXHAUSTED_ACTION = "grow";
	public static final int H2_BLOCK_TIME = 5000;
	public static boolean H2_ACTIVE = true;
	public static String dynamicPropertiesClass = "java.util.HashMap";
	public static int RHINO_OPTIMIZATION_LEVEL = 9;
	public static boolean USER_VARS_OPTIMIZATION = false;
	public static boolean DEBUG_INCOMING_MESSAGES = false;
	public static boolean DEBUG_OUTGOING_MESSAGES = false;
	public static boolean USE_NPC = false;
	public static String LSData[] = null;
	public static boolean WARN_ON_WRONG_ZONE_ERROR = false;
	public static String WRONG_ZONE_MESSAGE = "The requested zone:%s does not exist!";

	static
	{
		FILE_LOGGIN_LEVEL = Level.WARNING;
		CONSOLE_LOGGIN_LEVEL = Level.FINE;
	}
}
