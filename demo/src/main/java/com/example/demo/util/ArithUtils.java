package com.example.demo.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * 算术运算相关<br>
 *
 * @author zhangjr@sunwayworld.com 2019-10-28
 */
public class ArithUtils {
    private static final int DEFAULT_DIV_SCALE = 16;
    private static final BigDecimal MAX_DEVIATION = new BigDecimal("1E-" + DEFAULT_DIV_SCALE);

    public static double eval(String expression) {
        return Calculator.eval(expression);
    }

    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static double add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(StringUtils.trim(v1));
        BigDecimal b2 = new BigDecimal(StringUtils.trim(v2.trim()));
        return b1.add(b2).doubleValue();
    }

    // 多个数据相加，公式计算中用到，beanshell不支持可变参数
    public static double add(List<String> strList){
        BigDecimal result = new BigDecimal(0);
        for(String str : strList){
            result = result.add(new BigDecimal(StringUtils.trim(str)));
        }
        return result.doubleValue();
    }

    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double sub(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(StringUtils.trim(v1));
        BigDecimal b2 = new BigDecimal(StringUtils.trim(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double mul(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(StringUtils.trim(v1));
        BigDecimal b2 = new BigDecimal(StringUtils.trim(v2));
        return b1.multiply(b2).doubleValue();
    }

    // 多个数据相乘
    public static double mul(List<String> strList){
        BigDecimal result = new BigDecimal(1);
        for(String str : strList){
            result = result.multiply(new BigDecimal(StringUtils.trim(str)));
        }
        return result.doubleValue();
    }

    public static double div(double v1, double v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    public static double div(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(StringUtils.trim(v1));
        BigDecimal b2 = new BigDecimal(StringUtils.trim(v2));
        return b1.divide(b2, DEFAULT_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double div(double v1, double v2, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double sqrt(double v, double n) {
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal d = new BigDecimal(Double.toString(n));

        BigDecimal x = b.divide(d, MathContext.DECIMAL128);

        while(x.subtract(x = sqrtIteration(x, b, d)).abs().compareTo(MAX_DEVIATION) > 0);

        return x.doubleValue();
    }

    public static double pow(double v, double p) {
        return Math.pow(v, p);
    }

    public static double round(double v, int scale) {
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double round(String v, int scale) {
        BigDecimal b = new BigDecimal(StringUtils.trim(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //----------------------------------------------------------------------
    // 私有方法
    //----------------------------------------------------------------------
    private static BigDecimal sqrtIteration(BigDecimal x, BigDecimal n, BigDecimal d) {
        return x.add(n.divide(x, MathContext.DECIMAL128)).divide(d, MathContext.DECIMAL128);
    }


    //----------------------------------------------------------------------
    // 私有类
    //----------------------------------------------------------------------
    private static class Calculator {
        private Stack<String> postfixStack = new Stack<String>();// 后缀式栈
        private Stack<Character> opStack = new Stack<Character>();// 运算符栈
        private int[] operatPriority = new int[] { 0, 3, 2, 1, -1, 1, 0, 2 };// 运用运算符ASCII码-40做索引的运算符优先级

        public static double eval(String expression) {
            return new Calculator().calculate(transform(expression));
        }

        /**
         * 将表达式中负数的符号更改
         *
         * @param expression
         *            例如-2+-1*(-3E-2)-(-1)+2POW3+4SQRT2 被转为 ~2+~1*(~3e~2)-(~1)+2p2+4s2
         * @return
         */
        private static String transform(String expression) {
            expression = expression.toLowerCase();
            expression = StringUtils.replace(expression, "sqrt", "s");
            expression = StringUtils.replace(expression, "pow", "p");

            char[] arr = expression.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == '-') {
                    if (i == 0) {
                        arr[i] = '~';
                    } else {
                        char c = arr[i - 1];
                        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == 'e' || c == 's' || c == 'p') {
                            arr[i] = '~';
                        }
                    }
                }
            }
            
            if(arr[0]=='~' && arr[1]=='('){
                arr[0]='-';
                return "0" + new String(arr);
            }else{
                return new String(arr);
            }
        }

        /**
         * 按照给定的表达式计算
         *
         * @param expression
         *            要计算的表达式例如:5+12*(3+5)/7
         * @return
         */
        public double calculate(String expression) {
            Stack<String> resultStack = new Stack<String>();
            prepare(expression);
            Collections.reverse(postfixStack);// 将后缀式栈反转
            String firstValue, secondValue, currentValue;// 参与计算的第一个值，第二个值和算术运算符
            while (!postfixStack.isEmpty()) {
                currentValue = postfixStack.pop();
                if (!isOperator(currentValue.charAt(0))) {// 如果不是运算符则存入操作数栈中
                    currentValue = currentValue.replace("~", "-");
                    resultStack.push(currentValue);
                } else {// 如果是运算符则从操作数栈中取两个值和该数值一起参与运算
                    secondValue = resultStack.pop();
                    firstValue = resultStack.pop();

                    // 将负数标记符改为负号
                    firstValue = firstValue.replace("~", "-");
                    secondValue = secondValue.replace("~", "-");

                    String tempResult = calculate(firstValue, secondValue, currentValue.charAt(0));
                    resultStack.push(tempResult);
                }
            }

            return Double.valueOf(resultStack.pop());
        }

        /**
         * 数据准备阶段将表达式转换成为后缀式栈
         *
         * @param expression
         */
        private void prepare(String expression) {
            opStack.push(',');// 运算符放入栈底元素逗号，此符号优先级最低
            char[] arr = expression.toCharArray();
            int currentIndex = 0;// 当前字符的位置
            int count = 0;// 上次算术运算符到本次算术运算符的字符的长度便于或者之间的数值
            char currentOp, peekOp;// 当前操作符和栈顶操作符
            for (int i = 0; i < arr.length; i++) {
                currentOp = arr[i];
                if (isOperator(currentOp)) {// 如果当前字符是运算符
                    if (count > 0) {
                        postfixStack.push(new String(arr, currentIndex, count));// 取两个运算符之间的数字
                    }
                    peekOp = opStack.peek();
                    if (currentOp == ')') {// 遇到反括号则将运算符栈中的元素移除到后缀式栈中直到遇到左括号
                        while (opStack.peek() != '(') {
                            postfixStack.push(String.valueOf(opStack.pop()));
                        }
                        opStack.pop();
                    } else {
                        while (currentOp != '(' && peekOp != ',' && compare(currentOp, peekOp)) {
                            postfixStack.push(String.valueOf(opStack.pop()));
                            peekOp = opStack.peek();
                        }
                        opStack.push(currentOp);
                    }
                    count = 0;
                    currentIndex = i + 1;
                } else {
                    count++;
                }
            }
            if (count > 1 || (count == 1 && !isOperator(arr[currentIndex]))) {// 最后一个字符不是括号或者其他运算符的则加入后缀式栈中
                postfixStack.push(new String(arr, currentIndex, count));
            }

            while (opStack.peek() != ',') {
                postfixStack.push(String.valueOf(opStack.pop()));// 将操作符栈中的剩余的元素添加到后缀式栈中
            }
        }

        /**
         * 判断是否为算术符号
         */
        private boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == 'p' || c == 's';
        }

        /**
         * 利用ASCII码-40做下标去算术符号优先级
         */
        public boolean compare(char cur, char peek) {// 如果是peek优先级高于cur，返回true，默认都是peek优先级要低
            if (cur == 'p' || cur == 's') {
                if (peek == '(' || peek == ')') {
                    return true;
                } else {
                    return false;
                }
            }

            if (peek == 'p' || peek == 's') {
                if (cur == '(' || cur == ')') {
                    return false;
                } else {
                    return true;
                }
            }

            boolean result = false;
            if (operatPriority[(peek) - 40] >= operatPriority[(cur) - 40]) {
                result = true;
            }
            return result;
        }

        /**
         * 按照给定的算术运算符做计算
         */
        private String calculate(String firstValue, String secondValue, char currentOp) {
            String result = "";
            switch (currentOp) {
                case '+':
                    result = String.valueOf(ArithUtils.add(firstValue, secondValue));
                    break;
                case '-':
                    result = String.valueOf(ArithUtils.sub(firstValue, secondValue));
                    break;
                case '*':
                    result = String.valueOf(ArithUtils.mul(firstValue, secondValue));
                    break;
                case '/':
                    result = String.valueOf(ArithUtils.div(firstValue, secondValue));
                    break;
                case 'p':
                    result = String.valueOf(ArithUtils.pow(NumberUtils.parseDouble(firstValue), NumberUtils.parseDouble(secondValue)));
                    break;
                case 's':
                    result = String.valueOf(ArithUtils.sqrt(NumberUtils.parseDouble(firstValue), NumberUtils.parseDouble(secondValue)));
                    break;
            }
            return result;
        }
    }
}
