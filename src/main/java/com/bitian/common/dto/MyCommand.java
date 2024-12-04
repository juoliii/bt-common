package com.bitian.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author admin
 */
@Data
@AllArgsConstructor
public class MyCommand {
    private String command;
    private boolean quota;
    public MyCommand(String command){
        this.command=command;
    }
}
