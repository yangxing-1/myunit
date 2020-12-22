package com.example.demo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import bsh.EvalError;
import bsh.Interpreter;

public class RouterUtils {
    /**
     * -执行分支表达式,表达式中包含${}或#{}处理得到结果后统一交给javascript引擎判断真假
     * @param expression 表达式字符串
     * @param paramObj 业务对象
     * @param paramName 表达式中业务对象的实例名，仅对java表达式有效
     * @return
     */
    public static boolean evalExpression(String expression, Object paramObj, String paramName) {
        // 处理转义{}和[]
        String customExpression = revertKeyWord(transformKeyWord(expression));
        // 处理自定义的表达式,被&{}块包含
        List<String> customstringList = RouterUtils.substringsBetween(expression, "${", "}");
        for (String customstring : customstringList) {
            Object value = getValue(paramObj, customstring);
            if (value == null) {
                return false;
            }
            if (value != null && Number.class.isAssignableFrom(value.getClass())) {
                customExpression = StringUtils.replace(customExpression, "${" + customstring + "}", value.toString());
            } else {
                customExpression = StringUtils.replace(customExpression, "${" + customstring + "}", padSingleQuotes(value.toString()));
            }
        }
        // 处理Java语法的表达式,被#{}块包含
        List<String> javastringList = RouterUtils.substringsBetween(customExpression, "#{", "}");
        for (String javastring : javastringList) {
            Interpreter interpreter = new Interpreter();
            Object value = null;
            try {
                interpreter.set(paramName, paramObj);
                value = interpreter.eval(javastring);
            } catch (EvalError e) {
                //e.printStackTrace();
                
            }
            if (value == null) {
                return false;
            }
            if (value != null && Number.class.isAssignableFrom(value.getClass())) {
                customExpression = StringUtils.replace(customExpression, "#{" + javastring + "}", value.toString());
            } else {
                customExpression = StringUtils.replace(customExpression, "#{" + javastring + "}", padSingleQuotes(value.toString()));
            }
        }
        try {
            return ScriptUtils.evalJavaScript(customExpression);
        } catch (Exception ex) {
            System.out.println("表达式执行出错，解析前的表达式为："+expression+"\t解析后的表达式为："+customExpression);
        }
        return false;
    }

    /**
     * -自定义表达式根据参数类型选择不同的计算方式
     * @param paramObj 目前支持的类型有：json、xml、jdbc结果集
     * @param substring
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Object getValue(Object paramObj, String substring) {
        if (paramObj.getClass().isAssignableFrom(JsonObject.class)) {
            return RouterUtils.getJsonValue((JsonObject)paramObj, substring);
        } else if (paramObj instanceof  Document) {
            return RouterUtils.getXmlValue((Document)paramObj, substring);
        } else if (paramObj instanceof List<?>) {
            List<?> listObj = (List<?>) paramObj;
            if (listObj.isEmpty()) {
                return null;
            }
            if (listObj.get(0) instanceof Map) {
                return RouterUtils.getJdbcValue((List<Map<String, Object>>)paramObj, substring);
            }
        } else {
            System.out.println("错误的类型");
        }
        return null;
    }

    /**
     * -计算json对象表达式的结果
     * @param paramObj json对象
     * @param substring 表达式字符串
     * @return
     */
    public static Object getJsonValue(Object jsonObj, String substring) {
        try {
            String[] exp = conditionToList(substring);
            for (int i = 0; i < exp.length; i++) {
                List<String> conditionList = RouterUtils.substringsBetween(exp[i], "[", "]");
                if (conditionList.isEmpty()) {
                    jsonObj = ((JsonObject)jsonObj).get(exp[i]);
                } else {
                    String condition = conditionList.get(0);
                    String property = StringUtils.replace(exp[i], "[" + condition + "]", "");
                    JsonArray jsonArray = ((JsonObject)jsonObj).getAsJsonArray(property);
                    if (condition != null && condition.matches("^\\d+$")) {
                        jsonObj = jsonArray.get(Integer.valueOf(condition));
                    } else {
                        String filter = convertPackageFilter(condition, "e", "json");
                        Object findObj = null;
                        for (Object e : jsonArray) {
                            boolean pass = (boolean)evalExpression(filter, e, "e");
                            if (pass) {
                                findObj = e;
                                break;
                            }
                        }
                        if (findObj == null) {
                            return null;
                        } else {
                            jsonObj = findObj;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("获取json对象节点属性出错");
            return null;
        }
        return jsonObj;
    }

    /**
     * -计算xml对象表达式的结果
     * @param xmlObj xml对象
     * @param substring 表达式字符串
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object getXmlValue(Document xmlObj, String substring) {
        try {
            Element element = xmlObj.getRootElement();
            String[] exp = conditionToList(substring);
            for (int i = 0; i < exp.length; i++) {
                if (i == exp.length - 1) {
                    // 最后一个是取值
                    return element.attribute(exp[i]).getData();
                }
                List<String> conditionList = RouterUtils.substringsBetween(exp[i], "[", "]");
                if (conditionList.isEmpty()) {
                    element = element.element(exp[i]);
                } else {
                    String condition = conditionList.get(0);
                    String property = StringUtils.replace(exp[i], "[" + condition + "]", "");
                    List<Element> elementList = element.elements(property);
                    if (condition != null && condition.matches("^\\d+$")) {
                        element = elementList.get(Integer.valueOf(condition));
                    } else {
                        String filter = convertPackageFilter(condition, "e", "xml");
                        Element findObj = null;
                        for (Element e : elementList) {
                            boolean pass = (boolean)evalExpression(filter, e, "e");
                            if (pass) {
                                findObj = e;
                                break;
                            }
                        }
                        if (findObj == null) {
                            return null;
                        } else {
                            element = findObj;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("获取xml对象节点属性出错");
            return null;
        }
        return null;
    }

    /**
     * -计算jdbc结果集对象表达式的结果
     * @param paramObj
     * @param substring
     * @return
     */
    public static Object getJdbcValue(List<Map<String, Object>> jdbcObj, String substring) {
        try {
            String[] exp = conditionToList(substring);
            if (exp.length != 2) {
                return null;
            }
            String condition = exp[0].substring(exp[0].indexOf("[") + 1, exp[0].lastIndexOf("]"));
            Map<String, Object> map = null;
            if (condition != null && condition.matches("^\\d+$")) {
                map = jdbcObj.get(Integer.valueOf(condition));
            } else {
                String filter = convertPackageFilter(condition, "e", "jdbc");
                for (Map<String, Object> e : jdbcObj) {
                    boolean pass = (boolean)evalExpression(filter, e, "e");
                    if (pass) {
                        map = e;
                        break;
                    }
                }
            }
            if (map == null) {
                return null;
            } else {
                return map.get(exp[1]);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("获取jdbc对象节点属性出错");
            return null;
        }
    }

    /**
     * -转换数组定位过滤条件语法
     * @param condition 自定义筛选条件类似：a="1" && b<2
     * @param paramName 业务对象实例名
     * @param type
     * @return
     */
    private static String convertPackageFilter(String condition, String paramName, String type) {
        int index = 0, start = 0;
        String filter = "";
        String conditionCopy = condition;
        String qout = "";
        while (index > -1) {
            filter += " "+qout+" ";
            int indexAnd = conditionCopy.indexOf("&&");
            int indexOr = conditionCopy.indexOf("||");
            if (indexAnd == -1) {
                if (indexOr == -1) {
                    start = -1;
                } else {
                    start = indexOr;
                    qout = "||";
                }
            } else {
                if (indexOr == -1) {
                    start = indexAnd;
                    qout = "&&";
                } else {
                    if (indexAnd < indexOr) {
                        start = indexAnd;
                        qout = "&&";
                    } else {
                        start = indexOr;
                        qout = "||";
                    }
                }
            }
            if (start > 0) {
                String el = conditionCopy.substring(0, start);
                String ret = RouterUtils.toJavaExpression(el, paramName, type);
                filter += ret;
                conditionCopy = conditionCopy.substring(start + 2);
            } else {
                String ret = RouterUtils.toJavaExpression(conditionCopy, paramName, type);
                filter += ret;
            }
            index = start;
        }
        return filter;
    }

    /**
     * -将a=b 或 c>d这种自定义的表达式转换为java语法的表达式
     * @param exp
     * @param obj
     * @param type 对象类型分为json、xml、jdbc
     * @return
     */
    public static String toJavaExpression(String exp, String obj, String type) {
        //取出左右两侧的括号
        exp = exp.trim();
        String left = "", right = "";
        while (exp.startsWith("(")) {
            left += "(";
            exp = exp.substring(1);
        }
        while (exp.endsWith(")")) {
            right += ")";
            exp = exp.substring(0, exp.length() - 1);
        }
        if (exp.contains("!=")) {
            int index = exp.indexOf("!=");
            String property = exp.substring(0, index).trim().replace("\"", "").replace("'", "");
            String value = exp.substring(index + 2, exp.length()).trim();
            //return left + "!\"" +value+"\".equals("+getPropertyValueExpression(type, obj, property)+")" + right;
            return left+"#{"+getPropertyValueExpression(type, obj, property)+"}!="+ value + right;
        } else if (exp.contains("=") && !exp.contains("!=")) {
            int index = exp.indexOf("=");
            String property = exp.substring(0, index).trim().replace("\"", "").replace("'", "");
            String value = exp.substring(index + 1, exp.length()).trim();
            //return left + "\"" +value+"\".equals("+getPropertyValueExpression(type, obj, property)+")" + right;
            return left+"#{"+getPropertyValueExpression(type, obj, property)+"}=="+ value + right;
        } else if (exp.contains("<")) {
            int index = exp.indexOf("<");
            String property = exp.substring(0, index).trim().replace("\"", "").replace("'", "");
            String value = exp.substring(index + 1, exp.length()).trim();
            //return left + getPropertyValueExpression(type, obj, property) +" < \"" +value+"\"" + right;
            return left +"#{"+ getPropertyValueExpression(type, obj, property) +"} < \"" +value+"\"" + right;
        } else if (exp.contains(">")) {
            int index = exp.indexOf(">");
            String property = exp.substring(0, index).trim().replace("\"", "").replace("'", "");
            String value = exp.substring(index + 1, exp.length()).trim();
            //return left + getPropertyValueExpression(type, obj, property) + " > \"" +value+"\"" + right;
            return left +"#{"+ getPropertyValueExpression(type, obj, property) + "} > \"" +value+"\"" + right;
        }
        return null;
    }

    /**
     * -不同类型对象获取属性值的语法
     * @param type
     * @param obj
     * @param property
     * @return
     */
    private static String getPropertyValueExpression(String type, String obj, String property) {
        switch (type) {
        case "json":
        case "jdbc":
            return obj+".get(\""+property+"\")";
        case "xml":
            return obj+".attribute(\""+property+"\").getData()";
        default:
            break;
        }
        return null;
    }

    /**
     * -将获取属性值的字符串转化为数组，按点号解析，中括号不解析
     * @param substring 属性递归字符串，类似：obj1.obj2.obj3.value1
     * @return
     */
    public static String[] conditionToList(String str) {
        if (str == null) {
            return new String[0];
        }
        if (StringUtils.isEmpty(".")) {
            return new String[] { str };
        }
        final List<String> container = new ArrayList<String>();
        int nextIndex = 0;
        int lastIndex = 0;
        int pos = 0;
        int delimiterLength = ".".length();
        while ((nextIndex = str.indexOf(".", pos)) > -1) {
            String currentStr = str.substring(lastIndex, nextIndex);
            if (currentStr.contains("[") && !currentStr.contains("]")) {
                pos++;
                continue;
            }
            container.add(currentStr);
            lastIndex = nextIndex + delimiterLength;
            pos = lastIndex;
        }
        if (lastIndex <= str.length()) {
            container.add(str.substring(lastIndex));
        }
        return container.toArray(new String[0]);
    }

    /**
     * -搜索被限定在一组标记里面的所有子串{@link String}，例如:
     * StringUtils.substringsBetween("[a][b][c]", "[", "]") 将会返回含有 "a""b","c"的
     * {@link String} {@link List}
     *
     * @param target   用于提取的目标{@link String}
     * @param open     子串开始的标记
     * @param closeTag 子串关闭的标记
     * @return {@link String} {@link List}
     */
    public static List<String> substringsBetween(final String target, final String openTag, final String closeTag) {
        final String transformStr = transformKeyWord(target);
        final List<String> subStrings = new ArrayList<String>();

        if (StringUtils.isEmpty(transformStr) || StringUtils.isEmpty(openTag) || StringUtils.isEmpty(closeTag)) {
            return subStrings;
        }
        final int strLen = transformStr.length();

        if (strLen == 0) {
            return subStrings;
        }

        final int closeLen = closeTag.length();
        final int openLen = openTag.length();

        int pos = 0;
        while (pos < strLen - closeLen) {
            int start = transformStr.indexOf(openTag, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            final int end = transformStr.indexOf(closeTag, start);
            if (end < 0) {
                break;
            }
            subStrings.add(revertKeyWord(transformStr.substring(start, end)));
            pos = end + closeLen;
        }

        return subStrings;
    }
    
    /**
     * 转义解析字符
     * @param str
     * @return
     */
    public static String transformKeyWord(String str) {
        return str.replace("\\{", "@bl@").replace("\\}", "@br@").replace("\\[", "@ml@").replace("\\]", "@mr@");
    }
    
    /**
     * 还原解析字符
     * @param str
     * @return
     */
    public static String revertKeyWord(String str) {
        return str.replace("@bl@", "{").replace("@br@", "}").replace("@ml@", "[").replace("@mr@", "]");
    }
    
    /**
     * 字符串首位加单引号
     * @param str
     * @return
     */
    public static String padSingleQuotes(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str;
        }
        if (str.startsWith("'") && str.endsWith("'")) {
            return str;
        }
        return "'" + str + "'";
    }
}
