import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMarkerTest {
    @Test
    public void test() throws IOException, TemplateException {
        Configuration myCfg = new Configuration(Configuration.VERSION_2_3_32);
        // 指定模板文件所在的路径
        myCfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        // 设置模板文件使用的字符集
        myCfg.setDefaultEncoding("UTF-8");
        // now it will print 1000000
        myCfg.setNumberFormat("0.######");
        Template template = myCfg.getTemplate("Demo.html.ftl");

        Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("currentYear",2023);

        List<Map<String,Object>> dataModels = new ArrayList<>();
        Map<String,Object> dataModel1 = new HashMap<>();
        Map<String,Object> dataModel2 = new HashMap<>();
        dataModel1.put("url","http://xinapi.xinzz.vip/");
        dataModel1.put("label","api开放平台");
        dataModel2.put("url","https://xinzz.vip/");
        dataModel2.put("label","我的笔记");
        dataModels.add(dataModel1);
        dataModels.add(dataModel2);
        dataModel.put("menuItems",dataModels);
        Writer out = new FileWriter("myweb.html");
        template.process(dataModel,out);
        out.close();
    }
}
