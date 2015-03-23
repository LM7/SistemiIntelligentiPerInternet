package Final_Version.events;

import java.util.HashSet;

/**
 * Only to provide a static in-code list of stop words.
 */
public class StopWords {
  // stop words
  protected static HashSet stopSet = new HashSet();
  public static final String[] STOP_WORDS = {
    // traditional en stop words
    "a", "an", "and", "are", "as", "at", "be", "but", "by",
    "for", "if", "in", "into", "is", "it",
    "no", "not", "of", "on", "or", "s", "such",
    "t", "that", "the", "their", "then", "there", "these",
    "they", "this", "to", "was", "will", "with",
    // other stop words
    "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
    "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
    "l", "m", "n", "o", "p", "q", "r", "u", "v", "x",
    "y", "z", "&", "?",
    "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
    "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
    "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
    "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
    "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
    "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
    "60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
    "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
    "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
    "90", "91", "92", "93", "94", "95", "96", "97", "98", "99",
    // 100 basic english operators
    "come", "get", "give", "go", "keep", "let", "make", "put", "seem", "take",
    "be", "do", "have", "say", "see", "send", "may", "will", "about", "across",
    "after", "against", "among", "at", "before", "between", "by", "down",
    "from", "in", "off", "on", "over", "through", "to", "under", "up", "with",
    "as", "for", "of", "till", "than", "a", "the", "all", "any", "every", "no",
    "other", "some", "such", "that", "this", "i", "he", "you", "who", "and",
    "because", "but", "or", "if", "through", "while", "how", "when", "where",
    "why", "again", "ever", "far", "forward", "hear", "near", "now", "out",
    "still", "there", "then", "together", "well", "almost", "enough", "even",
    "little", "much", "not", "only", "quite", "so", "very", "tomorrow",
    "yesterday", "north", "south", "east", "west", "please", "yes",
    // pronouns and possessive adjectives
    "i", "me", "mine", "myself", "my",
    "you", "you", "yours", "yourself", "your",
    "he", "him", "his", "himself", "his",
    "she", "her", "hers", "herself", "her",
    "it", "it", "its", "itself", "its",
    "we", "us", "ours", "ourselves", "our",
    "you", "you", "yours", "yourselves ", "your",
    "they", "them", "theirs", "themselves", "their",
    // numbers, months, days
    "1st", "2nd", "3rd", "4th", "5th", "6th", "8th", "9th",
    "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov",
    "dec", "january", "february", "march", "april", "may", "june", "july",
    "august", "september", "october", "november", "december",
    "sunday", "monday", "tuesday", "friday", "wednesday", "thursday", "saturday",
    "sun", "mon", "tue", "wed", "thu", "fri", "sat",
    "year", "years",
    "zero", "first", "second", "third", "fourth", "fifth", "sixth", "seventh",
    "eighth", "ninth", "ten", "tenth", "eleven", "eleventh", "twelve",
    "twelfth", "thirteen", "thirteenth", "fourteen", "fourteenth", "fifteen",
    "fifteenth", "sixteen", "sixteenth", "seventeen", "seventeenth", "eighteen",
    "eighteenth", "eineteen", "nineteenth", "twenty", "twentieth", "thirty",
    "thirtieth", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
    "ten", "hundred", "hundredth", "negative", "minus", "point", "spring",
    "summer", "autumn", "fall", "winter", "hour", "minute", "second", "hours",
    "minutes", "seconds", "thousands", "thousand", "hundreds", "million", 
    "billion", "millions", "billions", 
    // measures
    "linea", "line", "l", "pollice", "inch", "in", "piede", "foot", "ft",
    "yard", "yd", "fathom", "fm", "rod", "rd", "pole", "po", "perch",
    "chain", "chn", "furlong", "fur", "chn", "statute", "mile", "mi",
    "nauticale", "naut", "mi", "knot", "k", "km", "lega", "league", "lea",
    "litro", "gill", "gi", "pint", "pt", "quart", "qt",  "imperial", "gallon",
    "imp", "gal", "qts", "barrel", "bbl", "hogshead", "pipe", "hhd", "wine",
    "gallon", "tierce", "tc", "puncheon", "pun", "gill", "gi", "pint", "pt",
    "quart", "qt", "gallon", "gal", "barrel", "bbl", "hogshead", "hhd",
    "pipe", "pint", "pt", "quart", "qt", "gallon", "gal", "peck", "bushel",
    "bu", "quarter", "qr", "chaldron", "chal", "pint", "pt", "quart", "qt",
    "gallon", "gal", "peck", "bushel", "bu", "quarter", "qr", "chaldron",
    "chal", "byte", "bytes", "kbyte", "kb", "kbs", "kilobyte", "kilobytes",
    "mb", "mbs", "mbyte", "megabyte", "megabytes", "gb", "gbs", "gigabyte",
    "gbyte", "gigabytes", "tb", "tbyte", "terabyte", "terabytes",
    // some colors
    "color", "colors", "white", "red", "black", "yellow", "violet",
    "blue", "orange", "green", "brown", "grey", "pink", "white", "black",
    // removed cos they get too high weight
    "links", "copyright", "mail", "language", "based", "list",
    // others
    "il", "use", "what", "di", "top", "bottom", "cs", "url", "&middot", "&nbsp",
    "click", "add", "alla", "aaa", "fax", "note", "time", "apr", "max", "min",
    "tip", "tips", "note", "fool", "voli", "volo",
    "say", "said", "enter", "stuff", "pdf", "inc", "img", "number",
    "tuo", "misc", "pop", "best", "don", "slide", "happy", "year", "great",
    "src", "line", "next", "must", "use", "men", "man", "girl", "girls",
    "boy", "boys", "sito", "il", "nel",
    "vai", "view", "old", "just", "faq", "faqs", "state", "being", "online",
    "women", "woman", "call", "mile", "miles", "view", "tutto", "tutti",
    "tutte", "tutta", "del", "second", "seconds", "denied", "text", "home",
    "yellow", "start", "iframes", "contact", "fan", "fans", "uses", "use",
    "uno", "una", "un", "il", "lo", "la", "gli", "i", "le", "special", "large",
    "global", "menu", "road", "card", "using", "browse", "road", "main", "needs",
    "need", "would", "will", "shell", "tools", "tool", "many", "much", "grin",
    "support", "link", "hour", "hours", "jpg", "sorry", "day", "days", "date",
    "like", "late", "latest", "big", "blank", "always", "did", "does", "do",
    "open", "close", "url", "weeks", "week", "item", "items", "user", "&middot",
    "right", "left", "front", "back", "website", "help", "want", "good", "bad",
    "better", "worst", "jump", "add", "way", "click", "from", "form", "to",
    "dat", "td", "tr", "br", "length", "basso", "alto", "move", "moved", "moves",
    "dello", "della", "degli", "delle", "black", "most", "few", "anchor",
    "stay", "multi", "mono", "same", "group", "doesn", "more", "quick", "file",
    "size", "thru", "through", "though", "lost", "loose", "ask", "tell",
    "answer", "previous", "next", "since", "calendar", "months", "month", "day",
    "days", "narrow", "wide", "set", "query", "also", "button", "soon", "late",
    "later", "sooner", "young", "old", "welcome", "was", "were", "table", "fit",
    "city", "state", "country", "tel", "telephone", "zip", "email", "lastname",
    "initial", "address", "province", "code", "volume", "buy", "bought",
    "sell", "new", "name", "should", "would", "could", "can", "will", "high",
    "low", "find", "search", "try", "tried", "have", "had", "hadn", "haven",
    "read", "here", "there", "called", "type", "typical", "able", "has",
    "saw", "increase", "decrease", "seen", "which", "allows", "allow", "own",
    "until", "til", "today", "tomorrow", "easy", "hard", "each", "moveon",
    "fast", "slow", "cosa", "useful", "div", "href", "htm", "within", "issue",
    "long", "short", "empty", "anno", "anni", "less", "questo", "questa",
    "questi", "queste", "mens", "once", "twice", "above", "below", "alle",
    "kg", "inch", "inches", "meters", "meter", "normal", "too", "also",
    "prev", "links", "link", "service", "services", "map", "page", "pages",
    "login", "untitled", "frames", "frame", "tmp", "content", "htm", "piu",
    "edu", "etc", "ecc", "quot", "true", "false", "middot", "nbsp", "stop",
    "site", "websites", "often", "join", "subscribe", "never", "small", "large",
    "newsletter", "thing", "something", "net", "mailing"
  };


  private static StopWords self = new StopWords();
  
  private StopWords() {
    // build stop word set
    for (int i = 0; i < STOP_WORDS.length; i++) {
      stopSet.add(STOP_WORDS[i]);
    }
  }

  public static boolean isStopWord(String word) {
    return stopSet.contains(word.toLowerCase());
  }
  
 
}
