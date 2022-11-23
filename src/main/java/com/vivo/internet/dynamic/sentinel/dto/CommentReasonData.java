package com.vivo.internet.dynamic.sentinel.dto;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class CommentReasonData {

    private String tag;

    private Map<String, List<CommentReason>> data = new HashMap<>();


    public void addData(CommentReason commentReason){
        if (commentReason == null || StringUtils.isBlank(commentReason.getAppId())){
            return;
        }

        List<CommentReason> reasons = data.get(commentReason.getAppId());
        if (reasons == null){
            reasons = new LinkedList<>();
            data.put(commentReason.getAppId(), reasons);
        }

        reasons.add(commentReason);
    }

    public Map<String, List<CommentReason>> getData() {
        return data;
    }

    public String acDataStr(){
        return JSONObject.toJSONString(data);
    }

}
