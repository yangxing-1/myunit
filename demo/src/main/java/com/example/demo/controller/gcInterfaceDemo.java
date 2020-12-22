package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RestController
public class gcInterfaceDemo {
    
    @RequestMapping("/demo/login/loginGC")
    public String getText(@RequestParam String org_id) {
        //模拟根据org_id获取token
        return "{\"code\":0,\"msg\":\"操作成功\",\"data\":\"eyJhbGciOiJIUzI1NiJ9\"}";
    }
    
    @RequestMapping("/demo/gc-interface-controller/getSampleNoByParams")
    public String getJson(@RequestHeader String token, @RequestParam String startDate, @RequestParam String endDate, @RequestParam String sampleType) {
        //模拟获取抽样单列表
        return "{\"code\":0,\"msg\":\"操作成功\",\"data\":[{\"sp_s_43\":\" xxxxxx \",\"sp_i_state\":2,\"sample_type\":1,\"sp_s_16\":\"GC1851000000\",\"gc_updated_at\":\"2020-09-14T02:26:28.000+00:00\",\"sp_d_46\":\"2020-09-13T16:00:00.000+00:00\"},{\"sp_s_43\":\"xxxxxx\",\"sp_i_state\":2,\"sample_type\":1,\"sp_s_16\":\" GC1851000001\",\"gc_updated_at\":\"2020-09-14T02:26:28.000+00:00\",\"sp_d_46\":\"2020-09-13T16:00:00.000+00:00\"}]}";
    }
    
    @RequestMapping("/demo/gc-interface-controller/getSampleInfoBySampleNoUsingPOST")
    public String testGetInfoFromEsb(@RequestHeader String token, @RequestParam String sampleNo) {
        //模拟获取样品信息
        return "{\"code\":0,\"msg\":\"操作成功\",\"data\":{\"id\":null,\"sp_s_1\":\"泸州市江阳区汇通时代商都有限公司汇通超市西城御景店\",\"sp_s_2\":\"超市\",\"sp_s_3\":\"四川\",\"sp_s_4\":\"泸州\",\"sp_s_5\":\"江阳\",\"sp_s_6\":\"袋\",\"sp_s_7\":null,\"sp_s_8\":\"卢芸\",\"sp_s_9\":null,\"sp_s_10\":\"/\",\"sp_s_11\":\"xxx\",\"sp_s_12\":\"xxxxxxxxxxxxx\",\"sp_s_13\":\"SC10351018100068\",\"sp_s_14\":\"浓缩卤汁(五香型)(测试)\",\"sp_n_15\":20,\"sp_s_16\":\"GC18510000003934888\",\"sp_s_17\":\"调味品\",\"sp_s_18\":\"调味料\",\"sp_s_19\":\"液体复合调味料\",\"sp_s_20\":\"其他液体调味料\",\"sp_s_21\":\"外购\",\"sp_s_23\":\"/\",\"sp_s_24\":\"非无菌采样\",\"sp_s_25\":\"/\",\"sp_s_26\":\"100g/袋\",\"sp_s_27\":\"/\",\"sp_d_28\":\"2018-05-26T16:00:00.000+00:00\",\"sp_d_28_name\":\"生产\",\"sp_n_29\":\"18个月\",\"sp_s_30\":\"常温\",\"sp_s_33\":\"塑料袋\",\"sp_s_34\":\"公章不在签字确认有效\",\"sp_s_35\":\"四川省食品药品检验检测院\",\"sp_s_36\":\"省（区）级\",\"sp_s_37\":\"xxxxx\",\"sp_d_38\":\"2018-06-12T16:00:00.000+00:00\",\"sp_s_39\":\"028-87858377\",\"sp_s_40\":\"xxxx\",\"sp_s_41\":\"028-87858377\",\"sp_s_42\":\"scsyjs@163.com\",\"sp_s_43\":\"四川省食品药品检验检测院\",\"sp_s_44\":\"抽检监测\",\"sp_d_46\":\"2020-09-13T16:00:00.000+00:00\",\"sp_s_49\":\"徐晓霞\",\"sp_s_50\":\"xxxxx\",\"sp_s_51\":\"xxxxxx\",\"sp_s_52\":\"四川\",\"sp_s_61\":\"液体\",\"sp_s_62\":\"工业加工食品\",\"sp_s_63\":\"预包装\",\"sp_s_64\":\"四川廖排骨食品有限公司\",\"sp_s_65\":\"成都市都江堰市蒲阳镇拉法基大道11号\",\"sp_s_68\":\"流通\",\"sp_s_72\":\"Q/LPG0001S-2017\",\"sp_s_73\":\"/\",\"sp_s_74\":\"廖排骨\",\"sp_s_75\":\"/\",\"sp_s_76\":\"xxxxxxx\",\"sp_s_85\":\"xxxx\",\"sp_d_86\":\"2018-06-18T16:00:00.000+00:00\",\"sp_s_87\":\"xxxxxxxx\",\"sp_s_88\":\"xxxxx\",\"sp_s_bsfl\":\"/\",\"sp_s_2_1\":\"xxxxxx\",\"sp_i_state\":null,\"sp_s_201\":\"城市\",\"sp_s_202\":\"四川\",\"sp_s_203\":\"普通食品\",\"sp_s_204\":\"4.5元/袋\",\"sp_s_206\":\"20袋\",\"sp_s_208\":\"4袋\",\"sp_s_211\":\"xxxxx\",\"sp_s_212\":\"611731\",\"sp_s_213\":\"xxxxxxxxxxxx\",\"sp_s_215\":\"xxxxxxxxxxx\",\"barcode\":\"6970872410006\",\"rainbowcode\":\"211392484828\",\"rainbowcode_url\":\"xxxxxxxxxxxxxxxxxxx\",\"qrCode\":null,\"sp_xkz\":\"经营许可证\",\"sp_xkz_id\":\"JY15105020022091\",\"sp_s_220\":\"成都\",\"sp_s_221\":\"都江堰\",\"sp_s_222\":\"6947024100016\",\"sp_s_sfjk\":\"否\",\"sp_s_ycg\":\"中国\",\"sp_s_sfwtsc\":\"否\",\"sp_s_wtxz\":null,\"sp_s_cycj\":null,\"sp_s_wtsheng\":\"\",\"sp_s_wtshi\":\"\",\"sp_s_wtxian\":\"\",\"sp_s_qymc\":\"/\",\"sp_s_qydz\":\"/\",\"sp_s_qs\":\"/\",\"sp_s_lxr\":\"/\",\"sp_s_tel\":\"/\",\"health_code\":\"/\",\"sp_s_dwwz\":\"\",\"sp_s_wcmc\":\"\",\"sp_s_wcyyzzh\":\"\",\"sp_s_wcicp\":\"\",\"sp_s_wcsheng\":\"\",\"sp_s_wcshi\":\"\",\"sp_s_wcxian\":\"\",\"sp_s_wcdz\":\"\",\"sp_s_wcwz\":\"\",\"sp_s_wclxr\":\"\",\"sp_s_wctel\":\"\",\"sp_s_wcbh\":\"\",\"scaname\":\"抽检监测（转移地方）\",\"scbname\":\"2020年总局计划\",\"health_func_cat\":\"/\"}}";
    }
}
