package com.lhl.spring.framework.web.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视图解析器
 * Created by hongliang.liu on 2018/5/12.
 */
public class MyViewResolver {
    private String fileName;
    private File templateFile;

    public MyViewResolver(String fileName, File templateFile) {
        this.fileName = fileName;
        this.templateFile = templateFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }

    public String resolver(MyModelAndView mv) throws Exception {
        RandomAccessFile ra = new RandomAccessFile(this.templateFile, "r");
        String line = null;
        StringBuffer sb = new StringBuffer();
        try {
            while ((line = ra.readLine()) != null) {
                line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                //匹配表达式，传入对应的值
                Matcher m = matcher(line);
                while (m.find()) {
                    for (int i = 1; i <= m.groupCount(); i++) {
                        String paramName = m.group(i);
                        Object value = mv.getModel().get(paramName);
                        if (value == null)
                            continue;
                        line = line.replaceAll("￥\\{" + paramName + "\\}", value.toString());
                        line = new String(line.getBytes("utf-8"), "ISO-8859-1");
                    }
                }
                sb.append(line);
            }

        } finally {
            ra.close();
        }
        return sb.toString();
    }

    private Matcher matcher(String line) {
        Pattern pattern = Pattern.compile("￥\\{(.+)\\}", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(line);


    }
}
