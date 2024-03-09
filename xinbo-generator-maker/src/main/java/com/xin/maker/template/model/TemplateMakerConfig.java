package com.xin.maker.template.model;

import com.xin.maker.meta.Meta;
import com.xin.model.TemplateMakerFileConfig;
import com.xin.model.TemplateMakerModelConfig;
import lombok.Data;

/**
 * @author 15712
 */
@Data
public class TemplateMakerConfig {
    private Long id;

    private Meta meta = new Meta();

    private String originProjectPath;

    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
}
