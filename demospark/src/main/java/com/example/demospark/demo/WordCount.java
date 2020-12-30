package com.example.demospark.demo;

import java.util.Arrays;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class WordCount {

    public static void main(String[] args) {
        System.out.println("开始...");
        String textFileName = "C:\\testFile\\testword.txt";
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("Spark WordCount Application (java)")
        .setMaster("local[*]");
        //          .set("spark.executor.cores", "4")
        //          .set("spark.submit.deployMode", "cluster")
        //          .set("spark.jars", appResource)
        //          .set("spark.executor.memory", "1g")
        //          .set("spark.cores.max", "4")
        //          .set("spark.driver.supervise", "false");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        //文本文件的hdfs路径
        //        String hdfsBasePath = "hdfs://" + hdfsHost + ":" + hdfsPort;
        //        String inputPath = hdfsBasePath + "/input/" + textFileName;

        //输出结果文件的hdfs路径
        //        String outputPath = hdfsBasePath + "/output/"
        //                + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        //        System.out.println("input path : " + inputPath);
        //        System.out.println("output path : " + outputPath);
        //导入文件
        JavaRDD<String> textFile = javaSparkContext.textFile(textFileName);
        JavaPairRDD<String, Integer> counts = textFile
                //每一行都分割成单词，返回后组成一个大集合
                .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
                //key是单词，value是1
                .mapToPair(word -> new Tuple2<>(word, 1))
                //基于key进行reduce，逻辑是将value累加
                .reduceByKey((a, b) -> a + b);
        //先将key和value倒过来，再按照key排序
        JavaPairRDD<Integer, String> sorts = counts
                //key和value颠倒，生成新的map
                .mapToPair(tuple2 -> new Tuple2<>(tuple2._2(), tuple2._1()))
                //按照key倒排序
                .sortByKey(false);
        //取前10个
        List<Tuple2<Integer, String>> top10 = sorts.take(10);
        //打印出来
        for(Tuple2<Integer, String> tuple2 : top10){
            System.out.println(tuple2._2() + "\t" + tuple2._1());
        }
        //分区合并成一个，再导出为一个txt保存在hdfs
        //javaSparkContext.parallelize(top10).coalesce(1).saveAsTextFile(outputPath);
        //关闭context
        javaSparkContext.close();
    }
}
