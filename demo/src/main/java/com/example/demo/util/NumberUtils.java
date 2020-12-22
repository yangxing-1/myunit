package com.example.demo.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import com.example.demo.exception.ConversionFailedException;
import com.example.demo.exception.UnexpectedException;
import com.example.demo.exception.UnsupportedException;


public class NumberUtils {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[-+]*(\\.\\d+|\\d+\\.{0,1}\\d*)([Ee][+-]?[\\d]+)?$");
    private static final Pattern SCIENTIFIC_NUMBER_PATTERN = Pattern.compile("^[-+]*(\\.\\d+|\\d+\\.{0,1}\\d*)[Ee]{1}[+-]?[\\d]+$");

    private static final String[] CHINESE_FRACTIONS = { "角", "分" };
    private static final String[] CHINESE_DIGITS = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
    private static final String[] CHINESE_UNITS = { "", "拾", "佰", "仟", "元", "万", "亿" };

    /**
     * Test the give text is number or not.
     */
    public static final boolean isNumber(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        return NUMBER_PATTERN.matcher(text).matches();
    }

    /**
     * Test the give text is scientific number or not.
     */
    public static final boolean isScientificNumber(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        return SCIENTIFIC_NUMBER_PATTERN.matcher(text).matches();
    }

    /**
     * Format the given number to chinese style.
     */
    public static final <T extends Number> String formatToChinese(T number) {
        String strNumber = formatNumber(number, 2, true);

        StringBuilder chineseBuilder = new StringBuilder();
        boolean hasFraction = strNumber.contains("."); // 是否有分数

        if (StringUtils.startsWith(strNumber, "-")) {
            chineseBuilder.append("负");
            strNumber = StringUtils.removeStart(strNumber, "-");
        }

        String intStrNumber = (hasFraction ? strNumber.substring(0, strNumber.indexOf(".")) : strNumber);

        char[] chars = intStrNumber.toCharArray();

        if (chars.length > 16) {
            //throw new OutOfRangeException("The number to format chinese must be less than ten million billion.");
            throw new UnexpectedException("The number to format chinese must be less than ten million billion.");
        }

        for (int i = 0, j = chars.length; i < j; i++) {
            char c = chars[i];
            chineseBuilder.append(CHINESE_DIGITS[Character.getNumericValue(c)]);
            // 赋 值{"", "拾", "佰", "仟"}
            int index = (j - i) % 4;
            if (index == 0) {
                index = 4;
            }
            index = index - 1;
            chineseBuilder.append(CHINESE_UNITS[index]);

            /**
             * 亿：位数为9 万：位数为 4 * (2 * n + 1) + 1，其中n为0开始的整数
             */
            if (j - i == 9) {
                chineseBuilder.append("亿");
            } else if ((j - i) % 4 == 1 && (((j - i) - 1) / 4) % 2 == 1) {
                chineseBuilder.append("万");
            }
        }

        chineseBuilder.append("元");

        String chinese = chineseBuilder.toString();

        // 数字大写特殊处理
        chinese = chinese.replaceAll("零[佰|仟|拾]", "零");
        chinese = chinese.replaceAll("零+", "零");
        chinese = chinese.replaceAll("零亿", "亿");
        chinese = chinese.replaceAll("零万", "万");
        chinese = chinese.replaceAll("亿万", "亿");

        if (!chinese.equals("负零元") && !chinese.equals("零元")) {
            chinese = chinese.replaceAll("零元", "元");
        }

        if (hasFraction) { // 处理角分
            String fractionStrNumber = strNumber.substring(strNumber.indexOf(".") + 1);
            char[] fractionChars = fractionStrNumber.toCharArray();

            for (int i = 0; i < fractionChars.length; i++) {
                if (i == 0) {
                    int numericValue = Character.getNumericValue(fractionChars[0]);
                    chinese += CHINESE_DIGITS[numericValue];

                    if (numericValue > 0) {
                        chinese += CHINESE_FRACTIONS[i];
                    }
                } else if (i == 1) {
                    chinese += CHINESE_DIGITS[Character.getNumericValue(fractionChars[1])] + CHINESE_FRACTIONS[i];
                } else {
                    throw new UnexpectedException("Oops, are you kidding me?");
                }
            }

            return chinese;
        } else {
            return chinese + "整";
        }
    }

    public static final <T extends Number> String formatNumber(T number, int precision, boolean significantDigit) {
        number = getCorrectedNumber(number);

        String numberStr = String.format("%1$." + precision + "f", number);

        if (significantDigit) {
            while (numberStr.contains(".") && StringUtils.endsWith(numberStr, "0")) {
                numberStr = StringUtils.removeEnd(numberStr, "0");
            }

            if (StringUtils.endsWith(numberStr, ".")) {
                return StringUtils.removeEnd(numberStr, ".");
            }

            return numberStr;
        }

        return numberStr;
    }

    /**
     * Print the given {@code number} for display.
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Number> String formatNumber(T number) {
        if (number == null) {
            return "";
        }

        Class<T> numberClass = (Class<T>) number.getClass();
        if (numberClass.isPrimitive()) {
            numberClass = (Class<T>) ClassUtils.getWrapperType(numberClass);
        }

        number = getCorrectedNumber(number);

        if (Float.class == numberClass || Double.class == numberClass || BigDecimal.class == numberClass) {
            if ((Float.class == numberClass
                    && Float.isNaN((Float) number))
                    || (Double.class == numberClass
                    && Double.isNaN((Double) number))) {
                return "";
            }

            return new DecimalFormat("#.##########").format(number);
        } else if (Byte.class == numberClass || Short.class == numberClass || Integer.class == numberClass
                || Long.class == numberClass || BigInteger.class == numberClass) {
            return new DecimalFormat("#").format(number);
        } else {
            throw new UnsupportedException("Print " + number + ".");
        }
    }

    public static final <T extends Number> String scientificNotationFormat(T number, int precision) {
        if (number == null) {
            return "";
        }

        double d = number.doubleValue();

        if (d == 0d) {
            return formatNumber(d, precision, false) + "E+0";
        }

        StringBuilder sb = new StringBuilder();

        if (d < 0) {
            sb.append("-");
            d = -d;
        }

        if (d >= 1) {
            int qty = 0;

            while(d >= 10) {
                qty++;

                d = ArithUtils.div(d, 10);
            }

            sb.append(formatNumber(d, precision, false)).append("E+").append(qty);
        } else {
            int qty = 0;

            while(d < 1) {
                qty++;

                d = ArithUtils.mul(d, 10);
            }

            sb.append(formatNumber(d, precision, false)).append("E-").append(qty);
        }

        return sb.toString();
    }

    /**
     * Parse the given {@code text} into a {@link Number} instance of the given
     * target class.
     * <p>
     * Remove all spaces and comma of the input {@code String} before attempting to
     * parse the number.
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Number> T parseNumber(String text, Class<T> targetClass) {
        if (StringUtils.isEmpty(text)) {
            if (targetClass.isPrimitive()) {
                return (T) ClassUtils.getPrimitiveDefaultValue(targetClass);
            } else {
                return null;
            }
        }

        if (ClassUtils.isPrimitiveType(targetClass)) {
            targetClass = (Class<T>) ClassUtils.getWrapperType(targetClass);
        }

        // get the accurate number, to avoid incorrect precision
        String newText = getAccurateNumber(text);

        if (Byte.class == targetClass) {
            return (T) (isHexNumber(newText) ? Byte.decode(newText) : Byte.valueOf(newText));
        } else if (Short.class == targetClass) {
            return (T) (isHexNumber(newText) ? Short.decode(newText) : Short.valueOf(newText));
        } else if (Integer.class == targetClass) {
            return (T) (isHexNumber(newText) ? Integer.decode(newText) : Integer.valueOf(newText));
        } else if (Long.class == targetClass) {
            return (T) (isHexNumber(newText) ? Long.decode(newText) : Long.valueOf(newText));
        } else if (BigInteger.class == targetClass) {
            return (T) (isHexNumber(newText) ? decodeBigInteger(newText) : new BigInteger(newText));
        } else if (Float.class == targetClass) {
            return (T) Float.valueOf(newText);
        } else if (Double.class == targetClass) {
            return (T) Double.valueOf(newText);
        } else if (BigDecimal.class == targetClass || Number.class == targetClass) {
            return (T) new BigDecimal(newText);
        } else {
            throw new ConversionFailedException(String.class, targetClass, text);
        }
    }

    public static <T extends Number> T max(T t1, T t2) {
        if (t1 == null) {
            return t2;
        }

        if (t2 == null) {
            return t1;
        }

        if (t1.doubleValue() > t2.doubleValue()) {
            return t1;
        } else {
            return t2;
        }
    }

    public static <T extends Number> T min(T t1, T t2) {
        if (t1 == null) {
            return t2;
        }

        if (t2 == null) {
            return t1;
        }

        if (t1.doubleValue() > t2.doubleValue()) {
            return t2;
        } else {
            return t1;
        }
    }

    // ---------------------------------------------------------------------
    // convenient methods
    // ---------------------------------------------------------------------
    public static <T> long parseLong(T text) {
        return ConvertUtils.convert(text, long.class);
    }

    public static <T> long parseLong(T text, long defaultValue) {
        try {
            return ConvertUtils.convert(text, long.class);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static <T> double parseDouble(T text) {
        return ConvertUtils.convert(text, double.class);
    }

    public static <T> double parseDouble(T text, double defaultValue) {
        try {
            return ConvertUtils.convert(text, double.class);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static <T> float parseFloat(T text) {
        return ConvertUtils.convert(text, float.class);
    }

    public static <T> float parseFloat(T text, float defaultValue) {
        try {
            return ConvertUtils.convert(text, float.class);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static <T> int parseInt(T text) {
        return ConvertUtils.convert(text, int.class);
    }

    public static <T> int parseInt(T text, int defaultValue) {
        try {
            return ConvertUtils.convert(text, int.class);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static <T> short parseShort(T text) {
        return ConvertUtils.convert(text, short.class);
    }

    public static <T> short parseShort(T text, short defaultValue) {
        try {
            return ConvertUtils.convert(text, short.class);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static <T> BigInteger parseBigInteger(T text) {
        return ConvertUtils.convert(text, BigInteger.class);
    }

    public static <T> BigInteger parseBigInteger(T text, BigInteger defaultValue) {
        try {
            return ConvertUtils.convert(text, BigInteger.class, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static <T> BigDecimal parseBigDecimal(T text) {
        return ConvertUtils.convert(text, BigDecimal.class);
    }

    public static <T> BigDecimal parseBigDecimal(T text, BigDecimal defaultValue) {
        try {
            return ConvertUtils.convert(text, BigDecimal.class, defaultValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /**
     * 转换{@code T}类型的值为{@link Double}，如果提供的{@Ling Object}为空，那么返回{@code 0}
     *
     * @param number 要转换的值
     * @return {@link Double}类型的数值
     */
    public static final <T> double toDouble(final T number) {
        if (number == null) {
            return 0d;
        }

        return ConvertUtils.convert(number.toString(), double.class, 0d);
    }

    /**
     * 转换{@code T}类型的值为金额类型的{@link Double}
     *
     * @param number 要转换的值
     * @return {@link Double}类型的金额
     */
    public static final <T> double toCurrencyDouble(final T number) {
        return toDouble(number, 2);
    }

    /**
     * 转换{@code T}类型的值为单价类型的{@link Double}
     *
     * @param number 要转换的值
     * @return {@link Double}类型的单价
     */
    public static final <T> double toUnitPriceDouble(final T number) {
        return toDouble(number, 6);
    }

    /**
     * 转换{@code T}类型的值为数量类型的{@link Double}
     *
     * @param number 要转换的值
     * @return {@link Double}类型的单价
     */
    public static final <T> double toQuantityDouble(final T number) {
        return toDouble(number, 4);
    }

    /**
     * 转换{@code T}类型的值为{@link Double}，如果提供的{@Ling Object}为空，那么返回{@code 0}<br>
     * 并根据提供的精度进行四舍五入操作
     *
     * @param number    要转换的值
     * @param precision 输出的精度
     * @return {@link Double}类型的数值
     */
    public static final <T> double toDouble(final T number, int precision) {
        if (number == null) {
            return 0d;
        }

        double d = toDouble(number);

        if (precision == 0) {
            return d;
        }

        return new BigDecimal(Double.toString(d)).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 格式化字符串为2进制数字
     *
     * @param number 字符串类型的数字
     * @return {@link Integer}类型的数值
     */
    public static final int toBinaryNumber(final String number) {
        if (number == null) {
            return 0;
        }

        try {
            return Integer.valueOf(number, 2);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public static final boolean isGreater(Number n1, Number n2) {
        if (n1 == null) {
            return false;
        }

        if (n2 == null) {
            return true;
        }

        return n1.doubleValue() > n2.doubleValue();
    }

    public static final boolean isLess(Number n1, Number n2) {
        if (n1 == null) {
            return n2 != null;
        }

        if (n2 == null) {
            return false;
        }

        return n1.doubleValue() < n2.doubleValue();
    }

    // ---------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------
    /**
     * Determine whether the given {@code value} String indicates a hex number, i.e.
     * needs to be passed into {@code Integer.decode} instead of
     * {@code Integer.valueOf}, etc.
     */
    private static boolean isHexNumber(String value) {
        int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }

    /**
     * Decode a {@link java.math.BigInteger} from the supplied {@link String} value.
     * <p>
     * Supports decimal, hex, and octal notation.
     */
    private static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;

        // Handle minus sign, if present.
        if (value.startsWith("-")) {
            negative = true;
            index++;
        }

        // Handle radix specifier, if present.
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }

        BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate() : result);
    }

    /**
     * correct the given double or float number, to fix double precision
     * problem.<br>
     * however, it is not the perfect solution, but a best solution.
     */
    private static final <T extends Number> T getCorrectedNumber(T number) {
        @SuppressWarnings("unchecked")
        Class<T> numberClass = (Class<T>) number.getClass();
        if (numberClass.equals(Double.class) || numberClass.equals(double.class) || numberClass.equals(Float.class)
                || numberClass.equals(float.class)) {
            return parseNumber(getAccurateNumber("" + number), numberClass);
        }

        return number;
    }

    /**
     * the incorrect double precision
     */
    private static final Pattern DOUBLE_INCORRECT_PRECISION = Pattern.compile("\\.[0-9]+9{7}[0-9]?$");

    private static String getAccurateNumber(final String strNumber) {
        final String noCommaStrNumber = StringUtils.trim(StringUtils.replace(strNumber, ",", ""));

        if (DOUBLE_INCORRECT_PRECISION.matcher(noCommaStrNumber).find()) {
            final int length = noCommaStrNumber.substring(noCommaStrNumber.indexOf(".")).length() - 3;

            StringBuilder offset = new StringBuilder("0.");
            for (int i = 0; i < length; i++) {
                offset.append("0");
            }
            offset.append("1");

            final Double d = new BigDecimal(noCommaStrNumber).doubleValue() + new Double(offset.toString());

            final DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(6);
            df.setGroupingUsed(false);

            return df.format(d);
        } else {
            return noCommaStrNumber;
        }
    }
}
