package com.bitian.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyCommand {
    private String command;
    private boolean quota;
    public MyCommand(String command){
        this.command=command;
    }
}
