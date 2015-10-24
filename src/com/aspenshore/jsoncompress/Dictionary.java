package com.aspenshore.jsoncompress;

import java.util.*;
import java.util.regex.*;

class Dictionary {
    //!\"#$%&[]
    private static Pattern wordsPattern = Pattern.compile("[A-Z][A-Z][A-Z][A-Z]*");
    private static Pattern longCodesPattern = Pattern.compile("<[N-Z0-9!\\\\\"#$%\\[\\]&][A-Z0-9!\\\\\"#$%\\[\\]&]");
    private static Pattern shortCodesPattern = Pattern.compile("<[A-M]");
    
    private static Comparator<String> byLength = new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return b.length() - a.length();
                }
            };


    private static String[] wordsAndCodes = {
          "A", "THE", "B", "AND", "C", "FOR", "D", "YOU", "E", "NOT", "F", "ARE",
          "G", "ALL", "H", "NEW", "I", "WAS", "J", "CAN", "K", "HAS", "L", "BUT",
          "M", "OUR", "NA", "THAT", "NB", "THIS", "NA", "THAT", "NB", "THIS", "NC", "WITH",
          "ND", "FROM", "NE", "YOUR", "NF", "HAVE", "NG", "MORE", "NH", "WILL", "NI", "HOME",
          "NJ", "ABOUT", "NK", "PAGE", "NL", "SEARCH", "NM", "FREE", "NN", "OTHER", "NO", "INFORMATION",
          "NP", "TIME", "NQ", "THEY", "NR", "SITE", "NS", "WHAT", "NT", "WHICH", "NU", "THEIR",
          "NV", "NEWS", "NW", "THERE", "NX", "ONLY", "NY", "WHEN", "NZ", "CONTACT", "N0", "HERE",
          "N1", "BUSINESS", "N2", "ALSO", "N3", "HELP", "N4", "VIEW", "N5", "ONLINE", "N6", "FIRST",
          "N7", "BEEN", "N8", "WOULD", "N9", "WERE", "N!", "SERVICES", "N\"", "SOME", "N#", "THESE",
          "N$", "CLICK", "N%", "LIKE", "N&", "SERVICE", "N[", "THAN", "N]", "FIND", "OA", "PRICE",
          "OB", "DATE", "OC", "BACK", "OD", "PEOPLE", "OE", "LIST", "OF", "NAME", "OG", "JUST",
          "OH", "OVER", "OI", "STATE", "OJ", "YEAR", "OK", "INTO", "OL", "EMAIL", "OM", "HEALTH",
          "ON", "WORLD", "OO", "NEXT", "OP", "USED", "OQ", "WORK", "OR", "LAST", "OS", "MOST",
          "OT", "PRODUCTS", "OU", "MUSIC", "OV", "DATA", "OW", "MAKE", "OX", "THEM", "OY", "SHOULD",
          "OZ", "PRODUCT", "O0", "SYSTEM", "O1", "POST", "O2", "CITY", "O3", "POLICY", "O4", "NUMBER",
          "O5", "SUCH", "O6", "PLEASE", "O7", "AVAILABLE", "O8", "COPYRIGHT", "O9", "SUPPORT", "O!", "MESSAGE",
          "O\"", "AFTER", "O#", "BEST", "O$", "SOFTWARE", "O%", "THEN", "O&", "GOOD", "O[", "VIDEO",
          "O]", "WELL", "PA", "WHERE", "PB", "INFO", "PC", "RIGHTS", "PD", "PUBLIC", "PE", "BOOKS",
          "PF", "HIGH", "PG", "SCHOOL", "PH", "THROUGH", "PI", "EACH", "PJ", "LINKS", "PK", "REVIEW",
          "PL", "YEARS", "PM", "ORDER", "PN", "VERY", "PO", "PRIVACY", "PP", "BOOK", "PQ", "ITEMS",
          "PR", "COMPANY", "PS", "READ", "PT", "GROUP", "PU", "NEED", "PV", "MANY", "PW", "USER",
          "PX", "SAID", "PY", "DOES", "PZ", "UNDER", "P0", "GENERAL", "P1", "RESEARCH", "P2", "UNIVERSITY",
          "P3", "JANUARY", "P4", "MAIL", "P5", "FULL", "P6", "REVIEWS", "P7", "PROGRAM", "P8", "LIFE",
          "P9", "KNOW", "P!", "GAMES", "P\"", "DAYS", "P#", "MANAGEMENT", "P$", "PART", "P%", "COULD",
          "P&", "GREAT", "P[", "UNITED", "P]", "HOTEL", "QA", "REAL", "QB", "ITEM", "QC", "INTERNATIONAL",
          "QD", "CENTER", "QE", "EBAY", "QF", "MUST", "QG", "STORE", "QH", "TRAVEL", "QI", "COMMENTS",
          "QJ", "MADE", "QK", "DEVELOPMENT", "QL", "REPORT", "QM", "MEMBER", "QN", "DETAILS", "QO", "LINE",
          "QP", "TERMS", "QQ", "BEFORE", "QR", "HOTELS", "QS", "SEND", "QT", "RIGHT", "QU", "TYPE",
          "QV", "BECAUSE", "QW", "LOCAL", "QX", "THOSE", "QY", "USING", "QZ", "RESULTS", "Q0", "OFFICE",
          "Q1", "EDUCATION", "Q2", "NATIONAL", "Q3", "DESIGN", "Q4", "TAKE", "Q5", "POSTED", "Q6", "INTERNET",
          "Q7", "ADDRESS", "Q8", "COMMUNITY", "Q9", "WITHIN", "Q!", "STATES", "Q\"", "AREA", "Q#", "WANT",
          "Q$", "PHONE", "Q%", "SHIPPING", "Q&", "RESERVED", "Q[", "SUBJECT", "Q]", "BETWEEN", "RA", "FORUM",
          "RB", "FAMILY", "RC", "LONG", "RD", "BASED", "RE", "CODE", "RF", "SHOW", "RG", "EVEN",
          "RH", "BLACK", "RI", "CHECK", "RJ", "SPECIAL", "RK", "PRICES", "RL", "WEBSITE", "RM", "INDEX",
          "RN", "BEING", "RO", "WOMEN", "RP", "MUCH", "RQ", "SIGN", "RR", "FILE", "RS", "LINK",
          "RT", "OPEN", "RU", "TODAY", "RV", "TECHNOLOGY", "RW", "SOUTH", "RX", "CASE", "RY", "PROJECT",
          "RZ", "SAME", "R0", "PAGES", "R1", "VERSION", "R2", "SECTION", "R3", "FOUND", "R4", "SPORTS",
          "R5", "HOUSE", "R6", "RELATED", "R7", "SECURITY", "R8", "BOTH", "R9", "COUNTY", "R!", "AMERICAN",
          "R\"", "PHOTO", "R#", "GAME", "R$", "MEMBERS", "R%", "POWER", "R&", "WHILE", "R[", "CARE",
          "R]", "NETWORK", "SA", "DOWN", "SB", "COMPUTER", "SC", "SYSTEMS", "SD", "THREE", "SE", "TOTAL",
          "SF", "PLACE", "SG", "FOLLOWING", "SH", "DOWNLOAD", "SI", "WITHOUT", "SJ", "ACCESS", "SK", "THINK",
          "SL", "NORTH", "SM", "RESOURCES", "SN", "CURRENT", "SO", "POSTS", "SP", "MEDIA", "SQ", "CONTROL",
          "SR", "WATER", "SS", "HISTORY", "ST", "PICTURES", "SU", "SIZE", "SV", "PERSONAL", "SW", "SINCE",
          "SX", "INCLUDING", "SY", "GUIDE", "SZ", "SHOP", "S0", "DIRECTORY", "S1", "BOARD", "S2", "LOCATION",
          "S3", "CHANGE", "S4", "WHITE", "S5", "TEXT", "S6", "SMALL", "S7", "RATING", "S8", "RATE",
          "S9", "GOVERNMENT", "S!", "CHILDREN", "S\"", "DURING", "S#", "RETURN", "S$", "STUDENTS", "S%", "SHOPPING",
          "S&", "ACCOUNT", "S[", "TIMES", "S]", "SITES", "TA", "LEVEL", "TB", "DIGITAL", "TC", "PROFILE",
          "TD", "PREVIOUS", "TE", "FORM", "TF", "EVENTS", "TG", "LOVE", "TH", "JOHN", "TI", "MAIN",
          "TJ", "CALL", "TK", "HOURS", "TL", "IMAGE", "TM", "DEPARTMENT", "TN", "TITLE", "TO", "DESCRIPTION",
          "TP", "INSURANCE", "TQ", "ANOTHER", "TR", "SHALL", "TS", "PROPERTY", "TT", "CLASS", "TU", "STILL",
          "TV", "MONEY", "TW", "QUALITY", "TX", "EVERY", "TY", "LISTING", "TZ", "CONTENT", "T0", "COUNTRY",
          "T1", "PRIVATE", "T2", "LITTLE", "T3", "VISIT", "T4", "SAVE", "T5", "TOOLS", "T6", "REPLY",
          "T7", "CUSTOMER", "T8", "DECEMBER", "T9", "COMPARE", "T!", "MOVIES", "T\"", "INCLUDE", "T#", "COLLEGE",
          "T$", "VALUE", "T%", "ARTICLE", "T&", "YORK", "T[", "CARD", "T]", "JOBS", "UA", "PROVIDE",
          "UB", "FOOD", "UC", "SOURCE", "UD", "AUTHOR", "UE", "DIFFERENT", "UF", "PRESS", "UG", "LEARN",
          "UH", "SALE", "UI", "AROUND", "UJ", "PRINT", "UK", "COURSE", "UL", "CANADA", "UM", "PROCESS",
          "UN", "TEEN", "UO", "ROOM", "UP", "STOCK", "UQ", "TRAINING", "UR", "CREDIT", "US", "POINT",
          "UT", "JOIN", "UU", "SCIENCE", "UV", "CATEGORIES", "UW", "ADVANCED", "UX", "WEST", "UY", "SALES",
          "UZ", "LOOK", "U0", "ENGLISH", "U1", "LEFT", "U2", "TEAM", "U3", "ESTATE", "U4", "CONDITIONS",
          "U5", "SELECT", "U6", "WINDOWS", "U7", "PHOTOS", "U8", "THREAD", "U9", "WEEK", "U!", "CATEGORY",
          "U\"", "NOTE", "U#", "LIVE", "U$", "LARGE", "U%", "GALLERY", "U&", "TABLE", "U[", "REGISTER",
          "U]", "HOWEVER", "VA", "JUNE", "VB", "OCTOBER", "VC", "NOVEMBER", "VD", "MARKET", "VE", "LIBRARY",
          "VF", "REALLY", "VG", "ACTION", "VH", "START", "VI", "SERIES", "VJ", "MODEL", "VK", "FEATURES",
          "VL", "INDUSTRY", "VM", "PLAN", "VN", "HUMAN", "VO", "PROVIDED", "VP", "REQUIRED", "VQ", "SECOND",
          "VR", "ACCESSORIES", "VS", "COST", "VT", "MOVIE", "VU", "FORUMS", "VV", "MARCH", "VW", "SEPTEMBER",
          "VX", "BETTER", "VY", "QUESTIONS", "VZ", "JULY", "V0", "YAHOO", "V1", "GOING", "V2", "MEDICAL",
          "V3", "TEST", "V4", "FRIEND", "V5", "COME", "V6", "SERVER", "V7", "STUDY", "V8", "APPLICATION",
          "V9", "CART", "V!", "STAFF", "V\"", "ARTICLES", "V#", "FEEDBACK", "V$", "AGAIN", "V%", "PLAY",
          "V&", "LOOKING", "V[", "ISSUES", "V]", "APRIL", "WA", "NEVER", "WB", "USERS", "WC", "COMPLETE",
          "WD", "STREET", "WE", "TOPIC", "WF", "COMMENT", "WG", "FINANCIAL", "WH", "THINGS", "WI", "WORKING",
          "WJ", "AGAINST", "WK", "STANDARD", "WL", "PERSON", "WM", "BELOW", "WN", "MOBILE", "WO", "LESS",
          "WP", "BLOG", "WQ", "PARTY", "WR", "PAYMENT", "WS", "EQUIPMENT", "WT", "LOGIN", "WU", "STUDENT",
          "WV", "PROGRAMS", "WW", "OFFERS", "WX", "LEGAL", "WY", "ABOVE", "WZ", "RECENT", "W0", "PARK",
          "W1", "STORES", "W2", "SIDE", "W3", "PROBLEM", "W4", "GIVE", "W5", "MEMORY", "W6", "PERFORMANCE",
          "W7", "SOCIAL", "W8", "AUGUST", "W9", "QUOTE", "W!", "LANGUAGE", "W\"", "STORY", "W#", "SELL",
          "W$", "OPTIONS", "W%", "EXPERIENCE", "W&", "RATES", "W[", "CREATE", "W]", "BODY", "XA", "YOUNG",
          "XB", "AMERICA", "XC", "IMPORTANT", "XD", "FIELD", "XE", "EAST", "XF", "PAPER", "XG", "SINGLE",
          "XH", "ACTIVITIES", "XI", "CLUB", "XJ", "EXAMPLE", "XK", "GIRLS", "XL", "ADDITIONAL", "XM", "PASSWORD",
          "XN", "LATEST", "XO", "SOMETHING", "XP", "ROAD", "XQ", "GIFT", "XR", "QUESTION", "XS", "CHANGES",
          "XT", "NIGHT", "XU", "HARD", "XV", "TEXAS", "XW", "FOUR", "XX", "POKER", "XY", "STATUS",
          "XZ", "BROWSE", "X0", "ISSUE", "X1", "RANGE", "X2", "BUILDING", "X3", "SELLER", "X4", "COURT",
          "X5", "FEBRUARY", "X6", "ALWAYS", "X7", "RESULT", "X8", "AUDIO", "X9", "LIGHT", "X!", "WRITE",
          "X\"", "OFFER", "X#", "BLUE", "X$", "GROUPS", "X%", "EASY", "X&", "GIVEN", "X[", "FILES",
          "X]", "EVENT", "YA", "RELEASE", "YB", "ANALYSIS", "YC", "REQUEST", "YD", "CHINA", "YE", "MAKING",
          "YF", "PICTURE", "YG", "NEEDS", "YH", "POSSIBLE", "YI", "MIGHT", "YJ", "PROFESSIONAL", "YK", "MONTH",
          "YL", "MAJOR", "YM", "STAR", "YN", "AREAS", "YO", "FUTURE", "YP", "SPACE", "YQ", "COMMITTEE",
          "YR", "HAND", "YS", "CARDS", "YT", "PROBLEMS", "YU", "LONDON", "YV", "WASHINGTON", "YW", "MEETING",
          "YX", "BECOME", "YY", "INTEREST", "YZ", "CHILD", "Y0", "KEEP", "Y1", "ENTER", "Y2", "CALIFORNIA",
          "Y3", "PORN", "Y4", "SHARE", "Y5", "SIMILAR", "Y6", "GARDEN", "Y7", "SCHOOLS", "Y8", "MILLION",
          "Y9", "ADDED", "Y!", "REFERENCE", "Y\"", "COMPANIES", "Y#", "LISTED", "Y$", "BABY", "Y%", "LEARNING",
          "Y&", "ENERGY", "Y[", "DELIVERY", "Y]", "POPULAR", "ZA", "TERM", "ZB", "FILM", "ZC", "STORIES",
          "ZD", "COMPUTERS", "ZE", "JOURNAL", "ZF", "REPORTS", "ZG", "WELCOME", "ZH", "CENTRAL", "ZI", "IMAGES",
          "ZJ", "PRESIDENT", "ZK", "NOTICE", "ZL", "ORIGINAL", "ZM", "HEAD", "ZN", "RADIO", "ZO", "UNTIL",
          "ZP", "CELL", "ZQ", "COLOR", "ZR", "SELF", "ZS", "COUNCIL", "ZT", "AWAY", "ZU", "INCLUDES",
          "ZV", "TRACK", "ZW", "AUSTRALIA", "ZX", "DISCUSSION", "ZY", "ARCHIVE", "ZZ", "ONCE", "Z0", "OTHERS",
          "Z1", "ENTERTAINMENT", "Z2", "AGREEMENT", "Z3", "FORMAT", "Z4", "LEAST", "Z5", "SOCIETY", "Z6", "MONTHS",
          "Z7", "SAFETY", "Z8", "FRIENDS", "Z9", "SURE", "Z!", "TRADE", "Z\"", "EDITION", "Z#", "CARS",
          "Z$", "MESSAGES", "Z%", "MARKETING", "Z&", "TELL", "Z[", "FURTHER", "Z]", "UPDATED", "0A", "ASSOCIATION",
          "0B", "ABLE", "0C", "HAVING", "0D", "PROVIDES", "0E", "DAVID", "0F", "ALREADY", "0G", "GREEN",
          "0H", "STUDIES", "0I", "CLOSE", "0J", "COMMON", "0K", "DRIVE", "0L", "SPECIFIC", "0M", "SEVERAL",
          "0N", "GOLD", "0O", "LIVING", "0P", "COLLECTION", "0Q", "CALLED", "0R", "SHORT", "0S", "ARTS",
          "0T", "DISPLAY", "0U", "LIMITED", "0V", "POWERED", "0W", "SOLUTIONS", "0X", "MEANS", "0Y", "DIRECTOR",
          "0Z", "DAILY", "00", "BEACH", "01", "PAST", "02", "NATURAL", "03", "WHETHER", "04", "ELECTRONICS",
          "05", "FIVE", "06", "UPON", "07", "PERIOD", "08", "PLANNING", "09", "DATABASE", "0!", "SAYS",
          "0\"", "OFFICIAL", "0#", "WEATHER", "0$", "LAND", "0%", "AVERAGE", "0&", "DONE", "0[", "TECHNICAL",
          "0]", "WINDOW", "1A", "FRANCE", "1B", "REGION", "1C", "ISLAND", "1D", "RECORD", "1E", "DIRECT",
          "1F", "MICROSOFT", "1G", "CONFERENCE", "1H", "ENVIRONMENT", "1I", "RECORDS", "1J", "DISTRICT", "1K", "CALENDAR",
          "1L", "COSTS", "1M", "STYLE", "1N", "FRONT", "1O", "STATEMENT", "1P", "UPDATE", "1Q", "PARTS",
          "1R", "EVER", "1S", "DOWNLOADS", "1T", "EARLY", "1U", "MILES", "1V", "SOUND", "1W", "RESOURCE",
          "1X", "PRESENT", "1Y", "APPLICATIONS", "1Z", "EITHER", "10", "DOCUMENT", "11", "WORD", "12", "WORKS",
          "13", "MATERIAL", "14", "BILL", "15", "WRITTEN", "16", "TALK", "17", "FEDERAL", "18", "HOSTING",
          "19", "RULES", "1!", "FINAL", "1\"", "ADULT", "1#", "TICKETS", "1$", "THING", "1%", "CENTRE",
          "1&", "REQUIREMENTS", "1[", "CHEAP", "1]", "NUDE", "2A", "KIDS", "2B", "FINANCE", "2C", "TRUE",
          "2D", "MINUTES", "2E", "ELSE", "2F", "MARK", "2G", "THIRD", "2H", "ROCK", "2I", "GIFTS",
          "2J", "EUROPE", "2K", "READING", "2L", "TOPICS", "2M", "INDIVIDUAL", "2N", "TIPS", "2O", "PLUS",
          "2P", "AUTO", "2Q", "COVER", "2R", "USUALLY", "2S", "EDIT", "2T", "TOGETHER", "2U", "VIDEOS",
          "2V", "PERCENT", "2W", "FAST", "2X", "FUNCTION", "2Y", "FACT", "2Z", "UNIT", "20", "GETTING",
          "21", "GLOBAL", "22", "TECH", "23", "MEET", "24", "ECONOMIC", "25", "PLAYER", "26", "PROJECTS",
          "27", "LYRICS", "28", "OFTEN", "29", "SUBSCRIBE", "2!", "SUBMIT", "2\"", "GERMANY", "2#", "AMOUNT",
          "2$", "WATCH", "2%", "INCLUDED", "2&", "FEEL", "2[", "THOUGH", "2]", "BANK", "3A", "RISK",
          "3B", "THANKS", "3C", "EVERYTHING", "3D", "DEALS", "3E", "VARIOUS", "3F", "WORDS", "3G", "LINUX",
          "3H", "PRODUCTION", "3I", "COMMERCIAL", "3J", "JAMES", "3K", "WEIGHT", "3L", "TOWN", "3M", "HEART",
          "3N", "ADVERTISING", "3O", "RECEIVED", "3P", "CHOOSE", "3Q", "TREATMENT", "3R", "NEWSLETTER", "3S", "ARCHIVES",
          "3T", "POINTS", "3U", "KNOWLEDGE", "3V", "MAGAZINE", "3W", "ERROR", "3X", "CAMERA", "3Y", "GIRL",
          "3Z", "CURRENTLY", "30", "CONSTRUCTION", "31", "TOYS", "32", "REGISTERED", "33", "CLEAR", "34", "GOLF",
          "35", "RECEIVE", "36", "DOMAIN", "37", "METHODS", "38", "CHAPTER", "39", "MAKES", "3!", "PROTECTION",
          "3\"", "POLICIES", "3#", "LOAN", "3$", "WIDE", "3%", "BEAUTY", "3&", "MANAGER", "3[", "INDIA",
          "3]", "POSITION", "4A", "TAKEN", "4B", "SORT", "4C", "LISTINGS", "4D", "MODELS", "4E", "MICHAEL",
          "4F", "KNOWN", "4G", "HALF", "4H", "CASES", "4I", "STEP", "4J", "ENGINEERING", "4K", "FLORIDA",
          "4L", "SIMPLE", "4M", "QUICK", "4N", "NONE", "4O", "WIRELESS", "4P", "LICENSE", "4Q", "PAUL",
          "4R", "FRIDAY", "4S", "LAKE", "4T", "WHOLE", "4U", "ANNUAL", "4V", "PUBLISHED", "4W", "LATER",
          "4X", "BASIC", "4Y", "SONY", "4Z", "SHOWS", "40", "CORPORATE", "41", "GOOGLE", "42", "CHURCH",
          "43", "METHOD", "44", "PURCHASE", "45", "CUSTOMERS", "46", "ACTIVE", "47", "RESPONSE", "48", "PRACTICE",
          "49", "HARDWARE", "4!", "FIGURE", "4\"", "MATERIALS", "4#", "FIRE", "4$", "HOLIDAY", "4%", "CHAT",
          "4&", "ENOUGH", "4[", "DESIGNED", "4]", "ALONG", "5A", "AMONG", "5B", "DEATH", "5C", "WRITING",
          "5D", "SPEED", "5E", "HTML", "5F", "COUNTRIES", "5G", "LOSS", "5H", "FACE", "5I", "BRAND",
          "5J", "DISCOUNT", "5K", "HIGHER", "5L", "EFFECTS", "5M", "CREATED", "5N", "REMEMBER", "5O", "STANDARDS",
          "5P", "YELLOW", "5Q", "POLITICAL", "5R", "INCREASE", "5S", "ADVERTISE", "5T", "KINGDOM", "5U", "BASE",
          "5V", "NEAR", "5W", "ENVIRONMENTAL", "5X", "THOUGHT", "5Y", "STUFF", "5Z", "FRENCH", "50", "STORAGE",
          "51", "JAPAN", "52", "DOING", "53", "LOANS", "54", "SHOES", "55", "ENTRY", "56", "STAY",
          "57", "NATURE", "58", "ORDERS", "59", "AVAILABILITY", "5!", "AFRICA", "5\"", "SUMMARY", "5#", "TURN",
          "5$", "MEAN", "5%", "GROWTH", "5&", "NOTES", "5[", "AGENCY", "5]", "KING", "6A", "MONDAY",
          "6B", "EUROPEAN", "6C", "ACTIVITY", "6D", "COPY", "6E", "ALTHOUGH", "6F", "DRUG", "6G", "PICS",
          "6H", "WESTERN", "6I", "INCOME", "6J", "FORCE", "6K", "CASH", "6L", "EMPLOYMENT", "6M", "OVERALL",
          "6N", "RIVER", "6O", "COMMISSION", "6P", "PACKAGE", "6Q", "CONTENTS", "6R", "SEEN", "6S", "PLAYERS",
          "6T", "ENGINE", "6U", "PORT", "6V", "ALBUM", "6W", "REGIONAL", "6X", "STOP", "6Y", "SUPPLIES",
          "6Z", "STARTED", "60", "ADMINISTRATION", "61", "INSTITUTE", "62", "VIEWS", "63", "PLANS", "64", "DOUBLE",
          "65", "BUILD", "66", "SCREEN", "67", "EXCHANGE", "68", "TYPES", "69", "SOON", "6!", "SPONSORED",
          "6\"", "LINES", "6#", "ELECTRONIC", "6$", "CONTINUE", "6%", "ACROSS", "6&", "BENEFITS", "6[", "NEEDED",
          "6]", "SEASON", "7A", "APPLY", "7B", "SOMEONE", "7C", "HELD", "7D", "ANYTHING", "7E", "PRINTER",
          "7F", "CONDITION", "7G", "EFFECTIVE", "7H", "BELIEVE", "7I", "ORGANIZATION", "7J", "EFFECT", "7K", "ASKED",
          "7L", "MIND", "7M", "SUNDAY", "7N", "SELECTION", "7O", "CASINO", "7P", "LOST", "7Q", "TOUR",
          "7R", "MENU", "7S", "VOLUME", "7T", "CROSS", "7U", "ANYONE", "7V", "MORTGAGE", "7W", "HOPE",
          "7X", "SILVER", "7Y", "CORPORATION", "7Z", "WISH", "70", "INSIDE", "71", "SOLUTION", "72", "MATURE",
          "73", "ROLE", "74", "RATHER", "75", "WEEKS", "76", "ADDITION", "77", "CAME", "78", "SUPPLY",
          "79", "NOTHING", "7!", "CERTAIN", "7\"", "EXECUTIVE", "7#", "RUNNING", "7$", "LOWER", "7%", "NECESSARY",
          "7&", "UNION", "7[", "JEWELRY", "7]", "ACCORDING", "8A", "CLOTHING", "8B", "PARTICULAR", "8C", "FINE",
          "8D", "NAMES", "8E", "ROBERT", "8F", "HOMEPAGE", "8G", "HOUR", "8H", "SKILLS", "8I", "BUSH",
          "8J", "ISLANDS", "8K", "ADVICE", "8L", "CAREER", "8M", "MILITARY", "8N", "RENTAL", "8O", "DECISION",
          "8P", "LEAVE", "8Q", "BRITISH", "8R", "TEENS", "8S", "HUGE", "8T", "WOMAN", "8U", "FACILITIES",
          "8V", "KIND", "8W", "SELLERS", "8X", "MIDDLE", "8Y", "MOVE", "8Z", "CABLE", "80", "OPPORTUNITIES",
          "81", "TAKING", "82", "VALUES", "83", "DIVISION", "84", "COMING", "85", "TUESDAY", "86", "OBJECT",
          "87", "LESBIAN", "88", "APPROPRIATE", "89", "MACHINE", "8!", "LOGO", "8\"", "LENGTH", "8#", "ACTUALLY",
          "8$", "NICE", "8%", "SCORE", "8&", "STATISTICS", "8[", "CLIENT", "8]", "RETURNS", "9A", "CAPITAL",
          "9B", "FOLLOW", "9C", "SAMPLE", "9D", "INVESTMENT", "9E", "SENT", "9F", "SHOWN", "9G", "SATURDAY",
          "9H", "CHRISTMAS", "9I", "ENGLAND", "9J", "CULTURE", "9K", "BAND", "9L", "FLASH", "9M", "LEAD",
          "9N", "GEORGE", "9O", "CHOICE", "9P", "WENT", "9Q", "STARTING", "9R", "REGISTRATION", "9S", "THURSDAY",
          "9T", "COURSES", "9U", "CONSUMER", "9V", "AIRPORT", "9W", "FOREIGN", "9X", "ARTIST", "9Y", "OUTSIDE",
          "9Z", "FURNITURE", "90", "LEVELS", "91", "CHANNEL", "92", "LETTER", "93", "MODE", "94", "PHONES",
          "95", "IDEAS", "96", "WEDNESDAY", "97", "STRUCTURE", "98", "FUND", "99", "SUMMER", "9!", "ALLOW",
          "9\"", "DEGREE", "9#", "CONTRACT", "9$", "BUTTON", "9%", "RELEASES", "9&", "HOMES", "9[", "SUPER",
          "9]", "MALE", "!A", "MATTER", "!B", "CUSTOM", "!C", "VIRGINIA", "!D", "ALMOST", "!E", "TOOK",
          "!F", "LOCATED", "!G", "MULTIPLE", "!H", "ASIAN", "!I", "DISTRIBUTION", "!J", "EDITOR", "!K", "INDUSTRIAL",
          "!L", "CAUSE", "!M", "POTENTIAL", "!N", "SONG", "!O", "CNET", "!P", "FOCUS", "!Q", "LATE",
          "!R", "FALL", "!S", "FEATURED", "!T", "IDEA", "!U", "ROOMS", "!V", "FEMALE", "!W", "RESPONSIBLE",
          "!X", "COMMUNICATIONS", "!Y", "ASSOCIATED", "!Z", "THOMAS", "!0", "PRIMARY", "!1", "CANCER", "!2", "NUMBERS",
          "!3", "REASON", "!4", "TOOL", "!5", "BROWSER", "!6", "SPRING", "!7", "FOUNDATION", "!8", "ANSWER",
          "!9", "VOICE", "!!", "FRIENDLY", "!\"", "SCHEDULE", "!#", "DOCUMENTS", "!$", "COMMUNICATION", "!%", "PURPOSE",
          "!&", "FEATURE", "![", "COMES", "!]", "POLICE", "\"A", "EVERYONE", "\"B", "INDEPENDENT", "\"C", "APPROACH",
          "\"D", "CAMERAS", "\"E", "BROWN", "\"F", "PHYSICAL", "\"G", "OPERATING", "\"H", "HILL", "\"I", "MAPS",
          "\"J", "MEDICINE", "\"K", "DEAL", "\"L", "HOLD", "\"M", "RATINGS", "\"N", "CHICAGO", "\"O", "FORMS",
          "\"P", "GLASS", "\"Q", "HAPPY", "\"R", "SMITH", "\"S", "WANTED", "\"T", "DEVELOPED", "\"U", "THANK",
          "\"V", "SAFE", "\"W", "UNIQUE", "\"X", "SURVEY", "\"Y", "PRIOR", "\"Z", "TELEPHONE", "\"0", "SPORT",
          "\"1", "READY", "\"2", "FEED", "\"3", "ANIMAL", "\"4", "SOURCES", "\"5", "MEXICO", "\"6", "POPULATION",
          "\"7", "REGULAR", "\"8", "SECURE", "\"9", "NAVIGATION", "\"!", "OPERATIONS", "\"\"", "THEREFORE",
          "\"#", "SIMPLY", "\"$", "EVIDENCE", "\"%", "STATION", "\"&", "CHRISTIAN", "\"[", "ROUND", "\"]", "PAYPAL",
          "#A", "FAVORITE", "#B", "UNDERSTAND", "#C", "OPTION", "#D", "MASTER", "#E", "VALLEY", "#F", "RECENTLY",
          "#G", "PROBABLY", "#H", "RENTALS", "#I", "BUILT", "#J", "PUBLICATIONS", "#K", "BLOOD", "#L", "WORLDWIDE",
          "#M", "IMPROVE", "#N", "CONNECTION", "#O", "PUBLISHER", "#P", "HALL", "#Q", "LARGER", "#R", "ANTI",
          "#S", "NETWORKS", "#T", "EARTH", "#U", "PARENTS", "#V", "NOKIA", "#W", "IMPACT", "#X", "TRANSFER",
          "#Y", "INTRODUCTION", "#Z", "KITCHEN", "#0", "STRONG", "#1", "CAROLINA", "#2", "WEDDING", "#3", "PROPERTIES",
          "#4", "HOSPITAL", "#5", "GROUND", "#6", "OVERVIEW", "#7", "SHIP", "#8", "ACCOMMODATION", "#9", "OWNERS",
          "#!", "DISEASE", "#\"", "EXCELLENT", "##", "PAID", "#$", "ITALY", "#%", "PERFECT", "#&", "HAIR",
          "#[", "OPPORTUNITY", "#]", "CLASSIC", "$A", "BASIS", "$B", "COMMAND", "$C", "CITIES", "$D", "WILLIAM", "$E", "EXPRESS",
          "$F", "ANAL", "$G", "AWARD", "$H", "DISTANCE", "$I", "TREE", "$J", "PETER", "$K", "ASSESSMENT",
          "$L", "ENSURE", "$M", "THUS", "$N", "WALL", "$O", "INVOLVED", "$P", "EXTRA", "$Q", "ESPECIALLY",
          "$R", "INTERFACE", "$S", "PUSSY", "$T", "PARTNERS", "$U", "BUDGET", "$V", "RATED", "$W", "GUIDES",
          "$X", "SUCCESS", "$Y", "MAXIMUM", "$Z", "OPERATION", "$0", "EXISTING", "$1", "QUITE", "$2", "SELECTED",
          "$3", "AMAZON", "$4", "PATIENTS", "$5", "RESTAURANTS", "$6", "BEAUTIFUL", "$7", "WARNING", "$8", "WINE",
          "$9", "LOCATIONS", "$!", "HORSE", "$\"", "VOTE", "$#", "FORWARD", "$$", "FLOWERS", "$%", "STARS",
          "$&", "SIGNIFICANT", "$[", "LISTS", "$]", "TECHNOLOGIES", "%A", "OWNER", "%B", "RETAIL", "%C", "ANIMALS",
          "%D", "USEFUL", "%E", "DIRECTLY", "%F", "MANUFACTURER", "%G", "WAYS", "%H", "PROVIDING", "%I", "RULE",
          "%J", "HOUSING", "%K", "TAKES", "%L", "BRING", "%M", "CATALOG", "%N", "SEARCHES", "%O", "TRYING",
          "%P", "MOTHER", "%Q", "AUTHORITY", "%R", "CONSIDERED", "%S", "TOLD", "%T", "TRAFFIC", "%U", "PROGRAMME",
          "%V", "JOINED", "%W", "INPUT", "%X", "STRATEGY", "%Y", "FEET", "%Z", "AGENT", "%0", "VALID",
          "%1", "MODERN", "%2", "SENIOR", "%3", "IRELAND", "%4", "SEXY", "%5", "TEACHING", "%6", "DOOR",
          "%7", "GRAND", "%8", "TESTING", "%9", "TRIAL", "%!", "CHARGE", "%\"", "UNITS", "%#", "INSTEAD",
          "%$", "CANADIAN", "%%", "COOL", "%&", "NORMAL", "%[", "WROTE", "%]", "ENTERPRISE", "&A", "SHIPS",
          "&B", "ENTIRE", "&C", "EDUCATIONAL", "&D", "LEADING", "&E", "METAL", "&F", "POSITIVE", "&G", "FITNESS",
          "&H", "CHINESE", "&I", "OPINION", "&J", "ASIA", "&K", "FOOTBALL", "&L", "ABSTRACT", "&M", "USES",
          "&N", "OUTPUT", "&O", "FUNDS", "&P", "GREATER", "&Q", "LIKELY", "&R", "DEVELOP", "&S", "EMPLOYEES",
          "&T", "ARTISTS", "&U", "ALTERNATIVE", "&V", "PROCESSING", "&W", "RESPONSIBILITY", "&X", "RESOLUTION", "&Y", "JAVA",
          "&Z", "GUEST", "&0", "SEEMS", "&1", "PUBLICATION", "&2", "PASS", "&3", "RELATIONS", "&4", "TRUST",
          "&5", "CONTAINS", "&6", "SESSION", "&7", "MULTI", "&8", "PHOTOGRAPHY", "&9", "REPUBLIC", "&!", "FEES",
          "&\"", "COMPONENTS", "&#", "VACATION", "&$", "CENTURY", "&%", "ACADEMIC", "&&", "ASSISTANCE", "&[", "COMPLETED",
          "&]", "SKIN", "[A", "GRAPHICS", "[B", "INDIAN", "[C", "PREV", "[D", "MARY", "[E", "EXPECTED",
          "[F", "RING", "[G", "GRADE", "[H", "DATING", "[I", "PACIFIC", "[J", "MOUNTAIN", "[K", "ORGANIZATIONS",
          "[L", "FILTER", "[M", "MAILING", "[N", "VEHICLE", "[O", "LONGER", "[P", "CONSIDER", "[Q", "NORTHERN",
          "[R", "BEHIND", "[S", "PANEL", "[T", "FLOOR", "[U", "GERMAN", "[V", "BUYING", "[W", "MATCH",
          "[X", "PROPOSED", "[Y", "DEFAULT", "[Z", "REQUIRE", "[0", "IRAQ", "[1", "BOYS", "[2", "OUTDOOR",
          "[3", "DEEP", "[4", "MORNING", "[5", "OTHERWISE", "[6", "ALLOWS", "[7", "REST", "[8", "PROTEIN",
          "[9", "PLANT", "[!", "REPORTED", "[\"", "TRANSPORTATION", "[#", "POOL", "[$", "MINI", "[%", "POLITICS",
          "[&", "PARTNER", "[[", "DISCLAIMER", "[]", "AUTHORS", "]A", "BOARDS", "]B", "FACULTY", "]C", "PARTIES",
          "]D", "FISH", "]E", "MEMBERSHIP", "]F", "MISSION", "]G", "STRING", "]H", "SENSE", "]I", "MODIFIED",
          "]J", "PACK", "]K", "RELEASED", "]L", "STAGE", "]M", "INTERNAL", "]N", "GOODS", "]O", "RECOMMENDED",
          "]P", "BORN", "]Q", "UNLESS", "]R", "RICHARD", "]S", "DETAILED", "]T", "JAPANESE", "]U", "RACE",
          "]V", "APPROVED", "]W", "BACKGROUND", "]X", "TARGET", "]Y", "EXCEPT", "]Z", "CHARACTER", "]0", "MAINTENANCE",
          "]1", "ABILITY", "]2", "MAYBE", "]3", "FUNCTIONS", "]4", "MOVING", "]5", "BRANDS", "]6", "PLACES",
          "]7", "PRETTY", "]8", "TRADEMARKS", "]9", "PHENTERMINE", "]!", "SPAIN", "]\"", "SOUTHERN", "]#", "YOURSELF",
          "]$", "WINTER", "]%", "RAPE", "]&", "BATTERY", "][", "YOUTH", "]]", "PRESSURE"
    };
    private static Map<String,String> wordsToCodes = new HashMap<String,String>();
    private static Map<String,String> codesToWords = new HashMap<String,String>();

    static {
        for (int i = 0; i < wordsAndCodes.length; i = i + 2) {
            String code = wordsAndCodes[i];
            String word = wordsAndCodes[i + 1];
            wordsToCodes.put(word, code);
            codesToWords.put(code, word);
        }
    }

    public static String encode(String word) {
        if (wordsToCodes.containsKey(word)) {
            return "<" + wordsToCodes.get(word);
        }
        return word;
    }

    public static String decode(String code1) {
        if (!code1.startsWith("<")) {
            return code1;
        }
        String code = code1.substring(1, code1.length());
        if (codesToWords.containsKey(code)) {
            return codesToWords.get(code);
        }
        return code1;
    }

    public static String lengthen(String result2) {
        List<String> shortCodesInString = wordsForPattern(shortCodesPattern, result2);
        for (String w : shortCodesInString) {
            String theWord = Dictionary.decode(w);
            result2 = result2.replace(w, theWord);
        }
        List<String> longCodesInString = wordsForPattern(longCodesPattern, result2);
        for (String w : longCodesInString) {
            String theWord = Dictionary.decode(w);
            result2 = result2.replace(w, theWord);
        }
        return result2;
    }

    public static String shorten(String str) {
        List<String> words = wordsForPattern(wordsPattern, str);
        String result = str;
        for (String w : words) {
            String code = Dictionary.encode(w);
            result = result.replace(w, code);
        }
        return result;
    }

    public static List<String> wordsForPattern(Pattern p, String str) {
        Matcher m = p.matcher(str);
        List<String> words = new ArrayList<String>();
        while(m.find()) {
            words.add(m.group(0));
        }

        Collections.sort(words, byLength);
        return words;
    }
}
