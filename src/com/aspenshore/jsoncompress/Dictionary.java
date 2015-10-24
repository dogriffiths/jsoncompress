import java.util.*;
import java.util.regex.*;

class Dictionary {
    private static Pattern wordsPattern = Pattern.compile("[A-Z][A-Z][A-Z][A-Z]*");
    private static Pattern longCodesPattern = Pattern.compile("<[N-Z0-9][A-Z0-9]");
    private static Pattern shortCodesPattern = Pattern.compile("<[A-M]");
    
    private static Comparator<String> byLength = new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return b.length() - a.length();
                }
            };


    private static String[] wordsAndCodes = {
        "A", "THE", "B", "AND", "C", "FOR", "D", "YOU", "E", "NOT",
        "F", "ARE", "G", "ALL", "H", "NEW", "I", "WAS", "J", "CAN",
        "K", "HAS", "L", "BUT", "M", "OUR", "NA", "THAT", "NB", "THIS",
        "NC", "WITH", "ND", "FROM", "NE", "YOUR", "NF", "HAVE", "NG", "MORE",
        "NH", "WILL", "NI", "HOME", "NJ", "ABOUT", "NK", "PAGE", "NL", "SEARCH",
        "NM", "FREE", "NN", "OTHER", "NO", "INFORMATION", "NP", "TIME", "NQ", "THEY",
        "NR", "SITE", "NS", "WHAT", "NT", "WHICH", "NU", "THEIR", "NV", "NEWS",
        "NW", "THERE", "NX", "ONLY", "NY", "WHEN", "NZ", "CONTACT", "N0", "HERE",
        "N1", "BUSINESS", "N2", "ALSO", "N3", "HELP", "N4", "VIEW", "N5", "ONLINE",
        "N6", "FIRST", "N7", "BEEN", "N8", "WOULD", "N9", "WERE", "OA", "SERVICES",
        "OB", "SOME", "OC", "THESE", "OD", "CLICK", "OE", "LIKE", "OF", "SERVICE",
        "OG", "THAN", "OH", "FIND", "OI", "PRICE", "OJ", "DATE", "OK", "BACK",
        "OL", "PEOPLE", "OM", "LIST", "ON", "NAME", "OO", "JUST", "OP", "OVER",
        "OQ", "STATE", "OR", "YEAR", "OS", "INTO", "OT", "EMAIL", "OU", "HEALTH",
        "OV", "WORLD", "OW", "NEXT", "OX", "USED", "OY", "WORK", "OZ", "LAST",
        "O0", "MOST", "O1", "PRODUCTS", "O2", "MUSIC", "O3", "DATA", "O4", "MAKE",
        "O5", "THEM", "O6", "SHOULD", "O7", "PRODUCT", "O8", "SYSTEM", "O9", "POST",
        "PA", "CITY", "PB", "POLICY", "PC", "NUMBER", "PD", "SUCH", "PE", "PLEASE",
        "PF", "AVAILABLE", "PG", "COPYRIGHT", "PH", "SUPPORT", "PI", "MESSAGE", "PJ", "AFTER",
        "PK", "BEST", "PL", "SOFTWARE", "PM", "THEN", "PN", "GOOD", "PO", "VIDEO",
        "PP", "WELL", "PQ", "WHERE", "PR", "INFO", "PS", "RIGHTS", "PT", "PUBLIC",
        "PU", "BOOKS", "PV", "HIGH", "PW", "SCHOOL", "PX", "THROUGH", "PY", "EACH",
        "PZ", "LINKS", "P0", "REVIEW", "P1", "YEARS", "P2", "ORDER", "P3", "VERY",
        "P4", "PRIVACY", "P5", "BOOK", "P6", "ITEMS", "P7", "COMPANY", "P8", "READ",
        "P9", "GROUP", "QA", "NEED", "QB", "MANY", "QC", "USER", "QD", "SAID",
        "QE", "DOES", "QF", "UNDER", "QG", "GENERAL", "QH", "RESEARCH", "QI", "UNIVERSITY",
        "QJ", "JANUARY", "QK", "MAIL", "QL", "FULL", "QM", "REVIEWS", "QN", "PROGRAM",
        "QO", "LIFE", "QP", "KNOW", "QQ", "GAMES", "QR", "DAYS", "QS", "MANAGEMENT",
        "QT", "PART", "QU", "COULD", "QV", "GREAT", "QW", "UNITED", "QX", "HOTEL",
        "QY", "REAL", "QZ", "ITEM", "Q0", "INTERNATIONAL", "Q1", "CENTER", "Q2", "EBAY",
        "Q3", "MUST", "Q4", "STORE", "Q5", "TRAVEL", "Q6", "COMMENTS", "Q7", "MADE",
        "Q8", "DEVELOPMENT", "Q9", "REPORT", "RA", "MEMBER", "RB", "DETAILS", "RC", "LINE",
        "RD", "TERMS", "RE", "BEFORE", "RF", "HOTELS", "RG", "SEND", "RH", "RIGHT",
        "RI", "TYPE", "RJ", "BECAUSE", "RK", "LOCAL", "RL", "THOSE", "RM", "USING",
        "RN", "RESULTS", "RO", "OFFICE", "RP", "EDUCATION", "RQ", "NATIONAL", "RR", "DESIGN",
        "RS", "TAKE", "RT", "POSTED", "RU", "INTERNET", "RV", "ADDRESS", "RW", "COMMUNITY",
        "RX", "WITHIN", "RY", "STATES", "RZ", "AREA", "R0", "WANT", "R1", "PHONE",
        "R2", "SHIPPING", "R3", "RESERVED", "R4", "SUBJECT", "R5", "BETWEEN", "R6", "FORUM",
        "R7", "FAMILY", "R8", "LONG", "R9", "BASED", "SA", "CODE", "SB", "SHOW",
        "SC", "EVEN", "SD", "BLACK", "SE", "CHECK", "SF", "SPECIAL", "SG", "PRICES",
        "SH", "WEBSITE", "SI", "INDEX", "SJ", "BEING", "SK", "WOMEN", "SL", "MUCH",
        "SM", "SIGN", "SN", "FILE", "SO", "LINK", "SP", "OPEN", "SQ", "TODAY",
        "SR", "TECHNOLOGY", "SS", "SOUTH", "ST", "CASE", "SU", "PROJECT", "SV", "SAME",
        "SW", "PAGES", "SX", "VERSION", "SY", "SECTION", "SZ", "FOUND", "S0", "SPORTS",
        "S1", "HOUSE", "S2", "RELATED", "S3", "SECURITY", "S4", "BOTH", "S5", "COUNTY",
        "S6", "AMERICAN", "S7", "PHOTO", "S8", "GAME", "S9", "MEMBERS", "TA", "POWER",
        "TB", "WHILE", "TC", "CARE", "TD", "NETWORK", "TE", "DOWN", "TF", "COMPUTER",
        "TG", "SYSTEMS", "TH", "THREE", "TI", "TOTAL", "TJ", "PLACE", "TK", "FOLLOWING",
        "TL", "DOWNLOAD", "TM", "WITHOUT", "TN", "ACCESS", "TO", "THINK", "TP", "NORTH",
        "TQ", "RESOURCES", "TR", "CURRENT", "TS", "POSTS", "TT", "MEDIA", "TU", "CONTROL",
        "TV", "WATER", "TW", "HISTORY", "TX", "PICTURES", "TY", "SIZE", "TZ", "PERSONAL",
        "T0", "SINCE", "T1", "INCLUDING", "T2", "GUIDE", "T3", "SHOP", "T4", "DIRECTORY",
        "T5", "BOARD", "T6", "LOCATION", "T7", "CHANGE", "T8", "WHITE", "T9", "TEXT",
        "UA", "SMALL", "UB", "RATING", "UC", "RATE", "UD", "GOVERNMENT", "UE", "CHILDREN",
        "UF", "DURING", "UG", "RETURN", "UH", "STUDENTS", "UI", "SHOPPING", "UJ", "ACCOUNT",
        "UK", "TIMES", "UL", "SITES", "UM", "LEVEL", "UN", "DIGITAL", "UO", "PROFILE",
        "UP", "PREVIOUS", "UQ", "FORM", "UR", "EVENTS", "US", "LOVE", "UT", "JOHN",
        "UU", "MAIN", "UV", "CALL", "UW", "HOURS", "UX", "IMAGE", "UY", "DEPARTMENT",
        "UZ", "TITLE", "U0", "DESCRIPTION", "U1", "INSURANCE", "U2", "ANOTHER", "U3", "SHALL",
        "U4", "PROPERTY", "U5", "CLASS", "U6", "STILL", "U7", "MONEY", "U8", "QUALITY",
        "U9", "EVERY", "VA", "LISTING", "VB", "CONTENT", "VC", "COUNTRY", "VD", "PRIVATE",
        "VE", "LITTLE", "VF", "VISIT", "VG", "SAVE", "VH", "TOOLS", "VI", "REPLY",
        "VJ", "CUSTOMER", "VK", "DECEMBER", "VL", "COMPARE", "VM", "MOVIES", "VN", "INCLUDE",
        "VO", "COLLEGE", "VP", "VALUE", "VQ", "ARTICLE", "VR", "YORK", "VS", "CARD",
        "VT", "JOBS", "VU", "PROVIDE", "VV", "FOOD", "VW", "SOURCE", "VX", "AUTHOR",
        "VY", "DIFFERENT", "VZ", "PRESS", "V0", "LEARN", "V1", "SALE", "V2", "AROUND",
        "V3", "PRINT", "V4", "COURSE", "V5", "CANADA", "V6", "PROCESS", "V7", "TEEN",
        "V8", "ROOM", "V9", "STOCK", "WA", "TRAINING", "WB", "CREDIT", "WC", "POINT",
        "WD", "JOIN", "WE", "SCIENCE", "WF", "CATEGORIES", "WG", "ADVANCED", "WH", "WEST",
        "WI", "SALES", "WJ", "LOOK", "WK", "ENGLISH", "WL", "LEFT", "WM", "TEAM",
        "WN", "ESTATE", "WO", "CONDITIONS", "WP", "SELECT", "WQ", "WINDOWS", "WR", "PHOTOS",
        "WS", "THREAD", "WT", "WEEK", "WU", "CATEGORY", "WV", "NOTE", "WW", "LIVE",
        "WX", "LARGE", "WY", "GALLERY", "WZ", "TABLE", "W0", "REGISTER", "W1", "HOWEVER",
        "W2", "JUNE", "W3", "OCTOBER", "W4", "NOVEMBER", "W5", "MARKET", "W6", "LIBRARY",
        "W7", "REALLY", "W8", "ACTION", "W9", "START", "XA", "SERIES", "XB", "MODEL",
        "XC", "FEATURES", "XD", "INDUSTRY", "XE", "PLAN", "XF", "HUMAN", "XG", "PROVIDED",
        "XH", "REQUIRED", "XI", "SECOND", "XJ", "ACCESSORIES", "XK", "COST", "XL", "MOVIE",
        "XM", "FORUMS", "XN", "MARCH", "XO", "SEPTEMBER", "XP", "BETTER", "XQ", "QUESTIONS",
        "XR", "JULY", "XS", "YAHOO", "XT", "GOING", "XU", "MEDICAL", "XV", "TEST",
        "XW", "FRIEND", "XX", "COME", "XY", "SERVER", "XZ", "STUDY", "X0", "APPLICATION",
        "X1", "CART", "X2", "STAFF", "X3", "ARTICLES", "X4", "FEEDBACK", "X5", "AGAIN",
        "X6", "PLAY", "X7", "LOOKING", "X8", "ISSUES", "X9", "APRIL", "YA", "NEVER",
        "YB", "USERS", "YC", "COMPLETE", "YD", "STREET", "YE", "TOPIC", "YF", "COMMENT",
        "YG", "FINANCIAL", "YH", "THINGS", "YI", "WORKING", "YJ", "AGAINST", "YK", "STANDARD",
        "YL", "PERSON", "YM", "BELOW", "YN", "MOBILE", "YO", "LESS", "YP", "BLOG",
        "YQ", "PARTY", "YR", "PAYMENT", "YS", "EQUIPMENT", "YT", "LOGIN", "YU", "STUDENT",
        "YV", "PROGRAMS", "YW", "OFFERS", "YX", "LEGAL", "YY", "ABOVE", "YZ", "RECENT",
        "Y0", "PARK", "Y1", "STORES", "Y2", "SIDE", "Y3", "PROBLEM", "Y4", "GIVE",
        "Y5", "MEMORY", "Y6", "PERFORMANCE", "Y7", "SOCIAL", "Y8", "AUGUST", "Y9", "QUOTE",
        "ZA", "LANGUAGE", "ZB", "STORY", "ZC", "SELL", "ZD", "OPTIONS", "ZE", "EXPERIENCE",
        "ZF", "RATES", "ZG", "CREATE", "ZH", "BODY", "ZI", "YOUNG", "ZJ", "AMERICA",
        "ZK", "IMPORTANT", "ZL", "FIELD", "ZM", "EAST", "ZN", "PAPER", "ZO", "SINGLE",
        "ZP", "ACTIVITIES", "ZQ", "CLUB", "ZR", "EXAMPLE", "ZS", "GIRLS", "ZT", "ADDITIONAL",
        "ZU", "PASSWORD", "ZV", "LATEST", "ZW", "SOMETHING", "ZX", "ROAD", "ZY", "GIFT",
        "ZZ", "QUESTION", "Z0", "CHANGES", "Z1", "NIGHT", "Z2", "HARD", "Z3", "TEXAS",
        "Z4", "FOUR", "Z5", "POKER", "Z6", "STATUS", "Z7", "BROWSE", "Z8", "ISSUE",
        "Z9", "RANGE", "0A", "BUILDING", "0B", "SELLER", "0C", "COURT", "0D", "FEBRUARY",
        "0E", "ALWAYS", "0F", "RESULT", "0G", "AUDIO", "0H", "LIGHT", "0I", "WRITE",
        "0J", "OFFER", "0K", "BLUE", "0L", "GROUPS", "0M", "EASY", "0N", "GIVEN",
        "0O", "FILES", "0P", "EVENT", "0Q", "RELEASE", "0R", "ANALYSIS", "0S", "REQUEST",
        "0T", "CHINA", "0U", "MAKING", "0V", "PICTURE", "0W", "NEEDS", "0X", "POSSIBLE",
        "0Y", "MIGHT", "0Z", "PROFESSIONAL", "00", "MONTH", "01", "MAJOR", "02", "STAR",
        "03", "AREAS", "04", "FUTURE", "05", "SPACE", "06", "COMMITTEE", "07", "HAND",
        "08", "CARDS", "09", "PROBLEMS", "1A", "LONDON", "1B", "WASHINGTON", "1C", "MEETING",
        "1D", "BECOME", "1E", "INTEREST", "1F", "CHILD", "1G", "KEEP", "1H", "ENTER",
        "1I", "CALIFORNIA", "1J", "PORN", "1K", "SHARE", "1L", "SIMILAR", "1M", "GARDEN",
        "1N", "SCHOOLS", "1O", "MILLION", "1P", "ADDED", "1Q", "REFERENCE", "1R", "COMPANIES",
        "1S", "LISTED", "1T", "BABY", "1U", "LEARNING", "1V", "ENERGY", "1W", "DELIVERY",
        "1X", "POPULAR", "1Y", "TERM", "1Z", "FILM", "10", "STORIES", "11", "COMPUTERS",
        "12", "JOURNAL", "13", "REPORTS", "14", "WELCOME", "15", "CENTRAL", "16", "IMAGES",
        "17", "PRESIDENT", "18", "NOTICE", "19", "ORIGINAL", "2A", "HEAD", "2B", "RADIO",
        "2C", "UNTIL", "2D", "CELL", "2E", "COLOR", "2F", "SELF", "2G", "COUNCIL",
        "2H", "AWAY", "2I", "INCLUDES", "2J", "TRACK", "2K", "AUSTRALIA", "2L", "DISCUSSION",
        "2M", "ARCHIVE", "2N", "ONCE", "2O", "OTHERS", "2P", "ENTERTAINMENT", "2Q", "AGREEMENT",
        "2R", "FORMAT", "2S", "LEAST", "2T", "SOCIETY", "2U", "MONTHS", "2V", "SAFETY",
        "2W", "FRIENDS", "2X", "SURE", "2Y", "TRADE", "2Z", "EDITION", "20", "CARS",
        "21", "MESSAGES", "22", "MARKETING", "23", "TELL", "24", "FURTHER", "25", "UPDATED",
        "26", "ASSOCIATION", "27", "ABLE", "28", "HAVING", "29", "PROVIDES", "3A", "DAVID",
        "3B", "ALREADY", "3C", "GREEN", "3D", "STUDIES", "3E", "CLOSE", "3F", "COMMON",
        "3G", "DRIVE", "3H", "SPECIFIC", "3I", "SEVERAL", "3J", "GOLD", "3K", "LIVING",
        "3L", "COLLECTION", "3M", "CALLED", "3N", "SHORT", "3O", "ARTS", "3P", "DISPLAY",
        "3Q", "LIMITED", "3R", "POWERED", "3S", "SOLUTIONS", "3T", "MEANS", "3U", "DIRECTOR",
        "3V", "DAILY", "3W", "BEACH", "3X", "PAST", "3Y", "NATURAL", "3Z", "WHETHER",
        "30", "ELECTRONICS", "31", "FIVE", "32", "UPON", "33", "PERIOD", "34", "PLANNING",
        "35", "DATABASE", "36", "SAYS", "37", "OFFICIAL", "38", "WEATHER", "39", "LAND",
        "4A", "AVERAGE", "4B", "DONE", "4C", "TECHNICAL", "4D", "WINDOW", "4E", "FRANCE",
        "4F", "REGION", "4G", "ISLAND", "4H", "RECORD", "4I", "DIRECT", "4J", "MICROSOFT",
        "4K", "CONFERENCE", "4L", "ENVIRONMENT", "4M", "RECORDS", "4N", "DISTRICT", "4O", "CALENDAR",
        "4P", "COSTS", "4Q", "STYLE", "4R", "FRONT", "4S", "STATEMENT", "4T", "UPDATE",
        "4U", "PARTS", "4V", "EVER", "4W", "DOWNLOADS", "4X", "EARLY", "4Y", "MILES",
        "4Z", "SOUND", "40", "RESOURCE", "41", "PRESENT", "42", "APPLICATIONS", "43", "EITHER",
        "44", "DOCUMENT", "45", "WORD", "46", "WORKS", "47", "MATERIAL", "48", "BILL",
        "49", "WRITTEN", "5A", "TALK", "5B", "FEDERAL", "5C", "HOSTING", "5D", "RULES",
        "5E", "FINAL", "5F", "ADULT", "5G", "TICKETS", "5H", "THING", "5I", "CENTRE",
        "5J", "REQUIREMENTS", "5K", "CHEAP", "5L", "NUDE", "5M", "KIDS", "5N", "FINANCE",
        "5O", "TRUE", "5P", "MINUTES", "5Q", "ELSE", "5R", "MARK", "5S", "THIRD",
        "5T", "ROCK", "5U", "GIFTS", "5V", "EUROPE", "5W", "READING", "5X", "TOPICS",
        "5Y", "INDIVIDUAL", "5Z", "TIPS", "50", "PLUS", "51", "AUTO", "52", "COVER",
        "53", "USUALLY", "54", "EDIT", "55", "TOGETHER", "56", "VIDEOS", "57", "PERCENT",
        "58", "FAST", "59", "FUNCTION", "6A", "FACT", "6B", "UNIT", "6C", "GETTING",
        "6D", "GLOBAL", "6E", "TECH", "6F", "MEET", "6G", "ECONOMIC", "6H", "PLAYER",
        "6I", "PROJECTS", "6J", "LYRICS", "6K", "OFTEN", "6L", "SUBSCRIBE", "6M", "SUBMIT",
        "6N", "GERMANY", "6O", "AMOUNT", "6P", "WATCH", "6Q", "INCLUDED", "6R", "FEEL",
        "6S", "THOUGH", "6T", "BANK", "6U", "RISK", "6V", "THANKS", "6W", "EVERYTHING",
        "6X", "DEALS", "6Y", "VARIOUS", "6Z", "WORDS", "60", "LINUX", "61", "PRODUCTION",
        "62", "COMMERCIAL", "63", "JAMES", "64", "WEIGHT", "65", "TOWN", "66", "HEART",
        "67", "ADVERTISING", "68", "RECEIVED", "69", "CHOOSE", "7A", "TREATMENT", "7B", "NEWSLETTER",
        "7C", "ARCHIVES", "7D", "POINTS", "7E", "KNOWLEDGE", "7F", "MAGAZINE", "7G", "ERROR",
        "7H", "CAMERA", "7I", "GIRL", "7J", "CURRENTLY", "7K", "CONSTRUCTION", "7L", "TOYS",
        "7M", "REGISTERED", "7N", "CLEAR", "7O", "GOLF", "7P", "RECEIVE", "7Q", "DOMAIN",
        "7R", "METHODS", "7S", "CHAPTER", "7T", "MAKES", "7U", "PROTECTION", "7V", "POLICIES",
        "7W", "LOAN", "7X", "WIDE", "7Y", "BEAUTY", "7Z", "MANAGER", "70", "INDIA",
        "71", "POSITION", "72", "TAKEN", "73", "SORT", "74", "LISTINGS", "75", "MODELS",
        "76", "MICHAEL", "77", "KNOWN", "78", "HALF", "79", "CASES", "8A", "STEP",
        "8B", "ENGINEERING", "8C", "FLORIDA", "8D", "SIMPLE", "8E", "QUICK", "8F", "NONE",
        "8G", "WIRELESS", "8H", "LICENSE", "8I", "PAUL", "8J", "FRIDAY", "8K", "LAKE",
        "8L", "WHOLE", "8M", "ANNUAL", "8N", "PUBLISHED", "8O", "LATER", "8P", "BASIC",
        "8Q", "SONY", "8R", "SHOWS", "8S", "CORPORATE", "8T", "GOOGLE", "8U", "CHURCH",
        "8V", "METHOD", "8W", "PURCHASE", "8X", "CUSTOMERS", "8Y", "ACTIVE", "8Z", "RESPONSE",
        "80", "PRACTICE", "81", "HARDWARE", "82", "FIGURE", "83", "MATERIALS", "84", "FIRE",
        "85", "HOLIDAY", "86", "CHAT", "87", "ENOUGH", "88", "DESIGNED", "89", "ALONG",
        "9A", "AMONG", "9B", "DEATH", "9C", "WRITING", "9D", "SPEED", "9E", "HTML",
        "9F", "COUNTRIES", "9G", "LOSS", "9H", "FACE", "9I", "BRAND", "9J", "DISCOUNT",
        "9K", "HIGHER", "9L", "EFFECTS", "9M", "CREATED", "9N", "REMEMBER", "9O", "STANDARDS",
        "9P", "YELLOW", "9Q", "POLITICAL", "9R", "INCREASE", "9S", "ADVERTISE", "9T", "KINGDOM",
        "9U", "BASE", "9V", "NEAR", "9W", "ENVIRONMENTAL", "9X", "THOUGHT", "9Y", "STUFF",
        "9Z", "FRENCH", "90", "STORAGE", "91", "JAPAN", "92", "DOING", "93", "LOANS",
        "94", "SHOES", "95", "ENTRY", "96", "STAY", "97", "NATURE", "98", "ORDERS", "99", "AVAILABILITY"
    };
    private static Map<String,String> wordsToCodes = new HashMap<String,String>();
    private static Map<String,String> codesToWords = new HashMap<String,String>();

    static {
        for (int i = 0; i < wordsAndCodes.length / 2; i++) {
            String word = wordsAndCodes[i];
            String code = wordsAndCodes[i + 1];
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
            result2 = result2.replaceAll(w, theWord);
        }
        List<String> longCodesInString = wordsForPattern(longCodesPattern, result2);
        for (String w : longCodesInString) {
            String theWord = Dictionary.decode(w);
            result2 = result2.replaceAll(w, theWord);
        }
        return result2;
    }

    public static String shorten(String str) {
        List<String> words = wordsForPattern(wordsPattern, str);
        String result = str;
        for (String w : words) {
            String code = Dictionary.encode(w);
            result = result.replaceAll(w, code);
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
