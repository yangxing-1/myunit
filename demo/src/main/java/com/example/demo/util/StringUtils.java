package com.example.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


public class StringUtils {
    /**
     * 判断字符串是否为{@literal null}或空格或换行等
     */
    public static final boolean isBlank(final String target) {
        return target == null || target.length() == 0 || trim(target).length() == 0;
    }

    /**
     * 判断字符串是否为{@literal null}或空
     */
    public static final boolean isEmpty(final String target) {
        return target == null || target.length() == 0;
    }

    /**
     * 判断字符串是否含有另外一个字符串
     */
    public static final boolean contains(String str, String searchPattern) {
        if (isEmpty(str)) {
            return false;
        }

        if (isEmpty(searchPattern)) {
            return true;
        }

        return str.contains(searchPattern);
    }

    /**
     * 判断字符串是否含有另外一个字符串，不区分大小写
     */
    public static final boolean containsIgnoreCase(String str, String searchPattern) {
        if (isEmpty(str)) {
            return false;
        }

        if (isEmpty(searchPattern)) {
            return true;
        }

        return str.toLowerCase().contains(searchPattern.toLowerCase());
    }

    /**
     * 字符串为非空的情况下，第一个字母大写
     */
    public static final String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     * 字符串为非空的情况下，第一个字母小写
     */
    public static final String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    /**
     * 判断字符串是否有给定的前缀，区分大小写
     */
    public static final boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }

        if (prefix.length() > str.length()) {
            return false;
        }

        return str.startsWith(prefix);
    }

    /**
     * 判断字符串是否有给定的前缀，不区分大小写
     */
    public static final boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }

        if (prefix.length() > str.length()) {
            return false;
        }

        return str.substring(0, prefix.length()).toLowerCase().equals(prefix.toLowerCase());
    }

    /**
     * 判断字符串是否有给定的后缀，区分大小写
     */
    public static final boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }

        if (suffix.length() > str.length()) {
            return false;
        }

        return str.endsWith(suffix);
    }

    /**
     * 判断字符串是否有给定的后缀，不区分大小写
     */
    public static final boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }

        if (suffix.length() > str.length()) {
            return false;
        }

        return str.substring(str.length() - suffix.length()).toLowerCase().equals(suffix.toLowerCase());
    }

    /**
     * 扩展{@link String#trim()}方法，删除开头和结尾的空格、回车、水平制表符、换行等都要去掉
     *
     * @param target 目标{@link String}
     * @return 替换后新的字符串
     */
    public static final String trim(final String target) {
        if (target == null)
            return null;

        char[] chars = target.toCharArray();

        int start = 0;
        int end = chars.length;

        for (char c : chars) {
            if (Character.isWhitespace(c)) {
                start++;
            } else {
                break;
            }
        }

        for (int i = chars.length - 1; i > 0; i--) {
            if (Character.isWhitespace(chars[i])) {
                end--;
            } else {
                break;
            }
        }

        if (end > start) {
            return String.copyValueOf(chars, start, end - start);
        } else {
            return "";
        }
    }

    /**
     * 字符串中的子字符串替换为新的字符串
     */
    public static final <V> String replace(String target, String searchPattern, V replacement) {
        if (isEmpty(target) || isEmpty(searchPattern))
            return target;

        int start = 0;

        int end = target.indexOf(searchPattern, start);

        if (end == -1)
            return target;

        final StringBuilder sb = new StringBuilder(target.substring(0, start));

        final int searchPatternLen = searchPattern.length();

        while (end != -1) {
            sb.append(target.substring(start, end)).append(replacement);

            start = end + searchPatternLen;

            end = target.indexOf(searchPattern, start);
        }

        sb.append(target.substring(start));

        return sb.toString();
    }

    /**
     * 字符串中的第一个匹配的子字符串替换为新的字符串
     */
    public static final <V> String replaceFirst(String target, String searchPattern, V replacement) {
        if (isEmpty(target) || isEmpty(searchPattern))
            return target;

        int pos = target.indexOf(searchPattern);

        if (pos == -1)
            return target;

        return new StringBuilder(target.substring(0, pos)).append(replacement)
                .append(target.substring(pos + searchPattern.length())).toString();
    }

    /**
     * 字符串中的最后匹配的子字符串替换为新的字符串
     */
    public static final <V> String replaceLast(String target, String searchPattern, V replacement) {
        if (isEmpty(target) || isEmpty(searchPattern))
            return target;

        int pos = target.lastIndexOf(searchPattern);

        if (pos == -1)
            return target;

        return new StringBuilder(target.substring(0, pos)).append(replacement)
                .append(target.substring(pos + searchPattern.length())).toString();
    }

    public static final <V> String replaceIgnoreCase(String target, String searchPattern, V replacement) {
        if (isEmpty(target) || isEmpty(searchPattern))
            return target;

        int targetLength = target.length();
        int searchPatternLength = searchPattern.length();

        StringBuilder sb = new StringBuilder();

        int start = 0;
        int lastEnd = 0;
        while (start + searchPatternLength <= targetLength) {
            String next = target.substring(start, start + searchPatternLength);

            if (next.equalsIgnoreCase(searchPattern)) {
                sb.append(target.substring(lastEnd, start)).append(replacement);
                lastEnd = start + searchPatternLength;

                start = lastEnd;
            } else {
                start++;
            }
        }

        sb.append(target.substring(lastEnd));

        return sb.toString();
    }

    /**
     * 删除字符串开始的字符串
     */
    public static final String removeStart(String target, String searchPattern) {
        if (isEmpty(target) || isEmpty(searchPattern)) {
            return target;
        }

        if (target.startsWith(searchPattern)) {
            return target.substring(searchPattern.length());
        }

        return target;
    }

    /**
     * 删除字符串开始的字符串
     */
    public static final String removeStartIgnoreCase(String target, String searchPattern) {
        if (isEmpty(target) || isEmpty(searchPattern)) {
            return target;
        }

        if (target.toLowerCase().startsWith(searchPattern.toLowerCase())) {
            return target.substring(searchPattern.length());
        }

        return target;
    }

    /**
     * 删除字符串结尾的字符串
     */
    public static final String removeEnd(String target, String searchPattern) {
        if (isEmpty(target) || isEmpty(searchPattern)) {
            return target;
        }

        if (target.endsWith(searchPattern)) {
            return target.substring(0, target.length() - searchPattern.length());
        }

        return target;
    }

    /**
     * 删除字符串结尾的字符串，不区分大小写
     */
    public static final String removeEndIgnoreCase(String target, String searchPattern) {
        if (isEmpty(target) || isEmpty(searchPattern)) {
            return target;
        }

        if (target.toLowerCase().endsWith(searchPattern.toLowerCase())) {
            return target.substring(0, target.length() - searchPattern.length());
        }

        return target;
    }

    /**
     * 删除字符串中所有的空字符串（如：回车、Tab、空格等等）
     */
    public static final String trimAllWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int len = str.length();
        final StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; i++) {
            final char c = str.charAt(i);
            if (!Character.isSpaceChar(c) && !Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 替换被限定在{@code span}标签里的子串{@link String}不含标记，用{@code Map}中的Value替换<br>
     * {@code span}的{@code id}为{@code Map}中的Key，Key对应的值将替换标签里的子串{@link String}<br>
     * 格式为：&lt;span id='key'&gt;xxxxx&lt;/span&gt;
     *
     * @param target 目标{@link String}被搜索和替换
     * @param map    含有要替换Value的{@code Map}
     * @return 替换完的字段
     */
    public static final String replaceBetweenSpan(final String target, final Map<String, Object> map) {
        if (map.isEmpty()) {
            return target;
        }

        String replaceTarget = target;

        final Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            final Entry<String, Object> entry = iterator.next();

            final String regex = "<span\\s+id\\s*=\\s*('|\")" + entry.getKey() + "('|\")\\s*>(.|\\s)*?</span>"; // 匹配的正则表达式

            final Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE); // 不区分大小写

            final Matcher m = p.matcher(replaceTarget);

            replaceTarget = m.replaceAll("<span id='" + entry.getKey() + "'>" + entry.getValue() + "</span>");
        }

        return replaceTarget;
    }

    /**
     * 重复指定次数的字符串
     */
    public static final String repeat(String source, int repeat) {
        if (repeat <= 0 || StringUtils.isEmpty(source)) {
            return "";
        }

        final StringBuilder sb = new StringBuilder(source.length() * repeat);

        for (int i = 0; i < repeat; i++) {
            sb.append(source);
        }

        return sb.toString();
    }

    /**
     * 字符串{@link str}含有子字符串{@link str}的数量
     */
    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }

        int count = 0;
        int subLength = sub.length();

        for (int i = 0; i <= str.length() - subLength;) {
            if (sub.equals(str.substring(i, i + subLength))) {
                count++;

                i += subLength;
            } else {
                i++;
            }
        }

        return count;
    }

    /**
     * 类似{@link String#split(String)}方法，但是这里不用正则表达式，且对于{@value null}是安全的
     */
    public static String[] split(String str, String delimiter) {
        if (str == null) {
            return new String[0];
        }

        if (StringUtils.isEmpty(delimiter)) {
            return new String[] { str };
        }

        final List<String> container = new ArrayList<String>();

        int currentIndex = 0;
        int lastIndex = 0;

        int delimiterLength = delimiter.length();

        while ((currentIndex = str.indexOf(delimiter, lastIndex)) > -1) {
            container.add(str.substring(lastIndex, currentIndex));

            lastIndex = currentIndex + delimiterLength;
        }

        if (lastIndex <= str.length()) {
            container.add(str.substring(lastIndex));
        }

        return container.toArray(new String[0]);
    }

    /**
     * 同{@link String#split(String)}方法，但是对于{@value null}是安全的
     */
    public static String[] regexSplit(String str, String regex) {
        if (str == null) {
            return new String[0];
        }

        if (StringUtils.isEmpty(regex)) {
            return new String[] { str };
        }

        return str.split(regex);
    }

    /**
     * 对于指定的字符串左侧补零
     *
     * @param source 要补零的字符串
     * @param length 补零后的长度
     * @return 补零后的字符串
     * @see #pad(String, int, boolean)
     */
    public static final String leftPad(String source, int length) {
        return pad(source, length, true);
    }

    /**
     * 对于指定的字符串右侧补零
     *
     * @param source 要补零的字符串
     * @param length 补零后的长度
     * @return 补零后的字符串
     * @see #pad(String, int, boolean)
     */
    public static final String rightPad(String source, int length) {
        return pad(source, length, false);
    }

    

    /**
     * 获取指定长度的UUID，删除“-”字符
     */
    public static String randomUUID(int size) {
        return randomUUID().substring(0, size);
    }

    /**
     * 获取UUID，删除“-”字符
     */
    public static String randomUUID() {
        UUID uuid = UUID.randomUUID();

        return StringUtils.replace(uuid.toString(), "-", "");
    }

    /*********************************************************************************
     * Private Method
     *********************************************************************************/
    /**
     * 字符串为非空情况下，更新首字母的大写或小写
     */
    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        if (capitalize) {
            sb.append(Character.toUpperCase(str.charAt(0)));
        } else {
            sb.append(Character.toLowerCase(str.charAt(0)));
        }
        sb.append(str.substring(1));
        return sb.toString();
    }

    /**
     * 对于指定的字符串根据指定的参数左侧或右侧补零
     *
     * @param source       要补零的字符串
     * @param length       补零后的长度
     * @param leftPosition 是否左侧补零
     * @return 补零后的字符串
     */
    private static final String pad(String source, int length, boolean leftPosition) {
        if (StringUtils.isEmpty(source)) {
            return StringUtils.repeat("0", length);
        } else if (length <= 0 || source.length() >= length) {
            return source;
        }

        if (leftPosition) {
            return StringUtils.repeat("0", length - source.length()) + source;
        } else {
            return source + StringUtils.repeat("0", length - source.length());
        }
    }

    /**
     * 判断两个字符串是否是相等（大小写不敏感）
     */
    public static final boolean equalsIgnoreCase(String v1, String v2) {
        if (v1 == null) {
            return v2 == null;
        }

        if (v2 == null) {
            return v1 == null;
        }

        return v1.equalsIgnoreCase(v2);
    }

    /**
     * 判断两个字符串是否是相等（大小写敏感）
     */
    public static final boolean equals(String v1, String v2) {
        if (v1 == null) {
            return v2 == null;
        }

        if (v2 == null) {
            return v1 == null;
        }

        return v1.equals(v2);
    }

    /**
     * 反转指定的字符串
     */
    public static final String reverse(String str) {
        if (str == null) {
            return null;
        }

        return new StringBuilder(str).reverse().toString();
    }

    public static final String lowerCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toLowerCase();
    }

    public static final Object lowerCaseIfPossible(Object value) {
        if (value == null || !(value instanceof String)) {
            return value;
        }

        return ((String) value).toLowerCase();
    }

    public static final String upperCase(String value) {
        if (value == null || !(value instanceof String)) {
            return value;
        }

        return ((String) value).toUpperCase();
    }

    public static final String abbreviate(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        
        int length = text.length();
        
        if (length > 240) {
            return text.substring(0, 240) + "...";
        }
        
        return text;
    }

    public static final boolean containsNumber(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        
        return text.matches(".*\\d+.*");
    }

    public static final boolean containsLowercaseLetter(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        
        return text.matches(".*[a-z]+.*");
    }

    public static final boolean containsCapitalLetter(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        
        return text.matches(".*[A-Z]+.*");
    }

    public static final boolean containsSpecialCharacter(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        
        return text.matches(".*[`~!@#$%^&*()_\\+\\-={}\\[\\]|:;\"\'<>,.?/\\\\]+.*");
    }
}
