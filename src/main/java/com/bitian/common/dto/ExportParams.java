package com.bitian.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportParams {

    private String code;
    private List<String> columns=new ArrayList<>();
    private List<Column> fullColumns=new ArrayList<>();
    private String format="xlsx";

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Column{
        private String key;
        private String title;
        private String dict;
        private Boolean dynamic=false;
    }
}
