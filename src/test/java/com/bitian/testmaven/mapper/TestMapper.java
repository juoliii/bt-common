package com.bitian.testmaven.mapper;

import com.bitian.common.dto.BaseForm;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author admin
 */
public interface TestMapper {

    List<String> query(BaseForm form);

}
