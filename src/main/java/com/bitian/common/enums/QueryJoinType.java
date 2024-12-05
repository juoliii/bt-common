package com.bitian.common.enums;

/**
 * @author admin
 */
public enum QueryJoinType {
    left_join,right_join,full_join;

    public String getName(){
        String value="";
        switch (this){
            case full_join:{
                value="join";
                break;
            }
            case left_join:{
                value="left join";
                break;
            }
            case right_join:{
                value="right join";
                break;
            }
        }
        return value;
    }
}
