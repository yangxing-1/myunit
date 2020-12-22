package com.example.demo.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.base.SunwayProcessInstance;
import com.example.demo.data.DataSet;
import com.example.demo.datamap.DataMapModule;
import com.example.demo.util.GsonUtils;
import com.example.demo.websocket.MyWebSocket;
import com.google.gson.JsonObject;

//@SuppressWarnings("resource")
//@RestController
public class controllerDemo {
    
    //@Autowired
    private JdbcTemplate jdbcTemplate;
    //@Resource
    private MyWebSocket webSocket;
    private final static TransportClient client;
    static {
//        Settings settings = Settings.builder().put("cluster.name", "my-application").build();
//        TransportAddress address1 = null;
//        try {
//            address1 = new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        client = new PreBuiltTransportClient(settings).addTransportAddress(address1);
        client = null;
    }
    
    @RequestMapping("/getLogFromElasticsearch/{serviceId}")
    public void getLogFromElasticsearch(@PathVariable Long serviceId) {
        int pageSize  = 5 ;
        int pageNum =2;
        //计算起始的数据
        int startNum = (pageNum - 1 ) * pageSize;
        SearchResponse searchResponse = client.prepareSearch("esbservice-"+serviceId).setTypes("_doc").setQuery(QueryBuilders.matchAllQuery())
                //.addSort("startTime", SortOrder.DESC)
                //.setFrom(startNum)
                //.setSize(pageSize)
                .get();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit searchHit : hits1) {
            System.out.println(searchHit.getSourceAsMap().get("message"));
        }
        return;
    }
    
    @RequestMapping("/deleteIndex")
    public void deleteIndex() {
        for (int i = 0; i < 50; i++) {
            client.admin().indices().prepareDelete("esbservice-"+i).execute().actionGet();
        }
        System.out.println("批量删除索引成功");
    }
    
    @RequestMapping("/")
    public void index() {
        return;
        //return new ModelAndView("index");
    }
    
    @RequestMapping("/t")
    public String testController(@RequestParam String serviceId, @RequestBody String jsonStr) {
        System.out.println("json数据转换开始");
        System.out.println(jsonStr);
        // 通过json转换
        List<DataSet> dataSetList1 = DataSet.parseDataSet(jsonStr);
        System.out.println("json数据转换结束");
        // 通过jdbc获取
        List<Map<String, Object>> data = jdbcTemplate.queryForList("select * from t_core_user");
        DataSet dataSet = new DataSet("t_core_user", data);
        List<DataSet> dataSetList2 = new ArrayList<DataSet>();
        dataSetList2.add(dataSet);
        // 字段映射组件
        DataMapModule dataMapModule = new DataMapModule();
        dataMapModule.setDataList(dataSetList2);
        Map<String, Map<String, String>> tableMap = new HashMap<String, Map<String,String>>();
        Map<String,String> columnMap = new HashMap<String, String>();
        columnMap.put("id", "keyid");
        columnMap.put("EMAIL", "EMAILADRESS");
        tableMap.put("t_core_user", columnMap);
        dataMapModule.setColumnMap(tableMap);
        List<DataSet> dataSetList3 =dataMapModule.doMap();
        return dataSetList3.toString();
    }
    
    @RequestMapping("/c")
    public Object testRouter(HttpServletRequest request, @RequestBody String body) throws DocumentException {
        String contentType = request.getHeader("Content-type");
        String no1 = request.getHeader("no");
        String name = request.getParameter("name");
        SunwayProcessInstance processInstance = SunwayProcessInstance.getSunwayProcessInstance("20200812");
        processInstance.getMessage().setRequest(request);
        if ("application/json".equalsIgnoreCase(contentType)) {
            JsonObject json = GsonUtils.fromJson(body, JsonObject.class);
            processInstance.getMessage().setPayload(json);
        } else if ("application/xml".equalsIgnoreCase(contentType)) {
            Document document = DocumentHelper.parseText(body);
            document.getRootElement().elements();
            processInstance.getMessage().setPayload(document);
        } else {
            return null;
            //模拟jdbc
            //List<Map<String, Object>> data = jdbcTemplate.queryForList("select * from t_core_user");
            //processInstance.getMessage().setPayload(data);
        }
        processInstance.excute();
        Object payload = processInstance.getMessage().getPayload();
        String className = payload.getClass().getName();
        return payload;
    }

    @GetMapping(value="/sendMsg/{msg}")
    public String sendMsg(@PathVariable String msg){
        webSocket.sendMessage(msg);
        return "推送的消息为:" + msg;
    }
    
    @RequestMapping("/get/text")
    public String getText(HttpServletRequest request, @RequestBody String body) {
        String contentType = request.getHeader("Content-type");
        return "java";
    }
    
    @RequestMapping("/get/json")
    public String getJson(HttpServletRequest request, @RequestBody String body) {
        String contentType = request.getHeader("Content-type");
        System.out.println("method:/get/json  Content-type:"+contentType);
        return "{\"status\":\"0000\",\"message\":\"success\",\"data\":{\"title\":{\"id\":\"001\",\"name\":\"白菜\"},\"content\":[{\"id\":\"001\",\"value\":\"你好 白菜\"},{\"id\":\"002\",\"value\":\"你好 萝卜\"}]}}";
    }
    
    @RequestMapping("/get/xml")
    public String getXml(HttpServletRequest request, @RequestBody String body) {
        String contentType = request.getHeader("Content-type");
        System.out.println("method:/get/json  Content-type:"+contentType);
        return "<person>\r\n" + 
                "    <name>shisl</name>\r\n" + 
                "    <age>18</age>\r\n" + 
                "    <sex>men</sex>\r\n" + 
                "    <friends>\r\n" + 
                "        <friend>\r\n" + 
                "            <name>zhoutt</name>\r\n" + 
                "            <age>16</age>\r\n" + 
                "            <sex>men</sex>\r\n" + 
                "        </friend>\r\n" + 
                "        <friend>\r\n" + 
                "            <name>huangzg</name>\r\n" + 
                "            <age>17</age>\r\n" + 
                "            <sex>men</sex>\r\n" + 
                "        </friend>\r\n" + 
                "    </friends>\r\n" + 
                "</person>";
    }
    
    @RequestMapping("/{id}/test/transform")
    public String testGetInfoFromEsb(HttpServletRequest request, @PathVariable Long id, @RequestBody String body) {
        String contentType = request.getHeader("Content-type");
        String name = request.getParameter("name");
        String str = "B服务接收到id="+id+",contentType="+contentType+",name="+name+",xml转换后的json数据为：\r";
        return str+body;
    }
}
