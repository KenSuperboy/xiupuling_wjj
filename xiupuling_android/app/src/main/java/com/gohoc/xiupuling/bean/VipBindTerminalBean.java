package com.gohoc.xiupuling.bean;

import java.util.List;

/**
 * Created by wjj on 2017/11/16.
 * vip绑定终端
 */
public class VipBindTerminalBean {

    /**
     * message : 查询成功
     * data : [{"term_soft_version":"1.0","terminal_no":"52","terminal_id":"a9f7c6bd597f4c0d800589dfe36227e3","shop_id":"1b6d1ac9142240f89696f01d6f609ff4","terminal_type":null,"shop_name":"绿茶（万象城店）"},{"term_soft_version":"V1.0","terminal_no":"01","terminal_id":"terminal001","shop_id":"shop001","terminal_type":2,"shop_name":"绿茶餐厅（海岸城店）"},{"term_soft_version":"V1.0","terminal_no":"03","terminal_id":"terminal002","shop_id":"shop001","terminal_type":2,"shop_name":"绿茶餐厅（海岸城店）"},{"term_soft_version":"V1.0","terminal_no":"04","terminal_id":"terminal003","shop_id":"shop001","terminal_type":2,"shop_name":"绿茶餐厅（海岸城店）"},{"term_soft_version":"V1.0","terminal_no":"01","terminal_id":"terminal004","shop_id":"shop002","terminal_type":2,"shop_name":"绿茶（海雅缤纷城店）"},{"term_soft_version":"V1.0","terminal_no":"02","terminal_id":"terminal005","shop_id":"shop002","terminal_type":2,"shop_name":"绿茶（海雅缤纷城店）"},{"term_soft_version":"V1.0","terminal_no":"03","terminal_id":"terminal006","shop_id":"shop002","terminal_type":2,"shop_name":"绿茶（海雅缤纷城店）"}]
     * code : 1
     */

    public String message;
    public int code;
    /**
     * term_soft_version : 1.0
     * terminal_no : 52
     * terminal_id : a9f7c6bd597f4c0d800589dfe36227e3
     * shop_id : 1b6d1ac9142240f89696f01d6f609ff4
     * terminal_type : null
     * shop_name : 绿茶（万象城店）
     */

    public List<DataBean> data;

    public static class DataBean {
        public String term_soft_version;
        public String terminal_no;
        public int term_orientation;
        public String terminal_id;
        public String shop_id;
        public Object terminal_type;
        public String shop_name;
        public int flag=0;
    }
}
